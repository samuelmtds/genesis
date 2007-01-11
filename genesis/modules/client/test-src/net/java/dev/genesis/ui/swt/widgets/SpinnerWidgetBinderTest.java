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
package net.java.dev.genesis.ui.swt.widgets;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swt.MockSWTBinder;
import net.java.dev.genesis.ui.swt.widgets.SpinnerWidgetBinder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

public class SpinnerWidgetBinderTest extends GenesisTestCase {
   private Shell root;
   private Spinner spinner;
   private MockSWTBinder binder;
   private WidgetBinder widgetBinder;
   private BoundField boundField;
   private MockForm form;
   private FieldMetadata fieldMeta;

   public SpinnerWidgetBinderTest() {
      super("Spinner Widget Binder Unit Test");
   }

   protected void setUp() throws Exception {
      spinner = new Spinner(root = new Shell(), SWT.NONE);
      spinner.setDigits(0);
      spinner.setMaximum(Integer.MAX_VALUE);
      spinner.setMinimum(Integer.MIN_VALUE);
      binder = new MockSWTBinder(root, form = new MockForm(), new Object());
      widgetBinder = binder.getWidgetBinder(spinner);
      fieldMeta = form.getFormMetadata().getFieldMetadata("intField");
   }

   public void testSetValue() throws Exception {
      assertTrue(widgetBinder instanceof SpinnerWidgetBinder);

      assertNull(widgetBinder.bind(binder, spinner, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, spinner,
            (DataProviderMetadata) null));

      boundField = widgetBinder.bind(binder, spinner, fieldMeta);
      assertNotNull(boundField);

      boundField.setValue(new Integer(3));
      assertEquals(3, spinner.getSelection());

      boundField.setValue(new Integer(100));
      assertEquals(100, spinner.getSelection());
   }

   public void testUpdateValue() throws Exception {
      assertNull(widgetBinder.bind(binder, spinner, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, spinner,
            (DataProviderMetadata) null));
      assertNotNull(widgetBinder.bind(binder, spinner, fieldMeta));

      simulateSelection(3);
      Object value = binder
            .get("populateForm(FieldMetadata,Object)");
      assertNotNull(value);
      assertEquals(new Integer(3), value);

      simulateSelection(100);
      value = binder.get("populateForm(FieldMetadata,Object)");
      assertNotNull(value);
      assertEquals(new Integer(100), value);

      spinner.setDigits(4);
      simulateSelection(31415);
      value = binder.get("populateForm(FieldMetadata,Object)");
      assertNotNull(value);
      assertEquals(new Float(3.1415), value);
      
   }
   
   private void simulateSelection(int selection) {
      spinner.setSelection(selection);
      Event event = new Event();
      event.widget = spinner;
      event.type = SWT.Selection;
      spinner.notifyListeners(event.type, event);
   }
}
