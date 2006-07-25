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

import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.GroupBinder;
import net.java.dev.genesis.ui.swing.SwingBinder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

public class ButtonGroupBinder implements GroupBinder {
   public BoundField bind(SwingBinder binder, Object group,
      FieldMetadata fieldMetadata) {
      return new ButtonGroupBoundField(binder, (ButtonGroup) group,
         fieldMetadata);
   }

   public class ButtonGroupBoundField implements BoundField {
      private final SwingBinder binder;
      private final ButtonGroup buttonGroup;
      private final FieldMetadata fieldMetadata;
      private final ActionListener listener;

      public ButtonGroupBoundField(SwingBinder binder, ButtonGroup buttonGroup,
         FieldMetadata fieldMetadata) {
         this.binder = binder;
         this.buttonGroup = buttonGroup;
         this.fieldMetadata = fieldMetadata;
         addActionListener(listener = createActionListener());
      }

      protected SwingBinder getBinder() {
         return binder;
      }

      protected ButtonGroup getButtonGroup() {
         return buttonGroup;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      protected ActionListener createActionListener() {
         return new ActionListener() {
               public void actionPerformed(ActionEvent event) {
                  try {
                     AbstractButton button = (AbstractButton) event.getSource();

                     if (!button.isSelected()) {
                        return;
                     }

                     getBinder().getFormController().populate(
								Collections.singletonMap(fieldMetadata
										.getName(), button.getActionCommand()),
								getBinder().getConverters());
                  } catch (Exception e) {
                     getBinder().handleException(e);
                  }
               }
            };
      }

      protected void addActionListener(ActionListener listener) {
         Enumeration elements = buttonGroup.getElements();

         while (elements.hasMoreElements()) {
            AbstractButton button = (AbstractButton) elements.nextElement();
            button.addActionListener(listener);
         }
      }

      public void setValue(Object value) throws Exception {
    	 String actionName = value == null ? null : value.toString();

    	 Enumeration en = buttonGroup.getElements();
    	 while(en.hasMoreElements()) {
    		 AbstractButton button =
    	            (AbstractButton) en.nextElement();

    		 if (actionName == null || !actionName.equals(button.getActionCommand())) {
    			 buttonGroup.setSelected(button.getModel(), false);
    			 continue;
    		 }
    		 
    		 buttonGroup.setSelected(button.getModel(), true);
    		 break;
    	 }
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
