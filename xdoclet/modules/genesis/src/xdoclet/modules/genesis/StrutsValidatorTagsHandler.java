/*
 * Copyright (c) 2001, 2002 The XDoclet team
 * All rights reserved.
 */
package xdoclet.modules.genesis;

/**
 * genesis Validator tag handler tags (for backwards compatibility)
 *
 * @author               Allan Jones
 * @created              March 17, 2005
 * @xdoclet.taghandler   namespace="GenesisStrutsValidator"
 * @version              $Revision: 1.1.2.1 $
 */
public class StrutsValidatorTagsHandler extends GenesisValidatorTagsHandler
{
    protected String getValidatorTagName()
    {
        return "struts.validator";
    }

    protected String getValidatorArgsTagName()
    {
        return "struts.validator-args";
    }
}
