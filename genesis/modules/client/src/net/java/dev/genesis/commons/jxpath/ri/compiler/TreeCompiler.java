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
package net.java.dev.genesis.commons.jxpath.ri.compiler;

import org.apache.commons.jxpath.ri.compiler.Step;

public class TreeCompiler extends
      org.apache.commons.jxpath.ri.compiler.TreeCompiler {

   public Object locationPath(boolean absolute, Object[] steps) {
      return new LocationPath(absolute, toStepArray(steps));
   }

   private Step[] toStepArray(Object[] array) {
      Step stepArray[] = null;
      if (array != null) {
         stepArray = new Step[array.length];
         for (int i = 0; i < stepArray.length; i++) {
            stepArray[i] = (Step) array[i];
         }
      }
      return stepArray;
   }
}