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
package net.java.dev.genesis.plugins.netbeans.buildsupport.spi;

import java.io.IOException;
import net.java.dev.genesis.plugins.netbeans.buildsupport.spi.GenesisProjectKind;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;

public abstract class AbstractGenesisBuildSupport implements GenesisBuildSupport {
   private final GenesisVersion version;

   protected AbstractGenesisBuildSupport(final GenesisVersion version) {
      this.version = version;
   }

   public void generateBuildFiles(GenesisProjectKind kind, 
         GeneratedFilesHelper helper, boolean check) throws IOException {
      if (kind == GenesisProjectKind.DESKTOP) {
         helper.refreshBuildScript("nbproject/desktop_build.xml",
               getClass().getResource("resources/desktop_build.xsl"), check);
      } else if (kind == GenesisProjectKind.WEB) {
         helper.refreshBuildScript("nbproject/web_build.xml",
               getClass().getResource("resources/web_build.xsl"), check);
      }

      helper.refreshBuildScript("nbproject/master_build.xml",
            getClass().getResource("resources/master_build.xsl"), check);
      helper.refreshBuildScript(
            GeneratedFilesHelper.BUILD_IMPL_XML_PATH, 
            getClass().getResource("resources/build-impl.xsl"), check);
      helper.refreshBuildScript(
            GeneratedFilesHelper.BUILD_XML_PATH, 
            getClass().getResource("resources/build.xsl"), check);
   }

   public GenesisVersion getVersion() {
      return version;
   }
}
