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
   public static final State BRASILIA = new State("Brasilia");
   public static final State RIO_DE_JANEIRO = new State("Rio de Janeiro");
   public static final State SAO_PAULO = new State("Sao Paulo");

   public static final State ALASKA = new State("Alaska");
   public static final State FLORIDA = new State("Florida");
   public static final State NEW_YORK = new State("New York");

   public static final State OTHER = new State("Other");

   private State(String name) {
      super(name);
   }

   public static Collection getStates(Country country) {
      final Collection states = new ArrayList();

      if (country == Country.BRAZIL) {
         states.add(BRASILIA);
         states.add(RIO_DE_JANEIRO);
         states.add(SAO_PAULO);
      } else if (country == Country.USA) {
         states.add(ALASKA);
         states.add(FLORIDA);
         states.add(NEW_YORK);
      } else {
         states.add(OTHER);
      }

      return states;
   }
}