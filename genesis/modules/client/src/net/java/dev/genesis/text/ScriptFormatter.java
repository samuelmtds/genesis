/*
 * The Genesis Project
 * Copyright (C) 2007-2008  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.text;

import net.java.dev.genesis.script.Script;
import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.script.ScriptExpression;
import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.controller.FormControllerRegistry;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataRegistry;
import net.java.dev.genesis.util.Bundle;

public class ScriptFormatter implements Formatter {
   public static final String ALIAS = "o"; // NOI18N

   private final Object form;
   private final FormController controller;
   private final Script script;
   private final ScriptExpression expression;
   private final String alias;

   public ScriptFormatter(Object form, String expression) {
      this(form, expression, ALIAS);
   }

   public ScriptFormatter(Object form, String expression, String alias) {
      final Class formClass = form.getClass();
      final FormMetadata formMetadata = FormMetadataRegistry.getInstance().get(
            formClass);

      if (formMetadata == null) {
         throw new IllegalArgumentException(Bundle.getMessage(ScriptFormatter.class,
               "X_HAS_NO_METADATA_REGISTERED", formClass)); // NOI18N
      }

      final FormController controller = FormControllerRegistry.getInstance().
            get(form);

      if (controller == null) {
         throw new IllegalArgumentException(Bundle.getMessage(ScriptFormatter.class,
               "X_HAS_NO_CONTROLLER_REGISTERED", form)); // NOI18N
      }

      this.form = form;
      this.controller = controller;
      this.script = formMetadata.getScript();
      this.expression = script.compile(expression);
      this.alias = alias;
   }

   public String format(Object o) {
      if (!controller.isSetup()) {
         throw new IllegalStateException(Bundle.getMessage(ScriptFormatter.class,
               "FORM_CONTROLLER_HAS_NOT_BEEN_SETUP")); // NOI18N
      }

      final ScriptContext scriptContext = controller.getScriptContext();

      if (scriptContext.getContextMap().containsKey(alias)) {
         throw new IllegalStateException(Bundle.getMessage(ScriptFormatter.class,
               "X_IS_ALREADY_PRESENT_IN_THE_SCRIPT_CONTEXT", alias)); // NOI18N
      }

      scriptContext.declare(alias, o);

      try {
         return FormatterRegistry.getInstance().format(expression.eval(
               scriptContext));
      } finally {
         scriptContext.undeclare(alias);
      }
   }
}
