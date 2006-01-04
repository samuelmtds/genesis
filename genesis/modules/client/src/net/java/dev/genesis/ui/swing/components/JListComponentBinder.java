/*
 * The Genesis Project
 * Copyright (C) 2005  Summa Technologies do Brasil Ltda.
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
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.swing.SwingBinder;
import net.java.dev.reusablecomponents.lang.Enum;

import org.apache.commons.beanutils.PropertyUtils;

import java.awt.Component;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class JListComponentBinder extends AbstractComponentBinder {
   private final boolean useListModelAdapter;

   public JListComponentBinder() {
      this(false);
   }

   public JListComponentBinder(boolean useListModelAdapter) {
      this.useListModelAdapter = useListModelAdapter;
   }

   public BoundDataProvider bind(SwingBinder binder, Component component,
      DataProviderMetadata dataProviderMetadata) {
      return new JListBoundMember(binder, (JList) component,
         dataProviderMetadata);
   }

   public class JListBoundMember extends AbstractBoundMember
         implements BoundField, BoundDataProvider {
      private final JList component;
      private final DataProviderMetadata dataProviderMetadata;
      private final ListSelectionListener listener;

      public JListBoundMember(SwingBinder binder, JList component,
         DataProviderMetadata dataProviderMetadata) {
         super(binder, component);
         this.component = component;
         this.dataProviderMetadata = dataProviderMetadata;

         this.component.addListSelectionListener(listener = createListSelectionListener());
      }

      protected JList getComponent() {
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
                  try {
                     int[] selected =
                        getBinder()
                              .getIndexesFromUI(component.getSelectedIndices(),
                              isBlank(component));

                     getBinder().getFormController()
                           .updateSelection(dataProviderMetadata, selected);
                  } catch (Exception e) {
                     getBinder().handleException(e);
                  }
               }
            };
      }

      public void updateIndexes(int[] indexes) {
         deactivateListeners();

         try {
            component.setSelectedIndices(getBinder()
                                               .getIndexesFromController(indexes,
                     isBlank(component)));
         } finally {
            reactivateListeners();
         }
      }

      public void updateList(List list) throws Exception {
         deactivateListeners();

         try {
            if (useListModelAdapter) {
               createListModelAdapter().setData(getData(list));
            } else {
               component.setListData(getData(list));
            }
         } finally {
            reactivateListeners();
         }
      }

      public void setValue(Object value) throws Exception {
         if ((dataProviderMetadata.getObjectField() == null) ||
               (component.getSelectionMode() != ListSelectionModel.SINGLE_SELECTION)) {
            return;
         }

         boolean isBlank = isBlank(component);

         int found = -1;

         if (value != null) {
            String selectedKey = getKey(value);

            int size = component.getModel().getSize();

            for (int i = isBlank ? 1 : 0; i < size; i++) {
               Object o = component.getModel().getElementAt(i);

               if (selectedKey.equals(getKey(o))) {
                  found = i;

                  break;
               }
            }
         } else if (isBlank) {
            found++;
         }

         deactivateListeners();

         try {
            component.setSelectedIndex(found);
         } finally {
            reactivateListeners();
         }
      }

      public void unbind() {
         if (listener != null) {
            component.removeListSelectionListener(listener);
         }
      }

      protected Object[] getData(List list) throws Exception {
         boolean isBlank = isBlank(component);
         Object[] values = new Object[isBlank ? (list.size() + 1) : list.size()];

         int i = 0;

         if (isBlank) {
            String blankLabel = (String) component
                  .getClientProperty(SwingBinder.BLANKLABEL_PROPERTY);
            values[i] = (blankLabel == null) ? "" : blankLabel;
            i++;
         }

         for (Iterator iter = list.iterator(); iter.hasNext(); i++) {
            values[i] = iter.next();
         }

         return values;
      }

      protected ListModelAdapter createListModelAdapter() {
         return new ListModelAdapter() {
               public void setData(Object[] data) {
                  DefaultListModel model =
                     (DefaultListModel) component.getModel();
                  model.clear();

                  for (int i = 0; i < data.length; i++) {
                     model.addElement(data[i]);
                  }
               }
            };
      }

      protected String getKey(Object value) throws Exception {
         String keyPropertyName = (String) component
               .getClientProperty(SwingBinder.KEY_PROPERTY);

         if (keyPropertyName != null) {
            Object o =
               (value == null) ? null
               : PropertyUtils.getProperty(value, keyPropertyName);

            return getBinder().format(keyPropertyName, o);
         } else if (value instanceof Enum) {
            return value.toString();
         }

         return getBinder()
                      .format(dataProviderMetadata.getObjectField()
                                                     .getFieldName(), value);
      }

      protected void deactivateListeners() {
         if (listener != null) {
            component.removeListSelectionListener(listener);
         }
      }

      protected void reactivateListeners() {
         if (listener != null) {
            component.addListSelectionListener(listener);
         }
      }
   }
}
