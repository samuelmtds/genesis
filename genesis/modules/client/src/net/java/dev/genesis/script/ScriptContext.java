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
package net.java.dev.genesis.script;

import java.util.Map;

public interface ScriptContext {
   public Object lookup(String name);
   public void declare(String name, Object value);
   public void declare(String name, Object value, Class type);
   public void undeclare(String name);
   public void registerFunctions(String prefix, Object instance);
   public void registerFunctions(String prefix, Class functionClass);
   public Map getContextMap();
   public Object eval(ScriptExpression expr);
   public Object eval(String expr);
}