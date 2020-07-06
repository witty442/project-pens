<%@page import="com.isecinc.pens.web.stock.StockBean"%>
<%@page import="com.pens.util.PageVisit"%>
<%@page import="com.pens.util.SessionUtils"%>
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
<jsp:useBean id="reportAllForm" class="com.isecinc.pens.web.reportall.ReportAllForm" scope="session" />
<%
User user = (User) request.getSession().getAttribute("user");
StockBean bean = reportAllForm.getBean().getStockBean();
String role = user.getRoleSalesTarget();
String pageName = Utils.isNull(request.getParameter("pageName"));
String popup = Utils.isNull(request.getParameter("popup"));
if(pageName.equals("")){
	pageName = reportAllForm.getPageName();
}
String pageNameTemp = pageName;

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
  .tr_orange{
    background-color: orange;
    color:black;
  }
</style>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script>
/** disable back button alway **/
/* window.location.hash="no-back-button";
window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
window.onhashchange=function(){window.location.hash="no-back-button";} */
</script>
<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
	new Epoch('epoch_popup', 'th', document.getElementById('endDate'));
	
	
    //document.getElementsByName('bean.stockBean.salesChannelNo')[0].value = "<%=bean.getSalesChannelNo()%>";
    //document.getElementsByName('bean.stockBean.custCatNo')[0].value = "<%=bean.getCustCatNo()%>";
    document.getElementsByName('bean.stockBean.salesrepCode')[0].value = '<%=bean.getSalesrepCode()%>'; 
}
function clearForm(path){
	var form = document.reportAllForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/reportAllAction.do?do=prepare&action=new&pageName="+pageName;
	form.submit();
	return true;
}
function searchReport(path){
	var form = document.reportAllForm;
	//alert(form.startDate.value);
	if(form.startDate.value =="" || form.endDate.value ==""){
		alert("กรุณาระบุ วันที่ From-To");
		return false;
	}

	form.action = path + "/jsp/reportAllAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(currPage){
	var form = document.reportAllForm;
	var path = document.getElementById("path").value;
	form.action = path + "/jsp/reportAllAction.do?do=search&currPage="+currPage;
    form.submit();
    return true;
}
function exportReport(){
	var form = document.reportAllForm;
	if(form.startDate.value =="" || form.endDate.value ==""){
		alert("กรุณาระบุ วันที่ From-To");
		return false;
	}
	 var path = document.getElementById("path").value;
	form.action = path + "/jsp/reportAllAction.do?do=export&action=newsearch";
	form.submit();
	return true;
}
function view(requestNo){
	var form = document.reportAllForm;
	var path = document.getElementById("path").value;
	form.action = path + "/jsp/reportAllAction.do?do=view&requestNo="+requestNo;
	form.submit();
	return true;
}
function loadSalesrepCodeList(){
	 var cboDistrict = document.getElementsByName('bean.stockBean.salesrepCode')[0];
	var param  ="salesChannelNo=" + document.getElementsByName('bean.stockBean.salesChannelNo')[0].value;
	    param +="&custCatNo="+ document.getElementsByName('bean.stockBean.custCatNo')[0].value;
	    param +="&salesZone="+ document.getElementsByName('bean.stockBean.salesZone')[0].value;
	    param +="&roleNameChk=ROLE_CR_STOCK&pageName=StockReturn";
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/genSalesrepCodeAllListBoxAjax.jsp",
			data : param,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboDistrict.innerHTML=returnString;
			}
		}).responseText;
	}); 
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
						<html:form action="/jsp/reportAllAction">
						<jsp:include page="../../error.jsp"/>

						<div align="center">
						<table align="center" border="0" cellpadding="3" cellspacing="0" >
					       <tr>
				                <td> วันที่<font color="red">*</font></td>
								<td>
								    <html:text property="bean.stockBean.startDate" styleId="startDate" size="20" readonly="true" styleClass=""/>
								        -
									<html:text property="bean.stockBean.endDate" styleId="endDate" size="20" readonly="true" styleClass=""/>
								</td>
								<td>
								 <fieldset>
								   <html:radio property="bean.stockBean.compareDate" value="compareStockDate">&nbsp;&nbsp;เทียบกับวันที่นับสต๊อก</html:radio>
								   <html:radio property="bean.stockBean.compareDate" value="compareCurrentDate">&nbsp;&nbsp;เทียบกับวันที่ปัจจุบัน</html:radio>
								 </fieldset>
								</td>
								<td>
								 <fieldset>
								   <html:radio property="bean.stockBean.brandGroupType" value="NISSIN">&nbsp;&nbsp;กลุ่ม Nissin</html:radio>
								   <html:radio property="bean.stockBean.brandGroupType" value="NON_NISSIN">&nbsp;&nbsp;ไม่ใช่กลุ่ม Nissin</html:radio>
								   <html:radio property="bean.stockBean.brandGroupType" value="ALL">&nbsp;&nbsp;ทั้งหมด</html:radio>
								  </fieldset>
								</td>
							</tr>
							 <tr>
				                <td> ประเภทขาย </td>
								<td colspan="3">
								 <html:select property="bean.stockBean.custCatNo" styleId="custCatNo">
										<html:options collection="CUST_CAT_LIST" property="custCatNo" labelProperty="custCatDesc"/>
								    </html:select>
								      &nbsp;&nbsp;&nbsp;&nbsp;
								ภาคการขาย 
								    <html:select property="bean.stockBean.salesChannelNo" styleId="salesChannelNo" onchange="loadSalesrepCodeList()">
										<html:options collection="SALES_CHANNEL_LIST" property="salesChannelNo" labelProperty="salesChannelDesc"/>
								    </html:select>
								      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
								       ภาคตามสายดูแล 
								      <html:select property="bean.stockBean.salesZone" styleId="salesZone" onchange="loadSalesrepCodeList()">
										<html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
								    </html:select>
								    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
								       พนักงานขาย 
								      <html:select property="bean.stockBean.salesrepCode" styleId="salesrepCode" >
										<html:options collection="SALESREP_LIST" property="salesrepCode" labelProperty="salesrepCode"/>
								    </html:select>
									  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
								    <html:checkbox property="bean.stockBean.dispExpireSoon">&nbsp;แสดงรายเฉพาะรายการใกล้หมดอายุ</html:checkbox>
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
					  //System.out.println("reportAllForm_RESULTS:"+request.getAttribute("reportAllForm_RESULTS"));
					  if(request.getAttribute("reportAllForm_RESULTS") != null) {
					     out.println(request.getAttribute("reportAllForm_RESULTS"));
					  }
					  %>
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
