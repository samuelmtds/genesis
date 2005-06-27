/*
 * The Genesis Project
 * Copyright (C) 2004-2005  Summa Technologies do Brasil Ltda.
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

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedList;
import net.java.dev.genesis.exception.TimeoutException;
import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;
import org.codehaus.aspectwerkz.CrossCuttingInfo;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;

/**
 * @Aspect perThread
 */
public class TimeoutAspect {
   private static final Log log = LogFactory.getLog(TimeoutAspect.class);

   private static final class LiveThreadWorkerThreadPair 
         extends PhantomReference {
      private final WeakReference reference;

      public LiveThreadWorkerThreadPair(Thread parentThread, ReferenceQueue queue,
            WorkerThread workerThread) {
         super(parentThread, queue);
         reference = new WeakReference(workerThread);
      }

      public WorkerThread getWorkerThread() {
         return (WorkerThread)reference.get();
      }
   }

   private static final class WorkerThreadDisposer extends Thread {
      private static WorkerThreadDisposer instance;
      private final ReferenceQueue queue = new ReferenceQueue();
      private final Collection references = new LinkedList();

      public WorkerThreadDisposer() {
         setDaemon(true);
         setPriority(Thread.MIN_PRIORITY);
         start();
      }

      public static WorkerThreadDisposer getInstance() {
         synchronized (WorkerThreadDisposer.class) {
            if (instance == null) {
               instance = new WorkerThreadDisposer();
            }
         }

         return instance;
      }

      public void enqueue(Thread parentThread, WorkerThread workerThread) {
         LiveThreadWorkerThreadPair pair = new LiveThreadWorkerThreadPair(
               parentThread, queue, workerThread);

         synchronized (references) {
            references.add(pair);
         }
      }

      public void run() {
         while (true) {
            try {
               LiveThreadWorkerThreadPair pair = 
                     (LiveThreadWorkerThreadPair)queue.remove();
               WorkerThread worker = pair.getWorkerThread();

               if (worker != null) {
                  worker.interrupt();
               }
               
               pair.clear();

               synchronized (references) {
                  references.remove(pair);
               }
            } catch (InterruptedException ie) {
               log.error("WorkerThreadDisposed interrupted", ie);
               return;
            }
         }
      }
   }

   private static final class WorkerThread extends Thread {
      private Throwable throwable;
      private Object returnValue;
      private JoinPoint jp;
      private Object lock = new Object();
      private boolean started;
      private boolean running;
      private boolean waiting;

      public WorkerThread() {
         setDaemon(true);
      }

      public void execute(JoinPoint jp, long timeout) {
         this.jp = jp;
         returnValue = null;
         throwable = null;
         running = true;
         waiting = false;

         if (!started) {
            started = true;
            start();
         } else {
            synchronized (lock) {
               lock.notifyAll();
            }
         }

         synchronized (lock) {
            try {
               waiting = true;
               lock.wait(timeout);
            } catch (InterruptedException ie) {
               log.info(ie);
            }
         }
      }

      public void run() {
         do {
            try {
               returnValue = jp.proceed();
            } catch (final Throwable t) {
               throwable = t;
            }

            running = false;
            jp = null;

            while (!waiting) {
               try {
                  Thread.sleep(10);
               } catch (InterruptedException ie) {
                  if (log.isTraceEnabled()) {
                     log.trace(ie);
                  }

                  return;
               }
            }

            synchronized (lock) {
               lock.notifyAll();

               try {
                  lock.wait();
               } catch (InterruptedException ie) {
                  if (log.isTraceEnabled()) {
                     log.trace(ie);
                  }

                  return;
               }
            }
         } while (true);
      }

      public Object getReturnValue() {
         return returnValue;
      }

      public Throwable getThrowable() {
         return throwable;
      }

      public boolean isRunning() {
         return running;
      }

      public void cleanUp() {
         returnValue = null;
         throwable = null;
      }
   }

   private final long timeout;
   private final boolean keepThreadInstance;
   private WorkerThread thread;
   
   public TimeoutAspect(final CrossCuttingInfo ccInfo) {
      this.timeout = Long.parseLong(ccInfo.getParameter("timeout"));
      this.keepThreadInstance = "true".equals(
            ccInfo.getParameter("keepThreadInstance"));
   }
   
   /**
    * @Around timeout
    */
   public Object timeoutAdvice(final JoinPoint jp) throws Throwable {
      if (thread == null) {
         thread = new WorkerThread();

         if (keepThreadInstance) {
            if (log.isDebugEnabled()) {
               log.debug("New thread created: " + thread);
            }

            WorkerThreadDisposer.getInstance().enqueue(Thread.currentThread(), 
                  thread);
         }
      } else {
         if (keepThreadInstance && log.isDebugEnabled()) {
            log.debug("Reusing thread: " + thread);
         }
      }

      thread.execute(jp, timeout);

      if (thread.isRunning()) {
         try {
            thread.interrupt();
         } catch (Throwable t) {
            log.error(t);
         }

         thread = null;

         throw new TimeoutException("Execution took more than " + timeout + 
               " ms");
      }
 
      final Throwable throwable = thread.getThrowable();
      final Object returnValue = thread.getReturnValue();

      if (keepThreadInstance) {
         thread.cleanUp();
      } else {
         try {
            thread.interrupt();
         } catch (Throwable t) {
            log.error(t);
         }

         thread = null;
      }

      if (throwable != null) {
         throw throwable;
      }

      return returnValue;
   }
}