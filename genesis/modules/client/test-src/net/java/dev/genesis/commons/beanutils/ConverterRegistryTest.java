/*
 * The Genesis Project
 * Copyright (C) 2005  Summa Technologies do Brasil Ltda.
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

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.commons.beanutils.converters.BigDecimalConverter;
import net.java.dev.genesis.commons.beanutils.converters.DefaultConverter;
import net.java.dev.reusablecomponents.converters.EnumConverter;
import net.java.dev.reusablecomponents.lang.Enum;

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

public class ConverterRegistryTest extends GenesisTestCase {
   private ConverterRegistry converter;

   public void setUp() {
      converter = new ConverterRegistry();
   }
   
   public void testLookup() {
      assertConverter(BigInteger.class, BigIntegerConverter.class);
      assertConverter(Boolean.TYPE, BooleanConverter.class);
      assertConverter(Boolean.class, BooleanConverter.class);
      assertConverter(boolean[].class, BooleanArrayConverter.class);
      assertConverter(Byte.TYPE, ByteConverter.class);
      assertConverter(Byte.class, ByteConverter.class);
      assertConverter(byte[].class, ByteArrayConverter.class);
      assertConverter(Character.TYPE, CharacterConverter.class);
      assertConverter(Character.class, CharacterConverter.class);
      assertConverter(char[].class, CharacterArrayConverter.class);
      assertConverter(Class.class, ClassConverter.class);
      assertConverter(Double.TYPE, DoubleConverter.class);
      assertConverter(Double.class, DoubleConverter.class);
      assertConverter(double[].class, DoubleArrayConverter.class);
      assertConverter(Float.TYPE, FloatConverter.class);
      assertConverter(Float.class, FloatConverter.class);
      assertConverter(float[].class, FloatArrayConverter.class);
      assertConverter(Integer.TYPE, IntegerConverter.class);
      assertConverter(Integer.class, IntegerConverter.class);
      assertConverter(int[].class, IntegerArrayConverter.class);
      assertConverter(Long.TYPE, LongConverter.class);
      assertConverter(Long.class, LongConverter.class);
      assertConverter(long[].class, LongArrayConverter.class);
      assertConverter(Short.TYPE, ShortConverter.class);
      assertConverter(Short.class, ShortConverter.class);
      assertConverter(short[].class, ShortArrayConverter.class);
      assertConverter(String.class, StringConverter.class);
      assertConverter(String[].class, StringArrayConverter.class);
      assertConverter(Date.class, SqlDateConverter.class);
      assertConverter(Time.class, SqlTimeConverter.class);
      assertConverter(Timestamp.class, SqlTimestampConverter.class);
      assertConverter(File.class, FileConverter.class);
      assertConverter(URL.class, URLConverter.class);
      assertConverter(BigDecimal.class, BigDecimalConverter.class);
      assertConverter(Enum.class, EnumConverter.class);
      assertConverter(Object.class, DefaultConverter.class);
   }

   private void assertConverter(Class lookupClass, Class converterClass) {
      Object converterInstance = converter.lookup(lookupClass);
      assertNotNull(converterInstance);
      assertEquals(converterClass, converterInstance.getClass());
   }
   
   public void testDeregisterWithArg() {
      testDeregisterWithArg(Object.class);
      testDeregisterWithArg(BigInteger.class);
      testDeregisterWithArg(Boolean.TYPE);
      testDeregisterWithArg(Boolean.class);
   }

   private void testDeregisterWithArg(Class lookupClass) {
      assertNotNull(converter.lookup(lookupClass));
      converter.deregister(lookupClass);
      assertNull(converter.lookup(lookupClass));
   }
   
   public void testDeRegisterNoArg() {
      assertNotNull(converter.lookup(BigInteger.class));
      assertNotNull(converter.lookup(Boolean.TYPE));
      assertNotNull(converter.lookup(Boolean.class));

      converter.deregister();

      assertNull(converter.lookup(BigInteger.class));
      assertNull(converter.lookup(Boolean.TYPE));
      assertNull(converter.lookup(Boolean.class));
   }

   public void testRegister() {
      final Converter converterInstance = new DefaultConverter();

      //Test for non-registered type
      converter.register(converterInstance, Comparable.class);
      assertSame(converterInstance, converter.lookup(Comparable.class));

      //Test for already registered type
      converter.register(converterInstance, Integer.class);
      assertSame(converterInstance, converter.lookup(Integer.class));
   }
}