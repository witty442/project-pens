<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.Address"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.model.MAddress"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="stockReturnForm" class="com.isecinc.pens.web.stockreturn.StockReturnForm" scope="request" />
<%
int tabIndex = 0;
if(stockReturnForm.getResults() != null){
	tabIndex = stockReturnForm.getResults().size()*2;
}

User user = ((User)session.getAttribute("user"));
String role = user.getType();
String userName = user.getUserName();
String backPage = Utils.isNull(request.getParameter("backPage"));
if(backPage.equals("")){
	backPage = Utils.isNull(request.getAttribute("backPage"));
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Pragma" content="no-cache" /> 
<meta http-equiv="Expires" content="0" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
</style>

<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css?v=<%=SessionGen.getInstance().getIdSession()%>"" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page_stock/epoch.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/stockReturn.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>

<script type="text/javascript">

//To disable f5
$(document).bind("keydown", disableF5);
function disableF5(e) {
	if (e.which == 116) e.preventDefault(); 
} 
function loadMe(){
	//alert("loadMe");
	  addRow(true);	
}
function backsearch(path) {
	document.stockReturnForm.action = path + "/jsp/stockReturnAction.do?do=prepare"+"&action=back";//stockSearch
	document.stockReturnForm.submit();
}
function openPopupInvoice(index){
	openPopupInvoiceModel('${pageContext.request.contextPath}',index,<%=user.getId()%>);
}
function  gotoReport(path, reportType,requestNumber){
	   var param  = "do=printReport&reportType=1";
	   param += "&requestNumber="+requestNumber;
	   param += "&fileType='PDF'";
	 //  alert("printReport2");
	   
	   document.stockReturnForm.action =  path+"/jsp/stockReturnAction.do?"+param;
	   document.stockReturnForm.submit(); 
}

function gotoReportXXXX(path, reportType,requestNumber){
	 var param ="report_name=stock_return_report";
	     param +="&reportType="+reportType;
	     param +="&printReport1=printReport1";
	     param +="&requestNumber="+requestNumber;
	     param +="&fileType='PDF'";
	     
	     var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
	     var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;

	     var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
	     var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

	     param +="&width="+(width-100);
	     param +="&height="+(height-100);
	     
	    //window.open(path + "/jsp/pop/printPopup.jsp?"+param, "Print2", "width=100,height=100,location=No,resizable=No");
	     PopupCenter(path + "/jsp/stockReturn/printStockReturnPopup.jsp?"+param,"Print",750,300);
	}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
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
				<jsp:param name="function" value="StockReturn"/>
				<jsp:param name="code" value=""/>
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
						<html:form action="/jsp/stockReturnAction">
						<jsp:include page="../error.jsp"/>
						
						<!-- Hidden -->
		                 <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
						 <input type="hidden" id="tabIndex" name="tabIndex" value="<%=tabIndex%>"/>
						   
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td colspan="4" align="center">
							        <font color="black" size="5"> <b> บันทึกข้อมูล ใบอนุมัติให้คืนคลัง PENS รอทำลาย </b> </font>
							    </td>
							</tr>
							<tr>
								<td colspan="4" align="center">
								   <table align="center" border="0" cellpadding="3" cellspacing="0" width="90%">
								   <tr>
										<td align="right" width="10%"nowrap> วันที่ทำรายการ    </td>
										<td align="left" colspan="3" width="80%" nowrap>
										  <html:text property="bean.requestDate" size="8" readonly="true" styleClass="disableText"/>
										   &nbsp;&nbsp;&nbsp;&nbsp; 
										     พนักงานขาย  :<input type="text" value="<%=user.getCode()%>" size="5" readonly class="disableText"/>
										     <input type="text" value="<%=user.getName()%>" size="20" readonly class="disableText"/>
										   &nbsp;&nbsp;&nbsp;&nbsp;  
										     เลขที่เอกสาร   <html:text property="bean.requestNumber" styleId="requestNumber" size="20" readonly="true" styleClass="disableText"/>
                                          &nbsp;&nbsp;                                                       
                                                                                                      สถานะ <html:text property="bean.statusLabel" size="5" readonly="true" styleClass="disableText"/>
                                          
                                           &nbsp;&nbsp;                                                         
                                                                                                     วันที่ย้อนหลังที่ใช้หาข้อมูล:<html:text property="bean.backDate" styleId="backDate" size="8" readonly="true" styleClass="disableText"/>
                                         </td>
									</tr>
									<tr>
										<td align="right" nowrap>
											รหัสลูกค้า<font color="red">*</font>
										</td>
										<td align="left" colspan="3" nowrap>
										    <html:text property="bean.customerCode" size="10"  styleId="customerCode" 
										      onkeypress="getCustNameKeypress(event,this)" styleClass="\" autoComplete=\"off"
										      onblur="getCustNameOnblur(event,this)"/>
										      
										      <input type="button" name="bt1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}')"/>
										    
											<html:text property="bean.customerName" styleId="customerName"  size="50" readonly="true" styleClass="disableText"/>
											<html:hidden property="bean.customerId" styleId="customerId" />
											&nbsp;&nbsp;ที่อยู่
											<html:text property="bean.customerAddress" styleId="customerAddress" size="70" readonly="true" styleClass="disableText"/>
										</td>
									</tr>
									</table>
							     </td>
							  </tr>
							<tr>
								<td colspan="4" align="center">
								<div align="left" style="margin-left:13px;">
								 <c:if test="${stockReturnForm.bean.showSaveBtn =='true'}">
								    <c:if test="${stockReturnForm.bean.canEdit =='true'}">
								      <div align="left">
								          <input type="button" class="newPosBtn" value="เพิ่มรายการ" onclick="addRow('${pageContext.request.contextPath}');"/>
								           <input type="button" class="newPosBtn" value="ลบรายการ" onclick="removeRow('${pageContext.request.contextPath}');"/>	
							           </div>
								     </c:if>
								   </c:if>
								</div>
								
								<!--  Results  -->
								<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
									<tr>
									    <th rowspan="3">
									     <!--   <input type="checkbox" name="chkAll"
											onclick="checkSelect(this,document.getElementsByName('lineids'));" /> -->
										</th>
					 					<th rowspan="3">No.</th> 
										<th rowspan="3">รหัสสินค้า</th>
										<th rowspan="3">ชื่อสินค้า</th>
										<th rowspan="3">เลขที่บิล</th>
										<th rowspan="3">จำนวนหีบคงเหลือในบิลที่สามารถคืนได้</th>
										<th colspan="5">สำหรับพนักงานขาย</th>
										<th rowspan="3">ราคาขายต่อหีบ</th>
										<th colspan="2">จำนวนเงิน</th>
									</tr>
									<tr>
										<th colspan="3">สินค้าแจ้งคืน</th>
										<th colspan="2">การบรรจุ(UOM)</th>
										<th rowspan="2">ส่วนลด<br/>(ต่อหีบ)</th>
										<th rowspan="2">รวม</th>
									</tr>
									 <tr>
										<th>เต็ม</th>
                                      	<th>เศษ</th>
                                      	<th>หน่วย</th>
										<th>หีบ</th>
										<th>เศษ</th>
									</tr>
								 <c:set var="tabIndex" value="${0}"/>
						         <c:forEach var="results" items="${stockReturnForm.lines}" varStatus="rows">
									<c:choose>
										<c:when test="${rows.index %2 == 0}">
											<c:set var="tabclass" value="lineO"/>
										</c:when>
										<c:otherwise>
											<c:set var="tabclass" value="lineE"/>
										</c:otherwise>
									</c:choose>
									<c:set var="tabIndex" value="${tabIndex + 1}" />

									<tr class="${tabclass}">
									    <td class = "td_text_center" width="5%">
										  <input type="checkbox" name="linechk" value="${results.lineId}" />
										  <input type="hidden" name="lineId" id="lineId" value ="${results.lineId}"/>
										   <input type="hidden" name="status" id="status" value ="${results.status}"/>
										</td>
										 <td class = "td_text_center" width="5%">
										   <input type='text' name='no' value='${rows.index + 1}' size='2' id="no" readonly class="disableTextCenter">
										 </td>
										
										<td class ="td_text_center"  width="6%">
											<input type="text" name="productCode" id="productCode" value ="${results.productCode}" size="5"
										        onkeypress="getProductKeypress(event,this,${results.no})"
											    onkeydown="getProductKeydown(event,this,${results.no})"
											    onchange="checkProductOnblur(event,this,${results.no})" 
											    readonly class="disableText"  tabindex="${tabIndex}"
											    autoComplete="off"
											  /> 
										</td>
										
										<td class="td_text"  width="15%">
									       <input type="text" name="productName" id="productName" value ="${results.productName}" size="40" readonly class="disableText"/>
									       <input type="hidden" name="inventoryItemId" id="inventoryItemId" value ="${results.inventoryItemId}"/>
									     
									       <input type="hidden" size='3' class='disableText' name="uom1ConvRate" id="uom1ConvRate" value ="${results.uom1ConvRate}"/>
									       <input type="hidden" size='3' class='disableText' name="uom2ConvRate" id="uom2ConvRate" value ="${results.uom2ConvRate}"/>	
										</td>
										<td class="td_text_center"  width="10%" nowrap>  
									        <input type="text" tabindex="<% tabIndex++; out.print(tabIndex);%>"
											value="${results.arInvoiceNo}" name="arInvoiceNo" size="9" id="arInvoiceNo" />
										    <input type="button" name="bt3" value="..." onclick="openPopupInvoice(${rows1.index + 1})"/>
										</td>
										<td class="td_number"  width="5%">
										  <!-- remainPriAllQty: --><input type="text" size="8" name="remainPriAllQty" id="remainPriAllQty" value ="${results.remainPriAllQty}" readonly class="disableNumber"/>	  
										  <!-- remainPriQty: --><input type="hidden" size='3' class='disableText' name="remainPriQty" id="priQty" value ="${results.remainPriQty}"/>
									      <!-- remainSubQty: --><input type="hidden" size='3' class='disableText' name="remainSubQty" id="subQty" value ="${results.remainSubQty}"/>
										</td>
										<td class="td_number" width="6%">
										  
										    <c:set var="tabIndex" value="${tabIndex + 1}" />
											<input type="text"
											tabindex="${tabIndex}"
											value="${results.uom1Qty}" name="uom1Qty" size="5"
											onkeydown="return isNum0to9andpoint(this,event);"
											onblur ="sumTotalInRow(${results.no})"
											class="numberText"  autoComplete="off"/>
											<!-- priQty: --><input type="hidden" name="priQty" class='disableText' size='3' id="priQty" value ="${results.priQty}"/>
										</td>
										<td class="td_number" width="6%">
										    <c:set var="tabIndex" value="${tabIndex + 1}" />
											<input type="text"
											tabindex="${tabIndex}"
											value="${results.uom2Qty}" name="uom2Qty" size="5"
											onkeydown="return isNum(this,event);"
											onblur ="sumTotalInRow(${results.no})"
											class="numberText"  autoComplete="off"/>
										</td>
										<td class="td_text_center" width="6%">
										  <input type='text' name='uom2'  size='3' value='${results.uom2}' id="uom2"  readonly class="disableText">
										</td>
										<td class="td_number" width="7%">
										  <input type='text' name='uom1Pac'  size='6' value='${results.uom1Pac}' id="uom1Pac"  readonly class="disableNumber">
										</td>
										<td class="td_number" width="7%">
										  <input type='text' name='uom2Pac'  size='6' value='${results.uom2Pac}' id="uom2Pac"  readonly class="disableNumber">
										</td>
										<td class="td_number" width="7%">
										  <input type='text' name='uom1Price'  size='6' value='${results.uom1Price}' id="uom1Price"  readonly class="disableNumber">
										</td>
	       							    <td class="td_number" width="7%">
										    <c:set var="tabIndex" value="${tabIndex + 1}" />
											<input type="text"
											tabindex="${tabIndex}"
											value="${results.discount}" name="discount" size="5"
											onkeydown="return isNum(this,event);"
											onblur ="sumTotalInRow(${results.no})"
											class="numberText" autoComplete="off" />
										</td>
										<td class="td_number" width="7%">
										  <input type='text' name='totalAmount'  size='10' value='${results.totalAmount}' id="price" readonly class="disableNumber">
										</td>
									</tr>
								</c:forEach>
								  
								</table>
								<!--  Results -->

								<!-- Total Sum -->
								 <table align="center" border="0" cellpadding="3" cellspacing="1" width="100%" >
								   <tr>
										<td  align="right" width="93%"><b>ยอดก่อน VAT</b></td>
										<td  align="right" width="7%">
									         <html:text property="bean.totalAllNonVatAmount" size="20" styleId="totalAllNonVatAmount" styleClass="disableNumberBold" readonly="true" />
										</td>
									</tr>
									<tr>
										<td  align="right" width="93%"><b>VAT</b></td>
										<td  align="right" width="7%">
									         <html:text property="bean.totalAllVatAmount" size="20" styleId="totalAllVatAmount" styleClass="disableNumberBold" readonly="true" />
										</td>
									</tr>
									<tr>
										<td  align="right" width="93%"><b>ยอดรวม</b></td>
										<td  align="right" width="7%">
									         <html:text property="bean.totalAllAmount" size="20" styleId="totalAllAmount" styleClass="disableNumberBold" readonly="true"/>
										</td>
									</tr>
									<tr>
										<td  align="left" width="100%" colspan="2" valign="top">
										<b>หมายเหตุในการคืนสินค้า :</b>
										<html:textarea property="bean.description" cols="100" rows="3" styleClass="\" autoComplete=\"off"/>
										
										</td>
									</tr>
								</table> -
							    <!--  Total Sum -->
								</td>
							</tr>
				
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
								<c:if test="${stockReturnForm.bean.showSaveBtn =='true'}">
								   <c:if test="${stockReturnForm.bean.canEdit =='true'}">
									<a href="#" onclick="return save('${pageContext.request.contextPath}');">
									  <input type="button" value="บันทึกรายการ" class="newPosBtnLong">
									</a>	
								   </c:if>	
								</c:if>		
								<c:if test="${stockReturnForm.bean.showPrintBtn =='true'}">
									<a href="#" onclick="gotoReport('${pageContext.request.contextPath}','1','${stockReturnForm.bean.requestNumber}');">
									    <input type="button" value="พิมพ์รายงาน"  class="newPosBtnLong">
								    </a> 
								 </c:if>
								<a href="#" onclick="backsearch('${pageContext.request.contextPath}');">
									  <input type="button" value="ปิดหน้าจอ"  class="newPosBtnLong">
								    </a>
								</td>
							</tr>
						</table>
						
						<!-- Hidden Field -->
						<html:hidden property="deletedId"/>
						<html:hidden property="lineNoDeleteArray"/>
						<input type="hidden" name="backPage" value="<%=backPage %>" />
						
						<div id="productList" style="display: none;">
						  
						</div>
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
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


