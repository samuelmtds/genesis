/*
 * The Genesis Project
 * Copyright (C) 2007  Summa Technologies do Brasil Ltda.
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

import net.java.dev.genesis.command.TransactionalInjector;
import net.java.dev.genesis.command.hibernate.HibernateCommand;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

import javax.ejb.EJBException;
import javax.ejb.SessionContext;


public class EJBHibernateCfgTransactionalInjector
   implements TransactionalInjector {
   private SessionContext ctx;
   private SessionFactory sessionFactory;
   private boolean transactional;
   private Session session;

   public void init(Object context) {
      this.ctx = (SessionContext) context;

      try {
         sessionFactory = HibernateHelper.getInstance().getSessionFactory();
      } catch (HibernateException he) {
         throw new EJBException(he);
      }
   }

   public void beforeInvocation(final Object target, final boolean transactional)
      throws HibernateException {
      this.transactional = transactional;

      if (!(target instanceof HibernateCommand)) {
         return;
      }

      session = sessionFactory.openSession();
      ((HibernateCommand) target).setSession(session);
   }

   public void afterInvocation() throws HibernateException {
      if ((session == null) || !transactional) {
         return;
      }

      session.flush();
   }

   public void onException(final Exception e) {
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


final class HibernateHelper {
   private static final HibernateHelper instance = new HibernateHelper();
   private SessionFactory factory;

   private HibernateHelper() {
   }

   public static HibernateHelper getInstance() {
      return instance;
   }

   public SessionFactory getSessionFactory() throws HibernateException {
      if (factory == null) {
         factory = new Configuration().configure("/remote-hibernate.cfg.xml").buildSessionFactory();
      }

      return factory;
   }

   public Session createSession() throws HibernateException {
      return getSessionFactory().openSession();
   }
}
