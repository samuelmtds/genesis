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
package net.java.dev.genesis.ui.swing.components;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import net.java.dev.genesis.ui.binding.AbstractBinder;
import net.java.dev.genesis.ui.binding.BindingStrategy;

import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.SwingBinder;

public class JTextComponentBinder extends AbstractComponentBinder {
   private final boolean trim;

   public JTextComponentBinder() {
      this(true);
   }

   public JTextComponentBinder(boolean trim) {
      this.trim = trim;
   }
   
   protected boolean isTrim() {
      return trim;
   }

   public BoundField bind(SwingBinder binder, Component component,
         FieldMetadata fieldMetadata) {
      return new JTextComponentBoundField(binder, (JTextComponent) component,
         fieldMetadata);
   }

   public static abstract class JTextComponentBindingStrategy 
         implements BindingStrategy {
      public void addListener(Object widget) {
         addListener((JTextComponent)widget);
      }

      protected abstract void addListener(JTextComponent component);

      public void removeListener(Object widget) {
         removeListener((JTextComponent)widget);
      }

      protected abstract void removeListener(JTextComponent component);
   }

   public class JTextComponentBoundField extends AbstractBoundMember
         implements BoundField {
      public class FocusBindingStrategy extends JTextComponentBindingStrategy {
         private FocusListener listener;

         protected void addListener(JTextComponent widget) {
            component.addFocusListener(listener = createFocusListener());
         }

         protected FocusListener createFocusListener() {
            return new FocusAdapter() {
               public void focusLost(FocusEvent event) {
                  updateForm();
               }
            };
         }

         public void setValue(Object value) {
            updateComponentValue(value);
         }

         public void removeListener(JTextComponent component) {
            if (listener != null) {
               component.removeFocusListener(listener);
            }
         }
      }

      public class DocumentBindingStrategy extends JTextComponentBindingStrategy {
         private DocumentListener listener;
         private boolean processing;

         public void addListener(JTextComponent component) {
            component.getDocument().addDocumentListener(
                  listener = createDocumentListener());
         }

         protected DocumentListener createDocumentListener() {
            return new DocumentListener() {
               public void changedUpdate(DocumentEvent e) {
                  update();
               }

               public void insertUpdate(DocumentEvent e) {
                  update();
               }

               public void removeUpdate(DocumentEvent e) {
                  update();
               }

               private void update() {
                  processing = true;

                  try {
                     updateForm();
                  } finally {
                     processing = false;
                  }
               }
            };
         }

         public void setValue(final Object value) {
            final Runnable updateValue = new Runnable() {
               public void run() {
                  updateComponentValue(value);
               }
            };

            if (!processing) {
               updateValue.run();
            } else {
               if (!fieldMetadata.getEqualityComparator().equals(
                     formatValue(value), getValue())) {
                  EventQueue.invokeLater(updateValue);
               }
            }
         }

         public void removeListener(JTextComponent component) {
            if (listener != null) {
               component.getDocument().removeDocumentListener(listener);
            }
         }
      }

      private final JTextComponent component;
      private final FieldMetadata fieldMetadata;
      private final BindingStrategy strategy;

      public JTextComponentBoundField(SwingBinder binder,
            JTextComponent component, FieldMetadata fieldMetadata) {
         super(binder, component);
         this.component = component;
         this.fieldMetadata = fieldMetadata;
         this.strategy = createBindingStrategy();

         strategy.addListener(component);
      }

      protected JTextComponent getComponent() {
         return component;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      protected BindingStrategy createBindingStrategy() {
         return AbstractBinder.BINDING_STRATEGY_AS_YOU_TYPE.equals(
               getBinder().getBindingStrategy((String)component
               .getClientProperty(AbstractBinder.BINDING_STRATEGY_PROPERTY))) ? 
               (BindingStrategy)new DocumentBindingStrategy() : 
               new FocusBindingStrategy();
      }

      public void updateForm() {
         getBinder().populateForm(getFieldMetadata(), getValue());
      }

      public String getValue() {
         return isTrim() ? component.getText().trim() : component.getText();
      }

      public String formatValue(Object value) {
         return getBinder().getFormatter(fieldMetadata).format(value);
      }

      public void updateComponentValue(Object value) {
         component.setText(formatValue(value));
      }

      public void setValue(Object value) {
         strategy.setValue(value);
      }

      public void unbind() {
         strategy.removeListener(component);
      }
   }
}