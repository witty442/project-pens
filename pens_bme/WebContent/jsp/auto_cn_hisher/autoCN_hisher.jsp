<%@page import="com.isecinc.pens.web.autocn.hisher.AutoCNHISHERBean"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="autoCNHISHERForm" class="com.isecinc.pens.web.autocn.hisher.AutoCNHISHERForm" scope="session" /> 

<%
User user = (User) request.getSession().getAttribute("user");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/field_size.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>

<script type="text/javascript">
function loadMe(){
  sumTotal();
}
function saveAction(path){
	var form = document.autoCNHISHERForm;
	if(confirm("ยืนยันบันทึกข้อมูล")){
	  form.action = path + "/jsp/autoCNHISHERAction.do?do=save";
	  form.submit();
	  return true;
	}
	return false;
}

function cancelAction(path){
	var form = document.autoCNHISHERForm;
	if(confirm("ยืนยันยกเลิกรายการ")){
	  form.action = path + "/jsp/autoCNHISHERAction.do?do=cancelAction";
	  form.submit();
	  return true;
	}
	return false;
}

function backAction(path){
	var form = document.autoCNHISHERForm;
	form.action = path + "/jsp/autoCNHISHERAction.do?do=search2&action=back";
	form.submit();
	return true;
}

function addRowAction(){
	// $('#myTable tr').length;
	var rows = $('#tblProduct tr').length-1;
	var rowId = rows;
	//alert("rowId["+rowId+"]");
	var tabclass='lineE';
	if(rowId%2==0){ 
		tabclass="lineO";
	}
	var rowData ="<tr class='"+tabclass+"'>"+
	    "<td class='td_text_center' width='5%'> "+
	     " <input type='checkbox' name='lineChk'>"+
	     " <input type='hidden' name='keyData' id='keyData' /> "+
	     " <input type='hidden' name='rowId' id='rowId' value='"+rowId+"' /> "+
	    "</td>"+
	    "<td class='td_text_center' width='10%'> "+
	    "  <input type='text' name='pensItem' id='pensItem' value='' size='10' "+
	    "    onkeypress = 'getProductKeypress(event,this,"+rowId+")'" +
	    "    autocomplete='off'/> "+
	    "  <input type='hidden' name='inventoryItemId' id='inventoryItemId' value=''/>"+
	    "</td>"+
	    "<td class='td_text' width='45%'> "+
	    "  <input type='text' name='itemName' id='itemName'  value='' size='60' readonly class='disableText'/> "+
	    "</td>"+
	    "<td class='td_number' width='10%'> "+
	    "  <input type='text' name='unitPrice' id='unitPrice'  value='' autocomplete='off' size='10' class='enableNumber' onblur='isNum2Digit(this);sumTotalInRow("+rowId+")' /> "+
	    "</td>"+
	    "<td class='td_number' width='14%'> "+
	    "  <input type='text' name='qty' id='qty'  value='' size='14' autocomplete='off' class='enableNumber' onblur='isNum(this);sumTotalInRow("+rowId+")'/> "+
	    "</td>"+
	    "<td class='td_number' width='16%'> "+
	    "  <input type='text' name='amount' id='amount'  value='' size='17' class='disableNumber' readonly/> "+
	    "</td>"+
	    "</tr>";

    $('#tblProduct').append(rowData);
    //set focus default
    var pensItem = document.getElementsByName("pensItem");
    pensItem[rowId].focus();
    
    //count Total rec
    document.getElementsByName("totalRec")[0].value =  1+convetTxtObjToFloat(document.getElementsByName("totalRec")[0]);
	toCurrenyNoDigit(document.getElementsByName("totalRec")[0]);
    
}
function sumTotalInRow(rowId){
	var qtyObj =document.getElementsByName("qty")[rowId];
	var qty = convetTxtObjToFloat(qtyObj);
	var unitPrice = convetTxtObjToFloat(document.getElementsByName("unitPrice")[rowId]);
	var amount = qty*unitPrice;
	document.getElementsByName("amount")[rowId].value = amount;
	toCurreny(document.getElementsByName("amount")[rowId]);
	
	sumTotal(); 
}
function sumTotal(){
	var chk = document.getElementsByName("lineChk");
	var qty = document.getElementsByName("qty");
	var amount = document.getElementsByName("amount");
	var keyData = document.getElementsByName("keyData");
    var totalQty = 0;
    var totalAmount = 0;
    var totalRec = 0;
	for(var i=0;i<chk.length;i++){
		if(keyData[i].value !='CANCEL'){
			totalQty += convetTxtObjToFloat(qty[i]);
			totalAmount +=convetTxtObjToFloat(amount[i]);
			totalRec++;
		}
	}
	document.getElementsByName("bean.totalQty")[0].value = totalQty;
	document.getElementsByName("bean.totalAmount")[0].value = totalAmount;
	document.getElementsByName("totalRec")[0].value = totalRec;
	
	toCurrenyNoDigit(document.getElementsByName("bean.totalQty")[0]);
	toCurreny(document.getElementsByName("bean.totalAmount")[0]);
	toCurrenyNoDigit(document.getElementsByName("totalRec")[0]);
} 
 function removeRowAction(path){
	if(confirm("ยืนยันลบข้อมูล")){
		var tbl = document.getElementById('tblProduct');
		var chk = document.getElementsByName("lineChk");
		//alert(chk.length);
		var drow;
		var bcheck=false;
		for(var i=chk.length-1;i>=0;i--){
			if(chk[i].checked){
				//alert(chk[i].checked);
				drow = tbl.rows[i+1];
				//$(drow).remove();
				bcheck=true;
				//hide row table
				removeRowByIndex(path,drow,i);
			}
		}
		if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}else{sumTotal();}
	}
} 
 function removeRowByIndex(path,drow,index){
    //alert(drow);
	//todo play with type	
	drow.style.display ='none';
	//set  staus = CANCEL 
	//alert(index);
	document.getElementsByName("keyData")[index].value ='CANCEL';
	
	sumTotal();
}
 function checkDupProduct(productCodeNew,curRowId){
	var pensItem = document.getElementsByName("pensItem");
	var keyData = document.getElementsByName("keyData");
	for(var i=0;i<pensItem.length;i++){
		if(keyData[i].value !='CANCEL' && i != curRowId && productCodeNew==pensItem[i].value){
			return true;
		}
	}
	return false;
 }
 function getProductKeypress(e,pensItemObj,rowId){
		//materialMaster groupCode pensItem wholePriceBF retailPriceBF
		//alert(barcode.value);
		
		var pensItem = document.getElementsByName("pensItem");
		var itemName = document.getElementsByName("itemName");
		var qty = document.getElementsByName("qty");
		var unitPrice = document.getElementsByName("unitPrice");
		var inventoryItemId = document.getElementsByName("inventoryItemId");
		
		if(e != null && e.keyCode == 13){
			if(pensItemObj.value ==''){
				itemName[rowId].value = '';
				unitPrice[rowId].value = '';
				qty[rowId].value = '';
				amount[rowId].value = '';
			}else{
				//check dup in page
				if(checkDupProduct(pensItemObj.value,rowId) == true){
					alert("ข้อมูล รหัส PensItem ["+pensItemObj.value +"] ซ้ำ");
					pensItemObj.value = "";
					itemName[rowId].value = '';
					unitPrice[rowId].value = '';
					qty[rowId].value = '';
					amount[rowId].value = '';
					return false;
				}
				
				var found = getProductModel(pensItemObj,rowId);
				if(found){
					//Set Prev row readonly 
					pensItem[rowId].className ="disableText";
					pensItem[rowId].readOnly = true;
					qty[rowId].focus();
				}
			}
		}
	}

	function getProductModel(pensItemObj,rowId){
		var found = false;
		var storeCode = document.getElementById("storeCode");
		
		var pensItem = document.getElementsByName("pensItem");
		var itemName = document.getElementsByName("itemName");
		var unitPrice = document.getElementsByName("unitPrice");
		var inventoryItemId = document.getElementsByName("inventoryItemId");
		
		var returnString = "";
		var form = document.barcodeForm;
		var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/auto_cn_hisher/getProductAjax.jsp",
				data : "pensItem=" + pensItemObj.value+"&storeCode="+storeCode.value,
				async: false,
				cache: false,
				success: function(getData){
				  returnString = jQuery.trim(getData);
				}
			}).responseText;
		   // alert("x:"+returnString);
			if(returnString==''){
				alert("ไม่พบข้อมูลสินค้า  "+pensItemObj.value);
				pensItemObj.focus();
				pensItem[rowId].value = '';
				itemName[rowId].value = '';
				unitPrice[rowId].value = '';
				inventoryItemId[rowId].value = '';
			}else{
				var s = returnString.split("|");
				itemName[rowId].value = s[0];
				unitPrice[rowId].value = s[1];
				inventoryItemId[rowId].value = s[2];
				found = true;
			}
		return found;
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
				<jsp:param name="function" value="AutoCNHISHER"/>
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
						<html:form action="/jsp/autoCNHISHERAction">
						<jsp:include page="../error.jsp"/>
                    <!-- head -->
                    <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
                                    <td align="right">  กลุ่มร้านค้า	</td>
									<td>	
									    <html:select property="bean.custGroup" styleId="custGroup" disabled="true" styleClass="disableText">
											<html:options collection="custGroupList" property="pensValue" labelProperty="pensDesc"/>
									    </html:select>	
									</td>
									<td>		
									   รหัสร้านค้า<font color="red"></font>
                                       <html:text property="bean.storeCode" styleId="storeCode" size="15"  disabled="true" styleClass="disableText"/>	
									  <html:text property="bean.storeName" styleId="storeName" size="30"  styleClass="disableText" readonly="true"/>
									</td>
								</tr>
								<tr>
                                    <td align="right">Job Id </td>
									<td colspan="2">		
										 <html:text property="bean.jobId" styleId="jobId" size="10"  onkeypress="getJobNameKeypress(event,this)"  disabled="true" styleClass="disableText"/>
									
									    <html:text property="bean.jobName" styleId="jobName" size="40" styleClass="disableText"  readonly="true"/>
									 &nbsp;&nbsp;Gr No  &nbsp;&nbsp; &nbsp;&nbsp;
									 <html:text property="bean.grNo" styleId="grNo" size="20"  disabled="true" styleClass="disableText"/>
									</td>
								</tr>
								<tr>
                                    <td align="right">Box No </td>
									<td colspan="2">
									 <html:text property="bean.boxNo" styleId="boxNo" size="20"  disabled="true" styleClass="disableText"/>
									</td>
								</tr>
						   </table>
                    <!-- table data -->
           
                      <%if(autoCNHISHERForm.getBean().isCanSave()) { %>
	                        <table id="tblProductDel" align="center" border="0" cellpadding="3" cellspacing="2"  width="100%">
	                         <tr><td >
	                             <input type="button" name="add" value="  เพิ่มข้อมูล  " onclick="addRowAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
	                             <input type="button" name="delete" value="  ลบข้อมูล  " onclick="removeRowAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
	                           </td></tr>
	                        </table>
                       <%} %>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
						       <tr>
						            <th >Select</th>
						            <th >Pens Item</th>
						            <th >Item Description</th>
									<th >Unit Price</th>
									<th >Qty</th>
									<th >Amount</th>
							   </tr>
							<% 
							String tabclass ="lineE";
							List<AutoCNHISHERBean> resultList = autoCNHISHERForm.getResultsSearch();
							String keyData = "";
							// Check disble text by view or verify
							boolean readonly = true;
							if(autoCNHISHERForm.getBean().isCanSave()){
								readonly = false;
							}
							
							if(resultList != null && resultList.size() >0){
							for(int n=0;n<resultList.size();n++){
								AutoCNHISHERBean mc = (AutoCNHISHERBean)resultList.get(n);
								if(n%2==0){ 
									tabclass="lineO";
								}
								keyData =mc.getPensItem();
								%>
								<tr class="<%=tabclass%>"> 
								    <td class="td_text_center" width="5%">
								       <input type="checkbox" name="lineChk" id="lineChk" 
								        value="<%=keyData%>" >
								       <input type="hidden" name="keyData" id="keyData" value="<%=mc.getKeyData() %>" />
								       <input type="hidden" name="rowId" id="rowId" value="<%=n%>" />
								    </td><!-- 1 -->
									<td class="td_text_center" width="10%">
									  <input type="text" name="pensItem" id="pensItem" readonly value="<%=mc.getPensItem() %>" size="10" class="disableText"/>
									  <input type="hidden" name="inventoryItemId" id="inventoryItemId" value="<%=mc.getInventoryItemId() %>" size="10"/>
									</td><!-- 1 -->
									<td class="td_text" width="45%">
									    <input type="text" name="itemName" id="itemName"  value="<%=mc.getItemName() %>" size="60" readonly class='disableText'/>
									</td><!-- 3 -->
								    <td class="td_number" width="10%">
								        <input type="text" name="unitPrice" id="unitPrice"  value="<%=mc.getUnitPrice() %>" size="10"
								         <%if(readonly){ %>
								             readonly class="disableNumber" 
								         <%}else{ %> 
								             class="enableNumber" 
								             onblur="isNum2Digit(this);sumTotalInRow(<%=n%>)"
								          <%} %>
								          />
                                    </td><!-- 4 -->
								    <td class="td_number" width="14%">
								        <input type="text" name="qty" id="qty"  value="<%=mc.getQty() %>" size="14" 
								         <%if(readonly){ %>
								             readonly class="disableNumber" 
								         <%}else{ %> 
								             class="enableNumber" 
								             onblur="isNum(this);sumTotalInRow(<%=n%>)"
								          <%} %>
								          />
                                    </td><!-- 5 -->
									<td class="td_number" width="16%">
                                       <input type="text" name="amount" id="amount" value="<%=mc.getAmount()%>" size="19" class="disableNumber" readonly/>
									</td><!-- 6 -->
								</tr>
							<%} } %> 
					</table>
					<table align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
						 <tr class="hilight_text">
					       <td class="td_text" width="60%" colspan="3">
					                   จำนวนรายการ&nbsp;
					          <input type="text" name="totalRec" readonly id="totalRec" value="" size="10"  class="disableNumberBold"/>
					       </td>
					       
					       <td class="td_text_right" width="10%">
					         Total
					       </td>
					        <td class="td_number_bold" width="14%">
					            <html:text property="bean.totalQty" styleId="totalQty" size="10"  readonly="true" styleClass="disableNumberBold"/>	
					        </td>
					        <td class="td_number_bold" width="16%">  
					           <html:text property="bean.totalAmount" styleId="totalAmount" size="15"  disabled="true" styleClass="disableNumberBold"/>
					        </td>  
					    </tr>
					</table>
	
				
		        <!-- ************************Result ***************************************************-->
	            <table id="tblProductBtn" align="center" border="0" cellpadding="3" cellspacing="2"  width="65%">
                         <tr><td align="center">
                            <%if(autoCNHISHERForm.getBean().isCanSave()) { %>
                                <input type="button" name="add" value="  Approve to Auto-CN  " onclick="saveAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
                            <%} %>
                            &nbsp;&nbsp;
                            <input type="button" name="delete" value="  ปิดหน้าจอ  " onclick="backAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/>
                           
                            <%if(autoCNHISHERForm.getBean().isCanCancel()) { %>
                              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                               <%-- <input type="button" name="cancel" value="  ยกเลิกรายการ  " onclick="cancelAction('${pageContext.request.contextPath}')" class="newPosBtnLong"/> --%>
                            <% }%>
                           </td></tr>
                   </table>
               </div>
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