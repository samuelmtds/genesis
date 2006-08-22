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
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swt.MockSwtBinder;
import net.java.dev.genesis.ui.swt.WidgetBinder;
import net.java.dev.genesis.ui.swt.widgets.TextWidgetBinder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class TextWidgetBinderTest extends GenesisTestCase {
   private Shell root;
   private Text text;
   private MockSwtBinder binder;
   private WidgetBinder widgetBinder;
   private BoundField boundField;
   private MockForm form;
   private FieldMetadata fieldMeta;

   public TextWidgetBinderTest() {
      super("Text Widget Binder Unit Test");
   }

   protected void setUp() throws Exception {
      text = new Text(root = new Shell(), SWT.NONE);
      binder = new MockSwtBinder(root, form = new MockForm(), null);
      binder.register("text", text);
      widgetBinder = binder.getWidgetBinder(text);
      fieldMeta = form.getFormMetadata().getFieldMetadata("stringField");
   }

   public void testSetValue() throws Exception {
      assertTrue(widgetBinder instanceof TextWidgetBinder);
      assertNull(widgetBinder
            .bind(binder, text, (DataProviderMetadata) null));
      assertNull(widgetBinder.bind(binder, text, (ActionMetadata) null));

      boundField = widgetBinder.bind(binder, text, fieldMeta);
      assertNotNull(boundField);

      boundField.setValue("someValue");
      assertEquals("someValue", text.getText());

      boundField.setValue("  ");
      assertEquals("  ", text.getText());

      boundField.setValue(null);
      assertEquals("", text.getText());
   }

   public void testUpdateValue() throws Exception {
      assertNull(widgetBinder.bind(binder, text,
            (DataProviderMetadata) null));
      assertNull(widgetBinder.bind(binder, text, (ActionMetadata) null));
      assertNotNull(widgetBinder.bind(binder, text, fieldMeta));

      text.setText("someValue");
      simulateFocusLost(text);
      assertEquals("someValue", binder.get("populateForm(FieldMetadata,Object)"));

      text.setText("  ");
      simulateFocusLost(text);
      assertEquals("", binder.get("populateForm(FieldMetadata,Object)"));

      text.setText("");
      simulateFocusLost(text);
      assertEquals("", binder.get("populateForm(FieldMetadata,Object)"));
   }

   public void testUpdateValueWithoutTrim() throws Exception {
      binder.registerWidgetBinder("text", new TextWidgetBinder(false));
      widgetBinder = binder.getWidgetBinder(text);
      
      assertNull(widgetBinder.bind(binder, text,
            (DataProviderMetadata) null));
      assertNull(widgetBinder.bind(binder, text, (ActionMetadata) null));
      assertNotNull(widgetBinder.bind(binder, text, fieldMeta));

      text.setText("someValue");
      simulateFocusLost(text);
      assertEquals("someValue", binder.get("populateForm(FieldMetadata,Object)"));

      text.setText("  ");
      simulateFocusLost(text);
      assertEquals("  ", binder.get("populateForm(FieldMetadata,Object)"));

      text.setText("");
      simulateFocusLost(text);
      assertEquals("", binder.get("populateForm(FieldMetadata,Object)"));
   }

   protected void simulateFocusLost(Widget component) {
      Event event = new Event();
      event.widget = component;
      event.button = 1;
      event.type = SWT.FocusOut;
      component.notifyListeners(event.type, event);
   }
}
