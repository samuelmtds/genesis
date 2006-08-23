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
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.MockSwingBinder;

public class JSpinnerComponentBinderTest extends GenesisTestCase {
   private JSpinner spinner;
   private MockSwingBinder binder;
   private WidgetBinder componentBinder;
   private BoundField boundField;
   private MockForm form;
   private FieldMetadata fieldMeta;

   public JSpinnerComponentBinderTest() {
      super("JSpinner Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      spinner = new JSpinner();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      componentBinder = binder.getWidgetBinder(spinner);
      fieldMeta = form.getFormMetadata().getFieldMetadata("intField");
   }

   public void testSetValue() throws Exception {
      assertTrue(componentBinder instanceof JSpinnerComponentBinder);

      assertNull(componentBinder.bind(binder, spinner, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, spinner,
            (DataProviderMetadata) null));

      boundField = componentBinder.bind(binder, spinner, fieldMeta);
      assertNotNull(boundField);

      boundField.setValue(new Integer(3));
      assertEquals(new Integer(3), spinner.getValue());

      boundField.setValue(new Integer(100));
      assertEquals(new Integer(100), spinner.getValue());
   }

   public void testUpdateValue() throws Exception {
      assertNull(componentBinder.bind(binder, spinner, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, spinner,
            (DataProviderMetadata) null));
      assertNotNull(componentBinder.bind(binder, spinner, fieldMeta));

      spinner.setValue(new Integer(3));
      Integer value = (Integer) binder
            .get("populateForm(FieldMetadata,Object)");
      assertNotNull(value);
      assertEquals(3, value.intValue());

      spinner.setValue(new Integer(100));
      value = (Integer) binder.get("populateForm(FieldMetadata,Object)");
      assertNotNull(value);
      assertEquals(100, value.intValue());
   }
   
   public void testSetValueWithOtherModel() throws Exception {
      fieldMeta = form.getFormMetadata().getFieldMetadata("stringField");
      spinner.setModel(new SpinnerListModel(new String[] {"first", "second", "third"}));
      
      assertTrue(componentBinder instanceof JSpinnerComponentBinder);

      assertNull(componentBinder.bind(binder, spinner, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, spinner,
            (DataProviderMetadata) null));

      boundField = componentBinder.bind(binder, spinner, fieldMeta);
      assertNotNull(boundField);

      boundField.setValue("second");
      assertEquals("second", spinner.getValue());

      boundField.setValue("first");
      assertEquals("first", spinner.getValue());
   }

   public void testUpdateValueWithOtherModel() throws Exception {
      fieldMeta = form.getFormMetadata().getFieldMetadata("stringField");
      spinner.setModel(new SpinnerListModel(new String[] { "first", "second",
            "third" }));

      assertNull(componentBinder.bind(binder, spinner, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, spinner,
            (DataProviderMetadata) null));
      assertNotNull(componentBinder.bind(binder, spinner, fieldMeta));

      spinner.setValue("second");
      assertEquals("second", binder.get("populateForm(FieldMetadata,Object)"));

      spinner.setValue("third");
      assertEquals("third", binder.get("populateForm(FieldMetadata,Object)"));
   }
}
