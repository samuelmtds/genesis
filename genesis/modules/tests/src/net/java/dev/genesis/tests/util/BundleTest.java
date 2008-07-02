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
package net.java.dev.genesis.tests.util;

import net.java.dev.genesis.util.Bundle;
import java.util.Locale;
import java.util.MissingResourceException;
import net.java.dev.genesis.tests.TestCase;

public class BundleTest extends TestCase {
   public void testGetMessage() {
      final Locale defaultLocale = Locale.getDefault();
      Locale.setDefault(Locale.ENGLISH);
      try {
         Bundle.getMessage(BundleTest.class, "falt");
         fail("A MissingResourceException should be thrown if a invalid " +
               "resource name is informed");
      } catch (MissingResourceException mre) {
      }
      assertEquals("Test", Bundle.getMessage(BundleTest.class, "test"));
      assertEquals("Default", Bundle.getMessage(BundleTest.class, "default"));
      assertEquals("Message from genesis", Bundle.getMessage(BundleTest.class,
            "message_0_1", new Object[] {"Message", "genesis"}));
      assertEquals("Message from genesis", Bundle.getMessage(BundleTest.class,
            "message_0_1", "Message", "genesis"));
      assertEquals("Message from genesis", Bundle.getMessage(BundleTest.class,
            "message_0", "genesis"));


      Locale pt_BR = new Locale("pt", "BR");
      Locale.setDefault(pt_BR);
      assertEquals("Teste", Bundle.getMessage(BundleTest.class, "test"));
      assertEquals("Default", Bundle.getMessage(BundleTest.class, "default"));
      assertEquals("Mensagem do genesis", Bundle.getMessage(BundleTest.class,
            "message_0_1", new Object[] {"Mensagem", "genesis"}));
      assertEquals("Mensagem do genesis", Bundle.getMessage(BundleTest.class,
            "message_0_1", "Mensagem", "genesis"));
      assertEquals("Mensagem do genesis", Bundle.getMessage(BundleTest.class,
            "message_0", "genesis"));

      Locale.setDefault(defaultLocale);
   }

   public void testGetBundle() {
      try {
         Bundle.getBundle("Error", Locale.getDefault(), Thread.currentThread().
               getContextClassLoader());
         fail("A MissingResourceException should be thrown if a invalid " +
               "bundle name is informed");
      } catch (MissingResourceException mre) {
      }

      Bundle.getBundle(BundleTest.class.getPackage().getName() + ".Bundle",
            Locale.getDefault(), Thread.currentThread().getContextClassLoader());

      Bundle.getBundle(BundleTest.class);
   }
}
