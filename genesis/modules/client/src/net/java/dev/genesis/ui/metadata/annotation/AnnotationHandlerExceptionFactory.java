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

import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.MemberMetadata;
import net.java.dev.genesis.ui.metadata.MethodMetadata;
import net.java.dev.genesis.util.Bundle;

class AnnotationHandlerExceptionFactory {
   private AnnotationHandlerExceptionFactory() {
   }

   private static String getAnnotationName(final String annotationName) {
      return "@" + annotationName; // NOI18N
   }

   public static String getMemberName(final FormMetadata formMetadata,
         final MemberMetadata memberMetadata) {
      return Bundle.getMessage(AnnotationHandlerExceptionFactory.class,
            "AT_MEMBER_X_Y", formMetadata.  // NOI18N
            getFormClass().getName(), memberMetadata.getName());
   }

   public static String getMethodName(final FormMetadata formMetadata,
         final MethodMetadata methodMetadata) {
      final String[] argsClassNames = methodMetadata.getMethodEntry().
            getArgsClassesNames();

      final StringBuffer classNames = new StringBuffer();
      for (int i = 0; i < argsClassNames.length; i++) {
         if (i > 0) {
            classNames.append(", "); // NOI18N
         }

         classNames.append(argsClassNames[i]);
      }

      return Bundle.getMessage(AnnotationHandlerExceptionFactory.class,
            "AT_X_Y_Z", new Object[] {formMetadata.getFormClass().getName(), // NOI18N
               methodMetadata.getName(), classNames});
   }

   public static void notFormAnnotation(final FormMetadata formMetadata, 
         final String annotationName) {
      throw new IllegalArgumentException(Bundle.getMessage(
            AnnotationHandlerExceptionFactory.class,
            "X_CANNOT_BE_USED_AS_A_FORM_ANNOTATION_AT_Y", // NOI18N
            getAnnotationName(annotationName),
            formMetadata.getFormClass().getName()));
   }

   public static void notFieldAnnotation(final FormMetadata formMetadata, 
         final FieldMetadata fieldMetadata, String annotationName, 
         final boolean methodIsValid) {
      final String key = methodIsValid ? "X_CANNOT_BE_USED_TO_ANNOTATE_A_PROPERTY_FIX_SUGGESTION_Y" // NOI18N
            : "X_CANNOT_BE_USED_TO_ANNOTATE_A_PROPERTY_Y"; // NOI18N
      annotationName = getAnnotationName(annotationName);
      final String memberName = getMemberName(formMetadata, fieldMetadata);

      throw new IllegalArgumentException(Bundle.getMessage(
            AnnotationHandlerExceptionFactory.class, key, annotationName,
            memberName));
   }

   public static void notMethodAnnotation(final FormMetadata formMetadata, 
         final MethodMetadata methodMetadata, String annotationName) {
      annotationName = getAnnotationName(annotationName);
      final String methodName = getMethodName(formMetadata, methodMetadata);

      throw new IllegalArgumentException(Bundle.getMessage(
            AnnotationHandlerExceptionFactory.class,
            "X_CANNOT_BE_USED_TO_ANNOTATE_A_METHOD_Y", annotationName, // NOI18N
            methodName));
   }


   public static void mustBeAction(final FormMetadata formMetadata,
         final MethodMetadata methodMetadata, String annotationName) {
      annotationName = getAnnotationName(annotationName);
      final String methodName = getMethodName(formMetadata, methodMetadata);

      throw new IllegalArgumentException(Bundle.getMessage(
            AnnotationHandlerExceptionFactory.class,
            "X_MUST_BE_ACTION_Y", annotationName, methodName)); // NOI18N
   }

   public static void mustBePropertyOrDataProvider(final FormMetadata formMetadata, 
         final MethodMetadata methodMetadata, String annotationName) {
      annotationName = getAnnotationName(annotationName);
      final String methodName = getMethodName(formMetadata, methodMetadata);

      throw new IllegalArgumentException(Bundle.getMessage(
            AnnotationHandlerExceptionFactory.class,
            "X_MUST_BE_PROPERTY_OR_DATA_PROVIDER_Y", annotationName, methodName)); // NOI18N
   }

   public static void mustBePropertyOrAction(final FormMetadata formMetadata, 
         final MethodMetadata methodMetadata, String annotationName) {
      annotationName = getAnnotationName(annotationName);
      final String methodName = getMethodName(formMetadata, methodMetadata);

      throw new IllegalArgumentException(Bundle.getMessage(
            AnnotationHandlerExceptionFactory.class,
            "X_MUST_BE_PROPERTY_OR_ACTION_Y", annotationName, methodName)); // NOI18N
   }
}
