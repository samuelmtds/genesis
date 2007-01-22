/*
 * The Genesis Project
 * Copyright (C) 2007  Summa Technologies do Brasil Ltda.
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

import java.util.Collections;
import net.java.dev.genesis.GenesisTestCase;
import org.apache.commons.beanutils.ConversionException;

public class EnumConverterTest extends GenesisTestCase {
   public static class SampleEnum extends net.java.dev.reusablecomponents.lang.Enum {
      public static final String A_NAME = "A_NAME";
      public static final String B_NAME = "B_NAME";

      public static final SampleEnum A = new SampleEnum(A_NAME);
      public static final SampleEnum B = new SampleEnum(B_NAME);

      private SampleEnum(String name) {
         super(name);
      }
   }

   private EnumConverter nonNullConverter;
   private EnumConverter nullConverter;
   
   protected void setUp() throws Exception {
      nonNullConverter = new EnumConverter();
      nullConverter = new EnumConverter(true);
   }
   

   public void testNullReturn() {
      assertFalse(nonNullConverter.isNullReturnAcceptable());
      assertTrue(nullConverter.isNullReturnAcceptable());

      try {
         nonNullConverter.convert(SampleEnum.class, "AnyString");
         fail(nonNullConverter + " should throw a NPE for null");
      } catch (ConversionException ce) {
         // ok, worked
      }

      assertNull(nullConverter.convert(SampleEnum.class, "AnyString"));
   }

   public void testValidValues() {
      assertSame(SampleEnum.A, nonNullConverter.convert(SampleEnum.class, 
            SampleEnum.A_NAME));
      assertSame(SampleEnum.A, nullConverter.convert(SampleEnum.class, 
            SampleEnum.A_NAME));
      assertSame(SampleEnum.B, nonNullConverter.convert(SampleEnum.class, 
            SampleEnum.B_NAME));
      assertSame(SampleEnum.B, nullConverter.convert(SampleEnum.class, 
            SampleEnum.B_NAME));
   }

   public void testNullValue() {
      try {
         nonNullConverter.convert(SampleEnum.class, null);
         fail("nonNullConverter should throw a ConversionException");
      } catch (ConversionException ce) {
         // ok, worked
      }

      assertNull(nullConverter.convert(SampleEnum.class, null));
   }

   public void testInvalidEnumClass() {
      try {
         nonNullConverter.convert(Integer.class, SampleEnum.A_NAME);
         fail("nonNullConverter should throw a ConversionException");
      } catch (ConversionException ce) {
         // ok, worked
      }

      try {
         nullConverter.convert(Integer.class, SampleEnum.A_NAME);
         fail("nullConverter should throw a ConversionException");
      } catch (ConversionException ce) {
         // ok, worked
      }
   }

   public void testInvalidValue() {
      try {
         nonNullConverter.convert(SampleEnum.class, Collections.EMPTY_LIST);
         fail("nonNullConverter should throw a ConversionException");
      } catch (ConversionException ce) {
         // ok, worked
      }

      try {
         nullConverter.convert(SampleEnum.class, Collections.EMPTY_LIST);
         fail("nullConverter should throw a ConversionException");
      } catch (ConversionException ce) {
         // ok, worked
      }
   }

   public void testEnums() {
      assertSame(SampleEnum.A, nonNullConverter.convert(SampleEnum.class, 
            SampleEnum.A));
      assertSame(SampleEnum.A, nullConverter.convert(SampleEnum.class, 
            SampleEnum.A));
      
      Class enumClass = net.java.dev.reusablecomponents.lang.Enum.class;

      assertSame(SampleEnum.A, nonNullConverter.convert(enumClass, 
            SampleEnum.A));
      assertSame(SampleEnum.A, nullConverter.convert(enumClass, SampleEnum.A));
   }
}
