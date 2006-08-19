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
package net.java.dev.genesis.commons.beanutils.converters;

import net.java.dev.genesis.GenesisTestCase;
import org.apache.commons.beanutils.ConversionException;

public class BooleanConverterTest extends GenesisTestCase {
   private void testConverter(BooleanConverter converter,
         Object trueValue, Object falseValue, boolean returnDefaultValue, 
         boolean nullAsDefaultValue, Boolean defaultValue) {
      // First true and false
      assertEquals(Boolean.TRUE, converter.convert(Boolean.class, Boolean.TRUE));
      assertEquals(Boolean.FALSE, converter.convert(Boolean.class, Boolean.FALSE));

      // Then true and false values
      assertEquals(Boolean.TRUE, converter.convert(Boolean.class, trueValue));
      assertEquals(Boolean.FALSE, converter.convert(Boolean.class, falseValue));

      // Then null
      Object ret = converter.convert(Boolean.class, null);

      if (nullAsDefaultValue) {
         assertSame(defaultValue, ret);
      } else {
         assertNull(ret);
      }

      // Then an arbitrary value
      try {
         assertSame(defaultValue, converter.convert(Object.class, new Object()));

         if (!returnDefaultValue) {
            fail("Should have thrown a ConversionException instead");
         }
      } catch (ConversionException ce) {
         if (returnDefaultValue) {
            fail("Should have returned the default value instead");
         }
      }
   }

   public void testDefaultConstructor() {
      testConverter(new BooleanConverter(), BooleanConverter.TRUE, 
            BooleanConverter.FALSE, true, false,  Boolean.FALSE);
   }

   public void testTwoObjectsConstructor() {
      Object trueValue = new Object();
      Object falseValue = new Object();

      testConverter(new BooleanConverter(trueValue, falseValue), trueValue, 
            falseValue, true, false, Boolean.FALSE);
   }

   public void testTwoObjectsDefaultConstructor() {
      Object trueValue = new Object();
      Object falseValue = new Object();
      Boolean defaultValue = Boolean.TRUE;

      testConverter(new BooleanConverter(trueValue, falseValue, defaultValue), 
            trueValue, falseValue, true, false, defaultValue);
   }

   public void testTwoObjectsFlagConstructor() {
      Object trueValue = new Object();
      Object falseValue = new Object();

      testConverter(new BooleanConverter(trueValue, falseValue, true), 
            trueValue, falseValue, true, false, Boolean.FALSE);
      testConverter(new BooleanConverter(trueValue, falseValue, false), 
            trueValue, falseValue, false, false, null);
   }

   public void testFullConstructor() {
      Object trueValue = new Object();
      Object falseValue = new Object();
      Boolean defaultValue = Boolean.TRUE;

      testConverter(new BooleanConverter(trueValue, falseValue, false, false, 
            defaultValue), trueValue, falseValue, false, false, defaultValue);
      testConverter(new BooleanConverter(trueValue, falseValue, false, true, 
            defaultValue), trueValue, falseValue, false, true, defaultValue);
      testConverter(new BooleanConverter(trueValue, falseValue, true, false, 
            defaultValue), trueValue, falseValue, true, false, defaultValue);
      testConverter(new BooleanConverter(trueValue, falseValue, true, true, 
            defaultValue), trueValue, falseValue, true, true, defaultValue);
   }
}