
<%@page import="org.openehealth.coms.cc.web_frontend.consentcreator.model.User"%>
<%@page import="org.openehealth.coms.cc.web_frontend.org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%
	JSONArray list = (JSONArray) request.getAttribute("consentlist");

%>

<div id="content">
<h1>Einwilligungserklärung freischalten</h1>
<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
	<table class="table"border="0" style="margin-left:50px;margin-right:50px;width:700px;">
	
		<tr>
			<td>Name:</td> <td>Emailaddresse:</td> <td>Geburtsdatum:</td> <td>Geschlecht:</td> <td>Teilnahme beenden:</td> <td>Erstellt am:</td>
		</tr>
	
<%	for (int i = 0; i < list.length(); i++) {
	
	String participation = (String) list.getJSONObject(i).get("participation");
	boolean deactivate = false;
	String endParticipation = "Nein";
	
	if (participation.equalsIgnoreCase("false")) {
		deactivate = true;
		endParticipation = "Ja";
	}
	else {
	}
%>
		<tr>
			<td><%=list.getJSONObject(i).get("name") %>, <%=list.getJSONObject(i).get("forename") %></td> <td><%=list.getJSONObject(i).get("emailaddress") %></td> <td><%=list.getJSONObject(i).get("birthdate") %></td> <td><%=list.getJSONObject(i).get("gender") %></td> <td><%=endParticipation %></td> <td><%=list.getJSONObject(i).get("timestamp") %></td> <td><a  href="<%=request.getContextPath().toString()%>/ClearConsentServiceServlet?type=acceptconsent&endparticipation=<%=deactivate %>&emailaddress=<%=list.getJSONObject(i).get("emailaddress") %>"><img title="Einwilligungserklärung freischalten" style="background-color: #00427A" src="images/ok.gif" ></img></a></td> <td><a href="<%=request.getContextPath().toString()%>/ClearConsentServiceServlet?type=rejectconsent&emailaddress=<%=list.getJSONObject(i).get("emailaddress") %>"><img title="Einwilligungserklärung verwerfen" style="background-color: #00427A" src="images/del.gif"></img></a></td>
		</tr>
<% }
%>		
	</table>	
</div>
