package net.java.dev.genesis.plugins.netbeans.projecttype;

import java.util.MissingResourceException;
import javax.swing.event.ChangeListener;
import net.java.dev.reusablecomponents.lang.Enum;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.support.ant.SourcesHelper;
import org.openide.ErrorManager;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.util.NbBundle;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

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
      GenesisProjectKind kind = determineKind();

      if (kind == GenesisProjectKind.DESKTOP) {
         addClientSourcesDir("LBL_Client_Sources_Display_Name", 
               JavaProjectConstants.SOURCES_TYPE_JAVA, helper, 
               "${basedir}/modules/client/src");
      } else if (kind == GenesisProjectKind.WEB) {
         addClientSourcesDir("LBL_Web_Sources_Display_Name", 
               JavaProjectConstants.SOURCES_TYPE_JAVA, helper, 
               "${basedir}/modules/web/src");
      }

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

      sources = helper.createSources();

      return null;
   }

   private GenesisProjectKind determineKind() throws DOMException {
      GenesisProjectKind kind = null;

      Element data = project.getHelper().getPrimaryConfigurationData(true);
      NodeList nl = data.getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "type");

      if (nl.getLength() == 1) {
         nl = nl.item(0).getChildNodes();

         if (nl.getLength() == 1 && nl.item(0).getNodeType() == Node.TEXT_NODE) {
            kind = (GenesisProjectKind)Enum.get(GenesisProjectKind.class, 
                  ((Text) nl.item(0)).getNodeValue());
         }
      }

      return kind;
   }

   private void addClientSourcesDir(final String displayNameKey, 
         final String sourcesType, final SourcesHelper helper, 
         final String defaultClientSourcesDir) {
      if (!Boolean.FALSE.equals(project.getEvaluator().getProperty(
            "has.client.sources"))) {
         String clientSourcesDir = project.getEvaluator().getProperty(
               "client.sources.dir");

         if (clientSourcesDir == null) {
            clientSourcesDir = defaultClientSourcesDir;
         }

         String clientDisplayName = NbBundle.getMessage(GenesisSources.class,
               displayNameKey);
         helper.addPrincipalSourceRoot(clientSourcesDir, clientDisplayName, 
               null, null);
         helper.addTypedSourceRoot(clientSourcesDir, sourcesType, 
               clientDisplayName,  null, null);
      }
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
