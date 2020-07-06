<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.stock.StockBean"%>
<%@page import="com.isecinc.pens.web.stock.StockForm"%>
<%@page import="com.pens.util.PageVisit"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@page import="com.isecinc.pens.web.stock.StockConstants"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="stockForm" class="com.isecinc.pens.web.stock.StockForm" scope="session" />
<%
StockBean bean = ((StockForm)session.getAttribute("stockForm")).getBean();
User user = (User) request.getSession().getAttribute("user");
String role = user.getRoleSalesTarget();
String pageName = Utils.isNull(request.getParameter("pageName"));
String popup = Utils.isNull(request.getParameter("popup"));
if(pageName.equals("")){
	pageName = stockForm.getPageName();
}
String pageNameTemp = pageName;
if(StockConstants.PAGE_STOCK_CREDIT.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "ReportStockCredit";
}else if(StockConstants.PAGE_STOCK_CALLC_CREDIT.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "ReportStockCallCardCredit";
}

/** Count Visit Page */
PageVisit.processPageVisit(request,pageNameTemp);

//check user login is map cust sales TT to fillter search customer popup 
boolean isUserMapCustSalesTT = GeneralDAO.isUserMapCustSalesTT(user);

String screenWidth = "";
if(session.getAttribute("screenWidth") != null){ 
	screenWidth = (String)session.getAttribute("screenWidth");
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<!-- For fix Head and Column Table -->
 <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-stickytable-3.0.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/jquery-stickytable-3.0.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<script>
/** disable back button alway **/
window.location.hash="no-back-button";
window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
window.onhashchange=function(){window.location.hash="no-back-button";}
</script>
<script type="text/javascript">
window.onload = function(){
	loadMe();
}

function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
}
function clearForm(path){
	var form = document.stockForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/stockAction.do?do=prepareSearch&action=new&pageName="+pageName;
	form.submit();
	return true;
}
function searchReport(path){
	var form = document.stockForm;
	//alert(form.startDate.value);
   
	 if( $('#customerCode').val()==""){
		alert("กรุณาระบุ รหัสร้านค้า");
		$('#customerCode').focus();
		return false;
	 } 
	 /* if( $('#brand').val()==""){
		alert("กรุณาระบุ แบรนด์");
		$('#brand').focus();
		return false;
	 }  */
	 if( $('#reportType').val()==""){
		alert("กรุณาระบุ รูปแบบ");
		$('#reportType').focus();
		return false;
	 } 
	form.action = path + "/jsp/stockAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function exportReport(path){
	var form = document.stockForm;
	 if( $('#customerCode').val()==""){
		alert("กรุณาระบุ รหัสร้านค้า");
		$('#customerCode').focus();
		return false;
	 } 
	/*  if( $('#brand').val()==""){
		alert("กรุณาระบุ แบรนด์");
		$('#brand').focus();
		return false;
	 }  */
	 if( $('#reportType').val()==""){
		alert("กรุณาระบุ รูปแบบ");
		$('#reportType').focus();
		return false;
	 } 
	form.action = path + "/jsp/stockAction.do?do=exportReport&action=newsearch";
	form.submit();
	return true;
}
function getBrandNameKeypress(e,brandId){
	var form = document.stockForm;
	if(e != null && e.keyCode == 13){
		if(brandId.value ==''){
			form.name.value = '';
		}else{
			getBrandNameModel(brandId);
		}
	}
}
//Return String :brandName
function getBrandNameModel(brandId){
	var returnString = "";
	var form = document.stockForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/getBrandNameAjax.jsp",
			data : "brandId=" + brandId.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	if(returnString !=''){
		//var retArr = returnString.split("|");
		form.brandName.value =returnString;	
	}else{
		alert("ไม่พบข้อมูล");
		form.brandId.value = "";	
		form.brandName.value = "";
	}
}

function openPopup(path,pageName){
	var form = document.stockForm;
	var param = "&hideAll=true&pageName="+pageName;
	if("CustomerCreditSales" == pageName){
		param += "&selectone=true";
		param += "&salesrepCode="+form.salesrepCode.value;
		param += "&startDate="+form.startDate.value;
		<%if(isUserMapCustSalesTT){%>
		    param +="&userName=<%=user.getUserName()%>";
		<%}%>
	}else if("SalesrepCreditSales" == pageName){
		param += "&selectone=false";
		<%if(isUserMapCustSalesTT){%>
		    param +="&userName=<%=user.getUserName()%>";
		<%}%>
	}else if("Brand" == pageName){
		param += "&selectone=false";
	}
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}

function setDataPopupValue(code,desc,pageName){
	var form = document.stockForm;
	if("Brand" == pageName){
		form.brand.value = code;
	}else if("CustomerCreditSales" == pageName){
		form.customerCode.value = code;
		form.customerName.value = name;
	}else if('SalesrepCreditSales' == pageName){
		form.salesrepCode.value = code;
	}
} 
function openDetail(recordType,requestDate,itemCode){
	var form = document.stockForm;
	var path = document.getElementById("path").value;
	var param  = "startDate="+form.startDate.value;
	    param += "&salesrepCode="+form.salesrepCode.value;
	    param += "&customerCode="+form.customerCode.value;
	    param += "&brand="+form.brand.value;
	    param += "&requestDate="+requestDate;
	    param += "&itemCode="+itemCode;
	    param += "&recordType="+recordType;
	var url = path +"/jsp/stock/popup/detailStockCallCardPopup.jsp?"+param;
	PopupCenter(url,"",250,250);
}
</script>
<style>
.td_bg_lineH{
	background-color: #03A4B6;
	text-align: center;
	height: 30px;
}
.td_bg_lineS{
	background-color: #AED6F1;
	color:red; 
	text-align: center;
}
.td_bg_lineS_num{
	background-color: #AED6F1;
	color:red; 
	text-align: right;
}
.td_bg_lineA{
	background-color: #F2D7D5;
	text-align: center;
}
.td_bg_lineA_num{
	background-color: #F2D7D5;
	text-align: right;
}
 #scroll {
<%if(!"0".equals(screenWidth)){%>
    width:<%=screenWidth%>px; 
    height:400px; 
    /* background:#A3CBE0; */
	/* border:1px solid #000; */
	overflow:auto;
	white-space:nowrap;
	/* box-shadow:0 0 25px #000; */
<%}%>
</style>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"  style="height: 100%;">
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
				<jsp:param name="function" value="<%=pageNameTemp%>"/>
			</jsp:include>
			<!-- Hidden Field -->
		    <html:hidden property="pageName" value="<%=pageName %>"/>
		    <html:hidden property="popup" value="<%=popup %>"/>
		    <input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
		    
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
						<html:form action="/jsp/stockAction">
						<jsp:include page="../../error.jsp"/>

						<div align="center">
						<table align="center" border="0" cellpadding="3" cellspacing="0" >
					       <tr>
					            <td colspan="3">
					             ใช้ข้อมูลตั้งแต่ &nbsp;&nbsp;
					              <html:text property="bean.startDate" styleId="startDate" size="10" readonly="true" styleClass="\" autoComplete=\"off"/> 
					            &nbsp; ถึงปัจจุบัน</td>
							</tr>
							<tr>
					            <td align="right">พนักงานขาย</td>
								<td colspan="2">
								    <html:text property="bean.salesrepCode" styleId="salesrepCode" size="20" styleClass="\" autoComplete=\"off" />
								     <input type="button" name="x2" value="..." onclick="openPopup('${pageContext.request.contextPath}','SalesrepCreditSales')"/>   
								&nbsp;&nbsp;&nbsp;&nbsp;
								รหัสร้านค้า <font color="red">*</font> &nbsp;
								  <html:text property="bean.customerCode" styleId="customerCode" size="20" styleClass="\" autoComplete=\"off" />
								     <input type="button" name="x2" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerCreditSales')"/>   
								  <html:hidden property="bean.customerName" styleId="customerName"/>
								</td>
							</tr>	
							<tr>
					            <td align="right"> แบรนด์ <!-- <font color="red">*</font> --></td>
								<td colspan="2">
								   <html:text property="bean.brand" styleId="brand" size="20" styleClass="\" autoComplete=\"off" />
								    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','Brand')"/>   
								&nbsp;&nbsp;&nbsp;&nbsp;
								รูปแบบการแสดงผล&nbsp;
								 <html:select property="bean.reportType">
								     <html:option value="1">แสดง SKU แนวตั้ง</html:option>
								     <html:option value="2">แสดง SKU แนวนอน</html:option>
								  </html:select>
								</td>
							</tr>	
							<tr>
					            <td align="right"> หมายเหตุ</td>
								<td>
								   <table  border="0" cellpadding="3" cellspacing="0" >
							         <tr> <td align="left" class="td_bg_lineS">บรรทัดสีฟ้า คือ ข้อมูลที่ได้นับสต๊อก</td> </tr>
								      <tr> <td align="left" class="td_bg_lineA">บรรทัดสีส้ม คือ ข้อมูลการเปิดบิลขายและแถม</td></tr>
								    </table>
								   </td>
								   <td>
								    <html:checkbox property="bean.dispOrderOnly">&nbsp; แสดงเฉพาะข้อมูลยอดขาย</html:checkbox>
								</td>
							</tr>	
					   </table>
					   <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
									<a href="javascript:searchReport('${pageContext.request.contextPath}')">
									  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
									</a>&nbsp;
									 <a href="javascript:exportReport('${pageContext.request.contextPath}')">
									  <input type="button" value="  Export  " class="newPosBtnLong"> 
									</a>
									&nbsp;
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									  <input type="button" value="   Clear   " class="newPosBtnLong">
									</a>			
								</td>
							</tr>
						</table>

					    </div>
					 
					   <!-- ************************Result ***************************************************-->
					  <%
					 // System.out.println("Results:"+request.getSession().getAttribute("RESULTS"));
					  if(request.getSession().getAttribute("stockForm_RESULTS") != null) {
					   %>
						<% out.println(request.getSession().getAttribute("stockForm_RESULTS")); %>
						<script>
							//load jquery
							$(function() {
								//Load fix column and Head
								$('#myTable').stickyTable({overflowy: true});
							});
						</script>
					 <%}%>
					<!-- ************************Result ***************************************************-->
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
</body>
</html>

 <!-- Control Save Lock Screen -->
<jsp:include page="../../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->
