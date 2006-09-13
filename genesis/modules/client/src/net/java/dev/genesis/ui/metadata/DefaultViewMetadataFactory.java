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

import java.lang.reflect.Method;

import net.java.dev.genesis.annotation.AfterAction;
import net.java.dev.genesis.annotation.BeforeAction;

import org.codehaus.backport175.reader.Annotation;
import org.codehaus.backport175.reader.Annotations;

public class DefaultViewMetadataFactory implements ViewMetadataFactory {
   private boolean skipSystemClasses = true;

   public void setSkipSystemClasses(boolean skip) {
      this.skipSystemClasses = skip;
   }

   public ViewMetadata getViewMetadata(final Class viewClass) {
      ViewMetadata viewMetadata = ViewMetadataRegistry.getInstance().get(viewClass);

      if (viewMetadata == null) {
         viewMetadata = createViewMetadata(viewClass);
         processAnnotations(viewMetadata);
         ViewMetadataRegistry.getInstance().register(viewClass, viewMetadata);
      }

      return viewMetadata;
   }

   protected ViewMetadata createViewMetadata(Class viewClass) {
      return new ViewMetadata(viewClass);
   }

   private void processAnnotations(final ViewMetadata viewMetadata) {
      processMethodsAnnotations(viewMetadata);
   }

   private void processMethodsAnnotations(final ViewMetadata viewMetadata) {
      final Method[] methods = viewMetadata.getViewClass().getMethods();

      for (int i = 0; i < methods.length; i++) {
         if (skipSystemClasses
               && methods[i].getDeclaringClass().getClassLoader() == null) {
            continue;
         }

         processMethodAnnotation(viewMetadata, methods[i]);
      }
   }

   private void processMethodAnnotation(final ViewMetadata viewMetadata,
         Method method) {
      Annotation[] annotations = Annotations.getAnnotations(method);

      for (int i = 0; i < annotations.length; i++) {
         if (BeforeAction.class.equals(annotations[i].annotationType())) {
            if (method.getParameterTypes().length != 0) {
               throw new IllegalArgumentException("@BeforeAction cannot "
                     + "be used in a method with parameters: "
                     + method.toString());
            }

            BeforeAction annon = (BeforeAction) annotations[i];
            final String[] actionNames = annon.value();
            final String methodName = method.getName();

            if (actionNames == null || actionNames.length == 0) {
               viewMetadata.addBeforeAction(methodName, methodName);
               continue;
            }

            for (int j = 0; j < actionNames.length; j++) {
               viewMetadata.addBeforeAction(actionNames[j], methodName);
            }
         } else if (AfterAction.class.equals(annotations[i].annotationType())) {
            if (method.getParameterTypes().length != 0) {
               throw new IllegalArgumentException("@AfterAction cannot "
                     + "be used in a method with parameters: "
                     + method.toString());
            }

            AfterAction annon = (AfterAction) annotations[i];
            final String[] actionNames = annon.value();
            final String methodName = method.getName();

            if (actionNames == null || actionNames.length == 0) {
               viewMetadata.addAfterAction(methodName, methodName);
               continue;
            }

            for (int j = 0; j < actionNames.length; j++) {
               viewMetadata.addAfterAction(actionNames[j], methodName);
            }
         }
      }
   }
}
