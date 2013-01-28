
<%@page import="org.openehealth.coms.cc.web_frontend.consentcreator.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%
	User user1 = (User) request.getAttribute("soughtUser");
%>

<div id="content">
<h1>Einwilligungshistorie - Bestätigen</h1>
<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
	<div id="form">
		<form action="ConsentHistoryServiceServlet?type=consentHistory" method="POST">
			<table border="0" style="margin-left:120px;">
				<tr>
					<td><p style="margin-bottom:5px;">Vorname:</p></td> <td><input style="margin-bottom:5px;" type="text" name="forename" value=<%=user1.getForename() %> readonly/></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Name:</p></td> <td><input style="margin-bottom:5px;" type="text" name="name" value=<%=user1.getName() %> readonly/></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Emailaddresse:</p></td> <td><input style="margin-bottom:5px;" type="text" name="emailaddress" value=<%=user1.getEmailaddress() %> readonly/></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Straße:</p></td> <td><input style="margin-bottom:5px;" type="text" name="street" value=<%=user1.getStreet() %> readonly/></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Postleitzahl:</p></td> <td><input style="margin-bottom:5px;" type="text" name="zipcode" value=<%=user1.getZipcode() %> readonly/></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Wohnort:</p></td> <td><input style="margin-bottom:5px;" type="text" name="city" value=<%=user1.getCity() %> readonly/></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Geburtsdatum:</p></td> <td><input style="margin-bottom:5px;" type="text" name="birthdate" value=<%=user1.getBirthdate() %> readonly/></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Geschlecht:</p></td> <td><select id="gender" name="gender" style="width:12em; margin-bottom:5px;" DISABLED>
  						<option value="male" <% if(user1.getGender().equalsIgnoreCase("male")) { %> SELECTED <% } %> >Männlich</option>
  						<option value="female" <% if(user1.getGender().equalsIgnoreCase("female")) { %> SELECTED <% } %> >Weiblich</option>
					</select></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Privilegien</p></td> <td><select id="privSelect" style="width:12em; margin-bottom:5px;" name="privileges" DISABLED>
  						<option value="0" <% if(user1.getPrivileges() ==0 ) { %> SELECTED <% } %> >Keine</option>
  						<option value="1" <% if(user1.getPrivileges() ==1 ) { %> SELECTED <% } %> >Leistungserbinger</option>
						<option value="2" <% if(user1.getPrivileges() ==2 ) { %> SELECTED <% } %> >Admin</option>
					</select></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Aktiv</p></td> <td><select style="width:12em; margin-bottom:5px;" name="active" DISABLED>
  						<option value="1" <% if(user1.isActive() ==1 ) { %> SELECTED <% } %> >Ja</option>
 						<option value="0" <% if(user1.isActive() ==0 ) { %> SELECTED <% } %> >Nein</option>
					</select></td>
				</tr>
				<tr>
					<td><input style="margin-right:15px; margin-left:15px; margin-top:5px;" class="mybutton" type="submit" value=Historie /></td> <td><input style="margin-right:15px; margin-left:15px; margin-top:5px;" class="mybutton" type="button" value="Abbrechen" onclick="window.location.href='<%=request.getContextPath().toString()%>/PrivilegedDispatcher?type=finduserpage&context=consenthistorypage"></td>
				</tr>
			</table>
		</form>
	</div>
</div>