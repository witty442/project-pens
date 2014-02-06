<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.report.salesanalyst.SAConstants"%>

<li><a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp';"><span><bean:message key="HomeMenu" bundle="sysprop"/></span></a>
	<ul>
          	<li>
			<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/userAction.do?do=init&action=new';"><span><bean:message key="User" bundle="sysprop"/></span></a>
		</li>
		<li>
			<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/groupRoleAction.do?do=prepare&action=new';"><span><bean:message key="Group" bundle="sysprop"/></span></a>
		</li>
		<li>
			<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/roleAction.do?do=prepare&action=new';"><span><bean:message key="Role" bundle="sysprop"/></span></a>
		</li> 
		
	</ul>
</li>

   