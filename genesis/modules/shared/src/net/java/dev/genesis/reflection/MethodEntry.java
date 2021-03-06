/*
 * The Genesis Project
 * Copyright (C) 2004-2008  Summa Technologies do Brasil Ltda.
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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodEntry implements Serializable {
   private final String methodName;
   private final String[] argsClassesNames;

   public MethodEntry(final String methodName, final String[] argsClassesNames) {
      this.methodName = methodName;
      this.argsClassesNames = argsClassesNames;
   }
   
   public MethodEntry(final Method method){
      final Class[] parameterTypes = method.getParameterTypes();
      final String[] classNames = new String[parameterTypes.length];

      for(int i = 0; i < parameterTypes.length; i++){
          classNames[i] = parameterTypes[i].getName();
      }
      this.methodName = method.getName();
      this.argsClassesNames = classNames;
   }

   public String[] getArgsClassesNames() {
      return argsClassesNames;
   }

   public String getMethodName() {
      return methodName;
   }

   public boolean equals(Object o) {
      MethodEntry that = (MethodEntry) o;

      return that.methodName.equals(this.methodName)
            && Arrays.equals(that.argsClassesNames, this.argsClassesNames);
   }

   public int hashCode() {
      return methodName.hashCode();
   }
   
   public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append(methodName);
      buffer.append("("); // NOI18N
      for (int i = 0; i < argsClassesNames.length; i++) {
         if(i > 0){
            buffer.append(","); // NOI18N
         }
         buffer.append(argsClassesNames[i]);
      }
      buffer.append(")"); // NOI18N
      return buffer.toString();
   }
}
