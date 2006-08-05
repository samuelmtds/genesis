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
package net.java.dev.genesis.ui.swing.renderers;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import net.java.dev.genesis.text.FormatterRegistry;
import net.java.dev.genesis.ui.swing.SwingBinder;

public class FormatterTableCellRenderer extends DefaultTableCellRenderer {
   protected SwingBinder getBinder(JTable table) {
      return (SwingBinder) table.getClientProperty(SwingBinder.BINDER_KEY);
   }

   protected String getColumnIdentifier(JTable table, int index) {
      TableColumn column = table.getColumnModel().getColumn(index);
      String[] names = (String[]) table.getClientProperty(SwingBinder.COLUMN_NAMES);

      if (names != null && names.length > column.getModelIndex()) {
         return names[column.getModelIndex()];
      }

      return (String) column.getIdentifier();
   }

   protected Object format(JTable table, Object value, int column) {
      final SwingBinder binder = getBinder(table);
      if (binder == null) {
         return format(value);
      }

      final String componentName = binder.getLookupStrategy().getName(table);
      final String property = getColumnIdentifier(table, column);

      return binder.format(componentName + '.' + property, value);
   }

   protected Object format(Object value) {
      return FormatterRegistry.getInstance().format(value);
   }

   public Component getTableCellRendererComponent(JTable table, Object value,
         boolean isSelected, boolean hasFocus, int row, int column) {
      return super.getTableCellRendererComponent(table, format(table, value,
            column), isSelected, hasFocus, row, column);
   }
}
