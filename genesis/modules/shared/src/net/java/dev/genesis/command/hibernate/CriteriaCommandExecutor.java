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
import net.sf.hibernate.Session;
import org.apache.commons.beanutils.PropertyUtils;

public class CriteriaCommandExecutor extends AbstractHibernateCommand {
   private Object realCommand;
   private String methodName;
   private String[] classNames;
   private Object[] args;
   private String persisterClassName;

   private Map propertiesMap;

   public CriteriaCommandExecutor(Object realCommand, String methodName,
         String[] classNames, Object[] args, String persisterClassName,
         Map propertiesMap) {
      this.realCommand = realCommand;
      this.methodName = methodName;
      this.classNames = classNames;
      this.args = args;
      this.persisterClassName = persisterClassName;
      this.propertiesMap = propertiesMap;
   }

   /**
    * @Remotable
    */
   public Object execute() throws Throwable {
      final HibernateCriteria hibCriteria = (HibernateCriteria) realCommand;

      if (persisterClassName != null && persisterClassName.trim().length() != 0) {
         hibCriteria.setCriteria(getSession().createCriteria(
               ClassesCache.getClass(persisterClassName)));
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