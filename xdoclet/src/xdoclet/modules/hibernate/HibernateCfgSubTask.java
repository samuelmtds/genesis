/*
 * Copyright (c) 2001, 2002 The XDoclet team
 * All rights reserved.
 */
package xdoclet.modules.hibernate;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.tools.ant.types.Parameter;

import xdoclet.XDocletException;
import xdoclet.XmlSubTask;

import xdoclet.util.Translator;

/**
 * Generate the hibernate.cfg.xml file. It lists all of the properties as well as a property for each hbm.xml file. This
 * file can be used for creating and installing a SessionFactory in JNDI as well as launching Hibern8IDE.
 *
 * @author        <a href="mailto:fbrier at users.sourceforge.net">Frederick N. Brier</a>
 * @created       February 6, 2004
 * @version       $Revision: 1.1 $
 * @ant.element   name="hibernatecfg" display-name="Hibernate Configuration File Generation"
 *      parent="xdoclet.modules.hibernate.HibernateDocletTask"
 */
public class HibernateCfgSubTask extends XmlSubTask implements HibernateProperties
{
    /**
     * Default template to use for hibernate files
     */
    private static String DEFAULT_TEMPLATE_FILE = "resources/hibernate-cfg.xdt";

    /**
     * Pattern for generation of hibernate files
     */
    private static String GENERATED_CONFIG_FILE_NAME = "hibernate.cfg.xml";

    private final static String HIBERNATE_PUBLICID_20 = "-//Hibernate/Hibernate Configuration DTD 2.0//EN";

    private final static String HIBERNATE_SYSTEMID_20 = "http://hibernate.sourceforge.net/hibernate-configuration-2.0.dtd";

    private final static String DTD_FILE_NAME_20 = "resources/hibernate-configuration-2.0.dtd";

    private String  jndiName = null;
    private String  dataSource = null;
    private String  dialect = null;
    private boolean useOuterJoin = false;
    private boolean showSql = false;
    private String  userName = null;
    private String  password = null;
    private String  userTransactionName = null;
    private String  transactionManagerStrategy = null;
    private String  driver;
    private String  jdbcUrl;
    private String  poolSize;
    private String  transactionManagerLookup;
    private ArrayList jndiProperties = new ArrayList();

    /**
     * Constructor for the HibernateSubTask object
     */
    public HibernateCfgSubTask()
    {
        setSubTaskName("hibernatecfg");
        setHavingClassTag("hibernate.class");
        setTemplateURL(getClass().getResource(DEFAULT_TEMPLATE_FILE));
        setDestinationFile(GENERATED_CONFIG_FILE_NAME);
        setPublicId(HIBERNATE_PUBLICID_20);
        setSystemId(HIBERNATE_SYSTEMID_20);
        setDtdURL(getClass().getResource(DTD_FILE_NAME_20));
    }

    public String getTransactionManagerLookup()
    {
        return transactionManagerLookup;
    }

    public Collection getJndiProperties()
    {
        return jndiProperties;
    }

    public String getTransactionManagerStrategy()
    {
        return transactionManagerStrategy;
    }

    public String getUserTransactionName()
    {
        return userTransactionName;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getPassword()
    {
        return password;
    }

    public boolean getUseOuterJoin()
    {
        return useOuterJoin;
    }

    public boolean getShowSql()
    {
        return showSql;
    }

    public String getJndiName()
    {
        return jndiName;
    }

    public String getDataSource()
    {
        return dataSource;
    }

    public String getDialect()
    {
        return dialect;
    }

    public String getDriver()
    {
        return driver;
    }

    public String getJdbcUrl()
    {
        return jdbcUrl;
    }

    public String getPoolSize()
    {
        return poolSize;
    }

    /**
     * Hibernate connection pool size.
     *
     * @param poolSize
     * @ant.not-required
     */
    public void setPoolSize(String poolSize)
    {
        this.poolSize = poolSize;
    }

    /**
     * URL for the JDBC Driver to make the connection to the database.
     *
     * @param jdbcUrl
     * @ant.not-required
     */
    public void setJdbcUrl(String jdbcUrl)
    {
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * Strategy for obtaining the JTA <tt>TransactionManager</tt>
     *
     * @param transactionManagerStrategy
     * @ant.not-required
     */
    public void setTransactionManagerStrategy(String transactionManagerStrategy)
    {
        this.transactionManagerStrategy = transactionManagerStrategy;
    }

    /**
     * The JNDI name of the JTA UserTransaction object
     *
     * @param userTransactionName
     * @ant.not-required
     */
    public void setUserTransactionName(String userTransactionName)
    {
        this.userTransactionName = userTransactionName;
    }

    /**
     * The fully qualified class name of the Hibernate <tt>TransactionFactory</tt> implementation.
     *
     * @param transactionManagerLookup
     * @ant.not-required
     */
    public void setTransactionManagerLookup(String transactionManagerLookup)
    {
        this.transactionManagerLookup = transactionManagerLookup;
    }

    /**
     * Whether to use outer join
     *
     * @param useOuterJoin
     * @ant.not-required    No. Defaults to false.
     */
    public void setUseOuterJoin(boolean useOuterJoin)
    {
        this.useOuterJoin = useOuterJoin;
    }

    /**
     * Log sql statements. Defaults to false.
     *
     * @param showSql
     * @ant.not-required
     */
    public void setShowSql(boolean showSql)
    {
        this.showSql = showSql;
    }

    /**
     * JNDI name to bind to the <tt>SessionFactory</tt>
     *
     * @param jndiName
     * @ant.not-required
     */
    public void setJndiName(String jndiName)
    {
        this.jndiName = jndiName;
    }

    /**
     * JNDI name of data source to use in the session factory.
     *
     * @param dataSource
     * @ant.not-required
     */
    public void setDataSource(String dataSource)
    {
        this.dataSource = dataSource;
    }

    /**
     * SQL <a href="http://hibernate.bluemars.net/hib_docs/api/cirrus/hibernate/sql/Dialect.html">dialect</a> of the
     * database.
     *
     * @param dialect
     * @ant.required   Yes. Use fully-qualified class name.
     */
    public void setDialect(String dialect)
    {
        this.dialect = dialect;
    }

    /**
     * JDBC Driver to make database connection.
     *
     * @param driver
     * @ant.not-required
     */
    public void setDriver(String driver)
    {
        this.driver = driver;
    }

    /**
     * Use this user name to login to the database
     *
     * @param userName
     * @ant.not-required
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * Use this password to login to the database
     *
     * @param password
     * @ant.not-required
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    public void validateOptions() throws XDocletException
    {
        super.validateOptions();

        if ((null == dataSource) &&
            ((null == driver) || (null == jdbcUrl) || (null == userName) || (null == password))) {
            // Need either a dataSource or standalone connection properties
            throw new XDocletException(Translator.getString(XDocletModulesHibernateMessages.class,
                XDocletModulesHibernateMessages.DATA_CONNECTION_REQUIRED));
        }

        if ((jndiProperties.size() > 0) && (null == jndiName)) {
            // Probably want a jndiName if you are going to specify additional properties to use with it.
            throw new XDocletException(Translator.getString(XDocletModulesHibernateMessages.class,
                XDocletModulesHibernateMessages.JNDI_NAME_FOR_PROPS_REQUIRED));
        }

        if (getDialect() == null) {
            throw new XDocletException(Translator.getString(XDocletModulesHibernateMessages.class,
                XDocletModulesHibernateMessages.SQL_DIALECT_REQUIRED));
        }
    }

    /**
     * These elements allow you to add properties to the JNDI context. For instance, if you do not want Weblogic
     * clustering to replicate the Hibernate SessionFactory, add a jndiProperty element with a "name" attribute of
     * "weblogic.jndi.replicateBindings" and a "value" attribute of "false".
     *
     * @param jndiProperty
     * @ant.not-required    No. Empty array of elements.
     */
    public void addConfiguredJndiProperty(Parameter jndiProperty)
    {
        System.out.println("addConfiguredJndiProperty(): name=" + jndiProperty.getName() + ", " + jndiProperty.getValue());
        jndiProperties.add(jndiProperty);
    }

    /**
     * Called when the engine is started
     *
     * @exception XDocletException  Thrown in case of problem
     */
    protected void engineStarted() throws XDocletException
    {
        System.out.println(Translator.getString(XDocletModulesHibernateMessages.class,
            XDocletModulesHibernateMessages.GENERATING_HIBERNATE_CFG_XML));
    }
}