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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class ReflectionInvoker {
   private static final ReflectionInvoker instance = new ReflectionInvoker();
   private Map reflectionCache = null;

   private ReflectionInvoker() {
      this.reflectionCache = new HashMap();
   }

   public static ReflectionInvoker getInstance() {
      return instance;
   }

   public Object invoke(final Object obj, final String methodName,
         final String[] classNames, final Object[] args)
         throws ClassNotFoundException, IllegalAccessException,
         NoSuchMethodException, InvocationTargetException {
      return getMethod(obj, methodName, classNames).invoke(obj, args);
   }

   public Method getMethod(final Object obj, final String methodName,
         final String[] classNames) throws ClassNotFoundException, 
         NoSuchMethodException {
      return getMethod(obj.getClass(), methodName, classNames);
   }

   public Method getMethod(final Class clazz,final String methodName,
         final String[] classNames) throws ClassNotFoundException, 
         NoSuchMethodException {
      Map methodsEntries = (Map) reflectionCache.get(clazz);
      final MethodEntry entry = new MethodEntry(methodName, classNames);

      if (methodsEntries == null) {
         methodsEntries = new HashMap();
         reflectionCache.put(clazz, methodsEntries);
      }

      Method method = (Method) methodsEntries.get(entry);

      if (method == null) {
         final Class[] classes = new Class[classNames.length];

         for (int i = 0; i < classes.length; i++) {
            classes[i] = ClassesCache.getClass(classNames[i]);
         }

         method = clazz.getMethod(methodName, classes);
         methodsEntries.put(entry, method);
      }

      return method;
   }
}