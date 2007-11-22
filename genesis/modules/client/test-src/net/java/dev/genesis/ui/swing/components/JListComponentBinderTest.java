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
package net.java.dev.genesis.ui.swing.components;

import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockBean;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.reflection.MethodEntry;
import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.MockSwingBinder;
import net.java.dev.genesis.ui.swing.SwingBinder;

public class JListComponentBinderTest extends GenesisTestCase {
   private JList list;
   private MockSwingBinder binder;
   private WidgetBinder componentBinder;
   private BoundDataProvider boundDataProvider;
   private BoundField boundField;
   private MockForm form;
   private DataProviderMetadata dataMeta;

   public JListComponentBinderTest() {
      super("JList Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      list = SwingUtils.newList();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      dataMeta = (DataProviderMetadata) form.getFormMetadata()
            .getDataProviderMetadatas().get(
                  new MethodEntry(form.getMethod("someDataProvider")));
      componentBinder = binder.getWidgetBinder(list);
      dataMeta.setResetSelection(true);
   }

   public void testSelectIndexes() {
      list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      assertTrue(componentBinder instanceof JListComponentBinder);

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(componentBinder.bind(binder, list, dataMeta));

      list.setSelectedIndex(2);
      int[] indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 2 }, indexes));

      list.setSelectedIndex(0);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 0 }, indexes));

      list.setSelectedIndex(1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 1 }, indexes));

      list.clearSelection();
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));

      list.setSelectedIndices(new int[] {2, 3});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 2, 3 }, indexes));

      list.setSelectedIndices(new int[] {});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));

      list.setSelectedIndices(new int[] {-1});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));
   }

   public void testSelectIndexesWithBlank() {
      list.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(componentBinder.bind(binder, list, dataMeta));

      list.setSelectedIndex(2);
      int[] indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 1 }, indexes));

      list.setSelectedIndex(1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 0 }, indexes));

      list.setSelectedIndex(0);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[0], indexes));

      list.setSelectedIndex(-1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[0], indexes));

      list.setSelectedIndices(new int[] {2, 3});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 1, 2 }, indexes));
      
      list.setSelectedIndices(new int[] {0});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));

      list.setSelectedIndices(new int[] {});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));

      list.setSelectedIndices(new int[] {-1});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));
   }

   public void testUpdateIndexes() {
      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundDataProvider = componentBinder.bind(binder, list,
            dataMeta));

      boundDataProvider.updateIndexes(new int[] { 2 });
      assertEquals(2, list.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { 1 });
      assertEquals(1, list.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertEquals(-1, list.getSelectedIndex());
      
      boundDataProvider.updateIndexes(new int[] { 2, 4 });
      assertTrue(Arrays.equals(new int[] { 2, 4 }, list.getSelectedIndices()));
      
      boundDataProvider.updateIndexes(new int[] {});
      assertTrue(Arrays.equals(new int[] {}, list.getSelectedIndices()));
      
      boundDataProvider.updateIndexes(new int[] {-1});
      assertTrue(Arrays.equals(new int[] {}, list.getSelectedIndices()));
   }

   public void testUpdateIndexesWithBlank() {
      list.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundDataProvider = componentBinder.bind(binder, list,
            dataMeta));

      boundDataProvider.updateIndexes(new int[] { 2 });
      assertEquals(3, list.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { 1 });
      assertEquals(2, list.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { 0 });
      assertEquals(1, list.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertEquals(-1, list.getSelectedIndex());
      
      boundDataProvider.updateIndexes(new int[] { 1, 3 });
      assertTrue(Arrays.equals(new int[] { 2, 4 }, list.getSelectedIndices()));
      
      boundDataProvider.updateIndexes(new int[] {});
      assertTrue(Arrays.equals(new int[] {}, list.getSelectedIndices()));
      
      boundDataProvider.updateIndexes(new int[] {-1});
      assertTrue(Arrays.equals(new int[] {}, list.getSelectedIndices()));
   }

   public void testUpdateList() throws Exception {
      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));

      boundDataProvider = componentBinder.bind(binder, list, dataMeta);
      assertNotNull(boundDataProvider);

      String[] newList = new String[] { "newOne", "newTwo", "newThree" };
      boundDataProvider.updateList(Arrays.asList(newList));
      int count = list.getModel().getSize();
      assertEquals(-1, list.getSelectedIndex());
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getModel().getElementAt(i), newList[i]);
      }

      list.setSelectedIndices(new int[] {0, 1, 2});
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getModel().getSize();
      assertEquals(0, list.getSelectedIndices().length);
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getModel().getElementAt(i), newList[i]);
      }

      dataMeta.setResetSelection(false);
      list.setSelectedIndices(new int[] {0, 1, 2});
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getModel().getSize();
      assertTrue(Arrays.equals(new int[] {0, 1, 2}, list.getSelectedIndices()));
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getModel().getElementAt(i), newList[i]);
      }

      newList = new String[] { "other" };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getModel().getSize();
      assertTrue(Arrays.equals(new int[] {0}, list.getSelectedIndices()));
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getModel().getElementAt(i), newList[i]);
      }

      newList = new String[0];
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getModel().getSize();
      assertEquals(count, newList.length);
   }

   public void testUpdateListWithBlank() throws Exception {
      list.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));

      boundDataProvider = componentBinder.bind(binder, list, dataMeta);
      assertNotNull(boundDataProvider);

      String[] newList = new String[] { "newOne", "newTwo", "newThree" };
      boundDataProvider.updateList(Arrays.asList(newList));
      int count = list.getModel().getSize();
      assertTrue(Arrays.equals(new int[] {0}, list.getSelectedIndices()));
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getModel().getElementAt(i + 1), newList[i]);
      }

      list.setSelectedIndices(new int[] {1, 2, 3});
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getModel().getSize();
      assertTrue(Arrays.equals(new int[] {0}, list.getSelectedIndices()));
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getModel().getElementAt(i + 1), newList[i]);
      }

      dataMeta.setResetSelection(false);
      list.setSelectedIndices(new int[] {1, 2, 3});
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getModel().getSize();
      assertTrue(Arrays.equals(new int[] {1, 2, 3}, list.getSelectedIndices()));
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getModel().getElementAt(i + 1), newList[i]);
      }

      newList = new String[] { "other" };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getModel().getSize();
      assertTrue(Arrays.equals(new int[] {1}, list.getSelectedIndices()));
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getModel().getElementAt(i + 1), newList[i]);
      }

      newList = new String[0];
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getModel().getSize();
      assertEquals(count, newList.length + 1);
   }

   public void testSetValue() throws Exception {
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      list.putClientProperty(SwingBinder.KEY_PROPERTY, "key");

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            list, dataMeta));

      DefaultListModel model = (DefaultListModel) list.getModel();

      Object value = model.getElementAt(3);
      boundField.setValue(value);
      assertEquals(value, list.getSelectedValue());
      assertEquals(3, list.getSelectedIndex());

      value = model.getElementAt(0);
      boundField.setValue(value);
      assertEquals(value, list.getSelectedValue());
      assertEquals(0, list.getSelectedIndex());

      value = new MockBean("none", "None");
      boundField.setValue(value);
      assertNull(list.getSelectedValue());
      assertEquals(-1, list.getSelectedIndex());
   }

   public void testSetValueWithBlank() throws Exception {
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      list.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);
      list.putClientProperty(SwingBinder.KEY_PROPERTY, "key");

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            list, dataMeta));

      DefaultListModel model = (DefaultListModel) list.getModel();

      Object value = model.getElementAt(3);
      boundField.setValue(value);
      assertEquals(value, list.getSelectedValue());
      assertEquals(3, list.getSelectedIndex());

      value = model.getElementAt(1);
      boundField.setValue(value);
      assertEquals(value, list.getSelectedValue());
      assertEquals(1, list.getSelectedIndex());

      value = new MockBean("none", "None");
      boundField.setValue(value);
      assertNull(list.getSelectedValue());
      assertEquals(-1, list.getSelectedIndex());
   }

   public void testSetValueMultiSelection() throws Exception {
      list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      list.putClientProperty(SwingBinder.KEY_PROPERTY, "key");

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            list, dataMeta));

      DefaultListModel model = (DefaultListModel) list.getModel();

      Object[] values = new Object[] { model.getElementAt(2),
            model.getElementAt(4) };
      boundField.setValue(values);
      assertTrue(Arrays.equals(values, list.getSelectedValues()));
      assertTrue(Arrays.equals(new int[] { 2, 4 }, list.getSelectedIndices()));

      values = new Object[] { model.getElementAt(0) };
      boundField.setValue(values);
      assertEquals(values[0], list.getSelectedValue());
      assertEquals(0, list.getSelectedIndex());

      values = new Object[] { new MockBean("none", "None") };
      boundField.setValue(values);
      assertNull(list.getSelectedValue());
      assertEquals(-1, list.getSelectedIndex());
   }

   public void testSetValueMultiSelectionWithBlank() throws Exception {
      list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      list.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);
      list.putClientProperty(SwingBinder.KEY_PROPERTY, "key");

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            list, dataMeta));

      DefaultListModel model = (DefaultListModel) list.getModel();

      Object[] values = new Object[] { model.getElementAt(2),
            model.getElementAt(4) };
      boundField.setValue(values);
      assertTrue(Arrays.equals(values, list.getSelectedValues()));
      assertTrue(Arrays.equals(new int[] { 2, 4 }, list.getSelectedIndices()));

      values = new Object[] { model.getElementAt(1) };
      boundField.setValue(values);
      assertEquals(values[0], list.getSelectedValue());
      assertEquals(1, list.getSelectedIndex());

      values = new Object[] { new MockBean("none", "None") };
      boundField.setValue(values);
      assertNull(list.getSelectedValue());
      assertEquals(-1, list.getSelectedIndex());
   }
}
