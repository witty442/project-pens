<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.EnvProperties"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%
String gotoPage = Utils.isNull(request.getParameter("gotoPage"));
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css">
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	background-repeat: repeat;
}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript">

function gologin(e){
	if(e == null || (e != null && e.keyCode == 13)){
		login('${pageContext.request.contextPath}');
	}
}

function popupChangePassword(path){
	var url = path + "/jsp/userAction.do?do=changePassword&action=init";
	window.open(url,"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=260px,status=no,left="+ 50 + ",top=" + 0);
}

function login(path){
	var w = screen.width;
  
	//alert(w+":"+h);
	document.getElementsByName('screenWidth')[0].value = w;
	
    if(Trim(document.getElementsByName('userName')[0].value)==''){
        document.getElementsByName('userName')[0].focus();
        return false;
    }
    if(Trim(document.getElementsByName('password')[0].value)==''){
    	document.getElementsByName('password')[0].focus();
        return false;
    }
    document.loginForm.action=path+"/login.do?do=login";
    document.loginForm.submit();
    return true;
}

</script>
</head>
<body onload="MM_preloadImages('${pageContext.request.contextPath}/images2/button_login2.png','${pageContext.request.contextPath}/images2/button_forgotpwd2.png')" topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0">
<table width="939" height="517" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
  <tr>
    <td background="${pageContext.request.contextPath}/images2/loginbox_all.png" valign="top">
    	<table width="939" border="0" align="center" cellpadding="0" cellspacing="0">
      	<tr>
        	<td width="61">&nbsp;</td>
       	 	<td width="355" valign="top">
       	 		<html:form action="/login" onsubmit="return false;">
       	 		<table width="355" border="0" cellspacing="0" cellpadding="0">
		            <tr>
		              <td colspan="5">
		                <img src="${pageContext.request.contextPath}/images2/blank.gif" width="1" height="50" />
		              </td>
		            </tr>
		            <tr>
		              <td colspan="5">
		                  <img src="${pageContext.request.contextPath}/images2/logo_salesystem.gif" width="303" height="38" />
		              </td>
		            </tr>
		            <tr>
		              <td width="103">&nbsp;</td>
		              <td width="94">&nbsp;</td>
		              <td colspan="3">&nbsp;</td>
		            </tr>
		            <tr>
		              <td>
		              	<bean:message key="User.UserName" bundle="sysele"/>
		              </td>
		              <td>
		              	<bean:message key="User.Password" bundle="sysele"/>
		              </td>
		              <td width="50" rowspan="3">
		              	<a href="#" onclick="return login('${pageContext.request.contextPath}');" onmouseout="MM_swapImgRestore()" onmouseover="MM_swapImage('Image1','','${pageContext.request.contextPath}/images2/button_login2.png',1)"><img src="${pageContext.request.contextPath}/images2/button_login1.png" name="Image1" width="46" height="46" border="0" id="Image1" /></a>
		              </td>
		              <td width="101" rowspan="3"></td>
		              <td width="7" rowspan="3">&nbsp;</td>
		            </tr>
		           
		            <tr>
		              <td><img src="${pageContext.request.contextPath}/images2/blank.gif" width="1" height="5" /></td>
		              <td><img src="${pageContext.request.contextPath}/images2/blank.gif" width="1" height="5" /></td>
		            </tr>
		            <tr>
		              <td><html:text property="userName" size="10" /></td>
		              <td><html:password property="password" size="10" onkeypress="gologin(event);"/></td> 
		              
		            <!-- MKT -->
		             <%--  <td><html:text property="userName" size="10" value="pattarin"/></td>
		              <td><html:password property="password" size="10" onkeypress="gologin(event);" value="matcy9019"/></td> --%>
		             
		             <!-- MT -->
		             <%--  <td><html:text property="userName" size="10" value="rattana"/></td>
		              <td><html:password property="password" size="10" onkeypress="gologin(event);" value="0810"/></td>  --%>
		              
		            </tr>
		             <tr>
<!--		              <td><a href="javascript:popupChangePassword('${pageContext.request.contextPath}')">����¹���ʼ�ҹ����</a></td>-->
		              <td></td>
		            </tr>
		            <tr>
		            	<td colspan="2" nowrap>
		            		Application Version
		            		<font color="red"><b><bean:message bundle="sysprop" key="AppVersion"/></b></font>
		            		<b>(<%=EnvProperties.getInstance().getProperty("product.type")%>)</b>
		            	</td>
		            </tr>
		            <tr>
		            	<td colspan="2">
		            		<span class="errormsg">${errormsg}</span>
		            	</td>
		            </tr>
		        </table>
		        <input type="hidden" id = "screenWidth" name="screenWidth" />
		        <input type="hidden" id = "screenHeight" name="screenHeight" />
		         <input type="hidden" id = "gotoPage" name="gotoPage" value="<%=gotoPage %>"/>
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
