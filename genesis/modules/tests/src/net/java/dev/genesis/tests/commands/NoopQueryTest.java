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

import net.java.dev.genesis.command.Query;
import net.java.dev.genesis.tests.TestCase;



public class NoopQueryTest extends TestCase {

    public void testLocal() {
        NoopQuery query = new NoopQuery();
        query.localMethod();
        assertTrue(query.value == 1);
    }

    public void testRemotable() throws Exception {
        NoopQuery query = new NoopQuery();
        assertEquals(query.invokeRemotable(), Status.STATUS_NO_TRANSACTION);
        assertTrue(query.value == 0);

    }

    public static class NoopQuery implements Query {

        private int value;

        public int invokeRemotable() throws Exception {
            value++;
            return ((UserTransaction) new InitialContext().lookup("UserTransaction")).getStatus();
        }

        public void localMethod() {
            value++;
        }
    }

}