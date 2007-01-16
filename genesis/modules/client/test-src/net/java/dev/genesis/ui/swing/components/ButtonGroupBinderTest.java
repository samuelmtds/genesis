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

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.GroupBinder;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.MockSwingBinder;
import net.java.dev.genesis.ui.swing.SwingBinder;

public class ButtonGroupBinderTest extends GenesisTestCase {   
   private AbstractButton button1;
   private AbstractButton button2;
   private MockSwingBinder binder;
   private MockForm form;
   private ButtonGroup group;
   private GroupBinder groupBinder;
   private FieldMetadata fieldMeta;
   
   public ButtonGroupBinderTest() {
      super("Button Group Binder Unit Test");
   }

   protected void setUp() throws Exception {
      button1 = SwingUtils.newButton();
      button2 = SwingUtils.newButton();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      fieldMeta = form.getFormMetadata().getFieldMetadata("stringField");
      group = new ButtonGroup();
      group.add(button1);
      group.add(button2);
   }

   public void testButtonGroupWithClientProperty() {
      groupBinder = binder.getGroupBinder(group);
      assertTrue(groupBinder instanceof ButtonGroupBinder);
      
      groupBinder.bind(binder, group, fieldMeta);
      
      Object value1 = new Object();
      button1.putClientProperty(SwingBinder.BUTTON_GROUP_SELECTION_VALUE,
            value1);

      Object value2 = new Object();
      button2.putClientProperty(SwingBinder.BUTTON_GROUP_SELECTION_VALUE,
            value2);

      assertFalse(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      group.setSelected(button1.getModel(), true);
      button1.doClick();
      assertTrue(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      assertSame(value1, binder.get("populateForm(FieldMetadata,Object)"));

      group.setSelected(button2.getModel(), true);
      button2.doClick();
      assertFalse(button1.getModel().isSelected());
      assertTrue(button2.getModel().isSelected());

      assertSame(value2, binder.get("populateForm(FieldMetadata,Object)"));
   }

   public void testButtonGroupWithActionCommand() {
      binder.registerButtonGroup("group", group, new ButtonGroupBinder(true));
      groupBinder = binder.getGroupBinder(group);
      groupBinder.bind(binder, group, fieldMeta);
      
      String value1 = "value1";
      button1.setActionCommand(value1);

      String value2 = "value2";
      button2.setActionCommand(value2);

      assertFalse(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      group.setSelected(button1.getModel(), true);
      button1.doClick();
      assertTrue(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      assertSame(value1, binder.get("populateForm(FieldMetadata,Object)"));

      group.setSelected(button2.getModel(), true);
      button2.doClick();
      assertFalse(button1.getModel().isSelected());
      assertTrue(button2.getModel().isSelected());

      assertSame(value2, binder.get("populateForm(FieldMetadata,Object)"));
   }

   public void testButtonGroupWithName() {
      groupBinder = binder.getGroupBinder(group);
      groupBinder.bind(binder, group, fieldMeta);
      
      String value1 = "value1";
      button1.setName(value1);

      String value2 = "value2";
      button2.setName(value2);

      assertFalse(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      group.setSelected(button1.getModel(), true);
      button1.doClick();
      assertTrue(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      assertSame(value1, binder.get("populateForm(FieldMetadata,Object)"));

      group.setSelected(button2.getModel(), true);
      button2.doClick();
      assertFalse(button1.getModel().isSelected());
      assertTrue(button2.getModel().isSelected());

      assertSame(value2, binder.get("populateForm(FieldMetadata,Object)"));
   }

   public void testSetValueWithClientProperty() throws Exception {
      groupBinder = binder.getGroupBinder(group);
      BoundField boundField = groupBinder.bind(binder, group, fieldMeta);
      
      Object value1 = new Object();
      button1.putClientProperty(SwingBinder.BUTTON_GROUP_SELECTION_VALUE,
            value1);

      Object value2 = new Object();
      button2.putClientProperty(SwingBinder.BUTTON_GROUP_SELECTION_VALUE,
            value2);

      assertNotNull(boundField);

      assertFalse(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      boundField.setValue(value1);

      assertTrue(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      boundField.setValue(value2);

      assertFalse(button1.getModel().isSelected());
      assertTrue(button2.getModel().isSelected());

      boundField.setValue(new Object());

      assertFalse(button1.getModel().isSelected());
      // It's not possible to unselect all after first selection
      assertTrue(button2.getModel().isSelected());
   }

   public void testSetValueWithActionCommand() throws Exception {
      binder.registerButtonGroup("group", group, new ButtonGroupBinder(true));
      groupBinder = binder.getGroupBinder(group);
      BoundField boundField = groupBinder.bind(binder, group, fieldMeta);
      
      String value1 = "value1";
      button1.setActionCommand(value1);

      String value2 = "value2";
      button2.setActionCommand(value2);

      assertNotNull(boundField);

      assertFalse(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      boundField.setValue(value1);

      assertTrue(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      boundField.setValue(value2);

      assertFalse(button1.getModel().isSelected());
      assertTrue(button2.getModel().isSelected());

      boundField.setValue("bogusvalue");

      assertFalse(button1.getModel().isSelected());
      // It's not possible to unselect all after first selection
      assertTrue(button2.getModel().isSelected());
   }

   public void testSetEnabled() {
      groupBinder = binder.getGroupBinder(group);
      BoundField boundField = groupBinder.bind(binder, group, fieldMeta);

      String value1 = "value1";
      button1.setActionCommand(value1);

      String value2 = "value2";
      button2.setActionCommand(value2);

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
      
      String value1 = "value1";
      button1.setActionCommand(value1);

      String value2 = "value2";
      button2.setActionCommand(value2);

      assertTrue(button1.isVisible());
      assertTrue(button2.isVisible());

      boundField.setVisible(false);

      assertFalse(button1.isVisible());
      assertFalse(button2.isVisible());

      boundField.setVisible(true);

      assertTrue(button1.isVisible());
      assertTrue(button2.isVisible());
   }
}
