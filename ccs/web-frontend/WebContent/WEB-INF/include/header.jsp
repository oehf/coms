<%-- 
Diese Seite stellt den Header (Include) dar.
--%>
<%@page import="org.openehealth.coms.cc.web_frontend.consentcreator.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	int rights = -1;
%>

<div
	id="portal_header">
	<div id="headercontent">
<%
	User user = (User) request.getSession().getAttribute("user");

	if (user == null) {
		// wenn kein User angegeben -> Fehler -> Sitzung beenden
		rights = -1;
%>
<%
	} // Ende - kein User
	else {
		// ab hier ist der User nicht gleich null
		rights = user.getPrivileges();
%>



<table class="usercontainer" align="right"  >
	<tr>
		<td><%=user.getForename()%> <%=user.getName()%></td>
		<%
			if (rights == 2) {
		%>
		<td>(Administrator)</td>
		<%
			} else if (rights == 1) {
		%>
		<td>(Leistungserbringer)</td>
		<%
			} else if (rights == 0) {
		%>
		<td>(Patient)</td>
		<td><a
			href="<%=request.getContextPath().toString()%>/UserDispatcher?type=editdetailspage"><img
			src="images/information.gif" alt="Benutzerinformationen anzeigen" title="Benutzerinformationen anzeigen"></a></td>
		<%
			}
		%>
		
		<td><a
			href="<%=request.getContextPath().toString()%>/LogoutServiceServlet?type=logout" title="Abmelden" onClick="
	if (!confirm('Durch best&auml;tigen werden Sie vom System abgemeldet.'))
		return false;">Abmelden</a></td>
	</tr>
</table>


<%
	}
%>
</div>
</div>


<!-- /kopfbereich -->

