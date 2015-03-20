<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<%@page import="com.isecinc.pens.SystemProperties"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css">
<style type="text/css">
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/login.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript">

function onLoad(){
	setTimeout('redirectPage()',1000);
}

function redirectPage(){
	var path = document.getElementById("path").value;
	var url = path+"<%=Utils.isNull(request.getAttribute("url"))%>";
	//alert(url);
	 
    document.loginForm.action=url;
    document.loginForm.submit();
    return true;
}

</script>

</head>
<body onload="onLoad()">
<table width="939" height="517" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
  <tr>
    <td valign="top">
    	<table width="939" border="0" align="center" cellpadding="0" cellspacing="0">
      	<tr>
        	<td width="61">&nbsp;</td>
       	 	<td width="355" valign="top">
       	 		<html:form action="/login" onsubmit="return false;">
       	 		  <input type="text" id ="path" name="path" value="${pageContext.request.contextPath}" />
		        </html:form>
			</td>
        	<td width="523">&nbsp;</td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td valign="top"><img src="${pageContext.request.contextPath}/images2/blank.gif" width="1" height="250" /></td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td valign="top"><table width="355" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td width="197"><img src="${pageContext.request.contextPath}/images2/footer.jpg" width="454" height="38" /></td>
          </tr>
        </table></td>
        <td>&nbsp;</td>
      </tr>
    </table></td>
  </tr>
</table>
</body>
</html>
