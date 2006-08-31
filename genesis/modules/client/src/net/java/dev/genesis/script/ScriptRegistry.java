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
package net.java.dev.genesis.script;

import net.java.dev.genesis.reflection.ClassesCache;

import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;

import java.util.HashMap;
import java.util.Map;

public class ScriptRegistry {
   public static final String JXPATH = "jxpath";
   public static final String JAVASCRIPT = "javascript";
   public static final String BEANSHELL = "beanshell";
   public static final String EL = "el";

   private static final ScriptRegistry instance = new ScriptRegistry();

   private final Map registry = new HashMap(4);
   private final Map scriptsRegistry = new HashMap(1);

   private String scriptFactoryName = JAVASCRIPT;
   private Map scriptFactoryProperties;
   private ScriptFactory scriptFactory;

   private ScriptRegistry() {
      registry.put(JXPATH,
            "net.java.dev.genesis.script.jxpath.JXPathScriptFactory");
      registry.put(BEANSHELL,
            "net.java.dev.genesis.script.bsf.BSFScriptFactory");
      registry.put(EL, "net.java.dev.genesis.script.el.ELScriptFactory");
      
      registry.put(JAVASCRIPT,
            ScriptUtils.supportsJavaxScript() ?
                  "net.java.dev.genesis.script.mustang.MustangScriptFactory"
                  : "net.java.dev.genesis.script.bsf.BSFScriptFactory");
   }

   public static ScriptRegistry getInstance() {
      return instance;
   }

   public void setScriptFactoryName(String name) {
      this.scriptFactoryName = name;
   }
   
   protected String getScriptFactoryName() {
      return scriptFactoryName;
   }

   public void setScriptFactoryProperties(Map scriptFactoryProperties) {
      this.scriptFactoryProperties = scriptFactoryProperties;
   }

   public void initialize() {
      scriptFactory = initialize(getScriptFactoryName(), scriptFactoryProperties);
   }

   public ScriptFactory initialize(String alias, Map properties) {
      String className = getClassNameFromAlias(alias);

      // doesn't need to use equals method
      if (alias != className) {
         if (properties == null) {
            properties = new HashMap();
         }

         properties.put("lang", alias);
      }

      ScriptFactory factory = (ScriptFactory) newInstance(className);

      if (properties != null && !properties.isEmpty()) {
         copyProperties(factory, properties);
      }

      return factory;
   }

   protected String getClassNameFromAlias(String alias) {
      String className = (String) registry.get(alias);

      if (className != null) {
         return className;
      }

      return alias;
   }

   protected Object newInstance(String className) {
      try {
         return ClassesCache.getClass(className).newInstance();
      } catch (InstantiationException e) {
         throw new RuntimeException(e.getMessage(), e);
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e.getMessage(), e);
      } catch (ClassNotFoundException e) {
         throw new RuntimeException(e.getMessage(), e);
      }
   }

   protected void copyProperties(Object dest, Object source) {
      try {
         PropertyUtils.copyProperties(dest, source);
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e.getMessage(), e);
      } catch (InvocationTargetException e) {
         throw new RuntimeException(e.getMessage(), e);
      } catch (NoSuchMethodException e) {
         throw new RuntimeException(e.getMessage(), e);
      }
   }

   public boolean isInitialized() {
      return scriptFactory != null;
   }

   public Script getScript(String alias) {
      String className = getClassNameFromAlias(alias);
      Script script = (Script) scriptsRegistry.get(className);
      if (script == null) {
         if (isCurrentScriptFactoryNameFor(alias)) {
            if (!isInitialized()) {
               initialize();
            }

            script = scriptFactory.newScript();
         } else {
            ScriptFactory factory = initialize(alias, null);
            script = factory.newScript();
         }

         scriptsRegistry.put(className, script);
      }

      return script;
   }

   public Script getScript() {
      return getScript(getScriptFactoryName());
   }

   public boolean isCurrentScriptFactoryNameFor(String alias) {
      if (getScriptFactoryName().equals(alias)) {
         return true;
      }

      return getScriptFactoryName().equals(registry.get(alias));
   }
}
