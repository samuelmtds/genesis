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
package net.java.dev.genesis.tests.hibernate;

import java.util.List;

import net.java.dev.genesis.command.hibernate.AbstractHibernateCommand;
import net.java.dev.genesis.tests.TestCase;
import net.sf.hibernate.ObjectNotFoundException;
import net.sf.hibernate.Query;

public class EJBHibernateTransactionalInjectorTest extends TestCase {
   public void tearDown() throws Exception {
      new HibernateCommand().deleteAll();
   }

   public void testSuccessfullyQuery() throws Exception {
      final HibernateCommand command = new HibernateCommand();
      command.insertBean("first bean");
      command.insertBean("seconde bean");
      final List list = command.getBeans();
      assertNotNull(list);
      assertEquals(2, list.size());
   }

   public void testUnsuccessfullyQuery() throws Exception {
      Exception ex = null;
      try {
         new HibernateCommand().getNonExistentBean(HibernateBean.class);
      } catch (Exception e) {
         ex = e;
      }
      assertNotNull(ex);
      assertTrue(ex instanceof ObjectNotFoundException);
   }

   public void testSuccessfullyTransaction() throws Exception {
      final HibernateCommand command = new HibernateCommand();
      command.insertBean("insertedBean");
      final HibernateBean bean = command.getBean("insertedBean");
      assertNotNull(bean);
      assertEquals("insertedBean", bean.getCode());
   }

   public void testUnsuccessfullyTransaction() throws Exception {
      Exception ex = null;
      final HibernateCommand command = new HibernateCommand();
      try {
         command.insertBeans();
      } catch (Exception e) {
         ex = e;
      }
      assertNotNull(ex);
      assertTrue(ex instanceof RuntimeException);
      assertEquals("Unit Test Exception", ex.getMessage());

      final List list = command.getBeans();
      assertTrue(list.isEmpty());
   }

   public void testExceptionPropagation() {
      Exception ex = null;
      final HibernateCommand command = new HibernateCommand();
      try {
         command.someException();
      } catch (Exception e) {
         ex = e;
      }
      assertNotNull(ex);
      assertTrue(ex instanceof UnsupportedOperationException);
      assertEquals("Unit Test Exception", ex.getMessage());
   }

   public static class HibernateCommand extends AbstractHibernateCommand {

      /**
       * @Remotable
       */
      public HibernateBean getBean(final String code) throws Exception {
         final Query query = getSession().createQuery(
               "from HibernateBean bean where bean.code = :code");
         query.setParameter("code", code);
         return (HibernateBean) query.uniqueResult();
      }

      /**
       * @Remotable
       */
      public List getBeans() throws Exception {
         final Query query = getSession().createQuery("from HibernateBean");
         return query.list();
      }

      /**
       * @Remotable
       */
      public HibernateBean getNonExistentBean(Class clazz) throws Exception {
         return (HibernateBean) getSession().load(clazz,
               new Long(Long.MIN_VALUE));
      }

      /**
       * @Remotable
       */
      public void someException() throws Exception {
         throw new UnsupportedOperationException("Unit Test Exception");
      }

      /**
       * @Transactional
       */
      public void insertBean(final String code) throws Exception {
         final HibernateBean bean = new HibernateBean();
         bean.setCode(code);
         getSession().save(bean);
      }
      
      /**
       * @Transactional
       */
      public void insertBeans() throws Exception {
         final HibernateBean bean = new HibernateBean();
         final HibernateBean bean2 = new HibernateBean();
         final HibernateBean bean3 = new HibernateBean();

         bean.setCode("first bean");
         bean2.setCode("second bean");
         bean3.setCode("third bean");

         getSession().save(bean);
         getSession().save(bean2);
         getSession().save(bean3);
         getSession().flush();

         throw new RuntimeException("Unit Test Exception");
      }

      /**
       * @Transactional
       */
      public int deleteAll() throws Exception {
         return getSession().delete("from HibernateBean");
      }
   }
}