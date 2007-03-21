/*
 * The Genesis Project
 * Copyright (C) 2005-2007  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.binding;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.java.dev.genesis.reflection.MethodEntry;
import net.java.dev.genesis.text.Formatter;
import net.java.dev.genesis.text.FormatterRegistry;
import net.java.dev.genesis.ui.ValidationUtils;
import net.java.dev.genesis.ui.controller.DefaultFormControllerFactory;
import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.controller.FormControllerFactory;
import net.java.dev.genesis.ui.controller.FormControllerListener;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.DefaultFormMetadataFactory;
import net.java.dev.genesis.ui.metadata.DefaultViewMetadataFactory;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;
import net.java.dev.genesis.ui.metadata.MethodMetadata;
import net.java.dev.genesis.ui.metadata.ViewMetadata;
import net.java.dev.genesis.ui.metadata.ViewMetadataFactory;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractBinder implements FormControllerListener {
   private static final Log log = LogFactory.getLog(AbstractBinder.class);

   /**
    * The key used to store whether or not the widget is bound
    */   
   public static final String GENESIS_BOUND = "genesis:boundField";

   /**
    * The key used to store values for the widget group
    */   
   public static final String WIDGET_GROUP_PROPERTY = "genesis:widgetGroup";

   /**
    * The key used to store values for the enabled group
    */
   public static final String ENABLED_GROUP_PROPERTY = "genesis:enabledGroup";

   /**
    * The key used to store values for the visible group
    */
   public static final String VISIBLE_GROUP_PROPERTY = "genesis:visibleGroup";

   /**
    * The key used to store the property name used in a blank label
    */
   public static final String BLANK_LABEL_PROPERTY = "genesis:blankLabel";

   /**
    * The key used to store whether or not the widget has blank label
    */
   public static final String BLANK_PROPERTY = "genesis:blank";

   /**
    * The key used to store the property used as a key in a combo or list
    */
   public static final String KEY_PROPERTY = "genesis:key";

   /**
    * The key used to store the value property in a combo or list
    */   
   public static final String VALUE_PROPERTY = "genesis:value";

   /**
    * The key used to store the column names of a table
    */
   public static final String COLUMN_NAMES = "genesis:columnNames";

   /**
    * The key used to store whether or not the widget is associated with
    * a virtual property
    */
   public static final String VIRTUAL = "genesis:virtual";

   /**
    * The prefix for virtual properties
    */
   public static final String VIRTUAL_PREFIX = "virtual:";

   /**
    * The key used to store the value of the button in a button group
    */
   public static final String BUTTON_GROUP_SELECTION_VALUE = 
         "genesis:buttonGroupSelectionValue";

   public static final String BINDING_STRATEGY_PROPERTY = 
         "genesis:bindingStrategy";

   public static final String BINDING_STRATEGY_AS_YOU_TYPE = 
         "genesis:bindingStrategyAsYouType";

   private static String defaultBindingStrategy;
   private String bindingStrategy;

   private final Map boundFields = new HashMap();
   private final Map boundActions = new HashMap();
   private final Map boundDataProviders = new HashMap();
   private final Object root;
   private final Object form;
   private final Object handler;
   private final FormController controller;
   private final FormMetadata formMetadata;
   private final ViewMetadata viewMetadata;
   private ExceptionHandler exceptionHandler;
   private final LookupStrategy lookupStrategy;
   private final Map formatters = new HashMap();
   private final Map converters = new HashMap();
   private final Map buttonGroupsMap = new HashMap();
   private final Map componentBinders = new HashMap();
   private final Map groupBinders = new IdentityHashMap();

   public AbstractBinder(Object root, Object form, Object handler,
         LookupStrategy lookupStrategy) {
      this.root = root;
      this.form = form;
      this.handler = handler;
      this.controller = getFormController(form);
      this.formMetadata = getFormMetadata(form);
      this.viewMetadata = getViewMetadata(handler);
      this.lookupStrategy = (lookupStrategy == null) ? createLookupStrategy()
            : lookupStrategy;
   }

   public Object getForm() {
      return form;
   }

   /**
    * Retrieves the root widget of the binder
    *
    * @return the root widget of the binder
    */
   public Object getRoot() {
      return root;
   }

   protected FormController getFormController() {
      return controller;
   }

   protected ViewMetadata getViewMetadata(final Object handler) {
      if (handler instanceof ViewMetadataFactory) {
         return ((ViewMetadataFactory) handler).getViewMetadata(handler.getClass());
      }

      return new DefaultViewMetadataFactory().getViewMetadata(handler.getClass());
   }

   protected FormController getFormController(final Object form) {
      if (form instanceof FormControllerFactory) {
         return ((FormControllerFactory) form).getFormController(form);   
      }

      return new DefaultFormControllerFactory().getFormController(form);
   }

   protected FormMetadata getFormMetadata(final Object form) {
      if (form instanceof FormMetadataFactory) {
         return ((FormMetadataFactory) form).getFormMetadata(form.getClass());
      }

      return new DefaultFormMetadataFactory().getFormMetadata(form.getClass());
   }

   /**
    * Retrieves the lookup strategy in use by the binder
    *
    * @return the lookup strategy in use by the binder
    */
   public LookupStrategy getLookupStrategy() {
      return lookupStrategy;
   }

   /**
    * Creates and returns a new instance of LookupStrategy.
    *
    * @return a new instance of a LookupStrategy
    */
   protected abstract LookupStrategy createLookupStrategy();

   /**
    * Lookup the object registered with the given name
    * 
    * @param name the object's name
    * @return the object registered with the given name
    */
   public Object lookup(String name) {
      return getLookupStrategy().lookup(root, name);
   }

   /**
    * Lookup the Button Group registered with the given name
    * 
    * @param name the button-group's name
    * @return the button group registered with the given name
    */
   public Object lookupButtonGroup(String name) {
      return buttonGroupsMap.get(name);
   }

   /**
    * Returns the widget name
    *
    * @return the widget name
    */
   public String getName(Object object) {
      return getLookupStrategy().getName(object);
   }

   /**
    * Method that does the bind. It will look in the form and associate
    * fields, actions and dataproviders with components in the UI.
    * This method should be the last one used by a binder. 
    */
   public void bind() {
      try {
         bindFieldMetadatas(formMetadata);
         bindActionMetadatas(formMetadata);
         bindDataProviders(formMetadata);

         setupController();
         markBound();
      } catch (Exception e) {
         unbind();

         handleException(e);
      }
   }

   protected abstract void markBound();
   protected abstract void markUnbound();

   protected void bindFieldMetadatas(final FormMetadata formMetadata) {
      for (final Iterator i =
            formMetadata.getFieldMetadatas().entrySet().iterator();
            i.hasNext();) {
         final Map.Entry entry = (Map.Entry) i.next();

         BoundField boundField =
            bindFieldMetadata(entry.getKey().toString(), formMetadata,
               (FieldMetadata) entry.getValue());

         if (boundField != null) {
            boundFields.put(boundField.getName(), boundField);
         }
      }
   }

   protected void bindActionMetadatas(final FormMetadata formMetadata) {
      for (final Iterator i =
            formMetadata.getActionMetadatas().entrySet().iterator();
            i.hasNext();) {
         final Map.Entry entry = (Map.Entry) i.next();

         BoundAction boundAction =
            bindActionMetadata(((MethodEntry) entry.getKey()).getMethodName(),
               formMetadata, (ActionMetadata) entry.getValue());

         if (boundAction != null) {
            boundActions.put(boundAction.getName(), boundAction);
         }
      }
   }

   protected void bindDataProviders(final FormMetadata formMetadata) {
      for (final Iterator i =
            formMetadata.getDataProviderMetadatas().values().iterator();
            i.hasNext();) {
         final DataProviderMetadata dataProviderMetadata =
            (DataProviderMetadata) i.next();

         BoundDataProvider boundDataProvider =
            bindDataProvider(dataProviderMetadata.getWidgetName(),
               formMetadata, dataProviderMetadata);

         if (boundDataProvider != null) {
            boundDataProviders.put(boundDataProvider.getName(),
               boundDataProvider);

            bindDataProviderObjectField(dataProviderMetadata, boundDataProvider);
         }
      }
   }

   protected void bindDataProviderObjectField(
      DataProviderMetadata dataProviderMetadata,
      BoundDataProvider boundDataProvider) {
      if ((dataProviderMetadata.getObjectField() != null) &&
            boundDataProvider instanceof BoundField) {
         boundFields.put(dataProviderMetadata.getObjectField().getFieldName(),
            boundDataProvider);
      }
   }

   protected BoundField bindFieldMetadata(String name,
         FormMetadata formMetadata, FieldMetadata fieldMetadata) {
      final Object widget = lookup(name);

      if (widget == null) {
         final Object group = lookupButtonGroup(name);

         if (group == null) {
            log.warn(name + " could not be found while binding "
                  + getForm().getClass());

            return null;
         }

         GroupBinder binder = getGroupBinder(group);

         if (binder == null) {
            log.warn("No Binder registered for " + group.getClass());

            return null;
         }

         return binder.bind(this, group, fieldMetadata);
      }

      WidgetBinder binder = getWidgetBinder(widget);

      if (binder == null) {
         log.warn("No Binder registered for " + widget.getClass());

         return null;
      }

      return binder.bind(this, widget, fieldMetadata);
   }

   protected BoundAction bindActionMetadata(String name,
         FormMetadata formMetadata, ActionMetadata actionMetadata) {
      final Object widget = lookup(name);

      if (widget == null) {
         log.warn(name + " could not be found while binding "
               + getForm().getClass());

         return null;
      }

      WidgetBinder binder = getWidgetBinder(widget);

      if (binder == null) {
         log.warn("No Binder registered for " + widget.getClass());

         return null;
      }

      return binder.bind(this, widget, actionMetadata);
   }

   protected BoundDataProvider bindDataProvider(String name,
         FormMetadata formMetadata, DataProviderMetadata dataProviderMetadata) {
      final Object widget = lookup(name);

      if (widget == null) {
         log.warn(name + " could not be found while binding "
               + getForm().getClass());

         return null;
      }

      WidgetBinder binder = getWidgetBinder(widget);

      if (binder == null) {
         log.warn("No Binder registered for " + widget.getClass());

         return null;
      }

      return binder.bind(this, widget, dataProviderMetadata);
   }

   protected void setupController() throws Exception {
      if (!controller.isSetup()) {
         controller.addFormControllerListener(getFormControllerListener());
         controller.setup();
      } else {
         controller.fireAllEvents(this);
         controller.addFormControllerListener(getFormControllerListener());
      }
   }

   protected FormControllerListener getFormControllerListener() {
      return this;
   }

   public boolean beforeInvokingMethod(MethodMetadata methodMetadata)
            throws Exception {
      return viewMetadata.invokeBeforeAction(handler, methodMetadata.getName());
   }

   public void afterInvokingMethod(MethodMetadata methodMetadata)
            throws Exception {
      viewMetadata.invokeAfterAction(handler, methodMetadata.getName());
   }

   public void dataProvidedListChanged(DataProviderMetadata metadata, List items)
            throws Exception {
      final String name = metadata.getWidgetName();
      final BoundDataProvider boundDataProvider = getBoundDataProvider(name);

      if (boundDataProvider == null) {
         log.warn(name + " could not be found while populating using " +
            form.getClass());

         return;
      }

      if (log.isDebugEnabled()) {
         log.debug("Populating " + name + " with " + metadata.getName());
      }

      boundDataProvider.updateList(items);
   }

   public void dataProvidedIndexesChanged(DataProviderMetadata metadata,
      int[] selectedIndexes) {
      final String name = metadata.getWidgetName();
      final BoundDataProvider boundDataProvider = getBoundDataProvider(name);

      if (boundDataProvider == null) {
         log.warn(name + " could not be found while updating indexes using " +
            getForm().getClass());

         return;
      }

      if (log.isDebugEnabled()) {
         log.debug("Updating indexes " + name + " with " + metadata.getName());
      }

      boundDataProvider.updateIndexes(selectedIndexes);
   }

   public void valuesChanged(Map updatedValues) throws Exception {
      for (Iterator iter = updatedValues.entrySet().iterator(); iter.hasNext();) {
         Map.Entry entry = (Map.Entry) iter.next();
         BoundField boundField = getBoundField(entry.getKey().toString());

         if (boundField == null) {
            continue;
         }

         boundField.setValue(entry.getValue());
      }
   }

   public void enabledConditionsChanged(Map updatedEnabledConditions) {
      conditionsChanged(updatedEnabledConditions, true);
   }

   public void visibleConditionsChanged(Map updatedVisibleConditions) {
      conditionsChanged(updatedVisibleConditions, false);
   }

   protected void conditionsChanged(Map updatedConditions, boolean enabled) {
      for (final Iterator i = updatedConditions.entrySet().iterator();
            i.hasNext();) {
         final Map.Entry entry = (Map.Entry) i.next();
         BoundMember member = getBoundMember(entry.getKey().toString());

         if (member == null) {
            return;
         }

         if (log.isDebugEnabled()) {
            log.debug("Changing " + entry.getKey() +
               (enabled ? " enabled" : " visible") + " state to " +
               entry.getValue());
         }

         if (enabled) {
            member.setEnabled(((Boolean) entry.getValue()).booleanValue());
         } else {
            member.setVisible(((Boolean) entry.getValue()).booleanValue());
         }
      }
   }

   protected BoundMember getBoundMember(String name) {
      BoundMember member = getBoundField(name);

      if (member != null) {
         return member;
      }

      return getBoundAction(name);
   }

   protected BoundField getBoundField(String name) {
      return (BoundField) boundFields.get(name);
   }

   protected BoundAction getBoundAction(String name) {
      return (BoundAction) boundActions.get(name);
   }

   protected BoundDataProvider getBoundDataProvider(String name) {
      return (BoundDataProvider) boundDataProviders.get(name);
   }

   public void unbind() {
      unbindFields();
      unbindActions();
      unbindDataProviders();

      controller.removeFormControllerListener(getFormControllerListener());
      markUnbound();
   }

   protected void unbindFields() {
      unbind(boundFields);
   }

   protected void unbindActions() {
      unbind(boundActions);
   }

   protected void unbindDataProviders() {
      unbind(boundDataProviders);
   }

   protected void unbind(Map boundElements) {
      for (Iterator iter = boundElements.entrySet().iterator(); iter.hasNext();) {
         Map.Entry entry = (Map.Entry) iter.next();

         ((BoundElement) entry.getValue()).unbind();

         iter.remove();
      }
   }

   public void refresh() {
      try {
         controller.update();
      } catch (Exception e) {
         handleException(e);
      }
   }

   public void invokeAction(String name) {
      try {
         Map stringProperties = null;

         if (name != null) {
            final MethodMetadata methodMetadata = getMethodMetadata(name);

            if ((methodMetadata != null) &&
                  (methodMetadata.getActionMetadata() != null) &&
                  methodMetadata.getActionMetadata().isValidateBefore()) {
               stringProperties = getStringProperties();
            }
         }

         controller.invokeAction(name, stringProperties);
      } catch (Exception e) {
         handleException(e);
      }
   }

   protected MethodMetadata getMethodMetadata(String name) {
      return formMetadata.getMethodMetadata(new MethodEntry(name, new String[0]));
   }

   protected Map getStringProperties() throws Exception {
      Map stringProperties = ValidationUtils.getInstance().getPropertiesMap(form);

      for (Iterator iter = stringProperties.entrySet().iterator(); iter.hasNext();) {
         Map.Entry entry = (Map.Entry) iter.next();
         String fieldName = (String) entry.getKey();
         Object value = entry.getValue();

         if (value == null || "".equals(value)) {
            BoundField bound = (BoundField) boundFields.get(fieldName);

            if (bound == null) {
               continue;
            }

            entry.setValue(bound.getValue());
         }
      }

      return stringProperties;
   }

   public boolean isVirtual(String propertyName) {
      return propertyName != null && propertyName.startsWith(VIRTUAL_PREFIX);
   }

   public abstract boolean isVirtual(Object widget);

   public boolean isVirtual(Object widget, String propertyName) {
      return isVirtual(propertyName) || isVirtual(widget);
   }

   protected Formatter getVirtualFormatter(String name, String propertyName) {
      if (propertyName != null && propertyName.startsWith(VIRTUAL_PREFIX)) {
         propertyName = propertyName.substring(VIRTUAL_PREFIX.length());
      }

      String key = propertyName == null ? name : name + '.' + propertyName;
      Formatter virtualFormatter = (Formatter) formatters.get(key);

      if (virtualFormatter == null) {
         throw new IllegalArgumentException("There is no formatter "
               + "registered for virtual property " + key);
      }

      return virtualFormatter;
   }

   public String format(String name, String property, Object value) {
      return format(name, property, value, isVirtual(property));
   }

   public String format(String name, String property, Object value, boolean isVirtual) {
      if (isVirtual) {
         return getVirtualFormatter(name, property).format(value);
      }

      Formatter formatter = null;

      if (formatters != null) {
         final String key = property == null ? name + '.' : name + '.'
               + property;
         formatter = (Formatter) formatters.get(key);
      }

      return (formatter == null) ? FormatterRegistry.getInstance()
            .format(value) : formatter.format(value);
   }

   public Map getConverters() {
      return converters;
   }

   public Map getFormatters() {
      return formatters;
   }

   protected abstract ExceptionHandler createExceptionHandler();

   public ExceptionHandler getExceptionHandler() {
      return exceptionHandler;
   }

   public void setExceptionHandler(ExceptionHandler exceptionHandler) {
      this.exceptionHandler = exceptionHandler;
   }

   public void handleException(Throwable throwable) {
      if (exceptionHandler == null) {
         exceptionHandler = createExceptionHandler();
      }

      exceptionHandler.handleException(throwable);
   }

   public Formatter registerFormatter(String componentName,
         Formatter formatter) {
         return (Formatter) formatters.put(componentName, formatter);
   }

   public Converter registerConverter(String componentName,
         Converter converter) {
         return (Converter) converters.put(componentName, converter);
   }

   public Converter getConverter(FieldMetadata fieldMetadata) {
      Converter converter = (Converter) converters.get(fieldMetadata.getName());

      if (converter == null) {
         converter = fieldMetadata.getConverter();
      }

      return converter;
   }

   public Formatter getFormatter(FieldMetadata fieldMetadata) {
      Formatter formatter = (Formatter) formatters.get(fieldMetadata.getName());

      if (formatter == null) {
         formatter = FormatterRegistry.getInstance()
                                         .get(fieldMetadata.getFieldClass(),
                  true);
      }

      return formatter;
   }

   /**
    * Associates the specified widget binder with the field with the specified name.
    * Use this to register a custom widget binder to a specified field.
    * 
    * @param name the name with which the specified widget binder is to be associated.
    * @param binder the widget binder to be associated with the specified name.
    * @return previous widget associated with specified name, or <tt>null</tt>
    *          if none.
    */
   public WidgetBinder registerWidgetBinder(String name, WidgetBinder binder) {
      return (WidgetBinder) componentBinders.put(name, binder);
   }

   protected Object registerGroup(String name, Object buttonGroup) {
      buttonGroupsMap.put(name, buttonGroup);

      return buttonGroup;
   }

   protected Object registerGroup(String name, Object buttonGroup, Object groupBinder) {
      registerGroup(name, buttonGroup);
      groupBinders.put(buttonGroup, groupBinder);

      return buttonGroup;
   }

   protected WidgetBinder getWidgetBinder(Object widget) {
      WidgetBinder binder = (WidgetBinder) componentBinders.get(getName(widget));

      if (binder != null) {
         return binder;
      }

      return getDefaultWidgetBinderFor(widget);
   }

   protected abstract WidgetBinder getDefaultWidgetBinderFor(Object widget);

   protected GroupBinder getGroupBinder(Object group) {
      GroupBinder binder = (GroupBinder) groupBinders.get(group);

      if (binder != null) {
         return binder;
      }
      return getDefaultGroupBinderFor(group);
   }

   protected abstract GroupBinder getDefaultGroupBinderFor(Object group);

   /**
    * Invokes the specified action defined by <code>actionMetadata</code>.
    * All exceptions are handled by the method itself
    * 
    * @param actionMetadata the ActionMetadata that represents an action
    */
   public void invokeFormAction(ActionMetadata actionMetadata) {
      try {
         invokeAction(actionMetadata.getName());
      } catch (Exception e) {
         handleException(e);
      }
   }

   /**
    * Updates the form selection of the DataProvider 
    * defined by <code>dataProviderMetadata</code>.
    * All exceptions are handled by the method itself
    * 
    * @param dataProviderMetadata the DataProviderMetadata that represents a DataProvider
    * @param indexes the indexes used to update form selection
    */
   public void updateFormSelection(DataProviderMetadata dataProviderMetadata,
         int[] indexes) {
      try {
         getFormController().updateSelection(dataProviderMetadata, indexes);
      } catch (Exception e) {
         handleException(e);
      }
   }

   /**
    * Updates the form field defined by <code>fieldMetadata</code>.
    * All exceptions are handled by the method itself
    * 
    * @param fieldMetadata the FieldMetadata that represents a form field
    * @param value the value to be set
    */
   public void populateForm(FieldMetadata fieldMetadata, Object value) {
      try {
         getFormController().populate(
               Collections.singletonMap(fieldMetadata.getName(), value),
               getConverters());
      } catch (Exception e) {
         handleException(e);
      }
   }

   /**
    * Converts the UI selected indexes to be used in the form. If the widget
    * uses a blank label, 1 is subtracted from all indexes. If any
    * index is less than zero, the index is removed.
    * 
    * @param selected the selected indexes
    * @param isBlank true if the widget uses a blank label
    * @return the converted indexes to be used in the form
    */
   public int[] getIndexesFromUI(int[] selected, boolean isBlank) {
      if ((selected.length >= 1) && isBlank) {
         boolean remove = false;

         for (int i = 0; i < selected.length; i++) {
            selected[i] = selected[i] - 1;

            if (selected[i] < 0) {
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

      return selected;
   }

   /**
    * Converts the form selected indexes to be used in UI. If the widget
    * uses a blank label, 1 is added to all indexes. If any
    * index is less than zero, -1 is assigned to it.
    * 
    * @param selected the selected indexes
    * @param isBlank true if the widget uses a blank label
    * @return the converted indexes to be used in the UI
    */
   public int[] getIndexesFromController(int[] selected, boolean isBlank) {
      if (isBlank) {
         if (selected.length == 0) {
            return new int[] { -1 };
         }

         for (int i = 0; i < selected.length; i++) {
            if (selected[i] < 0) {
               selected[i] = -1;
               continue;
            }

            selected[i] = selected[i] + 1;
         }
      }

      return selected;
   }


   public static String getDefaultBindingStrategy() {
      return defaultBindingStrategy;
   }

   public static void setDefaultBindingStrategy(String aDefaultBindingStrategy) {
      defaultBindingStrategy = aDefaultBindingStrategy;
   }

   public String getBindingStrategy() {
      return bindingStrategy;
   }

   public void setBindingStrategy(String bindingStrategy) {
      this.bindingStrategy = bindingStrategy;
   }

   public String getBindingStrategy(String componentBindingStrategy) {
      if (componentBindingStrategy != null) {
         return componentBindingStrategy;
      }

      String bindingStrategy = getBindingStrategy();

      return (bindingStrategy != null) ? bindingStrategy : 
            getDefaultBindingStrategy();
   }
}
