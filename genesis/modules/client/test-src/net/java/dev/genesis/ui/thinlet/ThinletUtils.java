/*
 * The Genesis Project
 * Copyright (C) 2005-2008 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.thinlet;

import thinlet.Thinlet;

public class ThinletUtils {
   private static final BaseThinlet helper = new BaseThinlet() {};

   public static Object create(String className, String name) {
      final Object widget = Thinlet.create(className);
      helper.setString(widget, BaseThinlet.NAME, name);
      return widget;
   }

   public static Object setSelectedIndexes(Object component, int[] indexes) {
      if (BaseThinlet.COMBOBOX.equals(Thinlet.getClass(component))) {
         setSelected(component, indexes.length > 0 ? indexes[0] : -1);
         return component;
      }

      final Object[] items = helper.getItems(component);
      int j = 0;

      for (int i = 0; i < items.length; i++) {
         if (indexes.length - j < 1 || indexes[j] != i) {
            setSelected(items[i], false);
            continue;
         }

         setSelected(items[i], true);
         j++;
      }
      return component;
   }

   public static Object setSelected(Object component, int index) {
      helper.setInteger(component, BaseThinlet.SELECTED, index);
      return component;
   }

   public static Object setSelected(Object component, boolean selected) {
      helper.setBoolean(component, BaseThinlet.SELECTED, selected);
      return component;
   }

   public static Object setValue(Object component, int value) {
      helper.setInteger(component, BaseThinlet.VALUE, value);
      return component;
   }

   public static Object setText(Object component, String text) {
      helper.setString(component, BaseThinlet.TEXT, text);
      return component;
   }

   public static Object setGroup(Object component, String group) {
      helper.setString(component, BaseThinlet.GROUP, group);
      return component;
   }

   public static Object setBlank(Object component) {
      helper.putProperty(component, "blank", "true");
      return component;
   }

   public static Object newCheckbox() {
      return create(BaseThinlet.CHECKBOX, "checkbox");
   }

   public static Object newCombobox() {
      Object combobox = create(BaseThinlet.COMBOBOX, "combobox");
      helper.add(combobox, newChoice(0));
      helper.add(combobox, newChoice(1));
      helper.add(combobox, newChoice(2));
      return combobox;
   }

   public static Object newChoice(int id) {
      return create(BaseThinlet.CHOICE, "choice_" + id);
   }

   public static Object newList() {
      Object list = create(BaseThinlet.LIST, "list");
      helper.add(list, newItem(0));
      helper.add(list, newItem(1));
      helper.add(list, newItem(2));
      return list;
   }

   public static Object newItem(int id) {
      Object item = create(BaseThinlet.ITEM, "item_" + id);
      return item;
   }

   public static Object newProgressBar() {
      return create(BaseThinlet.PROGRESS_BAR, "progressbar");
   }

   public static Object newSlider() {
      return create(BaseThinlet.SLIDER, "slider");
   }

   public static Object newSpinBox() {
      return create(BaseThinlet.SPINBOX, "spinbox");
   }

   public static Object newToggleButton() {
      return create(BaseThinlet.TOGGLE_BUTTON, "togglebutton");
   }

   public static Object newTextField() {
      return create(BaseThinlet.TEXTFIELD, "textfield");
   }

   public static Object newPasswordField() {
      return create(BaseThinlet.PASSWORD_FIELD, "passwordfield");
   }

   public static Object newTextArea() {
      return create(BaseThinlet.TEXTAREA, "textarea");
   }

   public static Object newLabel() {
      return create(BaseThinlet.LABEL, "label");
   }

   public static Object newTable() {
      Object table = create(BaseThinlet.TABLE, "table");
      helper.add(table, newRow(0));
      helper.add(table, newRow(1));
      helper.add(table, newRow(2));
      return table;
   }

   public static Object newRow(int id) {
      return Thinlet.create(BaseThinlet.ROW);
   }
   
   public static Object newButton() {
      return create(BaseThinlet.BUTTON, "button");
   }

   public static Object newPanel() {
      return create(BaseThinlet.PANEL, BaseThinlet.PANEL);
   }

   public static Object putProperty(Object widget, Object key, Object value) {
      helper.putProperty(widget, key, value);
      return widget;
   }
}