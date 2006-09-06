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

package net.java.dev.genesis.samples.useradmin.ui.swt;

import net.java.dev.genesis.samples.useradmin.databeans.User;
import net.java.dev.genesis.samples.useradmin.ui.InsertUpdateForm;
import net.java.dev.genesis.samples.useradmin.ui.swt.role.RoleChooser;
import net.java.dev.genesis.samples.useradmin.ui.swt.role.RoleChooserWidgetBinder;
import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.swt.SWTBinder;
import net.java.dev.genesis.ui.swt.WidgetBinderRegistryFactory;

import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @ViewHandler
 */
public class InsertUpdateView extends Dialog {
   static {
      WidgetBinderRegistryFactory.getInstance().register(RoleChooser.class,
            new RoleChooserWidgetBinder());
   }

   private final InsertUpdateForm form;
   private boolean hasChanged;
   private final SWTBinder binder;

   private Shell shell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
   private Composite composite = null;
   private Label nameLabel = null;
   private Text name = null;
   private Label roleLabel = null;
   private Label loginLabel = null;
   private Text login = null;
   private Label passwordLabel = null;
   private Label emailLabel = null;
   private Label birthdayLabel = null;
   private Label addressLabel = null;
   private Label countryLabel = null;
   private Label stateLabel = null;
   private Text password = null;
   private Text email = null;
   private Text birthday = null;
   private Text address = null;
   private Combo country = null;
   private Combo state = null;
   private SashForm buttonsContainer = null;
   private Button cancel = null;
   private Button save = null;
   private RoleChooser roleChooser = null;
   
   public InsertUpdateView(Shell parent) throws Exception {
      this(parent, null);
   }
   
   public InsertUpdateView(Shell parent, User user) throws Exception {
      super(parent);

      form = new InsertUpdateForm();

      if (user != null) {
         PropertyUtils.copyProperties(form, user);
      }

      shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
      shell.setText(getMessage(user == null ? "InsertView.title" : "UpdateView.title"));
      binder = new SWTBinder(shell, form, this);
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
         shell.setText("Shell");
      }

      shell.setLayout(shellGridLayout);

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
      GridData roleGridData = new GridData();
      roleGridData.widthHint = 200;

      GridData loginGridData = new GridData();
      loginGridData.verticalAlignment = GridData.CENTER;
      loginGridData.horizontalAlignment = GridData.FILL;

      GridData passwordGridData = new GridData();
      passwordGridData.verticalAlignment = GridData.CENTER;
      passwordGridData.horizontalAlignment = GridData.FILL;

      GridData emailGridData = new GridData();
      emailGridData.verticalAlignment = GridData.CENTER;
      emailGridData.horizontalAlignment = GridData.FILL;

      GridData birthdayGridData = new GridData();
      birthdayGridData.verticalAlignment = GridData.CENTER;
      birthdayGridData.horizontalAlignment = GridData.FILL;

      GridData addressGridData = new GridData();
      addressGridData.verticalAlignment = GridData.CENTER;
      addressGridData.horizontalAlignment = GridData.FILL;

      GridData nameGridData = new GridData();
      nameGridData.horizontalAlignment = GridData.FILL;
      nameGridData.verticalAlignment = GridData.CENTER;
      nameGridData.widthHint = -1;

      GridLayout compositeGridLayout = new GridLayout();
      compositeGridLayout.numColumns = 2;

      composite = new Composite(shell, SWT.BORDER);
      composite.setLayout(compositeGridLayout);

      nameLabel = new Label(composite, SWT.NONE);
      nameLabel.setText(getMessage("User.name"));

      name = new Text(composite, SWT.BORDER);
      name.setLayoutData(nameGridData);
      name.setData("name");

      roleLabel = new Label(composite, SWT.NONE);
      roleLabel.setText(getMessage("User.role"));

      roleChooser = new RoleChooser(composite, SWT.NONE);
      roleChooser.setData("role");
      roleChooser.setLayoutData(roleGridData);

      loginLabel = new Label(composite, SWT.NONE);
      loginLabel.setText(getMessage("User.login"));

      login = new Text(composite, SWT.BORDER);
      login.setLayoutData(loginGridData);
      login.setData("login");

      passwordLabel = new Label(composite, SWT.NONE);
      passwordLabel.setText(getMessage("User.password"));

      password = new Text(composite, SWT.PASSWORD | SWT.BORDER);
      password.setLayoutData(passwordGridData);
      password.setData("password");

      emailLabel = new Label(composite, SWT.NONE);
      emailLabel.setText(getMessage("User.email"));

      email = new Text(composite, SWT.BORDER);
      email.setLayoutData(emailGridData);
      email.setData("email");

      birthdayLabel = new Label(composite, SWT.NONE);
      birthdayLabel.setText(getMessage("User.birthday"));

      birthday = new Text(composite, SWT.BORDER);
      birthday.setLayoutData(birthdayGridData);
      birthday.setData("birthday");

      addressLabel = new Label(composite, SWT.NONE);
      addressLabel.setText(getMessage("User.address"));

      address = new Text(composite, SWT.BORDER);
      address.setLayoutData(addressGridData);
      address.setData("address");

      countryLabel = new Label(composite, SWT.NONE);
      countryLabel.setText(getMessage("User.country"));

      createCountry();

      stateLabel = new Label(composite, SWT.NONE);
      stateLabel.setText(getMessage("User.state"));

      createState();
   }

   /**
    * This method initializes country	
    *
    */
   private void createCountry() {
      GridData countryGridData = new GridData();
      countryGridData.verticalAlignment = GridData.CENTER;
      countryGridData.horizontalAlignment = GridData.FILL;

      country = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
      country.setLayoutData(countryGridData);
      country.setData("country");
   }

   /**
    * This method initializes state	
    *
    */
   private void createState() {
      GridData stateGridData = new GridData();
      stateGridData.verticalAlignment = GridData.CENTER;
      stateGridData.horizontalAlignment = GridData.FILL;

      state = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
      state.setLayoutData(stateGridData);
      state.setData("state");
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
      buttonsContainer.setLayout(new FillLayout());
      buttonsContainer.setLayoutData(buttonsContainerGridData);

      cancel = new Button(buttonsContainer, SWT.NONE);
      cancel.setText(getMessage("button.cancel"));

      cancel.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            dispose();
         }
      });
      save = new Button(buttonsContainer, SWT.NONE);
      save.setText(getMessage("button.save"));
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
         if (!getParent().getDisplay().readAndDispatch()) {
            getParent().getDisplay().sleep();
         }
      }

      return hasChanged;
   }

   /**
    * @AfterAction
    */
   public void save() {
      hasChanged = true;
      dispose();
   }

   private void dispose() {
      shell.dispose();
   }
}
