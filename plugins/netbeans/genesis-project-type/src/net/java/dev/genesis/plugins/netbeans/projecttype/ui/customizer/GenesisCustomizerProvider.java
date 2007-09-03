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
package net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProject;
import net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer.persister.PersistenceManager;
import net.java.dev.genesis.ui.ActionInvoker;

import org.netbeans.api.project.ProjectUtils;

import org.netbeans.spi.project.ui.CustomizerProvider;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

public class GenesisCustomizerProvider implements CustomizerProvider {
    
    private final GenesisProject project;
    
    private ProjectCustomizer.Category categories[];
    private ProjectCustomizer.CategoryComponentProvider panelProvider;
    
    private Dialog dialog;
    
    private static final String GENERAL = "General"; // NOI18N
    
    public GenesisCustomizerProvider(final GenesisProject project) {
        this.project = project;
    }
    
    public void showCustomizer() {
        showCustomizer( null );
    }
    
    
    public void showCustomizer( String preselectedCategory ) {
        showCustomizer( preselectedCategory, null );
    }
    
    public void showCustomizer( String preselectedCategory, String preselectedSubCategory ) {
        if ( this.dialog != null ) {
            this.dialog.setVisible( true );
            return;
        } else {
            GenesisProjectPropertiesController controller = new GenesisProjectPropertiesController();
            try {
                init( controller );
                
                OptionListener listener = new OptionListener( controller );
                if (preselectedCategory != null && preselectedSubCategory != null) {
                    for (int i=0; i<categories.length; i++ ) {
                        if (preselectedCategory.equals(categories[i].getName())) {
                            JComponent component = panelProvider.create(categories[i]);
                            if (component instanceof SubCategoryProvider) {
                                ((SubCategoryProvider)component).showSubCategory(preselectedSubCategory);
                            }
                            break;
                        }
                    }
                }
                this.dialog = ProjectCustomizer.createCustomizerDialog( categories, panelProvider, preselectedCategory, listener, null );
                this.dialog.addWindowListener( listener );
                this.dialog.setTitle( MessageFormat.format(
                        NbBundle.getMessage( GenesisCustomizerProvider.class, "LBL_Customizer_Title" ), // NOI18N
                        new Object[] { ProjectUtils.getInformation(project).getDisplayName() } ) );
                
                this.dialog.setVisible( true );
            } catch (Exception ex) {
                ErrorManager.getDefault().notify( ex );
            }
        }
    }
    
    private void init( GenesisProjectPropertiesController controller ) throws Exception {
        ResourceBundle bundle = NbBundle.getBundle( GenesisCustomizerProvider.class );
        
        ProjectCustomizer.Category general = ProjectCustomizer.Category.create(
                GENERAL,
                bundle.getString( "LBL_General" ),
                null,
                null );
        
        categories = new ProjectCustomizer.Category[] {
            general
        };
        
        Map<Category, GenesisView> panels = new HashMap<Category, GenesisView>();
        panels.put( general, new CustomizerGeneral( this.project ) );
        
        panelProvider = new PanelProvider( panels );
        controller.addAll( panels.values() );
    }
    
    private static class PanelProvider implements ProjectCustomizer.CategoryComponentProvider {
        
        private JPanel EMPTY_PANEL = new JPanel();
        
        private Map<Category,GenesisView> panels;
        
        PanelProvider( Map<Category,GenesisView> panels ) {
            this.panels = panels;
        }
        
        public JComponent create( ProjectCustomizer.Category category ) {
            JComponent panel = panels.get( category );
            return panel == null ? EMPTY_PANEL : panel;
        }
        
    }
    
    /** Listens to the actions on the Customizer's option buttons */
    private class OptionListener extends WindowAdapter implements ActionListener {
        
        private GenesisProjectPropertiesController controller;
        
        OptionListener( GenesisProjectPropertiesController controller ) {
            this.controller = controller;
        }
        
        // Listening to OK button ----------------------------------------------
        public void actionPerformed( ActionEvent e ) {
            // Store the properties into project
            // TODO Look for issue when press ENTER while yet editing field, the new value is not defined
            controller.save();
            
            // Close & dispose the the dialog
            if ( dialog != null ) {
                dialog.setVisible( false );
                dialog.dispose();
            }
        }
        
        // Listening to window events ------------------------------------------
        public void windowClosed( WindowEvent e) {
            dialog = null;
        }
        
        public void windowClosing(WindowEvent e) {
            //Dispose the dialog otherwsie the {@link WindowAdapter#windowClosed}
            //may not be called
            if ( dialog != null ) {
                dialog.setVisible( false );
                dialog.dispose();
            }
        }
    }
    
    private final class GenesisProjectPropertiesController {
        
        private PersistenceManager persisterManager;
        
        private List< GenesisView > views = new ArrayList< GenesisView >();
        
        /** Creates a new instance of GenesisProjectProperties */
        GenesisProjectPropertiesController() {
            this.persisterManager = new PersistenceManager( project );
        }
        
        public final void save() {
            try {
                // Store properties
                this.persisterManager.save( this.views );
            } catch (Exception e) {
                ErrorManager.getDefault().notify( e );
            }
        }
        
        private boolean refresh() {
            // Synchronize all panels with properties file(s)
            boolean exec = true;
            for ( GenesisView view : this.views ){
                try {
                    this.persisterManager.synchronize( view );
                    
                    ActionInvoker.refresh( view.getForm() );
                } catch (Exception ex) {
                    exec = false;
                    ErrorManager.getDefault().notify( ex );
                    break;
                }
            }
            
            return exec;
        }
        
        /**
         *
         * @param panel
         * @return
         */
        public boolean add( GenesisView view ) {
            boolean exec = this.views.add( view );
            if ( exec ){
                exec = this.refresh();
            }
            
            return exec;
        }
        
        /**
         *
         * @param panels
         * @return
         */
        public boolean addAll( Collection< ? extends GenesisView > views ) {
            boolean exec = this.views.addAll( views );
            if ( exec ){
                exec = this.refresh();
            }
            
            return exec;
        }
        
        /**
         *
         * @param panel
         * @return
         */
        public boolean remove( Object view ) {
            return this.views.remove( view );
        }
    }
    
    public abstract static class GenesisView extends JPanel {
        public abstract GenesisProjectProperties getForm();
    }
    
    static interface SubCategoryProvider {
        public void showSubCategory(String name);
    }
    
}
