<%@page import="util.UserUtils"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetConstants"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetBean"%>
<%@page import="util.Utils"%>
<%@page import="util.SIdUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
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
<jsp:useBean id="salesTargetForm" class="com.isecinc.pens.web.salestarget.SalesTargetForm" scope="session" />
<%
//for test
SIdUtils.getInstance().clearInstance();
		
int tabIndex = 0;
if(salesTargetForm.getBean().getItems() != null){
	tabIndex = salesTargetForm.getBean().getItems().size()*2;
}
User user = (User) request.getSession().getAttribute("user");
String role = user.getRoleSalesTarget();
String pageName = salesTargetForm.getPageName();
String pageNameTemp = "";

if(SalesTargetConstants.PAGE_MKT.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "MKT_SalesTarget";
}else if(SalesTargetConstants.PAGE_MTSALES.equalsIgnoreCase(pageName)){ 
	if(role.equalsIgnoreCase(User.DD_SALES)){
	   pageNameTemp = "DD_SalesTarget";
	}else if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.MT_SALES}) ){
	   pageNameTemp = "MT_SalesTarget";
	}
}else if(SalesTargetConstants.PAGE_MTMGR.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "MTMGR_SalesTarget";
}else if(SalesTargetConstants.PAGE_REPORT_SALES_TARGET.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "ReportSalesTarget";
}else if(SalesTargetConstants.PAGE_REPORT_SALES_TARGET_ALL.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "ReportSalesTargetAll";
}else if(SalesTargetConstants.PAGE_MKT_TT.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "MKT_SalesTarget_TT";
}else if(SalesTargetConstants.PAGE_TTSUPER.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "TTSUPER_SalesTarget";
}else if(SalesTargetConstants.PAGE_TTMGR.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "TTMGR_SalesTarget";
}
//System.out.println("pageNameTemp:"+pageNameTemp);

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
<style type="text/css">
 
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/salesTargetTT.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<script type="text/javascript">
//To disable f5
 $(document).bind("keydown", disableF5);
function disableF5(e) {
	if (e.which == 116) e.preventDefault(); 
} 

function loadMe(path){
	 <%if(salesTargetForm.getBean().isCanSet()){ %>
	    addRow(true);
	 <%}%>
}
function backForm(path){
	var form = document.salesTargetForm;
	form.action = path + "/jsp/salesTargetAction.do?do=prepareSearch&action=back";
	form.submit();
	return true;
}
function save(path){
	var form = document.salesTargetForm;
	
	if(checkTableCanSave()){
	   if(confirm("กรุณายืนยันการ บันทึก เป้าหมายให้กับ Sale")){
		   
		    /**Control Save Lock Screen **/
		    startControlSaveLockScreen();
		   
			form.action = path + "/jsp/salesTargetAction.do?do=save";
			form.submit();
			return true;
		}
	}
	return false;
}
function postToSales(path){
	var form = document.salesTargetForm;
	if(document.getElementById('check_save_before_post').value =="save_before_post"){
	    alert("มีการเปลี่ยนแปลงข้อมูล กรุณาบันทึกข้อมูลก่อน ทำการ Post To Sales")
		return false;
	}
	if(confirm("กรุณายืนยันการ POST เป้าหมายให้กับ Sale")){
		var r = checkCanPostToSalesFound();
		//alert(r);
		if(checkCanPostToSalesFound()){
			/**Control Save Lock Screen **/
			startControlSaveLockScreen();
			
			form.action = path + "/jsp/salesTargetAction.do?do=postToSales";
			form.submit();
			return true;
		}else{
			alert("ไม่มีสถานะใด ที่ POST ให้ Sale ได้ กรุณาตรวจสอบ");
			return false;
		}
	}
	return false;
}
function salesAcceptToSalesManager(path){
	var form = document.salesTargetForm;
	if(confirm("กรุณายืนยันการ Accept เป้ารายแบรนด์")){
		var r = checkCanAccept();
		//alert(r);
		if(r){
			/**Control Save Lock Screen **/
			startControlSaveLockScreen();
			
			form.action = path + "/jsp/salesTargetAction.do?do=salesAcceptToSalesManager";
			form.submit();
			return true;
		}else{
			alert("มีบางสถานะ ที่ไม่สามารถ Accept เป้าได้");
			return false;
		}
	}
	return false;
}
function exportToExcel(path){
	var form = document.salesTargetForm;
	form.action = path + "/jsp/salesTargetAction.do?do=exportToExcel";
	form.submit();
	return true;
}

function updateStatusManual(path,status){
	var form = document.salesTargetForm;
	if(confirm("ยืนยันแก้ไขสถานะ เป็น "+status)){
		form.action = path + "/jsp/salesTargetAction.do?do=updateStatusManual&status="+status;
		form.submit();
		return true;
	}
	return false;
}

/** disable back button alway **/
window.location.hash="no-back-button";
window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
window.onhashchange=function(){window.location.hash="no-back-button";}

</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe('${pageContext.request.contextPath}');MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="<%=pageNameTemp%>"/>
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
						<jsp:include page="../error.jsp"/>
						
							<!-- hidden field -->
						   <html:hidden property="pageName" />
						   <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
						   <input type="hidden" id="tabIndex" name="tabIndex" value="<%=tabIndex%>"/>
                           <html:hidden property="bean.customerId" styleId="customerId"/>
                           <html:hidden property="bean.salesrepId" styleId="salesrepId"/>
                           <html:hidden property="bean.period" styleId="period"/>
                           <html:hidden property="bean.priceListId" styleId="priceListId"/>
                           <html:hidden property="bean.id" styleId="id"/>
                           	<!-- hidden field -->
                           	
						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> เดือน</td>
									<td>					
										 <html:select property="bean.periodDesc" styleId="periodDesc" styleClass="disableText" disabled="true">
											<html:options collection="PERIOD_LIST" property="value" labelProperty="keyName"/>
									    </html:select>
									</td>
									<td> 
									     <html:text property="bean.startDate" styleId="startDate" size="10" readonly="true" styleClass="disableText"/>
									        -
										<html:text property="bean.endDate" styleId="endDate" size="10" readonly="true" styleClass="disableText"/>
									</td>
									<td>
									 &nbsp;&nbsp;&nbsp;
									แบรนด์  <html:text property="bean.brand" styleId="brand" size="10" readonly="true" styleClass="disableText"/>	
									    <html:text property="bean.brandName" styleId="brandName" readonly="true" styleClass="disableText" size="50"/>						
									</td>
								</tr>
								<tr>
                                    <td>  ประเภทขาย </td>
									<td colspan="3">
									    <html:select property="bean.custCatNo" styleId="custCatNo" styleClass="disableText" disabled="true">
											<html:options collection="CUSTOMER_CATEGORY_LIST" property="custCatNo" labelProperty="custCatDesc"/>
									    </html:select>
									    &nbsp;&nbsp;&nbsp;
									       ภาคตามสายดูแล
									    <html:select property="bean.salesZone" styleId="salesZone"  disabled="true">
											<html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
									    </html:select> 
									</td>
								</tr>					
						   </table>
					  </div>
					  
                    <c:if test="${salesTargetForm.results != null}">
                       <c:if test="${salesTargetForm.bean.canSet == true}">
	                        <div align="left">
								<input type="button" class="newPosBtn" value="เพิ่มรายการ" onclick="addRow('${pageContext.request.contextPath}');"/>
								<input type="button" class="newPosBtn" value="ลบรายการ" onclick="removeRow('${pageContext.request.contextPath}');"/>	
							</div>
				        </c:if> 
						<table id="tblProduct" align="center" border="1" width="100%" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
						            <th> 
						               <c:if test="${salesTargetForm.bean.canSet == true}">
						                <!-- <input type="checkbox" name="chkAll" onclick="checkAll(this)"/> -->
						               </c:if>
						            </th>
									<th>รหัสสินค้า</th>
									<th>ชื่อสินค้า</th>
									<th>ยอดขาย-คืน(เฉลี่ย 12 เดือน)</th>
									<th>ยอดขาย-คืน(เฉลี่ย 3 เดือน)</th>
									<th>ราคา List Price</th>
									<th>เป้าหมาย ขาย(หีบ)</th>
									<th>เป้าหมาย ขาย(บาท)</th>	
									<th>สถานะ</th>	
									<th>หมายเหตุ</th>	
									<c:if test="${salesTargetForm.bean.canReject == true}">
									<th>Reject</th>
									</c:if>
									<th>เหตุผลที่ทาง Sale ได้ Reject</th>
							   </tr>
					   <c:set var="tabIndex" value="${0}"/>
						<c:forEach var="results" items="${salesTargetForm.results}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								<c:set var="tabIndex" value="${tabIndex + 1}" />
								
								<tr class="<c:out value='${tabclass}'/>">
									<td class="td_text_center" width="3%">
									   <c:if test="${salesTargetForm.bean.canSet == true}">
										 <c:choose>
											<c:when test="${results.lineReadonly =='readonly'}">
											 <input type="checkbox" name="linechk" id="linechk"value="${results.lineId}" disabled/>
											</c:when>
											<c:otherwise>
												 <input type="checkbox" name="linechk" id="linechk"value="${results.lineId}"/>
											</c:otherwise>
										</c:choose>
									   </c:if>
									   <input type="hidden" name="lineId" id="lineId" value ="${results.lineId}"/>
									</td>
									<td class="td_text_center" width="7%">
									 <input type="text" name="itemCode" id="itemCode" value ="${results.itemCode}" size="5"
									        onkeypress="getProductKeypress(event,this,${results.rowId})"
										    onkeydown="getProductKeydown(event,this,${results.rowId})"
										    onchange="checkProductOnblur(event,this,${results.rowId})" 
										    readonly class="disableText"  tabindex="${tabIndex}"
									 /> 
									</td>
									<td class="td_text_center" width="19%">
									   <input type="hidden" name="itemId" id="itemId" value ="${results.itemId}"/>
									   <input type="text" name="itemName" id="itemName" value ="${results.itemName}" size="35" readonly class="disableText"/>
									</td>
									<td class="td_number" width="7%">
									  <input type="text" name="orderAmt12Month" id="orderAmt12Month" value ="${results.orderAmt12Month}" size="8" readonly class="disableNumber"/>
									</td>
									<td class="td_number" width="7%">
									  <input type="text" name="orderAmt3Month"  id="orderAmt3Month" value ="${results.orderAmt3Month}" size="8" readonly class="disableNumber"/>
									</td>
									<td class="td_number" width="7%">
									  <input type="text" name="price" id="price" value ="${results.price}" size="8" readonly class="disableNumber"/>
									</td>
									<td class="td_number" width="9%">
									  <c:set var="tabIndex" value="${tabIndex + 1}" />
									  <input type="text" name="targetQty" id="targetQty" value ="${results.targetQty}" size="10"
										    onblur="isNumPositive(this);calcTargetAmount(this,${results.rowId})"
										    onchange="setDataChange(this);"
										    class="${results.lineNumberStyle}"  tabindex="${tabIndex}"
										    ${results.lineReadonly}
										    onkeypress="nextRowKeypress(event,${results.rowId})"
										  />
									</td>
									<td class="td_number" width="9%">
									  <input type="text" name="targetAmount" id="targetAmount" value ="${results.targetAmount}" size="10" readonly class="disableNumber"/>
									</td>
									<td class="td_text_center" width="6%">
									  <input type="text" name="status" id="status" value ="${results.status}" size="6" readonly class="${results.lineStatusStyle}"/>
									</td>
									<c:if test="${results.canReject == true}">
										<td class="td_text_center" width="10%">
										  <input type="text" name="remark" id ="remark" value ="${results.remark}" 
										  size="20" ${results.lineReadonly} class="${results.lineStyle}" />
										</td>
									</c:if>
									<c:if test="${results.canReject == false}">
										<td class="td_text_center" width="15%">
										  <input type="text" name="remark" id ="remark" value ="${results.remark}" size="20"  class="${results.lineStyle}" />
										</td>
									</c:if>
									<c:if test="${results.canReject == true}">
										<td class="td_text_center" width="5%">
										   <a href="javascript:rejectRow('${pageContext.request.contextPath}',${results.id},${results.lineId},${results.rowId})">
										    <span id="span_reject_action_${results.rowId}"> Reject</span>
										   </a>
										</td>
									</c:if>
									<td class="td_text_center" width="11%">
										 <input type="text" name="rejectReason" id="rejectReason" 
									      value ="${results.rejectReason}" size="18"  
									      class="${results.lineRejectReasonStyle}"/> 
									</td>
								</tr>
						 </c:forEach>
					</table>
					<table id="tblProduct" align="center" border="1" width="100%" cellpadding="3" cellspacing="1" class="table_hilight">
					 <!-- Summary --> 
						  <tr>
						    <td class="" align="right" colspan="4" width="29%">
							  <B> Total</B>
							</td>
							<td class="td_number" width="7%">
							 <B>  <input type="text" name="totalOrderAmt12Month" value ="${salesTargetForm.bean.totalOrderAmt12Month}" size="7" readonly class="disableNumberBold"/></B>
							</td>
							<td class="td_number" width="7%">
							 <B>  <input type="text" name="totalOrderAmt3Month" value ="${salesTargetForm.bean.totalOrderAmt3Month}" size="7" readonly class="disableNumberBold"/></B>
							</td>
								<td class="td_text_center" align="right" width="7%"></td>
							<td class="td_number" width="9%">
							 <B>  <input type="text" name="totalTargetQty" value ="${salesTargetForm.bean.totalTargetQty}" size="7" readonly class="disableNumberBold"/></B>
							</td>
							<td class="td_number" width="9%">
							 <B>  <input type="text" name="totalTargetAmount" value ="${salesTargetForm.bean.totalTargetAmount}" size="9" readonly class="disableNumberBold"/></B>
							</td>
							 <c:if test="${salesTargetForm.bean.canReject == false}">
						       <td class="" width="32%" colspan="3">&nbsp;</td>
                             </c:if>
                             <c:if test="${salesTargetForm.bean.canReject == true}">
						       <td class="" width="32%" colspan="4">&nbsp;</td>
                             </c:if>
						 </tr>
					</table>
					<div align="center">
					   <table  border="0" cellpadding="3" cellspacing="0" width="100%">
								<tr>
								    <td align="left" width="10%">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									</td>
									<td align="left" width="10%">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									</td>
									<td align="left" width="10%">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									</td>
									<td align="left" width="5%">
									   <c:if test="${salesTargetForm.bean.canSet == true}">
											<a href="javascript:save('${pageContext.request.contextPath}')">
											  <input type="button" value="   บันทึก     " class="newPosBtnLong"> 
											</a>
										</c:if>
									</td>
									<td align="left" width="5%">
									 <c:if test="${salesTargetForm.bean.canExport == true}">
											<a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
											  <input type="button" value="Export To Excel" class="newPosBtnLong">
											</a>
									  </c:if>
									</td>
									<td align="left" width="5%">
									 <c:if test="${salesTargetForm.bean.canAccept == true}">
											<a href="javascript:salesAcceptToSalesManager('${pageContext.request.contextPath}')">
											  <input type="button" value=" Accept เป้าหมายแบรนด์  " class="newPosBtnLong">
											</a>
									  </c:if>
									</td>
									<td align="left" width="5%">
										<a href="javascript:backForm('${pageContext.request.contextPath}')">
										  <input type="button" value=" ปิดหน้าจอ  " class="newPosBtnLong">
										</a>
										
									</td>
									<td align="left" width="10%">
									    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									 </td>
									 <td align="left" width="10%">
									    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									 </td>
									<td align="right" width="10%">
										 <c:if test="${salesTargetForm.bean.canPost == true}">
											<a href="javascript:postToSales('${pageContext.request.contextPath}')">
											  <input type="button" value=" Post To Sales " class="newPosBtnLong"> 
											</a>
										</c:if>						
									</td>
								</tr>
						</table>
						<div align="center">
						   <%if("GOD".equalsIgnoreCase(user.getUserName())){ %>
								<a href="javascript:updateStatusManual('${pageContext.request.contextPath}','Open')">
									 <input type="button" value="อัพสถานะไปเป็น Open(GOD)" class="newPosBtn">
								</a>
								<a href="javascript:updateStatusManual('${pageContext.request.contextPath}','Post')">
									 <input type="button" value="อัพสถานะไปเป็น Post(GOD)" class="newPosBtn">
								</a>
								<a href="javascript:updateStatusManual('${pageContext.request.contextPath}','Accept')">
									 <input type="button" value="อัพสถานะไปเป็น Accept(GOD)" class="newPosBtn">
								</a>
								<a href="javascript:updateStatusManual('${pageContext.request.contextPath}','Finish')">
									 <input type="button" value="อัพสถานะไปเป็น Finish(GOD)" class="newPosBtn">
								</a>
								
							<%} %>
						</div>
					</div>
				</c:if>
					<!-- ************************Result ***************************************************-->
					<!-- hidden field -->
					<!-- check_save_before_post: --> <input type="hidden" name="check_save_before_post" id="check_save_before_post"/>
					</html:form>
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
   			<jsp:include page="../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>
<!-- Control Save Lock Screen -->
<jsp:include page="../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->