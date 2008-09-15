/*
 * The Genesis Project
 * Copyright (C) 2008  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.tests.ui;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.java.dev.genesis.tests.TestCase;
import net.java.dev.genesis.ui.ActionInvoker;
import net.java.dev.genesis.ui.controller.DefaultFormController;
import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.controller.FormControllerListener;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.MethodMetadata;
import org.apache.commons.beanutils.BeanUtils;

public class TransactionalFormControllerTest extends TestCase {
   /**
    * @Form
    */
   public static class MainForm {
      private String fieldString;

      public String getFieldString() {
         return fieldString;
      }

      public void setFieldString(String fieldString) throws Exception {
         this.fieldString = fieldString;

         if ("value2".equals(fieldString)) {
            throw new Exception();
         }
      }

      /**
       * @Action
       * @CallWhen genesis.hasChanged('form:fieldString')
       */
      public void action1() throws Exception {
         ActionInvoker.invoke(this, "action2");
      }

      /**
       * @Action
       */
      public void action2() {
      }
   }

   /**
    * @Form
    */
   public static class ComponentForm {
      private String fieldString;

      public String getFieldString() {
         return fieldString;
      }

      public void setFieldString(String fieldString) throws Exception {
         this.fieldString = fieldString;
      }
   }

   class FormControllerAdapter implements FormControllerListener {
      public void enabledConditionsChanged(Map updatedEnabledConditions) {
      }

      public void visibleConditionsChanged(Map updatedVisibleConditions) {
      }

      public boolean beforeInvokingMethod(MethodMetadata methodMetadata) throws
            Exception {
         return true;
      }

      public void afterInvokingMethod(MethodMetadata methodMetadata) throws
            Exception {
      }

      public void dataProvidedListChanged(DataProviderMetadata metadata,
            List items)
            throws Exception {
      }

      public void dataProvidedIndexesChanged(DataProviderMetadata metadata,
            int[] selectedIndexes) {
      }

      public void valuesChanged(Map updatedValues) throws Exception {
      }
   }

   private FormController getController(Object form) throws Exception {
      final FormController formController = new DefaultFormController();
      formController.setFormMetadata(getFormMetadata(form));
      formController.setForm(form);
      formController.setup();
      return formController;
   }

   public void testSimplePopulate() throws Exception {
      final ComponentForm formComponent = new ComponentForm();
      final FormController controllerComponent = getController(formComponent);

      final MainForm formMain = new MainForm();
      final FormController controllerMain = getController(formMain);

      final Map valueNull = Collections.singletonMap("fieldString", null);
      final Map value1 = Collections.singletonMap("fieldString", "value1");
      final Map value2 = Collections.singletonMap("fieldString", "value2");
      final Map value3 = Collections.singletonMap("fieldString", "value3");

      controllerComponent.addFormControllerListener(new FormControllerAdapter() {
         public void valuesChanged(Map updatedValues) throws Exception {
            if (updatedValues.containsKey("fieldString")) {
               controllerMain.populate(updatedValues, null);
            }
         }
      });
      controllerMain.addFormControllerListener(new FormControllerAdapter() {
         public void valuesChanged(Map updatedValues) throws Exception {
            if (updatedValues.containsKey("fieldString")) {
               controllerComponent.populate(updatedValues, null);
            }
         }
      });


      {
         controllerComponent.populate(value1, null);
         controllerComponent.populate(value1, null);
         try {
            controllerComponent.populate(value2, null);
         } catch (Exception exception) {
            //Expected...
         }

         final Map formMainData = BeanUtils.describe(formMain);
         final Map formComponentData = BeanUtils.describe(formComponent);
         assertDescribedMapEquals(value1, formComponentData);
         assertDescribedMapEquals(formComponentData, formMainData);
      }

      {
         controllerComponent.populate(value3, null);
         try {
            controllerComponent.populate(value2, null);
         } catch (Exception exception) {
            //Expected...
         }

         final Map formMainData = BeanUtils.describe(formMain);
         final Map formComponentData = BeanUtils.describe(formComponent);
         assertDescribedMapEquals(value3, formComponentData);
         assertDescribedMapEquals(formComponentData, formMainData);
      }

      {
         controllerComponent.populate(value1, null);
         controllerComponent.populate(value3, null);
         controllerComponent.populate(valueNull, null);
         try {
            controllerComponent.populate(value2, null);
         } catch (Exception exception) {
            //Expected...
         }

         final Map formMainData = BeanUtils.describe(formMain);
         final Map formComponentData = BeanUtils.describe(formComponent);
         assertDescribedMapEquals(valueNull, formComponentData);
         assertDescribedMapEquals(formComponentData, formMainData);
      }
   }

   public void testRecursiveAction() throws Exception {
      final ComponentForm formComponent = new ComponentForm();
      final FormController controllerComponent = getController(formComponent);

      final MainForm formMain = new MainForm();
      final FormController controllerMain = getController(formMain);

      final Map value1 = Collections.singletonMap("fieldString", "value1");

      controllerComponent.addFormControllerListener(new FormControllerAdapter() {
         public void valuesChanged(Map updatedValues) throws Exception {
            if (updatedValues.containsKey("fieldString")) {
               controllerMain.populate(updatedValues, null);
            }
         }
      });
      controllerMain.addFormControllerListener(new FormControllerAdapter() {
         public void valuesChanged(Map updatedValues) throws Exception {
            if (updatedValues.containsKey("fieldString")) {
               controllerComponent.populate(updatedValues, null);
            }
         }
      });

      // Should not throw a StackOverflowError or something like that
      controllerComponent.populate(value1, null);
   }
}
