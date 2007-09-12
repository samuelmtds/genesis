/*
 * GenesisProjectProperties.java
 *
 * Created on 25 de Agosto de 2007, 00:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer;


import net.java.dev.genesis.annotation.EnabledWhen;
import net.java.dev.genesis.annotation.Form;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProject;
import net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer.annotation.Property;

/**
 *
 * @author Michel Graciano
 */
@Form
public class GenesisProjectProperties {
    
    protected GenesisProject project;
    
    /** Creates a new instance of GenesisProjectProperties */
    GenesisProjectProperties( GenesisProject project ) {
        this.project = project;
    }
    
    /**
     * Mantém o valor da propriedade name.
     */
    private String name;
    
    /**
     * "Getter" para a propriedade name.
     * @return Valor para a propriedade name.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * "Setter" para a propriedade name.
     * @param name Novo valor para a propriedade name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Mantém o valor da propriedade prettyName.
     */
    private String prettyName;
    
    /**
     * "Getter" para a propriedade prettyName.
     * @return Valor para a propriedade prettyName.
     */
    @Property(value="genesisBasedApplication.prettyName")
    public String getPrettyName() {
        return this.prettyName;
    }
    
    /**
     * "Setter" para a propriedade prettyName.
     * @param prettyName Novo valor para a propriedade prettyName.
     */
    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    /**
     * Mantém o valor da propriedade mainClass.
     */
    private String mainClass;

    /**
     * "Getter" para a propriedade mainClass.
     * @return Valor para a propriedade mainClass.
     */
    @Property("genesisBasedApplication.mainClass")
    public String getMainClass() {
        return this.mainClass;
    }

    /**
     * "Setter" para a propriedade mainClass.
     * @param mainClass Novo valor para a propriedade mainClass.
     */
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    /**
     * Mantém o valor da propriedade localMode.
     */
    private boolean localMode;

    /**
     * "Getter" para a propriedade localMode.
     * @return Valor para a propriedade localMode.
     */
    @Property("local.mode")
    public boolean isLocalMode() {
        return this.localMode;
    }

    /**
     * "Setter" para a propriedade localMode.
     * @param localMode Novo valor para a propriedade localMode.
     */
    public void setLocalMode(boolean localMode) {
        this.localMode = localMode;
    }

    /**
     * Mantém o valor da propriedade remoteMode.
     */
    private boolean remoteMode;

    /**
     * "Getter" para a propriedade remoteMode.
     * @return Valor para a propriedade remoteMode.
     */
    @Property("remote.mode")
    public boolean isRemoteMode() {
        return this.remoteMode;
    }

    /**
     * "Setter" para a propriedade remoteMode.
     * @param remoteMode Novo valor para a propriedade remoteMode.
     */
    public void setRemoteMode(boolean remoteMode) {
        this.remoteMode = remoteMode;
    }

    /**
     * Mantém o valor da propriedade hibernateDialect.
     */
    private String hibernateDialect;

    /**
     * "Getter" para a propriedade hibernateDialect.
     * @return Valor para a propriedade hibernateDialect.
     */
    @Property("hibernate.dialect")
    @EnabledWhen("form.hibernateGenerateFiles")
    public String getHibernateDialect() {
        return this.hibernateDialect;
    }

    /**
     * "Setter" para a propriedade hibernateDialect.
     * @param hibernateDialect Novo valor para a propriedade hibernateDialect.
     */
    public void setHibernateDialect(String hibernateDialect) {
        this.hibernateDialect = hibernateDialect;
    }

    /**
     * Mantém o valor da propriedade validationGenerateFiles.
     */
    private boolean validationGenerateFiles;

    /**
     * "Getter" para a propriedade validationGenerateFiles.
     * @return Valor para a propriedade validationGenerateFiles.
     */
    @Property("validation.generate.files")
    public boolean isValidationGenerateFiles() {
        return this.validationGenerateFiles;
    }

    /**
     * "Setter" para a propriedade validationGenerateFiles.
     * @param validationGenerateFiles Novo valor para a propriedade validationGenerateFiles.
     */
    public void setValidationGenerateFiles(boolean validationGenerateFiles) {
        this.validationGenerateFiles = validationGenerateFiles;
    }
    
    /**
     * Mantém o valor da propriedade hibernateGenerateFiles.
     */
    private boolean hibernateGenerateFiles;

    /**
     * "Getter" para a propriedade hibernateGenerateFiles.
     * @return Valor para a propriedade hibernateGenerateFiles.
     */
    @Property("hibernate.generate.files")
    public boolean isHibernateGenerateFiles() {
        return this.hibernateGenerateFiles;
    }

    /**
     * "Setter" para a propriedade hibernateGenerateFiles.
     * @param hibernateGenerateFiles Novo valor para a propriedade hibernateGenerateFiles.
     */
    public void setHibernateGenerateFiles(boolean hibernateGenerateFiles) {
        this.hibernateGenerateFiles = hibernateGenerateFiles;
    }

    /**
     * Mantém o valor da propriedade hibernateShowSql.
     */
    private boolean hibernateShowSql;

    /**
     * "Getter" para a propriedade hibernateShowSql.
     * @return Valor para a propriedade hibernateShowSql.
     */
    @Property("hibernate.show.sql")
    @EnabledWhen("form.hibernateGenerateFiles")
    public boolean isHibernateShowSql() {
        return this.hibernateShowSql;
    }

    /**
     * "Setter" para a propriedade hibernateShowSql.
     * @param hibernateShowSql Novo valor para a propriedade hibernateShowSql.
     */
    public void setHibernateShowSql(boolean hibernateShowSql) {
        this.hibernateShowSql = hibernateShowSql;
    }

    /**
     * Mantém o valor da propriedade hibernateVersion.
     */
    private String hibernateVersion;

    /**
     * "Getter" para a propriedade hibernateVersion.
     * @return Valor para a propriedade hibernateVersion.
     */
    @Property("hibernate.version")
    @EnabledWhen("form.hibernateGenerateFiles")
    public String getHibernateVersion() {
        return this.hibernateVersion;
    }

    /**
     * "Setter" para a propriedade hibernateVersion.
     * @param hibernateVersion Novo valor para a propriedade hibernateVersion.
     */
    public void setHibernateVersion(String hibernateVersion) {
        this.hibernateVersion = hibernateVersion;
    }

    /**
     * Mantém o valor da propriedade jdbcConnectionUrl.
     */
    private String jdbcConnectionUrl;

    /**
     * "Getter" para a propriedade jdbcConnectionUrl.
     * @return Valor para a propriedade jdbcConnectionUrl.
     */
    @Property("jdbc.connection.url")
    @EnabledWhen("form.hibernateGenerateFiles")
    public String getJdbcConnectionUrl() {
        return this.jdbcConnectionUrl;
    }

    /**
     * "Setter" para a propriedade jdbcConnectionUrl.
     * @param jdbcConnectionUrl Novo valor para a propriedade jdbcConnectionUrl.
     */
    public void setJdbcConnectionUrl(String jdbcConnectionUrl) {
        this.jdbcConnectionUrl = jdbcConnectionUrl;
    }

    /**
     * Mantém o valor da propriedade jdbcDriverClass.
     */
    private String jdbcDriverClass;

    /**
     * "Getter" para a propriedade jdbcDriverClass.
     * @return Valor para a propriedade jdbcDriverClass.
     */
    @Property("jdbc.driver.class")
    @EnabledWhen("form.hibernateGenerateFiles")
    public String getJdbcDriverClass() {
        return this.jdbcDriverClass;
    }

    /**
     * "Setter" para a propriedade jdbcDriverClass.
     * @param jdbcDriverClass Novo valor para a propriedade jdbcDriverClass.
     */
    public void setJdbcDriverClass(String jdbcDriverClass) {
        this.jdbcDriverClass = jdbcDriverClass;
    }

    /**
     * Mantém o valor da propriedade jdbcDriver.
     */
    private String jdbcDriver;

    /**
     * "Getter" para a propriedade jdbcDriver.
     * @return Valor para a propriedade jdbcDriver.
     */
    @Property("jdbc.driver")
    @EnabledWhen("form.hibernateGenerateFiles")
    public String getJdbcDriver() {
        return this.jdbcDriver;
    }

    /**
     * "Setter" para a propriedade jdbcDriver.
     * @param jdbcDriver Novo valor para a propriedade jdbcDriver.
     */
    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    /**
     * Mantém o valor da propriedade jdbcPassword.
     */
    private String jdbcPassword;

    /**
     * "Getter" para a propriedade jdbcPassword.
     * @return Valor para a propriedade jdbcPassword.
     */
    @Property("jdbc.password")
    @EnabledWhen("form.hibernateGenerateFiles")
    public String getJdbcPassword() {
        return this.jdbcPassword;
    }

    /**
     * "Setter" para a propriedade jdbcPassword.
     * @param jdbcPassword Novo valor para a propriedade jdbcPassword.
     */
    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }

    /**
     * Mantém o valor da propriedade jdbcUserName.
     */
    private String jdbcUserName;

    /**
     * "Getter" para a propriedade jdbcUserName.
     * @return Valor para a propriedade jdbcUserName.
     */
    @Property("jdbc.username")
    @EnabledWhen("form.hibernateGenerateFiles")
    public String getJdbcUserName() {
        return this.jdbcUserName;
    }

    /**
     * "Setter" para a propriedade jdbcUserName.
     * @param jdbcUserName Novo valor para a propriedade jdbcUserName.
     */
    public void setJdbcUserName(String jdbcUserName) {
        this.jdbcUserName = jdbcUserName;
    }

    /**
     * Mantém o valor da propriedade webstartPort.
     */
    private Long webstartPort;

    /**
     * "Getter" para a propriedade webstartPort.
     * @return Valor para a propriedade webstartPort.
     */
    @Property("webstart.port")
    public Long getWebstartPort() {
        return this.webstartPort;
    }

    /**
     * "Setter" para a propriedade webstartPort.
     * @param webstartPort Novo valor para a propriedade webstartPort.
     */
    public void setWebstartPort(Long webstartPort) {
        this.webstartPort = webstartPort;
    }

    /**
     * Mantém o valor da propriedade webstartServer.
     */
    private String webstartServer;

    /**
     * "Getter" para a propriedade webstartServer.
     * @return Valor para a propriedade webstartServer.
     */
    @Property("webstart.server")
    public String getWebstartServer() {
        return this.webstartServer;
    }

    /**
     * "Setter" para a propriedade webstartServer.
     * @param webstartServer Novo valor para a propriedade webstartServer.
     */
    public void setWebstartServer(String webstartServer) {
        this.webstartServer = webstartServer;
    }

    /**
     * Mantém o valor da propriedade buildDir.
     */
    private String buildDir;

    /**
     * "Getter" para a propriedade buildDir.
     * @return Valor para a propriedade buildDir.
     */
    @Property("build.dir")
    public String getBuildDir() {
        return this.buildDir;
    }

    /**
     * "Setter" para a propriedade buildDir.
     * @param buildDir Novo valor para a propriedade buildDir.
     */
    public void setBuildDir(String buildDir) {
        this.buildDir = buildDir;
    }
    
}
