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

import javax.swing.JPanel;

import net.java.dev.genesis.GenesisTestCase;

public class RecursiveComponentLookupStrategyTest extends GenesisTestCase {
   private MockRecursiveComponentLookupStrategy strategy;
   private JPanel root;
   private JPanel panel;
   private JPanel anotherPanel;

   public RecursiveComponentLookupStrategyTest() {
      super("Recursive Component LookupStrategy Unit Test");
   }

   protected void setUp() {
      strategy = new MockRecursiveComponentLookupStrategy();

      root = new JPanel();
      panel = new JPanel();
      anotherPanel = new JPanel();
   }

   public void testWithRegister() {
      strategy.register("someName", panel);

      assertSame(panel, strategy.lookup(root, "someName"));
      assertEquals("someName", strategy.getName(panel));

      assertSame(root, strategy.lookup(root, "none"));
      assertEquals("none", strategy.get("lookupImpl(Object,String)"));
      assertNull(strategy.getName(anotherPanel));
      
      Exception ex = null;
      try {
         strategy.register("someOtherName", panel);
      } catch(Exception e) {
         ex = e;
      }

      assertTrue(ex instanceof IllegalArgumentException);

      assertSame(panel, strategy.lookup(root, "someName"));
      assertEquals("someName", strategy.getName(panel));
      assertSame(root, strategy.lookup(root, "someOtherName"));
      assertEquals("someOtherName", strategy.get("lookupImpl(Object,String)"));

      strategy.register("someName", anotherPanel);
      assertSame(anotherPanel, strategy.lookup(root, "someName"));
      assertEquals("someName", strategy.getName(anotherPanel));
      assertNull(strategy.getName(panel));
   }
   
   public void testWithComponentName() {
      panel.setName("someName");

      // Mock object will return root. So we ensure that lookupImpl was called
      assertSame(root, strategy.lookup(root, "someName"));
      assertEquals("someName", strategy.getName(panel));
      assertEquals("someName", strategy.get("lookupImpl(Object,String)"));

      assertEquals(root, strategy.lookup(root, "none"));
      assertEquals("none", strategy.get("lookupImpl(Object,String)"));
      assertNull(strategy.getName(anotherPanel));
      
      strategy.register("someName", panel);

      assertSame(panel, strategy.lookup(root, "someName"));
      assertEquals("someName", strategy.getName(panel));

      Exception ex = null;
      try {
         strategy.register("someOtherName", panel);
      } catch(Exception e) {
         ex = e;
      }

      assertTrue(ex instanceof IllegalArgumentException);

      assertSame(panel, strategy.lookup(root, "someName"));
      assertEquals("someName", strategy.getName(panel));
      assertSame(root, strategy.lookup(root, "someOtherName"));
      assertEquals("someOtherName", strategy.get("lookupImpl(Object,String)"));

      anotherPanel.setName("someName");
      assertSame(panel, strategy.lookup(root, "someName"));
      assertEquals("someName", strategy.getName(anotherPanel));
      assertEquals("someName", strategy.getName(panel));
   }
}
