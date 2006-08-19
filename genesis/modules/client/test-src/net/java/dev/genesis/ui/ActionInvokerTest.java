/*
 * The Genesis Project
 * Copyright (C) 2005 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;

public class ActionInvokerTest extends GenesisTestCase {
   private MockForm form;

   public ActionInvokerTest() {
      super("ActionInvoker Unit Test");
   }

   protected void setUp() throws Exception {
      form = new MockForm();
   }

   public void testInvoke() throws Exception {
      ActionInvoker.invoke(form, "action");

      // Assert that correct action was called
      assertEquals("action", form.getController().get(
            "invokeAction(actionName)"));
   }

   public void testRefresh() throws Exception {
      ActionInvoker.refresh(form);

      // Assert that controller.update() was called
      assertEquals(Boolean.TRUE, form.getController().get("update()"));
   }

   public void testSetupController() throws Exception {
      // Assert controller has not been setup
      assertTrue(!form.getController().isSetup());

      // 1st refresh
      ActionInvoker.refresh(form);
      // Assert that controller has been setup
      assertTrue(form.getController().isSetup());

      // 2nd refresh
      ActionInvoker.refresh(form);
      // Assert that controller has been setup
      assertTrue(form.getController().isSetup());
   }

}