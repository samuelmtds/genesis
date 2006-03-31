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
package net.java.dev.genesis.tests;

import java.util.Map;
import net.java.dev.genesis.ui.metadata.ViewMetadata;
import net.java.dev.genesis.ui.metadata.ViewMetadataFactory;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.jxpath.JXPathContextFactory;

import net.java.dev.genesis.commons.beanutils.ConverterRegistry;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;
import net.java.dev.genesis.util.GenesisUtils;


public class TestCase extends junit.framework.TestCase {

   static {
      BeanUtilsBean.setInstance(new BeanUtilsBean(new ConverterRegistry(),
            new PropertyUtilsBean()));
      System.setProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY,
            "net.java.dev.genesis.commons.jxpath.JXPathContextFactory");
   }

	protected FormMetadata getFormMetadata(final Object form) {
		return ((FormMetadataFactory) form).getFormMetadata(form.getClass());
	}

	protected ViewMetadata getViewMetadata(final Object viewHandler) {
		return ((ViewMetadataFactory) viewHandler).getViewMetadata(viewHandler.getClass());
	}

	public static void assertDescribedMapEquals(Map map, Map map2) {
		GenesisUtils.normalizeMap(map);
		GenesisUtils.normalizeMap(map2);
		junit.framework.TestCase.assertEquals(map, map2);
	}
}