<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:project="http://www.netbeans.org/ns/project/1"
                xmlns:genesis="https://genesis.dev.java.net/ns/netbeans/projecttype/1"
                xmlns:xalan="http://xml.apache.org/xslt"
                exclude-result-prefixes="xalan project genesis">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="3"/>
    <xsl:template match="/">
        <xsl:comment> You may freely edit this file. </xsl:comment>
        <xsl:comment> (If you delete it and reopen the project it will be recreated.) </xsl:comment>
        
        <project name="genesis-based-project-build" default="all"
                 xmlns:genesis="https://genesis.dev.java.net/nonav/ns/master_build.xml">
            <import file="nbproject/build-impl.xml"/>
        </project>
    </xsl:template>
</xsl:stylesheet> 
