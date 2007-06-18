/*
 * The Genesis Project
 * Copyright (C) 2005-2007 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.MethodMetadata;
import net.java.dev.genesis.ui.thinlet.BaseThinlet;
import net.java.dev.genesis.ui.thinlet.ThinletBinder;

public class DefaultFormControllerTest extends GenesisTestCase {
   private DefaultFormController controller;
   private MockForm form;
   
   public DefaultFormControllerTest() {
      super("DefaultFormController Unit Test");
   }

   protected void setUp() throws Exception {
      controller = new DefaultFormController();
      form = new MockForm();
      controller.setForm(form);
      controller.setFormMetadata(form.getFormMetadata());
   }

   public void testSetup() throws Throwable {
      final Map map = new HashMap();
      controller = new DefaultFormController() {
         protected void evaluate(boolean firstCall) throws Exception {
            map.put("evaluate(firstCall)", Boolean.valueOf(firstCall));
         }
      };
      controller.setForm(form);
      controller.setFormMetadata(form.getFormMetadata());
      controller.setup();

      // Assert controller is setup
      assertTrue(controller.isSetup());
      // Assert controller was evaluated
      assertEquals(Boolean.TRUE, map.get("evaluate(firstCall)"));
      // Assert Script context was created
      assertNotNull("Script context is null", controller.getScriptContext());
      // Assert FormState was created
      assertNotNull("No FormState", controller.getFormState());
      // Assert VariablesMap and Form's Context are the same object
      assertSame(controller.getScriptContext().getContextMap(), form.getContext());
      // Assert FormState is declared on script context
      assertSame(controller.getFormState(), controller.getScriptContext().getContextMap().get(
            FormController.CURRENT_STATE_KEY));
      // Assert FormMetadata is declared on script context
      assertSame(controller.getFormMetadata(), controller.getScriptContext().getContextMap().get(
            FormController.FORM_METADATA_KEY));

      Exception ex = null;

      try {
         controller.setup();
      } catch (Exception e) {
         ex = e;
      }

      // Assert that an IllegalStateException occured
      assertTrue(ex instanceof IllegalStateException);
   }

   public void testCreateScriptContext() {
      ScriptContext ctx = controller.createScriptContext();

      // Assert script context was created
      assertNotNull(ctx);
   }

   public void testFormControllerListeners() {
      FormControllerListener listener = new ThinletBinder(new BaseThinlet() {}, null, form);
      
      // Assert no listeners
      assertEquals(0, controller.getFormControllerListeners().size());
      
      controller.addFormControllerListener(listener);
      assertTrue(controller.getFormControllerListeners().contains(listener));
      assertEquals(1, controller.getFormControllerListeners().size());
      
      controller.removeFormControllerListener(listener);
      assertTrue(!controller.getFormControllerListeners().contains(listener));
      assertEquals(0, controller.getFormControllerListeners().size());
      
   }

   public void testBeforeActionDataProvider() throws Exception {
      controller.setup();

      final String dataProviderName = "someDataProvider";
      final Boolean called[] = new Boolean[1];

      controller.addFormControllerListener(new FormControllerListener() {
         public void enabledConditionsChanged(Map updatedEnabledConditions) {
         }

         public void visibleConditionsChanged(Map updatedVisibleConditions) {
         }

         public boolean beforeInvokingMethod(MethodMetadata methodMetadata) 
               throws Exception {
            if (dataProviderName.equals(methodMetadata.getName())) {
               called[0] = Boolean.TRUE;
            }

            return true;
         }

         public void afterInvokingMethod(MethodMetadata methodMetadata) 
               throws Exception {
         }

         public void dataProvidedListChanged(DataProviderMetadata metadata, 
               List items) throws Exception {
         }

         public void dataProvidedIndexesChanged(DataProviderMetadata metadata, 
               int[] selectedIndexes) {
         }

         public void valuesChanged(Map updatedValues) throws Exception {
         }
      });

      controller.invokeAction(dataProviderName, Collections.EMPTY_MAP);

      assertEquals("@BeforeAction was not called for @DataProvider", 
            Boolean.TRUE, called[0]);
   }

   public void testValuesChangedIsReentrant() throws Exception {
      controller.setup();

      controller.addFormControllerListener(new FormControllerListener() {
         private boolean firstTime = true;

         public void enabledConditionsChanged(Map updatedEnabledConditions) {
         }

         public void visibleConditionsChanged(Map updatedVisibleConditions) {
         }

         public boolean beforeInvokingMethod(MethodMetadata methodMetadata) throws Exception {
            return true;
         }

         public void afterInvokingMethod(MethodMetadata methodMetadata) throws Exception {
         }

         public void dataProvidedListChanged(DataProviderMetadata metadata, List items) throws Exception {
         }

         public void dataProvidedIndexesChanged(DataProviderMetadata metadata, int[] selectedIndexes) {
         }

         public void valuesChanged(Map updatedValues) throws Exception {
            for (Iterator i = updatedValues.entrySet().iterator(); i.hasNext();) {
               Map.Entry e = (Map.Entry) i.next();

               if (firstTime && "stringField".equals(e.getKey())) {
                  firstTime = false;
                  controller.updateChangedMap(Collections.singletonMap(
                        e.getKey(), e.getValue()), false, Collections.emptyMap());
               }
            }
         }
      });

      form.setStringField("abc");
      form.setObjectField(new Object());
      controller.update();
   }
}