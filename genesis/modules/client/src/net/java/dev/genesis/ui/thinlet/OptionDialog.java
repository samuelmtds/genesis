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
import net.java.dev.reusablecomponents.lang.Enum;

public class OptionDialog extends MessageDialog {
   public static final class Option extends Enum {
      public static final Option YES = new Option("Yes");
      public static final Option NO = new Option("No");
      public static final Option CANCEL = new Option("Cancel");

      private Option(String name) {
         super(name);
      }
   }

   public class OptionDialogScreenHandler extends MainScreenHandler {
      private Option selected = Option.CANCEL;

      private OptionDialogScreenHandler() throws ScreenNotFoundException {
         super("option.xml");
      }

      public void loseFocus() {
         requestFocus(find(getScreen(), Option.YES.toString().toLowerCase()));
      }

      public void yes() {
         selected = Option.YES;
         close();
      }

      public void no() {
         selected = Option.NO;
         close();
      }

      public Option getSelected() {
         return selected;
      }

      public void setCancel(boolean cancel) {
         setVisible(find(getScreen(), "cancel"), cancel);

         selected = (cancel) ? Option.CANCEL : Option.NO;
      }
   }

   public OptionDialog(BaseThinlet thinlet) throws ScreenNotFoundException {
      super(thinlet);
   }

   public OptionDialog(Frame frame) throws ScreenNotFoundException {
      super(frame);
   }

   protected MainScreenHandler createScreenHandler() throws ScreenNotFoundException {
      return new OptionDialogScreenHandler();
   }

   public Option getSelected() {
      return ((OptionDialogScreenHandler)getScreenHandler()).getSelected();
   }

   public static Option display(BaseThinlet thinlet, String title, String message) 
                                                throws ScreenNotFoundException {
      OptionDialog dialog = new OptionDialog(thinlet);
      dialog.show(title, message);

      return dialog.getSelected();
   }

   public static Option display(Frame frame, String title, String message) 
                                                throws ScreenNotFoundException {
      OptionDialog dialog = new OptionDialog(frame);
      dialog.show(title, message);

      return dialog.getSelected();
   }


   public static Option displayYesNo(BaseThinlet thinlet, String title, String message) 
                                                throws ScreenNotFoundException {
      OptionDialog dialog = new OptionDialog(thinlet);
      ((OptionDialogScreenHandler)dialog.getScreenHandler()).setCancel(false);
      dialog.show(title, message);

      return dialog.getSelected();
   }
}