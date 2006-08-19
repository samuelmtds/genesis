/*
 * The Genesis Project
 * Copyright (C) 2004-2005  Summa Technologies do Brasil Ltda.
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

import java.util.Map;
import java.util.WeakHashMap;

import net.java.dev.genesis.command.TransactionalInjector;

import org.codehaus.aspectwerkz.AspectContext;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;
import org.codehaus.aspectwerkz.joinpoint.MethodSignature;

/**
 * @Aspect("perJVM")
 */
public class LocalCommandExecutionAspect extends CommandInvocationAspect {
   private final Map injectorsPerThread = new WeakHashMap();
   
   public LocalCommandExecutionAspect(final AspectContext ctx) throws Exception {
      super(ctx);
   }
   
   private TransactionalInjector getTransactionalInjector() throws Exception {
      Thread currentThread = Thread.currentThread();
      TransactionalInjector injector = (TransactionalInjector)injectorsPerThread
            .get(currentThread);

      if (injector == null) {
         injector = (TransactionalInjector)Class.forName(
               ctx.getParameter("transactionalInjector"), true,
               currentThread.getContextClassLoader()).newInstance();

         if (!ctx.isPrototype()) {
            injector.init(ctx);
         }

         injectorsPerThread.put(currentThread, injector);
      }

      return injector;
   }
    
   /**
    * @Around("localCommandExecution")
    */
   public Object commandExecution(final JoinPoint joinPoint) throws Throwable {
      final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
      final CommandResolver obj = (CommandResolver)joinPoint.getTarget();
      final boolean transactional = obj.isTransactional(methodSignature.getMethod());

      if (!transactional && !obj.isRemotable(methodSignature.getMethod())) {
         return joinPoint.proceed();
      }

      TransactionalInjector injector = getTransactionalInjector();

      try {
         injector.beforeInvocation(obj, transactional);
         final Object ret = joinPoint.proceed();
         injector.afterInvocation();

         return ret;
      } catch (final Exception e) {
         injector.onException(e);

         throw e;
      } finally {
         injector.onFinally();
      }
   }
}