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
import net.java.dev.genesis.ui.controller.DefaultFormController;
import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.controller.FormControllerListener;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;
import net.java.dev.genesis.ui.metadata.MethodMetadata;
import net.java.dev.genesis.ui.thinlet.metadata.ThinletMetadata;
import net.java.dev.genesis.ui.thinlet.metadata.ThinletMetadataFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import thinlet.Thinlet;

public class ThinletBinder implements FormControllerListener {
   private static final Log log = LogFactory.getLog(ThinletBinder.class);
   /* these are not alphabetically ordered by accident or mere readability;
     it's needed so that binary searching works with them */
   private static final String[] supportedFieldWidgets = new String[] {
         BaseThinlet.CHECKBOX, BaseThinlet.COMBOBOX, BaseThinlet.LABEL, 
         BaseThinlet.LIST, BaseThinlet.PASSWORD_FIELD, BaseThinlet.SLIDER, 
         BaseThinlet.SPINBOX, BaseThinlet.TEXTAREA, BaseThinlet.TEXTFIELD, 
         BaseThinlet.TOGGLE_BUTTON};
   private static final String[] fieldsChangedByAction = new String[] {
         BaseThinlet.COMBOBOX, BaseThinlet.LIST, BaseThinlet.SPINBOX, 
         BaseThinlet.TOGGLE_BUTTON};

   private final BaseThinlet thinlet;
   private final Object root;
   private final Object form;
   private final FormController controller;

   private final Collection boundFieldNames = new HashSet();
   private final Map dataProvided = new HashMap();

   private final Map widgetGroupMap = new HashMap();

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
      //TODO: use introduction as well; there must be a single controller per form instance
      return new DefaultFormController();
   }

   public void bind() throws Exception {
      final FormMetadata formMetadata = getFormMetadata(form);
      controller.setForm(form);
      controller.setFormMetadata(formMetadata);
      controller.addFormControllerListener(this);

      final Collection dataProviders = new ArrayList(formMetadata
            .getDataProviderMetadatas().values());
      dataProviders.addAll(formMetadata.getActionMetadatas().values());

      bindFieldMetadatas(formMetadata);
      bindActionMetadatas(formMetadata);
      bindDataProviders(dataProviders);

      controller.setup();
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

         final Object mainComponent = components.get(0);
         final String className = Thinlet.getClass(mainComponent);
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

         createWidgetGroup(mainComponent, name);

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

   // TODO: consider caching components
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

   private void createWidgetGroup(Object component, String name) {
      final Collection widgetGroupCollection = new ArrayList();
      widgetGroupCollection.add(name);

      final String widgetGroup = (String)thinlet.getProperty(component, 
            "widgetGroup");

      if (widgetGroup != null) {
         widgetGroupCollection.addAll(Arrays.asList(widgetGroup.split(",")));

         if (log.isDebugEnabled()) {
            log.debug("Adding " + widgetGroup + " as a widget group for " +
                  name + " in class " + form.getClass());
         }
      }

      widgetGroupMap.put(name, widgetGroupCollection);
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

         createWidgetGroup(component, name);
         
         if (log.isTraceEnabled()) {
            log.trace("Binding action " + name + " in " + form.getClass().getName());
         }

         thinlet.setMethod(component, "action", "invokeAction(" + 
               name + ".name)", root, this);
      }
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

         if (!(className.equals(BaseThinlet.TABLE) || 
               className.equals(BaseThinlet.COMBOBOX) ||
               className.equals(BaseThinlet.LIST))) {
            log.warn(className + " is not a supported widget for binding a " +
                  "data provider");
            continue;
         }

         createWidgetGroup(component, name);

         if (log.isDebugEnabled()) {
            log.debug("Binding an action method for " + name);
         }

         thinlet.setMethod(component, "action", "updateSelection(" + name
               + ".name," + name + ")", root, this);
         dataProvided.put(name, dataProviderMetadata);
      }
   }

   protected String getFieldName(final DataProviderMetadata dataProviderMetadata) {
      return dataProviderMetadata.getObjectField() != null ? 
            dataProviderMetadata.getObjectField().getFieldName() : 
            dataProviderMetadata.getIndexField().getFieldName();
   }

   //TODO: move more of this logic to controller
   public void updateSelection(String name, Object widget) throws Exception {
      int[] selected = thinlet.getSelectedIndexes(widget);
      
      final String widgetClass = BaseThinlet.getClass(widget);

      if (selected.length >= 1 && (BaseThinlet.COMBOBOX.equals(widgetClass) ||
            BaseThinlet.LIST.equals(widgetClass)) && isBlank(widget, name)) {
         boolean remove = false;

         for (int i = 0; i < selected.length; i++) {
            selected[i] = selected[i] - 1;

            if (selected[i] == -1) {
               remove = true;
            }
         }

         if (remove) {
            if (selected.length == 1) {
               selected = new int[0];
            } else {
               final int[] aux = selected;
               selected = new int[aux.length - 1];
               System.arraycopy(aux, 1, selected, 0, aux.length - 1);
            }
         }
      }

      final DataProviderMetadata dataMeta = (DataProviderMetadata)dataProvided
            .get(name);
      final List list = (List)controller.getFormState().getDataProvidedMap()
            .get(dataMeta);

      dataMeta.populateSelectedFields(form, list, selected);
      controller.update();
   }

   public void invokeAction(String name) throws Exception {
      controller.invokeAction(name);
   }

   public void refresh() throws Exception {
      controller.update();
   }
   
   public void valuesChanged(Map updatedValues) throws Exception {
      thinlet.displayBean(updatedValues, root);
   }

   public boolean beforeInvokingMethod(MethodMetadata methodMetadata) 
         throws Exception {
      return getThinletMetadata(thinlet).invokeBeforeAction(thinlet, 
            methodMetadata.getName());
   }

   public void afterInvokingMethod(MethodMetadata methodMetadata) 
         throws Exception {
      getThinletMetadata(thinlet).invokeAfterAction(thinlet, 
            methodMetadata.getName());
   }
   
   public void dataProvidedListChanged(DataProviderMetadata metadata, List items)
         throws Exception {
      final String name = getFieldName(metadata);
      final Object component = thinlet.find(root, name);

      if (component == null) {
         log.warn(name + " could not be found while populating using "
               + form.getClass());
         return;
      }

      if (log.isDebugEnabled()) {
         log.debug("Populating " + name + " with " + metadata.getMethodEntry()
               .getMethodName());
      }

      final String className = Thinlet.getClass(component);

      if (className.equals(BaseThinlet.TABLE)) {
         metadata.resetSelectedFields(form);
         thinlet.populateFromCollection(component, items);
      } else if (className.equals(BaseThinlet.COMBOBOX) || 
               className.equals(BaseThinlet.LIST)) {
         //TODO: This parsing shouldn't occur every time
         final String key = (String) thinlet.getProperty(component, "key");

         if (key == null) {
            throw new PropertyMisconfigurationException("Property 'key' "
                  + "must be configured for the widget named " + name);
         }

         final String value = (String)thinlet.getProperty(component, "value");
         final boolean blank = isBlank(component, name);
         final String blankLabel = (String)thinlet.getProperty(component, 
               "blankLabel");

         thinlet.populateFromCollection(component, items, key, value, blank, 
               blankLabel);
      } else {
         throw new UnsupportedOperationException(className + " is not "
               + "supported for data providing");
      }
   }

   private boolean isBlank(Object component, String name) {
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

      return blank;
   }

   public void enabledConditionsChanged(Map updatedEnabledConditions) {
      for (final Iterator i = updatedEnabledConditions.entrySet().iterator(); 
            i.hasNext(); ) {
         final Map.Entry entry = (Map.Entry)i.next();

         final Collection widgetGroup = (Collection)widgetGroupMap.get(
               entry.getKey().toString());

         if (widgetGroup == null) {
            log.warn(entry.getKey() + " enabled state should be changed, " +
                  "but could not be found in the view component for " + 
                  form.getClass());
            continue;
         }

         if (log.isDebugEnabled()) {
            log.debug("Changing " + entry.getKey() + " enabled state to " + 
                  entry.getValue());
         }

         for (Iterator it = widgetGroup.iterator(); it.hasNext(); ) {
            final Object widget = thinlet.find(root, it.next().toString());

            if (widget == null) {
               log.warn(entry.getKey() + " enabled state should be changed, " +
                     "but could not be found in the view component for " + 
                     form.getClass());
               continue;
            }

            thinlet.setEnabled(widget, ((Boolean)entry.getValue())
                  .booleanValue());
         }
      }
   }

   // TODO: merge logic with enabledConditionsChanged
   public void visibleConditionsChanged(Map updatedVisibleConditions) {
      for (final Iterator i = updatedVisibleConditions.entrySet().iterator();
            i.hasNext(); ) {
         final Map.Entry entry = (Map.Entry)i.next();

         final Collection widgetGroup = (Collection)widgetGroupMap.get(
               entry.getKey().toString());

         if (widgetGroup == null) {
            log.warn(entry.getKey() + " visible state should be changed, " +
                  "but could not be found in the view component for " + 
                  form.getClass());
            continue;
         }

         if (log.isDebugEnabled()) {
            log.debug("Changing " + entry.getKey() + " visible state to " + 
                  entry.getValue());
         }

         for (Iterator it = widgetGroup.iterator(); it.hasNext(); ) {
            final Object widget = thinlet.find(root, it.next().toString());

            if (widget == null) {
               log.warn(entry.getKey() + " visible state should be changed, " +
                     "but could not be found in the view component for " + 
                     form.getClass());
               continue;
            }

            thinlet.setVisible(widget, ((Boolean)entry.getValue())
                  .booleanValue());
         }
      }
   }
}