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
package net.java.dev.genesis.ui.controller;

import net.java.dev.genesis.ui.metadata.DefaultFormMetadataFactory;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;

public class DefaultFormControllerFactory implements FormControllerFactory {
   private int maximumEvaluationTimes = 1;
   private boolean resetOnDataProviderChange = true;

   public FormController getFormController(Object form) {
      FormController controller = getController(form);
      if (controller == null) {
         controller = createFormController();

         configure(controller, form);
         registerController(form, controller);
      }

      return controller;
   }

   public void setMaximumEvaluationTimes(int maximumEvaluationTimes) {
      this.maximumEvaluationTimes = maximumEvaluationTimes;
   }

   public void setResetOnDataProviderChange(boolean resetOnDataProviderChange) {
      this.resetOnDataProviderChange = resetOnDataProviderChange;
   }

   protected FormController getController(Object form) {
      return FormControllerRegistry.getInstance().get(form);
   }
   
   protected void registerController(Object form, FormController controller) {
      FormControllerRegistry.getInstance().register(form, controller);
   }
   
   protected FormController createFormController() {
      return new DefaultFormController();
   }

   protected void configure(FormController controller, Object form) {
      controller.setForm(form);
      controller.setFormMetadata(getFormMetadata(form));
      controller.setMaximumEvaluationTimes(maximumEvaluationTimes);
      controller.setResetOnDataProviderChange(resetOnDataProviderChange);
   }

   protected FormMetadataFactory createFormMetadataFactory() {
      return new DefaultFormMetadataFactory();
   }

   protected FormMetadata getFormMetadata(final Object form) {
      return createFormMetadataFactory().getFormMetadata(form.getClass());
   }
}
