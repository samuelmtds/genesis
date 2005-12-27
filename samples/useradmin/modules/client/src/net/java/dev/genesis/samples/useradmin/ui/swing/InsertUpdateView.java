/*
 * The Genesis Project
 * Copyright (C) 2005  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.samples.useradmin.ui.swing;

import net.java.dev.genesis.samples.useradmin.databeans.User;
import net.java.dev.genesis.samples.useradmin.ui.InsertUpdateForm;
import net.java.dev.genesis.samples.useradmin.ui.swing.role.RoleChooser;
import net.java.dev.genesis.samples.useradmin.ui.swing.role.RoleChooserComponentBinder;
import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.swing.ComponentBinderRegistryFactory;
import net.java.dev.genesis.ui.swing.SwingBinder;
import net.java.dev.genesis.ui.swing.renderers.KeyValueListCellRenderer;

import org.apache.commons.beanutils.PropertyUtils;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

/**
 * @ViewHandler
 */
public class InsertUpdateView extends JDialog {
   static {
      ComponentBinderRegistryFactory.getInstance()
                                       .register(RoleChooser.class,
            new RoleChooserComponentBinder());
   }

   private final InsertUpdateForm form;
   private boolean hasChanged;
   private final SwingBinder binder;
   private JLabel nameLabel;
   private JTextField nameTextField;
   private JLabel labelRole;
   private RoleChooser roleChooser;
   private JLabel loginLabel;
   private JTextField loginTextField;
   private JLabel passwordLabel;
   private JPasswordField passwordField;
   private JLabel emailLabel;
   private JTextField emailTextField;
   private JLabel birthdayLabel;
   private JTextField birthdayTextField;
   private JLabel addressLabel;
   private JTextField addressTextField;
   private JLabel countryLabel;
   private JComboBox countryComboBox;
   private JLabel stateLabel;
   private JComboBox stateComboBox;
   private JButton cancelButton;
   private JButton saveButton;

   public InsertUpdateView(JFrame frame) throws Exception {
      this(frame, null);
   }

   public InsertUpdateView(JFrame frame, User user) throws Exception {
      super(frame,
         getMessage((user == null) ? "InsertView.title" : "UpdateView.title"));
      form = new InsertUpdateForm();

      if (user != null) {
         PropertyUtils.copyProperties(form, user);
      }

      binder = new SwingBinder(this, form);
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

      nameLabel = new JLabel(getMessage("User.name"));
      nameLabel.setPreferredSize(new Dimension(80, 20));
      c.gridx = 0;
      c.gridy = 0;
      panel.add(nameLabel, c);

      nameTextField = new JTextField();
      nameTextField.setName("name");
      nameTextField.setPreferredSize(new Dimension(200, 20));
      c.gridx = 1;
      c.gridy = 0;
      panel.add(nameTextField, c);

      labelRole = new JLabel(getMessage("User.role"));
      c.gridx = 0;
      c.gridy = 1;
      panel.add(labelRole, c);

      roleChooser = new RoleChooser((JFrame) getParent());
      roleChooser.setName("role");
      c.gridx = 1;
      c.gridy = 1;
      c.insets = new Insets(5, 0, 5, 5);
      panel.add(roleChooser, c);

      loginLabel = new JLabel(getMessage("User.login"));
      c.insets = new Insets(5, 5, 5, 5);
      c.gridx = 0;
      c.gridy = 2;
      panel.add(loginLabel, c);

      loginTextField = new JTextField();
      loginTextField.setName("login");
      c.gridx = 1;
      c.gridy = 2;
      panel.add(loginTextField, c);

      passwordLabel = new JLabel(getMessage("User.password"));
      c.gridx = 0;
      c.gridy = 3;
      panel.add(passwordLabel, c);

      passwordField = new JPasswordField();
      passwordField.setName("password");
      c.gridx = 1;
      c.gridy = 3;
      panel.add(passwordField, c);

      emailLabel = new JLabel(getMessage("User.email"));
      c.gridx = 0;
      c.gridy = 4;
      panel.add(emailLabel, c);

      emailTextField = new JTextField();
      emailTextField.setName("email");
      c.gridx = 1;
      c.gridy = 4;
      panel.add(emailTextField, c);

      birthdayLabel = new JLabel(getMessage("User.birthday"));
      c.gridx = 0;
      c.gridy = 5;
      panel.add(birthdayLabel, c);

      birthdayTextField = new JTextField();
      birthdayTextField.setName("birthday");
      c.gridx = 1;
      c.gridy = 5;
      panel.add(birthdayTextField, c);

      addressLabel = new JLabel(getMessage("User.address"));
      c.gridx = 0;
      c.gridy = 6;
      panel.add(addressLabel, c);

      addressTextField = new JTextField();
      addressTextField.setName("address");
      c.gridx = 1;
      c.gridy = 6;
      panel.add(addressTextField, c);

      countryLabel = new JLabel(getMessage("User.country"));
      c.gridx = 0;
      c.gridy = 7;
      panel.add(countryLabel, c);

      countryComboBox = new JComboBox();
      countryComboBox.setName("country");
      countryComboBox.putClientProperty("value", "name");
      countryComboBox.putClientProperty("key", "name");
      countryComboBox.setRenderer(new KeyValueListCellRenderer(binder,
            countryComboBox));
      c.gridx = 1;
      c.gridy = 7;
      panel.add(countryComboBox, c);

      stateLabel = new JLabel(getMessage("User.state"));
      c.gridx = 0;
      c.gridy = 8;
      panel.add(stateLabel, c);

      stateComboBox = new JComboBox();
      stateComboBox.setName("state");
      stateComboBox.putClientProperty("value", "name");
      stateComboBox.putClientProperty("key", "name");
      stateComboBox.setRenderer(new KeyValueListCellRenderer(binder,
            stateComboBox));
      c.gridx = 1;
      c.gridy = 8;
      panel.add(stateComboBox, c);

      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new FlowLayout());

      cancelButton = new JButton(getMessage("button.cancel"));
      cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               dispose();
            }
         });
      buttonPanel.add(cancelButton);

      saveButton = new JButton(getMessage("button.save"));
      saveButton.setName("save");

      buttonPanel.add(saveButton);

      c.gridx = 0;
      c.gridy = 9;
      c.gridwidth = 2;
      panel.add(buttonPanel, c);

      pack();
      setLocationRelativeTo(getParent());
      setModal(true);
   }

   private static String getMessage(String key) {
      return UIUtils.getInstance().getBundle().getString(key);
   }

   public boolean showView() throws Exception {
      setVisible(true);

      return hasChanged;
   }

   /**
    * @AfterAction
    */
   public void save() throws Exception {
      this.hasChanged = true;
      dispose();
   }

   public void cancel() {
      dispose();
   }
}
