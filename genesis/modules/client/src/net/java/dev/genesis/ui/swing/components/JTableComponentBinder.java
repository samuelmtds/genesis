/*
 * The Genesis Project
 * Copyright (C) 2005-2006  Summa Technologies do Brasil Ltda.
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

import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.swing.SwingBinder;

import org.apache.commons.beanutils.PropertyUtils;

import java.awt.Component;

import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;


public class JTableComponentBinder extends AbstractComponentBinder {
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

      public JTableComponentBoundDataProvider(SwingBinder binder,
         JTable component, DataProviderMetadata dataProviderMetadata) {
         super(binder, component);
         this.component = component;
         this.dataProviderMetadata = dataProviderMetadata;

         this.component.getSelectionModel()
                             .addListSelectionListener(listener = createListSelectionListener());
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

      protected ListSelectionListener createListSelectionListener() {
         return new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
               getBinder().updateFormSelection(getDataProviderMetadata(),
                     getIndexes());
            }
         };
      }

      protected int[] getIndexes() {
         return component.getSelectedRows();
      }

      public void updateIndexes(int[] indexes) {
         ListSelectionModel sm = component.getSelectionModel();
         sm.clearSelection();

         for (int i = 0; i < indexes.length; i++) {
            sm.addSelectionInterval(indexes[i], indexes[i]);
         }
      }

      public void updateList(List data) throws Exception {
         createTableModelAdapter().setData(data);
      }

      protected TableModelAdapter createTableModelAdapter() {
         return new TableModelAdapter() {
            public void setData(List data) throws Exception {
               DefaultTableModel model = (DefaultTableModel) component
                     .getModel();

               String[] valueProperties = new String[component.getColumnModel()
                     .getColumnCount()];

               model.setRowCount(data.size());
               model.setColumnCount(valueProperties.length);

               for (int i = 0; i < valueProperties.length; i++) {
                  valueProperties[i] = (String) component.getColumnModel()
                        .getColumn(i).getIdentifier();

                  if (valueProperties[i] == null) {
                     throw new IllegalArgumentException("Column number "
                           + i
                           + " from Table "
                           + getBinder().getLookupStrategy().getName(
                                 getComponent())
                           + " does not have an identifier");
                  }
               }

               int i = 0;

               for (Iterator iter = data.iterator(); iter.hasNext(); i++) {
                  Object bean = iter.next();

                  for (int j = 0; j < valueProperties.length; j++) {
                     Object value = PropertyUtils.getProperty(bean,
                           valueProperties[j]);
                     model.setValueAt(value, i, j);
                  }
               }
            }
         };
      }

      public void unbind() {
         if (listener != null) {
            component.getSelectionModel().removeListSelectionListener(listener);
         }
      }
   }
}
