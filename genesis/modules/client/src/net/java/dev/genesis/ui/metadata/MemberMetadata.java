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
package net.java.dev.genesis.ui.metadata;

import org.apache.commons.jxpath.CompiledExpression;

public abstract class MemberMetadata {
   private CompiledExpression enabledCondition;
   private CompiledExpression visibleCondition;

   public CompiledExpression getEnabledCondition() {
      return enabledCondition;
   }

   public void setEnabledCondition(CompiledExpression enabledCondition) {
      this.enabledCondition = enabledCondition;
   }

   public CompiledExpression getVisibleCondition() {
      return visibleCondition;
   }

   public void setVisibleCondition(CompiledExpression visibleCondition) {
      this.visibleCondition = visibleCondition;
   }
}