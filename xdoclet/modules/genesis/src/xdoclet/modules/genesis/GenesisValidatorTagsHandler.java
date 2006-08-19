/*
 * Copyright (c) 2001, 2002 The XDoclet team
 * All rights reserved.
 */
package xdoclet.modules.genesis;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.SequencedHashMap;

import xdoclet.XDocletException;
import xdoclet.tagshandler.AbstractProgramElementTagsHandler;
import xdoclet.tagshandler.MethodTagsHandler;
import xdoclet.util.Translator;
import xjavadoc.XClass;
import xjavadoc.XMethod;
import xjavadoc.XParameter;
import xjavadoc.XTag;

/**
 * genesis Validator tag handler tags
 *
 * @author               Allan Jones
 * @created              March 17, 2005
 * @xdoclet.taghandler   namespace="GenesisValidator"
 * @version              $Revision: 1.2 $
 */
public class GenesisValidatorTagsHandler extends AbstractProgramElementTagsHandler
{
    protected String curFieldName;
    protected String currentArgKey;
    protected Map   args;

    /**
     * Iterates over all arguments for the current field.
     *
     * @param template           The body of the block tag
     * @param attributes         The attributes of the template tag
     * @throws XDocletException
     * @doc.tag                  type="block"
     */
    public void forAllFieldArgs(String template, Properties attributes) throws XDocletException
    {
        for (Iterator iterator = args.keySet().iterator(); iterator.hasNext(); ) {
            currentArgKey = (String) iterator.next();
            generate(template);
        }
    }

    /**
     * Current argument index number (0 to 3).
     *
     * @param props  The content tag attributes.
     * @return       current argument index
     * @doc.tag      type="content"
     */
    public String argIndex(Properties props)
    {
        return currentArgKey.charAt(3) + "";
    }

    /**
     * Current argument name - only valid if argument is for a specific validator type.
     *
     * @param props  The content tag attributes.
     * @return       current argument name
     * @doc.tag      type="content"
     */
    public String argName(Properties props)
    {
        String name = currentArgKey.substring(currentArgKey.indexOf('_') + 1);

        return name;
    }

    /**
     * Current argument value, which is either an inline value or resource key.
     *
     * @param props  The content tag attributes.
     * @return       current argument value
     * @doc.tag      type="content"
     */
    public String argValue(Properties props)
    {
        return (String) args.get(currentArgKey);
    }

    /**
     * Evaluates body if current argument is a resource key.
     *
     * @param template           The body of the block tag
     * @param attributes         The attributes of the template tag
     * @throws XDocletException
     * @doc.tag                  type="block"
     */
    public void ifArgIsResource(String template, Properties attributes) throws XDocletException
    {
        if (currentArgKey.indexOf("resource") > 0) {
            generate(template);
        }
    }

    /**
     * Evaluates the body if the current argument is an inline value rather than a resource key.
     *
     * @param template           The body of the block tag
     * @param attributes         The attributes of the template tag
     * @throws XDocletException
     * @doc.tag                  type="block"
     */
    public void ifArgIsValue(String template, Properties attributes) throws XDocletException
    {
        if (currentArgKey.indexOf("value") > 0) {
            generate(template);
        }
    }

    /**
     * Evaluates the body if the current argument is a validator-specific argument.
     *
     * @param template           The body of the block tag
     * @param attributes         The attributes of the template tag
     * @throws XDocletException
     * @doc.tag                  type="block"
     */
    public void ifArgIsForType(String template, Properties attributes) throws XDocletException
    {
        if (currentArgKey.indexOf('_') > 0) {
            generate(template);
        }
    }

    /**
     * Evaluates the body if form has fields requiring validation.
     *
     * @param template           The body of the block tag
     * @param attributes         The attributes of the template tag
     * @throws XDocletException
     * @doc.tag                  type="block"
     */
    public void ifFormHasFields(String template, Properties attributes) throws XDocletException
    {
        if (getFields(getCurrentClass()).size() > 0) {
            generate(template);
        }
    }

    /**
     * Evaluates the body if the current field has an indexed component.
     *
     * @param template           The body of the block tag
     * @param attributes         The attributes of the template tag
     * @throws XDocletException
     * @doc.tag                  type="block"
     */
    public void ifFieldIsIndexed(String template, Properties attributes) throws XDocletException
    {
        if (curFieldName.indexOf("[]") >= 0) {
            generate(template);
        }
    }

    /**
     * Iterates the body for each field of the current form requiring validation.
     *
     * @param template           The body of the block tag
     * @param attributes         The attributes of the template tag
     * @throws XDocletException
     * @doc.tag                  type="block"
     */
    public void forAllFields(String template, Properties attributes) throws XDocletException
    {
        XClass clazz = getCurrentClass();
        Map setters = getFields(clazz);

        for (Iterator iterator = setters.keySet().iterator(); iterator.hasNext(); ) {
            curFieldName = (String) iterator.next();

            XMethod field = (XMethod) setters.get(curFieldName);

            setCurrentMethod(field);
            loadFieldArguments();
            generate(template);
        }
    }

    /**
     * Returns the current field's name, local to the indexed property if there is one.
     *
     * @param props  The content tag attributes.
     * @return       current field name
     * @doc.tag      type="content"
     */
    public String fieldName(Properties props)
    {
        int listIdx = curFieldName.indexOf("[]");

        if (listIdx == -1)
            return curFieldName;

        String result = curFieldName.substring(listIdx + 3);

        if (result.length() == 0)
            return curFieldName.substring(0, listIdx);

        return result;
    }

    /**
     * Returns the current field's indexedListProperty attribute, if any.
     *
     * @param props  The content tag attributes.
     * @return       current field's indexedListProperty
     * @doc.tag      type="content"
     */
    public String indexedListProperty(Properties props)
    {
        int listIdx = curFieldName.indexOf("[]");

        if (listIdx == -1)
            return "";

        return curFieldName.substring(0, listIdx);
    }

    /**
     * Returns a comma-separated list of the specified validator types.
     *
     * @param props  The content tag attributes.
     * @return       validator types list
     * @doc.tag      type="content"
     */
    public String validatorList(Properties props)
    {
        XMethod method = getCurrentMethod();
        Collection tags = method.getDoc().getTags(getValidatorTagName());
        StringBuffer buffer = new StringBuffer();

        for (Iterator iterator = tags.iterator(); iterator.hasNext(); ) {
            XTag tag = (XTag) iterator.next();

            buffer.append(tag.getAttributeValue("type"));
            if (iterator.hasNext()) {
                buffer.append(",");
            }
        }
        return buffer.toString();
    }

    protected String getValidatorTagName()
    {
        return "genesis.validator";
    }

    protected String getValidatorArgsTagName()
    {
        return "genesis.validator-args";
    }

    protected Map getFields(XClass clazz) throws XDocletException
    {
        return getFields(clazz, "");
    }

    protected Map getFields(XClass clazz, String prefix) throws XDocletException
    {
        Map fields = new SequencedHashMap();

        Collection curFields = clazz.getMethods(true);

        // TODO: nested forms currently won't work unless
        // there is a setter for it, but that is not needed
        // as only the sub-forms must have setters.  The top-level
        // only requires a getter.
        for (Iterator iterator = curFields.iterator(); iterator.hasNext(); ) {
            XMethod method = (XMethod) iterator.next();

            if (method.getDoc().getTag(getValidatorTagName()) != null) {
                List params = method.getParameters();

                String name = MethodTagsHandler.getPropertyNameFor(method);
                XParameter param = null;

                if (MethodTagsHandler.isSetterMethod(method)) {
                    param = (XParameter) params.get(0);

                    String type = param.getType().getQualifiedName();

                    fields.put(prefix + name, method);
                }
                else if (params.size() == 2) {
                    // Check for indexed setter setBlah(int index, <type> value)
                    if (!MethodTagsHandler.isSetter(method.getName()))
                        continue;

                    Iterator paramIter = params.iterator();

                    if (!((XParameter) paramIter.next()).getType().isA("int"))
                        continue;

                    if (name.indexOf("[]") >= 0) {
                        throw new XDocletException(Translator.getString(GenesisValidatorMessages.class,
                            GenesisValidatorMessages.ONLY_ONE_LEVEL_LIST_PROPS,
                            new String[]{clazz.getName() + '.' + name + "[]"}));
                    }

                    name = name + "[]";
                    param = (XParameter) paramIter.next();

                    boolean preDot = (prefix.length() > 0 && prefix.charAt(prefix.length() - 1) != '.');

                    fields.putAll(getFields(param.getType(), prefix + (preDot ? "." : "") + name + "."));
                }
                else
                    continue;
            }
        }

        return fields;
    }

    protected void loadFieldArguments()
    {
        /*
         * A bit of explanation is due here.  Rather than come up with
         * some fancy data structure to keep validator arguments stored into,
         * I simply store them into a Map with the value being the value
         * from the parameter.  The name, however, represents the argument
         * index, whether it's a resource or an inline value, and whether it
         * is attached to a particular validator type.  The name format is:
         * argN[resource|value][_TYPE]
         * the argN[resource|value] piece is the actual parameter name.
         * N is the argument index.
         * TYPE is added only if the parameter appears on genesis.validator tag
         * which indicates it's only associated with a specific validator type.
         */
        args = new SequencedHashMap();

        XMethod method = getCurrentMethod();

        // Collect all general args
        Collection argTags = method.getDoc().getTags(getValidatorArgsTagName());

        for (Iterator argsIterator = argTags.iterator(); argsIterator.hasNext(); ) {
            XTag tag = (XTag) argsIterator.next();
            Collection attributeNames = tag.getAttributeNames();

            for (Iterator attributesIterator = attributeNames.iterator(); attributesIterator.hasNext(); ) {
                String name = (String) attributesIterator.next();

                if (name.startsWith("arg")) {
                    args.put(name, tag.getAttributeValue(name));
                }
            }
        }

        // Collect all type-specific args
        Collection argTypeTags = method.getDoc().getTags(getValidatorTagName());

        for (Iterator typeTagsIterator = argTypeTags.iterator(); typeTagsIterator.hasNext(); ) {
            XTag tag = (XTag) typeTagsIterator.next();
            Collection attributeNames = tag.getAttributeNames();
            String type = tag.getAttributeValue("type");

            for (Iterator attributesIterator = attributeNames.iterator(); attributesIterator.hasNext(); ) {
                String name = (String) attributesIterator.next();

                if (name.startsWith("arg")) {
                    args.put(name + "_" + type, tag.getAttributeValue(name));
                }
            }
        }
    }
}
