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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import net.java.dev.genesis.command.NoopCommand;
import net.java.dev.genesis.commons.beanutils.ConverterRegistry;
import net.java.dev.genesis.script.bsf.javascript.BSFJavaScriptEngine;
import net.java.dev.genesis.text.FormatAdapter;
import net.java.dev.genesis.text.Formatter;
import net.java.dev.genesis.text.FormatterRegistry;
import net.java.dev.genesis.ui.ValidationUtils;

import org.apache.bsf.BSFManager;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.jxpath.JXPathContextFactory;

public class StartupHelper {
   private static Log log = LogFactory.getLog(StartupHelper.class);
   private BeanUtilsBean beanUtilsBean = new BeanUtilsBean(
         new ConverterRegistry(), new PropertyUtilsBean());
   private String jxpathContextFactoryClassName =
         "net.java.dev.genesis.commons.jxpath.JXPathContextFactory";
   private final Map converters = new HashMap();
   private final Map formatters = new HashMap();
   private Locale locale;
   private String dateFormat;
   private boolean runNoopCommand = true;
   private boolean loadValidatorRules = true;

   public StartupHelper() {
      this(Locale.getDefault(), "MM/dd/yyyy");
   }

   public StartupHelper(final Locale locale, final String dateFormat) {
      setLocale(locale);
      setDateFormat(dateFormat);
   }

   public void setLocale(Locale locale) {
      this.locale = locale;
   }

   public Locale getLocale() {
      return locale;
   }

   public void setDateFormat(String dateFormat) {
      this.dateFormat = dateFormat;

      addConverter(Date.class, new DateLocaleConverter(null, locale, dateFormat));
      addFormatter(Date.class, new FormatAdapter(new SimpleDateFormat(dateFormat),
            true));
   }

   public String getDateFormat() {
      return dateFormat;
   }

   public void setBeanUtilsBean(BeanUtilsBean beanUtilsBean) {
      this.beanUtilsBean = beanUtilsBean;
   }

   public BeanUtilsBean getBeanUtilsBean() {
      return beanUtilsBean;
   }

   protected void registerBeanUtilsBean() {
      log.info("Setting BeanUtilsBean instance");
      BeanUtilsBean.setInstance(getBeanUtilsBean());
   }

   public String getJXPathContextFactoryClassName() {
      return jxpathContextFactoryClassName;
   }

   public void setJXPathContextFactoryClassName(String jxpathContextFactoryClassName) {
      this.jxpathContextFactoryClassName = jxpathContextFactoryClassName;
   }

   public void registerBSFScriptLanguage(String language, String engineClassName) {
      BSFManager.registerScriptingEngine(language, engineClassName, null);
   }

   protected void registerJXPathContextFactory() {
      log.info("Registering JXPathContextFactory class name");
      System.setProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY,
            jxpathContextFactoryClassName);
   }

   protected void registerBSFScriptLanguages() {
      log.info("Registering ScriptLanguages");
      registerBSFScriptLanguage("javascript", BSFJavaScriptEngine.class.getName());
      registerBSFScriptLanguage("beanshell", "bsh.util.BeanShellBSFEngine");
   }

   public Converter addConverter(Class clazz, Converter converter) {
      return (Converter)converters.put(clazz, converter);
   }

   public Converter removeConverter(Class clazz) {
      return (Converter)converters.remove(clazz);
   }

   public Converter getConverter(Class clazz) {
      return (Converter)converters.get(clazz);
   }

   public Map getConverters() {
      return new HashMap(converters);
   }

   protected void registerConverters() {
      log.info("Registering converters");

      for (final Iterator i = converters.entrySet().iterator(); i.hasNext(); ) {
         Map.Entry entry = (Map.Entry)i.next();

         ConvertUtils.register((Converter)entry.getValue(), 
               (Class)entry.getKey());
      }
   }

   public Formatter addFormatter(Class clazz, Formatter formatter) {
      return (Formatter)formatters.put(clazz, formatter);
   }

   public Formatter removeFormatter(Class clazz) {
      return (Formatter)formatters.remove(clazz);
   }

   public Formatter getFormatter(Class clazz) {
      return (Formatter)formatters.get(clazz);
   }

   public Map getFormatters() {
      return new HashMap(formatters);
   }

   protected void registerFormatters() {
      log.info("Registering formatters");

      for (final Iterator i = formatters.entrySet().iterator(); i.hasNext(); ) {
         Map.Entry entry = (Map.Entry)i.next();

         FormatterRegistry.getInstance().register((Class)entry.getKey(),
               (Formatter)entry.getValue());
      }
   }

   public boolean isRunNoopCommand() {
      return runNoopCommand;
   }

   public void setRunNoopCommand(boolean runNoopCommand) {
      this.runNoopCommand = runNoopCommand;
   }

   protected void runNoopCommand() {
      if (!isRunNoopCommand()) {
         return;
      }

      final Thread t = new Thread() {
         {
            setDaemon(true);
         }

         public void run() {
            new NoopCommand().remotable();
            log.info("Noop command executed");
         }
      };

      t.start();
   }

   public boolean isLoadValidatorRules() {
      return loadValidatorRules;
   }

   public void setLoadValidatorRules(boolean loadValidatorRules) {
      this.loadValidatorRules = loadValidatorRules;
   }

   protected void loadValidatorRules() {
      if (!isLoadValidatorRules()) {
         return;
      }

      final Thread t = new Thread() {
         {
            setDaemon(true);
         }

         public void run() {
            ValidationUtils.getInstance();
            log.info("Validation rules loaded");
         }
      };

      t.start();
   }

   public void initialize() {
      registerBeanUtilsBean();
      registerBSFScriptLanguages();
      registerJXPathContextFactory();
      registerConverters();
      registerFormatters();
      runNoopCommand();
      loadValidatorRules();

      log.info("Initialization started");
   }
}