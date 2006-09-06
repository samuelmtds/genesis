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

import net.java.dev.genesis.samples.useradmin.UserAdmin;
import net.java.dev.genesis.samples.useradmin.ui.UserListForm;
import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.swt.SWTBinder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * @ViewHandler
 */
public class UserListView {
   private SWTBinder binder;
   private UserListForm form;
   
   private Shell shell = null;  //  @jve:decl-index=0:visual-constraint="10,2"
   private SashForm upperContainer = null;
   private Composite nameComposite = null;
   private Label nameLabel = null;
   private Text name = null;
   private Composite loginComposite = null;
   private Composite emailComposite = null;
   private Label loginLabel = null;
   private Label emailLabel = null;
   private Text login = null;
   private Text email = null;
   private Composite searchResetComposite = null;
   private Button search = null;
   private Button reset = null;
   private Composite usersComposite = null;
   private Table users = null;
   private Composite pagingComposite = null;
   private Button previousPage = null;
   private Button nextPage = null;
   private Composite bottomComposite = null;
   private Button create = null;
   private Button update = null;
   private Button remove = null;
   
   public UserListView() {
      shell = new Shell(SWT.TITLE | SWT.CLOSE);
      shell.setText(getMessage("UserListView.title"));

      binder = new SWTBinder(shell, form = new UserListForm(), this);
      createShell();
      binder.bind();
   }

   /**
    * This method initializes shell	
    *
    */
   private void createShell() {
      GridLayout shellGridLayout = new GridLayout();
      shellGridLayout.numColumns = 1;

      if (shell == null) {
         shell = new Shell();
      }
      shell.setLayout(shellGridLayout);

      createUpperContainer();
      createUsersComposite();
      createBottomComposite();
      shell.setDefaultButton(search);

      shell.pack();
   }

   /**
    * This method initializes upperContainer	
    *
    */
   private void createUpperContainer() {
      GridData upperContainerGridData = new GridData();
      upperContainerGridData.horizontalAlignment = GridData.FILL;
      upperContainerGridData.verticalAlignment = GridData.CENTER;

      upperContainer = new SashForm(shell, SWT.NONE);
      upperContainer.setLayoutData(upperContainerGridData);

      createNameComposite();
      createLoginComposite();
      createEmailComposite();
      createSearchResetComposite();
   }

   /**
    * This method initializes nameComposite	
    *
    */
   private void createNameComposite() {
      GridData nameGridData = new GridData();
      nameGridData.widthHint = 120;

      GridLayout nameCompositeGridLayout = new GridLayout();
      nameCompositeGridLayout.numColumns = 2;
      
      nameComposite = new Composite(upperContainer, SWT.NONE);
      nameComposite.setLayout(nameCompositeGridLayout);

      nameLabel = new Label(nameComposite, SWT.NONE);
      nameLabel.setText(getMessage("User.name"));
      
      name = new Text(nameComposite, SWT.BORDER);
      name.setLayoutData(nameGridData);
      name.setData("name");
   }

   /**
    * This method initializes loginComposite	
    *
    */
   private void createLoginComposite() {
      GridData loginGridData = new GridData();
      loginGridData.widthHint = 120;

      GridLayout loginCompositeGridLayout = new GridLayout();
      loginCompositeGridLayout.numColumns = 2;

      loginComposite = new Composite(upperContainer, SWT.NONE);
      loginComposite.setLayout(loginCompositeGridLayout);

      loginLabel = new Label(loginComposite, SWT.NONE);
      loginLabel.setText(getMessage("User.login"));

      login = new Text(loginComposite, SWT.BORDER);
      login.setLayoutData(loginGridData);
      login.setData("login");
   }

   /**
    * This method initializes emailComposite	
    *
    */
   private void createEmailComposite() {
      GridData emailGridData = new GridData();
      emailGridData.widthHint = 120;

      GridLayout emailCompositeGridLayout = new GridLayout();
      emailCompositeGridLayout.numColumns = 2;

      emailComposite = new Composite(upperContainer, SWT.NONE);
      emailComposite.setLayout(emailCompositeGridLayout);

      emailLabel = new Label(emailComposite, SWT.NONE);
      emailLabel.setText(getMessage("User.email"));

      email = new Text(emailComposite, SWT.BORDER);
      email.setLayoutData(emailGridData);
      email.setData("email");
   }

   /**
    * This method initializes searchResetComposite	
    *
    */
   private void createSearchResetComposite() {
      FillLayout searchResetCompositeLayout = new FillLayout();
      searchResetCompositeLayout.spacing = 3;
      searchResetCompositeLayout.marginWidth = 3;
      searchResetCompositeLayout.marginHeight = 3;

      searchResetComposite = new Composite(upperContainer, SWT.NONE);
      searchResetComposite.setLayout(searchResetCompositeLayout);

      search = new Button(searchResetComposite, SWT.NONE);
      search.setText(getMessage("button.search"));
      search.setData("doSearch");
      search.setData("doSearch");

      reset = new Button(searchResetComposite, SWT.NONE);
      reset.setText(getMessage("button.reset"));
      reset.setData("reset");
   }

   /**
    * This method initializes usersComposite	
    *
    */
   private void createUsersComposite() {
      GridData usersGridData = new GridData();
      usersGridData.widthHint = 730;
      usersGridData.heightHint = 180;
      usersGridData.verticalAlignment = GridData.CENTER;
      usersGridData.horizontalAlignment = GridData.FILL;

      usersComposite = new Composite(shell, SWT.NONE | SWT.BORDER);
      usersComposite.setLayout(new GridLayout());

      users = new Table(usersComposite, SWT.MULTI | SWT.FULL_SELECTION | SWT.VIRTUAL| SWT.BORDER);
      users.setHeaderVisible(true);
      users.setLayoutData(usersGridData);
      users.addMouseListener(new MouseAdapter() {
         public void mouseDoubleClick(MouseEvent e) {
            binder.invokeAction("update");
         }
      });
      users.setData("users");

      TableColumn columnName = new TableColumn(users, SWT.NONE);
      columnName.setText(getMessage("User.name"));
      columnName.setMoveable(true);
      columnName.setData(SWTBinder.TABLE_COLUMN_IDENTIFIER, "name");
      columnName.setWidth(200);

      TableColumn columnLogin = new TableColumn(users, SWT.NONE);
      columnLogin.setText(getMessage("User.login"));
      columnLogin.setMoveable(true);
      columnLogin.setData(SWTBinder.TABLE_COLUMN_IDENTIFIER, "login");
      columnLogin.setWidth(100);

      TableColumn columnEmail = new TableColumn(users, SWT.NONE);
      columnEmail.setText(getMessage("User.email"));
      columnEmail.setMoveable(true);
      columnEmail.setData(SWTBinder.TABLE_COLUMN_IDENTIFIER, "email");
      columnEmail.setWidth(140);

      TableColumn columnRole = new TableColumn(users, SWT.NONE);
      columnRole.setText(getMessage("User.role"));
      columnRole.setMoveable(true);
      columnRole.setData(SWTBinder.TABLE_COLUMN_IDENTIFIER, "role");
      columnRole.setWidth(110);

      TableColumn columnCountry = new TableColumn(users, SWT.NONE);
      columnCountry.setText(getMessage("User.country"));
      columnCountry.setMoveable(true);
      columnCountry.setData(SWTBinder.TABLE_COLUMN_IDENTIFIER, "country");
      columnCountry.setWidth(100);

      TableColumn columnState = new TableColumn(users, SWT.NONE);
      columnState.setText(getMessage("User.state"));
      columnState.setMoveable(true);
      columnState.setData(SWTBinder.TABLE_COLUMN_IDENTIFIER, "state");
      columnState.setWidth(100);

      createPagingComposite();
   }

   /**
    * This method initializes pagingComposite	
    *
    */
   private void createPagingComposite() {
      RowLayout pagingCompositeLayout = new RowLayout();
      pagingCompositeLayout.spacing = 705;

      pagingComposite = new Composite(usersComposite, SWT.NONE);
      pagingComposite.setLayout(pagingCompositeLayout);

      previousPage = new Button(pagingComposite, SWT.NONE);
      previousPage.setText("<<");
      previousPage.setData("previousPage");

      nextPage = new Button(pagingComposite, SWT.NONE);
      nextPage.setText(">>");
      nextPage.setData("nextPage");
   }

   /**
    * This method initializes bottomComposite	
    *
    */
   private void createBottomComposite() {
      GridData bottomCompositeGridData = new GridData();
      bottomCompositeGridData.horizontalAlignment = GridData.END;
      bottomCompositeGridData.verticalAlignment = GridData.CENTER;

      GridLayout bottomCompositeGridLayout = new GridLayout();
      bottomCompositeGridLayout.numColumns = 3;

      bottomComposite = new Composite(shell, SWT.NONE);
      bottomComposite.setLayout(bottomCompositeGridLayout);
      bottomComposite.setLayoutData(bottomCompositeGridData);

      create = new Button(bottomComposite, SWT.NONE);
      create.setText(getMessage("button.newUser"));
      create.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            try {
               if (new InsertUpdateView(shell).display()) {
                  runSearch();
                  binder.refresh();
               }
            } catch (Exception e) {
               binder.handleException(e);
            }
         }
      });

      update = new Button(bottomComposite, SWT.NONE);
      update.setText(getMessage("button.updateUser"));
      update.setData("update");

      remove = new Button(bottomComposite, SWT.NONE);
      remove.setText(getMessage("button.removeUser"));
      remove.setData("remove");
   }

   public void display() throws Exception {
      final Display display = Display.getCurrent();
      Rectangle bounds = display.getBounds();
      shell.setLocation(bounds.x
            + (bounds.width - shell.getBounds().width) / 2, bounds.y
            + (bounds.height - shell.getBounds().height) / 2);

      shell.open();
      shell.forceActive();
      
      while (!shell.isDisposed()) {
         if (!display.readAndDispatch()) {
            display.sleep();
         }
      }

      display.dispose();

      UserAdmin.showMainWindow();
   }

   private static String getMessage(String key) {
      return UIUtils.getInstance().getBundle().getString(key);
   }
   
   /**
    * @BeforeAction
    */
   public void update() throws Exception {
      final InsertUpdateView view = new InsertUpdateView(shell, form.getUser());

      if (view.display()) {
         runSearch();
      }
   }

   /**
    * @BeforeAction("remove")
    */
   public boolean confirmRemove() {
      MessageBox box = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES
            | SWT.NO | SWT.CANCEL);
      box.setText(getMessage("UserListView.deleteConfirmationTitle"));
      box.setMessage(getMessage("UserListView.deleteConfirmation"));

      return box.open() == SWT.YES;
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
