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
import javax.swing.JPanel;

import net.java.dev.genesis.GenesisTestCase;

public class SwingBinderTest extends GenesisTestCase {
   public SwingBinderTest() {
      super("Swing Binder Unit Test");
   }
   
   public void testBindAndUnbindDefaultButtonNPE() {
      SwingBinder binder = new SwingBinder(new JPanel(), new Object());
      try {
         binder.bind();
         binder.unbindDefaultButton();
         binder.bindDefaultButton(null);
         binder.unbindDefaultButton(null);
         binder.bindDefaultButton();
         binder.unbind();
         
         JFrame frame = new JFrame();
         final JButton defaultButton = new JButton();
         binder = new SwingBinder(frame, new Object());
         binder.bind();
         
         frame.getRootPane().setDefaultButton(defaultButton);
         frame.getRootPane().setDefaultButton(null);
      } catch(NullPointerException ex) {
         fail("NullPointerException should not be thrown for null default button.");
      }
   }
   
   public void testBindDefaultButton() {
      JFrame frame = new JFrame();
      final JButton defaultButton = new JButton();
      
      SwingBinder binder = new SwingBinder(frame, new Object());
      frame.getRootPane().setDefaultButton(defaultButton);
      
      binder.bind();
      assertSame(defaultButton, binder.getDefaultButton());
      
      frame.getRootPane().setDefaultButton(new JButton());
      assertNotSame(defaultButton, binder.getDefaultButton());
   }
   
   public void testIfDefaultButtonCreatesListener() {
      JFrame frame = new JFrame();
      final JButton defaultButton = new JButton();
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

      binder.unbindDefaultButton();
      assertEquals(length - 1, defaultButton.getActionListeners().length);

      binder.bindDefaultButton();
      assertEquals(listeners.length, length);
      
      final JButton newDefaultButton = new JButton();
      frame.getRootPane().setDefaultButton(newDefaultButton);
      assertEquals(length - 1, defaultButton.getActionListeners().length);
      assertEquals(length, newDefaultButton.getActionListeners().length);
      
      binder.unbindDefaultButton();
      assertEquals(length - 1, newDefaultButton.getActionListeners().length);
      
      binder.bindDefaultButton();
      assertEquals(length - 1, defaultButton.getActionListeners().length);
      assertEquals(length, newDefaultButton.getActionListeners().length);
      
      frame.getRootPane().setDefaultButton(null);
      assertEquals(length - 1, newDefaultButton.getActionListeners().length);
   }
   
   public void testDefaultButtonNPE() {
      SwingBinder binder = new SwingBinder(new JPanel(), new Object());

      try {
         binder.bind();
      } catch (NullPointerException ex) {
         fail("NullPointerException should not be thrown for null RootPane.");
      }
   }
   
   public void testDefaultButton() {
      SwingBinder binder = new SwingBinder(new JPanel(), new Object());
      binder.bind();
      assertNull(binder.getDefaultButton());
      
      JFrame frame = new JFrame();
      JButton defaultButton = new JButton();
      
      binder = new SwingBinder(frame, new Object());
      binder.bind();
      assertNull(binder.getDefaultButton());
      
      frame.getRootPane().setDefaultButton(defaultButton);
      binder.bindDefaultButton();
      assertNotNull(binder.getDefaultButton());
      assertSame(defaultButton, binder.getDefaultButton());
   }
   
   public void testHasDefaultButton() {
      SwingBinder binder = new SwingBinder(new JPanel(), new Object());
      assertFalse(binder.hasDefaultButton());
      
      JFrame frame = new JFrame();
      JButton defaultButton = new JButton();
      
      binder = new SwingBinder(frame, new Object());
      binder.bind();
      assertFalse(binder.hasDefaultButton());
      
      frame.getRootPane().setDefaultButton(defaultButton);
      binder.bindDefaultButton();
      assertTrue(binder.hasDefaultButton());
   }
}
