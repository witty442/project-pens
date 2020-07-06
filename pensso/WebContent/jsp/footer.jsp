<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.inf.helper.EnvProperties"%>
<%@page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<%
 String[] msg3 = new String[2];
if(request.getSession().getAttribute("appVersionCheckMsg") != null){
	msg3 =  (String[])request.getSession().getAttribute("appVersionCheckMsg");
}
String screenWidth= Utils.isNull(session.getAttribute("screenWidth"));
String screenHeight= Utils.isNull(session.getAttribute("screenHeight"));
%>
<script type="text/javascript">

function openPopup(path){
	
	var h= <%=screenHeight%>-50;
	var w = <%=screenWidth%>-50;
	PopupCenter(path+"/jsp/adminConsole.do?do=process&currentTab=tab_config_info", "Configuration",w,h); 
	//window.open(path+'/jsp/adminConsole.do?do=process&currentTab=tab_config_info','Configuration','width='+screen_width+',height='+screen_height+"'");
}

function openDBBackupPopup(path){
	var w = 400;
	var h = 200;
	
	var left = (<%=screenWidth%>/2)-(w/2);
	var top = (<%=screenHeight%>/2)-(h/2);
	 
	PopupCenter(path+"/jsp/pop/dbBackupPopup.jsp?", "Backup DB to Local only",w,h); 
	//window.open(path+'/jsp/pop/dbBackupPopup.jsp', 'Backup DB to Local only', ', width='+w+', height='+h+', top='+top+', left='+left);
	//window.open(path+'/jsp/pop/dbBackupPopup.jsp','','width='+screen_width+',height='+screen_height+',title=Configuration');
}

</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="27px;"><img src="${pageContext.request.contextPath}/images2/footer_left.png" border="0"/></td>
		<td width="44px;"><img src="${pageContext.request.contextPath}/images2/footer_left2.png" border="0"/>
		
		</td>
        <td background="${pageContext.request.contextPath}/images2/footer01.png">
           <b>
        	App Version
			 <a href ="javascript:openPopup('${pageContext.request.contextPath}');">
			   <font color="red"><b><bean:message bundle="sysprop" key="AppVersion"/></b></font>
			 </a>&nbsp;
			  <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/softwareUpdater/SalesAppUpdater.jsp';">
			  <font color="red"><%=Utils.isNull(msg3[0]) %></font> </a>|&nbsp;<%=Utils.isNull(msg3[1]) %>
			  &nbsp;<font color="black">
			  Config:<%=EnvProperties.getInstance().getProperty("config.type") %>
			  </font> |
			  <a href="javascript:openDBBackupPopup('${pageContext.request.contextPath}');">
			      <font color="green">สำรองข้อมูล</font>
			       <img src="${pageContext.request.contextPath}/icons/process.gif"></img>
			   </a>
			   &nbsp;|&nbsp;เบอร์โทรไอที : 087-8016837, 083-3061296
			  </b>
		</td>
        <td width="60px"><img src="${pageContext.request.contextPath}/images2/footer_right2.png" border="0"/></td>
        <td width="31px;"><img src="${pageContext.request.contextPath}/images2/footer_right.png" border="0"/></td>
	</tr>
</table>
