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
package net.java.dev.genesis.ui.thinlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.java.dev.genesis.reflection.MethodEntry;
import net.java.dev.genesis.ui.ValidationException;
import net.java.dev.genesis.ui.ValidationUtils;
import net.java.dev.genesis.ui.controller.DefaultFormController;
import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;
import net.java.dev.genesis.ui.thinlet.metadata.ThinletMetadata;
import net.java.dev.genesis.ui.thinlet.metadata.ThinletMetadataFactory;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import thinlet.Thinlet;

//TODO: call reset() in appropriate points
public class ThinletBinder {
   private static final Log log = LogFactory.getLog(ThinletBinder.class);
   /* these are not alphabetically ordered by accident or mere readability;
     it's needed so that binary searching works with them */
   private static final String[] supportedFieldWidgets = new String[] {
         BaseThinlet.CHECKBOX, BaseThinlet.COMBOBOX, BaseThinlet.LABEL, BaseThinlet.LIST,
         BaseThinlet.PASSWORD_FIELD, BaseThinlet.SLIDER, BaseThinlet.SPINBOX, 
         BaseThinlet.TEXTAREA, BaseThinlet.TEXTFIELD, BaseThinlet.TOGGLE_BUTTON};
   private static final String[] fieldsChangedByAction = new String[] {
         BaseThinlet.COMBOBOX, BaseThinlet.LIST, BaseThinlet.TOGGLE_BUTTON};

   private final BaseThinlet thinlet;
   private final Object root;
   private final Object form;
   private final Map visibleStateMap = new HashMap();
   private final Map enabledStateMap = new HashMap();
   private final FormController controller;
   private final Collection boundFieldNames = new HashSet();
   private final Map dataProvidedListByFieldName = new HashMap();
   
   private final Map dataProvided = new HashMap();

   public ThinletBinder(final BaseThinlet thinlet, final Object root, 
            final Object form) {
      this.thinlet = thinlet;
      this.root = root;
      this.form = form;
      this.controller = getController(form);
   }
   
   private ThinletMetadata getThinletMetadata(final BaseThinlet thinlet) {
      return ((ThinletMetadataFactory)thinlet).getThinletMetadata(thinlet.getClass());
   }

   protected FormController getController(final Object form) {
      //TODO: use introduction as well
      return new DefaultFormController();
   }

   public void bind() throws Exception {
      final FormMetadata formMetadata = getFormMetadata(form);
      controller.setForm(form);
      controller.setFormMetadata(formMetadata);
      controller.setup();

      final Collection dataProviders = new ArrayList(formMetadata.getDataProviderMetadatas().values());
      // Some actions could be data providers
      dataProviders.addAll(formMetadata.getActionMetadatas().values());
      
      bindFieldMetadatas(formMetadata);
      bindActionMetadatas(formMetadata);
      bindDataProviders(dataProviders);

      updateAndSave(dataProviders, true);
   }

   protected void updateAndSave(Collection dataProviders, boolean displayAll) 
         throws Exception {
      final Map changedMap = controller.getChangedMap();
      
      invokeCallWhenActions();
      populateDataProviders(controller.getFormMetadata(), dataProviders);
      controller.update();
      updateEnabledState();
      updateVisibleState();

      controller.save();

      Map toDisplay = null;
      if (displayAll) {
         toDisplay = PropertyUtils.describe(form);
         toDisplay.keySet().retainAll(boundFieldNames);
      } else {
         changedMap.putAll(controller.getChangedMap());
         toDisplay = changedMap;
      }

      if (log.isDebugEnabled()) {
         log.debug("Displaying: " + toDisplay);
      }

      thinlet.displayBean(toDisplay, root);
   }

   protected FormMetadata getFormMetadata(final Object form) {
      if (!(form instanceof FormMetadataFactory)) {
         throw new IllegalArgumentException(form + " should implement " + 
               "FormMetadataFactory; probably it should have been annotated " +
               "with @Form or your aop.xml/weaving process should be " +
               "properly configured.");
      }

      return ((FormMetadataFactory)form).getFormMetadata(form.getClass());
   }

   protected void bindFieldMetadatas(final FormMetadata formMetadata) {
      for (final Iterator i = formMetadata.getFieldMetadatas().entrySet()
            .iterator(); i.hasNext(); ) {
         final Map.Entry entry = (Map.Entry)i.next();
         final String name = entry.getKey().toString();
         final List components = findComponents(name);

         if (components.isEmpty()) {
            log.warn(name + " could not be found while binding " + form.getClass());
            continue;
         }

         final String className = Thinlet.getClass(components.get(0));
         final FieldMetadata fieldMetadata = (FieldMetadata)entry.getValue();

         if (Arrays.binarySearch(supportedFieldWidgets, className) < 0) {
            if (!fieldMetadata.isDisplayOnly()) {
               log.warn(name + " is not represented by an editable widget (" + 
                     className + ")");
            }

            continue;
         }

         if (fieldMetadata.isDisplayOnly()) {
            if (log.isDebugEnabled()) {
               log.debug(name + " is a display only field, skipping...");
            }
            
            continue;
         }

         if (log.isTraceEnabled()) {
            log.trace("Binding field " + name + " in " + 
                  form.getClass().getName() + " to " + className);
         }

         boundFieldNames.add(name);

         for (final Iterator it = components.iterator(); it.hasNext(); ) {
            final Object component = it.next();

            if (className.equals(BaseThinlet.CHECKBOX)) {
               thinlet.setMethod(component, "action", "setValue(" + 
                     thinlet.getName(component) + "," + 
                     thinlet.getName(component) + ".group)", root, this);
            } else if (Arrays.binarySearch(fieldsChangedByAction, className) > -1) {
               thinlet.setMethod(component, "action", "setValue(" + name + "," +
                     name + ".name)", root, this);
            } else {
               thinlet.setMethod(component, "focuslost", "setValue(" + name + 
                     "," + name + ".name)", root, this);
            }
         }
      }
   }

   private List findComponents(final String name) {
      final Object widget = thinlet.find(root, name);

      if (widget != null) {
         return Collections.singletonList(widget);
      }

      final List groupComponents = thinlet.getAllOfClass(root, 
            BaseThinlet.CHECKBOX);

      for (final Iterator  i = groupComponents.iterator(); i.hasNext(); ) {
         final Object component = i.next();

         if (!name.equals(thinlet.getGroup(component))) {
            i.remove();
         }
      }

      return groupComponents;
   }

   public void setValue(Object component, String name) throws Exception {
      final String type = Thinlet.getClass(component);

      String value = null;

      if (type.equals(BaseThinlet.CHECKBOX)) {
         if (!thinlet.isSelected(component)) {
            return;
         }

         value = name;
      } else if (type.equals(BaseThinlet.COMBOBOX) || type.equals(BaseThinlet.LIST)) {
         component = thinlet.getSelectedItem(component);
         
         value = thinlet.getName(component != null ? component : null);
      } else if (type.equals(BaseThinlet.PROGRESS_BAR) || type.equals(BaseThinlet.SLIDER)) {
         value = thinlet.getValue(component);
      } else {
         value = thinlet.getText(component);
      }

      final Map properties = new HashMap();
      properties.put(name, value);

      controller.populate(properties);

      try {
         updateAndSave(controller.getDataProviderActions(), false);
      } catch (Exception e) {
         controller.reset();
         updateAndSave(controller.getDataProviderActions(), false);
         throw e;
      }
   }

   protected void bindActionMetadatas(final FormMetadata formMetadata) {
      for (final Iterator i = formMetadata.getActionMetadatas().entrySet()
            .iterator(); i.hasNext(); ) {
         final Map.Entry entry = (Map.Entry)i.next();
         final String name = ((MethodEntry)entry.getKey()).getMethodName();
         final Object component = thinlet.find(root, name);

         if (component == null) {
            log.warn(name + " could not be found while binding " + form.getClass());
            continue;
         }

         final String className = Thinlet.getClass(component);

         if (!BaseThinlet.BUTTON.equals(className)) {
            log.warn(name + " is not represented by a button; it is a " + 
                  className);

            continue;
         }

         if (log.isTraceEnabled()) {
            log.trace("Binding action " + name + " in " + form.getClass().getName());
         }

         thinlet.setMethod(component, "action", "invokeAction(" + 
               name + ".name)", root, this);
      }
   }

   public void invokeAction(String name) throws Exception {
      if (log.isDebugEnabled()) {
         log.debug("Invoking action: " + name);
      }

      final ActionMetadata actionMetadata = ((ActionMetadata)controller
            .getFormMetadata().getActionMetadatas().get(new MethodEntry(name, 
            new String[0])));

      if (actionMetadata.isValidateBefore()) {
         final String formName = form.getClass().getName();

         final Map validationErrors = ValidationUtils.getInstance().getMessages(
                  ValidationUtils.getInstance().validate(form, formName), 
                  formName);

         if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
         }
      }
      
      invokeDataProviderMethod(actionMetadata, true);
      controller.update();
      updateAndSave(controller.getDataProviderActions(), false);
   }

   protected void bindDataProviders(final Collection dataProviders) {
      for (final Iterator i = dataProviders.iterator(); i.hasNext();) {
         final DataProviderMetadata dataProviderMetadata = 
               (DataProviderMetadata)i.next();
         if (!dataProviderMetadata.isProvider()) {
            continue;
         }
         
         final String name = getFieldName(dataProviderMetadata);
         final Object component = thinlet.find(root, name);

         if (component == null) {
            log.warn(name + " could not be found while binding data provider in " + 
                  form.getClass());
            continue;
         }

         final String className = Thinlet.getClass(component);

         if (!className.equals(BaseThinlet.TABLE)) {
            continue;
         }

         if (log.isDebugEnabled()) {
            log.debug("Binding an action method for " + name);
         }

         thinlet.setMethod(component, "action", "updateSelection(" + name
               + ".name," + name + ")", root, this);
         dataProvidedListByFieldName.put(name, dataProviderMetadata);
      }
   }

   protected String getFieldName(final DataProviderMetadata dataProviderMetadata) {
      return dataProviderMetadata.getObjectField() != null ? 
            dataProviderMetadata.getObjectField().getFieldName() : 
            dataProviderMetadata.getIndexField().getFieldName();
   }

   public void updateSelection(String name, Object table) throws Exception {
      final int[] selected = thinlet.getSelectedIndexes(table);
      final List list = (List)dataProvided.get(name);
      final DataProviderMetadata dataMeta = (DataProviderMetadata)dataProvidedListByFieldName
            .get(name);

      dataMeta.populateSelectedFields(form, list, selected);
      controller.update();
      updateAndSave(controller.getDataProviderActions(), false);
   }

   protected void populateDataProviders(final FormMetadata formMetadata, 
         final Collection dataProviders) throws Exception {
      for (final Iterator i = dataProviders.iterator(); 
            i.hasNext(); ) {
         final DataProviderMetadata dataProviderMetadata = 
               (DataProviderMetadata)i.next();
         
         invokeDataProviderMethod(dataProviderMetadata, false);
      }
   }
   
   protected void invokeDataProviderMethod(
         final DataProviderMetadata dataProviderMetadata, boolean invokeActions) throws Exception {
      
      if (!dataProviderMetadata.isProvider()) {
         if (invokeActions) {
            final String actionName = dataProviderMetadata.getName();
            final ThinletMetadata thinletMeta = getThinletMetadata(thinlet);
            if(thinletMeta.invokePreAction(thinlet, actionName)){
               dataProviderMetadata.invoke(form);
               thinletMeta.invokePosAction(thinlet, actionName);
            }
         }
         return;
      }

      final String name = getFieldName(dataProviderMetadata);
      final Object component = thinlet.find(root, name);

      if (component == null) {
         log.warn(name + " could not be found while populating using "
               + form.getClass());
         return;
      }

      if (log.isDebugEnabled()) {
         log.debug("Populating " + name + " with "
               + dataProviderMetadata.getMethodEntry().getMethodName());
      }

      final Object ret = dataProviderMetadata.invoke(form);
      final List items = (dataProviderMetadata.getObjectField().isArray()) ? Arrays
            .asList((Object[]) ret)
            : (List) ret;
      dataProvided.put(name, items);
      final String className = Thinlet.getClass(component);

      if (className.equals(BaseThinlet.TABLE)) {
         dataProviderMetadata.resetSelectedFields(form);
         thinlet.populateFromCollection(component, items);
      } else if (className.equals(BaseThinlet.COMBOBOX)) {
         final String key = (String) thinlet.getProperty(component, "key");

         if (key == null) {
            throw new PropertyMisconfigurationException("Property 'key' "
                  + "must be configured for the widget named " + name);
         }

         final String value = (String) thinlet.getProperty(component, "value");
         final Object blankObject = thinlet.getProperty(component, "blank");
         boolean blank;

         if (blankObject == null) {
            blank = false;
         } else if (blankObject instanceof String) {
            blank = Boolean.valueOf(blankObject.toString()).booleanValue();
         } else if (blankObject instanceof Boolean) {
            blank = ((Boolean) blankObject).booleanValue();
         } else {
            throw new PropertyMisconfigurationException("Property 'blank' "
                  + "for the widget named " + name + " must either be left "
                  + "empty or contain a boolean value");
         }

         thinlet.populateFromCollection(component, items, key, value, blank);
      } else {
         // TODO: only table and combobox are implemented
         throw new UnsupportedOperationException(className + " is not "
               + "supported for data providing");
      }
   }

   protected void invokeCallWhenActions() throws Exception {
      for (final Iterator i = controller.getCallActions().iterator(); 
            i.hasNext(); ) {
         final ActionMetadata actionMetadata = (ActionMetadata)i.next();
         
         if (log.isDebugEnabled()) {
            log.debug("Calling " + actionMetadata.getMethodEntry().getMethodName() +
                  " automatically");
         }
         
         invokeDataProviderMethod(actionMetadata, true);
      }
   }

   protected void updateEnabledState() {
      final Collection enabledStateEntries = enabledStateMap.entrySet();

      for (final Iterator i = controller.getEnabledMap().entrySet().iterator();
            i.hasNext(); ) {
         final Map.Entry entry = (Map.Entry)i.next();

         if (!enabledStateEntries.contains(entry)) {
            if (log.isDebugEnabled()) {
               log.debug("Changing " + entry.getKey() + " enabled state to " + 
                     entry.getValue());
            }

            thinlet.setEnabled(thinlet.find(root, entry.getKey().toString()), 
                  ((Boolean)entry.getValue()).booleanValue());
         }
      }

      enabledStateMap.clear();
      enabledStateMap.putAll(controller.getEnabledMap());
   }

   protected void updateVisibleState() {
      final Collection visibleStateEntries = visibleStateMap.entrySet();

      for (final Iterator i = controller.getVisibleMap().entrySet().iterator();
            i.hasNext(); ) {
         final Map.Entry entry = (Map.Entry)i.next();

         if (!visibleStateEntries.contains(entry)) {
            if (log.isDebugEnabled()) {
               log.debug("Changing " + entry.getKey() + " visible state to " + 
                     entry.getValue());
            }

            thinlet.setVisible(thinlet.find(root, entry.getKey().toString()), 
                  ((Boolean)entry.getValue()).booleanValue());
         }
      }

      visibleStateMap.clear();
      visibleStateMap.putAll(controller.getVisibleMap());
   }
}