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
package net.java.dev.genesis.ui.binding;

import java.util.HashMap;
import java.util.Map;

import net.java.dev.genesis.mockobjects.MockBoundAction;
import net.java.dev.genesis.mockobjects.MockBoundDataProvider;
import net.java.dev.genesis.mockobjects.MockBoundField;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;

public class MockAbstractBinder extends AbstractBinder {
   private Map map = new HashMap();

   public MockAbstractBinder(Object root, Object form, Object handler) {
      super(root, form, handler, null);
   }

   public Object get(Object key) {
      return map.get(key);
   }

   public void put(Object key, Object value) {
      map.put(key, value);
   }

   public void clear() {
      map.clear();
   }

   protected BoundAction bindActionMetadata(String name,
         FormMetadata formMetadata, ActionMetadata actionMetadata) {
      BoundAction bound = new MockBoundAction(name);
      map.put("bindActionMetadata(String,FormMetadata,ActionMetadata)", bound);
      return bound;
   }

   protected BoundDataProvider bindDataProvider(String name,
         FormMetadata formMetadata, DataProviderMetadata dataProviderMetadata) {
      BoundDataProvider bound = new MockBoundDataProvider(name);
      map.put("bindDataProvider(String,FormMetadata,DataProviderMetadata)",
            bound);
      return bound;
   }

   protected BoundField bindFieldMetadata(String name,
         FormMetadata formMetadata, FieldMetadata fieldMetadata) {
      BoundField bound = new MockBoundField(name);
      map.put("bindFieldMetadata(String,FormMetadata,FieldMetadata)", bound);
      return bound;
   }

   public void handleException(Throwable throwable) {
      map.put("handleException(Throwable)", throwable);
   }

   protected ExceptionHandler createExceptionHandler() {
      ExceptionHandler handler = new MockExceptionHandler();
      map.put("createExceptionHandler()", handler);
      return handler;
   }

   public boolean isVirtual(Object widget) {
      map.put("isVirtual(Object)", widget);
      return false;
   }

   public String getName(Object object) {
      map.put("getName(Object)", object);
      return object == null ? "null" : object.toString();
   }

   protected LookupStrategy createLookupStrategy() {
      return null;
   }

   protected GroupBinder getDefaultGroupBinderFor(Object group) {
      return null;
   }

   protected WidgetBinder getDefaultWidgetBinderFor(Object widget) {
      return null;
   }

   protected void markBound() {
   }
   
   protected void markUnbound() {
   }
}
