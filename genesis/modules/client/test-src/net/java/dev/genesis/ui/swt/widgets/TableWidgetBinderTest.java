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
package net.java.dev.genesis.ui.swt.widgets;

import java.util.Arrays;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockBean;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.reflection.MethodEntry;
import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swt.MockSWTBinder;
import net.java.dev.genesis.ui.swt.SWTBinder;
import net.java.dev.genesis.ui.swt.widgets.TableWidgetBinder;

import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TableWidgetBinderTest extends GenesisTestCase {
   private Shell root;
   private Table table;
   private MockSWTBinder binder;
   private WidgetBinder widgetBinder;
   private BoundDataProvider boundDataProvider;
   private MockForm form;
   private DataProviderMetadata dataMeta;

   public TableWidgetBinderTest() {
      super("Table Widget Binder Unit Test");
   }

   protected void setUp() throws Exception {
      table = new Table(root = new Shell(), SWT.MULTI | SWT.VIRTUAL) {
         private int[] selected;

         public void select(int index) {
            selected = new int[] {index};
         }

         public void select(int[] indices) {
            this.selected = indices;
         }

         public void deselectAll() {
            selected = new int[0];
         }

         public int getSelectionIndex() {
            return selected == null || selected.length == 0 ? -1 : selected[0];
         }

         public int[] getSelectionIndices() {
            return selected;
         }

         protected void checkSubclass() {
         }
      };
      TableColumn column1 = new TableColumn(table, SWT.NONE);
      column1.setData(SWTBinder.TABLE_COLUMN_IDENTIFIER, "key");
      TableColumn column2 = new TableColumn(table, SWT.NONE);
      column2.setData(SWTBinder.TABLE_COLUMN_IDENTIFIER, "value");
      
      binder = new MockSWTBinder(root, form = new MockForm(), new Object());
      dataMeta = (DataProviderMetadata) form.getFormMetadata()
            .getDataProviderMetadatas().get(
                  new MethodEntry(form.getMethod("someDataProvider")));
      widgetBinder = binder.getWidgetBinder(table);
      dataMeta.setResetSelection(true);
   }

   public void testSelectIndexes() {
      assertTrue(widgetBinder instanceof TableWidgetBinder);

      assertNull(widgetBinder.bind(binder, table, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, table, (FieldMetadata) null));
      assertNotNull(widgetBinder.bind(binder, table, dataMeta));

      simulateSelect(2);
      int[] indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 2 }, indexes));

      simulateSelect(0);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 0 }, indexes));

      simulateSelect(1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 1 }, indexes));

      simulateSelect(-1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));

      simulateSelect(new int[] { 2, 3 });
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 2, 3 }, indexes));

      simulateSelect(-1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));
   }

   public void testUpdateIndexes() {
      assertNull(widgetBinder.bind(binder, table, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, table, (FieldMetadata) null));
      assertNotNull(boundDataProvider = widgetBinder.bind(binder, table,
            dataMeta));

      boundDataProvider.updateIndexes(new int[] { 2 });
      assertEquals(2, table.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { 1 });
      assertEquals(1, table.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertEquals(-1, table.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { 2, 4 });
      assertTrue(Arrays.equals(new int[] { 2, 4 }, table.getSelectionIndices()));

      boundDataProvider.updateIndexes(new int[] {});
      assertTrue(Arrays.equals(new int[] {}, table.getSelectionIndices()));

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertTrue(Arrays.equals(new int[] {}, table.getSelectionIndices()));
   }

   public void testUpdateList() throws Exception {
      assertNull(widgetBinder.bind(binder, table, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, table, (FieldMetadata) null));

      boundDataProvider = widgetBinder.bind(binder, table, dataMeta);
      assertNotNull(boundDataProvider);

      Object[] newList = new Object[] { new MockBean("newOne", "NewOne"),
            new MockBean("newTwo", "NewTwo"),
            new MockBean("newThree", "NewThree") };

      boundDataProvider.updateList(Arrays.asList(newList));
      int count = table.getItemCount();
      assertEquals(0, table.getSelectionIndices().length);
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         for (int j = 0; j < 2; j++) {
            assertEquals(table.getItems()[i].getText(j), PropertyUtils
                  .getProperty(newList[i], j == 0 ? "key" : "value"));
         }
      }

      table.select(new int[] {0, 1, 2});
      boundDataProvider.updateList(Arrays.asList(newList));
      count = table.getItemCount();
      assertEquals(0, table.getSelectionIndices().length);
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         for (int j = 0; j < 2; j++) {
            assertEquals(table.getItems()[i].getText(j), PropertyUtils
                  .getProperty(newList[i], j == 0 ? "key" : "value"));
         }
      }

      dataMeta.setResetSelection(false);
      table.select(new int[] {0, 1, 2});
      boundDataProvider.updateList(Arrays.asList(newList));
      count = table.getItemCount();
      assertTrue(Arrays.equals(new int[] {0, 1, 2}, table.getSelectionIndices()));
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         for (int j = 0; j < 2; j++) {
            assertEquals(table.getItems()[i].getText(j), PropertyUtils
                  .getProperty(newList[i], j == 0 ? "key" : "value"));
         }
      }

      newList = new Object[] { new MockBean("other", "Other") };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = table.getItemCount();
      assertTrue(Arrays.equals(new int[] {0}, table.getSelectionIndices()));
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         for (int j = 0; j < 2; j++) {
            assertEquals(table.getItems()[i].getText(j), PropertyUtils
                  .getProperty(newList[i], j == 0 ? "key" : "value"));
         }
      }

      newList = new String[0];
      boundDataProvider.updateList(Arrays.asList(newList));
      count = table.getItemCount();
      assertEquals(count, newList.length);
   }

   private void simulateSelect(int index) {
      simulateSelect(new int[] { index });
   }

   private void simulateSelect(int[] indexes) {
      table.deselectAll();

      if (indexes != null && indexes.length > 0
            && (indexes.length != 1 || indexes[0] > -1)) {
         table.select(indexes);
      }

      Event event = new Event();
      event.widget = table;
      event.button = 1;
      event.type = SWT.Selection;
      table.notifyListeners(event.type, event);
   }
}
