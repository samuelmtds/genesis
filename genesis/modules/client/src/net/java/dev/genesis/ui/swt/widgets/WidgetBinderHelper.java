/*
 * The Genesis Project
 * Copyright (C) 2008-2009  Summa Technologies do Brasil Ltda.
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

import java.lang.reflect.InvocationTargetException;
import net.java.dev.genesis.ui.swt.SWTBinder;
import net.java.dev.genesis.util.Bundle;
import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.swt.widgets.Widget;

public class WidgetBinderHelper {
   private WidgetBinderHelper() {
   }

   public static String format(SWTBinder binder, Widget widget, Object value) {
      String valueProperty = (String)widget.getData(SWTBinder.VALUE_PROPERTY);

      if (value == null) {
         String blankLabel = (String) widget.getData(SWTBinder.BLANK_LABEL_PROPERTY);
         return (blankLabel == null) ? "" : blankLabel; // NOI18N
      } else if (value instanceof String) {
         return (String) value;
      } else if (valueProperty == null) {
         return binder.format(binder.getName(widget), null, value, 
               binder.isVirtual(widget));
      }

      boolean isVirtual = binder.isVirtual(widget, valueProperty);

      return binder.format(binder.getName(widget), valueProperty, 
            isVirtual ? value : getValue(binder, widget, value, 
            valueProperty), isVirtual);
   }

   private static Object getValue(SWTBinder binder, Widget widget, Object bean,
         String propertyName) {
      try {
         return PropertyUtils.getProperty(bean, propertyName);
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
         throw new RuntimeException(e);
      } catch (NoSuchMethodException e) {
         IllegalArgumentException iae = new IllegalArgumentException(
               Bundle.getMessage(WidgetBinderHelper.class,
               "THE_WIDGET_NAMED_X_WAS_EXPECTED_Y_TO_HAVE_A_PROPERTY_NAMED_Z_AT_BEAN_W", // NOI18N
               new Object[] {binder.getName(widget), bean.getClass().getName(),
                  propertyName, bean}));
         iae.initCause(e);
         throw iae;
      }
   }

   public static String getKey(SWTBinder binder, Widget widget, String name,
         Object value) {
      String keyPropertyName = (String)widget.getData(SWTBinder.KEY_PROPERTY);

      if (keyPropertyName != null) {
         Object o = (value == null) ? null : getValue(binder, widget, value,
               keyPropertyName);

         return binder.format(name, keyPropertyName, o);
      }

      return format(binder, widget, value);
   }
}
