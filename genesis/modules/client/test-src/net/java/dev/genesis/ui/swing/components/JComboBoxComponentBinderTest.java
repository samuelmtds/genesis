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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

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

public class JComboBoxComponentBinderTest extends GenesisTestCase {
   private JComboBox combo;
   private MockSwingBinder binder;
   private WidgetBinder componentBinder;
   private BoundDataProvider boundDataProvider;
   private BoundField boundField;
   private MockForm form;
   private DataProviderMetadata dataMeta;

   public JComboBoxComponentBinderTest() {
      super("JComboBox Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      combo = SwingUtils.newCombo();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      dataMeta = (DataProviderMetadata) form.getFormMetadata()
            .getDataProviderMetadatas().get(
                  new MethodEntry(form.getMethod("someDataProvider")));
      componentBinder = binder.getWidgetBinder(combo);
      dataMeta.setResetSelection(true);
   }

   public void testSelectIndexes() {
      assertTrue(componentBinder instanceof JComboBoxComponentBinder);
      assertNull(componentBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(componentBinder.bind(binder, combo, dataMeta));

      combo.setSelectedIndex(2);
      int[] indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 2 }, indexes));

      combo.setSelectedIndex(0);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 0 }, indexes));

      combo.setSelectedIndex(-1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[0], indexes));
   }

   public void testSelectIndexesWithBlank() {
      combo.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(componentBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(componentBinder.bind(binder, combo, dataMeta));

      combo.setSelectedIndex(2);
      int[] indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 1 }, indexes));

      combo.setSelectedIndex(1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 0 }, indexes));

      combo.setSelectedIndex(0);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[0], indexes));

      combo.setSelectedIndex(-1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[0], indexes));
   }

   public void testUpdateIndexes() {
      assertNull(componentBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(boundDataProvider = componentBinder.bind(binder, combo,
            dataMeta));

      boundDataProvider.updateIndexes(new int[] { 2 });
      assertEquals(2, combo.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { 1 });
      assertEquals(1, combo.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertEquals(-1, combo.getSelectedIndex());
   }

   public void testUpdateIndexesWithBlank() {
      combo.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(componentBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));

      boundDataProvider = componentBinder.bind(binder, combo, dataMeta);
      assertNotNull(boundDataProvider);

      boundDataProvider.updateIndexes(new int[] { 2 });
      assertEquals(3, combo.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { 1 });
      assertEquals(2, combo.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { 0 });
      assertEquals(1, combo.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertEquals(-1, combo.getSelectedIndex());
   }

   public void testUpdateList() throws Exception {
      assertNull(componentBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));

      boundDataProvider = componentBinder.bind(binder, combo, dataMeta);
      assertNotNull(boundDataProvider);

      String[] newList = new String[] { "newOne", "newTwo", "newThree" };
      boundDataProvider.updateList(Arrays.asList(newList));
      int count = combo.getItemCount();
      assertEquals(-1, combo.getSelectedIndex());
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItemAt(i), newList[i]);
      }

      combo.setSelectedIndex(2);
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(-1, combo.getSelectedIndex());
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItemAt(i), newList[i]);
      }

      dataMeta.setResetSelection(false);
      combo.setSelectedIndex(2);

      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(2, combo.getSelectedIndex());
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItemAt(i), newList[i]);
      }

      newList = new String[] { "other" };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(-1, combo.getSelectedIndex());
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItemAt(i), newList[i]);
      }

      newList = new String[0];
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(count, newList.length);
   }

   public void testUpdateListWithBlank() throws Exception {
      combo.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);
      assertNull(componentBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));

      boundDataProvider = componentBinder.bind(binder, combo, dataMeta);
      assertNotNull(boundDataProvider);

      String[] newList = new String[] { "newOne", "newTwo", "newThree" };
      boundDataProvider.updateList(Arrays.asList(newList));
      int count = combo.getItemCount();
      assertEquals(0, combo.getSelectedIndex());
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItemAt(i + 1), newList[i]);
      }

      combo.setSelectedIndex(3);
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(0, combo.getSelectedIndex());
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItemAt(i + 1), newList[i]);
      }

      dataMeta.setResetSelection(false);
      combo.setSelectedIndex(3);
      
      newList = new String[] { "newOne", "newTwo", "newThree" };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(3, combo.getSelectedIndex());
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItemAt(i + 1), newList[i]);
      }

      newList = new String[] { "other" };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(0, combo.getSelectedIndex());
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItemAt(i + 1), newList[i]);
      }

      newList = new String[0];
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(count, newList.length + 1);
   }

   public void testSetValue() throws Exception {
      combo.putClientProperty(SwingBinder.KEY_PROPERTY, "key");

      assertNull(componentBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            combo, dataMeta));

      DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();

      Object value = model.getElementAt(3);
      boundField.setValue(value);
      assertEquals(value, combo.getSelectedItem());
      assertEquals(3, combo.getSelectedIndex());

      value = model.getElementAt(0);
      boundField.setValue(value);
      assertEquals(value, combo.getSelectedItem());
      assertEquals(0, combo.getSelectedIndex());

      value = new MockBean("none", "None");
      boundField.setValue(value);
      assertNull(combo.getSelectedItem());
      assertEquals(-1, combo.getSelectedIndex());
   }

   public void testSetValueWithBlank() throws Exception {
      combo.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);
      combo.putClientProperty(SwingBinder.KEY_PROPERTY, "key");

      assertNull(componentBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            combo, dataMeta));

      DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();

      Object value = model.getElementAt(3);
      boundField.setValue(value);
      assertEquals(value, combo.getSelectedItem());
      assertEquals(3, combo.getSelectedIndex());

      value = model.getElementAt(1);
      boundField.setValue(value);
      assertEquals(value, combo.getSelectedItem());
      assertEquals(1, combo.getSelectedIndex());

      value = new MockBean("none", "None");
      boundField.setValue(value);
      assertNull(combo.getSelectedItem());
      assertEquals(-1, combo.getSelectedIndex());
   }
}
