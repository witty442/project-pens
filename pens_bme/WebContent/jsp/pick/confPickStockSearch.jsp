<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
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
<jsp:useBean id="confPickStockForm" class="com.isecinc.pens.web.pick.ConfPickStockForm" scope="session" />
<% 
if(session.getAttribute("statusIssueReqList2") == null){
	List<References> billTypeList = new ArrayList();
	References ref = new References("","");
	billTypeList.add(ref);
	billTypeList.addAll(PickConstants.getRequestStatusW2ListInPageConfPickStock());
	session.setAttribute("statusIssueReqList2",billTypeList);
}

if(session.getAttribute("custGroupList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(GeneralDAO.searchCustGroup( new PopupForm()));
	
	session.setAttribute("custGroupList",billTypeList);
}
if(session.getAttribute("wareHouseList2") == null){
	List<References> wareHouseList = new ArrayList();
	References ref1 = new References("","");
	wareHouseList.add(ref1);
	wareHouseList.addAll(PickConstants.getWareHouseList("'"+PickConstants.WAREHOUSE_W2+"','"+PickConstants.WAREHOUSE_W4+"'"));
	
	session.setAttribute("wareHouseList2",wareHouseList);
}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/conf_pick_stock.css" type="text/css" />
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
	 new Epoch('epoch_popup', 'th', document.getElementById('issueReqDate'));
	 new Epoch('epoch_popup', 'th', document.getElementById('statusDate'));
}
function clearForm(path){
	var form = document.confPickStockForm;
	form.action = path + "/jsp/confPickStockAction.do?do=clear2";
	form.submit();
	return true;
}

function search(path){
	var form = document.confPickStockForm;
	var issueReqDate =$('#issueReqDate').val();
	//var storeCode =$('#storeCode').val();
	/* if(issueReqDate ==""){
		alert("กรุณากรอกวันที่  Issue Req Date");
		return false;
	} */
	form.action = path + "/jsp/confPickStockAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}

function openView(path,documentNo,issueReqStatus){
	var form = document.confPickStockForm;
	form.action = path + "/jsp/confPickStockAction.do?do=prepare&mode=view&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus;
	form.submit();
	return true;
}

function openConfirm(path,documentNo,issueReqStatus){
	var form = document.confPickStockForm;
	form.action = path + "/jsp/confPickStockAction.do?do=prepare&mode=confirm&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus;
	form.submit();
	return true;
}

function openPopupCustomer(path,types,storeType){
	var form = document.confPickStockForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,storeNo,subInv,types){

	var form = document.confPickStockForm;
	//alert(form);
	form.storeCode.value = code;
	form.storeName.value = desc;
	
	if(storeNo=='' || subInv==''){
		if(storeNo==''){
			alert("ไม่พบข้อมูล Store no  ไม่สามารถทำงานต่อได้");
		}
		if(subInv==''){
			alert("ไม่พบข้อมูล Sub Inventory  ไม่สามารถทำงานต่อได้");
		}
		form.storeCode.value = '';
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
	}else{
	   form.storeNo.value = storeNo;
	   form.subInv.value = subInv;
	}
} 

function getCustNameKeypress(e,custCode,fieldName){
	var form = document.confPickStockForm;
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
	var form = document.confPickStockForm;
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
				
				if(retArr[1]=='' || retArr[2]==''){
					if(retArr[1]==''){
						alert("ไม่พบข้อมูล Store no  ไม่สามารถทำงานต่อได้");
					}
					if(retArr[2]==''){
						alert("ไม่พบข้อมูล Sub Inventory  ไม่สามารถทำงานต่อได้");
					}
					form.storeCode.value = '';
					form.storeName.value = "";
					form.storeNo.value = "";
					form.subInv.value = "";
				}else{
					form.storeNo.value = retArr[1];
					form.subInv.value = retArr[2];
				}
				
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
	var form = document.confPickStockForm;
	var storeGrouptext = $("#custGroup option:selected").text();
	
	if(storeGrouptext != ''){
		form.storeCode.value = "";
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
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
	      	
				<jsp:param name="function" value="confPickStock"/>
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
						<html:form action="/jsp/confPickStockAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td>
                                      Issue request Date 
                                     </td>
                                     <td><html:text property="bean.issueReqDate" styleId="issueReqDate" size="20" /></td>
									<td>						
									 Issue request No <html:text property="bean.issueReqNo" styleId="issueReqNo" size="20" />	  
									</td>
									 <td>
                                      Issue request status
                                      <html:select property="bean.status">
											<html:options collection="statusIssueReqList2" property="key" labelProperty="name"/>
									    </html:select>
                                     </td>
								</tr>
								<tr>
                                    <td> กลุ่มร้านค้า  </td>		
								    <td>
										<html:select property="bean.custGroup" styleId="custGroup" onchange="resetStore()">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
						           </td>
								   <td align="right"></td>
								   <td align="right"></td>
								</tr>
								<tr>
									<td >รหัสร้านค้า
									</td>
									<td align="left" colspan="2"> 
									  <html:text property="bean.storeCode" styleId="storeCode" size="20" onkeypress="getCustNameKeypress(event,this,'storeCode')"/>-
									  <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="30"/>
									
									 <html:hidden property="bean.subInv" styleId="subInv" />
						             <html:hidden property="bean.storeNo" styleId="storeNo" />
									</td>
									<td></td>
								</tr>
								<tr>
                                    <td> Status Date  </td>		
								    <td>
								      <html:text property="bean.statusDate" styleId="statusDate" size="20" />	  
						           </td>
								   <td align="right"> Warehouse
									      <html:select property="bean.wareHouse" styleId="wareHouse" >
											<html:options collection="wareHouseList2" property="key" labelProperty="name"/>
									    </html:select>
									    </td>
								   <td align="right"></td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
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

            <c:if test="${confPickStockForm.resultsSearch != null}">
                  	
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
									<th >Issue Req Date</th>
									<th >WareHouse</th>
									<th >Issue Req No</th>
									<th >Issue Req Status</th>
									<th >รหัสร้านค้า</th>
									<th >ชื่อร้านค้า</th>
									<th >Request Qty</th>
									<th >วันที่รับของ</th>
									<th >หมายเหตุ</th>
									<th >Action</th>					
							   </tr>
							<c:forEach var="results" items="${confPickStockForm.resultsSearch}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
									<tr class="<c:out value='${tabclass}'/>">
										<td class="search_issueReqDate">
										   ${results.issueReqDate}
										</td>
										<td class="search_issueReqDate">${results.wareHouse}</td>
										<td class="search_issueReqNo">${results.issueReqNo}</td>
										<td class="search_issueReqStatus">
											${results.statusDesc}
										</td>
										 <td class="search_storeCode">
										  ${results.storeCode}
										</td>
										 <td class="search_storeName">
										  ${results.storeName}
										</td>
										<td class="search_storeCode">
										  ${results.totalReqQty}
										</td>
										<td class="search_storeCode">
										  ${results.needDate}
										</td>
									    <td class="search_remark">
										  ${results.remark}
										</td>
										<td class="search_edit">
											 <c:if test="${results.canConfirm == false}">
												  <a href="javascript:openView('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.status}')">
												          ดู
												  </a>
											  </c:if>
											  <c:if test="${results.canConfirm == true}">
												  <a href="javascript:openConfirm('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.status}')">
												     Confirm
												  </a>
											  </c:if>
										</td>
									</tr>
							  </c:forEach>
					</table>
								
								
					<!-- BUTTON ACTION-->
					<div align="center">
						<table  border="0" cellpadding="3" cellspacing="0" >
							<tr><td>
														
								</td>
							</tr>
						</table>
					</div>
				</c:if>
				
				<!-- ************************Result ***************************************************-->
					
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