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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.java.dev.genesis.reflection.MethodEntry;
import net.java.dev.genesis.ui.controller.DefaultFormController;
import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//TODO: call reset() in appropriate points
public class ThinletBinder {
   private static final Log log = LogFactory.getLog(ThinletBinder.class);
   private static final String[] supportedFieldWidgets = new String[] {
         BaseThinlet.CHECKBOX, BaseThinlet.COMBOBOX, BaseThinlet.LIST,
         BaseThinlet.PASSWORD_FIELD, BaseThinlet.SLIDER, BaseThinlet.SPINBOX, 
         BaseThinlet.TEXTFIELD, BaseThinlet.TEXTAREA, BaseThinlet.TOGGLE_BUTTON};
   private static final String[] fieldsChangedByAction = new String[] {
         BaseThinlet.COMBOBOX, BaseThinlet.LIST, BaseThinlet.TOGGLE_BUTTON};

   private final BaseThinlet thinlet;
   private final Object root;
   private final Object form;
   private final Map visibleStateMap = new HashMap();
   private final Map enabledStateMap = new HashMap();
   private final FormController controller;
   private final Collection boundFieldNames = new HashSet();

   public ThinletBinder(final BaseThinlet thinlet, final Object root, 
            final Object form) {
      this.thinlet = thinlet;
      this.root = root;
      this.form = form;
      this.controller = getController(form);
   }

   protected FormController getController(final Object form) {
      return new DefaultFormController();
   }

   public void bind() throws Exception {
      final FormMetadata formMetadata = getFormMetadata(form);
      controller.setForm(form);
      controller.setFormMetadata(formMetadata);
      controller.setup();

      bindFieldMetadatas(formMetadata);
      bindActionMetadatas(formMetadata);

      updateAndSave(formMetadata.getDataProviderMetadatas().values(), true);
   }

   protected void updateAndSave(Collection dataProviders, boolean displayAll) 
         throws Exception {
      invokeCallWhenActions();
      populateDataProviders(controller.getFormMetadata(), dataProviders);
      updateEnabledState();
      updateVisibleState();

      controller.save();

      Map toDisplay = null;
      if (displayAll) {
         toDisplay = PropertyUtils.describe(form);
         toDisplay.keySet().retainAll(boundFieldNames);
      } else {
         toDisplay = controller.getChangedMap();
      }

      if (log.isDebugEnabled()) {
         log.debug("Displaying: " + toDisplay);
      }

      thinlet.displayBean(toDisplay, root);
   }

   protected FormMetadata getFormMetadata(final Object form) {
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

         final String className = thinlet.getClass(components.get(0));
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
            } else if (Arrays.binarySearch(fieldsChangedByAction, className) > 0) {
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
      final String type = thinlet.getClass(component);

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
      updateAndSave(controller.getDataProviderActions(), false);
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

         final String className = thinlet.getClass(component);

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
      //TODO: handle validation
      if (log.isDebugEnabled()) {
         log.debug("Invoking action: " + name);
      }

      ((ActionMetadata)controller.getFormMetadata().getActionMetadatas().get(
            new MethodEntry(name, new String[0]))).invoke(form);
      controller.update();
      updateAndSave(controller.getDataProviderActions(), false);
   }

   protected void populateDataProviders(final FormMetadata formMetadata, 
         final Collection dataProviders) throws Exception {
      for (final Iterator i = dataProviders.iterator(); 
            i.hasNext(); ) {
         final DataProviderMetadata dataProviderMetadata = 
               (DataProviderMetadata)i.next();
         final String name = dataProviderMetadata.getDataProvider().getFieldName();
         final Object component = thinlet.find(root, name);

         if (component == null) {
            log.warn(name + " could not be found while populating using " + 
                  form.getClass());
            continue;
         }

         if (log.isDebugEnabled()) {
            log.debug("Populating " + name + " with " + dataProviderMetadata
                  .getMethodEntry().getMethodName());
         }

         final Object ret = dataProviderMetadata.invoke(form);
         final Collection items = (dataProviderMetadata.getDataProvider()
               .isArray()) ? Arrays.asList((Object[])ret) : (List)ret;

         thinlet.populateFromCollection(component, items, 
               thinlet.getProperty(component, "key").toString(), 
               thinlet.getProperty(component, "value").toString(), 
               Boolean.valueOf(thinlet.getProperty(component, "blank")
               .toString()).booleanValue());
      }
   }

   protected void invokeCallWhenActions() 
         throws Exception {
      for (final Iterator i = controller.getCallActions().iterator(); 
            i.hasNext(); ) {
         final ActionMetadata actionMetadata = (ActionMetadata)i.next();
         
         if (log.isDebugEnabled()) {
            log.debug("Calling " + actionMetadata.getMethodEntry().getMethodName() +
                  " automatically");
         }

         actionMetadata.invoke(form);
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