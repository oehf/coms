
<%@page import="org.openehealth.coms.cc.web_frontend.consentcreator.model.User"%>
<%@page import="java.text.SimpleDateFormat" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%
	User user1 = (User) request.getSession().getAttribute("user");
	String context = (String) request.getAttribute("context");
	String editPrivileges = "disabled";
	String servlet = "";
	boolean editActive = false;
	
	
	SimpleDateFormat displaysdf = new SimpleDateFormat();
	displaysdf.applyPattern("dd.MM.yyyy");
	
	if(user1.getPrivileges() == 2) {
		editPrivileges = "";
	}
	if(user1.getPrivileges() >= 1) {
		editActive = true;
		user1 = (User) request.getAttribute("soughtUser");
		servlet = "Privileged";
	}
	
	
	if (context != null) {
		context = " - "+ context;
	}
	else {
		context = "";
	}
%>

<div id="content">
<h1>Daten bearbeiten - <%=user1.getName()%>, <%=user1.getForename() %></h1>
<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
	<div id="form">
		<form action="<%=servlet%>UpdateUserServiceServlet?type=update&id=<%=user1.getID()%>" method="POST">
			<table border="0" style="margin-left:120px;">
				<tr>
					<td>
						<p style="margin-bottom:5px;">Vorname:</p>
					</td> 
					<td>
						<input style="margin-bottom:5px;" type="text" name="forename" value="<%=user1.getForename() %>" />
					</td>
				</tr>
				<tr>
					<td>
						<p style="margin-bottom:5px;">Name:</p>
					</td> 
					<td>
						<input style="margin-bottom:5px;" type="text" name="name" value=<%=user1.getName() %> />
					</td>
				</tr>
				<tr>
					<td>
						<p style="margin-bottom:5px;">Emailaddresse:</p>
					</td> 
					<td>
						<input style="margin-bottom:5px;" type="text" name="emailaddress" value=<%=user1.getEmailaddress() %> />
					</td>
				</tr>
				<tr>
					<td>
						<p style="margin-bottom:5px;">Straße:</p>
					</td> 
					<td>
						<input style="margin-bottom:5px;" type="text" name="street" value="<%=user1.getStreet() %>" />
					</td>
				</tr>
				<tr>
					<td>
						<p style="margin-bottom:5px;">Postleitzahl:</p>
					</td> 
					<td>
						<input style="margin-bottom:5px;" type="text" name="zipcode" value=<%=user1.getZipcode() %> />
					</td>
				</tr>
				<tr>
					<td>
						<p style="margin-bottom:5px;">Wohnort:</p>
					</td> 
					<td>
						<input style="margin-bottom:5px;" type="text" name="city" value=<%=user1.getCity() %> />
					</td>
				</tr>
				<tr>
					<td>
						<p style="margin-bottom:5px;">Geburtsdatum:</p>
					</td> 
					<td>
						<input style="margin-bottom:5px;" type="text" name="birthdate" value=<%=displaysdf.format(user1.getBirthdate()) %> />
					</td>
				</tr>
				<tr>
					<td>
						<p style="margin-bottom:5px;">Geschlecht:</p>
					</td> 
					<td>
						<select id="gender" name="gender" style="width:12em; margin-bottom:5px;" >
  							<option value="male" <% if(user1.getGender().equalsIgnoreCase("male")) { %> SELECTED <% } %> >Männlich</option>
  							<option value="female" <% if(user1.getGender().equalsIgnoreCase("female")) { %> SELECTED <% } %> >Weiblich</option>
						</select>
					</td>
				</tr>
<%
	if (editActive) {
%>		
				<tr>
					<td>
						<p style="margin-bottom:5px;">Privilegien</p>
					</td> 
					<td>
						<select id="privSelect" style="width:12em; margin-bottom:5px;" name="privileges" <%=editPrivileges %>>
  							<option value="0" <% if(user1.getPrivileges() ==0 ) { %> SELECTED <% } %> >Keine</option>
  							<option value="1" <% if(user1.getPrivileges() ==1 ) { %> SELECTED <% } %> >Leistungserbinger</option>
							<option value="2" <% if(user1.getPrivileges() ==2 ) { %> SELECTED <% } %> >Admin</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<p style="margin-bottom:5px;">Aktiv</p>
					</td> 
					<td>
						<select style="width:12em; margin-bottom:5px;" name="active">
  							<option value="1" <% if(user1.isActive() ==1 ) { %> SELECTED <% } %> >Ja</option>
 							<option value="0" <% if(user1.isActive() ==0 ) { %> SELECTED <% } %> >Nein</option>
						</select>
					</td>
				</tr>

<%	}

%>
				<tr>
					<td>
						<input style="margin-right:15px; margin-left:15px; margin-top:5px;" class="mybutton" type="submit" value="Speichern" />
					</td> 
					<td>
						<input style="margin-right:15px; margin-left:15px; margin-top:5px;" class="mybutton" type="button" value="Abbrechen" onclick="window.location.href='<%=request.getContextPath().toString()%>/UserDispatcher?type=editdetailspage'">
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>