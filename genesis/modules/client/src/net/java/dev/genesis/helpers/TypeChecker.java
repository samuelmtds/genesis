/*
 * The Genesis Project
 * Copyright (C) 2005  Summa Technologies do Brasil Ltda.
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

import net.java.dev.genesis.ui.controller.FormControllerFactory;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;
import net.java.dev.genesis.ui.metadata.ViewMetadataFactory;

public final class TypeChecker {
   private TypeChecker() {
   }

   public static void checkViewMetadataFactory(final Object view) {
      if (!(view instanceof ViewMetadataFactory)) {
         throw new IllegalArgumentException(view + " should implement " +
               "ViewMetadataFactory; probably it should have been annotated " +
               "with @ViewHandler or your aop.xml/weaving process is not " +
               "properly configured.");
      }
   }

   public static void checkFormControllerFactory(final Object form) {
      if (!(form instanceof FormControllerFactory)) {
         throw new IllegalArgumentException(form + " should implement " +
               "FormControllerFactory; probably it should have been " +
               "annotated with @Form or your aop.xml/weaving process is not " +
               "properly configured.");
      }
   }

   public static void checkFormMetadataFactory(final Object form) {
      if (!(form instanceof FormMetadataFactory)) {
         throw new IllegalArgumentException(form + " should implement " +
               "FormMetadataFactory; probably it should have been " +
               "annotated with @Form or your aop.xml/weaving process is not " +
               "properly configured.");
      }
   }
}