/*
 * The Genesis Project
 * Copyright (C) 2005-2007 Summa Technologies do Brasil Ltda.
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
import java.util.List;
import java.util.Map;

import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;

public class MockFormController implements FormController {
   private Map map = new HashMap();
   private Map exceptions = new HashMap();
   
   public Object get(Object key) {
      return map.get(key);
   }

   public void put(Object key, Object value) {
      map.put(key, value);
   }

   public Exception getException(Object key) {
      return (Exception) exceptions.get(key);
   }

   public void putException(Object key, Exception value) {
      exceptions.put(key, value);
   }

   public void clear() {
      map.clear();
   }
   
   public void clearExceptions() {
      exceptions.clear();
   }

   public void addFormControllerListener(FormControllerListener listener) {
      map.put("addFormControllerListener(listener)", listener);
      getFormControllerListeners().add(listener);
   }

   public void fireAllEvents(FormControllerListener listener) throws Exception {
      map.put("fireAllEvents(listener)", listener);
   }

   public Object getForm() {
      return map.get("this.form");
   }

   public Collection getFormControllerListeners() {
      Collection listeners = (Collection)map.get("this.listeners");
      if (listeners == null) {
         listeners = new ArrayList();
         map.put("this.listeners", listeners);
      }
      return listeners;
   }

   public FormMetadata getFormMetadata() {
      return (FormMetadata)map.get("this.formMetadata");
   }

   public FormState getFormState() throws Exception {
      return (FormState)map.get("this.formState");
   }

   public void invokeAction(String actionName, Map stringProperties)
         throws Exception {
      Exception ex = getException("invokeAction(String,Map)");
      if (ex != null) {
         throw ex;
      }
      
      map.put("invokeAction(actionName)", actionName);
      map.put("invokeAction(stringProperties)", stringProperties);
   }

   public boolean isSetup() {
      Boolean isSetup = (Boolean)map.get("setup()");
      return isSetup != null && isSetup.booleanValue();
   }

   public void populate(Map properties, Map converters) throws Exception {
      map.putAll(properties);
      map.put("populate(properties)", properties);
      map.put("populate(converters)", converters);
   }

   public boolean removeFormControllerListener(FormControllerListener listener) {
      map.put("removeFormControllerListener(listener)", listener);
      return getFormControllerListeners().remove(listener);
   }

   public void reset(FormState state) throws Exception {
      map.put("reset(state)", state);
      map.put("this.formState", state);
   }

   public void setForm(Object form) {
      map.put("setForm(form)", form);
      map.put("this.form", form);
   }

   public void setFormMetadata(FormMetadata metadata) {
      map.put("setFormMetadata(metadata)", metadata);
      map.put("this.formMetadata", metadata);
   }

   public void setup() throws Exception {
      map.put("setup()", Boolean.TRUE);
   }

   public void update() throws Exception {
      Exception ex = (Exception) exceptions.get("update()");
      if (ex != null) {
         throw ex;
      }

      map.put("update()", Boolean.TRUE);
   }

   public void updateSelection(DataProviderMetadata dataProviderMetadata,
         int[] selected) throws Exception {
      map.put("updateSelection(dataProviderMetadata)", dataProviderMetadata);
      map.put("updateSelection(selected)", selected);
   }

   public void resetSelection(DataProviderMetadata dataProviderMetadata, List dataProvided) throws Exception {
      map.put("resetSelection(dataProviderMetadata)", dataProviderMetadata);
      map.put("resetSelection(dataProvided)", dataProvided);
   }

   public int getMaximumEvaluationTimes() {
      return ((Integer)map.get("this.maximumEvaluationTimes")).intValue();
   }
   
   public void setMaximumEvaluationTimes(int times) {
      Integer i = new Integer(times);
      map.put("setMaximumEvaluationTimes(times)", i);
      map.put("this.maximumEvaluationTimes", i);
  }

   public boolean isResetOnDataProviderChange() {
      Boolean b = (Boolean)map.get("this.resetOnDataProviderChange");

      if (b == null) {
         setResetOnDataProviderChange(true);
         return true;
      }

      return b.booleanValue();
   }

   public void setResetOnDataProviderChange(boolean resetOnDataProviderChange) {
      Boolean b = new Boolean(resetOnDataProviderChange);
      map.put("setResetOnDataProviderChange(resetOnDataProviderChange)", b);
      map.put("this.resetOnDataProviderChange", b);
   }

   public ScriptContext getScriptContext() {
      return (ScriptContext)map.get("this.scriptContext");
   }
}