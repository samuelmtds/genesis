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

public class DefaultConverterTest extends GenesisTestCase {
   private void testConverter(DefaultConverter converter,
         boolean returnDefaultValue, Object defaultValue) {
      assertNull(converter.convert(Object.class, null));

      Object value = new Object();
      assertEquals(converter.convert(Object.class, value), value);

      try {
         assertEquals(converter.convert(String.class, value), defaultValue);

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
      testConverter(new DefaultConverter(), true, null);
   }

   public void testObjectConstructor() {
      Object value = new Object();
      testConverter(new DefaultConverter(value), true, value);
   }

   public void testBooleanConstructor() {
      testConverter(new DefaultConverter(true), true, null);
      testConverter(new DefaultConverter(false), false, null);
   }

   public void testBooleanObjectConstructor() {
      Object value = new Object();
      testConverter(new DefaultConverter(true, value), true, value);
      testConverter(new DefaultConverter(false, value), false, value);
   }
}