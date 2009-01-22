/*
 * The Genesis Project
 * Copyright (C) 2004-2009  Summa Technologies do Brasil Ltda.
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

import net.java.dev.genesis.util.Bundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.aspectwerkz.AspectContext;
import org.codehaus.aspectwerkz.joinpoint.StaticJoinPoint;


/**
 * @Aspect("perJVM")
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
         super("WorkerThreadDisposer-Daemon"); // NOI18N
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
               log.error(Bundle.getMessage(TimeoutAspect.class,
                     "WORKERTHREADDISPOSED_INTERRUPTED"), ie); // NOI18N
               return;
            }
         }
      }
   }

   private static final class WorkerThread extends Thread {
      private final boolean keepThreadInstance;
      private Throwable throwable;
      private Object returnValue;
      private StaticJoinPoint jp;
      private Object lock = new Object();
      private boolean started;
      private boolean running;
      private boolean waiting;

      public WorkerThread(boolean keepThreadInstance) {
         super("WorkerThread-" + Thread.currentThread().getName()); // NOI18N
         setDaemon(true);
         this.keepThreadInstance = keepThreadInstance;
      }

      public void execute(StaticJoinPoint jp, long timeout) {
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

               if (!keepThreadInstance) {
                  return;
               }

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
   private final ThreadLocal threadLocal = new ThreadLocal();
   
   public TimeoutAspect(final AspectContext ctx) {
      this.timeout = Long.parseLong(ctx.getParameter("timeout")); // NOI18N
      this.keepThreadInstance = "true".equals( // NOI18N
            ctx.getParameter("keepThreadInstance")); // NOI18N
   }

   private WorkerThread getWorkerThread() {
      return (WorkerThread)threadLocal.get();
   }

   private void setWorkerThread(WorkerThread workerThread) {
      threadLocal.set(workerThread);
   }

   /**
    * @Around("timeout")
    */
   public Object timeoutAdvice(final StaticJoinPoint jp) throws Throwable {
      WorkerThread thread = getWorkerThread();

      if (thread == null) {
         thread = new WorkerThread(keepThreadInstance);
         setWorkerThread(thread);

         if (keepThreadInstance) {
            if (log.isDebugEnabled()) {
               log.debug(Bundle.getMessage(TimeoutAspect.class,
                     "NEW_THREAD_CREATED_X", thread)); // NOI18N
            }

            WorkerThreadDisposer.getInstance().enqueue(Thread.currentThread(), 
                  thread);
         }
      } else {
         if (keepThreadInstance && log.isDebugEnabled()) {
            log.debug(Bundle.getMessage(TimeoutAspect.class, "REUSING_THREAD_X", // NOI18N
                  thread));
         }
      }

      thread.execute(jp, timeout);

      if (thread.isRunning()) {
         try {
            thread.interrupt();
         } catch (Throwable t) {
            log.error(t);
         }

         setWorkerThread(null);

         throw new TimeoutException(Bundle.getMessage(TimeoutAspect.class,
               "EXECUTION_TOOK_MORE_THAN_X_MS", new Long(timeout))); // NOI18N
      }
 
      final Throwable throwable = thread.getThrowable();
      final Object returnValue = thread.getReturnValue();

      thread.cleanUp();

      if (!keepThreadInstance) {
         setWorkerThread(null);
      }

      if (throwable != null) {
         throw throwable;
      }

      return returnValue;
   }
}