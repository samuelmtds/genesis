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

import net.java.dev.genesis.command.Query;
import net.java.dev.genesis.command.Transaction;
import net.java.dev.genesis.ejb.CommandExecutor;
import net.java.dev.genesis.ejb.CommandExecutorHome;

import java.lang.reflect.InvocationTargetException;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import org.codehaus.aspectwerkz.xmldef.advice.AroundAdvice;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;
import org.codehaus.aspectwerkz.joinpoint.MethodJoinPoint;

public class CommandExecutionAdvice extends AroundAdvice {
   private CommandExecutor getCommandExecutor() {
      try {
         final CommandExecutorHome home = (CommandExecutorHome)PortableRemoteObject
               .narrow(new InitialContext().lookup(getParameter("jndiName")), 
               CommandExecutorHome.class);
         return home.create();
      } catch (final Exception e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
   }

    public Object execute(final JoinPoint joinPoint) throws Throwable {
        final MethodJoinPoint jp = (MethodJoinPoint)joinPoint;

        final Class[] classes = jp.getParameterTypes();
        final String[] classNames = new String[classes.length];
        
        for (int i = 0; i < classes.length; i++) {
           classNames[i] = classes[i].getName();
        }

        final Object o = jp.getTargetInstance();
        Object ret;

        if (o instanceof Transaction) {
           try {
              ret = execute((Transaction)o, jp.getMethodName(), classNames, 
                             jp.getParameters());
           } catch (InvocationTargetException ite) {
              throw ite.getTargetException();
           }
        } else if (o instanceof Query) {
           try {
              ret = execute((Query)o, jp.getMethodName(), classNames, 
                             jp.getParameters());
           } catch (InvocationTargetException ite) {
              throw ite.getTargetException();
           }
        } else {
           throw new RuntimeException("Class does not implement neither " + 
               "Transaction nor Query: " + o.getClass().getName());
        }

        return ret;
    }

   protected Object execute(Transaction t, String methodName, 
                            String[] classNames, Object[] parameters) 
                                                            throws Throwable {

      final CommandExecutor commandExecutor = getCommandExecutor();
      
      try {
         return commandExecutor.execute(t, methodName, classNames, parameters);
      } finally {
         commandExecutor.remove();
      }
   }

   protected Object execute(Query q, String methodName, 
                            String[] classNames, Object[] parameters) 
                                                            throws Throwable {

      final CommandExecutor commandExecutor = getCommandExecutor();
      
      try {
         return commandExecutor.execute(q, methodName, classNames, parameters);
      } finally {
         commandExecutor.remove();
      }
   }
}