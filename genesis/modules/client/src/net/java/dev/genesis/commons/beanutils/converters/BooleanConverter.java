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
package net.java.dev.genesis.commons.beanutils.converters;

import net.java.dev.genesis.util.Bundle;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

public class BooleanConverter implements Converter {
   public static final String TRUE = "true"; // NOI18N
   public static final String FALSE = "false"; // NOI18N

   private final Object trueValue;
   private final Object falseValue;
   private final boolean returnDefaultValue;
   private final boolean nullAsDefaultValue;
   private final Boolean defaultValue;

   public BooleanConverter() {
      this(TRUE, FALSE, true, false, Boolean.FALSE);
   }

   public BooleanConverter(Object trueValue, Object falseValue) {
      this(trueValue, falseValue, true, false, Boolean.FALSE);
   }

   public BooleanConverter(Object trueValue, Object falseValue, 
         Boolean defaultValue) {
      this(trueValue, falseValue, true, false, defaultValue);
   }

   public BooleanConverter(Object trueValue, Object falseValue, 
         boolean returnDefaultValue) {
      this(trueValue, falseValue, returnDefaultValue, false,
            returnDefaultValue ?  Boolean.FALSE : null);
   }

   public BooleanConverter(Object trueValue, Object falseValue, 
         boolean returnDefaultValue, boolean nullAsDefaultValue, 
         Boolean defaultValue) {
      this.trueValue = trueValue;
      this.falseValue = falseValue;
      this.returnDefaultValue = returnDefaultValue;
      this.nullAsDefaultValue = nullAsDefaultValue;
      this.defaultValue = defaultValue;
   }

   public Object convert(Class clazz, Object obj) {
      if (obj == null) {
         return nullAsDefaultValue ? defaultValue : null;
      }

      if (obj.getClass().equals(Boolean.class)) {
         return obj;
      }

      if (trueValue.equals(obj)) {
         return Boolean.TRUE;
      } else if (falseValue.equals(obj)) {
         return Boolean.FALSE;
      } else if (returnDefaultValue) {
         return defaultValue;
      } else {
         throw new ConversionException(Bundle.getMessage(BooleanConverter.class,
               "X_CANNOT_BE_CONVERTED_TO_Y_ITS_TYPE_IS_Z", new Object[]{ // NOI18N
            obj, clazz.getName(), obj.getClass().getName()}));
      }
   }
}