<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%
	String type = (String) request.getAttribute("type");
	String message = (String) request.getAttribute("message");
	String h2 = "";
%>

<div id="content">
<% 	if (type.equalsIgnoreCase("error")) {
%>		<h1>Fehlermeldung</h1>
		<%h2 = "Es ist ein Fehler bei ihrer Anfrage aufgetreten:"; %>
<%	}
	else if (type.equalsIgnoreCase("success")){
%>		<h1>Hinweis</h1>
		<%h2 = "Ihre Anfrage wurde erfolgreich bearbeitet:"; %>
<% 	}
%>
<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
<div id="message" style="display: inline; color: #00427A;"><%=h2 %></div>
<br />
<br>
<%=message %>

</div>