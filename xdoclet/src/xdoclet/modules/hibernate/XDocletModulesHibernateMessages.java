/*
 * Copyright (c) 2001, 2002 The XDoclet team
 * All rights reserved.
 */
package xdoclet.modules.hibernate;

/**
 * @author       Sébastien Guimont (sebastieng@sympatico.ca)
 * @created      August 9th, 2002
 * @version      $Revision: 1.1 $
 * @msg.bundle
 */
public final class XDocletModulesHibernateMessages
{

    //~ Instance/static variables ......................................................................................

    /**
     * @msg.bundle   msg="Generating mapping file for {0}."
     */
    public final static String GENERATING_HIBERNATE_FOR = "GENERATING_HIBERNATE_FOR";
    /**
     * @msg.bundle   msg="Generating jboss service descriptor"
     */
    public final static String GENERATING_JBOSS_SERVICE_DESCRIPTOR = "GENERATING_JBOSS_SERVICE_DESCRIPTOR";

    /**
     * @msg.bundle   msg="Generating hibernate.cfg.xml file"
     */
    public final static String GENERATING_HIBERNATE_CFG_XML = "GENERATING_HIBERNATE_CFG_XML";

    /**
     * @msg.bundle   msg="JNDI name is required for jboss service"
     */
    public final static String JNDI_NAME_REQUIRED = "JNDI_NAME_REQUIRED";
    /**
     * @msg.bundle   msg="Service name is required for jboss service"
     */
    public final static String SERVICE_NAME_REQUIRED = "SERVICE_NAME_REQUIRED";
    /**
     * @msg.bundle   msg="SQL Dialect is required for jboss service"
     */
    public final static String SQL_DIALECT_REQUIRED = "SQL_DIALECT_REQUIRED";
    /**
     * @msg.bundle   msg="Data source name is required for jboss service"
     */
    public final static String DATA_SOURCE_REQUIRED = "DATA_SOURCE_REQUIRED";

    /**
     * @msg.bundle   msg="Either a container or standalone database connection method must be specified for a
     *      hibernate.cfg.xml file."
     */
    public final static String DATA_CONNECTION_REQUIRED = "DATA_CONNECTION_REQUIRED";

    /**
     * @msg.bundle   msg="In specifying additional properties for a JNDI context, you also need to specify the JNDI name
     *      of the context."
     */
    public final static String JNDI_NAME_FOR_PROPS_REQUIRED = "JNDI_NAME_FOR_PROPS_REQUIRED";

    /**
     * @msg.bundle   msg="Class {0} misses ID property"
     */
    public static String NO_ID_PROPERTY = "NO_ID_PROPERTY";

    /**
     * @msg.bundle   msg="Composite ID property of type {0} is invalid. It has to be serializable and reimplement
     *      equals(Object)"
     */
    public static String WRONG_COMPOSITE_ID = "WRONG_COMPOSITE_ID";
    /**
     * @msg.bundle   msg="Factory name is required"
     */
    public static String FACTORY_NAME_REQUIRED = "FACTORY_NAME_REQUIRED";
    /**
     * @msg.bundle   msg="Generating factory class"
     */
    public static String GENERATING_HIBERNATE_FACTORY_CLASS = "GENERATING_HIBERNATE_FACTORY_CLASS";
}