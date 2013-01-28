<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@page import="org.openehealth.coms.cc.web_frontend.consentcreator.model.User"%>

<%  
	String context = (String) request.getAttribute("context");
	String emailaddress = (String) request.getAttribute("emailaddress");
	
	boolean participation = true;
	
	if (context.equalsIgnoreCase("deactivateUser") || context.equalsIgnoreCase("registerNoParticipation")) {
		participation = false;
	}
	
	String hrefPDF = request.getContextPath().toString() +"/PrivilegedCreateConsentServiceServlet?type=consentpdf&participation=" + participation + "&emailaddress=" + emailaddress ;
	
	String hrefStoreUnsignedConsent = request.getContextPath().toString()+ "/PrivilegedCreateConsentServiceServlet?type=storestandardunclearedconsent&participation=" + participation + "&emailaddress=" + emailaddress ;
	
 %>

<div id="content">
<h1>Schriftliche Einwilligungserklärung erstellen</h1>
<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
<div id="message" style="display: inline; color: #00427A;">Sie erstellen eine schriftliche Einwilligungserklärung für <%=emailaddress %></div>
<br/>
<br/>
<a  href="<%=request.getContextPath().toString()%>/PrivilegedDispatcher?type=helpwrittensignature"><img title="Hilfe zur schriftlichen Signatur" style="background-color: #00427A" src="images/inf.gif" ></img></a>
<br/>
<br/>

Text zum Prozedere mit Erklärung zum Ablauf der schriftlichen Signatur

<br/>
<br/>
<a class="mybutton" onClick="window.open('<%=hrefPDF %>','Einwilligungserklärung')">Einwilligungserklärung</a>
<br/>
<a class="mybutton" onClick="window.open('<%= hrefStoreUnsignedConsent %>','Einwilligungserklärung')">Einwilligungserklärung erstellen</a>

</div>