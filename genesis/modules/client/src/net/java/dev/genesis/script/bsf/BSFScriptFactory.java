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
package net.java.dev.genesis.script.bsf;

import org.apache.bsf.BSFManager;

import net.java.dev.genesis.script.Script;
import net.java.dev.genesis.script.ScriptFactory;
import net.java.dev.genesis.script.bsf.javascript.BSFJavaScriptEngine;

public class BSFScriptFactory implements ScriptFactory {
   static {
      BSFManager.registerScriptingEngine("javascript", BSFJavaScriptEngine.class.getName(), null);
      BSFManager.registerScriptingEngine("beanshell", "bsh.util.BeanShellBSFEngine", null);
   }

   private String lang;

   public BSFScriptFactory() {
   }

   public BSFScriptFactory(String lang) {
      this.lang = lang;
   }

   public Script newScript() {
      return new BSFScript(lang);
   }

   public void setLang(String lang) {
      this.lang = lang;
   }
}