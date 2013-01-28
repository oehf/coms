<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%  String context = (String) request.getAttribute("context");
	
	String text = "";
	
	if (context.equalsIgnoreCase("consenthistorypage")) {
		text = "Einwilligungshistorie";
	}
	else if (context.equalsIgnoreCase("editdetailspage")) {
		text = "Teilnehmer bearbeiten";
	}
	else if (context.equalsIgnoreCase("activatepage")) {
		text = "Teilnehmer aktivieren";
	}
	else if (context.equalsIgnoreCase("deactivatepage")) {
		text = "Teilnehmer inaktivieren";
	}
 %>
	
<div id="content">
<h1><%=text%> - Suchen</h1>
<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
	<div id="form">
		<form action="PrivilegedDispatcher?type=findUser&context=<%=context%>" method="POST">
			<table border="0" style="margin-left:120px;">
				<tr>
					<td><p style="margin-bottom:5px;">Name:</p></td> <td><input style="margin-bottom:5px;" type="text" name="name" /></td>
				</tr>
				<tr>
					<td><p style="margin-bottom:5px;">Vorname:</p></td> <td><input style="margin-bottom:5px;" type="text" name="forename" /></td>
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
					<td><input style="margin-right:15px; margin-left:15px; margin-top:5px;" type="submit" value="Suchen" class="mybutton"/></td> <td><input style="margin-right:15px; margin-left:15px; margin-top:5px;" type="reset" value="Abbrechen" class="mybutton"/></td>
				</tr>
			</table>
		</form>
	</div>
</div>