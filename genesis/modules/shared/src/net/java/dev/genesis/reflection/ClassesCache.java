/*
 * The Genesis Project
 * Copyright (C) 2004-2007  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.reflection;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class ClassesCache {
   private static Map cache = new HashMap();

   static {
      put(Byte.TYPE.getName(), Byte.TYPE);
      put(Character.TYPE.getName(), Character.TYPE);
      put(Short.TYPE.getName(), Short.TYPE);
      put(Integer.TYPE.getName(), Integer.TYPE);
      put(Long.TYPE.getName(), Long.TYPE);
      put(Float.TYPE.getName(), Float.TYPE);
      put(Double.TYPE.getName(), Double.TYPE);
      put(Boolean.TYPE.getName(), Boolean.TYPE);
      put(Void.TYPE.getName(), Void.TYPE);
   }

   public static Class getClass(String className) throws ClassNotFoundException {
      WeakReference r = (WeakReference)cache.get(className);
      Class clazz = null;

      if (r == null || (clazz = (Class)r.get()) == null) {
         clazz = Class.forName(className);
         put(className, clazz);
      }

      return clazz;
   }

   private static void put(String className, Class clazz) {
      cache.put(className, new WeakReference(clazz));
   }
}