<%@page import="com.isecinc.pens.bean.StockDiscountLine"%>
<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="stockDiscountForm" class="com.isecinc.pens.web.stockdiscount.StockDiscountForm" scope="request" />
<%
int tabIndex = 0;
if(stockDiscountForm.getResults() != null){
	tabIndex = stockDiscountForm.getResults().size()*2;
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
<style type="text/css"></style>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css?v=<%=SessionGen.getInstance().getIdSession()%>"" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/stockDiscount.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>

<script type="text/javascript">

//To disable f5
$(document).bind("keydown", disableF5);
function disableF5(e) {
	if (e.which == 116) e.preventDefault(); 
} 
function loadMe(){
	//check DocType
	checkDocType();
	
	//alert("loadMe");
	addRow(true);	
	  
	//resum Total 
	sumTotalAllRow();
}
function backsearch(path) {
	document.stockDiscountForm.action = path + "/jsp/stockDiscountAction.do?do=prepare"+"&action=back";//stockSearch
	document.stockDiscountForm.submit();
}
function clearForm(path) {
	document.stockDiscountForm.action = path + "/jsp/stockDiscountAction.do?do=createNewStock";//stockSearch
	document.stockDiscountForm.submit();
}
function printReport(path) {
	document.stockDiscountForm.action = path + "/jsp/stockDiscountAction.do?do=printReport";//stockSearch
	document.stockDiscountForm.submit();
}
function openPopupInvoice(index){
	openPopupInvoiceModel('${pageContext.request.contextPath}',index,<%=user.getId()%>);
}
/**
 * AddRow(setFocus)
 */
function addRow(setFocus){
	
	//Check can add new Row (maxRow=12)
	if( !canAddNewRow()){
		alert("ไม่สามารถเพิ่มรายการมากกว่า  12 รายการ  กรุณาเปิดรายการใหม่");
		return false;
	}
	
	var rows = $('#tblProduct tr').length;
	var className = 'lineO';
	if(rows%2 !=0){
		className = 'lineE';
	}
	var rowId = rows;
    var tabIndex = parseFloat(document.getElementById("tabIndex").value);
    var no = 1;
    //calc no (for case del row)
    var itemCode = document.getElementsByName("productCode");
	var status = document.getElementsByName("status");
	for(var i=0;i<itemCode.length;i++){
		if(status[i].value !='DELETE'){
			 no++; 
		}
	}//for
	tabIndex++;
	
	//alert("rows:"+rows+",rowId["+rowId+"]");
	//check docType 
	var docType = document.getElementById("docType").value;
	var readonlyProductCode = "";
	var classProductCode = "normalText";
	var readonlyProductName = "";
	var classProductName = "normalText";
    if(docType != "" && docType == "KEY_PRODUCT"){
    	readonlyProductName = "readonly";
    	classProductName = "disableText";
    }else if(docType != "" && docType == "KEY_DETAIL"){
    	readonlyProductCode = "readonly";
    	classProductCode = "disableText";
    }
    	
	var rowData ="<tr class='"+className+"'>"+
	    "<td class='td_text_center' width='5%'> " +
	    "  <input type='checkbox' tabindex ='-1' name='linechk' id='lineChk' value='0'/>" +
	    "  <input type='hidden' tabindex ='-1' name='lineId' id='lineId' value='0'/>"+
	    "  <input type='hidden' tabindex ='-1' name='status' id='status' value='SV' />"+
	    "</td>";
	
	    rowData +="<td class='td_text_center' width='5%'> " +
	    "  <input type='text' name='no' value='"+no+"' id='no' size='2' readonly class='disableTextCenter'>" +
	    "</td>";

	   rowData +="<td class='td_text_center' width='5%'> "+
	    "  <input type='text' name='productCode' id='productCode' size='5' class='"+classProductCode+"' "+readonlyProductCode+
	    "   onkeypress='getProductKeypress(event,this,"+rowId+")' "+
	    "   onchange='checkProductOnblur(event,this,"+rowId+")' " +
	    "   tabindex ="+tabIndex+
	    "  autoComplete='off'/>  </td>";
	    
	    rowData +="<td class='td_text'  width='25%'> "+
	    " <input type='text' tabindex ='-1' name='productName' size='40' class='"+classProductName+"' "+readonlyProductName+" autoComplete='off' onchange='disableInputProduct("+rowId+")'/>" +
	    " <input type='hidden' tabindex ='-1' name='inventoryItemId' id='inventoryItemId'/>"+
	    " <input type='hidden' size='3' class='disableText' tabindex ='-1' name='uom1ConvRate' id='uom1ConvRate'/>"+
	    " <input type='hidden' size='3' class='disableText' tabindex ='-1' name='uom2ConvRate' id='uom2ConvRate'/>"+
	    "</td>";
	    
	    tabIndex++;
	    rowData +="<td class='td_text_center'  width='15%'> "+
	    " <input type='text' name='arInvoiceNo' id='arInvoiceNo' autoComplete='off' value ='' size='15'  readonly tabindex ="+tabIndex+"/>" +
	    " <input type='button' name='bt3' value='...' onclick='openPopupInvoice("+no+")'/> "+
	    " <!--Hidden -->"+
	    " <!--rePriAllQty:--><input type='hidden' size='3' name='remainPriAllQty' id='remainPriAllQty' value =''/></br>" +
	    " <!--rePriQty:--><input type='hidden' size='3' name='remainPriQty' id='remainPriQty' value =''/>" +
	    " <!--reSubQty:--><input type='hidden' size='3' name='remainSubQty' id='remainSubQty' value =''/>" +
	    " <!--reAmount:--><input type='hidden' size='5' name='remainAmount' id='remainAmount' value =''/>" +
	    "</td>"+
	    
	    tabIndex++;
	    rowData +="<td class='td_number' width='10%'> "+
	    " <input type='text' tabindex ="+tabIndex+
	    "  value='' name='lineAmount' size='7' onblur=sumTotalInRow("+no+",'AMOUNT') "+
	    "  onkeydown='return isNum(this,event);' class='numberText' autoComplete='off'/> "+
	    "  </td>";
	    
	    tabIndex++;
	    rowData +="<td class='td_number' width='10%'> "+
	    " <input type='text' value='' tabindex ="+tabIndex+" name='vatAmount' size='6' readonly class='disableNumber'/> "+
	    "  </td>";
	    
	    rowData +="<td class='td_number' width='10%'> "+
	    "  <input type='text' name='netAmount' id='netAmount' size='7' readonly class='disableNumber'>"+
	    "  </td>";
	    
	    tabIndex++;
	    rowData +="<td class='td_number' width='5%'> "+
	    " <input type='text' tabindex ="+tabIndex+
	    "  value='' name='uom1Qty' size='5' "+
	    "  onblur =sumTotalInRow("+no+",'CTN') autoComplete='off'"+
	    "  onkeydown='return isNum0to9andpoint(this,event);'class='numberText' /> "+
	    " <!--priQty:--><input type='hidden' size='3' tabindex ='-1' class='disableText' name='priQty' id='priQty'/>"+
	    "  </td>";
	    
	    rowData +="<td class='td_text_center' width='5%'> "+
	    "  <input type='text' name='uom2' value='' id='uom2' size='3' readonly class='disableText'>"+
	    "  </td>";
	    
	    tabIndex++;
	    rowData +="<td class='td_number' width='5%'> "+
	    " <input type='text' tabindex ="+tabIndex+
	    "  value='' name='uom2Qty' size='5' "+
	    "  onblur =sumTotalInRow("+no+",'CTN') "+
	    "  onkeydown='return isNum0to9andpoint(this,event);' class='numberText' autoComplete='off'/> "+
	    " <!--Hidden -->"+
	    " <input type='hidden' name='uom1Pac' id='uom1Pac' value ='' />" +
	    " <input type='hidden' name='uom2Pac' id='uom2Pac' value ='' />" +
	    " <input type='hidden' name='uom1Price' id='uom1Price' value =''/>" +
	    " </td>"+
	    "</tr>";
	    
	//alert(rowData);
    $('#tblProduct').append(rowData);
    //set next tabIndex
    document.getElementById("tabIndex").value = tabIndex;
    
    //set focus default
    var itemCode = document.getElementsByName("productCode");
    //alert(setFocus);
    if(setFocus){
       itemCode[rowId-1].focus();
    }
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
				<jsp:param name="function" value="StockDiscount"/>
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
						<html:form action="/jsp/stockDiscountAction">
						<jsp:include page="../error.jsp"/>
						
						<!-- Hidden -->
		                 <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
						 <input type="hidden" id="tabIndex" name="tabIndex" value="<%=tabIndex%>"/>
						   
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td colspan="4" align="center">
							        <font color="black" size="5"> <b> บันทึกข้อมูล ใบอนุมัติให้ส่วนลดร้านค้า </b> </font>
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
                                           &nbsp;&nbsp;                                                         
                                          Vat Rate:
                                          <%if(1==2 && Utils.isNull(request.getParameter("action")).equalsIgnoreCase("view")){ %>
                                             <html:select property="bean.vatRate" styleId="vatRate" styleClass="disableText" disabled="true">
	                                            <html:option value=""></html:option>
	                                            <html:options collection="VATRATE_LIST" property="key" labelProperty="key"/>
	                                          </html:select>
                                          <%}else{ %>
	                                          <font color="red">*</font>
	                                          <html:select property="bean.vatRate" styleId="vatRate" onchange="reCalcChangeVatRate()">
	                                            <html:option value=""></html:option>
	                                            <html:options collection="VATRATE_LIST" property="key" labelProperty="key"/>
	                                          </html:select>
                                          <%} %>
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
										      
										      <input type="button" id="btFindCust" name="btFindCust" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}')"/>
										    
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
								 <c:if test="${stockDiscountForm.bean.showSaveBtn =='true'}">
								    <c:if test="${stockDiscountForm.bean.canEdit =='true'}">
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
									    <th>
									     <!--   <input type="checkbox" name="chkAll"
											onclick="checkSelect(this,document.getElementsByName('lineids'));" /> -->
										</th>
					 					<th>No.</th> 
										<th>รหัสสินค้า</th>
										<th>ชื่อสินค้า/รายละเอียด</th>
										<th>อ้างอิงเลขที่บิล</th>
										<th>จำนวนเงินส่วนลด</th>
										<th>Vat</th>
										<th>จำนวนเงินรวม</th>
										<th>จำนวน เต็มหีบ</th>
										<th>หน่วยเศษ</th>
										<th>จำนวนเศษ</th>
									</tr>
									
								<% 
                              String tabclass = "";
                              String selected = "";
                              if(stockDiscountForm.getLines() != null){
									for(int i=0;i<stockDiscountForm.getLines().size();i++){
										 StockDiscountLine item = stockDiscountForm.getLines().get(i); 
										 tabclass = i%2==0?"lineO":"lineE";
									%>
									<tr class="<%=tabclass%>">
									    <td class = "td_text_center" width="5%">
										  <input type="checkbox" name="linechk" value="<%=item.getLineId() %>" />
										  <input type="hidden" name="lineId" id="lineId" value ="<%=item.getLineId() %>"/>
										   <input type="hidden" name="status" id="status" value ="<%=item.getStatus() %>"/>
										</td>
										 <td class = "td_text_center" width="5%">
										   <input type='text' name='no' value='<%=(i+1) %>' size='2' id="no" readonly class="disableTextCenter">
										 </td>
										
										<td class ="td_text_center"  width="5%">
											<input type="text" name="productCode" id="productCode" value ="<%=item.getProductCode() %>" size="5"
										        onkeypress="getProductKeypress(event,this,<%=item.getNo() %>)"
											    onkeydown="getProductKeydown(event,this,<%=item.getNo() %>)"
											    onchange="checkProductOnblur(event,this,<%=item.getNo() %>)" 
											    readonly class="disableText"  tabindex="<%=tabIndex %>}"
											    autoComplete="off"
											  /> 
										</td>
										
										<td class="td_text"  width="25%">
										   <%if( !Utils.isNull(item.getProductCode()).equals("") ){%>
									         <input type="text" name="productName" id="productName" value ="<%=item.getProductName() %>" size="40" readonly  class="disableText"/>
									       <%}else{ %>
									         <input type="text" name="productName" id="productName" value ="<%=item.getProductName() %>" size="40" autoComplete="off" class="enableText"/>
									       <%} %>
									       <input type="hidden" name="inventoryItemId" id="inventoryItemId" value ="<%=item.getInventoryItemId() %>"/>
									     
									       <input type="hidden" size='3' class='disableText' name="uom1ConvRate" id="uom1ConvRate" value ="<%=item.getUom1ConvRate() %>"/>
									       <input type="hidden" size='3' class='disableText' name="uom2ConvRate" id="uom2ConvRate" value ="<%=item.getUom2ConvRate() %>"/>	
										
										
										</td>
										<td class="td_text_center"  width="15%" >  
									        <input type="text" tabindex="<% tabIndex++; out.print(tabIndex);%>"
											value="<%=item.getArInvoiceNo() %>" name="arInvoiceNo" size="15" id="arInvoiceNo" />
										    <input type="button" name="bt3" value="..." onclick="openPopupInvoice(<%=(i + 1)%>)"/>
										  
										  <!-- rePriAllQty--><input type="hidden" size="8" name="remainPriAllQty" id="remainPriAllQty" value ="<%=item.getRemainPriAllQty() %>" readonly class="disableNumber"/>	  
										  <!-- rePriQty: --><input type="hidden" size='3' class='disableText' name="remainPriQty" id="priQty" value ="<%=item.getRemainPriQty() %>"/>
									      <!-- reSubQty: --><input type="hidden" size='3' class='disableText' name="remainSubQty" id="subQty" value ="<%=item.getRemainSubQty() %>"/>
                                          <!-- reAmount--><input type="hidden" size="8" name="remainAmount" id="remainAmount" value ="<%=item.getRemainAmount() %>" readonly class="disableNumber"/>	  
										</td>
										
										<td class="td_number" width="10%">
											<input type="text"
											tabindex="<% tabIndex++; out.print(tabIndex);%>"
											value="<%=item.getLineAmount() %>" name="lineAmount" size="7"
											onkeydown="return isNum(this,event);"
											onblur ="sumTotalInRow(<%=item.getNo() %>,'AMOUNT')"
											class="numberText" autoComplete="off" />
										</td>
										<td class="td_number" width="10%"> 
										  <input type='text' name='vatAmount'  size='6' value='<%=item.getVatAmount() %>' id="vatAmount" readonly class="disableNumber">
										</td>
										<td class="td_number" width="10%">
										  <input type='text' name='netAmount'  size='7' value='<%=item.getNetAmount()%>' id="netAmount" readonly class="disableNumber">
										</td>
										
										<td class="td_number" width="5%">
											<input type="text"
											tabindex="<% tabIndex++; out.print(tabIndex);%>"
											value="<%=item.getUom1Qty() %>" name="uom1Qty" size="5"
											onkeydown="return isNum0to9andpoint(this,event);"
											onblur ="sumTotalInRow(${results.no},'CTN')"
											class="numberText"  autoComplete="off"/>
											<!-- priQty: -->
											<input type="hidden" name="priQty" class='disableText' size='3' id="priQty" value ="<%=item.getPriQty() %>"/>
										</td>
										<td class="td_text_center" width="5%">
										  <input type='text' name='uom2'  size='3' value='<%=item.getUom2() %>' id="uom2"  readonly class="disableText">
										</td>
										<td class="td_number" width="5%">
											<input type="text"
											tabindex="<% tabIndex++; out.print(tabIndex);%>"
											value="<%=item.getUom2Qty() %>" name="uom2Qty" size="5"
											onkeydown="return isNum(this,event);"
											onblur ="sumTotalInRow(<%=item.getNo() %>,'CTN')"
											class="numberText"  autoComplete="off"/>
											
										   <!-- HIDDEN -->
										  <input type='hidden' name='uom1Pac'  size='6' value='<%=item.getUom1Pac() %>' id="uom1Pac">
										  <input type='hidden' name='uom2Pac'  size='6' value='<%=item.getUom2Pac() %>' id="uom2Pac">
										  <input type='hidden' name='uom1Price'  size='6' value='<%=item.getUom1Price()%>' id="uom1Price">
										
										</td>
									</tr>
								<%
									}//for
                                 }//if
								%>							  
								</table>
								<!--  Results -->

								<!-- Total Sum -->
								 <table align="center" border="0" cellpadding="3" cellspacing="1" width="100%" >
								    <tr>
										<td  align="right" width="55%"><b>ยอดรวม</b></td>
										<td  align="right" width="10%">
									         <html:text property="bean.totalLineAmount" size="7" styleId="totalLineAmount" styleClass="disableNumberBold" readonly="true" />
										</td>
											<td  align="right" width="10%">
									         <html:text property="bean.totalVatAmount" size="7" styleId="totalVatAmount" styleClass="disableNumberBold" readonly="true" />
										</td>
											<td  align="right" width="10%">
									         <html:text property="bean.totalNetAmount" size="7" styleId="totalNetAmount" styleClass="disableNumberBold" readonly="true" />
										</td>
										<td  align="right" width="5%"></td>
										<td  align="right" width="5%"></td>
										<td  align="right" width="5%"></td>
									 </tr>
									 <tr>
										<td  align="left" width="100%" colspan="7" valign="top">
										<b>ให้ส่วนลดร้านค้าเพื่อ <font color="red">*</font> :</b>
										<html:textarea property="bean.description" styleId="description" cols="100" rows="3" styleClass="\" autoComplete=\"off"/>
										
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
								<c:if test="${stockDiscountForm.bean.showPrintBtn =='true'}">
									 <a href="#" onclick="printReport('${pageContext.request.contextPath}');">
									    <input type="button" value="พิมพ์รายงาน"  class="newPosBtnLong">
								    </a> 
								 </c:if>
								 
								<c:if test="${stockDiscountForm.bean.showSaveBtn =='true'}">
								   <c:if test="${stockDiscountForm.bean.canEdit =='true'}">
									<a href="#" onclick="return save('${pageContext.request.contextPath}');">
									  <input type="button" value="บันทึกรายการ" class="newPosBtnLong">
									</a>	
								   </c:if>	
								</c:if>		
							
								 <a href="#" onclick="clearForm('${pageContext.request.contextPath}');">
									  <input type="button" value="  Clear  "  class="newPosBtnLong">
								    </a>
								
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
						<!--docType:--><input type="hidden" name="docType" id="docType" value="" />
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


