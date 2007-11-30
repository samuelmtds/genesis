/*
 * The Genesis Project
 * Copyright (C) 2007  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer;

import net.java.dev.genesis.annotation.EnabledWhen;
import net.java.dev.genesis.annotation.Form;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProject;
import net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer.annotation.Property;

@Form
public class GenesisProjectProperties {
   
   protected GenesisProject project;
   
   private String name;
   private String prettyName;
   private String jseVersion;
   private String mainClass;
   private boolean localMode;
   private boolean remoteMode;
   private String hibernateDialect;
   private boolean validationGenerateFiles;
   private boolean hibernateGenerateFiles;
   private boolean hibernateShowSql;
   private String hibernateVersion;
   private String jdbcConnectionUrl;
   private String jdbcDriverClass;
   private String jdbcDriver;
   private String jdbcPassword;
   private String jdbcUserName;
   private Long webstartPort;
   private String webstartServer;
   private String buildDir;
   
   GenesisProjectProperties( GenesisProject project ) {
      this.project = project;
   }
   
   public String getName() {
      return this.name;
   }
   
   public void setName(String name) {
      this.name = name;
   }
   
   @Property(value="genesisBasedApplication.prettyName")
   public String getPrettyName() {
      return this.prettyName;
   }
   
   public void setPrettyName(String prettyName) {
      this.prettyName = prettyName;
   }
   
   @Property("genesisBasedApplication.mainClass")
   public String getMainClass() {
      return this.mainClass;
   }
   
   public void setMainClass(String mainClass) {
      this.mainClass = mainClass;
   }
   
   @Property("local.mode")
   public boolean isLocalMode() {
      return this.localMode;
   }
   
   public void setLocalMode(boolean localMode) {
      this.localMode = localMode;
   }
   
   @Property("remote.mode")
   public boolean isRemoteMode() {
      return this.remoteMode;
   }
   
   public void setRemoteMode(boolean remoteMode) {
      this.remoteMode = remoteMode;
   }
   
   @Property("hibernate.dialect")
   @EnabledWhen("form.hibernateGenerateFiles")
   public String getHibernateDialect() {
      return this.hibernateDialect;
   }
   
   public void setHibernateDialect(String hibernateDialect) {
      this.hibernateDialect = hibernateDialect;
   }
   
   @Property("validation.generate.files")
   public boolean isValidationGenerateFiles() {
      return this.validationGenerateFiles;
   }
   
   public void setValidationGenerateFiles(boolean validationGenerateFiles) {
      this.validationGenerateFiles = validationGenerateFiles;
   }
   
   @Property("hibernate.generate.files")
   public boolean isHibernateGenerateFiles() {
      return this.hibernateGenerateFiles;
   }
   
   public void setHibernateGenerateFiles(boolean hibernateGenerateFiles) {
      this.hibernateGenerateFiles = hibernateGenerateFiles;
   }
   
   @Property("hibernate.show.sql")
   @EnabledWhen("form.hibernateGenerateFiles")
   public boolean isHibernateShowSql() {
      return this.hibernateShowSql;
   }
   
   public void setHibernateShowSql(boolean hibernateShowSql) {
      this.hibernateShowSql = hibernateShowSql;
   }
   
   @Property("hibernate.version")
   @EnabledWhen("form.hibernateGenerateFiles")
   public String getHibernateVersion() {
      return this.hibernateVersion;
   }
   
   public void setHibernateVersion(String hibernateVersion) {
      this.hibernateVersion = hibernateVersion;
   }
   
   @Property("jdbc.connection.url")
   @EnabledWhen("form.hibernateGenerateFiles")
   public String getJdbcConnectionUrl() {
      return this.jdbcConnectionUrl;
   }
   
   public void setJdbcConnectionUrl(String jdbcConnectionUrl) {
      this.jdbcConnectionUrl = jdbcConnectionUrl;
   }
   
   @Property("jdbc.driver.class")
   @EnabledWhen("form.hibernateGenerateFiles")
   public String getJdbcDriverClass() {
      return this.jdbcDriverClass;
   }
   
   public void setJdbcDriverClass(String jdbcDriverClass) {
      this.jdbcDriverClass = jdbcDriverClass;
   }
   
   @Property("jdbc.driver")
   @EnabledWhen("form.hibernateGenerateFiles")
   public String getJdbcDriver() {
      return this.jdbcDriver;
   }
   
   public void setJdbcDriver(String jdbcDriver) {
      this.jdbcDriver = jdbcDriver;
   }
   
   @Property("jdbc.password")
   @EnabledWhen("form.hibernateGenerateFiles")
   public String getJdbcPassword() {
      return this.jdbcPassword;
   }
   
   public void setJdbcPassword(String jdbcPassword) {
      this.jdbcPassword = jdbcPassword;
   }
   
   @Property("jdbc.username")
   @EnabledWhen("form.hibernateGenerateFiles")
   public String getJdbcUserName() {
      return this.jdbcUserName;
   }
   
   public void setJdbcUserName(String jdbcUserName) {
      this.jdbcUserName = jdbcUserName;
   }
   
   @Property("webstart.port")
   public Long getWebstartPort() {
      return this.webstartPort;
   }
   
   public void setWebstartPort(Long webstartPort) {
      this.webstartPort = webstartPort;
   }
   
   @Property("webstart.server")
   public String getWebstartServer() {
      return this.webstartServer;
   }
   
   public void setWebstartServer(String webstartServer) {
      this.webstartServer = webstartServer;
   }
   
   @Property("build.dir")
   public String getBuildDir() {
      return this.buildDir;
   }
   
   public void setBuildDir(String buildDir) {
      this.buildDir = buildDir;
   }

   @Property("webstart.jnlp.j2se.version")
   public String getJseVersion() {
      return jseVersion;
   }

   public void setJseVersion(String jseVersion) {
      this.jseVersion = jseVersion;
   }
   
}
