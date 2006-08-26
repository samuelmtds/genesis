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
package net.java.dev.genesis.ui.metadata;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import net.java.dev.genesis.annotation.Action;
import net.java.dev.genesis.annotation.DataProvider;
import net.java.dev.genesis.annotation.NotBound;
import net.java.dev.genesis.script.ScriptRegistry;
import net.java.dev.genesis.ui.metadata.annotation.AnnotationHandler;
import net.java.dev.genesis.ui.metadata.annotation.AnnotationHandlerRegistry;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.backport175.reader.Annotation;
import org.codehaus.backport175.reader.Annotations;

public class DefaultFormMetadataFactory implements FormMetadataFactory {
   public FormMetadata getFormMetadata(final Class formClass) {
      FormMetadata formMetadata = FormMetadataRegistry.getInstance().get(formClass);

      if (formMetadata == null) {
         formMetadata = new FormMetadata(formClass, ScriptRegistry.getInstance().getScript());
         processAnnotations(formMetadata);
         FormMetadataRegistry.getInstance().register(formClass, formMetadata);
      }

      return formMetadata;
   }

   private void processAnnotations(final FormMetadata formMetadata) {
      processFormAnnotations(formMetadata);
      processFieldsAnnotations(formMetadata);
      processMethodsAnnotations(formMetadata);
   }

   private void processFormAnnotations(final FormMetadata formMetadata) {
      final Annotation[] annotations = Annotations.getAnnotations(formMetadata
            .getFormClass());
      AnnotationHandler handler;
      for (int i = 0; i < annotations.length; i++) {
         handler = AnnotationHandlerRegistry.getInstance().get(annotations[i].annotationType());

         if (handler == null) {
            continue;
         }

         handler.processFormAnnotation(formMetadata,
               annotations[i]);
      }
   }

   private void processFieldsAnnotations(final FormMetadata formMetadata) {
      final Class clazz = formMetadata.getFormClass();
      final PropertyDescriptor[] propertyDescriptors = PropertyUtils
            .getPropertyDescriptors(clazz);
      PropertyDescriptor propDesc;
      FieldMetadata fieldMetadata;
      for (int i = 0; i < propertyDescriptors.length; i++) {
         propDesc = propertyDescriptors[i];

         // Ignoring java.lang.Object.getClass()
         if (propDesc.getName().equals("class")) {
            continue;
         }

         final Method readMethod = getReadMethod(clazz, propDesc);

         if (readMethod == null || Annotations.getAnnotation(NotBound.class, 
               readMethod) != null) {
            continue;
         }

         fieldMetadata = new FieldMetadata(propDesc.getName(), propDesc
               .getPropertyType(), propDesc.getWriteMethod() != null);
         formMetadata.addFieldMetadata(propDesc.getName(), fieldMetadata);
         processFieldAnnotations(formMetadata, fieldMetadata, readMethod);
      }
   }

   private void processFieldAnnotations(final FormMetadata formMetadata,
         final FieldMetadata fieldMetadata, final Method fieldGetterMethod) {
      final Annotation[] annotations = Annotations.getAnnotations(fieldGetterMethod);
      AnnotationHandler handler;
      for (int i = 0; i < annotations.length; i++) {
         handler = AnnotationHandlerRegistry.getInstance().get(annotations[i].annotationType());

         if (handler == null) {
            continue;
         }

         handler.processFieldAnnotation(formMetadata,
               fieldMetadata, annotations[i]);
      }
   }

   private void processMethodsAnnotations(final FormMetadata formMetadata) {
      final Method[] methods = formMetadata.getFormClass().getMethods();
      boolean isAction;
      boolean isProvider;
      MethodMetadata methodMetadata;

      for (int i = 0; i < methods.length; i++) {
         isAction = Annotations.getAnnotation(Action.class, methods[i]) != null;
         isProvider = Annotations.getAnnotation(DataProvider.class, methods[i]) != null;

         if (!isAction && !isProvider) {
            continue;
         }

         if (formMetadata.getFieldMetadata(methods[i].getName()) != null) {
            throw new IllegalArgumentException("An @Action/@DataProvider " +
                  "cannot have the same name of a property: " + 
                  methods[i].getName());
         }

         methodMetadata = new MethodMetadata(methods[i], isAction, isProvider);
         formMetadata.addMethodMetadata(methods[i], methodMetadata);
         processMethodAnnotations(formMetadata, methodMetadata, methods[i]);
      }
   }

   private void processMethodAnnotations(final FormMetadata formMetadata,
         final MethodMetadata methodMetadata, final Method actionMethod) {
      final Annotation[] annotations = Annotations.getAnnotations(actionMethod);
      AnnotationHandler handler;
      for (int i = 0; i < annotations.length; i++) {
         handler = AnnotationHandlerRegistry.getInstance().get(annotations[i].annotationType());

         if (handler == null) {
            continue;
         }

         handler.processMethodAnnotation(formMetadata,
               methodMetadata, annotations[i]);
      }
   }

   // Work around for a bug in JDK 1.4.2
   private Method getReadMethod(Class clazz, PropertyDescriptor pd) {
      Method method = pd.getReadMethod();

      if (method == null || clazz.equals(method.getDeclaringClass())) {
         return method;
      }

      try {
         method = clazz.getMethod(method.getName(), 
               method.getParameterTypes());
         return (method == null) ? pd.getReadMethod() : method;
      } catch (NoSuchMethodException nsme) {
         return method;
      }
   }
}
