<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%
String resultBKDB = "";
try{
	User user = (User)session.getAttribute("user");
	String[] path = new com.isecinc.pens.db.backup.DBBackUpManager().processOnLocalOnly(request,user);
	
	resultBKDB +="\n ----------------- Result---------------------------- \n";
	resultBKDB +="\n Path Local To>> "+path[0];
	resultBKDB +="\n Path FTP Server To>> "+path[1];
	
}catch(Exception e){
	
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">

<title>Backup DB on LOCAL ONLY</title>

<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<script type="text/javascript">
  setTimeout(function(){window.close();},20000);
</script>
</head>
<body onload="loadMe('${pageContext.request.contextPath}');" topmargin="0" bottommargin="0" leftmargin="100" rightmargin="150" class="popbody">
<!-- BODY -->
<html:form action="/jsp/saleOrderAction">
<input type="hidden" name="load" value="">
<jsp:include page="../error.jsp"/>
<!-- BUTTON -->
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td align="center">
			<%=resultBKDB %>
		</td>
	</tr>
</table>
</html:form>
<!-- BODY -->
</body>
</html>