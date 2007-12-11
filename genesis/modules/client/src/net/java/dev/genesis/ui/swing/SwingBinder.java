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
package net.java.dev.genesis.ui.swing;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.swing.ButtonGroup;
import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import net.java.dev.genesis.ui.binding.AbstractBinder;
import net.java.dev.genesis.ui.binding.ExceptionHandler;
import net.java.dev.genesis.ui.binding.GroupBinder;
import net.java.dev.genesis.ui.binding.LookupStrategy;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.controller.FormControllerListener;
import net.java.dev.genesis.ui.swing.lookup.BreadthFirstComponentLookupStrategy;

/**
 * <code>SwingBinder</code> is the default implementation of
 * <code>AbstractBinder</code> for Swing. This class is
 * used to connect a Swing <code>Component</code> to a <code>form</code>.
 * Events generated by the <code>Component</code> are handled by
 * <code>SwingBinder</code> and passed to the <code>form</code>.
 * If the <code>form</code> state changes, <code>SwingBinder</code> will
 * reflect that in <code>Component</code>.
 */
public class SwingBinder extends AbstractBinder {
   /**
    * The key used to store the binder in a component client property.
    */
   public static final String BINDER_KEY = "genesis:SwingBinder";

   private final ComponentBinderRegistry factory = ComponentBinderRegistry.getInstance();
   private ActionListener defaultButtonListener;
   private final boolean bindDefaultButton;
   private final WindowListener windowListener;
   private final FormControllerListener listener;

   /**
    * Constructs a new <code>SwingBinder</code> initialized with the
    * <code>component</code> and the <code>form</code>. The <code>component</code>
    * will act as a <code>ViewHandler</code> and all <code>BeforeAction</code>
    * and <code>AfterAction</code> methods will be delegated to the <code>component</code>.
    * 
    * @param component the component used in binding action
    * @param form the form to be used
    * @see AbstractBinder
    */
   public SwingBinder(Component component, Object form) {
      this(component, (LookupStrategy) null, form, component, true);
   }

   /**
    * Constructs a new <code>SwingBinder</code> initialized with the
    * <code>component</code>, the specified <code>lookupStrategy</code>
    * and the <code>form</code>. 
    *
    * @param component the component used in binding action
    * @param lookupStrategy the lookup strategy to use
    * @param form the form to be used
    * 
    * @see AbstractBinder
    */
   public SwingBinder(Component component,
      LookupStrategy lookupStrategy, Object form) {
      this(component, lookupStrategy, form, component, true);
   }

   /**
    * Constructs a new <code>SwingBinder</code> initialized with the
    * <code>component</code>, the <code>form</code> and the specified
    * <code>handler</code>. 
    *
    * @param component the component used in binding action
    * @param form the form to be used
    * @param handler the view handler to use
    * 
    * @see AbstractBinder
    */
   public SwingBinder(Component component, Object form, Object handler) {
      this(component, (LookupStrategy) null, form, handler, true);
   }

   /**
    * Constructs a new <code>SwingBinder</code> initialized with the
    * <code>component</code>, the <code>lookupStrategy</code>,
    * the <code>form</code> and the specified <code>handler</code>. 
    *
    * @param component the component used in binding action
    * @param lookupStrategy the lookup strategy to use
    * @param form the form to be used
    * @param handler the view handler to use
    * 
    * @see AbstractBinder
    */
   public SwingBinder(Component component,
         LookupStrategy lookupStrategy, Object form, Object handler) {
      this(component, lookupStrategy, form, handler, true);
   }

   /**
    * Constructs a new <code>SwingBinder</code> initialized with the
    * <code>component</code>, the <code>lookupStrategy</code>,
    * the <code>form</code> and the specified <code>handler</code>. 
    *
    * @param component the component used in binding action
    * @param lookupStrategy the lookup strategy to use
    * @param form the form to be used
    * @param handler the view handler to use
    * @param bindDefaultButton whether the binder should bind the default button
    * 
    * @see AbstractBinder
    */
   public SwingBinder(Component component,
      LookupStrategy lookupStrategy, Object form, Object handler, boolean bindDefaultButton) {
      super(component, form, handler, lookupStrategy);
      this.listener = createFormControllerListener();
      this.bindDefaultButton = bindDefaultButton;
      this.windowListener = isWindow() ? createWindowListener() : null;
   }

   /**
    * Creates and returns a new instance of SwingExceptionHandler.
    * Override to change the default ExceptionHandler to use.
    *
    * @return a new instance of an ExceptionHandler
    */
   protected ExceptionHandler createExceptionHandler() {
      return new SwingExceptionHandler((Component) getRoot());
   }

   /**
    * Creates and returns a new instance of BreadthFirstComponentLookupStrategy.
    * Override to change the default ComponentLookupStrategy to use.
    *
    * @return a new instance of a ComponentLookupStrategy
    */
   protected LookupStrategy createLookupStrategy() {
      return new BreadthFirstComponentLookupStrategy();
   }

   /**
    * Associates the specified component with the specified name.
    * 
    * @param name the name with which the specified component is to be associated.
    * @param component the component to be associated with the specified name.
    * @return previous component associated with specified name, or <tt>null</tt>
    *          if none.
    */
   public Component register(String name, Component component) {
      return (Component) getLookupStrategy().register(name, component);
   }

   /**
    * Associates the specified widget binder with the field with the specified name.
    * Use this to register a custom component binder to a specified field.
    * 
    * @param componentName the name with which the specified component binder is to be associated.
    * @param binder the component binder to be associated with the specified name.
    * @return previous component associated with specified name, or <tt>null</tt>
    *          if none.
    *
    * @deprecated Use <code>registerWidgetBinder(String,WidgetBinder)</code> instead
    */
   public WidgetBinder registerComponentBinder(String componentName,
         WidgetBinder binder) {
      return registerWidgetBinder(componentName, binder);
   }

   /**
    * Associates the specified ButtonGroup with the field with the specified name.
    * Use this to bind ButtonGroups.
    * 
    * @param name the name with which the specified ButtonGroup is to be associated.
    * @param buttonGroup the ButtonGroup to be associated with the specified name.
    * @return the buttonGroup.
    */
   public ButtonGroup registerButtonGroup(String name, ButtonGroup buttonGroup) {
      return (ButtonGroup) registerGroup(name, buttonGroup);
   }

   /**
    * Associates the specified ButtonGroup with the field with the specified name and
    * with the GroupBinder to use in this association. Use this to bind ButtonGroups.
    * 
    * @param name the name with which the specified ButtonGroup is to be associated.
    * @param buttonGroup the ButtonGroup to be associated with the specified name.
    * @param groupBinder the specified GroupBinder to use
    * @return the buttonGroup.
    */
   public ButtonGroup registerButtonGroup(String name, ButtonGroup buttonGroup, GroupBinder groupBinder) {
      return (ButtonGroup) registerGroup(name, buttonGroup, groupBinder);
   }

   public boolean isVirtual(Object widget) {
      return widget instanceof JComponent && isVirtual((JComponent) widget);
   }

   public boolean isVirtual(Component component) {
      return component instanceof JComponent
            && isVirtual((JComponent) component);
   }

   public boolean isVirtual(JComponent component) {
      return Boolean.TRUE.equals(component.getClientProperty(VIRTUAL));
   }

   protected WidgetBinder getDefaultWidgetBinderFor(Object object) {
      return factory.get(object.getClass(), true);
   }

   protected GroupBinder getDefaultGroupBinderFor(Object group) {
      return factory.getButtonGroupBinder();
   }

   /**
    * Method that does the bind. It will look in the form and associate
    * fields, actions and dataproviders with components in the UI.
    * This method should be the last one used by a binder. 
    */
   public void bind() {
      super.bind();

      configureListenerForRootPaneDefaultButton();
      bindDefaultButton();
      bindWindowListener();
   }

   public void unbind() {
      unbindWindowListener();
      unbindDefaultButton();

      super.unbind();
   }

   protected void markBound() {
      if (getRoot() instanceof JComponent) {
         ((JComponent) getRoot()).putClientProperty(GENESIS_BOUND, Boolean.TRUE);
      }
   }

   protected void markUnbound() {
      if (getRoot() instanceof JComponent) {
         ((JComponent) getRoot()).putClientProperty(GENESIS_BOUND, Boolean.FALSE);
      }
   }

   protected boolean isWindow() {
      return getRoot() instanceof Window;
   }

   protected WindowListener createWindowListener() {
      return new WindowAdapter() {
         public void windowClosed(WindowEvent event) {
            unbind();
         }
      };
   }
   
   protected void unbindWindowListener() {
      if (windowListener == null) {
         return;
      }

      ((Window) getRoot()).removeWindowListener(windowListener);
   }

   protected void bindWindowListener() {
      if (windowListener == null) {
         return;
      }

      ((Window) getRoot()).addWindowListener(windowListener);
   }

   protected void unbindDefaultButton() {
      unbindDefaultButton(getDefaultButton());
   }
   
   protected void unbindDefaultButton(final JButton defaultButton) {
      if (defaultButtonListener == null || defaultButton == null) {
         return;
      }

         defaultButton.removeActionListener(defaultButtonListener);
      }

   protected void bindDefaultButton() {
      bindDefaultButton(getDefaultButton());
   }
   
   protected void bindDefaultButton(final JButton defaultButton) {
      if (!bindDefaultButton || defaultButton == null) {
         return;
      }

      if (this.defaultButtonListener == null){
         this.defaultButtonListener = createDefautButtonListener();
   }
      defaultButton.addActionListener(this.defaultButtonListener);
   }

   protected boolean hasDefaultButton() {
      return getDefaultButton() != null;
   }

   protected JButton getDefaultButton() {
      final JRootPane root = getRootPane();
      return root == null ? null : root.getDefaultButton();
   }
   
   private JRootPane getRootPane() {
      return SwingUtilities.getRootPane((Component)getRoot());
   }
   
   private void configureListenerForRootPaneDefaultButton() {
      if (bindDefaultButton && getRootPane() != null){
         getRootPane().addPropertyChangeListener("defaultButton", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
               unbindDefaultButton((JButton)evt.getOldValue());
               bindDefaultButton((JButton)evt.getNewValue());
            }
         });
      }
   }
   
   protected ActionListener createDefautButtonListener() {
      return new ActionListener() {
         public void actionPerformed(ActionEvent event) {
            Component defaultButton = (Component)event.getSource();
            Component c = FocusManager.getCurrentManager().getFocusOwner();

            if (c == defaultButton){
               return;
            }
            
            if (c != null) {
               c.dispatchEvent(new FocusEvent(defaultButton, 
                     FocusEvent.FOCUS_LOST));
            }

            defaultButton.dispatchEvent(new FocusEvent(defaultButton, 
                  FocusEvent.FOCUS_GAINED));
         }
      };
   }

   protected FormControllerListener getFormControllerListener() {
      return listener;
   }

   protected FormControllerListener createFormControllerListener() {
      return (FormControllerListener) Proxy.newProxyInstance(Thread
            .currentThread().getContextClassLoader(),
            new Class[] { FormControllerListener.class },
            new InvocationHandler() {
               public Object invoke(Object proxy, final Method method,
                     final Object[] args) throws Throwable {

                  if (method.getDeclaringClass() != FormControllerListener.class
                        || EventQueue.isDispatchThread()) {
                     if (isHashCode(method)) {
                        return new Integer(SwingBinder.this.listener.hashCode());
                     } else if (isEqualMethod(method)) {
                        return Boolean
                              .valueOf(SwingBinder.this.listener == args[0]);
                     }

                     try {
                        return method.invoke(SwingBinder.this, args);
                     } catch (InvocationTargetException ite) {
                        throw ite.getTargetException();
                     }
                  }

                  final Object[] result = new Object[1];
                  final Throwable[] throwable = new Throwable[1];

                  EventQueue.invokeAndWait(new Runnable() {
                     public void run() {
                        try {
                           result[0] = method.invoke(SwingBinder.this, args);
                        } catch (IllegalArgumentException ex) {
                           throwable[0] = ex;
                        } catch (InvocationTargetException ex) {
                           throwable[0] = ex.getTargetException();
                        } catch (IllegalAccessException ex) {
                           throwable[0] = ex;
                        }
                     }
                  });

                  if (throwable[0] != null) {
                     throw throwable[0];
                  }

                  return result[0];
               }

               private boolean isEqualMethod(Method method) {
                  return "equals".equals(method.getName())
                        && method.getParameterTypes().length == 1
                        && method.getParameterTypes()[0] == Object.class;
               }

               private boolean isHashCode(Method method) {
                  return "hashCode".equals(method.getName())
                        && method.getParameterTypes().length == 0;
               }
            });
   }
}
