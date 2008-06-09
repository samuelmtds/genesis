/*
 * The Genesis Project
 * Copyright (C) 2004-2008  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui;

import java.io.InputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import net.java.dev.genesis.text.FormatterRegistry;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;
import org.xml.sax.SAXException;

public final class ValidationUtils {
   private static final Log log = LogFactory.getLog(ValidationUtils.class);
   private static ValidationUtils instance;
   private final ValidatorResources resources;
   
   private ValidationUtils() throws IOException, SAXException {
      resources = initResources();
      UIUtils.getInstance().getBundle();
   }
   
   public static ValidationUtils getInstance() {
      if (instance == null) {
         try {
            instance = new ValidationUtils();
         } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
         } catch (SAXException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
         }
      }

      return instance;
   }

   private ValidatorResources initResources() throws IOException, SAXException {
      final InputStream rules = Thread.currentThread().getContextClassLoader()
                                    .getResourceAsStream("validator-rules.xml");
      final InputStream is = Thread.currentThread().getContextClassLoader()
                                       .getResourceAsStream("validation.xml");
      
      try {
         return new ValidatorResources(new InputStream[] {rules, is});
      } finally {
         if (rules != null) {
            rules.close();
         }

         if (is != null) {
            is.close();
         }
      }
   }

   public ValidatorResources getResources() {
      return resources;
   }

   public Map getMessages(ValidatorResults results, String formName) {
      return getMessages(resources, results, formName);
   }

   public Map getMessages(ValidatorResources resources, ValidatorResults results, 
         String formName) {
      final Map messages = new HashMap();
      final Form form = resources.getForm(Locale.getDefault(), formName);

      String property;
      Field field;
      ValidatorResult result;
      String actionName;
      String[] args;
      String msg;
      Arg arg;
      int argsLength;

      for (final Iterator properties = results.getPropertyNames().iterator();
                                                      properties.hasNext(); ) {
         property = properties.next().toString();
         field = form.getField(property);
         result = results.getValidatorResult(property);

         for (final Iterator actionNames = result.getActions();  
               actionNames.hasNext(); ) {
            actionName = actionNames.next().toString();

            if (result.isValid(actionName)) {
               continue;
            }

            msg = field.getMsg(actionName);

            if (msg == null) {
               msg = resources.getValidatorAction(actionName).getMsg();
            }

            msg = UIUtils.getInstance().getBundle().getString(msg);

            argsLength = field.getArgs(actionName).length;
            args = new String[argsLength];

            for (int i = 0; i < argsLength; i++) {
               arg = field.getArg(actionName, i);

               if (arg == null) {
                  break;
               }

               args[i] = arg.getKey();
               
               if (arg.isResource()) {
                  args[i] = UIUtils.getInstance().getBundle().getString(args[i]);
               }
            }

            messages.put(property, MessageFormat.format(msg, args));
            break;
         }
      }

      return messages;
   }

   /**
    * @deprecated
    */
   public boolean isValid(net.java.dev.genesis.ui.Form f) throws ValidatorException {
      try {
         return f.validate(getPropertiesMap(f)).isEmpty();
      } catch (ValidatorException ve) {
         throw ve;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public boolean isValid(Object form) throws ValidatorException {
      try {
         return validate(form, form.getClass().getName()).isEmpty();
      } catch (ValidatorException ve) {
         throw ve;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public ValidatorResults validate(Object form, String formName) 
         throws ValidatorException {
      return validate(form, formName, getResources());
   }
      
   public ValidatorResults validate(Object form, String formName, 
         ValidatorResources resources) throws ValidatorException {
      final Validator validator = new Validator(resources, formName);
      validator.setOnlyReturnErrors(true);
      validator.setUseContextClassLoader(true);

      try {
         validator.setParameter(Validator.BEAN_PARAM, getPropertiesMap(form));
      } catch (Exception e) {
         final ValidatorException validatorException = new ValidatorException();
         validatorException.initCause(e);
         throw validatorException;
      }

      return validator.validate();
   }

   public Map getPropertiesMap(Object form) throws Exception {
      if (form instanceof Map) {
         return (Map)form;
      }

      Map ret = PropertyUtils.describe(form);
      Map.Entry entry;
      for (Iterator i = ret.entrySet().iterator(); i.hasNext();) {
         entry = (Map.Entry) i.next();
         if ("class".equals(entry.getKey())) {
            i.remove();
         }
         entry.setValue(FormatterRegistry.getInstance()
               .format(entry.getValue()));
      }
      return ret;
   }
}