/*
 * The Genesis Project
 * Copyright (C) 2004-2009  Summa Technologies do Brasil Ltda.
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

import java.util.Map;

import net.java.dev.genesis.script.ScriptRegistry;
import net.java.dev.genesis.ui.metadata.DefaultFormMetadataFactory;
import net.java.dev.genesis.util.GenesisUtils;

import org.codehaus.aspectwerkz.aspect.management.Mixins;

public class FormMetadataFactoryAspect {
   /**
    * @Mixin(pointcut="formMetadataFactoryIntroduction", isTransient=true, deploymentModel="perJVM")
    */
   public static class AspectFormMetadataFactory extends
         DefaultFormMetadataFactory {
      public AspectFormMetadataFactory() throws Exception {
         Map parameters = Mixins.getParameters(getClass(), getClass()
               .getClassLoader());

         String scriptFactoryName = (String) parameters.get("scriptFactory"); // NOI18N
         
         if (scriptFactoryName != null) {
            ScriptRegistry.getInstance().setScriptFactoryName(scriptFactoryName);
         }

         String properties = (String) parameters.get("scriptFactoryProperties"); // NOI18N

         if (properties != null) {
            ScriptRegistry.getInstance().setScriptFactoryProperties(
                  GenesisUtils.getAttributesMap(properties, ",", "=")); // NOI18N
         }

         ScriptRegistry.getInstance().initialize();
      }
   }
}