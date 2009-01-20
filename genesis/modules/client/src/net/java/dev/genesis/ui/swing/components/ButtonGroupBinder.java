/*
 * The Genesis Project
 * Copyright (C) 2005-2008  Summa Technologies do Brasil Ltda.
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

import net.java.dev.genesis.helpers.EnumHelper;
import net.java.dev.genesis.ui.binding.AbstractBinder;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.GroupBinder;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.SwingBinder;

import net.java.dev.genesis.util.Bundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ButtonGroupBinder implements GroupBinder {
   private static final Log log = LogFactory.getLog(ButtonGroupBinder.class);

   private final boolean useActionCommandAsSelectedValue;

   public ButtonGroupBinder() {
      this(false);
   }

   public ButtonGroupBinder(boolean useActionCommandAsSelectedValue) {
      this.useActionCommandAsSelectedValue = useActionCommandAsSelectedValue;
   }

   public BoundField bind(AbstractBinder binder, Object group,
      FieldMetadata fieldMetadata) {
      return new ButtonGroupBoundField(binder, (ButtonGroup) group,
         fieldMetadata);
   }

   public class ButtonGroupBoundField implements BoundField {
      private final AbstractBinder binder;
      private final ButtonGroup buttonGroup;
      private final FieldMetadata fieldMetadata;
      private final ActionListener listener;

      public ButtonGroupBoundField(AbstractBinder binder, ButtonGroup buttonGroup,
         FieldMetadata fieldMetadata) {
         this.binder = binder;
         this.buttonGroup = buttonGroup;
         this.fieldMetadata = fieldMetadata;
         addActionListener(listener = createActionListener());
      }

      protected AbstractBinder getBinder() {
         return binder;
      }

      protected ButtonGroup getButtonGroup() {
         return buttonGroup;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      protected String format(Object value) {
         return binder.format(getName(), fieldMetadata.getFieldName(), value, false);
      }

      protected void addActionListener(ActionListener listener) {
         Enumeration elements = buttonGroup.getElements();

         while (elements.hasMoreElements()) {
            AbstractButton button = (AbstractButton) elements.nextElement();
            button.addActionListener(listener);
         }
      }

      protected ActionListener createActionListener() {
         return new ActionListener() {
            public void actionPerformed(ActionEvent event) {
               AbstractButton button = (AbstractButton) event.getSource();

               if (!button.isSelected()) {
                  return;
               }

               getBinder().populateForm(getFieldMetadata(), getValue(button));
            }
         };
      }

      protected Object getValue(AbstractButton button) {
         Object value;
         
         if (useActionCommandAsSelectedValue) {
            value = button.getActionCommand();
         } else {
            value = button.getClientProperty(
                  SwingBinder.BUTTON_GROUP_SELECTION_VALUE);
            
            if (value == null) {
               value = button.getName();
            }
         }

         if (value == null) {
            if (useActionCommandAsSelectedValue) {
               log.warn(
                     Bundle.getMessage(getClass(),
                     "USE_BUTTON_SETACTIONCOMMAND_SOMEVALUE_TO_DEFINE_ITS_VALUE", // NOI18N
                     button));
            } else {
               log.warn(
                     Bundle.getMessage(getClass(),
                     "USE_BUTTON_PUTCLIENTPROPERTY_SWINGBINDER_BUTTON_GROUP_SELECTION_VALUE_SOMEVALUE_OR_BUTTON_SETNAME_SOMEVALUE_TO_DEFINE_ITS_VALUE", // NOI18N
                     button));
            }
         }

         return value;
      }

      protected boolean equals(Object newValue, Object currentButtonValue) {
         if (newValue == null) {
            return currentButtonValue == null;
         }

         Class newClass = newValue.getClass();
         Class currentClass = currentButtonValue.getClass();

         if (newClass == String.class && currentClass != String.class) {
            currentButtonValue = EnumHelper.getInstance().isEnum(currentButtonValue) ?
                  EnumHelper.getInstance().getName(currentButtonValue) :
                  format(currentButtonValue);
         } else if (newClass != String.class && currentClass == String.class) {
            newValue = EnumHelper.getInstance().isEnum(newValue) ?
                  EnumHelper.getInstance().getName(newValue) :
                  format(newValue);
         }
               
         return newValue.equals(currentButtonValue);
      }

      public void setValue(Object value) throws Exception {
         if (useActionCommandAsSelectedValue && value != null) {
            value = getBinder().getFormatter(getFieldMetadata()).format(value);
         }

         Enumeration en = buttonGroup.getElements();
         while (en.hasMoreElements()) {
            AbstractButton button = (AbstractButton) en.nextElement();

            if (!equals(value, getValue(button))) {
               buttonGroup.setSelected(button.getModel(), false);
               continue;
            }

            buttonGroup.setSelected(button.getModel(), true);
            break;
         }
      }

      public String getValue() {
         Enumeration en = buttonGroup.getElements();
         while (en.hasMoreElements()) {
            AbstractButton button = (AbstractButton) en.nextElement();

            if (button.isSelected()) {
               return format(getValue(button));
            }
         }

         return null;
      }

      public void setEnabled(boolean enabled) {
         Enumeration elements = buttonGroup.getElements();

         while (elements.hasMoreElements()) {
            AbstractButton button = (AbstractButton) elements.nextElement();
            button.setEnabled(enabled);
         }
      }

      public void setVisible(boolean visible) {
         Enumeration elements = buttonGroup.getElements();

         while (elements.hasMoreElements()) {
            AbstractButton button = (AbstractButton) elements.nextElement();
            button.setVisible(visible);
         }
      }

      public String getName() {
         return fieldMetadata.getName();
      }

      public void unbind() {
         if (listener == null) {
            return;
         }

         Enumeration elements = buttonGroup.getElements();

         while (elements.hasMoreElements()) {
            AbstractButton button = (AbstractButton) elements.nextElement();
            button.removeActionListener(listener);
         }
      }
   }
}
