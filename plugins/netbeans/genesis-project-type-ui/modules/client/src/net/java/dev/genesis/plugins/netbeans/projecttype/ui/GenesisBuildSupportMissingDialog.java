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
package net.java.dev.genesis.plugins.netbeans.projecttype.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import net.java.dev.genesis.ui.ActionInvoker;
import net.java.dev.genesis.ui.swing.SwingBinder;
import org.openide.windows.WindowManager;

/**
 * @ViewHandler
 */
public class GenesisBuildSupportMissingDialog extends JDialog {
   private final SwingBinder binder;
   private final GenesisBuildSupportMissingForm form;

   public GenesisBuildSupportMissingDialog(String version) throws Exception {
      super(WindowManager.getDefault().getMainWindow(), true);
      initComponents();

      setLocationRelativeTo(null);
      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            try {
               ActionInvoker.invoke(form, "cancel");
            } catch (Exception ex) {
               binder.handleException(ex);
            }
         }
      });

      binder = new SwingBinder(this, form = new GenesisBuildSupportMissingForm(
            version));
      binder.bind();
   }

   public String getVersion() {
      setVisible(true);
      binder.unbind();

      return form.getVersion() == null ? null : form.getVersion().getVersion()
            .toString();
   }

   /**
    * @AfterAction
    */
   public void upgrade() {
      close();
   }

   /**
    * @AfterAction
    */
   public void cancel() {
      close();
   }

   private void close() {
      setVisible(false);
      dispose();
   }

   // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
   private void initComponents() {
      message = new javax.swing.JLabel();
      versions = new javax.swing.JComboBox();
      upgrade = new javax.swing.JButton();
      cancel = new javax.swing.JButton();

      setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
      setTitle(org.openide.util.NbBundle.getMessage(GenesisBuildSupportMissingDialog.class, "TIT_MissingDialog"));
      message.setName("message");

      versions.setName("versions");

      upgrade.setText(org.openide.util.NbBundle.getMessage(GenesisBuildSupportMissingDialog.class, "BTN_Upgrade"));
      upgrade.setName("upgrade");

      cancel.setText(org.openide.util.NbBundle.getMessage(GenesisBuildSupportMissingDialog.class, "BTN_Cancel"));
      cancel.setName("cancel");

      org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(layout.createSequentialGroup()
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
               .add(layout.createSequentialGroup()
                  .add(136, 136, 136)
                  .add(upgrade)
                  .add(94, 94, 94)
                  .add(cancel))
               .add(layout.createSequentialGroup()
                  .addContainerGap()
                  .add(message))
               .add(layout.createSequentialGroup()
                  .add(224, 224, 224)
                  .add(versions, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap(145, Short.MAX_VALUE))
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(layout.createSequentialGroup()
            .addContainerGap()
            .add(message)
            .add(9, 9, 9)
            .add(versions, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(22, 22, 22)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
               .add(upgrade)
               .add(cancel))
            .addContainerGap(43, Short.MAX_VALUE))
      );
      pack();
   }// </editor-fold>//GEN-END:initComponents
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton cancel;
   private javax.swing.JLabel message;
   private javax.swing.JButton upgrade;
   private javax.swing.JComboBox versions;
   // End of variables declaration//GEN-END:variables
   
}
