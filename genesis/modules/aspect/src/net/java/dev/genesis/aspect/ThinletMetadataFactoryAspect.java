/*
 * The Genesis Project
 * Copyright (C) 2004  Summa Technologies do Brasil Ltda.
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
import java.util.Map;

import net.java.dev.genesis.ui.thinlet.metadata.ThinletMetadata;
import net.java.dev.genesis.ui.thinlet.metadata.ThinletMetadataFactory;

import org.codehaus.aspectwerkz.CrossCuttingInfo;
import org.codehaus.aspectwerkz.annotation.Annotations;
import org.codehaus.aspectwerkz.annotation.UntypedAnnotationProxy;

public class ThinletMetadataFactoryAspect {
   protected final CrossCuttingInfo ccInfo;

   public ThinletMetadataFactoryAspect(final CrossCuttingInfo ccInfo) {
      this.ccInfo = ccInfo;
   }

   /**
    * @Introduce thinletMetadataFactoryIntroduction deploymentModel=perJVM
    */
   public static class AspectThinletMetadataFactory implements
         ThinletMetadataFactory {
      private final Map cache = new HashMap();

      public ThinletMetadata getThinletMetadata(final Class thinletClass) {
         ThinletMetadata thinletMetadata = (ThinletMetadata)cache
               .get(thinletClass);
         if (thinletMetadata == null) {
            thinletMetadata = new ThinletMetadata(thinletClass);
            processAnnotations(thinletMetadata);
            cache.put(thinletClass, thinletMetadata);
         }
         return thinletMetadata;
      }

      private void processAnnotations(final ThinletMetadata thinletMetadata) {
         processMethodsAnnotations(thinletMetadata);
      }

      private void processMethodsAnnotations(
            final ThinletMetadata thinletMetadata) {

         Method[] methods = thinletMetadata.getThinletClass()
               .getDeclaredMethods();
         UntypedAnnotationProxy annon;

         for (int i = 0; i < methods.length; i++) {

            annon = (UntypedAnnotationProxy)Annotations.getAnnotation(
                  "BeforeAction", methods[i]);
            if (annon != null) {
               final String actionName = annon.getValue();
               final String methodName = methods[i].getName();
               thinletMetadata.addBeforeAction(
                     actionName.trim().length() == 0 ? methodName : actionName,
                     methodName);
            }

            annon = (UntypedAnnotationProxy)Annotations.getAnnotation(
                  "AfterAction", methods[i]);
            if (annon != null) {
               final String actionName = annon.getValue();
               final String methodName = methods[i].getName();
               thinletMetadata.addAfterAction(
                     actionName.trim().length() == 0 ? methodName : actionName,
                     methodName);
            }
         }
      }
   }
}