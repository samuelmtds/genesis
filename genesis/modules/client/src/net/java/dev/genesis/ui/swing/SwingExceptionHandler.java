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
package net.java.dev.genesis.ui.swing;

import net.java.dev.genesis.ui.UIException;
import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.ValidationException;
import net.java.dev.genesis.ui.binding.ExceptionHandler;

import org.apache.commons.logging.LogFactory;

import java.awt.Component;
import java.util.Iterator;

import javax.swing.JOptionPane;

public class SwingExceptionHandler implements ExceptionHandler {
   private final Component root;

   public SwingExceptionHandler(Component root) {
      this.root = root;
   }

   public void handleException(Throwable throwable) {
      if (throwable instanceof ValidationException) {
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
         LogFactory.getLog(getClass()).error("Unknown exception", t);
      }

      handleUnknownException(throwable);
   }

   protected void handleException(String message, Throwable throwable) {
      JOptionPane.showMessageDialog(root,
         message + "\n" + UIUtils.getInstance().getStackTrace(throwable),
         getErrorMessage(), JOptionPane.ERROR_MESSAGE);

      LogFactory.getLog(getClass()).error(message, throwable);
   }

   protected boolean handleCustomException(Throwable t)
            throws Exception {
      if (t.getCause() != null) {
         handleException(t.getCause());

         return true;
      }

      return false;
   }

   protected void handleUIException(UIException uiException) {
      try {
         JOptionPane.showMessageDialog(root, uiException.getTitle(),
            uiException.getDescription(), JOptionPane.WARNING_MESSAGE);
      } catch (Throwable t) {
         LogFactory.getLog(getClass()).error("Unknown exception", t);
      }
   }

   protected void handleUnknownException(Throwable t) {
      handleException("Unexpected error occurred", t);
   }

   protected void showValidationErrors(final ValidationException ve) {
      final StringBuffer displayMessage = new StringBuffer();

      for (final Iterator messages =
            ve.getValidationErrors().values().iterator(); messages.hasNext();) {
         if (displayMessage.length() != 0) {
            displayMessage.append('\n');
         }

         displayMessage.append(messages.next().toString());
      }

      JOptionPane.showMessageDialog(root, displayMessage.toString(),
         UIUtils.getInstance().getBundle().getString("validation.errors.title"),
         JOptionPane.WARNING_MESSAGE);
   }

   protected String getErrorMessage() {
      return "Error";
   }
}
