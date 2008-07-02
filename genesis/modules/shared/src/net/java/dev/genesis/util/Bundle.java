/*
 * The Genesis Project
 * Copyright (C) 2008 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Bundle {
   public static String getMessage(Class clazz, String key) throws
         MissingResourceException {
      return getBundle(clazz).getString(key);
   }

   public static String getMessage(Class clazz, String key, Object param1)
         throws MissingResourceException {
      return getMessage(clazz, key, new Object[] {param1});
   }

   public static String getMessage(Class clazz, String key, Object param1,
         Object param2) throws MissingResourceException {
      return getMessage(clazz, key, new Object[] {param1, param2});
   }

   public static String getMessage(Class clazz, String key, Object[] arr)
         throws MissingResourceException {
      return java.text.MessageFormat.format(getMessage(clazz, key), arr);
   }

   public static ResourceBundle getBundle(Class clazz) throws
         MissingResourceException {
      String name = findName(clazz);
      return getBundle(name, Locale.getDefault(), clazz.getClassLoader());
   }

   /** Finds package name for given class */
   private static String findName(Class clazz) {
      String pref = clazz.getName();
      int last = pref.lastIndexOf('.');

      if (last >= 0) {
         pref = pref.substring(0, last + 1);

         return pref + "Bundle"; // NOI18N
      } else {
         // base package, search for bundle
         return "Bundle"; // NOI18N
      }
   }

   public static final ResourceBundle getBundle(String baseName, Locale locale,
         ClassLoader loader) throws MissingResourceException {
      ResourceBundle b = ResourceBundle.getBundle(baseName, locale, loader);

      if (b != null) {
         return b;
      } else {
         throw new MissingResourceException("No such bundle " + baseName, // NOI18N
               baseName, null);
      }
   }
}
