/*
 * The Genesis Project
 * Copyright (C) 2004  Summa Technologies do Brasil Ltda.
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.java.dev.genesis.text.FormatterRegistry;
import net.java.dev.genesis.ui.Form;
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
   public static final String ENABLED = "enabled";
   public static final String END = "end";
   public static final String GROUP = "group";
   public static final String HEADER = "header";
   public static final String LABEL = "label";
   public static final String LIST = "list";
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
   
   private ThinletBinder binder;

   protected static final String MESSAGE = "message";

   public abstract class ScreenHandler {
      private Object screen;

      protected ScreenHandler() {
      }

      protected ScreenHandler(String fileName) throws ScreenNotFoundException {
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

   protected InputStream getResourceAsStream(String name) {
      return getResourceAsStream(name, 
                                 Thread.currentThread().getContextClassLoader());
   }

   protected InputStream getResourceAsStream(String name, ClassLoader cl) {
      return cl.getResourceAsStream(name);
   }

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
      final Object choice = create(CHOICE);
      setName(choice, name);
      setText(choice, text);
      setTooltip(choice, text);
      
      return choice;
   }

   protected Object createRow() {
      return create(ROW);
   }

   protected Object createCell(String name, String text) {
      final Object cell = create(CELL);
      setName(cell, name);
      setText(cell, text);
      setTooltip(cell, text);
      
      return cell;
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
                     propertyValue = getPropertyValue(properties, propertyName, true);
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

            propertyValue = getPropertyValue(properties, propertyName, true);
            selectedComponent = find(component, propertyValue);

            if (selectedComponent != null) {
               if (type.equals(LIST)) {
                  setSelected(selectedComponent, true);
               } else {
                  setSelected(component, getIndexOf(component, selectedComponent));
               }
            }
         } else if (type.equals(PROGRESS_BAR) || type.equals(SLIDER)) {
            setValue(component, getPropertyValue(properties, propertyName, false));
         } else if (type.equals(PANEL)) {
            displayBean(properties.get(propertyName), component);
         } else if (!type.equals(TABLE)){
            setText(component, getPropertyValue(properties, propertyName, false));
         }
      }
   }

   protected String getPropertyValue(Map properties, String propertyName,
            boolean enumKey) {
        Object o = properties.get(propertyName);
        return (enumKey && o instanceof Enum) ? o.toString()
                : FormatterRegistry.getInstance().format(o);
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
      properties.putAll(BeanUtils.describe(bean));

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
         } else {
            properties.put(propertyName, getText(component));
         }
      }

      BeanUtils.populate(bean, properties);
      bean.afterPopulate();
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
      if (!getClass(component).equals(COMBOBOX)) {
         throw new UnsupportedOperationException();
      }

      String key;
      Object en;

      removeAll(component);

      if (blank) {
         add(component, createChoice("", ""));
      }

      for (final Iterator i = Enum.getInstances(clazz).iterator(); i.hasNext(); ) {
         en = i.next();
         key = en.toString();
         add(component, createChoice(key, FormatterRegistry.getInstance().format(en)));
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
      if (!getClass(component).equals(COMBOBOX)) {
         throw new UnsupportedOperationException();
      }

      String key;
      String description;
      Object o;
      Map properties;

      removeAll(component);

      if (blank) {
         add(component, createChoice("", ""));
      }

      for (final Iterator i = c.iterator(); i.hasNext(); ) {
         o = i.next();
         properties = BeanUtils.describe(o);

         key = FormatterRegistry.getInstance().format(properties.get(keyProperty));
         description = FormatterRegistry.getInstance().format(
               valueProperty == null ? o : properties.get(valueProperty));

         add(component, createChoice(key, description));
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
      if (!getClass(component).equals(TABLE)) {
         throw new UnsupportedOperationException();
      }

      removeAll(component);
      final Collection propertyNames = new ArrayList();
      final Object[] columns = getTableColumns(component); 
      
      for (int i = 0; i < columns.length; i++) {
         propertyNames.add(getName(columns[i]));
      }

      String propertyName;
      Object row;
      Object bean;

      for (final Iterator i = c.iterator(); i.hasNext(); ) {
         bean = i.next();
         row = createRow();

         for (final Iterator it = propertyNames.iterator(); it.hasNext(); ) {
            propertyName = it.next().toString();
            add(row, createCell(propertyName, 
                    FormatterRegistry.getInstance().format(
                           PropertyUtils.getProperty(bean, propertyName))));
         }

         add(component, row);
      }
   }

   protected void bind(Object form) throws Exception {
      bind(getDesktop(), form);
   }

   protected void bind(Object widget, Object form) throws Exception {
      binder = new ThinletBinder(this, widget, form);
      binder.bind();
   }
   
   public void action(String actionName) throws Exception {
      binder.invokeAction(actionName);
   }
   
   public void action(Object component) throws Exception {
      if (isEnabled(component) && isVisible(component)) {
         action(getName(component));
      }
   }

   protected int[] getSelectedIndices(String name){
       return getSelectedIndices(find(name));
   }
   
   protected int[] getSelectedIndices(Object component){
       final int selectedCount = getSelectedItems(component).length;

       final Object[] items = getItems(component);
       final int[] indices = new int[selectedCount];

       int j = 0;

       for (int i = 0; i < items.length; i++) {
           if (isSelected(items[i])) {
               indices[j] = i;
               j++;
           }
       }

       return indices;
   }

   private void handleException(String message, Throwable throwable) {
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
         handleException("Um arquivo necessário para exibição de uma tela " + 
                        "não pôde ser encontrado", throwable);

         return;
      } else if (throwable instanceof ValidationException) {
         showValidationErrors((ValidationException)throwable);
         return;
      }

      boolean unknown = true;

      try {
         if (handleCustomException(throwable)) {
            return;
         }
      } catch (Throwable t) {
         LogFactory.getLog(getClass()).error("Unknown exception", t);
      }

      handleUnknownException(throwable);
   }

   protected void handleUnknownException(Throwable t) {
      handleException("Ocorreu um erro inesperado", t);
   }

   protected boolean handleCustomException(Throwable t) throws Exception {
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
         MessageDialog.show(this, "Erros de validação", 
                                          displayMessage.toString());
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