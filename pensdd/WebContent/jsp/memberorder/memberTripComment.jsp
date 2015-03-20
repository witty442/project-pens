<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
String tripNo=request.getParameter("tripNo");
String orderId=request.getParameter("orderId");
String exported = request.getParameter("exported");

List<MemberTripComment> tripComments = new MMemberTripComment().lookUp(Integer.parseInt(orderId),Integer.parseInt(tripNo));

MemberTripComment tripComment = new MemberTripComment();
if(tripComments.size()>0){
	tripComment = tripComments.get(0);
}

String success = InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc();
String fail = InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc();

%>

<%@page import="com.isecinc.pens.bean.MemberTripComment"%>
<%@page import="com.isecinc.pens.model.MMemberTripComment"%>
<%@page import="java.util.List"%>
<%@page import="util.ConvertNullUtil"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.init.InitialMessages"%>
<%@page import="com.isecinc.core.bean.Messages"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript">
function loadMe(){
	$('#comment').focus();
}

function saveComment(){
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/memberorder/commentSave.jsp",
			data : "comment=" + encodeURIComponent($('#comment').val())
			+"&tripId="+$('#tripId').val()
			+"&tripNo="+$('#tripNo').val()
			+"&orderId="+$('#orderId').val()
			,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				if(returnString!=''){
					alert('<%=success%>');
				}else{
					alert('<%=fail%>');
				}
			}
		}).responseText;
	});
}
</script>
</head>
<body onload="loadMe();">
<%if(exported.equalsIgnoreCase("Y")){ %>
<textarea cols="30" rows="5" id="comment" name="comment" disabled="disabled" class="disableText"><%=ConvertNullUtil.convertToString(tripComment.getTripComment()) %></textarea>
<%}else{ %>
<textarea cols="30" rows="5" id="comment" name="comment" ><%=ConvertNullUtil.convertToString(tripComment.getTripComment()) %></textarea>
<%} %>

<br>
<%if(!exported.equalsIgnoreCase("Y")){ %>
<input type="button" value="บันทึก" onclick="saveComment();">
<%} %>
<input type="button" value="ปิดหน้าจอ" onclick="window.close();">
<input type="hidden" id="tripId" name="tripId" value="<%=tripComment.getId() %>">
<input type="hidden" id="tripNo" name="tripNo" value="<%=tripNo %>">
<input type="hidden" id="orderId" name="orderId" value="<%=orderId %>">
</body>
</html>