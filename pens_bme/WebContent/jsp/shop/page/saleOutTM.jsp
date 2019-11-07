<%@page import="com.isecinc.pens.web.shop.ShopBean"%>
<%@page import="com.isecinc.pens.web.shop.ShopAction"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<jsp:useBean id="shopForm" class="com.isecinc.pens.web.shop.ShopForm" scope="session" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<!-- Calendar -->
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/calendar/jquery.calendars.picker.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.10.0.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.plugin.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.plus.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.picker.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.thai.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.thai-th.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.picker-th.js"></script>
<%
	User user = (User)session.getAttribute("user");
	String screenWidth = Utils.isNull(session.getAttribute("screenWidth"));
	String screenHeight = Utils.isNull(session.getAttribute("screenHeight"));
	String pageName = Utils.isNull(request.getParameter("pageName"));
%>

<script type="text/javascript">
function loadMe(){
	//new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
	//new Epoch('epoch_popup', 'th', document.getElementById('endDate'));
	
	 $('#startDate').calendarsPicker({calendar: $.calendars.instance('thai','th')});
	 $('#endDate').calendarsPicker({calendar: $.calendars.instance('thai','th')});
}
function search(path){
	var form = document.shopForm;
   var startDate = form.startDate;
   var endDate = form.endDate;
   
   if(startDate.value =="" && endDate.value =="" ){
	   startDate.focus();
	   alert("กรุณากรอกข้อมูลในการค้นหาอย่างน้อยหนึ่งรายการ");
	   return false;
   }
   form.action = path + "/jsp/shopAction.do?do=search&pageName=<%=request.getParameter("pageName")%>";
   form.submit();
   return true;
}

function exportExcel(path){
	var form = document.shopForm;
	form.action = path + "/jsp/shopAction.do?do=export&pageName=<%=request.getParameter("pageName")%>";
	form.submit();
	return true;
}
function clearForm(path){
	var form = document.shopForm;
	form.action = path + "/jsp/shopAction.do?do=prepare&pageAction=new&pageName=<%=request.getParameter("pageName")%>";
	form.submit();
	return true;
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
			<jsp:include page="../../program.jsp">
				<jsp:param name="function" value="<%=pageName %>"/>
			</jsp:include>
			
	      	<!-- TABLE BODY -->
	      	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	      		<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_1.gif"/></td>
		            <td width="832px;" background="${pageContext.request.contextPath}/images2/boxcont1_5.gif"/></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_2.gif"/></td>
	      		</tr>
	      		<tr>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"></td>
		            <td bgcolor="#f8f8f8">
		            
						<!-- BODY -->
						<html:form action="/jsp/shopAction">
						<jsp:include page="../../error.jsp"/>
						
						<div id="div_message" style="font-size:15px;color:green" align="center"></div> 
						<div id="div_error_message" style="font-size:15px;color:red" align="center"></div> 
						
					<div id="div_m" align="center">	
					 <!-- ************* CRITERIA ********************************************************* -->
					<table  border="0" cellpadding="3" cellspacing="0" class="body" width="50%">
					   <tr>
						<td align="right" width="10%"> จาก วันที่ขาย</td>
						<td align="left" width="20%">
						<html:text property="bean.startDate" styleId="startDate" readonly="false" size="10"/>
						&nbsp;&nbsp;ถึง วันที่ขาย&nbsp;&nbsp;
					    <html:text property="bean.endDate" styleId="endDate" readonly="false" size="10"/>
						</td>
					  </tr>
					</table>
				     <!-- ************* CRITERIA ********************************************************* -->
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center" width="80%">
								<a href="javascript:search('${pageContext.request.contextPath}')">
								  <input type="button" value="ค้นหา" class="newPosBtn"> 
								</a>&nbsp;
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value="Clear" class="newPosBtn">
								</a>&nbsp;
								<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export" class="newPosBtn">
								</a>
							</td>
							 <td align="right" width="20%">
							</td>
						</tr>
					</table>
			     </div>

				  <!-- ****** RESULT ***************************************************************** -->
				  <%
					  //get d-xxx-d parameter d-49489-p=16
					  String queryStr= request.getQueryString();
					if(queryStr.indexOf("d-") != -1){
						queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
						System.out.println("queryStr:"+queryStr);
					}
					
					  String currentPage = Utils.isNull(request.getParameter(queryStr)).equals("")?"1":Utils.isNull(request.getParameter(queryStr));
					  String totalPage = "";
					
					  List<ShopBean> dataList = shopForm.getResults();
					  if(dataList != null && dataList.size() >0){
					    totalPage = String.valueOf((dataList.size()/ 50)+1);
					  } 
					  System.out.println("currentPage:"+currentPage+",totalPage:"+totalPage);
					%>
						<c:if test="${shopForm.results != null}">
						    <br/>
							<display:table style="width:100%;" id="item" name="sessionScope.shopForm.results" defaultsort="0"  defaultorder="descending" class="resultDisp"
							    requestURI="#" sort="list" pagesize="50">	
							    <display:column  title="Sales Date" property="orderDate" sortable="false" class="td_text_center" style="width:8%"/>
							    <display:column  title="Order No" property="orderNo"  sortable="false" class="td_text_center" style="width:8%"/>
							    <display:column  title="Pens Item" property="pensItem"  sortable="false" class="td_text" style="width:6%"/>
							    <display:column  title="Barcode" property="barcode"  sortable="false" class="td_text_center" style="width:10%"/>	
							    <display:column  title="Style" property="style"  sortable="false" class="td_text_center" style="width:8%"/>	
							    <display:column  title="Qty" property="qty"  sortable="false" class="td_number" style="width:8%"/>
							    <display:column  title="Free Item" property="freeItem"  sortable="false" class="td_text_center" style="width:5%"/>	
							    <%if( !user.getRole().getKey().equalsIgnoreCase(User.WACOAL)) {%>
							    <display:column  title="Unit Price" property="unitPrice"  sortable="false" class="td_number" style="width:7%"/>		
							    <display:column  title="Line Amount" property="lineAmount"  sortable="false" class="td_number" style="width:7%"/>
							    <display:column  title="Discount " property="discount"  sortable="false" class="td_number" style="width:7%"/>	
							    <display:column  title="Vat Amount " property="vatAmount"  sortable="false" class="td_number" style="width:7%"/>	
							    <%} %>
							    <display:column  title="Total Line Amount(In. Vat) " property="totalAmount"  sortable="false" class="td_number" style="width:10%"/>	
							    <display:column  title="Total Line Amount(Ex. Vat) " property="totalAmountExVat"  sortable="false" class="td_number" style="width:10%"/>	
							     <%if(currentPage.equalsIgnoreCase(totalPage)){ %>
							    <display:footer>
							       <tr class="text_blod">
							          <td colspan="5" align="right"><b>รวม</b></td>
							          <td class="td_number"><bean:write name="summary" property="qty"/></td>
							          <td ></td>
							          <%if( !user.getRole().getKey().equalsIgnoreCase(User.WACOAL)) {%>
								          <td ></td>
								          <td class="td_number"><bean:write name="summary" property="lineAmount"/></td>
								          <td class="td_number"><bean:write name="summary" property="discount"/></td>
								          <td class="td_number"><bean:write name="summary" property="vatAmount"/></td>
							          <%} %>
							          <td class="td_number"><bean:write name="summary" property="totalAmount"/></td>
							          <td class="td_number"><bean:write name="summary" property="totalAmountExVat"/></td>
							      </tr> 
							    </display:footer>	
							    <%} %>
							</display:table>
					   </c:if>
				  
                    <!-- ****** RESULT ***************************************************************** -->

					<!-- hidden field -->
					<input type="hidden" name="pageName" value="<%=request.getParameter("pageName") %>"/>
					</html:form>
					<!-- BODY -->
					</td>
					<td width="6px;" background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"></td>
				</tr>
				<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_4.gif"/></td>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_3.gif"/></td>
	          	</tr>
    		</table>
    	</td>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
   <tr>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
   			<jsp:include page="../../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../../footer.jsp"/></td>
  	</tr>
</table>
<script>
   loadCalendar();
</script>
</body>
</html>