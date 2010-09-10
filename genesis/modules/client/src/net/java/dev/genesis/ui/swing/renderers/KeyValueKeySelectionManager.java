/*
 * The Genesis Project
 * Copyright (C) 2010  Summa Technologies do Brasil Ltda.
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

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComboBox.KeySelectionManager;
import javax.swing.UIManager;
import net.java.dev.genesis.ui.swing.SwingBinder;
import net.java.dev.genesis.ui.swing.components.ComponentBinderHelper;

public class KeyValueKeySelectionManager implements KeySelectionManager {
   private final JComboBox combo;
   private final long delay;
   private long lastTime = -1;
   private String prefix;

   public KeyValueKeySelectionManager(JComboBox combo) {
      this(combo, getDefaultDelay());
   }

   public KeyValueKeySelectionManager(JComboBox combo, long delay) {
      this.combo = combo;
      this.delay = delay;
   }
   
   private static long getDefaultDelay() {
      Long defaultDelay = (Long)UIManager.get("ComboBox.timeFactor");
      return defaultDelay == null ? 1000 : defaultDelay.longValue();
   }

   public int selectionForKey(char key, ComboBoxModel model) {
      final long currentTime = System.currentTimeMillis();
      final SwingBinder binder = (SwingBinder) combo.getClientProperty(
            SwingBinder.BINDER_KEY);
      final int size = model.getSize();
      final String[] formatted = new String[size];
      final Object selectedItem = model.getSelectedItem();
      int selectedIndex = -1;
      
      for (int i = 0; i < size; i++) {
         Object element = model.getElementAt(i);
         
         if (selectedItem == element && selectedIndex == -1) {
            selectedIndex = i;
         }

         formatted[i] = ComponentBinderHelper.format(binder, combo, element);
      }
      
      key = Character.toLowerCase(key);
      int start = selectedIndex;

      if (prefix != null && prefix.length() == 1 && key == prefix.
            charAt(0)) {
         start++;
      } else if (lastTime == -1 || delay == 0 || currentTime - lastTime > delay) {
         prefix = String.valueOf(key);
         
         if (selectedIndex != -1 && matches(formatted[selectedIndex])) {
            start++;
         }
      } else {
         prefix += key;
      }

      lastTime = currentTime;

      for (int i = Math.max(start, 0); i < size; i++) {
         if (matches(formatted[i])) {
            return i;
         }
      }

      for (int i = 0; i <= start; i++) {
         if (matches(formatted[i])) {
            return i;
         }
      }
      
      return -1;
   }

   private boolean matches(final String s) {
      return s.regionMatches(true, 0, prefix, 0, prefix.length());
   }
}
