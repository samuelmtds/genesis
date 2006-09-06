/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
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

package net.java.dev.genesis.samples.useradmin.ui.swt.role;


import net.java.dev.genesis.samples.useradmin.databeans.Role;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class RoleChooser extends Composite {
   public static final int RoleChanged = -99;
   
   private Text roleCode;
   private Label roleLabel;
   private Button chooseButton;
   private Role role;
   
   public RoleChooser(Composite parent, int style) {
      super(parent, style);

      GridLayout layout = new GridLayout();
      layout.numColumns = 3;
      layout.marginWidth = 0;
      setLayout(layout);
      
      roleCode = new Text(this, SWT.BORDER);
      roleCode.setData("roleCode");

      chooseButton = new Button(this, SWT.NONE);
      chooseButton.setText("...");
      chooseButton.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            RoleListView dialog = new RoleListView(getParent().getShell());

            if (dialog.display()) {
               setRole(dialog.getRole());
               fireRoleChooserEvent(RoleChooser.this);
            }
         }
      });

      GridData roleLabelGridData = new GridData();
      roleLabelGridData.grabExcessHorizontalSpace = true;
      roleLabelGridData.widthHint = 100;

      roleLabel = new Label(this, SWT.NONE);
      roleLabel.setData("roleLabel");
      roleLabel.setLayoutData(roleLabelGridData);
   }
   
   public void addRoleChooserListener(RoleChooserListener l) {
      addListener(RoleChanged, l);
   }

   public void removeRoleChooserListener(RoleChooserListener l) {
      removeListener(RoleChanged, l);
   }

   public Role getRole() {
      return role;
   }
   
   public void setRole(Role role) {
      roleLabel.setText((role == null) ? "" : role.getLabel());
      roleCode.setText((role == null) ? "" : role.getCode());
      this.role = role;
   }
   
   protected void fireRoleChooserEvent(RoleChooser roleChooser) {
      Event event = new Event();
      event.widget = this;
      event.type = RoleChanged;
      notifyListeners(event.type, event);
   }
}
