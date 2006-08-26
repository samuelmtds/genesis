/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.binding;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.ui.binding.MapLookupStrategy;

public class MapLookupStrategyTest extends GenesisTestCase {
   private MapLookupStrategy strategy;
   private Object object1;
   private Object object2;

   public MapLookupStrategyTest() {
      super("Map LookupStrategy Unit Test");
   }

   protected void setUp() {
      strategy = new MapLookupStrategy() {};
      object1 = new Object();
      object2 = new Object();
   }

   public void testAll() {
      strategy.register("someName", object1);

      assertSame(object1, strategy.lookup(null, "someName"));
      assertEquals("someName", strategy.getName(object1));

      assertNull(strategy.lookup(null, "none"));
      assertNull(strategy.getName(object2));
      
      Exception ex = null;
      try {
         strategy.register("someOtherName", object1);
      } catch(Exception e) {
         ex = e;
      }

      assertTrue(ex instanceof IllegalArgumentException);

      assertSame(object1, strategy.lookup(null, "someName"));
      assertEquals("someName", strategy.getName(object1));
      assertNull(strategy.lookup(null, "someOtherName"));

      strategy.register("someName", object2);
      assertSame(object2, strategy.lookup(null, "someName"));
      assertEquals("someName", strategy.getName(object2));
      assertNull(strategy.getName(object1));
   }
}
