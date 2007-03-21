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

import net.java.dev.genesis.ui.binding.AbstractBinder;
import net.java.dev.genesis.ui.binding.BindingStrategy;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swt.SWTBinder;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class TextWidgetBinder extends AbstractWidgetBinder {
   private final boolean trim;

   public TextWidgetBinder() {
      this(true);
   }
   
   public TextWidgetBinder(boolean trim) {
      this.trim = trim;
   }
   
   protected boolean isTrim() {
      return trim;
   }

   public BoundField bind(SWTBinder binder, Widget widget,
         FieldMetadata fieldMetadata) {
      return new TextWidgetBoundField(binder, (Text) widget,
         fieldMetadata);
   }

   public static abstract class TextBindingStrategy 
         implements BindingStrategy {
      public void addListener(Object widget) {
         addListener((Text)widget);
      }

      protected abstract void addListener(Text widget);

      public void removeListener(Object widget) {
         removeListener((Text)widget);
      }

      protected abstract void removeListener(Text widget);
   }

   public class TextWidgetBoundField extends AbstractBoundMember
         implements BoundField {
      public class FocusBindingStrategy extends TextBindingStrategy {
         private FocusListener listener;

         protected void addListener(Text widget) {
            widget.addFocusListener(listener = createFocusListener());
         }

         protected FocusListener createFocusListener() {
            return new FocusAdapter() {
               public void focusLost(FocusEvent event) {
                  getBinder().populateForm(getFieldMetadata(), getValue());
               }
            };
         }

         public void setValue(Object value) {
            updateComponentValue(value);
         }

         protected void removeListener(Text component) {
            if (listener != null) {
               widget.removeFocusListener(listener);
            }
         }
      }

      public class ModifyBindingStrategy extends TextBindingStrategy {
         private ModifyListener listener;

         protected void addListener(Text widget) {
            widget.addModifyListener(listener = createModifyListener());
         }

         protected ModifyListener createModifyListener() {
            return new ModifyListener() {
               public void modifyText(ModifyEvent modifyEvent) {
                  getBinder().populateForm(getFieldMetadata(), getValue());
               }
            };
         }

         public void setValue(Object value) {
            final String valueAsString = formatValue(value);

            if (!fieldMetadata.getEqualityComparator().equals(
                  valueAsString, getValue())) {
               updateComponentValue(value);
            }
         }

         protected void removeListener(Text component) {
            if (listener != null) {
               widget.removeModifyListener(listener);
            }
         }
      }

      private final Text widget;
      private final FieldMetadata fieldMetadata;
      private final BindingStrategy strategy;

      public TextWidgetBoundField(SWTBinder binder,
            Text widget, FieldMetadata fieldMetadata) {
         super(binder, widget);
         this.widget = widget;
         this.fieldMetadata = fieldMetadata;
         this.strategy = createBindingStrategy();

         strategy.addListener(widget);
      }

      protected Text getWidget() {
         return widget;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      protected BindingStrategy createBindingStrategy() {
         return AbstractBinder.BINDING_STRATEGY_AS_YOU_TYPE.equals(
               getBinder().getBindingStrategy((String)widget.getData(
               AbstractBinder.BINDING_STRATEGY_PROPERTY))) ? 
               (BindingStrategy)new ModifyBindingStrategy() : 
               new FocusBindingStrategy();
      }

      public String getValue() {
         return isTrim() ? widget.getText().trim() : widget.getText();
      }

      public String formatValue(Object value) {
         return getBinder().getFormatter(fieldMetadata).format(value);
      }

      public void updateComponentValue(Object value) {
         widget.setText(formatValue(value));
      }

      public void setValue(Object value) throws Exception {
         strategy.setValue(value);
      }

      public void unbind() {
         strategy.removeListener(widget);
      }
   }
}
