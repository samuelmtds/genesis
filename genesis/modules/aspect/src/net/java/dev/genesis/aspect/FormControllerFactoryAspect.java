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

import net.java.dev.genesis.helpers.TypeChecker;
import net.java.dev.genesis.ui.controller.DefaultFormController;
import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.controller.FormControllerFactory;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;

public class FormControllerFactoryAspect {
   /**
    * @Mixin(pointcut="formControllerFactoryIntroduction", isTransient=true, deploymentModel="perInstance")
    */
   public static class AspectFormControllerFactory implements FormControllerFactory {
      private FormController controller;
      private Object form;

      public FormController getFormController(Object form) {
         if (this.form != null && this.form != form) {
            throw new IllegalArgumentException("Different form instances " +
                  "being used: this implementation works for a single form " +
                  "instance; first invoked with " + this.form + " and now " +
                  "with " + form);
         }

         if (controller == null) {
            controller = createFormController(form);
            controller.setForm(form);
            controller.setFormMetadata(getFormMetadata(form));
         }

         return controller;
      }

      protected FormController createFormController(Object form) {
         return new DefaultFormController();
      }

      protected FormMetadata getFormMetadata(final Object form) {
         TypeChecker.checkFormMetadataFactory(form);

         return ((FormMetadataFactory)form).getFormMetadata(form.getClass());
      }
   }
}