/*
 * The Genesis Project
 * Copyright (C) 2005  Summa Technologies do Brasil Ltda.
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
import java.util.IdentityHashMap;
import java.util.Map;

public class MapComponentLookupStrategy implements ComponentLookupStrategy {
   private Map components = new HashMap();
   private Map identityMap = new IdentityHashMap();

   public Component lookup(Component component, String name) {
      return (Component) components.get(name);
   }

   public Component register(String name, Component component) {
      if (identityMap.containsKey(component)) {
         throw new IllegalArgumentException("Component " + component +
            " is already bound");
      }

      identityMap.put(component, name);
      final Object oldValue = components.put(name, component);
      if (oldValue != null) {
         identityMap.remove(oldValue);
      }

      return component;
   }

   public String getName(Component component) {
      String name = (String) identityMap.get(component);

      if (name == null) {
         name = component.getName();
      }

      return name;
   }
}
