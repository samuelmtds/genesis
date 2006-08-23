/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.swing.lookup;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import net.java.dev.genesis.ui.binding.RecursiveLookupStrategy;

public class MockRecursiveComponentLookupStrategy extends
      RecursiveLookupStrategy {
   private final Map map = new HashMap();

   public Object get(Object key) {
      return map.get(key);
   }

   public void put(Object key, Object value) {
      map.put(key, value);
   }

   public void clear() {
      map.clear();
   }

   protected Object lookupImpl(Object object, String name) {
      put("lookupImpl(Object,String)", name);
      return object;
   }

   protected boolean isAlreadyBound(Object object) {
      put("isAlreadyBound(Object)", object);
      return false;
   }

   protected String getRealName(Object object) {
      put("getRealName(Object)", object);
      return ((Component)object).getName();
   }
}
