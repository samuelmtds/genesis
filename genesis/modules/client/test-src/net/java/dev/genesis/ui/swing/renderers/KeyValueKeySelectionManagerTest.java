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

import javax.swing.JComboBox;
import javax.swing.JPanel;
import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.ui.swing.MockSwingBinder;
import net.java.dev.genesis.ui.swing.SwingBinder;
import net.java.dev.genesis.ui.swing.components.SwingUtils;

public class KeyValueKeySelectionManagerTest extends GenesisTestCase {
   private JComboBox combo;
   private MockSwingBinder binder;

   protected void setUp() throws Exception {
      combo = SwingUtils.newCombo();
      binder = new MockSwingBinder(new JPanel(), new MockForm());
      combo.putClientProperty(SwingBinder.BINDER_KEY, binder);
      combo.putClientProperty(SwingBinder.VALUE_PROPERTY, "value");
   }

   public void testSelectionForKeyWithNoDelay() {
      combo.setKeySelectionManager(new KeyValueKeySelectionManager(combo, 0));
      combo.setSelectedIndex(-1);

      assertTrue(combo.selectWithKeyChar('O'));
      assertEquals(0, combo.getSelectedIndex());

      assertTrue(combo.selectWithKeyChar('t'));
      assertEquals(1, combo.getSelectedIndex());

      assertTrue(combo.selectWithKeyChar('T'));
      assertEquals(2, combo.getSelectedIndex());

      assertTrue(combo.selectWithKeyChar('o'));
      assertEquals(0, combo.getSelectedIndex());

      assertTrue(combo.selectWithKeyChar('O'));
      assertEquals(0, combo.getSelectedIndex());

      assertFalse(combo.selectWithKeyChar('x'));
      assertEquals(0, combo.getSelectedIndex());
   }

   public void testSelectionForKeyWithEternalDelay() {
      combo.setKeySelectionManager(new KeyValueKeySelectionManager(combo, 
            Long.MAX_VALUE));
      combo.setSelectedIndex(-1);

      assertTrue(combo.selectWithKeyChar('T'));
      assertEquals(1, combo.getSelectedIndex());

      assertTrue(combo.selectWithKeyChar('t'));
      assertEquals(2, combo.getSelectedIndex());

      assertTrue(combo.selectWithKeyChar('W'));
      assertEquals(1, combo.getSelectedIndex());

      assertFalse(combo.selectWithKeyChar('x'));
      assertEquals(1, combo.getSelectedIndex());
   }

   public void testSelectionForKeyWithSomeDelay() {
      final long delay = 50;

      combo.setKeySelectionManager(new KeyValueKeySelectionManager(combo, 
            delay));
      combo.setSelectedIndex(-1);

      assertTrue(combo.selectWithKeyChar('O'));
      assertEquals(0, combo.getSelectedIndex());

      waitForDelay(delay);

      assertTrue(combo.selectWithKeyChar('t'));
      assertEquals(1, combo.getSelectedIndex());

      assertTrue(combo.selectWithKeyChar('W'));
      assertEquals(1, combo.getSelectedIndex());

      waitForDelay(delay);

      assertTrue(combo.selectWithKeyChar('t'));
      assertEquals(2, combo.getSelectedIndex());

      assertTrue(combo.selectWithKeyChar('W'));
      assertEquals(1, combo.getSelectedIndex());

      waitForDelay(delay);

      assertTrue(combo.selectWithKeyChar('t'));
      assertEquals(2, combo.getSelectedIndex());

      waitForDelay(delay);

      assertFalse(combo.selectWithKeyChar('w'));
      assertEquals(2, combo.getSelectedIndex());
   }

   private void waitForDelay(final long delay) {
      long time = System.currentTimeMillis();
      long elapsed;
      
      while ((elapsed = System.currentTimeMillis() - time) <= delay) {
         try {
            Thread.sleep(delay - elapsed + 1);
         } catch (InterruptedException ex) {
         }
      }
   }
}
