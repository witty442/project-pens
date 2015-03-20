<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-874">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css" type="text/css">
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<script language="javascript">
	function downloadLog(){
		//alert("cc:"+document.form1.fileName.value);
		var url = "<%=request.getContextPath()%>/jsp/monitorInterfacesAction.do?do=downloadLog&fileName="+document.form1.fileName.value+"&transType="+document.form1.transType.value;
		window.open(url,'hiddenFrame','status = 0, height =0, width =0, resizable=0');		
	}
</script>
</head>
<body>
<form action="" name="form1">
<table width="100%"  border="0" cellspacing="0" cellpadding="10">
  <tr>
    <td height="10"></td>
  </tr>
  <tr>
    <td align="center"><strong>Log of File Name:<%=Utils.isNull(request.getAttribute("LOGS_NAME"))%></strong>
    <input type="hidden" name="fileName" value="<%=Utils.isNull(request.getAttribute("LOGS_NAME"))%>"/>
     <input type="hidden" name="transType" value="<%=Utils.isNull(request.getParameter("transType"))%>"/>
    </td>
  </tr>
  <tr>
    <td height="10"></td>
  </tr>
  <tr>
    <td height="2" bgcolor="#E8E8E8"></td>
  </tr>
  <tr>
    <td height="10"></td>
  </tr>
  <tr>
    <td align="left" valign="top">
     <%if(request.getAttribute("DATA_LOGS") != null){
		         out.println((String)request.getAttribute("DATA_LOGS"));
		        }
		   %>

	</td>
  </tr>
  <tr>
    <td>			
			<iframe id="hiddenFrame" name="hiddenFrame" frameborder= "0" height="0" width="0"> </iframe>
			<a href="#" onClick="downloadLog()">Download</a>&nbsp;
			<a href="#" onClick="window.close();">Close</a>
	</td>
  </tr>
  <tr>
    <td height="15">			
			
	</td>
  </tr>
</table>
</form>
</body>
</html>
	