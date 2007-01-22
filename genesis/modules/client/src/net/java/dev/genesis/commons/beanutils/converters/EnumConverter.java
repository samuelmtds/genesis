/*
 * The Genesis Project
 * Copyright (C) 2004-2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.commons.beanutils.converters;

import net.java.dev.genesis.helpers.EnumHelper;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

public class EnumConverter implements Converter {
   private final boolean nullReturnAcceptable;
   
   public EnumConverter() {
       this(false);
   }
   
   public EnumConverter(final boolean nullReturnAcceptable) {
       this.nullReturnAcceptable = nullReturnAcceptable;
   }
   
   public final boolean isNullReturnAcceptable() {
       return nullReturnAcceptable;
   }
   
   public Object convert(final Class type, Object value) {
       if (nullReturnAcceptable && value == null) {
           return null;
       }

       if (value != null && type.isAssignableFrom(value.getClass())) {
          return value;
       }

       Object en = null;
       Exception e = null;
       
       try {
           en = EnumHelper.getInstance().valueOf(type, (String) value);

           if (en == null && !nullReturnAcceptable) {
               throw new NullPointerException();
           }
       } catch(final ClassCastException cce) {
           e = cce;
       } catch(final IllegalArgumentException iae) {
           e = iae;
       } catch(final NullPointerException npe) {
           e = npe;
       }
       
       if (e != null) {
           throw new ConversionException(e);
       }

       return en;
   }
}

