package net.java.dev.genesis.samples.useradmin.ui.swt;

import net.java.dev.genesis.samples.useradmin.databeans.Role;
import net.java.dev.genesis.samples.useradmin.ui.RoleListForm;
import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.swt.SwtBinder;

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
   private SwtBinder binder;
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
      binder = new SwtBinder(shell, form = new RoleListForm(), this);
      createShell();
      
      binder.bind();
   }

   /**
    * This method initializes shell
    */
   private void createShell() {
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 1;

      if (shell == null) {
         shell = new Shell();
         shell.setText("Shell");
      }
      shell.setLayout(gridLayout);

      createComposite();      
      createButtonsContainer();

      shell.pack();
   }

   /**
    * This method initializes composite	
    *
    */
   private void createComposite() {
      GridData gridData1 = new GridData();
      gridData1.horizontalAlignment = GridData.FILL;
      gridData1.widthHint = 300;
      gridData1.heightHint = 200;
      gridData1.verticalAlignment = GridData.CENTER;
      GridData gridData = new GridData();
      gridData.horizontalAlignment = GridData.FILL;
      gridData.verticalAlignment = GridData.CENTER;

      composite = new Composite(shell, SWT.BORDER);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(gridData);

      roles = new Table(composite, SWT.SINGLE | SWT.FULL_SELECTION | SWT.VIRTUAL| SWT.BORDER);
      roles.setHeaderVisible(true);
      roles.setLayoutData(gridData1);
      roles.setLinesVisible(true);
      roles.addMouseListener(new MouseAdapter() {
         public void mouseDoubleClick(MouseEvent e) {
            binder.invokeAction("select");
         }
      });
      binder.register("role", roles);
      
      TableColumn columnCode = new TableColumn(roles, SWT.NONE);
      columnCode.setText(getMessage("Role.code"));
      columnCode.setData(SwtBinder.TABLE_COLUMN_IDENTIFIER, "code");
      columnCode.setWidth(100);
      
      TableColumn columnLabel = new TableColumn(roles, SWT.NONE);
      columnLabel.setText(getMessage("Role.label"));
      columnLabel.setData(SwtBinder.TABLE_COLUMN_IDENTIFIER, "label");
      columnLabel.setWidth(200);
   }

   /**
    * This method initializes buttonsContainer	
    *
    */
   private void createButtonsContainer() {
      GridData gridData2 = new GridData();
      gridData2.horizontalAlignment = GridData.END;
      gridData2.verticalAlignment = GridData.CENTER;
      buttonsContainer = new SashForm(shell, SWT.NONE);
      buttonsContainer.setLayoutData(gridData2);
      remove = new Button(buttonsContainer, SWT.NONE);
      remove.setText("Remove");
      binder.register("remove", remove);

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
      binder.register("select", ok);
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
      shell.dispose();

      hasChanged = true;
   }

   public Role getRole() {
      return form.getRole();
   }
}
