/*
 * The Genesis Project
 * Copyright (C) 2006 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.binding;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockBoundDataProvider;
import net.java.dev.genesis.mockobjects.MockBoundField;
import net.java.dev.genesis.mockobjects.MockBoundMember;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.mockobjects.MockViewHandler;
import net.java.dev.genesis.reflection.MethodEntry;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;

public class AbstractBinderTest extends GenesisTestCase {
   private Object root;
   private MockForm form;
   private MockViewHandler handler;

   public AbstractBinderTest() {
      super("Abstract Binder Unit Test");
   }

   protected void setUp() throws Exception {
      root = new Object();
      form = new MockForm();
      handler = new MockViewHandler();
   }

   public void testBind() {
      MockAbstractBinder binder = new MockAbstractBinder(root, form, handler) {
         protected void setupController() throws Exception {
            put("setupController()", Boolean.TRUE);
         }
      };

      binder.bind();

      assertNotNull(binder
            .get("bindActionMetadata(String,FormMetadata,ActionMetadata)"));
      assertNotNull(binder
            .get("bindDataProvider(String,FormMetadata,DataProviderMetadata)"));
      assertNotNull(binder
            .get("bindFieldMetadata(String,FormMetadata,FieldMetadata)"));
      assertNotNull(binder.get("setupController()"));
   }

   public void testBindWithException() {
      MockAbstractBinder binder = new MockAbstractBinder(root, form, handler) {
         protected BoundField bindFieldMetadata(String name,
               FormMetadata formMetadata, FieldMetadata fieldMetadata) {
            throw new IllegalArgumentException();
         }

         public void unbind() {
            put("unbind()", Boolean.TRUE);
         }

         public void handleException(Throwable throwable) {
            put("handleException(Throwable)", throwable);
         }
      };

      binder.bind();

      assertNotNull(binder.get("unbind()"));
      assertTrue(binder.get("handleException(Throwable)") instanceof IllegalArgumentException);
   }

   public void testSetupController() throws Exception {
      MockAbstractBinder binder = new MockAbstractBinder(root, form, handler);

      // Assert that controller has not been setup
      assertNull(form.getController().get("setup()"));
      assertTrue(!form.getController().isSetup());
      assertNull(form.getController()
            .get("addFormControllerListener(listener)"));
      assertTrue(!form.getController().getFormControllerListeners().contains(
            binder));

      // controller.setup() (1st time)
      binder.setupController();
      assertEquals(Boolean.TRUE, form.getController().get("setup()"));
      assertTrue(form.getController().isSetup());
      // Assert binder was added to the controller's listeners
      assertEquals(binder, form.getController().get(
            "addFormControllerListener(listener)"));
      assertTrue(form.getController().getFormControllerListeners().contains(
            binder));

      // controller.setup() (2nd time)
      // Remove binder from controller's listeners
      form.getController().getFormControllerListeners().remove(binder);
      binder.setupController();
      assertTrue(form.getController().isSetup());
      // Assert binder was added to the controller's listeners
      assertTrue(form.getController().getFormControllerListeners().contains(
            binder));
      // Assert that controller.fireAllEvents(binder) was called
      assertSame(binder, form.getController().get("fireAllEvents(listener)"));
   }

   public void testDataProvidedListChanged() throws Exception {
      DataProviderMetadata dataMeta = (DataProviderMetadata) form
            .getFormMetadata().getDataProviderMetadatas().get(
                  new MethodEntry(form.getMethod("someDataProvider")));

      MockAbstractBinder binder = new MockAbstractBinder(root, form, handler) {
         protected void resetSelectedFields(DataProviderMetadata meta)
               throws Exception {
            put("resetSelectedFields(DataProviderMetadata)", meta);
         }
      };

      binder.bind();
      MockBoundDataProvider bound = (MockBoundDataProvider) binder
            .getBoundDataProvider(dataMeta.getWidgetName());

      final List list = Collections.singletonList("someValue");

      binder.dataProvidedListChanged(dataMeta, list);

      assertSame(list, bound.get("updateList(List)"));

      form.getController().setResetOnDataProviderChange(false);
      binder.clear();
      bound.clear();

      binder.dataProvidedListChanged(dataMeta, list);

      assertSame(list, bound.get("updateList(List)"));
      assertNull(binder.get("resetSelectedFields(DataProviderMetadata)"));
   }

   public void testDataProvidedIndexesChanged() throws Exception {
      DataProviderMetadata dataMeta = (DataProviderMetadata) form
            .getFormMetadata().getDataProviderMetadatas().get(
                  new MethodEntry(form.getMethod("someDataProvider")));

      MockAbstractBinder binder = new MockAbstractBinder(root, form, handler);

      binder.bind();

      MockBoundDataProvider bound = (MockBoundDataProvider) binder
            .getBoundDataProvider(dataMeta.getWidgetName());

      final int[] indexes = new int[] { 2, 5, 10 };

      binder.dataProvidedIndexesChanged(dataMeta, indexes);

      assertSame(indexes, bound.get("updateIndexes(int[])"));
   }

   public void testValuesChanged() throws Exception {
      final MockAbstractBinder binder = new MockAbstractBinder(root, form, handler);

      binder.bind();

      final MockBoundField bound = (MockBoundField) binder
            .getBoundField("stringField");
      final Map map = Collections.singletonMap("stringField", "someValue");

      binder.valuesChanged(map);

      assertSame(bound.get("setValue(Object)"), "someValue");
   }

   public void testEnabledConditionsChanged() throws Exception {
      final MockAbstractBinder binder = new MockAbstractBinder(root, form, handler);

      binder.bind();

      final MockBoundMember bound = (MockBoundMember) binder
            .getBoundMember("stringField");
      final Map map = Collections.singletonMap("stringField", Boolean.FALSE);

      binder.enabledConditionsChanged(map);
      assertEquals(bound.get("setEnabled(boolean)"), Boolean.FALSE);

      bound.clear();

      binder.visibleConditionsChanged(map);
      assertEquals(bound.get("setVisible(boolean)"), Boolean.FALSE);
   }

   public void testUnbind() throws Exception {
      final MockAbstractBinder binder = new MockAbstractBinder(root, form, handler);

      binder.bind();

      assertNotNull(binder.getBoundField("stringField"));
      assertNotNull(binder.getBoundAction("someAction"));
      assertNotNull(binder.getBoundDataProvider("dataProviderField"));

      binder.unbind();

      assertNull(binder.getBoundField("stringField"));
      assertNull(binder.getBoundAction("someAction"));
      assertNull(binder.getBoundDataProvider("dataProviderField"));
   }

   public void testRefresh() throws Exception {
      final MockAbstractBinder binder = new MockAbstractBinder(root, form, handler);

      binder.bind();
      binder.refresh();

      assertNotNull(form.getController().get("update()"));
   }

   public void testRefreshWithException() throws Exception {
      final MockAbstractBinder binder = new MockAbstractBinder(root, form, handler);
      final Exception ex = new IllegalArgumentException();
      form.getController().putException("update()", ex);

      binder.bind();
      binder.refresh();

      assertNull(form.getController().get("update()"));
      assertSame(ex, binder.get("handleException(Throwable)"));
   }

   public void testInvokeAction() throws Exception {
      final MockAbstractBinder binder = new MockAbstractBinder(root, form, handler);

      binder.bind();
      binder.invokeAction("someAction");

      assertSame("someAction", form.getController().get(
            "invokeAction(actionName)"));

      binder.clear();
      form.getController().clear();

      final Exception ex = new IllegalArgumentException();
      form.getController().putException("invokeAction(String,Map)", ex);
      binder.invokeAction("someAction");

      assertNull(form.getController().get("invokeAction(actionName)"));
      assertSame(ex, binder.get("handleException(Throwable)"));
   }

   public void testGetIndexesFromController() {
      final MockAbstractBinder binder = new MockAbstractBinder(root, form, handler);

      int[] indexes = new int[] { 1, 2, 3 };
      int[] expected = indexes;
      int[] result = binder.getIndexesFromController(indexes, false);
      assertTrue(Arrays.equals(expected, result));

      expected = new int[] { 2, 3, 4 };
      result = binder.getIndexesFromController(indexes, true);
      assertTrue(Arrays.equals(expected, result));

      indexes = new int[] { 0 };
      expected = new int[] { 1 };
      result = binder.getIndexesFromController(indexes, true);
      assertTrue(Arrays.equals(expected, result));

      indexes = new int[] {};
      expected = new int[] { -1 };
      result = binder.getIndexesFromController(indexes, true);
      assertTrue(Arrays.equals(expected, result));

      indexes = new int[] { -1 };
      expected = new int[] { -1 };
      result = binder.getIndexesFromController(indexes, true);
      assertTrue(Arrays.equals(expected, result));
   }

   public void testGetIndexesFromUI() {
      final MockAbstractBinder binder = new MockAbstractBinder(root, form, handler);

      int[] indexes = new int[] { 1, 2, 3 };
      int[] expected = indexes;
      int[] result = binder.getIndexesFromUI(indexes, false);
      assertTrue(Arrays.equals(expected, result));

      expected = new int[] { 0, 1, 2 };
      result = binder.getIndexesFromUI(indexes, true);
      assertTrue(Arrays.equals(expected, result));

      indexes = new int[] { 0 };
      expected = new int[] {};
      result = binder.getIndexesFromUI(indexes, true);
      assertTrue(Arrays.equals(expected, result));

      indexes = new int[] {};
      expected = new int[] {};
      result = binder.getIndexesFromUI(indexes, true);
      assertTrue(Arrays.equals(expected, result));

      indexes = new int[] { -1 };
      expected = new int[] {};
      result = binder.getIndexesFromUI(indexes, true);
      assertTrue(Arrays.equals(expected, result));
   }
}
