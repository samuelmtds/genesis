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
package net.java.dev.genesis.ui.swing.components;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.text.JTextComponent;

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

   public class JTextComponentBoundField extends AbstractBoundMember
         implements BoundField {
      private final JTextComponent component;
      private final FieldMetadata fieldMetadata;
      private final FocusListener listener;

      public JTextComponentBoundField(SwingBinder binder,
         JTextComponent component, FieldMetadata fieldMetadata) {
         super(binder, component);
         this.component = component;
         this.fieldMetadata = fieldMetadata;
         this.component.addFocusListener(listener = createFocusListener());
      }

      protected JTextComponent getComponent() {
         return component;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      protected FocusListener getListener() {
         return listener;
      }

      protected FocusListener createFocusListener() {
         return new FocusAdapter() {
            public void focusLost(FocusEvent event) {
               getBinder().populateForm(getFieldMetadata(), getValue());
            }
         };
      }

      public String getValue() {
         return isTrim() ? component.getText().trim() : component.getText();
      }

      public void setValue(Object value) throws Exception {
         component.setText(getBinder().getFormatter(fieldMetadata).format(value));
      }

      public void unbind() {
         if (listener != null) {
            component.removeFocusListener(listener);
         }
      }
   }
}
