/*
 * The Genesis Project
 * Copyright (C) 2007 Summa Technologies do Brasil Ltda.
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.ResourceBundle;
import net.java.dev.genesis.GenesisTestCase;

public class UIUtilsTest extends GenesisTestCase {
   public void testMessagesLoadingWithMultipleClassLoaders()
         throws ClassNotFoundException, NoSuchMethodException,
         IllegalAccessException, IllegalArgumentException,
         InvocationTargetException {
      URLClassLoader cl = new URLClassLoader(new URL[] {
            UIUtils.class.getProtectionDomain().getCodeSource().getLocation()}) {
         public InputStream getResourceAsStream(String name) {
            return name.endsWith("messages.properties") ? null : 
                  super.getResourceAsStream(name);
         }

         protected synchronized Class loadClass(String name, boolean resolve) 
               throws ClassNotFoundException {
            if (name.equals("messages")) {
               throw new ClassNotFoundException("messages");
            }

            if (!name.equals(UIUtils.class.getName())) {
               return super.loadClass(name, resolve);
            }

            Class clazz = findClass(name);
            
            if (resolve) {
               resolveClass(clazz);
            }

            return clazz;
         }

      };
      
      Class clazz = cl.loadClass(UIUtils.class.getName());
      Method getInstance = clazz.getMethod("getInstance", EMPTY_CLASS_ARRAY);
      Object singleton = getInstance.invoke(null, EMPTY_OBJECT_ARRAY);
      Method getBundle = clazz.getMethod("getBundle", EMPTY_CLASS_ARRAY);
      ResourceBundle bundle = (ResourceBundle) getBundle.invoke(singleton, 
            EMPTY_OBJECT_ARRAY);
   }
}
