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
package net.java.dev.genesis.tests.commands;

import net.java.dev.genesis.command.hibernate.AbstractHibernateCommand;
import net.java.dev.genesis.command.hibernate.AbstractHibernateQuery;
import net.java.dev.genesis.command.hibernate.AbstractHibernateTransaction;
import net.java.dev.genesis.tests.TestCase;
import net.sf.hibernate.FlushMode;
import net.sf.hibernate.Session;


public class LocalCommandExecutionTest extends TestCase {

    public void testLocalCommand() throws Exception {
        NoopCommand command = new NoopCommand();
        command.local();
        assertTrue(command.value == 1);
        assertNull(command.hibernateSession);
    }

    public void testRemotableCommand() {
        NoopCommand command = new NoopCommand();
        command.remotable();
        assertTrue(command.value == 1);
        assertEquals(FlushMode.AUTO, command.flushMode);
    }

    public void testTransactionalCommand() throws Exception {
        NoopCommand command = new NoopCommand();
        command.transactional();
        assertTrue(command.value == 1);
        assertEquals(FlushMode.COMMIT, command.flushMode);
    }

    public void testNestedCommand() throws Exception {
       NoopCommand command = new NoopCommand();
       assertTrue("Sessions should be the same in nested command invocations", 
             command.nested());
    }

    public void testLocalQuery() {
        NoopQuery query = new NoopQuery();
        query.localMethod();
        assertTrue(query.value == 1);
        assertNull(query.hibernateSession);
    }

    public void testRemotableQuery() throws Exception {
        NoopQuery query = new NoopQuery();
        query.invokeRemotable();
        assertTrue(query.value == 1);
        assertEquals(FlushMode.AUTO, query.flushMode);
    }

    public void testLocalTransaction() {
        NoopTransaction transaction = new NoopTransaction();
        transaction.localMethod();
        assertTrue(transaction.value == 1);
        assertNull(transaction.hibernateSession);
    }

    public void testTransactionalTransaction() {
        NoopTransaction transaction = new NoopTransaction();
        transaction.invokeTransactional();
        assertTrue(transaction.value == 1);
        assertEquals(FlushMode.COMMIT, transaction.flushMode);
    }

    public static class NoopCommand extends AbstractHibernateCommand {

        private FlushMode flushMode;
        private Session hibernateSession;
        private int value;

        /**
         * @Remotable
         */
        public boolean nested() {
           return getSession() == remotable();
        }

        /**
         * @Remotable
         */
        public Session remotable() {
            value++;
            flushMode = getSession().getFlushMode();
            
            return getSession();
        }

        public void local() {
            value++;
            hibernateSession = getSession();
        }

        /**
         * @Transactional
         */
        public void transactional() {
            value++;
            flushMode = getSession().getFlushMode();
        }
    }

    public static class NoopQuery extends AbstractHibernateQuery {

        private FlushMode flushMode;
        private Session hibernateSession;
        private int value;

        public void invokeRemotable() {
            value++;
            flushMode = getSession().getFlushMode();
        }

        public void localMethod() {
            value++;
            hibernateSession = getSession();
        }
    }

    public static class NoopTransaction extends AbstractHibernateTransaction {

        private FlushMode flushMode;
        private Session hibernateSession;
        private int value;

        public void invokeTransactional() {
            value++;
            flushMode = getSession().getFlushMode();
        }

        public void localMethod() {
            value++;
            hibernateSession = getSession();
        }
    }

}