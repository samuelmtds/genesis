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
package net.java.dev.genesis.aspect;

import org.apache.commons.logging.LogFactory;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;
import org.codehaus.aspectwerkz.xmldef.advice.AroundAdvice;

import net.java.dev.genesis.exception.TimeoutException;

/**
 * AroundAdvice for timeout, just in cases where
 * queries or transactions takes too long. If a timeout
 * occurs, we kill the thread and throw a TimeoutException
 * 
 */
public class TimeoutAdvice extends AroundAdvice {

    private long timeout = -1;
    private Throwable t;
    private Object ret;
    
    private long getTimeout(){
        if(timeout < 0) {
            timeout = Long.parseLong(getParameter("timeout"));
        }
        return timeout;
    }

    public Object execute(final JoinPoint jp) throws Throwable {
        t = null;
        ret = null;

        final Thread thread = new Thread() {

            public void run() {
                try {
                    ret = jp.proceedInNewThread();
                }
                catch (final Throwable th) {
                    t = th;
                }
            }
        };
        thread.start();
        thread.join(getTimeout());

        if (thread.isAlive()) {
            try {
                thread.interrupt();
            }
            catch (Exception e) {
                LogFactory.getLog(getClass()).error(e);
            }
            throw new TimeoutException();
        }

        if (t != null) {
            throw t;
        }
        return ret;
    }

}