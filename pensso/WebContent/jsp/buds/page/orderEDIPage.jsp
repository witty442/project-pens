<%@page import="com.isecinc.pens.bean.OrderEDIBean"%>
<%@page import="com.isecinc.pens.web.buds.BudsAllForm"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="budsAllForm" class="com.isecinc.pens.web.buds.BudsAllForm" scope="session" />

<%
OrderEDIBean bean = ((BudsAllForm)session.getAttribute("budsAllForm")).getBean().getOrderEDIBean();
System.out.println("ConfPickingBean:"+bean);
String role = ((User)session.getAttribute("user")).getType();
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
<%
	User user = (User)session.getAttribute("user");
	String screenWidth = Utils.isNull(session.getAttribute("screenWidth"));
	String screenHeight = Utils.isNull(session.getAttribute("screenHeight")); 
	String hideAll = "true"; 
	String pageName = Utils.isNull(request.getParameter("pageName"));
%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 
<script type="text/javascript">
function loadMe(){
}
function save(){
	var form = document.budsAllForm;
	var path = document.getElementById("path").value;
	if(!validateData()){ 
		alert("กรุณาระบุ  QTY ให้ถูกต้อง  ไม่สามารถเป็น 0 หรือ ช่องว่าง ได้");
		return false
	}
	/**Control Save Lock Screen **/
	startControlSaveLockScreen();
	
	form.action = path + "/jsp/budsAllAction.do?do=saveAction&method=saveOrderEDI";
	form.submit();
	return true;
}
//save and confirm
function confirmOrder(){
	var form = document.budsAllForm;
	var path = document.getElementById("path").value;
	if(!validateData()){ 
		alert("กรุณาระบุ  QTY ให้ถูกต้อง  ไม่สามารถเป็น 0 หรือ ช่องว่าง ได้");
		return false
	}
	/**Control Save Lock Screen **/
	startControlSaveLockScreen();
	
	form.action = path + "/jsp/budsAllAction.do?do=saveAction&method=confirmOrderEDI";
	form.submit();
	return true;
}
function cancelOrder(){
	var form = document.budsAllForm;
	var path = document.getElementById("path").value;
	if(confirm("ยืนยัน ยกเลิก Order นี้")){ 
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
		
		form.action = path + "/jsp/budsAllAction.do?do=saveAction&method=cancelOrderEDI";
		form.submit();
		return true;
	}
	return false;
}
function exportExcel(){
	var form = document.budsAllForm;
	var path = document.getElementById("path").value;
	form.action = path + "/jsp/budsAllAction.do?do=export";
	form.submit();
	return true;
}
function backToMain(){
	var form = document.budsAllForm;
	var path = document.getElementById("path").value;
	form.action = path + "/jsp/budsAllAction.do?do=searchHead&pageName=OrderEDISearch&subPageName=OrderEDISearch&action=back";
	form.submit();
	return true;
}
function removeRow(){
	//todo play with type
	var tbl = document.getElementById('tblProduct');
	var chk = document.getElementsByName("linechk");
	var status = document.getElementsByName("status");
	var lineId = document.getElementsByName("lineId");
	var deleteLineIds = document.getElementById("deleteLineIds");
	var drow;
	var bcheck=false;
	for(var i=0;i<chk.length;i++){
		if(chk[i].checked){
			//alert(status[i].value);
			if(status[i].value =="SUCCESS"){
				alert("ไม่สามารถลบรายการ สถานะ Success ได้ ");
				bcheck=true;
				chk[i].checked =false;//reset check
			}else if(status[i].value !="DELETE"){
				drow = tbl.rows[i+1];
				status[i].value ="DELETE";
				$(drow).hide();
				bcheck=true;
				//alert(lineId[i].value);
				if(lineId[i].value != 0){
					deleteLineIds.value = deleteLineIds.value+lineId[i].value+",";
				}
			}
		}
	}
	if(!bcheck){
		alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;
	}
}

function validateData(){
	var pass = true;
	var table = document.getElementById('tblProduct');
	var rows = table.getElementsByTagName("tr"); 
	if(rows.length ==1){
		return false;
	}
	var productCode = document.getElementsByName("productCode");
	var status = document.getElementsByName("status");
	var qty = document.getElementsByName("qty");
	
	//alert(itemIssue.length);
	
	var errorList = new Object();
    for(var i= 0;i<productCode.length;i++){
		var rowObj = new Object();
		var lineClass ="lineE";
		if(i%2==0){
			lineClass = "lineO";
		}
		//alert(itemIssue[i].value );
		
		if(productCode[i].value != "" && status[i].value !='DELETE'){
			if( qty[i].value =="" || qty[i].value =="0"){
			   rows[i+1].className ='lineError';
			   pass = false;
			}
		}
		
		//no error
		if(pass){
			rows[i+1].className =lineClass;
		}
		//alert("rows["+i+"]:"+rows[i].className);
	}// for
	return pass ;
} 
function calcAmount(no){
	
	var unitPrice = document.getElementsByName("unitPrice")[no-1];
	var qty = document.getElementsByName("qty")[no-1];
	var sumLineAmount = convetTxtObjToFloat(qty)*convetTxtObjToFloat(unitPrice);
	document.getElementsByName("lineAmount")[no-1].value = sumLineAmount;
	toCurrenyNoDigit(document.getElementsByName("lineAmount")[no-1]);
} 
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
			<jsp:include page="../../program.jsp">
				<jsp:param name="function" value="<%=pageName %>"/>
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
						<html:form action="/jsp/budsAllAction">
						<jsp:include page="../../error.jsp"/>
						
						<div id="div_message" style="font-size:15px;color:green" align="center"></div> 
						<div id="div_error_message" style="font-size:15px;color:red" align="center"></div> 
						
					<div id="div_m" align="center">	
				    	<!-- ***** Criteria ******* -->
				    	<table align="center" border="0" cellpadding="3" cellspacing="0" >
					        <tr>
				                <td> Cust Po Number</td>
								<td>
								    <html:text property="bean.orderEDIBean.orderNo" styleId="orderNo" size="13" readonly="true" /> 
								    &nbsp;Order Date:
								    <html:text property="bean.orderEDIBean.orderDate" styleId="orderDate" size="10" readonly="true" /> 
								   &nbsp;Status:
								    <html:text property="bean.orderEDIBean.docStatusDesc" styleId="docStatusDesc" size="15" readonly="true" /> 
								    <html:hidden property="bean.orderEDIBean.docStatus" styleId="docStatus" /> 
							        <html:hidden property="bean.orderEDIBean.headerId"/>
							        &nbsp;Picking No:
							         <html:text property="bean.orderEDIBean.pickingNo" styleId="pickingNo" size="15" readonly="true" /> 
							    </td>
							 </tr>
							 <tr>
				                <td > Customer Code</td>
								<td >
								    <html:text property="bean.orderEDIBean.customerCode" styleId="customerCode" size="15" styleClass="disableText" readonly="true"/> 
								     Customer Name :
								    <html:text property="bean.orderEDIBean.customerName" styleId="customerName" size="50" styleClass="disableText" readonly="true"/> 
							    </td>
							 </tr>
						</table>
					<br>
			  </div>
			
			     <!-- ****** RESULT *************************************************** -->
			     <c:if test="${budsAllForm.bean.orderEDIBean.itemsList != null}">
                  	    <c:if test="${budsAllForm.bean.orderEDIBean.canEdit == true}">
	                        <div align="left">
								<input type="button" class="newPosBtn" value="ลบรายการ" onclick="removeRow();"/>	
							</div>
				        </c:if> 
				         		
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						    <tr>
								<th ><!-- <input type="checkbox" name="chkAll" onclick="checkAll(this)"/> --></th>
								<th >รหัสสินค้า</th>
								<th >ชื่อสินค้า</th>
								<th >หน่วยนับ</th>
								<th >Qty</th>		
								<th >Unit Price</th>	
								<th >Amount (Exc. Vat)</th>					
							</tr>
							<% 
							String tabclass ="lineE";
							List<OrderEDIBean> resultList = budsAllForm.getBean().getOrderEDIBean().getItemsList();
							
							for(int n=0;n<resultList.size();n++){
								OrderEDIBean mc = (OrderEDIBean)resultList.get(n);
								tabclass ="lineE";
								if(n%2==0){
									tabclass="lineO";
								}
								
								tabclass = !Utils.isNull(mc.getRowStyle()).equals("")?mc.getRowStyle():tabclass;
								%>
								
									<tr class="<%=tabclass %>">
										<td class="td_text_center" nowrap width="10%">
										  <input type="checkbox" name="linechk" value="<%=mc.getLineId()%>"/>
										  <input type="hidden" name="lineId" value="<%=mc.getLineId()%>" /> 
										   <!-- for check delete on screen -->
										   <input type="hidden" name="status" value ="<%=mc.getStatus()%>" size="5" readonly class="disableText"/>
										   
										</td>
										<td class="td_text_center" width="10%">
										   <input type="text" name="productCode" value ="<%=mc.getProductCode()%>" size="10" readonly class="disableText"/>
										   <input type="hidden" name="productId" value="<%=mc.getProductId()%>" />
										</td>
										<td class="td_text_center" width="30%">
											<input type="text" name="productName" value ="<%=mc.getProductName()%>" size="50" readonly class="disableText"/>
										</td>
										<td class='td_text_center' width='10%'>
										   <input type="text" name="uom" value ="<%=mc.getUom()%>" size="5" readonly class="disableText"/>
										</td>
										
										<td class='td_text_center' width='10%'>
										  <input type="text" name="qty" value ="<%=mc.getQty()%>" size="10" 
										  autoComplete='off' class="enableNumber" 
										  onkeydown='return isNum(this,event)' onblur="calcAmount(<%=mc.getNo()%>)">
										</td>
										<td class='td_text_center' width='10%'>
										   <input type="text" name="unitPrice" value ="<%=mc.getUnitPrice()%>" size="5" readonly class="disableNumber"/>
		 								</td>
		 								<td class='td_text_center' width='10%'>
										   <input type="text" name="lineAmount" value ="<%=mc.getLineAmount()%>" size="5" readonly class="disableNumber"/>
		 								</td>
									</tr>
							 <%} %>
					</table>
			     </c:if>
			      <!-- ***************************************************************** -->
			      
                       <div align="center">
                         <c:if test="${budsAllForm.bean.orderEDIBean.canEdit == true}">
						    <input type="button" class="newPosBtn" value="บันทึก" onclick="save();"/>	&nbsp;&nbsp;
						    <input type="button" class="newPosBtn" value="ยืนยัน" onclick="confirmOrder();"/>&nbsp;&nbsp;	
						     <input type="button" class="newPosBtn" value="ยกเลิก Order" onclick="cancelOrder();"/>&nbsp;&nbsp;
						 </c:if> 
						 <input type="button" class="newPosBtn" value="ปิดหน้าจอ" onclick="backToMain()"/>
					  </div>
		         
				        
					<!-- hidden field -->
					<input type="hidden" name="pageName" value="<%=request.getParameter("pageName") %>"/>
					<input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
					<html:hidden property="bean.orderEDIBean.deleteLineIds" styleId="deleteLineIds"/>
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
   			<jsp:include page="../../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>

 <!-- Control Save Lock Screen -->
<jsp:include page="../../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->