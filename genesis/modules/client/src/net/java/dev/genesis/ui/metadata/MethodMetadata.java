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
package net.java.dev.genesis.ui.metadata;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.java.dev.genesis.reflection.MethodEntry;
import net.java.dev.genesis.reflection.ReflectionInvoker;

import org.apache.commons.jxpath.CompiledExpression;

public abstract class MethodMetadata extends MemberMetadata {
   private final static Object[] emptyObjectArray = new Object[0];
   private final static String[] emptyStringArray = new String[0];

   private final MethodEntry methodEntry;
   private CompiledExpression callCondition;

   public MethodMetadata(Method method) {
      this.methodEntry = new MethodEntry(method);
   }

   public MethodEntry getMethodEntry() {
      return methodEntry;
   }

   public CompiledExpression getCallCondition() {
      return callCondition;
   }

   public void setCallCondition(CompiledExpression callWhen) {
      this.callCondition = callWhen;
   }

   public Object invoke(final Object target) throws NoSuchMethodException,
         IllegalAccessException, ClassNotFoundException,
         InvocationTargetException {
      return ReflectionInvoker.getInstance().invoke(target,
            methodEntry.getMethodName(), emptyStringArray, emptyObjectArray);
   }
}