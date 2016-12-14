<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@page import="com.isecinc.pens.bean.User"%>
<%
String role = ((User)session.getAttribute("user")).getType();
User user = (User)session.getAttribute("user");
%>
<ul id="nav">
<%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){%>
  <li class="parent"><a href="#"><span>ScanBarcode</span></a>
  		<ul>
          	<li>
          		<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mttAction.do?do=prepare2&action=new';"><span>Scan Barcode</span></a>
          	</li>
      </ul>
  </li>
 <%} %>
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
  <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){%>
   <li class="parent"><a href="#"><span>รายงาน</span></a>
      <ul>
	    <li>
			<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mttAction.do?do=prepareScanReport&action=new'"><span>1.<bean:message bundle="sysprop" key="SummaryBMEScanReport"/></span></a>
		</li>
	  </ul>
   </li>
  <%} %>
   <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.POS}) ){%>
   <li class="parent"><a href="window.location='${pageContext.request.contextPath}/jsp/posAction.do?do=prepare2&action=new'"><span>ออกใบกำกับภาษี</span></a>
   </li>
  <%} %>
  </ul>
  
  