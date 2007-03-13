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

import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import javax.swing.FocusManager;

import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.ui.binding.AbstractBinder;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.MockSwingBinder;

public class JTextComponentBinderTest extends GenesisTestCase {
   private class JTextField extends javax.swing.JTextField {
      public void processKeyEvent(KeyEvent e) {
         super.processKeyEvent(e);
      }
   }

   private JTextField text;
   private MockSwingBinder binder;
   private WidgetBinder componentBinder;
   private BoundField boundField;
   private MockForm form;
   private FieldMetadata fieldMeta;

   public JTextComponentBinderTest() {
      super("JText Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      text = new JTextField();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      binder.register("text", text);
      componentBinder = binder.getWidgetBinder(text);
      fieldMeta = form.getFormMetadata().getFieldMetadata("stringField");
   }

   public void testSetValue() throws Exception {
      assertTrue(componentBinder instanceof JTextComponentBinder);
      assertNull(componentBinder
            .bind(binder, text, (DataProviderMetadata) null));

      boundField = componentBinder.bind(binder, text, fieldMeta);
      assertNotNull(boundField);

      boundField.setValue("someValue");
      assertEquals("someValue", text.getText());

      boundField.setValue("  ");
      assertEquals("  ", text.getText());

      boundField.setValue(null);
      assertEquals("", text.getText());
   }

   public void testUpdateValue() throws Exception {
      assertNull(componentBinder.bind(binder, text,
            (DataProviderMetadata) null));
      assertNotNull(componentBinder.bind(binder, text, fieldMeta));

      text.setText("someValue");
      simulateFocusLost(text);
      assertEquals("someValue", binder.get("populateForm(FieldMetadata,Object)"));

      text.setText("  ");
      simulateFocusLost(text);
      assertEquals("", binder.get("populateForm(FieldMetadata,Object)"));

      text.setText(null);
      simulateFocusLost(text);
      assertEquals("", binder.get("populateForm(FieldMetadata,Object)"));
   }
   
   public void testUpdateValueAsYouType() throws Exception {
      text.putClientProperty(AbstractBinder.BINDING_STRATEGY_PROPERTY, 
            AbstractBinder.BINDING_STRATEGY_AS_YOU_TYPE);
      assertNull(componentBinder.bind(binder, text,
            (DataProviderMetadata) null));
      assertNotNull(componentBinder.bind(binder, text, fieldMeta));

      simulateTyping(text, "someValue");
      assertEquals("someValue", binder.get("populateForm(FieldMetadata,Object)"));

      simulateTyping(text, " ");
      assertEquals("", binder.get("populateForm(FieldMetadata,Object)"));
   }

   public void testUpdateValueWithoutTrim() throws Exception {
      binder.registerWidgetBinder("text", new JTextComponentBinder(false));
      componentBinder = binder.getWidgetBinder(text);
      
      assertNull(componentBinder.bind(binder, text,
            (DataProviderMetadata) null));
      assertNotNull(componentBinder.bind(binder, text, fieldMeta));

      text.setText("someValue");
      simulateFocusLost(text);
      assertEquals("someValue", binder.get("populateForm(FieldMetadata,Object)"));

      text.setText("  ");
      simulateFocusLost(text);
      assertEquals("  ", binder.get("populateForm(FieldMetadata,Object)"));

      text.setText(null);
      simulateFocusLost(text);
      assertEquals("", binder.get("populateForm(FieldMetadata,Object)"));
   }

   protected void simulateFocusLost(JTextComponent component) {
      FocusListener[] listeners = component.getFocusListeners();
      if (listeners.length == 0) {
         return;
      }

      FocusEvent event = new FocusEvent(component, FocusEvent.FOCUS_LOST);
      for (int i = 0; i < listeners.length; i++) {
         listeners[i].focusLost(event);
      }
   }

   private void simulateTyping(final JTextField component, 
         final String value) throws InterruptedException, InvocationTargetException {
      component.setText("");
      final char[] chars = value.toCharArray();

      for (int i = 0; i < chars.length; i++) {
         final char c = chars[i];

         EventQueue.invokeAndWait(new Runnable() {
            public void run() {
               component.processKeyEvent(new KeyEvent(component, KeyEvent.KEY_TYPED, 
                     EventQueue.getMostRecentEventTime(), 0, KeyEvent.VK_UNDEFINED, 
                     c));
            }
         });
      }
   }
}
