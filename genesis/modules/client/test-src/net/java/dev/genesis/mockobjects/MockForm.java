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
package net.java.dev.genesis.mockobjects;

import java.util.Map;

import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.controller.FormControllerFactory;
import net.java.dev.genesis.ui.controller.MockFormController;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;

public class MockForm implements FormMetadataFactory, FormControllerFactory {
   private FormMetadata metadata = new FormMetadata(getClass());
   private MockFormController controller = new MockFormController();
   private Map context;

   public FormMetadata getFormMetadata(Class formClass) {
      return metadata;
   }
   
   public FormMetadata getFormMetadata() {
      return getFormMetadata(getClass());
   }

   public FormController getFormController(Object form) {
      return controller;
   }

   public MockFormController getController() {
      return controller;
   }

   public FormMetadata getMetadata() {
      return metadata;
   }

   public Map getContext() {
      return context;
   }

   public void setContext(Map context) {
      this.context = context;
   }
}