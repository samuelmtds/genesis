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

import net.java.dev.genesis.annotation.VisibleWhen;
import net.java.dev.genesis.script.Script;
import net.java.dev.genesis.script.ScriptRegistry;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.MemberMetadata;
import net.java.dev.genesis.ui.metadata.MethodMetadata;

import net.java.dev.genesis.util.Bundle;
import org.codehaus.backport175.reader.Annotation;

public class VisibleWhenAnnotationHandler implements AnnotationHandler {
   public void processFormAnnotation(final FormMetadata formMetadata,
         final Annotation annotation) {
      AnnotationHandlerExceptionFactory.notFormAnnotation(formMetadata, 
            "VisibleWhen"); // NOI18N
   }

   public void processFieldAnnotation(final FormMetadata formMetadata,
         final FieldMetadata fieldMetadata, final Annotation annotation) {
      processMemberAnnotation(formMetadata, fieldMetadata, annotation);
   }

   public void processMethodAnnotation(final FormMetadata formMetadata,
         final MethodMetadata methodMetadata, final Annotation annotation) {
      if (methodMetadata.getActionMetadata() == null) {
         AnnotationHandlerExceptionFactory.mustBePropertyOrAction(formMetadata, 
               methodMetadata, "VisibleWhen"); // NOI18N
      }

      processMemberAnnotation(formMetadata, methodMetadata
            .getActionMetadata(), annotation);
   }

   private void processMemberAnnotation(final FormMetadata formMetadata,
         final MemberMetadata memberMetadata, final Annotation annotation) {
      VisibleWhen annon = (VisibleWhen) annotation;
      String[] values = annon.value();
      final String memberName =
            AnnotationHandlerExceptionFactory.getMemberName(formMetadata,
            memberMetadata);
      if (values == null || values.length == 0) {
         throw new IllegalArgumentException(Bundle.getMessage(
               getClass(),
               "VISIBLEWHEN_MUST_DEFINE_AT_LEAST_ONE_SCRIPT_CONDITION_X", // NOI18N
               memberName));
      }

      final Script script = formMetadata.getScript();

      if (values.length == 1) {
         memberMetadata.setVisibleCondition(script.compile(values[0]));
      } else if (values.length % 2 != 0) {
         throw new IllegalArgumentException(Bundle.getMessage(
               getClass(),
               "VISIBLEWHEN_MUST_DEFINE_AT_LEAST_ONE_SCRIPT_CONDITION_OR_PAIRS_OF_SCRIPT_CONDITIONS_X", // NOI18N
               memberName));
      } else {
         for (int i = 0; i < values.length; i += 2) {
            if (!ScriptRegistry.getInstance().isCurrentScriptFactoryNameFor(
                  values[i])) {
               continue;
            }

            memberMetadata.setVisibleCondition(script.compile(values[i + 1]));
            break;
         }
      }
   }
}
