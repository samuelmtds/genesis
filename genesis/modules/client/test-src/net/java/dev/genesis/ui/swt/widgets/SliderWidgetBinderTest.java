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
package net.java.dev.genesis.ui.swt.widgets;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swt.MockSWTBinder;
import net.java.dev.genesis.ui.swt.widgets.SliderWidgetBinder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;

public class SliderWidgetBinderTest extends GenesisTestCase {
   private Shell root;
   private Slider slider;
   private MockSWTBinder binder;
   private WidgetBinder widgetBinder;
   private BoundField boundField;
   private MockForm form;
   private FieldMetadata fieldMeta;

   public SliderWidgetBinderTest() {
      super("Slider Widget Binder Unit Test");
   }

   protected void setUp() throws Exception {
      slider = new Slider(root = new Shell(), SWT.NONE);
      binder = new MockSWTBinder(root, form = new MockForm(), new Object());
      widgetBinder = binder.getWidgetBinder(slider);
      fieldMeta = form.getFormMetadata().getFieldMetadata("intField");
   }

   public void testSetValue() throws Exception {
      assertTrue(widgetBinder instanceof SliderWidgetBinder);
      
      assertNull(widgetBinder.bind(binder, slider, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, slider,
            (DataProviderMetadata) null));

      boundField = widgetBinder.bind(binder, slider, fieldMeta);
      assertNotNull(boundField);

      boundField.setValue(new Integer(3));
      assertEquals(3, slider.getSelection());

      boundField.setValue(new Integer(50));
      assertEquals(50, slider.getSelection());
   }

   public void testUpdateValue() throws Exception {
      assertNull(widgetBinder.bind(binder, slider, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, slider,
            (DataProviderMetadata) null));
      assertNotNull(widgetBinder.bind(binder, slider, fieldMeta));

      simulateSelection(3);
      Integer value = (Integer) binder.get("populateForm(FieldMetadata,Object)");
      assertNotNull(value);
      assertEquals(3, value.intValue());

      simulateSelection(10);
      value = (Integer)  binder.get("populateForm(FieldMetadata,Object)");
      assertNotNull(value);
      assertEquals(10, value.intValue());
   }
   
   private void simulateSelection(int selection) {
      slider.setSelection(selection);
      Event event = new Event();
      event.widget = slider;
      event.type = SWT.Selection;
      slider.notifyListeners(event.type, event);
   }
}
