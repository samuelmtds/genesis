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

import net.java.dev.genesis.commons.jxpath.ri.axes.ChildContext;

import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.axes.AncestorContext;
import org.apache.commons.jxpath.ri.axes.AttributeContext;
import org.apache.commons.jxpath.ri.axes.DescendantContext;
import org.apache.commons.jxpath.ri.axes.NamespaceContext;
import org.apache.commons.jxpath.ri.axes.ParentContext;
import org.apache.commons.jxpath.ri.axes.PrecedingOrFollowingContext;
import org.apache.commons.jxpath.ri.axes.SelfContext;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.Step;

public class LocationPath extends
      org.apache.commons.jxpath.ri.compiler.LocationPath {

   public LocationPath(boolean absolute, Step[] steps) {
      super(absolute, steps);
   }

   protected EvalContext createContextForStep(EvalContext context, int axis,
         NodeTest nodeTest) {
      if (nodeTest instanceof NodeNameTest) {
         QName qname = ((NodeNameTest) nodeTest).getNodeName();
         String prefix = qname.getPrefix();
         if (prefix != null) {
            String namespaceURI = context.getJXPathContext().getNamespaceURI(
                  prefix);
            nodeTest = new NodeNameTest(qname, namespaceURI);
         }
      }

      switch (axis) {
         case Compiler.AXIS_ANCESTOR:
            return new AncestorContext(context, false, nodeTest);
         case Compiler.AXIS_ANCESTOR_OR_SELF:
            return new AncestorContext(context, true, nodeTest);
         case Compiler.AXIS_ATTRIBUTE:
            return new AttributeContext(context, nodeTest);
         case Compiler.AXIS_CHILD:
            return new ChildContext(context, nodeTest, false, false);
         case Compiler.AXIS_DESCENDANT:
            return new DescendantContext(context, false, nodeTest);
         case Compiler.AXIS_DESCENDANT_OR_SELF:
            return new DescendantContext(context, true, nodeTest);
         case Compiler.AXIS_FOLLOWING:
            return new PrecedingOrFollowingContext(context, nodeTest, false);
         case Compiler.AXIS_FOLLOWING_SIBLING:
            return new ChildContext(context, nodeTest, true, false);
         case Compiler.AXIS_NAMESPACE:
            return new NamespaceContext(context, nodeTest);
         case Compiler.AXIS_PARENT:
            return new ParentContext(context, nodeTest);
         case Compiler.AXIS_PRECEDING:
            return new PrecedingOrFollowingContext(context, nodeTest, true);
         case Compiler.AXIS_PRECEDING_SIBLING:
            return new ChildContext(context, nodeTest, true, true);
         case Compiler.AXIS_SELF:
            return new SelfContext(context, nodeTest);
      }
      
      throw new IllegalArgumentException(axis + " is not a valid value for axis");
   }
}