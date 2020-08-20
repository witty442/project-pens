<%@page import="com.isecinc.pens.web.buds.BudsAllForm"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.ConfPickingBean"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="budsAllForm" class="com.isecinc.pens.web.buds.BudsAllForm" scope="session" />
<%
ConfPickingBean bean = ((BudsAllForm)session.getAttribute("budsAllForm")).getBean().getConfPickingBean();
System.out.println("ConfPickingBean:"+bean);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<%
	User user = (User)session.getAttribute("user");
	String screenWidth = ""+(Utils.convertStrToInt(Utils.isNull(session.getAttribute("screenWidth")))-50);
	String screenHeight = Utils.isNull(session.getAttribute("screenHeight")); 
	String hideAll = "true"; 
	String pageName = !Utils.isNull(request.getParameter("pageName")).equals("")?Utils.isNull(request.getParameter("pageName")):budsAllForm.getPageName();
	String subPageName = !Utils.isNull(request.getParameter("subPageName")).equals("")?Utils.isNull(request.getParameter("subPageName")):budsAllForm.getSubPageName();
%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<!-- For fix Head and Column Table -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-stickytable-3.0.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/jquery-stickytable-3.0.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<!-- REPORT JAVASCRIPT -->

<script type="text/javascript">
 $(document).ready(function() {
  $(window).keydown(function(event){
    if(event.keyCode == 13) {
      event.preventDefault();
      return false;
    }
  });
}); 
function loadMe(){
	var form = document.budsAllForm;
}
function search(){
	var form = document.budsAllForm;
	var path =document.getElementById("path").value 
	var provinceCri =document.getElementById("provinceCri"); 
	if(provinceCri.value ==''){
		alert("กรุณาระบุ ระบุสายขนส่ง/จังหวัดที่ต้องการจัดสินค้า");
		provinceCri.focus();
		return false;
	}
	form.action = path + "/jsp/budsAllAction.do?do=search";
	form.submit();
	return true;
}
function searchPickingNo(){
	var form = document.budsAllForm;
	var path =document.getElementById("path").value ;
	var pickingNo =document.getElementById("pickingNo"); 
	if(pickingNo.value ==''){
		alert("กรุณาระบุ เลขที่ Picking No");
		pickingNo.focus();
		return false;
	}
	//alert("url:"+form.action);
	form.action = path + "/jsp/budsAllAction.do?do=search";
	form.submit();
	return true;
}

function searchPickingNoKeypress(e,obj){
	//alert("enter:"+obj.value);
	if(e != null && e.keyCode == 13){
		//alert("enter:"+obj.value);
		if(obj.value ==''){
			//searchPickingNo();
		}
	}
}
function exportExcel(reportName){
	var form = document.budsAllForm;
	var path =document.getElementById("path").value 
	form.action = path + "/jsp/budsAllAction.do?do=export&reportType=excel&reportName="+reportName;
	form.submit();
	return true;
}

function printReport(reportName){
	var form = document.budsAllForm;
	var path =document.getElementById("path").value 
	form.action = path + "/jsp/budsAllAction.do?do=printReport&reportType=pdf&reportName="+reportName;
	form.submit();
}
function clearForm(path){
	var form = document.budsAllForm;
	form.action = path + "/jsp/budsAllAction.do?do=viewDetail&mode=new";
	form.submit();
	return true;
}
function backToMainPage(path){
	var form = document.budsAllForm;
	//set Move page to Search Main Page
	document.getElementById("pageName").value = "ConfPickingSearch";
	//document.getElementById("subPageName").value = "ConfPicking";
	form.action = path + "/jsp/budsAllAction.do?do=searchHead&action=back";
	form.submit();
	return true;
}
function confirmPicking(path){
	var form = document.budsAllForm;
	if(confirm("ยืนยัน Picking")){
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
		
		form.action = path + "/jsp/budsAllAction.do?do=confirmPicking";
		form.submit();
		return true;
	}
}
function rejectOrder(path){
	var form = document.budsAllForm;
	 if(!validChkRejectOrder()){
		alert("กรุณาเลือก Order ที่จะทำการ Reject");
		return false;
	}
	 
	 if(confirm("ยืนยัน Reject Order กลับไปให้เซลส์ทำการแก้ไข")){
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
		
		form.action = path + "/jsp/budsAllAction.do?do=rejectOrder";
		form.submit();
		return true;
	 }
	return false;
}
function validChkRejectOrder(){
	var foundCheck = false;
	var chkOrder = document.getElementsByName("chkOrder");
	for(var i=0;i<chkOrder.length;i++){
		if(chkOrder[i].checked){
			foundCheck =true;
			break;
		}
	}
	return foundCheck;
}
function genInvoice(path){
	var form = document.budsAllForm;
	if(confirm("ยืนยัน Gen Invoice")){
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
		form.action = path + "/jsp/budsAllAction.do?do=genInvoice";
		form.submit();
		return true;
	}
	return false;
}
function openPopupPage(page){
	var form = document.budsAllForm;
	var path = document.getElementById("path").value;
    var param = "&page="+page;
    if("PICKING_NO"==page){
    }else if("TRANSPORT"==page){
    	param += "&pickingNo="+form.pickingNo.value;
    }
	var url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	
	PopupCenterFullHeight(url,"",700);
}

function setMainValue(page,data,data2,data3){
	if("PICKING_NO"==page){
	   document.getElementById("pickingNo").value = data;
	}else if("TRANSPORT"==page){
	   document.getElementById("regionCri").value = data;
	   document.getElementById("provinceCri").value = data2;
	   document.getElementById("amphurCri").value = data3;
	}
} 
function addOrderPickingManual(){
	var path = document.getElementById("path").value;
	var param = "&pickingNo="+document.getElementById("pickingNo").value;
	    param += "&regionCri="+document.getElementById("regionCri").value;
	    param += "&provinceCri="+document.getElementById("provinceCri").value;
	    param += "&amphurCri="+document.getElementById("amphurCri").value;
	var url = path + "/jsp/buds/popup/addOrderPickingPopup.jsp?"+encodeURI(param);
	//PopupCenterFullHeight(url,"Add Order Manual",<%=(Utils.convertToInt(screenWidth)-50)%>);
	PopupCenterFull(url,"Add Order Manual");
}
function setMainAddOrderPickingManual(orderNoAll){
	var form = document.budsAllForm;
	var path = document.getElementById("path").value;
	/**Control Save Lock Screen **/
	startControlSaveLockScreen();
	
	form.action = path + "/jsp/budsAllAction.do?do=addOrderPickingManual&orderNo="+orderNoAll;
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
				<jsp:param name="function" value="<%=subPageName %>"/>
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
						<html:form action="/jsp/budsAllAction">
						<jsp:include page="../../error.jsp"/>
						
						<div id="div_message" style="font-size:15px;color:green" align="center"></div> 
						<div id="div_error_message" style="font-size:15px;color:red" align="center"></div> 
						
					<div id="div_m" align="center">	
				    	<!-- ***** Criteria ******* -->
				    	<table align="center" border="0" cellpadding="3" cellspacing="0" >
				    	
				    	   <%if("ConfPicking".equals(subPageName)){ %>
						        <tr>
					                <td valign="top"> ระบุสายขนส่ง/จังหวัดที่ต้องการจัดสินค้า <font color="red">*</font></td>
									<td valign="top">
									    <html:textarea property="bean.confPickingBean.regionCri" styleId="regionCri" rows="3" cols="20"/>
									</td>
									 <td valign="top">  จังหวัด</td>
									  <td valign="top">
									    <html:textarea property="bean.confPickingBean.provinceCri" styleId="provinceCri" rows="3" cols="30">
									    </html:textarea>
									  </td>
									    <td valign="top">อำเภอ/เขต </td>
									   <td valign="top">
									    <html:textarea property="bean.confPickingBean.amphurCri" styleId="amphurCri" rows="3" cols="30"/>
									    <input type="button" name="btTransport" value="...." 
									    onclick="openPopupPage('TRANSPORT')" class="newPosBtnLong"/>
								    </td>
								 </tr>
								  <tr>
								     <td colspan="6">
										<b><font size="2">เลขที่ Picking No:</font></b>
										<html:text property="bean.confPickingBean.pickingNo" styleId="pickingNo" size="10" readonly="true" styleClass="disableTextBigBold"/>
								       &nbsp;&nbsp;&nbsp;&nbsp;
								        Transaction Date &nbsp;
								        <html:text property="bean.confPickingBean.transactionDate" styleId="transactionDate" size="10" readonly="true" styleClass="disableText"/> 
								           &nbsp; สถานะ &nbsp;
								        <html:text property="bean.confPickingBean.status" size="10" readonly="true" styleClass="disableText"/> 
								        &nbsp;&nbsp;&nbsp;&nbsp;
								        <%if(Utils.isNull(budsAllForm.getBean().getConfPickingBean().getPickingNo()).equals("")){ %>
										   	<a href="javascript:search()">
											  <input type="button" value="   ค้นหา   " class="newPosBtnLong"> 
											</a>
										<%} %>
									 </td>
								 </tr>
							 <%}else if("BudsConfPicking".equals(subPageName)){  %>
							    <tr>
							      <td><b><font size="2">เลขที่ Picking No:</font></b></td>
								  <td>	
									<html:text property="bean.confPickingBean.pickingNo" styleClass="disableTextBigBold" 
									styleId="pickingNo" size="10" readonly="true"></html:text>
								 
								    &nbsp;&nbsp;&nbsp;&nbsp;
								    Transaction Date &nbsp;
								    <html:text property="bean.confPickingBean.transactionDate" styleId="transactionDate" size="10" readonly="true" styleClass="disableText"/> 
								    &nbsp; สถานะ &nbsp;
								    <html:text property="bean.confPickingBean.status" size="10" readonly="true" styleClass="disableText"/> 
							      </td>
							    </tr>
							    
							 <%} %>
						</table>
					<br>
					
			  </div>
			
			     <!-- ****** RESULT *************************************************** -->
			     <%
			       if(request.getAttribute("budsAllForm_RESULTS") != null ){
			    	%>
			    	 <div style="height:300px;width:<%=screenWidth%>px;">
			    	<%
			    	    out.println(((StringBuffer)request.getAttribute("budsAllForm_RESULTS")).toString());
			    	%>
			    	 </div>
			    	 
			    	 <script>
						//load jquery
						 $(function() { 
							//Load fix column and Head
							$('#tblProduct').stickyTable({overflowy: true});
						}); 
					 </script>
					 <%
			    	    out.println(((StringBuffer)request.getAttribute("budsAllForm_total_RESULTS")).toString());
			    	 %>
			    <%} %>
			     <!-- ***************************************************************** -->
			     <!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center" width="100%">
							<%if("ConfPicking".equals(subPageName)){ %>
								<%if(UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN,User.BUD_ADMIN})
										&& bean.isCanConfirm()){ %>
								    &nbsp;&nbsp;&nbsp;&nbsp;
									<a href="javascript:confirmPicking('${pageContext.request.contextPath}')">
									  <input type="button" value="ยืนยันชุดข้อมูลการจัด Picking" class="newPosBtnLong">
									</a>
									
								<%} %>
								
								<%if(UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN,User.BUD_ADMIN})
										&& bean.isCanAddOrderManual()){ %> 
									 &nbsp;&nbsp;
									<a href="javascript:addOrderPickingManual('${pageContext.request.contextPath}')">
									  <input type="button" value="เพิ่ม รายการ Order" class="newPosBtnLong">
									</a>	
								<%} %>
										
								&nbsp;
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value=" Clear หน้าจอ " class="newPosBtnLong">
								</a>
								&nbsp;&nbsp;&nbsp;&nbsp;
								<a href="javascript:backToMainPage('${pageContext.request.contextPath}')">
								  <input type="button" value=" ปิดหน้าจอ  " class="newPosBtnLong">
								</a>
								
								 <%if(UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN,User.BUD_ADMIN}) 
										&& bean.isCanReject()){ %>
								    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<a href="javascript:rejectOrder('${pageContext.request.contextPath}')">
									  <input type="button" value="Reject Order" class="newPosBtnLong">
									</a>
								 <%} %> 
								  <%if(UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN,User.BUD_ADMIN})
										&& bean.isCanPrintPicking()){ %>
									 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									 <a href="javascript:printReport('PickingReport')">
									   <input type="button" value="พิมพ์ใบ Picking List" class="newPosBtnLong">
									 </a> &nbsp;
									  <a href="javascript:exportExcel('SalesReport')">
									   <input type="button" value="Export ไฟล์สั่งขาย" class="newPosBtnLong">
									 </a>&nbsp;
									  <a href="javascript:exportExcel('SalesDetailReport')">
									   <input type="button" value="Export รายละเอียดใบขาย" class="newPosBtnLong">
									 </a> 
								  <%} %> 
								   <%if(UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN,User.BUD_ADMIN})
										&& bean.isCanPrintLoading()){ %>
										&nbsp;
									   <a href="javascript:exportExcel('PickingReport')">
											  <input type="button" value="Export Picking (ใบโหลด)" class="newPosBtnLong">
									   </a>
								   <%} %> 
								<%}else if("BudsConfPicking".equals(subPageName)) {%>
								
									<%if(UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN,User.BUD_ADMIN})
											&& bean.isCanFinish() ){ %>
										&nbsp;&nbsp;&nbsp;&nbsp;
										<a href="javascript:genInvoice('${pageContext.request.contextPath}')">
										  <input type="button" value="ยืนยันให้ทำการ Gen Invoice" class="newPosBtnLong">
										</a>
									<%} %> 
									     &nbsp;&nbsp;&nbsp;&nbsp;
											<a href="javascript:backToMainPage('${pageContext.request.contextPath}')">
											  <input type="button" value=" ปิดหน้าจอ  " class="newPosBtnLong">
											</a>
									    <%if(UserUtils.userInRole("ROLE_ACCESS",user,new String[]{User.ADMIN,User.BUD_ADMIN})
											&& bean.isCanPrintLoading()){ %>
										    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										 	<a href="javascript:printReport('PickingReport')">
											  <input type="button" value="พิมพ์ใบ Picking List" class="newPosBtnLong">
											</a>
											<a href="javascript:exportExcel('PickingReport')">
											  <input type="button" value="Export Picking (ใบโหลด)" class="newPosBtnLong">
											</a>
									   <%} %> 
								 <%} %> 
							</td>
							 <td align="right" width="40%" nowrap></td>
						</tr>
					</table>
					<!-- hidden field -->
					<html:hidden property="mode"/>
					<input type="hidden" name="pageName" id="pageName" value="<%=request.getParameter("pageName") %>"/>
					<input type="hidden" name="subPageName" id="subPageName" value="<%=request.getParameter("subPageName") %>"/>
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

 <!-- Control Save Lock Screen -->
<jsp:include page="../../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->