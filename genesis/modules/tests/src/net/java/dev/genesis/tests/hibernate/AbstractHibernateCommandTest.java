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
package net.java.dev.genesis.tests.hibernate;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.java.dev.genesis.tests.TestCase;
import net.sf.hibernate.HibernateException;

public class AbstractHibernateCommandTest extends TestCase {
   private final DbActions actions = new DbActions();

   public void tearDown() throws Exception {
      actions.deleteAll();
   }

   public void testAppendClause() throws Exception {
	actions.insert(2);

	AbstractHibernateCommand command = new AbstractHibernateCommand();
	assertNotNull(command.executeQuery(Arrays.asList(new Long[] {new Long(1), 
		new Long(2)})));
   }

   public static class AbstractHibernateCommand extends 
	   net.java.dev.genesis.command.hibernate.AbstractHibernateCommand 
	   implements Serializable {
	/**
	 * @Remotable
	 */
	public HibernateBean executeQuery(List pks) throws HibernateException {
	   StringBuffer where = new StringBuffer();
	   Map parameters = new HashMap();

	   appendClause(where, "h.pk in (:pks)");
	   parameters.put("pks", pks);

	   appendClause(where, "h.pk = :pk");
	   parameters.put("pk", pks.get(0));

	   return (HibernateBean) generateQuery("select h", 
		   Collections.singletonMap("h", "HibernateBean"), where.toString(), 
		   "", parameters).uniqueResult();
	}
   }
}