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
package net.java.dev.genesis.ui.binding;

import net.java.dev.genesis.ui.UIException;
import net.java.dev.genesis.ui.ValidationException;

public interface DispatcherExceptionHandler extends ExceptionHandler {
   public void handleException(Throwable t);
   public boolean handleCustomException(Throwable t) throws Exception;
   public void handleUIException(UIException uiException);
   public void handleUnknownException(Throwable t);
   public void handleException(String message, Throwable t);
   public String getErrorMessage();
   public void showValidationErrors(final ValidationException ve);
}
