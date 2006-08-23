/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.swt.widgets;

import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swt.GroupBinder;
import net.java.dev.genesis.ui.swt.SwtBinder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

public class ButtonGroupBinder implements GroupBinder {
   public BoundField bind(SwtBinder binder, Object group,
         FieldMetadata fieldMetadata) {
      return new ButtonGroupBoundField(binder, (Composite) group, fieldMetadata);
   }

   public class ButtonGroupBoundField implements BoundField {
      private final SwtBinder binder;
      private final Composite buttonGroup;
      private final FieldMetadata fieldMetadata;
      private final SelectionListener listener;

      public ButtonGroupBoundField(SwtBinder binder, Composite buttonGroup,
            FieldMetadata fieldMetadata) {
         this.binder = binder;
         this.buttonGroup = buttonGroup;
         this.fieldMetadata = fieldMetadata;
         addSelectionListener(listener = createSelectionListener());
      }

      protected SwtBinder getBinder() {
         return binder;
      }

      protected Composite getButtonGroup() {
         return buttonGroup;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      protected boolean isToggleStyle(Widget widget) {
         return (widget.getStyle() & (SWT.CHECK | SWT.RADIO | SWT.TOGGLE)) != 0;
      }

      protected void addSelectionListener(SelectionListener listener) {
         Control[] controls = buttonGroup.getChildren();

         for (int i = 0; i < controls.length; i++) {
            if (!isToggleStyle(controls[i]) || !(controls[i] instanceof Button)) {
               continue;
            }

            Button button = (Button) controls[i];
            button.addSelectionListener(listener);
         }
      }

      protected SelectionListener createSelectionListener() {
         return new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
               Button button = (Button) event.getSource();

               Control[] controls = buttonGroup.getChildren();

               for (int i = 0; i < controls.length; i++) {
                  if (!isToggleStyle(controls[i])
                        || !(controls[i] instanceof Button)) {
                     continue;
                  }

                  Button otherButton = (Button) controls[i];
                  if (button != otherButton) {
                     otherButton.setSelection(false);
                  }
               }

               if (!button.getSelection()) {
                  return;
               }

               getBinder().populateForm(getFieldMetadata(), getValue(button));
            }
         };
      }

      protected Object getValue(Button button) {
         return button.getData(SwtBinder.BUTTON_GROUP_SELECTION_VALUE);
      }

      public void setValue(Object value) throws Exception {
         Control[] controls = buttonGroup.getChildren();

         for (int i = 0; i < controls.length; i++) {
            if (!isToggleStyle(controls[i]) || !(controls[i] instanceof Button)) {
               continue;
            }

            Button button = (Button) controls[i];
            if (!equals(value, getValue(button))) {
               button.setSelection(false);
               continue;
            }

            button.setSelection(true);
         }
      }

      protected boolean equals(Object newValue, Object currentButtonValue) {
         if (newValue == null) {
            return currentButtonValue == null;
         }

         return newValue.equals(currentButtonValue);
      }

      public void setEnabled(boolean enabled) {
         Control[] controls = buttonGroup.getChildren();

         for (int i = 0; i < controls.length; i++) {
            if (!isToggleStyle(controls[i]) || !(controls[i] instanceof Button)) {
               continue;
            }

            controls[i].setEnabled(enabled);
         }
      }

      public void setVisible(boolean visible) {
         Control[] controls = buttonGroup.getChildren();

         for (int i = 0; i < controls.length; i++) {
            if (!isToggleStyle(controls[i]) || !(controls[i] instanceof Button)) {
               continue;
            }

            controls[i].setVisible(visible);
         }
      }

      public String getName() {
         return fieldMetadata.getName();
      }

      public void unbind() {
         if (listener == null) {
            return;
         }

         Control[] controls = buttonGroup.getChildren();

         for (int i = 0; i < controls.length; i++) {
            if (!isToggleStyle(controls[i]) || !(controls[i] instanceof Button)) {
               continue;
            }

            Button button = (Button) controls[i];
            button.removeSelectionListener(listener);
         }
      }
   }
}