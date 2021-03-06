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
package net.java.dev.genesis.ui.swing;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;

import net.java.dev.genesis.ui.binding.GroupBinder;
import net.java.dev.genesis.ui.binding.LookupStrategy;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;

public class MockSwingBinder extends SwingBinder {
   private Map map = new HashMap();

   public MockSwingBinder(Component component,
         LookupStrategy lookupStrategy, Object form, Object handler,
         boolean bindDefaultButton) {
      super(component, lookupStrategy, form, handler, bindDefaultButton);
   }

   public MockSwingBinder(Component component,
         LookupStrategy lookupStrategy, Object form, Object handler) {
      super(component, lookupStrategy, form, handler);
   }

   public MockSwingBinder(Component component,
         LookupStrategy lookupStrategy, Object form) {
      super(component, lookupStrategy, form);
   }

   public MockSwingBinder(Component component, Object form, Object handler) {
      super(component, form, handler);
   }

   public MockSwingBinder(Component component, Object form) {
      super(component, form);
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

   public WidgetBinder getWidgetBinder(Component component) {
      return super.getWidgetBinder(component);
   }

   public GroupBinder getGroupBinder(ButtonGroup group) {
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
