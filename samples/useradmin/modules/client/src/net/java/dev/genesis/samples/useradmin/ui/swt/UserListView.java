package net.java.dev.genesis.samples.useradmin.ui.swt;

import net.java.dev.genesis.samples.useradmin.UserAdmin;
import net.java.dev.genesis.samples.useradmin.ui.UserListForm;
import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.swt.SwtBinder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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
   private SwtBinder binder;
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
      binder = new SwtBinder(shell, form = new UserListForm(), this);
      createShell();
      binder.bind();
   }

   /**
    * This method initializes shell	
    *
    */
   private void createShell() {
      GridLayout gridLayout3 = new GridLayout();
      gridLayout3.numColumns = 1;

      if (shell == null) {
         shell = new Shell();
      }
      shell.setLayout(gridLayout3);

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
      GridData gridData = new GridData();
      gridData.horizontalAlignment = GridData.FILL;
      gridData.verticalAlignment = GridData.CENTER;

      upperContainer = new SashForm(shell, SWT.NONE);
      upperContainer.setLayoutData(gridData);

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
      GridData gridData3 = new GridData();
      gridData3.widthHint = 120;

      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      
      nameComposite = new Composite(upperContainer, SWT.NONE);
      nameComposite.setLayout(gridLayout);

      nameLabel = new Label(nameComposite, SWT.NONE);
      nameLabel.setText(getMessage("User.name"));
      
      name = new Text(nameComposite, SWT.BORDER);
      name.setLayoutData(gridData3);
      binder.register("name", name);
   }

   /**
    * This method initializes loginComposite	
    *
    */
   private void createLoginComposite() {
      GridData gridData4 = new GridData();
      gridData4.widthHint = 120;

      GridLayout gridLayout1 = new GridLayout();
      gridLayout1.numColumns = 2;

      loginComposite = new Composite(upperContainer, SWT.NONE);
      loginComposite.setLayout(gridLayout1);

      loginLabel = new Label(loginComposite, SWT.NONE);
      loginLabel.setText(getMessage("User.login"));

      login = new Text(loginComposite, SWT.BORDER);
      login.setLayoutData(gridData4);
      binder.register("login", login);
   }

   /**
    * This method initializes emailComposite	
    *
    */
   private void createEmailComposite() {
      GridData gridData5 = new GridData();
      gridData5.widthHint = 120;

      GridLayout gridLayout2 = new GridLayout();
      gridLayout2.numColumns = 2;

      emailComposite = new Composite(upperContainer, SWT.NONE);
      emailComposite.setLayout(gridLayout2);

      emailLabel = new Label(emailComposite, SWT.NONE);
      emailLabel.setText(getMessage("User.email"));

      email = new Text(emailComposite, SWT.BORDER);
      email.setLayoutData(gridData5);
      binder.register("email", email);
   }

   /**
    * This method initializes searchResetComposite	
    *
    */
   private void createSearchResetComposite() {
      FillLayout fillLayout = new FillLayout();
      fillLayout.spacing = 3;
      fillLayout.marginWidth = 3;
      fillLayout.marginHeight = 3;

      searchResetComposite = new Composite(upperContainer, SWT.NONE);
      searchResetComposite.setLayout(fillLayout);

      search = new Button(searchResetComposite, SWT.NONE);
      search.setText(getMessage("button.search"));
      binder.register("doSearch", search);

      reset = new Button(searchResetComposite, SWT.NONE);
      reset.setText(getMessage("button.reset"));
      binder.register("reset", reset);
   }

   /**
    * This method initializes usersComposite	
    *
    */
   private void createUsersComposite() {
      GridData gridData1 = new GridData();
      gridData1.widthHint = 730;
      gridData1.heightHint = 180;
      gridData1.verticalAlignment = GridData.CENTER;
      gridData1.horizontalAlignment = GridData.FILL;

      usersComposite = new Composite(shell, SWT.NONE | SWT.BORDER);
      usersComposite.setLayout(new GridLayout());

      users = new Table(usersComposite, SWT.MULTI | SWT.FULL_SELECTION | SWT.VIRTUAL| SWT.BORDER);
      users.setHeaderVisible(true);
      users.setLayoutData(gridData1);
      users.addMouseListener(new MouseAdapter() {
         public void mouseDoubleClick(MouseEvent e) {
            binder.invokeAction("update");
         }
      });
      binder.register("users", users);
      
      TableColumn columnName = new TableColumn(users, SWT.NONE);
      columnName.setText(getMessage("User.name"));
      columnName.setMoveable(true);
      columnName.setData(SwtBinder.TABLE_COLUMN_IDENTIFIER, "name");
      columnName.setWidth(200);
      
      TableColumn columnLogin = new TableColumn(users, SWT.NONE);
      columnLogin.setText(getMessage("User.login"));
      columnLogin.setMoveable(true);
      columnLogin.setData(SwtBinder.TABLE_COLUMN_IDENTIFIER, "login");
      columnLogin.setWidth(100);
      
      TableColumn columnEmail = new TableColumn(users, SWT.NONE);
      columnEmail.setText(getMessage("User.email"));
      columnEmail.setMoveable(true);
      columnEmail.setData(SwtBinder.TABLE_COLUMN_IDENTIFIER, "email");
      columnEmail.setWidth(140);
      
      TableColumn columnRole = new TableColumn(users, SWT.NONE);
      columnRole.setText(getMessage("User.role"));
      columnRole.setMoveable(true);
      columnRole.setData(SwtBinder.TABLE_COLUMN_IDENTIFIER, "role");
      columnRole.setWidth(110);
      
      TableColumn columnCountry = new TableColumn(users, SWT.NONE);
      columnCountry.setText(getMessage("User.country"));
      columnCountry.setMoveable(true);
      columnCountry.setData(SwtBinder.TABLE_COLUMN_IDENTIFIER, "country");
      columnCountry.setWidth(100);

      TableColumn columnState = new TableColumn(users, SWT.NONE);
      columnState.setText(getMessage("User.state"));
      columnState.setMoveable(true);
      columnState.setData(SwtBinder.TABLE_COLUMN_IDENTIFIER, "state");
      columnState.setWidth(100);

      createPagingComposite();
   }

   /**
    * This method initializes pagingComposite	
    *
    */
   private void createPagingComposite() {
      RowLayout rowLayout1 = new RowLayout();
      rowLayout1.spacing = 705;

      pagingComposite = new Composite(usersComposite, SWT.NONE);
      pagingComposite.setLayout(rowLayout1);

      previousPage = new Button(pagingComposite, SWT.NONE);
      previousPage.setText("<<");
      binder.register("previousPage", previousPage);

      nextPage = new Button(pagingComposite, SWT.NONE);
      nextPage.setText(">>");
      binder.register("nextPage", nextPage);
   }

   /**
    * This method initializes bottomComposite	
    *
    */
   private void createBottomComposite() {
      GridData gridData2 = new GridData();
      gridData2.horizontalAlignment = GridData.END;
      gridData2.verticalAlignment = GridData.CENTER;

      GridLayout gridLayout4 = new GridLayout();
      gridLayout4.numColumns = 3;

      bottomComposite = new Composite(shell, SWT.NONE);
      bottomComposite.setLayout(gridLayout4);
      bottomComposite.setLayoutData(gridData2);

      create = new Button(bottomComposite, SWT.NONE);
      create.setText(getMessage("button.newUser"));
      create.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
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
      binder.register("update", update);

      remove = new Button(bottomComposite, SWT.NONE);
      remove.setText(getMessage("button.removeUser"));
      binder.register("remove", remove);
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
