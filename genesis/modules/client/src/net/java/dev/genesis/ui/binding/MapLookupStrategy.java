/*
 * The Genesis Project
 * Copyright (C) 2006-2008  Summa Technologies do Brasil Ltda.
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
import java.util.IdentityHashMap;
import java.util.Map;
import net.java.dev.genesis.util.Bundle;


public class MapLookupStrategy implements LookupStrategy {
   private Map components = new HashMap();
   private Map identityMap = new IdentityHashMap();

   public Object lookup(Object object, String name) {
      return components.get(name);
   }

   public Object register(String name, Object object) {
      return register(name, object, false);
   }

   protected Object register(String name, Object object, boolean onlyIfMissing) {
      if (identityMap.containsKey(object)) {
         if (onlyIfMissing) {
            return null;
         }

         throw new IllegalArgumentException(Bundle.getMessage(MapLookupStrategy.class,
               "COMPONENT_X_IS_ALREADY_BOUND", object)); // NOI18N
      }

      identityMap.put(object, name);
      final Object oldValue = components.put(name, object);
      if (oldValue != null) {
         identityMap.remove(oldValue);
      }

      return object;
   }

   protected void registerMap(String name, Object object) {
      if (name == null || components.containsKey(name)) {
         return;
      }

      register(name, object, true);
   }

   public String getName(Object object) {
      return (String) identityMap.get(object);
   }
}
