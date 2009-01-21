/*
 * The Genesis Project
 * Copyright (C) 2006-2009  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.thinlet;

import net.java.dev.genesis.ui.binding.AbstractDispatcherExceptionHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThinletExceptionHandler extends AbstractDispatcherExceptionHandler {
   private static final Log log = LogFactory.getLog(ThinletExceptionHandler.class);
   private final BaseThinlet thinlet;

   public ThinletExceptionHandler(BaseThinlet thinlet) {
      this.thinlet = thinlet;
   }

   protected void showErrorMessageDialog(String title, String message,
         Throwable throwable) {
      try {
         ErrorReporterDialog.show(thinlet, title, message, throwable);
      } catch (ScreenNotFoundException scnfe) {
         log.error("The error screen file could not be found", scnfe); // NOI18N
         log.error("Original error:", throwable); // NOI18N
      }
   }

   protected void showWarningMessageDialog(String title, String message) {
      try {
         MessageDialog.show(thinlet, title, message);
      } catch (ScreenNotFoundException snfe) {
         handleException(snfe);
      }
   }
}
