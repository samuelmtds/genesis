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

import net.java.dev.genesis.command.TransactionalInjector;
import net.java.dev.genesis.command.hibernate.HibernateCommand;
import net.sf.hibernate.FlushMode;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.cfg.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.aspectwerkz.AspectContext;

public class HibernateTransactionalInjector implements TransactionalInjector {
   private static final Log log =
         LogFactory.getLog(HibernateTransactionalInjector.class);
   private boolean isHibernateCommand;
   private Transaction transaction;
   private HibernateCommand hibernateCommand;
   private Session session;
   
   public void init(Object context) {
      if (!(context instanceof AspectContext) || "false".equals(
            ((AspectContext)context).getParameter("preLoadSessionFactory"))) {
         return;
      }
      
      try {
         HibernateHelper.getInstance().getSessionFactory();
      } catch (HibernateException he) {
         log.error("Error preloading SessionFactory", he);
      }
   }
   
   public void beforeInvocation(final Object target, final boolean transactional)
         throws HibernateException {
      isHibernateCommand = target instanceof HibernateCommand;
      
      if (!isHibernateCommand) {
         return;
      }
      
      hibernateCommand = (HibernateCommand)target;
      session = HibernateHelper.getInstance().createSession();
      
      transaction = null;
      
      hibernateCommand.setSession(session);
      
      if (transactional) {
         transaction = session.beginTransaction();
         session.setFlushMode(FlushMode.COMMIT);
      }
   }
   
   public void afterInvocation() throws HibernateException {
      if (transaction != null) {
         transaction.commit();
      }
   }
   
   public void onException(final Exception e) {
      if (transaction != null) {
         try {
            transaction.rollback();
         } catch (final Exception ex) {
            throw new RuntimeException(ex);
         }
      }
   }
   
   public void onFinally() throws HibernateException {
      if(hibernateCommand != null){
         hibernateCommand.setSession(null);
         hibernateCommand = null;
      }
      
      transaction = null;
      
      if(session != null){
         try {
            session.close();
         } finally {
            session = null;
         }
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
         factory = new Configuration().configure().buildSessionFactory();
      }
      
      return factory;
   }
   
   public Session createSession() throws HibernateException {
      return getSessionFactory().openSession();
   }
}