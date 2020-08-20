<%@page import="com.isecinc.pens.web.buds.page.ConfPickingDAO"%>
<%@page import="com.isecinc.pens.bean.ConfPickingBean"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
<title>Search Transport</title>
<!-- For fix Head and Column Table -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-stickytable-3.0.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/jquery-stickytable-3.0.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<jsp:useBean id="popupForm" class="com.isecinc.pens.web.popup.PopupForm" scope="request" />
<%
User user = (User) request.getSession().getAttribute("user");
String screenWidth = ""+(Utils.convertStrToInt(Utils.isNull(session.getAttribute("screenWidth")))-50);
String screenHeight = Utils.isNull(session.getAttribute("screenHeight")); 

//Get Paramter
String pickingNo = Utils.isNull(request.getParameter("pickingNo"));
String regionCri = Utils.isNull(request.getParameter("regionCri"));
regionCri = new String(regionCri.getBytes("ISO8859_1"), "UTF-8");

String provinceCri = Utils.isNull(request.getParameter("provinceCri"));
provinceCri = new String(provinceCri.getBytes("ISO8859_1"), "UTF-8");

String amphurCri = Utils.isNull(request.getParameter("amphurCri"));
amphurCri = new String(amphurCri.getBytes("ISO8859_1"), "UTF-8");

ConfPickingBean confPickingBean = new ConfPickingBean();
confPickingBean.setPickingNo(pickingNo);
confPickingBean.setRegionCri(regionCri);
confPickingBean.setProvinceCri(provinceCri);
confPickingBean.setAmphurCri(amphurCri);
//search Order By Manual
confPickingBean = ConfPickingDAO.searchPickingDetail("edit","ConfPickingAddOrderManual", confPickingBean, false, user);
if(confPickingBean != null && confPickingBean.getDataStrBuffer() != null){
}else{
	request.setAttribute("Message", "ไม่พบข้อมูล");
}
%>
<script type="text/javascript">

function selectChk(){
	var chkOrder = null;
	var orderNoAll = '';
	if("ยืนยันเพิ่มรายการ Order นี้"){
		//get RegionChk
		chkOrder = document.getElementsByName("chkOrder");
		for(var i=0;i<chkOrder.length;i++){
			if(chkOrder[i].checked){
				orderNoAll +=chkOrder[i].value+",";
			}
		}
		
	    window.opener.setMainAddOrderPickingManual(orderNoAll);
		window.close();
	}
	return false;
}

</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/tempAction">
<p></p>
<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" >
    <tr height="30px" class="txt1" >
		<td width="90%" colspan="2" align="center"><b><font size="2">เพิ่ม Order Manual </font></b></td>
	</tr>
</table>

 <!-- ****** RESULT *************************************************** -->
     <%
     if(confPickingBean != null && confPickingBean.getDataStrBuffer() != null){
    	%>
    	<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
			<tr>
				<td align="center">
					<input type="button" name="OK" value="ยืนยันเลือกข้อมูล" onclick="selectChk()"  class="newPosBtnLong"/>
					<input type="button" name="CLOSE" value="ปิดหน้าจอนี้" onclick="javascript:window.close();"  class="newPosBtnLong"/>
				</td>
			</tr>
		</table>

    	 <div style="height:300px;width:<%=screenWidth%>px;">
    	<%out.println(confPickingBean.getDataStrBuffer().toString());%>
    	 </div>
    	<script>
			//load jquery
			 $(function() { 
				//Load fix column and Head
				$('#tblProduct').stickyTable({overflowy: true});
			}); 
		 </script>
    <%}else{ %>
      <div align="center">
         <font color="red" size="2">ไม่พบข้อมูล</font>
      </div>
    <%} %>
 <!-- ***************************************************************** -->


</html:form>
</body>
</html>