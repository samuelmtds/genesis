/*
 * Copyright (c) 2001, 2002 The XDoclet team
 * All rights reserved.
 */
package xdoclet.modules.genesis;

/**
 * Generates genesis Validator validation.xml deployment descriptor. (for backwards compatibility)
 *
 * @author               Allan Jones
 * @created              August 17, 2005
 * @ant.element          display-name="validation.xml" name="strutsvalidationxml"
 *      parent="xdoclet.modules.genesis.GenesisDocletTask"
 * @version              $Revision: 1.1.2.1 $
 * @xdoclet.merge-file   file="validation-global.xml" relates-to="validation.xml" description="An XML unparsed entity
 *      containing the global elements for the validation descriptor."
 */
public class StrutsValidationXmlSubTask extends GenesisValidationXmlSubTask
{
    protected String getTemplateFile()
    {
        return "resources/struts_validation_xml.xdt";
    }
}
