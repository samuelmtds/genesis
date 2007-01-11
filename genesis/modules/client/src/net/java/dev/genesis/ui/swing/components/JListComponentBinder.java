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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.java.dev.genesis.helpers.EnumHelper;
import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.PropertyMisconfigurationException;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.swing.SwingBinder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JListComponentBinder extends AbstractComponentBinder {
   private static final Log log = LogFactory.getLog(JListComponentBinder.class);
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
               if (event.getValueIsAdjusting()) {
                  return;
               }
               
               getBinder().updateFormSelection(getDataProviderMetadata(), getIndexes());
            }
         };
      }

      protected int[] getIndexes() {
         return getBinder().getIndexesFromUI(component.getSelectedIndices(),
               isBlank(component));
      }

      public void updateIndexes(int[] indexes) {
         if (component.getSelectionMode() == ListSelectionModel.SINGLE_SELECTION
               && indexes.length > 1) {
            StringBuffer sb = new StringBuffer('[');
            
            for (int i = 0; i < indexes.length; i++) {
               sb.append(indexes[i]).append(", ");
            }

            sb.append(']');

            throw new IllegalArgumentException("Component " + 
                  getBinder().getName(component) + " is a single selection " +
                  "list. It can't be updated with indexes " + sb.toString());
         }
         
         if (Arrays.equals(getIndexes(), indexes)) {
            return;
         }

         boolean isBlank = isBlank(component);
         indexes = getBinder().getIndexesFromController(indexes, isBlank);

         deactivateListeners();

         try {
            setSelectedIndexes(isBlank, indexes);
         } finally {
            reactivateListeners();
         }
      }

      protected void setSelectedIndexes(boolean isBlank, int[] indexes) {
         if (indexes.length == 0 || (isBlank && indexes.length == 1 && indexes[0] < 0)) {
            component.clearSelection();
         } else {
            component.setSelectedIndices(indexes);
         }
      }
      
      public void updateList(List list) throws Exception {
         deactivateListeners();

         try {
            boolean isBlank = isBlank(component);

            int[] selected = component.getSelectedIndices();
            if (dataProviderMetadata.isResetSelection()) {
               selected = isBlank ? new int[] { 0 } : new int[] { -1 };
            } else {
               int maxSelectionSize = isBlank ? list.size() + 1 : list.size();
               int j = 0;
               for (int i = 0; i < selected.length; i++) {
                  if (selected[i] < maxSelectionSize) {
                     j++;
                  }
               }

               int[] temp = new int[j];
               System.arraycopy(selected, 0, temp, 0, j);

               selected = temp;
            }
            
            if (useListModelAdapter) {
               createListModelAdapter().setData(getData(list));
            } else {
               component.setListData(getData(list));
            }

            setSelectedIndexes(isBlank, selected);
         } finally {
            reactivateListeners();
         }
      }

      public String getValue() throws Exception {
         if (dataProviderMetadata.getObjectField() == null
               || component.getSelectionMode() != ListSelectionModel.SINGLE_SELECTION) {
            return null;
         }

         if (isBlank(component) && component.getSelectedIndex() == 0) {
            return null;
         }

         return getKey(component.getSelectedValue());
      }

      public void setValue(Object value) throws Exception {
         if (dataProviderMetadata.getObjectField() == null) {
            return;
         }

         if (component.getSelectionMode() != ListSelectionModel.SINGLE_SELECTION) {
            log.warn("Cannot update " + getBinder().getName(component)
                  + " component because it's not a single selection list.");
            return;
         }

         int index = getIndexFor(value);

         deactivateListeners();

         try {
            if (index < 0) {
               component.clearSelection();
            } else {
               component.setSelectedIndex(index);   
            }
         } finally {
            reactivateListeners();
         }
      }

      protected int getIndexFor(Object value) throws Exception {
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

         return found;
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
                  .getClientProperty(SwingBinder.BLANK_LABEL_PROPERTY);
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
               DefaultListModel model = (DefaultListModel) component.getModel();
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
               : getProperty(value, keyPropertyName);

            return getBinder().format(getName(), keyPropertyName, o);
         } else if (EnumHelper.getInstance().isEnum(value)) {
            return value.toString();
         }

         throw new PropertyMisconfigurationException("Property 'key' "
               + "must be configured for the component named " + getName());
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
