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

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JOptionPane;

import net.java.dev.genesis.ui.binding.AbstractDispatcherExceptionHandler;

import org.apache.commons.logging.LogFactory;

public class SwingExceptionHandler extends AbstractDispatcherExceptionHandler {
   private final Component root;

   public SwingExceptionHandler(Component root) {
      this.root = root;
   }

   public Component getRoot() {
      return root;
   }

   protected void showWarningMessageDialog(final String title, final String message) {
      showMessageDialog(title, message, JOptionPane.WARNING_MESSAGE);
   }

   protected void showErrorMessageDialog(final String title, final String message, Throwable throwable) {
      showMessageDialog(title, message + '\n' + getStackTrace(throwable),
            JOptionPane.ERROR_MESSAGE);
   }

   protected void showMessageDialog(final String title, final String message,
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
}