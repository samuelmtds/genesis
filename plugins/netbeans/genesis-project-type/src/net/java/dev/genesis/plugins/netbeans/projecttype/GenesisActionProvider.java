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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.spi.project.ActionProvider;
import org.openide.util.Lookup;

public class GenesisActionProvider implements ActionProvider {
   private final GenesisProject project;
   private final String[] supportedActions = new String[] {COMMAND_BUILD, 
         COMMAND_CLEAN, COMMAND_REBUILD, COMMAND_RUN};
   private final Map<String, String[]> targetNamesByActionString = new HashMap<String, String[]>();

   public GenesisActionProvider(final GenesisProject project) {
      this.project = project;

      Arrays.sort(supportedActions);

      targetNamesByActionString.put(COMMAND_BUILD, new String [] {"all"});
      targetNamesByActionString.put(COMMAND_CLEAN, new String [] {"clean"});
      targetNamesByActionString.put(COMMAND_REBUILD, new String [] 
            {"clean-build"});

      project.getEvaluator().addPropertyChangeListener(
         new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
               if (!Utils.isExecutionRelatedProperty(event.getPropertyName())) {
                  return;
               }

               determineRunTarget(project);
            }
         });
      determineRunTarget(project);
   }

   private void determineRunTarget(final GenesisProject project) {
      targetNamesByActionString.put(COMMAND_RUN, new String [] {
            Utils.getExecutionMode(project) == 
            GenesisProjectExecutionMode.LOCAL_MODE_ONLY ? 
            Utils.RUN_LOCAL_TARGET : Utils.RUN_REMOTE_TARGET});
   }
   
   public String[] getSupportedActions() {
      return supportedActions;
   }

   public void invokeAction(String key, Lookup lookup) 
         throws IllegalArgumentException {
      Utils.invokeAction(project, targetNamesByActionString.get(key));
   }

   public boolean isActionEnabled(String key, Lookup lookup) 
         throws IllegalArgumentException {
      if (Arrays.binarySearch(supportedActions, key) < 0) {
         return false;
      }

      return Utils.getBuildFile(project, false) != null;
   }
}
