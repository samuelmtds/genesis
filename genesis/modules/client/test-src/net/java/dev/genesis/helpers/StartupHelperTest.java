/*
 * The Genesis Project
 * Copyright (C) 2005-2007 Summa Technologies do Brasil Ltda.
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

import java.util.Locale;
import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.commons.beanutils.converters.BigDecimalConverter;
import net.java.dev.genesis.commons.beanutils.converters.BooleanConverter;
import net.java.dev.genesis.text.BooleanFormatter;
import net.java.dev.genesis.text.DefaultFormatter;
import net.java.dev.genesis.text.FormatterRegistry;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.jxpath.JXPathContextFactory;

public class StartupHelperTest extends GenesisTestCase {
   private StartupHelper startupHelper;

   protected void tearDown() {
      startupHelper = null;
   }
   
   public void testNoArgConstructor() {
      startupHelper = new StartupHelper();

      //validating with the default values.
      verifyBeforeInitialize(Locale.getDefault(), "MM/dd/yyyy");
   }
   
   public void testTwoArgConstructor() {
      //change the locale and dateFormater
      startupHelper = new StartupHelper(Locale.JAPANESE,"dd-MM-yyyy");
      //validating with the changed value.
      verifyBeforeInitialize(Locale.JAPANESE,"dd-MM-yyyy");
   }
   
   private void verifyBeforeInitialize(Locale locale, String dateFormat) {
      //testing if the locale isn't null.
      assertNotNull(startupHelper.getLocale());
      //testing if the locale is the same.
      assertEquals(locale, startupHelper.getLocale());
      //testing if the DateFormater isn't null.
      assertNotNull(startupHelper.getDateFormat());
      //testing if the date formater is the same.
      assertEquals(dateFormat,startupHelper.getDateFormat());
      //testing if the converters collection contains one element.
      assertEquals(1, startupHelper.getConverters().size());
      //testing if the formatters collection contains one element.
      assertEquals(1, startupHelper.getFormatters().size());
      //testing if the JXPathContextFactoryClassName is still the default value.
      assertEquals("net.java.dev.genesis.commons.jxpath.JXPathContextFactory",
            startupHelper.getJXPathContextFactoryClassName());
      //testing if isLoadValidatorRules is still the default value.
      assertTrue(startupHelper.isLoadValidatorRules());
      //testing if isRunNoopCommand is still the default value.
      assertTrue(startupHelper.isRunNoopCommand());
   }
   
   public void testInitializeWithNoChanges() {
      startupHelper = new StartupHelper();

      //test with the default values.
      verifyBeforeInitialize(Locale.getDefault(), "MM/dd/yyyy");
      
      //initializing
      startupHelper.initialize();
      
      //verifying the values
      assertNotNull(startupHelper.getBeanUtilsBean());
      //verifying the property value of FACTORY_NAME_PROPERTY
      assertNotNull(System.getProperties().getProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY));
      assertEquals(System.getProperties().getProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY),
            "net.java.dev.genesis.commons.jxpath.JXPathContextFactory");
      
   }
   public void testInitializeWithChanges() {
      startupHelper = new StartupHelper();

      //test with the default values.
      verifyBeforeInitialize(Locale.getDefault(), "MM/dd/yyyy");
      //changing some properties besides add some converters and formatters.

      //setting the new BeanUtilsBean.
      BeanUtilsBean bean = new BeanUtilsBean(new TestConvertUtilsBean(), 
            new PropertyUtilsBean());
      startupHelper.setBeanUtilsBean(bean);

      //setting the new Locale
      startupHelper.setLocale(Locale.ENGLISH);
      //setting the new DateFormat
      startupHelper.setDateFormat("MM-dd-yyyy");
      //setting the new JXPathContextFactoryClassName
      startupHelper.setJXPathContextFactoryClassName("net.java.dev.genesis.helpers.TestJXPathContextFactory");

      //adding some converters
      BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();
      startupHelper.addConverter(BigDecimalConverter.class, bigDecimalConverter);
      BooleanConverter booleanConverter = new BooleanConverter();
      startupHelper.addConverter(BooleanConverter.class, booleanConverter);

      //adding some formatters
      BooleanFormatter booleanFormatter = new BooleanFormatter();
      startupHelper.addFormatter(BooleanFormatter.class, booleanFormatter);
      DefaultFormatter defaultFormatter = new DefaultFormatter();
      startupHelper.addFormatter(DefaultFormatter.class, defaultFormatter);
      
      //initializing
      startupHelper.initialize();
      
      //verifying the values
      assertNotNull(startupHelper.getBeanUtilsBean());
      //validating if the BeanUtilsBean is the same instance as the changed
      assertSame(bean, BeanUtilsBean.getInstance());

      //verifying the property value of FACTORY_NAME_PROPERTY
      assertNotNull(System.getProperties().getProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY));
      assertEquals(System.getProperties().getProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY),
            "net.java.dev.genesis.helpers.TestJXPathContextFactory");

      //testing the converters
      assertNotNull(startupHelper.getConverter(BigDecimalConverter.class));
      assertSame(bigDecimalConverter, startupHelper.getConverter(
            BigDecimalConverter.class));
      assertNotNull(ConvertUtils.lookup(BigDecimalConverter.class));
      assertSame(bigDecimalConverter, ConvertUtils.lookup(
            BigDecimalConverter.class));
      
      assertNotNull(startupHelper.getConverter(BooleanConverter.class));
      assertSame(booleanConverter, startupHelper.getConverter(
            BooleanConverter.class));
      assertNotNull(ConvertUtils.lookup(BooleanConverter.class));
      assertSame(startupHelper.getConverter(BooleanConverter.class),
            ConvertUtils.lookup(BooleanConverter.class));
      
      //testing the formatters
      assertNotNull(startupHelper.getFormatter(BooleanFormatter.class));
      assertSame(booleanFormatter, startupHelper.getFormatter(
            BooleanFormatter.class));
      assertNotNull(FormatterRegistry.getInstance().get(BooleanFormatter.class));
      assertEquals(startupHelper.getFormatter(BooleanFormatter.class),
            FormatterRegistry.getInstance().get(BooleanFormatter.class));
      
      assertNotNull(startupHelper.getFormatter(DefaultFormatter.class));
      assertSame(defaultFormatter, startupHelper.getFormatter(DefaultFormatter.class));
      assertNotNull(FormatterRegistry.getInstance().get(BooleanFormatter.class));
      assertEquals(startupHelper.getFormatter(DefaultFormatter.class),
            FormatterRegistry.getInstance().get(DefaultFormatter.class));

      //testing the map collections
      assertEquals(3, startupHelper.getConverters().size());
      assertEquals(3, startupHelper.getFormatters().size());
   }

   public void testRemoveConverter() {
      startupHelper = new StartupHelper();

      BooleanConverter booleanConverter = new BooleanConverter();
      startupHelper.addConverter(BooleanConverter.class, booleanConverter);

      assertSame(booleanConverter, startupHelper.getConverter(
            BooleanConverter.class));

      int size = startupHelper.getConverters().size();

      assertSame(booleanConverter, startupHelper.removeConverter(
            BooleanConverter.class));
      assertEquals(size - 1, startupHelper.getConverters().size());
   }

   public void testRemoveFormatter() {
      startupHelper = new StartupHelper();

      BooleanFormatter booleanFormatter = new BooleanFormatter();
      startupHelper.addFormatter(BooleanFormatter.class, booleanFormatter);

      assertSame(booleanFormatter, startupHelper.getFormatter(
            BooleanFormatter.class));

      int size = startupHelper.getFormatters().size();

      assertSame(booleanFormatter, startupHelper.removeFormatter(
            BooleanFormatter.class));
      assertEquals(size - 1, startupHelper.getFormatters().size());
   }
}

class TestConvertUtilsBean extends ConvertUtilsBean {
}

class TestJXPathContextFactory extends org.apache.commons.jxpath.ri.JXPathContextFactoryReferenceImpl {
}