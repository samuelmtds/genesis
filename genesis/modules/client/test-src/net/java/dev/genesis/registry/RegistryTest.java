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
package net.java.dev.genesis.registry;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

import net.java.dev.genesis.GenesisTestCase;

public class RegistryTest extends GenesisTestCase {
   
   private Registry registry = new Registry();
   
   public RegistryTest() {
      super("Registry Unit Test");
   }
   
   public void testRegister() throws Exception {
      assertNull(registry.register(Integer.class, "Integer"));
      assertNull(registry.register(Long.class, "Long"));
      assertNull(registry.register(Float.class, "Float"));
   }
   
   public void testGetClass() throws Exception {
      testRegister();
      assertNotNull(registry.get(Integer.class));
      assertNotNull(registry.get(Long.class));
      assertNotNull(registry.get(Float.class));
      assertNull(registry.get(String.class));
   }
   
   public void testDeregister() throws Exception {
      testRegister();
      registry.deregister(Integer.class);
      assertNull(registry.get(Integer.class));
   }
   
   public void testDeregisterAll() throws Exception {
      testRegister();
      registry.deregister();
      assertNull(registry.get(Integer.class));
      assertNull(registry.get(Long.class));
      assertNull(registry.get(Float.class));
   }
   
   public void testSearchHierarchy() throws Exception {
      addArrayListHierarchy();

      //first, it should retrieve the interfaces
      assertEquals(registry.searchHierarchy(ArrayList.class), "List");
      registry.deregister(List.class);
      assertEquals(registry.searchHierarchy(ArrayList.class), "Collection");
      registry.deregister(Collection.class);
      assertEquals(registry.searchHierarchy(ArrayList.class), "RandomAccess");
      registry.deregister(RandomAccess.class);
      assertEquals(registry.searchHierarchy(ArrayList.class), "Cloneable");
      registry.deregister(Cloneable.class);
      assertEquals(registry.searchHierarchy(ArrayList.class), "Serializable");
      registry.deregister(Serializable.class);

      //then, it should retrieve the classes
      assertEquals(registry.searchHierarchy(ArrayList.class), "AbstractList");
      registry.deregister(AbstractList.class);
      assertEquals(registry.searchHierarchy(ArrayList.class), "AbstractCollection");
      registry.deregister(AbstractCollection.class);
      
      //at last, it should retrive the Object class
      assertEquals(registry.searchHierarchy(ArrayList.class), "Object");
      registry.deregister(Object.class);
      
      //and there is nothing more in the registry
      assertNull(registry.searchHierarchy(ArrayList.class));
   }
   
   public void testGetClassAndSuperClass() throws Exception {
      addArrayListHierarchy();

      //there isn't ArrayList in the register
      assertNull(registry.get(ArrayList.class, false));

      //first, it should retrieve the classes
      assertEquals(registry.get(ArrayList.class, true), "AbstractList");
      registry.deregister(AbstractList.class);
      assertEquals(registry.get(ArrayList.class, true), "AbstractCollection");
      registry.deregister(AbstractCollection.class);

      //then, it should retrieve the interfaces
      assertEquals(registry.get(ArrayList.class, true), "List");
      registry.deregister(List.class);
      assertEquals(registry.get(ArrayList.class, true), "Collection");
      registry.deregister(Collection.class);
      assertEquals(registry.get(ArrayList.class, true), "RandomAccess");
      registry.deregister(RandomAccess.class);
      assertEquals(registry.get(ArrayList.class, true), "Cloneable");
      registry.deregister(Cloneable.class);
      assertEquals(registry.get(ArrayList.class, true), "Serializable");
      registry.deregister(Serializable.class);

      //at last, it should retrive the Object class
      assertEquals(registry.get(ArrayList.class, true), "Object");
      registry.deregister(Object.class);

      //and there is nothing more in the registry
      assertNull(registry.get(ArrayList.class, true));
   }
   
   public void testGetObject() throws Exception {
      addArrayListHierarchy();

      ArrayList instance = new ArrayList();

      //first, it should retrieve the classes
      assertEquals(registry.get(instance), "AbstractList");
      registry.deregister(AbstractList.class);
      assertEquals(registry.get(instance), "AbstractCollection");
      registry.deregister(AbstractCollection.class);

      //then, it should retrieve the interfaces
      assertEquals(registry.get(instance), "List");
      registry.deregister(List.class);
      assertEquals(registry.get(instance), "Collection");
      registry.deregister(Collection.class);
      assertEquals(registry.get(instance), "RandomAccess");
      registry.deregister(RandomAccess.class);
      assertEquals(registry.get(instance), "Cloneable");
      registry.deregister(Cloneable.class);
      assertEquals(registry.get(instance), "Serializable");
      registry.deregister(Serializable.class);

      //at last, it should retrive the Object class
      assertEquals(registry.get(instance), "Object");
      registry.deregister(Object.class);

      //and there is nothing more in the registry
      assertNull(registry.get(instance));
   }
   
   private void addArrayListHierarchy() {
      //classes
      assertNull(registry.register(AbstractCollection.class,
            "AbstractCollection"));
      assertNull(registry.register(AbstractList.class, "AbstractList"));
      assertNull(registry.register(Object.class, "Object"));

      //interfaces
      assertNull(registry.register(Collection.class, "Collection"));
      assertNull(registry.register(List.class, "List"));
      assertNull(registry.register(Cloneable.class, "Cloneable"));
      assertNull(registry.register(RandomAccess.class, "RandomAccess"));
      assertNull(registry.register(Serializable.class, "Serializable"));
   }
}