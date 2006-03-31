/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.tests.ui;

import net.java.dev.genesis.tests.TestCase;
import net.java.dev.genesis.ui.metadata.ViewMetadata;

public class ViewMetadataFactoryTest extends TestCase {
   /**
    * @ViewHandler
    */
   public static class ValidViewHandler {
      static final String SIMPLE_METHOD = "simpleMethod";
      static final String ALIASED_METHOD = "aliasedMethod";
      static final String ARRAY_METHOD = "arrayMethod";
      String value;

      /**
       * @BeforeAction
       * @AfterAction
       */
      public void simpleMethod() {
         value = SIMPLE_METHOD;
      }

      /**
       * @BeforeAction anotherName
       * @AfterAction anotherName
       */
      public void aliasedMethod() {
         value = ALIASED_METHOD;
      }

      /**
       * @BeforeAction({"a", "b"})
       * @AfterAction({"a", "b"})
       */
      public void arrayMethod() {
         value = ARRAY_METHOD;
      }
   }

   /**
    * @ViewHandler
    */
   public static class InvalidAfterActionViewHandler {
      /**
       * @AfterAction
       */
      public void invalidAfterAction(String arg) {
      }
   }

   /**
    * @ViewHandler
    */
   public static class InvalidBeforeActionViewHandler {
      /**
       * @BeforeAction
       */
      public void invalidBeforeAction(String arg) {
      }
   }

   public void testAfterAction() throws Exception {
      ValidViewHandler valid = new ValidViewHandler();
      ViewMetadata validMetadata = getViewMetadata(valid);

      valid.value = null;
      validMetadata.invokeAfterAction(valid, "non-existant");
      assertNull(valid.value);

      valid.value = null;
      validMetadata.invokeAfterAction(valid, ValidViewHandler.SIMPLE_METHOD);
      assertSame(ValidViewHandler.SIMPLE_METHOD, valid.value);

      valid.value = null;
      validMetadata.invokeAfterAction(valid, "anotherName");
      assertSame(ValidViewHandler.ALIASED_METHOD, valid.value);

      valid.value = null;
      validMetadata.invokeAfterAction(valid, "a");
      assertSame(ValidViewHandler.ARRAY_METHOD, valid.value);

      valid.value = null;
      validMetadata.invokeAfterAction(valid, "b");
      assertSame(ValidViewHandler.ARRAY_METHOD, valid.value);

      try {
         getViewMetadata(new InvalidAfterActionViewHandler());
         fail("An IllegalArgumentException should be thrown while creating the " +
               "ViewMetadata instance");
      } catch (IllegalArgumentException iae) {
         // expected, it is working
      }
   }

   public void testBeforeAction() throws Exception {
      ValidViewHandler valid = new ValidViewHandler();
      ViewMetadata validMetadata = getViewMetadata(valid);

      valid.value = null;
      validMetadata.invokeBeforeAction(valid, "non-existant");
      assertNull(valid.value);

      valid.value = null;
      validMetadata.invokeBeforeAction(valid, ValidViewHandler.SIMPLE_METHOD);
      assertSame(ValidViewHandler.SIMPLE_METHOD, valid.value);

      valid.value = null;
      validMetadata.invokeBeforeAction(valid, "anotherName");
      assertSame(ValidViewHandler.ALIASED_METHOD, valid.value);

      valid.value = null;
      validMetadata.invokeBeforeAction(valid, "a");
      assertSame(ValidViewHandler.ARRAY_METHOD, valid.value);

      valid.value = null;
      validMetadata.invokeBeforeAction(valid, "b");
      assertSame(ValidViewHandler.ARRAY_METHOD, valid.value);

      try {
         getViewMetadata(new InvalidBeforeActionViewHandler());
         fail("An IllegalArgumentException should be thrown while creating the " +
               "ViewMetadata instance");
      } catch (IllegalArgumentException iae) {
         // expected, it is working
      }
   }
}