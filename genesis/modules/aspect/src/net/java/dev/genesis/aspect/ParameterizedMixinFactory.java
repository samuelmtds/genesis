/**
 * 
 */
package net.java.dev.genesis.aspect;

import java.util.Iterator;
import java.util.Map;

import org.codehaus.aspectwerkz.aspect.MixinFactory;
import org.codehaus.aspectwerkz.definition.MixinDefinition;
import org.codehaus.aspectwerkz.definition.SystemDefinition;
import org.codehaus.aspectwerkz.definition.SystemDefinitionContainer;

/**
 * @author allan.jones
 * 
 */
public class ParameterizedMixinFactory implements MixinFactory {

   /*
    * (non-Javadoc)
    * 
    * @see org.codehaus.aspectwerkz.aspect.MixinFactory#mixinOf()
    */
   public Object mixinOf() {
      // TODO Auto-generated method stub
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.codehaus.aspectwerkz.aspect.MixinFactory#mixinOf(java.lang.Class)
    */
   public Object mixinOf(Class arg0) {
      // TODO Auto-generated method stub
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.codehaus.aspectwerkz.aspect.MixinFactory#mixinOf(java.lang.Object)
    */
   public Object mixinOf(Object arg0) {
      // TODO Auto-generated method stub
      return null;
   }

   public static String getParameter(Object mixin, String paramName) {
      Map parameters = null;
      SystemDefinition def = SystemDefinitionContainer.getDefinitionFor(mixin
            .getClass().getClassLoader(), SystemDefinitionContainer
            .getVirtualDefinitionUuid(mixin.getClass().getClassLoader()));
      for (Iterator iterator = def.getMixinDefinitions().iterator(); iterator
            .hasNext();) {
         MixinDefinition mixinDefinition = (MixinDefinition)iterator.next();
         if (mixinDefinition.getMixinImpl().getName().equals(
               mixin.getClass().getName().replace('/', '.'))) {
            parameters = mixinDefinition.getParameters();
            break;
         }
      }
      if (parameters == null) {
         return null;
      }
      return (String)parameters.get(paramName);
   }

}
