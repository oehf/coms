<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" indent="yes" />
	<xsl:template match="ClinicalDocument">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="simpleA4"
					page-height="29.7cm" page-width="21cm" margin-top="1cm"
					margin-bottom="2cm" margin-left="2cm" margin-right="2cm">
					<fo:region-body />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="simpleA4">
				<fo:flow flow-name="xsl-region-body">
					<fo:block text-align="justify" hyphenate="true" xml:lang="de"
						font-family="Arial Unicode MS" font-size="11">
						<fo:block text-align="right" space-after="5mm">
							<fo:external-graphic width="19cm"
								src="url('file:///home/mis-admin/workspace/cc/web-frontend/src/main/java/de/ukhd/zim/coms/cc/web_frontend/consentcreator/files/skeleton/logo_siegel.gif')" />
						</fo:block>
						<xsl:apply-templates
							select="component/structuredBody/component/section/textRep/header" />
						<fo:block space-before="5mm" space-after="5mm" />
						<xsl:apply-templates select="recordTarget" />
						<fo:block space-before="5mm" space-after="5mm" />
						<xsl:apply-templates
							select="component/structuredBody/component/section/textRep" />
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template match="recordTarget">
		<xsl:apply-templates />
	</xsl:template>
	<!-- ========================= -->
	<!-- child element:patientRole -->
	<!-- ======================== -->
	<xsl:template match="patientRole">
		<fo:table table-layout="fixed" width="100%">
			<fo:table-column column-width="45mm" />
			<fo:table-column column-width="125mm" />
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:text>Patient</xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:value-of select="patient/name/given" />
							<xsl:text> </xsl:text>
							<xsl:value-of select="patient/name/family" />
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:text>Adresse</xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:value-of select="addr/streetAddressLine" />
							<fo:block />
							<xsl:value-of select="addr/postalCode" />
							<xsl:text> </xsl:text>
							<xsl:value-of select="addr/city" />
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:text>Geburtsdatum</xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:value-of select="patient/birthTime/@value" />
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:text>Aufklärende Stelle</xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:text>123</xsl:text>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	<!-- ========================= -->
	<!-- component -->
	<!-- ========================= -->
	<xsl:template match="component">
		<xsl:apply-templates select="structuredBody" />
		<xsl:apply-templates select="section" />
		\
	</xsl:template>
	<!-- ========================= -->
	<!-- structuredBody -->
	<!-- ========================= -->
	<xsl:template match="structuredBody">
		<xsl:apply-templates select="component" />
	</xsl:template>
	<!-- ========================= -->
	<!-- section -->
	<!-- ========================= -->
	<xsl:template match="section">
		<xsl:apply-templates select="textRep" />
	</xsl:template>
	<!-- ========================= -->
	<!-- PolicySet -->
	<!-- ========================= -->
	<xsl:template match="PolicySet">
		<xsl:apply-templates select="Description" />
	</xsl:template>
	<!-- ========================= -->
	<!-- child element: textRep -->
	<!-- ======================== -->
	<xsl:template match="textRep">
		<fo:block space-after="5mm">
			<xsl:apply-templates select="main" />
		</fo:block>
		<fo:block space-after="20mm">
			<xsl:apply-templates select="liste" />
		</fo:block>
		<fo:block>
			<xsl:apply-templates select="header2" />
		</fo:block>
		<fo:block space-after="5mm">
			<xsl:apply-templates select="consent" />
		</fo:block>
		<fo:block space-after="15mm">
			<xsl:apply-templates select="tabelle1" />
		</fo:block>
		<fo:block space-after="5mm">
			<xsl:value-of select="vermerk" />
		</fo:block>
		<fo:block text-align="center" space-after="10mm">
			<fo:leader leader-length="17cm" leader-pattern="rule"
				alignment-baseline="middle" rule-thickness="0.5pt" color="black" />
		</fo:block>
		<fo:block>
			<xsl:apply-templates select="tabelle2" />
		</fo:block>
	</xsl:template>
	<!-- ========================= -->
	<!-- child element: Datenschutzregeln -->
	<!-- ======================== -->
	<xsl:template match="Datenschutzregeln">
		<fo:list-block>
			<xsl:for-each select="//Description">
				<xsl:if test="parent::Rule">
					<fo:list-item>
						<fo:list-item-label end-indent="label-end()">
							<fo:block>
								-
							</fo:block>
						</fo:list-item-label>
						<fo:list-item-body start-indent="body-start()">
							<fo:block>
								<xsl:value-of select="." />
							</fo:block>
						</fo:list-item-body>
					</fo:list-item>
				</xsl:if>
			</xsl:for-each>
		</fo:list-block>
	</xsl:template>
	<!-- ========================= -->
	<!-- header -->
	<!-- ========================= -->
	<xsl:template match="header">
		<fo:block border-style="solid" border-width="0.1mm"
			background-color="#F3F3F3">
			<fo:block font-size="14pt" font-weight="bold" font-family="Arial"
				color="#000080" text-align="left" margin="2mm">
				<xsl:value-of select="." />
			</fo:block>
		</fo:block>
	</xsl:template>
	<!-- ========================= -->
	<!-- header2 -->
	<!-- ========================= -->
	<xsl:template match="header2">
		<fo:block border-width="1mm">
			<fo:block font-size="14pt" font-weight="bold" font-family="Arial"
				space-before="5mm" space-after="5mm" text-align="center">
				<xsl:value-of select="." />
			</fo:block>
		</fo:block>
	</xsl:template>
	<!-- ========================= -->
	<!-- liste -->
	<!-- ========================= -->
	<xsl:template match="liste">
		<fo:list-block>
			<xsl:for-each select="listitem">
				<fo:list-item>
					<fo:list-item-label end-indent="label-end()">
						<fo:block>
							<xsl:number format="1." />
						</fo:block>
					</fo:list-item-label>
					<fo:list-item-body start-indent="body-start()">
						<fo:block>
							<xsl:value-of select="." />
							<xsl:for-each select="Datenschutzregeln">
								<xsl:apply-templates select="." />
							</xsl:for-each>
						</fo:block>
					</fo:list-item-body>
				</fo:list-item>
			</xsl:for-each>
		</fo:list-block>
	</xsl:template>
	<!-- ========================= -->
	<!-- tabelle1 -->
	<!-- ========================= -->
	<xsl:template match="tabelle1">
		<fo:table table-layout="fixed" width="100%">
			<fo:table-column column-width="85mm" />
			<fo:table-column column-width="85mm" />
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:value-of select="ort" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:text> </xsl:text>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						height="40mm">
						<fo:block>
							<xsl:text> </xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-style="inset" border-width="0.1mm">
						<fo:block>
							<xsl:text> </xsl:text>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:value-of select="u1" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:value-of select="u2" />
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						height="40mm">
						<fo:block>
							<xsl:text> </xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-style="inset" border-width="0.1mm">
						<fo:block>
							<xsl:text> </xsl:text>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:value-of select="u3" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-style="inset" border-width="0.1mm">
						<fo:block>
							<xsl:text> </xsl:text>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	<!-- ========================= -->
	<!-- tabelle2 -->
	<!-- ========================= -->
	<xsl:template match="tabelle2">
		<fo:table table-layout="fixed" width="100%" background-color="#F3F3F3">
			<fo:table-column column-width="105mm" />
			<fo:table-column column-width="65mm" />
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:value-of select="n1" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:text>Ja </xsl:text>
							<fo:inline font-family="ZapfDingbats">&#x2751;</fo:inline>
							<xsl:text> Nein </xsl:text>
							<fo:inline font-family="ZapfDingbats">&#x2751;</fo:inline>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:value-of select="n2" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:text> Ja </xsl:text>
							<fo:inline font-family="ZapfDingbats">&#x2751;</fo:inline>
							<xsl:text> Nein </xsl:text>
							<fo:inline font-family="ZapfDingbats">&#x2751;</fo:inline>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:apply-templates select="n3" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-style="inset" border-width="0.1mm"
						padding-start="2mm" padding-end="2mm" padding-top="2mm"
						padding-bottom="2mm">
						<fo:block>
							<xsl:text> </xsl:text>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	<!-- ========================= -->
	<!-- linebreak -->
	<!-- ========================= -->
	<xsl:template match="br">
		<fo:block />
	</xsl:template>
	<!-- ========================= -->
	<!-- bold -->
	<!-- ========================= -->
	<xsl:template match="b">
		<fo:inline font-weight="bold">
			<xsl:value-of select="." />
		</fo:inline>
	</xsl:template>
	<!-- ========================= -->
	<!-- redd -->
	<!-- ========================= -->
	<xsl:template match="r">
		<fo:inline color="red">
			<xsl:value-of select="." />
		</fo:inline>
	</xsl:template>
</xsl:stylesheet>