/*
 * Copyright (c) 2001, 2002 The XDoclet team
 * All rights reserved.
 */
package xdoclet.modules.hibernate;

import java.util.Collection;

/**
 * <p>
 *
 * Common getter methods interface to avoid conditional checking since we cannot do a common base class.</p> File
 * Creation Date: Feb 5, 2004<br>
 * <br>
 *
 *
 * @author    <a href="mailto:fbrier at users.sourceforge.net">Frederick N. Brier</a>
 * @created   February 5, 2004
 * @version   $Revision: 1.1 $
 */
public interface HibernateProperties
{
    String getTransactionManagerStrategy();

    String getUserTransactionName();

    String getUserName();

    String getPassword();

    boolean getUseOuterJoin();

    boolean getShowSql();

    String getJndiName();

    Collection getJndiProperties();

    String getDataSource();

    String getDialect();

    String getDriver();

    String getJdbcUrl();

    String getPoolSize();
}