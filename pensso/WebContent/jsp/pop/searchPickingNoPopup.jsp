<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.model.MTransport"%>
<%@page import="com.isecinc.pens.bean.Transport"%>
<%@page import="java.util.List"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="popupForm" class="com.isecinc.pens.web.popup.PopupForm" scope="request" />
<%
//Search
User user = (User) request.getSession().getAttribute("user");
PopupBean popupFormCri = new PopupBean();
List<PopupBean> results  = null;//
if(request.getAttribute("DATA_LIST") != null){
	results = (List<PopupBean>)request.getAttribute("DATA_LIST");
}
%>
<html>
<head> 
<title>Search Transport</title>
<!-- For fix Head and Column Table -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-stickytable-3.0.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/jquery-stickytable-3.0.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<script type="text/javascript">
function searchPopup(path, type) {
    document.popupForm.action = path + "/jsp/popupAction.do?do=search";
    document.popupForm.submit();
   return true;
}

function selectChk(){
	var temp = "";
	var pickingNoRadio = document.getElementsByName("pickingNo");
	for(var i=0;i<pickingNoRadio.length;i++){
		if(pickingNoRadio[i].checked){
			temp =pickingNoRadio[i].value;
			break;
		}//if
	}//for
    window.opener.setMainValue('<%=Utils.isNull(request.getParameter("page"))%>',temp);
	window.close();
}


</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/popupAction">
<html:hidden property="page" value="PICKING_NO"></html:hidden>

<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" class="tableHead">
    <tr height="21px" class="headTitle">
		<td width="100%" colspan="2" align="center"><b>ค้นหาข้อมูล Picking No</b></td>
	</tr>
	<tr height="21px" class="txt1">
		<td width="15%" ><b>Picking No</b>  </td>
		<td width="90%" ><html:text property="bean.codeSearch" size="20" styleClass="\" autoComplete=\"off"/>
		<input type="button" name="search" value="  Search  " class="newPosBtnLong"
		onclick="searchPopup('<%=request.getContextPath()%>','')" />
		</td>
	</tr>
</table>

<!-- RESULT -->
<%if(results != null && results.size() >0){ %>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
	<tr>
		<td align="center">
			<input type="button" name="OK" value="ยืนยันเลือกข้อมูล" onclick="selectChk()"  class="newPosBtnLong"/>
			&nbsp;<input type="button" name="CLOSE" value="ปิดหน้าจอนี้" onclick="javascript:window.close();"  class="newPosBtnLong"/>
		</td>
	</tr>
</table>
<div style='height:600px;width:700px;'>
<table id='myTable' class='table table-condensed table-striped' border='1' cellpadding='3' cellspacing='1'> 
   <thead> 
    <tr>
        <th >.</th>
		<th >Picking No</th>
		<th >Transaction Date</th>
	</tr>
	</thead>
	<%
	  for(int i=0;i<results.size();i++){
		  PopupBean t = results.get(i);
	%>
	<tbody>
	<tr> 
	    <td align="center"><input type="radio" name="pickingNo" value="<%=t.getCode()%>"></td>
	    <td align="center">&nbsp;<%=t.getCode()%></td>
	    <td align="center"><%=t.getDesc()%></td>
	</tr>
	<%
	}//for
	%>
	</tbody>
</table>
</div>

<script>
	//load jquery
	$(function() {
		//Load fix column and Head
		$('#myTable').stickyTable({overflowy: true});
	});
</script>
<%} %>
<!-- RESULT -->

</html:form>
</body>
</html>