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
package net.java.dev.genesis.script.javaxscript.bridge;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

public class JavaxScriptBridge {
   private static final JavaxScriptBridge instance = new JavaxScriptBridge();

   private Class scriptEngineManagerClass;
   private Class scriptContextClass;
   private Class compiledScriptClass;
   private Class compilableClass;
   private Method originalEvalMethod;
   private int globalScope;
   private int engineScope;
   private Set methods = new HashSet(2);

   private JavaxScriptBridge() {
      try {
         scriptContextClass = Class.forName("javax.script.ScriptContext");
         compiledScriptClass = Class.forName("javax.script.CompiledScript");
         compilableClass = Class.forName("javax.script.Compilable");
         originalEvalMethod = compiledScriptClass.getDeclaredMethod("eval",
               new Class[] { scriptContextClass });
         globalScope = scriptContextClass.getField("GLOBAL_SCOPE").getInt(null);
         engineScope = scriptContextClass.getField("ENGINE_SCOPE").getInt(null);

         methods.add("getEngineByName");
         methods.add("getContext");

         scriptEngineManagerClass = Class
               .forName("javax.script.ScriptEngineManager");
      } catch (Exception e) {
         // not JDK 6 or later
      }
   }

   public static JavaxScriptBridge getInstance() {
      return instance;
   }

   public boolean supportsJavaxScript() {
      return scriptEngineManagerClass != null;
   }

   public Class getScriptEngineManagerClass() {
      return scriptEngineManagerClass;
   }

   public int getGlobalScope() {
      return globalScope;
   }

   public int getEngineScope() {
      return engineScope;
   }

   public Object eval(Object realCompiledScript, Object realCtx)
         throws Exception {
      return originalEvalMethod.invoke(realCompiledScript,
            new Object[] { realCtx });
   }

   public ScriptEngineManager createScriptEngineManager() {
      Object targetInstance = newInstance(scriptEngineManagerClass);
      ProxyInvocationHandler handler = new ProxyInvocationHandler(
            targetInstance);

      return (ScriptEngineManager) createProxy(ScriptEngineManager.class,
            handler);
   }

   protected Object createProxy(Class desiredClass, InvocationHandler handler) {
      return createProxy(new Class[] { desiredClass }, handler);
   }

   protected Object createProxy(Class[] desiredClasses,
         InvocationHandler handler) {
      return Proxy.newProxyInstance(Thread.currentThread()
            .getContextClassLoader(), desiredClasses, handler);
   }

   protected Object newInstance(Class clazz) {
      try {
         return clazz.newInstance();
      } catch (InstantiationException e) {
         throw new RuntimeException(e.getMessage(), e);
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e.getMessage(), e);
      }
   }

   public class ProxyInvocationHandler implements InvocationHandler {
      private Object target;
      private boolean isScriptContext;
      private boolean isCompilable;

      public ProxyInvocationHandler(Object target) {
         this.target = target;
         isScriptContext = scriptContextClass.isAssignableFrom(target
               .getClass());
         isCompilable = compilableClass.isAssignableFrom(target.getClass());
      }

      protected boolean needsProxy(String methodName) {
         return methods.contains(methodName);
      }

      public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
         final String methodName = method.getName();
         if (isScriptContext && methodName.equals("getRealContext")) {
            return target;
         }

         if (methodName.equals("compile") && !isCompilable) {
            return null;
         }

         final Method originalMethod = target.getClass().getMethod(
               method.getName(), method.getParameterTypes());

         Object result;

         try {
            result = originalMethod.invoke(target, args);
         } catch (InvocationTargetException ite) {
            throw ite.getTargetException();
         }

         if (result == null) {
            return null;
         }

         if (!needsProxy(methodName)) {
            return result;
         }

         return createProxy(new Class[] { method.getReturnType() },
               new ProxyInvocationHandler(result));
      }
   }
}
