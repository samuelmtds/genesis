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
package net.java.dev.genesis.text;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.reusablecomponents.lang.Enum;

public class FormatterRegistryTest extends GenesisTestCase {
   private static final class EnumClass extends Enum {
      public static final EnumClass INSTANCE = new EnumClass("A");

      private EnumClass(String value) {
         super(value);
      }
   }

   private static final String TEXT = "text";

   private final Formatter formatter = new Formatter() {
      public String format(Object o) {
         return TEXT;
      }
   };

   public void testGetClass() {
      assertNull(FormatterRegistry.getInstance().get(EnumClass.class));
      assertEquals(EnumFormatter.class.getName(), FormatterRegistry
            .getInstance().get(Enum.class).getClass().getName());

      assertNull(FormatterRegistry.getInstance().get(String.class));
      assertNull(FormatterRegistry.getInstance().get(Comparable.class));
      assertEquals(DefaultFormatter.class.getName(), FormatterRegistry
            .getInstance().get(Object.class).getClass().getName());
   }

   public void testGetClassBoolean() {
      testGetClassBoolean(EnumFormatter.class, EnumClass.class, true);
      testGetClassBoolean(null, EnumClass.class, false);

      testGetClassBoolean(EnumFormatter.class, Enum.class, true);
      testGetClassBoolean(EnumFormatter.class, Enum.class, false);

      testGetClassBoolean(DefaultFormatter.class, String.class, true);
      testGetClassBoolean(null, String.class, false);

      testGetClassBoolean(DefaultFormatter.class, Comparable.class, true);
      testGetClassBoolean(null, Comparable.class, false);

      testGetClassBoolean(DefaultFormatter.class, Object.class, true);
      testGetClassBoolean(DefaultFormatter.class, Object.class, false);
   }

   private void testGetClassBoolean(Class formatterClass, Class lookupClass,
         boolean superClass) {
      final Object ret = FormatterRegistry.getInstance().get(lookupClass,
            superClass);
      if (formatterClass == null) {
         assertNull(ret);
      } else {
         assertEquals(formatterClass.getName(), ret.getClass().getName());
      }
   }

   public void testGetObject() {
      assertEquals(EnumFormatter.class.getName(), FormatterRegistry
            .getInstance().get(EnumClass.INSTANCE).getClass().getName());

      assertEquals(DefaultFormatter.class.getName(), FormatterRegistry
            .getInstance().get("").getClass().getName());

      assertEquals(DefaultFormatter.class.getName(), FormatterRegistry
            .getInstance().get(new Object()).getClass().getName());

      assertNull(FormatterRegistry.getInstance().get(null));
   }

   public void testRegister() {
      FormatterRegistry.getInstance().register(String.class, formatter);

      assertSame(formatter, FormatterRegistry.getInstance().get(String.class));
      assertSame(formatter, FormatterRegistry.getInstance().get(String.class,
            true));
      assertSame(formatter, FormatterRegistry.getInstance().get(String.class,
            false));
      assertSame(formatter, FormatterRegistry.getInstance().get(""));

      assertEquals(TEXT, FormatterRegistry.getInstance().format(""));

      FormatterRegistry.getInstance().register(String.class, null);
   }

   public void testFormat() {
      assertEquals("", FormatterRegistry.getInstance().format(null));

      Object value = new Object();
      assertEquals(value.toString(), FormatterRegistry.getInstance().
            format(value));

      assertEquals(new EnumFormatter().format(EnumClass.INSTANCE),
            FormatterRegistry.getInstance().format(EnumClass.INSTANCE));
   }
}