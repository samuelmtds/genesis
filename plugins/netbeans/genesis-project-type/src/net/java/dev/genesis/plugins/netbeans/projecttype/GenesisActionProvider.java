package net.java.dev.genesis.plugins.netbeans.projecttype;

import org.netbeans.spi.project.ActionProvider;
import org.openide.util.Lookup;

public class GenesisActionProvider implements ActionProvider {
   private final GenesisProject project;
   private final String[] supportedActions = new String[] {COMMAND_BUILD, 
         COMMAND_CLEAN, COMMAND_REBUILD, COMMAND_RUN};

   public GenesisActionProvider(final GenesisProject project) {
      this.project = project;
   }
   
   public String[] getSupportedActions() {
      return supportedActions;
   }

   public void invokeAction(String string, Lookup lookup) throws IllegalArgumentException {
   }

   public boolean isActionEnabled(String string, Lookup lookup) throws IllegalArgumentException {
      return true;
   }
}
