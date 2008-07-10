/*
 * The Genesis Project
 * Copyright (C) 2004-2008  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.commons.jxpath.ri.axes;

import net.java.dev.genesis.commons.jxpath.GenesisNodeSet;

import net.java.dev.genesis.util.Bundle;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.NodeSet;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.compiler.NodeTest;

public class ChildContext extends
      org.apache.commons.jxpath.ri.axes.ChildContext {
   private final String path;

   public ChildContext(EvalContext parentContext, NodeTest nodeTest,
         boolean startFromParentLocation, boolean reverse) {
      super(parentContext, nodeTest, startFromParentLocation, reverse);
      if (parentContext instanceof ChildContext) {
         path = ((ChildContext) parentContext).getPath() + "/" // NOI18N
               + nodeTest.toString();
      } else {
         path = nodeTest.toString();
      }
   }

   public NodeSet getNodeSet() {
      if (position != 0) {
         throw new JXPathException(Bundle.getMessage(ChildContext.class,
               "SIMULTANEOUS_OPERATIONS_WARNING")); // NOI18N
      }
      GenesisNodeSet set = new GenesisNodeSet(getPath());
      while (nextSet()) {
         while (nextNode()) {
            set.add((Pointer) getCurrentNodePointer().clone());
         }
      }
      return set;
   }

   public String getPath() {
      return path;
   }

}