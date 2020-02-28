<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
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
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<jsp:useBean id="pickStockForm" class="com.isecinc.pens.web.pick.PickStockForm" scope="session" />
<% 
if(session.getAttribute("statusIssueReqList") == null){
	List<References> billTypeList = new ArrayList();
	References ref = new References("","");
	billTypeList.add(ref);
	billTypeList.addAll(PickConstants.getIssueReqStatusW3List());
	session.setAttribute("statusIssueReqList",billTypeList);
}

if(session.getAttribute("pickTypeList") == null){
	List<References> pickTypeList = new ArrayList();
	pickTypeList.add(new References(PickConstants.PICK_TYPE_BOX, PickConstants.getStatusDesc(PickConstants.PICK_TYPE_BOX)));
	session.setAttribute("pickTypeList",pickTypeList);
}

if(session.getAttribute("custGroupList") == null){
	List<PopupForm> billTypeList = new ArrayList();
	PopupForm ref = new PopupForm("",""); 
	billTypeList.add(ref);
	billTypeList.addAll(GeneralDAO.searchCustGroup( new PopupForm()));
	
	session.setAttribute("custGroupList",billTypeList);
}

if(session.getAttribute("forwarderList") == null){
	//forwarder
    List<PopupForm> forwarderList = new ArrayList<PopupForm>();
    PopupForm refP = new PopupForm("",""); 
    forwarderList.add(refP);
    forwarderList.addAll(GeneralDAO.searchForwarderList( new PopupForm()));
    request.getSession().setAttribute("forwarderList",forwarderList);
}

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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('issueReqDate'));
	 new Epoch('epoch_popup', 'th', document.getElementById('confirmIssueDate'));
}
function clearForm(path){
	var form = document.pickStockForm;
	form.action = path + "/jsp/pickStockAction.do?do=clear2";
	form.submit();
	return true;
}
function search(path){
	var form = document.pickStockForm;
	var issueReqDate =$('#issueReqDate').val();
	//var storeCode =$('#storeCode').val();
	/* if(issueReqDate ==""){
		alert("กรุณากรอกวันที่  Issue Req Date");
		return false;
	} */
	form.action = path + "/jsp/pickStockAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(path,currPage){
	var form = document.pickStockForm;
	form.action = path + "/jsp/pickStockAction.do?do=search2&currPage="+currPage;
    form.submit();
    return true;
}
function openEdit(path,documentNo,issueReqStatus){
	var form = document.pickStockForm;
	form.action = path + "/jsp/pickStockAction.do?do=prepare&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus;
	form.submit();
	return true;
}
function openEditAllBox(path,documentNo,issueReqStatus){
	var form = document.pickStockForm;
	form.action = path + "/jsp/pickStockAction.do?do=prepareAllBox&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus;
	form.submit();
	return true;
}
function openEditAllPartBox(path,documentNo,issueReqStatus){
	var form = document.pickStockForm;
	form.action = path + "/jsp/pickStockAction.do?do=prepareAllPartBox&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus;
	form.submit();
	return true;
}
function openConfirm(path,documentNo,issueReqStatus){
	var form = document.pickStockForm;
	form.action = path + "/jsp/pickStockAction.do?do=prepare&process=confirm&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus;
	form.submit();
	return true;
}
function openConfirmAllBox(path,documentNo,issueReqStatus){
	var form = document.pickStockForm;
	form.action = path + "/jsp/pickStockAction.do?do=prepareAllBox&process=confirm&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus;
	form.submit();
	return true;
}
function openConfirmAllPartBox(path,documentNo,issueReqStatus){
	var form = document.pickStockForm;
	form.action = path + "/jsp/pickStockAction.do?do=prepareAllPartBox&process=confirm&issueReqNo="+documentNo+"&issueReqStatus="+issueReqStatus;
	form.submit();
	return true;
}
function resetStore(){
	var form = document.pickStockForm;
	var storeGrouptext = $("#custGroup option:selected").text();
	
	if(storeGrouptext != ''){
		form.storeCode.value = "";
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
	}
}
function openPopupCustomer(path,types,storeType){
	var form = document.pickStockForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,storeNo,subInv,types){
	var form = document.pickStockForm;
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
	var form = document.pickStockForm;
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
	var form = document.pickStockForm;
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
	      	
				<jsp:param name="function" value="pickStock"/>
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
						<html:form action="/jsp/pickStockAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td align="right">Issue request Date </td>
                                    <td><html:text property="bean.issueReqDate" styleId="issueReqDate" size="20" /></td>
									<td align="right"> Issue request No </td>
									<td> <html:text property="bean.issueReqNo" styleId="issueReqNo" size="20" />	</td>
								</tr>
								 <tr>
                                    <td  align="right">Issue request status</td>
                                     <td>
                                      <html:select property="bean.issueReqStatus">
											<html:options collection="statusIssueReqList" property="key" labelProperty="name"/>
									    </html:select>
                                     </td>
									<td  align="right">	 ผู้เบิก </td>
									<td> <html:text property="bean.pickUser" styleId="pickUser" size="20" /></td>
								</tr>
								<tr>
                                    <td align="right"> Confrim Issue Date</td>
									<td > 
						               <html:text property="bean.confirmIssueDate" styleId="confirmIssueDate" size="20"/>
									</td>
									<td align="right"> Pick Type </td>
									 <td> 
									    <html:select property="bean.pickType">
											<html:options collection="pickTypeList" property="key" labelProperty="name"/>
									    </html:select>
									  </td>
								</tr>
								<tr>
                                    <td align="right"> กลุ่มร้านค้า  </td>
                                    <td>
										<html:select property="bean.custGroup" styleId="custGroup" onchange="resetStore()">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
						           </td >
						           <td align="right">Invoice No </td>
						           <td > <html:text property="bean.invoiceNo" styleId="invoiceNo" size="20" /></td>
								</tr>
								<tr>
									<td  align="right">รหัสร้านค้า </td>
									<td colspan="3">
									  <html:text property="bean.storeCode" styleId="storeCode" size="20" onkeypress="getCustNameKeypress(event,this,'storeCode')"/>-
									  <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="60"/>
									
									 <html:hidden property="bean.subInv" styleId="subInv" />
						             <html:hidden property="bean.storeNo" styleId="storeNo" />
									</td>
								</tr>
								<tr>
                                    <td align="right"> หมายเหตุ</td>
									<td colspan="3" > 
						               <html:text property="bean.remark" styleId="remark" size="50" />
						          
									</td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										
										<%-- <a href="javascript:openEdit('${pageContext.request.contextPath}','','')">
										  <input type="button" value="   เพิ่มรายการใหม่ (เบิกรายตัว)  " class="newPosBtnLong">
										</a>	 --%>
										<a href="javascript:openEditAllBox('${pageContext.request.contextPath}','','')">
										  <input type="button" value="   เพิ่มรายการใหม่ (เบิกทั้งกล่อง)  " class="newPosBtnLong">
										</a>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>	
									</td>
									</tr>
									<tr>
									 <td align="left">
										<%-- <a href="javascript:openEditAllPartBox('${pageContext.request.contextPath}','','')">
										  <input type="button" value="เพิ่มรายการใหม่ (เบิกทั้งกล่อง (เฉพาะกล่องที่มีการเบิกบางส่วน))" class="newPosBtnLong">
										</a> --%>
															
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${pickStockForm.resultsSearch != null}">
                  	<% 
					   int totalPage = pickStockForm.getTotalPage();
					   int totalRecord = pickStockForm.getTotalRecord();
					   int currPage =  pickStockForm.getCurrPage();
					   int startRec = pickStockForm.getStartRec();
					   int endRec = pickStockForm.getEndRec();
					%>
					    
					<div align="left">
					   <span class="pagebanner">รายการทั้งหมด  <%=totalRecord %> รายการ, แสดงรายการที่  <%=startRec %> ถึง  <%=endRec %>.</span>
					   <span class="pagelinks">
						หน้าที่ 
						 <% 
							 for(int r=0;r<totalPage;r++){
								 if(currPage ==(r+1)){
							 %>
			 				   <strong><%=(r+1) %></strong>
							 <%}else{ %>
							    <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(r+1)%>')"  
							       title="Go to page <%=(r+1)%>"> <%=(r+1) %></a>
						 <% }} %>				
						</span>
					</div>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
									<th >Issue Req Date</th>
									<th >Issue Req No</th>
									<th >Issue Req Status</th>
									<th >Confirm Issue Date</th>
									<th >Pick Type</th>
									<th >ทำ Auto-Trans แล้ว</th>
									<th >หมายเหตุ</th>
									<th >แก้ไข</th>	
									<th >ยืนยัน</th>						
							   </tr>
							<c:forEach var="results" items="${pickStockForm.resultsSearch}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
									<tr class="<c:out value='${tabclass}'/>">
										<td class="td_text_center" width="10%">
										   ${results.issueReqDate}
										</td>
										<td class="td_text_center" width="10%">${results.issueReqNo}</td>
										<td class="td_text_center" width="5%">
											${results.issueReqStatusDesc}
										</td>
										<td class="td_text_center" width="10%">
										    ${results.confirmIssueDate}
										</td>
										
										<td class="td_text_center" width="5%">
										  ${results.pickTypeDesc}
										</td>
										<td class="td_text_center" width="10%">
											<c:if test="${results.autoTrans == true}">
											  <img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
											</c:if>
										</td>
									    <td class="td_text" width="20%">
										  ${results.remark}
										</td>
										<td class="td_text_center" width="10%">
										  <font size="2">
											 <c:if test="${results.pickType == 'ITEM'}">
												<c:choose>
													<c:when test="${results.subPickType == 'PBOX'}">
														<c:if test="${results.canEdit == false}">
															  <a href="javascript:openEditAllPartBox('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.issueReqStatus}')">
															          ดู
															  </a>
														  </c:if>
														  <c:if test="${results.canEdit == true}">
															  <a href="javascript:openEditAllPartBox('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.issueReqStatus}')">
															          แก้ไข
															  </a>
														  </c:if>
													</c:when>
													<c:otherwise>
														 <c:if test="${results.canEdit == false}">
														  <a href="javascript:openEdit('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.issueReqStatus}')">
														          ดู
														  </a>
													  </c:if>
													  <c:if test="${results.canEdit == true}">
														  <a href="javascript:openEdit('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.issueReqStatus}')">
														          แก้ไข
														  </a>
													  </c:if>
													</c:otherwise>
												  </c:choose>
									           </c:if>
								
											    <c:if test="${results.pickType == 'BOX'}">
												    <c:if test="${results.canEdit == false}">
														  <a href="javascript:openEditAllBox('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.issueReqStatus}')">
														          ดู
														  </a>
													  </c:if>
													  <c:if test="${results.canEdit == true}">
														  <a href="javascript:openEditAllBox('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.issueReqStatus}')">
														          แก้ไข
														  </a>
													  </c:if>
											    </c:if>
											  </font>
											</td>
											<td class="td_text_center" width="10%">
											 <font size="2">
											 <c:if test="${results.pickType == 'ITEM'}">
												<c:choose>
													<c:when test="${results.subPickType == 'PBOX'}">
														 <c:if test="${results.canConfirm == true}">
														<a href="javascript:openConfirmAllPartBox('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.issueReqStatus}')">
															   ยืนยัน
														</a>
													  </c:if>
													</c:when>
													<c:otherwise>
														<c:if test="${results.canConfirm == true}">
														  <a href="javascript:openConfirm('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.issueReqStatus}')">
														          ยืนยัน
														</a>
													  </c:if>
													</c:otherwise>
												  </c:choose>
									           </c:if>
									          
											    <c:if test="${results.pickType == 'BOX'}">			    
												     <c:if test="${results.canConfirm == true}">
														<a href="javascript:openConfirmAllBox('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.issueReqStatus}')">
															   ยืนยัน
														</a>
													  </c:if>
											    </c:if> 
											  </font>
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