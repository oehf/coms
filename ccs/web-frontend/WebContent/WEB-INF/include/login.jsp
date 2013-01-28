<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
	
<div id="content">
<h1>Anmeldung</h1>
<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
<div>
Sie haben sich schon Registriert? Dann können Sie sich hier mit Ihren Benutzerdaten anmelden. Wenn nicht können
Sie sich <a href="Dispatcher?type=registrationpage">hier</a> registrieren. Fals sie Ihr Passwort vergessen haben
können Sie <a href="Dispatcher?type=passwordpage">hier</a> ein neues anfordern.
</div>
<br>
	<div id="form">
		<form action="LoginServiceServlet?type=login" method="POST">
				<table border="0" style="margin-left:120px;">
					<tr>
						<td>
							<p STYLE="margin-bottom:5px;">Emailaddresse:</p>
						</td> 
						<td>
							<input STYLE="margin-bottom:5px;" type="text" name="emailaddress" />
						</td>
					</tr>
					<tr>
						<td>
							<p STYLE="margin-bottom:5px;">Passwort:</p>
						</td> 
						<td>
							<input STYLE="margin-bottom:5px;" type="password" name="password" />
						</td>
					</tr>
					<tr>
						<td>
							<input STYLE="margin-right:15px; margin-left:15px; margin-top:5px;" type="submit" value="Anmelden" class="mybutton"/>
						</td> 
						<td>
							<input STYLE="margin-right:15px; margin-left:15px; margin-top:5px;" type="reset" value="Abbrechen" class="mybutton"/>
						</td>
					</tr>
				</table>
		</form>
	</div>
</div>