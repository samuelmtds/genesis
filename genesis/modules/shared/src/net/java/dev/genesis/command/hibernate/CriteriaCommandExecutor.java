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
package net.java.dev.genesis.command.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import net.java.dev.genesis.reflection.ClassesCache;
import net.java.dev.genesis.reflection.ReflectionInvoker;
import net.sf.hibernate.Criteria;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Order;

import org.apache.commons.beanutils.PropertyUtils;

public class CriteriaCommandExecutor extends AbstractHibernateCommand {
   private final Object realCommand;
   private final String methodName;
   private final String[] classNames;
   private final Object[] args;
   private final String persisterClassName;
   private final String[] orderBy;
   private final boolean[] isAsc;

   private Map propertiesMap;

   public CriteriaCommandExecutor(Object realCommand, String methodName,
         String[] classNames, Object[] args, String persisterClassName,
         String[] orderBy, boolean[] isAsc,
         Map propertiesMap) {
      this.realCommand = realCommand;
      this.methodName = methodName;
      this.classNames = classNames;
      this.args = args;
      this.persisterClassName = persisterClassName;
      this.orderBy = orderBy;
      this.isAsc = isAsc;
      this.propertiesMap = propertiesMap;
   }

   /**
    * @Remotable
    */
   public Object execute() throws Throwable {
      final HibernateCriteria hibCriteria = (HibernateCriteria) realCommand;

      if (persisterClassName != null && persisterClassName.trim().length() != 0) {
         final Criteria crit = getSession().createCriteria(
               ClassesCache.getClass(persisterClassName));
         
         hibCriteria.setCriteria(crit);
         
         if (orderBy != null) {
            for (int i = 0; i < orderBy.length; i++) {
               crit.addOrder(isAsc[i] ? Order.asc(orderBy[i]) : Order.desc(orderBy[i]));
            }
         }
         
      } else {
         hibCriteria.setCriteria(null);
      }

      PropertyUtils.copyProperties(hibCriteria, propertiesMap);

      try {
         return ReflectionInvoker.getInstance().invoke(realCommand, methodName,
               classNames, args);
      } catch (InvocationTargetException ite) {
         throw ite.getTargetException();
      }
   }

   public void setSession(Session session) {
      super.setSession(session);

      if (realCommand instanceof HibernateCommand) {
         ((HibernateCommand)realCommand).setSession(session);
      }
   }
}