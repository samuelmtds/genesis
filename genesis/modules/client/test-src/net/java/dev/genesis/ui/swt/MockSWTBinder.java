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
package net.java.dev.genesis.ui.swt;

import java.util.HashMap;
import java.util.Map;

import net.java.dev.genesis.ui.binding.GroupBinder;
import net.java.dev.genesis.ui.binding.LookupStrategy;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

public class MockSWTBinder extends SWTBinder {
   private Map map = new HashMap();

   public MockSWTBinder(Composite composite, Object form, Object handler) {
      super(composite, form, handler);
   }

   public MockSWTBinder(Composite composite,
         LookupStrategy lookupStrategy, Object form, Object handler,
         boolean bindDefaultButton) {
      super(composite, lookupStrategy, form, handler, bindDefaultButton);
   }

   public MockSWTBinder(Composite composite,
         LookupStrategy lookupStrategy, Object form, Object handler) {
      super(composite, lookupStrategy, form, handler);
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
   
   public WidgetBinder registerWidgetBinder(String name, Object binder) {
      return super.registerWidgetBinder(name, binder);
   }

   public WidgetBinder getWidgetBinder(Widget widget) {
      return super.getWidgetBinder(widget);
   }

   public GroupBinder getGroupBinder(Composite group) {
      return super.getGroupBinder(group);
   }

   public void invokeFormAction(ActionMetadata actionMetadata) {
      map.put("invokeFormAction(ActionMetadata)", actionMetadata);
   }

   public void populateForm(FieldMetadata fieldMetadata, Object value) {
      map.put("populateForm(FieldMetadata,Object)", value);
   }

   public void updateFormSelection(DataProviderMetadata dataProviderMetadata,
         int[] indexes) {
      map.put("updateFormSelection(DataProviderMetadata,int[])", indexes);
   }

}
