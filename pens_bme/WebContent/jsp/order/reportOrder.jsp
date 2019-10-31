<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.web.order.OrderAction"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="orderForm" class="com.isecinc.pens.web.order.OrderForm" scope="session" />

<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "orderForm");

if(session.getAttribute("custGroupList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(GeneralDAO.searchCustGroupByCustomer(new PopupForm()));
	
	session.setAttribute("custGroupList",billTypeList);
}

int start = 0;
int end = 0;
int pageNumber = 1;
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('salesDateFrom'));
	 new Epoch('epoch_popup', 'th', document.getElementById('salesDateTo'));
}
function search(path){
	var form = document.orderForm;
	var salesDateFrom =$('#salesDateFrom');
	var salesDateTo =$('#salesDateTo');
	var custGroup =$('#custGroup');
	
	if(custGroup.val() ==""){
		alert("กรุณาเลือก กลุ่มร้านค้า");
		custGroup.focus();
		return false;
	}
	if(salesDateFrom.val() ==""){
		alert("กรุณากรอกวันที่ Order From");
		salesDateFrom.focus();
		return false;
	}
	if(salesDateTo.val() ==""){
		alert("กรุณากรอกวันที่ Order To");
		salesDateTo.focus();
		return false;
	}
	form.action = path + "/jsp/orderAction.do?do=searchReportOrder&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(path,pageNumber){
	var form = document.orderForm;
	form.action = path + "/jsp/orderAction.do?do=searchReportOrder&pageNumber="+pageNumber;
	form.submit();
	return true;
	
}
function exportToExcel(path){
	var form = document.orderForm;
	var salesDateFrom =$('#salesDateFrom');
	var salesDateTo =$('#salesDateTo');
	var custGroup =$('#custGroup');
	
	if(custGroup.val() ==""){
		alert("กรุณาเลือก กลุ่มร้านค้า");
		custGroup.focus();
		return false;
	}
	if(salesDateFrom.val() ==""){
		alert("กรุณากรอกวันที่ Order From");
		salesDateFrom.focus();
		return false;
	}
	if(salesDateTo.val() ==""){
		alert("กรุณากรอกวันที่ Order To");
		salesDateTo.focus();
		return false;
	}
	form.action = path + "/jsp/orderAction.do?do=exportReportOrderToExcel";
	form.submit();
	return true;
}
function exportBarcodeToExcel(path){
	var form = document.orderForm;
	var salesDateFrom =$('#salesDateFrom');
	var salesDateTo =$('#salesDateTo');
	var custGroup =$('#custGroup');
	
	if(custGroup.val() ==""){
		alert("กรุณาเลือก กลุ่มร้านค้า");
		custGroup.focus();
		return false;
	}
	if(salesDateFrom.val() ==""){
		alert("กรุณากรอกวันที่ Order From");
		salesDateFrom.focus();
		return false;
	}
	if(salesDateTo.val() ==""){
		alert("กรุณากรอกวันที่ Order To");
		salesDateTo.focus();
		return false;
	}
	form.action = path + "/jsp/orderAction.do?do=exportBarcodeToExcel";
	form.submit();
	return true;
}
function clearForm(path){
	var form = document.orderForm;
	form.action = path + "/jsp/orderAction.do?do=prepareReportOrder&action=new";
	form.submit();
	return true;
}

function openPopupCustomer(path,types,storeType){
	var form = document.orderForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,storeNo,subInv,types){
	var form = document.orderForm;
	//alert(form);
	form.storeCode.value = code;
	form.storeName.value = desc;
} 

function getCustNameKeypress(e,custCode,fieldName){
	var form = document.orderForm;
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			if("storeCode" == fieldName){
				form.storeCode.value = '';
				form.storeName.value = "";
				form.storeNo.value = "";
				form.subInv.value = "";
			}
		}else{
		  getCustName(custCode,fieldName);
		}
	}
}

function getCustName(custCode,fieldName){
	var returnString = "";
	var form = document.orderForm;
	var storeGroup = form.custGroup.value;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/getCustNameWithSubInvAjax.jsp",
			data : "custCode=" + custCode.value+"&storeGroup="+storeGroup,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	
	if("storeCode" == fieldName){
		if(returnString !=''){
			var retArr = returnString.split("|");
			form.storeName.value = retArr[0];

		}else{
			alert("ไม่พบข้อมูล");
			form.storeCode.focus();
			form.storeCode.value ="";
			form.storeName.value = "";
			form.storeNo.value = "";
			form.subInv.value = "";
		}
	}
}
function resetStore(){
	var form = document.orderForm;
	var storeGrouptext = $("#custGroup option:selected").text();
	
	if(storeGrouptext != ''){
		form.storeCode.value = "";
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
	}
}
function openPopupProduct(path,types){
	var form = document.orderForm;
	var storeType ="";
	if("020058"==form.custGroup.value){
		storeType ='tops';
	}
	var param = "&types="+types+"&storeType="+storeType;
	url = path + "/jsp/searchProductPopupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setProductMainValue(code,desc,types){
	var form = document.orderForm;
	//alert(form);
	if("from" == types){
		form.pensItemFrom.value = code;
	}else{
		form.pensItemTo.value = code;
	}
} 
function openPopupGroup(path,selectOne){
	var form = document.orderForm;
	var storeType ="";
	if("020058"==form.custGroup.value){
		storeType ='tops';
	}
    var param = "&selectOne="+selectOne+"&storeType="+storeType;
	url = path + "/jsp/searchGroupPopupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setGroupMainValue(code,desc,types){
	var form = document.orderForm;
	form.groupCode.value = code;
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
				<jsp:param name="function" value="reportOrderBME"/>
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
						<html:form action="/jsp/orderAction">
						<jsp:include page="../error.jsp"/>
						
					    <div align="center">
					     <table align="center" border="0" cellpadding="3" cellspacing="0" >
					        <tr>
                                <td  align="right"> กลุ่มร้านค้า <font color="red">*</font></td>
								<td>
								     <html:select property="order.custGroup" styleId="custGroup" onchange="resetStore()">
										<html:options collection="custGroupList" property="code" labelProperty="desc"/>
								    </html:select>
								</td>
							</tr>
							<tr>
								<td  align="right">รหัสร้านค้า </td>
								<td>
								  <html:text property="order.storeCode" styleId="storeCode" size="20" onkeypress="getCustNameKeypress(event,this,'storeCode')"/>-
								  <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
								  <html:text property="order.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="40"/>
								
								  <html:hidden property="order.subInv" styleId="subInv"/>
								  <html:hidden property="order.storeNo" styleId="storeNo"/>
								</td>
							</tr>
							<tr>
								<td align="right">
								    วันที่ Order <font color="red">*</font>
								 </td>
								 <td>
								  <html:text property="order.salesDateFrom" styleId="salesDateFrom" size="20" readonly="true"/>
								 - 
								  <html:text property="order.salesDateTo" styleId="salesDateTo" size="20" readonly="true" />
								</td>
							</tr>
							<tr>
								 <td align="right">
								    Pens Item From
								 </td>
								 <td>
								  <html:text property="order.pensItemFrom" styleId="pensItemFrom" styleClass="\" autoComplete=\"off"/>
							      <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','from')"/>   
								 - 
								  &nbsp;&nbsp; <html:text property="order.pensItemTo" styleId="pensItemTo" styleClass="\" autoComplete=\"off"/>
							      <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','to')"/>   
								</td>
							</tr>
							<tr>
								 <td align="right">
								   Group Code
								 </td>
								 <td>
								    <html:text property="order.groupCode" styleId="groupCode" styleClass="\" autoComplete=\"off"/>
								    <input type="button" name="x1" value="..." onclick="openPopupGroup('${pageContext.request.contextPath}','selectOne')"/>
								</td>
							</tr>
							<tr>
								<td align="right">
								   Invoice No<font color="red"></font>
								</td>
								<td >
								  <html:text property="order.invoiceNo" styleId="invoiceNo" size="20" styleClass="\" autoComplete=\"off"/>
								 &nbsp;&nbsp; 
								 Order lot no 
								  <html:text property="order.orderLotNo" styleId="orderLotNo" size="20" styleClass="\" autoComplete=\"off"/>
								</td>
							</tr>
					   </table>
					   <br/>
					   <table  align="center" border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td >
									<a href="javascript:search('${pageContext.request.contextPath}')">
									  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
									</a>	
									<a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
									  <input type="button" value="Export To Excel" class="newPosBtnLong">
									</a>					
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									  <input type="button" value="   Clear   " class="newPosBtnLong">
									</a>		
									<a href="javascript:exportBarcodeToExcel('${pageContext.request.contextPath}')">
									  <input type="button" value="Export Barcode To Excel" class="newPosBtnLong">
									</a>			
								</td>
							</tr>
					  </table>
						
				     </div>
				     <br/>

					<!-- ************************Result ***************************************************-->
                    <br/>&nbsp;
					<!-- HEAD -->
			<% if( session.getAttribute("results") != null){  %>
					<!-- Page -->
					<% if(session.getAttribute("totalPage") != null){ 
					
					   int totalPage = ((Integer)session.getAttribute("totalPage")).intValue();
					   int totalRow = ((Integer)session.getAttribute("totalRow")).intValue();
					   int pageSize = OrderAction.reportOrderPageSize;
					   if(Utils.isNull(request.getAttribute("action")).equalsIgnoreCase("newsearch")){
						  pageNumber = 1;
					   }else{
					      pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
					   }
					   
					   start = ((pageNumber-1)*pageSize)+1;
					   end = (pageNumber * pageSize);
					   if(end > totalRow){
						   end = totalRow;
					   }
					   %>
					   
					<div align="left">
					   <span class="pagebanner">รายการทั้งหมด  <%=totalRow %> รายการ, แสดงรายการที่  <%=start %> ถึง  <%=end %>.</span>
					   <span class="pagelinks">
						หน้าที่ 
						 <% 
							 for(int r=0;r<totalPage;r++){
								 if(pageNumber ==(r+1)){
							 %>
			 				   <strong><%=(r+1) %></strong>
							 <%}else{ %>
							 
							    <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(r+1)%>')"  title="Go to page <%=(r+1)%>"> <%=(r+1) %></a>
							    
						 <% }} %>				
						</span>
					</div>
					<%} %>
					
					<table align="center" border="1" cellpadding="3" cellspacing="0" class="result">
		                <tbody>
		                  <tr>
		                  <th width="3%">Store Code</th>
		                  <th width="4%">Order Date</th>
		                  <th width="3%">Pens Item</th>
		                  <th width="5%">Group Code</th>
		                  <th width="5%">Material Master</th>
		                  <th width="3%">Barcode</th>
		                  <th width="3%">QTY</th>
		                  <th width="3%">Whole Price</th>
		                  <th width="3%">Retail Price</th>
		                  <th width="5%">Invoice No</th>
		                  <th width="5%">Order Lot No</th>
		                  </tr>
		               </tbody>    
					 <!-- HEAD -->
					 
					<!-- Content -->
					<% if(session.getAttribute("results") != null){
						List<Order> orderItemList =(List<Order>) session.getAttribute("results");
						
						
						int tabindex = 0;
						int no= start;
						String titleDisp ="";
						for(int i=0;i<orderItemList.size();i++){
						   Order o = (Order) orderItemList.get(i);
						  
						   String classStyle ="lineO";
						%>
					
						<tr class="<%=classStyle%>">
						    <td><%=o.getStoreCode()%></td>
						    <td><%=o.getOrderDate()%></td>
							<td><%=o.getItem()%></td>
							<td><%=o.getGroupCode()%></td>
							<td><%=o.getMaterialMaster()%></td>
							<td><%=o.getBarcode()%></td>
							<td><%=o.getQty()%></td>
							<td><%=o.getWholePriceBF()%></td>
							<td><%=o.getRetailPriceBF()%></td>
							<td><%=o.getInvoiceNo()%></td>
							<td><%=o.getOrderLotNo()%></td>
						 </tr>
						<%  
						 no++;
						   } //for 1
						%>
						</table>
						<%
						}//if 1
						
			}//if resultList != null	
						%>
					
                    <!-- ************************Result ***************************************************-->

					<jsp:include page="../searchCriteria.jsp"></jsp:include>
					
					<!-- hidden field -->
					<input type="hidden" name="pageNumber" id="pageNumber" value="<%=pageNumber%>"/>
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