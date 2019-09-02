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

pageNameTemp = "TTSUPER_SalesTarget";

//System.out.println("pageNameTemp:"+pageNameTemp);
List<SalesTargetBean> productMKTList = null;
List<SalesTargetBean> salesrepList = null;
Map<String, SalesTargetBean> dataMap = null;
  if(  request.getSession().getAttribute("productMKTList") != null 
    && request.getSession().getAttribute("salesrepList") != null 
    && request.getSession().getAttribute("dataMap") != null ) {
	  
	  productMKTList =(List<SalesTargetBean>)request.getSession().getAttribute("productMKTList");
	  salesrepList = (List<SalesTargetBean>)request.getSession().getAttribute("salesrepList");
	  dataMap =(Map<String, SalesTargetBean>)request.getSession().getAttribute("dataMap");
  }

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

<script type="text/javascript">
//To disable f5
 $(document).bind("keydown", disableF5);
function disableF5(e) {
	if (e.which == 116) e.preventDefault(); 
} 

function loadMe(path){
	<%if(request.getSession().getAttribute("dataMap") != null ) {%>
	  sumTotalByAllRow();
	  sumTotalByAllCol();
	<%}%>
}
function backForm(path){
	var form = document.salesTargetForm;
	form.action = path + "/jsp/salesTargetAction.do?do=prepareSearch&action=back";
	form.submit();
	return true;
}

//Sum byRow
function sumTotalByRow(obj,col,row){
	//validate
    if(obj.value ==''){
    	obj.value ="0";
    }
	if( !isNumPositive(obj)){
		obj.value ='0';
	}
   var target_qty_bj ="";
   var target_amount_bbj ="";
   var price_obj ="";
	
   var target_qty_temp = 0;
   var price_temp = 0;
   var target_amount_temp = 0;
	
   var total_ctn_by_sales_calc = 0;
   var total_amount_by_sales_calc = 0;
   var maxColumns = $('#maxColumns').val();
   var sumQtyInRow = 0;
   var qty = 0;
   for(var col=0;col< maxColumns;col++){
	   target_qty_obj =document.getElementById('target_qty_'+col+"_"+row);
	   target_amount_obj =document.getElementById('target_amount_'+col+"_"+row);
	   price_obj = document.getElementById('price_'+col+"_"+row);
		
	   if(target_qty_obj.value != ''){
			// alert("target_qty:"+target_qty_obj.val()+",price:"+price_obj.val());
			//calc
		 	target_qty_temp = convetTxtObjToFloat(target_qty_obj);
			price_temp = convetTxtObjToFloat(price_obj);
			target_amount_temp = target_qty_temp*price_temp; 
			 
			//set value to screen
			target_amount_obj.value= target_amount_temp;
			 
			//sum All Row By SalesrepCode
			total_ctn_by_sales_calc += target_qty_temp;
			total_amount_by_sales_calc += target_amount_temp;
		 }
     }//for
     
   //display sumRowBySales
 	document.getElementById('total_ctn_by_sales_'+row).value = total_ctn_by_sales_calc;
 	document.getElementById('total_amount_by_sales_'+row).value=total_amount_by_sales_calc;

 	toCurrenyNoDigit(document.getElementById('total_ctn_by_sales_'+row));
 	toCurreny(document.getElementById('total_amount_by_sales_'+row));
 	
 	sumTotalByAllRow();
 	sumTotalByAllCol();
 	
 	//set curr qty =blank case value =0
 	if(obj.value =='0'){
 		obj.value ='';
 	}
}

//SummAllRow
function sumTotalByAllRow(){
   var target_qty_bj ="";
   var target_amount_bbj ="";
   var price_obj ="";
   var target_qty_temp = 0;
   var price_temp = 0;
   var target_amount_temp = 0;
	
   var total_ctn_by_sales_calc = 0;
   var total_amount_by_sales_calc = 0;
   var maxColumns = $('#maxColumns').val();
   var maxRows = $('#maxRows').val();
   var sumQtyInRow = 0;
   var qty = 0;
   for(var row=0;row< maxRows;row++){
	   for(var col=0;col< maxColumns;col++){
		   target_qty_obj =document.getElementById('target_qty_'+col+"_"+row);
		   target_amount_obj =document.getElementById('target_amount_'+col+"_"+row);
		   price_obj = document.getElementById('price_'+col+"_"+row);
			
		   if(target_qty_obj.value != ''){
				// alert("target_qty:"+target_qty_obj.val()+",price:"+price_obj.val());
				//calc
			 	target_qty_temp = convetTxtObjToFloat(target_qty_obj);
				price_temp = convetTxtObjToFloat(price_obj);
				target_amount_temp = target_qty_temp*price_temp; 
				 
				//set value to screen
				target_amount_obj.value= target_amount_temp;
				 
				//sum All Row By SalesrepCode
				total_ctn_by_sales_calc += target_qty_temp;
				total_amount_by_sales_calc += target_amount_temp;
			 }
	     }//for 2
	     
	    if(total_ctn_by_sales_calc != 0){
		   //display sumRowByRow
		 	document.getElementById('total_ctn_by_sales_'+row).value = total_ctn_by_sales_calc;
		 	document.getElementById('total_amount_by_sales_'+row).value=total_amount_by_sales_calc;
	
		 	toCurrenyNoDigit(document.getElementById('total_ctn_by_sales_'+row));
		 	toCurreny(document.getElementById('total_amount_by_sales_'+row));
	    }
	 	//reset by row
	 	total_ctn_by_sales_calc =0;
	 	total_amount_by_sales_calc=0;
   }//for 1
}

//SummAllRow
function sumTotalByAllCol(){
   var target_qty_bj ="";
   var target_amount_bbj ="";
   var price_obj ="";
   var target_qty_temp = 0;
   var price_temp = 0;
   var target_amount_temp = 0;
	
   var total_listprice_by_sku_calc = 0;
   var total_ctn_by_sku_calc = 0;
   var total_price_by_sku_calc = 0;
   
   var total_ctn_all = 0;
   var total_price_all = 0;
   var maxColumns = $('#maxColumns').val();
   var maxRows = $('#maxRows').val();
   var sumQtyInRow = 0;
   var qty = 0;
   for(var col=0;col< maxColumns;col++){
	   
       for(var row=0;row< maxRows;row++){
		   target_qty_obj =document.getElementById('target_qty_'+col+"_"+row);
		   target_amount_obj =document.getElementById('target_amount_'+col+"_"+row);
		   price_obj = document.getElementById('price_'+col+"_"+row);
			
		   if(target_qty_obj.value != ''){
				// alert("target_qty:"+target_qty_obj.val()+",price:"+price_obj.val());
				//calc
			 	target_qty_temp = convetTxtObjToFloat(target_qty_obj);
				price_temp = convetTxtObjToFloat(price_obj);
				target_amount_temp = target_qty_temp*price_temp; 
				 
				//sum All Row By SalesrepCode
				total_listprice_by_sku_calc += price_temp;
				total_ctn_by_sku_calc +=target_qty_temp;
				total_price_by_sku_calc +=target_amount_temp;
				
				//sum all
				total_ctn_all +=target_qty_temp;
				total_price_all +=target_amount_temp;
			 }
	     }//for 2
	     
	    //display sumRowByCol
	    if(total_listprice_by_sku_calc != 0){
		 	document.getElementById('total_listprice_by_sku_'+col).value = total_listprice_by_sku_calc;
		 	toCurrenyNoDigit(document.getElementById('total_listprice_by_sku_'+col));
	    }
	    if(total_ctn_by_sku_calc != 0){
		 	document.getElementById('total_ctn_by_sku_'+col).value = total_ctn_by_sku_calc;
		 	toCurrenyNoDigit(document.getElementById('total_ctn_by_sku_'+col));
	    }
	    if(total_price_by_sku_calc != 0){
		 	document.getElementById('total_price_by_sku_'+col).value = total_price_by_sku_calc;
		 	toCurrenyNoDigit(document.getElementById('total_price_by_sku_'+col));
	    }
	 	//reset by col
	 	total_listprice_by_sku_calc =0;
	 	total_ctn_by_sku_calc =0;
	 	total_price_by_sku_calc = 0;
   }//for 1
   
   if(total_ctn_all != 0){
	 	document.getElementById('total_ctn_all').value = total_ctn_all;
	 	toCurrenyNoDigit(document.getElementById('total_ctn_all'));
   }
   if(total_price_all != 0){
	 	document.getElementById('total_price_all').value = total_price_all;
	 	toCurrenyNoDigit(document.getElementById('total_price_all'));
   }
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
                           <input type="hidden" name="maxColumns" id="maxColumns" value="<%=productMKTList!=null?productMKTList.size():0%>"/>
                           <input type="hidden" name="maxRows" id="maxRows" value="<%=salesrepList!=null?salesrepList.size():0%>"/>
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
									   ภาคตามสายดูแล
									    <html:select property="bean.salesZone" styleId="salesZone"  disabled="true">
											<html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
									    </html:select> 
									</td>
								</tr>
								<tr>
                                    <td> แบรนด์  </td>
									<td colspan="3">
									    <html:text property="bean.brand" styleId="brand" size="10" readonly="true" styleClass="disableText"/>	
									   <%--  <html:text property="bean.brandName" styleId="brandName" readonly="true" styleClass="disableText" size="50"/>	 --%>					
									    &nbsp;&nbsp;&nbsp;
									     ประเภทขาย 
									   <html:select property="bean.custCatNo" styleId="custCatNo" styleClass="disableText" disabled="true">
											<html:options collection="CUSTOMER_CATEGORY_LIST" property="custCatNo" labelProperty="custCatDesc"/>
									    </html:select>
									</td>
								</tr>					
						   </table>
					  </div>
					<% 
					  if(request.getSession().getAttribute("productMKTList") != null 
					  && request.getSession().getAttribute("salesrepList") != null 
					  && request.getSession().getAttribute("dataMap") != null ) {
						 
					%>
					<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearchNoSetTH'>
						<tbody>
							<tr>
								<td class="td_text_center"><font color='#4d4dff'><b>รวบหีบ By MKT</b></font></td>
								<%
								double totalCTNMKT = 0;
								for(int i=0;i<productMKTList.size();i++){ 
									SalesTargetBean productBean = productMKTList.get(i);
									totalCTNMKT += Utils.convertStrToDouble(productBean.getTotalTargetQty());
								%>
								  <td class="td_number"><font color='#4d4dff'><b><%=productBean.getTotalTargetQty()%></b></font></td>
								<% }%>
								<!-- Total Ctn by MKT -->
								<td class="td_number_bold">
									  <input type="text" class="disableNumberBoldBlue" 
									 name="total_price_all_by_mkt" id="total_price_all_by_mkt"size="10" readonly
									 value="<%=Utils.decimalFormat(totalCTNMKT, Utils.format_current_no_disgit) %>"/>
								</td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td class="td_text_center"><font color='#4d4dff'><b>รวบบาท By MKT</b></font></td>
								<%
								double totalAmountMKT = 0;
								for(int i=0;i<productMKTList.size();i++){ 
									SalesTargetBean productBean = productMKTList.get(i);
									totalAmountMKT += Utils.convertStrToDouble(productBean.getTotalTargetAmount());
								%>
								  <td class="td_number"><font color='#4d4dff'><b><%=productBean.getTotalTargetAmount()%></b></font></td>
								<% }%>
								<!-- Total Ctn by MKT -->
								<td class="td_number_bold">
								    <input type="text" class="disableNumberBoldBlue" 
									 name="total_price_all_by_mkt" id="total_price_all_by_mkt"size="10" readonly
									 value="<%=Utils.decimalFormat(totalAmountMKT, Utils.format_current_2_disgit) %>"/>
								 </td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
						 <tr>
							<th class="td_text_center"rowspan="2">พนักงานขาย</th>
							<%	
							//Loop Product
							for(int i=0;i<productMKTList.size();i++){ 
								SalesTargetBean productBean = productMKTList.get(i);
							%>
							  <th class="td_text_center">
							     <%=productBean.getItemCode()%><br/>-<%=productBean.getItemName()%>
							  </th>
							<% }%>
									
							<th class="td_text_center" rowspan="2">ยอดรวมหีบ รายเซลส์</th>
							<th class="td_text_center" rowspan="2">ยอดรวมบาท รายเซลส์</th>
							<th class="td_text_center" rowspan="2">ยอดรวมรายเซลส์<br/> ของทุกแบรนด์</th>
							</tr>
							<tr>
							<%	
							//Loop Product ระบุเป้า(หีบ)
							for(int i=0;i<productMKTList.size();i++){ 
								SalesTargetBean productBean = productMKTList.get(i);
							%>
							  <th  class="td_text_center">ระบุเป้า(หีบ)</th>
							<% }%>
							</tr>
							<!-- ***********Row Detail********************* -->
							<%
							 String keyMap = "";
							 SalesTargetBean keyBean = null;
							 String targetQty ="";
							 String targetAmount = "";
							 long lineId = 0;
							 for(int row=0;row<salesrepList.size();row++){
								 SalesTargetBean salesrep = salesrepList.get(row);
							%>
								<tr class="lineE">
									<td class="td_text_center"><%=salesrep.getSalesrepCode() %></td>
									<%	
										//Loop Product ระบุเป้า(หีบ)
										for(int col=0;col<productMKTList.size();col++){ 
											SalesTargetBean productBean = productMKTList.get(col);
											keyMap = "target_qty_"+productBean.getItemCode()+"_"+salesrep.getSalesrepCode();
											keyBean = dataMap.get(keyMap);
											if(keyBean != null){
												targetQty = keyBean.getTargetQty();
												targetAmount = keyBean.getTargetAmount();
												lineId= keyBean.getLineId();
											}else{
												targetQty = "";
												targetAmount = "";
												lineId=0;
											}
									%>
									     <td class="td_number">
									        <div class="tooltip">	
									        <input type="text" size="5"  class="disableNumber" autoComplete="off"
									           name="target_qty_<%=productBean.getItemCode() %>_<%=salesrep.getSalesrepCode()%>"
									           id="target_qty_<%=col%>_<%=row%>"
									           onblur="isNum(this);sumTotalByRow(this,'<%=col%>','<%=row%>')" 
									           value="<%=targetQty%>" readonly  
									           />
									        <span class="tooltiptext"><%=salesrep.getSalesrepCode()%></span>
									        </div>
									       <!--  ,amount:   --> 
									        <input type="hidden" size="5"
									          name="target_amount_<%=productBean.getItemCode() %>_<%=salesrep.getSalesrepCode()%>"
									          id="target_amount_<%=col%>_<%=row%>"
									          value="<%=targetAmount %>" />  
									        <!--  ,price: -->
									        <input type="hidden" size="5"
									          name="price_<%=productBean.getItemCode() %>_<%=salesrep.getSalesrepCode()%>"
									          id="price_<%=col%>_<%=row%>"
									          value="<%=productBean.getPrice() %>" />
									         
									          <input type="hidden" size="1"
									          name="id_<%=productBean.getItemCode() %>_<%=salesrep.getSalesrepCode()%>"
									          id="id_<%=col%>_<%=row%>"
									          value="<%=salesrep.getId()%>" />
									         
									          <input type="hidden" size="1"
									          name="line_id_<%=productBean.getItemCode() %>_<%=salesrep.getSalesrepCode()%>"
									          id="line_id_<%=col%>_<%=row%>"
									          value="<%=lineId%>" />
									     </td>
									<%
									   }//for product
									%>
									<!-- Sum By Sales -->
									<td class="td_number">
									    <input type="text" class="disableNumberBlue" 
									    name="total_ctn_by_sales_<%=row%>" 
									    id="total_ctn_by_sales_<%=row%>"
									    size="10" readonly/>
									</td>
									<td class="td_number">
                                         <input type="text" class="disableNumberBlue" 
                                         name="total_amount_by_sales_<%=row%>" 
                                         id="total_amount_by_sales_<%=row%>" 
                                         size="10" readonly/>
									</td>
									<td class="td_number">
                                         <input type="text" class="disableNumberBlue" 
                                         name="total_amount_by_sales_allbrand_<%=row%>" 
                                         id="total_amount_by_sales_allbrand_<%=row%>" 
                                         size="11" readonly value="<%=salesrep.getTotalAmountBrandBySale() %>"/>
                                         <input type="hidden" 
                                           name="total_amount_by_sales_allbrand_o_<%=row%>" 
                                           id="total_amount_by_sales_allbrand_o_<%=row%>" 
                                           value="<%=salesrep.getTotalAmountBrandBySale() %>"/>
									</td>
								</tr>
							<%
							 }//for salesrep
							%>
							<!-- ***********Summary by Column ********************* -->
							<tr>
								<td class="td_text_right"><b>ราคา List Price</b></td>
								  <!-- Loop By ProductMKTList -->
									<%	
										for(int col=0;col<productMKTList.size();col++){ 
											SalesTargetBean productBean = productMKTList.get(col);
									%>
										<td class="td_number">
										    <input type="text" class="disableNumberBlue" 
										    name="total_listprice_by_sku_<%=col%>" 
										    id="total_listprice_by_sku_<%=col%>"
										    size="10" readonly/>
										</td>
									<%  }  %>
									<td class="td_number"></td>
									<td class="td_number"></td>
									<td class="td_number"></td>
							 </tr>
							 <tr>
								<td class="td_text_right"><b>รวมหีบ Sales By SKU</b></td>
								  <!-- Loop By ProductMKTList -->
									<%	
										for(int col=0;col<productMKTList.size();col++){ 
											SalesTargetBean productBean = productMKTList.get(col);
									%>
										<td class="td_number">
										    <input type="text" class="disableNumberBlue" 
										    name="total_ctn_by_sku_<%=col%>" 
										    id="total_ctn_by_sku_<%=col%>"
										    size="10" readonly/>
										</td>
									<%  }  %>
									<td class="td_number">
									  <input type="text" class="disableNumberBoldBlue" 
									    name="total_ctn_all" id="total_ctn_all"size="10" readonly/>
									</td>
									<td class="td_number"></td>
									<td class="td_number"></td>
							 </tr>
							 <tr>
								<td class="td_text_right" ><b>รวมบาท Sales By SKU</b></td>
								  <!-- Loop By ProductMKTList -->
									<%	
										for(int col=0;col<productMKTList.size();col++){ 
											SalesTargetBean productBean = productMKTList.get(col);
										
									%>
										<td class="td_number">
										    <input type="text" class="disableNumberBlue" 
										    name="total_price_by_sku_<%=col%>" 
										    id="total_price_by_sku_<%=col%>"
										    size="10" readonly/>
										</td>
									<%  }  %>
									<td class="td_number">
									  <input type="text" class="disableNumberBoldBlue" 
									    name="total_price_all" id="total_price_all"size="10" readonly/>
									</td>
									<td class="td_number"></td>
									<td class="td_number"></td>
							 </tr>
							<!-- ***********Summary by Column ********************* -->
							</tbody>
						</table>
					<%} %>
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
											  <input type="button" value=" ยืนยันรับเป้าแบรนด์รายเซลล์    " class="newPosBtnLong"> 
											</a>
										</c:if>
									</td>
									<td align="left" width="5%">
										<a href="javascript:backForm('${pageContext.request.contextPath}')">
										  <input type="button" value=" ปิดหน้าจอ  " class="newPosBtnLong">
										</a>
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
			
					<!-- ************************Result ***************************************************-->
					<!-- hidden field -->
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