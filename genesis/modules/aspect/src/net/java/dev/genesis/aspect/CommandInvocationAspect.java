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

import java.io.Serializable;
import java.lang.reflect.Method;
import net.java.dev.genesis.command.Query;
import net.java.dev.genesis.command.Transaction;
import org.codehaus.aspectwerkz.CrossCuttingInfo;
import org.codehaus.aspectwerkz.annotation.Annotations;

public class CommandInvocationAspect {
   protected final CrossCuttingInfo ccInfo;

   public CommandInvocationAspect(final CrossCuttingInfo ccInfo) {
      this.ccInfo = ccInfo;
   }

   /**
    * @Introduce commandResolverIntroduction deploymentModel=perJVM
    */
   public static class CommandResolverImpl implements CommandResolver, Serializable {
      private final boolean useFastMode;

      public CommandResolverImpl(CrossCuttingInfo ccInfo) {
         useFastMode = (ccInfo.isPrototype()) ? true :
               !"false".equals(ccInfo.getParameter("useFastMode"));
      }

      public boolean isRemotable(Method m) {
         return useFastMode || Annotations.getAnnotation("Remotable", m) != null
               || Query.class.isAssignableFrom(m.getDeclaringClass())
               || isTransactional(m);
      }

      /**
       * To be a <b>Transactional Method </b> the object and/or the method
       * must satisfy at least one of the following conditions:
       * <ul>
       * <li>The object needs to implement
       * <code>net.java.dev.genesis.command.Transaction</code>.</li>
       * <li>The method must contain at least one <code>Transactional</code>
       * annotation.</li>
       * </ul>
       *
       * @param m
       *            The proxy method
       */
      public boolean isTransactional(Method m) {
         return Annotations.getAnnotation("Transactional", m) != null
               || Transaction.class.isAssignableFrom(m.getDeclaringClass());
      }
   }
}