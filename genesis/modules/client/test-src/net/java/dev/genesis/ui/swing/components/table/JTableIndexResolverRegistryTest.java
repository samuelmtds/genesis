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

import javax.swing.JTable;
import net.java.dev.genesis.GenesisTestCase;

public class JTableIndexResolverRegistryTest extends GenesisTestCase {
   private class SubJTable extends JTable {
   }
   private JTableIndexResolverRegistry indexResolverRegistry;

   public JTableIndexResolverRegistryTest() {
      super("JTableIndexResolverRegistry Unit Test");
   }

   protected void setUp() throws Exception {
      indexResolverRegistry = JTableIndexResolverRegistry.getInstance();
   }

   public void testIndexResolverRegistry() {
      assertNull(indexResolverRegistry.get(MockSortableTable.class));

      JTableIndexResolver indexResolver;
      indexResolverRegistry.register(MockSortableTable.class,
            indexResolver = new DefaultJTableIndexResolver());

      assertNotNull(indexResolverRegistry.get(MockSortableTable.class));
      assertNotNull(indexResolverRegistry.get(JTable.class));
      assertNull(indexResolverRegistry.get(SubJTable.class));

      assertNotNull(indexResolverRegistry.get(new MockSortableTable()));
      assertNotNull(indexResolverRegistry.get(new JTable()));
      assertNotNull(indexResolverRegistry.get(new SubJTable()));

      assertNotNull(indexResolverRegistry.get(MockSortableTable.class, false));
      assertNotNull(indexResolverRegistry.get(JTable.class, false));
      assertNull(indexResolverRegistry.get(SubJTable.class, false));
      assertNotNull(indexResolverRegistry.get(MockSortableTable.class, true));
      assertNotNull(indexResolverRegistry.get(JTable.class, true));
      assertNotNull(indexResolverRegistry.get(SubJTable.class, true));

      assertSame(indexResolver, indexResolverRegistry.get(
            MockSortableTable.class));
      assertSame(indexResolver, indexResolverRegistry.get(
            new MockSortableTable()));
      assertSame(indexResolver, indexResolverRegistry.get(
            MockSortableTable.class, false));
      assertSame(indexResolver, indexResolverRegistry.get(
            MockSortableTable.class, true));

      final JTableIndexResolver tableIndexResolver = indexResolverRegistry.get(
            JTable.class);
      assertTrue(tableIndexResolver instanceof DefaultJTableIndexResolver);
      assertSame(tableIndexResolver, indexResolverRegistry.get(new JTable()));
      assertSame(tableIndexResolver, indexResolverRegistry.get(JTable.class,
            false));
      assertSame(tableIndexResolver, indexResolverRegistry.get(JTable.class,
            true));

      indexResolverRegistry.deregister(MockSortableTable.class);
      assertNull(indexResolverRegistry.get(MockSortableTable.class));

      indexResolverRegistry.register(MockSortableTable.class,
            new DefaultJTableIndexResolver());
      indexResolverRegistry.deregister();
      assertNull(indexResolverRegistry.get(MockSortableTable.class));
      assertNull(indexResolverRegistry.get(JTable.class));
   }
}
