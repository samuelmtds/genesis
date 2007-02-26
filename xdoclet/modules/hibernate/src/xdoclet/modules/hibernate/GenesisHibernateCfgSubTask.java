/*
 * Copyright (c) 2001, 2002 The XDoclet team
 * All rights reserved.
 */
package xdoclet.modules.hibernate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import xdoclet.DocletContext;
import xdoclet.SubTask;
import xdoclet.XDocletException;

/**
 * Generate the hibernate.cfg.xml file. It lists all of the properties as well
 * as a property for each hbm.xml file. This file can be used for creating and
 * installing a SessionFactory in JNDI as well as launching Hibern8IDE.
 * 
 * @author <a href="mailto:fbrier at users.sourceforge.net">Frederick N. Brier</a>
 * @author <a href="mailto:dchannon at users.sourceforge.net">David Channon</a>
 * @created February 6, 2004
 * @version $Revision: 1.1 $
 * @ant.element name="genesishibernatecfg" display-name="Hibernate Configuration
 *              File Generation"
 *              parent="xdoclet.modules.hibernate.HibernateDocletTask"
 */
public class GenesisHibernateCfgSubTask extends HibernateCfgSubTask {
   private static final String SUBTASK_NAME = "genesishibernatecfg";

   // SORTED
   private static final String[] REMOTE_PROPERTIES = new String[] {
         SUBTASK_NAME + ".driver", SUBTASK_NAME + ".jdbcurl",
         SUBTASK_NAME + ".password", SUBTASK_NAME + ".poolsize",
         SUBTASK_NAME + ".username" };

   // SORTED
   private static final String[] LOCAL_PROPERTIES = new String[] {
         SUBTASK_NAME + ".datasource",
         SUBTASK_NAME + ".transactionmanagerlookup",
         SUBTASK_NAME + ".transactionmanagerstrategy",
         SUBTASK_NAME + ".usertransactionname" };

   private static final String TRANSACTION_MANAGER_FACTORY = SUBTASK_NAME
         + ".transactionmanagerfactory";

   private String localTransactionManagerFactory;
   private String remoteTransactionManagerFactory;
   private String localDestinationFile;
   private String remoteDestinationFile;
   private boolean localMode;
   private boolean remoteMode;

   public GenesisHibernateCfgSubTask() {
      setSubTaskName(SUBTASK_NAME);

      if (getTemplateURL() == null) {
         setTemplateURL(getClass().getSuperclass().getResource("resources/hibernate-cfg.xdt"));
      }
   }
   
   public boolean isLocalMode() {
      return localMode;
   }

   public void setLocalMode(boolean localMode) {
      this.localMode = localMode;
   }

   public boolean isRemoteMode() {
      return remoteMode;
   }

   public void setRemoteMode(boolean remoteMode) {
      this.remoteMode = remoteMode;
   }

   public String getLocalDestinationFile() {
      return localDestinationFile;
   }

   public void setLocalDestinationFile(String localDestinationFile) {
      this.localDestinationFile = localDestinationFile;
   }

   public String getLocalTransactionManagerFactory() {
      return localTransactionManagerFactory;
   }

   public void setLocalTransactionManagerFactory(
         String localTransactionManagerFactory) {
      this.localTransactionManagerFactory = localTransactionManagerFactory;
   }

   public String getRemoteDestinationFile() {
      return remoteDestinationFile;
   }

   public void setRemoteDestinationFile(String remoteDestinationFile) {
      this.remoteDestinationFile = remoteDestinationFile;
   }

   public String getRemoteTransactionManagerFactory() {
      return remoteTransactionManagerFactory;
   }

   public void setRemoteTransactionManagerFactory(
         String remoteTransactionManagerFactory) {
      this.remoteTransactionManagerFactory = remoteTransactionManagerFactory;
   }

   public void execute() throws XDocletException {
      final DocletContext ctx = getContext();

      try {
         if (remoteMode) {
            DocletContext.setSingleInstance(new WrapperContext(ctx, true));
            
            setDestinationFile(getRemoteDestinationFile());
            
            super.execute();
         }

         if (localMode) {
            DocletContext.setSingleInstance(new WrapperContext(ctx, false));
            
            setDestinationFile(getLocalDestinationFile());
            
            super.execute();
         }
      } finally {
         DocletContext.setSingleInstance(ctx);
      }
   }

   public class WrapperContext extends DocletContext {
      private final DocletContext original;
      private final boolean remote;

      public WrapperContext(DocletContext original, boolean remote) {
         super(original.getDestDir(), original.getMergeDir(), original
               .getExcludedTags(), original.getSubTasks(), (Hashtable) null, (HashMap) null, original.isForce(), original
               .isVerbose(), original.getAddedTags());
         this.original = original;
         this.remote = remote;
      }

      public SubTask getActiveSubTask() {
         return original.getActiveSubTask();
      }

      public Map getProperties() {
         return original.getProperties();
      }

      public String getProperty(String name) {
         return original.getProperty(name);
      }

      public Object getConfigParam(String name) {
         name = name.toLowerCase();
         if (remote) {
            if (TRANSACTION_MANAGER_FACTORY.equals(name)) {
               return getRemoteTransactionManagerFactory();
            }

            if (Arrays.binarySearch(REMOTE_PROPERTIES, name) > -1) {
               return null;
            }

            return original.getConfigParam(name);
         }

         if (TRANSACTION_MANAGER_FACTORY.equals(name)) {
            return getLocalTransactionManagerFactory();
         }

         if (Arrays.binarySearch(LOCAL_PROPERTIES, name) > -1) {
            return null;
         }

         return original.getConfigParam(name);
      }
   }
}
