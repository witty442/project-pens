
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.model.MOrder"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.Messages"%>
<%@page import="com.isecinc.pens.init.InitialMessages"%>
<%@page import="com.isecinc.pens.model.MTrip"%><%@ page language="java"
	contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String memberId = request.getParameter("memberId");
	String selected = request.getParameter("selected");
	List<Order> orders = new MOrder().lookUpForMember(memberId,selected);
%>

<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.text.DecimalFormat"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript">
function search(){
	//call ajax
	$('#spnWait').show();
	$('#dvresult').html('');
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/orderLineQuery.jsp",
			data : "orderId=" + $('#order').val()+"&selected=<%=selected%>"
			,
			async: true,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				$('#dvresult').html(returnString);
				$('#spnWait').hide();
			}
		}).responseText;
	});
}

function selectItem(){
	var items = new Array();
	var chks = document.getElementsByName('chkLine');
	var orderId = document.getElementsByName('lineOrderId');
	var orderNo = document.getElementsByName('lineOrderNo');
	var lineDesc = document.getElementsByName('lineDesc');
	var lineAmount = document.getElementsByName('lineAmount');
	var item;
	var j=0;
	for(i=0;i<chks.length;i++){
		if(chks[i].checked){
			item=new Object();
			item.lineId=chks[i].value;
			item.orderId=orderId[i].value;
			item.orderNo=orderNo[i].value;
			item.lineDesc=lineDesc[i].value;
			item.lineAmount=lineAmount[i].value;
			items[j]=item;
			j++;
		}
	}
	window.opener.addBillLine('${pageContext.request.contextPath}',items);
	window.close();
}
</script>
</head>
<body>
เลขที่รายการขาย : 
<select name="order" id="order" onchange="search()">
<option></option> 
<%for(Order o : orders){ %>
<option value="<%=o.getId() %>"><%=o.getOrderNo() %> - <%=new DecimalFormat("#,##0.00").format(o.getNetAmount()) %></option>
<%} %>
</select>
<span id="spnWait" style="display: none;"><img src="${pageContext.request.contextPath}/icons/waiting.gif" align="absmiddle" border="0"/></span>
<br><br>
<div id="dvresult"></div>
<br><br>
<center>
<input type="button" value="ตกลง" class="newPosBtn" onclick="return selectItem();">
<input type="button" value="ปิดหน้าจอ" class="newNegBtn" onclick="window.close();">
</center>
</body>
</html>