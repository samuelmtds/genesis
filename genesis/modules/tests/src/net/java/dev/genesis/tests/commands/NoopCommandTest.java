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

import java.io.Serializable;

import javax.naming.InitialContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import net.java.dev.genesis.tests.TestCase;


public class NoopCommandTest extends TestCase {
    public void testLocal() {
        NoopCommand command = new NoopCommand();
        command.local();
        assertTrue(command.value == 1);
    }

    public void testRemotable() throws Exception {
        NoopCommand command = new NoopCommand();
        assertEquals(command.remotable(), Status.STATUS_NO_TRANSACTION);
        assertTrue(command.value == 0);
    }

    public void testTransactional() throws Exception {
        NoopCommand command = new NoopCommand();
        assertEquals(command.transactional(), Status.STATUS_ACTIVE);
        assertTrue(command.value == 0);
    }

    public static class NoopCommand implements Serializable {

        private int value;

        /**
         * @Remotable
         */
        public int remotable() throws Exception {
            value++;
            return ((UserTransaction) new InitialContext().lookup("UserTransaction")).getStatus();
        }

        public void local() {
            value++;
        }

        /**
         * @Transactional
         */
        public int transactional() throws Exception {
            value++;
            return ((UserTransaction) new InitialContext().lookup("UserTransaction")).getStatus();
        }
    }
}