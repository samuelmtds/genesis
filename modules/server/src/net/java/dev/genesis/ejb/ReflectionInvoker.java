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
package net.java.dev.genesis.ejb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class ReflectionInvoker {
   private static final ReflectionInvoker instance = new ReflectionInvoker();
   
   private Map classesCache = null;
   private Map reflectionCache = null;
   
   private ReflectionInvoker() {
      this.classesCache = new HashMap();
      this.reflectionCache = new HashMap();
      initializePrimitiveMap();
   }
   
   private void initializePrimitiveMap(){
      classesCache.put(Byte.TYPE.getName(), Byte.TYPE);
      classesCache.put(Character.TYPE.getName(), Character.TYPE);
      classesCache.put(Short.TYPE.getName(), Short.TYPE);
      classesCache.put(Integer.TYPE.getName(), Integer.TYPE);
      classesCache.put(Long.TYPE.getName(), Long.TYPE);
      classesCache.put(Float.TYPE.getName(), Float.TYPE);
      classesCache.put(Double.TYPE.getName(), Double.TYPE);
      classesCache.put(Boolean.TYPE.getName(), Boolean.TYPE);
      classesCache.put(Void.TYPE.getName(), Void.TYPE);
   }
   
   public static ReflectionInvoker getInstance() {
      return instance;
   }
   
   private Class getClazz(String className) throws ClassNotFoundException{
      Class clazz = (Class)classesCache.get(className);
      
      if (clazz == null) {
         clazz = Class.forName(className);
         classesCache.put(className, clazz);
      }
      
      return clazz;
   }

   public Object invoke(final Object obj, final String methodName, 
                        final String[] classNames, final Object[] args)
                throws ClassNotFoundException, IllegalAccessException, 
                NoSuchMethodException, InvocationTargetException  {
      Map methodsEntries = (Map)reflectionCache.get(obj.getClass());
      final MethodEntry entry = new MethodEntry(methodName,classNames);

      if (methodsEntries == null){
         methodsEntries = new HashMap();
         reflectionCache.put(obj.getClass(), methodsEntries);
      }

      Method method = (Method) methodsEntries.get(entry);
      
      if (method == null){
         final Class[] classes = new Class[classNames.length];
         
         for (int i = 0; i < classes.length; i++) {
            classes[i] = getClazz(classNames[i]);
         }

         method = obj.getClass().getMethod(methodName, classes);
         methodsEntries.put(entry,method);
      }

      return method.invoke(obj, args);
   }

   public final static class MethodEntry {
      private final String methodName;
      private final String[] argsClassesNames;
      
      public MethodEntry(final String methodName, final String[] argsClassesNames){
         this.methodName = methodName;
         this.argsClassesNames = argsClassesNames;
      }
      
      public boolean equals(Object o) {
         MethodEntry that = (MethodEntry) o;
         
         return that.methodName.equals(this.methodName) &&
                Arrays.equals(that.argsClassesNames,this.argsClassesNames);
      }
      
      public int hashCode(){
         return methodName.hashCode();
      }
   }
}