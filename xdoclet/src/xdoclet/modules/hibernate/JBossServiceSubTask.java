/*
 * Copyright (c) 2001, 2002 The XDoclet team
 * All rights reserved.
 */
package xdoclet.modules.hibernate;

import java.util.Collection;

import xdoclet.XDocletException;

import xdoclet.XmlSubTask;

import xdoclet.util.Translator;
/**
 * Generate jboss mbean descriptor for hibernated classes
 *
 * @author               Konstantin Pribluda (kpribluda@j-tec-team.de)
 * @created              December 11, 2002
 * @version              $Revision: 1.2 $
 * @ant.element          name="jbossservice" display-name="JBoss service configuration"
 *      parent="xdoclet.modules.hibernate.HibernateDocletTask"
 * @xdoclet.merge-file   file="jboss-service-custom.xdt" relates-to="jboss-service.xml" description="An XML unparsed
 *      entity or XDoclet template file, for custom elements to be included in the generated jboss-service.xml"
 */
public class JBossServiceSubTask
     extends XmlSubTask implements HibernateProperties
{

    /**
     * Default template to use for hibernate files
     */
    private static String DEFAULT_TEMPLATE_FILE = "resources/jboss-service.xdt";

    /**
     * Pattern for generation of hibernate files
     */
    private static String GENERATED_SERVICE_FILE_NAME = "jboss-service.xml";

    private String  _jndiName = null;
    private String  _dataSource = null;
    private String  _dialect = null;
    private String  _serviceName = null;
    private boolean _useOuterJoin = false;
    private boolean _showSql = false;
    private String  _userName = null;
    private String  _password = null;
    private String  _userTransactionName = null;
    private String  _transactionStrategy = null;
    private String  _cacheProvider = null;
    private String  _depends = null;

    private String  _transactionManagerStrategy = null;
    private String  _maxFetchDepth = null;
    private String  _jdbcFetchSize = null;
    private String  _useQueryCache = null;
    private String  _querySubstitutions = null;
    private String  _defaultSchema = null;
    private String  _autoCreate = null;
    private String  _version = "2.1";

    /**
     * Constructor for the HibernateSubTask object
     */
    public JBossServiceSubTask()
    {
        setSubTaskName("jbossservice");
        setHavingClassTag("hibernate.class");
        setTemplateURL(getClass().getResource(DEFAULT_TEMPLATE_FILE));
        setDestinationFile(GENERATED_SERVICE_FILE_NAME);
    }

    public String getTransactionManagerStrategy()
    {
        return _transactionManagerStrategy;
    }

    public String getUserTransactionName()
    {
        return _userTransactionName;
    }

    public String getTransactionStrategy()
    {
        return _transactionStrategy;
    }

    public String getCacheProvider()
    {
        return _cacheProvider;
    }

    public String getDepends()
    {
        return _depends;
    }

    public String getUserName()
    {
        return _userName;
    }

    public String getPassword()
    {
        return _password;
    }

    public boolean getUseOuterJoin()
    {
        return _useOuterJoin;
    }

    public boolean getShowSql()
    {
        return _showSql;
    }

    /**
     * return configured service name
     *
     * @return
     */
    public String getServiceName()
    {
        return _serviceName;
    }

    public String getJndiName()
    {
        return _jndiName;
    }

    public String getDataSource()
    {
        return _dataSource;
    }

    public String getDialect()
    {
        return _dialect;
    }

    public String getAutoCreate()
    {
        return _autoCreate;
    }

    public String getDriver()
    {
        throw new UnsupportedOperationException("JBossServiceSubTask does not have a Driver attribute.");
    }

    public String getJdbcUrl()
    {
        throw new UnsupportedOperationException("JBossServiceSubTask does not have a jdbcUrl attribute.");
    }

    public String getPoolSize()
    {
        throw new UnsupportedOperationException("JBossServiceSubTask does not have a poolSize attribute.");
    }

    public Collection getJndiProperties()
    {
        throw new UnsupportedOperationException("JBossServiceSubTask does not have a jndiProperties attribute.");
    }

    public Collection getOtherProperties()
    {
        throw new UnsupportedOperationException("JBossServiceSubTask does not have an otherProperties attribute.");
    }

    public Collection getOtherMappings()
    {
        throw new UnsupportedOperationException("JBossServiceSubTask does not have an otherMappings attribute.");
    }

    /**
     * @return
     */
    public String getDefaultSchema()
    {
        return _defaultSchema;
    }

    /**
     * @return
     */
    public String getJdbcFetchSize()
    {
        return _jdbcFetchSize;
    }

    /**
     * @return
     */
    public String getMaxFetchDepth()
    {
        return _maxFetchDepth;
    }

    /**
     * @return
     */
    public String getQuerySubstitutions()
    {
        return _querySubstitutions;
    }

    /**
     * @return
     */
    public String getUseQueryCache()
    {
        return _useQueryCache;
    }

    public String getVersion()
    {
        return _version;
    }

    /**
     * Parameter for <tt>hibernate.hbm2ddl.auto</tt> property. Available since hibernate 2.1.6. Allowed values are
     * 'create', 'create-drop' and 'update'.
     *
     * @param autoCreate
     * @ant.not-required
     */
    public void setAutoCreate(String autoCreate)
    {
        _autoCreate = autoCreate;
    }

    /**
     * Strategy for obtaining the JTA <tt>TransactionManager</tt>
     *
     * @param transactionManagerStrategy
     * @ant.required
     */
    public void setTransactionManagerStrategy(String transactionManagerStrategy)
    {
        _transactionManagerStrategy = transactionManagerStrategy;
    }

    /**
     * The JNDI name of the JTA UserTransaction object
     *
     * @param userTransactionName
     * @ant.not-required
     */
    public void setUserTransactionName(String userTransactionName)
    {
        _userTransactionName = userTransactionName;
    }

    /**
     * The fully qualified class name of the Hibernate <tt>TransactionFactory</tt> implementation.
     *
     * @param transactionStrategy
     * @ant.not-required
     */
    public void setTransactionStrategy(String transactionStrategy)
    {
        _transactionStrategy = transactionStrategy;
    }

    /**
     * The fully qualified class name of the Hibernate <tt>CacheProvider</tt> implementation. For Hibernate 2.1+ only.
     *
     * @param cacheProvider
     * @ant.not-required
     */
    public void setCacheProvider(String cacheProvider)
    {
        _cacheProvider = cacheProvider;
    }

    /**
     * The complete name of the data source service name that this service depends on.
     *
     * @param depends
     * @ant.not-required
     */
    public void setDepends(String depends)
    {
        _depends = depends;
    }

    /**
     * Use this user name to login to the database
     *
     * @param userName
     * @ant.not-required
     */
    public void setUserName(String userName)
    {
        _userName = userName;
    }

    /**
     * Use this password to login to the database
     *
     * @param password
     * @ant.not-required
     */
    public void setPassword(String password)
    {
        _password = password;
    }

    /**
     * Whether to use outer join
     *
     * @param useOuterJoin
     * @ant.not-required    No. Defaults to false.
     */
    public void setUseOuterJoin(boolean useOuterJoin)
    {
        _useOuterJoin = useOuterJoin;
    }

    /**
     * Log sql statements.
     *
     * @param showSql
     * @ant.not-required   No. Defaults to false.
     */
    public void setShowSql(boolean showSql)
    {
        _showSql = showSql;
    }

    /**
     * @param serviceName
     * @ant.required
     */
    public void setServiceName(String serviceName)
    {
        _serviceName = serviceName;
    }

    /**
     * JNDI name to bind to the <tt>SessionFactory</tt>
     *
     * @param jndiName
     * @ant.required
     */
    public void setJndiName(String jndiName)
    {
        _jndiName = jndiName;
    }

    /**
     * JNDI name of data source to use in the session factory.
     *
     * @param dataSource
     * @ant.required
     */
    public void setDataSource(String dataSource)
    {
        _dataSource = dataSource;
    }

    /**
     * SQL <a href="http://www.hibernate.org/hib_docs/api/net/sf/hibernate/dialect/Dialect.html">dialect</a> of the
     * database.
     *
     * @param dialect
     * @ant.required   Yes. Use fully-qualified class name.
     */
    public void setDialect(String dialect)
    {
        _dialect = dialect;
    }

    /**
     * Only for Hibernate 2.1+
     *
     * @param string
     * @ant.not-required
     */
    public void setDefaultSchema(String string)
    {
        _defaultSchema = string;
    }

    /**
     * Only for Hibernate 2.1+
     *
     * @param string
     * @ant.not-required
     */
    public void setJdbcFetchSize(String string)
    {
        _jdbcFetchSize = string;
    }

    /**
     * Only for Hibernate 2.1+
     *
     * @param string
     * @ant.not-required
     */
    public void setMaxFetchDepth(String string)
    {
        _maxFetchDepth = string;
    }

    /**
     * Only for Hibernate 2.1+
     *
     * @param string
     * @ant.not-required
     */
    public void setQuerySubstitutions(String string)
    {
        _querySubstitutions = string;
    }

    /**
     * Only for Hibernate 2.1+
     *
     * @param string
     * @ant.not-required
     */
    public void setUseQueryCache(String string)
    {
        _useQueryCache = string;
    }

    /**
     * Set the Hibernate version that the jboss service should be generated for. Values are "1.1", "2.0" and "2.1". This
     * value normally corresponds to the setting of the <tt>hibernate</tt> subtask.
     * Default is "2.1" !
     * @param version
     * @ant.not-required
     */
    public void setVersion(String version)
    {
        _version = version;
    }


    public void validateOptions() throws XDocletException
    {
        super.validateOptions();

        if (getJndiName() == null) {
            throw new XDocletException(Translator.getString(XDocletModulesHibernateMessages.class,
                XDocletModulesHibernateMessages.JNDI_NAME_REQUIRED));
        }

        if (getServiceName() == null) {
            throw new XDocletException(Translator.getString(XDocletModulesHibernateMessages.class,
                XDocletModulesHibernateMessages.SERVICE_NAME_REQUIRED));
        }

        if (getDialect() == null) {
            throw new XDocletException(Translator.getString(XDocletModulesHibernateMessages.class,
                XDocletModulesHibernateMessages.SQL_DIALECT_REQUIRED));
        }
        if (getDataSource() == null) {
            throw new XDocletException(Translator.getString(XDocletModulesHibernateMessages.class,
                XDocletModulesHibernateMessages.DATA_SOURCE_REQUIRED));
        }
    }

    /**
     * Called when the engine is started
     *
     * @exception XDocletException  Thrown in case of problem
     */
    protected void engineStarted() throws XDocletException
    {
        System.out.println(Translator.getString(XDocletModulesHibernateMessages.class,
            XDocletModulesHibernateMessages.GENERATING_JBOSS_SERVICE_DESCRIPTOR));
    }
}