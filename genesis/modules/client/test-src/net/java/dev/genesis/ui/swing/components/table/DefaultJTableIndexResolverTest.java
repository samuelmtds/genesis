/*
 * The Genesis Project
 * Copyright (C) 2008 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.swing.components.table;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import net.java.dev.genesis.GenesisTestCase;

public class DefaultJTableIndexResolverTest extends GenesisTestCase {
   private JComponent unsortableTable;
   private MockSortableTable sortableTable;
   private JTableIndexResolver defaultIndexResolver;

   public DefaultJTableIndexResolverTest() {
      super("DefaultJTableIndexResolver Unit Test");
   }

   protected void setUp() throws Exception {
      unsortableTable = new JComponent() {
      };
      sortableTable = new MockSortableTable();

      defaultIndexResolver = new DefaultJTableIndexResolver();
   }

   public void testTableIndexResolver() throws Exception {
      assertFalse(defaultIndexResolver.needsConversion(unsortableTable));
      assertTrue(defaultIndexResolver.needsConversion(sortableTable));

      try {
         assertEquals(0, defaultIndexResolver.convertRowIndexToView(
               unsortableTable, 0));
         fail("Must be not possible convert a index when needsConversion is false");
      } catch (NullPointerException npe) {
      }

      try {
         assertEquals(0, defaultIndexResolver.convertRowIndexToModel(
               unsortableTable, 0));
         fail("Must be not possible convert a index when needsConversion is false");
      } catch (NullPointerException npe) {
      }

      Map map = new HashMap();
      map.put(new Integer(1), new Integer(3));
      map.put(new Integer(2), new Integer(2));
      map.put(new Integer(3), new Integer(4));
      map.put(new Integer(4), new Integer(0));
      map.put(new Integer(5), new Integer(1));

      for (int i = 1; i <= 5; i++) {
         int modelIndex = ((Integer)map.get(new Integer(i))).intValue();
         int viewIndex = modelIndex;

         validateIndexes(modelIndex, viewIndex);

         assertEquals(defaultIndexResolver.convertRowIndexToModel(sortableTable,
               viewIndex), defaultIndexResolver.convertRowIndexToView(
               sortableTable, modelIndex));
      }

      sortableTable.sort();

      for (int i = 1; i <= 5; i++) {
         int modelIndex = ((Integer)map.get(new Integer(i))).intValue();
         int viewIndex = i - 1;

         validateIndexes(modelIndex, viewIndex);
      }
   }

   private void validateIndexes(int modelIndex, int viewIndex) throws Exception {
      assertEquals(modelIndex, defaultIndexResolver.convertRowIndexToModel(
            sortableTable, viewIndex));
      assertEquals(viewIndex, defaultIndexResolver.convertRowIndexToView(
            sortableTable, modelIndex));
   }
}
