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
package net.java.dev.genesis.tests.hibernate;

import java.io.Serializable;

/**
 * @hibernate.class table="HibernateBean"
 */
public class HibernateBean implements Serializable {

   private Long pk;
   private String codigo;

   /**
    * @hibernate.id generator-class="increment"
    */
   public Long getPk() {
      return pk;
   }

   public void setPk(Long pk) {
      this.pk = pk;
   }

   /**
    * @hibernate.property
    */
   public String getCodigo() {
      return codigo;
   }

   public void setCodigo(String codigo) {
      this.codigo = codigo;
   }
   
   public String toString(){
      return getClass().getName() + " - pk: " + getPk() + ", codigo: " + getCodigo();
   }
}