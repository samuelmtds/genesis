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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import net.java.dev.genesis.helpers.EnumHelper;
import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.swing.SwingBinder;

public class JComboBoxComponentBinder extends AbstractComponentBinder {
   public BoundDataProvider bind(SwingBinder binder, Component component,
      DataProviderMetadata dataProviderMetadata) {
      return new JComboBoxBoundMember(binder, (JComboBox) component,
         dataProviderMetadata);
   }

   public class JComboBoxBoundMember extends AbstractBoundMember
         implements BoundField, BoundDataProvider {
      private final JComboBox component;
      private final DataProviderMetadata dataProviderMetadata;
      private final ActionListener listener;

      public JComboBoxBoundMember(SwingBinder binder, JComboBox component,
         DataProviderMetadata dataProviderMetadata) {
         super(binder, component);
         this.component = component;
         this.dataProviderMetadata = dataProviderMetadata;

         this.component.addActionListener(listener = createActionListener());
      }

      protected JComboBox getComponent() {
         return component;
      }

      protected DataProviderMetadata getDataProviderMetadata() {
         return dataProviderMetadata;
      }

      protected ActionListener getListener() {
         return listener;
      }

      protected ActionListener createActionListener() {
         return new ActionListener() {
            public void actionPerformed(ActionEvent event) {
               getBinder().updateFormSelection(getDataProviderMetadata(), getIndexes());
            }
         };
      }

      protected int[] getIndexes() {
         int selectedIndex = component.getSelectedIndex();
         return getBinder().getIndexesFromUI(
               (selectedIndex < 0) ? new int[0] : new int[] { selectedIndex },
               isBlank(component));
      }

      public void updateIndexes(int[] indexes) {
         indexes = getBinder()
                         .getIndexesFromController(indexes, isBlank(component));

         if (indexes.length != 1) {
            throw new IllegalArgumentException("Length of selected indexes must be one");
         }

         if (component.getSelectedIndex() == indexes[0]) {
            return;
         }

         deactivateListeners();
         try {
            component.setSelectedIndex(indexes[0]);
         } finally {
            reactivateListeners();
         }
      }

      public void updateList(List list) throws Exception {
         deactivateListeners();
         try {
            boolean isBlank = isBlank(component);

            int selected = component.getSelectedIndex();
            if (dataProviderMetadata.isResetSelection()) {
               selected = isBlank ? 0 : -1;
            } else {
               selected = selected <= list.size() ? selected : isBlank ? 0 : -1;
            }

            createComboBoxModelAdapter().setData(getData(list));

            component.setSelectedIndex(selected);
         } finally {
            reactivateListeners();
         }
      }

      public String getValue() throws Exception {
         if (dataProviderMetadata.getObjectField() == null) {
            return null;
         }

         if (isBlank(component) && component.getSelectedIndex() == 0) {
            return null;
         }

         return getKey(component.getSelectedItem());
      }

      public void setValue(Object value) throws Exception {
         if (dataProviderMetadata.getObjectField() == null) {
            return;
         }

         boolean isBlank = isBlank(component);

         int found = -1;

         if (value != null) {
            String selectedKey = getKey(value);

            int size = component.getItemCount();

            for (int i = isBlank ? 1 : 0; i < size; i++) {
               Object o = component.getItemAt(i);

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

      protected ComboBoxModelAdapter createComboBoxModelAdapter() {
         return new ComboBoxModelAdapter() {
            public void setData(Object[] data) {
               DefaultComboBoxModel model = (DefaultComboBoxModel) component
                     .getModel();

               model.removeAllElements();

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

         return String.valueOf(System.identityHashCode(value));
      }

      protected void deactivateListeners() {
         if (listener != null) {
            component.removeActionListener(listener);
         }
      }

      protected void reactivateListeners() {
         if (listener != null) {
            component.addActionListener(listener);
         }
      }

      public void unbind() {
         if (listener != null) {
            component.removeActionListener(listener);
         }
      }
   }
}
