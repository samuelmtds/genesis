package net.java.dev.genesis.plugins.netbeans.projecttype;

import java.io.IOException;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

public class Utils {
   private Utils() {
   }

   public static FileObject getBuildFile(Project project, boolean showMessages) {
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

   public static void invokeAction(Project project, String[] targets) {
      FileObject buildFile = Utils.getBuildFile(project, true);

      if (buildFile == null) {
         return;
      }

      try {
         ActionUtils.runTarget(buildFile, targets, null);
      } catch (IOException e) {
         ErrorManager.getDefault().notify(e);
      }
   }
}
