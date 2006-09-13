/*
 * The Genesis Project
 * Copyright (C) 2005-2006 Summa Technologies do Brasil Ltda.
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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.java.dev.genesis.reflection.FieldEntry;
import net.java.dev.genesis.script.jxpath.JXPathScript;
import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.controller.FormControllerFactory;
import net.java.dev.genesis.ui.controller.MockFormController;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;
import net.java.dev.genesis.ui.metadata.MethodMetadata;

public class MockForm implements FormMetadataFactory, FormControllerFactory {
   private FormMetadata metadata = new FormMetadata(getClass(),
         new JXPathScript());

   private MockFormController controller = new MockFormController();

   private Map context;
   
   // Dummy properties
   private Object objectField;
   private String stringField;
   private int intField;

   public MockForm() {
      Method someAction = getMethod("someAction");
      Method someDataProvider = getMethod("someDataProvider");
      metadata.addFieldMetadata("stringField", new FieldMetadata("stringField",
            String.class, true));
      metadata.addFieldMetadata("objectField", new FieldMetadata("objectField",
            Object.class, true));
      metadata.addFieldMetadata("intField", new FieldMetadata("intField",
            Integer.TYPE, true));
      metadata.addMethodMetadata(someAction, new MethodMetadata(someAction,
            true, false));
      
      MethodMetadata methodMeta = new MethodMetadata(
            someDataProvider, true, true);
      metadata.addMethodMetadata(someDataProvider, methodMeta);
      methodMeta.getDataProviderMetadata().setWidgetName("dataProviderField");
      methodMeta.getDataProviderMetadata().setObjectField(new FieldEntry("objectField", Object.class));
   }

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

   public Method getMethod(String methodName) {
      try {
         return getClass().getMethod(methodName, new Class[0]);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public void someAction() {
   }

   public List someDataProvider() {
      return Collections.EMPTY_LIST;
   }

   public Object getObjectField() {
      return objectField;
   }

   public void setObjectField(Object objectField) {
      this.objectField = objectField;
   }

   public String getStringField() {
      return stringField;
   }

   public void setStringField(String stringField) {
      this.stringField = stringField;
   }

   public int getIntField() {
      return intField;
   }

   public void setIntField(int intField) {
      this.intField = intField;
   }
}