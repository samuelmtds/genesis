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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThinletBinder {
   private static final Log log = LogFactory.getLog(ThinletBinder.class);
   private static final String[] supportedFieldWidgets = new String[] {
         BaseThinlet.CHECKBOX, BaseThinlet.COMBOBOX, BaseThinlet.LIST,
         BaseThinlet.PASSWORD_FIELD, BaseThinlet.SLIDER, BaseThinlet.SPINBOX, 
         BaseThinlet.TEXTFIELD, BaseThinlet.TEXTAREA, BaseThinlet.TOGGLE_BUTTON};
   private static final String[] fieldsChangedByAction = new String[] {
         BaseThinlet.COMBOBOX, BaseThinlet.LIST, BaseThinlet.TOGGLE_BUTTON};

   private final BaseThinlet thinlet;
   private final Object root;
   private final Object form;

   public ThinletBinder(final BaseThinlet thinlet, final Object root, 
            final Object form) {
      this.thinlet = thinlet;
      this.root = root;
      this.form = form;
   }

   public void bind() throws Exception {
      final FormMetadata formMetadata = ((FormMetadataFactory)form)
            .getFormMetadata(form.getClass());
      final FormController controller = (FormController)form;
      controller.setForm(form);
      controller.setFormMetadata(formMetadata);
      controller.setup();

      bindFieldMetadatas(formMetadata);
      bindActionMetadatas(formMetadata);
   }

   private void bindFieldMetadatas(final FormMetadata formMetadata) {
      for (final Iterator i = formMetadata.getFieldMetadatas().entrySet()
            .iterator(); i.hasNext(); ) {
         final Map.Entry entry = (Map.Entry)i.next();
         final String name = entry.getKey().toString();
         final List components = findComponents(name);

         if (components.isEmpty()) {
            log.warn(name + " could not be found while binding " + form.getClass());
            continue;
         }

         final String className = thinlet.getClass(components.get(0));
         final FieldMetadata fieldMetadata = (FieldMetadata)entry.getValue();

         if (Arrays.binarySearch(supportedFieldWidgets, className) < 0) {
            if (log.isDebugEnabled() && !fieldMetadata.isDisplayOnly()) {
               log.debug(name + " is not represented by an editable widget (" + 
                     className + ")");
            }

            continue;
         }

         if (fieldMetadata.isDisplayOnly()) {
            if (log.isDebugEnabled()) {
               log.debug(name + " is a display only field, skipping...");
            }
            
            continue;
         }

         if (log.isTraceEnabled()) {
            log.trace("Binding field " + name + " in " + 
                  form.getClass().getName() + " to " + className);
         }

         for (final Iterator it = components.iterator(); it.hasNext(); ) {
            final Object component = it.next();

            if (className.equals(BaseThinlet.CHECKBOX)) {
               thinlet.setMethod(component, "action", "setValue(" + 
                     thinlet.getName(component) + ".group)", root, this);
            } else if (Arrays.binarySearch(fieldsChangedByAction, className) > 0) {
               thinlet.setMethod(component, "action", "setValue(" + name + 
                     ".name)", root, this);
            } else {
               thinlet.setMethod(component, "focuslost", "setValue(" + name + 
                     ".name)", root, this);
            }
         }
      }
   }

   private List findComponents(final String name) {
      final Object widget = thinlet.find(root, name);

      if (widget != null) {
         return Collections.singletonList(widget);
      }

      final List groupComponents = thinlet.getAllOfClass(root, 
            BaseThinlet.CHECKBOX);

      for (final Iterator  i = groupComponents.iterator(); i.hasNext(); ) {
         final Object component = i.next();

         if (!name.equals(thinlet.getGroup(component))) {
            i.remove();
         }
      }

      return groupComponents;
   }

   public void setValue(String name) {
      //TODO: implement this
   }

   private void bindActionMetadatas(final FormMetadata formMetadata) {
      for (final Iterator i = formMetadata.getActionMetadatas().entrySet()
            .iterator(); i.hasNext(); ) {
         final Map.Entry entry = (Map.Entry)i.next();
         final String name = entry.getKey().toString();
         final Object component = thinlet.find(root, name);

         if (component == null) {
            log.warn(name + " could not be found while binding " + form.getClass());
            continue;
         }

         final String className = thinlet.getClass(component);

         if (!BaseThinlet.BUTTON.equals(className)) {
            if (log.isDebugEnabled()) {
               log.debug(name + " is not represented by a button; it is a " + 
                     className);
            }

            continue;
         }

         final ActionMetadata actionMetadata = (ActionMetadata)entry.getValue();

         if (log.isTraceEnabled()) {
            log.trace("Binding action " + name + " in " + form.getClass().getName());
         }

         thinlet.setMethod(component, "action", "invokeAction(" + 
               name + ".name)", root, this);
      }
   }

   public void invokeAction(String name) {
      //TODO: implement this
   }
}