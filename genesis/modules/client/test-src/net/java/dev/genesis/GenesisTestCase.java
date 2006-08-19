/*
 * The Genesis Project
 * Copyright (C) 2005 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis;

import junit.framework.TestCase;
import net.java.dev.genesis.ui.UIUtils;

public abstract class GenesisTestCase extends TestCase {
   public static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
   public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

   public GenesisTestCase() {
   }

   public GenesisTestCase(String name) {
      super(name);
   }

   public void putInResourceBundle(String key, Object value) {
      ((PropertiesProvider)UIUtils.getInstance().getBundle()).getProperties()
            .put(key, value);
   }
}