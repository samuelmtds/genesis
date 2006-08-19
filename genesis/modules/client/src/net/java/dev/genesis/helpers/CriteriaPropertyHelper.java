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
package net.java.dev.genesis.helpers;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.java.dev.genesis.command.hibernate.CriteriaResolver;
import net.java.dev.genesis.command.hibernate.HibernateCriteria;
import net.java.dev.genesis.resolvers.EmptyResolver;
import net.java.dev.genesis.resolvers.EmptyResolverRegistry;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;
import net.java.dev.genesis.util.GenesisUtils;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CriteriaPropertyHelper {
   private static final Log log = LogFactory.getLog(CriteriaPropertyHelper.class);

   private CriteriaPropertyHelper() {
   }

   public static void fillCriteria(HibernateCriteria hibCriteria, Object form)
         throws Exception {
      final CriteriaResolver critResolver = (CriteriaResolver) hibCriteria;

      final FormMetadata formMeta = ((FormMetadataFactory)form).getFormMetadata(form.getClass());
      final Map propertiesMap = PropertyUtils.describe(form);
      GenesisUtils.normalizeMap(propertiesMap);

      final PropertyDescriptor[] descriptors = PropertyUtils
            .getPropertyDescriptors(hibCriteria);
      final Collection criteriaPropertyNames = new ArrayList(descriptors.length);

      for (int i = 0; i < descriptors.length; i++) {
         criteriaPropertyNames.add(descriptors[i].getName());
      }

      Map.Entry entry;
      FieldMetadata fieldMeta;
      Object value;
      EmptyResolver emptyResolver;

      propertiesMap.keySet().retainAll(criteriaPropertyNames);

      for (Iterator iter = propertiesMap.entrySet().iterator(); iter.hasNext();) {
         entry = (Map.Entry) iter.next();
         fieldMeta = formMeta.getFieldMetadata(entry.getKey().toString());
         value = entry.getValue();

         emptyResolver = (fieldMeta == null) ? EmptyResolverRegistry
               .getInstance().getDefaultEmptyResolverFor((value == null ?
               Object.class : value.getClass())) : fieldMeta.getEmptyResolver();

         if (emptyResolver.isEmpty(value)) {
            iter.remove();
            continue;
         }
      }

      if (log.isDebugEnabled()) {
         log.debug("Storing properties " + propertiesMap + " for later execution");
      }

      critResolver.setPropertiesMap(propertiesMap);
   }
}