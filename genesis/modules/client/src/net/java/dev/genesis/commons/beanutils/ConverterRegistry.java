/*
 * The Genesis Project
 * Copyright (C) 2004  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.commons.beanutils;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import net.java.dev.genesis.commons.beanutils.converters.BigDecimalConverter;
import net.java.dev.reusablecomponents.converters.EnumConverter;
import net.java.dev.reusablecomponents.lang.Enum;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import org.apache.commons.beanutils.converters.BooleanArrayConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.ByteArrayConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.CharacterArrayConverter;
import org.apache.commons.beanutils.converters.CharacterConverter;
import org.apache.commons.beanutils.converters.ClassConverter;
import org.apache.commons.beanutils.converters.DoubleArrayConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FileConverter;
import org.apache.commons.beanutils.converters.FloatArrayConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerArrayConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongArrayConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortArrayConverter;
import org.apache.commons.beanutils.converters.ShortConverter;
import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.beanutils.converters.SqlTimeConverter;
import org.apache.commons.beanutils.converters.SqlTimestampConverter;
import org.apache.commons.beanutils.converters.StringArrayConverter;
import org.apache.commons.beanutils.converters.StringConverter;
import org.apache.commons.beanutils.converters.URLConverter;

public final class ConverterRegistry extends ConvertUtilsBean {

   public ConverterRegistry() {
      register(new BigIntegerConverter(null), BigInteger.class);
      register(new BooleanConverter(null), Boolean.TYPE);
      register(new BooleanConverter(null), Boolean.class);
      register(new BooleanArrayConverter(null), boolean[].class);
      register(new ByteConverter(null), Byte.TYPE);
      register(new ByteConverter(null), Byte.class);
      register(new ByteArrayConverter(null), byte[].class);
      register(new CharacterConverter(null), Character.TYPE);
      register(new CharacterConverter(null), Character.class);
      register(new CharacterArrayConverter(null), char[].class);
      register(new ClassConverter(), Class.class);
      register(new DoubleConverter(null), Double.TYPE);
      register(new DoubleConverter(null), Double.class);
      register(new DoubleArrayConverter(null), double[].class);
      register(new FloatConverter(null), Float.TYPE);
      register(new FloatConverter(null), Float.class);
      register(new FloatArrayConverter(null), float[].class);
      register(new IntegerConverter(null), Integer.TYPE);
      register(new IntegerConverter(null), Integer.class);
      register(new IntegerArrayConverter(null), int[].class);
      register(new LongConverter(null), Long.TYPE);
      register(new LongConverter(null), Long.class);
      register(new LongArrayConverter(null), long[].class);
      register(new ShortConverter(null), Short.TYPE);
      register(new ShortConverter(null), Short.class);
      register(new ShortArrayConverter(null), short[].class);
      register(new StringConverter(), String.class);
      register(new StringArrayConverter(null), String[].class);
      register(new SqlDateConverter(), Date.class);
      register(new SqlTimeConverter(), Time.class);
      register(new SqlTimestampConverter(), Timestamp.class);
      register(new FileConverter(), File.class);
      register(new URLConverter(), URL.class);

      register(new BigDecimalConverter(), BigDecimal.class);
      register(new EnumConverter(), Enum.class);
      register(new StringConverter(), Object.class);
   }

   public Converter lookup(Class clazz) {
      Converter c = null;
      if (clazz.isInterface()) {
         return super.lookup(Object.class);
      }
      while (clazz != null && (c = super.lookup(clazz)) == null
            && !clazz.equals(Object.class)) {
         clazz = clazz.getSuperclass();
      }
      return c;
   }
}