/*
 * The Genesis Project
 * Copyright (C) 2004  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.tests.ui;

import java.util.HashMap;
import java.util.Map;

import net.java.dev.genesis.tests.TestCase;
import net.java.dev.genesis.ui.controller.DefaultFormController;
import net.java.dev.genesis.ui.controller.FormController;

public class ContextTest extends TestCase {

   private FormController getController(Object form) throws Exception {
      final FormController formController = new DefaultFormController();
      formController.setFormMetadata(getFormMetadata(form));
      formController.setForm(form);
      formController.setup();
      return formController;
   }

   public void testContext() throws Exception {
      final FooForm form = new FooForm();
      final FormController controller = getController(form);

      assertEquals(form.context.get("someVar"), Boolean.TRUE);

      final Map newValues = new HashMap();
      newValues.put("string1", " a342 23");
      controller.populate(newValues);

      assertEquals(form.context.get("someVar"), Boolean.FALSE);
   }

   /**
    * @Form
    */
   public static class FooForm {
      private Map context;
      private String string1;

      public void setContext(Map context) {
         this.context = context;
      }

      /**
       * @Condition someVar=g:isEmpty(string1)
       */
      public String getString1() {
         return string1;
      }

      public void setString1(String string1) {
         this.string1 = string1;
      }
   }
}