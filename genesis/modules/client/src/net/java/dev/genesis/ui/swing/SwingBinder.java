/*
 * The Genesis Project
 * Copyright (C) 2005-2006  Summa Technologies do Brasil Ltda.
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

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.RootPaneContainer;

import net.java.dev.genesis.ui.binding.AbstractBinder;
import net.java.dev.genesis.ui.binding.BoundAction;
import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.BoundMember;
import net.java.dev.genesis.ui.binding.ExceptionHandler;
import net.java.dev.genesis.ui.controller.FormControllerListener;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.swing.lookup.BreadthFirstComponentLookupStrategy;
import net.java.dev.genesis.ui.swing.lookup.ComponentLookupStrategy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SwingBinder extends AbstractBinder {
   private static final Log log = LogFactory.getLog(SwingBinder.class);

   public static final String GENESIS_BOUND = "genesis:boundField";
   public static final String WIDGET_GROUP_PROPERTY = "widgetGroup";
   public static final String ENABLED_GROUP_PROPERTY = "enabledGroup";
   public static final String VISIBLE_GROUP_PROPERTY = "visibleGroup";
   public static final String BLANK_PROPERTY = "blank";
   public static final String BLANK_LABEL_PROPERTY = "blankLabel";
   public static final String KEY_PROPERTY = "key";
   public static final String VALUE_PROPERTY = "value";
   public static final String BUTTON_GROUP_SELECTION_VALUE = "buttonGroupSelectionValue";
   public static final String BINDER_KEY = "genesis:SwingBinder";

   private final ComponentBinderRegistryFactory factory =
      ComponentBinderRegistryFactory.getInstance();
   private final Component root;
   private final ComponentLookupStrategy lookupStrategy;
   private final Map buttonGroupsMap = new HashMap();
   private final Map componentBinders = new HashMap();
   private final Map groupBinders = new IdentityHashMap();
   private final ActionListener defaultButtonListener;

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
      this(component, lookupStrategy, form, handler, true);
   }

   public SwingBinder(Component component,
      ComponentLookupStrategy lookupStrategy, Object form, Object handler, boolean bindDefaultButton) {
      super(form, handler);
      this.root = component;
      this.lookupStrategy = (lookupStrategy == null)
         ? createComponentLookupStrategy() : lookupStrategy;
      this.defaultButtonListener = bindDefaultButton ? createDefautButtonListener()
            : null;
   }

   public ComponentLookupStrategy getLookupStrategy() {
      return lookupStrategy;
   }

   public Component getRoot() {
      return root;
   }

   protected ExceptionHandler createExceptionHandler() {
      return new SwingExceptionHandler(getRoot());
   }

   public Component register(String name, Component component) {
      return getLookupStrategy().register(name, component);
   }

   public ComponentBinder registerComponentBinder(String componentName,
      ComponentBinder binder) {
      return (ComponentBinder) componentBinders.put(componentName, binder);
   }

   public ButtonGroup registerButtonGroup(String name, ButtonGroup buttonGroup) {
      buttonGroupsMap.put(name, buttonGroup);

      return buttonGroup;
   }

   public ButtonGroup registerButtonGroup(String name, ButtonGroup buttonGroup, GroupBinder groupBinder) {
      registerButtonGroup(name, buttonGroup);
      groupBinders.put(buttonGroup, groupBinder);

      return buttonGroup;
   }

   protected ComponentLookupStrategy createComponentLookupStrategy() {
      return new BreadthFirstComponentLookupStrategy();
   }

   protected ComponentBinder getComponentBinder(Component component) {
      ComponentBinder binder =
         (ComponentBinder) componentBinders.get(getLookupStrategy().getName(component));

      if (binder != null) {
         return binder;
      }

      return factory.get(component.getClass(), true);
   }

   protected GroupBinder getGroupBinder(ButtonGroup group) {
      GroupBinder binder = (GroupBinder) groupBinders.get(group);

      if (binder != null) {
         return binder;
      }

      return factory.getButtonGroupBinder();
   }

   public void bind() {
      super.bind();

      bindDefaultButton();
      markBound();
   }

   protected void bindDefaultButton() {
      if (defaultButtonListener == null || !(root instanceof RootPaneContainer)) {
         return;
      }

      final JButton defaultButton = ((RootPaneContainer) root).getRootPane()
            .getDefaultButton();

      if (defaultButton == null) {
         return;
      }

      defaultButton.addActionListener(defaultButtonListener);
   }
   
   protected ActionListener createDefautButtonListener() {
      return new ActionListener() {
         public void actionPerformed(ActionEvent event) {
            Component defaultButton = (Component)event.getSource();
            Component c = FocusManager.getCurrentManager().getFocusOwner();

            if (c != null) {
               c.dispatchEvent(new FocusEvent(defaultButton, FocusEvent.FOCUS_LOST));
            }

            defaultButton.dispatchEvent(new FocusEvent(defaultButton, FocusEvent.FOCUS_GAINED));
         }
      };
   }

   protected void markBound() {
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

         GroupBinder binder = getGroupBinder(group);

         if (binder == null) {
            log.warn("No GroupBinder registered for " + group.getClass());

            return null;
         }

         return binder.bind(this, group, fieldMetadata);
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

   protected FormControllerListener getFormControllerListener() {
      return (FormControllerListener) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class[] {FormControllerListener.class}, new InvocationHandler() {
               public Object invoke(Object proxy, final Method method, 
                     final Object[] args) throws Throwable {
                  if (method.getDeclaringClass() != FormControllerListener.class || 
                        EventQueue.isDispatchThread()) {
                     return method.invoke(SwingBinder.this, args);
                  }

                  final Object[] result = new Object[1];
                  final Exception[] exception = new Exception[1];

                  EventQueue.invokeAndWait(new Runnable() {
                     public void run() {
                        try {
                           result[0] = method.invoke(SwingBinder.this, args);
                        } catch (IllegalArgumentException ex) {
                           exception[0] = ex;
                        } catch (InvocationTargetException ex) {
                           exception[0] = ex;
                        } catch (IllegalAccessException ex) {
                           exception[0] = ex;
                        }
                     }
                  });

                  if (exception[0] != null) {
                     throw exception[0];
                  }

                  return result[0];
               }
            });
   }
}
