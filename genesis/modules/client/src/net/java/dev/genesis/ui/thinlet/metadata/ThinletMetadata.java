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
package net.java.dev.genesis.ui.thinlet.metadata;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.java.dev.genesis.reflection.ReflectionInvoker;

public class ThinletMetadata {
   private final Class thinletClass;

   private final Map preActions;
   private final Map posActions;

   public ThinletMetadata(final Class formClass) {
      this.thinletClass = formClass;
      this.preActions = new HashMap();
      this.posActions = new HashMap();
   }

   public Class getThinletClass() {
      return thinletClass;
   }

   public void addPreAction(final String actionName, final String methodName) {
      preActions.put(actionName, methodName);
   }

   public void addPosAction(final String actionName, final String methodName) {
      posActions.put(actionName, methodName);
   }

   public boolean invokePreAction(final Object target, final String actionName)
         throws NoSuchMethodException, IllegalAccessException,
         ClassNotFoundException, InvocationTargetException {

      final String methodName = (String)preActions.get(actionName);

      if (methodName == null) {
         return true;
      }

      final Object result = ReflectionInvoker.getInstance().invoke(target,
            methodName);

      return !Boolean.FALSE.equals(result);
   }

   public void invokePosAction(final Object target, final String actionName)
         throws NoSuchMethodException, IllegalAccessException,
         ClassNotFoundException, InvocationTargetException {

      final String methodName = (String)posActions.get(actionName);

      if (methodName == null) {
         return;
      }

      ReflectionInvoker.getInstance().invoke(target, methodName);
   }
}