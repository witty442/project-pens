<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
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
<jsp:useBean id="reqReturnWacoalForm" class="com.isecinc.pens.web.pick.ReqReturnWacoalForm" scope="session" />

<%
 String mode = reqReturnWacoalForm.getMode();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/pick_reqReturnWacoal.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
span.pagebanner {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	margin-top: 10px;
	display: block;
	border-bottom: none;
	font-size: 15px;
}

span.pagelinks {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	display: block;
	border-top: none;
	margin-bottom: -1px;
	font-size: 15px;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('requestDate'));

	 sumTotal();
}
function clearForm(path){
	var form = document.reqReturnWacoalForm;
	form.action = path + "/jsp/reqReturnAction.do?do=clear";
	form.submit();
	return true;
}

function cancel(path){
	if(confirm("ยืนยันการยกเลิกรายการนี้")){
		var form = document.reqReturnWacoalForm;
		form.action = path + "/jsp/reqReturnAction.do?do=cancel";
		form.submit();
		return true;
	}
	return false;
}

function back(path){
	var form = document.reqReturnWacoalForm;
	form.action = path + "/jsp/reqReturnAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function save(path){
	var form = document.reqReturnWacoalForm;
	var requestDate =$('#requestDate').val();
	
	if(requestDate ==""){
		alert("กรุณากรอก requestDate");
		return false;
	}
	
	if( !checkOneSelected()){
		alert("กรุณาเลือกรายการอย่างน้อย 1 รายการ");
		return false;
	}
	
	if(confirm("ยันยันการบันทึกข้อมูล")){
	   form.action = path + "/jsp/reqReturnAction.do?do=save";
	   form.submit();
	   return true;
	}
	return false;
}


function checkAll(chkObj){
	var chk = document.getElementsByName("linechk");
	for(var i=0;i<chk.length;i++){
		chk[i].checked = chkObj.checked;
	}
	
	sumTotal();
}
function checkOneSelected(){
	var chk = document.getElementsByName("linechk");
	var chkOne = false;
	for(var i=0;i<chk.length;i++){
		 if(chk[i].checked){
			 chkOne = true;
			 break;
		 }
	}
	return chkOne;
}

function sumTotal(chkObj){
	var chk = document.getElementsByName("linechk");
	var lineId = document.getElementsByName("lineId");
	var qty = document.getElementsByName("qty");
	
	var totalBoxTemp =0;
	var totalQtyTemp =0;
	for(var i=0;i<chk.length;i++){
		 if(chk[i].checked){
			 totalBoxTemp = totalBoxTemp+1;
			 totalQtyTemp += parseInt(qty[i].value);
			 
			 lineId[i].value = ""+(i+1);
		 } else{
			 lineId[i].value = "";
		 }
	}
	//alert(totalBoxTemp+","+totalQtyTemp);
	
	 $('#totalBox').val(totalBoxTemp);
	 $('#totalQty').val(totalQtyTemp);
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
				<jsp:param name="function" value="reqReturnWacoal"/>
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
						<html:form action="/jsp/reqReturnAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Request Date</td>
									<td>					
										<c:choose>
											<c:when test="${reqReturnWacoalForm.bean.canEdit == true}">
												 <html:text property="bean.requestDate" styleId="requestDate" size="20"/>
											</c:when>
											<c:otherwise>
												 <html:text property="bean.requestDate" styleId="requestDateX" size="20" styleClass="disableText" readonly="true"/>
											</c:otherwise>
									   </c:choose>
									</td>
									<td> 
									    Request No <html:text property="bean.requestNo" styleId="requestNo" size="20" styleClass="disableText"/>
									</td>
									<td nowrap>					
										สถานะ   
									  <html:text property="bean.statusDesc" styleId="status" size="20" styleClass="disableText"/>
									</td>
								</tr>
								<tr>
                                    <td> หมายเหตุ</td>
									<td colspan="3">
									<c:choose>
											<c:when test="${reqReturnWacoalForm.bean.canEdit == true}">
												 <html:text property="bean.remark" styleId="remark" size="80" />
											</c:when>
											<c:otherwise>
												 <html:text property="bean.remark" styleId="remark" size="80" styleClass="disableText" readonly="true"/>
											</c:otherwise>
									   </c:choose>
						              
									</td>
								</tr>	
								
						   </table>
						   
				 <!-- Table Data -->
				<c:if test="${reqReturnWacoalForm.results != null}">
		
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						    <tr>
							<!-- 	<th >No</th> -->
								<th ><input type="checkbox" name="chkAll" onclick="checkAll(this)"/></th>
								<th >เลขที่กล่อง</th>
								<th >Qty</th>
								<th >รับคืนจาก</th>
											
							</tr>
							<c:forEach var="results" items="${reqReturnWacoalForm.results}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
									<tr class="<c:out value='${tabclass}'/>">
										
										<td class="data_chk">
											<c:choose>
												<c:when test="${results.selected == 'true'}">
													 <input type="checkbox" name="linechk"  onclick="sumTotal()" checked/>		
												</c:when>
												<c:otherwise>
													 <input type="checkbox" name="linechk"  onclick="sumTotal()"/>		
												</c:otherwise>
											</c:choose>
								
										 							  
										  <input type="hidden" name="lineId" value="${results.lineId}" />
								
										</td>
										<td class="data_boxNo" align="center">${results.boxNo}
											<input type="hidden" name="boxNo" value ="${results.boxNo}" size="40" readonly class="disableText"/>
											
										</td>
										<td class="data_qty" align="center">${results.qty}
										   <input type="hidden" name="qty" value ="${results.qty}" size="20" readonly class="disableText"/>
										</td>
										<td class="data_jobName" align="left">${results.jobId}-${results.jobName}
										   <input type="hidden" name="jobId" value ="${results.jobId}" size="20" readonly class="disableText"/>
										</td>
										
									</tr>
							
							  </c:forEach>
					</table>
					
						 <div align="left">
							 รวมจำนวนกล่อง     &nbsp;&nbsp;: <input type="text" size="10" id ="totalBox" name ="bean.totalBox" class="disableNumber" value="" readonly/> กล่อง
						</div>
						 <div align="left">
							 รวมจำนวนที่จะคืน : <input type="text" size="10" id ="totalQty" name ="bean.totalQty" class="disableNumber" value="" readonly/> ชิ้น
						</div>
						
				</c:if>
						   <!-- Table Data -->
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										 <c:if test="${reqReturnWacoalForm.bean.canEdit == true}">
											<a href="javascript:save('${pageContext.request.contextPath}')">
											  <input type="button" value="    บันทึก      " class="newPosBtnLong"> 
											 </a>
										 </c:if>	
										<c:if test="${reqReturnWacoalForm.bean.canEdit == true}">
										   <c:if test="${reqReturnWacoalForm.mode == 'edit'}">
											 <a href="javascript:cancel('${pageContext.request.contextPath}')">
											   <input type="button" value="    ยกเลิก     " class="newPosBtnLong"> 
											 </a>
										   </c:if>
										 </c:if>		
										<a href="javascript:back('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
										</a>	
										
									</td>
								</tr>
							</table>
					  </div>
					<!-- ************************Result ***************************************************-->
					
					<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
					
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