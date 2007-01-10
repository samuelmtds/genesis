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

import javax.swing.JPanel;
import javax.swing.JToggleButton;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.MockSwingBinder;

public class JToggleButtonComponentBinderTest extends GenesisTestCase {
   private JToggleButton toggle;
   private MockSwingBinder binder;
   private WidgetBinder componentBinder;
   private BoundField boundField;
   private MockForm form;
   private FieldMetadata fieldMeta;

   public JToggleButtonComponentBinderTest() {
      super("JToggleButton Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      toggle = new JToggleButton();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      componentBinder = binder.getWidgetBinder(toggle);
      fieldMeta = form.getFormMetadata().getFieldMetadata("stringField");
   }

   public void testSetValue() throws Exception {
      assertTrue(componentBinder instanceof JToggleButtonComponentBinder);
      assertNull(componentBinder.bind(binder, toggle,
            (DataProviderMetadata) null));

      boundField = componentBinder.bind(binder, toggle, fieldMeta);
      assertNotNull(boundField);

      assertFalse(toggle.isSelected());

      boundField.setValue(Boolean.TRUE);
      assertTrue(toggle.isSelected());

      boundField.setValue(Boolean.FALSE);
      assertFalse(toggle.isSelected());
   }

   public void testUpdateValue() throws Exception {
      assertNull(componentBinder.bind(binder, toggle,
            (DataProviderMetadata) null));
      assertNotNull(componentBinder.bind(binder, toggle, fieldMeta));

      toggle.doClick();
      assertEquals(Boolean.TRUE, binder.get("populateForm(FieldMetadata,Object)"));

      toggle.doClick();
      assertEquals(Boolean.FALSE, binder.get("populateForm(FieldMetadata,Object)"));
   }
}
