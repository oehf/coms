<%-- 
Diese Seite stellt das Menü (Include) dar.
--%>

<%
	int RIGHTS = -1;
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<div id="navigation">

<%@ include file="logo.jsp"%>

<ul style="margin-top: 25px">
	<li id="id_li_de_homepage" style="border-top: 1px solid #006D6B">
		<a href="<%=request.getContextPath().toString()%>/Dispatcher?type=homepage" title="Startseite" id="id_a_de_homepage">
		Startseite
		</a>
	</li>


	<li id="id_li_de_loginpage">
		<a href="<%=request.getContextPath().toString()%>/Dispatcher?type=loginpage" title="Anmelden" id="id_a_de_loginpage">
		Anmelden
		</a>
	</li>


	<li id="id_li_de_registrationpage">
		<a href="<%=request.getContextPath().toString()%>/Dispatcher?type=registrationpage" title="Registrieren" id="id_a_de_registrationpage">
		Registrieren
		</a>
	</li>


	<li id="id_li_de_keywordpage">
		<a href="<%=request.getContextPath().toString()%>/Dispatcher?type=passwordpage" title="Passwort" id="id_a_de_keywordpage">
		Passwort anfordern
		</a>
	</li>


	<li id="id_li_de_contactpage">
		<a href="<%=request.getContextPath().toString()%>/Dispatcher?type=contactpage" title="Kontakt" id="id_a_de_contactpage">
			Kontakt
		</a>
	</li>


	<li id="id_li_de_legalnoticepage">
		<a href="<%=request.getContextPath().toString()%>/Dispatcher?type=legalnoticepage" title="Impressum" id="id_a_de_legalnoticepage">
		Impressum
		</a>
	</li>
</ul>
<!--  <img src="images/de.gif" alt="deutsch/german" title="Sprache=deutsch/german"
	style="margin-top: 30px; margin-left: 90px;"></img>--></div>
