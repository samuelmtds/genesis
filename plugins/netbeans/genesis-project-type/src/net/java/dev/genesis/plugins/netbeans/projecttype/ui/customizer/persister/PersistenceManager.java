/*
 * PersisterManager.java
 *
 * Created on 28 de Agosto de 2007, 11:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer.persister;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;

import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProject;
import net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer.GenesisCustomizerProvider.GenesisView;
import net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer.GenesisProjectProperties;
import net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer.annotation.Property;
import net.java.dev.genesis.ui.ActionInvoker;

import org.apache.commons.beanutils.PropertyUtils;

import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.util.Mutex;

/**
 * Control the operations about storing and loading fileds from/to properties files
 * from/to views.
 *
 * @author Michel Graciano
 */
public class PersistenceManager {
    
    private GenesisProject project;
    private EditableProperties projectProperties;
    
    /** Creates a new instance of PersisterManager */
    public PersistenceManager( GenesisProject project ) {
        this.project = project;
        this.projectProperties = project.getHelper().getProperties( AntProjectHelper.PROJECT_PROPERTIES_PATH );
    }
    
    /**
     * Refresh the view fields, setting the values from properties files.
     * @param view 
     * @throws java.lang.Exception 
     */
    public void synchronize( GenesisView view ) throws Exception {
        GenesisProjectProperties properties = view.getForm();
        BeanInfo beanInfo = Introspector.getBeanInfo( properties.getClass() );
        
        for ( PropertyDescriptor pd : beanInfo.getPropertyDescriptors() ){
            Method method = pd.getReadMethod();
            if ( method != null &&
                    method.isAnnotationPresent( Property.class ) ){
                String propertyName = method.getAnnotation( Property.class ).value();
                PropertyUtils.setProperty( properties, pd.getName(), this.projectProperties.getProperty( propertyName ) );
            }
        }
    }
    
    /**
     * Store the fileds, from view, to the properties files.
     * @param views 
     * @throws java.lang.Exception 
     */
    public final void save( final Collection< GenesisView > views ) throws Exception {
        // Store properties
        ProjectManager.getDefault().mutex().writeAccess(
                new Mutex.ExceptionAction() {
            public Object run() throws Exception {
                storeProperties( views );
                ProjectManager.getDefault().saveProject( project );
                return null;
            }
        });
    }
    
    private void storeProperties( Collection< GenesisView > views ) throws Exception {
        // Synchronize all the panels with the properties
        for ( GenesisView view : views ){
            GenesisProjectProperties properties = view.getForm();
            BeanInfo beanInfo = Introspector.getBeanInfo( properties.getClass() );
            
            for ( PropertyDescriptor pd : beanInfo.getPropertyDescriptors() ){
                Method method = pd.getReadMethod();
                if ( method != null &&
                        method.isAnnotationPresent( Property.class ) ){
                    String propertyName = method.getAnnotation( Property.class ).value();
                    String propertyValue = (String) PropertyUtils.getProperty( properties, pd.getName() );
                    projectProperties.setProperty( propertyName, propertyValue );
                }
            }
        }
        
        // Store special properties
        this.project.getHelper().putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH,
                this.projectProperties);
    }
}
