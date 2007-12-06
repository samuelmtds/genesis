/*
 * The Genesis Project
 * Copyright (C) 2004-2007  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class UIUtils {
   private static final UIUtils instance = new UIUtils();
   private ResourceBundle bundle;

   private UIUtils() {
   }
   
   public static UIUtils getInstance() {
      return instance;
   }
   
   /**
    * @deprecated
    */
   public String asIdentifier(final String className) {
      return className.replace('.', '_');
   }

   /**
    * @deprecated
    */
   public String asClassName(final String identifier) {
      return identifier.replace('_', '.');
   }

   public String getStackTrace(final Throwable throwable) {
      final StringWriter stringWriter = new StringWriter();
      throwable.printStackTrace(new PrintWriter(stringWriter));

      return stringWriter.toString().replaceAll("\t", "   ");
   }

   public ResourceBundle getBundle() {
      if (bundle == null) {
         try {
            bundle = ResourceBundle.getBundle("messages", Locale.getDefault(), 
                  Thread.currentThread().getContextClassLoader());
         } catch (MissingResourceException e) {
            throw new RuntimeException("The 'messages.properties' file was " +
                  "not found in classpath.");
         }
      }

      return bundle;
   }
}