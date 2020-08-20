
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.Constants"%>
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
<jsp:useBean id="budsAllForm" class="com.isecinc.pens.web.buds.BudsAllForm" scope="session" />
<%
String mode = budsAllForm.getMode(); 

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
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
}
function clearForm(path){
	var form = document.stockInvForm;
	form.action = path + "/jsp/barcodeAction.do?do=clearManualStock";
	form.submit();
	return true;
}
function printReport(path){
	var form = document.stockInvForm;
	form.action = path + "/jsp/barcodeAction.do?do=printReport";
	form.submit();
	return true;
}
function cancel(path){
	if(confirm("ยืนยันการยกเลิกรายการนี้")){
		var form = document.stockInvForm;
		form.action = path + "/jsp/barcodeAction.do?do=cancel";
		form.submit();
		return true;
	}
	return false;
}
function back(path){
	var form = document.stockInvForm;
	form.action = path + "/jsp/barcodeAction.do?do=search2&action=back";
	form.submit();
	return true;
}
function save(path){
	//alert(countSave);
	/** Check Save duplicate */
    
	var form = document.stockInvForm;
	var jobId =$('#jobId').val();
	var storeCode =$('#storeName').val();
	var storeNo =$('#storeNo').val();
	var subInv =$('#subInv').val();
	
	if(jobId ==""){
		alert("กรุณากรอก เลือก  รับคืนจาก");
		return false;
	}
	
	/**Control Save Lock Screen **/
	startControlSaveLockScreen();
	
	form.action = path + "/jsp/barcodeAction.do?do=saveManualStock";
	form.submit();
	return true;
}

function removeRow(){
	//todo play with type
	var tbl = document.getElementById('tblProduct');
	var chk = document.getElementsByName("linechk");
	var status = document.getElementsByName("status");
	var drow;
	var bcheck=false;
	for(var i=0;i<chk.length;i++){
		if(chk[i].checked){
			drow = tbl.rows[i+1];
			status[i].value ="DELETE";
			$(drow).hide();
			bcheck=true;
		}
	}
	if(!bcheck){
		alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;
	}
}
function checkAll(chkObj){
	var chk = document.getElementsByName("linechk");
	for(var i=0;i<chk.length;i++){
		chk[i].checked = chkObj.checked;
	}
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
				<jsp:param name="function" value="OrderEDI"/>
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
						<html:form action="/jsp/pickingAllAction">
						<jsp:include page="../../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Transaction Date</td>
									<td>					
										 <html:text property="bean.transactionDate" styleId="transactionDate" size="20" readonly="true" styleClass="disableText"/>
									</td>
									<td> </td>
									<td></td>
								</tr>
								
						   </table>
				 <!-- Table Data -->
				<c:if test="${pickingAllForm.bean.orderEDIBean.items != null}">
                  	    <c:if test="${pickingAllForm.bean.canEdit == true}">
	                        <div align="left">
								<input type="button" class="newPosBtn" value="เพิ่มรายการ" onclick="addRow();"/>	
								<input type="button" class="newPosBtn" value="ลบรายการ" onclick="removeRow();"/>	
							</div>
				        </c:if> 
				         		
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						    <tr>
							<!-- 	<th >No</th> -->
								<th ><input type="checkbox" name="chkAll" onclick="checkAll(this)"/></th>
								<th >PENS Item</th>
								<th >Material Master.</th>
								<th >Barcode</th>
								<th >Group Code</th>
								<th >ราคาขายส่งก่อน VAT</th>
								<th >ราคาขายปลีกก่อน VAT</th>	
								<th >QTY(จำนวนชิ้น)</th>						
							</tr>
							<c:forEach var="results" items="${pickingAllForm.bean.orderEDIBean.items}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
									<tr class="<c:out value='${tabclass}'/>">
										<td class="td_text_center" nowrap width="10%">
										  <input type="checkbox" name="linechk" value="${results.lineId}"/>
										  <input type="hidden" name="lineId" value="${results.lineId}" />
										  <input type="hidden" name="status" value="" />
										</td>
										<td class="td_text_center" width="10%">
										   <input type="text" name="pensItem" value ="${results.pensItem}" size="10" readonly class="disableText"/>
										</td>
										<td class="td_text_center" width="15%">
											<input type="text" name="materialMaster" value ="${results.materialMaster}" size="15" readonly class="disableText"/>
										</td>
										<td class="td_text_center" width="10%">
										   <input type="text" name="barcode" value ="${results.barcode}" size="20" readonly class="disableText"/>
										</td>
										<td class="td_text_center" width="10%">
										   <input type="text" name="groupCode" value ="${results.groupCode}" size="20" readonly class="disableText"/>
										</td>
										<td class='td_text_center' width='10%'>
										   <input type="text" name="wholePriceBF" value ="${results.wholePriceBF}" size="15" readonly class="disableNumber"/>
										</td>
										<td class='td_text_center' width='10%'>
										   <input type="text" name="retailPriceBF" value ="${results.retailPriceBF}" size="15" readonly class="disableNumber"/>
										</td>
										<td class='td_text_center' width='15%'>
										 <input type="text" name="qty" value ="${results.qty}" size="15"  class="enableNumber" onkeydown='return isNum(this,event)'/>
										</td>
									</tr>
							
							  </c:forEach>
					</table>
					
				</c:if>
					   <!-- Table Data -->
					   <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
									 <c:if test="${barcodeForm.job.canEdit == true}">
										<a href="javascript:save('${pageContext.request.contextPath}')">
										  <input type="button" value="    บันทึก      " class="newPosBtnLong"> 
										 </a>
									 </c:if>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									  <input type="button" value="   Clear   " class="newPosBtnLong">
									</a>	
														
									<a href="javascript:back('${pageContext.request.contextPath}','','add')">
									  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
									</a>	
									</td>
									<td align="right">
								  </td>
							</tr>
						</table>
					  </div>
					<!-- ************************Result ***************************************************-->
					<!-- hidden field -->
					 <input type="hidden" id="tabIndex" name="tabIndex" value="<%=%>"/>
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