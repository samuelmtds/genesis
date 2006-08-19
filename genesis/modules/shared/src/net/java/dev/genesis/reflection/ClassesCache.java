/*
 * The Genesis Project
 * Copyright (C) 2004  Summa Technologies do Brasil Ltda.
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

import java.util.HashMap;
import java.util.Map;

public final class ClassesCache {

   private static Map cache = new HashMap();

   static {
      cache.put(Byte.TYPE.getName(), Byte.TYPE);
      cache.put(Character.TYPE.getName(), Character.TYPE);
      cache.put(Short.TYPE.getName(), Short.TYPE);
      cache.put(Integer.TYPE.getName(), Integer.TYPE);
      cache.put(Long.TYPE.getName(), Long.TYPE);
      cache.put(Float.TYPE.getName(), Float.TYPE);
      cache.put(Double.TYPE.getName(), Double.TYPE);
      cache.put(Boolean.TYPE.getName(), Boolean.TYPE);
      cache.put(Void.TYPE.getName(), Void.TYPE);
   }

   public static Class getClass(String className) throws ClassNotFoundException {
      Class clazz = (Class) cache.get(className);

      if (clazz == null) {
         clazz = Class.forName(className);
         cache.put(className, clazz);
      }

      return clazz;
   }

}