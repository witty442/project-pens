<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="util.AppversionVerify"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="27px;"><img src="${pageContext.request.contextPath}/images2/footer_left.png" border="0"/></td>
		<td width="44px;"><img src="${pageContext.request.contextPath}/images2/footer_left2.png" border="0"/></td>
        <td background="${pageContext.request.contextPath}/images2/footer01.png">
        	Application Version
			 <a href ="javascript:window.open('<%=request.getContextPath()%>/jsp/adminConsole.do?do=process&currentTab=tab_config_info','','width=800px,height=400px')" title="Configuration">
			   <font color="red"><b><bean:message bundle="sysprop" key="AppVersion"/></b></font>
			 </a>&nbsp;&nbsp;
			  <font color="red"><%=AppversionVerify.checkAppVersion(request) %> </font>&nbsp;&nbsp;
			  <font color="black">�ѹ���Ѩ�غѹ 
			   <script>
			  var currentdate = new Date(); 
			  var datetime = "" + currentdate.getDate() + "/"
			                  + (currentdate.getMonth()+1)  + "/" 
			                  + (currentdate.getFullYear()+543) + " ����  "  
			                  + currentdate.getHours() + ":"  
			                  + currentdate.getMinutes() + ":" 
			                  + currentdate.getSeconds();
			            document.write(datetime);
			  </script>
			  </font>
		</td>
        <td width="60px"><img src="${pageContext.request.contextPath}/images2/footer_right2.png" border="0"/></td>
        <td width="31px;"><img src="${pageContext.request.contextPath}/images2/footer_right.png" border="0"/></td>
	</tr>
</table>
  