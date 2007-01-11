/*
 * The Genesis Project
 * Copyright (C) 2006-2007 Summa Technologies do Brasil Ltda.
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

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;

public class LabelWidgetBinder extends AbstractWidgetBinder {
   public BoundField bind(SWTBinder binder, Widget widget,
      FieldMetadata fieldMetadata) {
      return new LabelWidgetBoundField(binder, (Label) widget,
         fieldMetadata);
   }

   public class LabelWidgetBoundField extends AbstractBoundMember
         implements BoundField {
      private final Label widget;
      private final FieldMetadata fieldMetadata;

      public LabelWidgetBoundField(SWTBinder binder, Label widget,
         FieldMetadata fieldMetadata) {
         super(binder, widget);
         this.widget = widget;
         this.fieldMetadata = fieldMetadata;
      }

      protected Label getWidget() {
         return widget;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      public void setValue(Object value) {
         widget.setText(getBinder().getFormatter(fieldMetadata).format(value));
      }

      public Object getValue() {
         return widget.getText();
      }
   }
}
