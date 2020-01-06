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
	//new Epoch('epoch_popup', 'th', document.getElementById('asOfDate'));
	 $('#asOfDate').calendarsPicker({calendar: $.calendars.instance('thai','th')});
}
function search(path){
	var form = document.shopForm;
	var asOfDate = form.asOfDate; 
	if(asOfDate.value ==""){
		 asOfDate.focus();
		 alert("กรุณากรอก วันที่ขาย (As Of Date)");
		 return false;
	}
	form.action = path + "/jsp/shopAction.do?do=search&pageName=<%=request.getParameter("pageName")%>";
	form.submit();
	return true;
}

function openPopup(path,pageName,fieldName,multipleCheck){
	var form = document.shopForm;
	var param = "&pageName="+pageName;
	    param +="&fieldName="+fieldName;
        param +="&groupStore="+form.custGroup.value;
        param +="&groupStoreName=MAYA&hideAll=true";
        
	url = path + "/jsp/popupSearchAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName,fieldName){
	var form = document.shopForm;
	document.getElementById(fieldName).value = code;
} 
function search(path){
	var form = document.shopForm;
   var startDate = form.asOfDate;
   
   if(asOfDate.value =="" ){
	   asOfDate.focus();
	   alert("กรุณากรอกข้อมูล วันที่ขาย (As Of Date)");
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
					<table  border="0" cellpadding="3" cellspacing="0" width="50%">
					<tr>
						<td align="right" width="10%"> Shop</td>
						<td align="left" width="6%"><html:text property="bean.custGroup" styleId="custGroup" styleClass="disableText" readonly="true" size="15"/></td>
						<td align="right" width="6%"> </td>
						<td align="left" width="10%"></td>
					  </tr>
					 <tr>
						<td align="right" width="10%"> วันที่ขาย (As Of Date)<font color="red">*</font></td>
						<td align="left" width="6%"><html:text property="bean.asOfDate" styleId="asOfDate" readonly="false" size="15" styleClass="\" autoComplete=\"off"/></td>
						<td align="right" width="6%"> </td>
						<td align="left" width="10%"></td>
					  </tr>
					   <tr>
						<td align="right" width="10%"> Group</td>
						<td align="left" width="6%">
						<html:text property="bean.groupCodeFrom" styleId="groupCodeFrom" size="15" maxlength="6"/>
						 <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_GroupCode','groupCodeFrom','false')"/>   
						</td>
						<td align="right" width="6%"> </td>
						<td align="left" width="10%">   
						</td>
					  </tr>
					  <tr>
						<td align="right" width="10%"> From Style</td>
						<td align="left" width="6%"><html:text property="bean.styleFrom" styleId="styleFrom"size="15"/>
						 <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_Style','styleFrom','false')"/>   
						</td>
						<td align="right" width="6%">To Style</td>
						<td align="left" width="10%"><html:text property="bean.styleTo" styleId="styleTo" size="15"/>
						<input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_Style','styleTo','false')"/> 
						</td>
					  </tr>
					  <tr>
						<td align="right" width="10%"> From Pens Item</td>
						<td align="left" width="6%"><html:text property="bean.pensItemFrom" styleId="pensItemFrom" size="15"/>
						<input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_PensItem','pensItemFrom','false')"/> 
						</td>
						<td align="right" width="6%">To Pens Item</td>
						<td align="left" width="10%"><html:text property="bean.pensItemTo" styleId="pensItemTo"  size="15"/>
						<input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','FIND_PensItem','pensItemTo','false')"/> 
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
				
				  String currentPage = Utils.isNull(request.getParameter(queryStr));
				  String totalPage = "";
				  System.out.println("currentPage:"+currentPage);
				  List<ShopBean> dataList = shopForm.getResults();
				  if(dataList != null && dataList.size() >0){
				    totalPage = String.valueOf((dataList.size()/ 50)+1);
				  } 
				%>
					<c:if test="${shopForm.results != null}">
					    <br/>
						<display:table style="width:100%;" id="item" name="sessionScope.shopForm.results" defaultsort="0"  defaultorder="descending" class="resultDisp"
						    requestURI="#" sort="list" pagesize="50">	
						    <display:column  title="Group" property="groupCode" sortable="false" class="td_text_center" style="width:8%"/>
						    <display:column  title="Pens Item" property="pensItem"  sortable="false" class="td_text" style="width:6%"/>
						    <display:column  title="Matarial Master" property="style"  sortable="false" class="td_text_center" style="width:8%"/>	
						    <display:column  title="Barcode" property="barcode"  sortable="false" class="td_text_center" style="width:10%"/>	
						  
						    <display:column  title="Initial Stock" property="initSaleQty"  sortable="false" class="td_number" style="width:8%"/>
						    <display:column  title="Trans In Qty" property="transInQty"  sortable="false" class="td_number" style="width:8%"/>		
						    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false" class="td_number" style="width:8%"/>
						    <display:column  title="Return Qty" property="saleReturnQty"  sortable="false" class="td_number" style="width:8%"/>	
						    <display:column  title="Adjust Qty" property="adjustQty"  sortable="false" class="td_number" style="width:8%"/>	
						    <display:column  title="Onhand Qty " property="onhandQty"  sortable="false" class="td_number" style="width:8%"/>		
						     <%if(currentPage.equalsIgnoreCase(totalPage)){ %>
						    <display:footer>
						      <%-- <tr class="text_blod">
						          <td colspan="4" align="right"><b>รวม</b></td>
						          <td class="td_number"><bean:write name="summary" property="initSaleQty"/></td>
						          <td class="td_number"><bean:write name="summary" property="saleInQty"/></td>
						          <td class="td_number"><bean:write name="summary" property="saleOutQty"/></td>
						          <td class="td_number"><bean:write name="summary" property="saleReturnQty"/></td>
						          <td class="td_number"><bean:write name="summary" property="onhandQty"/></td>
						      </tr> --%>
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