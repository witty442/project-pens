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
<jsp:useBean id="confFinishForm" class="com.isecinc.pens.web.pick.ConfFinishForm" scope="session" />

<%
 String mode = confFinishForm.getMode();
if(session.getAttribute("typeDispList") == null){
	List<References> billTypeList = new ArrayList();
	References ref = new References("GropCode","GroupCode");
	billTypeList.add(ref);
	ref = new References("Box","Box");
	billTypeList.add(ref);
	session.setAttribute("typeDispList",billTypeList);
}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/pick_confFinish.css" type="text/css" />
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
	 new Epoch('epoch_popup', 'th', document.getElementById('confirmDate'));

}
function clearForm(path){
	var form = document.confFinishForm;
	form.action = path + "/jsp/confFinishAction.do?do=clear";
	form.submit();
	return true;
}

function cancel(path){
	if(confirm("ยืนยันการยกเลิกรายการนี้")){
		var form = document.confFinishForm;
		form.action = path + "/jsp/confFinishAction.do?do=cancel";
		form.submit();
		return true;
	}
	return false;
}

function back(path){
	var form = document.confFinishForm;
	form.action = path + "/jsp/confFinishAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function viewDisp(path){
	var form = document.confFinishForm;
	form.action = path + "/jsp/confFinishAction.do?do=viewDisp";
	form.submit();
	return true;
}

function printGroupCodeBoxReport(path,requestNo){
	window.open(path + "/jsp/printGroupCodeBoxAction.do?do=prepare&requestNo="+requestNo, "Print GroupCode Box", "width=500,height=400,location=No,resizable=No");
}


function save(path){
	var form = document.confFinishForm;
	var requestDate =$('#requestDate').val();
	var confirmDate =$('#confirmDate').val();
	
	 if(confirmDate ==""){
		alert("กรุณากรอก confirmDate");
		$('#confirmDate').focus();
		return false;
	} 
	 
	if(checkCompareDate(requestDate,confirmDate) ==false){
		alert("วันที่ Confirm Date ต้องมากกว่า หรือเท่ากับ Request Date ");
		$('#confirmDate').focus();
		return false;
	}
	
	if(confirm("ยันยัน Confirm To Finishing")){
		form.action = path + "/jsp/confFinishAction.do?do=save";
		form.submit();
		return true;
	}
	return false;
}

function checkCompareDate(DateFrom, DateTo){
	if(DateFrom=='' || DateTo==''){return true;}
	DateFrom = DateFrom.split("/");
	starttime = new Date(DateFrom[2],DateFrom[1]-1,DateFrom[0]);

	DateTo = DateTo.split("/");
	endtime = new Date(DateTo[2],DateTo[1]-1,DateTo[0]);
	if((endtime-starttime) < 0){
		return false;
	}else{
		return true;
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
				<jsp:param name="function" value="confFinish"/>
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
						<html:form action="/jsp/confFinishAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Request Date</td>
									<td>					
										<html:text property="bean.requestDate" styleId="requestDate" size="20" styleClass="disableText" readonly="true"/>
									</td>
									<td> 
									    Request No <html:text property="bean.requestNo" styleId="requestNo" size="20" styleClass="disableText"/>
									</td>
									<td> 
									    Confirm Date
									      <c:if test="${confFinishForm.bean.canEdit == true}">
									          <font color="red">*</font><html:text property="bean.confirmDate" styleId="confirmDate" size="20" styleClass=""/>
									      </c:if>
									     <c:if test="${confFinishForm.bean.canEdit == false}">
									         <html:text property="bean.confirmDate" styleId="confirmDate" size="20" styleClass="disableText"/>
									      </c:if>
									</td>
									<td nowrap>					
										สถานะ   
									  <html:text property="bean.statusDesc" styleId="status" size="15" styleClass="disableText"/>
									</td>
								</tr>
								<tr>
                                    <td colspan="3">การแสดงข้อมูล 
                                       <html:select property="bean.typeDisp">
											<html:options collection="typeDispList" property="key" labelProperty="name"/>
									    </html:select>
									    
                                    
                                        <a href="javascript:viewDisp('${pageContext.request.contextPath}')">
										  <input type="button" value="   แสดง   " class="newPosBtnLong">
										</a>
                                    </td>
                                    <td align="left">
                                        Warehouse
										      <html:select property="bean.wareHouse" styleId="wareHouse" disabled="true">
												<html:options collection="wareHouseList2" property="key" labelProperty="name"/>
										    </html:select>
                                    </td>
                                </tr>
						   </table>
						   
				 <!-- Table Data -->
				<c:if test="${confFinishForm.results != null}">
		
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						    <tr>
								<th >ลำดับที่</th>
								<c:choose>
									<c:when test="${confFinishForm.bean.typeDisp == 'Box'}">
										<th >Box </th>
									</c:when>
									<c:otherwise>
										<th >Group Code</th>
									</c:otherwise>
							      </c:choose>  
								<th >Qty</th>
							</tr>
							<c:forEach var="results" items="${confFinishForm.results}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
									<tr class="<c:out value='${tabclass}'/>">
										
										<td class="data_no"> ${results.no}</td>
										
										 <c:choose>
											<c:when test="${confFinishForm.bean.typeDisp == 'Box'}">
												<td class="data_boxNo"> ${results.boxNo}</td>
											</c:when>
											<c:otherwise>
												<td class="data_groupCode"> ${results.groupCode}</td>
											</c:otherwise>
									      </c:choose>      
										
										<td class="data_qty" align="center">
										   ${results.qty}
										   <input type="hidden" name="qty" value ="${results.qty}" size="20" readonly class="disableText"/>
										</td>
									</tr>
							  </c:forEach>
					</table>
					<br/>
						<div align="left"><b>
							 รวมจำนวนกล่อง      &nbsp;&nbsp;:&nbsp;  <html:text property="bean.totalBox" styleId="totalBox" size="10" styleClass="disableText"/>
							 กล่อง
						   </b>
						</div><br/>
						 <div align="left"><b>
							 รวมจำนวนชิ้น &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp; <html:text property="bean.totalQty" styleId="totalQty" size="10" styleClass="disableText"/> ชิ้น
						    </b>
						</div>
						
				</c:if>
						   <!-- Table Data -->
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									     <c:if test="${confFinishForm.bean.canPrint == true}">
											<a href="javascript:printGroupCodeBoxReport('${pageContext.request.contextPath}','${confFinishForm.bean.requestNo}')">
											  <input type="button" value=" พิมพ์ใบปะกล่อง " class="newPosBtnLong"> 
											 </a>
										 </c:if>	
										 
										 <c:if test="${confFinishForm.bean.canEdit == true}">
											<a href="javascript:save('${pageContext.request.contextPath}')">
											  <input type="button" value="    บันทึก      " class="newPosBtnLong"> 
											 </a>
										 </c:if>
										 	
	
										<a href="javascript:back('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
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