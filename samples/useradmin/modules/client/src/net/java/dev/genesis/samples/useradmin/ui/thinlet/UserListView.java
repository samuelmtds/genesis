/*
 * The Genesis Project
 * Copyright (C) 2004-2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.samples.useradmin.ui.thinlet;

import net.java.dev.genesis.samples.useradmin.UserAdmin;
import net.java.dev.genesis.samples.useradmin.ui.UserListForm;
import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.thinlet.OptionDialog;
import net.java.dev.genesis.ui.thinlet.OptionDialog.Option;


public class UserListView extends BaseView {
   private final UserListForm form;

   public UserListView() throws Exception {
      super("UserListView.title", "user-list.xml", 800, 360, false);
      bind(form = new UserListForm());
   }
   
   protected void onClose() throws Exception {
      getFrame().dispose();
      UserAdmin.showMainWindow();
   }
   
   public void create() throws Exception {
      final InsertUpdateView view = new InsertUpdateView(getFrame());

      if (view.showView()) {
         runSearch();
         refreshView();
      }
   }

   /**
    * @BeforeAction
    */
   public void update() throws Exception {
      final InsertUpdateView view = new InsertUpdateView(getFrame(), form
            .getUser());

      if (view.showView()) {
         runSearch();
      }
   }

   /**
    * @BeforeAction("remove")
    */
   public boolean confirmRemove() throws Exception {
      return OptionDialog.displayYesNo(this, UIUtils.getInstance().getBundle()
            .getString("UserListView.deleteConfirmationTitle"), UIUtils
            .getInstance().getBundle().getString(
                  "UserListView.deleteConfirmation")) == Option.YES;
   }

   /**
    * @AfterAction
    */
   public void remove() {
      runSearch();
   }

   private void runSearch() {
      form.reset();
      form.setResetSearch(true);
      form.setRunSearch(true);
   }
}