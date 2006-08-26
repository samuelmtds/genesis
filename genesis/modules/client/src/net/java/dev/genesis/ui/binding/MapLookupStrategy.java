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
package net.java.dev.genesis.ui.binding;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;


public class MapLookupStrategy implements LookupStrategy {
   private Map components = new HashMap();
   private Map identityMap = new IdentityHashMap();

   public Object lookup(Object object, String name) {
      return components.get(name);
   }

   public Object register(String name, Object object) {
      if (identityMap.containsKey(object)) {
         throw new IllegalArgumentException("Component " + object +
            " is already bound");
      }

      identityMap.put(object, name);
      final Object oldValue = components.put(name, object);
      if (oldValue != null) {
         identityMap.remove(oldValue);
      }

      return object;
   }

   public String getName(Object object) {
      return (String) identityMap.get(object);
   }
}
