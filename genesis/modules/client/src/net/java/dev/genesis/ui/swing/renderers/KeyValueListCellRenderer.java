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
package net.java.dev.genesis.ui.swing.renderers;

import net.java.dev.genesis.ui.swing.SwingBinder;

import org.apache.commons.beanutils.PropertyUtils;

import java.awt.Component;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class KeyValueListCellRenderer extends JLabel implements ListCellRenderer,
   Serializable {
   protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
   private final SwingBinder binder;
   private final JComponent component;

   public KeyValueListCellRenderer(SwingBinder binder, JComponent component) {
      setOpaque(true);
      this.binder = binder;
      this.component = component;
   }

   public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean cellHasFocus) {
      String valueProperty = (String) component.getClientProperty("value");

      setComponentOrientation(list.getComponentOrientation());

      if (isSelected) {
         setBackground(list.getSelectionBackground());
         setForeground(list.getSelectionForeground());
      } else {
         setBackground(list.getBackground());
         setForeground(list.getForeground());
      }

      String name = binder.getLookupStrategy().getName(component);
      String text;

      if (value == null) {
         String blankLabel = (String) component.getClientProperty("blankLabel");
         text = (blankLabel == null) ? "" : blankLabel;
      } else if (value instanceof String) {
         text = (String) value;
      } else if (valueProperty == null) {
         text = binder.format(name + '.', value);
      } else {
         text = binder.format(name + '.' + valueProperty,
               getProperty(value, valueProperty));
      }

      setText(text);

      setEnabled(list.isEnabled());
      setFont(list.getFont());
      setBorder((cellHasFocus)
         ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

      return this;
   }

   private Object getProperty(Object object, String propertyName) {
      try {
         return PropertyUtils.getProperty(object, propertyName);
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
         throw new RuntimeException(e);
      } catch (NoSuchMethodException e) {
         throw new RuntimeException(e);
      }
   }
}
