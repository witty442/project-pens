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
<style>
  .tr_red{
    background-color: #EC7063;
    color:black;
  }
  .tr_negative{
     background-color: #d6d6c2;
     color:black;
  }
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script> --%>
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
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	var form = document.stockForm;
	
	new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
	new Epoch('epoch_popup', 'th', document.getElementById('endDate'));
	
	<%if( !"".equals(Utils.isNull(bean.getSalesChannelNo())) || !"".equals(Utils.isNull(bean.getCustCatNo())) ) { %>
         document.getElementsByName('bean.salesChannelNo')[0].value = "<%=bean.getSalesChannelNo()%>";
         document.getElementsByName('bean.custCatNo')[0].value = "<%=bean.getCustCatNo()%>";

	     document.getElementsByName('bean.salesrepCode')[0].value = '<%=bean.getSalesrepCode()%>';
	<% } %>
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
	 if( $('#startDate').val()==""){
		alert("กรุณาระบุ วันที่เริ่ม");
		$('#startDate').focus();
		return false;
	} 
	if( $('#endDate').val()==""){
		alert("กรุณาระบุ วันที่สิ้นสุด");
		$('#endDate').focus();
		return false;
	} 
	form.action = path + "/jsp/stockAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}

function sort(columnNameSort,orderSortType){
  var form = document.stockForm;
  var path = document.getElementById("path").value;
  var param ="&columnNameSort="+columnNameSort+"&orderSortType="+orderSortType;
  form.action = path + "/jsp/stockAction.do?do=searchHead&action=sort"+param;
  form.submit();
  return true;
}
function exportReport(path){
	var form = document.stockForm;
	 if( $('#startDate').val()==""){
		alert("กรุณาระบุ วันที่เริ่ม");
		$('#startDate').focus();
		return false;
	} 
	if( $('#endDate').val()==""){
		alert("กรุณาระบุ วันที่สิ้นสุด");
		$('#endDate').focus();
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
function loadSalesrepCodeList(){
	var cboDistrict = document.getElementsByName('bean.salesrepCode')[0];
	var param  ="salesChannelNo=" + document.getElementsByName('bean.salesChannelNo')[0].value;
	    param +="&custCatNo="+ document.getElementsByName('bean.custCatNo')[0].value;
	    param +="&salesZone="+ document.getElementsByName('bean.salesZone')[0].value;
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/stock/ajax/genSalesrepCodeListAjax.jsp",
			data : param,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboDistrict.innerHTML=returnString;
			}
		}).responseText;
	});
}
function openPopup(path,pageName){
	var form = document.stockForm;
	var param = "&pageName="+pageName;
	if("CustomerStock" == pageName){
        param +="&salesChannelNo="+form.salesChannelNo.value;
        param +="&salesrepCode="+form.salesrepCode.value;
	}else if("ItemStock" == pageName){
		param +="&brand="+form.brand.value;
	}else if("BrandStock" == pageName){
		param +="&selectone=false";
	}
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.stockForm;
	if("BrandStock" == pageName){
		form.brand.value = code;
	}else if("CustomerStock" == pageName){
		form.customerCode.value = code;
	}else if("ItemStock" == pageName){
		form.itemCode.value = code;
	}
} 
function popupBrandSaveZone(path){
	var url = path + "/jsp/stock/popup/brandSaveZonePopup.jsp";
	PopupCenterFullHeight(url,"",500);
}
</script>

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
				                <td align="right"> วันที่ตรวจนับ<font color="red">*</font></td>
								<td >					
									 <html:text property="bean.startDate" styleId="startDate" size="10" readonly="true" styleClass=""/>
								       -
									 <html:text property="bean.endDate" styleId="endDate" size="10" readonly="true" styleClass=""/>
								</td>
								<td colspan="2">ประเภทรายงาน:
								 <font color="red"></font>
								   <html:select property="bean.typeSearch" styleId="typeSearch">
										<html:options collection="TYPE_SEARCH_LIST" property="reportValue" labelProperty="reportType"/>
								    </html:select> 
								</td>
							</tr>
							<tr>
				                <td align="right"> ประเภทขาย </td>
								<td colspan="2">
								 <html:select property="bean.custCatNo" styleId="custCatNo">
										<html:options collection="CUST_CAT_LIST" property="custCatNo" labelProperty="custCatDesc"/>
								    </html:select>
								      &nbsp;&nbsp;&nbsp;&nbsp;
								ภาคการขาย 
								    <html:select property="bean.salesChannelNo" styleId="salesChannelNo" onchange="loadSalesrepCodeList()">
										<html:options collection="SALES_CHANNEL_LIST" property="salesChannelNo" labelProperty="salesChannelDesc"/>
								    </html:select>
								      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
								       ภาคตามสายดูแล 
								      <html:select property="bean.salesZone" styleId="salesZone" onchange="loadSalesrepCodeList()">
										<html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
								    </html:select>
								    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
								       พนักงานขาย 
								      <html:select property="bean.salesrepCode" styleId="salesrepCode" >
										<html:options collection="SALESREP_LIST" property="salesrepCode" labelProperty="salesrepCode"/>
								    </html:select>
								</td>
							</tr>	
							<tr>
				                <td align="right"> แบรนด์ </td>
								<td colspan="2">
								   <html:text property="bean.brand" styleId="brand" size="20" styleClass="\" autoComplete=\"off" />
								    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','BrandStock')"/>   
								    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								    รหัสร้านค้า
								    <html:text property="bean.customerCode" styleId="customerCode" size="20" styleClass="\" autoComplete=\"off" />
								     <input type="button" name="x2" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerStock')"/>   
								  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								  SKU
								   <html:text property="bean.itemCode" styleId="itemCode" size="20" styleClass="\" autoComplete=\"off" />
								     <input type="button" name="x2" value="..." onclick="openPopup('${pageContext.request.contextPath}','ItemStock')"/>   
								</td>
							</tr>	
							<tr>
				                <td align="right"> รูปแบบการแสดงผล  <font color="red">*</font></td>
								<td colspan="2">
								   <html:select property="bean.reportType" styleId="reportType">
										<html:options collection="REPORT_TYPE_LIST" property="reportValue" labelProperty="reportType"/>
								    </html:select> 
								    
								      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
								    <html:checkbox property="bean.dispExpireSoon">&nbsp;แสดงรายเฉพาะรายการใกล้หมดอายุ</html:checkbox>
								  <%--  <html:checkbox property="bean.dispExpired">&nbsp;แสดงรายเฉพาะรายการที่หมดอายุ</html:checkbox> --%>
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
									&nbsp;
									<a href="javascript:popupBrandSaveZone('${pageContext.request.contextPath}')">
									  <input type="button" value="เช็คตาราง Shelf Life ของแต่ละสินค้า" class="newPosBtnLong">
									</a>			
								</td>
							</tr>
						</table>
					    </div>
					  
					   <!-- ************************Result ***************************************************-->
					  <%
					 // System.out.println("Results:"+request.getSession().getAttribute("RESULTS"));
					  if(request.getSession().getAttribute("stockForm_RESULTS") != null) {
					     out.println(request.getSession().getAttribute("stockForm_RESULTS"));
					     %>
					     <script>
							//load jquery
							$(function() {
								//Load fix column and Head
								$('#tblProduct').stickyTable({overflowy: true});
							});
						</script>
					 <% }%>
					<!-- ************************Result ***************************************************-->
					</html:form>
					<!-- BODY -->
					
					<script>
					   loadSalesrepCodeList();
					</script>
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
