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

import net.java.dev.genesis.helpers.EnumHelper;
import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.swt.SwtBinder;
import net.java.dev.genesis.ui.thinlet.PropertyMisconfigurationException;

import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Widget;

public class ListWidgetBinder extends AbstractWidgetBinder {
   public BoundDataProvider bind(SwtBinder binder, Widget widget,
         DataProviderMetadata dataProviderMetadata) {
      return new ListBoundMember(binder, (org.eclipse.swt.widgets.List) widget,
            dataProviderMetadata);
   }

   public class ListBoundMember extends AbstractBoundMember implements
         BoundField, BoundDataProvider {
      private final org.eclipse.swt.widgets.List widget;
      private final DataProviderMetadata dataProviderMetadata;
      private final SelectionListener listener;

      public ListBoundMember(SwtBinder binder,
            org.eclipse.swt.widgets.List widget,
            DataProviderMetadata dataProviderMetadata) {
         super(binder, widget);
         this.widget = widget;
         this.dataProviderMetadata = dataProviderMetadata;

         this.widget.addSelectionListener(listener = createSelectionListener());
      }

      protected org.eclipse.swt.widgets.List getWidget() {
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
         return getBinder().getIndexesFromUI(widget.getSelectionIndices(),
               isBlank(widget));
      }

      public void updateIndexes(int[] indexes) {
         indexes = getBinder().getIndexesFromController(indexes,
               isBlank(widget));
         widget.deselectAll();
         if (indexes != null && indexes.length > 0
               && (indexes.length != 1 || indexes[0] > -1)) {
            widget.setSelection(indexes);
         }
      }

      public void updateList(List list) throws Exception {
         widget.setItems(getData(list));
      }

      public void setValue(Object value) throws Exception {
         if (dataProviderMetadata.getObjectField() == null
               || (widget.getStyle() & SWT.SINGLE) != 0) {
            return;
         }

         boolean isBlank = isBlank(widget);

         int found = -1;

         if (value != null) {
            String selectedKey = getKey(value);

            int size = widget.getItemCount();

            for (int i = isBlank ? 1 : 0; i < size; i++) {
               if (selectedKey.equals(getKey(i))) {
                  found = i;

                  break;
               }
            }
         } else if (isBlank) {
            found++;
         }

         if (found < 0) {
            widget.deselectAll();
         } else {
            widget.select(found);
         }
      }

      public void unbind() {
         if (listener != null) {
            widget.removeSelectionListener(listener);
         }
      }

      protected String[] getData(List list) throws Exception {
         boolean isBlank = isBlank(widget);
         String[] values = new String[isBlank ? (list.size() + 1) : list.size()];

         int i = 0;

         if (isBlank) {
            String blankLabel = (String) widget
                  .getData(SwtBinder.BLANK_LABEL_PROPERTY);
            values[i] = (blankLabel == null) ? "" : blankLabel;
            i++;
         }

         for (Iterator iter = list.iterator(); iter.hasNext(); i++) {
            Object value = iter.next();
            values[i] = getValue(widget, value);
            setKey(i, getKey(value));
         }

         return values;
      }

      protected void setKey(int index, String key) throws Exception {
         widget.setData(SwtBinder.KEY_PROPERTY + '-' + index, key);
      }

      protected String getKey(int index) throws Exception {
         return (String) widget.getData(SwtBinder.KEY_PROPERTY + '-' + index);
      }

      protected String getValue(Widget widget, Object value) throws Exception {
         String valueProperty = (String) widget
               .getData(SwtBinder.VALUE_PROPERTY);

         if (value == null) {
            String blankLabel = (String) widget
                  .getData(SwtBinder.BLANK_LABEL_PROPERTY);
            return (blankLabel == null) ? "" : blankLabel;
         } else if (value instanceof String) {
            return (String) value;
         } else if (valueProperty == null) {
            return getBinder().format(getName(), null, value, getBinder().isVirtual(widget));
         }

         boolean isVirtual = getBinder().isVirtual(widget, valueProperty);

         return getBinder().format(getName(), valueProperty, isVirtual ?
               value : PropertyUtils.getProperty(value, valueProperty), isVirtual);
      }

      protected String getKey(Object value) throws Exception {
         String keyPropertyName = (String) widget
               .getData(SwtBinder.KEY_PROPERTY);

         if (keyPropertyName != null) {
            Object o = (value == null) ? null : PropertyUtils.getProperty(
                  value, keyPropertyName);

            return getBinder().format(getName(), keyPropertyName, o);
         } else if (EnumHelper.getInstance().isEnum(value)) {
            return value.toString();
         }

         throw new PropertyMisconfigurationException("Property 'key' "
               + "must be configured for the widget named " + getName());
      }
   }
}
