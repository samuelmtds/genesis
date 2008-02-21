/*
 * The Genesis Project
 * Copyright (C) 2008  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.text;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockBean;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.ui.controller.FormControllerRegistry;
import net.java.dev.genesis.ui.controller.MockFormController;
import net.java.dev.genesis.ui.metadata.FormMetadataRegistry;

public class ScriptFormatterTest extends GenesisTestCase {
   public void testFormWithoutMetadata() {
      try {
         new ScriptFormatter(new MockForm(), "");
         fail("Form has no metadata and still worked");
      } catch (IllegalArgumentException iae) {
         // expected
      }   
   }

   public void testFormWithoutController() {
      try {
         MockForm form = new MockForm();
         FormMetadataRegistry.getInstance().register(MockForm.class, 
               form.getFormMetadata());
         new ScriptFormatter(form, "");
         fail("Form has no controller and still worked");
      } catch (IllegalArgumentException iae) {
         // expected
      }   
   }

   public void testConstructorWithWorkingForm() {
      createFormatter(false);
   }

   private ScriptFormatter createFormatter(boolean setup) {
      MockForm form = createForm(setup);

      return new ScriptFormatter(form, "concat($o/key, ' - ', $o/value)");
   }

   private MockForm createForm(boolean setup) {
      MockForm form = new MockForm();
      FormMetadataRegistry.getInstance().
            register(MockForm.class, form.getFormMetadata());
      MockFormController controller = form.getController();
      FormControllerRegistry.getInstance().register(form, controller);

      if (setup) {
         try {
            controller.setup();
            controller.put(MockFormController.SCRIPT_CONTEXT, form.
                  getFormMetadata().getScript().newContext(form));
         } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
         }
      }

      return form;
   }

   public void testFormatWithNonSetupController() {
      try {
         createFormatter(false).format(null);
         fail("Should throw a IllegalStateException since form has not been " +
               "setup");
      } catch (IllegalStateException ise) {
         // expected
      }   
   }

   public void testFormatWithValidObject() {
      assertEquals("test - Test", createFormatter(true).format(new MockBean(
            "test", "Test")));
   }

   public void testFormatWithRegisteredAlias() {
      try {
         MockForm form = createForm(true);
         form.getController().getScriptContext().declare("aVar", form);
         new ScriptFormatter(form, "$aVar/key", "aVar").format(new MockBean(
               "test", "Test"));
         fail("Should throw a IllegalStateException since aVar is already " +
               "declared");
      } catch (IllegalStateException ise) {
         // expected
      }
   }
}
