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

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public abstract class BaseDialogThinlet extends BaseThinlet {
   private final Dialog dialog;
   private ScreenHandler handler;
   private WindowListener listener;
   private boolean exitOnClose;
   private boolean doDialogPackAction = true;

   protected BaseDialogThinlet(final BaseThinlet thinlet, final int width, 
                              final int height) {
      this(new Dialog(thinlet.getFrame(), true), width, height);
   }

   protected BaseDialogThinlet(final BaseThinlet thinlet) {
      this(new Dialog(thinlet.getFrame(), true));
   }

   protected BaseDialogThinlet(final Dialog dialog, final int width, 
                              final int height) {
      this(dialog);
      setDialogBounds(width, height);
   }

   protected BaseDialogThinlet(final Dialog dialog) {
      this.dialog = dialog;

      dialog.add(this, BorderLayout.CENTER);
      dialog.addWindowListener(listener = new WindowAdapter() {
         public void windowClosing(WindowEvent we) {
            try {
               onClose();
            } catch (Exception e) {
               handleException(e);
            }
         }
      });

      dialog.setResizable(true);
   }

   protected BaseDialogThinlet() {
      this(new Dialog(new Frame()));
   }
   
   protected BaseDialogThinlet(final Frame frame) {
      this(new Dialog(frame));
   }

   /**
    * @deprecated
    */
   protected ScreenHandler getHandler() {
      return handler;
   }

   /**
    * @deprecated
    */
   protected void setHandler(ScreenHandler handler) throws Exception{
      if (this.handler != null) {
         handler.close();
      }

      handler.show();
      this.handler = handler;
   }
   
   protected final Dialog getDialog() {
      return dialog;
   }

   public Frame getFrame() {
       return (Frame)dialog.getOwner();
   }

   protected void showDialog() {
      dialog.setVisible(true);
   }

   public void display() throws Exception {
      packAndShowDialog();
   }
   
   protected void packDialog() {
      if (doDialogPackAction) {
         dialog.pack();
         final Rectangle bounds = dialog.getBounds();
         setDialogBounds(bounds.width, bounds.height);
      }
   }

   protected void packAndShowDialog() {
      packDialog();
      showDialog();
   }

   protected void setDialogBounds(int width, int height) {
      final Dimension screen = getToolkit().getScreenSize();
      dialog.setBounds((screen.width - width) / 2, (screen.height - height) / 2,
            width, height); 
   }

   protected boolean isExitOnClose() {
      return exitOnClose;
   }

   protected void setExitOnClose(boolean exitOnClose) {
      this.exitOnClose = exitOnClose;
   }

   public void onClose() throws Exception {
      dialog.dispose();

      dialog.remove(this);
      dialog.getParent().remove(dialog);

      if (listener != null) {
         dialog.removeWindowListener(listener);
         listener = null;
      }

      releaseThinletThread();

      if (exitOnClose) {
         System.exit(0);
      }
   }
   
   public void setDoDialogPackAction(boolean b) {
      this.doDialogPackAction = b;
   }
}