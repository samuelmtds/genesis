/*
 * The Genesis Project
 * Copyright (C) 2004  Summa Technologies do Brasil Ltda.
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

import net.java.dev.genesis.ui.UIUtils;
import java.awt.Frame;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ErrorReporterDialog extends BaseDialogThinlet {
   private static Log log = LogFactory.getLog(ErrorReporterDialog.class);
   private final MainScreenHandler handler = new MainScreenHandler();

   public class MainScreenHandler extends ScreenHandler {
      private static final String STACK_TRACE = "stackTrace";

      public MainScreenHandler() throws ScreenNotFoundException {
         super("error_reporter.xml");
      }

      public void show(String title, final String message, 
                       final Throwable throwable) {
         getDialog().setTitle(title);
         setText(find(getScreen(), MESSAGE), message);
         setText(find(getScreen(), STACK_TRACE), 
                           UIUtils.getInstance().getStackTrace(throwable));
         new Thread() {
            public void run() {
               log.error(message, throwable);
            }
         }.start();

         show();
      }

      public void close() {
         super.close();
         getDialog().dispose();
      }
   }

   public ErrorReporterDialog(BaseThinlet thinlet) throws ScreenNotFoundException {
      super(thinlet);
   }

   public ErrorReporterDialog(Frame frame) throws ScreenNotFoundException {
      super(frame);
   }

   public void show(String title, String message, Throwable throwable) {
      handler.show(title, message, throwable);
      packAndShowDialog();
   }

   public static void show(BaseThinlet thinlet, String title, String message, 
                           Throwable throwable) throws ScreenNotFoundException {
      new ErrorReporterDialog(thinlet).show(title, message, throwable);
   }

   public static void show(Frame frame, String title, String message, 
                           Throwable throwable) throws ScreenNotFoundException {
      new ErrorReporterDialog(frame).show(title, message, throwable);
   }
}