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
package example.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.java.dev.genesis.commons.beanutils.ConverterRegistry;
import net.java.dev.genesis.text.FormatAdapter;
import net.java.dev.genesis.text.FormatterRegistry;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UIUtils {
   private static final UIUtils instance = new UIUtils();
   private Log log = LogFactory.getLog(UIUtils.class);

   private UIUtils() {
      BeanUtilsBean.setInstance(new BeanUtilsBean(new ConverterRegistry(),
            new PropertyUtilsBean()));
   }

   public static UIUtils getInstance() {
      return instance;
   }

   public void initialize() {
      setupConverters();
      setupFormatters();
      log.info("Initialization started");
   }

   private void setupConverters() {
      Locale ptBR = new Locale("pt", "BR");

      Converter dateConverter = new DateLocaleConverter(null, ptBR,
            "MM/dd/yyyy");
      ConvertUtils.register(dateConverter, Date.class);
   }

   private void setupFormatters() {
      FormatterRegistry.getInstance().register(Date.class,
            new FormatAdapter(new SimpleDateFormat("MM/dd/yyyy"), true));
   }

}