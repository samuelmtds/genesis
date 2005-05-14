<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

 <xsl:output method="xml" indent="yes" /> 
 
  <xsl:template match="/xdoclet">
    <doclipse>
    <xsl:apply-templates select="./namespace/tags/tag"/>
    </doclipse>
  </xsl:template>

 
  <xsl:template match="/xdoclet/namespace/tags/tag">
    <tag target="class">
      <xsl:attribute name="name">@<xsl:value-of select="./name"/></xsl:attribute>
      <xsl:attribute name="target"><xsl:value-of select="./level"/></xsl:attribute>
      <xsl:for-each select="./parameter">
      <attribute>
        <xsl:attribute name="name">
          <xsl:value-of select="./name"/>
        </xsl:attribute>

        <xsl:if test="./mandatory = 'true'">
        <xsl:attribute name="required">
          <xsl:value-of select="./mandatory"/>
        </xsl:attribute>
        </xsl:if>
      </attribute>
      </xsl:for-each>
    </tag>
  </xsl:template> 

</xsl:stylesheet>
