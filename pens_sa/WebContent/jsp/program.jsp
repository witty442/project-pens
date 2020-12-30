<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String system="";
String function="";
String code="";
String programName = "";
system=(request.getParameter("system")==null?"":request.getParameter("system"));
function=(request.getParameter("function")==null?"":request.getParameter("function"));
code=(request.getParameter("code")==null?"":request.getParameter("code"));

programName = function;
//System.out.println(system);
//System.out.println(function);
//System.out.println(code);
String fontColor = "";
if("ProjectCAnalyst".equalsIgnoreCase(function)){
	fontColor = "red";
}

if(system.length()>0) system = SystemProperties.getCaption(system,Locale.getDefault());
if(function.length()>0) function = SystemProperties.getCaption(function,Locale.getDefault());
if(code.equals("null")){
	code = SystemProperties.getCaption(SystemProperties.CREATE_NEW_RECORD,Locale.getDefault());
}else if(code != null && !code.equals("")){
	code = SystemProperties.getCaption(code,Locale.getDefault());
}


%>

<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	<tr>
 		<td width="41" align="left"><img src="${pageContext.request.contextPath}/images2/bullet.gif" width="37" height="19" /></td>
   		<td class="nav_txt" style="background:#FFFFFF url('${pageContext.request.contextPath}/images2/bulletLine.gif') no-repeat right;">
   		<%-- 	<a href="javascript:popupFull('/pens_help/jsp/doc/pens_sa/<%=programKey %>_DOC.jsp')">
   		       <font color="<%=fontColor %>">
   		         <%=function.length()>0? function:"" %><%=code.length()>0? " > " + code : "" %>
   		       </font>
   			</a> --%>
   		   <strong>
	   			<a href="javascript:popupFull('/pens_help/jsp/document.jsp?sessionId=<%=session.getId() %>&module=PENSSA&programName=<%=programName%>')">
	   		       <font color="<%=fontColor %>">
	   		        <%=function.length()>0? function:"" %><%=code.length()>0? " > " + code : "" %>
	   		       </font>
	   			</a>
   			</strong>
   		</td>
	</tr>
 	<tr>
 		<td colspan="3"><img src="${pageContext.request.contextPath}/images2/blank.gif" width="1" height="15" /></td>
	</tr>
</table>
