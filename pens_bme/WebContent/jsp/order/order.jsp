<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.web.order.OrderAction"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.bean.StoreBean"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>

<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="orderForm" class="com.isecinc.pens.web.order.OrderForm" scope="session" />
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "orderForm");

List<StoreBean> storeList = null;
if(session.getAttribute("storeList") != null){
	storeList = (List<StoreBean>)session.getAttribute("storeList");
}
int start = 0;
int end = 0;
int pageNumber = 1;
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

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>

<!-- Control Save Lock Screen -->
 <jsp:include page="../controlSaveLockScreen.jsp"/> 
<!-- Control Save Lock Screen -->

<script type="text/javascript">

function loadMe(){
	 //new Epoch('epoch_popup', 'th', document.getElementById('orderDate'));
	 <%if(request.getAttribute("Message") != null){ %>
		 alert("<%=Utils.isNull(request.getAttribute("Message"))%>");
	 <%} %>
	 
	 //popup Order error
	 <%if(request.getSession().getAttribute("ORDER_ERROR") != null){ %>
	     var url = "${pageContext.request.contextPath}/jsp/order/orderError.jsp";
	     //$.blockUI();
	     PopupCenterFullHeightCustom(url,"Display Error",window.innerWidth-500,-100);
	 <%}%>
	 
	 <%if( Utils.isNull(request.getAttribute("validateStore")).equals("false")){ %>
        //alert("��ҹ���  Over Credit Limit ");
        deleteOrderInPage();
     <%} %>
     
     /** Sumit Load File Export Order TO Text All " **/
     <%if( !Utils.isNull(request.getAttribute("DOWNLOAD_FILE")).equals("")){ %>
         downloadFileOrderToTextAll('<%=Utils.isNull(request.getAttribute("DOWNLOAD_FILE"))%>');
     <%}%>
}

function unblockUI(){
	$.unblockUI();
}
function deleteOrderInPage(){
	var orderDate =$('#orderDate').val();
	var getData = $.ajax({
		url: "${pageContext.request.contextPath}/jsp/ajax/deleteOrderInPageAjax.jsp",
		data : "orderDate="+orderDate,
		async: true,
		cache: false,
		success: function(getData){
		  returnString = jQuery.trim(getData);
		}
	}).responseText;
}

function search(path){
	var form = document.orderForm;
	var orderDate =$('#orderDate').val();
	if(orderDate ==""){
		alert("��سҡ�͡�ѹ��� Order");
		return false;
	}
	form.action = path + "/jsp/orderAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}

function save(path){
	var form = document.orderForm;
	var orderDate =$('#orderDate').val();
	var pageNumber =$('#pageNumber').val();
	
	if(orderDate ==""){
		alert("��سҡ�͡�ѹ��� Order");
		return false;
	}
	
	/**Control Save Lock Screen **/
	startControlSaveLockScreen();
	
	form.action = path + "/jsp/orderAction.do?do=save&pageNumber="+pageNumber;
	form.submit();
	return true;
}

function gotoPage(path,pageNumber){
	var form = document.orderForm;
	
	//if(confirm("�������˹�ҹ�� �ж١�ѹ�֡ ��سҡ����������׹�ѹ��úѹ�֡ ����˹�ҶѴ� ")){
		var orderDate =$('#orderDate').val();
		var prevPageNumber =$('#pageNumber').val();

		if(orderDate ==""){
			alert("��سҡ�͡�ѹ��� Order");
			return false;
		}
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
		
		form.action = path + "/jsp/orderAction.do?do=search&action=save&pageNumber="+pageNumber+"&prevPageNumber="+prevPageNumber;
		form.submit();
		return true;
	//}
	//return false;
}

function exportToText(path){
	var form = document.orderForm;
	
	form.action = path + "/jsp/orderAction.do?do=exportToText";
	form.submit();
	return true;
}

function exportToTextAll(path){
	var form = document.orderForm;
	var returnString = "";
	/** warining Gen Text more than 1 time **/
	var getData = $.ajax({
		url: "${pageContext.request.contextPath}/jsp/ajax/getCountOrderGenTextAjax.jsp",
		data : "orderDate="+form.orderDate.value,
		async: false,
		cache: false,
		success: function(getData){
		  returnString = jQuery.trim(getData);
		}
	}).responseText;
	
	//alert(returnString);
	if(returnString != ''){
	    if(confirm("�ա�� Export To Text �ء��ҧ �����Ǩӹǹ "+returnString +"����   �׹�ѹ�ӡ�� Export")){
			/**Control Save Lock Screen **/
			startControlSaveLockScreen();
			
			form.action = path + "/jsp/orderAction.do?do=exportToTextAll";
			form.submit();
			return true;
	    }
	}else{
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
		
		form.action = path + "/jsp/orderAction.do?do=exportToTextAll";
		form.submit();
		return true;
	}
}
function downloadFileOrderToTextAll(fileName){
	var form = document.orderForm;
	var path = document.getElementById("path").value;
	form.action = path + "/jsp/orderAction.do?do=downloadFileOrderToTextAll&fileName="+fileName;
	form.submit();
	return true;
}

function exportToExcel(path){
	var form = document.orderForm;
	form.action = path + "/jsp/orderAction.do?do=exportToExcel";
	form.submit();
	return true;
}

function exportSummaryToExcel(path){
	var form = document.orderForm;
	form.action = path + "/jsp/orderAction.do?do=exportSummaryToExcel";
	form.submit();
	return true;
}
function exportSummaryAllToExcel(path){
	var form = document.orderForm;
	form.action = path + "/jsp/orderAction.do?do=exportSummaryAllToExcel";
	form.submit();
	return true;
}
function exportDetailToExcel(path){
	var form = document.orderForm;
	form.action = path + "/jsp/orderAction.do?do=exportDetailToExcel";
	form.submit();
	return true;
}

function clearForm(path){
	var form = document.orderForm;
	form.action = path + "/jsp/orderAction.do?do=prepare&action=new";
	form.submit();
	return true;
}

function chkQtyKeypress(obj,e,col,row){
	//alert(obj.value);
	if(e != null && e.keyCode == 13){
		return validateQty(obj,col,row);
	}
}

function validateQty(obj,col,row){
	var sumInRow = sumQtyInRow(row);
	validateQtyModel(obj,col,row,sumInRow);
}

function validateLimit(obj,col,row,sumInRow){
	var r = isNumPositive(obj);
	var wholePriceBF  = parseFloat($('#wholePriceBF_'+row).val());
	var limitAmt = parseFloat($('#limit_'+col+"_"+row).val());
	
	if(r && limitAmt >= 0){
		//validate Onhand Qty
		var currQty = parseInt(obj.value);
		var curAmt = wholePriceBF*currQty;
		
		//alert("onhandQty["+onhandQty+"],currQty["+currQty+"],sumInRowNotCurr["+sumInRowNotCurr+"],remainQty["+remainQty+"]");
		
		if(curAmt > limitAmt){
			alert("�������Թ Credit Limit");
			//obj.value = "";	
			
			obj.focus();
			return false;
		}
	}
	return true;
}

function validateQtyModel(obj,col,row,sumInRow){
	var r = isNumPositive(obj);
	var onhandQty =$('#onhandQty_'+row).val();
	if(r){
		//validate Onhand Qty
		var currQty = parseInt(obj.value);
		var sumInRowNotCurr = sumInRow - currQty;
		var remainQty = onhandQty - sumInRowNotCurr;
		var remainCalcQty = 0;
		//alert("onhandQty["+onhandQty+"],currQty["+currQty+"],sumInRowNotCurr["+sumInRowNotCurr+"],remainQty["+remainQty+"]");
		if(sumInRow > onhandQty){
			
			if(currQty > remainQty){
			   remainCalcQty  = remainQty ;
			}else{
			   remainCalcQty  = remainQty - currQty  ;
			}
			alert("�ӹǹ��� QTY("+sumInRow+") �Թ��ҹ��ء�Ң� ���ҡ���  Onhand QTY("+onhandQty+") �ӹǹ�������ö���� ("+remainCalcQty+")");
			obj.value = remainCalcQty;
			if(remainCalcQty==0){
				obj.value = "";	
			}
			obj.focus();
			return false;
		}
	}
	return true;
}

function sumQtyInRow(row){
   var maxColumns = $('#maxColumns').val();
   var sumQtyInRow = 0;
   var qty = 0;
   for(var k=0;k< maxColumns;k++){
	   var v = $('#qty_'+k+"_"+row).val();
	   //alert("c["+k+"]r["+row+"]:"+$('#qty_'+k+"_"+row).val());
	   if(v != ""){
          qty = parseInt($('#qty_'+k+"_"+row).val());
          sumQtyInRow += qty;
	   }
     }         
    return sumQtyInRow;
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
				<jsp:param name="function" value="Order"/>
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
						<% if(session.getAttribute("results") != null){ %> 
						    <div align="left">
						     <table align="left" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
									<td >
									     ��ҧ &nbsp;&nbsp; 
									     <html:select property="order.storeType">
											<html:options collection="storeTypeList" property="key" labelProperty="name"/>
									     </html:select>
									</td>
									<td>
									     ��������ҹ���&nbsp;&nbsp; 
									     <html:select property="order.billType">
											<html:options collection="billTypeList" property="key" labelProperty="name"/>
									     </html:select>
									</td>
									<td>
									    Group &nbsp;&nbsp; 
									    <html:text property="order.groupCode"  size="20" />
									</td>
								</tr>
								<tr>
									<td >
									     �Ҥ &nbsp;&nbsp; 
									     <html:select property="order.region">
											<html:options collection="regionList" property="key" labelProperty="name"/>
									     </html:select>
									</td>
									<td> �ѹ��� Order &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									  <html:text property="order.orderDate" styleId="orderDate" size="20" readonly="true" styleClass="disableText"/>
									  </td>
									 <td>
									   &nbsp;&nbsp;
									</td>
								</tr>
						   </table>
						      <br/><br/><br/><br/>
						   <table  align="left" border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ����      " class="newPosBtnLong"> 
										</a>						
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
						  </table>
					     </div>
					     
					     <br/>
					     <br/>
					     <br/> 
						<%}else{ %>
						   <div align="center">
						  
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
									<td >
									     ��ҧ &nbsp;&nbsp; 
									     <html:select property="order.storeType">
											<html:options collection="storeTypeList" property="key" labelProperty="name"/>
									     </html:select>
									</td>
									<td >
									     ��������ҹ���&nbsp;&nbsp; 
									     <html:select property="order.billType">
											<html:options collection="billTypeList" property="key" labelProperty="name"/>
									     </html:select>
									</td>
									<td>
									    Group &nbsp;&nbsp; 
									    <html:text property="order.groupCode"  size="20" />
									</td>
								</tr>
								<tr>
									<td >
									     �Ҥ &nbsp;&nbsp; 
									     <html:select property="order.region">
											<html:options collection="regionList" property="key" labelProperty="name"/>
									     </html:select>
									</td>
									<td> �ѹ��� Order &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									  <html:text property="order.orderDate" styleId="orderDate" size="20" readonly="true" styleClass="disableText"/>
									  </td>
									<td>
									   &nbsp;&nbsp; 
									</td>
								</tr>
								
						   </table>
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ����      " class="newPosBtnLong"> 
										</a>
										
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
						  
					  </div>
						<%} %>
						
					<% if(storeList != null && storeList.size() > 0 && session.getAttribute("results") != null){ %>
					<!-- BUTTON ACTION-->
					<table align="left" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						<tr>
							<td align="left" width="8%">
							    &nbsp;
							</td>
							<td align="left" width="92%">
								<a href="javascript:save('${pageContext.request.contextPath}')">
								  <input type="button" value="�ѹ�֡������" class="newPosBtnLong"> 
								</a>
								<a href="javascript:exportToTextAll('${pageContext.request.contextPath}')">
								     <input type="button" value="Export To Text �ء��ҧ" class="newPosBtnLong">
								</a>		
								<a href="javascript:exportSummaryToExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export Summary To Excel" class="newPosBtnLong">
								</a>
								<a href="javascript:exportDetailToExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export Detail To Excel" class="newPosBtnLong">
								</a>
								<a href="javascript:exportSummaryAllToExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export ��ػ�ʹ����ء��ҧ" class="newPosBtnLong">
								</a>
								<%-- <a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export To Excel" class="newPosBtnLong">
								</a> --%>						
							</td>
						</tr>
					</table>
				   <% } %>
				   
					<!-- ************************Result ***************************************************-->
                    <br/>&nbsp;
					<!-- HEAD -->
			<% if(storeList != null && storeList.size() > 0 && session.getAttribute("results") != null){  %>
					<!-- Page -->
					<% if(session.getAttribute("totalPage") != null){ 
					
					   int totalPage = ((Integer)session.getAttribute("totalPage")).intValue();
					   int totalRow = ((Integer)session.getAttribute("totalRow")).intValue();
					   int pageSize = OrderAction.pageSize;
					   if(Utils.isNull(request.getAttribute("action")).equalsIgnoreCase("newsearch")){
						  pageNumber = 1;
					   }else{
						   //Case Validate Error 
						   if( Utils.isNull(request.getAttribute("validateStore")).equals("false")){
							   pageNumber = Utils.convertStrToInt((String)request.getAttribute("prevPageNumber")); 
						   }else{
							   pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
						   }
					   }
					   
					   start = ((pageNumber-1)*pageSize)+1;
					   end = (pageNumber * pageSize);
					   if(end > totalRow){
						   end = totalRow;
					   }
					   %>
					   
					<div align="left">
					   <span class="pagebanner">��¡�÷�����  <%=totalRow %> ��¡��, �ʴ���¡�÷��  <%=start %> �֧  <%=end %>.</span>
					   
					   <span class="pagelinks">
						˹�ҷ�� 
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
					<table align="center" border="1" cellpadding="3" cellspacing="0" class="result2">
		                <tr>
		                  <th width="2%">No</th>
		                  <th width="3%">Group</th>
		                  <th width="4%">Size/ Color</th>
		                  <th width="3%">Item</th>
		                  <th width="5%">Onhand Qty</th>
		                  <th width="3%">�ҤҢ�� ��ա��͹ VAT</th>
		                 
		               <%
		                if(storeList != null && storeList.size()>0){ 
		               	  for(int k=0;k<storeList.size();k++){
		                        StoreBean s = (StoreBean)storeList.get(k);
		                        String storeColumnClass = s.getStoreStyle();
		                %>
		                         <th width="5%" class="<%=storeColumnClass%>"><%=s.getStoreCode() %> : <%=s.getStoreDisp() %></th>
		                <%
		                       }//for 2
		                   }//if 
		               
		                %>
		            </tr>     
					        
					 <!-- HEAD -->
					<!-- Content -->
					<% if(session.getAttribute("results") != null){
						List<Order> orderItemList =(List<Order>) session.getAttribute("results");
						Map<String,String> itemErrorMap = new HashMap<String, String>();
						if(session.getAttribute("itemErrorMap") != null){
							itemErrorMap = (Map)session.getAttribute("itemErrorMap");
						}
						
						/** Get session StoreNo can order group Code **/
						Map<String,String> canOrderMap = new HashMap<String,String>();
						if(session.getAttribute("canOrderMap") != null){
							canOrderMap = (Map)session.getAttribute("canOrderMap");
						}
						
						String key = "";
						String value ="";
						int tabindex = 0;
						int no = start;
						String titleDisp ="";
						for(int i=0;i<orderItemList.size();i++){
						   Order o = (Order) orderItemList.get(i);
						   titleDisp = o.getItemDesc()+" Onhand("+o.getOnhandQty()+")";
						  
						   String classStyle = (i%2==0)?"lineO":"lineE";
						
						   if(i%15==0 && i > 0){
						%>
						     <tr>
				                  <th width="2%">&nbsp;</th>
				                  <th width="3%">&nbsp;</th>
				                  <th width="4%">&nbsp;</th>
				                  <th width="3%">&nbsp;</th>
				                  <th width="5%">&nbsp;</th>
				                  <th width="3%">&nbsp;</th> 
					               <%
					                if(storeList != null && storeList.size()>0){ 
					               	  for(int k=0;k<storeList.size();k++){
					                        StoreBean s = (StoreBean)storeList.get(k);
					                        String storeColumnClass = s.getStoreStyle();
					                %>
					                         <th  width="5%" class="<%=storeColumnClass%>"><%=s.getStoreDisp() %></th>
					                <%
					                       }//for 2
					                   }//if 
					               
					                %>
					            </tr>    
						<%} %>
						<tr class="<%=classStyle%>">
						       <td><%=no%></td>
						       <td><input type="text" name="groupCode" value="<%=o.getGroupCode()%>" readonly size="10" class="disableText"></td>
						       <td><input type="text" name="itemDesc" value="<%=o.getItemDisp()%>" readonly size="4" class="disableText"></td>
						       <td><input type="text" name="item" value="<%=o.getItem()%>" readonly size="6" class="disableText"></td>
						       <td><input type="text" name="onhandQty_<%=i%>" id="onhandQty_<%=i%>"  readonly value="<%=o.getOnhandQty()%>" size="3" class="disableText"></td>	
						       <td><input type="text" name="retailPriceBF" value="<%=o.getRetailPriceBF()%>" readonly size="5" class="disableText">
						       
						       <input type="hidden" name="wholePriceBF" id="wholePriceBF_<%=i%>" value="<%=o.getWholePriceBF()%>" readonly size="5" class="disableText">
						       <input type="hidden" name="barcode" value="<%=o.getBarcode()%>" readonly size="12" class="disableText">
						       <!-- Hidden For get value -->
						       <input type="hidden" name="onhandQty"  value="<%=o.getOnhandQty()%>">
						      
						       </td>
						       <!--  For By Store -->
						        <%
						        //System.out.println("StoreListItemList Size:"+o.getStoreItemList().size());
						        if(o.getStoreItemList() != null && o.getStoreItemList().size()>0){ 
						        	String readOnly = "";
						        	String className = "";
						        	for(int c=0;c<o.getStoreItemList().size();c++){
						              StoreBean storeItem = (StoreBean)o.getStoreItemList().get(c);
						              
						              String storeColumnClass = storeItem.getStoreStyle();
						              
						             // System.out.println("Barcocde:"+o.getBarcode()+",storeCode:"+storeItem.getStoreCode()+",LimitAmt:"+storeItem.getLimitAmt());
						              
						              tabindex++;
						              //disp
						              //StoreBean storeDisp = (StoreBean)storeList.get(c);
						              
						                 
						               /**** Key for check canOrder ****************************/
						               key = o.getGroupCode()+"_"+storeItem.getCustGroup();
						               value = Utils.isNull(canOrderMap.get(key));
						               readOnly = "";
							           className = ""; 
						               if( !Utils.isNull(value).equals("")){
							               if(value.indexOf("ALL") != -1){
							            	   readOnly = "readonly";
							            	   className = "disableText";
							               }else if( Utils.stringInStringArr(storeItem.getStoreCode(), value.split("\\,"))){
							            	   readOnly = "readonly";
								               className = "disableText"; 
							               }
						               }

						             /*********************************************************/
						         %>
						              <td  class="<%=storeColumnClass%>">  
						                 <input type="text" name="qty_<%=c%>_<%=i%>" id="qty_<%=c%>_<%=i%>" 
						                        value="<%=Utils.isNull(storeItem.getQty())%>" tabindex="<%=tabindex%>" size="3" 
						                        onkeypress="chkQtyKeypress(this,event,'<%=c%>','<%=i%>')"
						                        onchange="validateQty(this,'<%=c%>','<%=i%>')"
						                        title="<%=titleDisp %>"
						                        <%=readOnly%> class="<%=className%>"
						                        />
						                        
						                 <%if( "storeError".equalsIgnoreCase(storeColumnClass)){ %>
						                   <input type="hidden" name="orderNo_<%=c%>_<%=i%>"  value="">
						                   <input type="hidden" name="barOnBox_<%=c%>_<%=i%>"  value="">
						                 <%}else{ %>
						                    <input type="hidden" name="orderNo_<%=c%>_<%=i%>"  value="<%=Utils.isNull(storeItem.getOrderNo())%>">
						                    <input type="hidden" name="barOnBox_<%=c%>_<%=i%>"  value="<%=Utils.isNull(storeItem.getBarOnBox())%>">
						                 <%} %>
						             </td>
						         <%
						             }//for 2
						           }//if  
						         %>
						        <!--  For By Store -->
						     </tr>
						
						<%  
						 no++;
						   } //for 1
						%>
						</table>
						<%
						}//if 1
						
			}//if StoreList != null	
						%>
					
                    <!-- ************************Result ***************************************************-->

					<jsp:include page="../searchCriteria.jsp"></jsp:include>
					
					<!-- hidden field -->
					<input type="hidden" name="maxColumns" id="maxColumns" value="<%=storeList!=null?storeList.size():0%>"/>
				    <input type="hidden" name="pageNumber" id="pageNumber" value="<%=pageNumber%>"/>
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
