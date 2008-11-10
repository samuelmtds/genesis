/*
 * The Genesis Project
 * Copyright (C) 2006-2008  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.binding;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import net.java.dev.genesis.ui.UIException;
import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.ValidationException;

import net.java.dev.genesis.util.Bundle;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractDispatcherExceptionHandler implements
      DispatcherExceptionHandler {
   public void handleException(Throwable throwable) {
      if (throwable instanceof InvocationTargetException) {
         handleException(((InvocationTargetException)throwable).getTargetException());

         return;
      } else if (throwable instanceof ValidationException) {
         showValidationErrors((ValidationException) throwable);

         return;
      } else if (throwable instanceof UIException) {
         handleUIException((UIException) throwable);

         return;
      }

      try {
         if (handleCustomException(throwable)) {
            return;
         }
      } catch (Throwable t) {
         LogFactory.getLog(getClass()).error("Unknown exception", t); // NOI18N
      }

      handleUnknownException(throwable);
   }
   
   public boolean handleCustomException(Throwable t) throws Exception {
      if (t.getCause() != null) {
         handleException(t.getCause());

         return true;
      }

      return false;
   }

   public void handleUIException(UIException uiException) {
      showWarningMessageDialog(uiException.getTitle(), uiException
            .getDescription());
   }

   public void handleUnknownException(Throwable t) {
      handleException(Bundle.getMessage(AbstractDispatcherExceptionHandler.class,
            "UNEXPECTED_ERROR_OCCURRED"), t); // NOI18N
   }

   public void showValidationErrors(ValidationException ve) {
      final StringBuffer displayMessage = new StringBuffer();

      for (final Iterator messages = ve.getValidationErrors().values()
            .iterator(); messages.hasNext();) {
         if (displayMessage.length() != 0) {
            displayMessage.append('\n');
         }

         displayMessage.append(messages.next().toString());
      }

      showWarningMessageDialog(UIUtils.getInstance().getBundle().getString(
            "validation.errors.title"), displayMessage.toString()); // NOI18N
   }

   public String getErrorMessage() {
      return Bundle.getMessage(AbstractDispatcherExceptionHandler.class, "ERROR"); // NOI18N
   }

   public void handleException(String message, Throwable throwable) {
      showErrorMessageDialog(getErrorMessage(), message, throwable);

      LogFactory.getLog(getClass()).error(message, throwable);
   }

   protected String getStackTrace(Throwable throwable) {
      return UIUtils.getInstance().getStackTrace(throwable);
   }

   protected abstract void showWarningMessageDialog(final String title, final String message);
   protected abstract void showErrorMessageDialog(final String title, final String message, final Throwable throwable);
}
