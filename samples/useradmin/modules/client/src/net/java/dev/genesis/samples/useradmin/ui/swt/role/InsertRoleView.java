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

import net.java.dev.genesis.samples.useradmin.ui.InsertRoleForm;
import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.swt.SWTBinder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @ViewHandler
 */
public class InsertRoleView extends Dialog {
   private SWTBinder binder;
   private boolean hasChanged;
   
   private Shell shell = null;
   private Composite composite = null;
   private Label codeLabel = null;
   private Text code = null;
   private Label labelLabel = null;
   private Text label = null;
   private SashForm buttonsContainer = null;
   private Button cancel = null;
   private Button save = null;
   
   public InsertRoleView(Shell parent) {
      super(parent);
      shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
      shell.setText(getMessage("InsertRoleView.title"));
      binder = new SWTBinder(shell, new InsertRoleForm(), this);
      createShell();
      binder.bind();
   }
   
   /**
    * This method initializes shell
    */
   private void createShell() {
      if (shell == null) {
         shell = new Shell();
         shell.setText("Shell");
      }
      shell.setLayout(new GridLayout());

      createComposite();
      createButtonsContainer();
      shell.setDefaultButton(save);
      
      shell.pack();
   }

   /**
    * This method initializes composite	
    *
    */
   private void createComposite() {
      GridData labelGridData = new GridData();
      labelGridData.horizontalAlignment = GridData.FILL;
      labelGridData.verticalAlignment = GridData.CENTER;

      GridData codeGridData = new GridData();
      codeGridData.horizontalAlignment = GridData.FILL;
      codeGridData.widthHint = 150;
      codeGridData.verticalAlignment = GridData.CENTER;

      GridLayout compositeGridLayout = new GridLayout();
      compositeGridLayout.numColumns = 2;
      composite = new Composite(shell, SWT.BORDER);
      composite.setLayout(compositeGridLayout);

      codeLabel = new Label(composite, SWT.NONE);
      codeLabel.setText("Code");

      code = new Text(composite, SWT.BORDER);
      code.setLayoutData(codeGridData);
      code.setData("code");

      labelLabel = new Label(composite, SWT.NONE);
      labelLabel.setText("Label");

      label = new Text(composite, SWT.BORDER);
      label.setLayoutData(labelGridData);
      label.setData("label");
   }

   /**
    * This method initializes buttonsContainer	
    *
    */
   private void createButtonsContainer() {
      GridData buttonsContainerGridData = new GridData();
      buttonsContainerGridData.horizontalAlignment = GridData.CENTER;
      buttonsContainerGridData.verticalAlignment = GridData.CENTER;

      buttonsContainer = new SashForm(shell, SWT.NONE);
      buttonsContainer.setLayoutData(buttonsContainerGridData);

      cancel = new Button(buttonsContainer, SWT.NONE);
      cancel.setText("Cancel");

      cancel.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            dispose();
         }
      });
      save = new Button(buttonsContainer, SWT.NONE);
      save.setText("Save");
      save.setData("save");
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
   public void save() throws Exception {
      hasChanged = true;

      dispose();
   }

   private void dispose() {
      shell.dispose();
   }
}
