package net.java.dev.genesis.plugins.netbeans.projecttype.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.JSeparator;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProject;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.ErrorManager;
import org.openide.actions.FindAction;
import org.openide.actions.ToolsAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.FolderLookup;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

public class GenesisLogicalViewProvider implements LogicalViewProvider {
   private final GenesisProject project;

   private class GenesisLogicalProviderChildren extends Children.Array {
      protected void addNotify() {
         try {
            add(new Node[] {DataObject.find(project.getProjectDirectory())
                  .getNodeDelegate()});
         } catch (DataObjectNotFoundException ex) {
            ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
         }
      }
   }

   private class GenesisLogicalProviderRootNode extends AbstractNode {
      public GenesisLogicalProviderRootNode() {
         super(new GenesisLogicalProviderChildren(), Lookups.singleton(project));
         super.setName(ProjectUtils.getInformation(project).getDisplayName());
      }

      public Action[] getActions(boolean b) {
         ResourceBundle bundle = NbBundle.getBundle(
               GenesisLogicalViewProvider.class);
         
         List actions = new ArrayList();
         
         actions.add(CommonProjectActions.newFileAction());
         actions.add(null);
         actions.add(ProjectSensitiveActions.projectCommandAction(
               ActionProvider.COMMAND_BUILD, bundle.getString(
               "LBL_BuildAction_Name"), null));
         actions.add(ProjectSensitiveActions.projectCommandAction(
               ActionProvider.COMMAND_REBUILD, bundle.getString(
               "LBL_RebuildAction_Name"), null));
         actions.add(ProjectSensitiveActions.projectCommandAction(
               ActionProvider.COMMAND_CLEAN, bundle.getString(
               "LBL_CleanAction_Name"), null));
         actions.add(null);
         actions.add(ProjectSensitiveActions.projectCommandAction(
               ActionProvider.COMMAND_RUN, bundle.getString(
               "LBL_RunAction_Name"), null));
         actions.add(null);
         actions.add(CommonProjectActions.setAsMainProjectAction());
         actions.add(CommonProjectActions.closeProjectAction());
         actions.add(null);
         actions.add(SystemAction.get(FindAction.class));
         
         try {
            FileObject fo = Repository.getDefault().getDefaultFileSystem()
                  .findResource("Projects/Actions");

            if (fo != null) {
               DataObject dobj = DataObject.find(fo);
               FolderLookup actionRegistry = new FolderLookup((DataFolder)dobj);
               Lookup.Template query = new Lookup.Template(Object.class);
               Lookup lookup = actionRegistry.getLookup();
               Iterator it = lookup.lookup(query).allInstances().iterator();

               if (it.hasNext()) {
                  actions.add(null);
               }

               while (it.hasNext()) {
                  Object next = it.next();

                  if (next instanceof Action) {
                     actions.add(next);
                  } else if (next instanceof JSeparator) {
                     actions.add(null);
                  }
               }
            }
         } catch (DataObjectNotFoundException ex) {
            ErrorManager.getDefault().notify(ex);
         }
         
         actions.add(null);
         actions.add(SystemAction.get(ToolsAction.class));
         actions.add(null);

         actions.add(CommonProjectActions.customizeProjectAction());
         
         return (Action[])actions.toArray(new Action[actions.size()]);
      }
   }

   public GenesisLogicalViewProvider(final GenesisProject project) {
      this.project = project;
   }

   public Node createLogicalView() {
      return new GenesisLogicalProviderRootNode();
   }

   public Node findPath(Node root, Object target) {
     Project project = (Project)root.getLookup().lookup(Project.class);

     if (project == null) {
         return null;
     }

     if (target instanceof FileObject) {
         FileObject fo = (FileObject) target;
         Project owner = FileOwnerQuery.getOwner(fo);

         if (!project.equals(owner)) {
             return null;
         }

         Node[] nodes = root.getChildren().getNodes(true);

         for (int i = 0; i < nodes.length; i++) {
             Node result = PackageView.findPath(nodes[i], target);
             if (result != null) {
                 return result;
             }
         }
     }

     return null;
   }
}