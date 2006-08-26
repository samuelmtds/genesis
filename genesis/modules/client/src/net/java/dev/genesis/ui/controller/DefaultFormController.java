/*
 * The Genesis Project
 * Copyright (C) 2004-2006  Summa Technologies do Brasil Ltda.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.java.dev.genesis.reflection.MethodEntry;
import net.java.dev.genesis.reflection.ReflectionInvoker;
import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.script.ScriptExpression;
import net.java.dev.genesis.ui.ValidationException;
import net.java.dev.genesis.ui.ValidationUtils;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.MemberMetadata;
import net.java.dev.genesis.ui.metadata.MethodMetadata;
import net.java.dev.genesis.util.GenesisUtils;
import org.apache.commons.beanutils.Converter;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultFormController implements FormController {
   private static final Log log = LogFactory
         .getLog(DefaultFormController.class);

   private static final List EMPTY_LIST = Collections.EMPTY_LIST;
   private static final int[] EMPTY_INT_ARRAY = new int[0];

   private boolean setup;

   private Object form;
   private FormMetadata formMetadata;
   private ScriptContext ctx;
   private int maximumEvaluationTimes = 1;
   private boolean resetOnDataProviderChange = true;

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

   protected final ScriptContext getScriptContext() {
      return ctx;
   }

   public int getMaximumEvaluationTimes() {
      return maximumEvaluationTimes;
   }

   public void setMaximumEvaluationTimes(int maximumEvaluationTimes) {
      this.maximumEvaluationTimes = maximumEvaluationTimes;
   }

   public boolean isResetOnDataProviderChange() {
      return resetOnDataProviderChange;
   }

   public void setResetOnDataProviderChange(boolean resetOnDataProviderChange) {
      this.resetOnDataProviderChange = resetOnDataProviderChange;
   }

   protected void setContext(ScriptContext ctx) {
      this.ctx = ctx;
   }

   protected FormState createFormState() {
      return new FormStateImpl();
   }

   protected FormState createFormState(FormState state) {
      Map clonedValues = new HashMap(state.getValuesMap().size());

      for (Iterator iter = state.getValuesMap().entrySet().iterator(); iter
            .hasNext();) {
         Map.Entry entry = (Map.Entry)iter.next();

         FieldMetadata fieldMeta = formMetadata.getFieldMetadata(entry.getKey()
               .toString());

         clonedValues.put(entry.getKey(), fieldMeta.getCloner().clone(
               entry.getValue()));
      }

      return new FormStateImpl(state, clonedValues);
   }

   public void setup() throws Exception {
      if (setup) {
         throw new IllegalStateException("setup() has already been called");
      }

      setup = true;

      ScriptContext ctx = createScriptContext();
      ctx.declare(FORM_METADATA_KEY, formMetadata);

      currentState = createFormState();
      ctx.declare(CURRENT_STATE_KEY, currentState);

      setContext(ctx);

      if (PropertyUtils.isWriteable(getForm(), "context")
            && Map.class.isAssignableFrom(PropertyUtils.getPropertyType(getForm(),
                  "context"))) {
         PropertyUtils.setProperty(getForm(), "context", ctx.getContextMap());
      }

      evaluate(true);
   }
   
   protected ScriptContext createScriptContext() {
      return getFormMetadata().getScript().newContext(getForm());
   }

   public boolean isSetup() {
      return setup;
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

   public void populate(Map properties, Map converters) throws Exception {
      populate(properties, true, converters);
   }

   public void update() throws Exception {
      populate(null, false, null);
   }

   protected void populate(Map properties, boolean stringMap, Map converters) 
         throws Exception {
      final boolean createPreviousState = createPreviousState();

      try {
         if (properties == null) {
            properties = PropertyUtils.describe(getForm());
         }

         updateChangedMap(properties, stringMap, converters);

         if (currentState.getChangedMap().isEmpty()) {
            log.debug("Nothing changed.");

            return;
         }

         if (stringMap) {
            PropertyUtils.copyProperties(getForm(), currentState.getChangedMap());
         }

         evaluate(false);
      } catch (Exception e) {
         resetPreviousState(createPreviousState);

         throw e;
      } finally {
         releasePreviousState(createPreviousState);
      }
   }

   protected boolean updateChangedMap(Map newData, boolean stringMap, 
            Map converters) {
      boolean changed = false;

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

         if (fieldMeta == null) {
            continue;
         }

         value = (stringMap) ? getConverter(fieldMeta, converters).convert(
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
         
         changed = true;
      }

      return changed;
   }

   private Converter getConverter(FieldMetadata fieldMetadata, Map converters) {
      if (converters == null) {
         return fieldMetadata.getConverter();
      }

      final Converter converter = (Converter)converters.get(
            fieldMetadata.getName());

      return (converter == null) ? fieldMetadata.getConverter() : converter;
   }

   protected void evaluate(boolean firstCall) throws Exception {
      boolean changed = true;

      for (int times = 0; changed && times < getMaximumEvaluationTimes(); 
            times++) {
         changed = doEvaluate(firstCall);
      }
   }

   protected boolean doEvaluate(boolean firstCall) throws Exception {
      boolean changed = false;
      evaluateNamedConditions();

      if (evaluateClearOnConditions()) {
         changed = true;
         evaluateNamedConditions();
      }

      if (evaluateCallWhenConditions(firstCall)) {
         changed = true;
         evaluateNamedConditions();
      }

      if (evaluateDataProvidedIndexes(firstCall)) {
         changed = true;
         evaluateNamedConditions();
      }

      final Map newData = PropertyUtils.describe(getForm());
      changed |= updateChangedMap(newData, false, null);

      fireValuesChanged(currentState.getChangedMap());

      changed |= evaluateEnabledWhenConditions();
      changed |= evaluateVisibleWhenConditions();
      
      return changed;
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
      final Map properties = PropertyUtils.describe(getForm());

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
         
         for (Iterator i = dataProviderMetadatas.entrySet().iterator(); 
               i.hasNext();) {
            entry = (Map.Entry) i.next();
            dataProviderMeta = (DataProviderMetadata) entry.getValue();
            
            if (dataProviderMeta.getClearOnCondition() == null) {
               i.remove();
               continue;
            }

            if (isConditionSatisfied(dataProviderMeta.getClearOnCondition())) {
               if (log.isDebugEnabled()) {
                  log.debug("ClearOn Condition for data provider '" + 
                        entry.getKey() + "' satisfied. Clearing data " +
                        "provider list.");
               }

               changed = true;
               updateDataProviderCurrentSelection(dataProviderMeta,
                     items = EMPTY_LIST, EMPTY_INT_ARRAY);
               fireDataProvidedListMetadataChanged(dataProviderMeta, items, false);
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

      PropertyUtils.copyProperties(getForm(), toCopy);
      currentState.getChangedMap().putAll(toCopy);

      return true;
   }

   protected void evaluateNamedConditions() {
      Map.Entry entry;

      for (final Iterator i = formMetadata.getNamedConditions().entrySet()
            .iterator(); i.hasNext();) {
         entry = (Map.Entry) i.next();
         
         evaluateNamedCondition(entry.getKey().toString(), 
                 (ScriptExpression)entry.getValue());
      }
   }

   protected void evaluateNamedCondition(String conditionName, 
         ScriptExpression expr) {
      Boolean conditionValue = isSatisfied(expr);
      getScriptContext().declare(conditionName, conditionValue);

      if (log.isDebugEnabled()) {
         log.debug("Named Condition '" + conditionName + "' evaluated as '" + 
                 conditionValue + "'");
      }
   }

   protected boolean evaluateEnabledWhenConditions() {
      return evaluateConditions(true);
   }

   protected boolean evaluateVisibleWhenConditions() {
      return evaluateConditions(false);
   }

   protected boolean evaluateConditions(boolean enabled) {
      Map.Entry entry;

      final Map updatedConditions = new HashMap();
      final Map toEvaluate = new HashMap(formMetadata.getFieldMetadatas());
      toEvaluate.putAll(formMetadata.getActionMetadatas());

      for (final Iterator i = toEvaluate.entrySet().iterator(); i.hasNext();) {
         entry = (Map.Entry) i.next();
         evaluateCondition((MemberMetadata) entry.getValue(), updatedConditions, enabled);
      }

      if (enabled) {
         fireEnabledConditionChanged(updatedConditions);
      } else {
         fireVisibleConditionChanged(updatedConditions);
      }

      return !updatedConditions.isEmpty();
   }
   
   protected void evaluateCondition(MemberMetadata memberMetadata, Map updatedConditions, boolean enabled) {
      if ((enabled && memberMetadata.getEnabledCondition() == null)
            || (!enabled && memberMetadata.getVisibleCondition() == null)) {
         return;
      }

      final Object newValue = isSatisfied(enabled ? memberMetadata.getEnabledCondition()
            : memberMetadata.getVisibleCondition());
      
      final Map conditionMap = enabled ?
            currentState.getEnabledMap() : currentState.getVisibleMap();

      if (!newValue.equals(conditionMap.get(memberMetadata
            .getName()))) {
         conditionMap.put(memberMetadata.getName(), newValue);
         updatedConditions.put(memberMetadata.getName(), newValue);
      }

      if (log.isDebugEnabled()) {
         log.debug((enabled ? "EnabledWhen" : "VisibleWhen")
               + " Condition for member '" + memberMetadata.getName()
               + "' evaluated as '"
               + conditionMap.get(memberMetadata.getName()) + "'");
      }
   }

   protected void fireEnabledConditionChanged(Map updatedEnabledConditions) {
      for (final Iterator i = listeners.iterator(); i.hasNext(); ) {
         ((FormControllerListener)i.next()).enabledConditionsChanged(
               updatedEnabledConditions);
      }
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

   protected boolean evaluateDataProvidedIndexes(boolean firstCall) throws Exception {
      boolean changed = false;

      Map currentMap = firstCall ? PropertyUtils.describe(getForm()) : currentState.getChangedMap();

      for (final Iterator i = formMetadata.getDataProviderIndexes().values()
            .iterator(); i.hasNext(); ) {
         changed |= evaluateDataProvidedIndex((DataProviderMetadata) i.next(), currentMap);
      }

      return changed;
   }

   protected boolean evaluateDataProvidedIndex(DataProviderMetadata dataMeta,
         Map currentMap) throws Exception {
      final Object indexes = currentMap.get(dataMeta.getIndexField()
            .getFieldName());

      if (indexes == null) {
         if (log.isDebugEnabled()) {
            log.debug("Index field " + dataMeta.getIndexField().getFieldName()
                  + " haven't been changed.");
         }

         return false;
      }

      final int[] selectedIndexes = dataMeta.getSelectedIndexes(indexes);

      final Object objectFieldValue = dataMeta.populateSelectedFields(getForm(),
            (List)currentState.getDataProvidedMap().get(dataMeta),
            selectedIndexes);
      fireDataProvidedIndexesChanged(dataMeta, selectedIndexes);

      currentState.getDataProvidedIndexesMap().put(dataMeta, selectedIndexes);

      if (dataMeta.getObjectField() != null) {
         currentState.getChangedMap().put(
               dataMeta.getObjectField().getFieldName(), objectFieldValue);
      }

      return true;
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
            updateDataProviderCurrentSelection(dataProviderMeta,
                  items = EMPTY_LIST, EMPTY_INT_ARRAY);
            fireDataProvidedListMetadataChanged(dataProviderMeta, items, false);

            return true;
         }
         conditionally = false;
      }

      final boolean satisfied = !conditionally || isConditionSatisfied(
            methodMetadata.getCallCondition());

      if (satisfied) {
         if (methodMetadata.getActionMetadata() != null && 
               !beforeInvokingMethod(methodMetadata)) {
            return false;
         }

         final Object ret = methodMetadata.invoke(getForm());

         if (dataProviderMeta != null) {
            if (ret == null) {
               throw new IllegalStateException("DataProvider " + 
                     methodMetadata.getName() + " in " + getForm() + " returned " +
                     "null; it should return an empty " + 
                     ReflectionInvoker.getInstance().getMethod(getForm(), 
                     methodMetadata.getName(), methodMetadata.getMethodEntry()
                     .getArgsClassesNames()).getReturnType().getName() + 
                     " instead");
            }

            items = (ret.getClass().isArray()) ? Arrays.asList((Object[]) ret) :
                 (List)ret;
            currentState.getDataProvidedMap().put(dataProviderMeta, items);
            fireDataProvidedListMetadataChanged(dataProviderMeta, items, firstCall);
         } 

         if (methodMetadata.getActionMetadata() != null) {
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

   public void resetSelection(DataProviderMetadata meta, List dataProvided)
         throws Exception {
      int[] selected;

      if (meta.isResetSelection()) {
         meta.resetSelectedFields(getForm());
         selected = EMPTY_INT_ARRAY;
      } else {
         int[] currentSelection = (int[])currentState
               .getDataProvidedIndexesMap().get(meta);
         selected = meta.retainSelectedFields(getForm(), dataProvided,
               currentSelection == null ? EMPTY_INT_ARRAY : currentSelection);
      }

      currentState.getDataProvidedIndexesMap().put(meta, selected);
   }

   protected void updateDataProviderCurrentSelection(DataProviderMetadata meta, List objectList, int[] indexes) {
      currentState.getDataProvidedMap().put(meta, objectList);
      currentState.getDataProvidedIndexesMap().put(meta, indexes);
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
         DataProviderMetadata metadata, List items, boolean firstCall) throws Exception {

      if (!firstCall && isResetOnDataProviderChange()) {
         resetSelection(metadata, items);
      }

      for (final Iterator i = listeners.iterator(); i.hasNext(); ) {
         ((FormControllerListener)i.next()).dataProvidedListChanged(metadata,
               items);
      }
   }

   protected void fireDataProvidedIndexesChanged(DataProviderMetadata metadata,
         int[] selectedIndexes) throws Exception {
      for (final Iterator i = listeners.iterator(); i.hasNext();) {
         ((FormControllerListener)i.next()).dataProvidedIndexesChanged(
               metadata, selectedIndexes);
      }
   }

   protected boolean isConditionSatisfied(ScriptExpression expr) {
      return Boolean.TRUE.equals(expr.eval(getScriptContext()));
   }

   protected Boolean isSatisfied(ScriptExpression expr) {
      return Boolean.valueOf(isConditionSatisfied(expr));
   }

   public void reset(FormState state) throws Exception {
      if (log.isDebugEnabled()) {
         log.debug("Reseting form '" + getForm() + "'");
      }

      currentState = state = createFormState(state);
      getScriptContext().declare(CURRENT_STATE_KEY, currentState);

      fireEnabledConditionChanged(state.getEnabledMap());
      fireVisibleConditionChanged(state.getVisibleMap());

      for (final Iterator i = state.getDataProvidedMap().entrySet().iterator(); 
            i.hasNext();) {
         final Map.Entry entry = (Map.Entry)i.next();
         fireDataProvidedListMetadataChanged((DataProviderMetadata)entry
               .getKey(), (List)entry.getValue(), false);
      }

      final Map currentValues = PropertyUtils.describe(getForm());

      for (final Iterator i = formMetadata.getDataProviderIndexes().values()
            .iterator(); i.hasNext(); ) {
         DataProviderMetadata dataMeta = (DataProviderMetadata) i.next();
         String fieldName = dataMeta.getIndexField().getFieldName();
         Object indexes = currentValues.get(fieldName);

         if (getFormMetadata().getFieldMetadata(fieldName)
               .getEqualityComparator().equals(indexes, state.getValuesMap()
               .get(fieldName))) {
            continue;
         }

         fireDataProvidedIndexesChanged(dataMeta, 
               dataMeta.getSelectedIndexes(indexes));
      }


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

      PropertyUtils.copyProperties(getForm(), currentValues);
      state.getChangedMap().clear();
      state.getChangedMap().putAll(currentValues);

      fireValuesChanged(state.getValuesMap());
   }

   public FormState getFormState() throws Exception {
      return currentState;
   }

   public void invokeAction(String name, Map stringProperties) throws Exception {
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

      if (methodMetadata.getActionMetadata() != null && methodMetadata
            .getActionMetadata().isValidateBefore()) {
         final String formName = getForm().getClass().getName();

         final Map validationErrors = ValidationUtils.getInstance()
               .getMessages(ValidationUtils.getInstance().validate(
               stringProperties == null || stringProperties.isEmpty() ?
               getForm() : stringProperties, formName), formName);

         if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
         }
      } 

      invokeActionWithReset(methodMetadata, false, false);

      update();
   }

   protected void invokeActionWithReset(MethodMetadata metadata, 
         boolean firstCall, boolean conditionally) throws Exception {

      final boolean createPreviousState = createPreviousState();

      try {
         invokeAction(metadata, firstCall, conditionally);
      } catch (Exception e) {
         resetPreviousState(createPreviousState);

         throw e;
      } finally {
         releasePreviousState(createPreviousState);
      }
   }

   public void updateSelection(DataProviderMetadata dataProviderMetadata, 
         int[] selected) throws Exception {

      final boolean createPreviousState = createPreviousState();

      try {
         final List list = (List)currentState.getDataProvidedMap().get(
               dataProviderMetadata);

         dataProviderMetadata.populateSelectedFields(getForm(), list, selected);
         currentState.getDataProvidedIndexesMap().put(dataProviderMetadata,
               selected);

         update();
      } catch (Exception e) {
         resetPreviousState(createPreviousState);

         throw e;
      } finally {
         releasePreviousState(createPreviousState);
      }
   }

   protected boolean createPreviousState() {
      final boolean createPreviousState = previousState == null;

      if (createPreviousState) {
         previousState = createFormState(currentState);
      }

      return createPreviousState;
   }

   protected void resetPreviousState(boolean createPreviousState) throws Exception {
      if (createPreviousState) {
         reset(previousState);
      }
   }

   protected void releasePreviousState(boolean createPreviousState) {
      if (createPreviousState) {
         previousState = null;
      }
   }

   public void fireAllEvents(FormControllerListener listener) throws Exception {
      listener.enabledConditionsChanged(currentState.getEnabledMap());
      listener.visibleConditionsChanged(currentState.getVisibleMap());

      for (final Iterator i = currentState.getDataProvidedMap().entrySet()
            .iterator(); i.hasNext();) {
         final Map.Entry entry = (Map.Entry)i.next();
         listener.dataProvidedListChanged((DataProviderMetadata)entry.getKey(), 
               (List)entry.getValue());
      }

      final Map currentValues = PropertyUtils.describe(getForm());

      for (final Iterator i = formMetadata.getDataProviderIndexes().values()
            .iterator(); i.hasNext(); ) {
         DataProviderMetadata dataMetadata = (DataProviderMetadata) i.next();
         String fieldName = dataMetadata.getIndexField().getFieldName();
         Object currentValue = currentValues.get(fieldName);

         listener.dataProvidedIndexesChanged(dataMetadata,
               dataMetadata.getSelectedIndexes(currentValue));
      }

      for (final Iterator i = currentValues.entrySet().iterator(); 
            i.hasNext();) {
         Map.Entry entry = (Map.Entry)i.next();
         FieldMetadata fieldMeta = formMetadata.getFieldMetadata(entry.getKey()
               .toString());

         if (fieldMeta == null) {
            i.remove();

            continue;
         }
      }

      listener.valuesChanged(currentValues);
   }
}