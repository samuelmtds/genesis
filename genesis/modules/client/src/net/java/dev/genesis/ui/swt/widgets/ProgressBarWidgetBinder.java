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

import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swt.SWTBinder;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Widget;

public class ProgressBarWidgetBinder extends AbstractWidgetBinder {
   public BoundField bind(SWTBinder binder, Widget widget,
      FieldMetadata fieldMetadata) {
      return new ProgressBarBoundField(binder, (ProgressBar) widget,
         fieldMetadata);
   }

   public class ProgressBarBoundField extends AbstractBoundMember
         implements BoundField {
      private final ProgressBar widget;
      private final FieldMetadata fieldMetadata;

      public ProgressBarBoundField(SWTBinder binder, ProgressBar widget,
         FieldMetadata fieldMetadata) {
         super(binder, widget);
         this.widget = widget;
         this.fieldMetadata = fieldMetadata;
      }

      protected ProgressBar getWidget() {
         return widget;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
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

      public String getValue() {
         return format(fieldMetadata, new Integer(widget.getSelection()));
      }
   }
}
