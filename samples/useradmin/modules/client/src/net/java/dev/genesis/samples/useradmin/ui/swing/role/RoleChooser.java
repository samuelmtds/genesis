/*
 * The Genesis Project
 * Copyright (C) 2005-2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.samples.useradmin.ui.swing.role;

import net.java.dev.genesis.samples.useradmin.databeans.Role;
import net.java.dev.genesis.samples.useradmin.ui.InsertRoleForm;
import net.java.dev.genesis.samples.useradmin.ui.RoleListForm;
import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.swing.ComponentBinderRegistry;
import net.java.dev.genesis.ui.swing.SwingBinder;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class RoleChooser extends JPanel {
   static {
      ComponentBinderRegistry.getInstance().register(RoleChooser.class,
            new RoleChooserComponentBinder());
   }

   private final JFrame owner;
   private JTextField roleCodeTextField;
   private JButton chooseButton;
   private JLabel roleLabel;
   private Role role;

   public RoleChooser(JFrame owner) {
      this.owner = owner;
      initialize();
   }

   private void initialize() {
      setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

      roleCodeTextField = new JTextField();
      roleCodeTextField.setName("roleCode");
      roleCodeTextField.setPreferredSize(new Dimension(50, 20));
      add(roleCodeTextField);

      chooseButton = new JButton("...");
      chooseButton.setPreferredSize(new Dimension(25, 20));
      chooseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
               RoleChooserDialog dialog = new RoleChooserDialog(owner);

               if (dialog.showView()) {
                  setRole(dialog.getRole());
                  fireRoleChooserEvent(RoleChooser.this);
               }
            }
         });
      add(chooseButton);

      roleLabel = new JLabel();
      roleLabel.setName("roleLabel");
      roleLabel.setPreferredSize(new Dimension(100, 20));
      add(roleLabel);
   }

   public void addRoleChooserListener(RoleChooserListener l) {
      listenerList.add(RoleChooserListener.class, l);
   }

   public void removeRoleChooserListener(RoleChooserListener l) {
      listenerList.remove(RoleChooserListener.class, l);
   }

   protected void fireRoleChooserEvent(RoleChooser roleChooser) {
      Object[] listeners = listenerList.getListenerList();

      for (int i = 0; i < listeners.length; i++) {
         if (listeners[i] == RoleChooserListener.class) {
            ((RoleChooserListener) listeners[i + 1]).roleChanged(roleChooser);
         }
      }
   }

   public Role getRole() {
      return role;
   }

   public void setRole(Role role) {
      roleLabel.setText((role == null) ? "" : role.getLabel());
      roleCodeTextField.setText((role == null) ? "" : role.getCode());
      this.role = role;
   }

   private static String getMessage(String key) {
      return UIUtils.getInstance().getBundle().getString(key);
   }

   /**
    * @ViewHandler
    */
   public static class RoleChooserDialog extends JDialog {
      private final RoleListForm form;
      private final SwingBinder binder;
      private boolean hasChanged;
      private JTable roles;
      private JButton add;
      private JButton remove;
      private JButton ok;
      private JButton cancel;

      public RoleChooserDialog(JFrame owner) {
         super(owner, getMessage("RoleListView.title"), true);
         binder = new SwingBinder(this, form = new RoleListForm());
         initialize();
         binder.bind();
      }

      private void initialize() {
         JPanel panel = new JPanel();
         setContentPane(panel);
         panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

         GridBagConstraints c = new GridBagConstraints();
         c.insets = new Insets(5, 5, 5, 5);
         c.fill = GridBagConstraints.HORIZONTAL;
         c.weightx = 1.0;
         panel.setLayout(new GridBagLayout());

         JScrollPane tablePane = new JScrollPane();
         tablePane.setBorder(new EtchedBorder());
         tablePane.setPreferredSize(new Dimension(360, 180));

         TableColumn codeColumn = new TableColumn(0);
         codeColumn.setIdentifier("code");
         codeColumn.setHeaderValue(getMessage("Role.code"));

         TableColumn labelColumn = new TableColumn(1);
         labelColumn.setIdentifier("label");
         labelColumn.setHeaderValue(getMessage("Role.label"));

         DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
         columnModel.addColumn(codeColumn);
         columnModel.addColumn(labelColumn);

         TableModel model =
            new DefaultTableModel() {
               public boolean isCellEditable(int row, int column) {
                  return false;
               }
            };

         roles = new JTable(model, columnModel);
         roles.setName("role");
         roles.setPreferredSize(new Dimension(350, 150));
         roles.setShowHorizontalLines(false);
         roles.setShowVerticalLines(false);
         roles.setColumnSelectionAllowed(false);
         roles.addMouseListener(new MouseAdapter() {
               public void mouseClicked(MouseEvent event) {
                  if (event.getClickCount() == 2) {
                     binder.invokeAction("select");
                  }
               }
            });

         tablePane.setViewportView(roles);
         c.gridx = 0;
         c.gridy = 0;
         panel.add(tablePane, c);

         JPanel buttonPanel = new JPanel();
         buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

         remove = new JButton(getMessage("button.remove"));
         remove.setName("remove");
         buttonPanel.add(remove);

         add = new JButton(getMessage("button.add"));
         add.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent event) {
                  create();
               }
            });
         buttonPanel.add(add);

         cancel = new JButton(getMessage("button.cancel"));
         cancel.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  cancel();
               }
            });
         buttonPanel.add(cancel);

         ok = new JButton(getMessage("button.ok"));
         ok.setName("select");
         buttonPanel.add(ok);

         c.gridx = 0;
         c.gridy = 1;
         panel.add(buttonPanel, c);

         pack();
         setLocationRelativeTo(getParent());
         setResizable(false);
         setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      }

      public boolean showView() {
         setVisible(true);

         return hasChanged;
      }

      public Role getRole() {
         return form.getRole();
      }

      public void cancel() {
         dispose();
      }

      /**
       * @AfterAction
       */
      public void select() {
         hasChanged = true;
         dispose();
      }

      public void create() {
         InsertRoleDialog dialog = new InsertRoleDialog(this);

         if (dialog.showView()) {
            binder.invokeAction("provideRoles");
         }
      }
   }

   /**
    * @ViewHandler
    */
   public static class InsertRoleDialog extends JDialog {
      private final SwingBinder binder;
      private boolean hasChanged;
      private JLabel codeLabel;
      private JLabel labelLabel;
      private JTextField code;
      private JTextField label;
      private JButton cancel;
      private JButton save;

      public InsertRoleDialog(Dialog owner) {
         super(owner, getMessage("InsertRoleView.title"));
         binder = new SwingBinder(this, new InsertRoleForm());
         initialize();
         binder.bind();
      }

      private void initialize() {
         JPanel panel = new JPanel();
         setContentPane(panel);
         panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

         GridBagConstraints c = new GridBagConstraints();
         c.insets = new Insets(5, 5, 5, 5);
         c.fill = GridBagConstraints.HORIZONTAL;
         c.weightx = 1.0;
         panel.setLayout(new GridBagLayout());

         codeLabel = new JLabel(getMessage("Role.code"));
         c.gridx = 0;
         c.gridy = 0;
         panel.add(codeLabel, c);

         code = new JTextField();
         code.setName("code");
         code.setPreferredSize(new Dimension(150, 20));
         c.gridx = 1;
         c.gridy = 0;
         panel.add(code, c);

         labelLabel = new JLabel(getMessage("Role.label"));
         c.gridx = 0;
         c.gridy = 1;
         panel.add(labelLabel, c);

         label = new JTextField();
         label.setName("label");
         label.setPreferredSize(new Dimension(150, 20));
         c.gridx = 1;
         c.gridy = 1;
         panel.add(label, c);

         JPanel buttonPanel = new JPanel();
         buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

         cancel = new JButton(getMessage("button.cancel"));
         cancel.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  cancel();
               }
            });
         buttonPanel.add(cancel);

         save = new JButton(getMessage("button.save"));
         save.setName("save");
         buttonPanel.add(save);

         c.gridx = 0;
         c.gridy = 2;
         c.gridwidth = 2;
         panel.add(buttonPanel, c);

         pack();
         setLocationRelativeTo(getParent());
         setResizable(false);
         setModal(true);
         setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      }

      public boolean showView() {
         setVisible(true);

         return hasChanged;
      }

      public void cancel() {
         dispose();
      }

      /**
       * @AfterAction
       */
      public void save() {
         hasChanged = true;
         dispose();
      }
   }
}
