/*
 * The Genesis Project
 * Copyright (C) 2004  Summa Technologies do Brasil Ltda.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.java.dev.genesis.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.java.dev.genesis.commons.jxpath.VariablesImpl;
import net.java.dev.genesis.commons.jxpath.functions.ExtensionFunctions;
import net.java.dev.genesis.reflection.MethodEntry;
import net.java.dev.genesis.ui.ValidationException;
import net.java.dev.genesis.ui.ValidationUtils;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.MemberMetadata;
import net.java.dev.genesis.ui.metadata.MethodMetadata;
import net.java.dev.genesis.util.GenesisUtils;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.jxpath.ClassFunctions;
import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.Functions;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultFormController implements FormController {
   private static final Log log = LogFactory
         .getLog(DefaultFormController.class);

   private Object form;
   private FormMetadata formMetadata;
   private JXPathContext ctx;

   private FormState currentState;
   private FormState previousState;

   private Collection listeners = new ArrayList();

   public void setForm(Object form) {
      this.form = form;
   }

   public Object getForm() {
      return form;
   }

   public void setFormMetadata(FormMetadata formMetadata) {
      this.formMetadata = formMetadata;
   }

   public FormMetadata getFormMetadata() {
      return formMetadata;
   }

   protected JXPathContext createJXPathContext() {
      final JXPathContext ctx = JXPathContext.newContext(form);
      ctx.setFunctions(getFunctions());
      ctx.setVariables(getVariables());
      return ctx;
   }

   protected Functions getFunctions() {
      return new ClassFunctions(ExtensionFunctions.class, "g");
   }

   protected Variables getVariables() {
      return new VariablesImpl();
   }

   protected Map getVariablesMap() {
      return ((VariablesImpl) ctx.getVariables()).getVariablesMap();
   }

   protected final JXPathContext getContext() {
      return ctx;
   }

   protected FormState createFormState() {
      return new FormStateImpl();
   }

   protected FormState createFormState(FormState state) {
      return new FormStateImpl(state);
   }

   public void setup() throws Exception {
      ctx = createJXPathContext();
      ctx.getVariables().declareVariable(FORM_METADATA_KEY, formMetadata);

      currentState = createFormState();
      ctx.getVariables().declareVariable(CURRENT_STATE_KEY, currentState);

      if (PropertyUtils.isWriteable(form, "context")
            && Map.class.isAssignableFrom(PropertyUtils.getPropertyType(form,
                  "context"))) {
         PropertyUtils.setProperty(form, "context", getVariablesMap());
      }

      evaluate(true);
   }

   public void addFormControllerListener(FormControllerListener listener) {
      listeners.add(listener);
   }

   public boolean removeFormControllerListener(FormControllerListener listener) {
      return listeners.remove(listener);
   }

   public Collection getFormControllerListeners() {
      return new ArrayList(listeners);
   }

   public void populate(Map properties) throws Exception {
      populate(properties, true);
   }

   public void update() throws Exception {
      populate(null, false);
   }

   protected void populate(Map properties, boolean stringMap) throws Exception {
      if (previousState == null) {
         previousState = createFormState(currentState);
      }

      try {
         if (properties == null) {
            properties = PropertyUtils.describe(form);
         }

         updateChangedMap(properties, stringMap);

         if (currentState.getChangedMap().isEmpty()) {
            log.debug("Nothing changed.");

            return;
         }

         if (stringMap) {
            PropertyUtils.copyProperties(form, currentState.getChangedMap());
         }

         evaluate(false);
      } catch (Exception e) {
         reset(previousState);

         throw e;
      } finally {
         previousState = null;
      }
   }

   private void updateChangedMap(Map newData, boolean stringMap) {
      Object value;
      FieldMetadata fieldMeta;
      Map.Entry entry;

      final Map changedMap = currentState.getChangedMap();

      changedMap.clear();
      changedMap.putAll(GenesisUtils.normalizeMap(newData));

      if (log.isDebugEnabled()) {
         log.debug("changedMap started as: " + changedMap);
      }

      for (final Iterator i = changedMap.entrySet().iterator(); i.hasNext();) {
         entry = (Map.Entry) i.next();
         fieldMeta = formMetadata.getFieldMetadata(entry.getKey().toString());

         value = (stringMap) ? fieldMeta.getConverter().convert(
               fieldMeta.getFieldClass(), entry.getValue()) : entry.getValue();

         if (previousState != null && fieldMeta.getEqualityComparator().equals(
               value, previousState.getValuesMap().get(entry.getKey()))
               && fieldMeta.getEqualityComparator().equals(value,
                     currentState.getValuesMap().get(entry.getKey()))) {
            i.remove();

            if (log.isDebugEnabled()) {
               log.debug(entry.getKey() + " removed from changedMap for being "
                     + " equal to previousValue; is " + entry.getValue()
                     + "; was:" + previousState.getValuesMap().get(entry
                     .getKey()));
            }

            continue;
         }

         if (log.isDebugEnabled()) {
            log.debug("Field '" + entry.getKey() + "' changed to '"
                  + entry.getValue() + "'; was " + (previousState == null ? 
                  "undefined" : previousState.getValuesMap().get(entry
                  .getKey())));
         }

         entry.setValue(value);
         currentState.getValuesMap().put(entry.getKey(), entry.getValue());
      }
   }

   protected void evaluate(boolean firstCall) throws Exception {
      evaluateNamedConditions();

      if (evaluateClearOnConditions()) {
         evaluateNamedConditions();
      }

      if (evaluateCallWhenConditions(firstCall)) {
         evaluateNamedConditions();
      }

      final Map newData = PropertyUtils.describe(form);
      updateChangedMap(newData, false);

      fireValuesChanged(currentState.getChangedMap());

      evaluateEnabledWhenConditions();
      evaluateVisibleWhenConditions();
   }

   protected void fireValuesChanged(Map updatedValues) throws Exception {
      for (final Iterator i = listeners.iterator(); i.hasNext(); ) {
         ((FormControllerListener)i.next()).valuesChanged(updatedValues);
      }
   }

   protected boolean evaluateClearOnConditions() throws Exception {
      final Map fieldMetadatas = formMetadata.getFieldMetadatas();
      final Map dataProviderMetadatas = formMetadata.getDataProviderMetadatas();
      final Map toCopy = new HashMap();

      boolean changed;
      FieldMetadata fieldMetadata;
      DataProviderMetadata dataProviderMeta;
      List items;
      Map.Entry entry;
      final Map properties = PropertyUtils.describe(form);

      do {
         changed = false;
         properties.putAll(toCopy);

         for (final Iterator i = fieldMetadatas.entrySet().iterator(); i
               .hasNext();) {
            entry = (Map.Entry) i.next();
            fieldMetadata = (FieldMetadata) entry.getValue();

            if (fieldMetadata.getClearOnCondition() == null
                  || fieldMetadata.getEmptyResolver().isEmpty(
                        properties.get(entry.getKey()))) {
               i.remove();
               continue;
            }

            if (isConditionSatisfied(fieldMetadata.getClearOnCondition())) {
               if (log.isDebugEnabled()) {
                  log.debug("ClearOn Condition for field '" + entry.getKey()
                        + "' satisfied. Setting '"
                        + fieldMetadata.getEmptyValue() + "'");
               }
               changed = true;
               toCopy.put(entry.getKey(), fieldMetadata.getEmptyValue());
               i.remove();
            }
         }
         
         for (Iterator i = dataProviderMetadatas.entrySet().iterator(); i.hasNext();) {
            entry = (Map.Entry) i.next();
            dataProviderMeta = (DataProviderMetadata) entry.getValue();
            
            if (dataProviderMeta.getClearOnCondition() == null) {
               i.remove();
               continue;
            }

            if (isConditionSatisfied(dataProviderMeta.getClearOnCondition())) {
               if (log.isDebugEnabled()) {
                  log.debug("ClearOn Condition for data provider '" + entry.getKey()
                        + "' satisfied. Clearing data provider list.");
               }
               
               changed = true;
               currentState.getDataProvidedMap().put(dataProviderMeta, items = new ArrayList());
               fireDataProvidedListMetadataChanged(dataProviderMeta, items);
               i.remove();
            }
         }
      } while (changed);

      if (toCopy.isEmpty()) {
         if (log.isDebugEnabled()) {
            log.debug("Nothing changed in ClearOn conditions.");
         }

         return false;
      }

      PropertyUtils.copyProperties(form, toCopy);
      currentState.getChangedMap().putAll(toCopy);

      return true;
   }

   protected void evaluateNamedConditions() {
      Map.Entry entry;

      for (final Iterator i = formMetadata.getNamedConditions().entrySet()
            .iterator(); i.hasNext();) {
         entry = (Map.Entry) i.next();

         ctx.getVariables().declareVariable(entry.getKey().toString(),
               isSatisfied((CompiledExpression) entry.getValue()));

         if (log.isDebugEnabled()) {
            log.debug("Named Condition '" + entry.getKey() + "' evaluated as '"
                  + ctx.getVariables().getVariable(entry.getKey().toString())
                  + "'");
         }
      }
   }

   protected void evaluateEnabledWhenConditions() {
      Map.Entry entry;
      MemberMetadata memberMetadata;
      Boolean newValue;

      final Map updatedEnabledConditions = new HashMap();
      final Map toEvaluate = new HashMap(formMetadata.getFieldMetadatas());
      toEvaluate.putAll(formMetadata.getActionMetadatas());

      for (final Iterator i = toEvaluate.entrySet().iterator(); i.hasNext();) {
         entry = (Map.Entry) i.next();
         memberMetadata = (MemberMetadata) entry.getValue();

         if (memberMetadata.getEnabledCondition() == null) {
            continue;
         }

         newValue = isSatisfied(memberMetadata.getEnabledCondition());

         if (!newValue.equals(currentState.getEnabledMap().get(memberMetadata
               .getName()))) {
            currentState.getEnabledMap().put(memberMetadata.getName(), newValue);
            updatedEnabledConditions.put(memberMetadata.getName(), newValue);
         }

         if (log.isDebugEnabled()) {
            log.debug("EnabledWhen Condition for member '"
                  + memberMetadata.getName() + "' evaluated as '"
                  + currentState.getEnabledMap().get(memberMetadata.getName()) +
                  "'");
         }
      }

      fireEnabledConditionChanged(updatedEnabledConditions);
   }

   protected void fireEnabledConditionChanged(Map updatedEnabledConditions) {
      for (final Iterator i = listeners.iterator(); i.hasNext(); ) {
         ((FormControllerListener)i.next()).enabledConditionsChanged(
               updatedEnabledConditions);
      }
   }

   protected void evaluateVisibleWhenConditions() {
      Map.Entry entry;
      MemberMetadata memberMetadata;
      Boolean newValue;

      final Map updatedVisibleConditions = new HashMap();
      final Map toEvaluate = new HashMap(formMetadata.getFieldMetadatas());
      toEvaluate.putAll(formMetadata.getActionMetadatas());

      for (final Iterator i = toEvaluate.entrySet().iterator(); i.hasNext();) {
         entry = (Map.Entry) i.next();
         memberMetadata = (MemberMetadata) entry.getValue();

         if (memberMetadata.getVisibleCondition() == null) {
            continue;
         }

         newValue = isSatisfied(memberMetadata.getVisibleCondition());

         if (!newValue.equals(currentState.getVisibleMap().get(memberMetadata
               .getName()))) {
            currentState.getVisibleMap().put(memberMetadata.getName(), newValue);
            updatedVisibleConditions.put(memberMetadata.getName(), newValue);
         }

         if (log.isDebugEnabled()) {
            log.debug("VisibleWhen Condition for member '"
                  + memberMetadata.getName() + "' evaluated as '"
                  + currentState.getVisibleMap().get(memberMetadata.getName()) + 
                  "'");
         }
      }

      fireVisibleConditionChanged(updatedVisibleConditions);
   }

   protected void fireVisibleConditionChanged(Map updatedVisibleConditions) {
      for (final Iterator i = listeners.iterator(); i.hasNext(); ) {
         ((FormControllerListener)i.next()).visibleConditionsChanged(
               updatedVisibleConditions);
      }
   }

   protected boolean evaluateCallWhenConditions(boolean firstCall) throws Exception {
      boolean changed = false;

      for (final Iterator i = formMetadata.getMethodMetadatas().values()
            .iterator(); i.hasNext(); ) {
         changed |= invokeAction((MethodMetadata)i.next(), firstCall, true);
      }

      return changed;
   }

   protected boolean invokeAction(MethodMetadata methodMetadata, 
         boolean firstCall, boolean conditionally) throws Exception {
      List items = null;
      DataProviderMetadata dataProviderMeta = methodMetadata
            .getDataProviderMetadata();

      if ((methodMetadata.getCallCondition() == null || firstCall) 
            && conditionally) {
         if (dataProviderMeta == null || (conditionally && !firstCall)) {
            return false;
         }

         if (!dataProviderMeta.isCallOnInit()) {
            currentState.getDataProvidedMap().put(dataProviderMeta, 
                  items = new ArrayList());
            fireDataProvidedListMetadataChanged(dataProviderMeta, items);

            return true;
         }
         conditionally = false;
      }

      final boolean satisfied = !conditionally || isConditionSatisfied(
            methodMetadata.getCallCondition());

      if (satisfied) {
         if (dataProviderMeta == null && !beforeInvokingMethod(methodMetadata)) {
            return false;
         }

         final Object ret = methodMetadata.invoke(form);

         if (dataProviderMeta != null) {
            items = (ret.getClass().isArray()) ? Arrays.asList((Object[]) ret) :
                 (List)ret;
            currentState.getDataProvidedMap().put(dataProviderMeta, items);
            fireDataProvidedListMetadataChanged(dataProviderMeta, items);
         } else {
            afterInvokingMethod(methodMetadata);
         }
      }

      if (log.isDebugEnabled()) {
         log.debug("CallWhen Condition for method '" + 
               methodMetadata.getName() + "' evaluated as '" + 
               satisfied + "'");
      }

      return satisfied;
   }

   protected boolean beforeInvokingMethod(MethodMetadata metadata) 
         throws Exception {
      for (final Iterator i = listeners.iterator(); i.hasNext(); ) {
         if (!((FormControllerListener)i.next()).beforeInvokingMethod(metadata)) {
            return false;
         }
      }

      return true;
   }

   protected void afterInvokingMethod(MethodMetadata metadata) 
         throws Exception {
      for (final Iterator i = listeners.iterator(); i.hasNext(); ) {
         ((FormControllerListener)i.next()).afterInvokingMethod(metadata);
      }
   }

   protected void fireDataProvidedListMetadataChanged(
         DataProviderMetadata metadata, List items) throws Exception {
      for (final Iterator i = listeners.iterator(); i.hasNext(); ) {
         ((FormControllerListener)i.next()).dataProvidedListChanged(metadata, 
               items);
      }
   }

   protected boolean isConditionSatisfied(CompiledExpression compiledEx) {
      return Boolean.TRUE.equals(compiledEx.getValue(ctx));
   }

   protected Boolean isSatisfied(CompiledExpression compiledEx) {
      return Boolean.valueOf(isConditionSatisfied(compiledEx));
   }

   public void reset(FormState state) throws Exception {
      if (log.isDebugEnabled()) {
         log.debug("Reseting form '" + form + "'");
      }

      currentState = state;
      ctx.getVariables().declareVariable(CURRENT_STATE_KEY, currentState);

      fireEnabledConditionChanged(state.getEnabledMap());
      fireVisibleConditionChanged(state.getVisibleMap());

      for (final Iterator i = state.getDataProvidedMap().entrySet().iterator(); 
            i.hasNext();) {
         final Map.Entry entry = (Map.Entry)i.next();
         fireDataProvidedListMetadataChanged((DataProviderMetadata)entry
               .getKey(), (List)entry.getValue());
      }

      final Map currentValues = PropertyUtils.describe(form);

      for (final Iterator i = currentValues.entrySet().iterator(); 
            i.hasNext();) {
         Map.Entry entry = (Map.Entry)i.next();
         FieldMetadata fieldMeta = formMetadata.getFieldMetadata(entry.getKey()
               .toString());

         if (fieldMeta == null || fieldMeta.getEqualityComparator().equals(
               entry.getValue(), state.getValuesMap().get(entry.getKey()))) {
            i.remove();

            continue;
         }

         currentValues.put(entry.getKey(), state.getValuesMap().get(
               entry.getKey()));
      }

      PropertyUtils.copyProperties(form, currentValues);
      state.getChangedMap().clear();
      state.getChangedMap().putAll(currentValues);

      fireValuesChanged(state.getValuesMap());
   }

   public FormState getFormState() throws Exception {
      return currentState;
   }

   public void invokeAction(String name) throws Exception {
      if (name == null) {
         throw new IllegalArgumentException("Cannot invoke null action");
      }

      if (log.isDebugEnabled()) {
         log.debug("Invoking action: " + name);
      }

      final MethodEntry entry = new MethodEntry(name, new String[0]);
      final MethodMetadata methodMetadata = getFormMetadata()
            .getMethodMetadata(entry);

      if (methodMetadata == null) {
         throw new IllegalArgumentException("Couldn't find action named " + 
               name);
      }

      if (methodMetadata.getActionMetadata() != null && methodMetadata.getActionMetadata().isValidateBefore()) {

         final String formName = form.getClass().getName();

         final Map validationErrors = ValidationUtils.getInstance()
               .getMessages(ValidationUtils.getInstance().validate(form, 
               formName), formName);

         if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
         }

      } 

      invokeActionWithReset(methodMetadata, false, false);

      update();
   }

   protected void invokeActionWithReset(MethodMetadata metadata, 
         boolean firstCall, boolean conditionally) throws Exception {
      previousState = createFormState(currentState);

      try {
         invokeAction(metadata, firstCall, conditionally);
      } catch (Exception e) {
         reset(previousState);
         previousState = null;

         throw e;
      }
   }
}