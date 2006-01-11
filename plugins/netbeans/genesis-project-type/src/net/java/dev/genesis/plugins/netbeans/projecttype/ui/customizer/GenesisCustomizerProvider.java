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
