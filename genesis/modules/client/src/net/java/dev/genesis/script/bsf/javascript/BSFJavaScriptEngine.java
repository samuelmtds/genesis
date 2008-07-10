/*
 * The Genesis Project
 * Copyright (C) 2005-2008  Summa Technologies do Brasil Ltda.
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

import org.apache.bsf.BSFDeclaredBean;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.BSFEngineImpl;
import org.apache.bsf.util.BSFFunctions;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.WrappedException;
import org.mozilla.javascript.Wrapper;

import java.util.Iterator;
import java.util.Vector;
import net.java.dev.genesis.util.Bundle;

public class BSFJavaScriptEngine extends BSFEngineImpl {
   private static WrapFactory wrapFactory =
      new net.java.dev.genesis.script.bsf.javascript.WrapFactory();
   private Scriptable global;

   public Object call(Object object, String method, Object[] args)
            throws BSFException {
      Object theReturnValue = null;
      Context cx;

      try {
         cx = Context.enter();

         Object fun = global.get(method, global);

         if (fun == Scriptable.NOT_FOUND) {
            throw new JavaScriptException(Bundle.getMessage(
                  BSFJavaScriptEngine.class, "FUNCTION_X_NOT_FOUND", method), "", 0); // NOI18N
         }

         cx.setGeneratingDebug(false);
         cx.setGeneratingSource(false);
         cx.setWrapFactory(wrapFactory);
         cx.setOptimizationLevel(-1);

         Function function = (Function) fun;
         Scriptable thisObj = ScriptRuntime.toObjectOrNull(cx, fun);

         if (thisObj == null) {
            throw ScriptRuntime.undefCallError(thisObj, "function"); // NOI18N
         }

         theReturnValue = function.call(cx, null, thisObj, args);

         if (theReturnValue instanceof Wrapper) {
            theReturnValue = ((Wrapper) theReturnValue).unwrap();
         }
      } catch (Throwable t) {
         handleError(t);
      } finally {
         Context.exit();
      }

      return theReturnValue;
   }

   public void declareBean(BSFDeclaredBean bean) throws BSFException {
      Context.enter();

      try {
         global.put(bean.name, global, Context.toObject(bean.bean, global));
      } finally {
         Context.exit();
      }
   }

   public Object eval(String source, int lineNo, int columnNo, Object oscript)
            throws BSFException {
      String scriptText = oscript.toString();
      Object retval = null;
      Context cx;

      try {
         cx = Context.enter();

         cx.setOptimizationLevel(-1);
         cx.setGeneratingDebug(false);
         cx.setGeneratingSource(false);
         cx.setWrapFactory(wrapFactory);

         retval = cx.evaluateString(global, scriptText, source, lineNo, null);

         if (retval instanceof Wrapper) {
            retval = ((Wrapper) retval).unwrap();
         } else if (retval instanceof ScriptableObject) {
            retval = ((ScriptableObject) retval).getDefaultValue(ScriptRuntime.BooleanClass);
         }
      } catch (Throwable t) { // includes JavaScriptException, rethrows Errors
         handleError(t);
      } finally {
         Context.exit();
      }

      return retval;
   }

   private void handleError(Throwable t) throws BSFException {
      if (t instanceof WrappedException) {
         t = ((WrappedException) t).getWrappedException();
      }

      String message = null;
      Throwable target = t;

      if (t instanceof JavaScriptException) {
         message = t.getLocalizedMessage();

         // Is it an exception wrapped in a JavaScriptException?
         Object value = ((JavaScriptException) t).getValue();

         if (value instanceof Throwable) {
            // likely a wrapped exception from a LiveConnect call.
            // Display its stack trace as a diagnostic
            target = (Throwable) value;
         }
      } else if (t instanceof EvaluatorException ||
            t instanceof SecurityException) {
         message = t.getLocalizedMessage();
      } else if (t instanceof RuntimeException) {
         message = Bundle.getMessage(BSFJavaScriptEngine.class,
               "INTERNAL_ERROR_X", t.toString()); // NOI18N
      } else if (t instanceof StackOverflowError) {
         message = Bundle.getMessage(BSFJavaScriptEngine.class, "STACK_OVERFLOW"); // NOI18N
      }

      if (message == null) {
         message = t.toString();
      }

      // REMIND: can we recover the line number here? I think
      // Rhino does this by looking up the stack for bytecode
      // see Context.getSourcePositionFromStack()
      // but I don't think this would work in interpreted mode
      if (t instanceof Error && !(t instanceof StackOverflowError)) {
         // Re-throw Errors because we're supposed to let the JVM see it
         // Don't re-throw StackOverflows, because we know we've
         // corrected the situation by aborting the loop and
         // a long stacktrace would end up on the user's console
         throw (Error) t;
      } else {
         throw new BSFException(BSFException.REASON_OTHER_ERROR,
            Bundle.getMessage(BSFJavaScriptEngine.class, "JAVASCRIPT_ERROR_X", // NOI18N
            message), target);
      }
   }

   public void initialize(BSFManager mgr, String lang, Vector declaredBeans)
            throws BSFException {
      super.initialize(mgr, lang, declaredBeans);

      try {
         Context cx = Context.enter();
         cx.setWrapFactory(wrapFactory);
         global = new ImporterTopLevel(cx);
         cx.evaluateString(global, "importPackage(java.lang)", null, 0, null); // NOI18N

         Scriptable bsf = Context.toObject(new BSFFunctions(mgr, this), global);
         global.put("bsf", global, bsf); // NOI18N

         for (Iterator iter = declaredBeans.iterator(); iter.hasNext();) {
            BSFDeclaredBean bean = (BSFDeclaredBean) iter.next();
            Scriptable wrapped = Context.toObject(bean.bean, global);
            global.put(bean.name, global, wrapped);
         }
      } catch (Throwable t) {
         handleError(t);
      } finally {
         Context.exit();
      }
   }

   public void undeclareBean(BSFDeclaredBean bean) throws BSFException {
      Context.enter();

      try {
         global.delete(bean.name);
      } finally {
         Context.exit();
      }
   }
}
