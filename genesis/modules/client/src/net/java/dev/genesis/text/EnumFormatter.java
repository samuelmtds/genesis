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
package net.java.dev.genesis.text;

import java.util.MissingResourceException;

import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.reusablecomponents.lang.Enum;
import org.apache.commons.logging.LogFactory;

public class EnumFormatter implements Formatter {
   public String format(final Object o) {
      final Enum en = (Enum)o;

      String className = en.getBaseClass().getName();
      className = className.substring(className.lastIndexOf('.') + 1);
      className = className.substring(className.lastIndexOf('$') + 1);

      final String key = className + '.' + en.getName();

      try {
         return UIUtils.getInstance().getBundle().getString(key);
      } catch (MissingResourceException mre) {
         LogFactory.getLog(getClass()).info("Not found: " + key);
         return en.getName();
      }
   }
}