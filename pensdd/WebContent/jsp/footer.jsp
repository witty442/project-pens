<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="org.hibernate.cfg.Configuration"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<script type="text/javascript">

function openPopup(path){
	
	var screen_height= window.innerHeight-10;
	var screen_width = window.innerWidth-20;
	
	window.open(path+'/jsp/adminConsole.do?do=process&currentTab=tab_config_info','','width='+screen_width+',height='+screen_height+',title=Configuration');
}

function openDBBackupPopup(path){
	var w = 400;
	var h = 200;
	
	var left = (screen.width/2)-(w/2);
	var top = (screen.height/2)-(h/2);
	 
	window.open(path+'/jsp/pop/dbBackupPopup.jsp', 'Backup DB to Local only', ', width='+w+', height='+h+', top='+top+', left='+left);
	//window.open(path+'/jsp/pop/dbBackupPopup.jsp','','width='+screen_width+',height='+screen_height+',title=Configuration');
}

</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="27px;"><img src="${pageContext.request.contextPath}/images2/footer_left.png" border="0"/></td>
		<td width="44px;"><img src="${pageContext.request.contextPath}/images2/footer_left2.png" border="0"/></td>
        <td background="${pageContext.request.contextPath}/images2/footer01.png">
        	Application Version
        	 <a href ="javascript:openPopup('${pageContext.request.contextPath}');">
			<font color="red"><b><bean:message bundle="sysprop" key="AppVersion"/></b></font>
			</a>
			<font color="black">�ѹ���Ѩ�غѹ
			   <script>
			  var currentdate = new Date(); 
			  var datetime = "" + currentdate.getDate() + "/"
			                  + (currentdate.getMonth()+1)  + "/" 
			                  + (currentdate.getFullYear()+543) + " ���� "  
			                  + currentdate.getHours() + ":"  
			                  + currentdate.getMinutes() + ":" 
			                  + currentdate.getSeconds();
			            document.write(datetime);
			  </script>
			  </font>
			   
			  <%
			    Configuration hibernateConfig = new Configuration();
				hibernateConfig.configure();
				String url = hibernateConfig.getProperty("connection.url");
			  %>
			  DB[<%=url%>]
		</td>
        <td width="60px"><img src="${pageContext.request.contextPath}/images2/footer_right2.png" border="0"/></td>
        <td width="31px;"><img src="${pageContext.request.contextPath}/images2/footer_right.png" border="0"/></td>
	</tr>
</table>
