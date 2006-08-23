/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.swt;

import net.java.dev.genesis.ui.binding.AbstractDispatcherExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;

public class SWTExceptionHandler extends AbstractDispatcherExceptionHandler {
   private final Composite root;

   public SWTExceptionHandler(Composite root) {
		this.root = root;
	}

	public Composite getRoot() {
		return root;
	}

   protected void showWarningMessageDialog(final String title, final String message) {
      showMessageDialog(title, message, SWT.ICON_WARNING | SWT.OK);
   }

   protected void showErrorMessageDialog(final String title, final String message, Throwable throwable) {
      showMessageDialog(title, message + '\n' + getStackTrace(throwable), SWT.ICON_ERROR | SWT.OK);
   }

   protected void showMessageDialog(final String title, final String message, int style) {
      MessageBox msgBox = new MessageBox(root.getShell(), style);

      msgBox.setMessage(message);
      msgBox.setText(title);
      msgBox.open();
   }
}
