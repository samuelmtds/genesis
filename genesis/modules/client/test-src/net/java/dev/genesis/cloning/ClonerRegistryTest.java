/*
 * The Genesis Project
 * Copyright (C) 2005 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.cloning;

import java.util.Collections;

import net.java.dev.genesis.GenesisTestCase;

public class ClonerRegistryTest extends GenesisTestCase {
   public static final class SomeCloner implements Cloner {
      private boolean acceptNull;

      public Object clone(Object o) {
         if (o == null && !acceptNull) {
            throw new IllegalArgumentException();
         }

         return o;
      }

      public boolean isAcceptNull() {
         return acceptNull;
      }

      public void setAcceptNull(boolean acceptNull) {
         this.acceptNull = acceptNull;
      }

   }

   public ClonerRegistryTest() {
      super("Cloner Registry Unit Test");
   }

   public void testGetDefaultEmptyResolverForClass() {
      assertEquals(DefaultCloner.class, ClonerRegistry.getInstance()
            .getDefaultClonerFor(Object.class).getClass());
      assertEquals(DefaultCloner.class, ClonerRegistry.getInstance()
            .getDefaultClonerFor(String.class).getClass());
   }

   public void testGetCloner() {
      assertTrue(((SomeCloner)ClonerRegistry.getInstance().getCloner(
            SomeCloner.class.getName(),
            Collections.singletonMap("acceptNull", "true"))).isAcceptNull());
   }

   public void testGetDefaultClonerForClassMap() {
      assertEquals(DefaultCloner.class, ClonerRegistry.getInstance()
            .getDefaultClonerFor(String.class, null).getClass());

      ClonerRegistry.getInstance().register(String.class, new SomeCloner());

      assertFalse(((SomeCloner)ClonerRegistry.getInstance()
            .getDefaultClonerFor(String.class)).isAcceptNull());

      assertTrue(((SomeCloner)ClonerRegistry.getInstance().getDefaultClonerFor(
            String.class, Collections.singletonMap("acceptNull", "true")))
            .isAcceptNull());
   }

}
