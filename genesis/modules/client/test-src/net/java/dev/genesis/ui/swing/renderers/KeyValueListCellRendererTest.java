/*
 * The Genesis Project
 * Copyright (C) 2009 Summa Technologies do Brasil Ltda.
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
import net.java.dev.genesis.ui.swing.SwingBinder;
import net.java.dev.genesis.ui.swing.components.SwingUtils;

public class KeyValueListCellRendererTest extends GenesisTestCase {
   public KeyValueListCellRendererTest() {
      super("KeyValueListCellRenderer Unit Test");
   }

   public void testConstructor() {
      new KeyValueListCellRenderer(new JComboBox());
      try {
         new KeyValueListCellRenderer(null);
         fail("IllegalArgumentException must be thrown when component is null");
      } catch (IllegalArgumentException iae) {
         // expected
      }
   }

   public void testFormat() {
      KeyValueListCellRenderer renderer = new KeyValueListCellRenderer(
            new JComboBox());
      assertEquals("1", renderer.format("1"));
      assertEquals("1", renderer.format(new Integer(1)));
      assertEquals(" ", renderer.format(""));
      assertEquals(" ", renderer.format(null));
   }

   public void testFormatWithBinder() {
      JComboBox combo = combo = SwingUtils.newCombo();
      combo.setName("someDataProvider");
      JPanel panel = new JPanel();
      panel.add(combo);
      SwingBinder binder = new SwingBinder(panel, new MockForm());
      binder.bind();

      KeyValueListCellRenderer renderer = new KeyValueListCellRenderer(combo);
      assertEquals("1", renderer.format("1"));
      assertEquals("1", renderer.format(new Integer(1)));
      assertEquals(" ", renderer.format(""));
      assertEquals(" ", renderer.format(null));
   }
}
