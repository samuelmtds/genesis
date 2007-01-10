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
package net.java.dev.genesis.ui.swing.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.swing.MockSwingBinder;
import net.java.dev.genesis.ui.swing.SwingBinder;
import net.java.dev.genesis.ui.swing.components.AbstractComponentBinder.AbstractBoundMember;

public class AbstractComponentBinderTest extends GenesisTestCase {
   private MockForm form;
   private JTextField comp1;
   private JTextField comp2;
   private MockSwingBinder binder;
   private AbstractComponentBinder componentBinder;
   private AbstractBoundMember boundMember;
   private ActionMetadata actionMeta;

   public AbstractComponentBinderTest() {
      super("Abstract Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      form = new MockForm();
      comp1 = new JTextField();
      comp2 = new JTextField();

      binder = new MockSwingBinder(new JPanel(), form);
      comp1.putClientProperty(SwingBinder.ENABLED_GROUP_PROPERTY, comp2);
      comp1.putClientProperty(SwingBinder.VISIBLE_GROUP_PROPERTY, comp2);

      componentBinder = new AbstractComponentBinder() {
      };
      boundMember = componentBinder.new AbstractBoundMember(binder, comp1) {
      };
      actionMeta = (ActionMetadata) form.getFormMetadata().getActionMetadatas().get("someAction");
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

   public void testSimpleActionBinding() {
      assertNotNull(componentBinder.bind(binder, comp1, (ActionMetadata) 
            form.getFormMetadata().getActionMetadatas().get("someAction")));
      comp1.postActionEvent();
      assertSame(actionMeta, binder.get("invokeFormAction(ActionMetadata)"));
   }
   
   public void testMultipleActions() {
      assertNull(componentBinder.bind(binder, new InvalidMultipleActions(), 
            (ActionMetadata)form.getFormMetadata().getActionMetadatas()
            .get("someAction")));
      
      ValidMultipleActions valid = new ValidMultipleActions();
      assertNotNull(componentBinder.bind(binder, valid, (ActionMetadata) 
            form.getFormMetadata().getActionMetadatas().get("someAction")));

      binder.put("invokeFormAction(ActionMetadata)", null);
      valid.fireActionOneEvent();
      assertNull(binder.get("invokeFormAction(ActionMetadata)"));

      valid.fireActionEvent();
      assertSame(actionMeta, binder.get("invokeFormAction(ActionMetadata)"));
   }

   public static class InvalidMultipleActions extends JComponent {
      private final Collection actionOneListeners = new ArrayList();
      private final Collection actionTwoListeners = new ArrayList();

      public void addActionOneListener(ActionListener listener) {
         actionOneListeners.add(listener);
      }

      public void removeActionOneListener(ActionListener listener) {
         actionOneListeners.remove(listener);
      }

      public Collection getActionOneListeners() {
         return actionOneListeners;
      }

      public void fireActionOneEvent() {
         ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, 
               "actionOne");

         for (Iterator i = actionOneListeners.iterator(); i.hasNext();) {
            ((ActionListener) i.next()).actionPerformed(e);
         }
      }
      
      public void addActionTwoListener(ActionListener listener) {
         actionTwoListeners.add(listener);
      }

      public void removeActionTwoListener(ActionListener listener) {
         actionTwoListeners.remove(listener);
      }

      public Collection getActionTwoListeners() {
         return actionTwoListeners;
      }

      public void fireActionTwoEvent() {
         ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, 
               "actionTwo");

         for (Iterator i = actionTwoListeners.iterator(); i.hasNext();) {
            ((ActionListener) i.next()).actionPerformed(e);
         }
      }

   }
   
   public static class ValidMultipleActions extends JComponent {
      private final Collection actionOneListeners = new ArrayList();
      private final Collection actionListeners = new ArrayList();

      public void addActionOneListener(ActionListener listener) {
         actionOneListeners.add(listener);
      }

      public void removeActionOneListener(ActionListener listener) {
         actionOneListeners.remove(listener);
      }

      public Collection getActionOneListeners() {
         return actionOneListeners;
      }

      public void fireActionOneEvent() {
         ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, 
               "actionOne");

         for (Iterator i = actionOneListeners.iterator(); i.hasNext();) {
            ((ActionListener) i.next()).actionPerformed(e);
         }
      }
      
      public void addActionListener(ActionListener listener) {
         actionListeners.add(listener);
      }

      public void removeActionListener(ActionListener listener) {
         actionListeners.remove(listener);
      }

      public Collection getActionTwoListeners() {
         return actionListeners;
      }

      public void fireActionEvent() {
         ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, 
               "action");

         for (Iterator i = actionListeners.iterator(); i.hasNext();) {
            ((ActionListener) i.next()).actionPerformed(e);
         }
      }

   }
}
