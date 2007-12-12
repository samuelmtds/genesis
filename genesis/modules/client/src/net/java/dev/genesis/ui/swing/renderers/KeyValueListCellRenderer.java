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
package net.java.dev.genesis.ui.swing.renderers;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;

import net.java.dev.genesis.text.FormatterRegistry;
import net.java.dev.genesis.ui.swing.SwingBinder;
import net.java.dev.genesis.ui.swing.components.ComponentBinderHelper;

public class KeyValueListCellRenderer extends DefaultListCellRenderer {
   private final JComponent component;

   public KeyValueListCellRenderer(JComponent component) {
      this.component = component;
   }

   protected SwingBinder getBinder() {
      return (SwingBinder) component.getClientProperty(SwingBinder.BINDER_KEY);
   }

   protected String format(Object value) {
      final SwingBinder binder = getBinder();

      if (binder == null) {
         return FormatterRegistry.getInstance().format(value);
      }

      return ComponentBinderHelper.format(binder, component, value);
   }

   public Component getListCellRendererComponent(JList list, Object value,
         int index, boolean isSelected, boolean cellHasFocus) {
      return super.getListCellRendererComponent(list, format(value), index,
            isSelected, cellHasFocus);
   }
}
