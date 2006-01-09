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

   public void invokeAction(String string, Lookup lookup) 
         throws IllegalArgumentException {
      FileObject buildFile = getBuildFile(true);

      if (buildFile == null) {
         return;
      }

      try {
         ActionUtils.runTarget(buildFile, 
               (String[])targetNamesByActionString.get(string), null);
      } catch (IOException e) {
         ErrorManager.getDefault().notify(e);
      }
   }

   public boolean isActionEnabled(String key, Lookup lookup) 
         throws IllegalArgumentException {
      if (Arrays.binarySearch(supportedActions, key) < 0) {
         return false;
      }

      return getBuildFile(false) != null;
   }

   private FileObject getBuildFile(boolean showMessages) {
      FileObject build = project.getProjectDirectory().getFileObject(
            GeneratedFilesHelper.BUILD_XML_PATH);

      if (build == null || !build.isValid()) {
         if (showMessages) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                  NbBundle.getMessage(GenesisActionProvider.class,
                  "LBL_No_Build_XML_Found"), NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
         }
         
         return null;
      }

      return build;
   }
}