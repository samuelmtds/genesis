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

         String buildDir = eval.getProperty("build.dir");

         if (buildDir == null) {
            buildDir = "target";
         }

         String needsJarValue = eval.getProperty("needs.jar");
         boolean needsJar = !"false".equals(needsJarValue);

         addSharedJar(artifacts, eval, needsJar, buildDir);

         GenesisProjectExecutionMode mode = Utils.getExecutionMode(project);

         addLocalJar(artifacts, eval, needsJar, buildDir, mode);
         addRemoteJar(artifacts, eval, needsJar, buildDir, mode);
      }

      return (AntArtifact[])artifacts.toArray(new AntArtifact[artifacts.size()]);
   }

   private void addSharedJar(final Collection artifacts, 
         final PropertyEvaluator eval, final boolean needsJar, 
         final String buildDir) {
      String jarSharedNeededValue = eval.getProperty("jar.shared.needed");

      boolean jarSharedNeeded = needsJar;

      if (jarSharedNeededValue != null) {
         jarSharedNeeded = !"false".equals(jarSharedNeededValue);
      }

      if (!jarSharedNeeded) {
         return;
      }

      String jarSharedLocation = eval.getProperty("jar.shared.location");
      
      if (jarSharedLocation == null) {
         jarSharedLocation = eval.evaluate(buildDir +
               "/${genesisBasedApplication.name}-shared.jar");
      }
      
      artifacts.add(new GenesisAntArtifact("shared",
            JavaProjectConstants.ARTIFACT_TYPE_JAR, jarSharedLocation,
            "build-jar", "clean-jar"));
   }

   private void addLocalJar(final Collection artifacts, 
         final PropertyEvaluator eval, final boolean needsJar, 
         final String buildDir, final GenesisProjectExecutionMode mode) {
      String jarLocalNeededValue = eval.getProperty("jar.local.needed");

      boolean jarLocalNeeded = needsJar && 
            mode != GenesisProjectExecutionMode.REMOTE_MODE_ONLY;

      if (jarLocalNeededValue != null) {
         jarLocalNeeded = !"false".equals(jarLocalNeededValue);
      }

      if (!jarLocalNeeded) {
         return;
      }

      String jarLocalLocation = eval.getProperty("jar.local.location");

      if (jarLocalLocation == null) {
         String jarLocalName = eval.getProperty("jar.local.name");

         if (jarLocalName == null) {
            jarLocalName = eval.evaluate(
                  "${genesisBasedApplication.name}-local-weaved.jar");
         }

         jarLocalLocation = eval.evaluate(buildDir + "/" + jarLocalName);
      }

      artifacts.add(new GenesisAntArtifact("local",
            JavaProjectConstants.ARTIFACT_TYPE_JAR, jarLocalLocation,
            "build-jar", "clean-jar"));
   }

   private void addRemoteJar(final Collection artifacts, 
         final PropertyEvaluator eval, final boolean needsJar, 
         final String buildDir, final GenesisProjectExecutionMode mode) {
      String jarRemoteNeededValue = eval.getProperty("jar.remote.needed");

      boolean jarRemoteNeeded = needsJar && 
            mode != GenesisProjectExecutionMode.REMOTE_MODE_ONLY;

      if (jarRemoteNeededValue != null) {
         jarRemoteNeeded = !"false".equals(jarRemoteNeededValue);
      }

      if (!jarRemoteNeeded) {
         return;
      }

      String jarRemoteLocation = eval.getProperty("jar.remote.location");

      if (jarRemoteLocation == null) {
         String jarRemoteName = eval.getProperty("jar.remote.name");

         if (jarRemoteName == null) {
            jarRemoteName = eval.evaluate(
                  "${genesisBasedApplication.name}-remote-weaved.jar");
         }

         jarRemoteLocation = eval.evaluate(buildDir + "/" + jarRemoteName);
      }

      artifacts.add(new GenesisAntArtifact("remote",
            JavaProjectConstants.ARTIFACT_TYPE_JAR, jarRemoteLocation,
            "build-jar", "clean-jar"));
   }
}