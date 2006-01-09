package net.java.dev.genesis.plugins.netbeans.projecttype;

import javax.swing.event.ChangeListener;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.support.ant.SourcesHelper;
import org.openide.ErrorManager;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.util.NbBundle;

public class GenesisSources implements Sources {
   private final GenesisProject project;
   private Sources sources;

   public GenesisSources(final GenesisProject project) {
      this.project = project;

      try {
         ProjectManager.mutex().readAccess(new Mutex.ExceptionAction() {
            public Object run() {
               return buildSources();
            }
         });
      } catch (MutexException ex) {
         ErrorManager.getDefault().notify(ex);
      }
   }

   private Object buildSources() {
      final SourcesHelper helper = new SourcesHelper(project.getHelper(),
            project.getEvaluator());
      
      if (!Boolean.FALSE.equals(project.getEvaluator().getProperty(
            "has.shared.sources"))) {
         String sharedSourcesDir = project.getEvaluator().getProperty(
               "shared.sources.dir");

         if (sharedSourcesDir == null) {
            //TODO: improve this handling
            sharedSourcesDir = "${basedir}/modules/shared/src";
         }

         String sharedDisplayName = NbBundle.getMessage(GenesisSources.class,
               "LBL_Shared_Sources_Display_Name");
         helper.addPrincipalSourceRoot(sharedSourcesDir, sharedDisplayName, 
               null, null);
         helper.addTypedSourceRoot(sharedSourcesDir, 
               JavaProjectConstants.SOURCES_TYPE_JAVA, sharedDisplayName,  
               null, null);
      }
/*      
      Element data = project.getHelper().getPrimaryConfigurationData(true);
      GenesisProjectKind kind = (GenesisProjectKind)Enum.get(
            GenesisProjectKind.class, data.getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "type")
            .item(0).getNodeValue());
*/

      sources = helper.createSources();

      return null;
   }

   public SourceGroup[] getSourceGroups(String type) {
      return sources.getSourceGroups(type);
   }

   public void addChangeListener(ChangeListener changeListener) {
      //TODO:
   }

   public void removeChangeListener(ChangeListener changeListener) {
      //TODO:
   }
}
