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
package net.java.dev.genesis.ui.swt.widgets;

import java.util.Iterator;
import java.util.List;

import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.swt.SWTBinder;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

public class TableWidgetBinder extends AbstractWidgetBinder {
   public BoundDataProvider bind(SWTBinder binder, Widget widget,
      DataProviderMetadata dataProviderMetadata) {
      return new TableWidgetBoundDataProvider(binder, (Table) widget,
         dataProviderMetadata);
   }

   public class TableWidgetBoundDataProvider extends AbstractBoundMember
         implements BoundDataProvider {
      private final Table widget;
      private final DataProviderMetadata dataProviderMetadata;
      private final SelectionListener listener;

      public TableWidgetBoundDataProvider(SWTBinder binder,
         Table widget, DataProviderMetadata dataProviderMetadata) {
         super(binder, widget);
         this.widget = widget;
         this.dataProviderMetadata = dataProviderMetadata;

         this.widget.addSelectionListener(listener = createSelectionListener());
      }

      protected Table getWidget() {
         return widget;
      }

      protected DataProviderMetadata getDataProviderMetadata() {
         return dataProviderMetadata;
      }

      protected SelectionListener getListener() {
         return listener;
      }

      protected SelectionListener createSelectionListener() {
         return new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
               getBinder().updateFormSelection(getDataProviderMetadata(),
                     getIndexes());
            }
         };
      }

      protected int[] getIndexes() {
         return widget.getSelectionIndices();
      }

      public void updateIndexes(int[] indexes) {
         widget.deselectAll();

         if (indexes != null && indexes.length > 0
               && (indexes.length != 1 || indexes[0] > 0)) {
            widget.select(indexes);
         }
      }

      public void updateList(List data) throws Exception {
         widget.deselectAll();
         widget.setItemCount(data.size());

         TableItem[] items = widget.getItems();
         int i = 0;
         for (Iterator iter = data.iterator(); iter.hasNext(); i++) {
            Object bean = iter.next();

            for (int j = 0; j < widget.getColumnCount(); j++) {
               int modelIndex = getModelIndex(j);
               String identifier = getIdentifier(modelIndex);
               boolean isVirtual = getBinder().isVirtual(identifier);

               Object value = isVirtual ? bean : getProperty(
                     bean, identifier);
               items[i].setText(modelIndex, getBinder().format(getName(),
                     identifier, value, isVirtual));
            }
         }
      }
      
      protected int getModelIndex(int index) {
         int[] order = widget.getColumnOrder();
         for (int i = 0; i < order.length; i++) {
            if (order[i] == index) {
               return i;
            }
         }

         throw new IllegalArgumentException("Cannot retrieve model index (" + index + ")");
      }

      protected String getIdentifier(int modelIndex) {
         String identifier = null;
         String[] names = (String[]) widget.getData(
               SWTBinder.COLUMN_NAMES);

         if (names != null) {
            identifier = names[modelIndex];

            if (identifier != null) {
               return identifier;
            }
         }

         identifier = (String) widget.getColumn(modelIndex).getData(
               SWTBinder.TABLE_COLUMN_IDENTIFIER);
         if (identifier == null) {
            throw new IllegalArgumentException("Column number " + modelIndex
                  + " from Table " + getName() + " does not have an identifier");
         }

         return identifier;
      }

      public void unbind() {
         if (listener != null) {
            widget.removeSelectionListener(listener);
         }
      }
   }
}
