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
package net.java.dev.genesis.plugins.netbeans.projecttype;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import net.java.dev.genesis.plugins.netbeans.buildsupport.api.GenesisBuildSupportManager;
import net.java.dev.genesis.plugins.netbeans.buildsupport.spi.GenesisProjectKind;
import net.java.dev.genesis.plugins.netbeans.projecttype.classpath.ClassPathProviderImpl;
import net.java.dev.genesis.plugins.netbeans.projecttype.queries.SourceLevelQueryImpl;
import net.java.dev.genesis.plugins.netbeans.projecttype.ui.GenesisBuildSupportMissingDialog;
import net.java.dev.genesis.plugins.netbeans.projecttype.ui.project.GenesisLogicalViewProvider;
import net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer.GenesisCustomizerProvider;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.support.ant.ProjectXmlSavedHook;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GenesisProject implements Project {
   private static final String APPLICATION_NAME_PROPERTY = 
         "genesisBasedApplication.name";
   private static final String APPLICATION_DISPLAY_NAME_PROPERTY = 
         "genesisBasedApplication.prettyName";
   private static final Icon PROJECT_ICON =  new ImageIcon(Utilities.loadImage(
         Utils.ICON_PATH));

   private final class GenesisProjectInformation implements ProjectInformation {
      private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
      
      public GenesisProjectInformation() {
         getEvaluator().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
               if (APPLICATION_DISPLAY_NAME_PROPERTY.equals(
                     event.getPropertyName())) {
                  firePropertyChange(ProjectInformation.PROP_DISPLAY_NAME);
               }
            }
         });
      }

      public String getName() {
         return getEvaluator().getProperty(APPLICATION_NAME_PROPERTY);
      }

      public String getDisplayName() {
         return getEvaluator().getProperty(APPLICATION_DISPLAY_NAME_PROPERTY);
      }

      public Icon getIcon() {
         return PROJECT_ICON;
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

      void firePropertyChange(String property) {
         pcs.firePropertyChange(property, null, null);
      }
   }

   private final class GenesisProjectXmlSavedHook extends ProjectXmlSavedHook {
      protected void projectXmlSaved() throws IOException {
         try {
            generateBuildFiles(false, false);
         } catch (IOException ioe) {
            throw ioe;
         } catch (Exception e) {
            ErrorManager.getDefault().notify(e);
         }
      }
   }

   private final class GenesisProjectOpenedHook extends ProjectOpenedHook {
      protected void projectOpened() {
         try {
            generateBuildFiles(true, false);
         } catch (Exception e) {
            ErrorManager.getDefault().notify(e);
         }

         ClassPathProviderImpl cp = (ClassPathProviderImpl)getLookup().lookup(
               ClassPathProviderImpl.class);
         cp.register();
      }

      protected void projectClosed() {
         try {
            ProjectManager.getDefault().saveProject(GenesisProject.this);
         } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ioe);
         }

         ClassPathProviderImpl cp = (ClassPathProviderImpl)getLookup().lookup(
               ClassPathProviderImpl.class);
         cp.unregister();
      }
   }

   private final AntProjectHelper helper;
   private final PropertyEvaluator evaluator;
   private final AuxiliaryConfiguration auxiliaryConfiguration;
   private final GeneratedFilesHelper generatedFilesHelper;
   private final ReferenceHelper referenceHelper;
   private final Lookup lookup;

   GenesisProject(final AntProjectHelper helper) {
      this.helper = helper;
      evaluator = helper.getStandardPropertyEvaluator();
      auxiliaryConfiguration = helper.createAuxiliaryConfiguration();
      generatedFilesHelper = new GeneratedFilesHelper(helper);
      referenceHelper = new ReferenceHelper(helper, auxiliaryConfiguration, 
            evaluator);
      lookup = createLookup();
   }

   public AntProjectHelper getHelper() {
      return helper;
   }

   public PropertyEvaluator getEvaluator() {
      return evaluator;
   }

   public FileObject getProjectDirectory() {
      return getHelper().getProjectDirectory();
   }

   public Lookup getLookup() {
      return lookup;
   }

   private Lookup createLookup() {
      return Lookups.fixed(new Object[] {this,
         new GenesisProjectInformation(),
         auxiliaryConfiguration,
         getHelper().createCacheDirectoryProvider(),
         new GenesisActionProvider(this),
         new GenesisLogicalViewProvider(this),
         new GenesisSources(this),
         new GenesisCustomizerProvider(this),
         new GenesisProjectXmlSavedHook(),
         new GenesisProjectOpenedHook(),
         new SourceLevelQueryImpl(getHelper()),
         new ClassPathProviderImpl(this),
         new GenesisAntArtifactProvider(this)
         });
   }

   private void generateBuildFiles(boolean check, boolean overwriteGenesisHome) 
         throws Exception {
      GenesisProjectKind kind = Utils.getKind(this);
      final String version = Utils.getVersion(this);

      if (!GenesisBuildSupportManager.getInstance().generateBuildFiles(kind,
            generatedFilesHelper, version, check)) {
         handleMissingSupport(version);

         return;
      }
 
      FileObject genesisHomeDir = GenesisBuildSupportManager.getInstance()
            .getGenesisHome(version);

      if (genesisHomeDir == null) {
         //TODO: handle absence of genesis build support for the project
         ErrorManager.getDefault().log(ErrorManager.ERROR, "Libraries for " +
               "genesis version " + version + " could not be found!");
         return;
      }

      String currentGenesisHome = evaluator.getProperty("genesis.home");

      if (currentGenesisHome != null && !overwriteGenesisHome) {
         return;
      }

      final String reference = referenceHelper.createForeignFileReference(
            FileUtil.toFile(genesisHomeDir), null);

      if (reference.equals(currentGenesisHome)) {
         return;
      }

      try {
         ProjectManager.getDefault().mutex().writeAccess(
               new Mutex.ExceptionAction() {
            public Object run() throws IOException {
               EditableProperties properties = helper.getProperties(
                     AntProjectHelper.PROJECT_PROPERTIES_PATH);
               properties.setProperty("genesis.home", reference);
               helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, 
                     properties);
               ProjectManager.getDefault().saveProject(GenesisProject.this);

               return null;
            }
         });
      } catch (MutexException ex) {
         ErrorManager.getDefault().log(ErrorManager.ERROR, "Could not make " +
               "property genesis.home point to " + reference);
      }
   }

   private void handleMissingSupport(final String version) 
         throws InterruptedException, InvocationTargetException {
      Runnable r = new Runnable() {
         public void run() {
            try {
               final String newVersion = new GenesisBuildSupportMissingDialog(
                     version).getVersion();

               if (newVersion != null) {
                  ProjectManager.getDefault().mutex().writeAccess(
                        new Mutex.ExceptionAction() {
                     public Object run() throws Exception {
                        Element root = GenesisProject.this.getHelper()
                              .getPrimaryConfigurationData(true);
                        Node node = Utils.getVersionNode(root);
                        node.setNodeValue(newVersion);
                        helper.putPrimaryConfigurationData(root, true);
                        ProjectManager.getDefault().saveProject(
                              GenesisProject.this);

                        return null;
                     }
                  });

                  generateBuildFiles(true, true);
               }
            } catch (Exception ex) {
               ErrorManager.getDefault().notify(ex);
            }
         }
      };

      if (EventQueue.isDispatchThread()) {
         r.run();
      } else {
         EventQueue.invokeAndWait(r);
      }
   }
}