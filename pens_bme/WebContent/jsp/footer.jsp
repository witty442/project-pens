
<%@page import="com.isecinc.pens.inf.helper.EnvProperties"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="27px;"><img src="${pageContext.request.contextPath}/images2/footer_left.png" border="0"/></td>
		<td width="44px;"><img src="${pageContext.request.contextPath}/images2/footer_left2.png" border="0"/></td>
        <td background="${pageContext.request.contextPath}/images2/footer01.png">
        	Application Version
			
			<a href ="javascript:window.open('<%=request.getContextPath()%>/jsp/adminConsole.do?do=process','','width=800px,height=700px')" title="Configuration">
			  <font color="red"><b><bean:message bundle="sysprop" key="AppVersion"/></b></font>
			</a>
			<font color="black">วันที่ปัจจุบัน
			   <script>
			  var currentdate = new Date(); 
			  var datetime = "" + currentdate.getDate() + "/"
			                  + (currentdate.getMonth()+1)  + "/" 
			                  + (currentdate.getFullYear()+543) + " เวลา  "  
			                  + currentdate.getHours() + ":"  
			                  + currentdate.getMinutes() + ":" 
			                  + currentdate.getSeconds();
			            document.write(datetime);
			  </script>
			  </font>
			   
			  <%
			    
				String url = EnvProperties.getInstance().getProperty("db.url");
			  %>
			  DB[<%=url%>]
		</td>
        <td width="60px"><img src="${pageContext.request.contextPath}/images2/footer_right2.png" border="0"/></td>
        <td width="31px;"><img src="${pageContext.request.contextPath}/images2/footer_right.png" border="0"/></td>
	</tr>
</table>