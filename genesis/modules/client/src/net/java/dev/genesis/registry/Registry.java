/*
 * The Genesis Project
 * Copyright (C) 2004-2005  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.registry;

import java.util.HashMap;
import java.util.Map;

public class Registry {
   private final Map registry = new HashMap();

   public Object register(Class clazz, Object o) {
      return registry.put(clazz, o);
   }

   public void deregister() {
      registry.clear();
   }

   public void deregister(Class clazz) {
      registry.remove(clazz);
   }

   public Object get(Class clazz) {
      return registry.get(clazz);
   }

   protected Object searchHierarchy(Class clazz) {
      if (clazz == null) {
         return null;
      }

      Object o = null;

      if ((o = get(clazz)) != null) {
         return o;
      }

      final Class[] interfaces = clazz.getInterfaces();

      for (int i = 0; i < interfaces.length; i++) {
         if ((o = searchHierarchy(interfaces[i])) != null) {
            return o;
         }
      }

      return clazz.isInterface() ? null : 
            searchHierarchy(clazz.getSuperclass());
   }

   public Object get(final Class clazz, boolean superClass) {
      if (!superClass) {
         return get(clazz);
      }

      Object o = null;
      Class currentClass = clazz;

      while ((o = get(currentClass)) == null && !Object.class.equals(
            currentClass)) {
         currentClass = currentClass.getSuperclass();

         if (currentClass == null) {
            currentClass = Object.class;
         }
      }

      if (!Object.class.equals(currentClass)) {
         return o;
      }

      final Object hierarchyObject = searchHierarchy(clazz);

      return hierarchyObject == null ? o : hierarchyObject;
   }

   public Object get(Object o) {
      final Class clazz = o == null ? Object.class : o.getClass();
      return get(clazz, true);
   }
}