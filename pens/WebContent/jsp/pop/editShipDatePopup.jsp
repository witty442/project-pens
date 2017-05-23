<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%
	String shipDate = request.getParameter("sd");
	String startRow = request.getParameter("srow");
	pageContext.setAttribute("shipDate", shipDate, PageContext.PAGE_SCOPE);
	pageContext.setAttribute("startRow", startRow, PageContext.PAGE_SCOPE);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('shipDate'));
}

function addShipDate(path){
	if(document.getElementsByName('shipDate')[0].value==''){
		alert('กรุณาใส่ข้อมูลให้ครบถ้วน');
		document.getElementsByName('shipDate')[0].focus();
		return;
	}
	if(!checkDateDiff('<%=shipDate%>',document.getElementsByName('shipDate')[0].value)){return;}

	window.opener.put_new_shipdate(document.getElementsByName('shipDate')[0].value,'<%=startRow%>');
	window.close();
}

</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<!-- BODY -->
<html:form action="/jsp/saleOrderAction">
<!-- INCLUDE -->
<jsp:include page="../program.jsp">
	<jsp:param name="system" value="Transaction"/>
	<jsp:param name="function" value=""/>
</jsp:include>
<jsp:include page="../error.jsp"/>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td colspan="4" align="left">&nbsp;</td>
	</tr>
	<tr>
		<td align="right" width="30%"><bean:message key="Order.ShipmentDate" bundle="sysele"/></td>
		<td align="center" width="1%"><font color="red">*</font></td>
		<td align="left"><input type="text" name="shipDate" id="shipDate" value="${shipDate}" readonly="readonly" size="15"/></td>
	</tr>
</table>
<br><br><br>
<!-- BUTTON -->
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td align="right">
			<a href="#" onclick="return addShipDate('${pageContext.request.contextPath}');">
<!--			<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ตกลง" class="newPosBtn">
			</a>
			<a href="#" onclick="window.close();">
<!--			<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ยกเลิก" class="newNegBtn">
			</a>
		</td>
		<td width="20%">&nbsp;</td>
	</tr>
</table>
</html:form>
<!-- BODY -->
</body>
</html>