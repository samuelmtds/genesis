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
package net.java.dev.genesis.ui.metadata.annotation;

import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.MemberMetadata;
import net.java.dev.genesis.ui.metadata.MethodMetadata;

class AnnotationHandlerExceptionFactory {
   private AnnotationHandlerExceptionFactory() {
   }

   private static StringBuffer getAnnotationName(final String annotationName) {
      return new StringBuffer("@").append(annotationName);
   }

   private static void appendFormClassName(final FormMetadata formMetadata, 
         final StringBuffer errorMessage) {
      errorMessage.append(" ( at ").append(formMetadata.getFormClass().getName())
            .append(")");
   }

   public static void appendMemberName(final FormMetadata formMetadata, 
         final MemberMetadata memberMetadata, final StringBuffer errorMessage) {
      errorMessage.append(" ( at member ").append(formMetadata.getFormClass()
            .getName()).append(":").append(memberMetadata.getName())
            .append(")");
   }

   public static void appendMethodName(final FormMetadata formMetadata, 
         final MethodMetadata methodMetadata, final StringBuffer errorMessage) {
      errorMessage.append(" ( at ").append(formMetadata.getFormClass()
            .getName()).append(".").append(methodMetadata.getName()).append("(");

      final String[] argsClassNames = methodMetadata.getMethodEntry()
            .getArgsClassesNames();

      for (int i = 0; i < argsClassNames.length; i++) {
         if (i > 0) {
            errorMessage.append(", ");
         }
         
         errorMessage.append(argsClassNames[i]);
      }

      errorMessage.append(") )");
   }

   public static void notFormAnnotation(final FormMetadata formMetadata, 
         final String annotationName) {
      StringBuffer errorMessage = getAnnotationName(annotationName);
      errorMessage.append(" cannot be used as a form annotation");
      appendFormClassName(formMetadata, errorMessage);

      throw new IllegalArgumentException(errorMessage.toString());
   }

   public static void notFieldAnnotation(final FormMetadata formMetadata, 
         final FieldMetadata fieldMetadata, final String annotationName, 
         final boolean methodIsValid) {
      StringBuffer errorMessage = getAnnotationName(annotationName);
      errorMessage.append(" cannot be used to annotate a property");
      
      if (methodIsValid) {
         errorMessage.append("; maybe removing the get prefix from the " +
               "method will fix it");
      }

      appendMemberName(formMetadata, fieldMetadata, errorMessage);

      throw new IllegalArgumentException(errorMessage.toString());
   }

   public static void notMethodAnnotation(final FormMetadata formMetadata, 
         final MethodMetadata methodMetadata, final String annotationName) {
      StringBuffer errorMessage = getAnnotationName(annotationName);
      errorMessage.append(" cannot be used to annotate a method");
      appendMethodName(formMetadata, methodMetadata, errorMessage);

      throw new IllegalArgumentException(errorMessage.toString());
   }


   public static void mustBeAction(final FormMetadata formMetadata, 
         final MethodMetadata methodMetadata, final String annotationName) {
      mustBeUsed(formMetadata, methodMetadata, annotationName, 
            "with a method annotated with @Action");
   }

   public static void mustBePropertyOrDataProvider(final FormMetadata formMetadata, 
         final MethodMetadata methodMetadata, final String annotationName) {
      mustBeUsed(formMetadata, methodMetadata, annotationName, 
            "either with a getter or a method annotated with @DataProvider");
   }

   public static void mustBePropertyOrAction(final FormMetadata formMetadata, 
         final MethodMetadata methodMetadata, final String annotationName) {
      mustBeUsed(formMetadata, methodMetadata, annotationName, 
            "either with a getter or a method annotated with @Action");
   }

   private static void mustBeUsed(final FormMetadata formMetadata, 
         final MethodMetadata methodMetadata, final String annotationName, 
         final String what) throws IllegalArgumentException {
      StringBuffer errorMessage = getAnnotationName(annotationName);
      errorMessage.append(" must be used ").append(what);
      appendMethodName(formMetadata, methodMetadata, errorMessage);

      throw new IllegalArgumentException(errorMessage.toString());
   }
}
