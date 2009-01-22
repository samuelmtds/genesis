/*
 * The Genesis Project
 * Copyright (C) 2004-2009  Summa Technologies do Brasil Ltda.
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.naming.InitialContext;

import net.java.dev.genesis.ejb.CommandExecutorLocal;
import net.java.dev.genesis.ejb.CommandExecutorLocalHome;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.aspectwerkz.AspectContext;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;
import org.codehaus.aspectwerkz.joinpoint.MethodRtti;

/**
 * @Aspect("perJVM")
 */
public class LocalEJBCommandExecutionAspect extends CommandInvocationAspect {
   private static Log log = LogFactory.getLog(LocalEJBCommandExecutionAspect.class);
   private ThreadLocal threadLocal = new InheritableThreadLocal();

   private CommandExecutorLocalHome home;
   private CommandExecutorLocal session;

   public LocalEJBCommandExecutionAspect(final AspectContext ctx) {
      super(ctx);
   }

   private CommandExecutorLocalHome getHome() throws Exception {
      if (home == null) {
         home = (CommandExecutorLocalHome)new InitialContext().lookup(
             ctx.getParameter("jndiName")); // NOI18N
      }

      return home;
   }

   private CommandExecutorLocal getCommandExecutor() {
      if (session == null) {
         try {
            session = getHome().create();
         } catch (final Exception e) {
            home = null;
            log.error(e.getMessage(), e);

            throw new RuntimeException(e);
         }
      }

      return session;
   }

   /**
    * @Around("localEjbCommandExecution")
    */
   public Object commandExecution(final JoinPoint jp) throws Throwable {
      final MethodRtti rtti = (MethodRtti)jp.getRtti();

      Method m = (Method) threadLocal.get();
      if (m != null && m.equals(rtti.getMethod())) {
         Object value = jp.proceed();
         threadLocal.set(null);
         return value;
      }

      final CommandResolver obj = (CommandResolver)jp.getTarget();
      final Class[] classes = rtti.getParameterTypes();
      final String[] classNames = new String[classes.length];
      final String methodName = rtti.getMethod().getName();
      final Object[] parameterValues = rtti.getParameterValues();

      for (int i = 0; i < classes.length; i++) {
         classNames[i] = classes[i].getName();
      }

      CommandExecutorLocal commandExecutorLocal = null;

      try {
         commandExecutorLocal = getCommandExecutor();

         threadLocal.set(rtti.getMethod());

         if (obj.isTransactional(rtti.getMethod())) {
            return commandExecutorLocal.executeTransaction(obj, methodName,
                  classNames, parameterValues);
         } else if (obj.isRemotable(rtti.getMethod())) {
            return commandExecutorLocal.executeQuery(obj, methodName, 
                  classNames, parameterValues);
         } else {
            return jp.proceed();
         }
      } catch (final InvocationTargetException ite) {
         throw ite.getTargetException();
      } finally {
         threadLocal.set(null);
      }
   }
}