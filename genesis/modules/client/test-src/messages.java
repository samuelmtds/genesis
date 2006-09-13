/*
 * The Genesis Project
 * Copyright (C) 2005  Summa Technologies do Brasil Ltda.
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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import net.java.dev.genesis.PropertiesProvider;

/*
 * As scary as it may seem, this class is needed for methods that use a
 * ResourceBundle...
 */
public class messages extends ResourceBundle implements PropertiesProvider {
   private final Map properties = new HashMap();

   public Enumeration getKeys() {
      return Collections.enumeration(properties.keySet());
   }
   
   public Object handleGetObject(String key) {
      Object value = properties.get(key);

      if (value == null) {
         throw new MissingResourceException("Could not be found", 
               getClass().getName(), key);
      }

      return value;
   }

   public Map getProperties() {
      return properties;
   }
}