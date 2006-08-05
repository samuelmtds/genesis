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
package net.java.dev.genesis.ui.swing.factory;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import net.java.dev.genesis.ui.swing.SwingBinder;
import net.java.dev.genesis.ui.swing.renderers.KeyValueListCellRenderer;

public class SwingFactory {
   public static JComboBox createComboBox() {
      return createComboBox((String) null, (String) null, (String) null);
   }

   public static JComboBox createComboBox(String keyProperty) {
      return createComboBox(keyProperty, (String) null, (String) null);
   }

   public static JComboBox createComboBox(String keyProperty,
         String valueProperty) {
      return createComboBox(keyProperty, valueProperty, (String) null);
   }

   public static JComboBox createComboBox(String keyProperty,
         String valueProperty, String blankLabelProperty) {
      JComboBox combobox = new JComboBox();
      combobox.putClientProperty(SwingBinder.KEY_PROPERTY, keyProperty);
      combobox.putClientProperty(SwingBinder.VALUE_PROPERTY, valueProperty);

      if (blankLabelProperty != null) {
         combobox.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);
         combobox.putClientProperty(SwingBinder.BLANK_LABEL_PROPERTY,
               blankLabelProperty);
      }

      return combobox;
   }

   public static JComboBox createComboBox(SwingBinder binder, String name) {
      return createComboBox(binder, name, (String) null, (String) null);
   }

   public static JComboBox createComboBox(SwingBinder binder, String name,
         String keyProperty) {
      return createComboBox(binder, name, keyProperty, (String) null);
   }

   public static JComboBox createComboBox(SwingBinder binder, String name,
         String keyProperty, String valueProperty) {
      return createComboBox(binder, name, keyProperty, valueProperty,
            (String) null);
   }

   public static JComboBox createComboBox(SwingBinder binder, String name,
         String keyProperty, String valueProperty, String blankLabelProperty) {
      JComboBox combobox = createComboBox(keyProperty, valueProperty,
            blankLabelProperty);
      combobox.setName(name);
      combobox.setRenderer(new KeyValueListCellRenderer(combobox));

      binder.getLookupStrategy().register(name, combobox);

      return combobox;
   }

   public static JList createList() {
      return createList((String) null, (String) null, (String) null);
   }

   public static JList createList(String keyProperty) {
      return createList(keyProperty, (String) null, (String) null);
   }

   public static JList createList(String keyProperty, String valueProperty) {
      return createList(keyProperty, keyProperty, (String) null);
   }

   public static JList createList(String keyProperty, String valueProperty,
         String blankLabelProperty) {
      JList list = new JList();
      list.putClientProperty(SwingBinder.KEY_PROPERTY, keyProperty);
      list.putClientProperty(SwingBinder.VALUE_PROPERTY, valueProperty);

      if (blankLabelProperty != null) {
         list.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);
         list.putClientProperty(SwingBinder.BLANK_LABEL_PROPERTY,
               blankLabelProperty);
      }

      return list;
   }

   public static JList createList(SwingBinder binder, String name) {
      return createList(binder, name, (String) null, (String) null,
            (String) null);
   }

   public static JList createList(SwingBinder binder, String name,
         String keyProperty) {
      return createList(binder, name, keyProperty, (String) null, (String) null);
   }

   public static JList createList(SwingBinder binder, String name,
         String keyProperty, String valueProperty) {
      return createList(binder, name, keyProperty, valueProperty, (String) null);
   }

   public static JList createList(SwingBinder binder, String name,
         String keyProperty, String valueProperty, String blankLabelProperty) {
      JList list = createList(keyProperty, valueProperty, blankLabelProperty);
      list.setName(name);
      list.setCellRenderer(new KeyValueListCellRenderer(list));

      binder.getLookupStrategy().register(name, list);

      return list;
   }

   public static JTable createTable(String[] columnIdentifiers,
         String[] columnDisplayName) {
      return createTable(columnIdentifiers, columnDisplayName, (int[]) null,
            (TableCellRenderer[]) null);
   }

   public static JTable createTable(String[] columnIdentifiers,
         String[] columnDisplayName, int[] preferredWidth) {
      return createTable(columnIdentifiers, columnDisplayName, preferredWidth,
            (TableCellRenderer[]) null);
   }

   public static JTable createTable(String[] columnIdentifiers,
         String[] columnDisplayName, int[] preferredWidth,
         TableCellRenderer[] renderers) {
      if (columnIdentifiers.length != columnDisplayName.length) {
         throw new IllegalArgumentException(
               "Column identifiers and Column names must have same length");
      }

      if (preferredWidth != null
            && preferredWidth.length != columnIdentifiers.length) {
         throw new IllegalArgumentException(
               "Column identifier and Column preferred width must have same length");
      }

      if (renderers != null && renderers.length != columnIdentifiers.length) {
         throw new IllegalArgumentException(
               "Column identifier and TableCellRenderer must have same length");
      }

      TableColumnModel columnModel = new DefaultTableColumnModel();
      TableModel model = new DefaultTableModel(columnDisplayName, 0) {
         public boolean isCellEditable(int row, int column) {
            return false;
         }
      };

      for (int i = 0; i < columnIdentifiers.length; i++) {
         TableColumn column = new TableColumn(i);
         //column.setIdentifier(columnIdentifiers[i]);
         column.setHeaderValue(columnDisplayName[i]);

         if (preferredWidth != null && preferredWidth[i] > 0) {
            column.setPreferredWidth(preferredWidth[i]);
         }

         if (renderers != null && renderers[i] != null) {
            column.setCellRenderer(renderers[i]);
         }

         columnModel.addColumn(column);
      }

      JTable table = new JTable(model, columnModel);
      table.putClientProperty(SwingBinder.COLUMN_NAMES, columnIdentifiers);

      return table;
   }

   public static JTable createTable(SwingBinder binder, String name,
         String[] columnIdentifiers, String[] columnDisplayName) {
      return createTable(binder, name, columnIdentifiers, columnDisplayName,
            (int[]) null, (TableCellRenderer[]) null);
   }

   public static JTable createTable(SwingBinder binder, String name,
         String[] columnIdentifiers, String[] columnDisplayName,
         int[] preferredWidth) {
      return createTable(binder, name, columnIdentifiers, columnDisplayName,
            preferredWidth, (TableCellRenderer[]) null);
   }

   public static JTable createTable(SwingBinder binder, String name,
         String[] columnIdentifiers, String[] columnDisplayName,
         int[] preferredWidth, TableCellRenderer[] renderers) {
      JTable table = createTable(columnIdentifiers, columnDisplayName,
            preferredWidth, renderers);
      table.setName(name);

      binder.getLookupStrategy().register(name, table);

      return table;
   }
}
