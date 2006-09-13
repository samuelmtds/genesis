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
package net.java.dev.genesis.ui.swing.lookup;

import java.awt.Component;

import javax.swing.JPanel;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.ui.binding.MapLookupStrategy;

public class MapComponentLookupStrategyTest extends GenesisTestCase {
   private MapLookupStrategy strategy;
   private JPanel panel;
   private JPanel anotherPanel;

   public MapComponentLookupStrategyTest() {
      super("Map Component LookupStrategy Unit Test");
   }

   protected void setUp() {
      strategy = new MapComponentLookupStrategy() {
         protected String getRealName(Object object) {
            return ((Component)object).getName();
         }
      };
      panel = new JPanel();
      anotherPanel = new JPanel();
   }

   public void testAll() {
      strategy.register("someName", panel);

      assertSame(panel, strategy.lookup(null, "someName"));
      assertEquals("someName", strategy.getName(panel));

      assertNull(strategy.lookup(null, "none"));
      assertNull(strategy.getName(anotherPanel));
      
      Exception ex = null;
      try {
         strategy.register("someOtherName", panel);
      } catch(Exception e) {
         ex = e;
      }

      assertTrue(ex instanceof IllegalArgumentException);

      assertSame(panel, strategy.lookup(null, "someName"));
      assertEquals("someName", strategy.getName(panel));
      assertNull(strategy.lookup(null, "someOtherName"));

      strategy.register("someName", anotherPanel);
      assertSame(anotherPanel, strategy.lookup(null, "someName"));
      assertEquals("someName", strategy.getName(anotherPanel));
      assertNull(strategy.getName(panel));
   }
}
