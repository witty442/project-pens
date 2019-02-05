<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<jsp:useBean id="confPickStockForm" class="com.isecinc.pens.web.pick.ConfPickStockForm" scope="session" />
<% 
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

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
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

function openView(path,documentNo,issueReqStatus,wareHouse){
	var form = document.confPickStockForm;
	form.action = path + "/jsp/confPickStockAction.do?do=prepare&mode=view&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus+"&wareHouse="+wareHouse;
	form.submit();
	return true;
}

function openEdit(path,documentNo,issueReqStatus,wareHouse){
	var form = document.confPickStockForm;
	form.action = path + "/jsp/confPickStockAction.do?do=prepare&mode=edit&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus+"&wareHouse="+wareHouse;
	form.submit();
	return true;
}
function openConfirm(path,documentNo,issueReqStatus,wareHouse){
	var form = document.confPickStockForm;
	form.action = path + "/jsp/confPickStockAction.do?do=prepare&mode=confirm&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus+"&wareHouse="+wareHouse;
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

function printBillMiniAll(path){
	var form = document.confPickStockForm;
	if(!isSelectOne()){
		alert("กรุณาเลือกอย่างน้อย 1 รายการ");
		return false;
	}
	form.action = path + "/jsp/confPickStockAction.do?do=printBillMiniAll";
	form.submit();
	return true;
}

function checkAll(chkObj){
	var chk = document.getElementsByName("linechk");
	for(var i=0;i<chk.length;i++){
		chk[i].checked = chkObj.checked;
	}
}

function isSelectOne(){
	var selected = false;
	var chk = document.getElementsByName("linechk");
	for(var i=0;i<chk.length;i++){
		if(chk[i].checked){
			selected = true;
			break;
		}
	}
	return selected;
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
                                     <td colspan="3">
                                     <html:text property="bean.issueReqDate" styleId="issueReqDate" size="20"  styleClass="\" autoComplete=\"off"/>
									 Issue request No <html:text property="bean.issueReqNo" styleId="issueReqNo" size="20" />	  
									
                                      Issue request status
                                      <html:select property="bean.status">
											<html:options collection="statusIssueReqList2" property="key" labelProperty="name"/>
									    </html:select>
                                     </td>
								</tr>
								<tr>
                                    <td> กลุ่มร้านค้า  </td>		
								    <td colspan="3">
										<html:select property="bean.custGroup" styleId="custGroup" onchange="resetStore()">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
									    &nbsp;&nbsp;&nbsp;
									    Invoice No &nbsp;<html:text property="bean.invoiceNo" styleId="invoiceNo" size="20" />
						           </td>
						
								</tr>
								<tr>
									<td >รหัสร้านค้า
									</td>
									<td align="left" colspan="2"> 
									  <html:text property="bean.storeCode" styleId="storeCode" size="20" 
									  onkeypress="getCustNameKeypress(event,this,'storeCode')"  styleClass="\" autoComplete=\"off"/>-
									  <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="60"/>
									
									 <html:hidden property="bean.subInv" styleId="subInv" />
						             <html:hidden property="bean.storeNo" styleId="storeNo" />
									</td>
									<td></td>
								</tr>
								<tr>
                                    <td> Status Date  </td>		
								    <td colspan="2">
								      <html:text property="bean.statusDate" styleId="statusDate" size="20"  styleClass="\" autoComplete=\"off"/>	  
						            Warehouse
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
										 <a href="javascript:printBillMiniAll('${pageContext.request.contextPath}')">
									       <input type="button" value="พิมพ์ใบเดินบิล (รวม)" class="newPosBtnLong"> 
									    </a>
									</td>
									</tr>
							</table>
					  </div>
            <c:if test="${confPickStockForm.resultsSearch != null}">
                  	
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearchNoWidth" width="100%">
						       <tr>
						            <th> <input type="checkbox" name="chkAll" onclick="checkAll(this)"/> </th>
									<th >Issue Req Date</th>
									<th >W/H</th>
									<th >Issue Req No</th>
									<th >Issue Req Status</th>
									<th >รหัสร้านค้า</th>
									<th >ชื่อร้านค้า</th>
									<th >Request Qty</th>
									<th >Issued Qty</th>
									<th >วันที่รับของ</th>
									<th >หมายเหตุ</th>
									<th >วันที่พร้อมจัดส่ง</th>
									<th >รวมจำนวนหีบ</th>
								    <th >Action</th>	
									<th >Confirm</th>				
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
									     <td class="td_text_center" width="2%">
										  <input type="checkbox" name="linechk" value="${results.issueReqNo}"/>
										</td>
										<td class="td_text_center" width="8%">
										   ${results.issueReqDate}
										</td>
										<td class="td_text_center" width="5%">${results.wareHouse}</td>
										<td class="td_text_center" width="8%">${results.issueReqNo}</td>
										<td class="td_text_center" width="5%">
											${results.statusDesc}
										</td>
										 <td class="td_text_center" width="8%">
										  ${results.storeCode}
										</td>
										 <td class="td_text" width="15%">
										  ${results.storeName}
										</td>
										<td class="td_text_center" width="5%">
										  ${results.totalReqQty}
										</td>
										<td class="td_text_center" width="5%">
										  ${results.totalIssueQty}
										</td>
										<td class="td_text_center" width="8%">
										  ${results.needDate}
										</td>
									    <td class="td_text_center" width="15%">
										  ${results.remark}
										</td>
										<td class="td_text_center" width="6%">
										  ${results.deliveryDate}
										</td>
										<td class="td_text_center" width="5%">
										<c:if test="${results.totalCtn != 0}">
										  ${results.totalCtn}
										  </c:if>
										</td>
										<td class="td_text_center" width="15%">
											 <c:if test="${results.canEdit == false}">
											     <font size="2">
													  <a href="javascript:openView('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.status}','${results.wareHouse}')">
													          รายละเอียด
													  </a>
												  </font>
											  </c:if>
											  <c:if test="${results.canEdit == true}">
											     <font size="2">
													  <a href="javascript:openEdit('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.status}','${results.wareHouse}')">
													     แก้ไข
													  </a>
												  </font>
											  </c:if>
										</td>
										<td class="td_text_center" width="15%">
											
											  <c:if test="${results.canConfirm == true}">
											     <font size="2">
													  <a href="javascript:openConfirm('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.status}','${results.wareHouse}')">
													     Confirm
													  </a>
												  </font>
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