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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import net.java.dev.genesis.plugins.netbeans.projecttype.ui.GenesisLogicalViewProvider;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

public class GenesisProject implements Project {
   private static final String APPLICATION_NAME_PROPERTY = 
         "genesisBasedApplication.name";
   private static final String APPLICATION_DISPLAY_NAME_PROPERTY = 
         "genesisBasedApplication.prettyName";
   private static final Icon PROJECT_ICON =  new ImageIcon(Utilities.loadImage(
         Utils.ICON_PATH));

   private final class GenesisProjectInformation implements ProjectInformation {
      private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

      public String getName() {
         return getHelper().getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH)
               .getProperty(APPLICATION_NAME_PROPERTY);
      }

      public String getDisplayName() {
         return getHelper().getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH)
               .getProperty(APPLICATION_DISPLAY_NAME_PROPERTY);
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
   }

   private final AntProjectHelper helper;
   private final PropertyEvaluator evaluator;
   private final AuxiliaryConfiguration auxiliaryConfiguration;
   private final Lookup lookup;

   GenesisProject(final AntProjectHelper helper) {
      this.helper = helper;
      evaluator = helper.getStandardPropertyEvaluator();
      auxiliaryConfiguration = helper.createAuxiliaryConfiguration();
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
         new GenesisSources(this)
         });
   }
}