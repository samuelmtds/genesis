/*
 * The Genesis Project
 * Copyright (C) 2005  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.aspect;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.java.dev.genesis.ui.metadata.ViewMetadata;
import net.java.dev.genesis.ui.metadata.ViewMetadataFactory;

import org.codehaus.aspectwerkz.annotation.Annotations;
import org.codehaus.aspectwerkz.annotation.UntypedAnnotation;

public class ViewMetadataFactoryAspect {
   /**
    * @Mixin(pointcut="viewMetadataFactoryIntroduction", isTransient=true, 
    *        deploymentModel="perJVM")
    */
   public static class AspectViewMetadataFactory implements
         ViewMetadataFactory {
      private final Map cache = new HashMap();

      public ViewMetadata getViewMetadata(final Class viewClass) {
         ViewMetadata viewMetadata = (ViewMetadata)cache
               .get(viewClass);

         if (viewMetadata == null) {
            viewMetadata = createViewMetadata(viewClass);
            processAnnotations(viewMetadata);
            cache.put(viewClass, viewMetadata);
         }

         return viewMetadata;
      }

      protected ViewMetadata createViewMetadata(Class viewClass) {
         return new ViewMetadata(viewClass);
      }

      private void processAnnotations(final ViewMetadata viewMetadata) {
         processMethodsAnnotations(viewMetadata);
      }

      private void processMethodsAnnotations(
            final ViewMetadata viewMetadata) {
         final Method[] methods = viewMetadata.getViewClass().getMethods();
         UntypedAnnotation annon;

         for (int i = 0; i < methods.length; i++) {
            for (final Iterator it = Annotations.getAnnotations(
                  "BeforeAction", methods[i]).iterator(); it.hasNext(); ) {
               annon = (UntypedAnnotation)it.next();

               final String actionName = annon.value().toString();
               final String methodName = methods[i].getName();
               viewMetadata.addBeforeAction(
                     actionName.trim().length() == 0 ? methodName : actionName,
                     methodName);
            }

            for (final Iterator it = Annotations.getAnnotations(
                  "AfterAction", methods[i]).iterator(); it.hasNext(); ) {
               annon = (UntypedAnnotation)it.next();

               final String actionName = annon.value().toString();
               final String methodName = methods[i].getName();
               viewMetadata.addAfterAction(
                     actionName.trim().length() == 0 ? methodName : actionName,
                     methodName);
            }
         }
      }
   }
}