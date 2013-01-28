<%-- 
Diese Seite stellt das Menü (Include) dar.
--%>
<%@page import="org.openehealth.coms.cc.web_frontend.consentcreator.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<%  
	String showPDF = request.getContextPath().toString() +"/RetrieveConsentServiceServlet?type=retrieveConsent";
	
 %>


<div id="navigation">

<%@ include file="logo.jsp"%>

<ul style="margin-top: 25px">
	<li id="id_li_de_homepage" style="border-top: 1px solid #006D6B">
		<a href="<%=request.getContextPath().toString()%>/Dispatcher?type=homepage" title="Startseite" id="id_a_de_homepage">
		Startseite
		</a>
	</li>


	<li id="id_li_de_createconsentpage">
		<a href="<%=request.getContextPath().toString()%>/UserDispatcher?type=createconsentpage" title="Einwilligungserklärung erstellen" id="id_a_de_loginpage">
		Einwilligungserklärung erstellen
		</a>
	</li>

	<li id="id_li_de_showconsentpage">
		<a onClick="window.open('<%=showPDF%>','Aktuelle_Einwilligungserklärung','width=900,height=800')" title="Einwilligungserklärung anzeigen" id="id_a_de_loginpage">
		Einwilligungserklärung anzeigen
		</a>
	</li>


	<li id="id_li_de_revokeconsentpage">
		<a href="<%=request.getContextPath().toString()%>/UserDispatcher?type=revokeconsentpage&endparticipation=false" title="Einwilligungserklärung zurücksetzen" id="id_a_de_contactpage">
		Einwilligungserklärung zurücksetzen
		</a>
	</li>


	<li id="id_li_de_revokeconsentpage">
		<a href="<%=request.getContextPath().toString()%>/UserDispatcher?type=revokeconsentpage&endparticipation=true" title="Teilnahme beenden" id="id_a_de_legalnoticepage">
		Teilnahme beenden
		</a>
	</li>
		
	<li id="id_li_de_revokeconsentpage">
		<a href="<%=request.getContextPath().toString()%>/UserDispatcher?type=editdetailspage" title="Eigene Daten bearbeiten" id="id_a_de_contactpage">
		Eigene Daten bearbeiten
		</a>
	</li>
	
	<li id="id_li_de_logoutpage">
		<a href="<%=request.getContextPath().toString()%>/LogoutServiceServlet?type=logout"
		title="Abmelden" id="id_a_de_legalnoticepage" onClick="
		if (!confirm('Durch best&auml;tigen werden Sie vom System abgemeldet.'))
		return false;">
		Abmelden
		</a>
	</li>
</ul>
</div>
