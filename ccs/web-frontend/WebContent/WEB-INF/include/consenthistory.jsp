
<%@page import="org.openehealth.coms.cc.web_frontend.consentcreator.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%
	String showList = (String) request.getAttribute("showList");
	
	if (showList == null) {
		showList = "";
	}
%>

<%  if(!showList.equalsIgnoreCase("true"))  { %>
	<%@ include file="consenthistorydetails.jsp"%>
<%  }
	else  { %>
		<%@ include file="consenthistorylist.jsp"%>
<% }
%>