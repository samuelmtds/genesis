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
package example.ui;

import java.awt.Frame;

import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.thinlet.BaseDialogThinlet;

public class InsertRoleView extends BaseDialogThinlet {
   private final InsertRoleForm form;
   private boolean hasChanged;
   
   public InsertRoleView(final Frame frame) throws Exception {
      super(frame);
      getDialog().setModal(true);
      getDialog().setResizable(false);
      getDialog().setTitle("Insert Role");
      setAllI18n(true);
      setResourceBundle(UIUtils.getInstance().getBundle());
      add(parse("insert-role.xml"));
      bind(form = new InsertRoleForm());
   }
   
   public boolean showView() throws Exception {
      display();
      return hasChanged;
   }

   /**
    * @PosAction
    */
   public void cancel() {
      getDialog().dispose();
   }
   
   /**
    * @PosAction
    */
   public void save() {
      getDialog().dispose();
      hasChanged = true;
   }

}

