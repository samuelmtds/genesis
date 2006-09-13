/*
 * The Genesis Project
 * Copyright (C) 2005-2006 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.thinlet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.mockobjects.MockViewHandler;
import net.java.dev.genesis.ui.binding.PropertyMisconfigurationException;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.MethodMetadata;
import net.java.dev.genesis.ui.metadata.ViewMetadata;

public class ThinletBinderTest extends GenesisTestCase {
   private MockForm form;
   private ThinletBinder binder;
   private BaseThinlet thinlet;
   private MockViewHandler viewHandler;
   
   public ThinletBinderTest() {
      super("ThinletBinder Unit Test");
   }
   
   protected void setUp() throws Exception {
      form = new MockForm();
      viewHandler = new MockViewHandler();
      thinlet = new BaseThinlet() {};
      binder = new ThinletBinder(thinlet, thinlet.getDesktop(), form, viewHandler);
   }

   private Method getMethod(String name) throws Exception {
      return getClass().getMethod(name, EMPTY_CLASS_ARRAY);
   }

   public void testBind() throws Throwable {
      final Map map = new HashMap();
      
      binder = new ThinletBinder(thinlet, thinlet.getDesktop(), form) {
         protected void bindFieldMetadatas(final FormMetadata formMetadata) {
            map.put("bindFieldMetadatas(formMetadata)", formMetadata);
         }

         protected void bindActionMetadatas(final FormMetadata formMetadata) {
            map.put("bindActionMetadatas(formMetadata)", formMetadata);
         }

         protected void bindDataProviders(final Collection dataProviders) {
            map.put("bindDataProviders(dataProviders)", dataProviders);
         }

         protected void setupController() throws Exception {
            map.put("setupController()", Boolean.TRUE);
         }
      };

      binder.bind();
      
      // Assert FormMetadata used on bindings and form.getFormMetadata() are the same
      assertSame("FormMetadata used on bindFieldMetadatas(formMetadata) is wrong.",
            form.getFormMetadata(), map.get("bindFieldMetadatas(formMetadata)"));
      assertSame("FormMetadata used on bindActionMetadatas(formMetadata) is wrong.",
            form.getFormMetadata(), map.get("bindActionMetadatas(formMetadata)"));
      
      // Assert dataProvider values are equals
      assertEquals("DataProvider used on bindDataProviders(dataProviders) is wrong.",
            new ArrayList(form.getFormMetadata().getDataProviderMetadatas().values()),
            map.get("bindDataProviders(dataProviders)"));
      
      // Assert that setupController() was called
      assertEquals("binder.setupController() was not called.",
            Boolean.TRUE, map.get("setupController()"));
   }
   
   public void testSetupController() throws Exception {
      
      // Assert that controller has not been setup
      assertNull(form.getController().get("setup()"));
      assertTrue(!form.getController().isSetup());
      assertNull(form.getController().get("addFormControllerListener(listener)"));
      assertTrue(!form.getController().getFormControllerListeners().contains(binder));

      // controller.setup() (1st time)
      binder.setupController();
      assertEquals(Boolean.TRUE, form.getController().get("setup()"));
      assertTrue(form.getController().isSetup());
      // Assert binder was added to the controller's listeners
      assertEquals(binder, form.getController().get("addFormControllerListener(listener)"));
      assertTrue(form.getController().getFormControllerListeners().contains(binder));
      
      // controller.setup() (2nd time)
      // Remove binder from controller's listeners
      form.getController().getFormControllerListeners().remove(binder);
      binder.setupController();
      assertTrue(form.getController().isSetup());
      // Assert binder was added to the controller's listeners
      assertTrue(form.getController().getFormControllerListeners().contains(binder));
      // Assert that controller.fireAllEvents(binder) was called
      assertSame(binder, form.getController().get("fireAllEvents(listener)"));
   }

   public void testSetValue() throws Exception {
      // Unselected checkbox with no group: Value must be false
      binder.setValue(ThinletUtils.newCheckbox(), "checkbox");
      assertValue("checkbox", "false");

      // Selected checkbox with no group: Value must be true
      binder.setValue(ThinletUtils
            .setSelected(ThinletUtils.newCheckbox(), true), "checkbox");
      assertValue("checkbox", "true");

      // Unselected checkbox with group: Value cannot change.
      form.getController().put("checkbox", "unchanged_value");
      binder.setValue(ThinletUtils
            .setGroup(ThinletUtils.newCheckbox(), "group"), "checkbox");
      assertValue("checkbox", "unchanged_value");

      // Selected checkbox with group: Value must be the checkbox name
      binder.setValue(ThinletUtils.setGroup(ThinletUtils.setSelected(
            ThinletUtils.newCheckbox(), true), "group"), "checkbox");
      assertValue("checkbox", "checkbox");

      // Unselected togglebutton: Value must be 'false'
      binder.setValue(ThinletUtils.newToggleButton(), "togglebutton");
      assertValue("togglebutton", "false");

      // Selected togglebutton: Value must be 'true'
      binder.setValue(ThinletUtils.setSelected(ThinletUtils.newToggleButton(),
            true), "togglebutton");
      assertValue("togglebutton", "true");

      // Unselected togglebutton with group: Value cannot change
      form.getController().put("togglebutton", "unchanged_value");
      binder.setValue(ThinletUtils.setGroup(ThinletUtils.newToggleButton(),
            "group"), "togglebutton");
      assertValue("togglebutton", "unchanged_value");

      // Selected togglebutton with group: Value must be the togglebutton name
      binder.setValue(ThinletUtils.setGroup(ThinletUtils.setSelected(
            ThinletUtils.newToggleButton(), true), "group"), "togglebutton");
      assertValue("togglebutton", "togglebutton");

      // Combobox with no choice selected: Value must be null
      binder.setValue(ThinletUtils.newCombobox(), "combobox");
      assertValue("combobox", null);

      // Combobox with first choice selected: Value must be the first choice name 'choice_0'
      binder.setValue(ThinletUtils.setSelected(ThinletUtils.newCombobox(), 0),
            "combobox");
      assertValue("combobox", "choice_0");

      // Combobox with third choice selected: Value must be the third choice name 'choice_2'
      binder.setValue(ThinletUtils.setSelected(ThinletUtils.newCombobox(), 2),
            "combobox");
      assertValue("combobox", "choice_2");

      // List with no item selected: Value must be null
      binder.setValue(ThinletUtils.newList(), "list");
      assertValue("list", null);

      // List with first item selected: Value must be the first item name 'item_0'
      binder.setValue(ThinletUtils.setSelectedIndexes(ThinletUtils.newList(),
            new int[] { 0 }), "list");
      assertValue("list", "item_0");

      // List with third item selected: Value must be the third item name 'item_0'
      binder.setValue(ThinletUtils.setSelectedIndexes(ThinletUtils.newList(),
            new int[] { 2 }), "list");
      assertValue("list", "item_2");

      // New Progress bar: Value must be '0'
      binder.setValue(ThinletUtils.newProgressBar(), "progress_bar");
      assertValue("progress_bar", "0");

      // Progress bar setted to 50: Value must be '50'
      binder.setValue(ThinletUtils.setValue(ThinletUtils.newProgressBar(), 50),
            "progress_bar");
      assertValue("progress_bar", "50");

      // New slider: Value must be '0'
      binder.setValue(ThinletUtils.newSlider(), "slider");
      assertValue("slider", "0");

      // Slider setted to 50: Value must be '50'
      binder.setValue(ThinletUtils.setValue(ThinletUtils.newSlider(), 50),
            "slider");
      assertValue("slider", "50");

      // New Spinbox: Value must be ''
      binder.setValue(ThinletUtils.newSpinBox(), "spinbox");
      assertValue("spinbox", "");

      // Spinbox setted to '1': Value must be '1'
      binder.setValue(ThinletUtils.setText(ThinletUtils.newSpinBox(), "1"),
            "spinbox");
      assertValue("spinbox", "1");

      // New textfield: Value must be ''
      binder.setValue(ThinletUtils.newTextField(), "textfield");
      assertValue("textfield", "");

      // Textfield with text 'sometext': Value must be 'sometext'
      binder.setValue(ThinletUtils.setText(ThinletUtils.newTextField(),
            "sometext"), "textfield");
      assertValue("textfield", "sometext");

      // New passwordfield: Value must be ''
      binder.setValue(ThinletUtils.newPasswordField(), "passwordfield");
      assertValue("passwordfield", "");

      // Passwordfield with text 'sometext': Value must be 'sometext'
      binder.setValue(ThinletUtils.setText(ThinletUtils.newPasswordField(),
            "sometext"), "passwordfield");
      assertValue("passwordfield", "sometext");

      // New textarea: Value must be ''
      binder.setValue(ThinletUtils.newTextArea(), "textarea");
      assertValue("textarea", "");

      // Textarea with text 'sometext': Value must be 'sometext'
      binder.setValue(ThinletUtils.setText(ThinletUtils.newTextArea(),
            "sometext"), "textarea");
      assertValue("textarea", "sometext");
      
      // New label: Value must be null
      binder.setValue(ThinletUtils.newLabel(), "label");
      assertValue("label", null);

      // Label with text 'sometext': Value must be 'sometext'
      binder.setValue(ThinletUtils.setText(ThinletUtils.newLabel(),
            "sometext"), "label");
      assertValue("label", "sometext");
   }

   private void assertValue(String propertyName, Object expected) {
      assertEquals(expected, form.getController().get(propertyName));
   }

   public void testUpdateSelection() throws Exception {

      // New table: Selected indexes must be {}
      binder.updateSelection("table", ThinletUtils.newTable());
      assertSelection(new int[0]);

      // table with first and third rows selected: Selected indexes must be {0, 2}
      binder.updateSelection("table", ThinletUtils.setSelectedIndexes(
            ThinletUtils.newTable(), new int[] { 0, 2 }));
      assertSelection(new int[] { 0, 2 });

      // New combobox: Selected indexes must be < 0
      binder.updateSelection("combobox", ThinletUtils.newCombobox());
      assertSelection(-1);

      // combobox with second choice selected: Selected indexes must be {1}
      binder.updateSelection("combobox", ThinletUtils.setSelected(ThinletUtils
            .newCombobox(), 1));
      assertSelection(1);

      // New combobox with a blank label: Selected indexes must be < 0
      binder.updateSelection("combobox", ThinletUtils.setBlank(ThinletUtils
            .newCombobox()));
      assertSelection(-1);

      // combobox with second choice selected and a blank label: Selected indexes must be {0}
      binder.updateSelection("combobox", ThinletUtils.setBlank(ThinletUtils
            .setSelected(ThinletUtils.newCombobox(), 1)));
      assertSelection(0);

      // combobox with third choice selected and a blank label: Selected indexes must be {1}
      binder.updateSelection("combobox", ThinletUtils.setBlank(ThinletUtils
            .setSelected(ThinletUtils.newCombobox(), 2)));
      assertSelection(1);

      // New list: Selected indexes must be {}
      binder.updateSelection("list", ThinletUtils.newList());
      assertSelection(new int[0]);

      // list with second and third choice selected: Selected indexes must be {1, 2}
      binder.updateSelection("list", ThinletUtils.setSelectedIndexes(
            ThinletUtils.newList(), new int[] { 1, 2 }));
      assertSelection(new int[] { 1, 2 });

      // New list with a blank label: Selected indexes must be {}
      binder.updateSelection("list", ThinletUtils.setBlank(ThinletUtils
            .newList()));
      assertSelection(new int[0]);
      
      // New list with a blank label and first element selected: Selected indexes must be {}
      binder.updateSelection("list", ThinletUtils.setBlank(ThinletUtils
            .setSelectedIndexes(ThinletUtils.newList(), new int[] { 0 })));
      assertSelection(new int[0]);

      // list with first, second and third choice selected and a blank label: Selected indexes must be {0, 1}
      binder.updateSelection("list", ThinletUtils.setBlank(ThinletUtils
                  .setSelectedIndexes(ThinletUtils.newList(), new int[] { 0, 1,
                        2 })));
      assertSelection(new int[] { 0, 1 });

      // list with second and third choice selected and a blank label: Selected indexes must be {0, 1}
      binder.updateSelection("list", ThinletUtils.setBlank(ThinletUtils
            .setSelectedIndexes(ThinletUtils.newList(), new int[] { 1, 2 })));
      assertSelection(new int[] { 0, 1 });
   }

   private void assertSelection(int selection) {
      int[] selected = (int[])form.getController().get("updateSelection(selected)");
      assertEquals(1, selected.length);
      if (selection < 0) {
         assertTrue(selected[0] < 0);
      } else {
         assertEquals(selection, selected[0]);
      }
   }

   private void assertSelection(int[] expected) {
      assertTrue(Arrays.equals(expected, (int[])form.getController().get("updateSelection(selected)")));
   }

   public void testRefresh() throws Exception {
      binder.refresh();

      // Assert that controller update() has been called
      assertEquals(Boolean.TRUE, form.getController().get("update()"));
   }
   
   public void testBindFieldMetadatas() throws Throwable {
      final Map methods = new IdentityHashMap();
      final Map widgetGroup = new HashMap();
      final Map componentsMap = new HashMap();
      // All supported field widgets
      componentsMap.put("checkbox", Collections.singletonList(ThinletUtils.newCheckbox()));
      componentsMap.put("checkbox_group", Collections.singletonList(ThinletUtils.setGroup(ThinletUtils.newCheckbox(), "checkbox_group")));
      componentsMap.put("togglebutton", Collections.singletonList(ThinletUtils.newToggleButton()));
      componentsMap.put("togglebutton_group", Collections.singletonList(ThinletUtils.setGroup(ThinletUtils.newToggleButton(), "togglebutton_group")));
      componentsMap.put("combobox", Collections.singletonList(ThinletUtils.newCombobox()));
      componentsMap.put("list", Collections.singletonList(ThinletUtils.newList()));
      componentsMap.put("spinbox", Collections.singletonList(ThinletUtils.newSpinBox()));
      componentsMap.put("label", Collections.singletonList(ThinletUtils.newLabel()));
      componentsMap.put("passwordfield", Collections.singletonList(ThinletUtils.newPasswordField()));
      componentsMap.put("slider", Collections.singletonList(ThinletUtils.newSlider()));
      componentsMap.put("textarea", Collections.singletonList(ThinletUtils.newTextArea()));
      componentsMap.put("textfield", Collections.singletonList(ThinletUtils.newTextField()));
      
      // Some unsupported field widgets
      componentsMap.put("table", Collections.singletonList(ThinletUtils.newTable()));
      
      // Some supported field widgets, but not writeable fields
      componentsMap.put("checkbox_not_writeable", Collections.singletonList(ThinletUtils.newCheckbox()));
      
      // empty widgets list
      componentsMap.put("no_components", Collections.EMPTY_LIST);
      
      thinlet = new BaseThinlet() {
         public void setMethod(Object component, String key, String value, Object root, Object handler) {
            methods.put(component, "key=" + key + ";value=" + value
                  + ";root=" + root + ";handler=" + handler);
         }
      };
      binder = new ThinletBinder(thinlet, thinlet.getDesktop(), form) {    
         protected List findComponents(final String name) {
            List list = (List)componentsMap.get(name);

            if (list == null) {
               return Collections.EMPTY_LIST;
            }

            return list;
         }
         
         protected void createWidgetGroup(Object component, String name) {
            widgetGroup.put(name, component);
         }     
      };
      
      final FormMetadata formMetadata = form.getFormMetadata();
      formMetadata.addFieldMetadata("checkbox", new FieldMetadata("checkbox", Object.class, true));
      formMetadata.addFieldMetadata("checkbox_group", new FieldMetadata("checkbox_group", Object.class, true));
      formMetadata.addFieldMetadata("togglebutton", new FieldMetadata("togglebutton", Object.class, true));
      formMetadata.addFieldMetadata("togglebutton_group", new FieldMetadata("togglebutton_group", Object.class, true));
      formMetadata.addFieldMetadata("combobox", new FieldMetadata("combobox", Object.class, true));
      formMetadata.addFieldMetadata("list", new FieldMetadata("list", Object.class, true));
      formMetadata.addFieldMetadata("spinbox", new FieldMetadata("spinbox", Object.class, true));
      formMetadata.addFieldMetadata("label", new FieldMetadata("label", Object.class, true));
      formMetadata.addFieldMetadata("slider", new FieldMetadata("slider", Object.class, true));
      formMetadata.addFieldMetadata("textarea", new FieldMetadata("textarea", Object.class, true));
      formMetadata.addFieldMetadata("textfield", new FieldMetadata("textfield", Object.class, true));
      formMetadata.addFieldMetadata("table", new FieldMetadata("table", Object.class, true));
      formMetadata.addFieldMetadata("checkbox_not_writeable", new FieldMetadata("checkbox_not_writeable", Object.class, false));
      formMetadata.addFieldMetadata("no_components", new FieldMetadata("no_components", Object.class, false));

      binder.bindFieldMetadatas(formMetadata);

      // Assert that supported widgets are bound
      assertEquals("checkbox", thinlet.getName(widgetGroup.get("checkbox")));
      assertEquals("checkbox", thinlet.getName(widgetGroup.get("checkbox_group")));
      assertEquals("togglebutton", thinlet.getName(widgetGroup.get("togglebutton")));
      assertEquals("togglebutton", thinlet.getName(widgetGroup.get("togglebutton_group")));
      assertEquals("combobox", thinlet.getName(widgetGroup.get("combobox")));
      assertEquals("list", thinlet.getName(widgetGroup.get("list")));
      assertEquals("spinbox", thinlet.getName(widgetGroup.get("spinbox")));
      assertEquals("label", thinlet.getName(widgetGroup.get("label")));
      assertEquals("slider", thinlet.getName(widgetGroup.get("slider")));
      assertEquals("textarea", thinlet.getName(widgetGroup.get("textarea")));
      assertEquals("textfield", thinlet.getName(widgetGroup.get("textfield")));
      
      // Unsupported widgets are not bound
      assertNull(widgetGroup.get("table"));
      
      // Not writeable fields are not bound, but widgetGroup are created
      assertNotNull(widgetGroup.get("checkbox_not_writeable"));
      
      // Not found widgets are not bound 
      assertNull(widgetGroup.get("no_components"));
      
      // Assert action methods
      assertEquals(getText("action", "setValue(checkbox,checkbox.name)"), methods.get(widgetGroup.get("checkbox")));
      assertEquals(getText("action", "setValue(checkbox,checkbox.group)"), methods.get(widgetGroup.get("checkbox_group")));
      assertEquals(getText("action", "setValue(togglebutton,togglebutton.name)"), methods.get(widgetGroup.get("togglebutton")));
      assertEquals(getText("action", "setValue(togglebutton,togglebutton.group)"), methods.get(widgetGroup.get("togglebutton_group")));
      assertEquals(getText("action", "setValue(combobox,combobox.name)"), methods.get(widgetGroup.get("combobox")));
      assertEquals(getText("action", "setValue(list,list.name)"), methods.get(widgetGroup.get("list")));
      assertEquals(getText("action", "setValue(spinbox,spinbox.name)"), methods.get(widgetGroup.get("spinbox")));
      
      // Assert focuslost methods
      assertEquals(getText("focuslost", "setValue(label,label.name)"), methods.get(widgetGroup.get("label")));
      assertEquals(getText("focuslost", "setValue(slider,slider.name)"), methods.get(widgetGroup.get("slider")));
      assertEquals(getText("focuslost", "setValue(textarea,textarea.name)"), methods.get(widgetGroup.get("textarea")));
      assertEquals(getText("focuslost", "setValue(textfield,textfield.name)"), methods.get(widgetGroup.get("textfield")));
   }
   
   private String getText(String key, String value) {
      return "key=" + key + ";value=" + value + ";root="
            + thinlet.getDesktop() + ";handler=" + binder;
   }
   
   public void testFindComponents() {
      Object checkbox = ThinletUtils.newCheckbox();
      Object togglebutton = ThinletUtils.newToggleButton();
      Object combobox = ThinletUtils.newCombobox();
      Object list = ThinletUtils.newList();
      Object textfield = ThinletUtils.newTextField();
      
      thinlet.add(thinlet.getDesktop(), checkbox);
      thinlet.add(thinlet.getDesktop(), togglebutton);
      thinlet.add(thinlet.getDesktop(), combobox);
      thinlet.add(thinlet.getDesktop(), list);
      thinlet.add(thinlet.getDesktop(), textfield);

      // Simple Components
      assertEquals(Collections.singletonList(checkbox), binder.findComponents("checkbox"));
      assertEquals(Collections.singletonList(togglebutton), binder.findComponents("togglebutton"));
      assertEquals(Collections.singletonList(combobox), binder.findComponents("combobox"));
      assertEquals(Collections.singletonList(list), binder.findComponents("list"));
      assertEquals(Collections.singletonList(textfield), binder.findComponents("textfield"));
      
      // Group components
      ThinletUtils.setGroup(checkbox, "group_0");
      assertEquals(Collections.singletonList(checkbox), binder.findComponents("group_0"));
      
      ThinletUtils.setGroup(togglebutton, "group_1");
      assertEquals(Collections.singletonList(togglebutton), binder.findComponents("group_1"));
   }

   public void testFindComponentsInDepth() {
      Object panel1 = ThinletUtils.newPanel();
      thinlet.add(thinlet.getDesktop(), panel1);
      thinlet.add(thinlet.getDesktop(), ThinletUtils.newButton());
      thinlet.add(thinlet.getDesktop(), ThinletUtils.newCheckbox());

      Object checkboxLevel1 = ThinletUtils.newCheckbox();
      ThinletUtils.setGroup(checkboxLevel1, "group_1");
      thinlet.add(panel1, checkboxLevel1);

      Object panel2 = ThinletUtils.newPanel();
      thinlet.add(panel1, panel2);

      Object checkboxLevel2 = ThinletUtils.newCheckbox();
      ThinletUtils.setGroup(checkboxLevel2, "group_2");
      thinlet.add(panel2, checkboxLevel2);

      binder.setComponentSearchDepth(1);

      assertEquals(1, binder.getComponentSearchDepth());

      // This should be found...
      assertEquals(Collections.singletonList(checkboxLevel1), 
            binder.findComponents("group_1"));
      
      // This shouldn't
      assertTrue(binder.findComponents("group_2").isEmpty());
   }
   
   public void testCreateWidgetGroup() {
      Object noGroup = ThinletUtils.newTextField();
      Object widgetGroup = ThinletUtils.putProperty(ThinletUtils.newTextField(), "widgetGroup", "group_0");
      Object enabledGroup = ThinletUtils.putProperty(ThinletUtils.newTextField(), "enabledWidgetGroup", "group_1");
      Object visibleGroup = ThinletUtils.putProperty(ThinletUtils.newTextField(), "visibleWidgetGroup", "group_2");
      final List enabledList = new ArrayList();
      final List visibleList = new ArrayList();
      
      // No widgetGroup, no enabledWidgetGroup, no visibleWidgetGroup
      binder.createWidgetGroup(noGroup, "field_0");
      enabledList.add("field_0");
      visibleList.add("field_0");
      assertEquals(enabledList, binder.getWidgetGroupCollection("field_0", true));
      assertEquals(visibleList, binder.getWidgetGroupCollection("field_0", false));
      
      // Only widgetGroup
      binder.createWidgetGroup(widgetGroup, "field_1");
      enabledList.clear();
      visibleList.clear();
      enabledList.add("field_1");
      visibleList.add("field_1");
      enabledList.add("group_0");
      visibleList.add("group_0");
      assertEquals(enabledList, binder.getWidgetGroupCollection("field_1", true));
      assertEquals(visibleList, binder.getWidgetGroupCollection("field_1", false));
      
      // Only enabledWidgetGroup
      binder.createWidgetGroup(enabledGroup, "field_2");
      enabledList.clear();
      visibleList.clear();
      enabledList.add("field_2");
      visibleList.add("field_2");
      enabledList.add("group_1");
      assertEquals(enabledList, binder.getWidgetGroupCollection("field_2", true));
      assertEquals(visibleList, binder.getWidgetGroupCollection("field_2", false));
      
      // Only visibleWidgetGroup
      binder.createWidgetGroup(visibleGroup, "field_3");
      enabledList.clear();
      visibleList.clear();
      enabledList.add("field_3");
      visibleList.add("field_3");
      visibleList.add("group_2");
      assertEquals(enabledList, binder.getWidgetGroupCollection("field_3", true));
      assertEquals(visibleList, binder.getWidgetGroupCollection("field_3", false));
   }

   // Action or DataProvider method
   public List button() {
      return null;
   }

   // Action or DataProvider method
   public List textfield() {
      return null;
   }

   // Action or DataProvider method
   public List action() {
      return null;
   }

   // Action or DataProvider method
   public List table() {
      return null;
   }

   // Action or DataProvider method
   public List list() {
      return null;
   }

   // Action or DataProvider method
   public List combobox() {
      return null;
   }

   public void testBindActionMetadatas() throws Exception {
      final Map methodsMap = new IdentityHashMap();
      final Map widgetGroupMap = new HashMap();
      // Button (supported widget)
      Object button = ThinletUtils.newButton();
      
      // Textfield (unsupported widget)
      Object textfield = ThinletUtils.newTextField();
      
      thinlet = new BaseThinlet() {
         public void setMethod(Object component, String key, String value, Object root, Object handler) {
            methodsMap.put(component, "key=" + key + ";value=" + value
                  + ";root=" + root + ";handler=" + handler);
         }
      };
      
      thinlet.add(thinlet.getDesktop(), button);
      thinlet.add(thinlet.getDesktop(), textfield);

      binder = new ThinletBinder(thinlet, thinlet.getDesktop(), form) {
         protected void createWidgetGroup(Object component, String name) {
            widgetGroupMap.put(name, component);
         }
      };
      form.getFormMetadata().addMethodMetadata(getMethod("action"), new MethodMetadata(getMethod("action"), true, false));
      form.getFormMetadata().addMethodMetadata(getMethod("textfield"), new MethodMetadata(getMethod("textfield"), true, false));
      form.getFormMetadata().addMethodMetadata(getMethod("button"), new MethodMetadata(getMethod("button"), true, false));
      
      // Action method without widget to bind
      binder.bindActionMetadatas(form.getFormMetadata());
      assertNull(widgetGroupMap.get("action"));
      
      // Action method with unsupported widget to bind
      binder.bindActionMetadatas(form.getFormMetadata());
      assertNull(widgetGroupMap.get("textfield"));
      assertNull(methodsMap.get(textfield));
      
      // Action method with supported widget to bind
      binder.bindActionMetadatas(form.getFormMetadata());
      assertSame(button, widgetGroupMap.get("button"));
      assertEquals("key=action;value=invokeAction(button.name);root="
            + thinlet.getDesktop() + ";handler=" + binder, methodsMap
            .get(button));
   }
   
   public void testBindDataProviders() throws Exception {
      final Map methodsMap = new IdentityHashMap();
      final Map widgetGroupMap = new HashMap();
      // Table (supported widget)
      Object table = ThinletUtils.newTable();
      // List (supported widget)
      Object list = ThinletUtils.newList();
      // Combo (supported widget)
      Object combobox = ThinletUtils.newCombobox();
      
      // Textfield (unsupported widget)
      Object textfield = ThinletUtils.newTextField();
      
      thinlet = new BaseThinlet() {
         public void setMethod(Object component, String key, String value, Object root, Object handler) {
            methodsMap.put(component, "key=" + key + ";value=" + value
                  + ";root=" + root + ";handler=" + handler);
         }
      };
      
      thinlet.add(thinlet.getDesktop(), table);
      thinlet.add(thinlet.getDesktop(), list);
      thinlet.add(thinlet.getDesktop(), combobox);
      thinlet.add(thinlet.getDesktop(), textfield);

      binder = new ThinletBinder(thinlet, thinlet.getDesktop(), form) {
         protected void createWidgetGroup(Object component, String name) {
            widgetGroupMap.put(name, component);
         }
      };
      
      Collection dataProviders = new ArrayList();
      DataProviderMetadata meta = new MethodMetadata(getMethod("action"), false, true).getDataProviderMetadata();
      meta.setWidgetName("action");
      dataProviders.add(meta);
      
      meta = new MethodMetadata(getMethod("table"), false, true).getDataProviderMetadata();
      meta.setWidgetName("table");
      dataProviders.add(meta);
      meta = new MethodMetadata(getMethod("list"), false, true).getDataProviderMetadata();
      meta.setWidgetName("list");
      dataProviders.add(meta);
      meta = new MethodMetadata(getMethod("combobox"), false, true).getDataProviderMetadata();
      meta.setWidgetName("combobox");
      dataProviders.add(meta);
      meta = new MethodMetadata(getMethod("textfield"), false, true).getDataProviderMetadata();
      meta.setWidgetName("textfield");
      dataProviders.add(meta);
      
      binder.bindDataProviders(dataProviders);

      // DataProvider method without widget to bind
      assertNull(widgetGroupMap.get("action"));
      // DataProvider method with unsupported widget to bind      
      assertNull(widgetGroupMap.get("textfield"));
      assertNull(methodsMap.get(textfield));
      // (table) Action method with supported widget to bind
      assertSame(table, widgetGroupMap.get("table"));
      assertEquals("key=action;value=updateSelection(table.name,table);root="
            + thinlet.getDesktop() + ";handler=" + binder, methodsMap
            .get(table));
      // (combobox) Action method with supported widget to bind
      assertSame(combobox, widgetGroupMap.get("combobox"));
      assertEquals("key=action;value=updateSelection(combobox.name,combobox);root="
            + thinlet.getDesktop() + ";handler=" + binder, methodsMap
            .get(combobox));
      // (list) Action method with supported widget to bind
      assertSame(list, widgetGroupMap.get("list"));
      assertEquals("key=action;value=updateSelection(list.name,list);root="
            + thinlet.getDesktop() + ";handler=" + binder, methodsMap
            .get(list));
   }
   
   public void testInvokeAction() throws Exception {
      final Map props = new HashMap();
      props.put("populateCount", new Integer(0));
      
      thinlet = new BaseThinlet() {         
         protected void populate(Object bean, Object root, final Map properties, 
               final boolean changeBean) throws IllegalAccessException, 
               InvocationTargetException, NoSuchMethodException {
            
            // Assert bean is null
            assertNull(bean);
            // Assert that root and desktop are the same object
            assertSame(getDesktop(), root);
            // Assert changeBean is false
            assertFalse(changeBean);
            // Assert that populate properties and that used on stringProperties are the same
            assertSame(props, properties);
            
            // Count how many times populate(...) was called
            increaseCount();
         }
         
         private void increaseCount() {
            Integer i = (Integer)props.get("populateCount");
            props.put("populateCount", new Integer(i.intValue() + 1));
         }
      };
      
      binder = new ThinletBinder(thinlet, thinlet.getDesktop(), form) {
         private Map methodsMeta = new HashMap();
         {
            // ActionMetadata with validate before
            MethodMetadata meta = new MethodMetadata(getMethod("getClass"), true, false);
            meta.getActionMetadata().setValidateBefore(true);
            methodsMeta.put("validate_before", meta);
            
            // ActionMetadata with no validate before
            methodsMeta.put("no_validate_before", new MethodMetadata(getMethod("toString"), true, false));
            
            // MethodMetadata with no ActionMetadata
            methodsMeta.put("no_action_metadata", new MethodMetadata(getMethod("wait"), false, false));
         }
         
         protected MethodMetadata getMethodMetadata(String name) {
            return (MethodMetadata)methodsMeta.get(name);
         }
         
         protected Map getStringProperties() throws Exception {
            return props;
         }
      };
      
      // Invoking action null
      binder.invokeAction(null);
      // Assert thinlet.populate(...) has not been called.
      assertEquals(new Integer(0), props.get("populateCount"));
      // Assert controller.invokeAction has been called with null values
      assertNull(form.getController().get("invokeAction(actionName)"));
      assertNull(form.getController().get("invokeAction(stringProperties)"));
      
      // Invoking action 'no_validate_before'
      binder.invokeAction("no_validate_before");
      // Assert thinlet.populate(...) has not been called.
      assertEquals(new Integer(0), props.get("populateCount"));
      // Assert controller.invokeAction has been called with correct values
      assertEquals("no_validate_before", form.getController().get("invokeAction(actionName)"));
      assertNull(form.getController().get("invokeAction(stringProperties)"));
      
      // Invoking action 'no_action_metadata'
      binder.invokeAction("no_action_metadata");
      // Assert thinlet.populate(...) has not been called.
      assertEquals(new Integer(0), props.get("populateCount"));
      // Assert controller.invokeAction has been called with correct values
      assertEquals("no_action_metadata", form.getController().get("invokeAction(actionName)"));
      assertNull(form.getController().get("invokeAction(stringProperties)"));

      // Invoking action 'validate_before'
      binder.invokeAction("validate_before");
      // Assert thinlet.populate(...) has been called.
      assertEquals(new Integer(1), props.get("populateCount"));
      // Assert controller.invokeAction has been called with correct values
      assertEquals("validate_before", form.getController().get("invokeAction(actionName)"));
      assertSame(props, form.getController().get("invokeAction(stringProperties)"));
   }
   
   public void testValuesChanged() throws Throwable {
      final Map map = new IdentityHashMap();
      
      // Some properties
      final Map properties = new HashMap();
      properties.put("field_0", new Object());
      properties.put("field_1", Boolean.TRUE);
      properties.put("field_2", "");
      
      thinlet = new BaseThinlet() {
         protected void displayBean(Object bean, Object root, Map formatters) 
         throws IllegalAccessException, InvocationTargetException, 
                NoSuchMethodException {
            map.put(root, bean);
         }
      };
      binder = new ThinletBinder(thinlet, thinlet.getDesktop(), form);
      
      binder.valuesChanged(properties);
      // Assert thinlet.displayBean was called with correct values
      assertSame(map.get(thinlet.getDesktop()), properties);
   }
   
   public void testBeforeInvokingMethodAndAfterInvokingMethod() throws Throwable {
      final Map beforeMap = new IdentityHashMap();
      final Map afterMap = new IdentityHashMap();
      
      // Some methods
      final String beforeAction = "getClass";
      final String afterAction = "toString";
      
      final ViewMetadata viewMeta = new ViewMetadata(viewHandler.getClass()) {
         public boolean invokeBeforeAction(final Object target,
               final String actionName) throws NoSuchMethodException,
               IllegalAccessException, ClassNotFoundException,
               InvocationTargetException {
            beforeMap.put(target, actionName);
            return true;
         }

         public void invokeAfterAction(final Object target,
               final String actionName) throws NoSuchMethodException,
               IllegalAccessException, ClassNotFoundException,
               InvocationTargetException {
            afterMap.put(target, actionName);
         }
      };
      viewHandler.setViewMetadata(viewMeta);
      binder = new ThinletBinder(thinlet, thinlet.getDesktop(), form, viewHandler);

      binder.beforeInvokingMethod(new MethodMetadata(getMethod(beforeAction), true, false));
      binder.afterInvokingMethod(new MethodMetadata(getMethod(afterAction), true, false));

      // Assert that invokeBeforeAction was correctly called
      assertEquals(beforeAction, beforeMap.get(viewHandler));
      // Assert that invokeAfterAction was correctly called
      assertEquals(afterAction, afterMap.get(viewHandler));
   }

   // Method to be used as a DataProvider
   public List someMethod() {
      return null;
   }

   public void testDataProvidedListChanged() throws Exception {
      final Map populateMap = new IdentityHashMap();
      final Map populateComboListMap = new IdentityHashMap();
      final Map resetMap = new HashMap();
      final List someItems = Collections.singletonList(new Object());
      
      final String methodName = "someMethod";
      final DataProviderMetadata meta = new MethodMetadata(getMethod(methodName), false, true).getDataProviderMetadata();
      
      // Table
      final Object table = ThinletUtils.newTable();
      // Combo and list
      final Object combobox = ThinletUtils.newCombobox();
      final Object list = ThinletUtils.newList();

      // Some unsupported dataprovider widget
      final Object textfield = ThinletUtils.newTextField();
      
      // Widget not found
      final Object list2 = ThinletUtils.newList();

      Exception ex = null;

      thinlet = new BaseThinlet() {
         protected void populateFromCollection(Object component, Collection c,
               Map formatters, Map widgetFactories) throws IllegalAccessException,
               InvocationTargetException, NoSuchMethodException {
            populateMap.put(component, c);
         }

         protected void populateFromCollection(Object component, Collection c,
               String keyProperty, String valueProperty, boolean virtual, 
               boolean blank, String blankLabel, Map formatters, Map widgetFactories)
               throws IllegalAccessException, InvocationTargetException,
               NoSuchMethodException {
            populateMap.put(component, c);
            populateComboListMap.put(component, "key=" + keyProperty
                  + ";value=" + valueProperty + ";blank=" + blank
                  + ";blankLabel=" + blankLabel);
         }
      };
      
      thinlet.add(thinlet.getDesktop(), table);
      thinlet.add(thinlet.getDesktop(), combobox);
      thinlet.add(thinlet.getDesktop(), list);
      thinlet.add(thinlet.getDesktop(), textfield);
      
      binder = new ThinletBinder(thinlet, thinlet.getDesktop(), form) {
         protected void resetSelectedFields(DataProviderMetadata meta) throws Exception {
            resetMap.put(meta.getWidgetName(), Boolean.TRUE);
         }
      };

      // Unsupported widget
      meta.setWidgetName("textfield");
      try {
         binder.dataProvidedListChanged(meta, someItems);
      } catch(Exception e) {
         ex = e; 
      }
      // Assert UnsupportedOperationException occured
      assertTrue(ex instanceof UnsupportedOperationException);
      
      // Widget not found
      meta.setWidgetName("list2");
      binder.dataProvidedListChanged(meta, someItems);
      assertNull(resetMap.get(meta.getWidgetName()));
      assertNull(populateMap.get(list2));
      assertNull(populateComboListMap.get(list2));

      // Table
      meta.setWidgetName("table");
      binder.dataProvidedListChanged(meta, someItems);
      // Assert thinlet.populated was called with correct values
      assertSame(someItems, populateMap.get(table));

      form.getController().setResetOnDataProviderChange(false);
      binder.dataProvidedListChanged(meta, someItems);
      // Assert thinlet.populated was called with correct values
      assertSame(someItems, populateMap.get(table));

      // Combo with bogus blank property
      ex = null;
      meta.setWidgetName("combobox");
      ThinletUtils.putProperty(combobox, "key", "someKey");
      ThinletUtils.putProperty(combobox, "value", "someValue");
      ThinletUtils.putProperty(combobox, "blank", new Object());
      ThinletUtils.putProperty(combobox, "blankLabel", "someBlankLabel");
      try {
         binder.dataProvidedListChanged(meta, someItems);
      } catch (Exception e) {
         ex = e;
      }
      // Assert PropertyMisconfigurationException occured
      assertTrue(ex instanceof PropertyMisconfigurationException);
      
      // Combo
      meta.setWidgetName("combobox");
      ThinletUtils.putProperty(combobox, "key", "someKey");
      ThinletUtils.putProperty(combobox, "value", "someValue");
      ThinletUtils.putProperty(combobox, "blank", Boolean.TRUE);
      ThinletUtils.putProperty(combobox, "blankLabel", "someBlankLabel");
      binder.dataProvidedListChanged(meta, someItems);
      // Assert resetSelectedFields(..) was not called
      assertNull(resetMap.get(meta.getWidgetName()));
      // Assert thinlet.populated was called with correct values
      assertSame(someItems, populateMap.get(combobox));
      // Assert that properties are correct
      assertEquals("key=someKey;value=someValue;blank=" + Boolean.TRUE
            + ";blankLabel=someBlankLabel", populateComboListMap.get(combobox));
      
      // List
      meta.setWidgetName("list");
      ThinletUtils.putProperty(list, "key", "someKey");
      ThinletUtils.putProperty(list, "value", "someValue");
      ThinletUtils.putProperty(list, "blank", Boolean.FALSE);
      ThinletUtils.putProperty(list, "blankLabel", "someBlankLabel");
      binder.dataProvidedListChanged(meta, someItems);
      // Assert resetSelectedFields(..) was not called
      assertNull(resetMap.get(meta.getWidgetName()));
      // Assert thinlet.populated was called with correct values
      assertSame(someItems, populateMap.get(list));
      // Assert that properties are correct
      assertEquals("key=someKey;value=someValue;blank=" + Boolean.FALSE
            + ";blankLabel=someBlankLabel", populateComboListMap.get(list));
   }

   public void testDataProvidedIndexesChanged() throws Throwable {
      final Map map = new HashMap();

      final String methodName = "someMethod";
      final String widgetName = "widgetName";
      final MethodMetadata methodMeta = new MethodMetadata(getMethod(methodName), false, true);
      
      thinlet = new BaseThinlet() {         
         protected void setSelectedIndexes(String name, int[] indexes) {
            map.put(name, indexes);
         }
      };
      binder = new ThinletBinder(thinlet, thinlet.getDesktop(), form);
      
      methodMeta.getDataProviderMetadata().setWidgetName(widgetName);
      binder.dataProvidedIndexesChanged(methodMeta.getDataProviderMetadata(), new int[] {2, 5, 9});

      // Assert thinlet.setSelectedIndexes was called with correct indexes
      assertTrue(Arrays.equals(new int[] {2, 5, 9}, (int[])map.get(methodMeta.getDataProviderMetadata().getWidgetName())));
   }
   
   public void testConditionsChanged() {
      // Some conditions
      final Map updatedConditions = new HashMap();
      updatedConditions.put("condition_true", Boolean.TRUE);
      updatedConditions.put("condition_false", Boolean.FALSE);
      updatedConditions.put("condition_without_widgetgroup", Boolean.TRUE);
      updatedConditions.put("unknown_widget", Boolean.TRUE);

      // Some widgets
      Object textfield = ThinletUtils.newTextField();
      Object textarea = ThinletUtils.newTextArea();
      Object combobox = ThinletUtils.newCombobox();
      Object passwordfield = ThinletUtils.newPasswordField();

      thinlet.add(thinlet.getDesktop(), textfield);
      thinlet.add(thinlet.getDesktop(), textarea);
      thinlet.add(thinlet.getDesktop(), combobox);
      thinlet.add(thinlet.getDesktop(), passwordfield);

      binder = new ThinletBinder(thinlet, thinlet.getDesktop(), form) {
         private Map widgetGroupCollection = new HashMap();
         {
            // textfield and combobox with widgetGroup collection
            widgetGroupCollection.put("condition_true", 
                  Collections.singletonList("textfield"));
            widgetGroupCollection.put("condition_false", Arrays.asList( 
                  new Object [] {"combobox", "textarea"}));
            
            // passwordfield with no widgetGroupCollection
            
            // widgetGroupCollection with unknown widgets
            widgetGroupCollection.put("unknown_widget", 
                  Collections.singletonList("unknown"));
         }
         
         protected Collection getWidgetGroupCollection(String widgetGroupKey, 
               boolean enabled) {
            return (Collection)widgetGroupCollection.get(widgetGroupKey);
         }
      };

      // Enabled Condition
      binder.enabledConditionsChanged(updatedConditions);
      assertTrue("textfield must be enabled.", thinlet.isEnabled(textfield));
      assertTrue("textarea must be not editable.", !thinlet.isEditable(textarea));
      assertTrue("combobox must be disabled.", !thinlet.isEnabled(combobox));
      assertTrue("passwordfield must be enabled.", thinlet.isEnabled(passwordfield));
      
      // Visible Condition
      binder.visibleConditionsChanged(updatedConditions);
      assertTrue("textfield must be visible.", thinlet.isVisible(textfield));
      assertTrue("textarea must be invisible.", !thinlet.isVisible(textarea));
      assertTrue("combobox must be invisible.", !thinlet.isVisible(combobox));
      assertTrue("passwordfield must be visible.", thinlet.isVisible(passwordfield));
   }
}