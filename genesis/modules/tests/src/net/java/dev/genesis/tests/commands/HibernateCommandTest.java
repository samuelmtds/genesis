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

import javax.naming.InitialContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import net.java.dev.genesis.command.hibernate.AbstractHibernateCommand;
import net.java.dev.genesis.command.hibernate.AbstractHibernateQuery;
import net.java.dev.genesis.command.hibernate.AbstractHibernateTransaction;
import net.java.dev.genesis.tests.TestCase;



public class HibernateCommandTest extends TestCase {
    public void testCommandLocal() {
       System.out.println("local");
        HibernateCommand command = new HibernateCommand();
        assertFalse(command.local());
        assertTrue(command.value == 1);
    }

    public void testCommandRemotable() throws Exception {
       System.out.println("remotable");
        HibernateCommand command = new HibernateCommand();
        assertEquals(command.remotable(), Status.STATUS_NO_TRANSACTION);
        assertTrue(command.value == 0);
    }

    public void testCommandTransactional() throws Exception {
        HibernateCommand command = new HibernateCommand();
        assertEquals(command.transactional(), Status.STATUS_ACTIVE);
        assertTrue(command.value == 0);
    }

    public void testQueryLocal() {
        HibernateQuery command = new HibernateQuery();
        assertFalse(command.local());
        assertTrue(command.value == 1);
    }

    public void testQueryRemotable() throws Exception {
        HibernateQuery command = new HibernateQuery();
        assertEquals(command.invokeRemotable(), Status.STATUS_NO_TRANSACTION);
        assertTrue(command.value == 0);
    }

    public void testTransactionLocal() {
        HibernateTransaction command = new HibernateTransaction();
        assertFalse(command.local());
        assertTrue(command.value == 1);
    }

    public void testTransactionTransactional() throws Exception {
        HibernateTransaction command = new HibernateTransaction();
        assertEquals(command.invokeTransactional(), Status.STATUS_ACTIVE);
        assertTrue(command.value == 0);
    }

    public static class HibernateCommand extends AbstractHibernateCommand {

        private int value;

        public boolean local() {
            value++;
            return getSession() != null;
        }

        /**
         * @Remotable
         */
        public int remotable() throws Exception {
            value++;
            return ((UserTransaction) new InitialContext().lookup("UserTransaction")).getStatus();
        }

        /**
         * @Transactional
         */
        public int transactional() throws Exception {
            value++;
            return ((UserTransaction) new InitialContext().lookup("UserTransaction")).getStatus();
        }

    }

    public static class HibernateQuery extends AbstractHibernateQuery {

        private int value;

        public boolean local() {
            value++;
            return getSession() != null;
        }

        public int invokeRemotable() throws Exception {
            value++;
            return ((UserTransaction) new InitialContext().lookup("UserTransaction")).getStatus();
        }
    }

    public static class HibernateTransaction extends AbstractHibernateTransaction {

        private int value;

        public boolean local() {
            value++;
            return getSession() != null;
        }

        public int invokeTransactional() throws Exception {
            value++;
            return ((UserTransaction) new InitialContext().lookup("UserTransaction")).getStatus();
        }
    }

}