/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.aspect;

import net.java.dev.genesis.script.ScriptableObject;
import net.java.dev.genesis.script.ScriptableObjectImpl;

import org.codehaus.aspectwerkz.joinpoint.FieldSignature;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;

/**
 * @Aspect("perJVM")
 */
public class ScriptableObjectAspect {
   /**
    * @Around("scriptableObjectGetter")
    */
   public Object interceptGetter(JoinPoint jp) throws Throwable {
      ScriptableObject obj = (ScriptableObject) jp.getTarget();
      final Object result = jp.proceed();

      if (obj != null) {
         FieldSignature sig = (FieldSignature) jp.getSignature();
         obj.visitField(sig.getName(), result, sig.getFieldType().isPrimitive());
      }

      return result;
   }

   /**
    * @Mixin(pointcut="scriptableObjectMixin", isTransient=true,
    *        deploymentModel="perInstance")
    */
   public static class ScriptableObjectMixin extends ScriptableObjectImpl
         implements ScriptableObject {
   }
}
