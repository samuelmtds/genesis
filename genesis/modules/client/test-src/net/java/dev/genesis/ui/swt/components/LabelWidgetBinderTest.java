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
import net.java.dev.genesis.ui.swt.widgets.LabelWidgetBinder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class LabelWidgetBinderTest extends GenesisTestCase {
   private Shell root;
   private Label label;
   private MockSWTBinder binder;
   private WidgetBinder widgetBinder;
   private BoundField boundField;
   private MockForm form;
   private FieldMetadata fieldMeta;

   public LabelWidgetBinderTest() {
      super("Label Widget Binder Unit Test");
   }

   protected void setUp() throws Exception {
      label = new Label(root = new Shell(), SWT.NONE);
      binder = new MockSWTBinder(root, form = new MockForm(), null);
      widgetBinder = binder.getWidgetBinder(label);
      fieldMeta = form.getFormMetadata().getFieldMetadata("stringField");
   }

   public void testSetValue() throws Exception {
      assertTrue(widgetBinder instanceof LabelWidgetBinder);

      assertNull(widgetBinder.bind(binder, label,
            (DataProviderMetadata) null));
      assertNull(widgetBinder.bind(binder, label, (ActionMetadata) null));
      assertNotNull(boundField = widgetBinder.bind(binder, label, fieldMeta));

      boundField.setValue("someValue");
      assertEquals("someValue", label.getText());

      boundField.setValue(null);
      assertEquals("", label.getText());
   }
}
