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
package net.java.dev.genesis.aspect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import org.codehaus.aspectwerkz.DeploymentModel;
import org.codehaus.aspectwerkz.aspect.MixinFactory;
import org.codehaus.aspectwerkz.definition.MixinDefinition;
import org.codehaus.aspectwerkz.definition.SystemDefinition;
import org.codehaus.aspectwerkz.definition.SystemDefinitionContainer;
import org.codehaus.aspectwerkz.exception.DefinitionException;
import org.codehaus.aspectwerkz.exception.WrappedRuntimeException;

public class ParameterizedMixinFactory implements MixinFactory {
   private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

   private final Class mixinClass;
   private final DeploymentModel deploymentModel;

   private Constructor constructor;
   private boolean isMixinDefinition;
   private int parameterCount;

   private MixinDefinition mixinDefinition;

   private Object perJVM;
   private final Map perClassMixins = new WeakHashMap();
   private final Map perInstanceMixins = new WeakHashMap();

   public ParameterizedMixinFactory(Class mixinClass, 
         DeploymentModel deploymentModel) {
      this.mixinClass = mixinClass;
      this.deploymentModel = deploymentModel;

      final Constructor[] constructors = mixinClass.getConstructors();

      for (int i = 0; i < constructors.length; i++) {
         Class[] parameterTypes = constructors[i].getParameterTypes();

         if (parameterTypes.length == 2) {
            if (deploymentModel == DeploymentModel.PER_JVM || !parameterTypes[0]
                  .getName().equals(MixinDefinition.class.getName()) ||
                  (deploymentModel == DeploymentModel.PER_CLASS && 
                   !parameterTypes[1].getName().equals(Class.class.getName()))) {
               continue;
            }

            parameterCount = 2;
            constructor = constructors[i];

            break;
         } else if (parameterTypes.length == 1) {
            if (!parameterTypes[0].getName().equals(MixinDefinition.class
                  .getName())) {
               if (deploymentModel == DeploymentModel.PER_JVM || 
                     (deploymentModel == DeploymentModel.PER_CLASS && 
                     !parameterTypes[0].getName().equals(Class.class.getName()))) {
                  continue;
               }
               isMixinDefinition = false;
            } else {
               isMixinDefinition = true;
            }

            parameterCount = 1;
            constructor = constructors[i];
         } else if (parameterTypes.length == 0 && parameterCount != 1) {
            parameterCount = 0;
            constructor = constructors[i];
            isMixinDefinition = false;
         }
      }
   }

   public Object mixinOf() {
      synchronized (this) {
         if (perJVM == null) {
            perJVM = create(null);
         }
      }

      return perJVM;
   }

   public Object mixinOf(Class clazz) {
      return getOrCreateAndPut(perClassMixins, clazz);
   }

   public Object mixinOf(Object instance) {
      return getOrCreateAndPut(perInstanceMixins, instance);
   }

   private Object getOrCreateAndPut(Map map, Object key) {
      Object ret = map.get(key);

      synchronized (map) {
         if (ret == null) {
            map.put(key, ret = create(key));
         }
      }

      return ret;
   }
   
   private Object create(Object parameter) {
      try {
         if (parameterCount == 0) {
            return constructor.newInstance(EMPTY_OBJECT_ARRAY);
         }

         if (parameterCount == 2) {
            return constructor.newInstance(new Object[] {getMixinDefinition(), 
                  parameter});
         }

         return constructor.newInstance(new Object[] {(isMixinDefinition) ? 
               getMixinDefinition() : parameter});
      } catch (InvocationTargetException e) {
         throw new WrappedRuntimeException(e.getTargetException());
      } catch (Exception e) {
         throw new WrappedRuntimeException(e);
      }
   }

   private MixinDefinition getMixinDefinition() {
      synchronized (this) {
         if (mixinDefinition == null) {
            ClassLoader loader = mixinClass.getClassLoader();
            // Feels like a hack...
            boolean found = false;

            for (final Iterator i = SystemDefinitionContainer
                  .getDefinitionsFor(loader).iterator(); i.hasNext(); ) {
               if (findMixinDefinition((SystemDefinition)i.next())) {
                  found = true;
                  break;
               }
            }

            if (!found) {
               throw new DefinitionException("Cannot find MixinDefinition " +
                     "for " + mixinClass.getName());
            }
         }
      }

      return mixinDefinition;
   }

   private boolean findMixinDefinition(SystemDefinition def) {
      for (Iterator iterator = def.getMixinDefinitions().iterator(); 
            iterator.hasNext(); ) {
         mixinDefinition = (MixinDefinition)iterator.next();

         if (mixinDefinition.getMixinImpl().getName().equals(
               mixinClass.getName().replace('/', '.'))) {
            return true;
         }
      }

      return false;
   }
}