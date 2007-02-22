/*
 * The Genesis Project
 * Copyright (C) 2006-2007  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.metadata.annotation;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.java.dev.genesis.annotation.DataProvider;
import net.java.dev.genesis.reflection.FieldEntry;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.MethodMetadata;
import net.java.dev.genesis.util.GenesisUtils;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.backport175.reader.Annotation;

public class DataProviderAnnotationHandler implements AnnotationHandler {
   public void processFormAnnotation(final FormMetadata formMetadata,
         final Annotation annotation) {
      AnnotationHandlerExceptionFactory.notFormAnnotation(formMetadata, 
            "DataProvider");
   }

   public void processFieldAnnotation(final FormMetadata formMetadata,
         final FieldMetadata fieldMetadata, final Annotation annotation) {
      AnnotationHandlerExceptionFactory.notFieldAnnotation(formMetadata, 
            fieldMetadata, "DataProvider", true);
   }

   public void processMethodAnnotation(final FormMetadata formMetadata,
         final MethodMetadata methodMetadata, final Annotation annotation) {

      final DataProvider annon = (DataProvider) annotation;
      final String widgetName = annon.widgetName();
      final String objectFieldName = annon.objectField();
      final String indexFieldName = annon.indexField();
      final boolean callOnInit = annon.callOnInit();
      final boolean resetSelection = annon.resetSelection();

      final DataProviderMetadata dataProviderMetadata = methodMetadata
            .getDataProviderMetadata();

      if (GenesisUtils.isBlank(widgetName)
            && GenesisUtils.isBlank(objectFieldName)
            && GenesisUtils.isBlank(indexFieldName)) {
         StringBuffer errorMessage = new StringBuffer("At least one of " +
               "widgetName, objectField or indexField must be specified for " +
               "@DataProvider");
         AnnotationHandlerExceptionFactory.appendMethodName(formMetadata, 
               methodMetadata, errorMessage);
         throw new IllegalArgumentException(errorMessage.toString());
      }

      dataProviderMetadata.setCallOnInit(callOnInit);
      dataProviderMetadata.setResetSelection(resetSelection);

      PropertyDescriptor[] descriptors = PropertyUtils
            .getPropertyDescriptors(formMetadata.getFormClass());

      Map descriptorsPerPropertyName = new HashMap();

      for (int i = 0; i < descriptors.length; i++) {
         descriptorsPerPropertyName.put(descriptors[i].getName(),
               descriptors[i]);
      }

      if (!GenesisUtils.isBlank(objectFieldName)) {
         PropertyDescriptor descriptor = (PropertyDescriptor) descriptorsPerPropertyName
               .get(objectFieldName);

         if (descriptor == null || descriptor.getReadMethod() == null ||
               descriptor.getWriteMethod() == null) {
            StringBuffer errorMessage = new StringBuffer("@DataProvider " +
                  "objectField refers to '").append(objectFieldName).append(
                  "', but ");

            if (descriptor == null) {
               errorMessage.append("such a public property does not exist");
            } else {
               errorMessage.append("it is not a read-write property");
            }

            errorMessage.append("; maybe creating public <type> get");
            
            StringBuffer propertyName = new StringBuffer().append(Character
                  .toUpperCase(objectFieldName.charAt(0)));

            if (objectFieldName.length() > 1) {
               propertyName.append(objectFieldName.substring(1));
            }

            errorMessage.append(propertyName).append("() and public void set")
                  .append(propertyName).append("(<type> var) will fix it");
            AnnotationHandlerExceptionFactory.appendMethodName(formMetadata, 
                  methodMetadata, errorMessage);

            throw new IllegalArgumentException(errorMessage.toString());
         }

         final Class fieldType = descriptor.getPropertyType();

         if (fieldType.isPrimitive()
               || (fieldType.isArray() && fieldType.getComponentType()
                     .isPrimitive())) {
            StringBuffer errorMessage = new StringBuffer("@DataProvider " +
                  "objectField refers to '").append(objectFieldName).append(
                  "', but it cannot be a primitive or an array of " +
                  "primitives");

            AnnotationHandlerExceptionFactory.appendMethodName(formMetadata, 
                  methodMetadata, errorMessage);

            throw new IllegalArgumentException(errorMessage.toString());
         }

         dataProviderMetadata.setObjectField(new FieldEntry(descriptor
               .getName(), descriptor.getPropertyType()));
      }

      if (!GenesisUtils.isBlank(indexFieldName)) {
         PropertyDescriptor descriptor = (PropertyDescriptor) descriptorsPerPropertyName
               .get(indexFieldName);

         if (descriptor == null || descriptor.getReadMethod() == null ||
               descriptor.getWriteMethod() == null) {
            StringBuffer errorMessage = new StringBuffer("@DataProvider " +
                  "indexField refers to '").append(indexFieldName).append(
                  "', but ");

            if (descriptor == null) {
               errorMessage.append("such a public property does not exist");
            } else {
               errorMessage.append("it is not a read-write property");
            }

            errorMessage.append("; maybe creating public <type> get");

            StringBuffer propertyName = new StringBuffer().append(Character
                  .toUpperCase(indexFieldName.charAt(0)));

            if (indexFieldName.length() > 1) {
               propertyName.append(indexFieldName.substring(1));
            }

            errorMessage.append(propertyName).append("() and public void set")
                  .append(propertyName).append("(<type> var) will fix it");
            AnnotationHandlerExceptionFactory.appendMethodName(formMetadata, 
                  methodMetadata, errorMessage);

            throw new IllegalArgumentException(errorMessage.toString());
         }

         final Class fieldType = descriptor.getPropertyType().isArray() ? descriptor
               .getPropertyType().getComponentType()
               : descriptor.getPropertyType();

         if (!Collection.class.isAssignableFrom(descriptor.getPropertyType())
               && !Integer.TYPE.isAssignableFrom(fieldType)
               && !Integer.class.isAssignableFrom(fieldType)) {
            StringBuffer errorMessage = new StringBuffer("@DataProvider " +
                  "indexField refers to '").append(indexFieldName).append(
                  "', but it is not an Integer, int, array of the previous " +
                  "types nor a java.util.Collection");

            AnnotationHandlerExceptionFactory.appendMethodName(formMetadata, 
                  methodMetadata, errorMessage);

            throw new IllegalArgumentException(errorMessage.toString());
         }

         dataProviderMetadata.setIndexField(new FieldEntry(
               descriptor.getName(), descriptor.getPropertyType()));
      }

      dataProviderMetadata
            .setWidgetName(!GenesisUtils.isBlank(widgetName) ? widgetName
                  : !GenesisUtils.isBlank(objectFieldName) ? objectFieldName
                        : indexFieldName);
   }
}