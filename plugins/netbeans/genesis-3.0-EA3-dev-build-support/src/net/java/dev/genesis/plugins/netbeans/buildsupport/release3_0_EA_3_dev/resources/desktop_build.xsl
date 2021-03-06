<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:project="http://www.netbeans.org/ns/project/1"
                xmlns:genesis="https://genesis.dev.java.net/ns/netbeans/projecttype/1"
                xmlns:xalan="http://xml.apache.org/xslt"
                exclude-result-prefixes="xalan project genesis">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="3"/>
    <xsl:template match="/">
        <xsl:comment><![CDATA[
*** GENERATED FROM project.xml - DO NOT EDIT  ***
***         EDIT ../build.xml INSTEAD         ***
]]></xsl:comment>
<project name="genesis-desktop-based-project" default="all" basedir="."
         xmlns:genesis="https://genesis.dev.java.net/nonav/ns/master_build.xml">
   <import file="master_build.xml" />

   <target name="-do-additional-init">
      <property name="local.mode" value="false" />
      <property name="remote.mode" value="true" />

      <condition property="local.mode.only">
         <and>
            <istrue value="${{local.mode}}" />
            <isfalse value="${{remote.mode}}" />
         </and>
      </condition>

      <condition property="remote.mode.only">
         <and>
            <istrue value="${{remote.mode}}" />
            <isfalse value="${{local.mode}}" />
         </and>
      </condition>

      <condition property="local.and.remote">
         <and>
            <istrue value="${{local.mode}}" />
            <istrue value="${{remote.mode}}" />
         </and>
      </condition>
   </target>

   <target name="-do-additional-init-paths">
      <property name="orangevolt.dist" 
                location="${{genesis.home}}/lib/orangevolt-ant-tasks" />

      <path id="orangevolt.path">
         <fileset dir="${{orangevolt.dist}}" includes="*.jar" />
      </path>

   </target>

   <target name="shared:additional-check-hibernate-conditions">
      <condition property="shared.hibernate.local.needed">
         <and>
            <istrue value="${{shared.hibernate.needed}}" />
            <istrue value="${{local.mode.only}}" />
         </and>
      </condition>

      <condition property="shared.hibernate.remote.needed">
         <and>
            <istrue value="${{shared.hibernate.needed}}" />
            <istrue value="${{remote.mode.only}}" />
         </and>
      </condition>

      <condition property="shared.hibernate.both.needed">
         <and>
            <istrue value="${{shared.hibernate.needed}}" />
            <istrue value="${{local.and.remote}}" />
         </and>
      </condition>
   </target>
   
   <target name="additional-hibernate-macrodefs" if="shared.hibernate.tasks.needed">
      <macrodef name="hibernatedoclet-local"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <sequential>
            <genesis:hibernatedoclet-macro>
               <subtasks>
                  <hibernatecfg dialect="${{hibernate.dialect}}"
                                userName="${{jdbc.username}}"
                                password="${{jdbc.password}}"
                                driver="${{jdbc.driver.class}}"
                                jdbcUrl="${{jdbc.connection.url}}"
                                transactionManagerStrategy="net.sf.hibernate.transaction.JDBCTransactionFactory"
                                showSql="${{hibernate.show.sql}}" />
               </subtasks>
            </genesis:hibernatedoclet-macro>
         </sequential>
      </macrodef>

      <macrodef name="hibernatedoclet-both"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <sequential>
            <genesis:hibernatedoclet-remote>
               <customize>
                  <hibernatecfg dialect="${{hibernate.dialect}}"
                                userName="${{jdbc.username}}"
                                password="${{jdbc.password}}"
                                driver="${{jdbc.driver.class}}"
                                jdbcUrl="${{jdbc.connection.url}}"
                                transactionManagerStrategy="net.sf.hibernate.transaction.JDBCTransactionFactory"
                                showSql="${{hibernate.show.sql}}" />
               </customize>
            </genesis:hibernatedoclet-remote>
         </sequential>
      </macrodef>
   </target>

   <target name="shared:hibernate-doclet-local" 
           if="shared.hibernate.local.needed"
           depends="shared:check-hibernate-conditions,hibernate-macrodefs">
      <genesis:hibernatedoclet-local />
   </target>

   <target name="shared:hibernate-doclet-remote" 
           if="shared.hibernate.remote.needed"
           depends="shared:check-hibernate-conditions,hibernate-macrodefs">
      <genesis:hibernatedoclet-remote />
   </target>

   <target name="shared:hibernate-doclet-both" 
           if="shared.hibernate.both.needed"
           depends="shared:check-hibernate-conditions,hibernate-macrodefs">
      <genesis:hibernatedoclet-both />
   </target>

   <target name="shared:hibernate-doclet" 
           depends="shared:hibernate-doclet-local,
                    shared:hibernate-doclet-remote,
                    shared:hibernate-doclet-both" />

   <target name="client:additional-init">
      <property name="client.annotated.dir" value="${{client.dir}}/${{annotated.dir}}" />
      <property name="client.annotated.timestamp.file" 
                location="${{client.annotated.dir}}/${{timestamp.file}}" />
      <property name="client.validation.dir" 
                value="${{client.dir}}/${{build.dir}}/validation" />
      <property name="client.validation.timestamp.file" 
                location="${{client.validation.dir}}/${{timestamp.file}}" />

      <property name="validation.generate.files" value="true" />
      
      <path id="client.standard.javac.classpath">
         <path location="${{shared.classes.dir}}" />
         <path refid="aspectwerkz.path" />
         <fileset dir="${{genesis.dist}}">
            <include name="${{genesis-annotation.jar}}" />
            <include name="genesis-shared-${{genesis.version}}.jar" />
            <include name="genesis-client-${{genesis.version}}.jar" />
            <include name="genesis-client-swing-${{genesis.version}}.jar" />
            <include name="genesis-client-swt-${{genesis.version}}.jar" />
            <include name="genesis-client-thinlet-${{genesis.version}}.jar" />
         </fileset>
         <fileset dir="${{commons.dist}}">
            <include name="commons-beanutils*.jar" />
            <include name="commons-digester*.jar" />
            <include name="commons-jxpath*.jar" />
            <include name="commons-logging*.jar" />
            <include name="commons-validator*.jar" />
            <include name="jakarta-oro-*.jar" />
            <include name="reusable-components*.jar" />
         </fileset>
         <fileset dir="${{thinlet.dist}}" includes="thinlet*.jar" />
         <fileset dir="${{hibernate.dist}}" includes="hibernate2.jar" />
      </path>
   </target>

   <target name="client:additional-clean" if="client.clean">
      <delete dir="${{client.validation.dir}}" />
   </target>

   <target name="client:check-annotations-needed" depends="client:init">
      <condition property="client.classes.changed">
         <and>
            <istrue value="${{has.client.sources}}" />
            <or>
               <not>
                  <available file="${{client.annotated.dir}}" />
               </not>
               <not>
                  <uptodate>
                     <srcfiles dir="${{client.classes.dir}}" />
                     <mapper type="merge" to="${{client.annotated.timestamp.file}}" />
                  </uptodate>
               </not>
            </or>
         </and>
      </condition>

      <condition property="aw.tasks.needed">
         <istrue value="${{client.classes.changed}}" />
      </condition>

      <condition property="client.annotationc.tasks.needed">
         <and>
            <istrue value="${{client.classes.changed}}" />
            <istrue value="${{needs.annotation}}" />
         </and>
      </condition>
   </target>

   <target name="client:annotations" if="client.annotationc.tasks.needed"
           depends="client:check-annotations-needed,define-aw-macros">
      <genesis:annotationc srcdir="${{client.sources.dir}}" 
                           destdir="${{client.annotated.dir}}"
                           classpath="${{client.classes.dir}}"
                           touchfile="${{client.annotated.timestamp.file}}" />
   </target>

   <target name="client:check-validation-conditions" depends="client:init">
      <condition property="client.validation.needed">
         <and>
            <istrue value="${{has.client.sources}}" />
            <istrue value="${{validation.generate.files}}" />
            <or>
               <istrue value="${{validation.force.generation}}" />
               <not>
                  <available file="${{client.validation.dir}}" />
               </not>
               <not>
                  <uptodate>
                     <srcfiles dir="${{client.sources.dir}}" includes="**/*.java" />
                     <mapper type="merge" to="${{client.validation.timestamp.file}}" />
                  </uptodate>
               </not>
            </or>
         </and>
      </condition>
   </target>

   <target name="validation-macrodefs" if="client.validation.needed"
           depends="client:check-validation-conditions">
      <macrodef name="validation-macro"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <element name="filesets" optional="false" />
         <element name="validation-task" optional="false" />
         <sequential>
            <taskdef name="genesisdoclet" 
                     classname="xdoclet.modules.genesis.GenesisDocletTask" 
                     classpathref="xdoclet.path" />

            <genesisdoclet destdir="${{client.validation.dir}}" 
                           verbose="${{verbose}}">
               <filesets />
               <validation-task />
            </genesisdoclet>

            <touch file="${{client.validation.timestamp.file}}" />
         </sequential>
      </macrodef>

      <presetdef name="validation"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:validation-macro>
            <filesets>
               <fileset dir="${{client.sources.dir}}" includes="**/*.java" />
            </filesets>
            <validation-task>
               <genesisvalidationxml xmlencoding="ISO-8859-1" />
            </validation-task>
         </genesis:validation-macro>
      </presetdef>
   </target>

   <target name="-do-client-validation">
      <genesis:validation />
   </target>

   <target name="client:validation" if="client.validation.needed" 
           depends="define-call-task,client:check-validation-conditions,validation-macrodefs">
      <genesis:call target="-do-client-validation" />
   </target>

   <target name="client:compile" 
           depends="client:javac,client:annotations,client:validation" />

   <target name="weaving:additional-init">
      <property name="preweaving.local.dir" value="${{preweaving.dir}}/local" />
      <property name="weaving.local.dir" value="${{weaving.dir}}/local" />
      <property name="weaving.local.timestamp.file" 
                location="${{weaving.local.dir}}/${{timestamp.file}}" />
      <property name="preweaving.remote.dir" value="${{preweaving.dir}}/remote" />
      <property name="weaving.remote.dir" value="${{weaving.dir}}/remote" />
      <property name="weaving.remote.timestamp.file" 
                location="${{weaving.remote.dir}}/${{timestamp.file}}" />

      <property name="weaving.xml.definition.dir" 
                location="${{client.sources.dir}}/META-INF" />

      <condition property="weaving.local.xml" 
                 value="${{weaving.xml.definition.dir}}/aop.xml">
         <and>
            <istrue value="${{local.mode.only}}" />
            <available file="${{weaving.xml.definition.dir}}/aop.xml" />
         </and>
      </condition>
      <condition property="weaving.local.xml" 
                 value="${{weaving.xml.definition.dir}}/local-aop.xml">
         <and>
            <istrue value="${{local.mode.only}}" />
            <not>
               <available file="${{weaving.xml.definition.dir}}/aop.xml" />
            </not>
         </and>
      </condition>
      <condition property="weaving.local.xml" 
                 value="${{weaving.xml.definition.dir}}/local-aop.xml">
         <istrue value="${{local.and.remote}}" />
      </condition>

      <condition property="weaving.remote.xml" 
                 value="${{weaving.xml.definition.dir}}/aop.xml">
         <and>
            <istrue value="${{remote.mode.only}}" />
            <available file="${{weaving.xml.definition.dir}}/aop.xml" />
         </and>
      </condition>
      <condition property="weaving.remote.xml" 
                 value="${{weaving.xml.definition.dir}}/local-aop.xml">
         <and>
            <istrue value="${{remote.mode.only}}" />
            <not>
               <available file="${{weaving.xml.definition.dir}}/aop.xml" />
            </not>
         </and>
      </condition>
      <condition property="weaving.remote.xml" 
                 value="${{weaving.xml.definition.dir}}/remote-aop.xml">
         <istrue value="${{local.and.remote}}" />
      </condition>

      <path id="weaving.standard.local.classpath">
         <path path="${{genesis.dist}}/genesis-aspect-annotated-${{genesis.version}}.jar" />
         <path refid="client.javac.classpath" />
         <path refid="shared.javac.classpath" />
         <path refid="j2ee.path" />
      </path>

      <path id="weaving.standard.remote.classpath">
         <path path="${{genesis.dist}}/genesis-aspect-annotated-${{genesis.version}}.jar" />
         <path refid="client.javac.classpath" />
         <path refid="shared.javac.classpath" />
         <path refid="j2ee.path" />
      </path>
   </target>

   <target name="weaving:clean" if="weaving.clean" 
           depends="weaving:define-conditions">
      <delete dir="${{preweaving.local.dir}}" />
      <delete dir="${{weaving.local.dir}}" />
      <delete dir="${{weaving.local.timestamp.file}}" />
      <delete dir="${{preweaving.remote.dir}}" />
      <delete dir="${{weaving.remote.dir}}" />
      <delete dir="${{weaving.remote.timestamp.file}}" />
   </target>

   <target name="weaving:pre-define-local-classpath">
      <xsl:comment><![CDATA[ Override to define weaving.local.overriden.classpath or 
         weaving.additional.local.classpath ]]></xsl:comment> 
   </target>

   <target name="weaving:check-local-classpath-conditions" depends="weaving:init">
      <condition property="weaving.set.custom.local.classpath">
         <and>
            <istrue value="${{local.mode}}" />
            <isreference refid="weaving.local.overriden.classpath" type="path" />
         </and>
      </condition>
      <condition property="weaving.set.additional.local.classpath">
         <and>
            <istrue value="${{local.mode}}" />
            <not>
               <isset property="weaving.set.custom.local.classpath" />
            </not>
            <isreference refid="weaving.additional.local.classpath" />
         </and>
      </condition>
      <condition property="weaving.set.standard.local.classpath">
         <and>
            <istrue value="${{local.mode}}" />
            <not>
               <isset property="weaving.set.additional.local.classpath" />
            </not>
            <not>
               <isset property="weaving.set.custom.local.classpath" />
            </not>
         </and>
      </condition>
   </target>

   <target name="weaving:define-overriden-local-classpath"
           depends="weaving:init" if="weaving.set.custom.local.classpath">
      <path id="weaving.local.classpath">
         <path refid="weaving.local.overriden.classpath" />
      </path>
   </target>

   <target name="weaving:define-local-classpath-with-extensions" 
           depends="weaving:init" if="weaving.set.additional.local.classpath">
      <path id="weaving.local.classpath">
         <path refid="weaving.standard.local.classpath" />
         <path refid="weaving.additional.local.classpath" />
      </path>
   </target>

   <target name="weaving:define-standard-local-classpath"
           depends="weaving:init" if="weaving.set.standard.local.classpath">
      <path id="weaving.local.classpath">
         <path refid="weaving.standard.local.classpath" />
      </path>
   </target>

   <target name="weaving:define-local-classpath" 
           depends="weaving:init,weaving:pre-define-local-classpath,
                    weaving:check-local-classpath-conditions,
                    weaving:define-overriden-local-classpath,
                    weaving:define-local-classpath-with-extensions,
                    weaving:define-standard-local-classpath"/>

   <target name="weaving:pre-define-remote-classpath">
      <xsl:comment><![CDATA[ Override to define weaving.remote.overriden.classpath or 
         weaving.additional.remote.classpath ]]></xsl:comment> 
   </target>

   <target name="weaving:check-remote-classpath-conditions" depends="weaving:init">
      <condition property="weaving.set.custom.remote.classpath">
         <and>
            <istrue value="${{remote.mode}}" />
            <isreference refid="weaving.remote.overriden.classpath" type="path" />
         </and>
      </condition>
      <condition property="weaving.set.additional.remote.classpath">
         <and>
            <istrue value="${{remote.mode}}" />
            <not>
               <isset property="weaving.set.custom.remote.classpath" />
            </not>
            <isreference refid="weaving.additional.remote.classpath" />
         </and>
      </condition>
      <condition property="weaving.set.standard.remote.classpath">
         <and>
            <istrue value="${{remote.mode}}" />
            <not>
               <isset property="weaving.set.additional.remote.classpath" />
            </not>
            <not>
               <isset property="weaving.set.custom.remote.classpath" />
            </not>
         </and>
      </condition>
   </target>

   <target name="weaving:define-overriden-remote-classpath"
           depends="weaving:init" if="weaving.set.custom.remote.classpath">
      <path id="weaving.remote.classpath">
         <path refid="weaving.remote.overriden.classpath" />
      </path>
   </target>

   <target name="weaving:define-remote-classpath-with-extensions" 
           depends="weaving:init" if="weaving.set.additional.remote.classpath">
      <path id="weaving.remote.classpath">
         <path refid="weaving.standard.remote.classpath" />
         <path refid="weaving.additional.remote.classpath" />
      </path>
   </target>

   <target name="weaving:define-standard-remote-classpath"
           depends="weaving:init" if="weaving.set.standard.remote.classpath">
      <path id="weaving.remote.classpath">
         <path refid="weaving.standard.remote.classpath" />
      </path>
   </target>

   <target name="weaving:define-remote-classpath" 
           depends="weaving:init,weaving:pre-define-remote-classpath,
                    weaving:check-remote-classpath-conditions,
                    weaving:define-overriden-remote-classpath,
                    weaving:define-remote-classpath-with-extensions,
                    weaving:define-standard-remote-classpath"/>

   <target name="weaving:define-default-copy-fileset">
      <condition property="default-shared-copy-fileset" value="${{shared.annotated.dir}}">
         <istrue value="${{shared.annotationc.tasks.needed}}" />
      </condition>
      <condition property="default-shared-copy-fileset" value="${{shared.classes.dir}}">
         <not>
            <istrue value="${{shared.annotationc.tasks.needed}}" />
         </not>
      </condition>
      <condition property="default-client-copy-fileset" value="${{client.annotated.dir}}">
         <istrue value="${{client.annotationc.tasks.needed}}" />
      </condition>
      <condition property="default-client-copy-fileset" value="${{client.classes.dir}}">
         <not>  
            <istrue value="${{client.annotationc.tasks.needed}}" />
         </not>
      </condition>
   </target>

   <target name="weaving:check-conditions" depends="weaving:init,weaving:define-default-copy-fileset">
      <condition property="weaving.local.needed">
         <and>
            <istrue value="${{needs.weaving}}" />
            <istrue value="${{local.mode}}" />
            <or>
               <not>
                  <available file="${{weaving.local.dir}}" />
               </not>
               <not>
                  <uptodate>
                     <srcfiles dir="${{default-client-copy-fileset}}" 
                               includes="**/*.class" />
                     <mapper type="merge" to="${{weaving.local.timestamp.file}}" />
                  </uptodate>
               </not>
               <not>
                  <uptodate>
                     <srcfiles dir="${{default-shared-copy-fileset}}" 
                               includes="**/*.class" />
                     <mapper type="merge" to="${{weaving.local.timestamp.file}}" />
                  </uptodate>
               </not>
            </or>
         </and>
      </condition>

      <condition property="weaving.remote.needed">
         <and>
            <istrue value="${{needs.weaving}}" />
            <istrue value="${{remote.mode}}" />
            <or>
               <not>
                  <available file="${{weaving.remote.dir}}" />
               </not>
               <not>
                  <uptodate>
                     <srcfiles dir="${{default-client-copy-fileset}}" 
                               includes="**/*.class" />
                     <mapper type="merge" to="${{weaving.remote.timestamp.file}}" />
                  </uptodate>
               </not>
               <not>
                  <uptodate>
                     <srcfiles dir="${{default-shared-copy-fileset}}" 
                               includes="**/*.class" />
                     <mapper type="merge" to="${{weaving.remote.timestamp.file}}" />
                  </uptodate>
               </not>
            </or>
         </and>
      </condition>
   </target>

   <target name="weaving:additional-macrodefs">
      <presetdef name="weaving-copy"
                 uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:weaving-copy-macro>
            <default-copy-fileset>
               <fileset dir="${{default-client-copy-fileset}}" includes="**/*.class" />
               <fileset dir="${{default-shared-copy-fileset}}" includes="**/*.class" />
            </default-copy-fileset>
            <default-unjar-fileset>
               <fileset dir="${{thinlet.dist}}" includes="thinlet*.jar" />
               <fileset dir="${{genesis.dist}}"
                        includes="genesis-shared-annotated-${{genesis.version}}.jar" />
            </default-unjar-fileset>
         </genesis:weaving-copy-macro>
      </presetdef>
   </target>

   <target name="weaving:copy-local-files" 
           depends="weaving:check-conditions,weaving:macrodefs" 
           if="weaving.local.needed">
      <genesis:call target="-do-weaving-copy-local-files" />
   </target>

   <target name="-do-weaving-copy-local-files">
      <genesis:weaving-copy targetdir="${{preweaving.local.dir}}" 
                                  xml="${{weaving.local.xml}}" />
   </target>

   <target name="weaving:local" 
           depends="weaving:define-local-classpath,weaving:copy-local-files,
                    define-aw-macros"
           if="weaving.local.needed">
      <genesis:awc classpath="weaving.local.classpath" 
                   sourcepath="${{preweaving.local.dir}}"
                   destdir="${{weaving.local.dir}}"
                   touchfile="${{weaving.local.timestamp.file}}" />
   </target>

   <target name="weaving:copy-remote-files" depends="weaving:check-conditions" 
           if="weaving.remote.needed">
      <genesis:call target="-do-weaving-copy-remote-files" />
   </target>

   <target name="-do-weaving-copy-remote-files">
      <genesis:weaving-copy targetdir="${{preweaving.remote.dir}}" 
                            xml="${{weaving.remote.xml}}" />
   </target>

   <target name="weaving:remote" 
           depends="weaving:define-remote-classpath,weaving:copy-remote-files,
                    define-aw-macros"
           if="weaving.remote.needed">
      <genesis:awc classpath="weaving.remote.classpath" 
                   sourcepath="${{preweaving.remote.dir}}"
                   destdir="${{weaving.remote.dir}}"
                   touchfile="${{weaving.remote.timestamp.file}}" />
   </target>

   <target name="weaving" 
           depends="weaving:local,weaving:remote,-do-custom-weaving" />

   <target name="run:pre-init">
      <xsl:comment><![CDATA[ Override to define any properties that shouldn't be redefined by 
         the run:init target ]]></xsl:comment> 
   </target>

   <target name="run:init" depends="init-paths,run:pre-init">
      <property name="jar.local.name"
               value="${{genesisBasedApplication.name}}-local-weaved.jar" />
      <property name="jar.local.location" 
               location="${{build.dir}}/${{jar.local.name}}" />
      <property name="jar.remote.name"
               value="${{genesisBasedApplication.name}}-remote-weaved.jar" />
      <property name="jar.remote.location" 
               location="${{build.dir}}/${{jar.remote.name}}" />
      <property name="jar.shared.location" 
               location="${{build.dir}}/${{genesisBasedApplication.name}}-shared.jar" />

      <property name="jboss.provider.url" value="jnp://127.0.0.1" />
      <property name="jboss.factory.initial" 
                value="org.jnp.interfaces.NamingContextFactory" />

      <property name="debug.port" value="10000" />
      <property name="debug.suspend" value="n" />

      <path id="run.standard.classpath">
         <path refid="backport175.path" />
         <fileset dir="${{genesis.dist}}">
            <include name="${{genesis-annotation.jar}}" />
            <include name="genesis-aspect-annotated-*.jar" />
            <include name="genesis-client-*.jar" />
            <include name="genesis-client-swing-${{genesis.version}}.jar" />
            <include name="genesis-client-swt-${{genesis.version}}.jar" />
            <include name="genesis-client-thinlet-${{genesis.version}}.jar" />
         </fileset>
         <fileset dir="${{aspectwerkz.dist}}">
            <include name="aspectwerkz-*.jar" />
            <include name="dom4j-*.jar" />
            <include name="jrexx-*.jar" />
            <include name="trove-*.jar" />
         </fileset>
         <fileset dir="${{hibernate.dist}}">
            <include name="hibernate2.jar" />
            <include name="commons-collections-*.jar" />
            <include name="odmg-*.jar" />
         </fileset>
         <fileset dir="${{commons.dist}}">
            <include name="commons-beanutils-*.jar" />
            <include name="commons-digester-*.jar" />
            <include name="commons-jxpath-*.jar" />
            <include name="commons-logging-*.jar" />
            <include name="commons-validator-*.jar" />
            <include name="jakarta-oro-*.jar" />
            <include name="reusable-components-*.jar" />
         </fileset>
         <fileset dir="${{script.dist}}">
            <include name="commons-jxpath-*.jar" if="jxpath.needed" />
            <include name="bsf-*.jar" />
            <include name="bsh-*.jar" />
            <include name="js-*.jar" />
            <include name="commons-el-*.jar" />
            <include name="jakarta-taglibs-standard-*.jar" />
            <include name="jsp-api.jar" />
            <include name="rhino-1.5-R3.jar" />
         </fileset>
      </path>

      <path id="run.standard.local.classpath">
         <fileset file="${{jar.local.location}}" />
         <fileset file="${{jdbc.driver}}" />
         <fileset dir="${{hibernate.dist}}">
            <include name="cglib-full-*.jar"/>
            <include name="ehcache-*.jar"/>
            <include name="jta*.jar"/>
         </fileset>
      </path>

      <path id="run.standard.remote.classpath">
         <fileset file="${{jar.remote.location}}" />
         <fileset dir="${{jboss.client}}">
            <include name="jboss-client.jar" />
            <include name="jboss-common-client.jar" />
            <include name="jboss-j2ee.jar" />
            <include name="jboss-transaction-client.jar" />
            <include name="jbosssx-client.jar" />
            <include name="jnp-client.jar" />
         </fileset>
      </path>
   </target>

   <target name="run:pre-define-classpath">
      <xsl:comment><![CDATA[ Override to define run.overriden.classpath or 
         run.additional.classpath]]></xsl:comment> 
   </target>

   <target name="run:check-classpath-conditions" depends="run:init">
      <condition property="run.set.custom.classpath">
         <isreference refid="run.overriden.classpath" type="path" />
      </condition>
      <condition property="run.set.additional.classpath">
         <and>
            <not>
               <isset property="run.set.custom.classpath" />
            </not>
            <isreference refid="run.additional.classpath" type="path" />
         </and>
      </condition>
      <condition property="run.set.standard.classpath">
         <and>
            <not>
               <isset property="run.set.additional.classpath" />
            </not>
            <not>
               <isset property="run.set.custom.classpath" />
            </not>
         </and>
      </condition>

      <condition property="run.set.custom.local.classpath">
         <isreference refid="run.overriden.local.classpath" type="path" />
      </condition>
      <condition property="run.set.additional.local.classpath">
         <and>
            <not>
               <isset property="run.set.custom.local.classpath" />
            </not>
            <isreference refid="run.additional.local.classpath" type="path" />
         </and>
      </condition>
      <condition property="run.set.standard.local.classpath">
         <and>
            <not>
               <isset property="run.set.additional.local.classpath" />
            </not>
            <not>
               <isset property="run.set.custom.local.classpath" />
            </not>
         </and>
      </condition>

      <condition property="run.set.custom.remote.classpath">
         <isreference refid="run.overriden.remote.classpath" type="path" />
      </condition>
      <condition property="run.set.additional.remote.classpath">
         <and>
            <not>
               <isset property="run.set.custom.remote.classpath" />
            </not>
            <isreference refid="run.additional.remote.classpath" type="path" />
         </and>
      </condition>
      <condition property="run.set.standard.remote.classpath">
         <and>
            <not>
               <isset property="run.set.additional.remote.classpath" />
            </not>
            <not>
               <isset property="run.set.custom.remote.classpath" />
            </not>
         </and>
      </condition>
   </target>

   <target name="run:define-overriden-classpath"
           depends="run:init" if="run.set.custom.classpath">
      <path id="run.classpath">
         <path refid="run.overriden.classpath" />
      </path>
   </target>

   <target name="run:define-classpath-with-extensions" 
           depends="run:init" if="run.set.additional.classpath">
      <path id="run.classpath">
         <path refid="run.standard.classpath" />
         <path refid="run.additional.classpath" />
      </path>
   </target>

   <target name="run:define-standard-classpath"
           depends="run:init" if="run.set.standard.classpath">
      <path id="run.classpath">
         <path refid="run.standard.classpath" />
      </path>
   </target>

   <target name="run:define-overriden-local-classpath"
           depends="run:init" if="run.set.custom.local.classpath">
      <path id="run.local.classpath">
         <path refid="run.overriden.local.classpath" />
      </path>
   </target>

   <target name="run:define-local-classpath-with-extensions" 
           depends="run:init" if="run.set.additional.local.classpath">
      <path id="run.local.classpath">
         <path refid="run.standard.local.classpath" />
         <path refid="run.additional.local.classpath" />
         <path refid="run.classpath" />
      </path>
   </target>

   <target name="run:define-standard-local-classpath"
           depends="run:init" if="run.set.standard.local.classpath">
      <path id="run.local.classpath">
         <path refid="run.standard.local.classpath" />
         <path refid="run.classpath" />
      </path>
   </target>

   <target name="run:define-overriden-remote-classpath"
           depends="run:init" if="run.set.custom.remote.classpath">
      <path id="run.remote.classpath">
         <path refid="run.overriden.remote.classpath" />
      </path>
   </target>

   <target name="run:define-remote-classpath-with-extensions" 
           depends="run:init" if="run.set.additional.remote.classpath">
      <path id="run.remote.classpath">
         <path refid="run.standard.remote.classpath" />
         <path refid="run.additional.remote.classpath" />
         <path refid="run.classpath" />
      </path>
   </target>

   <target name="run:define-standard-remote-classpath"
           depends="run:init" if="run.set.standard.remote.classpath">
      <path id="run.remote.classpath">
         <path refid="run.standard.remote.classpath" />
         <path refid="run.classpath" />
      </path>
   </target>

   <target name="run:define-classpath" 
           depends="run:init,run:pre-define-classpath,
                    run:check-classpath-conditions,
                    run:define-overriden-classpath,
                    run:define-classpath-with-extensions,
                    run:define-standard-classpath,
                    run:define-overriden-local-classpath,
                    run:define-local-classpath-with-extensions,
                    run:define-standard-local-classpath,
                    run:define-overriden-remote-classpath,
                    run:define-remote-classpath-with-extensions,
                    run:define-standard-remote-classpath"/>

   <target name="run:macrodefs" depends="run:init">
      <macrodef name="java-macro"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <attribute name="fork" default="true" />
         <attribute name="failonerror" default="true" />
         <attribute name="classpathref" />
         <element name="sysproperties" optional="true" />
         <element name="jvmargs" optional="true" />
         <element name="customize" optional="true" />
         <sequential>
            <java classname="${{genesisBasedApplication.mainClass}}" fork="@{{fork}}" 
                  failonerror="@{{failonerror}}" classpathref="@{{classpathref}}">
               <sysproperties />
               <jvmargs />
               <customize />
            </java>
         </sequential>
      </macrodef>

      <presetdef name="java-local"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:java-macro classpathref="run.local.classpath" />
      </presetdef>

      <presetdef name="java-remote"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:java-macro classpathref="run.remote.classpath">
            <sysproperties>
               <sysproperty key="java.naming.factory.initial"
                            value="${{jboss.factory.initial}}" />
               <sysproperty key="java.naming.provider.url"
                            value="${{jboss.provider.url}}" />
            </sysproperties>
         </genesis:java-macro>
      </presetdef>

      <presetdef name="debug-local"
                 uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:java-local>
            <jvmargs>
               <jvmarg value="-Xdebug"/>
               <jvmarg value="-Xrunjdwp:transport=dt_socket,address=${{debug.port}},server=y,suspend=${{debug.suspend}}"/>
            </jvmargs>
         </genesis:java-local>
      </presetdef>

      <presetdef name="debug-remote"
                 uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:java-remote>
            <jvmargs>
               <jvmarg value="-Xdebug"/>
               <jvmarg value="-Xrunjdwp:transport=dt_socket,address=${{debug.port}},server=y,suspend=${{debug.suspend}}"/>
            </jvmargs>
         </genesis:java-remote>
      </presetdef>
   </target>

   <target name="-do-run-local">
      <genesis:java-local />
   </target>

   <target name="run:local" 
           depends="run:define-classpath,run:macrodefs,-do-run-local" />

   <target name="-do-run-remote">
      <genesis:java-remote />
   </target>

   <target name="run:remote" 
           depends="run:define-classpath,run:macrodefs,-do-run-remote" />

   <target name="-do-run-debug-local">
      <genesis:debug-local />
   </target>

   <target name="run:debug-local" 
           depends="run:define-classpath,run:macrodefs,-do-run-debug-local" />

   <target name="-do-run-debug-remote">
      <genesis:debug-remote />
   </target>

   <target name="run:debug-remote" 
           depends="run:define-classpath,run:macrodefs,-do-run-debug-remote" />

   <target name="jar:additional-init" depends="run:define-classpath">
      <path id="jar.standard.local.classpath">
         <pathelement path="${{jdbc.driver}}" />
         <fileset dir="${{hibernate.dist}}">
            <include name="cglib-full-*.jar"/>
            <include name="ehcache-*.jar"/>
            <include name="jta*.jar"/>
         </fileset>
         <path refid="j2ee.path"/>
         <path refid="run.classpath" />
      </path>
   </target>

   <target name="jar:clean" depends="jar:init">
      <delete file="${{jar.local.location}}" />
      <delete file="${{jar.remote.location}}" />
      <delete file="${{jar.shared.location}}" />
   </target>

   <target name="jar:additional-check-conditions">
      <condition property="jar.local.needed">
         <and>
            <istrue value="${{needs.jar}}" />
            <istrue value="${{local.mode}}" />
         </and>
      </condition>

      <condition property="jar.remote.needed">
         <and>
            <istrue value="${{needs.jar}}" />
            <istrue value="${{remote.mode}}" />
         </and>
      </condition>
   </target>

   <target name="jar:additional-macrodefs" if="jar.needed">
      <presetdef name="jar-client"
                 uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:jar sourcesdir="${{client.sources.dir}}">
            <additional-filesets>
               <fileset dir="${{client.validation.dir}}">
                  <exclude name="${{timestamp.file}}" />
               </fileset>
               <fileset dir="${{shared.hibernate.dir}}">
                  <patternset refid="classes.patternset" />
                  <exclude name="jboss-service.xml" />
               </fileset>
               <fileset dir="${{shared.sources.dir}}">
                  <patternset refid="sources.patternset" />
               </fileset>
            </additional-filesets>
         </genesis:jar>
      </presetdef>
      <presetdef name="jar-local"
                 uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:jar-client sourcesdir="${{client.sources.dir}}">
            <customize>
               <manifest>
                  <attribute name="Main-Class" 
                             value="${{genesisBasedApplication.mainClass}}"/>
                  <attribute name="Class-Path" 
                             value="${{jar.local.flat.classpath}}"/>
               </manifest>
            </customize>
         </genesis:jar-client>
      </presetdef>
   </target>

   <target name="jar:pre-define-local-classpath">
      <xsl:comment><![CDATA[ Override to define jar.local.overriden.classpath or 
         jar.additional.local.classpath]]></xsl:comment> 
   </target>

   <target name="jar:check-local-classpath-conditions" depends="jar:init">
      <condition property="jar.set.custom.local.classpath">
         <isreference refid="jar.local.overriden.classpath" type="path" />
      </condition>
      <condition property="jar.set.additional.local.classpath">
         <and>
            <not>
               <isset property="jar.set.custom.local.classpath" />
            </not>
            <isreference refid="jar.additional.local.classpath" />
         </and>
      </condition>
      <condition property="jar.set.standard.local.classpath">
         <and>
            <not>
               <isset property="jar.set.additional.local.classpath" />
            </not>
            <not>
               <isset property="jar.set.custom.local.classpath" />
            </not>
         </and>
      </condition>
   </target>

   <target name="jar:define-overriden-local-classpath"
           depends="jar:init" if="jar.set.custom.local.classpath">
      <path id="jar.local.classpath">
         <path refid="jar.local.overriden.classpath" />
      </path>
   </target>

   <target name="jar:define-local-classpath-with-extensions" 
           depends="jar:init" if="jar.set.additional.local.classpath">
      <path id="jar.local.classpath">
         <path refid="jar.standard.local.classpath" />
         <path refid="jar.additional.local.classpath" />
      </path>
   </target>

   <target name="jar:define-standard-local-classpath"
           depends="jar:init" if="jar.set.standard.local.classpath">
      <path id="jar.local.classpath">
         <path refid="jar.standard.local.classpath" />
      </path>
   </target>

   <target name="jar:define-local-classpath" 
           depends="jar:init,jar:pre-define-local-classpath,
                    jar:check-local-classpath-conditions,
                    jar:define-overriden-local-classpath,
                    jar:define-local-classpath-with-extensions,
                    jar:define-standard-local-classpath"/>

   <target name="-do-jar-local">
      <genesis:jar-local destfile="${{jar.local.location}}" 
                         classesdir="${{weaving.local.dir}}" />
   </target>

   <target name="jar:local" 
           depends="define-call-task,jar:check-conditions,jar:macrodefs,jar:define-local-classpath" 
           if="jar.local.needed">
      <pathconvert property="jar.local.flat.classpath" pathsep=" ">
         <mapper type="flatten"/>
         <path refid="jar.local.classpath" />
      </pathconvert>

      <genesis:call target="-do-jar-local" />
   </target>

   <target name="-do-jar-remote">
      <genesis:jar-client destfile="${{jar.remote.location}}" 
                         classesdir="${{weaving.remote.dir}}" />
   </target>

   <target name="jar:remote" depends="define-call-task,jar:check-conditions,jar:macrodefs" 
           if="jar.remote.needed">
      <genesis:call target="-do-jar-remote" />
   </target>

   <target name="jar:shared" depends="jar:check-conditions,jar:macrodefs" 
           if="jar.shared.needed">
      <genesis:jar destfile="${{jar.shared.location}}" 
                   classesdir="${{shared.classes.dir}}"
                   sourcesdir="${{shared.sources.dir}}" />
   </target>

   <target name="jar" depends="jar:local,jar:remote,jar:shared,-do-custom-jar" />

   <target name="server-artifact:additional-macrodefs" if="server.artifact.needed">
      <presetdef name="sar"
                 uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:sar-macro>
            <additional-fileset>
               <fileset dir="${{genesis.dist}}">
                  <include name="genesis-shared-${{genesis.version}}.jar" />
               </fileset>
               <fileset file="${{jar.shared.location}}" />
            </additional-fileset>
         </genesis:sar-macro>
      </presetdef>
   </target>

   <target name="-do-server-artifact">
      <genesis:sar />
   </target>

   <target name="webstart:pre-init">
      <xsl:comment><![CDATA[ Override to define any properties that shouldn't be redefined by 
         the webstart:init target ]]></xsl:comment> 
   </target>

   <target name="webstart:init" 
           depends="init-paths,jar:init,webstart:pre-init">
      <property name="needs.webstart" value="${{remote.mode}}" />

      <property name="webstart.sources.dir" value="webstart" />
      <property name="webstart.target.dir" 
                value="${{build.dir}}/war" />
      <property name="webstart.jars.dir.name" value="application" />
      <property name="webstart.jars.dir" 
                value="${{webstart.target.dir}}/${{webstart.jars.dir.name}}" />

      <property name="webstart.war.file" 
                value="${{build.dir}}/${{genesisBasedApplication.name}}.war" />
      <property name="webstart.web.xml"
                value="${{webstart.sources.dir}}/WEB-INF/web.xml" />

      <property name="webstart.signjar.alias" value="genesis" />
      <property name="webstart.signjar.keystore" 
                value="${{webstart.sources.dir}}/${{webstart.jars.dir.name}}/genesis.keystore" />
      <property name="webstart.signjar.storepass" value="summa@brasil" />
      <property name="webstart.signjar.keypass" value="g3n3sis" />

      <property name="webstart.server" value="127.0.0.1" />
      <property name="webstart.port" value="8080" />
      <property name="webstart.context" 
                value="${{genesisBasedApplication.name}}"/>

      <property name="webstart.jnlp.homepage"
                value="http://${{webstart.server}}:${{webstart.port}}/${{webstart.context}}" />
      <property name="webstart.jnlp.codebase"
                value="${{webstart.jnlp.homepage}}/${{webstart.jars.dir.name}}" />
      <property name="webstart.jnlp.title" 
                value="${{genesisBasedApplication.prettyName}}" />
      <property name="webstart.jnlp.vendor" 
                value="${{genesisBasedApplication.prettyName}}" />
      <property name="webstart.jnlp.description"
                value="${{genesisBasedApplication.prettyName}}" />
      <property name="webstart.jnlp.tooltip"
                value="${{webstart.jnlp.description}}" />
      <property name="webstart.jnlp.icon" value="icon.gif" />

      <property name="webstart.jnlp.j2se.version" value="1.4+" />
      <property name="webstart.jnlp.j2se.initial.heap.size" value="32M" />
      <property name="webstart.jnlp.j2se.max.heap.size" value="64M" />

      <property name="webstart.jnlp.location.dir"
                location="${{webstart.jars.dir}}" />
      <property name="webstart.jnlp.local.name" value="local.jnlp" />
      <property name="webstart.jnlp.remote.name" value="remote.jnlp" />
      <property name="webstart.jnlp.single.name" value="webstart.jnlp" />

      <condition property="webstart.local.jnlp.location" 
                 value="${{webstart.jnlp.location.dir}}/${{webstart.jnlp.single.name}}">
         <istrue value="${{local.mode.only}}" />
      </condition>
      <condition property="webstart.local.jnlp.location" 
                 value="${{webstart.jnlp.location.dir}}/${{webstart.jnlp.local.name}}">
         <istrue value="${{local.and.remote}}" />
      </condition>

      <condition property="webstart.remote.jnlp.location" 
                 value="${{webstart.jnlp.location.dir}}/${{webstart.jnlp.single.name}}">
         <istrue value="${{remote.mode.only}}" />
      </condition>
      <condition property="webstart.remote.jnlp.location" 
                 value="${{webstart.jnlp.location.dir}}/${{webstart.jnlp.remote.name}}">
         <istrue value="${{local.and.remote}}" />
      </condition>
   </target>

   <target name="webstart:clean" depends="webstart:init">
      <delete dir="${{webstart.target.dir}}" />
      <delete file="${{webstart.war.file}}" />
   </target>

   <target name="webstart:check-conditions" depends="webstart:init">
      <condition property="webstart.needed">
         <istrue value="${{needs.webstart}}" />
      </condition>
   </target>

   <target name="webstart:macrodefs" depends="webstart:check-conditions" 
           if="webstart.needed">
      <macrodef name="webstart-signjar-macro"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <attribute name="lazy" default="true" />
         <element name="customize" optional="true" />
         <element name="jar-fileset" optional="false" />
         <element name="before-signjar" optional="true" />
         <sequential>
            <copy todir="${{webstart.jars.dir}}">
               <jar-fileset />
               <customize />
            </copy>
            <before-signjar />
            <signjar alias="${{webstart.signjar.alias}}"
                  keystore="${{webstart.signjar.keystore}}"
                  storepass="${{webstart.signjar.storepass}}" 
                  keypass="${{webstart.signjar.keypass}}"
                  lazy="@{{lazy}}">
               <fileset dir="${{webstart.jars.dir}}" includes="*.jar" />
            </signjar>
         </sequential>
      </macrodef>
      <presetdef name="webstart-signjar"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:webstart-signjar-macro>
            <jar-fileset>
               <fileset file="${{jar.local.location}}" />
               <fileset file="${{jar.remote.location}}" />
               <fileset dir="${{hibernate.dist}}">
                  <include name="cglib-full*.jar" />
                  <include name="commons-collection*.jar" />
                  <include name="ehcache*.jar" />
                  <include name="hibernate*.jar" />
                  <include name="jta.jar" />
                  <include name="odmg-*.jar" />
               </fileset>
               <fileset dir="${{genesis.dist}}">
                  <include name="genesis-annotation-jdk14-${{genesis.version}}.jar"/>
                  <include name="genesis-client-${{genesis.version}}.jar"/>
                  <include name="genesis-client-swing-${{genesis.version}}.jar" />
                  <include name="genesis-client-swt-${{genesis.version}}.jar" />
                  <include name="genesis-client-thinlet-${{genesis.version}}.jar" />
                  <include name="genesis-aspect-annotated-${{genesis.version}}.jar" />
               </fileset>
               <fileset dir="${{commons.dist}}">
                  <include name="commons-beanutils*.jar"/>
                  <include name="commons-jxpath*.jar"/>
                  <include name="commons-logging*.jar"/>
                  <include name="commons-digester*.jar"/>
                  <include name="commons-validator*.jar"/>
                  <include name="jakarta-oro-*.jar" />
                  <include name="reusable-components*.jar"/>
               </fileset>
               <fileset dir="${{aspectwerkz.dist}}">
                  <include name="aspectwerkz-*.jar" />
                  <include name="dom4j*.jar" />
                  <include name="jrexx-*.jar" />
                  <include name="trove*.jar" />
               </fileset>
               <fileset dir="${{backport175.dist}}">
                  <include name="backport175-*.jar" />
               </fileset>
               <fileset file="${{jdbc.driver}}" />
               <fileset dir="${{jboss.client}}">
                  <xsl:comment><![CDATA[ Common files (JBoss 3.2.x and JBoxx 4.x) ]]></xsl:comment> 
                  <include name="jboss-client.jar" />
                  <include name="jboss-common-client.jar" />
                  <include name="jboss-j2ee.jar" />
                  <include name="jboss-transaction-client.jar" />
                  <include name="jbosssx-client.jar" />
                  <include name="jnp-client.jar" />

                  <xsl:comment><![CDATA[ JBoss 4.x specific files  ]]></xsl:comment> 
                  <include name="jboss-remoting.jar" />
               </fileset>
            </jar-fileset>
         </genesis:webstart-signjar-macro>
      </presetdef>

      <macrodef name="webstart-jnlp-macro"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <attribute name="tofile" />
         <element name="customize-fileset" optional="true" />
         <element name="default-excludes" optional="true" />
         <element name="customize-information" optional="true" />
         <element name="customize-resources" optional="true" />
         <element name="customize-security" optional="true" />
         <element name="default-properties" optional="true" />
         <element name="properties" optional="true" />
         <sequential>
            <taskdef classpathref="orangevolt.path" 
                     resource="com/orangevolt/tools/ant/taskdefs.properties" />
            <jnlp codebase="${{webstart.jnlp.codebase}}" tofile="@{{tofile}}">
               <information>
                  <title>${webstart.jnlp.title}</title>
                  <vendor>${webstart.jnlp.vendor}</vendor>
                  <description>${webstart.jnlp.description}</description>
                  <description kind="tooltip">${webstart.jnlp.tooltip}</description>
                  <homepage href="${{webstart.jnlp.homepage}}" />
                  <icon href="${{webstart.jnlp.icon}}" />
                  <customize-information />
               </information>
               <customize-security />
               <resources>
                  <j2se version="${{webstart.jnlp.j2se.version}}" 
                        initial_heap_size="${{webstart.jnlp.j2se.initial.heap.size}}" 
                        max_heap_size="${{webstart.jnlp.j2se.max.heap.size}}" />
                  <customize-resources />
                  <jar download="eager">
                     <fileset dir="${{webstart.jars.dir}}" includes="**/*.jar">
                        <default-excludes />
                        <customize-fileset />
                     </fileset>
                  </jar>
                  <default-properties />
                  <properties />
               </resources>
               <application_desc main_class="${{genesisBasedApplication.mainClass}}" />
            </jnlp>
         </sequential>
      </macrodef>
      <presetdef name="webstart-jnlp"
                 uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:webstart-jnlp-macro>
            <default-excludes>
               <exclude name="${{jar.local.name}}" />
               <exclude name="${{jar.remote.name}}" />
               <exclude name="${{jdbc.driver.jar.name}}" />
            </default-excludes>
            <customize-security>
               <security>
                 <all_permissions />
               </security>
            </customize-security>
         </genesis:webstart-jnlp-macro>
      </presetdef>
      <presetdef name="webstart-jnlp-local"
                 uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:webstart-jnlp tofile="${{webstart.local.jnlp.location}}">
            <customize-resources>
               <jar download="eager" main="true">
                  <fileset dir="${{webstart.jars.dir}}" 
                           includes="${{jar.local.name}}" />
               </jar>
               <jar download="eager">
                  <fileset dir="${{webstart.jars.dir}}" 
                           includes="${{jdbc.driver.jar.name}}" />
               </jar>
            </customize-resources>
         </genesis:webstart-jnlp>
      </presetdef>
      <presetdef name="webstart-jnlp-remote"
                 uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:webstart-jnlp tofile="${{webstart.remote.jnlp.location}}">
            <customize-resources>
               <jar download="eager" main="true">
                  <fileset dir="${{webstart.jars.dir}}" 
                           includes="${{jar.remote.name}}" />
               </jar>
            </customize-resources>
            <default-properties>
               <property name="java.naming.provider.url" 
                         value="${{jboss.provider.url}}"/>
               <property name="java.naming.factory.initial" 
                         value="${{jboss.factory.initial}}"/>
            </default-properties>
         </genesis:webstart-jnlp>
      </presetdef>

      <macrodef name="webstart-copy-macro"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <attribute name="targetdir" default="${{webstart.target.dir}}" />
         <attribute name="sourcedir" default="${{webstart.sources.dir}}" />
         <element name="filter-tokens" optional="true" />
         <element name="includes" optional="true" />
         <element name="excludes" optional="true" />
         <sequential>
            <copy todir="@{{targetdir}}">
               <filterset>
                  <filter-tokens />
               </filterset>
               <fileset dir="@{{sourcedir}}">
                  <includes />
               </fileset>
            </copy>
            <copy todir="@{{targetdir}}">
               <fileset dir="@{{sourcedir}}">
                  <excludes />
               </fileset>
            </copy>
         </sequential>
      </macrodef>
      <presetdef name="webstart-copy"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:webstart-copy-macro>
            <filter-tokens>
               <filter token="app.main" 
                       value="${{genesisBasedApplication.mainClass}}"/>
               <filter token="pretty.name" 
                       value="${{genesisBasedApplication.prettyName}}"/>
            </filter-tokens>
            <includes>
               <include name="**/*.html"/>
            </includes>
            <excludes>
               <exclude name="**/*.jnlp"/>
               <exclude name="**/*.html"/>
               <exclude name="WEB-INF/web.xml"/>
            </excludes>
         </genesis:webstart-copy-macro>
      </presetdef>
   </target>

   <target name="-do-webstart-signjar">
      <genesis:webstart-signjar />
   </target>

   <target name="webstart:signjar" depends="define-call-task,webstart:macrodefs" 
           if="webstart.needed">
      <genesis:call target="-do-webstart-signjar" />
   </target>

   <target name="webstart:check-jnlp-conditions" 
           depends="webstart:check-conditions">
      <condition property="webstart.local.jnlp.needed">
         <and>
            <istrue value="${{webstart.needed}}" />
            <istrue value="${{local.mode}}" />
            <not>
               <uptodate>
                  <srcfiles dir="${{webstart.jars.dir}}" includes="**/*.jar" />
                  <mapper type="merge" to="${{webstart.local.jnlp.location}}" />
               </uptodate>
            </not>
         </and>
      </condition>

      <condition property="webstart.remote.jnlp.needed">
         <and>
            <istrue value="${{webstart.needed}}" />
            <istrue value="${{remote.mode}}" />
            <not>
               <uptodate>
                  <srcfiles dir="${{webstart.jars.dir}}" includes="**/*.jar" />
                  <mapper type="merge" to="${{webstart.remote.jnlp.location}}" />
               </uptodate>
            </not>
         </and>
      </condition>
   </target>

   <target name="-do-webstart-jnlp-local">
      <genesis:webstart-jnlp-local />
   </target>

   <target name="webstart:jnlp-local" 
           depends="define-call-task,webstart:macrodefs,webstart:check-jnlp-conditions"
           if="webstart.local.jnlp.needed">
      <genesis:call target="-do-webstart-jnlp-local" />
   </target>

   <target name="-do-webstart-jnlp-remote">
      <genesis:webstart-jnlp-remote />
   </target>

   <target name="webstart:jnlp-remote"
           depends="define-call-task,webstart:macrodefs,webstart:check-jnlp-conditions"
           if="webstart.remote.jnlp.needed">
      <genesis:call target="-do-webstart-jnlp-remote" />
   </target>

   <target name="webstart:jnlp" 
           depends="webstart:jnlp-local,webstart:jnlp-remote" />

   <target name="webstart:copy" depends="webstart:macrodefs"
           if="webstart.needed">
      <genesis:webstart-copy />
   </target>

   <target name="webstart:war" depends="webstart:check-conditions" 
           if="webstart.needed">
      <war warfile="${{webstart.war.file}}" basedir="${{webstart.target.dir}}" 
           webxml="${{webstart.web.xml}}" />
   </target>

   <target name="webstart" 
           depends="webstart:signjar,webstart:jnlp,webstart:copy,webstart:war" />

   <target name="deploy:additional-init" depends="webstart:init">
      <property name="needs.deploy" value="${{remote.mode}}" />
   </target>

   <target name="deploy:additional-check-conditions">
      <condition property="deploy.webstart.needed">
         <and>
            <istrue value="${{deploy.needed}}" />
            <istrue value="${{needs.webstart}}" />
         </and>
      </condition>
   </target>

   <target name="deploy:webstart" depends="deploy:check-conditions"
           if="deploy.webstart.needed">
      <copy todir="${{jboss.app.deploy}}">
         <fileset file="${{webstart.war.file}}" />
      </copy>
   </target>

   <target name="deploy" 
           depends="deploy:create-server,deploy:server-artifact,deploy:webstart" />
   
   <target name="all.with.webstart" 
           depends="compile,weaving,jar,server-artifact,webstart,deploy" 
           description="Builds the whole project and webstart artifacts"/>

   <target name="clean-webstart" depends="clean,webstart:clean,all.with.webstart"
           description="Deletes build artifacts, including webstart's, before 
                       building the project and webstart artifacts"/>
</project>
    </xsl:template>
</xsl:stylesheet> 
