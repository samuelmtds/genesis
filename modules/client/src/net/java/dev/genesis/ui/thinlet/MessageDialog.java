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

import java.awt.Frame;
import java.util.StringTokenizer;

public class MessageDialog extends BaseDialogThinlet {
   private static final int MAX_COLUMNS = 255;
   private static final int MAX_ROWS = 10;

   private final MainScreenHandler handler = createScreenHandler();

   public class MainScreenHandler extends ScreenHandler {
      protected MainScreenHandler() throws ScreenNotFoundException {
         this("message.xml");
      }

      protected MainScreenHandler(String xml) throws ScreenNotFoundException {
         super(xml);
      }

      public void show(String title, String message) {
         getDialog().setTitle(title);

         Object messageField = find(getScreen(), MESSAGE);

         int rows = 0;
         int columns = 0;

         for (final StringTokenizer st = new StringTokenizer(message, "\n"); 
                                                   st.hasMoreTokens(); rows++) {
            columns = Math.min(MAX_COLUMNS, Math.max(st.nextToken().length(), columns));
         }

         setColumns(messageField, columns);
         setRows(messageField, Math.min(rows, MAX_ROWS));
         setText(messageField, message);

         loseFocus();

         show();
      }

      public void loseFocus() {
         requestFocus(find(getScreen(), CLOSE));
      }

      public void close() {
         super.close();
         getDialog().dispose();
      }
   }

   public MessageDialog(BaseThinlet thinlet) throws ScreenNotFoundException {
      super(thinlet);
   }

   public MessageDialog(Frame frame) throws ScreenNotFoundException {
      super(frame);
   }

   protected MainScreenHandler createScreenHandler() throws ScreenNotFoundException {
      return new MainScreenHandler();
   }

   protected MainScreenHandler getScreenHandler() {
      return handler;
   }

   public void show(String title, String message) {
      handler.show(title, message);
      packAndShowDialog();
   }

   public static void show(BaseThinlet thinlet, String title, String message) 
                                                throws ScreenNotFoundException {
      new MessageDialog(thinlet).show(title, message);
   }

   public static void show(Frame frame, String title, String message) 
                                                throws ScreenNotFoundException {
      new MessageDialog(frame).show(title, message);
   }
}