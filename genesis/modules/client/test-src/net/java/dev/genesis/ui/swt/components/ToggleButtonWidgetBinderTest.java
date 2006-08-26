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
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swt.MockSWTBinder;
import net.java.dev.genesis.ui.swt.widgets.ButtonWidgetBinder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

public class ToggleButtonWidgetBinderTest extends GenesisTestCase {
   private Shell root;
   private Button button;
   private MockSWTBinder binder;
   private WidgetBinder widgetBinder;
   private MockForm form;
   private BoundField boundField;
   private FieldMetadata fieldMeta;

   public ToggleButtonWidgetBinderTest() {
      super("Toggle Button Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      button = new Button(root = new Shell(), SWT.CHECK);
      binder = new MockSWTBinder(root, form = new MockForm(), new Object());
      widgetBinder = binder.getWidgetBinder(button);
      fieldMeta = form.getFormMetadata().getFieldMetadata("stringField");
   }

   public void testSetValue() throws Exception {
      assertTrue(widgetBinder instanceof ButtonWidgetBinder);
      
      assertNull(widgetBinder.bind(binder, button, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, button, (DataProviderMetadata) null));

      boundField = widgetBinder.bind(binder, button, fieldMeta);
      assertNotNull(boundField);

      assertFalse(button.getSelection());

      boundField.setValue(Boolean.TRUE);
      assertTrue(button.getSelection());

      boundField.setValue(Boolean.FALSE);
      assertFalse(button.getSelection());
   }

   public void testUpdateValue() throws Exception {
      assertNull(widgetBinder.bind(binder, button, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, button, (DataProviderMetadata) null));
      assertNotNull(widgetBinder.bind(binder, button, fieldMeta));

      simulateClick(button);
      assertEquals(Boolean.TRUE, binder
            .get("populateForm(FieldMetadata,Object)"));

      simulateClick(button);
      assertEquals(Boolean.FALSE, binder
            .get("populateForm(FieldMetadata,Object)"));
   }

   private void simulateClick(Button button) {
      button.setSelection(!button.getSelection());
      Event event = new Event();
      event.widget = button;
      event.button = 1;
      event.type = SWT.Selection;
      button.notifyListeners(event.type, event);
   }
}
