<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath().toString();
	String show = (String) request.getAttribute("show");
	if (show == null)
		show = "";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" >
<title>Intersektorales Informationssystem - Consent Creator</title>
<link rel="stylesheet" href="<%=path%>/style/style.css" type="text/css">

<style type="text/css" media="all">
<!--
#navigation a#id_a_de_homepage {
	font-weight: bold;   
}

#navigation a#id_a_de_homepage {
	font-weight: bold;
}

#navigation li#id_li_de_homepage {
	background-color: #efefef;
}

#navigation li#id_li_de_homepage {
	background-color: #efefef;
}
-->
</style>
</head>
<body>

<%@ include file="WEB-INF/include/header.jsp"%>

<div id="page">
<%
	if (rights == 2 || rights == 1) {
%> <%@ include file="WEB-INF/include/menuadmin.jsp"%> <%
 	}
 	if (rights == 0) {
 %> <%@ include file="WEB-INF/include/menupatient.jsp"%>
<%
	}
 	if (rights == -1) {
 %> <%@ include file="WEB-INF/include/mainmenu.jsp"%> <%
 	}
 %>

<%
	if (show.equalsIgnoreCase("messagepage")) {
%>	
	<%@ include file="WEB-INF/include/message.jsp"%>
<%  }



	else if (show.equalsIgnoreCase("")) {
%>		<%@ include file="WEB-INF/include/welcome.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("loginpage")) {
%>		<%@ include file="WEB-INF/include/login.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("registrationpage")) {
%>		<%@ include file="WEB-INF/include/register.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("passwordpage")) {
		%>		<%@ include file="WEB-INF/include/password.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("contactpage")) {
		%>		<%@ include file="WEB-INF/include/contact.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("legalnoticepage")) {
		%>		<%@ include file="WEB-INF/include/legalnotice.jsp"%>
<%	}



	else if (show.equalsIgnoreCase("finduserpage")) {
		%>		<%@ include file="WEB-INF/include/finduser.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("registeruserpage")) {
		%>		<%@ include file="WEB-INF/include/registeruser.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("consenthistorypage")) {
		%>		<%@ include file="WEB-INF/include/consenthistory.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("userconflictspage")) {
		%>		<%@ include file="WEB-INF/include/userconflicts.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("clearconsentpage")) {
		%>		<%@ include file="WEB-INF/include/clearconsent.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("activatepage")) {
		%>		<%@ include file="WEB-INF/include/deactivateuser.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("deactivatepage")) {
		%>		<%@ include file="WEB-INF/include/deactivateuser.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("signchoicepage")) {
		%>		<%@ include file="WEB-INF/include/signchoicepriv.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("signdigitalprivpage")) {
		%>		<%@ include file="WEB-INF/include/signdigitalpriv.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("signwrittenprivpage")) {
		%>		<%@ include file="WEB-INF/include/signwrittenpriv.jsp"%>
<%	}





	else if (show.equalsIgnoreCase("createconsentpage")) {
		%>		<%@ include file="WEB-INF/include/createconsent.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("revokeconsentpage")) {
		%>		<%@ include file="WEB-INF/include/revokeconsent.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("signdigitalpage")) {
		%>		<%@ include file="WEB-INF/include/signdigital.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("signwrittenpage")) {
		%>		<%@ include file="WEB-INF/include/signwritten.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("editdetailspage")) {
		%>		<%@ include file="WEB-INF/include/editdetails.jsp"%>
<%	}
	else if (show.equalsIgnoreCase("setpasswordpage")) {
	%>			<%@ include file="WEB-INF/include/setpassword.jsp"%>
<%	}
%>


<%@ include file="WEB-INF/include/footer.jsp"%></div>

</body>
</html>