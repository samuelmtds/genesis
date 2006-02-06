<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:project="http://www.netbeans.org/ns/project/1"
                xmlns:g="https://genesis.dev.java.net/ns/netbeans/projecttype/1"
                xmlns:xalan="http://xml.apache.org/xslt"
                xmlns:genesis="https://genesis.dev.java.net/nonav/ns/master_build.xml"
                exclude-result-prefixes="xalan project g">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="3"/>
    <xsl:template match="/">

        <xsl:comment><![CDATA[
*** GENERATED FROM project.xml - DO NOT EDIT  ***
***         EDIT ../build.xml INSTEAD         ***
]]></xsl:comment>
        <project name="genesis-based-project-impl" default="all" basedir="..">
            <xsl:if test="not(/project:project/project:configuration/g:data/g:type='freeform')">
               <import>
                  <xsl:attribute name="file"><xsl:value-of select="/project:project/project:configuration/g:data/g:type" />_build.xml</xsl:attribute>
               </import>
            </xsl:if>

            <target name="-pre-pre-init">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="-do-pre-init">
               <property file="nbproject/project.properties" />
            </target>
            <target name="-post-pre-init">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="-pre-init" depends="-pre-pre-init,-do-pre-init,-post-pre-init" />

            <target name="build-jar" depends="compile,weaving,jar" />
            <target name="clean-jar" 
                    depends="pre-clean,shared:clean,client:clean,weaving:clean,jar:clean" />

         <xsl:if test="/project:project/project:configuration/g:data/g:source-packages/g:client/g:compilation">
            <target name="client:pre-init">
               <path id="client.additional.javac.classpath">
               <xsl:for-each select="/project:project/project:configuration/g:data/g:source-packages/g:client/g:compilation/g:path">
                  <path location="{.}" />
               </xsl:for-each>
               </path>
            </target>
         </xsl:if>

         <xsl:if test="/project:project/project:configuration/g:data/g:source-packages/g:shared/g:compilation">
            <target name="shared:pre-init">
               <path id="shared.additional.javac.classpath">
               <xsl:for-each select="/project:project/project:configuration/g:data/g:source-packages/g:shared/g:compilation/g:path">
                  <path location="{.}" />
               </xsl:for-each>
               </path>
            </target>
         </xsl:if>

         <xsl:if test="/project:project/project:configuration/g:data/g:execution">
            <target name="run:pre-init">
               <path id="run.additional.classpath">
               <xsl:for-each select="/project:project/project:configuration/g:data/g:execution/g:path">
                  <path location="{.}" />
               </xsl:for-each>
               </path>
            </target>
         </xsl:if>

         <xsl:if test="/project:project/project:configuration/g:data/g:webstart">
            <target name="-do-webstart-signjar">
               <genesis:webstart-signjar>
                  <customize>
                     <xsl:for-each select="/project:project/project:configuration/g:data/g:webstart/g:file">
                        <fileset file="{.}" />
                     </xsl:for-each>
                  </customize>
               </genesis:webstart-signjar>
            </target>
         </xsl:if>

        </project>
    </xsl:template>
</xsl:stylesheet>