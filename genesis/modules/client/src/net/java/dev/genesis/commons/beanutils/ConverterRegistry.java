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
import net.java.dev.genesis.commons.beanutils.converters.DefaultConverter;
import net.java.dev.genesis.registry.Registry;
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
   private Registry registry;

   public ConverterRegistry() {
      getRegistry().register(BigInteger.class, new BigIntegerConverter());
      registry.register(Boolean.TYPE, new BooleanConverter(Boolean.FALSE));
      registry.register(Boolean.class, new BooleanConverter(null));
      registry.register(boolean[].class, new BooleanArrayConverter(
            new boolean[0]));
      registry.register(Byte.TYPE, new ByteConverter(new Byte((byte)0)));
      registry.register(Byte.class, new ByteConverter(null));
      registry.register(byte[].class, new ByteArrayConverter(new byte[0]));
      registry.register(Character.TYPE, new CharacterConverter(new Character(
            ' ')));
      registry.register(Character.class, new CharacterConverter(null));
      registry.register(char[].class, new CharacterArrayConverter(new char[0]));
      registry.register(Class.class, new ClassConverter());
      registry.register(Double.TYPE, new DoubleConverter(new Double(0.0)));
      registry.register(Double.class, new DoubleConverter(null));
      registry
            .register(double[].class, new DoubleArrayConverter(new double[0]));
      registry.register(Float.TYPE, new FloatConverter(new Float(0.0f)));
      registry.register(Float.class, new FloatConverter(null));
      registry.register(float[].class, new FloatArrayConverter(new float[0]));
      registry.register(Integer.TYPE, new IntegerConverter(new Integer(0)));
      registry.register(Integer.class, new IntegerConverter(null));
      registry.register(int[].class, new IntegerArrayConverter(new int[0]));
      registry.register(Long.TYPE, new LongConverter(new Long(0L)));
      registry.register(Long.class, new LongConverter(null));
      registry.register(long[].class, new LongArrayConverter(new long[0]));
      registry.register(Short.TYPE, new ShortConverter(new Short((short)0)));
      registry.register(Short.class, new ShortConverter(null));
      registry.register(short[].class, new ShortArrayConverter(new short[0]));
      registry.register(String.class, new StringConverter());
      registry
            .register(String[].class, new StringArrayConverter(new String[0]));
      registry.register(Date.class, new SqlDateConverter());
      registry.register(Time.class, new SqlTimeConverter());
      registry.register(Timestamp.class, new SqlTimestampConverter());
      registry.register(File.class, new FileConverter());
      registry.register(URL.class, new URLConverter());

      registry.register(BigDecimal.class, new BigDecimalConverter());
      registry.register(Enum.class, new EnumConverter());
      registry.register(Object.class, new DefaultConverter());
   }

   // To avoid NPE when ConvertUtilsBean constructor calls 
   // register(converter,class). The registry instance would be null at that 
   // time due to the initialization order of member variables
   private Registry getRegistry() {
      if (registry == null) {
         registry = new Registry();
      }
      return registry;
   }

   public void register(Converter converter, Class clazz) {
      getRegistry().register(clazz, converter);
   }

   public void deregister() {
      getRegistry().deregister();
   }

   public void deregister(Class clazz) {
      getRegistry().deregister(clazz);
   }

   public Converter lookup(Class clazz) {
      return (Converter)registry.get(clazz, true);
   }
}