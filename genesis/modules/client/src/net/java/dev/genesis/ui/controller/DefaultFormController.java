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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.java.dev.genesis.commons.jxpath.VariablesImpl;
import net.java.dev.genesis.commons.jxpath.functions.ExtensionFunctions;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.MemberMetadata;
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
   public static final String FORM_METADATA_KEY = "genesis:formMetadata";
   public static final String CHANGED_MAP_KEY = "genesis:changedMap";
   public static final String LAST_MAP_KEY = "genesis:last";
   public static final String LAST_SAVED_MAP_KEY = "genesis:lastSaved";

   private static final Log log = LogFactory
         .getLog(DefaultFormController.class);

   private Object form;
   private FormMetadata formMetadata;
   private JXPathContext ctx;
   private final Map last = new HashMap();
   private final Map lastSaved = new HashMap();
   private final Map enabledMap = new HashMap();
   private final Map visibleMap = new HashMap();
   private final Collection callActions = new ArrayList();
   private final Collection dataProviderActions = new ArrayList();
   private final Map changedMap = new HashMap();

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

   public void setup() throws Exception {
      ctx = createJXPathContext();
      ctx.getVariables().declareVariable(FORM_METADATA_KEY, formMetadata);
      ctx.getVariables().declareVariable(CHANGED_MAP_KEY, changedMap);
      if (PropertyUtils.isWriteable(form, "context")
            && Map.class.isAssignableFrom(PropertyUtils.getPropertyType(form,
                  "context"))) {
         PropertyUtils.setProperty(form, "context", getVariablesMap());
      }

      evaluate(false);

      last.putAll(PropertyUtils.describe(form));
      lastSaved.clear();
      lastSaved.putAll(last);

      ctx.getVariables().declareVariable(LAST_MAP_KEY, last);
      ctx.getVariables().declareVariable(LAST_SAVED_MAP_KEY, lastSaved);
   }

   public void populate(Map properties) throws Exception {
      populate(properties, true);
   }

   protected void populate(Map properties, boolean stringMap) throws Exception {
      updateChangedMap(properties, stringMap, true);

      if (changedMap.isEmpty()) {
         log.debug("Nothing changed.");
         return;
      }

      PropertyUtils.copyProperties(form, changedMap);
      evaluate(true);
   }

   private void updateChangedMap(Map newData, boolean stringMap,
         boolean logMessages) {
      Object value;
      FieldMetadata fieldMeta;
      Map.Entry entry;

      changedMap.clear();
      changedMap.putAll(GenesisUtils.normalizeMap(newData));

      if (log.isDebugEnabled()) {
         log.debug("changedMap started as: " + changedMap);
      }

      for (final Iterator i = changedMap.entrySet().iterator(); i.hasNext();) {
         entry = (Map.Entry) i.next();
         fieldMeta = formMetadata.getFieldMetadata(entry.getKey().toString());

         if (fieldMeta.isDisplayOnly()) {
            i.remove();

            if (log.isDebugEnabled()) {
               log.debug(entry.getKey()
                     + " removed from changedMap for being a "
                     + " displayOnly field");
            }

            continue;
         }

         value = (stringMap) ? fieldMeta.getConverter().convert(
               fieldMeta.getFieldClass(), entry.getValue()) : entry.getValue();

         if (fieldMeta.getEqualityComparator().equals(value,
               last.get(entry.getKey()))) {
            i.remove();

            if (log.isDebugEnabled()) {
               log.debug(entry.getKey() + " removed from changedMap for being "
                     + " equal to previousValue; is " + entry.getValue()
                     + "; was:" + last.get(entry.getKey()));
            }

            continue;
         }

         if (/*logMessages && */log.isDebugEnabled()) {
            log.debug("Field '" + entry.getKey() + "' changed to '"
                  + entry.getValue() + "'; was " + last.get(entry.getKey()));
         }

         entry.setValue(value);
      }
   }

   public void update() throws Exception {
      updateChangedMap(PropertyUtils.describe(form), false, true);
      evaluate(true);
   }

   protected void evaluate(boolean updateMaps) throws Exception {
      evaluateNamedConditions();

      if (evaluateClearOnConditions()) {
         evaluateNamedConditions();
      }

      evaluateEnabledWhenConditions();
      evaluateVisibleWhenConditions();
      evaluateCallWhenConditions();
      evaluateDataProviderTriggers();

      if (!updateMaps) {
         return;
      }

      final Map newData = PropertyUtils.describe(form);
      updateChangedMap(newData, false, false);
      last.clear();
      last.putAll(newData);
   }

   protected boolean evaluateClearOnConditions() throws Exception {
      final Map fieldMetadatas = formMetadata.getFieldMetadatas();
      final Map toCopy = new HashMap();

      boolean changed;
      FieldMetadata fieldMetadata;
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
      } while (changed);

      if (toCopy.isEmpty()) {
         if (log.isDebugEnabled()) {
            log.debug("Nothing changed in ClearOn conditions.");
         }

         return false;
      }

      PropertyUtils.copyProperties(form, toCopy);
      changedMap.putAll(toCopy);

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

      final Map toEvaluate = new HashMap(formMetadata.getFieldMetadatas());
      toEvaluate.putAll(formMetadata.getActionMetadatas());

      for (final Iterator i = toEvaluate.entrySet().iterator(); i.hasNext();) {
         entry = (Map.Entry) i.next();
         memberMetadata = (MemberMetadata) entry.getValue();

         if (memberMetadata.getEnabledCondition() == null) {
            continue;
         }

         enabledMap.put(memberMetadata.getName(), isSatisfied(memberMetadata
               .getEnabledCondition()));

         if (log.isDebugEnabled()) {
            log.debug("EnabledWhen Condition for member '"
                  + memberMetadata.getName() + "' evaluated as '"
                  + enabledMap.get(memberMetadata.getName()) + "'");
         }
      }
   }

   protected void evaluateVisibleWhenConditions() {
      Map.Entry entry;
      MemberMetadata memberMetadata;

      final Map toEvaluate = new HashMap(formMetadata.getFieldMetadatas());
      toEvaluate.putAll(formMetadata.getActionMetadatas());

      for (final Iterator i = toEvaluate.entrySet().iterator(); i.hasNext();) {
         entry = (Map.Entry) i.next();
         memberMetadata = (MemberMetadata) entry.getValue();

         if (memberMetadata.getVisibleCondition() == null) {
            continue;
         }

         visibleMap.put(memberMetadata.getName(), isSatisfied(memberMetadata
               .getVisibleCondition()));

         if (log.isDebugEnabled()) {
            log.debug("VisibleWhen Condition for member '"
                  + memberMetadata.getName() + "' evaluated as '"
                  + visibleMap.get(memberMetadata.getName()) + "'");
         }
      }
   }

   protected void evaluateCallWhenConditions() {
      Map.Entry entry;
      ActionMetadata actionMetadata;

      for (final Iterator i = formMetadata.getActionMetadatas().entrySet()
            .iterator(); i.hasNext();) {
         entry = (Map.Entry) i.next();
         actionMetadata = (ActionMetadata) entry.getValue();

         if (actionMetadata.getCallCondition() == null) {
            continue;
         }

         final boolean satisfied = isSatisfied(
               actionMetadata.getCallCondition()).booleanValue();

         if (satisfied) {
            callActions.add(actionMetadata);
         }

         if (log.isDebugEnabled()) {
            log.debug("CallWhen Condition for method '" + entry.getKey()
                  + "' evaluated as '" + satisfied + "'");
         }
      }
   }

   protected void evaluateDataProviderTriggers() {
      Map.Entry entry;
      DataProviderMetadata dataProviderMetadata;

      for (final Iterator i = formMetadata.getDataProviderMetadatas()
            .entrySet().iterator(); i.hasNext();) {
         entry = (Map.Entry) i.next();
         dataProviderMetadata = (DataProviderMetadata) entry.getValue();

         if (dataProviderMetadata.getCallCondition() == null) {
            continue;
         }

         final boolean satisfied = isSatisfied(
               dataProviderMetadata.getCallCondition()).booleanValue();

         if (satisfied) {
            dataProviderActions.add(dataProviderMetadata);
         }

         if (log.isDebugEnabled()) {
            log.debug("CallWhen Condition for method '" + entry.getKey()
                  + "' evaluated as '" + satisfied + "'");
         }
      }
   }

   protected boolean isConditionSatisfied(CompiledExpression compiledEx) {
      return Boolean.TRUE.equals(compiledEx.getValue(ctx));
   }

   protected Boolean isSatisfied(CompiledExpression compiledEx) {
      return Boolean.valueOf(isConditionSatisfied(compiledEx));
   }

   public Map getEnabledMap() {
      return new HashMap(enabledMap);
   }

   public Map getVisibleMap() {
      return new HashMap(visibleMap);
   }

   public Collection getCallActions() {
      return new ArrayList(callActions);
   }

   public Collection getDataProviderActions() {
      return new ArrayList(dataProviderActions);
   }

   public Map getChangedMap() {
      return new HashMap(changedMap);
   }

   public void reset() throws Exception {
      if (log.isDebugEnabled()) {
         log.debug("reseting form '" + form + "'");
         Map.Entry entry;
         for (Iterator iter = lastSaved.entrySet().iterator(); iter.hasNext();) {
            entry = (Map.Entry) iter.next();
            log.debug("Last saved value for '" + entry.getKey() + "' is '"
                  + entry.getValue() + "'");
         }
      }

      populate(lastSaved, false);

      cleanUp();
   }

   public void save() throws Exception {
      if (log.isDebugEnabled()) {
         log.debug("saving form '" + form + "'");
      }

      cleanUp();
   }

   private void cleanUp() {
      lastSaved.clear();
      lastSaved.putAll(last);
      callActions.clear();
      dataProviderActions.clear();
   }
}