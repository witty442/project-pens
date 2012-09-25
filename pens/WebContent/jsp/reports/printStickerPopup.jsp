<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
String reqDate = ConvertNullUtil.convertToString(request.getParameter("reqDate"));
%>

<%@page import="util.ConvertNullUtil"%>
<logic:match header="user-agent" value="MSIE">
<object id="printControls" classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" width="85" height="38"
   codebase="http://java.sun.com/update/1.5.0/jinstall-1_5_0_06-windows-i586.cab#Version=1,5,0,06" onclick="javascript:window.close();">
   <param name = "java_code"     value = "MyPrinterApplet">
   <param name = "java_codebase" value = "<c:out value='${pageContext.request.contextPath}'/>/appletsPrint">
   <PARAM NAME="cache_archive" VALUE="jasperreports-2.0.1-applet.jar,swing-layout-1.0.3.jar,appframework-1.0.3.jar,swing-worker-1.1.jar">
   <PARAM NAME="cache_version" VALUE="0.0.0.2,0.0.0.1, 0.0.0.1, 0.0.0.1">
   <param name="java_archive"    value = "jasperreports-2.0.1-applet.jar,swing-layout-1.0.3.jar,appframework-1.0.3.jar,swing-worker-1.1.jar">
   <param name="type"            value = "application/x-java-applet;">
   <param name="molecule"        value = "molecules/benzene.mol">
   <PARAM NAME="scriptable"      VALUE = "true">
   <PARAM NAME = "REPORT_URL"    VALUE ="<c:out value='${pageContext.request.contextPath}'/>/jsp/printSticker">
   <PARAM NAME = "ID"  VALUE ="<%=reqDate %>">
</object>
</logic:match>
<logic:match header="user-agent" value="Firefox">
<embed type="application/x-java-applet;version=1.5"
	java_code = "MyPrinterApplet"
    java_codebase = "<c:out value='${pageContext.request.contextPath}'/>/appletsPrint"
    cache_archive = "jasperreports-2.0.1-applet.jar,swing-layout-1.0.3.jar,appframework-1.0.3.jar,swing-worker-1.1.jar"
	cache_version = "0.0.0.4,0.0.0.1, 0.0.0.1, 0.0.0.1"
    java_archive = "jasperreports-2.0.1-applet.jar,swing-layout-1.0.3.jar,appframework-1.0.3.jar,swing-worker-1.1.jar"
    width = "85" height="38"
    molecule = "molecules/benzene.mol"
    scriptable="true"
    REPORT_URL="<c:out value='${pageContext.request.contextPath}'/>/jsp/printSticker"
	ID="<%=reqDate%>"
    pluginspage="http://java.sun.com/j2se/1.5.0/download.html">
    <noembed> </noembed>
</embed>
</logic:match>
<embed type="application/x-java-applet;version=1.5"
	java_code = "MyPrinterApplet"
    java_codebase = "<c:out value='${pageContext.request.contextPath}'/>/appletsPrint"
    cache_archive = "jasperreports-2.0.1-applet.jar,swing-layout-1.0.3.jar,appframework-1.0.3.jar,swing-worker-1.1.jar"
	cache_version = "0.0.0.4,0.0.0.1, 0.0.0.1, 0.0.0.1"
    java_archive = "jasperreports-2.0.1-applet.jar,swing-layout-1.0.3.jar,appframework-1.0.3.jar,swing-worker-1.1.jar"
    width = "85" height="38"
    molecule = "molecules/benzene.mol"
    scriptable="true"
    REPORT_URL="<c:out value='${pageContext.request.contextPath}'/>/jsp/printSticker"
	ID="<%=reqDate%>"
    pluginspage="http://java.sun.com/j2se/1.5.0/download.html">
    <noembed> </noembed>
</embed>
