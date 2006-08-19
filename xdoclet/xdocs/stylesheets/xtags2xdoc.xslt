<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/">
<document>
    <properties>
        <title>Tag Reference</title>
    </properties>

    <head>
        <link rel="shortcut icon" href="../favicon.ico" />
        <link rel="icon" href="../favicon.ico" />
    </head>

    <body>
        <xsl:for-each select="xdoclet/namespace">
            <section>
                <xsl:attribute name="name"><xsl:text>@</xsl:text><xsl:value-of select="name"/><xsl:text> Tag Reference</xsl:text></xsl:attribute>
                <p>
                    <xsl:value-of select="usage-description"/>
                </p>
                <xsl:if test="count(condition-description) &gt; 0">
                        <p>Applies to: <xsl:value-of select="condition-description"/></p>
                </xsl:if>
                <div>
                    <xsl:attribute name="id"><xsl:text>source</xsl:text></xsl:attribute>
                    <subsection>
                    <xsl:attribute name="name"><xsl:text>@</xsl:text><xsl:value-of select="name"/><xsl:text> Class Level Tags</xsl:text></xsl:attribute>
                    <xsl:for-each select="tags/tag[level='class']">
                        <xsl:sort select="name"/>
	                        <a>
	                            <xsl:if test="contains(./unique, 'true')">
	                                <xsl:attribute name="href"><xsl:text>#@</xsl:text><xsl:value-of select="name"/><xsl:text> (0..1)</xsl:text></xsl:attribute>
	                            </xsl:if>
	                            <xsl:if test="not(contains(./unique, 'true'))">
	                                <xsl:attribute name="href"><xsl:text>#@</xsl:text><xsl:value-of select="name"/><xsl:text> (0..*)</xsl:text></xsl:attribute>
	                            </xsl:if>
	                            <xsl:attribute name="title"><xsl:value-of select="normalize-space(usage-description)"/></xsl:attribute>
	                            <xsl:text>@</xsl:text><xsl:value-of select="name"/>
	                        </a><br/>
		                </xsl:for-each>
                    </subsection>
                </div>
                <div>
                    <xsl:attribute name="id"><xsl:text>source</xsl:text></xsl:attribute>
                    <subsection>
                    <xsl:attribute name="name"><xsl:text>@</xsl:text><xsl:value-of select="name"/><xsl:text> Method Level Tags</xsl:text></xsl:attribute>
                    <xsl:for-each select="tags/tag[level='method']">
                        <xsl:sort select="name"/>
	                        <a>
	                            <xsl:if test="contains(./unique, 'true')">
	                                <xsl:attribute name="href"><xsl:text>#@</xsl:text><xsl:value-of select="name"/><xsl:text> (0..1)</xsl:text></xsl:attribute>
	                            </xsl:if>
	                            <xsl:if test="not(contains(./unique, 'true'))">
	                                <xsl:attribute name="href"><xsl:text>#@</xsl:text><xsl:value-of select="name"/><xsl:text> (0..*)</xsl:text></xsl:attribute>
	                            </xsl:if>
	                            <xsl:attribute name="title"><xsl:value-of select="normalize-space(usage-description)"/></xsl:attribute>
	                            <xsl:text>@</xsl:text><xsl:value-of select="name"/>
	                        </a><br/>
                    </xsl:for-each>
                    </subsection>
                </div>
                <div>
                    <xsl:attribute name="id"><xsl:text>source</xsl:text></xsl:attribute>
                    <subsection>
                    <xsl:attribute name="name"><xsl:text>@</xsl:text><xsl:value-of select="name"/><xsl:text> Field Level Tags</xsl:text></xsl:attribute>
                    <xsl:for-each select="tags/tag[level='field']">
                        <xsl:sort select="name"/>
	                        <a>
	                            <xsl:if test="contains(./unique, 'true')">
	                                <xsl:attribute name="href"><xsl:text>#@</xsl:text><xsl:value-of select="name"/><xsl:text> (0..1)</xsl:text></xsl:attribute>
	                            </xsl:if>
	                            <xsl:if test="not(contains(./unique, 'true'))">
	                                <xsl:attribute name="href"><xsl:text>#@</xsl:text><xsl:value-of select="name"/><xsl:text> (0..*)</xsl:text></xsl:attribute>
	                            </xsl:if>
	                            <xsl:attribute name="title"><xsl:value-of select="normalize-space(usage-description)"/></xsl:attribute>
	                            <xsl:text>@</xsl:text><xsl:value-of select="name"/>
	                        </a><br/>
                    </xsl:for-each>
                    </subsection>
                </div>
            </section>
            <section>
                <xsl:attribute name="name"><xsl:text>@</xsl:text><xsl:value-of select="name"/><xsl:text> Class Level Tag Usage</xsl:text></xsl:attribute>
                <xsl:for-each select="tags/tag[level='class']">
                    <xsl:sort select="name"/>
                        <subsection>
                            <xsl:if test="contains(./unique, 'true')">
                                <xsl:attribute name="name"><xsl:text>@</xsl:text><xsl:value-of select="name"/><xsl:text> (0..1)</xsl:text></xsl:attribute>
                            </xsl:if>
                            <xsl:if test="not(contains(./unique, 'true'))">
                                <xsl:attribute name="name"><xsl:text>@</xsl:text><xsl:value-of select="name"/><xsl:text> (0..*)</xsl:text></xsl:attribute>
                            </xsl:if>
                            <p><xsl:value-of select="usage-description"/></p>
                            <xsl:if test="count(condition-description) &gt; 0">
                                    <p>Applies to: <xsl:value-of select="condition-description"/></p>
                            </xsl:if>
                            <xsl:if test="count(parameter) &gt; 0">
                                <table>
                                    <tr>
                                        <th>Parameter</th>
                                        <th>Type</th>
                                        <th>Applicability</th>
                                        <th>Description</th>
                                        <th>Mandatory</th>
                                    </tr>
                                    <xsl:for-each select="parameter">
                                        <tr valign="top">
                                            <td><xsl:value-of select="name"/></td>
                                            <td><xsl:value-of select="@type"/></td>
                                            <td><xsl:value-of select="condition-description"/></td>
                                            <td><xsl:value-of select="usage-description"/>
                                            <xsl:if test="count(default) &gt; 0">
                                                  <br/>Default value(s):
                                                  <br/><xsl:value-of select="default"/>
                                            </xsl:if>
                                                <!-- give the allowed values to the enduser -->
                                                <xsl:if test="count(option-sets) &gt;0" >
                                                    <xsl:call-template name="process-param-options">
                                                        <xsl:with-param name="node" select="option-sets"/>
                                                    </xsl:call-template>
                                                </xsl:if>
                                            </td>
                                            <td>
                                                <xsl:if test="contains(./mandatory, 'true')">
                                                    <b><xsl:value-of select="mandatory"/></b>
                                                </xsl:if>
                                                <xsl:if test="not(contains(./mandatory, 'true'))">
                                                    <xsl:value-of select="mandatory"/>
                                                </xsl:if>
                                            </td>
                                        </tr>
                                    </xsl:for-each>
                                </table>
                            </xsl:if>
                        </subsection>
                </xsl:for-each>
            </section>
            <section>
                <xsl:attribute name="name"><xsl:text>@</xsl:text><xsl:value-of select="name"/><xsl:text> Method Level Tag Usage</xsl:text></xsl:attribute>
                <xsl:for-each select="tags/tag[level='method']">
                    <xsl:sort select="name"/>
                        <subsection>
                            <xsl:if test="contains(./unique, 'true')">
                                <xsl:attribute name="name"><xsl:text>@</xsl:text><xsl:value-of select="name"/><xsl:text> (0..1)</xsl:text></xsl:attribute>
                            </xsl:if>
                            <xsl:if test="not(contains(./unique, 'true'))">
                                <xsl:attribute name="name"><xsl:text>@</xsl:text><xsl:value-of select="name"/><xsl:text> (0..*)</xsl:text></xsl:attribute>
                            </xsl:if>
                            <p><xsl:value-of select="usage-description"/></p>
                            <xsl:if test="count(condition-description) &gt; 0">
                                    <p>Applies to: <xsl:value-of select="condition-description"/></p>
                            </xsl:if>
                            <xsl:if test="count(parameter) &gt; 0">
                                <table>
                                    <tr>
                                        <th>Parameter</th>
                                        <th>Type</th>
                                        <th>Applicability</th>
                                        <th>Description</th>
                                        <th>Mandatory</th>
                                    </tr>
                                    <xsl:for-each select="parameter">
                                        <tr valign="top">
                                            <td><xsl:value-of select="name"/></td>
                                            <td><xsl:value-of select="@type"/></td>
                                            <td><xsl:value-of select="condition-description"/></td>
                                            <td><xsl:value-of select="usage-description"/>
                                          <xsl:if test="count(default) &gt; 0">
                                                      <br/>Default value(s):
                                                      <br/><xsl:value-of select="default"/>
                                              </xsl:if>
                                                <!-- give the allowed values to the enduser -->
                                                <xsl:if test="count(option-sets) &gt;0" >
                                                    <xsl:call-template name="process-param-options">
                                                        <xsl:with-param name="node" select="option-sets"/>
                                                    </xsl:call-template>
                                                </xsl:if>
                                            </td>
                                            <td>
                                                <xsl:if test="contains(./mandatory, 'true')">
                                                    <b><xsl:value-of select="mandatory"/></b>
                                                </xsl:if>
                                                <xsl:if test="not(contains(./mandatory, 'true'))">
                                                    <xsl:value-of select="mandatory"/>
                                                </xsl:if>
                                            </td>
                                        </tr>
                                    </xsl:for-each>
                                </table>
                            </xsl:if>
                        </subsection>
                </xsl:for-each>
            </section>
             <section>
                <xsl:attribute name="name"><xsl:text>@</xsl:text><xsl:value-of select="name"/><xsl:text> Field Level Tag Usage</xsl:text></xsl:attribute>
                <xsl:for-each select="tags/tag[level='field']">
                    <xsl:sort select="name"/>
                        <subsection>
                            <xsl:if test="contains(./unique, 'true')">
                                <xsl:attribute name="name"><xsl:text>@</xsl:text><xsl:value-of select="name"/><xsl:text> (0..1)</xsl:text></xsl:attribute>
                            </xsl:if>
                            <xsl:if test="not(contains(./unique, 'true'))">
                                <xsl:attribute name="name"><xsl:text>@</xsl:text><xsl:value-of select="name"/><xsl:text> (0..*)</xsl:text></xsl:attribute>
                            </xsl:if>
                            <p><xsl:value-of select="usage-description"/></p>
                            <xsl:if test="count(condition-description) &gt; 0">
                                    <p>Applies to: <xsl:value-of select="condition-description"/></p>
                            </xsl:if>
                            <xsl:if test="count(parameter) &gt; 0">
                                <table>
                                    <tr>
                                        <th>Parameter</th>
                                        <th>Type</th>
                                        <th>Applicability</th>
                                        <th>Description</th>
                                        <th>Mandatory</th>
                                    </tr>
                                    <xsl:for-each select="parameter">
                                        <tr valign="top">
                                            <td><xsl:value-of select="name"/></td>
                                            <td><xsl:value-of select="@type"/></td>
                                            <td><xsl:value-of select="condition-description"/></td>
                                            <td><xsl:value-of select="usage-description"/>
                                            <xsl:if test="count(default) &gt; 0">
                                                  <br/>Default value(s):
                                                  <br/><xsl:value-of select="default"/>
                                            </xsl:if>
                                                <!-- give the allowed values to the enduser -->
                                                <xsl:if test="count(option-sets) &gt;0" >
                                                    <xsl:call-template name="process-param-options">
                                                        <xsl:with-param name="node" select="option-sets"/>
                                                    </xsl:call-template>
                                                </xsl:if>
                                            </td>
                                            <td>
                                                <xsl:if test="contains(./mandatory, 'true')">
                                                    <b><xsl:value-of select="mandatory"/></b>
                                                </xsl:if>
                                                <xsl:if test="not(contains(./mandatory, 'true'))">
                                                    <xsl:value-of select="mandatory"/>
                                                </xsl:if>
                                            </td>
                                        </tr>
                                    </xsl:for-each>
                                </table>
                            </xsl:if>
                        </subsection>
                </xsl:for-each>
            </section>
       </xsl:for-each>
    </body>
</document>
</xsl:template>

<!-- this template describes the options that are valid for a parameter -->
<xsl:template name="process-param-options">
    <xsl:param name="node"/>
       <br>Valid options are:</br>
      <xsl:for-each select="$node/option-set/options/option">
              <br><xsl:value-of select="."/></br>
      </xsl:for-each>                                            
      <xsl:if test="count($node/option-set/default) &gt; 0">
              <br>Default value(s):</br>
              <xsl:for-each select="$node/option-set">
                    <br>
                        <xsl:value-of select="default"/>
                  <xsl:if test="condition/@type='type'">
                  (for <xsl:value-of select="condition/condition-parameter"/>) <!-- XXX need to process nested conditions -->
                  </xsl:if>
                  </br>
              </xsl:for-each>
      </xsl:if>
</xsl:template>
</xsl:stylesheet>
