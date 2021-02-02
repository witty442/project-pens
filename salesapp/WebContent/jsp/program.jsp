<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String system="";
String function="";
String code="";
system=(request.getParameter("system")==null?"":request.getParameter("system"));
function=(request.getParameter("function")==null?"":request.getParameter("function"));
code=(request.getParameter("code")==null?"":request.getParameter("code"));

//System.out.println(system);
//System.out.println(function);
//System.out.println(code);


if(system.length()>0) system = SystemProperties.getCaption(system,Locale.getDefault());
if(function.length()>0) function = SystemProperties.getCaption(function,Locale.getDefault());
if(code.equals("null")) code = SystemProperties.getCaption(SystemProperties.CREATE_NEW_RECORD,Locale.getDefault());
%>

<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>

<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	<tr>
 		<td width="5%" align="left"><img src="${pageContext.request.contextPath}/images2/bullet.gif" width="37" height="19" /></td>
   		<td width="85%" style="background:#FFFFFF url('${pageContext.request.contextPath}/images2/bulletLine.gif') no-repeat right;">
   			<strong><%=function.length()>0? function:"" %><%=code.length()>0? " > " + code : "" %></strong>
   		</td>
   		<td width="10%" align="right" nowrap>
   		   <div id="time" style="font-weight: bold"> </div>
   		</td>
	</tr>
 	<tr>
 		<td colspan="4">
 		  <img src="${pageContext.request.contextPath}/images2/blank.gif" width="1" height="15" />
 		</td>
	</tr>
</table> 
<script>
	function updateClock() {
		 var currentdate = new Date(); 
		  var datetime = "" + currentdate.getDate() + "/"
		                  + (currentdate.getMonth()+1)  + "/" 
		                  + (currentdate.getFullYear()+543) + " "  
		                  + currentdate.getHours() + ":"  
		                  + ((""+currentdate.getMinutes()).length==1?"0"+currentdate.getMinutes():currentdate.getMinutes());
		                  
	    // set the content of the element with the ID time to the formatted string
	    document.getElementById('time').innerHTML = ""+datetime;
	
	    // call this function again in 1000ms
	    setTimeout(updateClock, 1000);
	}
	updateClock(); // initial call
</script>
