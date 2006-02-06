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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import net.java.dev.genesis.plugins.netbeans.buildsupport.spi.GenesisProjectKind;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.spi.project.ant.AntArtifactProvider;
import org.netbeans.spi.project.support.ant.AntProjectEvent;
import org.netbeans.spi.project.support.ant.AntProjectListener;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.ErrorManager;

public class GenesisAntArtifactProvider implements AntArtifactProvider {
   private final GenesisProject project;
   private AntArtifact[] artifacts;

   private final class GenesisAntArtifact extends AntArtifact {
      private final String id;
      private final String type;
      private final String location;
      private final String target;
      private final String clean;

      private File script;
      private URI[] uris;

      private GenesisAntArtifact(final String id, final String type, 
            final String location, final String target, final String clean) {
         this.id = id;
         this.type = type;
         this.location = location;
         this.target = target;
         this.clean = clean;
      }

      public String getType() {
         return type;
      }

      public File getScriptLocation() {
         if (script == null) {
            script = project.getHelper().resolveFile(GeneratedFilesHelper.BUILD_XML_PATH);
         }

         return script;
      }

      public String getTargetName() {
         return target;
      }

      public String getCleanTargetName() {
         return clean;
      }

      public String getID() {
         return id;
      }

      public URI[] getArtifactLocations() {
         if (uris == null) {
            uris = new URI[1];

            File f = new File(location);

            if (f.isAbsolute()) {
               uris[0] = f.toURI();
            } else {
               try {
                  uris[0] = new URI(null, null, location.replace(
                        File.separatorChar, '/'), null);
               } catch (URISyntaxException e) {
                  ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                  uris[0] = URI.create("file:/BROKEN");
               }
            }
         }

         return uris;
      }
   }

   public GenesisAntArtifactProvider(final GenesisProject project) {
      this.project = project;
      project.getHelper().addAntProjectListener(new AntProjectListener() {
         public void configurationXmlChanged(AntProjectEvent e) {
            synchronized (GenesisAntArtifactProvider.this) {
               artifacts = null;
            }
         }

         public void propertiesChanged(AntProjectEvent e) {
         }
      });
      project.getEvaluator().addPropertyChangeListener(new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent e) {
            synchronized (GenesisAntArtifactProvider.this) {
               artifacts = null;
            }
         }
      });
   }

   public synchronized AntArtifact[] getBuildArtifacts() {
      if (artifacts == null) {
         artifacts = createArtifacts();
      }

      return artifacts;
   }

   private AntArtifact[] createArtifacts() {
      Collection artifacts = new ArrayList();
      GenesisProjectKind kind = Utils.getKind(project);

      if (kind == GenesisProjectKind.DESKTOP) {
         PropertyEvaluator eval = project.getEvaluator();
         String needsJarValue = eval.getProperty("needs.jar");

         boolean needsJar = !"false".equals(needsJarValue);

         String jarSharedNeededValue = eval.getProperty("jar.shared.needed");

         boolean jarSharedNeeded = needsJar;

         if (jarSharedNeededValue != null) {
            jarSharedNeeded = !"false".equals(jarSharedNeededValue);
         }

         if (jarSharedNeeded) {
            String jarSharedLocation = eval.getProperty("jar.shared.location");

            if (jarSharedLocation == null) {
               String buildDir = eval.getProperty("build.dir");

               if (buildDir == null) {
                  buildDir = "target";
               }

               jarSharedLocation = eval.evaluate(buildDir + 
                     "/${genesisBasedApplication.name}-shared.jar");
            }

            artifacts.add(new GenesisAntArtifact("shared",
                  JavaProjectConstants.ARTIFACT_TYPE_JAR, jarSharedLocation,
                  "build-jar", "clean-jar"));
         }
      }

      return (AntArtifact[])artifacts.toArray(new AntArtifact[artifacts.size()]);
   }
}
