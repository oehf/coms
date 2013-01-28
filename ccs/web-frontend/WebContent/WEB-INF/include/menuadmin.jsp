<%-- 
Diese Seite stellt das Menü (Include) dar.
--%>
<%@page import="org.openehealth.coms.cc.web_frontend.consentcreator.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<div id="navigation">

<%@ include file="logo.jsp"%>

<ul style="margin-top: 25px">
	<li id="id_li_de_homepage" style="border-top: 1px solid #006D6B"><a
		href="<%=request.getContextPath().toString()%>/Dispatcher?type=homepage"
		title="Startseite" id="id_a_de_homepage">Startseite</a></li>


	<li id="id_li_de_registeruserpage"><a
		href="<%=request.getContextPath().toString()%>/PrivilegedDispatcher?type=registeruserpage"
		title="Teilnehmer hinzufügen" id="id_a_de_registeruserpage">Teilnehmer hinzufügen</a></li>


	<li id="id_li_de_consenthistorypage"><a
		href="<%=request.getContextPath().toString()%>/PrivilegedDispatcher?type=finduserpage&context=consenthistorypage"
		title="Einwilligungshistorie" id="id_a_de_consenthistorypage">Einwilligungshistorie</a></li>

<% 	if (((User) request.getSession().getAttribute("user")).getPrivileges() == 2) {
%>		<li id="id_li_de_userconflictspage"><a
		href="<%=request.getContextPath().toString()%>/PrivilegedDispatcher?type=userconflictspage"
		title="Benutzerkonflikte" id="id_a_de_userconflictspage">Teilnehmer freischalten</a></li>
		
		<li id="id_li_de_clearconsentpage"><a
		href="<%=request.getContextPath().toString()%>/PrivilegedDispatcher?type=clearconsentpage"
		title="Benutzerkonflikte" id="id_a_de_clearconsentpage">Einwilligungserklärung freischalten</a></li>
<%	}

%>	

	<li id="id_li_de_finduserpage"><a
		href="<%=request.getContextPath().toString()%>/PrivilegedDispatcher?type=finduserpage&context=editdetailspage"
		title="Teilnehmer bearbeiten" id="id_a_de_editdetailspage">Teilnehmer bearbeiten</a></li>


	<li id="id_li_de_finduserpage"><a
		href="<%=request.getContextPath().toString()%>/PrivilegedDispatcher?type=finduserpage&context=activatepage"
		title="Teilnehmer aktivieren" id="id_a_de_activateuserpage">Teilnehmer aktivieren</a></li>
		
	<li id="id_li_de_finduserpage"><a
		href="<%=request.getContextPath().toString()%>/PrivilegedDispatcher?type=finduserpage&context=deactivatepage"
		title="Teilnehmer inaktivieren" id="id_a_de_deactivateuserepage">Teilnehmer inaktivieren</a></li>
		
	<li id="id_li_de_logoutpage"><a
		href="<%=request.getContextPath().toString()%>/LogoutServiceServlet?type=logout"
		title="Abmelden" id="id_a_de_logoutpage">Abmelden</a></li>
</ul>
</div>
