/*
 * The Genesis Project
 * Copyright (C) 2004-2005  Summa Technologies do Brasil Ltda.
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

import net.java.dev.genesis.ui.thinlet.metadata.ThinletMetadata;
import net.java.dev.genesis.ui.thinlet.metadata.ThinletMetadataFactory;

import org.codehaus.aspectwerkz.annotation.Annotations;
import org.codehaus.aspectwerkz.annotation.UntypedAnnotationProxy;

public class ThinletMetadataFactoryAspect {
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
         final Method[] methods = thinletMetadata.getThinletClass().getMethods();
         UntypedAnnotationProxy annon;

         for (int i = 0; i < methods.length; i++) {
            for (final Iterator it = Annotations.getAnnotations(
                  "BeforeAction", methods[i]).iterator(); it.hasNext(); ) {
               annon = (UntypedAnnotationProxy)it.next();

               final String actionName = annon.getValue();
               final String methodName = methods[i].getName();
               thinletMetadata.addBeforeAction(
                     actionName.trim().length() == 0 ? methodName : actionName,
                     methodName);
            }

            for (final Iterator it = Annotations.getAnnotations(
                  "AfterAction", methods[i]).iterator(); it.hasNext(); ) {
               annon = (UntypedAnnotationProxy)it.next();

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