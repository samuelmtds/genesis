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
package net.java.dev.genesis.equality;

public class StringEqualityComparator extends DefaultEqualityComparator {

    private boolean trim = true;

    public boolean isTrim() {
        return trim;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    public boolean equals(Object o1, Object o2) {
        return super.equals(normalize(o1), normalize(o2));
    }

    private String normalize(Object o) {
        if(o == null){
            return "";
        }

        final String s = o.toString();

        return trim ? s.trim() : s;
    }

   /* public String toString() {
        return "[" + getClass().getName() + ": trim=" + trim + "]";
    }*/
}