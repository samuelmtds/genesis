/*
 * The Genesis Project
 * Copyright (C) 2005 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.mockobjects;

import net.java.dev.genesis.ui.metadata.ViewMetadataFactory;
import net.java.dev.genesis.ui.metadata.ViewMetadata;

public class MockViewHandler implements ViewMetadataFactory {
   private ViewMetadata meta = new ViewMetadata(getClass());

   public ViewMetadata getViewMetadata(Class viewClass) {
      return meta;
   }

   public ViewMetadata getViewMetadata() {
      return getViewMetadata(getClass());
   }

   public void setViewMetadata(ViewMetadata meta) {
      this.meta = meta;
   }
}