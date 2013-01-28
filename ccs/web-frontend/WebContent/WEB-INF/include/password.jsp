<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
	
<div id="content">
<h1>Passwort anfordern</h1>
<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
<div>
Sie haben Ihr Password vergessen? Bitte tragen Sie ihre E-Mailadresse ein, wir senden Ihnen ein neues
Passwort zu. 
</div>
</br>
	<div id="form">
		<form action="RetrievePasswordServiceServlet?type=requestPassword" method="POST">
			<table border="0" style="margin-left:200px;">
					<tr>
						<td><p STYLE="margin-bottom:5px;">Emailaddresse:</p></td><td><input STYLE="margin-bottom:5px;" type="text" name="emailaddress" /></td>
					</tr>
					<tr>
						<td colspan="2" align="center"><input STYLE="margin-right:15px; margin-left:15px; margin-top:5px;" type="submit" value="Anfordern" class="mybutton"/></td>
					</tr>
				</table>
		</form>
	</div>
</div>