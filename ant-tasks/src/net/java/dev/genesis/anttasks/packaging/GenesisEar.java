/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.anttasks.packaging;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.taskdefs.War;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.zip.ZipOutputStream;


/**
 * Creates a EAR archive. Based on WAR task
 *
 *
 * @since Ant 1.4
 *
 * @ant.task category="packaging"
 */
public class GenesisEar extends Jar {
   private static final FileUtils fu = FileUtils.newFileUtils();
   private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<!DOCTYPE application PUBLIC" +
      " \"-//Sun Microsystems, Inc.//DTD J2EE Application 1.3//EN\"" +
      " \"http://java.sun.com/dtd/application_1_3.dtd\">";
   private static final String APPXML_NAME = "META-INF/application.xml";
   private static final String JBOSS_APP_XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<!DOCTYPE jboss-app PUBLIC \"-//JBoss//DTD J2EE Application 1.3V2//EN\"" +
      " \"http://www.jboss.org/j2ee/dtd/jboss-app_3_2.dtd\">";
   private static final String JBOSS_APP_XML_NAME = "META-INF/jboss-app.xml";
   private static final String EJB_JAR_XML_NAME = "META-INF/ejb-jar.xml";

   private static final String HIBERNATE_FACTORY_JNDI = "jboss:/hibernate/SessionFactory";
   private static final String TRANSACTIONAL_INJECTOR = "net.java.dev.genesis.ejb.hibernate.EJBHibernateTransactionalInjector";

   private Manifest manifest;
   private String displayName;
   private String smallIcon;
   private String largeIcon;
   private String hibernatefactoryjndi;
   private String transactionalinjector;
   private List modules = new ArrayList();
   private List roles = new ArrayList();
   private List libs = new ArrayList();
   private List services = new ArrayList();

   /**
    * Create an Ear task.
    */
   public GenesisEar() {
      archiveType = "ear";
      emptyBehavior = "create";
   }

   public void setHibernatefactoryjndi(String jndi) {
      this.hibernatefactoryjndi = jndi;
   }

   public void setTransactionalinjector(String injector) {
      this.transactionalinjector = injector;
   }

   /**
    * add lib files
    */
   public void addLib(FileSet fs) {
      libs.add(fs);
      super.addFileset(fs);
   }

   /**
    * add java files
    */
   public void addJava(Module fs) {
      // We just set the prefix for this fileset, and pass it up.
      fs.getType().setValue("java");
      modules.add(fs);
      super.addFileset(fs);
   }

   /**
    * add connector files
    */
   public void addConnector(Module fs) {
      // We just set the prefix for this fileset, and pass it up.
      fs.getType().setValue("connector");
      modules.add(fs);
      super.addFileset(fs);
   }

   /**
    * add ejb files
    */
   public void addEjb(Module fs) {
      // We just set the prefix for this fileset, and pass it up.
      libs.add(fs);
      fs.getType().setValue("ejb");
      modules.add(fs);
      super.addFileset(fs);
   }

   /**
    * add war files
    */
   public void addWeb(Module fs) {
      // We just set the prefix for this fileset, and pass it up.
      fs.getType().setValue("web");
      modules.add(fs);
      super.addFileset(fs);
   }

   /**
    * add services files
    */
   public void addService(Module fs) {
      fs.getType().setValue("service");
      services.add(fs);
      super.addFileset(fs);
   }

   protected Manifest getManifest() throws BuildException {
      if (manifest != null) {
         return manifest;
      }

      try {
         Manifest manifest = Manifest.getDefaultManifest();
         String classpath = getFlattenClasspath();

         log("Using class-path: " + classpath, Project.MSG_DEBUG);

         manifest.addConfiguredAttribute(new Manifest.Attribute("Class-Path",
               classpath));

         return manifest;
      } catch (ManifestException e) {
         log("Manifest is invalid: " + e.getMessage(), Project.MSG_ERR);
         throw new BuildException("Invalid Manifest", e, getLocation());
      }
   }

   protected String getFlattenClasspath() {
      StringBuffer buffer = new StringBuffer();
      
      FileSet[] fs = (FileSet[]) libs.toArray(new FileSet[libs.size()]);

      Resource[][] resources = grabResources(fs);

      String name;
      for (int i = 0; i < resources.length; i++) {
         for (int j = 0; j < resources[i].length; j++) {
            name = getResourceName(fs[i], resources[i][j]);
            if (name == null) {
               continue;
            }

            if (buffer.length() > 0) {
               buffer.append(' ');
            }
            buffer.append(name);
         }
      }

      return buffer.toString();
   }

   protected String getResourceName(FileSet fs, Resource resource) {
      String name = resource.getName().replace(File.separatorChar, '/');

      if ("".equals(name)) {
         return null;
      }

      if (resource.isDirectory() && !name.endsWith("/")) {
         name = name + "/";
      }

      return name;
   }

   /**
    * Overridden from Zip class to deal with application.xml
    */
   protected void zipFile(File file, ZipOutputStream zOut, String vPath,
      int mode) throws IOException {
      // If the file being added is META-INF/application.xml, we
      // warn if it's not the one specified in the "appxml"
      // attribute - or if it's being added twice, meaning the same
      // file is specified by the "appxml" attribute and in a
      // <fileset> element.
      if (vPath.equalsIgnoreCase(APPXML_NAME)) {
         log("Warning: selected " + archiveType +
            " files include a META-INF/application.xml which will" +
            " be ignored (please use appxml attribute to " + archiveType +
            " task)", Project.MSG_WARN);
      } else {
         super.zipFile(getFile(file), zOut, vPath, mode);
      }
   }

   protected File getFile(File file) throws IOException {
      String filename = file.getName().toLowerCase();

      if (filename.endsWith(".war")) {
         return getWarWithMergedManifest(file);
      } else if (filename.endsWith(".jar")) {
         return getJarWithMergedManifest(file);
      }

      return file;
   }

   protected File getJarWithMergedManifest(final File file) throws IOException {
      final ZipFile zip = new ZipFile(file);
      final ZipEntry entry = zip.getEntry("META-INF/ejb-jar.xml");

      if (entry == null) {
         return file;
      }

      File tempDir = new File(System.getProperty("java.io.tmpdir"));
      File renamedFile = fu.createTempFile(file.getName(), ".tmp", tempDir);
      renamedFile.deleteOnExit();
      fu.copyFile(file, renamedFile);

      try {
         Jar jar = new Jar() {
            public void log(String msg) {
               // Ignore logging
            }

            protected void initZipOutputStream(ZipOutputStream zOut)
                  throws IOException, BuildException {
               super.initZipOutputStream(zOut);

               if (!file.getName().toLowerCase().startsWith("genesis-server")) {
                  return;
               }

               StringBuffer sb = updateEjbJarXmlEntries(zip, entry);
               
               if (sb == null) {
                  return;
               }

               zipDir(null, zOut, "META-INF/", ZipFileSet.DEFAULT_DIR_MODE);

               ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
               OutputStreamWriter out = new OutputStreamWriter(bufOut, "ISO-8859-1");
               PrintWriter writer = new PrintWriter(out);
               writer.println(sb.toString());
               writer.flush();

               ByteArrayInputStream bufIn = new ByteArrayInputStream(bufOut
                     .toByteArray());

               super.zipFile(bufIn, zOut, EJB_JAR_XML_NAME, System
                     .currentTimeMillis(), null, ZipFileSet.DEFAULT_FILE_MODE);
            }
         };
         jar.setTaskName(getTaskName());
         jar.setProject(getProject());
         jar.setDestFile(renamedFile);
         jar.setUpdate(true);
         Duplicate duplicate = new Duplicate();
         duplicate.setValue("preserve");
         jar.setDuplicate(duplicate);
         jar.addConfiguredManifest(getManifest());
         jar.execute();

         return renamedFile;
      } catch (ManifestException e) {
         throw new BuildException("Invalid Manifest", e, getLocation());
      }
   }

   protected File getWarWithMergedManifest(File file) throws IOException {
      File tempDir = new File(System.getProperty("java.io.tmpdir"));
      File renamedFile = fu.createTempFile(file.getName(), ".tmp", tempDir);
      renamedFile.deleteOnExit();
      fu.copyFile(file, renamedFile);

      try {
         War war = new War() {
            public void log(String msg) {
               // Ignore logging
            }
         };
         war.setTaskName(getTaskName());
         war.setProject(getProject());
         war.setDestFile(renamedFile);
         war.setUpdate(true);
         war.addConfiguredManifest(getManifest());
         war.execute();

         return renamedFile;
      } catch (ManifestException e) {
         throw new BuildException("Invalid Manifest", e, getLocation());
      }
   }
   
   protected StringBuffer updateEjbJarXmlEntries(ZipFile zip, ZipEntry entry) throws IOException {
      if (entry == null) {
         return null;
      }
      
      boolean updateJndi = !HIBERNATE_FACTORY_JNDI.equals(hibernatefactoryjndi);
      boolean updateInjector = !TRANSACTIONAL_INJECTOR.equals(transactionalinjector);
      if (!updateJndi && !updateInjector) {
         return null;
      }

      StringBuffer sb = getInputStreamAsStringBuffer(zip.getInputStream(entry));
      
      if (updateJndi) {
         updateHibernateFactoryAddress(sb);
      }
      
      if (updateInjector) {
         updateTransactionalInjector(sb);
      }
      
      return sb;
   }

   protected StringBuffer getInputStreamAsStringBuffer(InputStream in) throws IOException {
      char[] buffer = new char[1024];
      int length = 0;
      BufferedReader reader = null;
      try {
          reader = new BufferedReader(new InputStreamReader(in));
          StringBuffer sb = new StringBuffer();

          while ((length =
             reader.read(buffer)) >= 0) {
             sb.append(buffer, 0, length);
          }

          return sb;
      } finally {
          if (reader != null) {
              try {
                  reader.close();
              } catch (IOException e) {
                  // ignore
              }
          }
      }
   }

   protected StringBuffer updateHibernateFactoryAddress(StringBuffer sb) {
      int indexOf = sb.indexOf(HIBERNATE_FACTORY_JNDI);

      if (indexOf < 0) {
         return sb;
      }

      sb.replace(indexOf, indexOf + HIBERNATE_FACTORY_JNDI.length(), hibernatefactoryjndi);

      return sb;
   }

   protected StringBuffer updateTransactionalInjector(StringBuffer sb) {
      int indexOf = sb.indexOf(TRANSACTIONAL_INJECTOR);

      if (indexOf < 0) {
         return sb;
      }

      sb.replace(indexOf, indexOf + TRANSACTIONAL_INJECTOR.length(), transactionalinjector);

      return sb;
   }

   public void execute() throws BuildException {
      if (displayName == null) {
         throw new BuildException(
            "The 'displayname' must be specified, according to the J2EE/EAR spec.");
      }

      super.execute();
   }

   protected void initZipOutputStream(ZipOutputStream zOut)
      throws IOException, BuildException {
      super.initZipOutputStream(zOut);
      writeAppXML(zOut, mkApplicationXML());
      writeJBossServiceXML(zOut, mkJBossAppXML());
   }

   private void writeAppXML(ZipOutputStream zOut, String appXML)
      throws IOException {
      zipDir(null, zOut, "META-INF/", ZipFileSet.DEFAULT_DIR_MODE);

      ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
      OutputStreamWriter out = new OutputStreamWriter(bufOut, "UTF-8");
      PrintWriter writer = new PrintWriter(out);
      writer.println(appXML);
      writer.flush();

      ByteArrayInputStream bufIn = new ByteArrayInputStream(bufOut.toByteArray());

      super.zipFile(bufIn, zOut, APPXML_NAME, System.currentTimeMillis(), null,
         ZipFileSet.DEFAULT_FILE_MODE);
   }

   private void writeJBossServiceXML(ZipOutputStream zOut,
      String jbossserviceXML) throws IOException {
      if (services.isEmpty()) {
         return;
      }

      zipDir(null, zOut, "META-INF/", ZipFileSet.DEFAULT_DIR_MODE);

      ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
      OutputStreamWriter out = new OutputStreamWriter(bufOut, "UTF-8");
      PrintWriter writer = new PrintWriter(out);
      writer.println(jbossserviceXML);
      writer.flush();

      ByteArrayInputStream bufIn = new ByteArrayInputStream(bufOut.toByteArray());

      super.zipFile(bufIn, zOut, JBOSS_APP_XML_NAME,
         System.currentTimeMillis(), null, ZipFileSet.DEFAULT_FILE_MODE);
   }

   // ----------------------------------------------------
   // SecurityRole subtag
   // ----------------------------------------------------
   /**
    * Adds a 'securityRole' element.
    *
    * @param r
    */
   public void addSecurityRole(SecurityRole r) {
      roles.add(r);
   }

   // ----------------------------------------------------
   // Attributes
   // ----------------------------------------------------
   public String getDisplayname() {
      return displayName;
   }

   public void setDisplayname(String displayName) {
      this.displayName = displayName;
   }

   public String getSmallIcon() {
      return smallIcon;
   }

   public void setSmallIcon(String smallIcon) {
      this.smallIcon = smallIcon;
   }

   public String getLargeIcon() {
      return largeIcon;
   }

   public void setLargeIcon(String largeIcon) {
      this.largeIcon = largeIcon;
   }

   // ----------------------------------------------------
   // Helpers
   // ----------------------------------------------------
   protected String mkApplicationXML() {
      StringBuffer buf = new StringBuffer(1000);

      buf.append(XML_HEADER).append("\n\n");
      buf.append("<application>\n");

      if ((getSmallIcon() != null) || (getLargeIcon() != null)) {
         buf.append("  <icon>");
         mkSimpleTag(buf, "small-icon", getLargeIcon());
         mkSimpleTag(buf, "large-icon", getLargeIcon());
         buf.append("  </icon>\n");
      }

      mkSimpleTag(buf, "display-name", getDisplayname(), "  ").append("\n");
      mkSimpleTag(buf, "description", getDescription(), "  ").append("\n");
      mkTagList(buf, modules, "  ");
      mkTagList(buf, roles, "  ");

      buf.append("</application>\n");

      return buf.toString();
   }

   protected String mkJBossAppXML() {
      StringBuffer buf = new StringBuffer(1000);

      buf.append(JBOSS_APP_XML_HEADER).append("\n\n");
      buf.append("<jboss-app>\n");
      mkTagList(buf, services, "  ");

      buf.append("</jboss-app>\n");

      return buf.toString();
   }

   protected static void mkTagList(StringBuffer buf, List lst, String indent) {
      for (int i = 0; i < lst.size(); i++) {
         buf.append(indent).append(lst.get(i));
      }
   }

   protected static StringBuffer mkSimpleTag(StringBuffer buf, String tag,
      String value) {
      return mkSimpleTag(buf, tag, value, "");
   }

   protected static StringBuffer mkSimpleTag(StringBuffer buf, String tag,
      String value, String indent) {
      if ((value == null) || (value.trim().length() == 0)) {
         return buf;
      }

      return buf.append(indent).append("<").append(tag).append(">").append(value)
                      .append("</").append(tag).append(">");
   }

   public static class Module extends ZipFileSet {
      private Types type = new Types();
      private String context = null;

      public Module() {
         super.setPrefix("");
         type.setValue("java");
      }

      public String toString() {
         StringBuffer buf = new StringBuffer(100);

         String[] fileNames = getDirectoryScanner(getProject())
                                       .getIncludedFiles();
         validate(fileNames);

         for (int i = 0; i < fileNames.length; i++) {
            mkModule(buf, new File(fileNames[i]));
         }

         return buf.toString();
      }

      protected void validate(String[] fileNames) throws BuildException {
         if ((type == null) || (type.getValue() == null)) {
            throw new BuildException("Missing type attribute for module");
         }

         if ((fileNames == null) || (fileNames.length == 0)) {
            throw new BuildException(type + " module FileSet was empty");
         }

         if (type.getValue().equals("web")) {
            if ((context != null) && (fileNames.length != 1)) {
               throw new BuildException("Web module with context=" + context +
                  " has more than 1 file: " + Arrays.asList(fileNames));
            }
         }
      }

      protected StringBuffer mkModule(StringBuffer buf, File file) {
         buf.append("<module>");

         buf.append("<").append(getType()).append(">");

         if (type.getValue().equals("web")) {
            mkSimpleTag(buf, "web-uri", file.getName());
            mkSimpleTag(buf, "context-root", mkContext(file, getContext()));
         } else if (type.getValue().equals("service")) {
            buf.append(file.getName());
         } else {
            buf.append(file.getName());
         }

         buf.append("</").append(getType()).append(">");

         buf.append("</module>\n");

         return buf;
      }

      protected static String mkContext(File file, String appName) {
         if (appName != null) {
            return appName;
         }

         String fileName = file.getName();
         fileName = fileName.substring(0, fileName.lastIndexOf('.'));

         return "/" + fileName;
      }

      public Types getType() {
         return type;
      }

      public void setType(Types type) {
         this.type = type;
      }

      public String getContext() {
         return context;
      }

      public void setContext(String context) {
         this.context = context;
      }
   }

   /**
    * Support type, restricting Module.setType() to one if {"java", "ejb",
    * "web", "connector"}.
    */
   public static class Types extends EnumeratedAttribute {
      public String[] getValues() {
         return new String[] { "java", "ejb", "web", "connector", "service" };
      }
   }

   /**
    * Nested element 'securityRole'.
    */
   public static class SecurityRole {
      private String name;
      private String description;

      public SecurityRole() {
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public String getDescription() {
         return description;
      }

      public void setDescription(String description) {
         this.description = description;
      }

      public String toString() {
         StringBuffer buf = new StringBuffer(100);
         buf.append("<security-role>");
         mkSimpleTag(buf, "description", getDescription());
         mkSimpleTag(buf, "role-name", getName());
         buf.append("</security-role>\n");

         return buf.toString();
      }
   }
}
