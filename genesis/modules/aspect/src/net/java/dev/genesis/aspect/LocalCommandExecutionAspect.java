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
package net.java.dev.genesis.aspect;

import net.java.dev.genesis.command.TransactionalInjector;

import org.codehaus.aspectwerkz.CrossCuttingInfo;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;
import org.codehaus.aspectwerkz.joinpoint.MethodSignature;

/**
 * @Aspect perJVM
 */
public class LocalCommandExecutionAspect extends CommandInvocationAspect {
   private final TransactionalInjector injector;

   public LocalCommandExecutionAspect(final CrossCuttingInfo ccInfo) 
                                                            throws Exception {
      super(ccInfo);

      injector = (ccInfo.isPrototype()) ? null : (TransactionalInjector)
            Class.forName(ccInfo.getParameter("transactionalInjector"), true, 
            Thread.currentThread().getContextClassLoader()).newInstance();
   }
    
   /**
    * @Around localCommandExecution
    */
   public Object commandExecution(final JoinPoint joinPoint) throws Throwable {
      final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
      final CommandResolver obj = (CommandResolver)joinPoint.getTarget();
      final boolean transactional = obj.isTransaction(methodSignature.getMethod());

      try {
         injector.beforeInvocation(obj, transactional);
         final Object ret = joinPoint.proceed();
         injector.afterInvocation();

         return ret;
      } catch (final Exception e) {
         injector.onException(e);

         throw e;
      } finally{
         injector.onFinally();
      }
   }
}