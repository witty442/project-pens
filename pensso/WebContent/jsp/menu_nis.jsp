<%@page import="com.pens.util.UserUtils"%>
<%@page import="com.pens.util.EncyptUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page import="com.isecinc.pens.bean.User"%>
<%
  String role = ((User)session.getAttribute("user")).getType();
  User user = (User)session.getAttribute("user");
  
  /** No of menu **/
  int no = 0;
  int subNo = 0;
%>

<!-- **************************** MENU NISSIN *********************************************** -->
 <%if ( UserUtils.userInRole("ROLE_ALL",user,new String[]{User.ADMIN,User.NIS}) ){%>
     <li>
       <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/customerNissin.do';">
       <span><bean:message key="NissinCustomerSearch" bundle="sysprop"/></span>
       </a> 
    </li>
 <%} %>
    <%if ( UserUtils.userInRole("ROLE_ALL",user,new String[]{User.ADMIN,User.NIS,User.NIS_PENS,User.NIS_VIEW,User.VAN}) 
    		|| role.equalsIgnoreCase("VAN")){%>
    <li>
       <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/orderNissinAction.do?do=prepareSearchHead&action=new';">
       <span>
          <%if ( UserUtils.userInRole("ROLE_ALL",user,new String[]{User.ADMIN,User.NIS_PENS})  || role.equalsIgnoreCase("VAN")){%>
              <bean:message key="OrderNissinSearch" bundle="sysprop"/>
          <%}else{ %>
              <bean:message key="OrderNissinSearch2" bundle="sysprop"/>
          <%} %>
       </span>
       </a>
    </li>
 <%} %>
<!-- **************************** /MENU NISSIN ********************************************** -->
  