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
package net.java.dev.genesis.helpers;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import net.java.dev.genesis.reflection.ReflectionInvoker;
import net.java.dev.genesis.util.Bundle;


public final class EnumHelper {
   private static final EnumHelper instance = new EnumHelper();

   private Class enumClass;

   private EnumHelper() {
      try {
         enumClass = Class.forName("java.lang.Enum"); // NOI18N
      } catch (Exception e) {
         // not JDK 5 or later
      }
   }

   private Object invoke(Object target, String methodName) {
      try {
         return ReflectionInvoker.getInstance().invoke(target, methodName);
      } catch (InvocationTargetException e) {
         throw new RuntimeException(e.getTargetException());
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public static EnumHelper getInstance() {
      return instance;
   }

   public Class getEnumClass() {
      return enumClass;
   }

   public boolean supportsEnum() {
      return enumClass != null;
   }
   
   public boolean isReusableComponentEnum(Class clazz) {
      return net.java.dev.reusablecomponents.lang.Enum.class.isAssignableFrom(clazz);
   }
   
   public boolean isJava5Enum(Class clazz) {
      return supportsEnum() && Boolean.TRUE.equals(invoke(clazz, "isEnum")); // NOI18N
   }
   
   public boolean isReusableComponentEnum(Object object) {
      return object instanceof net.java.dev.reusablecomponents.lang.Enum;
   }
   
   public boolean isJava5Enum(Object object) {
      return object != null && isJava5Enum(object.getClass());
   }
   
   public boolean isEnum(Object object) {
      return isReusableComponentEnum(object) || isJava5Enum(object);
   }
   
   public Collection values(Class clazz) {
      if (isReusableComponentEnum(clazz)) {
         return net.java.dev.reusablecomponents.lang.Enum.getInstances(clazz);
      } else if (isJava5Enum(clazz)) {
         return Arrays.asList((Object[])invoke(clazz, "getEnumConstants")); // NOI18N
      }

      throw new IllegalArgumentException(Bundle.getMessage(EnumHelper.class,
            "X_IS_NOT_AN_ENUM", clazz.getName())); // NOI18N
   }
   
   public Object valueOf(Class clazz, String name) {
      if (isReusableComponentEnum(clazz)) {
         return net.java.dev.reusablecomponents.lang.Enum.get(clazz, name);
      } else if (isJava5Enum(clazz)) {
         Object[] enums = (Object[]) invoke(clazz, "getEnumConstants"); // NOI18N
         for (int i = 0; i < enums.length; i++) {
            String enumName = getName(enums[i]);
            
            if (name.equals(enumName)) {
               return enums[i];
            }
         }

         return null;
      }

      throw new IllegalArgumentException(Bundle.getMessage(EnumHelper.class,
            "X_IS_NOT_AN_ENUM", clazz.getName())); // NOI18N
   }
   
   public Class getDeclaringClass(Object object) {
      if (isReusableComponentEnum(object)) {
         return ((net.java.dev.reusablecomponents.lang.Enum)object).getBaseClass();
      } else if (isJava5Enum(object)) {
         return (Class) invoke(object, "getDeclaringClass"); // NOI18N
      }

      throw new IllegalArgumentException(Bundle.getMessage(EnumHelper.class,
            "X_IS_NOT_AN_ENUM", object)); // NOI18N
   }
   
   public String getName(Object object) {
      if (isReusableComponentEnum(object)) {
         return ((net.java.dev.reusablecomponents.lang.Enum)object).getName();
      } else if (isJava5Enum(object)) {
         return object.toString();
      }

      throw new IllegalArgumentException(Bundle.getMessage(EnumHelper.class,
            "X_IS_NOT_AN_ENUM", object)); // NOI18N
   }
}
