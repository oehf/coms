
<%@page import="org.openehealth.coms.cc.web_frontend.consentcreator.model.User"%>
<%@page import="org.openehealth.coms.cc.web_frontend.org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%
	User user2 = (User) request.getAttribute("soughtUser");
	JSONArray list = (JSONArray) request.getAttribute("list");
%>

<div id="content">
<h1>Einwilligungshistorie - <%=user2.getName() %>, <%=user2.getForename() %></h1>
<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
	<table border="0" style="margin-left:200px;">
<%	for (int i = 0; i < list.length(); i++) {
%>
		<tr>
			<td><a class="mybutton" onClick="window.open('<%=request.getContextPath().toString()%>/ConsentHistoryServiceServlet?type=consent&oid=<%=list.getJSONObject(i).get("extension")%>','Einwilligungserklärung des Patienten <%=user2.getForename()%> <%=user2.getName() %> vom <%=list.getJSONObject(i).get("effectiveTime") %>','width=800,height=600')"><%=list.getJSONObject(i).get("effectiveTime") %></a></td>
		</tr>
<% }
%>		
	</table>	
</div>
