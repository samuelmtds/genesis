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
package net.java.dev.genesis.script.bsf.javascript;

import java.util.Vector;

import org.apache.bsf.BSFDeclaredBean;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.engines.javascript.JavaScriptEngine;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeBoolean;
import org.mozilla.javascript.ScriptRuntime;

public class BSFJavaScriptEngine extends JavaScriptEngine {
   private Context ctx;

   public void declareBean(BSFDeclaredBean bean) throws BSFException {
      ctx = Context.enter(ctx);
      try {
         super.declareBean(bean);
      } finally {
         Context.exit();
      }
   }

   public void undeclareBean(BSFDeclaredBean bean) throws BSFException {
      ctx = Context.enter(ctx);
      try {
         super.undeclareBean(bean);
      } finally {
         Context.exit();
      }
   }

   public void initialize(BSFManager mgr, String lang, Vector declaredBeans)
         throws BSFException {
      try {
         ctx = Context.enter();
         super.initialize(mgr, lang, declaredBeans);
      } finally {
         Context.exit();
      }
   }

   public Object eval(String source, int lineNo, int columnNo, Object oscript)
         throws BSFException {
      final Object result = super.eval(source, lineNo, columnNo, oscript);
      if (result instanceof NativeBoolean) {
         return ((NativeBoolean)result)
               .getDefaultValue(ScriptRuntime.BooleanClass);
      }
      return result;
   }
}