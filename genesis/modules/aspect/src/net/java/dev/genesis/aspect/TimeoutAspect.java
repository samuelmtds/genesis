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

import net.java.dev.genesis.exception.TimeoutException;

import org.apache.commons.logging.LogFactory;
import org.codehaus.aspectwerkz.CrossCuttingInfo;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;

/**
 * @Aspect perThread
 */
public class TimeoutAspect {
    private final long timeout;
    
    private Throwable t;
    private Object ret;

    public TimeoutAspect(final CrossCuttingInfo ccInfo) {
        this.timeout = Long.parseLong(ccInfo.getParameter("timeout"));
    }
    
    /**
     * @Around timeout
     * 
     */
    public Object timeoutAdvice(final JoinPoint jp) throws Throwable {
        t = null;
        ret = null;

        final Thread thread = new Thread() {
            public void run() {
                try {
                    ret = jp.proceed();
                } catch (final Throwable th) {
                    t = th;
                }
            }
        };
        thread.start();
        thread.join(timeout);

        if (thread.isAlive()) {
            try {
                thread.interrupt();
            } catch (Exception e) {
                LogFactory.getLog(getClass()).error(e);
            }

            throw new TimeoutException("Execution took more than " + timeout + " ms");
        }

        if (t != null) {
            throw t;
        }
        return ret;
    }
}