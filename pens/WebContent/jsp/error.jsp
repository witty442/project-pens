<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<center>
<% String jsMsg = null;
	if(request.getAttribute("JSMsg") !=null)
		jsMsg = (String)request.getAttribute("JSMsg");
	
	if(jsMsg != null && jsMsg.trim().length() >0){
%>
<script>alert('<%=jsMsg%>');</script>
<% } %>
<span id="msg" class="errormsg">
<logic:present name="Message" scope="request">
	${Message}
</logic:present>
</span>
</center>

