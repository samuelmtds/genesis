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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.java.dev.genesis.commons.jxpath.VariablesImpl;
import net.java.dev.genesis.commons.jxpath.functions.ExtensionFunctions;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
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
   private final Map last = new HashMap();
   private final Map lastSaved = new HashMap();
   private final Map enabledMap = new HashMap();
   private final Map visibleMap = new HashMap();

   public void setForm(Object form) {
      this.form = form;
   }

   protected Object getForm() {
      return form;
   }

   public void setFormMetadata(FormMetadata formMetadata) {
      this.formMetadata = formMetadata;
   }

   protected FormMetadata getFormMetadata() {
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
      ctx.getVariables().declareVariable("formMetadata", formMetadata);
      if (PropertyUtils.isWriteable(form, "context")) {
         PropertyUtils.setProperty(form, "context", getVariablesMap());
      }
      evaluate();
      last.putAll(PropertyUtils.describe(form));
      GenesisUtils.normalizeMap(last);
      lastSaved.clear();
      lastSaved.putAll(last);

      ctx.getVariables().declareVariable("last", last);
      ctx.getVariables().declareVariable("lastSaved", lastSaved);
   }

   public void populate(Map properties) throws Exception {
      populate(properties, true);
   }

   protected void populate(Map properties, boolean stringMap) throws Exception {
      Map.Entry entry;
      Object value;
      FieldMetadata fieldMeta;
      final Map toCopy = GenesisUtils.normalizeMap(new HashMap(properties));

      for (final Iterator i = toCopy.entrySet().iterator(); i.hasNext();) {
         entry = (Map.Entry) i.next();
         fieldMeta = formMetadata.getFieldMetadata(entry.getKey().toString());

         if (fieldMeta.isDisplayOnly()) {
            i.remove();
            continue;
         }

         value = (stringMap) ? fieldMeta.getConverter().convert(
               fieldMeta.getFieldClass(), entry.getValue()) : entry.getValue();

         if (fieldMeta.getEqualityComparator().equals(value,
               last.get(entry.getKey()))) {
            i.remove();
            continue;
         }

         if (log.isDebugEnabled()) {
            log.debug("Field '" + entry.getKey() + "' changed to '"
                  + entry.getValue() + "'");
         }

         entry.setValue(value);
      }

      if (toCopy.isEmpty()) {
         log.debug("Nothing changed.");
         return;
      }

      PropertyUtils.copyProperties(form, toCopy);
      evaluate();
      last.clear();
      last.putAll(PropertyUtils.describe(form));
   }

   protected void evaluate() throws Exception {
      evaluateClearOnConditions();
      evaluateNamedConditions();
      evaluateEnabledWhenConditions();
      evaluateVisibleWhenConditions();
   }

   protected void evaluateClearOnConditions() throws Exception {
      final Map fieldMetadatas = new HashMap(formMetadata.getFieldMetadatas());
      final Map toCopy = new HashMap();

      boolean changed;
      FieldMetadata fieldMetadata;
      Map.Entry entry;
      Map properties;

      do {
         changed = false;
         properties = PropertyUtils.describe(form);

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
      }

      PropertyUtils.copyProperties(form, toCopy);
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
      FieldMetadata fieldMetadata;

      for (final Iterator i = formMetadata.getFieldMetadatas().entrySet()
            .iterator(); i.hasNext();) {
         entry = (Map.Entry) i.next();
         fieldMetadata = (FieldMetadata) entry.getValue();

         if (fieldMetadata.getEnabledCondition() == null) {
            continue;
         }

         enabledMap.put(entry.getKey(), isSatisfied(fieldMetadata
               .getEnabledCondition()));

         if (log.isDebugEnabled()) {
            log.debug("EnabledWhen Condition for field '" + entry.getKey()
                  + "' evaluated as '" + enabledMap.get(entry.getKey()) + "'");
         }
      }
   }

   protected void evaluateVisibleWhenConditions() {
      Map.Entry entry;
      FieldMetadata fieldMetadata;

      for (final Iterator i = formMetadata.getFieldMetadatas().entrySet()
            .iterator(); i.hasNext();) {
         entry = (Map.Entry) i.next();
         fieldMetadata = (FieldMetadata) entry.getValue();

         if (fieldMetadata.getVisibleCondition() == null) {
            continue;
         }

         visibleMap.put(entry.getKey(), isSatisfied(fieldMetadata
               .getVisibleCondition()));

         if (log.isDebugEnabled()) {
            log.debug("VisibleWhen Condition for field '" + entry.getKey()
                  + "' evaluated as '" + visibleMap.get(entry.getKey()) + "'");
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

      last.clear();
      last.putAll(lastSaved);
   }

   public void save() throws Exception {
      if (log.isDebugEnabled()) {
         log.debug("saving form '" + form + "'");
      }
      lastSaved.clear();
      lastSaved.putAll(last);
   }
}