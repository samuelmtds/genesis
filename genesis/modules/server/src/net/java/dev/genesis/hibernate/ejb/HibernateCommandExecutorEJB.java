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
package net.java.dev.genesis.hibernate.ejb;

import java.lang.reflect.InvocationTargetException;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.java.dev.genesis.command.hibernate.HibernateCommand;
import net.java.dev.genesis.ejb.CommandExecutorEJB;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

/**
 * @ejb.bean name="ejb/HibernateCommandExecutor" type="Stateless" 
 *           view-type="both" jndi-name="ejb/HibernateCommandExecutor"
 * @ejb.home remote-class="net.java.dev.genesis.ejb.CommandExecutorHome"
 *           local-class="net.java.dev.genesis.ejb.CommandExecutorLocalHome" 
 *           generate="false"
 * @ejb.interface remote-class="net.java.dev.genesis.ejb.CommandExecutor"
 *           local-class="net.java.dev.genesis.ejb.CommandExecutorLocal"
 *           generate="false"
 * @ejb.env-entry name="HibernateFactoryAddress" type="java.lang.String" 
 *                value="jboss:/hibernate/SessionFactory"
 */
public class HibernateCommandExecutorEJB extends CommandExecutorEJB {

    private SessionFactory sessionFactory;
    private Session session;

    /**
     * @ejb.interface-method
     * @ejb.transaction type="Required"
     */
    public Object executeTransaction(Object o, String methodName,
            String[] classNames, Object[] args) throws ClassNotFoundException,
            IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        try {
            setSession(o);

            final Object ret = super.executeTransaction(o, methodName,
                    classNames, args);
            flushSession();

            return ret;
        }
        catch (HibernateException he) {
            ctx.setRollbackOnly();
            throw new InvocationTargetException(he);
        }
        finally {
            closeSession();
        }
    }

    /**
     * @ejb.interface-method
     * @ejb.transaction type="Supports"
     */
    public Object executeQuery(Object o, String methodName,
            String[] classNames, Object[] args) throws ClassNotFoundException,
            IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        try {
            setSession(o);

            return super.executeQuery(o, methodName, classNames, args);
        }
        catch (HibernateException he) {
            ctx.setRollbackOnly();
            throw new InvocationTargetException(he);
        }
        finally {
            closeSession();
        }
    }

    public void setSessionContext(SessionContext sessionContext) {
        super.setSessionContext(sessionContext);

        try {
            InitialContext ctx = new InitialContext();

            sessionFactory = (SessionFactory) ctx.lookup((String) ctx
                    .lookup("java:comp/env/HibernateFactoryAddress"));
        }
        catch (NamingException ne) {
            throw new EJBException(ne);
        }
    }

    private void setSession(Object o) throws HibernateException {
        if (o instanceof HibernateCommand) {
            session = sessionFactory.openSession();
            ((HibernateCommand) o).setSession(session);
        }
    }

    private void flushSession() throws HibernateException {
        if (session == null) {
            return;
        }
        session.flush();
    }

    private void closeSession() throws InvocationTargetException {
        if (session == null) {
            return;
        }

        try {
            session.close();
        }
        catch (HibernateException he) {
            throw new InvocationTargetException(he);
        }
        finally {
            session = null;
        }
    }
}