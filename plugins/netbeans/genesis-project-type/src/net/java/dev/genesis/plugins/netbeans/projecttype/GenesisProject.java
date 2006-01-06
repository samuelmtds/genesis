package net.java.dev.genesis.plugins.netbeans.projecttype;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.Icon;
import net.java.dev.genesis.plugins.netbeans.projecttype.ui.GenesisLogicalViewProvider;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

public class GenesisProject implements Project {
   private static final String APPLICATION_NAME_PROPERTY = 
         "genesisBasedApplication.name";
   private static final String APPLICATION_DISPLAY_NAME_PROPERTY = 
         "genesisBasedApplication.prettyName";

   private final class GenesisProjectInformation implements ProjectInformation {
      private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

      public String getName() {
         return helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH)
               .getProperty(APPLICATION_NAME_PROPERTY);
      }

      public String getDisplayName() {
         return helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH)
               .getProperty(APPLICATION_DISPLAY_NAME_PROPERTY);
      }

      public Icon getIcon() {
         return null;
      }

      public Project getProject() {
         return GenesisProject.this;
      }

      public void addPropertyChangeListener(PropertyChangeListener listener) {
         pcs.addPropertyChangeListener(listener);
      }

      public void removePropertyChangeListener(PropertyChangeListener listener) {
         pcs.removePropertyChangeListener(listener);
      }
   }

   private final AntProjectHelper helper;
   private final AuxiliaryConfiguration auxiliaryConfiguration;
   private final Lookup lookup;

   GenesisProject(final AntProjectHelper helper) {
      this.helper = helper;
      auxiliaryConfiguration = helper.createAuxiliaryConfiguration();
      lookup = createLookup();
   }

   public FileObject getProjectDirectory() {
      return helper.getProjectDirectory();
   }

   public Lookup getLookup() {
      return lookup;
   }

   private Lookup createLookup() {
      return Lookups.fixed(new Object[] {this,
         new GenesisProjectInformation(),
         auxiliaryConfiguration,
         helper.createCacheDirectoryProvider(),
         new GenesisActionProvider(this),
         new GenesisLogicalViewProvider(this)
         });
   }
}
