/*
 * The Genesis Project
 * Copyright (C) 2005 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.helpers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockViewHandler;
import net.java.dev.genesis.ui.controller.FormControllerFactory;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;

public class TypeCheckerTest extends GenesisTestCase {
   private static final class InvocationHandlerImpl implements InvocationHandler {
      public Object invoke(Object o, Method m, Object[] parameters) {
         throw new UnsupportedOperationException();
      }
   }

   public TypeCheckerTest() {
      super("TypeChecker Unit Test");
   }

   public void testCheckFormControllerFactory() {
      try {
         TypeChecker.checkFormControllerFactory(null);
         fail("An IllegalArgumentException should have been thrown for null");
      } catch (IllegalArgumentException iea) {
      }

      try {
         TypeChecker.checkFormControllerFactory(new Object());
         fail("An IllegalArgumentException should have been thrown for a " +
               "new Object()");
      } catch (IllegalArgumentException iea) {
      }

      TypeChecker.checkFormControllerFactory(Proxy.newProxyInstance(Thread
            .currentThread().getContextClassLoader(), 
            new Class[] {FormControllerFactory.class}, 
            new InvocationHandlerImpl()));
   }

   public void testFormMetadataFactory() {
      try {
         TypeChecker.checkFormMetadataFactory(null);
         fail("An IllegalArgumentException should have been thrown for null");
      } catch (IllegalArgumentException iea) {
      }

      try {
         TypeChecker.checkFormMetadataFactory(new Object());
         fail("An IllegalArgumentException should have been thrown for a " +
               "new Object()");
      } catch (IllegalArgumentException iea) {
      }

      TypeChecker.checkFormMetadataFactory(Proxy.newProxyInstance(Thread
            .currentThread().getContextClassLoader(), 
            new Class[] {FormMetadataFactory.class}, 
            new InvocationHandlerImpl()));
   }

   public void testCheckViewMetadataFactory() {
      try {
         TypeChecker.checkViewMetadataFactory(null);
         fail("An IllegalArgumentException should have been thrown for null");
      } catch (IllegalArgumentException iea) {
      }

      try {
         TypeChecker.checkViewMetadataFactory(new Object());
         fail("An IllegalArgumentException should have been thrown for a " +
               "new Object()");
      } catch (IllegalArgumentException iea) {
      }

      TypeChecker.checkViewMetadataFactory(new MockViewHandler());
   }
}