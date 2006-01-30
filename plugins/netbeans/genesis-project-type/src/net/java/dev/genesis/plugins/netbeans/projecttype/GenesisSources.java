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

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.support.ant.AntProjectEvent;
import org.netbeans.spi.project.support.ant.AntProjectListener;
import org.netbeans.spi.project.support.ant.SourcesHelper;
import org.openide.filesystems.FileObject;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GenesisSources implements Sources, AntProjectListener {
   public static final String SOURCES_TYPE_FOLDER = "genesis:folder";

   private final GenesisProject project;
   private final Collection listeners = new ArrayList();
   private Sources sources;
   private FileObject clientSourcesRoot;
   private FileObject sharedSourcesRoot;

   public GenesisSources(final GenesisProject project) {
      this.project = project;
      project.getHelper().addAntProjectListener(this);
   }

   private Sources buildSources() {
      final SourcesHelper helper = new SourcesHelper(project.getHelper(),
            project.getEvaluator());
      String clientSourcesDir = Utils.getClientSourcesDir(project);

      if (clientSourcesDir != null) {
         clientSourcesRoot = addSourcesDir(Utils.getClientSourcesDisplayKey(
               project), helper, clientSourcesDir);
      }

      String sharedSourcesDir = Utils.getSharedSourcesDir(project);

      if (sharedSourcesDir != null) {
         sharedSourcesRoot = addSourcesDir("LBL_Shared_Sources_Display_Name", 
               helper, sharedSourcesDir);
      }

      addCustomSourceFolders(helper);

      return helper.createSources();
   }

   private FileObject addSourcesDir(final String displayNameKey, 
         final SourcesHelper helper, final String sourcesDir) {
      String clientDisplayName = NbBundle.getMessage(GenesisSources.class,
            displayNameKey);
      helper.addPrincipalSourceRoot(sourcesDir, clientDisplayName, 
            null, null);
      helper.addTypedSourceRoot(sourcesDir, 
            JavaProjectConstants.SOURCES_TYPE_JAVA, clientDisplayName, null, 
            null);

      return project.getHelper().resolveFileObject(sourcesDir);
   }

   private void addCustomSourceFolders(SourcesHelper helper) {
      Element data = project.getHelper().getPrimaryConfigurationData(true);
      NodeList nl = data.getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "source-folders");

      if (nl.getLength() != 1) {
         return;
      }
      
      nl = ((Element)nl.item(0)).getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "source-group");

      for (int i = 0; i < nl.getLength(); i++) {
         Node node = nl.item(i);

         NodeList paths = ((Element)node).getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "source-path");

         for (int j = 0; j < paths.getLength(); j++) {
            NodeList path = paths.item(j).getChildNodes();

            if (path.getLength() != 1) {
               continue;
            }

            String location = path.item(0).getNodeValue();

            helper.addPrincipalSourceRoot(location, location, null, null);
            helper.addTypedSourceRoot(location, 
                  JavaProjectConstants.SOURCES_TYPE_JAVA, location, null, null);
         }
      }
   }

   public SourceGroup[] getSourceGroups(final String type) {
      return (SourceGroup[])ProjectManager.mutex().readAccess(new Mutex.Action() {
         public Object run() {
            synchronized (GenesisSources.this) {
               if (sources == null) {
                  sources = buildSources();
               }

               return sources.getSourceGroups(type);
            }
         }
      });
   }

   public void addChangeListener(ChangeListener listener) {
      synchronized (listeners) {
         listeners.add(listener);
      }
   }

   public void removeChangeListener(ChangeListener listener) {
      synchronized (listeners) {
         listeners.remove(listener);
      }
   }

   private void fireChanges() {
      ChangeListener[] listeners;

      synchronized (this.listeners) {
         sources = null;
         clientSourcesRoot = null;
         sharedSourcesRoot = null;

         if (this.listeners.isEmpty()) {
            return;
         }

         listeners = (ChangeListener[])this.listeners.toArray(
               new ChangeListener[this.listeners.size()]);
      }

      ChangeEvent event = new ChangeEvent(this);

      for (int i = 0; i < listeners.length; i++) {
         listeners[i].stateChanged(event);
      }
   }
   
   public void configurationXmlChanged(AntProjectEvent event) {
      fireChanges();
   }

   public void propertiesChanged(AntProjectEvent event) {
      fireChanges();
   }

   public FileObject getClientSourcesRoot() {
      return clientSourcesRoot;
   }

   public FileObject getSharedSourcesRoot() {
      return sharedSourcesRoot;
   }
}