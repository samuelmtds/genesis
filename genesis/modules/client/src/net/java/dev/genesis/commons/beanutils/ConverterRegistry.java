/*
 * The Genesis Project
 * Copyright (C) 2004-2005  Summa Technologies do Brasil Ltda.
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
      register(new BigIntegerConverter(), BigInteger.class);
      register(new BooleanConverter(Boolean.FALSE), Boolean.TYPE);
      register(new BooleanConverter(null), Boolean.class);
      register(new BooleanArrayConverter(new boolean[0]), boolean[].class);
      register(new ByteConverter(new Byte((byte)0)), Byte.TYPE);
      register(new ByteConverter(null), Byte.class);
      register(new ByteArrayConverter(new byte[0]), byte[].class);
      register(new CharacterConverter(new Character(' ')), Character.TYPE);
      register(new CharacterConverter(null), Character.class);
      register(new CharacterArrayConverter(new char[0]), char[].class);
      register(new ClassConverter(), Class.class);
      register(new DoubleConverter(new Double(0.0)), Double.TYPE);
      register(new DoubleConverter(null), Double.class);
      register(new DoubleArrayConverter(new double[0]), double[].class);
      register(new FloatConverter(new Float(0.0f)), Float.TYPE);
      register(new FloatConverter(null), Float.class);
      register(new FloatArrayConverter(new float[0]), float[].class);
      register(new IntegerConverter(new Integer(0)), Integer.TYPE);
      register(new IntegerConverter(null), Integer.class);
      register(new IntegerArrayConverter(new int[0]), int[].class);
      register(new LongConverter(new Long(0L)), Long.TYPE);
      register(new LongConverter(null), Long.class);
      register(new LongArrayConverter(new long[0]), long[].class);
      register(new ShortConverter(new Short((short)0)), Short.TYPE);
      register(new ShortConverter(null), Short.class);
      register(new ShortArrayConverter(new short[0]), short[].class);
      register(new StringConverter(), String.class);
      register(new StringArrayConverter(new String[0]), String[].class);
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

      //TODO: support full hierarchy support
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