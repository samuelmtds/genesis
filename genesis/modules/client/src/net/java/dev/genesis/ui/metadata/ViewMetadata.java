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
package net.java.dev.genesis.ui.metadata;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.java.dev.genesis.reflection.ReflectionInvoker;

public class ViewMetadata {
   private final Class viewClass;

   private final Map beforeActions;
   private final Map afterActions;

   public ViewMetadata(final Class viewClass) {
      this.viewClass = viewClass;
      this.beforeActions = new HashMap();
      this.afterActions = new HashMap();
   }

   public Class getViewClass() {
      return viewClass;
   }

   public void addBeforeAction(final String actionName, final String methodName) {
      beforeActions.put(actionName, methodName);
   }

   public void addAfterAction(final String actionName, final String methodName) {
      afterActions.put(actionName, methodName);
   }

   public boolean invokeBeforeAction(final Object target, final String actionName)
         throws NoSuchMethodException, IllegalAccessException,
         ClassNotFoundException, InvocationTargetException {

      final String methodName = (String)beforeActions.get(actionName);

      if (methodName == null) {
         return true;
      }

      final Object result = ReflectionInvoker.getInstance().invoke(target,
            methodName);

      return !Boolean.FALSE.equals(result);
   }

   public void invokeAfterAction(final Object target, final String actionName)
         throws NoSuchMethodException, IllegalAccessException,
         ClassNotFoundException, InvocationTargetException {

      final String methodName = (String)afterActions.get(actionName);

      if (methodName == null) {
         return;
      }

      ReflectionInvoker.getInstance().invoke(target, methodName);
   }
}