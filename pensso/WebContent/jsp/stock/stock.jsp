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
<jsp:useBean id="stockForm" class="com.isecinc.pens.web.stock.StockForm" scope="request" />
<%

int tabIndex = 0;
if(stockForm.getResults() != null){
	tabIndex = stockForm.getResults().size()*2;
}

User user = ((User)session.getAttribute("user"));
String role = user.getType();
String userName = user.getUserName();
String backPage = Utils.isNull(request.getParameter("backPage"));
if(backPage.equals("")){
	backPage = Utils.isNull(request.getAttribute("backPage"));
}

List<Address> custAddr = new ArrayList<Address>();
custAddr = new MAddress().lookUp(stockForm.getBean().getCustomerId()); 

List<Address> billAddr = new ArrayList<Address>();
List<Address> shipAddr = new ArrayList<Address>();

for(Address address:custAddr){
	if("Y".equals(address.getIsActive()))
	{
		if("B".equalsIgnoreCase(address.getPurpose()))
			billAddr.add(address);
		else if("S".equalsIgnoreCase(address.getPurpose()))
			shipAddr.add(address);
	}
}
pageContext.setAttribute("billAddr",billAddr,PageContext.PAGE_SCOPE);
pageContext.setAttribute("shipAddr",shipAddr,PageContext.PAGE_SCOPE);
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
<link type="text/css" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" rel="stylesheet" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}

-->
 
.currPage{ border:1px solid #000000; padding-left:4px;padding-right:4px;padding-top:2px; }
.pageLink{padding-left:4px;padding-right:2px;padding-top:2px; }
.paging{height:18px;width:100%;}
.catalog{text-align:center;height:60px;width:25%;/*background-color:#FFCC99;*/}
.brandName{width:120px;vertical-align:top;}

table#productList thead{background:#FFE4CA;}
.qtyInput{width:70px; height:26px;text-align:right;}
table#productList tbody td{vertical-align:top;padding-left:2px;padding-right:4px;}
table#productList tbody td.number{text-align:right;}

.dateStyle{
	vertical-align:text-bottom; 
	cursor: hand;
   }
	
#dummyDate {
    opacity: 0;
    position: absolute;
    top: 0;
    left: 0;    
}

#ui-datepicker-div{
        z-index: 9999999;
   }
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css?v=<%=SessionGen.getInstance().getIdSession()%>"" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page_stock/epoch.js"></script>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/tablesorter.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/stock.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>

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
    <%if(backPage.equalsIgnoreCase("stockSearch")){ %>
	   document.stockForm.action = path + "/jsp/stockAction.do?do=prepare"+"&action=back";//stockSearch
	<%}else { %>
	   document.stockForm.action = path + "/jsp/stockAction.do?do=prepareCustomer"+"&action=back&search=new";//stockCustomerSearch
	<%}%>
	document.stockForm.submit();
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
				<jsp:param name="function" value="Stock"/>
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
						<html:form action="/jsp/stockAction">
						<jsp:include page="../error.jsp"/>
						
						<!-- Hidden -->
		                 <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
						 <input type="hidden" id="tabIndex" name="tabIndex" value="<%=tabIndex%>"/>
						   
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td colspan="4" align="center">
							        <font color="black" size="5"> <b> บันทึกข้อมูลสต๊อกร้านค้า</b> </font>
							    </td>
							</tr>
							<tr>
								<td colspan="4" align="center">
								   <table align="center" border="0" cellpadding="3" cellspacing="0" width="80%">
								   <tr>
										<td align="right" width="20%"></td>
										<td align="right" colspan="3" width="60%">
									              เลขที่เอกสาร <html:text property="bean.requestNumber" size="10" readonly="true" styleClass="disableText"/>
										  &nbsp;&nbsp; 
										     วันที่ทำรายการ   <html:text property="bean.requestDate" size="8" readonly="true" styleClass="disableText"/>
										
                                                                                                      สถานะ <html:text property="bean.statusLabel" size="5" readonly="true" styleClass="disableText"/>
                                         </td>
									</tr>
									
									<tr>
										<td align="right">
											ลูกค้า
										</td>
										<td align="left" colspan="3">
											<html:text property="bean.customerName" size="80" readonly="true" styleClass="disableText"/>
											<html:hidden property="bean.customerId" styleId="bean.customerId" />
											
										</td>
									</tr>
									<tr>
										<td align="right">ที่อยู่ในการจัดส่งสินค้า</td>
										<td align="left" colspan="3">
											<html:select property="bean.shipAddressId" style="width:80%" disabled="true"  styleClass="disableText">
												<html:options collection="shipAddr" property="id" labelProperty="lineString"/>
											</html:select>
										</td>
									</tr>
									<%-- <tr>
										<td align="right">ที่อยู่ในการจัดส่งเอกสาร</td>
										<td align="left" colspan="3">
											<html:select property="bean.billAddressId" style="width:80%"  disabled="true" styleClass="disableText">
												<html:options collection="billAddr" property="id" labelProperty="lineString"/>
											</html:select>
										</td>
									</tr> --%>
									<tr>
										<td align="right">Remark</td>
										<td align="left" colspan="3">
											<html:text property="bean.description" size="60" styleClass="\" autoComplete=\"off"/>
											&nbsp;&nbsp;&nbsp;
											ใช้ข้อมูลการสั่งซื้อย้อนหลัง <html:text property="bean.backAvgMonth" size="5"  readonly="true" styleClass="disableTextCenter"/>&nbsp;เดือน
										</td>
									</tr>
									</table>
							     </td>
							  </tr>
							  
							<tr>
								<td colspan="4" align="center">
								<div align="left" style="margin-left:13px;">
								 <c:if test="${stockForm.bean.showSaveBtn =='true'}">
								    <c:if test="${stockForm.bean.canEdit =='true'}">
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
									    <th rowspan="2">
									     <!--   <input type="checkbox" name="chkAll"
											onclick="checkSelect(this,document.getElementsByName('lineids'));" /> -->
										</th>
					<!-- 					<th rowspan="2">No.</th> -->
										<th rowspan="2">รหัสสินค้า</th>
										<th rowspan="2">ชื่อสินค้า</th>
										<th rowspan="2">ค่าเฉลี่ยยอดสั่งซื้อย้อนหลัง</th>
										<th rowspan="2">หน่วยนับ</th>
										<th rowspan="2">อัตรา</th>
										<th colspan="3">กลุ่มที่ 1</th>
										<th colspan="3">กลุ่มที่ 2</th>
										<th colspan="3">กลุ่มที่ 3</th>
									</tr>
									<tr>
										<th>หีบ</th>
										<th>เศษ</th>
										<th>วันหมดอายุ</th>
										
										<th>หีบ</th>
										<th>เศษ</th>
										<th>วันหมดอายุ</th>
										
										<th>หีบ</th>
										<th>เศษ</th>
										<th>วันหมดอายุ</th>
										
									</tr>
								 <c:set var="tabIndex" value="${0}"/>
						         <c:forEach var="results" items="${stockForm.lines}" varStatus="rows">
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
									    <td class = "td_text_center" width="3%">
										  <input type="checkbox" name="linechk" value="${results.lineId}" />
										  <input type="hidden" name="lineId" id="lineId" value ="${results.lineId}"/>
										   <input type="hidden" name="status" id="status" value ="${results.status}"/>
										</td>
										<%-- <td class = "td_text_center" width="3%">
										   <input type='text' name='no' value='${rows1.index + 1}' size='2' id="no"  readonly class="disableText">
										 </td> --%>
										
										<td class ="td_text_center"  width="5%">
											<input type="text" name="productCode" id="productCode" value ="${results.productCode}" size="5"
										        onkeypress="getProductKeypress(event,this,${results.no})"
											    onkeydown="getProductKeydown(event,this,${results.no})"
											    onchange="checkProductOnblur(event,this,${results.no})" 
											     readonly class="disableText"  tabindex="${tabIndex}"
											     autoComplete="off"
											  /> 
										</td>
										
										<td class="td_text"  width="15%">
										    <input type="hidden" name="inventoryItemId" id="inventoryItemId" value ="${results.inventoryItemId}"/>
									       <input type="text" name="productName" id="productName" value ="${results.productName}" size="40" readonly class="disableText"/>	
										</td>
										<td class="td_text_center"  width="5%">   
									       <input type="text" name="avgOrderQty" id="avgOrderQty" value ="${results.avgOrderQty}" size="5" readonly class="disableNumber"/>	
										</td>
										<td class="td_text_number"  width="5%">
										 <input type="text" name="fullUom" id="fullUom" value ="${results.fullUom}" size="6" readonly class="disableText"/>	  
										</td>
										<td class="td_text_center"  width="5%">
										 <input type="text" name="conversionRate" id="conversionRate" value ="${results.conversionRate}" size="7" readonly class="disableText"/>	  
										</td>
										<td class="td_number" width="3%">
										    <c:set var="tabIndex" value="${tabIndex + 1}" />
											<input type="text"
											tabindex="${tabIndex}"
											value="${results.qty}" name="qty" size="5"
											onkeydown="return isNum0to9andpoint(this,event);"
											class="numberText" autoComplete="off"/>
										</td>
										<td class="td_number" width="3%">
										    <c:set var="tabIndex" value="${tabIndex + 1}" />
											<input type="text"
											tabindex="${tabIndex}"
											value="${results.sub}" name="sub" size="5"
											onkeydown="return isNum0to9andpoint(this,event);"
											class="numberText" autoComplete="off"/>
										</td>
										<td class="td_text_center" width="5%">
										  <input type='text' name='expireDate'  size='8' value='${results.expireDate}' id="expireDate" onmouseover="popCalendar(this,this)" readonly>
									       <font color="red"></font>
										</td>
										
										<td class="td_number" width="3%">
										    <c:set var="tabIndex" value="${tabIndex + 1}" />
											<input type="text"
											tabindex="${tabIndex}"
											value="${results.qty2}" name="qty2" size="5"
											onkeydown="return isNum0to9andpoint(this,event);"
											class="numberText" autoComplete="off"/>
										</td>
										<td class="td_number" width="3%">
										    <c:set var="tabIndex" value="${tabIndex + 1}" />
											<input type="text"
											tabindex="${tabIndex}"
											value="${results.sub2}" name="sub2" size="5"
											onkeydown="return isNum0to9andpoint(this,event);"
											class="numberText" autoComplete="off"/>
										</td>
										<td class="td_text_center" width="5%">
										  <input type='text' name='expireDate2'  size='8' value='${results.expireDate2}' id="expireDate" onmouseover="popCalendar(this,this)" readonly>
									       <font color="red"></font>
										</td>
										
										<td class="td_number" width="3%">
										    <c:set var="tabIndex" value="${tabIndex + 1}" />
											<input type="text"
											tabindex="${tabIndex}"
											value="${results.qty3}" name="qty3" size="5"
											onkeydown="return isNum0to9andpoint(this,event);"
											class="numberText" autoComplete="off"/>
										</td>
										<td class="td_number" width="3%">
										    <c:set var="tabIndex" value="${tabIndex + 1}" />
											<input type="text"
											tabindex="${tabIndex}"
											value="${results.sub3}" name="sub3" size="5"
											onkeydown="return isNum0to9andpoint(this,event);"
											class="numberText" autoComplete="off"/>
										</td>
										<td class="td_text_center" width="5%">
										  <input type='text' name='expireDate3'  size='8' value='${results.expireDate3}' id="expireDate" onmouseover="popCalendar(this,this)" readonly>
									       <font color="red"></font>
										</td>
									</tr>
								</c:forEach>
								  
								</table>
								<!--  Results -->

								<!-- Total Sum -->
								<%-- <table align="center" border="0" cellpadding="3" cellspacing="1" width="100%" >
								    <tr>
								  	    <td  width="73%" >&nbsp;</td>
										<td  align="right">&nbsp;</td>
										<td  align="left">&nbsp;</td>
										<td class="status">&nbsp;</td>
									</tr>
								   <tr>
									    <td  width="50%" >&nbsp;</td>
										<td  align="right" width="23%"><b>ยอดรวมทั้งสิ้น</b></td>
										<td  align="left">
									         <html:text property="bean.totalAmount"  size="20"  styleId="totalAmount" styleClass="disableText" style="text-align: right;"/>
										</td>
										<td class="status">&nbsp;</td>
									</tr>
								</table> --%>
							    <!--  Total Sum -->
								</td>
							</tr>
				
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
								<c:if test="${stockForm.bean.showSaveBtn =='true'}">
								   <c:if test="${stockForm.bean.canEdit =='true'}">
									<a href="#" onclick="return save('${pageContext.request.contextPath}');">
									  <input type="button" value="บันทึกรายการ" class="newPosBtnLong">
									</a>	
								   </c:if>	
								</c:if>		
								
                                <c:if test="${stockForm.bean.showCancelBtn =='true'}">
                                   <c:if test="${stockForm.bean.canEdit =='true'}">
										<a href="#" onclick="cancelStock('${pageContext.request.contextPath}');"">
										  <input type="button" value="ยกเลิกรายการ"  class="newPosBtnLong">
									    </a>
								   </c:if>
								</c:if>
								
								<a href="#" onclick="backsearch('${pageContext.request.contextPath}');">
									  <input type="button" value="ปิดหน้าจอ"  class="newPosBtnLong">
								    </a>
								</td>
							</tr>
						</table>
						
						<!-- Hidden Field -->
						<html:hidden property="bean.priceListId" styleId="bean.priceListId"/>
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

 <!-- Control Save Lock Screen -->
<jsp:include page="../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->
