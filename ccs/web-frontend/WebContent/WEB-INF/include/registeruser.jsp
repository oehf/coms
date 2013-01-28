<%@page import="org.openehealth.coms.cc.web_frontend.consentcreator.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<% 
	String readonly = "";
	if (((User) request.getSession().getAttribute("user")).getPrivileges() != 2) {
		readonly="DISABLED";
	}
	String message = (String) request.getParameter("errormessage");
	if (message == null) {
		message = "";
	}
%>
		
<div id="content">
<h1>Teilnehmer hinzufügen</h1>

<%
	if (!message.equalsIgnoreCase("")) {
%>		Fehler: <%=message%>
<%	}
	else {
%>		<%=message%>
<% }
%>

<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
	<div id="form">
		<form action="PrivilegedRegisterServiceServlet?type=registerUser" method="POST" accept-charset="UTF-8">
			<table border="0" style="margin-left:120px;">
				<tr>
					<td><p style="margin-bottom:5px;">Vorname:</p></td> <td><input style="margin-bottom:5px;" type="text" name="forename" /></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Name:</p></td> <td><input style="margin-bottom:5px;" type="text" name="name" /></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Emailaddresse:</p></td> <td><input style="margin-bottom:5px;" type="text" name="emailaddress" /></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Straße:</p></td> <td><input style="margin-bottom:5px;" type="text" name="street" /></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Postleitzahl:</p></td> <td><input style="margin-bottom:5px;" type="text" name="zipcode" /></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Wohnort:</p></td> <td><input style="margin-bottom:5px;" type="text" name="city" /></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Geburtsdatum:</p></td> <td><input style="margin-bottom:5px;" type="text" name="birthdate" /></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Geschlecht:</p></td> <td><select name="gender" style="width:12em; margin-bottom:5px;">
  						<option  value="male">Männlich</option>
  						<option value="female">Weiblich</option>
					</select></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Privilegien</p></td> <td><select id="privSelect" style="width:12em; margin-bottom:5px;" name="privilegesSelect" <%=readonly %>>
  						<option value="0">Keine</option>
  						<option value="1">Leistungserbinger</option>
						<option value="2">Admin</option>
					</select></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Aktiv</p></td> <td><select style="width:12em; margin-bottom:5px;" name="active">
  						<option value="1">Ja</option>
 						<option value="0">Nein</option>
					</select></td>
				</tr>
				<tr>
					<td><input style="margin-right:15px; margin-left:15px; margin-top:5px;" class="mybutton" type="submit" value="Registrieren" /></td> <td><input style="margin-right:15px; margin-left:15px; margin-top:5px;" class="mybutton" type="reset" value="Abbrechen" /></td>
				</tr>
			</table>
			<input type="hidden" id="privs" name="privileges" value="0" />
		</form>
	</div>
</div>