/*
 * The Genesis Project
 * Copyright (C) 2007  Summa Technologies do Brasil Ltda.
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
import java.util.HashMap;
import java.util.Map;

import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProject;
import net.java.dev.genesis.plugins.netbeans.projecttype.Utils;
import net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer.GenesisCustomizerProvider.GenesisView;
import net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer.GenesisProjectProperties;
import net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer.annotation.Property;
import net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer.annotation.XmlProperty;

import org.apache.commons.beanutils.PropertyUtils;

import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.util.Mutex;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Control the operations about storing and loading fileds from/to properties files
 * from/to views.
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
   public void synchronize(final GenesisView view) throws Exception {
      final GenesisProjectProperties properties = view.getForm();
      final BeanInfo beanInfo = Introspector.getBeanInfo( properties.getClass() );
      final AntProjectHelper helper = project.getHelper();
      final Element data = helper.getPrimaryConfigurationData(true);
      
      for ( PropertyDescriptor pd : beanInfo.getPropertyDescriptors() ){
         Method method = pd.getReadMethod();
         if ( method != null ){
            boolean defined = false;
            if (method.isAnnotationPresent(Property.class)) {
               String propertyName = method.getAnnotation(Property.class).value();
               Object propertyValue = this.resolveValue(propertyName,
                       PropertyUtils.getPropertyType(properties, pd.getName()));
               PropertyUtils.setProperty(properties,
                       pd.getName(),
                       propertyValue);
               defined = propertyValue != null;
            }
            
            if (!defined && method.isAnnotationPresent(XmlProperty.class)) {
               String propertyName = method.
                       getAnnotation(XmlProperty.class).value();
               String propertyNamespace = method.
                       getAnnotation(XmlProperty.class).namespace();
               String propertyValue = null;
               
               Node node = Utils.getTextNode(data,
                       propertyNamespace, propertyName);
               if (node != null) {
                  propertyValue = node.getNodeValue();
               }
               
               PropertyUtils.setProperty(properties,
                       pd.getName(),
                       propertyValue);
            }
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
      final String antPropertyName = "${" + propertyName + "}";
      final String antPropertyValue = this.project.getEvaluator().evaluate(antPropertyName);
      Object value = antPropertyName.equals(antPropertyValue) ? null : antPropertyValue;
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
            storeProperties(views);
            ProjectManager.getDefault().saveProject(project);
            return null;
         }
      });
   }
   
   private void storeProperties( final Collection< GenesisView > views ) throws Exception {
      final EditableProperties originalProjectProperties =
              projectProperties.cloneProperties();
      final AntProjectHelper helper = project.getHelper();
      final Element data = helper.getPrimaryConfigurationData(true);
      final Map<String,String> originalXmlPropertiesCache =
              new HashMap<String, String>();
      
      // Synchronize all the panels with the properties
      for ( GenesisView view : views ){
         GenesisProjectProperties properties = view.getForm();
         BeanInfo beanInfo = Introspector.getBeanInfo( properties.getClass() );
         
         for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()){
            Method method = pd.getReadMethod();
            if (method != null) {
               Object propertyValue = PropertyUtils.getProperty(properties, pd.getName());
               propertyValue = propertyValue != null ? propertyValue : "";
               
               if (method.isAnnotationPresent(Property.class)) {
                  String propertyName = method.getAnnotation(Property.class).value();
                  
                  String original = originalProjectProperties.getProperty(propertyName);
                  original = original != null ? original : "";
                  //Just changed values should be updated
                  if (!original.equals(propertyValue.toString())) {
                     projectProperties.setProperty(propertyName, propertyValue.toString());
                  }
               }
               if (method.isAnnotationPresent(XmlProperty.class)) {
                  String propertyName = method.
                          getAnnotation(XmlProperty.class).value();
                  String propertyNamespace = method.
                          getAnnotation(XmlProperty.class).namespace();
                  
                  String original = Utils.getSourceLevel(project.getHelper());
                  original = original != null ? original : "";
                  if (!originalXmlPropertiesCache.containsKey(propertyName)) {
                     originalXmlPropertiesCache.put(propertyName, original);
                  } else {
                     original = originalXmlPropertiesCache.get(propertyName);
                  }
                  
                  //Just changed values should be updated
                  if (!original.equals(propertyValue.toString())) {
                     Node node = Utils.getTextNode(data,
                             propertyNamespace, propertyName);
                     if (node != null) {
                        node.setNodeValue(propertyValue.toString());
                     }
                  }
               }
            }
            
            helper.putPrimaryConfigurationData(data, true);
         }
         
         // Store special properties
         this.project.getHelper().putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH,
                 this.projectProperties);
      }
   }
}
