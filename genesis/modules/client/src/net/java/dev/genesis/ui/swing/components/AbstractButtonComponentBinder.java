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
package net.java.dev.genesis.ui.swing.components;

import net.java.dev.genesis.ui.binding.BoundAction;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.swing.SwingBinder;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;

public class AbstractButtonComponentBinder extends AbstractComponentBinder {
   public BoundAction bind(SwingBinder binder, Component component,
      ActionMetadata actionMetadata) {
      return new AbstractButtonComponentBoundAction(binder,
         (AbstractButton) component, actionMetadata);
   }

   public class AbstractButtonComponentBoundAction extends AbstractBoundMember
         implements BoundAction {
      private final AbstractButton component;
      private final ActionMetadata actionMetadata;
      private final ActionListener listener;

      public AbstractButtonComponentBoundAction(SwingBinder binder,
         AbstractButton component, ActionMetadata actionMetadata) {
         super(binder, component);
         this.component = component;
         this.actionMetadata = actionMetadata;
         this.component.addActionListener(listener = createActionListener());
      }

      protected ActionMetadata getActionMetadata() {
         return actionMetadata;
      }

      protected AbstractButton getComponent() {
         return component;
      }

      protected ActionListener getListener() {
         return listener;
      }

      protected ActionListener createActionListener() {
         return new ActionListener() {
               public void actionPerformed(ActionEvent event) {
                  try {
                     getBinder().invokeAction(actionMetadata.getName());
                  } catch (Exception e) {
                     getBinder().handleException(e);
                  }
               }
            };
      }

      public void unbind() {
         if (listener != null) {
            component.removeActionListener(listener);
         }
      }
   }
}
