/*
 * The Genesis Project
 * Copyright (C) 2005-2009  Summa Technologies do Brasil Ltda.
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
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.HeadlessException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.binding.AbstractDispatcherExceptionHandler;

import net.java.dev.genesis.util.Bundle;
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

   protected void showErrorMessageDialog(final String title,
         final String message, Throwable throwable) {
      showMessageDialog(title, createStackTracePanel(message, throwable), JOptionPane.ERROR_MESSAGE);
   }
   
   protected Object createStackTracePanel(String message, Throwable throwable) {
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

      JLabel messageLabel = new JLabel(message);
      messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD));
      messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

      JLabel stackLabel = new JLabel(UIUtils.getInstance().getBundle()
            .getString("ErrorReporterDialog.stackTrace")); // NOI18N
      stackLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

      JTextArea stackTextArea = new JTextArea(getStackTrace(throwable));
      stackTextArea.setEditable(false);
      stackTextArea.setColumns(50);
      stackTextArea.setRows(10);

      panel.add(messageLabel);
      panel.add(Box.createRigidArea(new Dimension(0, 5)));
      panel.add(stackLabel);
      panel.add(Box.createRigidArea(new Dimension(0, 5)));
      panel.add(new JScrollPane(stackTextArea));

      return panel;
   }

   protected void showMessageDialog(final String title, final Object message,
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
         LogFactory.getLog(getClass()).error(Bundle.getMessage(
               SwingExceptionHandler.class, "UNKNOWN_EXCEPTION"), ex); // NOI18N
      } catch (InterruptedException ex) {
         LogFactory.getLog(getClass()).error(Bundle.getMessage(
               SwingExceptionHandler.class, "UNKNOWN_EXCEPTION"), ex); // NOI18N
      } catch (InvocationTargetException ex) {
         LogFactory.getLog(getClass()).error(Bundle.getMessage(
               SwingExceptionHandler.class, "UNKNOWN_EXCEPTION"), ex); // NOI18N
      }
   }
}