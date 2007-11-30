/*
 * The Genesis Project
 * Copyright (C) 2007  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer;

import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import net.java.dev.genesis.annotation.DataProvider;

import net.java.dev.genesis.annotation.Form;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProject;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

@Form
public class CustomizerGeneralForm extends GenesisProjectProperties {
   
   public enum JseVersion {
      JAVA_16("1.6"), JAVA_15("1.5"), JAVA_14("1.4");
      
      private String version;
      JseVersion(String version) {
         this.version = version;
      }
      
      public String getVersion() {
         return version;
      }
   };
   
   public CustomizerGeneralForm(GenesisProject project) throws Exception {
      super( project );
   }
   
   public String getProjectFolder() {
      FileObject projectFolder = this.project.getProjectDirectory();
      File pf = FileUtil.toFile( projectFolder );
      return pf == null ? "" : pf.getPath(); // NOI18N
   }
   
   @DataProvider(widgetName="jseAvailableVersions", objectField="jseEnumVersion")
   public List<JseVersion> jseAvailableVersions() {
      return Arrays.asList(JseVersion.JAVA_16,
              JseVersion.JAVA_15,
              JseVersion.JAVA_14);
   }
   
   public void setJseEnumVersion(JseVersion version) {
      setJseVersion(version.getVersion());
   }
   
   public JseVersion getJseEnumVersion() {
      try {
         return getJseVersion() ==  null ? JseVersion.JAVA_16 :
            JseVersion.valueOf("JAVA_" + getJseVersion().replaceAll("\\D", ""));
      } catch (IllegalArgumentException ex) {
         return null;
      }
   }
}
