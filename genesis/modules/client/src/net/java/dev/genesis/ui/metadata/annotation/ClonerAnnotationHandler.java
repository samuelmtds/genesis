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

import java.util.Map;

import net.java.dev.genesis.cloning.Cloner;
import net.java.dev.genesis.cloning.ClonerRegistry;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.MethodMetadata;
import net.java.dev.genesis.util.GenesisUtils;

import org.codehaus.backport175.reader.Annotation;

public class ClonerAnnotationHandler implements AnnotationHandler {
   public void processFormAnnotation(final FormMetadata formMetadata,
         final Annotation annotation) {
      AnnotationHandlerExceptionFactory.notFormAnnotation(formMetadata, 
            "Cloner");
   }

   public void processFieldAnnotation(final FormMetadata formMetadata,
         final FieldMetadata fieldMetadata, final Annotation annotation) {
      fieldMetadata.setCloner(getCloner(fieldMetadata,
            (net.java.dev.genesis.annotation.Cloner) annotation));
   }

   public void processMethodAnnotation(final FormMetadata formMetadata,
         final MethodMetadata methodMetadata, final Annotation annotation) {
      AnnotationHandlerExceptionFactory.notMethodAnnotation(formMetadata, 
            methodMetadata, "Cloner");
   }

   private Cloner getCloner(final FieldMetadata fieldMetadata,
         final net.java.dev.genesis.annotation.Cloner annon) {

      final Map map = GenesisUtils.getAttributesMap(annon.properties());
      Class klazz = annon.value();
      if (klazz == null || Object.class.equals(klazz)) {
         return ClonerRegistry.getInstance().getDefaultClonerFor(
               fieldMetadata.getFieldClass(), map);
      }

      return ClonerRegistry.getInstance().getCloner(annon.value().getName(),
            map);
   }

}
