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
package net.java.dev.genesis.commons.jxpath.functions;

import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import net.java.dev.genesis.equality.EqualityComparator;
import net.java.dev.genesis.equality.EqualityComparatorRegistry;
import net.java.dev.genesis.resolvers.EmptyResolverRegistry;
import net.java.dev.genesis.ui.metadata.FormMetadata;

import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.NodeSet;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ExtensionFunctions {
   private static Log log = LogFactory.getLog(ExtensionFunctions.class);

   private static Pointer getArg(final NodeSet set) {
      for (Iterator iter = set.getPointers().iterator(); iter.hasNext();) {
         return (Pointer) iter.next();
      }
      throw new JXPathException("Incorrect number of arguments");
   }

   private static String getFieldName(final Pointer pointer) {
      final StringTokenizer tokenizer = new StringTokenizer(pointer.toString(),
            "/");
      final StringBuffer buffer = new StringBuffer();
      if (tokenizer.hasMoreTokens()) {
         buffer.append(tokenizer.nextToken());
      }
      while (tokenizer.hasMoreTokens()) {
         buffer.append(".");
         buffer.append(tokenizer.nextToken());
      }
      return buffer.toString();
   }
   
   public static boolean hasChanged(final ExpressionContext context,
         final Object obj) {
      final NodeSet nodeSet = (NodeSet) obj;
      final Map changedMap = (Map) context.getJXPathContext()
            .getVariables().getVariable("genesis:changedMap");
      final Pointer pointer = getArg(nodeSet);
      return changedMap.containsKey(getFieldName(pointer));
   }

   public static boolean isEmpty(final ExpressionContext context,
         final Object obj) {
      if (!(obj instanceof NodeSet)) {
         return EmptyResolverRegistry.getInstance().getDefaultEmptyResolverFor(
               obj.getClass()).isEmpty(obj);
      }
      final NodeSet nodeSet = (NodeSet) obj;
      final FormMetadata formMeta = (FormMetadata) context.getJXPathContext()
            .getVariables().getVariable("genesis:formMetadata");
      final Pointer pointer = getArg(nodeSet);

      return formMeta.getFieldMetadata(getFieldName(pointer))
            .getEmptyResolver().isEmpty(pointer.getValue());
   }

   public static boolean equals(final ExpressionContext context,
         final Object arg1, final Object arg2) {
      final Pointer pointer1 = arg1 instanceof NodeSet
            ? getArg((NodeSet) arg1)
            : null;
      final Pointer pointer2 = arg2 instanceof NodeSet
            ? getArg((NodeSet) arg2)
            : null;
      EqualityComparator comp1 = pointer1 == null
            ? EqualityComparatorRegistry.getInstance()
                  .getDefaultEqualityComparatorFor(arg1.getClass())
            : null;
      EqualityComparator comp2 = pointer2 == null
            ? EqualityComparatorRegistry.getInstance()
                  .getDefaultEqualityComparatorFor(arg2.getClass())
            : null;

      final FormMetadata formMeta = (FormMetadata) context.getJXPathContext()
            .getVariables().getVariable("genesis:formMetadata");

      if (comp1 == comp2) {
         if (comp1 == null) {
            // Both Pointers
            comp1 = formMeta.getFieldMetadata(getFieldName(pointer1))
                  .getEqualityComparator();
            comp2 = formMeta.getFieldMetadata(getFieldName(pointer2))
                  .getEqualityComparator();
            // Different equality comparators
            if (comp1 != comp2) {
               throw new JXPathException(
                     "Arguments have different Equality Comparators.");
            }
            if (log.isDebugEnabled()) {
               log.debug("Evaluation equals for Pointer '"
                     + pointer1.getValue() + "' and Pointer '"
                     + pointer2.getValue() + "'");
            }
            return comp1.equals(pointer1.getValue(), pointer2.getValue());
         } else {
            // arg1 is simple object, arg2 is simple object
            if (log.isDebugEnabled()) {
               log.debug("Evaluation equals for object '" + arg1
                     + "' and object '" + arg2 + "'");
            }
            return comp1.equals(arg1, arg2);
         }
      } else {
         if (comp1 == null) {
            // arg1 is Pointer, arg2 is simple object
            if (pointer1.getValue() == null || !pointer1.getValue().getClass().isAssignableFrom(
                  arg2.getClass())) {
               if (log.isDebugEnabled()) {
                  log.debug("Different classes for '" + pointer1.getValue()
                        + "' and '" + arg2 + "'");
               }
               return false;
            }
            comp1 = formMeta.getFieldMetadata(getFieldName(pointer1))
                  .getEqualityComparator();
            if (log.isDebugEnabled()) {
               log.debug("Evaluation equals for Pointer '"
                     + pointer1.getValue() + "' and object '" + arg2 + "'");
            }
            return comp1.equals(pointer1.getValue(), arg2);
         } else if (comp2 == null) {
            //arg1 is simple object, arg2 is Pointer;
            if (pointer2.getValue() == null
                  || !pointer2.getValue().getClass().isAssignableFrom(
                        arg1.getClass())) {
               if (log.isDebugEnabled()) {
                  log.debug("Different classes for '" + arg1 + "' and '"
                        + pointer2.getValue() + "'");
               }
               return false;
            }
            comp2 = formMeta.getFieldMetadata(getFieldName(pointer2))
                  .getEqualityComparator();
            if (log.isDebugEnabled()) {
               log.debug("Evaluation equals for object '" + arg1
                     + "' and Pointer '" + pointer2.getValue() + "'");
            }
            return comp2.equals(pointer2.getValue(), arg1);
         } else {
            if (log.isDebugEnabled()) {
               log.debug("Different classes for '" + arg1 + "' and '" + arg2
                     + "'");
            }
            // arg1 is simple object, arg2 is simple object
            // but arg1 and arg2 have different class
            return false;
         }
      }
   }

}