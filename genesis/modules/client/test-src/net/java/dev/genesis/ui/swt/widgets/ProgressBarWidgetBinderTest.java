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
import net.java.dev.genesis.ui.swt.widgets.ProgressBarWidgetBinder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class ProgressBarWidgetBinderTest extends GenesisTestCase {
   private Shell root;
   private ProgressBar progress;
   private MockSWTBinder binder;
   private WidgetBinder widgetBinder;
   private BoundField boundField;
   private MockForm form;
   private FieldMetadata fieldMeta;

   
   public ProgressBarWidgetBinderTest() {
      super("ProgressBar Widget Binder Unit Test");
   }

   protected void setUp() throws Exception {
      progress = new ProgressBar(root = new Shell(), SWT.NONE);
      binder = new MockSWTBinder(root, form = new MockForm(), new Object());
      widgetBinder = binder.getWidgetBinder(progress);
      fieldMeta = form.getFormMetadata().getFieldMetadata("intField");
   }
   
   public void testSetValue() throws Exception {
      assertTrue(widgetBinder instanceof ProgressBarWidgetBinder);
      
      assertNull(widgetBinder.bind(binder, progress, (ActionMetadata) null));
      assertNull(widgetBinder.bind(binder, progress, (DataProviderMetadata) null));

      boundField = widgetBinder.bind(binder, progress, fieldMeta);
      assertNotNull(boundField);

      assertEquals(0, progress.getSelection());

      boundField.setValue(new Integer(3));
      assertEquals(3, progress.getSelection());

      boundField.setValue(new Integer(100));
      assertEquals(100, progress.getSelection());
   }
}
