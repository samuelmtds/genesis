/*
 * The Genesis Project
 * Copyright (C) 2006-2009 Summa Technologies do Brasil Ltda.
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
import net.java.dev.genesis.helpers.EnumHelper;
import net.java.dev.genesis.mockobjects.MockBean;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.reflection.MethodEntry;
import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swt.MockSWTBinder;
import net.java.dev.genesis.ui.swt.SWTBinder;
import net.java.dev.genesis.ui.swt.widgets.ComboWidgetBinder;

import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class ComboWidgetBinderTest extends GenesisTestCase {
   private Shell root;
   private Combo combo;
   private MockSWTBinder binder;
   private WidgetBinder widgetBinder;
   private BoundDataProvider boundDataProvider;
   private BoundField boundField;
   private MockForm form;
   private DataProviderMetadata dataMeta;
   private MockBean[] beans;

   public ComboWidgetBinderTest() {
      super("Combo Widget Binder Unit Test");
   }

   protected void setUp() throws Exception {
      combo = new Combo(root = new Shell(), SWT.NONE);
      combo.setData(SWTBinder.KEY_PROPERTY, "key");
      combo.setData(SWTBinder.VALUE_PROPERTY, "value");
      binder = new MockSWTBinder(root, form = new MockForm(), new Object());
      dataMeta = (DataProviderMetadata) form.getFormMetadata()
            .getDataProviderMetadatas().get(
                  new MethodEntry(form.getMethod("someDataProvider")));

      beans = new MockBean[] { new MockBean("one", "One"),
            new MockBean("two", "Two"), new MockBean("three", "Three"),
            new MockBean("four", "Four"), new MockBean("five", "Five") };

      String[] values = new String[beans.length];

      for (int i = 0; i < beans.length; i++) {
         values[i] = beans[i].getValue();
         setKey(i, getKey(beans[i]));
      }

      combo.setItems(values);

      widgetBinder = binder.getWidgetBinder(combo);
      dataMeta.setResetSelection(true);
   }

   protected void setKey(int index, String key) throws Exception {
      combo.setData(SWTBinder.KEY_PROPERTY + '-' + index, key);
   }

   protected String getKey(Object value) throws Exception {
      String keyPropertyName = (String) combo.getData(SWTBinder.KEY_PROPERTY);

      if (keyPropertyName != null) {
         Object o = (value == null) ? null : PropertyUtils.getProperty(value,
               keyPropertyName);

         return binder.format(getName(), keyPropertyName, o);
      } else if (EnumHelper.getInstance().isEnum(value)) {
         return value.toString();
      }

      return String.valueOf(System.identityHashCode(value));
   }

   protected String getValue(Widget widget, Object value) throws Exception {
      String valueProperty = (String) widget.getData(SWTBinder.VALUE_PROPERTY);
      if (value instanceof String) {
         return (String) value;
      } else if (valueProperty == null) {
         return binder.format(binder.getLookupStrategy().getName(widget), null,
               value);
      }

      return binder.format(binder.getLookupStrategy().getName(widget),
            valueProperty, PropertyUtils.getProperty(value, valueProperty));
   }

   public void testSelectIndexes() {
      assertTrue(widgetBinder instanceof ComboWidgetBinder);
      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(widgetBinder.bind(binder, combo, dataMeta));

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

      simulateSelect(-1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[0], indexes));
   }

   public void testSelectIndexesWithBlank() {
      combo.setData(SWTBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(widgetBinder.bind(binder, combo, dataMeta));

      simulateSelect(2);
      int[] indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 1 }, indexes));

      simulateSelect(1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 0 }, indexes));

      simulateSelect(0);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[0], indexes));

      simulateSelect(-1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[0], indexes));
   }

   public void testUpdateIndexes() {
      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(boundDataProvider = widgetBinder.bind(binder, combo,
            dataMeta));

      boundDataProvider.updateIndexes(new int[] { 2 });
      assertEquals(2, combo.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { 1 });
      assertEquals(1, combo.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertEquals(-1, combo.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { 0 });
      assertEquals(0, combo.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] {});
      assertEquals(-1, combo.getSelectionIndex());

      try {
         boundDataProvider.updateIndexes(new int[] {1, 2});
         fail("IllegalArgumentException should be thrown when length of array is greater than one");
      } catch (IllegalArgumentException e) {
         // expected
      }
   }

   public void testUpdateIndexesWithBlank() {
      combo.setData(SWTBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));

      boundDataProvider = widgetBinder.bind(binder, combo, dataMeta);
      assertNotNull(boundDataProvider);

      boundDataProvider.updateIndexes(new int[] { 2 });
      assertEquals(3, combo.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { 1 });
      assertEquals(2, combo.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { 0 });
      assertEquals(1, combo.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertEquals(-1, combo.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { 0 });
      assertEquals(1, combo.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] {});
      assertEquals(-1, combo.getSelectionIndex());

      try {
         boundDataProvider.updateIndexes(new int[] {1, 2});
         fail("IllegalArgumentException should be thrown when length of array is greater than one");
      } catch (IllegalArgumentException e) {
         // expected
      }
   }

   public void testUpdateList() throws Exception {
      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));

      boundDataProvider = widgetBinder.bind(binder, combo, dataMeta);
      assertNotNull(boundDataProvider);

      MockBean[] newList = new MockBean[] { new MockBean("newOne", "NewOne"),
            new MockBean("newTwo", "NewTwo"),
            new MockBean("newThree", "NewThree") };
      boundDataProvider.updateList(Arrays.asList(newList));
      int count = combo.getItemCount();
      assertEquals(-1, combo.getSelectionIndex());
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItem(i), getValue(combo, newList[i]));
      }

      combo.select(2);
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(-1, combo.getSelectionIndex());
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItem(i), getValue(combo, newList[i]));
      }

      dataMeta.setResetSelection(false);
      combo.select(2);

      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(2, combo.getSelectionIndex());
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItem(i), getValue(combo, newList[i]));
      }

      newList = new MockBean[] { new MockBean("other", "Other") };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(-1, combo.getSelectionIndex());
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItem(i), getValue(combo, newList[i]));
      }

      newList = new MockBean[0];
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(count, newList.length);
   }

   public void testUpdateListWithBlank() throws Exception {
      combo.setData(SWTBinder.BLANK_PROPERTY, Boolean.TRUE);
      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));

      boundDataProvider = widgetBinder.bind(binder, combo, dataMeta);
      assertNotNull(boundDataProvider);

      MockBean[] newList = new MockBean[] { new MockBean("newOne", "NewOne"),
            new MockBean("newTwo", "NewTwo"),
            new MockBean("newThree", "NewThree") };
      boundDataProvider.updateList(Arrays.asList(newList));
      int count = combo.getItemCount();
      assertEquals(0, combo.getSelectionIndex());
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItem(i + 1), getValue(combo, newList[i]));
      }

      combo.select(3);
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(0, combo.getSelectionIndex());
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItem(i + 1), getValue(combo, newList[i]));
      }

      dataMeta.setResetSelection(false);
      combo.select(3);

      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(3, combo.getSelectionIndex());
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItem(i + 1), getValue(combo, newList[i]));
      }

      newList = new MockBean[] { new MockBean("other", "Other") };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(0, combo.getSelectionIndex());
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItem(i + 1), getValue(combo, newList[i]));
      }

      newList = new MockBean[0];
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(count, newList.length + 1);
   }

   public void testSetValue() throws Exception {
      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) widgetBinder.bind(binder, combo,
            dataMeta));

      Object value = beans[3];
      boundField.setValue(value);
      assertEquals(getValue(combo, value), combo.getItem(combo
            .getSelectionIndex()));
      assertEquals(3, combo.getSelectionIndex());

      value = beans[0];
      boundField.setValue(value);
      assertEquals(getValue(combo, value), combo.getItem(combo
            .getSelectionIndex()));
      assertEquals(0, combo.getSelectionIndex());

      value = new MockBean("none", "None");
      boundField.setValue(value);
      assertEquals(-1, combo.getSelectionIndex());
   }

   public void testSetValueWithoutKey() throws Exception {
      combo.setData(SWTBinder.KEY_PROPERTY, null);
      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata)null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata)null));
      assertNotNull(boundField = (BoundField)widgetBinder.bind(binder, combo,
            dataMeta));

      String value = beans[3].getKey();
      boundField.setValue(value);
      assertEquals(3, combo.getSelectionIndex());
      assertEquals(beans[3].getValue(),
            combo.getItem(combo.getSelectionIndex()));

      value = beans[0].getKey();
      boundField.setValue(value);
      assertEquals(0, combo.getSelectionIndex());
      assertEquals(beans[0].getValue(),
            combo.getItem(combo.getSelectionIndex()));

      value = "none";
      boundField.setValue(value);
      assertEquals(-1, combo.getSelectionIndex());
   }

   public void testSetValueUsingString() throws Exception {
      combo.setData(SWTBinder.KEY_PROPERTY, null);
      String[] values = new String[] {"one", "two", "three", "four", "five"};
      combo.setItems(values);

      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) widgetBinder.bind(binder, combo,
            dataMeta));

      String value = new String(values[3]);
      boundField.setValue(value);
      assertEquals(3, combo.getSelectionIndex());
      assertEquals(value, combo.getItem(combo.getSelectionIndex()));

      value = new String(values[0]);
      boundField.setValue(value);
      assertEquals(0, combo.getSelectionIndex());
      assertEquals(value, combo.getItem(combo.getSelectionIndex()));

      value = "none";
      boundField.setValue(value);
      assertEquals(-1, combo.getSelectionIndex());
   }

   public void testSetValueWithBlank() throws Exception {
      combo.setData(SWTBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) widgetBinder.bind(binder, combo,
            dataMeta));

      Object value = beans[3];
      boundField.setValue(value);
      assertEquals(getValue(combo, value), combo.getItem(combo
            .getSelectionIndex()));
      assertEquals(3, combo.getSelectionIndex());

      value = beans[1];
      boundField.setValue(value);
      assertEquals(getValue(combo, value), combo.getItem(combo
            .getSelectionIndex()));
      assertEquals(1, combo.getSelectionIndex());

      value = new MockBean("none", "None");
      boundField.setValue(value);
      assertEquals(-1, combo.getSelectionIndex());
   }

   public void testSetValueWithBlankUsingString() throws Exception {
      combo.setData(SWTBinder.BLANK_PROPERTY, Boolean.TRUE);
      combo.setData(SWTBinder.KEY_PROPERTY, null);
      String[] values = new String[] {"one", "two", "three", "four", "five"};
      combo.setItems(values);

      assertNull(widgetBinder.bind(binder, combo, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) widgetBinder.bind(binder, combo,
            dataMeta));

      String value = new String(values[3]);
      boundField.setValue(value);
      assertEquals(3, combo.getSelectionIndex());
      assertEquals(value, combo.getItem(combo.getSelectionIndex()));

      value = new String(values[1]);
      boundField.setValue(value);
      assertEquals(1, combo.getSelectionIndex());
      assertEquals(value, combo.getItem(combo.getSelectionIndex()));

      value = "none";
      boundField.setValue(value);
      assertEquals(-1, combo.getSelectionIndex());
   }

   private void simulateSelect(int index) {
      if (index < 0) {
         combo.deselectAll();
      } else {
         combo.select(index);
      }
      
      Event event = new Event();
      event.widget = combo;
      event.button = 1;
      event.type = SWT.Selection;
      combo.notifyListeners(event.type, event);
   }
}
