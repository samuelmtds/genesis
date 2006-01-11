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
package net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer;

import java.awt.Dialog;
import javax.swing.JComponent;
import javax.swing.JPanel;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProject;
import org.netbeans.spi.project.ui.CustomizerProvider;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;

public class GenesisCustomizerProvider implements CustomizerProvider {
   private final GenesisProject project;

   public GenesisCustomizerProvider(final GenesisProject project) {
      this.project = project;
   }

   public void showCustomizer() {
      Dialog d = ProjectCustomizer.createCustomizerDialog(
            new ProjectCustomizer.Category[] {ProjectCustomizer.Category.create(
            "Test", "Sample category", null, null)}, 
            new ProjectCustomizer.CategoryComponentProvider() {
               public JComponent create(ProjectCustomizer.Category category) {
                  return new JPanel();
               }
            }, null, null, null);
      d.setVisible(true);
   }
}
