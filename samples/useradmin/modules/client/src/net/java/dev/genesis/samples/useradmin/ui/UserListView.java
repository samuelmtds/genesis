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
package net.java.dev.genesis.samples.useradmin.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.thinlet.BaseThinlet;

public class UserListView extends BaseThinlet {
   private final UserListForm form;

   public UserListView() throws Exception {
      int width = 800;
      int height = 350;
      Frame f = new Frame();
      f.add(this);
      Insets is = f.getInsets();
      width += is.left + is.right;
      height += is.top + is.bottom;
      Dimension ss = getToolkit().getScreenSize();
      width = Math.min(width, ss.width);
      height = Math.min(height, ss.height);
      f.setBounds((ss.width - width) / 2, (ss.height - height) / 2, width,
            height);
      f.setResizable(false);
      f.setTitle("Users");
      f.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            System.exit(0);
         }
      });
      setAllI18n(true);
      setResourceBundle(UIUtils.getInstance().getBundle());
      add(parse("user-list.xml"));

      bind(form = new UserListForm());
   }
   
   /**
    * @PreAction
    */
   public void create() throws Exception {
      final InsertUpdateView view = new InsertUpdateView(getFrame());
      if (view.showView()) {
         invokeFormAction("search");
      }
   }

   /**
    * @PreAction
    */
   public void update() throws Exception {
      final InsertUpdateView view = new InsertUpdateView(getFrame(), form
            .getUser());
      if (view.showView()) {
         invokeFormAction("search");
      }
   }
   
   /**
    * @PosAction
    */
   public void remove() throws Exception {
      invokeFormAction("search");
   }
   
   /**
    * @PosAction
    */
   public void nextPage() throws Exception {
      invokeFormAction("search");
   }
   
   /**
    * @PosAction
    */
   public void previousPage() throws Exception {
      invokeFormAction("search");
   }
}

