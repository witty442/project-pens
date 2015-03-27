<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="org.hibernate.cfg.Configuration"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%User user = (User)session.getAttribute("user");
  System.out.println("Role:"+user.getRole().getKey());
  
  String userName =user.getUserName();
  String password=user.getPassword();
  
  String webPath = request.getContextPath();
  String contextPath = request.getContextPath();
  
%>
<script>
//a href="#" onclick="window.location='http://dd-server:8081/<%=contextPath%>/login.do?do=loginCrossServer&pathRedirect=payAction|prepare2|new";
 
 function linkToDD(url){
	  url += "&userName=<%=userName%>&password=<%=password%>";
	  window.location = url;
  }
 
 //
  function link(url){
	  <% 
	    if(request.getLocalAddr().equals("192.168.202.244")){
	    	// Link to Isec Server  DDServer To IsecServer
	    	//input  : /jsp/mcAction.do?do=prepareMCStaff&action=new
	    	//output : login.do?do=loginCrossServer&pathRedirect=payAction|prepare2|new
	  %>
	     var pageAction = url.substring(url.indexOf("jsp")+4,url.indexOf(".do"));
  	     var doAction = url.substring(url.indexOf("do=")+3,url.indexOf("&"));
  	     var action = url.substring(url.indexOf("action")+7,url.length);
  	     var newUrl = "http://192.168.37.185:8080/<%=contextPath%>/login.do?do=loginCrossServer&pathRedirect="+pageAction+"|"+doAction+"|"+action;
	     
  	     url = newUrl+"&userName=<%=userName%>&password=<%=password%>";
	     window.location = url;
	 
	  <%}else{//Link normal
       %>
	     //alert(url);
         window.location = url;
	  <% } %>
  }
  

</script>

<ul id="nav">
<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){%> 
	 <li class="parent"><a href="#"><span><bean:message key="Interfaces" bundle="sysprop"/></span></a>
   		<ul>
           	<li>
           		<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/interfacesAction.do?do=prepare';"><span><bean:message key="Interfaces" bundle="sysprop"/></span></a>
           	</li>
          	<li>
           		<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/monitorInterfacesAction.do?do=prepare';"><span><bean:message key="MonitorInterfaces" bundle="sysprop"/></span></a>
           	</li>
          
       </ul>
   </li>
<%} %>
</ul>
   