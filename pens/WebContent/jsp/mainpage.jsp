<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%
String role = ((User)session.getAttribute("user")).getType();
User user = (User)session.getAttribute("user");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="com.isecinc.pens.bean.User"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620" />
<title>PENS SALESYSTEM</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>

<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" type="text/css" />

<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
	<tr>
		<td colspan="3"><jsp:include page="header.jsp"/></td>
	</tr>
  	<tr id="framerow">
  		<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td>
			        	<jsp:include page="menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	
    	</td>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<jsp:include page="contentbottom.jsp"/>
        </td>
        
        
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="footer.jsp"/></td>
  	</tr>
</table>

<div id="dialog" title="คำแนะนำ">
	<p><b>กรุณาดึงข้อมูลจากส่วนกลาง อย่างน้อยวันละหนึ่งครั้ง  ก่อนทำ รายการขาย/รายการรับเงิน   เพื่อที่ข้อมูลจะได้ถูกต้อง</b></p><br />
	<p align="center"> <a href="javascript:close();"><input type="submit" onclick="linkToInterfaces('<%=request.getContextPath() %>');" value="ไปยังหน้าดึงข้อมูลจากส่วนกลาง"/></a></p>
	<p align="center"> <a href="javascript:close();"><input type="submit" onclick="close();" value="ปิดหน้าจอ"/></a></p>
</div>

<script>
  $(function() {
	$("#dialog").dialog({ height: 250,width:300,modal:false });
  });
 
 function close(){
	 $("#dialog").dialog('close');
 }
 
 function linkToInterfaces(path){
	window.location = path+"/jsp/interfaces/interfaces.jsp";
 }
</script>
</body>
</html>
