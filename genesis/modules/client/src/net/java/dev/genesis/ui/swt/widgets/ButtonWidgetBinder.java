/*
 * The Genesis Project
 * Copyright (C) 2006-2007  Summa Technologies do Brasil Ltda.
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

import net.java.dev.genesis.ui.binding.BoundAction;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swt.SWTBinder;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Widget;

public class ButtonWidgetBinder extends AbstractWidgetBinder {
   public BoundAction bind(SWTBinder binder, Widget widget,
         ActionMetadata actionMetadata) {
      if (isToggleStyle(widget)) {
         return null;
      }

      return new ButtonWidgetBoundAction(binder, (Button) widget,
            actionMetadata);
   }

   public BoundField bind(SWTBinder binder, Widget widget,
         FieldMetadata fieldMetadata) {
      if (!isToggleStyle(widget)) {
         return null;
      }

      return new ButtonWidgetBoundField(binder, (Button) widget, fieldMetadata);
   }

   protected boolean isToggleStyle(Widget widget) {
      return (widget.getStyle() & (SWT.CHECK | SWT.RADIO | SWT.TOGGLE)) != 0;
   }

   public class ButtonWidgetBoundAction extends AbstractBoundMember implements
         BoundAction {
      private final Button widget;
      private final ActionMetadata actionMetadata;
      private final SelectionListener listener;

      public ButtonWidgetBoundAction(SWTBinder binder, Button widget,
            ActionMetadata actionMetadata) {
         super(binder, widget);
         this.widget = widget;
         this.actionMetadata = actionMetadata;
         this.widget.addSelectionListener(listener = createSelectionListener());
      }

      protected ActionMetadata getActionMetadata() {
         return actionMetadata;
      }

      protected Button getWidget() {
         return widget;
      }

      protected SelectionListener getListener() {
         return listener;
      }

      protected SelectionListener createSelectionListener() {
         return new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
               getBinder().invokeFormAction(getActionMetadata());
            }
         };
      }

      public void unbind() {
         if (listener != null) {
            widget.removeSelectionListener(listener);
         }
      }
   }

   public class ButtonWidgetBoundField extends AbstractBoundMember implements
         BoundField {
      private final Button widget;
      private final FieldMetadata fieldMetadata;
      private final SelectionListener listener;

      public ButtonWidgetBoundField(SWTBinder binder, Button widget,
            FieldMetadata fieldMetadata) {
         super(binder, widget);
         this.widget = widget;
         this.fieldMetadata = fieldMetadata;
         this.widget.addSelectionListener(listener = createSelectionListener());
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      protected Button getWidget() {
         return widget;
      }

      protected SelectionListener getListener() {
         return listener;
      }

      protected SelectionListener createSelectionListener() {
         return new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
               getBinder().populateForm(fieldMetadata, getValue());
            }
         };
      }

      public Object getValue() {
         return Boolean.valueOf(widget.getSelection());
      }

      public void setValue(Object value) {
         Boolean b = (Boolean) BeanUtilsBean.getInstance().getConvertUtils()
               .lookup(Boolean.TYPE).convert(Boolean.TYPE, value);
         widget.setSelection((b != null) && b.booleanValue());
      }

      public void unbind() {
         if (listener != null) {
            widget.removeSelectionListener(listener);
         }
      }
   }
}
