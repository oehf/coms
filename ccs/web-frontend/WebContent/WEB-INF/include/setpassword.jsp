<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
	
<%
	String emailaddress = (String) request.getAttribute("emailaddress");
	String ref = (String) request.getAttribute("ref");
	ref = ref.trim();
	System.out.println(ref);
%>

<div id="content">
	<h1>Passwort setzen</h1>
	<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
	<div id="message" style="display: inline; color: #00427A;, margin-bottom: 15px;">Auf dieser Seite können Sie ein neues Passwort setzen:</div>
	
	<div id="form">
	<form action="RetrievePasswordServiceServlet?type=setPassword" method="POST">
	<input type="hidden" name="ref" value="<%=ref%>" />
		<table border="0" style="margin-left:120px;">
			<tr>
				<td><p style="margin-bottom:5px;">Emailaddresse:</p></td> <td><input style="margin-bottom:5px;" type="text" name="emailaddress" value=<%=emailaddress %> readonly /></td>
			</tr>
			<tr>
				<td><p style="margin-bottom:5px;">Neues Passwort:</p></td> <td><input style="margin-bottom:5px;" type="password" name="password" /></td>
			</tr>
			<tr>
				<td colspan="2"><input style="margin-right:15px; margin-left:15px; margin-top:5px;" type="submit" value="Speichern" class="mybutton"/></td>
			</tr>
		</table>
	</form>
</div>
	
</div>




