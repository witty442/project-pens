<%@page import="com.isecinc.pens.web.salestarget.SalesTargetConstants"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="salesTargetForm" class="com.isecinc.pens.web.salestarget.SalesTargetForm" scope="session" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/jquery.stickytable.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<%
	User user = (User)session.getAttribute("user");
	String screenWidth = Utils.isNull(session.getAttribute("screenWidth"));
	String screenHeight = Utils.isNull(session.getAttribute("screenHeight"));
	String pageName = Utils.isNull(request.getParameter("pageName"));
	String hideAll = "true";
	
	String subPageName = Utils.isNull(request.getParameter("subPageName"));
	if(subPageName.equals("")){
		subPageName = salesTargetForm.getSubPageName();
	}
%>
<style>
.sticky-table {
    max-width: <%=(String)session.getAttribute("screenWidth")%>px;
    max-height: 70vh;
    overflow: auto;
    border-top: 1px solid #ddd;
    border-bottom: 1px solid #ddd;
    padding: 0 !important;
    transition: width 2s; 
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.10.0.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.stickytable.js"></script> 
<script type="text/javascript">

//'.tbl-content' consumed little space for vertical scrollbar, scrollbar width depend on browser/os/platfrom. Here calculate the scollbar width .
$(window).on("load resize ", function() {
  var scrollWidth = $('.tbl-content').width() - $('.tbl-content table').width();
  $('.tbl-header').css({'padding-right':scrollWidth});
}).resize();

window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	var form = document.salesTargetForm;
	// new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
	
	//setDateMonth
	setPeriodDate(form.periodDesc);
}

function clearForm(path){
	var form = document.salesTargetForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/salesTargetAction.do?do=prepareSearch&action=new&pageName="+pageName;
	form.submit();
	return true;
}
function search(path){
	var form = document.salesTargetForm;
	 if( $('#periodDesc').val()==""){
		alert("กรุณากรอก เดือน");
		$('#periodDesc').focus();
		return false;
	 } 
	// alert(document.getElementById('dispCurrentStockPD').checked);
	 if(document.getElementById('dispCurrentStockPD').checked){
		 if( $('#salesZone').val()==""){
			alert("กรุณากรอก ภาคตามสายดูแล");
			$('#salesZone').focus();
			return false;
		 } 
	 }
	form.action = path + "/jsp/salesTargetAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.salesTargetForm;
	 if( $('#periodDesc').val()==""){
		alert("กรุณากรอก เดือน");
		return false;
	 } 
	form.action = path + "/jsp/salesTargetAction.do?do=exportToExcel";
	form.submit();
	return true;
}
function setPeriodDate(periodDesc){
	var form = document.salesTargetForm;
	//alert(periodDesc);
	form.period.value = periodDesc.value.split("|")[0];
	form.startDate.value = periodDesc.value.split("|")[1];
	form.endDate.value = periodDesc.value.split("|")[2]; 
}

function openPopup(path,pageName){
	var form = document.salesTargetForm;
	var param = "&pageName="+pageName+"&hideAll=true";
	if("PD" == pageName){
		param +="&selectone=false";
	}else if("Brand" == pageName){
		param +="&brand="+form.brand.value+"&selectone=false";
	}else if("Item" == pageName){
		param +="&brand="+form.brand.value+"&selectone=false";
	}
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.salesTargetForm;
	if("PD" == pageName){
		form.pdCode.value = code;
	}else if("Brand" == pageName){
		form.brand.value = code;
	}else if("Item" == pageName){
		form.itemCode.value = code;
	}
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
					<html:form action="/jsp/salesTargetAction">
					<jsp:include page="../../error.jsp"/>
						
					<table align="center" border="0" cellpadding="3" cellspacing="0" >
			            <tr>
		                <td> เดือน <font color="red">*</font></td>
						<td>					
							 <html:select property="bean.periodDesc" styleId="periodDesc" onchange="setPeriodDate(this)">
								<html:options collection="PERIOD_LIST" property="value" labelProperty="keyName"/>
						    </html:select>
						     <html:hidden property="bean.period" styleId="period"/>
						</td>
						<td> 
						     <html:text property="bean.startDate" styleId="startDate" size="20" readonly="true" styleClass="disableText"/>
						        -
							<html:text property="bean.endDate" styleId="endDate" size="20" readonly="true" styleClass="disableText"/>
						
						
						</td>
					</tr>
					<tr>
		                <td>  ภาคตามสายดูแล <font color="red"></font></td>
						<td colspan="2">
						     <html:select property="bean.salesZone" styleId="salesZone">
								<html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
						    </html:select> 
						    &nbsp;&nbsp;
						    PD:
						      <html:text property="bean.pdCode" styleId="pdCode" size="20" styleClass="\" autoComplete=\"off"/>
				              <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','PD')"/>   
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<html:checkbox property="bean.dispRoundUp" styleId="dispRoundUp"> &nbsp;แสดงตาม % การแบ่งเป้าจริง(ปัดเศษขึ้นเป็นจำนวนเต็ม)</html:checkbox>				
						</td>
					</tr>
					<tr>
		                <td> แบรนด์ <font color="red"></font></td>
						<td colspan="2">
						    <html:text property="bean.brand" styleId="brand" size="20" styleClass="\" autoComplete=\"off"/>
						    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','Brand')"/>   
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						       รหัสสินค้า
						   <html:text property="bean.itemCode" styleId="itemCode" size="20" styleClass="\" autoComplete=\"off"/>
						     <input type="button" name="x2" value="..." onclick="openPopup('${pageContext.request.contextPath}','Item')"/>   
						    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						    
						   <html:checkbox property="bean.dispCurrentStockPD" styleId="dispCurrentStockPD"> &nbsp;แสดงยอดสต๊อกปัจจุบันด้วย</html:checkbox>
						</td>
					</tr>		
			   </table>
			   <div align="center">
				   <table  border="0" cellpadding="3" cellspacing="0" >
						<tr>
							<td align="left">
								<a href="javascript:search('${pageContext.request.contextPath}')">
								  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
								</a>
								<a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export" class="newPosBtnLong"> 
								</a>
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value="   Clear   " class="newPosBtnLong">
								</a>		
							</td>
						</tr>
					</table>
				</div>
			    <!-- *********************** RESULT ************************************************** -->
			    <%
				  //System.out.println("Results:"+request.getSession().getAttribute("salesTargetForm_RESULTS"));
				  if(request.getSession().getAttribute("salesTargetForm_RESULTS") != null) {
					 out.println("<div class ='sticky-table sticky-ltr-cells' >");
					 out.println(request.getSession().getAttribute("salesTargetForm_RESULTS"));
					 out.println("</div>");
				  }
				  %>
			     <!-- ******************************************************************************* -->
			     
				    <!-- hidden field -->
		            <html:hidden property="pageName" value="<%=pageName %>"/>
		            <html:hidden property="subPageName" value="<%=subPageName %>"/>
					<input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
					
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