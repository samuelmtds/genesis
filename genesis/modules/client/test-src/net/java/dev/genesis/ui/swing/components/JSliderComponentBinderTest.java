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
import javax.swing.JSlider;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.MockSwingBinder;

public class JSliderComponentBinderTest extends GenesisTestCase {
   private JSlider slider;
   private MockSwingBinder binder;
   private WidgetBinder componentBinder;
   private BoundField boundField;
   private MockForm form;
   private FieldMetadata fieldMeta;

   public JSliderComponentBinderTest() {
      super("JSlider Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      slider = new JSlider();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      componentBinder = binder.getWidgetBinder(slider);
      fieldMeta = form.getFormMetadata().getFieldMetadata("intField");
   }

   public void testSetValue() throws Exception {
      assertTrue(componentBinder instanceof JSliderComponentBinder);
      
      assertNull(componentBinder.bind(binder, slider, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, slider,
            (DataProviderMetadata) null));

      boundField = componentBinder.bind(binder, slider, fieldMeta);
      assertNotNull(boundField);

      boundField.setValue(new Integer(3));
      assertEquals(3, slider.getValue());

      boundField.setValue(new Integer(100));
      assertEquals(100, slider.getValue());
   }

   public void testUpdateValue() throws Exception {
      assertNull(componentBinder.bind(binder, slider, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, slider,
            (DataProviderMetadata) null));
      assertNotNull(componentBinder.bind(binder, slider, fieldMeta));

      slider.setValue(3);
      Integer value = (Integer) binder.get("populateForm(FieldMetadata,Object)");
      assertNotNull(value);
      assertEquals(3, value.intValue());

      slider.setValue(100);
      value = (Integer)  binder.get("populateForm(FieldMetadata,Object)");
      assertNotNull(value);
      assertEquals(100, value.intValue());
   }
}
