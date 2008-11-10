/*
 * The Genesis Project
 * Copyright (C) 2006-2008  Summa Technologies do Brasil Ltda.
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
import net.java.dev.genesis.util.Bundle;
import net.java.dev.genesis.util.GenesisUtils;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.backport175.reader.Annotation;

public class DataProviderAnnotationHandler implements AnnotationHandler {
   public void processFormAnnotation(final FormMetadata formMetadata,
         final Annotation annotation) {
      AnnotationHandlerExceptionFactory.notFormAnnotation(formMetadata, 
            "DataProvider"); // NOI18N
   }

   public void processFieldAnnotation(final FormMetadata formMetadata,
         final FieldMetadata fieldMetadata, final Annotation annotation) {
      AnnotationHandlerExceptionFactory.notFieldAnnotation(formMetadata, 
            fieldMetadata, "DataProvider", true); // NOI18N
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

      final String methodName =
            AnnotationHandlerExceptionFactory.getMethodName(formMetadata,
            methodMetadata);

      if (GenesisUtils.isBlank(widgetName)
            && GenesisUtils.isBlank(objectFieldName)
            && GenesisUtils.isBlank(indexFieldName)) {
         String errorMessage = Bundle.getMessage(getClass(),
               "AT_LEAST_ONE_OF_OPTIONS_MUST_BE_SPECIFIED_FOR_DATAPROVIDER_X", // NOI18N
               methodName);
         throw new IllegalArgumentException(errorMessage);
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

            StringBuffer propertyName = new StringBuffer().append(Character
                  .toUpperCase(objectFieldName.charAt(0)));

            if (objectFieldName.length() > 1) {
               propertyName.append(objectFieldName.substring(1));
            }

            String key =
                  descriptor == null ? "DATAPROVIDER_OBJECTFIELD_REFERS_TO_X_BUT_PUBLIC_PROPERTY_DOES_NOT_EXIST" : // NOI18N
                  "DATAPROVIDER_OBJECTFIELD_REFERS_TO_X_BUT_IT_IS_NOT_A_READ_WRITE_PROPERTY"; // NOI18N
            String errorMessage = Bundle.getMessage(getClass(), key,
                  new Object[] {objectFieldName, propertyName, methodName});

            throw new IllegalArgumentException(errorMessage);
         }

         final Class fieldType = descriptor.getPropertyType();

         if (fieldType.isPrimitive()
               || (fieldType.isArray() && fieldType.getComponentType()
                     .isPrimitive())) {
            String errorMessage = Bundle.getMessage(getClass(),
                  "DATAPROVIDER_OBJECTFIELD_REFERS_TO_X_BUT_IT_IS_NOT_A_PRIMITIVE_OR_ARRAY_OF_PRIMITIVES", // NOI18N
                  objectFieldName, methodName);

            throw new IllegalArgumentException(errorMessage);
         }

         dataProviderMetadata.setObjectField(new FieldEntry(descriptor
               .getName(), descriptor.getPropertyType()));
      }

      if (!GenesisUtils.isBlank(indexFieldName)) {
         PropertyDescriptor descriptor = (PropertyDescriptor) descriptorsPerPropertyName
               .get(indexFieldName);

         if (descriptor == null || descriptor.getReadMethod() == null ||
               descriptor.getWriteMethod() == null) {
            final StringBuffer propertyName = new StringBuffer().append(Character
                  .toUpperCase(indexFieldName.charAt(0)));

            if (indexFieldName.length() > 1) {
               propertyName.append(indexFieldName.substring(1));
            }

            String key =
                  descriptor == null ? "DATAPROVIDER_INDEXFIELD_REFERS_TO_X_BUT_SUCH_PUBLIC_PROPERTY_DOES_NOT_EXIST" : // NOI18N
                  "DATAPROVIDER_INDEXFIELD_REFERS_TO_X_BUT_IT_IS_NOT_A_READ_WRITE_PROPERTY"; // NOI18N
            String errorMessage = Bundle.getMessage(getClass(), key,
                  new Object[] {indexFieldName, propertyName, methodName});
            throw new IllegalArgumentException(errorMessage);
         }

         final Class fieldType = descriptor.getPropertyType().isArray() ? descriptor
               .getPropertyType().getComponentType()
               : descriptor.getPropertyType();

         if (!Collection.class.isAssignableFrom(descriptor.getPropertyType())
               && !Integer.TYPE.isAssignableFrom(fieldType)
               && !Integer.class.isAssignableFrom(fieldType)) {
            String errorMessage = Bundle.getMessage(getClass(),
                  "DATAPROVIDER_INDEXFIELD_REFERS_TO_X_BUT_IT_IS_NOT_AN_INTEGER_INT_ARRAY_COLLECTION", // NOI18N
                  indexFieldName, methodName);
            throw new IllegalArgumentException(errorMessage);
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