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
package net.java.dev.genesis.ejb;

import java.lang.reflect.InvocationTargetException;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.java.dev.genesis.command.TransactionalInjector;
import net.java.dev.genesis.reflection.ReflectionInvoker;

/**
 * @ejb.bean name="ejb/CommandExecutor" type="Stateless" view-type="both"
 *           jndi-name="ejb/CommandExecutor"
 * @ejb.home remote-class="net.java.dev.genesis.ejb.CommandExecutorHome"
 *           local-class="net.java.dev.genesis.ejb.CommandExecutorLocalHome"
 * @ejb.interface remote-class="net.java.dev.genesis.ejb.CommandExecutor"
 *           local-class="net.java.dev.genesis.ejb.CommandExecutorLocal"
 * @ejb.env-entry name="HibernateFactoryAddress" type="java.lang.String" 
 *                value="jboss:/hibernate/SessionFactory"
 * @ejb.env-entry name="TransactionalInjector" type="java.lang.String" 
 *                value="net.java.dev.genesis.ejb.hibernate.EJBHibernateTransactionalInjector"
 */
public class CommandExecutorEJB implements SessionBean {
   private TransactionalInjector injector;
   
   public void ejbActivate() {}
   public void ejbPassivate() {}
   public void ejbCreate() {}
   public void ejbRemove() {}
   
   public void setSessionContext(SessionContext sessionContext) {
      try {
         final InitialContext ctx = new InitialContext();
         final String className = (String)ctx.lookup(
               "java:comp/env/TransactionalInjector");
         injector = (TransactionalInjector)Class.forName(className, true, 
               Thread.currentThread().getContextClassLoader()).newInstance();
      } catch (final NamingException ne) {
         throw new EJBException(ne);
      } catch (final ClassNotFoundException cnfe) {
         throw new EJBException(cnfe);
      } catch (final InstantiationException ie) {
         throw new EJBException(ie);
      } catch (final IllegalAccessException iae) {
         throw new EJBException(iae);
      }

      injector.init(sessionContext);
   }

   /**
    * @ejb.interface-method
    * @ejb.transaction type="Required"
    */
   public Object executeTransaction(Object o, String methodName,
         String[] classNames, Object[] args) throws ClassNotFoundException,
                              IllegalAccessException, NoSuchMethodException,
                              InvocationTargetException {
       return execute(o, methodName, classNames, args, true);
   }

   /**
    * @ejb.interface-method
    * @ejb.transaction type="Supports"
    */
   public Object executeQuery(Object o, String methodName,
         String[] classNames, Object[] args) throws ClassNotFoundException,
                              IllegalAccessException, NoSuchMethodException,
                              InvocationTargetException {
      return execute(o, methodName, classNames, args, false);
   }
   
   private Object execute(Object o, String methodName, String[] classNames,
         Object[] args, boolean transactional) throws ClassNotFoundException,
                           IllegalAccessException, NoSuchMethodException,
                           InvocationTargetException {
      Exception e = null;

      try {
         injector.beforeInvocation(o, transactional);

         final Object ret =  ReflectionInvoker.getInstance().invoke(o, methodName,
               classNames, args);
         
         injector.afterInvocation();

         return ret;
      } catch (final ClassNotFoundException cnfe) {
         e = cnfe;
         throw cnfe;
      } catch (final IllegalAccessException iae) {
         e = iae;
         throw iae;
      } catch (final NoSuchMethodException nsme) {
         e = nsme;
         throw nsme;
      } catch (final InvocationTargetException ite) {
         e = ite;
         throw ite;
      } catch (final Exception ex) {
         e = ex;
         throw new InvocationTargetException(ex);
      } finally {
         if (e != null) {
            injector.onException(e);
         }

         try {
            injector.onFinally();
         } catch (final Exception ex) {
            throw new InvocationTargetException(combine(ex, e));
         }
      }
   }

   private Throwable combine(final Throwable t, final Exception e) {
      Throwable last = t;

      while (last.getCause() != null) {
         last = last.getCause();
      }

      last.initCause(e);

      return t;
   }
}