<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%  String endparticipation = (String) request.getAttribute("endparticipation");
	
	String text = "";
	
	if (endparticipation.equalsIgnoreCase("true")) {
		text = "Teilnahme beenden";
	}
	else {
		text = "Einwilligungserklärung zurücksetzen";
	}
 %>

<div id="content">
<h1><%=text %></h1>
<img src="images/horizontaldashedseperator.gif"	style="margin-top: 5px; margin-bottom: 15px;">
<div id="message" style="display: inline; color: #00427A;">Bitte wählen Sie aus, wie Sie Ihre <%=text %> wollen:</div>
<br/>
<br/>
<table border="0" style="margin-left:120px;">
	<tr>
		<td style="text-align:center;width:220px;">
			<a class="mybutton" style="text-decoration:none" href="<%=request.getContextPath().toString()%>/UserDispatcher?type=signdigitalpage&endparticipation=<%= endparticipation%>">
			Digitale Signatur
			</a>
		</td> 			
		<td>
			<a  href="<%=request.getContextPath().toString()%>/UserDispatcher?type=helpdigitalsignature">
				<img title="Hilfe zur digitalen Signatur" style="background-color: #00427A" src="images/inf.gif" ></img>
			</a>
		</td>
	</tr>
	<tr>
		<td style="text-align:center;width:220px;">
			<a class="mybutton" style="text-decoration:none" href="<%=request.getContextPath().toString()%>/UserDispatcher?type=signwrittenpage&endparticipation=<%= endparticipation%>">
			Schriftliche Signatur
			</a>
		</td>		
		<td>
			<a  href="<%=request.getContextPath().toString()%>/UserDispatcher?type=helpwrittensignature">
				<img title="Hilfe zur schriftlichen Signatur" style="background-color: #00427A" src="images/inf.gif" ></img>
			</a>
		</td>
	</tr>
</table>

</div>