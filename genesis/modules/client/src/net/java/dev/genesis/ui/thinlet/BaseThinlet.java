/*
 * The Genesis Project
 * Copyright (C) 2004-2005  Summa Technologies do Brasil Ltda.
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

import java.awt.Frame;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.java.dev.genesis.text.Formatter;

import net.java.dev.genesis.text.FormatterRegistry;
import net.java.dev.genesis.ui.Form;
import net.java.dev.genesis.ui.UIException;
import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.ValidationException;
import net.java.dev.genesis.ui.ValidationUtils;
import net.java.dev.reusablecomponents.lang.Enum;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.ValidatorException;

import thinlet.Thinlet;

public abstract class BaseThinlet extends Thinlet {
   public static final String ACTION = "action";
   public static final String ALIGNMENT = "alignment";
   public static final String BUTTON = "button";
   public static final String CHECKBOX = "checkbox";
   public static final String CELL = "cell";
   public static final String CHOICE = "choice";
   public static final String CLOSE = "close";
   public static final String COLUMNS = "columns";
   public static final String COMBOBOX = "combobox";
   public static final String EDITABLE = "editable";
   public static final String ENABLED = "enabled";
   public static final String END = "end";
   public static final String GROUP = "group";
   public static final String HEADER = "header";
   public static final String ITEM = "item";
   public static final String LABEL = "label";
   public static final String LIST = "list";
   public static final String MESSAGE = "message";
   public static final String MNEMONIC = "mnemonic";
   public static final String NAME = "name";
   public static final String PANEL = "panel";
   public static final String PASSWORD_FIELD = "passwordfield";
   public static final String PROGRESS_BAR = "progressbar";
   public static final String RIGHT = "right";
   public static final String ROW = "row";
   public static final String ROWS = "rows";
   public static final String SELECTED = "selected";
   public static final String SLIDER = "slider";
   public static final String SPINBOX = "spinbox";
   public static final String START = "start";
   public static final String TABLE = "table";
   public static final String TEXT = "text";
   public static final String TEXTAREA = "textarea";
   public static final String TEXTFIELD = "textfield";
   public static final String TOGGLE_BUTTON = "togglebutton";
   public static final String TOOLTIP = "tooltip";
   public static final String VALUE = "value";
   public static final String VISIBLE = "visible";

   private final Map formPerClassPerComponent = new IdentityHashMap();
   private final Map binderPerForm = new IdentityHashMap();

   /**
    * @deprecated
    */
   public abstract class ScreenHandler {
      private Object screen;

      protected ScreenHandler() {
      }

      protected ScreenHandler(String fileName) throws ScreenNotFoundException {
         setAllI18n(true);
         setResourceBundle(UIUtils.getInstance().getBundle());
         parseAndSet(fileName, this);
      }

      protected Object getScreen() {
         return screen;
      }

      protected void setScreen(Object screen) {
         this.screen = screen;
      }

      public void show() {
         add(screen);
      }

      public void close() {
         remove(screen);
         repaint();
      }
   }

   public static class ItemType extends Enum {
      public static final ItemType CELL = new ItemType(BaseThinlet.CELL);
      public static final ItemType CHOICE = new ItemType(BaseThinlet.CHOICE);
      public static final ItemType ITEM = new ItemType(BaseThinlet.ITEM);

      private ItemType(String type) {
         super(type);
      }
   }

   protected InputStream getResourceAsStream(String name) {
      return getResourceAsStream(name, 
                                 Thread.currentThread().getContextClassLoader());
   }

   protected InputStream getResourceAsStream(String name, ClassLoader cl) {
      return cl.getResourceAsStream(name);
   }

   /**
    * @deprecated
    */
   protected Object parseAndSet(String fileName, ScreenHandler handler) 
                                                throws ScreenNotFoundException {
      try {
         InputStream is = getResourceAsStream(fileName, 
                                             handler.getClass().getClassLoader());

         if (is == null) {
            throw new ScreenNotFoundException(fileName);
         }
         
         handler.setScreen(parse(is, handler));

         is.close();

         return handler.getScreen();
      } catch (IOException ioe) {
         throw new ScreenNotFoundException(fileName);
      }
   }

   protected Object createChoice(String name, String text) {
      return createItemOfType(name, text, ItemType.CHOICE);
   }

   protected Object createItem(String name, String text) {
      return createItemOfType(name, text, ItemType.ITEM);
   }

   protected Object createItemOfType(String name, String text, ItemType type) {
      final Object item = create(type.getName());
      setName(item, name);
      setText(item, text);
      setTooltip(item, text);
      
      return item;
   }

   protected Object createRow() {
      return create(ROW);
   }

   protected Object createCell(String name, String text) {
      return createItemOfType(name, text, ItemType.CELL);
   }
   
   protected Object createCell(String name, String text, String alignment) {
      final Object cell = createCell(name, text);
      setChoice(cell, ALIGNMENT, alignment);
      
      return cell;
   }

   protected List getAllOfClass(Object component, String className) {
      return getAllOfClass(component, new String[] {className});
   }

   protected List getAllOfClass(Object component, String[] classNames) {
      List all = new ArrayList();
      Object[] components = getItems(component);

      for (int i = 0; i < components.length; i++) {
         for (int j = 0; j < classNames.length; j++) {
            if (getClass(components[i]).equals(classNames[j])) {
               all.add(components[i]);
               break;
            }
         }
      }

      return all;
   }

   protected int getColumns(Object component) {
      return getInteger(component, COLUMNS);
   }

   protected void setColumns(Object component, int columns) {
      setInteger(component, COLUMNS, columns);
   }

   protected boolean isEnabled(Object component) {
      return getBoolean(component, ENABLED);
   }

   protected void setEnabled(Object component, boolean enabled) {
      setBoolean(component, ENABLED, enabled);
   }

   protected boolean isEditable(Object component) {
      return getBoolean(component, EDITABLE);
   }

   protected void setEditable(Object o, boolean editable) {
      setBoolean(o, EDITABLE, editable);
   }

   protected int getEnd(Object component) {
      return getInteger(component, END);
   }

   protected void setEnd(Object component, int end) {
      setInteger(component, END, end);
   }

   protected String getGroup(Object component) {
      return getString(component, GROUP);
   }

   protected void setGroup(Object component, String group) {
      setString(component, GROUP, group);
   }
   
   protected void setMnemonic(Object component, int value) {
      setInteger(component, MNEMONIC, value);
   }

   protected String getName(Object component) {
      return getString(component, NAME);
   }

   protected void setName(Object component, String name) {
      setString(component, NAME, name);
   }

   protected int getRows(Object component) {
      return getInteger(component, ROWS);
   }

   protected void setRows(Object component, int rows) {
      setInteger(component, ROWS, rows);
   }

   protected void setSelected(Object component, boolean selected) {
      setBoolean(component, SELECTED, selected);
   }

   protected boolean isSelected(Object component) {
      return getBoolean(component, SELECTED);
   }

   protected void setSelected(Object component, int index) {
      setInteger(component, SELECTED, index);
   }

   protected int getSelected(Object component) {
      return getInteger(component, SELECTED);
   }

   protected int getStart(Object component) {
      return getInteger(component, START);
   }

   protected void setStart(Object component, int start) {
      setInteger(component, START, start);
   }

   public Object[] getTableColumns(Object table) {
      return getItems(getWidget(table, HEADER));
   }

   protected String getText(Object component) {
      return getString(component, TEXT);
   }

   protected void setText(Object component, String text) {
      setString(component, TEXT, text);
   }

   protected String getTooltip(Object component) {
      return getString(component, TOOLTIP);
   }

   protected void setTooltip(Object component, String tooltip) {
      setString(component, TOOLTIP, tooltip);
   }

   protected void setValue(Object component, String value) {
      setString(component, VALUE, value);
   }

   protected String getValue(Object component) {
      return getString(component, VALUE);
   }

   protected boolean isVisible(Object component) {
      return getBoolean(component, VISIBLE);
   }

   protected void setVisible(Object component, boolean visible) {
      setBoolean(component, VISIBLE, visible);
   }

   protected int getIndexOf(Object parent, Object component) {
      final Object[] components = getItems(parent);

      for (int i = 0; i < components.length; i++) {
         if (components[i] == component) {
            return i;
         }
      }

      return -1;
   }

   protected void displayBean(Object bean) throws IllegalAccessException,
                              InvocationTargetException, NoSuchMethodException {
      displayBean(bean, this);
   }

   protected void displayBean(Object bean, String name) throws IllegalAccessException,
                              InvocationTargetException, NoSuchMethodException {
      displayBean(bean, find(name));
   }

   protected void displayBean(Object bean, Object root) throws IllegalAccessException,
                              InvocationTargetException, NoSuchMethodException {
      displayBean(bean, root, null);
   }

   protected void displayBean(Object bean, Object root, Map formatters) 
         throws IllegalAccessException, InvocationTargetException, 
                NoSuchMethodException {
      final Map properties = bean instanceof Map ? (Map)bean : 
            PropertyUtils.describe(bean);

      if (root instanceof Thinlet) {
         root = ((Thinlet)root).getDesktop();
      }

      String propertyName;
      String propertyValue = null;
      String type;
      Object component;
      Collection groupComponents = null;

      for (final Iterator propertyNames = properties.keySet().iterator();
                                                   propertyNames.hasNext(); ) {
         propertyName = propertyNames.next().toString();
         propertyValue = null;
         component = find(root, propertyName);

         if (component == null) {
            if (groupComponents == null) {
               groupComponents = getAllOfClass(root, CHECKBOX);
            }

            for (final Iterator  i = groupComponents.iterator(); i.hasNext(); ) {
               component = i.next();

               if (propertyName.equals(getGroup(component)) && 
                      getName(component) != null ) {
                  if (propertyValue == null) {
                     propertyValue = getPropertyValue(properties, propertyName, 
                           true, formatters);
                  }

                  if (getName(component).equals(propertyValue)) {
                     setSelected(component, true);
                     break;
                  }
               }
            }

            continue;
         }

         type = getClass(component);

         if (type.equals(COMBOBOX) || type.equals(LIST)) {
            Object selectedComponent = getSelectedItem(component);

            if (selectedComponent != null && type.equals(LIST)) {
               setSelected(selectedComponent, false);
            }

            if (type.equals(COMBOBOX)) {
               setSelected(component, -1);
               setText(component, "");
            }

            String valuePropertyName = (String)getProperty(component, "value");

            if (valuePropertyName != null) {
               Object o = properties.get(propertyName);
               o = o == null ? null : PropertyUtils.getProperty(
                     properties.get(propertyName), valuePropertyName);

               propertyValue = format(formatters, valuePropertyName, o);
            } else {
               propertyValue = getPropertyValue(properties, propertyName, true, 
                     formatters);
            }

            selectedComponent = find(component, propertyValue);

            if (selectedComponent != null) {
               if (type.equals(LIST)) {
                  setSelected(selectedComponent, true);
               } else {
                  setSelected(component, getIndexOf(component, selectedComponent));
               }
            }
         } else if (type.equals(PROGRESS_BAR) || type.equals(SLIDER)) {
            setValue(component, getPropertyValue(properties, propertyName, 
                  false, formatters));
         } else if (type.equals(PANEL)) {
            displayBean(properties.get(propertyName), component);
         } else if (type.equals(CHECKBOX)) {
            setSelected(component, Boolean.TRUE.equals(properties.get(propertyName)));
         } else if (!type.equals(TABLE)){
            setText(component, getPropertyValue(properties, propertyName, 
                  false, formatters));
         }
      }
   }

   protected String getPropertyValue(Map properties, String propertyName,
         boolean enumKey, Map formatters) {
      Object o = properties.get(propertyName);

      if (enumKey && o instanceof Enum) {
        return o.toString();
      }

      return format(formatters, propertyName, o);
   }

   protected String format(Map formatters, String propertyName, Object value) {
      Formatter formatter = null;

      if (formatters != null) {
         formatter = (Formatter)formatters.get(propertyName);
      }

      return (formatter == null) ? FormatterRegistry.getInstance().format(value) :
           formatter.format(value);
   }

   protected void populate(Form bean) throws IllegalAccessException, 
                              InvocationTargetException, NoSuchMethodException {
      populate(bean, this);
   }

   protected void populate(Form bean, Object root) throws IllegalAccessException,
                              InvocationTargetException, NoSuchMethodException {
      populate(bean, root, new HashMap());
   }

   protected void populate(Form bean, Object root, final Map properties) 
         throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      populate(bean, root, properties, true);
   }

   protected void populate(Object bean, Object root, final Map properties, 
         final boolean changeBean) throws IllegalAccessException, 
         InvocationTargetException, NoSuchMethodException {
      if (changeBean) {
         properties.putAll(BeanUtils.describe(bean));
      }

      if (root instanceof Thinlet) {
         root = ((Thinlet)root).getDesktop();
      }

      String propertyName;
      String type;
      Object component;
      Collection groupComponents = null;

      for (final Iterator propertyNames = properties.keySet().iterator();
                                                   propertyNames.hasNext(); ) {
         propertyName = propertyNames.next().toString();
         component = find(root, propertyName);

         if (component == null) {
            if (groupComponents == null) {
               groupComponents = getAllOfClass(root, CHECKBOX);
            }

            for (final Iterator  i = groupComponents.iterator(); i.hasNext(); ) {
               component = i.next();

               if (propertyName.equals(getGroup(component)) && 
                     isSelected(component)) {
                  properties.put(propertyName, getName(component));
                  break;
               }
            }

            continue;
         }

         type = getClass(component);

         if (type.equals(COMBOBOX) || type.equals(LIST)) {
            component = getSelectedItem(component);

            if (component != null) {
               properties.put(propertyName, getName(component));
            } else {
               properties.put(propertyName, null);
            }
         } else if (type.equals(PROGRESS_BAR) || type.equals(SLIDER)) {
            properties.put(propertyName, getValue(component));
         } else if (type.equals(TABLE)) {
            // skip it intentionally
         } else {
            properties.put(propertyName, getText(component));
         }
      }

      if (changeBean) {
         BeanUtils.populate(bean, properties);

         if (bean instanceof Form) {
            ((Form)bean).afterPopulate();
         }
      }
   }

   protected void populateAndValidate(Form bean) throws IllegalAccessException, 
         InvocationTargetException, NoSuchMethodException, ValidationException,
         ValidatorException {
      populateAndValidate(bean, this);
   }

   protected void populateAndValidate(Form bean, Object root) 
         throws IllegalAccessException, InvocationTargetException, 
                NoSuchMethodException, ValidationException, ValidatorException {
      final Map properties = new HashMap();

      populate(bean, root, properties);

      final Map validationErrors = ValidationUtils.getInstance().getMessages(
                  bean.validate(properties), bean.getFormName());

      if (!validationErrors.isEmpty()) {
         throw new ValidationException(validationErrors);
      }
   }

   protected void populateFromEnum(String name, Class clazz) {
      populateFromEnum(name, clazz, false);
   }

   protected void populateFromEnum(String name, Class clazz, boolean blank) {
      populateFromEnum(find(name), clazz, blank);
   }

   protected void populateFromEnum(Object component, Class clazz, boolean blank) {
      final boolean combobox = getClass(component).equals(COMBOBOX);

      if (!combobox && !getClass(component).equals(LIST)) {
         throw new UnsupportedOperationException();
      }

      final ItemType type = combobox ? ItemType.CHOICE : ItemType.ITEM;

      String key;
      Object enumInstance;

      removeAll(component);

      if (blank) {
         add(component, createItemOfType("", "", type));
      }

      for (final Iterator i = Enum.getInstances(clazz).iterator(); i.hasNext(); ) {
         enumInstance = i.next();
         key = enumInstance.toString();

         add(component, createItemOfType(key, FormatterRegistry.getInstance()
               .format(enumInstance), type));
      }
   }

   protected void populateFromCollection(String nome, Collection c, 
                                      String keyProperty, String valueProperty) 
         throws IllegalAccessException, InvocationTargetException, 
                NoSuchMethodException {
      populateFromCollection(nome, c, keyProperty, valueProperty, false);
   }

   protected void populateFromCollection(String nome, Collection c, 
                      String keyProperty, String valueProperty, boolean blank) 
         throws IllegalAccessException, InvocationTargetException, 
                NoSuchMethodException {
      populateFromCollection(find(nome), c, keyProperty, valueProperty, blank);
   }

   protected void populateFromCollection(Object component, Collection c, 
                        String keyProperty, String valueProperty, boolean blank) 
         throws IllegalAccessException, InvocationTargetException, 
                NoSuchMethodException {
      populateFromCollection(component, c, keyProperty, valueProperty, blank, 
            null);
   }

   protected void populateFromCollection(Object component, Collection c, 
                        String keyProperty, String valueProperty, boolean blank,
                        String blankLabel) 
         throws IllegalAccessException, InvocationTargetException, 
                NoSuchMethodException {
       populateFromCollection(component, c, keyProperty, valueProperty, blank,
                        blankLabel, null);
   }

   protected void populateFromCollection(Object component, Collection c, 
                        String keyProperty, String valueProperty, boolean blank,
                        String blankLabel, Map formatters) 
         throws IllegalAccessException, InvocationTargetException, 
                NoSuchMethodException {
      final boolean combobox = getClass(component).equals(COMBOBOX);

      if (!combobox && !getClass(component).equals(LIST)) {
         throw new UnsupportedOperationException();
      }

      final ItemType type = combobox ? ItemType.CHOICE : ItemType.ITEM;

      String key;
      String description;
      Object o;

      removeAll(component);

      if (blank) {
         add(component, createItemOfType("", blankLabel == null ? 
               "" : blankLabel, type));
      }

      final String componentName = getName(component);

      for (final Iterator i = c.iterator(); i.hasNext(); ) {
         o = i.next();

         key = format(formatters, componentName + '.' + keyProperty, PropertyUtils
               .getProperty(o, keyProperty));
         description = valueProperty == null ? 
               format(formatters, componentName + '.', o) : format(formatters, 
               componentName + '.' + valueProperty, PropertyUtils.getProperty(o, 
               valueProperty));

         add(component, createItemOfType(key, description, type));
      }
   }

   protected void populateFromCollection(String name, Collection c)
         throws IllegalAccessException, InvocationTargetException, 
                NoSuchMethodException {
      populateFromCollection(find(name), c);
   }

   protected void populateFromCollection(Object component, Collection c)
         throws IllegalAccessException, InvocationTargetException, 
                NoSuchMethodException {
      populateFromCollection(component, c, null);
   }
   
   protected void populateFromCollection(Object component, Collection c, 
         Map formatters) throws IllegalAccessException, 
         InvocationTargetException, NoSuchMethodException {
      if (!getClass(component).equals(TABLE)) {
         throw new UnsupportedOperationException();
      }

      removeAll(component);

      final Collection propertyNames = new ArrayList();
      final Object[] columns = getTableColumns(component); 
      
      for (int i = 0; i < columns.length; i++) {
         final String name = getName(columns[i]);

         if (name == null) {
            throw new IllegalArgumentException("column (index " + i + ") in " + 
                  "table " + getName(component) + " does not have a name");
         }

         propertyNames.add(name);
      }

      final String componentName = getName(component);

      String propertyName;
      Object row;
      Object bean;
      int indexOfDot;
      boolean skip;

      for (final Iterator i = c.iterator(); i.hasNext(); ) {
         bean = i.next();
         row = createRow();

         for (final Iterator it = propertyNames.iterator(); it.hasNext(); ) {
            propertyName = it.next().toString();

            indexOfDot = 0;
            skip = false;

            while ((indexOfDot = propertyName.indexOf('.', indexOfDot)) != -1) {
               if (PropertyUtils.getProperty(bean, 
                     propertyName.substring(0, indexOfDot)) == null) {
                  add(row, createCell(propertyName, 
                        format(formatters, componentName + '.' + propertyName, 
                        null)));
                  skip = true;
                  break;
               }

               indexOfDot += 1;
            }

            if (skip) {
               continue;
            }

            add(row, createCell(propertyName, format(formatters, 
                  componentName + '.' + propertyName, 
                  PropertyUtils.getProperty(bean, propertyName))));
         }

         add(component, row);
      }
   }

   protected void bind(Object form) throws Exception {
      bind(getDesktop(), form);
   }

   protected void bind(Object widget, Object form) throws Exception {
      prepareBinder(widget, form).bind();
   }

   protected ThinletBinder prepareBinder(Object widget, Object form) 
         throws Exception {
      Map formPerClass = (Map)formPerClassPerComponent.get(widget);

      if (formPerClass == null) {
         formPerClass = new HashMap();
         formPerClassPerComponent.put(widget, formPerClass);
      } else {
         Object oldForm = formPerClass.get(form.getClass());

         if (oldForm != null) {
            binderPerForm.remove(oldForm);
         }
      }

      formPerClass.put(form.getClass(), form);

      final ThinletBinder binder = createBinder(widget, form);
      binderPerForm.put(form, binder);

      return binder;
   }

   protected ThinletBinder createBinder(Object widget, Object form){
      return new ThinletBinder(this, widget, form);
   }

   public void invokeFormAction(Object component) throws Exception {
      if (isEnabled(component) && isVisible(component)) {
         invokeFormAction(getName(component));
      }
   }

   public void invokeFormAction(String actionName) throws Exception {
      final Map formPerClass = (Map)formPerClassPerComponent.get(getDesktop());

      if (formPerClass == null) {
         throw new IllegalStateException("No form is currently bound to desktop");
      }

      final Collection forms = formPerClass.values();

      if (forms.size() > 1) {
         throw new UnsupportedOperationException("More than one form is " +
               "bound to desktop");
      }

      invokeFormAction(forms.iterator().next(), actionName);
   }

   public void invokeFormAction(Object form, String actionName) throws Exception {
      final ThinletBinder binder = (ThinletBinder)binderPerForm.get(form);

      if (binder == null) {
         throw new IllegalArgumentException(form + " is not bound");
      }

      binder.invokeAction(actionName);
   }

   public void refreshView() throws Exception {
      refreshView(getDesktop());
   }

   public void refreshView(Object widget) throws Exception {
      final Map formPerClass = (Map)formPerClassPerComponent.get(widget);

      if (formPerClass == null) {
         throw new IllegalStateException("No form is currently bound to widget");
      }

      for (final Iterator i = formPerClass.values().iterator(); i.hasNext(); ) {
         refreshViewFromForm(i.next());
      }
   }

   public void refreshViewFromForm(Object form) throws Exception {
      final ThinletBinder binder = (ThinletBinder)binderPerForm.get(form);

      if (binder == null) {
         throw new IllegalArgumentException(form + " is not bound");
      }

      binder.refresh();
   }

   protected int[] getSelectedIndexes(String name){
       return getSelectedIndexes(find(name));
   }
   
   protected int[] getSelectedIndexes(Object component) {
      if (COMBOBOX.equals(getClass(component))) {
         return new int[] {getSelected(component)};
      }

      final int selectedCount = getSelectedItems(component).length;

      final Object[] items = getItems(component);
      final int[] indexes = new int[selectedCount];

      int j = 0;

      for (int i = 0; i < items.length; i++) {
         if (isSelected(items[i])) {
            indexes[j] = i;
            j++;
         }
      }

      return indexes;
   }

   protected void setSelectedIndexes(String name, int[] indexes){
      setSelectedIndexes(find(name), indexes);
   }

   protected void setSelectedIndexes(Object component, int[] indexes) {
      if (COMBOBOX.equals(getClass(component))) {
         setSelected(component, indexes.length > 0 ? indexes[0] : -1);
         return;
      }

      final Object[] items = getItems(component);
      int j = 0;

      for (int i = 0; i < items.length; i++) {
         if (indexes.length - j < 1 || indexes[j] != i) {
            setSelected(items[i], false);
            continue;
         }

         setSelected(items[i], true);
         j++;
      }

      // TODO: Thinlet bug... component needs to be repainted... verify
      repaint(component);
   }

   protected void handleException(String message, Throwable throwable) {
      try {
         ErrorReporterDialog.show(this, getErrorMessage(), message, throwable);
      } catch (ScreenNotFoundException scnfe) {
         System.out.println("The error screen file could not be found");
         scnfe.printStackTrace();
      }
      
      LogFactory.getLog(getClass()).error(message, throwable);
   }

   public void handleException(Throwable throwable) {
      if (throwable instanceof ScreenNotFoundException) {
         handleException("The error screen file could not be found", throwable);

         return;
      } else if (throwable instanceof ValidationException) {
         showValidationErrors((ValidationException)throwable);
         return;
      } else if (throwable instanceof UIException) {
         handleUIException((UIException)throwable);
         return;
      }

      try {
         if (handleCustomException(throwable)) {
            return;
         }
      } catch (Throwable t) {
         LogFactory.getLog(getClass()).error("Unknown exception", t);
      }

      handleUnknownException(throwable);
   }

   protected void handleUIException(UIException uiException) {
      try {
         MessageDialog.show(this, uiException.getTitle(), 
               uiException.getDescription());
      } catch (Throwable t) {
         LogFactory.getLog(getClass()).error("Unknown exception", t);
      }
   }

   protected void handleUnknownException(Throwable t) {
      handleException("Unexpected error occurred", t);
   }

   protected boolean handleCustomException(Throwable t) throws Exception {
      if (t.getCause() != null) {
         handleException(t.getCause());
         return true;
      }

      return false;
   }

   protected void showValidationErrors(final ValidationException ve) {
      final StringBuffer displayMessage = new StringBuffer();

      for (final Iterator messages = ve.getValidationErrors().values()
                                          .iterator(); messages.hasNext(); ) {
         if (displayMessage.length() != 0) {
            displayMessage.append('\n');
         }

         displayMessage.append(messages.next().toString());
      }

      try {
         MessageDialog.show(this, UIUtils.getInstance().getBundle().getString(
               "validation.errors.title"), displayMessage.toString());
      } catch (ScreenNotFoundException snfe) {
         handleException(snfe);
      }
   }

   public Frame getFrame() {
      return (Frame)getParent();
   }

   public void display() throws Exception {
      this.getFrame().setVisible(true);
   }

   protected String getErrorMessage() {
      return "Error";
   }
}