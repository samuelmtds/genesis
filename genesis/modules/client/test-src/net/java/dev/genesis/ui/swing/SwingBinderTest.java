/*
 * The Genesis Project
 * Copyright (C) 2006-2007 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.swing;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import net.java.dev.genesis.GenesisTestCase;

public class SwingBinderTest extends GenesisTestCase {
   public SwingBinderTest() {
      super("Swing Binder Unit Test");
   }
   
   public void testDefaultButtonListener() {
      JFrame frame = new JFrame();
      JButton defaultButton = new JButton();
      final ActionListener[] listeners = new ActionListener[1];
      
      SwingBinder binder = new SwingBinder(frame, new Object()) {
         protected ActionListener createDefautButtonListener() {
            listeners[0] = super.createDefautButtonListener();

            return listeners[0];
         }
      };
      frame.getRootPane().setDefaultButton(defaultButton);

      binder.bind();
      int length = defaultButton.getActionListeners().length;
      
      assertEquals(listeners.length, length);

      defaultButton.removeActionListener(listeners[0]);

      assertEquals(length - 1, defaultButton.getActionListeners().length);
   }
}
