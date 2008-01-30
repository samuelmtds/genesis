/*
 * The Genesis Project
 * Copyright (C) 2006-2008 Summa Technologies do Brasil Ltda.
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

import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class ListWidgetBinderTest extends GenesisTestCase {
   private Shell root;
   private List list;
   private MockSWTBinder binder;
   private WidgetBinder widgetBinder;
   private BoundDataProvider boundDataProvider;
   private BoundField boundField;
   private MockForm form;
   private DataProviderMetadata dataMeta;
   private MockBean[] beans;

   public ListWidgetBinderTest() {
      super("JList Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      list = new List(root = new Shell(), SWT.MULTI);
      list.setData(SWTBinder.KEY_PROPERTY, "key");
      list.setData(SWTBinder.VALUE_PROPERTY, "value");
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

      list.setItems(values);
      
      widgetBinder = binder.getWidgetBinder(list);
      dataMeta.setResetSelection(true);
   }

   protected void createSingleList() throws Exception {
      list = new List(root = new Shell(), SWT.SINGLE);
      list.setData(SWTBinder.KEY_PROPERTY, "key");
      list.setData(SWTBinder.VALUE_PROPERTY, "value");
      String[] values = new String[beans.length];

      for (int i = 0; i < beans.length; i++) {
         values[i] = beans[i].getValue();
         setKey(i, getKey(beans[i]));
      }

      list.setItems(values);
      
      widgetBinder = binder.getWidgetBinder(list);
      dataMeta.setResetSelection(true);
   }

   protected void setKey(int index, String key) throws Exception {
      list.setData(SWTBinder.KEY_PROPERTY + '-' + index, key);
   }

   protected String getKey(Object value) throws Exception {
      String keyPropertyName = (String) list.getData(SWTBinder.KEY_PROPERTY);

      if (keyPropertyName != null) {
         Object o = (value == null) ? null : PropertyUtils.getProperty(value,
               keyPropertyName);

         return binder.format(getName(), keyPropertyName, o);
      } else if (EnumHelper.getInstance().isEnum(value)) {
         return value.toString();
      }

      return String.valueOf(System.identityHashCode(value));
   }

   protected String[] getValues(Widget widget, Object[] values) throws Exception {
      String[] array = new String[values.length];
      
      for (int i = 0; i < values.length; i++) {
         array[i] = getValue(widget, values[i]);
      }
      
      return array;
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
      assertTrue(widgetBinder instanceof ListWidgetBinder);

      assertNull(widgetBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(widgetBinder.bind(binder, list, dataMeta));

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

      simulateSelect(new int[] {2, 3});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 2, 3 }, indexes));

      simulateSelect(new int[] {});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));

      simulateSelect(new int[] {-1});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));
   }

   public void testSelectIndexesWithBlank() {
      list.setData(SWTBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(widgetBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(widgetBinder.bind(binder, list, dataMeta));

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

      simulateSelect(new int[] {2, 3});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 1, 2 }, indexes));
      
      simulateSelect(new int[] {0});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));

      simulateSelect(new int[] {});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));

      simulateSelect(new int[] {-1});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));
   }

   public void testUpdateIndexes() {
      assertNull(widgetBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundDataProvider = widgetBinder.bind(binder, list,
            dataMeta));

      boundDataProvider.updateIndexes(new int[] { 2 });
      assertEquals(2, list.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { 1 });
      assertEquals(1, list.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertEquals(-1, list.getSelectionIndex());
      
      boundDataProvider.updateIndexes(new int[] { 2, 4 });
      assertTrue(Arrays.equals(new int[] { 2, 4 }, list.getSelectionIndices()));
      
      boundDataProvider.updateIndexes(new int[] {});
      assertTrue(Arrays.equals(new int[] {}, list.getSelectionIndices()));
      
      boundDataProvider.updateIndexes(new int[] {-1});
      assertTrue(Arrays.equals(new int[] {}, list.getSelectionIndices()));
   }

   public void testUpdateIndexesWithBlank() {
      list.setData(SWTBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(widgetBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundDataProvider = widgetBinder.bind(binder, list,
            dataMeta));

      boundDataProvider.updateIndexes(new int[] { 2 });
      assertEquals(3, list.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { 1 });
      assertEquals(2, list.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { 0 });
      assertEquals(1, list.getSelectionIndex());

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertEquals(-1, list.getSelectionIndex());
      
      boundDataProvider.updateIndexes(new int[] { 1, 3 });
      assertTrue(Arrays.equals(new int[] { 2, 4 }, list.getSelectionIndices()));
      
      boundDataProvider.updateIndexes(new int[] {});
      assertTrue(Arrays.equals(new int[] {}, list.getSelectionIndices()));
      
      boundDataProvider.updateIndexes(new int[] {-1});
      assertTrue(Arrays.equals(new int[] {}, list.getSelectionIndices()));
   }

   public void testUpdateList() throws Exception {
      assertNull(widgetBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, list, (FieldMetadata) null));

      boundDataProvider = widgetBinder.bind(binder, list, dataMeta);
      assertNotNull(boundDataProvider);

      MockBean[] newList = new MockBean[] { new MockBean("newOne", "NewOne"),
            new MockBean("newTwo", "NewTwo"),
            new MockBean("newThree", "NewThree") };
      boundDataProvider.updateList(Arrays.asList(newList));
      assertEquals(0, list.getSelectionIndices().length);
      assertEquals(list.getItemCount(), newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getItem(i), getValue(list, newList[i]));
      }

      list.select(new int[] {0, 1, 2});
      boundDataProvider.updateList(Arrays.asList(newList));
      assertEquals(0, list.getSelectionIndices().length);
      assertEquals(list.getItemCount(), newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getItem(i), getValue(list, newList[i]));
      }

      dataMeta.setResetSelection(false);
      list.select(new int[] {0, 1, 2});
      boundDataProvider.updateList(Arrays.asList(newList));
      assertTrue(Arrays.equals(new int[] {0, 1, 2}, list.getSelectionIndices()));
      assertEquals(list.getItemCount(), newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getItem(i), getValue(list, newList[i]));
      }

      newList = new MockBean[] { new MockBean("other", "Other") };
      boundDataProvider.updateList(Arrays.asList(newList));
      assertTrue(Arrays.equals(new int[] {0}, list.getSelectionIndices()));
      assertEquals(list.getItemCount(), newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getItem(i), getValue(list, newList[i]));
      }

      newList = new MockBean[0];
      boundDataProvider.updateList(Arrays.asList(newList));
      assertEquals(list.getItemCount(), newList.length);
   }

   public void testUpdateListWithBlank() throws Exception {
      list.setData(SWTBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(widgetBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, list, (FieldMetadata) null));

      boundDataProvider = widgetBinder.bind(binder, list, dataMeta);
      assertNotNull(boundDataProvider);

      MockBean[] newList = new MockBean[] { new MockBean("newOne", "NewOne"),
            new MockBean("newTwo", "NewTwo"),
            new MockBean("newThree", "NewThree") };
      boundDataProvider.updateList(Arrays.asList(newList));
      assertTrue(Arrays.equals(new int[] {0}, list.getSelectionIndices()));
      int count = list.getItemCount();
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getItem(i + 1), getValue(list, newList[i]));
      }
      
      list.select(new int[] {1, 2, 3});
      boundDataProvider.updateList(Arrays.asList(newList));
      assertTrue(Arrays.equals(new int[] {0}, list.getSelectionIndices()));
      count = list.getItemCount();
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getItem(i + 1), getValue(list, newList[i]));
      }

      dataMeta.setResetSelection(false);
      list.deselectAll();
      list.select(new int[] {1, 2, 3});
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getItemCount();
      assertTrue(Arrays.equals(new int[] {1, 2, 3}, list.getSelectionIndices()));
      
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getItem(i + 1), getValue(list, newList[i]));
      }

      newList = new MockBean[] { new MockBean("other", "Other") };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getItemCount();
      assertTrue(Arrays.equals(new int[] {1}, list.getSelectionIndices()));
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getItem(i + 1), getValue(list, newList[i]));
      }

      newList = new MockBean[0];
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getItemCount();
      assertEquals(count, newList.length + 1);
   }

   public void testSetValue() throws Exception {
      createSingleList();
      list.setData(SWTBinder.KEY_PROPERTY, "key");

      assertNull(widgetBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) widgetBinder.bind(binder,
            list, dataMeta));

      Object value = beans[3];
      boundField.setValue(value);
      assertEquals(getValue(list, value), list.getSelection()[0]);
      assertEquals(3, list.getSelectionIndex());

      value = beans[0];
      boundField.setValue(value);
      assertEquals(getValue(list, value), list.getSelection()[0]);
      assertEquals(0, list.getSelectionIndex());

      value = new MockBean("none", "None");
      boundField.setValue(value);
      assertEquals(-1, list.getSelectionIndex());
   }

   public void testSetValueWithBlank() throws Exception {
      createSingleList();
      list.setData(SWTBinder.BLANK_PROPERTY, Boolean.TRUE);
      list.setData(SWTBinder.KEY_PROPERTY, "key");

      assertNull(widgetBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) widgetBinder.bind(binder,
            list, dataMeta));

      Object value = beans[3];
      boundField.setValue(value);
      assertEquals(getValue(list, value), list.getSelection()[0]);
      assertEquals(3, list.getSelectionIndex());

      value = beans[1];
      boundField.setValue(value);
      assertEquals(getValue(list, value), list.getSelection()[0]);
      assertEquals(1, list.getSelectionIndex());

      value = new MockBean("none", "None");
      boundField.setValue(value);
      assertEquals(-1, list.getSelectionIndex());
   }

   public void testSetValueWithoutKey() throws Exception {
      createSingleList();
      list.setData(SWTBinder.KEY_PROPERTY, null);

      assertNull(widgetBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) widgetBinder.bind(binder,
            list, dataMeta));

      Object value = new String(beans[3].getKey());
      boundField.setValue(value);
      assertEquals(3, list.getSelectionIndex());
      assertEquals(beans[3].getValue(), list.getSelection()[0]);

      value = new String(beans[0].getKey());
      boundField.setValue(value);
      assertEquals(0, list.getSelectionIndex());
      assertEquals(beans[0].getValue(), list.getSelection()[0]);

      value = "none";
      boundField.setValue(value);
      assertEquals(-1, list.getSelectionIndex());
   }

   public void testSetValueUsingString() throws Exception {
      createSingleList();
      list.setData(SWTBinder.KEY_PROPERTY, null);
      String[] values = new String[] {"one", "two", "three", "four", "five"};
      list.setItems(values);
      
      assertNull(widgetBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) widgetBinder.bind(binder,
            list, dataMeta));

      Object value = new String(values[3]);
      boundField.setValue(value);
      assertEquals(3, list.getSelectionIndex());
      assertEquals(value, list.getSelection()[0]);

      value = new String(values[0]);
      boundField.setValue(value);
      assertEquals(0, list.getSelectionIndex());
      assertEquals(value, list.getSelection()[0]);

      value = "none";
      boundField.setValue(value);
      assertEquals(-1, list.getSelectionIndex());
   }

   public void testSetValueMultiSelection() throws Exception {
      list.setData(SWTBinder.KEY_PROPERTY, "key");

      assertNull(widgetBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) widgetBinder.bind(binder,
            list, dataMeta));

      Object[] values = new Object[] { beans[2], beans[4] };
      boundField.setValue(values);
      assertTrue(Arrays.equals(getValues(list, values), list.getSelection()));
      assertTrue(Arrays.equals(new int[] {2,4}, list.getSelectionIndices()));

      values = new Object[] { beans[0] };
      boundField.setValue(values);
      assertTrue(Arrays.equals(getValues(list, values), list.getSelection()));
      assertTrue(Arrays.equals(new int[] {0}, list.getSelectionIndices()));

      values = new Object[] {new MockBean("none", "None")};
      boundField.setValue(values);
      assertEquals(-1, list.getSelectionIndex());
   }

   public void testSetValueMultiSelectionWithBlank() throws Exception {
      list.setData(SWTBinder.BLANK_PROPERTY, Boolean.TRUE);
      list.setData(SWTBinder.KEY_PROPERTY, "key");

      assertNull(widgetBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) widgetBinder.bind(binder,
            list, dataMeta));

      Object[] values = new Object[] { beans[2], beans[4] };
      boundField.setValue(values);
      assertTrue(Arrays.equals(getValues(list, values), list.getSelection()));
      assertTrue(Arrays.equals(new int[] {2,4}, list.getSelectionIndices()));

      values = new Object[] { beans[1] };
      boundField.setValue(values);
      assertTrue(Arrays.equals(getValues(list, values), list.getSelection()));
      assertTrue(Arrays.equals(new int[] {1}, list.getSelectionIndices()));

      values = new Object[] {new MockBean("none", "None")};
      boundField.setValue(values);
      assertEquals(-1, list.getSelectionIndex());
   }

   private void simulateSelect(int index) {
      simulateSelect(new int[] {index});
   }
   
   private void simulateSelect(int[] indexes) {
      list.deselectAll();

      if (indexes != null && indexes.length > 0
            && (indexes.length != 1 || indexes[0] > -1)) {
         list.select(indexes);
      }
      
      Event event = new Event();
      event.widget = list;
      event.button = 1;
      event.type = SWT.Selection;
      list.notifyListeners(event.type, event);
   }
}
