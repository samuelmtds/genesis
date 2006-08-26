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
   private static final ScriptRegistry instance = new ScriptRegistry();

   private final Map registry = new HashMap();
   private String scriptFactoryName;
   private Map scriptFactoryProperties;
   private ScriptFactory scriptFactory;
   private Script script;

   private ScriptRegistry() {
      registry.put("jxpath",
         "net.java.dev.genesis.script.jxpath.JXPathScriptFactory");
      registry.put("javascript",
         "net.java.dev.genesis.script.bsf.BSFScriptFactory");
      registry.put("beanshell",
         "net.java.dev.genesis.script.bsf.BSFScriptFactory");
      registry.put("el", "net.java.dev.genesis.script.el.ELScriptFactory");
   }

   public static ScriptRegistry getInstance() {
      return instance;
   }

   public void setScriptFactoryName(String name) {
      this.scriptFactoryName = name;
   }

   public void setScriptFactoryProperties(Map scriptFactoryProperties) {
      this.scriptFactoryProperties = scriptFactoryProperties;
   }

   public void initialize() {
      if (scriptFactoryName == null) {
         scriptFactoryName = "javascript";
      }

      String className = (String) registry.get(scriptFactoryName);

      if (className != null) {
         addProperty("lang", scriptFactoryName);
      } else {
         className = scriptFactoryName;
      }

      try {
         scriptFactory = (ScriptFactory)
               ClassesCache.getClass(className).newInstance();

         if ((scriptFactoryProperties != null) &&
                  !scriptFactoryProperties.isEmpty()) {
            PropertyUtils.copyProperties(scriptFactory, scriptFactoryProperties);
         }
      } catch (InstantiationException e) {
         throw new RuntimeException(e.getMessage(), e);
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e.getMessage(), e);
      } catch (ClassNotFoundException e) {
         throw new RuntimeException(e.getMessage(), e);
      } catch (InvocationTargetException e) {
         throw new RuntimeException(e.getMessage(), e);
      } catch (NoSuchMethodException e) {
         throw new RuntimeException(e.getMessage(), e);
      }
   }

   protected void addProperty(String key, Object value) {
      if (scriptFactoryProperties == null) {
         scriptFactoryProperties = new HashMap();
      }

      scriptFactoryProperties.put("lang", scriptFactoryName);
   }

   public boolean isInitialized() {
      return scriptFactory != null;
   }

   public Script getScript() {
      if (!isInitialized()) {
         initialize();
      }

      if (script == null) {
         script = scriptFactory.newScript();
      }

      return script;
   }

   public boolean isCurrentScriptFactoryNameFor(String alias) {
      if (scriptFactoryName.equals(alias)) {
         return true;
      }

      return scriptFactoryName.equals(registry.get(alias));
   }
}
