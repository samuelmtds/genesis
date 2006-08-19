package net.java.dev.genesis.script.bsf.javascript;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.java.dev.genesis.script.ScriptFunctionsAdapter;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

public class WrapFactory extends org.mozilla.javascript.WrapFactory {
   private static Method[] methods = ScriptFunctionsAdapter.class.getMethods();
   private static Map javaMethods = new HashMap(methods.length);

   public WrapFactory() {
      setJavaPrimitiveWrap(false);
   }

   public Scriptable wrapAsJavaObject(Context cx, Scriptable scope,
         Object javaObject, Class staticType) {
      if (javaObject instanceof ScriptFunctionsAdapter) {
         return new NativeJavaObject();
      }

      return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
   }

   public Object wrap(Context cx, Scriptable scope, Object obj, Class staticType) {
      if ((obj == null) || (obj == Undefined.instance)
            || obj instanceof Scriptable) {
         return obj;
      }

      if ((staticType != null) && staticType.isPrimitive()) {
         if (staticType == Void.TYPE) {
            return Undefined.instance;
         }

         return obj;
      }

      if (!isJavaPrimitiveWrap()) {
         if (obj instanceof String || obj instanceof Number
               || obj instanceof Boolean || obj instanceof Character) {
            return obj;
         }
      }

      Class cls = obj.getClass();

      if (cls.isArray()) {
         return NativeJavaArray.wrap(scope, obj);
      }

      return wrapAsJavaObject(cx, scope, obj, staticType);
   }

   public static void main(String[] args) {
      Integer a = new Integer(0);
      Object o = Context.jsToJava(a, Character.TYPE);
      System.out.println(o.getClass());
   }

   protected class NativeJavaMethod extends
         org.mozilla.javascript.NativeJavaMethod {
      private final Method m;

      public NativeJavaMethod(Method method, String name) {
         super(method, name);
         this.m = method;
      }

      /**
       * <p>
       * Alternative call method that do not coerce Java Objects (e.g.:
       * java.lang.Integer to java.lang.Double).
       * </p>
       * 
       * <p>
       * Only Javascript Objects are coerced
       * </p>
       */
      public Object call(Context cx, Scriptable scope, Scriptable thisObj,
            Object[] args) {
         try {
            Class[] parameterTypes = m.getParameterTypes();

            if (parameterTypes.length != args.length) {
               methodNofFound(args);
            }

            for (int i = 0; i < args.length; i++) {
               if (args[i] == null) {
                  continue;
               }

               if ((args[i] == Undefined.instance)
                     || args[i] instanceof Scriptable
                     || (parameterTypes[i] == Character.TYPE)) {
                  if (!NativeJavaObject.canConvert(args[i], parameterTypes[i])) {
                     methodNofFound(args);
                  }

                  args[i] = Context.jsToJava(args[i], parameterTypes[i]);
               }
            }

            return m.invoke(null, args);
         } catch (Exception ex) {
            throw Context.throwAsScriptRuntimeEx(ex);
         }
      }

      protected void methodNofFound(Object[] args) {
         StringBuffer buffer = new StringBuffer();
         buffer.append(m.getDeclaringClass());
         buffer.append('.');
         buffer.append(getFunctionName());
         buffer.append('(');

         for (int i = 0; i < args.length; i++) {
            if (i > 0) {
               buffer.append(',');
            }

            buffer.append((args[i] == null) ? "null" : args[i].getClass()
                  .getName());
         }

         buffer.append(')');

         throw Context.reportRuntimeError(ScriptRuntime.getMessage1(
               "msg.java.no_such_method", buffer.toString()));
      }
   }

   protected class NativeJavaObject extends
         org.mozilla.javascript.NativeJavaObject {
      public Object get(String name, Scriptable start) {
         NativeJavaMethod method = (NativeJavaMethod) javaMethods.get(name);

         if (method != null) {
            return method;
         }

         for (int i = 0; i < methods.length; i++) {
            final Method m = methods[i];

            if (m.getName().equals(name)) {
               method = new NativeJavaMethod(m, name);
               javaMethods.put(name, method);

               return method;
            }
         }

         return Scriptable.NOT_FOUND;
      }
   }
}
