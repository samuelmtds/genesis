/*
 * The Genesis Project
 * Copyright (C) 2006 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.swt.lookup;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.ui.binding.MapLookupStrategy;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class MapWidgetLookupStrategyTest extends GenesisTestCase {
   private MapLookupStrategy strategy;
   private Shell shell;
   private Shell anotherShell;

   public MapWidgetLookupStrategyTest() {
      super("Map Widget LookupStrategy Unit Test");
   }

   protected void setUp() {
      strategy = new MapWidgetLookupStrategy() {
         public String getRealName(Object object) {
            return (String) ((Widget)object).getData();
         }
      };
      shell = new Shell();
      anotherShell = new Shell();
   }

   public void testAll() {
      strategy.register("someName", shell);

      assertSame(shell, strategy.lookup(null, "someName"));
      assertEquals("someName", strategy.getName(shell));

      assertNull(strategy.lookup(null, "none"));
      assertNull(strategy.getName(anotherShell));
      
      Exception ex = null;
      try {
         strategy.register("someOtherName", shell);
      } catch(Exception e) {
         ex = e;
      }

      assertTrue(ex instanceof IllegalArgumentException);

      assertSame(shell, strategy.lookup(null, "someName"));
      assertEquals("someName", strategy.getName(shell));
      assertNull(strategy.lookup(null, "someOtherName"));

      strategy.register("someName", anotherShell);
      assertSame(anotherShell, strategy.lookup(null, "someName"));
      assertEquals("someName", strategy.getName(anotherShell));
      assertNull(strategy.getName(shell));
   }
}
