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

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import net.java.dev.genesis.ejb.CommandExecutor;
import net.java.dev.genesis.ejb.CommandExecutorHome;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.aspectwerkz.CrossCuttingInfo;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;
import org.codehaus.aspectwerkz.joinpoint.MethodRtti;
import org.codehaus.aspectwerkz.joinpoint.impl.MethodRttiImpl;

/**
 * @Aspect perJVM
 */
public class EJBCommandExecutionAspect extends CommandInvocationAspect {
   private static Log log = LogFactory.getLog(EJBCommandExecutionAspect.class);

   private CommandExecutorHome home;
   private CommandExecutor session;

   public EJBCommandExecutionAspect(CrossCuttingInfo ccInfo) {
      super(ccInfo);
   }

   private CommandExecutorHome getHome() throws Exception {
      if (home != null) {
         return home;
      }
      home = (CommandExecutorHome)PortableRemoteObject.narrow(
            new InitialContext().lookup(ccInfo.getParameter("jndiName")),
            CommandExecutorHome.class);
      return home;
   }

   private CommandExecutor getCommandExecutor() {
      if (session != null) {
         return session;
      }
      try {
         session = getHome().create();
         return session;
      } catch (final Exception e) {
         home = null;
         log.error(e.getMessage(), e);
         throw new RuntimeException(e);
      }
   }

   /**
    * 
    * @param jp
    *            the join point
    * @return the resulting object
    * @throws Throwable
    * 
    * @Around ejbCommandExecution
    */
   public Object commandExecution(final JoinPoint jp) throws Throwable {

      final MethodRtti rtti = (MethodRtti)jp.getRtti();
      final CommandResolver obj = (CommandResolver)jp.getTarget();
      final Class[] classes = rtti.getParameterTypes();
      final String[] classNames = new String[classes.length];
      final String methodName = ((MethodRttiImpl)rtti).getMethodTuple()
            .getWrapperMethod().getName();
      final Object[] parameterValues = rtti.getParameterValues();

      for (int i = 0; i < classes.length; i++) {
         classNames[i] = classes[i].getName();
      }

      CommandExecutor commandExecutor = null;

      try {
         commandExecutor = getCommandExecutor();
         if (obj.isTransaction(rtti.getMethod())) {
            return commandExecutor.executeTransaction(obj, methodName,
                  classNames, parameterValues);
         }
         return commandExecutor.executeQuery(obj, methodName, classNames,
               parameterValues);
      } catch (final RemoteException re) {
         log.error("RemoteException occured", re);
         session = null;
         throw re;
      } catch (final InvocationTargetException ite) {
         throw ite.getTargetException();
      } finally {
         if (commandExecutor != null) {
            commandExecutor.remove();
         }
      }

   }
}