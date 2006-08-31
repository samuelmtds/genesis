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
package net.java.dev.genesis.script.mustang.bridge;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

public class JavaxScriptBridge {
   private static final JavaxScriptBridge instance = new JavaxScriptBridge();

   private Class scriptEngineManagerClass;
   private Class scriptEngineClass;
   private Class scriptContextClass;
   private Class bindingsClass;
   private int globalScope;
   private int engineScope;
   private Set methods = new HashSet(3);

   private JavaxScriptBridge() {
      try {
         scriptEngineManagerClass = Class
               .forName("javax.script.ScriptEngineManager");
         scriptContextClass = Class.forName("javax.script.ScriptContext");
         bindingsClass = Class.forName("javax.script.Bindings");
         globalScope = scriptContextClass.getField("GLOBAL_SCOPE").getInt(null);
         engineScope = scriptContextClass.getField("ENGINE_SCOPE").getInt(null);

         methods.add(ScriptEngineManager.class.getDeclaredMethod(
               "getEngineByName", new Class[] { String.class }));
         methods.add(ScriptEngine.class.getDeclaredMethod("getContext",
               new Class[0]));
         methods.add(ScriptContext.class.getDeclaredMethod("getBindings",
               new Class[] { Integer.TYPE }));

         scriptEngineClass = Class.forName("javax.script.ScriptEngine");
      } catch (Exception e) {
         // not JDK 6 or later
      }
   }

   public static JavaxScriptBridge getInstance() {
      return instance;
   }

   public boolean supportsJavaxScript() {
      return scriptEngineClass != null;
   }

   public Class getScriptEngineManagerClass() {
      return scriptEngineManagerClass;
   }

   public Class getScriptEngineClass() {
      return scriptEngineClass;
   }

   public Class getScriptContextClass() {
      return scriptContextClass;
   }

   public Class getBindingsClass() {
      return bindingsClass;
   }

   public int getGlobalScope() {
      return globalScope;
   }

   public int getEngineScope() {
      return engineScope;
   }

   public ScriptEngineManager createScriptEngineManager() {
      Object targetInstance = newInstance(scriptEngineManagerClass);
      ProxyInvocationHandler handler = new ProxyInvocationHandler(
            targetInstance);

      return (ScriptEngineManager) createProxy(ScriptEngineManager.class,
            handler);
   }

   protected Object createProxy(Class desiredClass, InvocationHandler handler) {
      return Proxy.newProxyInstance(Thread.currentThread()
            .getContextClassLoader(), new Class[] { desiredClass }, handler);
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

      public ProxyInvocationHandler(Object target) {
         this.target = target;
      }

      protected boolean needsProxy(Method method) {
         return methods.contains(method);
      }

      public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
         final Method originalMethod = target.getClass().getMethod(
               method.getName(), method.getParameterTypes());

         Object result;

         try {
            result = originalMethod.invoke(target, args);
         } catch (InvocationTargetException ite) {
            throw ite.getTargetException();
         }

         if (!needsProxy(method)) {
            return result;
         }

         ProxyInvocationHandler handler = new ProxyInvocationHandler(result);

         return createProxy(method.getReturnType(), handler);
      }
   }
}
