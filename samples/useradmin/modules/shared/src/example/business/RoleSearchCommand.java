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
package example.business;

import java.util.List;

import example.databeans.Role;

import net.java.dev.genesis.command.hibernate.AbstractHibernateCommand;
import net.sf.hibernate.Query;

public class RoleSearchCommand extends AbstractHibernateCommand {

   /**
    * @Remotable
    */
   public List getRoles() throws Exception {
      return getSession().createQuery("from Role").list();
   }
   
   /**
    * @Remotable
    */
   public Role getRole(String roleCode) throws Exception {
      final Query query = getSession().getNamedQuery("Role.findByCode");
      query.setParameter("code", roleCode.toLowerCase());
      return (Role)query.uniqueResult();
   }
}