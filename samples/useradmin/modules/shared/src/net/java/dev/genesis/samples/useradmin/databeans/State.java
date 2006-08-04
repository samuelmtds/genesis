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
package net.java.dev.genesis.samples.useradmin.databeans;

import java.util.ArrayList;
import java.util.Collection;

import net.java.dev.reusablecomponents.lang.Enum;

public class State extends Enum {
   public static final State DISTRITO_FEDERAL = new State("DF");
   public static final State RIO_DE_JANEIRO = new State("RJ");
   public static final State SAO_PAULO = new State("SP");

   public static final State ALASKA = new State("AL");
   public static final State FLORIDA = new State("FL");
   public static final State NEW_YORK = new State("NY");

   public static final State OTHER = new State("OTHER");

   private State(String name) {
      super(name);
   }

   public static Collection getStates(Country country) {
      final Collection states = new ArrayList();

      if (country == Country.BRAZIL) {
         states.add(DISTRITO_FEDERAL);
         states.add(RIO_DE_JANEIRO);
         states.add(SAO_PAULO);
      } else if (country == Country.USA) {
         states.add(ALASKA);
         states.add(FLORIDA);
         states.add(NEW_YORK);
      } else if (country == Country.OTHER) {
         states.add(OTHER);
      }

      return states;
   }
}