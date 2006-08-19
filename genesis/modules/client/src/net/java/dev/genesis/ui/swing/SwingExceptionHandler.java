/*
 * The Genesis Project
 * Copyright (C) 2005-2006  Summa Technologies do Brasil Ltda.
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

import java.awt.HeadlessException;
import java.lang.reflect.InvocationTargetException;
import net.java.dev.genesis.ui.UIException;
import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.ValidationException;
import net.java.dev.genesis.ui.binding.ExceptionHandler;

import org.apache.commons.logging.LogFactory;

import java.awt.Component;
import java.awt.EventQueue;
import java.util.Iterator;

import javax.swing.JOptionPane;

public class SwingExceptionHandler implements ExceptionHandler {
   private final Component root;
   
   public SwingExceptionHandler(Component root) {
      this.root = root;
   }
   
   public Component getRoot() {
      return root;
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
      showMessageDialog(message + "\n" + 
            UIUtils.getInstance().getStackTrace(throwable), getErrorMessage(), 
            JOptionPane.ERROR_MESSAGE);
      
      LogFactory.getLog(getClass()).error(message, throwable);
   }
   
   protected boolean handleCustomException(Throwable t) throws Exception {
      if (t.getCause() != null) {
         handleException(t.getCause());
         
         return true;
      }

      return false;
   }
   
   protected void handleUIException(UIException uiException) {
      showMessageDialog(uiException.getDescription(),
            uiException.getTitle(), JOptionPane.WARNING_MESSAGE);
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
      
      showMessageDialog(displayMessage.toString(),
            UIUtils.getInstance().getBundle().getString("validation.errors.title"),
            JOptionPane.WARNING_MESSAGE);
   }
   
   protected void showMessageDialog(final String message, final String title,
         final int type) {
      if (EventQueue.isDispatchThread()) {
         JOptionPane.showMessageDialog(root, message, title, type);
         
         return;
      }
      
      try {
         EventQueue.invokeAndWait(new Runnable() {
            public void run() {
               JOptionPane.showMessageDialog(root, message, title, type);
            }
         });
      } catch (HeadlessException ex) {
         LogFactory.getLog(getClass()).error("Unknown exception", ex);
      } catch (InterruptedException ex) {
         LogFactory.getLog(getClass()).error("Unknown exception", ex);
      } catch (InvocationTargetException ex) {
         LogFactory.getLog(getClass()).error("Unknown exception", ex);
      }
   }
   
   protected String getErrorMessage() {
      return "Error";
   }
}