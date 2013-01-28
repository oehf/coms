<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<%@page import="org.openehealth.coms.cc.web_frontend.consentcreator.model.User"%>

<%  String endparticipation = (String) request.getAttribute("endparticipation");
	
	String text = "";
	String type = "";
	
	if (endparticipation.equalsIgnoreCase("true")) {
		text = "Teilnahme beenden";
		type = "endparticipation";
	}
	else {
		text = "Einwilligungserklärung zurücksetzen";
		type = "revokeconsent";
	}
	
	
	Cookie[] cookies = request.getCookies();

	User userS = (User) request.getSession().getAttribute("user");
	
	String server = request.getServerName();
	int port = request.getServerPort();
 %>

<div id="content">
<h1><%=text %></h1>
<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
<div id="message" style="display: inline; color: #00427A;"><%=text %> durch digitale Signatur</div>
<br/>
<br/>
<a  href="<%=request.getContextPath().toString()%>/UserDispatcher?type=helpdigitalsignature"><img title="Hilfe zur digitalen Signatur" style="background-color: #00427A" src="images/inf.gif" ></img></a>
<br/>
<br/>

Text zum Prozedere mit Erklärung zum Ablauf der digitalen Signatur

<br/>
<br/>


<applet codebase="<%=request.getContextPath().toString()%>/applet" code="org.openehealth.coms.cc.consent_applet.applet.ConsentApplet"
			WIDTH="750" HEIGHT="800"  ARCHIVE="ssapplet.jar, commons-io-1.3.1.jar">
			
			<param name="relURL" value="http://<%=server%>:<%=port%><%=request.getContextPath().toString()%>"/> <!--  TODO -->
			<param name="cookieName" value="<%= cookies[0].getName() %>" />
    		<param name="cookieValue" value="<%= cookies[0].getValue() %>" />
    		
    		<param name="name" value="<%= userS.getName() %>" />
    		<param name="forename" value="<%= userS.getForename() %>" />
    		<param name="emailaddress" value="<%= userS.getEmailaddress() %>" />
    		<param name="privileges" value="0" />
    		<param name="mode" value="signonly" />
    		<param name="endparticipation" value="<%=endparticipation %>" />
			
			</applet>

</div>