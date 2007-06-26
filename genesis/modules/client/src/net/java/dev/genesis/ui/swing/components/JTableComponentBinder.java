/*
 * The Genesis Project
 * Copyright (C) 2005-2007  Summa Technologies do Brasil Ltda.
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

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.swing.SwingBinder;
import net.java.dev.genesis.ui.swing.renderers.FormatterTableCellRenderer;

public class JTableComponentBinder extends AbstractComponentBinder {
   private static volatile boolean initialized = false;
   private static Method convertRowIndexToModel;
   private static Method convertRowIndexToView;

   protected static boolean needsConversion() {
      if (initialized) {
         return convertRowIndexToModel != null;
      }

      try {
         convertRowIndexToModel = JTable.class.getMethod(
               "convertRowIndexToModel", new Class[] {int.class});
         convertRowIndexToView = JTable.class.getMethod(
               "convertRowIndexToView", new Class[] {int.class});
      } catch (SecurityException se) {
         IllegalStateException ise = new IllegalStateException(
               "Could not load convertRowIndexTo* methods");
         ise.initCause(se);
         throw ise;
      } catch (NoSuchMethodException nsme) {
         // Expected
      } finally {
         initialized = true;
      }

      return convertRowIndexToModel != null;
   }

   public BoundDataProvider bind(SwingBinder binder, Component component,
         DataProviderMetadata dataProviderMetadata) {
      return new JTableComponentBoundDataProvider(binder, (JTable) component,
         dataProviderMetadata);
   }

   public class JTableComponentBoundDataProvider extends AbstractBoundMember
         implements BoundDataProvider {
      private final JTable component;
      private final DataProviderMetadata dataProviderMetadata;
      private final ListSelectionListener listener;
      private final TableCellRenderer renderer;

      public JTableComponentBoundDataProvider(SwingBinder binder,
            JTable component, DataProviderMetadata dataProviderMetadata) {
         super(binder, component);
         this.component = component;
         this.dataProviderMetadata = dataProviderMetadata;

         this.component.getSelectionModel().addListSelectionListener(
               listener = createListSelectionListener());

         this.renderer = createCellRenderer();

         configureTableCellRenderer();
      }

      protected JTable getComponent() {
         return component;
      }

      protected DataProviderMetadata getDataProviderMetadata() {
         return dataProviderMetadata;
      }

      protected ListSelectionListener getListener() {
         return listener;
      }

      protected TableCellRenderer createCellRenderer() {
         return new FormatterTableCellRenderer();
      }

      protected void configureTableCellRenderer() {
         Enumeration en = component.getColumnModel().getColumns();

         while (en.hasMoreElements()) {
            TableColumn column = (TableColumn) en.nextElement();

            if (column.getCellRenderer() == null) {
               column.setCellRenderer(renderer);
            }
         }
      }

      protected ListSelectionListener createListSelectionListener() {
         return new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
               if (event.getValueIsAdjusting()) {
                  return;
               }

               getBinder().updateFormSelection(getDataProviderMetadata(),
                     getIndexes());
            }
         };
      }

      protected int[] getIndexes() {
         int[] indexes = component.getSelectedRows();

         if (!needsConversion()) {
            return indexes;
         }

         try {
            for (int i = 0; i < indexes.length; i++) {
               indexes[i] = ((Integer)convertRowIndexToModel.invoke(component, 
                     new Object[] {new Integer(indexes[i])})).intValue();
            }
         } catch (IllegalArgumentException iae) {
            throw new RuntimeException(iae);
         } catch (InvocationTargetException ite) {
            throw new RuntimeException(ite.getCause());
         } catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
         }

         return indexes;
      }

      public void updateIndexes(int[] indexes) {
         getViewIndexes(indexes);

         if (Arrays.equals(getIndexes(), indexes)) {
            return;
         }

         deactivateSelectionListener();

         try {
            ListSelectionModel sm = component.getSelectionModel();
            sm.clearSelection();
   
            for (int i = 0; i < indexes.length; i++) {
               if (indexes[i] == -1) {
                  continue;
               }

               sm.addSelectionInterval(indexes[i], indexes[i]);
            }
         } finally {
            reactivateSelectionListener();
         }
      }

      protected void getViewIndexes(final int[] indexes) {
         if (!needsConversion()) {
            return;
         }

         try {
            for (int i = 0; i < indexes.length; i++) {
               indexes[i] = ((Integer)convertRowIndexToView.invoke(component, 
                     new Object[] {new Integer(indexes[i])})).intValue();
            }
         } catch (IllegalArgumentException iae) {
            throw new RuntimeException(iae);
         } catch (InvocationTargetException ite) {
            throw new RuntimeException(ite.getCause());
         } catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
         }
      }

      public void updateList(List data) throws Exception {
         int[] selected = dataProviderMetadata.isResetSelection() ? new int[0]
               : getIndexes();

         createTableModelAdapter().setData(data);
         setSelectedIndexes(data.size(), selected);
      }

      protected void setSelectedIndexes(int listSize, int[] indexes) {
         getViewIndexes(indexes);
         deactivateSelectionListener();
         
         try {
            component.clearSelection();
            for (int i = 0; i < indexes.length; i++) {
               if (indexes[i] >= listSize) {
                  continue;
               }
   
               component.getSelectionModel().addSelectionInterval(indexes[i],
                     indexes[i]);
            }
         } finally {
            reactivateSelectionListener();
         }
      }

      protected void deactivateSelectionListener() {
         if (listener != null) {
            component.getSelectionModel().removeListSelectionListener(listener);
         }
      }

      protected void reactivateSelectionListener() {
         if (listener != null) {
            component.getSelectionModel().addListSelectionListener(listener);
         }
      }

      protected TableModelAdapter createTableModelAdapter() {
         return new TableModelAdapter() {
            public void setData(List data) throws Exception {
               DefaultTableModel model = (DefaultTableModel) component
                     .getModel();

               int dataSize = data.size();
               if (model.getRowCount() != data.size()) {
                  model.setRowCount(dataSize);
               }

               int columnCount = component.getColumnModel().getColumnCount();
               if (model.getColumnCount() != columnCount) {
                  model.setColumnCount(columnCount);
               }

               int i = 0;
               TableColumn column;
               String propertyName;
               for (Iterator iter = data.iterator(); iter.hasNext(); i++) {
                  Object bean = iter.next();

                  for (int j = 0; j < model.getColumnCount(); j++) {
                     column = component.getColumnModel().getColumn(j);
                     int modelIndex = column.getModelIndex();

                     Object value = getBinder().isVirtual(
                           propertyName = getIdentifier(column)) ? bean
                           : getProperty(bean, propertyName);
                     model.setValueAt(value, i, modelIndex);
                  }
               }
            }
         };
      }

      protected String getIdentifier(TableColumn column) {
         String identifier = null;
         String[] names = (String[]) component.getClientProperty(
               SwingBinder.COLUMN_NAMES);

         if (names != null && names.length > column.getModelIndex()) {
            identifier = names[column.getModelIndex()];

            if (identifier != null) {
               return identifier;
            }
         }

         identifier = (String) column.getIdentifier();
         if (identifier == null) {
            throw new IllegalArgumentException("Column number "
                  + column.getModelIndex() + " from Table "
                  + getBinder().getName(getComponent())
                  + " does not have an identifier");
         }

         return identifier;
      }

      public void unbind() {
         if (listener != null) {
            component.getSelectionModel().removeListSelectionListener(listener);
         }

         if (renderer != null) {
            Enumeration en = component.getColumnModel().getColumns();

            while (en.hasMoreElements()) {
               TableColumn column = (TableColumn) en.nextElement();

               if (column.getCellRenderer() == renderer) {
                  column.setCellRenderer(null);
               }
            }
         }
      }
   }
}