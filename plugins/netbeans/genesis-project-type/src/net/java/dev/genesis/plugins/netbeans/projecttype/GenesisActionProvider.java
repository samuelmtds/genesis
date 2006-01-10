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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class GenesisActionProvider implements ActionProvider {
   private final GenesisProject project;
   private final String[] supportedActions = new String[] {COMMAND_BUILD, 
         COMMAND_CLEAN, COMMAND_REBUILD, COMMAND_RUN};
   private final Map targetNamesByActionString = new HashMap();

   public GenesisActionProvider(final GenesisProject project) {
      this.project = project;

      Arrays.sort(supportedActions);

      targetNamesByActionString.put(COMMAND_BUILD, new String [] {"all"});
      targetNamesByActionString.put(COMMAND_CLEAN, new String [] {"clean"});
      targetNamesByActionString.put(COMMAND_REBUILD, new String [] 
            {"clean-build"});
      targetNamesByActionString.put(COMMAND_RUN, new String [] {"run:local"});
   }
   
   public String[] getSupportedActions() {
      return supportedActions;
   }

   public void invokeAction(String key, Lookup lookup) 
         throws IllegalArgumentException {
      Utils.invokeAction(project, (String[])targetNamesByActionString.get(key));
   }

   public boolean isActionEnabled(String key, Lookup lookup) 
         throws IllegalArgumentException {
      if (Arrays.binarySearch(supportedActions, key) < 0) {
         return false;
      }

      return Utils.getBuildFile(project, false) != null;
   }
}
