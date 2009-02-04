/*
 * The Genesis Project
 * Copyright (C) 2006-2009  Summa Technologies do Brasil Ltda.
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

import net.java.dev.genesis.annotation.EnabledWhen;
import net.java.dev.genesis.script.Script;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.MemberMetadata;
import net.java.dev.genesis.ui.metadata.MethodMetadata;

import net.java.dev.genesis.util.Bundle;
import org.codehaus.backport175.reader.Annotation;

public class EnabledWhenAnnotationHandler implements AnnotationHandler {
   public void processFormAnnotation(final FormMetadata formMetadata,
         final Annotation annotation) {
      AnnotationHandlerExceptionFactory.notFormAnnotation(formMetadata,
            "EnabledWhen"); // NOI18N
   }

   public void processFieldAnnotation(final FormMetadata formMetadata,
         final FieldMetadata fieldMetadata, final Annotation annotation) {
      processMemberAnnotation( formMetadata,
            fieldMetadata,annotation);
   }

   public void processMethodAnnotation(final FormMetadata formMetadata,
         final MethodMetadata methodMetadata, final Annotation annotation) {
      if (methodMetadata.getActionMetadata() == null) {
         AnnotationHandlerExceptionFactory.mustBePropertyOrAction(formMetadata,
               methodMetadata, "EnabledWhen"); // NOI18N
      }

      processMemberAnnotation(formMetadata,methodMetadata.getActionMetadata(), annotation);
   }

   private void processMemberAnnotation(final FormMetadata formMetadata,
         final MemberMetadata memberMetadata, final Annotation annotation) {
      EnabledWhen annon = (EnabledWhen)annotation;
      final String value = annon.value();

      if (value == null || value.trim().length() == 0) {
         final String memberName = AnnotationHandlerExceptionFactory.
               getMemberName(formMetadata, memberMetadata);

         throw new IllegalArgumentException(Bundle.getMessage(
               getClass(), "X_MUST_DEFINE_AT_LEAST_ONE_SCRIPT_CONDITION_Y", // NOI18N
               "EnabledWhen", memberName));
      }

      final Script script = formMetadata.getScript();
      memberMetadata.setEnabledCondition(script.compile(value));
   }
}
