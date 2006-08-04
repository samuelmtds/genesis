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
import javax.swing.JProgressBar;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.ComponentBinder;
import net.java.dev.genesis.ui.swing.MockSwingBinder;

public class JProgressBarComponentBinderTest extends GenesisTestCase {
   private JProgressBar progress;
   private MockSwingBinder binder;
   private ComponentBinder componentBinder;
   private BoundField boundField;
   private MockForm form;
   private FieldMetadata fieldMeta;

   
   public JProgressBarComponentBinderTest() {
      super("JProgressBar Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      progress = new JProgressBar();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      componentBinder = binder.getComponentBinder(progress);
      fieldMeta = form.getFormMetadata().getFieldMetadata("intField");
   }
   
   public void testSetValue() throws Exception {
      assertTrue(componentBinder instanceof JProgressBarComponentBinder);
      
      assertNull(componentBinder.bind(binder, progress, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, progress, (DataProviderMetadata) null));

      boundField = componentBinder.bind(binder, progress, fieldMeta);
      assertNotNull(boundField);

      assertEquals(0, progress.getValue());

      boundField.setValue(new Integer(3));
      assertEquals(3, progress.getValue());

      boundField.setValue(new Integer(100));
      assertEquals(100, progress.getValue());
   }
}
