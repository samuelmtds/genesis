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

import net.java.dev.genesis.command.Query;
import net.java.dev.genesis.command.Transaction;
import java.lang.reflect.InvocationTargetException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

/**
 * @ejb.bean name="ejb/CommandExecutor" type="Stateless" view-type="both"
 *           jndi-name="ejb/CommandExecutor"
 * @ejb.home remote-class="net.java.dev.genesis.ejb.CommandExecutorHome"
 *           local-class="net.java.dev.genesis.ejb.CommandExecutorLocalHome"
 * @ejb.interface remote-class="net.java.dev.genesis.ejb.CommandExecutor"
 *           local-class="net.java.dev.genesis.ejb.CommandExecutorLocal"
 */
public class CommandExecutorEJB implements SessionBean {
   protected SessionContext ctx;

   public void ejbActivate() {}
   public void ejbPassivate() {}
   public void ejbCreate() {}
   public void ejbRemove() {}

   public void setSessionContext(SessionContext sessionContext) {
      this.ctx = sessionContext;
   }

   /**
    * @ejb.interface-method
    * @ejb.transaction type="Required"
    */
   public Object execute(Transaction t, String methodName, String[] classNames, 
                         Object[] args) 
         throws ClassNotFoundException, IllegalAccessException, 
                NoSuchMethodException, InvocationTargetException {
      Exception e = null;

      try {
         return execute((Object)t, methodName, classNames, args);
      } catch (ClassNotFoundException ex) {
         e = ex;
         throw ex;
      } catch (IllegalAccessException ex) {
         e = ex;
         throw ex;
      } catch (NoSuchMethodException ex) {
         e = ex;
         throw ex;
      } catch (InvocationTargetException ex) {
         e = ex;
         throw ex;
      } finally {
         if (e != null) {
            ctx.setRollbackOnly();
         }
      }
   }

   /**
    * @ejb.interface-method
    * @ejb.transaction type="Supports"
    */
   public Object execute(Query q, String methodName, String[] classNames, 
                         Object[] args) 
         throws ClassNotFoundException, IllegalAccessException, 
                NoSuchMethodException, InvocationTargetException {
      return execute((Object)q, methodName, classNames, args);
   }

   private Object execute(Object o, String methodName, String[] classNames, 
                          Object[] args)
         throws ClassNotFoundException, IllegalAccessException, 
                NoSuchMethodException, InvocationTargetException {
      return ReflectionInvoker.getInstance().invoke(o, methodName, classNames, args);
   }
}