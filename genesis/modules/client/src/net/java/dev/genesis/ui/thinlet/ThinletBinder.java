/*
 * The Genesis Project
 * Copyright (C) 2004-2005  Summa Technologies do Brasil Ltda.
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

import net.java.dev.genesis.helpers.TypeChecker;
import net.java.dev.genesis.reflection.MethodEntry;
import net.java.dev.genesis.text.Formatter;
import net.java.dev.genesis.ui.ValidationUtils;
import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.controller.FormControllerFactory;
import net.java.dev.genesis.ui.controller.FormControllerListener;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;
import net.java.dev.genesis.ui.metadata.MethodMetadata;
import net.java.dev.genesis.ui.metadata.ViewMetadata;
import net.java.dev.genesis.ui.metadata.ViewMetadataFactory;

import org.apache.commons.beanutils.Converter;
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
         BaseThinlet.CHECKBOX, BaseThinlet.COMBOBOX, BaseThinlet.LIST,
         BaseThinlet.SPINBOX, BaseThinlet.TOGGLE_BUTTON };

   private final BaseThinlet thinlet;
   private final Object root;
   private final Object form;
   private final Object handler;
   private final FormController controller;

   private int componentSearchDepth = 0;
   private List groupComponents;

   private final Collection boundFieldNames = new HashSet();
   private final Map dataProvided = new HashMap();

   private final Map enabledWidgetGroupMap = new HashMap();
   private final Map visibleWidgetGroupMap = new HashMap();

   private final Map formatters = new HashMap();
   private final Map converters = new HashMap();

   public ThinletBinder(final BaseThinlet thinlet, final Object root, 
         final Object form) {
      this(thinlet, root, form, thinlet);
   }

   public ThinletBinder(final BaseThinlet thinlet, final Object root, 
         final Object form, final Object handler) {
      this.thinlet = thinlet;
      this.root = root;
      this.form = form;
      this.handler = handler;
      this.controller = getFormController(form);
   }
   
   protected ViewMetadata getViewMetadata(final Object handler) {
      TypeChecker.checkViewMetadataFactory(handler);

      return ((ViewMetadataFactory)handler).getViewMetadata(handler.getClass());
   }

   protected FormController getFormController(final Object form) {
      TypeChecker.checkFormControllerFactory(form);

      return ((FormControllerFactory)form).getFormController(form);
   }

   protected FormMetadata getFormMetadata(final Object form) {
      TypeChecker.checkFormMetadataFactory(form);

      return ((FormMetadataFactory)form).getFormMetadata(form.getClass());
   }

   public void bind() throws Exception {
      final FormMetadata formMetadata = getFormMetadata(form);
      final Collection dataProviders = new ArrayList(formMetadata
            .getDataProviderMetadatas().values());

      bindFieldMetadatas(formMetadata);
      bindActionMetadatas(formMetadata);
      bindDataProviders(dataProviders);

      setupController();
   }

   protected void setupController() throws Exception {
      if (!controller.isSetup()) {
         controller.addFormControllerListener(this);
         controller.setup();
      } else {
         controller.fireAllEvents(this);
         controller.addFormControllerListener(this);
      }
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
            if (fieldMetadata.isWriteable()) {
               log.warn(name + " is not represented by an editable widget (" + 
                     className + ")");
            }

            continue;
         }

         createWidgetGroup(mainComponent, name);

         if (!fieldMetadata.isWriteable()) {
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

            if (className.equals(BaseThinlet.CHECKBOX) ||
                  className.equals(BaseThinlet.TOGGLE_BUTTON)) {
               thinlet.setMethod(component, "action", "setValue(" + 
                     thinlet.getName(component) + "," + 
                     thinlet.getName(component) + 
                     (thinlet.getGroup(component) == null ? ".name)" : ".group)"),
                     root, this);
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

   protected List findComponents(final String name) {
      final Object widget = thinlet.find(root, name);

      if (widget != null) {
         return Collections.singletonList(widget);
      }

      if (groupComponents == null) {
         groupComponents = thinlet.getAllOfClass(root, new String[] {
               BaseThinlet.CHECKBOX, BaseThinlet.TOGGLE_BUTTON }, 
               componentSearchDepth);
      }

      final List components = new ArrayList();

      for (final Iterator  i = groupComponents.iterator(); i.hasNext(); ) {
         final Object component = i.next();

         if (name.equals(thinlet.getGroup(component))) {
            components.add(component);
         }
      }

      return components;
   }

   public int getComponentSearchDepth() {
      return componentSearchDepth;
   }

   public void setComponentSearchDepth(int componentSearchDepth) {
      this.componentSearchDepth = componentSearchDepth;
   }

   protected void createWidgetGroup(Object component, String name) {
      final Collection enabledWidgetGroupCollection = new ArrayList();
      final Collection visibleWidgetGroupCollection = new ArrayList();

      enabledWidgetGroupCollection.add(name);
      visibleWidgetGroupCollection.add(name);

      final String widgetGroup = (String)thinlet.getProperty(component,
            "widgetGroup");
      final String enabledWidgetGroup = (String)thinlet.getProperty(component,
            "enabledWidgetGroup");
      final String visibleWidgetGroup = (String)thinlet.getProperty(component,
            "visibleWidgetGroup");

      if (widgetGroup != null) {
         final List widgets = Arrays.asList(widgetGroup.split(","));
         enabledWidgetGroupCollection.addAll(widgets);
         visibleWidgetGroupCollection.addAll(widgets);

         if (log.isDebugEnabled()) {
            log.debug("Adding " + widgetGroup + " as a widget group for " +
                  name + " in class " + form.getClass());
         }
      }
      if (enabledWidgetGroup != null) {
         enabledWidgetGroupCollection.addAll(Arrays.asList(enabledWidgetGroup.split(",")));

         if (log.isDebugEnabled()) {
            log.debug("Adding " + enabledWidgetGroup + " as a enabled widget group for " +
                  name + " in class " + form.getClass());
         }
      }
      if (visibleWidgetGroup != null) {
         visibleWidgetGroupCollection.addAll(Arrays.asList(visibleWidgetGroup.split(",")));

         if (log.isDebugEnabled()) {
            log.debug("Adding " + visibleWidgetGroup + " as a visible widget group for " +
                  name + " in class " + form.getClass());
         }
      }

      enabledWidgetGroupMap.put(name, enabledWidgetGroupCollection);
      visibleWidgetGroupMap.put(name, visibleWidgetGroupCollection);
   }

   public void setValue(Object component, String name) throws Exception {
      final String type = Thinlet.getClass(component);

      String value = null;

      if (type.equals(BaseThinlet.CHECKBOX) || type.equals(BaseThinlet.TOGGLE_BUTTON)) {
         if (thinlet.getGroup(component) != null) {
            if (!thinlet.isSelected(component)) {
               return;
            }
         
            value = name;
         } else {
            value = String.valueOf(thinlet.isSelected(component));
         }
      } else if (type.equals(BaseThinlet.COMBOBOX) || type.equals(BaseThinlet.LIST)) {
         component = thinlet.getSelectedItem(component);
         
         value = component != null ? thinlet.getName(component) : null;
      } else if (type.equals(BaseThinlet.PROGRESS_BAR) || type.equals(BaseThinlet.SLIDER)) {
         value = String.valueOf(thinlet.getValue(component));
      } else {
         value = thinlet.getText(component);
      }

      final Map properties = new HashMap();
      properties.put(name, value);

      controller.populate(properties, converters);
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

         final String name = dataProviderMetadata.getWidgetName();
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

      controller.updateSelection(dataMeta, selected);
   }

   public void invokeAction(String name) throws Exception {
      Map stringProperties = null;

      if (name != null) {
         final MethodMetadata methodMetadata = getMethodMetadata(name);

         if (methodMetadata != null && 
               methodMetadata.getActionMetadata() != null && 
               methodMetadata.getActionMetadata().isValidateBefore()) {
            stringProperties = getStringProperties();
            thinlet.populate(null, root, stringProperties, false);
         }
      }

      controller.invokeAction(name, stringProperties);
   }

   protected MethodMetadata getMethodMetadata(String name) {
      return getFormMetadata(form).getMethodMetadata(
            new MethodEntry(name, new String[0]));
   }

   protected Map getStringProperties() throws Exception {
      return ValidationUtils.getInstance().getPropertiesMap(form);
   }

   public void refresh() throws Exception {
      controller.update();
   }

   public Formatter registerFormatter(String key, Formatter formatter) {
      return (Formatter)formatters.put(key, formatter);
   }

   public Converter registerConverter(String key, Converter converter) {
      return (Converter)converters.put(key, converter);
   }

   public void valuesChanged(Map updatedValues) throws Exception {
      thinlet.displayBean(updatedValues, root, formatters);
   }

   public boolean beforeInvokingMethod(MethodMetadata methodMetadata) 
         throws Exception {
      return getViewMetadata(handler).invokeBeforeAction(handler, 
            methodMetadata.getName());
   }

   public void afterInvokingMethod(MethodMetadata methodMetadata) 
         throws Exception {
      getViewMetadata(handler).invokeAfterAction(handler, 
            methodMetadata.getName());
   }
   
   public void dataProvidedListChanged(DataProviderMetadata metadata, List items)
         throws Exception {
      final String name = metadata.getWidgetName();
      final Object component = thinlet.find(root, name);

      if (component == null) {
         log.warn(name + " could not be found while populating using "
               + form.getClass());
         return;
      }

      if (log.isDebugEnabled()) {
         log.debug("Populating " + name + " with " + metadata.getName());
      }

      final String className = Thinlet.getClass(component);

      if (className.equals(BaseThinlet.TABLE)) {
         if (!controller.isResetOnDataProviderChange()) {
            resetSelectedFields(metadata);
         }

         thinlet.populateFromCollection(component, items, formatters);
      } else if (className.equals(BaseThinlet.COMBOBOX) || 
               className.equals(BaseThinlet.LIST)) {
         //TODO: This parsing shouldn't occur every time
         final String key = (String) thinlet.getProperty(component, "key");

         if (key == null) {
            throw new PropertyMisconfigurationException("Property 'key' "
                  + "must be configured for the widget named " + name);
         }

         final String value = (String)thinlet.getProperty(component, "value");
         final boolean virtual = isVirtual(component, name);
         final boolean blank = isBlank(component, name);
         final String blankLabel = (String)thinlet.getProperty(component, 
               "blankLabel");

         thinlet.populateFromCollection(component, items, key, value, virtual, 
               blank, blankLabel, formatters);
      } else {
         throw new UnsupportedOperationException(className + " is not "
               + "supported for data providing");
      }
   }

   protected void resetSelectedFields(DataProviderMetadata meta) throws Exception {
      meta.resetSelectedFields(form);
   }

   public void dataProvidedIndexesChanged(DataProviderMetadata metadata,
         int[] selectedIndexes) {
      thinlet.setSelectedIndexes(metadata.getWidgetName(), selectedIndexes);
   }

   private boolean isBlank(Object component, String name) {
      return isBoolean(component, name, "blank");
   }

   private boolean isVirtual(Object component, String name) {
      return isBoolean(component, name, BaseThinlet.VIRTUAL);
   }

   private boolean isBoolean(Object component, String name, String propertyName) {
      final Object booleanObject = thinlet.getProperty(component, propertyName);

      boolean ret;

      if (booleanObject == null) {
         ret = false;
      } else if (booleanObject instanceof String) {
         ret = Boolean.valueOf(booleanObject.toString()).booleanValue();
      } else if (booleanObject instanceof Boolean) {
         ret = ((Boolean) booleanObject).booleanValue();
      } else {
         throw new PropertyMisconfigurationException("Property '" + 
               propertyName + "' " + "for the widget named " + name + " must " +
               "either be left empty or contain a boolean value");
      }

      return ret;
   }

   public void enabledConditionsChanged(Map updatedEnabledConditions) {
      conditionsChanged(updatedEnabledConditions, true);
   }

   public void visibleConditionsChanged(Map updatedVisibleConditions) {
      conditionsChanged(updatedVisibleConditions, false);
   }

   protected void conditionsChanged(Map updatedConditions, boolean enabled) {
      for (final Iterator i = updatedConditions.entrySet().iterator();
            i.hasNext(); ) {
         final Map.Entry entry = (Map.Entry)i.next();

         final Collection widgetGroup = getWidgetGroupCollection(
               entry.getKey().toString(), enabled);

         if (widgetGroup == null) {
            log.warn(entry.getKey() + (enabled ? " enabled" : " visible")
                  + " state should be changed, "
                  + "but could not be found in the view component for "
                  + form.getClass());
            continue;
         }

         if (log.isDebugEnabled()) {
            log.debug("Changing " + entry.getKey()
                  + (enabled ? " enabled" : " visible") + " state to "
                  + entry.getValue());
         }

         for (Iterator it = widgetGroup.iterator(); it.hasNext(); ) {
            final Object widget = thinlet.find(root, it.next().toString());

            if (widget == null) {
               log.warn(entry.getKey() + (enabled ? " enabled" : " visible")
                     + " state should be changed, "
                     + "but could not be found in the view component for "
                     + form.getClass());
               continue;
            }

            if (enabled) {
               thinlet.setEnabled(widget, ((Boolean)entry.getValue())
                     .booleanValue());
            } else {
               thinlet.setVisible(widget, ((Boolean)entry.getValue())
                     .booleanValue());
            }
         }
      }
   }

   protected Collection getWidgetGroupCollection(String fieldKey, boolean enabled) {
      return enabled ?
            (Collection)enabledWidgetGroupMap.get(fieldKey) :
            (Collection)visibleWidgetGroupMap.get(fieldKey);
   }
}