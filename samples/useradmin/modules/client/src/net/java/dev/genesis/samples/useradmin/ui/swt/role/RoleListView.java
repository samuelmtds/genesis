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
import net.java.dev.genesis.samples.useradmin.ui.RoleListForm;
import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.swt.SWTBinder;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;

/**
 * @ViewHandler
 */
public class RoleListView extends Dialog {

   private final RoleListForm form;
   private SWTBinder binder;
   private boolean hasChanged;
   
   private Shell shell = null;
   private Composite composite = null;
   private Table roles = null;
   private SashForm buttonsContainer = null;
   private Button add = null;
   private Button remove = null;
   private Button ok = null;
   private Button cancel = null;

   public RoleListView(Shell parent) {
      super(parent);
      shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
      shell.setText(getMessage("RoleListView.title"));
      binder = new SWTBinder(shell, form = new RoleListForm(), this);
      createShell();
      
      binder.bind();
   }

   /**
    * This method initializes shell
    */
   private void createShell() {
      GridLayout shellGridLayout = new GridLayout();
      shellGridLayout.numColumns = 1;

      if (shell == null) {
         shell = new Shell();
         shell.setText(getMessage("RoleListView.title"));
      }
      shell.setLayout(shellGridLayout);

      createComposite();      
      createButtonsContainer();

      shell.pack();
   }

   /**
    * This method initializes composite	
    *
    */
   private void createComposite() {
      GridData rolesGridData = new GridData();
      rolesGridData.horizontalAlignment = GridData.FILL;
      rolesGridData.widthHint = 300;
      rolesGridData.heightHint = 200;
      rolesGridData.verticalAlignment = GridData.CENTER;

      GridData compositeGridData = new GridData();
      compositeGridData.horizontalAlignment = GridData.FILL;
      compositeGridData.verticalAlignment = GridData.CENTER;

      composite = new Composite(shell, SWT.BORDER);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(compositeGridData);

      roles = new Table(composite, SWT.SINGLE | SWT.FULL_SELECTION | SWT.VIRTUAL| SWT.BORDER);
      roles.setHeaderVisible(true);
      roles.setLayoutData(rolesGridData);
      roles.setLinesVisible(true);
      roles.addMouseListener(new MouseAdapter() {
         public void mouseDoubleClick(MouseEvent e) {
            binder.invokeAction("select");
         }
      });
      roles.setData("role");
      
      TableColumn columnCode = new TableColumn(roles, SWT.NONE);
      columnCode.setText(getMessage("Role.code"));
      columnCode.setData(SWTBinder.TABLE_COLUMN_IDENTIFIER, "code");
      columnCode.setWidth(100);
      
      TableColumn columnLabel = new TableColumn(roles, SWT.NONE);
      columnLabel.setText(getMessage("Role.label"));
      columnLabel.setData(SWTBinder.TABLE_COLUMN_IDENTIFIER, "label");
      columnLabel.setWidth(200);
   }

   /**
    * This method initializes buttonsContainer	
    *
    */
   private void createButtonsContainer() {
      GridData buttonsContainerGridData = new GridData();
      buttonsContainerGridData.horizontalAlignment = GridData.END;
      buttonsContainerGridData.verticalAlignment = GridData.CENTER;

      buttonsContainer = new SashForm(shell, SWT.NONE);
      buttonsContainer.setLayoutData(buttonsContainerGridData);
      remove = new Button(buttonsContainer, SWT.NONE);
      remove.setText("Remove");
      remove.setData("remove");

      add = new Button(buttonsContainer, SWT.NONE);
      add.setText("Add");
      add.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            if (new InsertRoleView(shell).display()) {
               binder.invokeAction("provideRoles");
            }
         }
      });

      cancel = new Button(buttonsContainer, SWT.NONE);
      cancel.setText("Cancel");
      cancel.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            binder.invokeAction("select");
         }
      });

      ok = new Button(buttonsContainer, SWT.NONE);
      ok.setText("Ok");
      ok.setData("ok");
   }

   private static String getMessage(String key) {
      return UIUtils.getInstance().getBundle().getString(key);
   }

   public boolean display() {
      Rectangle bounds = shell.getParent().getBounds();
      shell.setLocation(bounds.x + (bounds.width - shell.getBounds().width) / 2,
            bounds.y + (bounds.height - shell.getBounds().height) / 2);

      shell.open();
      while (!shell.isDisposed()) {
         if (!shell.getDisplay().readAndDispatch()) {
            shell.getDisplay().sleep();
         }
      }

      return hasChanged;
   }

   /**
    * @AfterAction
    */
   public void select() throws Exception {
      dispose();

      hasChanged = true;
   }

   public Role getRole() {
      return form.getRole();
   }
   
   private void dispose() {
      shell.dispose();
   }
}
