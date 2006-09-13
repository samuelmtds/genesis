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
import net.java.dev.genesis.ui.swt.SWTBinder;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Widget;


public class SpinnerWidgetBinder extends AbstractWidgetBinder {
   public BoundField bind(SWTBinder binder, Widget widget,
      FieldMetadata fieldMetadata) {
      return new SpinnerBoundField(binder, (Spinner) widget,
         fieldMetadata);
   }

   public class SpinnerBoundField extends AbstractBoundMember
         implements BoundField {
      private final Spinner widget;
      private final FieldMetadata fieldMetadata;
      private final SelectionListener listener;

      public SpinnerBoundField(SWTBinder binder, Spinner widget,
         FieldMetadata fieldMetadata) {
         super(binder, widget);
         this.widget = widget;
         this.fieldMetadata = fieldMetadata;
         widget.addSelectionListener(listener = createSelectionListener());
      }

      protected Spinner getWidget() {
         return widget;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      protected SelectionListener getListener() {
         return listener;
      }

      protected SelectionListener createSelectionListener() {
         return new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
               getBinder().populateForm(getFieldMetadata(), getValue());
            }
         };
      }

      protected Object getValue() {
         return getBinder().getConverter(fieldMetadata).convert(String.class,
               new Integer(widget.getSelection()));
      }

      protected int toInt(Object value) throws Exception {
         Integer integer = (Integer) BeanUtilsBean.getInstance()
               .getConvertUtils().lookup(Integer.TYPE).convert(Integer.TYPE,
                     value);

         return integer.intValue();
      }

      public void setValue(Object value) throws Exception {
         widget.setSelection(toInt(value));
      }

      public void unbind() {
         if (listener != null) {
            widget.removeSelectionListener(listener);
         }
      }
   }
}
