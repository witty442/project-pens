<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="com.isecinc.pens.bean.MoveStockWarehouseBean"%>
<%@page import="com.isecinc.pens.bean.MoveWarehouse"%>
<%@page import="com.isecinc.pens.dao.JobDAO"%>
<%@page import="com.isecinc.pens.bean.ReqPickStock"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.bean.PickStock"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>

<jsp:useBean id="moveStockWarehouseForm" class="com.isecinc.pens.web.pick.MoveStockWarehouseForm" scope="session" />
<%
List<MoveStockWarehouseBean> resultList =(List<MoveStockWarehouseBean>) moveStockWarehouseForm.getResults();

if(session.getAttribute("wareHouseList") == null){
	List<References> wareHouseList = new ArrayList();
	References ref1 = new References("","");
	wareHouseList.add(ref1);
	wareHouseList.addAll(JobDAO.getWareHouseList("'W2','W3','W4','W5'"));
	
	session.setAttribute("wareHouseList",wareHouseList);
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>" ></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>" ></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 <%if(resultList != null && resultList.size() >0) {%>
	    sumQty();
	    sumOnhandQty();
	  <%}%>
	  if(document.getElementsByName('bean.materialMaster')[0].value != ''){
		  loadPensItemModel("<%=moveStockWarehouseForm.getBean().getPensItem()%>");
		  setOnhandQtyByPensItemAfterSave("<%=moveStockWarehouseForm.getBean().getPensItem()%>");
	  }
	  <%
	  if(request.getAttribute("Message") != null){
	  %>
	    alert("<%=(String)request.getAttribute("Message")%>");
	    //clear old data
		document.getElementsByName('bean.materialMaster')[0].value="";
		loadPensItemModel("");
		document.getElementsByName('bean.onhandQty')[0].value ="";
		$('#transferQty').val('');
	  <%}%>
}

function clearForm(path){
	var form = document.moveStockWarehouseForm;
	form.action = path + "/jsp/moveStockWarehouseAction.do?do=clear";
	form.submit();
	return true;
}
function back(path){
	var form = document.moveStockWarehouseForm;
	form.action = path + "/jsp/moveStockWarehouseAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function search(path){
	var form = document.moveStockWarehouseForm;
	var warehouseFrom =$('#warehouseFrom').val();
	
	 if(warehouseFrom ==""){
		alert("กรุณากรอก  Move From Warehouse ");
		$('#warehouseFrom').focus();
		return false;
	} 
	
	form.action = path + "/jsp/moveStockWarehouseAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}
function loadPensItem(e){
	if(e != null && e.keyCode == 13){
		loadPensItemModel('');
	}
}

function loadPensItemModel(pensItem){
		var pensItemList = document.getElementById('pensItem');
		var mat = document.getElementsByName('bean.materialMaster')[0];
		var warehouseFrom = document.getElementsByName('bean.warehouseFrom')[0];
		
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/getPensItemMoveStockWarehoseListAjax.jsp",
				data : "warehouse=" + warehouseFrom.value+"&mat="+mat.value+"&pensItem="+pensItem,
				async: false,
				success: function(getData){
					var returnString = jQuery.trim(getData);
					pensItemList.innerHTML=returnString;
					//alert(returnString);
				}
			}).responseText;
		});
	
}

function setOnhandQtyByPensItem(pensItem){
	//alert(pensItem.value);
	var pensItemTemp = pensItem.value;
	var onhandQty = pensItemTemp.split("|")[1]; 
	var pensItemStr = pensItemTemp.split("|")[0]; 
	
	if(onhandQty=='0'){
		document.getElementsByName('bean.onhandQty')[0].value ="";
		alert("ไม่สามารถ Transfer Stock PensItem["+pensItemStr+"]ได้  เนื่องจาก  ยอด onhand เป็น 0");
		pensItem.value = "";
	}else{
	  document.getElementsByName('bean.onhandQty')[0].value = onhandQty;
	}
}

/** display new onhand qty after save **/
function setOnhandQtyByPensItemAfterSave(pensItem){
	//alert(pensItem.value);
	var pensItemTemp =  document.getElementById('pensItem').value;
	var onhandQty = pensItemTemp.split("|")[1]; 
    document.getElementsByName('bean.onhandQty')[0].value = onhandQty;
}

function save(path){
	var form = document.moveStockWarehouseForm;

	var warehouseFrom =$('#warehouseFrom').val();
	var warehouseTo =$('#warehouseTo').val();
	
	var materialMaster =$('#materialMaster').val();
	var transferQty =$('#transferQty').val();
	
	if(warehouseFrom ==""){
		alert("กรุณากรอก Warehouse From");
		$('#warehouseFrom').focus();
		return false;
	}
	
	if(warehouseTo ==""){
		alert("กรุณากรอก Warehouse To");
		$('#warehouseTo').focus();
		return false;
	}
	
	if(warehouseFrom == warehouseTo){
		alert("ไม่สามารถย้ายเข้าคลังเดียวกันได้");
		$('#warehouseTo').focus();
		return false;
	}
	
	if(materialMaster ==""){
		alert("กรุณากรอก รุ่นสินค้า");
		$('#materialMaster').focus();
		return false;
	}
	if(pensItem ==""){
		alert("กรุณากรอก เลือก Pens Item");
		$('#pensItem').focus();
		return false;
	}
	/* if(onhandQty =="" && onhandQty != '0'){
		alert("ไม่่สามารถ ใช้ Pens Itemนี้ได้เนือ่งจากยอด Onhand เป็น 0");
		$('#pensItem').focus();
		return false;
	} */
	
	if(transferQty =="" && transferQty != '0'){
		alert("กรุณากรอก จำนวน");
		$('#transferQty').focus();
		return false;
	}
	
	if(confirm("กรุณายืนยันการโอนสินค้า")){
		form.action = path + "/jsp/moveStockWarehouseAction.do?do=save";
		form.submit();
		return true;
	}
	return false;
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

	function chkQtyKeypress(obj,e,row){
		//alert(obj.value);
		if(e != null && e.keyCode == 13){
			return validateQty(obj,row);
		}
	}

	function validateQty(obj,row){
		var table = document.getElementById('tblProduct');
		var rows = table.getElementsByTagName("tr"); 
		
		obj.value = currencyToNum(obj.value);
		var r = isNum(obj);
		var onhandQtyObj = document.getElementsByName("onhandQty");
		//alert(onhandQtyObj[row].value);
		if(r){
			//validate Onhand Qty
			var onhandQty = currencyToNum(onhandQtyObj[row].value);
			var currQty = currencyToNum(obj.value);
			if(currQty > onhandQty){
				alert("จำนวนรวม QTY("+currQty+") มีมากว่า  Onhand QTY("+onhandQty+")");
				//rows[row+1].className ="lineError";
				obj.value = "";
				obj.focus();
				return false;
			}
			sumQty();
		}
		return true;
	}

	function sumOnhandQty(){
		var onhandQtyObj = document.getElementsByName("onhandQty");
		
		var sumCurPageQty = 0;
		for(var i=0;i<onhandQtyObj.length;i++){
			if(onhandQtyObj[i].value != '')
				sumCurPageQty = sumCurPageQty + currencyToNum(onhandQtyObj[i].value);
		}
		document.getElementsByName("bean.totalOnhandQty")[0].value = currencyToNum(sumCurPageQty) ;
	}

	function sumQty(){
		var qtyObj = document.getElementsByName("transferQty");
		var totalQtyNotInCurPage = 0;
		if(document.getElementsByName("totalQtyNotInCurPage")[0].value !=""){
			totalQtyNotInCurPage = currencyToNum(document.getElementsByName("totalQtyNotInCurPage")[0].value);
		}
		
		//alert(totalQtyNotCurPage);
		var sumCurPageQty = 0;
		for(var i=0;i<qtyObj.length;i++){
			if(qtyObj[i].value != '')
				sumCurPageQty = sumCurPageQty + currencyToNum(qtyObj[i].value);
		}
		//cur Page
		document.getElementsByName("curPageQty")[0].value = currencyToNum(sumCurPageQty);
		//total All show
		//alert(totalQtyNotInCurPage +","+ sumCurPageQty);
		
		document.getElementsByName("bean.totalTransferQty")[0].value = currencyToNum(totalQtyNotInCurPage + sumCurPageQty) ;
		
	}

	function currencyToNum(str){
		str = str+"";
		var temp =  str.replace(/\,/g,''); //alert(r);
		return parseInt(temp);
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
				<jsp:param name="function" value="moveStockWarehouse"/>
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
						<html:form action="/jsp/moveStockWarehouseAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						        <tr>
                                  <td colspan="3" align="center"><font size="3"><b></b></font></td>
							   </tr>
							    <tr>
                                    <td align="right"> Move From Warehouse<font color="red">*</font></td>
                                      <td>
                                        <html:select property="bean.warehouseFrom" styleId="warehouseFrom" >
											<html:options collection="wareHouseList" property="key" labelProperty="name"/>
									    </html:select>
									     &nbsp;&nbsp;Move To Warehouse <font color="red">*</font>
									      <html:select property="bean.warehouseTo" styleId="warehouseTo" >
											 <html:options collection="wareHouseList" property="key" labelProperty="name"/>
									       </html:select>
                                     </td>
									
								</tr>     			
						        <tr>
                                    <td align="right"> รุ่นสินค้า(สีไซร์)<font color="red">*</font></td>
                                    <td>
                                        <html:text property="bean.materialMaster" styleId="materialMaster" onkeypress='loadPensItem(event)'/>
                                        &nbsp;&nbsp;PensItem :<font color="red">*</font>
                                        <html:select property="bean.pensItem" styleId="pensItem" onchange="setOnhandQtyByPensItem(this)" >
                                          
                                        </html:select>
                                         &nbsp;&nbsp;Onhand Qty :
                                        <html:text property="bean.onhandQty" styleId="onhandQty" readonly="true" styleClass="disableText" size="5"/>
                                        
                                          &nbsp;&nbsp;   จำนวน <font color="red">*</font> <html:text property="bean.transferQty" styleId="transferQty" onchange="isNum(this)"/>
                                     </td>
								</tr>     
								<%-- <tr>
                                    <td align="right">Group Code </td>
                                    <td>  <html:text property="bean.groupCodeSearch" styleId="groupCodeSearch" size="20"/>
                                          <a href="javascript:search('${pageContext.request.contextPath}')">
										     <input type="button" value=" แสดงข้อมูล   " class="newPosBtnLong"> 
										 </a>
										 <a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="    Clear      " class="newPosBtnLong"> 
								         </a>
                                    </td>
									<td align="left">
									      	
									</td>
									<td align="left"></td>
								</tr>     	 --%>	
								
						   </table>
					  </div>
					  
					  <!-- Result Table -->
		<%-- 			  	<c:if test="${moveStockWarehouseForm.results != null}">
								<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch" width="50%">
								     <tr>
								        <th >Group Code</th>
										<th >MaterialMaster</th>
										<th >Barcode</th>
										<th >Available Qty</th>
										<th >Transfer Qty</th>
									  </tr>
									<%
									
									int tabindex = 0;
									String titleDisp ="";
									for(int i=0;i<resultList.size();i++){
									   MoveStockWarehouseBean o = (MoveStockWarehouseBean) resultList.get(i);
									  // System.out.println("getLineItemStyle:"+o.getLineItemStyle());
									   
									   String classStyle = (i%2==0)?"lineO":"lineE";
									  
									%>
									      <tr class="<%=classStyle%>" id="<%=o.getLineItemId()%>"> 
									           
									            
									      	    <td class="td_text_center" width="10%"><%=o.getGroupCode()%></td>
												<td class="td_text_center" width="10%"><%=o.getMaterialMaster()%> </td>
												<td class="td_text_center" width="10%"><%=o.getBarcode() %></td>
												<td class="td_text_number" width="10%">
												    <input tabindex="-1" type="text" name="onhandQty" value ="<%=o.getOnhandQty()%>" size="20" class="disableNumber"/>
												</td>
												<td class="td_text_number" width="10%">
													  <input tabindex="1" type="text" name="transferQty" value ="<%=Utils.isNull(o.getTransferQty()) %>" size="20"  class="enableNumber"
													   onkeypress="chkQtyKeypress(this,event,<%=i%>)"
									                   onchange="validateQty(this,<%=i%>)"
													  />
													  
													  <input type="hidden" name="groupCode" value="<%=o.getGroupCode() %>"/>
													  <input type="hidden" name="materialMaster" value="<%=o.getMaterialMaster() %>"/>
													  <input type="hidden" name="barcode" value="<%=o.getBarcode() %>"/>
												</td>
										</tr>
									<%} %>
									
									<tr class=""> 
											<td class="data_barcode" colspan="3" align="right">รวมทั้งสิ้น :</td>
											<td class="data_onhandQty">
											   <html:text property="bean.totalOnhandQty" styleId="totalOnhandQty" size="20" styleClass="disableNumber"/>
											</td>
											<td class="data_qty">
											    <html:text property="bean.totalTransferQty" styleId="totalTransferQty" size="20" styleClass="disableNumber"/>
												 <input type="hidden" name="totalQtyNotInCurPage" id="totalQtyNotInCurPage" value=""/>
										         <input type="hidden" name = "curPageQty" id="curPageQty"/>
											</td>
									</tr>
							</table>
					</c:if> --%>
					  
					
					<div align="center"><p></p>
					   <!-- Table Data -->
					   <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="center">
									<a href="javascript:save('${pageContext.request.contextPath}')">
										<input type="button" value="    บันทึก      " class="newPosBtnLong"> 
									 </a>
									
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="    Clear    " class="newPosBtnLong"> 
								    </a>
										
								</td>
							</tr>
						</table>
				   </div>
		
					<!-- ************************Result ***************************************************-->
					<!-- hidden field -->
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