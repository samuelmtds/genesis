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
<project name="genesis-web-based-project" default="all" basedir="."
         xmlns:genesis="https://genesis.dev.java.net/nonav/ns/master_build.xml">
   <import file="master_build.xml" />

   <target name="shared:hibernate-doclet" 
           if="shared.hibernate.needed"
           depends="shared:check-hibernate-conditions,hibernate-macrodefs">
      <genesis:hibernatedoclet-remote />
   </target>

   <target name="client:pre-additional-init">
      <property name="client.dir" value="modules/web" />
   </target>

   <target name="client:additional-init">
      <path id="client.standard.javac.classpath">
         <path location="${{shared.classes.dir}}" />
         <path refid="j2ee.path" />
         <fileset dir="${{genesis.dist}}">
            <include name="genesis-shared-${{genesis.version}}.jar" />
         </fileset>
         <fileset dir="${{commons.dist}}">
            <include name="commons-beanutils*.jar" />
            <include name="commons-jxpath*.jar" />
            <include name="commons-logging*.jar" />
            <include name="commons-validator*.jar" />
            <include name="reusable-components*.jar" />
         </fileset>
         <fileset dir="${{hibernate.dist}}" includes="hibernate2.jar" />
      </path>
   </target>

   <target name="weaving:additional-init">
      <property name="weaving.timestamp.file" 
                location="${{weaving.dir}}/${{timestamp.file}}" />

      <property name="weaving.xml.definition.dir" 
                location="${{shared.sources.dir}}/META-INF" />

      <property name="weaving.xml" 
                 value="${{weaving.xml.definition.dir}}/aop.xml"/>

      <path id="weaving.standard.classpath">
         <path path="${{genesis.dist}}/genesis-aspect-annotated-${{genesis.version}}.jar" />
         <path refid="shared.javac.classpath" />
         <path refid="j2ee.path" />
      </path>
   </target>

   <target name="weaving:clean" if="weaving.clean" 
           depends="weaving:define-conditions">
      <delete dir="${{weaving.dir}}" />
      <delete dir="${{weaving.timestamp.file}}" />
   </target>

   <target name="weaving:pre-define-classpath">
      <xsl:comment><![CDATA[ Override to define weaving.overriden.classpath or 
         weaving.additional.classpath ]]></xsl:comment> 
   </target>

   <target name="weaving:check-classpath-conditions" depends="weaving:init">
      <condition property="weaving.set.custom.classpath">
         <isreference refid="weaving.overriden.classpath" type="path" />
      </condition>
      <condition property="weaving.set.additional.classpath">
         <and>
            <not>
               <isset property="weaving.set.custom.classpath" />
            </not>
            <isreference refid="weaving.additional.classpath" />
         </and>
      </condition>
      <condition property="weaving.set.standard.classpath">
         <and>
            <not>
               <isset property="weaving.set.additional.classpath" />
            </not>
            <not>
               <isset property="weaving.set.custom.classpath" />
            </not>
         </and>
      </condition>
   </target>

   <target name="weaving:define-overriden-classpath"
           depends="weaving:init" if="weaving.set.custom.classpath">
      <path id="weaving.classpath">
         <path refid="weaving.overriden.classpath" />
      </path>
   </target>

   <target name="weaving:define-classpath-with-extensions" 
           depends="weaving:init" if="weaving.set.additional.classpath">
      <path id="weaving.classpath">
         <path refid="weaving.standard.classpath" />
         <path refid="weaving.additional.classpath" />
      </path>
   </target>

   <target name="weaving:define-standard-classpath"
           depends="weaving:init" if="weaving.set.standard.classpath">
      <path id="weaving.classpath">
         <path refid="weaving.standard.classpath" />
      </path>
   </target>

   <target name="weaving:define-classpath" 
           depends="weaving:init,weaving:pre-define-classpath,
                    weaving:check-classpath-conditions,
                    weaving:define-overriden-classpath,
                    weaving:define-classpath-with-extensions,
                    weaving:define-standard-classpath"/>

   <target name="weaving:define-default-copy-fileset">
      <condition property="default-shared-copy-fileset" value="${{shared.annotated.dir}}">
         <istrue value="${{shared.annotationc.tasks.needed}}" />
      </condition>
      <condition property="default-shared-copy-fileset" value="${{shared.classes.dir}}">
         <not>      
            <istrue value="${{shared.annotationc.tasks.needed}}" />
         </not>
      </condition>
   </target>

   <target name="weaving:check-conditions" depends="weaving:init,weaving:define-default-copy-fileset">
      <condition property="weaving.needed">
         <and>
            <istrue value="${{needs.weaving}}" />
            <or>
               <not>
                  <available file="${{weaving.dir}}" />
               </not>
               <not>
                  <uptodate>
                     <srcfiles dir="${{default-shared-copy-fileset}}" 
                               includes="**/*.class" />
                     <mapper type="merge" to="${{weaving.timestamp.file}}" />
                  </uptodate>
               </not>
            </or>
         </and>
      </condition>
   </target>

   <target name="weaving:copy-files" depends="weaving:check-conditions,weaving:macrodefs" 
           if="weaving.needed">
      <genesis:call target="-do-weaving-copy-files" />
   </target>

   <target name="-do-weaving-copy-files">
      <genesis:weaving-copy targetdir="${{preweaving.dir}}" 
                            xml="${{weaving.xml}}" />
   </target>

   <target name="-do-weaving" 
           depends="weaving:define-classpath,weaving:copy-files,
                    define-aw-macros"
           if="weaving.needed">
      <genesis:awc classpath="weaving.classpath" 
                   sourcepath="${{preweaving.dir}}"
                   destdir="${{weaving.dir}}"
                   touchfile="${{weaving.timestamp.file}}" />
   </target>

   <target name="weaving" depends="-do-weaving,-do-custom-weaving" />

   <target name="jar:additional-init">
      <property name="jar.client.name"
               value="${{genesisBasedApplication.name}}-client.jar" />
      <property name="jar.client.location" 
               location="${{build.dir}}/${{jar.client.name}}" />
      <property name="jar.shared.name"
               value="${{genesisBasedApplication.name}}-shared-weaved.jar" />
      <property name="jar.shared.location" 
               location="${{build.dir}}/${{jar.shared.name}}" />
   </target>

   <target name="jar:clean" depends="jar:init">
      <delete file="${{jar.client.location}}" />
      <delete file="${{jar.shared.location}}" />
   </target>

   <target name="jar:additional-check-conditions">
      <condition property="jar.client.needed">
         <istrue value="${{needs.jar}}" />
      </condition>
   </target>

   <target name="jar:additional-macrodefs" if="jar.needed">
      <presetdef name="jar-client"
                 uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:jar sourcesdir="${{client.sources.dir}}"/>
      </presetdef>
      <presetdef name="jar-shared"
                 uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:jar sourcesdir="${{shared.sources.dir}}">
            <additional-filesets>
               <fileset dir="${{shared.hibernate.dir}}">
                  <patternset refid="classes.patternset" />
                  <exclude name="jboss-service.xml" />
               </fileset>
            </additional-filesets>
         </genesis:jar>
      </presetdef>
   </target>
   
   <target name="-do-jar-client">
      <genesis:jar-client destfile="${{jar.client.location}}" 
                         classesdir="${{client.classes.dir}}" />
   </target>

   <target name="jar:client" 
           depends="define-call-task,jar:check-conditions,jar:macrodefs" 
           if="jar.client.needed">
      <genesis:call target="-do-jar-client" />
   </target>

   <target name="-do-jar-shared">
      <genesis:jar-shared destfile="${{jar.shared.location}}" 
                         classesdir="${{weaving.dir}}" />
   </target>

   <target name="jar:shared" depends="define-call-task,jar:check-conditions,jar:macrodefs" 
           if="jar.shared.needed">
      <genesis:call target="-do-jar-shared" />
   </target>

   <target name="jar" depends="jar:client,jar:shared,-do-custom-jar" />

   <target name="server-artifact:additional-init">
      <property name="server.client.artifact.name" 
               value="${{genesisBasedApplication.name}}.war" />
      <property name="server.client.artifact.location" 
               location="${{build.dir}}/${{server.client.artifact.name}}" />

      <property name="client.lib.dir" value="${{client.dir}}/lib" />
      <property name="client.content.dir" value="${{client.dir}}/content" />
      <property name="client.webxml.location" location="${{client.content.dir}}/WEB-INF/web.xml" />
   </target>

   <target name="server-artifact:additional-clean">
      <delete file="${{server.client.artifact.location}}" />
   </target>

   <target name="server-artifact:additional-macrodefs" if="server.artifact.needed">
      <presetdef name="sar"
                 uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <genesis:sar-macro>
            <additional-fileset>
               <fileset dir="${{genesis.dist}}">
                  <include name="genesis-annotation-jdk14-${{genesis.version}}.jar" />
                  <include name="genesis-aspect-annotated-${{genesis.version}}.jar" />
               </fileset>
               <fileset dir="${{aspectwerkz.dist}}">
                  <include name="aspectwerkz-*.jar" />
                  <include name="dom4j*.jar" />
                  <include name="jrexx*.jar" />
                  <include name="qdox*.jar" />
                  <include name="trove*.jar" />
               </fileset>
               <fileset dir="${{backport175.dist}}">
                  <include name="backport175-*.jar" />
               </fileset>
               <zipfileset src="${{jar.shared.location}}" />
            </additional-fileset>
         </genesis:sar-macro>
      </presetdef>

      <macrodef name="war"
                uri="https://genesis.dev.java.net/nonav/ns/master_build.xml">
         <element name="customize" optional="true"/>
         <sequential>
            <war destfile="${{server.client.artifact.location}}" webxml="${{client.webxml.location}}">
               <zipfileset dir="${{client.content.dir}}" excludesfile="${{client.webxml.location}}" />
               <lib file="${{jar.client.location}}" />
               <lib dir="${{client.lib.dir}}" />
               <customize />
            </war>
         </sequential>
      </macrodef>
   </target>

   <target name="-do-server-artifact">
      <genesis:sar />
      <genesis:war />
   </target>

   <target name="deploy:additional-init">
      <property name="needs.deploy" value="true" />
   </target>

   <target name="-do-deploy-server-artifact">
      <genesis:deploy-server-artifact>
         <filesets>
            <fileset file="${{server.client.artifact.location}}" />
         </filesets>
      </genesis:deploy-server-artifact>
   </target>
</project>
    </xsl:template>
</xsl:stylesheet> 
