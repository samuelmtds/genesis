package net.java.dev.genesis.samples.useradmin.ui.swt;

import net.java.dev.genesis.samples.useradmin.databeans.User;
import net.java.dev.genesis.samples.useradmin.ui.InsertUpdateForm;
import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.swt.SWTBinder;

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

   private final InsertUpdateForm form;
   private boolean hasChanged;
   private final SWTBinder binder;

   private Shell shell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
   private Composite composite = null;
   private Label nameLabel = null;
   private Text name = null;
   private Label roleLabel = null;
   private Text roleCode = null;
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
   private Button findRole = null;
   private Label role = null;
   
   public InsertUpdateView(Shell parent) throws Exception{
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
      GridLayout gridLayout1 = new GridLayout();
      gridLayout1.numColumns = 1;

      if (shell == null) {
         shell = new Shell();
         shell.setText("Shell");
      }

      shell.setLayout(gridLayout1);

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
      GridData gridData9 = new GridData();
      gridData9.widthHint = 80;
      GridData gridData8 = new GridData();
      gridData8.horizontalSpan = 3;
      gridData8.verticalAlignment = GridData.CENTER;
      gridData8.horizontalAlignment = GridData.FILL;
      GridData gridData7 = new GridData();
      gridData7.horizontalSpan = 3;
      gridData7.verticalAlignment = GridData.CENTER;
      gridData7.horizontalAlignment = GridData.FILL;
      GridData gridData6 = new GridData();
      gridData6.horizontalSpan = 3;
      gridData6.verticalAlignment = GridData.CENTER;
      gridData6.horizontalAlignment = GridData.FILL;
      GridData gridData5 = new GridData();
      gridData5.horizontalSpan = 3;
      gridData5.verticalAlignment = GridData.CENTER;
      gridData5.horizontalAlignment = GridData.FILL;
      GridData gridData4 = new GridData();
      gridData4.horizontalSpan = 3;
      gridData4.verticalAlignment = GridData.CENTER;
      gridData4.horizontalAlignment = GridData.FILL;
      GridData gridData1 = new GridData();
      gridData1.horizontalSpan = 3;
      gridData1.horizontalAlignment = GridData.FILL;
      gridData1.verticalAlignment = GridData.CENTER;
      gridData1.widthHint = -1;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 4;

      composite = new Composite(shell, SWT.BORDER);
      composite.setLayout(gridLayout);

      nameLabel = new Label(composite, SWT.NONE);
      nameLabel.setText(getMessage("User.name"));

      name = new Text(composite, SWT.BORDER);
      name.setLayoutData(gridData1);
      name.setData(SWTBinder.NAME_PROPERTY, "name");

      roleLabel = new Label(composite, SWT.NONE);
      roleLabel.setText(getMessage("User.role"));

      roleCode = new Text(composite, SWT.BORDER);
      roleCode.setData(SWTBinder.NAME_PROPERTY, "roleCode");

      findRole = new Button(composite, SWT.NONE);
      findRole.setText("...");

      findRole.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            RoleListView view = new RoleListView(shell);
            if (view.display()) {
               form.setRole(view.getRole());
               binder.refresh();
            }
         }
      });
      role = new Label(composite, SWT.NONE);
      role.setText("");
      role.setLayoutData(gridData9);
      role.setData(SWTBinder.NAME_PROPERTY, "roleLabel");

      loginLabel = new Label(composite, SWT.NONE);
      loginLabel.setText(getMessage("User.login"));

      login = new Text(composite, SWT.BORDER);
      login.setLayoutData(gridData8);
      login.setData(SWTBinder.NAME_PROPERTY, "login");

      passwordLabel = new Label(composite, SWT.NONE);
      passwordLabel.setText(getMessage("User.password"));

      password = new Text(composite, SWT.PASSWORD | SWT.BORDER);
      password.setLayoutData(gridData7);
      password.setData(SWTBinder.NAME_PROPERTY, "password");

      emailLabel = new Label(composite, SWT.NONE);
      emailLabel.setText(getMessage("User.email"));

      email = new Text(composite, SWT.BORDER);
      email.setLayoutData(gridData6);
      email.setData(SWTBinder.NAME_PROPERTY, "email");

      birthdayLabel = new Label(composite, SWT.NONE);
      birthdayLabel.setText(getMessage("User.birthday"));

      birthday = new Text(composite, SWT.BORDER);
      birthday.setLayoutData(gridData5);
      birthday.setData(SWTBinder.NAME_PROPERTY, "birthday");

      addressLabel = new Label(composite, SWT.NONE);
      addressLabel.setText(getMessage("User.address"));

      address = new Text(composite, SWT.BORDER);
      address.setLayoutData(gridData4);
      address.setData(SWTBinder.NAME_PROPERTY, "address");

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
      GridData gridData3 = new GridData();
      gridData3.horizontalSpan = 3;

      gridData3.verticalAlignment = GridData.CENTER;
      gridData3.horizontalAlignment = GridData.FILL;
      country = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
      country.setLayoutData(gridData3);
      country.setData(SWTBinder.NAME_PROPERTY, "country");
   }

   /**
    * This method initializes state	
    *
    */
   private void createState() {
      GridData gridData2 = new GridData();
      gridData2.horizontalSpan = 3;

      gridData2.verticalAlignment = GridData.CENTER;
      gridData2.horizontalAlignment = GridData.FILL;
      state = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
      state.setLayoutData(gridData2);
      state.setData(SWTBinder.NAME_PROPERTY, "state");
   }

   /**
    * This method initializes buttonsContainer	
    *
    */
   private void createButtonsContainer() {
      GridData gridData = new GridData();
      gridData.horizontalAlignment = GridData.CENTER;
      gridData.verticalAlignment = GridData.CENTER;

      buttonsContainer = new SashForm(shell, SWT.NONE);
      buttonsContainer.setLayout(new FillLayout());
      buttonsContainer.setLayoutData(gridData);

      cancel = new Button(buttonsContainer, SWT.NONE);
      cancel.setText(getMessage("button.cancel"));

      cancel.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            shell.dispose();
         }
      });
      save = new Button(buttonsContainer, SWT.NONE);
      save.setText(getMessage("button.save"));
      save.setData(SWTBinder.NAME_PROPERTY, "save");
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
      shell.dispose();
   }
}
