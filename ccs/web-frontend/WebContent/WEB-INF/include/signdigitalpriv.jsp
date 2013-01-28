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

	Cookie[] cookies = request.getCookies();

	User consentUser = (User) request.getSession().getAttribute("consentUser");
	
	User author = (User) request.getSession().getAttribute("user");
	
	String server = request.getServerName();
	int port = request.getServerPort();
	
 %>

<div id="content">
<h1>Digitale Einwilligungserklärung erstellen</h1>
<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
<div id="message" style="display: inline; color: #00427A;">Sie erstellen eine digitale Einwilligungserklärung für <%=emailaddress %></div>
<br/>
<br/>
<a  href="<%=request.getContextPath().toString()%>/PrivilegedDispatcher?type=helpdigitalsignature"><img title="Hilfe zur digitalen Signatur" style="background-color: #00427A" src="images/inf.gif" ></img></a>
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
    		
    		<param name="name" value="<%= consentUser.getName() %>" />
    		<param name="forename" value="<%= consentUser.getForename() %>" />
    		<param name="emailaddress" value="<%= consentUser.getEmailaddress() %>" />
    		<param name="privileges" value="<%=author.getPrivileges() %>" />
    		<param name="mode" value="signonly" />
    		<param name="endparticipation" value="<%=!participation %>" />
			
			</applet>

</div>