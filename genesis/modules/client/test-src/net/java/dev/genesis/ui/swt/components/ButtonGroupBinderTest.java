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
package net.java.dev.genesis.ui.swt.components;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.GroupBinder;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swt.MockSWTBinder;
import net.java.dev.genesis.ui.swt.SWTBinder;
import net.java.dev.genesis.ui.swt.widgets.ButtonGroupBinder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class ButtonGroupBinderTest extends GenesisTestCase {
   private Shell root;
   private Button button1;
   private Button button2;
   private MockSWTBinder binder;
   private MockForm form;
   private Composite group;
   private GroupBinder groupBinder;
   private FieldMetadata fieldMeta;

   public ButtonGroupBinderTest() {
      super("Button Group Binder Unit Test");
   }

   protected void setUp() throws Exception {
      group = new Group(root = new Shell(), SWT.NONE);
      button1 = new Button(group, SWT.RADIO) {
         private boolean visible = true;

         public boolean isVisible() {
            return visible;
         }

         public void setVisible(boolean visible) {
            this.visible = visible;
         }

         protected void checkSubclass() {
         }
      };
      button2 = new Button(group, SWT.RADIO) {
         private boolean visible = true;

         public boolean isVisible() {
            return visible;
         }

         public void setVisible(boolean visible) {
            this.visible = visible;
         }

         protected void checkSubclass() {
         }
      };
      binder = new MockSWTBinder(root, form = new MockForm(), new Object());
      fieldMeta = form.getFormMetadata().getFieldMetadata("stringField");
   }

   public void testButtonGroup() {
      groupBinder = binder.getGroupBinder(group);
      assertTrue(groupBinder instanceof ButtonGroupBinder);

      groupBinder.bind(binder, group, fieldMeta);

      Object value1 = new Object();
      button1.setData(SWTBinder.BUTTON_GROUP_SELECTION_VALUE, value1);

      Object value2 = new Object();
      button2.setData(SWTBinder.BUTTON_GROUP_SELECTION_VALUE, value2);

      assertFalse(button1.getSelection());
      assertFalse(button2.getSelection());

      simulateClick(button1);
      assertTrue(button1.getSelection());
      assertFalse(button2.getSelection());

      assertSame(value1, binder.get("populateForm(FieldMetadata,Object)"));

      simulateClick(button2);
      assertFalse(button1.getSelection());
      assertTrue(button2.getSelection());

      assertSame(value2, binder.get("populateForm(FieldMetadata,Object)"));
   }

   public void testSetValue() throws Exception {
      groupBinder = binder.getGroupBinder(group);
      BoundField boundField = groupBinder.bind(binder, group, fieldMeta);

      Object value1 = new Object();
      button1.setData(SWTBinder.BUTTON_GROUP_SELECTION_VALUE, value1);

      Object value2 = new Object();
      button2.setData(SWTBinder.BUTTON_GROUP_SELECTION_VALUE, value2);

      assertNotNull(boundField);

      assertFalse(button1.getSelection());
      assertFalse(button2.getSelection());

      boundField.setValue(value1);

      assertTrue(button1.getSelection());
      assertFalse(button2.getSelection());

      boundField.setValue(value2);

      assertFalse(button1.getSelection());
      assertTrue(button2.getSelection());

      boundField.setValue(new Object());

      assertFalse(button1.getSelection());
      assertFalse(button2.getSelection());
   }

   public void testSetEnabled() {
      groupBinder = binder.getGroupBinder(group);
      BoundField boundField = groupBinder.bind(binder, group, fieldMeta);

      assertTrue(button1.isEnabled());
      assertTrue(button2.isEnabled());

      boundField.setEnabled(false);

      assertFalse(button1.isEnabled());
      assertFalse(button2.isEnabled());

      boundField.setEnabled(true);

      assertTrue(button1.isEnabled());
      assertTrue(button2.isEnabled());
   }

   public void testSetVisible() {
      groupBinder = binder.getGroupBinder(group);
      BoundField boundField = groupBinder.bind(binder, group, fieldMeta);

      assertTrue(button1.isVisible());
      assertTrue(button2.isVisible());

      boundField.setVisible(false);

      assertFalse(button1.isVisible());
      assertFalse(button2.isVisible());

      boundField.setVisible(true);

      assertTrue(button1.isVisible());
      assertTrue(button2.isVisible());
   }

   private void simulateClick(Button button) {
      button.setSelection(true);
      Event event = new Event();
      event.widget = button;
      event.button = 1;
      event.type = SWT.Selection;
      button.notifyListeners(event.type, event);
   }
}
