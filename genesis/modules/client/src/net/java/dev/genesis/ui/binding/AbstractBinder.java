/*
 * The Genesis Project
 * Copyright (C) 2005-2006  Summa Technologies do Brasil Ltda.
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

import net.java.dev.genesis.helpers.TypeChecker;
import net.java.dev.genesis.reflection.MethodEntry;
import net.java.dev.genesis.text.Formatter;
import net.java.dev.genesis.text.FormatterRegistry;
import net.java.dev.genesis.ui.ValidationUtils;
import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.controller.FormControllerFactory;
import net.java.dev.genesis.ui.controller.FormControllerListener;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;
import net.java.dev.genesis.ui.metadata.MethodMetadata;
import net.java.dev.genesis.ui.metadata.ViewMetadata;
import net.java.dev.genesis.ui.metadata.ViewMetadataFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractBinder implements FormControllerListener {
   private static final Log log = LogFactory.getLog(AbstractBinder.class);

   private final Map boundFields = new HashMap();
   private final Map boundActions = new HashMap();
   private final Map boundDataProviders = new HashMap();
   private final Object form;
   private final Object handler;
   private final FormController controller;

   public AbstractBinder(Object form, Object handler) {
      this.form = form;
      this.handler = handler;
      this.controller = getFormController(form);
   }

   public Object getForm() {
      return form;
   }

   public FormController getFormController() {
      return controller;
   }

   protected ViewMetadata getViewMetadata(final Object handler) {
      TypeChecker.checkViewMetadataFactory(handler);

      return ((ViewMetadataFactory) handler).getViewMetadata(handler.getClass());
   }

   protected FormController getFormController(final Object form) {
      TypeChecker.checkFormControllerFactory(form);

      return ((FormControllerFactory) form).getFormController(form);
   }

   protected FormMetadata getFormMetadata(final Object form) {
      TypeChecker.checkFormMetadataFactory(form);

      return ((FormMetadataFactory) form).getFormMetadata(form.getClass());
   }

   public void bind() throws Exception {
      try {
         final FormMetadata formMetadata = getFormMetadata(form);

         bindFieldMetadatas(formMetadata);
         bindActionMetadatas(formMetadata);
         bindDataProviders(formMetadata);

         setupController();
      } catch (Exception e) {
         unbind();

         throw e;
      }
   }

   protected void bindFieldMetadatas(final FormMetadata formMetadata) {
      for (final Iterator i =
            formMetadata.getFieldMetadatas().entrySet().iterator();
            i.hasNext();) {
         final Map.Entry entry = (Map.Entry) i.next();

         BoundField boundField =
            bindFieldMetadata(entry.getKey().toString(), formMetadata,
               (FieldMetadata) entry.getValue());

         if (boundField != null) {
            boundFields.put(boundField.getName(), boundField);
         }
      }
   }

   protected void bindActionMetadatas(final FormMetadata formMetadata) {
      for (final Iterator i =
            formMetadata.getActionMetadatas().entrySet().iterator();
            i.hasNext();) {
         final Map.Entry entry = (Map.Entry) i.next();

         BoundAction boundAction =
            bindActionMetadata(((MethodEntry) entry.getKey()).getMethodName(),
               formMetadata, (ActionMetadata) entry.getValue());

         if (boundAction != null) {
            boundActions.put(boundAction.getName(), boundAction);
         }
      }
   }

   protected void bindDataProviders(final FormMetadata formMetadata) {
      for (final Iterator i =
            formMetadata.getDataProviderMetadatas().values().iterator();
            i.hasNext();) {
         final DataProviderMetadata dataProviderMetadata =
            (DataProviderMetadata) i.next();

         BoundDataProvider boundDataProvider =
            bindDataProvider(dataProviderMetadata.getWidgetName(),
               formMetadata, dataProviderMetadata);

         if (boundDataProvider != null) {
            boundDataProviders.put(boundDataProvider.getName(),
               boundDataProvider);

            bindDataProviderObjectField(dataProviderMetadata, boundDataProvider);
         }
      }
   }

   protected void bindDataProviderObjectField(
      DataProviderMetadata dataProviderMetadata,
      BoundDataProvider boundDataProvider) {
      if ((dataProviderMetadata.getObjectField() != null) &&
            boundDataProvider instanceof BoundField) {
         boundFields.put(dataProviderMetadata.getObjectField().getFieldName(),
            boundDataProvider);
      }
   }

   protected abstract BoundField bindFieldMetadata(String name,
      FormMetadata formMetadata, FieldMetadata fieldMetadata);

   protected abstract BoundAction bindActionMetadata(String name,
      FormMetadata formMetadata, ActionMetadata actionMetadata);

   protected abstract BoundDataProvider bindDataProvider(String name,
      FormMetadata formMetadata, DataProviderMetadata dataProviderMetadata);

   protected void setupController() throws Exception {
      if (!controller.isSetup()) {
         controller.addFormControllerListener(getFormControllerListener());
         controller.setup();
      } else {
         controller.fireAllEvents(this);
         controller.addFormControllerListener(getFormControllerListener());
      }
   }

   protected FormControllerListener getFormControllerListener() {
      return this;
   }

   public boolean beforeInvokingMethod(MethodMetadata methodMetadata)
            throws Exception {
      return getViewMetadata(handler)
                   .invokeBeforeAction(handler, methodMetadata.getName());
   }

   public void afterInvokingMethod(MethodMetadata methodMetadata)
            throws Exception {
      getViewMetadata(handler)
            .invokeAfterAction(handler, methodMetadata.getName());
   }

   public void dataProvidedListChanged(DataProviderMetadata metadata, List items)
            throws Exception {
      final String name = metadata.getWidgetName();
      final BoundDataProvider boundDataProvider =
         (BoundDataProvider) boundDataProviders.get(name);

      if (boundDataProvider == null) {
         log.warn(name + " could not be found while populating using " +
            form.getClass());

         return;
      }

      if (log.isDebugEnabled()) {
         log.debug("Populating " + name + " with " + metadata.getName());
      }

      if (!controller.isResetOnDataProviderChange()) {
         resetSelectedFields(metadata);
      }

      boundDataProvider.updateList(items);
   }

   public void dataProvidedIndexesChanged(DataProviderMetadata metadata,
      int[] selectedIndexes) {
      final String name = metadata.getWidgetName();
      final BoundDataProvider boundDataProvider =
         (BoundDataProvider) boundDataProviders.get(metadata.getName());

      if (boundDataProvider == null) {
         log.warn(name + " could not be found while updating indexes using " +
            getForm().getClass());

         return;
      }

      if (log.isDebugEnabled()) {
         log.debug("Updating indexes " + name + " with " + metadata.getName());
      }

      boundDataProvider.updateIndexes(selectedIndexes);
   }

   protected void resetSelectedFields(DataProviderMetadata meta)
            throws Exception {
      meta.resetSelectedFields(form);
   }

   public void valuesChanged(Map updatedValues) throws Exception {
      for (Iterator iter = updatedValues.entrySet().iterator(); iter.hasNext();) {
         Map.Entry entry = (Map.Entry) iter.next();
         BoundField boundField =
            (BoundField) boundFields.get(entry.getKey().toString());

         if (boundField == null) {
            continue;
         }

         boundField.setValue(entry.getValue());
      }
   }

   public void enabledConditionsChanged(Map updatedEnabledConditions) {
      conditionsChanged(updatedEnabledConditions, true);
   }

   public void visibleConditionsChanged(Map updatedVisibleConditions) {
      conditionsChanged(updatedVisibleConditions, false);
   }

   protected void conditionsChanged(Map updatedConditions, boolean enabled) {
      for (final Iterator i = updatedConditions.entrySet().iterator();
            i.hasNext();) {
         final Map.Entry entry = (Map.Entry) i.next();
         BoundMember member = getBoundMember(entry.getKey().toString());

         if (member == null) {
            return;
         }

         if (log.isDebugEnabled()) {
            log.debug("Changing " + entry.getKey() +
               (enabled ? " enabled" : " visible") + " state to " +
               entry.getValue());
         }

         if (enabled) {
            member.setEnabled(((Boolean) entry.getValue()).booleanValue());
         } else {
            member.setVisible(((Boolean) entry.getValue()).booleanValue());
         }
      }
   }

   protected BoundMember getBoundMember(String name) {
      BoundMember member = (BoundMember) boundFields.get(name);

      if (member == null) {
         member = (BoundMember) boundActions.get(name);
      }

      return member;
   }

   public void unbind() {
      unbindFields();
      unbindActions();
      unbindDataProviders();
   }

   protected void unbindFields() {
      unbind(boundFields);
   }

   protected void unbindActions() {
      unbind(boundActions);
   }

   protected void unbindDataProviders() {
      unbind(boundDataProviders);
   }

   protected void unbind(Map boundElements) {
      for (Iterator iter = boundElements.entrySet().iterator(); iter.hasNext();) {
         Map.Entry entry = (Map.Entry) iter.next();

         ((BoundElement) entry.getValue()).unbind();

         iter.remove();
      }
   }

   public void refresh() throws Exception {
      controller.update();
   }

   public void invokeAction(String name) throws Exception {
      Map stringProperties = null;

      if (name != null) {
         final MethodMetadata methodMetadata = getMethodMetadata(name);

         if ((methodMetadata != null) &&
               (methodMetadata.getActionMetadata() != null) &&
               methodMetadata.getActionMetadata().isValidateBefore()) {
            stringProperties = getStringProperties();
         }
      }

      controller.invokeAction(name, stringProperties);
   }

   protected MethodMetadata getMethodMetadata(String name) {
      return getFormMetadata(form)
                   .getMethodMetadata(new MethodEntry(name, new String[0]));
   }

   protected Map getStringProperties() throws Exception {
      return ValidationUtils.getInstance().getPropertiesMap(form);
   }

   protected String format(Map formatters, String key, Object value) {
      Formatter formatter = null;

      if (formatters != null) {
         formatter = (Formatter) formatters.get(key);
      }

      return (formatter == null)
      ? FormatterRegistry.getInstance().format(value) : formatter.format(value);
   }

   public int[] getIndexesFromUI(int[] selected, boolean isBlank) {
      if ((selected.length >= 1) && isBlank) {
         boolean remove = false;

         for (int i = 0; i < selected.length; i++) {
            selected[i] = selected[i] - 1;

            if (selected[i] == -1) {
               remove = true;
            }
         }

         if (remove) {
            if (selected.length == 1) {
               selected = new int[0];
            } else {
               final int[] aux = selected;
               selected = new int[aux.length - 1];
               System.arraycopy(aux, 1, selected, 0, aux.length - 1);
            }
         }
      }

      return selected;
   }

   public int[] getIndexesFromController(int[] selected, boolean isBlank) {
      if (isBlank) {
         if (selected.length == 0) {
            return new int[] { -1 };
         }

         for (int i = 0; i < selected.length; i++) {
            selected[i] = selected[i] + 1;
         }
      }

      return selected;
   }
}
