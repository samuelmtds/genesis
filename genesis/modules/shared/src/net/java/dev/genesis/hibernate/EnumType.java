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
package net.java.dev.genesis.hibernate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import net.java.dev.genesis.helpers.EnumHelper;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.UserType;

public abstract class EnumType implements UserType {
    private static final int[] TYPES = new int[] {Types.VARCHAR};

    public Object deepCopy(Object obj) throws HibernateException {
        return obj;
    }
    
    public boolean equals(Object obj, Object obj1) throws HibernateException {
       if (obj == null) {
           return obj1 == null;
       }

       return obj.equals(obj1);
    }

    public boolean isMutable() {
        return false;
    }

    public Object nullSafeGet(ResultSet resultSet, String[] str, Object obj) 
                                     throws HibernateException, SQLException {
       String key = resultSet.getString(str[0]);

       return toEnum(key);
    }

    protected Object toEnum(String key) {
       if (key == null || key.trim().length() == 0) {
          return null;
       }

       return EnumHelper.getInstance().valueOf(returnedClass(), key);
    }

    public void nullSafeSet(PreparedStatement preparedStatement, Object obj, 
                            int param) throws HibernateException, SQLException {
       obj = toDatabaseObject(obj);

       if (obj == null) {
           preparedStatement.setNull(param, TYPES[0]);
           return;
       }

       preparedStatement.setString(param, obj.toString());
    }

    protected Object toDatabaseObject(Object o) {
       return o;
    }

    public abstract Class returnedClass();

    public int[] sqlTypes() {
        return TYPES;
    }
}