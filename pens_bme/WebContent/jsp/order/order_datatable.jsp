<%@page import="com.isecinc.pens.web.order.OrderAction"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.bean.StoreBean"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
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
ImportDAO importDAO = new ImportDAO();
List<References> dataList = importDAO.getStoreTypeList();
pageContext.setAttribute("storeTypeList",dataList,PageContext.PAGE_SCOPE);

List<References> regionList = importDAO.getRegionList();
pageContext.setAttribute("regionList",regionList,PageContext.PAGE_SCOPE);

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

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css" title="currentStyle">
	@import "${pageContext.request.contextPath}/css/demo_page.css";
	@import "${pageContext.request.contextPath}/css/demo_table.css";
</style>
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/js/jquery.dataTables.js"></script>
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/js/ColVis.min.js"></script>
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/js/FixedColumns.js"></script>
<script type="text/javascript">
function loadMe(){
	 //new Epoch('epoch_popup', 'th', document.getElementById('orderDate'));
	 <%if(request.getAttribute("Message") != null){ %>
		 alert("<%=Utils.isNull(request.getAttribute("Message"))%>");
	 <%} %>
}

function search(path){
	var form = document.orderForm;
	var orderDate =$('#orderDate').val();
	if(orderDate ==""){
		alert("กรุณากรอกวันที่ Order");
		return false;
	}
	form.action = path + "/jsp/orderAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}

function save(path){
	var form = document.orderForm;
	var orderDate =$('#orderDate').val();
	if(orderDate ==""){
		alert("กรุณากรอกวันที่ Order");
		return false;
	}
	
	form.action = path + "/jsp/orderAction.do?do=save";
	form.submit();
	return true;
}

function gotoPage(path,pageNumber){
	var form = document.orderForm;
	
	//if(confirm("ข้อมูลในหน้านี้ จะถูกบันทึก กรุณากดปุ่มเพื่อยืนยันการบันทึก และไปหน้าถัดไป ")){
		var orderDate =$('#orderDate').val();
		if(orderDate ==""){
			alert("กรุณากรอกวันที่ Order");
			return false;
		}
		
		form.action = path + "/jsp/orderAction.do?do=search&action=save&pageNumber="+pageNumber;
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
	
	var r = isNum(obj);
	var onhandQty =$('#onhandQty_'+row).val();
	if(r){
		//validate Onhand Qty
		var currQty = parseInt(obj.value);
		var sumInRow = sumQtyInRow(row);
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
			alert("จำนวนรวม QTY("+sumInRow+") สินค้านี้ทุกสาขา มีมากว่า  Onhand QTY("+onhandQty+") จำนวนที่สามารถทำได้ ("+remainCalcQty+")");
			obj.value = remainCalcQty;
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

function isNum(obj){
  if(obj.value != ""){
	var newNum = parseInt(obj.value);
	if(isNaN(newNum)){
		alert('ให้กรอกได้เฉพาะตัวเลขเท่านั้น');
		obj.value = "";
		obj.focus();
		return false;
	}else{return true;}
   }
  return true;
}

</script>
</head>
<body id="dt_example" topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();" style="height: 100%;">


						<!-- BODY -->
						<html:form action="/jsp/orderAction">
						<jsp:include page="../error.jsp"/>
						<% if(session.getAttribute("results") != null){ %> 
						    <div align="left">
						<%}else{ %>
						    <div align="center">
						<%} %>
							<table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
									<td >
									     ห้าง &nbsp;&nbsp; 
									     <html:select property="order.storeType">
											<html:options collection="storeTypeList" property="key" labelProperty="name"/>
									     </html:select>
									</td>
								</tr>
								<tr>
									<td >
									     ภาค &nbsp;&nbsp; 
									     <html:select property="order.region">
											<html:options collection="regionList" property="key" labelProperty="name"/>
									     </html:select>
									</td>
								</tr>
								<tr>
									<td> วันที่ Order
									  <html:text property="order.orderDate" styleId="orderDate" size="20" readonly="true" styleClass="disableText"/></td>
								</tr>
	
						   </table>
					  </div>
					   <% if(session.getAttribute("results") != null){ %> 
						  <div align="left">
						<%}else{ %>
						  <div align="center">
						<%} %>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
								   
									<a href="javascript:search('${pageContext.request.contextPath}')">
									  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
									</a>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									  <input type="button" value="   Clear   " class="newPosBtnLong">
									</a>						
								</td>
							</tr>
						</table>
					</div>
					<% if(storeList != null && storeList.size() > 0){ %>
					<!-- BUTTON ACTION-->
					<table align="left" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
								<a href="javascript:save('${pageContext.request.contextPath}')">
								  <input type="button" value="บันทึกข้อมูล" class="newPosBtnLong"> 
								</a>
								<a href="javascript:exportToText('${pageContext.request.contextPath}')">
								  <input type="button" value="Export To Text" class="newPosBtnLong">
								</a>	
								<a href="javascript:exportSummaryToExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export Summary To Excel" class="newPosBtnLong">
								</a>
								<a href="javascript:exportDetailToExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export Detail To Excel" class="newPosBtnLong">
								</a>
								<%-- <a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export To Excel" class="newPosBtnLong">
								</a> --%>						
							</td>
						</tr>
					</table>
				   <% } %>
				   
					<!-- ************************Result ***************************************************-->
					<!-- HEAD -->
			<% if(storeList != null && storeList.size() > 0){  %>

					<div id="demo">
					<table align="center" border="0" cellpadding="0" cellspacing="0" class="display" id="example">
					                <thead>
					                <tr>
					                  <th width="2%">No</th>
					                  <th width="3%">Group</th>
					                  <th width="4%">Size/ Color</th>
					                  <th width="3%">Item</th>
					                  <th width="5%">Onhand Qty</th>
					                  <th width="3%">ราคาขาย ปลีกก่อน VAT</th>
					                 
					               <%
					                if(storeList != null && storeList.size()>0){ 
					               	  for(int k=0;k<storeList.size();k++){
					                        StoreBean s = (StoreBean)storeList.get(k);
					                %>
					                         <th width="5%"><%=s.getStoreDisp() %></th>
					                <%
					                       }//for 2
					                   }//if 
					               
					                %>
					                </tr>
					            </thead>    
					        
					        
					 <!-- HEAD -->
					<!-- Content -->
					<tbody>
					<% if(session.getAttribute("results") != null){
						List<Order> orderItemList =(List<Order>) session.getAttribute("results");
						Map<String,String> itemErrorMap = new HashMap<String, String>();
						if(session.getAttribute("itemErrorMap") != null){
							itemErrorMap = (Map)session.getAttribute("itemErrorMap");
						}
						
						int tabindex = 0;
						int no= 1;
						String titleDisp ="";
						for(int i=0;i<orderItemList.size();i++){
						   Order o = (Order) orderItemList.get(i);
						   titleDisp = o.getItemDesc()+" Onhand("+o.getOnhandQty()+")";
						  
						   String classStyle = itemErrorMap.get(o.getItem())!=null?itemErrorMap.get(o.getItem()):"lineO";
						   
						 %>
						<tr>
						       <td><%=no%></td>
						       <td><input type="text" name="groupCode" value="<%=o.getGroupCode()%>" readonly size="5" class="disableText"></td>
						       <td><input type="text" name="itemDesc" value="<%=o.getItemDisp()%>" readonly size="4" class="disableText"></td>
						       <td><input type="text" name="item" value="<%=o.getItem()%>" readonly size="6" class="disableText"></td>
						       <td><input type="text" name="onhandQty_<%=i%>" id="onhandQty_<%=i%>"  readonly value="<%=o.getOnhandQty()%>" size="3" class="disableText"></td>	
						       <td><input type="text" name="retailPriceBF" value="<%=o.getRetailPriceBF()%>" readonly size="5" class="disableText">
						       
						       <input type="hidden" name="wholePriceBF" value="<%=o.getWholePriceBF()%>" readonly size="5" class="disableText">
						       <input type="hidden" name="barcode" value="<%=o.getBarcode()%>" readonly size="12" class="disableText">
						       <!-- Hidden For get value -->
						       <input type="hidden" name="onhandQty"  value="<%=o.getOnhandQty()%>">
					
						       <!--  For By Store -->
						        <%if(o.getStoreItemList() != null && o.getStoreItemList().size()>0){ 
						        	for(int c=0;c<o.getStoreItemList().size();c++){
						              StoreBean storeItem = (StoreBean)o.getStoreItemList().get(c);
						             
						              tabindex++;
						              //disp
						              StoreBean storeDisp = (StoreBean)storeList.get(c);
						              
						         %>
						              <td>  
						                 <input type="text" name="qty_<%=c%>_<%=i%>" id="qty_<%=c%>_<%=i%>" 
						                        value="<%=Utils.isNull(storeItem.getQty())%>" tabindex="<%=tabindex%>" size="3" 
						                        onkeypress="chkQtyKeypress(this,event,'<%=c%>','<%=i%>')"
						                        onchange="validateQty(this,'<%=c%>','<%=i%>')"
						                        title="<%=titleDisp %>"
						                        />
						                 <input type="hidden" name="orderNo_<%=c%>_<%=i%>"  value="<%=Utils.isNull(storeItem.getOrderNo())%>">
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
						 </tbody>
						</table>
					 </div>
						<%
						}//if 1
						
			}//if StoreList != null	
						%>
					
          <!-- ************************Result ***************************************************-->

         <script type="text/javascript" charset="utf-8">
           <% if(session.getAttribute("results") != null){ %>
				$(document).ready(function() {
					
					 var oTable = $('#example').dataTable( {
						"sScrollY": "500px",
						"sScrollX": "100%",
				        
						"bScrollCollapse": true,
						"bPaginate": true,
						"sDom": 'C<"clear">lfrtip'
					} ); 
					
					/* var oTable = $('#example').dataTable( {
						"sScrollX": "100%",
						"sScrollXInner": "150%",
						"bScrollCollapse": true
					} ); */
					
					new FixedColumns( oTable );
					
				} );
			<% } %>
		 </script>
		
					<jsp:include page="../searchCriteria.jsp"></jsp:include>
					
					<!-- hidden field -->
					<input type="hidden" name="maxColumns" id="maxColumns" value="<%=storeList!=null?storeList.size():0%>"/>
					<input type="hidden" name="pageNumber" id="pageNumber" value="<%=pageNumber%>"/>
					</html:form>
					<!-- BODY -->
					
</body>
</html>