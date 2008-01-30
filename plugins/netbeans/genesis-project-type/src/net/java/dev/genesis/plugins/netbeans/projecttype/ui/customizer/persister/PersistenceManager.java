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
                PropertyUtils.setProperty( properties,
                        pd.getName(),
                        this.resolveValue( propertyName,
                        PropertyUtils.getPropertyType( properties, pd.getName() ) ) );
            }
        }
    }
    
    /**
     * Resolve values from properties to a Java valid value, i.e. boolean values.
     * 
     * @return 
     * @param propertyType 
     * @param propertyName 
     * @throws java.lang.Exception 
     */
    private Object resolveValue( String propertyName, Class propertyType ) throws Exception {
        Object value = this.project.getEvaluator().evaluate( "${" + propertyName + "}" );
        if ( Boolean.class.equals( propertyType ) ||
                boolean.class.equals( propertyType ) ){
            value = "true".equals( value ) ? true : false;
        } else if ( value != null &&
                ( Long.class.equals( propertyType ) ||
                long.class.equals( propertyType ) ) ){
            value = new Long( value.toString() );
        }
        
        return value;
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
        EditableProperties originalProjectProperties = projectProperties.cloneProperties();
        
        // Synchronize all the panels with the properties
        for ( GenesisView view : views ){
            GenesisProjectProperties properties = view.getForm();
            BeanInfo beanInfo = Introspector.getBeanInfo( properties.getClass() );
            
            for ( PropertyDescriptor pd : beanInfo.getPropertyDescriptors() ){
                Method method = pd.getReadMethod();
                if ( method != null &&
                        method.isAnnotationPresent( Property.class ) ){
                    String propertyName = method.getAnnotation( Property.class ).value();
                    Object propertyValue = PropertyUtils.getProperty( properties, pd.getName() );
                    propertyValue = propertyValue != null ? propertyValue : "";
                    
                    String original = originalProjectProperties.getProperty( propertyName );
                    original = original != null ? original : "";
                    //Just changed values should be updated
                    if ( !original.equals( propertyValue.toString() ) ){
                        projectProperties.setProperty( propertyName, propertyValue.toString() );
                    }
                }
            }
        }
        
        // Store special properties
        this.project.getHelper().putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH,
                this.projectProperties);
    }
}
