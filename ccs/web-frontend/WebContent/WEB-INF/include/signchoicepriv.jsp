<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%  String title = (String) request.getAttribute("title");
	String message = (String) request.getAttribute("message");
	String messageOrder = (String) request.getAttribute("messageOrder");
	String emailaddress = (String) request.getAttribute("emailaddress");
	String context = (String) request.getAttribute("context");
	
	String hrefDigital = request.getContextPath().toString()+ "/PrivilegedDispatcher?type=signdigitalpage&context=" + context + "&emailaddress=" + emailaddress;
	String hrefWritten = request.getContextPath().toString()+ "/PrivilegedDispatcher?type=signwrittenpage&context=" + context + "&emailaddress=" + emailaddress;
	
	
	
	boolean consentAppletAvailable = false;
	
	if (context.equalsIgnoreCase("registered")) {
		consentAppletAvailable = true;
	}
	
	String hrefApplet = request.getContextPath().toString()+ "/PrivilegedDispatcher?type=createconsentpage&emailaddress=" + emailaddress +"&newlyregistered="+consentAppletAvailable;
	
	//Einwilligungserklärung erstellen ist immer im Applet und dann daraus
	//Teilnehmer inaktivieren -> negative Einwilligung dig/wri signieren
	//Teilnehmer aktivieren -> posititive Einwilligung dig/wri signieren
	//Teilnehmer freischalten -> posititive Einwilligung dig/wri signieren
	//Teilnehmer hinzufügen -> Entweder Applet erstellen oder posititive/negative Einwilligung dig/wri signieren
 %>

<div id="content">
<h1><%=title %></h1>
<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
<div id="message" style="display: inline; color: #00427A;"><%=message %></div>
<br/>
<br/>
<%=messageOrder %>
<br/>
<br/>
<table border="0" style="margin-left:120px;">
	
<% if (consentAppletAvailable) {
%>
	<tr>
		<td style="text-align:center;width:220px;"><a class="mybutton" style="text-decoration:none" href=<%=hrefApplet %>>Personalisierte Einwilligungserklärung</a></td> 			<td><a  href="<%=request.getContextPath().toString()%>/PrivilegedDispatcher?type=helpdigitalsignature"><img title="Hilfe zur digitalen Signatur" style="background-color: #00427A" src="images/inf.gif" ></img></a></td>
	</tr>
<% } %>
	
	<tr>
		<td style="text-align:center;width:220px;"><a class="mybutton" style="text-decoration:none" href=<%=hrefDigital %>>Digitale Signatur</a></td> 			<td><a  href="<%=request.getContextPath().toString()%>/PrivilegedDispatcher?type=helpdigitalsignature"><img title="Hilfe zur digitalen Signatur" style="background-color: #00427A" src="images/inf.gif" ></img></a></td>
	</tr>
	<tr>
		<td style="text-align:center;width:220px;"><a class="mybutton" style="text-decoration:none" href=<%=hrefWritten %>>Schriftliche Signatur</a></td>		<td><a  href="<%=request.getContextPath().toString()%>/PrivilegedDispatcher?type=helpwrittensignature"><img title="Hilfe zur schriftlichen Signatur" style="background-color: #00427A" src="images/inf.gif" ></img></a></td>
	</tr>
</table>

</div>