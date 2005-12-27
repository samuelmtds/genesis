/*
 * The Genesis Project
 * Copyright (C) 2005  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.swing;

import net.java.dev.genesis.text.Formatter;
import net.java.dev.genesis.text.FormatterRegistry;
import net.java.dev.genesis.ui.binding.AbstractBinder;
import net.java.dev.genesis.ui.binding.BoundAction;
import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.BoundMember;
import net.java.dev.genesis.ui.binding.ExceptionHandler;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.swing.components.ButtonGroupBinder;
import net.java.dev.genesis.ui.swing.lookup.BreadthFirstComponentLookupStrategy;
import net.java.dev.genesis.ui.swing.lookup.ComponentLookupStrategy;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;

public class SwingBinder extends AbstractBinder {
   public static final String GENESIS_BOUND = "genesis:boundField";
   private static final Log log = LogFactory.getLog(SwingBinder.class);

   private final ComponentBinderRegistryFactory factory =
      ComponentBinderRegistryFactory.getInstance();
   private final Component root;
   private final ComponentLookupStrategy lookupStrategy;
   private ExceptionHandler exceptionHandler;
   private final Map formatters = new HashMap();
   private final Map converters = new HashMap();
   private final Map buttonGroupsMap = new HashMap();
   private final Map componentBinders = new HashMap();

   public SwingBinder(Component component, Object form) {
      this(component, (ComponentLookupStrategy) null, form, component);
   }

   public SwingBinder(Component component,
      ComponentLookupStrategy lookupStrategy, Object form) {
      this(component, lookupStrategy, form, component);
   }

   public SwingBinder(Component component, Object form, Object handler) {
      this(component, (ComponentLookupStrategy) null, form, handler);
   }

   public SwingBinder(Component component,
      ComponentLookupStrategy lookupStrategy, Object form, Object handler) {
      super(form, handler);
      this.root = component;
      this.lookupStrategy = (lookupStrategy == null)
         ? createComponentLookupStrategy() : lookupStrategy;
   }

   public Map getConverters() {
      return converters;
   }

   public Map getFormatters() {
      return formatters;
   }

   public ComponentLookupStrategy getLookupStrategy() {
      return lookupStrategy;
   }

   public Component getRoot() {
      return root;
   }

   public void handleException(Throwable throwable) {
      if (exceptionHandler == null) {
         exceptionHandler = createExceptionHandler();
      }

      exceptionHandler.handleException(throwable);
   }

   protected ExceptionHandler createExceptionHandler() {
      return new SwingExceptionHandler(getRoot());
   }

   public ExceptionHandler getExceptionHandler() {
      return exceptionHandler;
   }

   public void setExceptionHandler(ExceptionHandler exceptionHandler) {
      this.exceptionHandler = exceptionHandler;
   }

   public ComponentBinder registerComponentBinder(String componentName,
      ComponentBinder binder) {
      return (ComponentBinder) componentBinders.put(componentName, binder);
   }

   public ButtonGroup registerButtonGroup(String name, ButtonGroup buttonGroup) {
      buttonGroupsMap.put(name, buttonGroup);

      return buttonGroup;
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

   protected ComponentLookupStrategy createComponentLookupStrategy() {
      return new BreadthFirstComponentLookupStrategy();
   }

   protected ComponentBinder getComponentBinder(Component component) {
      ComponentBinder binder =
         (ComponentBinder) componentBinders.get(component.getName());

      if (binder != null) {
         return binder;
      }

      return factory.get(component.getClass(), true);
   }

   protected BoundField createGroupBoundField(ButtonGroup group,
      FieldMetadata fieldMetadata) {
      return new ButtonGroupBinder().bind(this, group, fieldMetadata);
   }

   public void bind() throws Exception {
      super.bind();

      if (root instanceof JComponent) {
         ((JComponent) root).putClientProperty(GENESIS_BOUND, Boolean.TRUE);
      }
   }

   protected BoundField bindFieldMetadata(String name,
      FormMetadata formMetadata, FieldMetadata fieldMetadata) {
      final Component component = lookupStrategy.lookup(root, name);

      if (component == null) {
         final ButtonGroup group = (ButtonGroup) buttonGroupsMap.get(name);

         if (group == null) {
            log.warn(name + " could not be found while binding " +
               getForm().getClass());

            return null;
         }

         return createGroupBoundField(group, fieldMetadata);
      }

      ComponentBinder binder = getComponentBinder(component);

      if (binder == null) {
         log.warn("No ComponentBinder registered for " + component.getClass());

         return null;
      }

      return binder.bind(this, component, fieldMetadata);
   }

   protected BoundAction bindActionMetadata(String name,
      FormMetadata formMetadata, ActionMetadata actionMetadata) {
      final Component component = lookupStrategy.lookup(root, name);

      if (component == null) {
         log.warn(name + " could not be found while binding " +
            getForm().getClass());

         return null;
      }

      ComponentBinder binder = getComponentBinder(component);

      if (binder == null) {
         log.warn("No ComponentBinder registered for " + component.getClass());

         return null;
      }

      return binder.bind(this, component, actionMetadata);
   }

   protected BoundDataProvider bindDataProvider(String name,
      FormMetadata formMetadata, DataProviderMetadata dataProviderMetadata) {
      final Component component = lookupStrategy.lookup(root, name);

      if (component == null) {
         log.warn(name + " could not be found while binding " +
            getForm().getClass());

         return null;
      }

      ComponentBinder binder = getComponentBinder(component);

      if (binder == null) {
         log.warn("No ComponentBinder registered for " + component.getClass());

         return null;
      }

      return binder.bind(this, component, dataProviderMetadata);
   }

   public BoundMember getBoundMember(String name) {
      BoundMember member = super.getBoundMember(name);

      if (member == null) {
         member = (BoundMember) buttonGroupsMap.get(name);
      }

      return member;
   }

   public String format(String propertyName, Object value) {
      return format(getFormatters(), propertyName, value);
   }
}
