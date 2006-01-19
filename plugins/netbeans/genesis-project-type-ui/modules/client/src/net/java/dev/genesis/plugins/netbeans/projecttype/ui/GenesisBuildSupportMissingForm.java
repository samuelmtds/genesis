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
package net.java.dev.genesis.plugins.netbeans.projecttype.ui;

import java.util.List;
import net.java.dev.genesis.plugins.netbeans.buildsupport.api.GenesisBuildSupportManager;
import net.java.dev.genesis.plugins.netbeans.buildsupport.spi.GenesisBuildSupport;
import org.openide.util.NbBundle;

/**
 * @Form
 */
public class GenesisBuildSupportMissingForm {
   private final String message;
   private GenesisBuildSupport version;

   public GenesisBuildSupportMissingForm(final String currentVersion) {
      this.message = NbBundle.getMessage(GenesisBuildSupportMissingDialog.class, 
            "LBL_BuildSupportMissing", new Object[] {currentVersion});
   }

   public String getMessage() {
      return message;
   }

   /**
    * @DataProvider widgetName=versions objectField=version
    */
   public List provideVersions() {
      return GenesisBuildSupportManager.getInstance().getBuildSupport();
   }

   public GenesisBuildSupport getVersion() {
      return version;
   }

   public void setVersion(GenesisBuildSupport version) {
      this.version = version;
   }

   /**
    * @Action
    * @EnabledWhen g:isNotEmpty(version)
    */
   public void upgrade() {
   }

   /**
    * @Action
    */
   public void cancel() {
      setVersion(null);
   }
}
