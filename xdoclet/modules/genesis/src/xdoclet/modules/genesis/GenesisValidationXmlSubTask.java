/*
 * Copyright (c) 2001, 2002 The XDoclet team
 * All rights reserved.
 */
package xdoclet.modules.genesis;

import xdoclet.XDocletException;
import xdoclet.XmlSubTask;

/**
 * Generates genesis Validator validation.xml deployment descriptor.
 *
 * @author               Allan Jones
 * @created              August 17, 2005
 * @ant.element          display-name="validation.xml" name="genesisvalidationxml"
 *      parent="xdoclet.modules.genesis.GenesisDocletTask"
 * @version              $Revision: 1.1.2.1 $
 * @xdoclet.merge-file   file="validation-global.xml" relates-to="validation.xml" description="An XML unparsed entity
 *      containing the global elements for the validation descriptor."
 */
public class GenesisValidationXmlSubTask extends XmlSubTask
{
    protected final static String DTD_FILE_NAME_11 = "resources/validation_1_1.dtd";
    protected final static String VALIDATION_PUBLICID_11 = "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.1//EN";
    protected final static String VALIDATION_SYSTEMID_11 = "http://jakarta.apache.org/commons/dtds/validator_1_1.dtd";

    protected final static String DTD_FILE_NAME_113 = "resources/validation_1_1_3.dtd";
    protected final static String VALIDATION_PUBLICID_113 = "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.1.3//EN";
    protected final static String VALIDATION_SYSTEMID_113 = "http://jakarta.apache.org/commons/dtds/validator_1_1_3.dtd";
    protected static String GENERATED_FILE_NAME = "validation.xml";

    protected String version = "1.1.3";

    /**
     * Creates a new validation task instance.
     */
    public GenesisValidationXmlSubTask()
    {
        setTemplateURL(getClass().getResource(getTemplateFile()));
        setDestinationFile(GENERATED_FILE_NAME);
    }

    /**
     * Gets the Version attribute of the task tag.
     *
     * @return   The Version value
     */
    public String getVersion()
    {
        return this.version;
    }

    /**
     * Sets the validator version to use. Legal values are "1.1" and "1.1.3".
     *
     * @param version
     * @ant.not-required   No. Default is "1.1".
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    /**
     * Describe what the method does
     *
     * @exception XDocletException
     */
    public void execute() throws XDocletException
    {
        if (version.equals("1.1")) {
            setPublicId(VALIDATION_PUBLICID_11);
            setSystemId(VALIDATION_SYSTEMID_11);
            setDtdURL(getClass().getResource(DTD_FILE_NAME_11));
        }
        else {
            setPublicId(VALIDATION_PUBLICID_113);
            setSystemId(VALIDATION_SYSTEMID_113);
            setDtdURL(getClass().getResource(DTD_FILE_NAME_113));
        }
        startProcess();
    }

    protected String getTemplateFile()
    {
        return "resources/validation_xml.xdt";
    }
}
