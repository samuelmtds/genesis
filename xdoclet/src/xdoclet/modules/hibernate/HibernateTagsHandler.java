/*
 * Copyright (c) 2001, 2002 The XDoclet team
 * All rights reserved.
 */
package xdoclet.modules.hibernate;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.tools.ant.types.Parameter;

import xjavadoc.XClass;
import xjavadoc.XMethod;
import xdoclet.DocletContext;
import xdoclet.DocletSupport;
import xdoclet.DocletTask;
import xdoclet.XDocletException;
import xdoclet.XDocletTagSupport;
import xdoclet.tagshandler.ClassTagsHandler;
import xdoclet.tagshandler.TypeTagsHandler;
import xdoclet.util.LogUtil;
import xdoclet.util.Translator;
/**
 * Specific tags handler to make the template easy.
 *
 * @author               Sébastien Guimont (sebastieng@sympatico.ca)
 * @created              August 9th, 2002
 * @version              $Revision: 1.1 $
 * @xdoclet.taghandler   namespace="Hibernate"
 */
public class HibernateTagsHandler
     extends XDocletTagSupport
{

    private String  currentTag;

    private String  currentMappingElement;

    private Parameter currentJndiParameter;

    /**
     * Returns full path of hibernate file for the current class.
     *
     * @return                      The full file path of the current class.
     * @exception XDocletException
     * @doc.tag                     type="content"
     */
    public String getFileName()
         throws XDocletException
    {
        return getCurrentClass().getQualifiedName().replace('.', '/');
    }

    public JBossServiceSubTask getJBossServiceSubTask()
    {
        return (JBossServiceSubTask) (DocletContext.getInstance().getSubTaskBy(DocletTask.getSubTaskName(JBossServiceSubTask.class)));
    }

    public FactoryClassSubTask getFactoryClassSubTask()
    {
        return (FactoryClassSubTask) (DocletContext.getInstance().getSubTaskBy(DocletTask.getSubTaskName(FactoryClassSubTask.class)));
    }

    public HibernateProperties getHibernateProperties() throws XDocletException
    {
        try {
            return (HibernateProperties) DocletContext.getInstance().getActiveSubTask();
        }
        catch (ClassCastException e) {
            throw new XDocletException(e, "May occur if attribute is used with incorrect subtask.");
        }
    }

    /**
     * find id property of current class.
     *
     * @return
     * @exception XDocletException
     */
    public XMethod getIdMethod() throws XDocletException
    {
        XClass clazz = getCurrentClass();
        Iterator methodIterator = clazz.getMethods(true).iterator();

        // iterate through all the methods defined in this class
        XMethod method;

        while (methodIterator.hasNext()) {
            method = (XMethod) methodIterator.next();

            if (method.getDoc().hasTag("hibernate.id")) {
                return method;
            }
        }
        return null;
    }

    public String getCurrentTag(Properties attributes)
    {
        return currentTag;
    }

    public String getCurrentMappingElement(Properties attributes)
    {
        return currentMappingElement;
    }

    public void setCurrentTag(String template, Properties attributes) throws XDocletException
    {
        currentTag = attributes.getProperty("name");
        currentMappingElement = attributes.getProperty("mappingElement");
        generate(template);
        currentTag = null;
        currentMappingElement = null;
    }

    /**
     * Get the attribute used for collection property names in this version of Hibernate (ie. "role" or "name").
     *
     * @param attributes
     * @return
     */
    public String roleAttribute(Properties attributes)
    {
        return "1.1".equals(getHibernateSubTask().getVersion()) ? "role" : "name";
    }

    /**
     * Get the name of the class the implements the SessionFactory as a MBean is this version of Hibernate.
     *
     * @param attributes
     * @return
     */
    public String serviceClassName(Properties attributes)
    {
        if ("1.1".equals(getHibernateSubTask().getVersion()))
            return "cirrus.hibernate.jmx.HibernateService";
        else
            return "net.sf.hibernate.jmx.HibernateService";
    }

    /**
     * Render template if ID is composite.
     *
     * @param template
     * @param attributes
     * @exception XDocletException
     * @doc.tag                     type="block"
     */
    public void ifHasCompositeId(String template, Properties attributes) throws XDocletException
    {
        hasCompositeId_Impl(template, true);
    }

    /**
     * Render template if id is primitive.
     *
     * @param template
     * @param attributes
     * @exception XDocletException
     * @doc.tag                     type="block"
     */
    public void ifHasPrimitiveId(String template, Properties attributes) throws XDocletException
    {
        hasCompositeId_Impl(template, false);
    }

    /**
     * Return configured service name.
     *
     * @return
     * @exception XDocletException
     * @doc.tag                     type="content"
     */
    public String serviceName() throws XDocletException
    {
        return getJBossServiceSubTask().getServiceName();
    }

    /**
     * Configured JNDI name.
     *
     * @return
     * @exception XDocletException
     * @doc.tag                     type="content"
     */
    public String jndiName() throws XDocletException
    {
        return getHibernateProperties().getJndiName();
    }

    /**
     * Render template if jndiName of JBossServiceSubtask is valid. This is a required parameter, but the user might not
     * be using that subtask.
     *
     * @param template
     * @param attributes
     * @exception XDocletException
     * @doc.tag                     type="block"
     */
    public void ifUseJndiFactory(String template, Properties attributes) throws XDocletException
    {
        if (getFactoryClassSubTask().isUseJndiFactory()) {
            generate(template);
        }
    }

    /**
     * Render template if jndiName of JBossServiceSubtask is valid. This is a required parameter, but the user might not
     * be using that subtask.
     *
     * @param template
     * @param attributes
     * @exception XDocletException
     * @doc.tag                     type="block"
     */
    public void ifNotUseJndiFactory(String template, Properties attributes) throws XDocletException
    {
        if (!getFactoryClassSubTask().isUseJndiFactory()) {
            generate(template);
        }
    }

    /**
     * Render template if jndiName of JBossServiceSubtask is valid. This is a required parameter, but the user might not
     * be using that subtask.
     *
     * @param template
     * @param attributes
     * @exception XDocletException
     * @doc.tag                     type="block"
     */
    public void ifHasJndiName(String template, Properties attributes) throws XDocletException
    {
        if (jndiName() != null) {
            generate(template);
        }
    }

    /**
     * Render template if all of the properties needed are valid.
     *
     * @param template
     * @param attributes
     * @exception XDocletException
     * @doc.tag                     type="block"
     */
    public void ifGeneratePropertyCache(String template, Properties attributes) throws XDocletException
    {
        if (dialect() != null
            && driver() != null
            && dataSource() != null
            && userName() != null
            && password() != null) {
            generate(template);
        }
        if (dialect() != null && jndiName() != null) {
            generate(template);
        }
    }

    /**
     * Render template if jndiName of JBossServiceSubtask is not valid.
     *
     * @param template
     * @param attributes
     * @exception XDocletException
     * @doc.tag                     type="block"
     */
    public void ifNotHasJndiName(String template, Properties attributes) throws XDocletException
    {
        if (jndiName() == null) {
            generate(template);
        }
    }

    /**
     * SQL dialect extractor.
     *
     * @return
     * @exception XDocletException
     * @doc.tag                     type="content"
     */

    public String dialect() throws XDocletException
    {
        return getHibernateProperties().getDialect();
    }

    /**
     * Data source JNDI Name extractor.
     *
     * @return
     * @exception XDocletException
     * @doc.tag                     type="content"
     */
    public String dataSource() throws XDocletException
    {
        return getHibernateProperties().getDataSource();
    }

    /**
     * Driver Name extractor.
     *
     * @return
     * @exception XDocletException
     * @doc.tag                     type="content"
     */
    public String driver() throws XDocletException
    {
        return getHibernateProperties().getDriver();
    }

    /**
     * JDBC URL extractor.
     *
     * @return
     * @exception XDocletException
     * @doc.tag                     type="content"
     */
    public String jdbcUrl() throws XDocletException
    {
        return getHibernateProperties().getJdbcUrl();
    }

    /**
     * username extractor.
     *
     * @return
     * @exception XDocletException
     * @doc.tag                     type="content"
     */
    public String userName() throws XDocletException
    {
        return getHibernateProperties().getUserName();
    }

    /**
     * password extractor.
     *
     * @return
     * @exception XDocletException
     * @doc.tag                     type="content"
     */
    public String password() throws XDocletException
    {
        return getHibernateProperties().getPassword();
    }

    /**
     * poolSize extractor.
     *
     * @return
     * @exception XDocletException
     * @doc.tag                     type="content"
     */
    public String poolSize() throws XDocletException
    {
        return getHibernateProperties().getPoolSize();
    }

    /**
     * classname extractor.
     *
     * @return
     * @exception XDocletException
     * @doc.tag                     type="content"
     */
    public String factoryClass() throws XDocletException
    {
        return getFactoryClassSubTask().getFactoryClass();
    }

    /**
     * Comma separated list of hibernate mappings.
     *
     * @return
     * @exception XDocletException
     * @doc.tag                     type="content"
     */
    public String mappingList() throws XDocletException
    {
        String mappingName;
        StringBuffer sb = new StringBuffer();
        Collection classes = ClassTagsHandler.getAllClasses();
        XClass clazz;

        for (Iterator i = classes.iterator(); i.hasNext(); ) {
            clazz = (XClass) i.next();

            if (clazz.getDoc().hasTag("hibernate.class", false)) {
                mappingName = getHibernateSubTask().getMappingURL(clazz);
                sb.append(mappingName);
                sb.append(",");
            }
        }
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - 1);
        }
        else {
            return "";
        }
    }

    /**
     * Print the name of the current class to the console.
     *
     * @param attributes
     * @exception XDocletException
     * @doc.tag                     type="content"
     */
    public void logMapping(Properties attributes) throws XDocletException
    {
        System.out.println("   " + getCurrentClass().getQualifiedName());
    }


    /**
     * Iterates over all classes marked as persistent.
     *
     * @param template              The body of the block tag
     * @param attributes            The attributes of the template tag
     * @exception XDocletException  Description of Exception
     * @doc.tag                     type="block"
     */
    public void forAllPersistentClasses(String template, Properties attributes) throws XDocletException
    {
        Collection classes = ClassTagsHandler.getAllClasses();
        XClass clazz;

        for (Iterator i = classes.iterator(); i.hasNext(); ) {
            clazz = (XClass) i.next();

            if (clazz.getDoc().hasTag("hibernate.class", false)) {
                pushCurrentClass(clazz);
                generate(template);
                popCurrentClass();
            }
        }
    }

    /**
     * Iterates over all jndiProperties specified.
     *
     * @param template              The body of the block tag
     * @param attributes            The attributes of the template tag
     * @exception XDocletException  Description of Exception
     * @doc.tag                     type="block"
     */
    public void forAllJndiProperties(String template, Properties attributes) throws XDocletException
    {
        Collection properties = getHibernateProperties().getJndiProperties();

        for (Iterator i = properties.iterator(); i.hasNext(); ) {
            currentJndiParameter = (Parameter) i.next();
            generate(template);
            currentJndiParameter = null;
        }
    }

    public String jndiParameterName()
    {
        return currentJndiParameter.getName();
    }

    public String jndiParameterValue()
    {
        return currentJndiParameter.getValue();
    }

    /**
     * Iterates over all classes loaded by javadoc that are direct subclasses of the current class and evaluates the
     * body of the tag for each class. It discards classes that have an xdoclet-generated class tag defined.
     *
     * @param template              The body of the block tag
     * @param attributes            The attributes of the template tag
     * @exception XDocletException  Description of Exception
     * @doc.tag                     type="block"
     */
    public void forAllSubclasses(String template, Properties attributes) throws XDocletException
    {

        Log log = LogUtil.getLog(HibernateTagsHandler.class, "forAllSubclasses");

        try {

            String typeName = getCurrentClass().getQualifiedName();

            if (log.isDebugEnabled())
                log.debug("typeName=" + typeName);

            Collection classes = getXJavaDoc().getSourceClasses();
            XClass clazz;

            for (Iterator i = classes.iterator(); i.hasNext(); ) {
                clazz = (XClass) i.next();

                log.debug("clazz=" + clazz);

                if (DocletSupport.isDocletGenerated(clazz)) {
                    log.debug("isDocletGenerated");
                }
                else if (clazz.getSuperclass() != null && clazz.getSuperclass().getQualifiedName().equals(typeName)) {
                    log.debug("is a subclass");

                    XClass current = getCurrentClass();

                    pushCurrentClass(clazz);
                    generate(template);
                    popCurrentClass();

                    if (getCurrentClass() != current)
                        setCurrentClass(current);
                    //TODO: why do we need this?!
                }
                else {
                    log.debug("is not a subclass");
                }
            }
        }
        catch (Exception e) {
            log.error("exception occurred", e);
        }
    }

    /**
     * Actual templating for composite/primitive IDs.
     *
     * @param template
     * @param composite
     * @exception XDocletException
     */
    void hasCompositeId_Impl(String template, boolean composite) throws XDocletException
    {
        XClass oldClass = getCurrentClass();

        XMethod method = getIdMethod();

        // bomb politely if no ID method could be found
        if (method == null) {
            throw new XDocletException(
                Translator.getString(XDocletModulesHibernateMessages.class,
                XDocletModulesHibernateMessages.NO_ID_PROPERTY,
                new String[]{getCurrentClass().getQualifiedName()}));
        }

        // Determine whether or not the ID is a user-defined type.
        // If it is then it is not a composite id.
        boolean isUserType = false;

        String typeStr = method.getDoc().getTagAttributeValue("hibernate.id", "type");

        if (typeStr != null) {
            // The type attribute was supplied, so check
            // whether it implements cirrus.hibernate.UserType
            XClass typeClass = getXJavaDoc().getXClass(typeStr);

            if (typeClass != null) {
                isUserType = typeClass.isA("cirrus.hibernate.UserType") || typeClass.isA("net.sf.hibernate.UserType");
            }
        }

        // decide whether we have composite or primitive ID
        String type = method.getReturnType().getType().getQualifiedName();
        boolean isPrimitive = TypeTagsHandler.isPrimitiveType(type) ||
            "java.lang.Byte".equals(type) ||
            "java.lang.Double".equals(type) ||
            "java.lang.Float".equals(type) ||
            "java.lang.Integer".equals(type) ||
            "java.lang.Long".equals(type) ||
            "java.lang.Short".equals(type) ||
            "java.lang.String".equals(type) ||
            "java.math.BigDecimal".equals(type) ||
            isUserType;

        if (isPrimitive && !composite) {
            setCurrentMethod(method);
            generate(template);
        }

        if (composite && !isPrimitive) {
            // check whether specified type satisfies us
            // it has to be serializable,
            // and implement equals itself.
            // bomb if not.
            XClass returnType = method.getReturnType().getType();

            if (returnType.isA("java.io.Serializable") && !returnType.isAbstract() &&
                !"java.lang.Object".equals(returnType.getMethod("equals(java.lang.Object)", true).getContainingClass().getQualifiedName())) {
                setCurrentMethod(method);
                generate(template);

            }
            else {
                // bomb politely that given property does not qualify as composite ID
                throw new XDocletException(
                    Translator.getString(XDocletModulesHibernateMessages.class,
                    XDocletModulesHibernateMessages.WRONG_COMPOSITE_ID,
                    new String[]{returnType.getQualifiedName()}));
            }
        }

        if (getCurrentClass() != oldClass)
            setCurrentClass(oldClass);
        //TODO: Why do we need this!!??
    }

    private HibernateSubTask getHibernateSubTask()
    {
        return ((HibernateSubTask) (DocletContext.getInstance()
            .getSubTaskBy(DocletTask.getSubTaskName(HibernateSubTask.class))));
    }

}