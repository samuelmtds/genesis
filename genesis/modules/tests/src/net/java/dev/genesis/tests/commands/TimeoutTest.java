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

import net.java.dev.genesis.exception.TimeoutException;
import net.java.dev.genesis.tests.TestCase;



public class TimeoutTest extends TestCase {

    public void testDontWait() throws Exception {
       System.out.println("dont wait");
        TimeoutCommand command = new TimeoutCommand();
        Exception e = null;
        try{
            command.dontWait();
        }
        catch(Exception ex){
            e = ex;
        }
        assertNull(e);
    }

    public void testWaitALot() throws Exception {
        TimeoutCommand command = new TimeoutCommand();
        Exception e = null;

        try{
            command.takesALot();
        }
        catch(Exception ex){
            e = ex;
        }

        assertNotNull(e);
        assertTrue(e instanceof TimeoutException);
    }

    public static class TimeoutCommand implements Serializable {

        /**
         * @Remotable
         */
        public void takesALot() throws InterruptedException {
            System.out.println("takes a loooong time");
            Thread.sleep(20000);
        }

        /**
         * @Remotable
         */
        public void dontWait() {
           System.out.println("dont wait");   
        }

    }

}