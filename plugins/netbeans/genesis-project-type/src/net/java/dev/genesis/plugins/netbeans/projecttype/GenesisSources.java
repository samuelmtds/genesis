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
import net.java.dev.reusablecomponents.lang.Enum;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.support.ant.AntProjectEvent;
import org.netbeans.spi.project.support.ant.AntProjectListener;
import org.netbeans.spi.project.support.ant.SourcesHelper;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class GenesisSources implements Sources, AntProjectListener {
   public static final String SOURCES_TYPE_FOLDER = "genesis:folder";

   private final GenesisProject project;
   private final Collection listeners = new ArrayList();
   private Sources sources;

   public GenesisSources(final GenesisProject project) {
      this.project = project;
      project.getHelper().addAntProjectListener(this);
   }

   private Sources buildSources() {
      final SourcesHelper helper = new SourcesHelper(project.getHelper(),
            project.getEvaluator());
      GenesisProjectKind kind = Utils.determineKind(project);

      if (kind == GenesisProjectKind.DESKTOP) {
         addClientSourcesDir("LBL_Client_Sources_Display_Name", 
               JavaProjectConstants.SOURCES_TYPE_JAVA, helper, 
               "${basedir}/modules/client/src");
      } else if (kind == GenesisProjectKind.WEB) {
         addClientSourcesDir("LBL_Web_Sources_Display_Name", 
               JavaProjectConstants.SOURCES_TYPE_JAVA, helper, 
               "${basedir}/modules/web/src");
      }

      if (!"false".equals(project.getEvaluator().getProperty(
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

      addCustomSourceFolders(helper);

      return helper.createSources();
   }

   private void addClientSourcesDir(final String displayNameKey, 
         final String sourcesType, final SourcesHelper helper, 
         final String defaultClientSourcesDir) {
      if (!"false".equals(project.getEvaluator().getProperty(
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

   private void addCustomSourceFolders(SourcesHelper helper) {
      Element data = project.getHelper().getPrimaryConfigurationData(true);
      NodeList nl = data.getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "view");

      if (nl.getLength() != 1) {
         return;
      }
      
      nl = ((Element)nl.item(0)).getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "items");

      if (nl.getLength() != 1) {
         return;
      }

      nl = ((Element)nl.item(0)).getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "source-folder");

      for (int i = 0; i < nl.getLength(); i++) {
         Node node = nl.item(i);
         Node styleNode = node.getAttributes().getNamedItem("style");
         String type = SOURCES_TYPE_FOLDER;

         if (styleNode == null || "packages".equals(styleNode.getNodeValue())) {
            type = JavaProjectConstants.SOURCES_TYPE_JAVA;
         }

         NodeList subnodes = ((Element)node).getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "label");

         if (subnodes.getLength() != 1) {
            continue;
         }

         subnodes = subnodes.item(0).getChildNodes();

         if (subnodes.getLength() != 1) {
            continue;
         }

         String displayName = subnodes.item(0).getNodeValue();

         subnodes = ((Element)node).getElementsByTagNameNS(
            GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "location");

         if (subnodes.getLength() != 1) {
            continue;
         }

         subnodes = subnodes.item(0).getChildNodes();

         if (subnodes.getLength() != 1) {
            continue;
         }

         String location = subnodes.item(0).getNodeValue();

         helper.addPrincipalSourceRoot(location, displayName, null, null);
         helper.addTypedSourceRoot(location, type, displayName, null, null);
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
}
