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

public class ScriptFormatter implements Formatter {
   public static final String ALIAS = "o";

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
         throw new IllegalArgumentException(formClass + " has no metadata " +
               "registered; you probably need to instantiate a binder for it " +
               "before calling this method");
      }

      final FormController controller = FormControllerRegistry.getInstance().
            get(form);

      if (controller == null) {
         throw new IllegalArgumentException(form + " has no controller " +
               "registered; you probably need to instantiate a binder for it " +
               "before calling this method");
      }

      this.form = form;
      this.controller = controller;
      this.script = formMetadata.getScript();
      this.expression = script.compile(expression);
      this.alias = alias;
   }

   public String format(Object o) {
      if (!controller.isSetup()) {
         throw new IllegalStateException("Form controller has not been setup");
      }

      final ScriptContext scriptContext = controller.getScriptContext();

      if (scriptContext.getContextMap().containsKey(alias)) {
         throw new IllegalStateException(alias + " is already present in the " +
               "script context; try to use a different alias at the " +
               "constructor");
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
