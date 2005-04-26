/*
 * The Genesis Project
 * Copyright (C) 2004-2005  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.aspect;

import net.java.dev.genesis.ui.metadata.ViewMetadata;
import net.java.dev.genesis.ui.thinlet.metadata.ThinletMetadata;

/**
 * @deprecated Use net.java.dev.genesis.aspect.ViewMetadataFactoryAspect instead. 
 *             This class will be removed in the next major genesis release.
 */
public class ThinletMetadataFactoryAspect {
   /**
    * @deprecated Use net.java.dev.genesis.aspect.ViewMetadataFactoryAspect$AspectViewMetadataFactory 
    *             instead. This class will be removed in the next major genesis 
    *             release.
    * @Introduce thinletMetadataFactoryIntroduction deploymentModel=perJVM
    */
   public static class AspectThinletMetadataFactory extends
         ViewMetadataFactoryAspect.AspectViewMetadataFactory {
      public ThinletMetadata getThinletMetadata(final Class thinletClass) {
         return (ThinletMetadata)getViewMetadata(thinletClass);
      }

      protected ViewMetadata createViewMetadata(Class viewClass) {
         return new ThinletMetadata(viewClass);
      }      
   }
}