/*
 * The Genesis Project
 * Copyright (C) 2004-2008  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ejb.hibernate;

import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.java.dev.genesis.command.hibernate.HibernateCommand;
import net.java.dev.genesis.command.TransactionalInjector;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

public class EJBHibernateTransactionalInjector implements TransactionalInjector {
   private SessionContext ctx;
   private SessionFactory sessionFactory;
   private boolean transactional;
   private Session session;

   public void init(Object context) {
      this.ctx = (SessionContext)context;

      try {
         final InitialContext ctx = new InitialContext();

         sessionFactory = (SessionFactory) ctx.lookup((String) ctx
               .lookup("java:comp/env/HibernateFactoryAddress")); // NOI18N
      } catch (NamingException ne) {
         throw new EJBException(ne);
      }
   }

   public void beforeInvocation(Object target, boolean transactional) 
                                                   throws HibernateException {
      this.transactional = transactional;

      if (!(target instanceof HibernateCommand)) {
         return;
      }
      
      session = sessionFactory.openSession();
      ((HibernateCommand)target).setSession(session);
   }

   public void afterInvocation() throws HibernateException {
      if (session == null || !transactional) {
         return;
      }

      session.flush();
   }
   
   public void onException(Exception e) {
      if (transactional) {
         ctx.setRollbackOnly();
      }
   }
   
   public void onFinally() throws HibernateException {
      if (session == null) {
         return;
      }

      try {
         session.close();
      } finally {
         session = null;
      }
   }
}