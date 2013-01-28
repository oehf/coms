<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
		
<div id="content">
<h1>Registrierung</h1>
<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
<div>
Hier können Sie sich für die Nutzung der PEPA registrieren. Bitte füllen Sie dazu die nachfolgenden
Felder aus.
<br>
Alle Felder sind Pflichtfelder!
</div>
<br>
	<div id="form">
		<form action="RegisterServiceServlet?type=registerSelf" method="POST">
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
					<td><p style="margin-bottom:5px;">Geschlecht:</p></td> <td><select name="gender" style="width:11em; margin-bottom:5px;">
  						<option  value="male">Männlich</option>
  						<option value="female">Weiblich</option>
					</select></td>
				</tr>
				<tr>
					<td><input style="margin-right:15px; margin-left:15px; margin-top:5px;" class="mybutton" type="submit" value="Registrieren" /></td> <td><input style="margin-right:15px; margin-left:15px; margin-top:5px;" class="mybutton" type="reset" value="Abbrechen" /></td>
				</tr>
			</table>
		</form>
	</div>
</div>