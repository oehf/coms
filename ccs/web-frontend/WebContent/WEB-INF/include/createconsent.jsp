<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@page import="org.openehealth.coms.cc.web_frontend.consentcreator.model.User"%>

<% 
	Cookie[] cookies = request.getCookies();

	User consentUser = (User) request.getSession().getAttribute("user");
	User author = null;
	int privileges = consentUser.getPrivileges();
	
	if (consentUser.getPrivileges() >=1) {
		
		author = consentUser;
		consentUser = (User) request.getSession().getAttribute("consentUser");
		privileges = author.getPrivileges();
	}
	
	String server = request.getServerName();
	int port = request.getServerPort();
	
%>

<div id="content">
<h1>Einwilligungserklärung erstellen</h1>
<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
Erstellen Sie eine neue individuelle Einwilligungserklärung.
<br/>
<br/>

<applet codebase="<%=request.getContextPath().toString()%>/applet" code="org.openehealth.coms.cc.consent_applet.applet.ConsentApplet" 
			WIDTH="750" HEIGHT="800"  ARCHIVE="ssapplet.jar, commons-io-1.3.1.jar">
			
			<param name="relURL" value="http://<%=server%>:<%=port%><%=request.getContextPath().toString()%>"/>
			<param name="cookieName" value="<%= cookies[0].getName() %>" />
    		<param name="cookieValue" value="<%= cookies[0].getValue() %>" />
    		
    		<param name="name" value="<%= consentUser.getName() %>" />
    		<param name="forename" value="<%= consentUser.getForename() %>" />
    		<param name="emailaddress" value="<%= consentUser.getEmailaddress() %>" />
    		<param name="privileges" value="<%= privileges %>" />
    		<param name="mode" value="create" />
			
</applet>

</div>