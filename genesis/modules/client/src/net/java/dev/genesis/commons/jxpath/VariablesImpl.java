/*
 * The Genesis Project
 * Copyright (C) 2004  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.commons.jxpath;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jxpath.Variables;

public class VariablesImpl implements Variables {

   private final Map vars = new HashMap();

   public boolean isDeclaredVariable(String varName) {
      return vars.containsKey(varName);
   }

   public Object getVariable(String varName) {
      if (vars.containsKey(varName)) {
         return vars.get(varName);
      }
      throw new IllegalArgumentException("No such variable: '" + varName + "'");
   }

   public void declareVariable(String varName, Object value) {
      vars.put(varName, value);
   }

   public void undeclareVariable(String varName) {
      vars.remove(varName);
   }

   public String toString() {
      return vars.toString();
   }
   
   public Map getVariablesMap(){
      return vars;
   }
}