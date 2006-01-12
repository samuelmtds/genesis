<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:project="http://www.netbeans.org/ns/project/1"
                xmlns:genesis="https://genesis.dev.java.net/ns/netbeans/projecttype/1"
                xmlns:xalan="http://xml.apache.org/xslt"
                exclude-result-prefixes="xalan project genesis">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="4"/>
    <xsl:template match="/">

        <xsl:comment><![CDATA[
*** GENERATED FROM project.xml - DO NOT EDIT  ***
***         EDIT ../build.xml INSTEAD         ***
]]></xsl:comment>
        <project name="genesis-based-project-impl" default="all" basedir="..">
            <xsl:if test="not(/project:project/project:configuration/genesis:data/genesis:type='freeform')">
               <import>
                  <xsl:attribute name="file"><xsl:value-of select="/project:project/project:configuration/genesis:data/genesis:type" />_build.xml</xsl:attribute>
               </import>
            </xsl:if>

            <target name="-pre-pre-init">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="-pre-init" depends="-pre-pre-init">
               <property file="nbproject/project.properties" />
            </target>
        </project>
    </xsl:template>
</xsl:stylesheet>
