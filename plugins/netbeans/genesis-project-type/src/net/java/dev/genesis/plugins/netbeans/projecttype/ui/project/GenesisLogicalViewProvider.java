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
package net.java.dev.genesis.plugins.netbeans.projecttype.ui.project;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProject;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProjectExecutionMode;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProjectType;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisSources;
import net.java.dev.genesis.plugins.netbeans.projecttype.Utils;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.queries.VisibilityQuery;
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
import org.openide.loaders.ChangeableDataFilter;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.FolderLookup;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GenesisLogicalViewProvider implements LogicalViewProvider {
   private final GenesisProject project;

   private class GenesisLogicalProviderRootNode extends AbstractNode {
      public GenesisLogicalProviderRootNode() {
         super(new GenesisLogicalProviderChildren(), Lookups.singleton(project));
         setName();
         setIconBaseWithExtension(Utils.ICON_PATH);
         setShortDescription(project.getProjectDirectory().getPath());

         ProjectUtils.getInformation(project).addPropertyChangeListener(
               new PropertyChangeListener() {
                  public void propertyChange(PropertyChangeEvent event) {
                     if (!ProjectInformation.PROP_DISPLAY_NAME.equals(
                           event.getPropertyName())) {
                        return;
                     }

                     setName();
                     fireNameChange(null, null);
                  }
            });
      }

      private void setName() {
         setName(ProjectUtils.getInformation(project).getDisplayName());
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

         Collection antActions = getAntActions();

         if (!antActions.isEmpty()) {
            actions.add(null);
            actions.addAll(antActions);
         }

         actions.add(null);

         addRunActions(actions, bundle);
         addWebstartActions(actions, bundle);

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

      private List getAntActions() {
         final List actions = new ArrayList();
         Element data = project.getHelper().getPrimaryConfigurationData(true);
         NodeList nl = data.getElementsByTagNameNS(
               GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "view");

         if (nl.getLength() != 1) {
            return actions;
         }

         nl = ((Element)nl.item(0)).getElementsByTagNameNS(
               GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "context-menu");

         if (nl.getLength() != 1) {
            return actions;
         }

         nl = ((Element)nl.item(0)).getElementsByTagNameNS(
               GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "action");

         for (int i = 0; i < nl.getLength(); i++) {
            NodeList labelNodes = ((Element)nl.item(i)).getElementsByTagNameNS(
               GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "label");

            if (labelNodes.getLength() != 1) {
               continue;
            }

            String label = labelNodes.item(0).getChildNodes().item(0)
                  .getNodeValue();

            NodeList targetNodes = ((Element)nl.item(0)).getElementsByTagNameNS(
               GenesisProjectType.PROJECT_CONFIGURATION_NAMESPACE, "target");

            if (targetNodes.getLength() == 0) {
               continue;
            }

            Collection targets = new ArrayList(targetNodes.getLength());

            for (int j = 0; j < targetNodes.getLength(); j++) {
               targets.add(targetNodes.item(j).getChildNodes().item(0)
                  .getNodeValue());
            }

            actions.add(new CustomAntAction(project, label, 
                  (String[])targets.toArray(new String[targets.size()])));
         }

         return actions;
      }

      private void addRunActions(final List actions, final ResourceBundle bundle) {
         GenesisProjectExecutionMode executionMode = 
               Utils.getExecutionMode(project);

         if (executionMode != GenesisProjectExecutionMode.LOCAL_AND_REMOTE) {
            actions.add(ProjectSensitiveActions.projectCommandAction(
                  ActionProvider.COMMAND_RUN, bundle.getString(
                  "LBL_RunAction_Name"), null));
         } else {
            actions.add(new CustomAntAction(project, bundle.getString(
                  "LBL_RunLocalAction_Name"), new String[] {
                  Utils.RUN_LOCAL_TARGET}));
            actions.add(new CustomAntAction(project, bundle.getString(
                  "LBL_RunRemoteAction_Name"), new String[] {
                  Utils.RUN_REMOTE_TARGET}));
         }
      }

      private void addWebstartActions(List actions, ResourceBundle bundle) {
         if (!Utils.usesWebstart(project)) {
            return;
         }

         actions.add(null);
         actions.add(new CustomAntAction(project, bundle.getString(
                  "LBL_BuildWebstartAction_Name"), new String[] {
                  Utils.WEBSTART_TARGET}));
         actions.add(new CustomAntAction(project, bundle.getString(
                  "LBL_CleanBuildWebstartAction_Name"), new String[] {
                  Utils.CLEAN_WEBSTART_TARGET}));
      }
   }

   private static class CustomAntAction extends AbstractAction {
      private final GenesisProject project;
      private final String displayName;
      private final String[] targets;

      public CustomAntAction(final GenesisProject project, 
            final String displayName, final String[] targets) {
         this.project = project;
         this.displayName = displayName;
         this.targets = targets;
      }

      public void actionPerformed(ActionEvent e) {
         Utils.invokeAction(project, targets);
      }

      public Object getValue(String key) {
         if (Action.NAME.equals(key)) {
            return displayName;
         }

         return super.getValue(key);
      }

      public boolean isEnabled() {
         return Utils.getBuildFile(project, false) != null;
      }
   }

   private class GenesisLogicalProviderChildren extends Children.Array {
      private final Collection nodes = new ArrayList();

      public GenesisLogicalProviderChildren() {
         ((Sources)project.getLookup().lookup(Sources.class)).addChangeListener(
               new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
               nodes.clear();
               createNodes();
               refresh();
            }
         });
      }

      protected Collection initCollection() {
         createNodes();
         return nodes;
      }

      private void createNodes() {
         Sources sources = (Sources)project.getLookup().lookup(Sources.class);

         SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.
               SOURCES_TYPE_JAVA);

         for (int i = 0; i < groups.length; i++) {
            nodes.add(PackageView.createPackageView(groups[i]));
         }

         groups = sources.getSourceGroups(GenesisSources.SOURCES_TYPE_FOLDER);

         for (int i = 0; i < groups.length; i++) {
            try {
               nodes.add(new TreeNode(((DataFolder)DataObject.find(groups[i]
                     .getRootFolder())), groups[i].getDisplayName()));
            } catch (DataObjectNotFoundException ex) {
               ErrorManager.getDefault().notify(ex);
            }
         }
      }
   }

   private static final class TreeNode extends FilterNode {
      private static final DataFilter filter = new VisibilityQueryDataFilter();
      private final String displayName;

      public TreeNode(final DataFolder folder, final String displayName) {
         super(folder.getNodeDelegate(), folder.createNodeChildren(filter));
         this.displayName = displayName;
      }

      public String getDisplayName() {
         return displayName;
      }
   }
    
   private static final class VisibilityQueryDataFilter 
         implements ChangeListener, ChangeableDataFilter {
      private final Collection listeners = new ArrayList();
      
      public VisibilityQueryDataFilter() {
         VisibilityQuery.getDefault().addChangeListener(this);
      }
      
      public boolean acceptDataObject(DataObject dataObject) {
         return VisibilityQuery.getDefault().isVisible(
               dataObject.getPrimaryFile());
      }
      
      public void stateChanged(ChangeEvent e) {
         if (listeners.isEmpty()) {
            return;
         }

         ChangeEvent event = new ChangeEvent(this);

         for (Iterator i = listeners.iterator(); i.hasNext();) {
            ChangeListener listener = (ChangeListener)i.next();
            listener.stateChanged(event);
         }
      }
      
      public void addChangeListener(ChangeListener listener) {
         listeners.add(listener);
      }
      
      public void removeChangeListener(ChangeListener listener) {
         listeners.remove(listener );
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