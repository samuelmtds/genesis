/*
 * The Genesis Project
 * Copyright (C) 2006 Summa Technologies do Brasil Ltda.
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

import javax.swing.JPanel;
import javax.swing.JTextField;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.ui.swing.MockSwingBinder;
import net.java.dev.genesis.ui.swing.SwingBinder;
import net.java.dev.genesis.ui.swing.components.AbstractComponentBinder.AbstractBoundMember;

public class AbstractComponentBinderTest extends GenesisTestCase {
   private JTextField comp1;
   private JTextField comp2;
   private MockSwingBinder binder;
   private AbstractBoundMember boundMember;

   public AbstractComponentBinderTest() {
      super("Abstract Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      comp1 = new JTextField();
      comp2 = new JTextField();
      binder = new MockSwingBinder(new JPanel(), new MockForm());
      comp1.putClientProperty(SwingBinder.ENABLED_GROUP_PROPERTY, comp2);
      comp1.putClientProperty(SwingBinder.VISIBLE_GROUP_PROPERTY, comp2);
      boundMember = new AbstractComponentBinder() {
      }.new AbstractBoundMember(binder, comp1) {
      };
   }

   public void testEnabled() {
      assertTrue(comp1.isEnabled());
      assertTrue(comp2.isEnabled());

      boundMember.setEnabled(false);

      assertFalse(comp1.isEnabled());
      assertFalse(comp2.isEnabled());
   }

   public void testVisible() {
      assertTrue(comp1.isVisible());
      assertTrue(comp2.isVisible());

      boundMember.setVisible(false);

      assertFalse(comp1.isVisible());
      assertFalse(comp2.isVisible());
   }
}
