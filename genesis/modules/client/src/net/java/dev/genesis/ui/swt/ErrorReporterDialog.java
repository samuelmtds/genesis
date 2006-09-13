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

import net.java.dev.genesis.ui.UIUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class ErrorReporterDialog extends Dialog {
   private Shell shell;
   private Composite stackTraceComposite = null;
   private Label messageLabel = null;
   private Label stackTraceLabel = null;
   private Text stackTraceArea = null;
   private Button button = null;

   public ErrorReporterDialog(Shell parent, int style) {
      super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | style);
      initialize();
   }

   private void initialize() {
      shell = new Shell(getStyle());
      shell.setLayout(new GridLayout());

      createStackTraceComposite();

      GridData buttonGridData = new GridData();
      buttonGridData.horizontalAlignment = GridData.CENTER;
      buttonGridData.verticalAlignment = GridData.CENTER;

      button = new Button(shell, SWT.NONE);
      button.setText(UIUtils.getInstance().getBundle()
                                  .getString("ErrorReporterDialog.ok"));
      button.setLayoutData(buttonGridData);
      button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
               shell.dispose();
            }
         });
   }

   private void createStackTraceComposite() {
      stackTraceComposite = new Composite(shell, SWT.NONE);
      stackTraceComposite.setLayout(new GridLayout());

      messageLabel = new Label(stackTraceComposite, SWT.BOLD);

      Font originalFont = messageLabel.getFont();
      FontData fontData = messageLabel.getFont().getFontData()[0];
      messageLabel.setFont(new Font(originalFont.getDevice(),
            fontData.getName(), fontData.getHeight(), SWT.BOLD));

      stackTraceLabel = new Label(stackTraceComposite, SWT.NONE);
      stackTraceLabel.setText(UIUtils.getInstance().getBundle()
                                           .getString("ErrorReporterDialog.stackTrace"));

      GridData stackTraceGridData = new GridData();
      stackTraceGridData.horizontalAlignment = GridData.CENTER;
      stackTraceGridData.verticalAlignment = GridData.CENTER;
      stackTraceGridData.widthHint = 400;
      stackTraceGridData.heightHint = 200;

      stackTraceArea = new Text(stackTraceComposite,
            SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
      stackTraceArea.setLayoutData(stackTraceGridData);
   }

   public void setMessage(String message) {
      messageLabel.setText(message);
   }

   public void setStackTraceMessage(String message) {
      stackTraceArea.setText(message);
   }

   public int open() {
      shell.pack();

      final Display display = getParent().getDisplay();
      final Rectangle bounds = display.getBounds();
      shell.setLocation(bounds.x +
         ((bounds.width - shell.getBounds().width) / 2),
         bounds.y + ((bounds.height - shell.getBounds().height) / 2));

      shell.open();

      while (!shell.isDisposed()) {
         if (!display.readAndDispatch()) {
            display.sleep();
         }
      }

      return SWT.OK;
   }
}
