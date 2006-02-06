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
package net.java.dev.genesis.plugins.netbeans.projecttype.queries;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProject;
import net.java.dev.genesis.plugins.netbeans.projecttype.Utils;
import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.spi.queries.SharabilityQueryImplementation;

public class GenesisSharabilityQueryImplementation
      implements SharabilityQueryImplementation {
   private final GenesisProject project;
   private final String nbproject;
   private final String nbprojectPrivate;
   private String buildDir;

   public GenesisSharabilityQueryImplementation(final GenesisProject project) {
      this.project = project;
      this.nbproject = project.getHelper().resolveFile("nbproject")
            .getAbsolutePath();
      this.nbprojectPrivate = project.getHelper()
            .resolveFile("nbproject/private").getAbsolutePath();

      project.getEvaluator().addPropertyChangeListener(new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent e) {
            if (Utils.BUILD_DIR_PROPERTY.equals(e.getPropertyName())) {
               buildDir = null;
            }
         }
      });
   }

   public int getSharability(File file) {
      String absolutePath = file.getAbsolutePath();
      
      if (absolutePath.equals(nbproject)) {
         return SharabilityQuery.MIXED;
      }

      if (absolutePath.startsWith(nbproject)) {
         return absolutePath.startsWith(nbprojectPrivate) ? 
               SharabilityQuery.NOT_SHARABLE : SharabilityQuery.SHARABLE;
      }

      if (buildDir == null) {
         buildDir = project.getHelper().resolveFile(Utils.getBuildDir(project))
               .getAbsolutePath();
      }

      if (absolutePath.startsWith(buildDir)) {
         return SharabilityQuery.NOT_SHARABLE;
      }

      return SharabilityQuery.UNKNOWN;
   }
}