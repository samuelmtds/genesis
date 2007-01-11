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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

import org.apache.commons.beanutils.BeanUtilsBean;

import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.SwingBinder;

public class JToggleButtonComponentBinder extends AbstractComponentBinder {
   public BoundField bind(SwingBinder binder, Component component,
      FieldMetadata fieldMetadata) {
      return new JToggleButtonBoundField(binder, (JToggleButton) component,
         fieldMetadata);
   }

   public class JToggleButtonBoundField extends AbstractBoundMember
         implements BoundField {
      private final JToggleButton component;
      private final FieldMetadata fieldMetadata;
      private final ActionListener listener;

      public JToggleButtonBoundField(SwingBinder binder,
         JToggleButton component, FieldMetadata fieldMetadata) {
         super(binder, component);
         this.component = component;
         this.fieldMetadata = fieldMetadata;

         this.component.addActionListener(listener = createActionListener());
      }

      protected JToggleButton getComponent() {
         return component;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      protected ActionListener getListener() {
         return listener;
      }

      protected ActionListener createActionListener() {
         return new ActionListener() {
            public void actionPerformed(ActionEvent event) {
               getBinder().populateForm(getFieldMetadata(), getCurrentValue());
            }
         };
      }

      protected Object getCurrentValue() {
         return Boolean.valueOf(component.isSelected());
      }

      public String getValue() {
         return format(fieldMetadata, getCurrentValue());
      }

      protected boolean toBoolean(Object value) throws Exception {
         Boolean b = (Boolean) BeanUtilsBean.getInstance().getConvertUtils()
               .lookup(Boolean.TYPE).convert(Boolean.TYPE, value);

         return b.booleanValue();
      }

      public void setValue(Object value) throws Exception {
         deactivateListeners();

         try {
            component.setSelected(toBoolean(value));
         } finally {
            reactivateListeners();
         }
      }

      protected void deactivateListeners() {
         if (listener != null) {
            component.removeActionListener(listener);
         }
      }

      protected void reactivateListeners() {
         if (listener != null) {
            component.addActionListener(listener);
         }
      }

      public void unbind() {
         if (listener != null) {
            component.removeActionListener(listener);
         }
      }
   }
}
