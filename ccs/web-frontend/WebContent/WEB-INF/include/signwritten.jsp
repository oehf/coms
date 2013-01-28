<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@page import="org.openehealth.coms.cc.web_frontend.consentcreator.model.User"%>

<%  
	User signUser = (User) request.getSession().getAttribute("user");

	String title = "";
	String type = "";

	if (signUser.getPrivileges() == 0) {
		
		String endparticipation = (String) request.getAttribute("endparticipation");
		
		if (endparticipation.equalsIgnoreCase("true")) {
			title = "Teilnahme beenden";
			type = "endparticipation";
		}
		else {
			title = "Einwilligungserklärung zurücksetzen";
			type = "revokeconsent";
		}
	}
	
 %>

<div id="content">
<h1><%=title %></h1>
<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
<div id="message" style="display: inline; color: #00427A;"><%=title %> durch scrhriftliche Signatur</div>
<br/>
<br/>
<a  href="<%=request.getContextPath().toString()%>/UserDispatcher?type=helpwrittensignature"><img title="Hilfe zur schriftlichen Signatur" style="background-color: #00427A" src="images/inf.gif" ></img></a>
<br/>
<br/>

Text zum Prozedere mit Erklärung zum Ablauf der schriftlichen Signatur

<br/>
<br/>
<a class="mybutton" onClick="window.open('<%=request.getContextPath().toString()%>/RevokeConsentServiceServlet?type=revokeconsentpdf&participation=<%=type %>','Einwilligungserklärung')">Einwilligungserklärung</a>
<br/>
<a class="mybutton" onClick="window.open('<%=request.getContextPath().toString()%>/RevokeConsentServiceServlet?type=<%=type %>&context=written','Einwilligungserklärung')"><%=title %></a>

</div>