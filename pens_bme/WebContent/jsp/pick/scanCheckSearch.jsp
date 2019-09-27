<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.pens.util.*"%>
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
<jsp:useBean id="scanCheckForm" class="com.isecinc.pens.web.pick.ScanCheckForm" scope="session" />
<%
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
}
function clearForm(path){
	var form = document.scanCheckForm;
	form.action = path + "/jsp/scanCheckAction.do?do=clear2";
	form.submit();
	return true;
}

function exportExcel(path){
	var form = document.scanCheckForm;
	if( $('#transactionDate').val()=="" && $('#boxNo').val()==""
		&& $('#jobId').val()=="" && $('#status').val()=="" 
		&& $('#remark').val()=="" && $('#storeCode').val()==""){
		alert("กรุณากรอก ข้อมูลค้นหาอย่างน้อย 1 รายการ");
		return false;
	}
	
	form.action = path + "/jsp/scanCheckAction.do?do=exportExcel";
	form.submit();
	return true;
}

function search(path){
	var form = document.scanCheckForm;
	/* if( $('#transactionDate').val()=="" && $('#boxNo').val()==""
		&& $('#jobId').val()=="" && $('#status').val()=="" 
		&& $('#remark').val()=="" && $('#storeCode').val()==""){
		alert("กรุณากรอก ข้อมูลค้นหาอย่างน้อย 1 รายการ");
		return false;
	} */
	
	form.action = path + "/jsp/scanCheckAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(path,currPage){
	var form = document.scanCheckForm;
	form.action = path + "/jsp/scanCheckAction.do?do=search2&currPage="+currPage;
    form.submit();
    return true;
}
function openEdit(path,issueReqNo,boxNo,warehouse,mode){
	var form = document.scanCheckForm;
	form.action = path + "/jsp/scanCheckAction.do?do=prepare&issueReqNo="+issueReqNo+"&boxNo="+boxNo+"&mode="+mode+"&warehouse="+warehouse;
	form.submit();
	return true;
}

function openPopupCustomer(path,types,storeType){
	var form = document.scanCheckForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,storeNo,subInv,types){

	var form = document.scanCheckForm;
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
	var form = document.scanCheckForm;
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
	var form = document.scanCheckForm;
	var storeGroup = form.custGroup.value;
	
	if(storeGroup != "" && custCode.value != ""){
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
}

function resetStore(){
	var form = document.scanCheckForm;
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
				<jsp:param name="function" value="scanCheck"/>
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
						<html:form action="/jsp/scanCheckAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Issue Request No</td>
									<td>					
										 <html:text property="bean.issueReqNo" styleId="issueReqNo" size="20"  styleClass="\" autoComplete=\"off"/>
									</td>
									<td> 
									           สถานะ   
										<html:select property="bean.status" styleId="status">
											<html:options collection="barcodeStatusList_scan" property="key" labelProperty="name"/>
									    </html:select>
									</td>
									<td></td>
								</tr>
								 <tr>
                                    <td>  Warehouse </td>
									<td colspan="2">					
										 <html:select property="bean.wareHouse" styleId="wareHouse">
											<html:options collection="warehouseList_scan" property="key" labelProperty="name"/>
									    </html:select>
									</td>
								</tr>
								 <tr>
                                    <td>  กลุ่มร้านค้า </td>
									<td colspan="2">					
										 <html:select property="bean.custGroup" styleId="custGroup" onchange="resetStore()">
											<html:options collection="custGroupList_scan" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
                                    <td> 
                                                                                                                                            รหัสร้านค้า    
                                    </td>
									<td colspan="2">
									   <html:text property="bean.storeCode" styleId="storeCode" size="20" onblur="getCustName(this,'storeCode')" 
									   onkeypress="getCustNameKeypress(event,this,'storeCode')"  styleClass="\" autoComplete=\"off"/>-
									  <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="40"/>
									   
									   <!-- Sub Inventory -->
						               <html:hidden property="bean.subInv" styleId="subInv" />
						               <!-- Store No -->
						               <html:hidden property="bean.storeNo" styleId="storeNo" />
									</td>
								</tr>	
								 <tr>
                                    <td>  แสดงตาม </td>
									<td colspan="2">					
										 <html:select property="bean.summaryType" styleId="summaryType">
											<html:option value="box">แสดงเป็นกล่อง</html:option>
											<html:option value="detail">แสดงเป็นสีไซร์</html:option>
									    </html:select>
									</td>
								</tr>
								
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   	<%-- <a href="javascript:exportExcel('${pageContext.request.contextPath}')">
										  <input type="button" value=" Export To Excel " class="newPosBtnLong">
										</a> --%>
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										
										<a href="javascript:openEdit('${pageContext.request.contextPath}','','','','add')">
										  <input type="button" value="   เพิ่มรายการใหม่   " class="newPosBtnLong">
										</a>	
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>
					  
      <c:if test="${scanCheckForm.bean.summaryType == 'box'}">

            <c:if test="${scanCheckForm.resultsSearch != null}">
                  	<% 
					   int totalPage = scanCheckForm.getTotalPage();
					   int totalRecord = scanCheckForm.getTotalRecord();
					   int currPage =  scanCheckForm.getCurrPage();
					   int startRec = scanCheckForm.getStartRec();
					   int endRec = scanCheckForm.getEndRec();
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
									<th >No</th>
									<th >เลขที่กล่อง</th>
									<th >Issue req no</th>
									<th >Check-out Date</th>
									<th >Warehouse</th>
									<th >รหัสร้านค้า</th>
									<th >จำนวน scan check-out</th>
									<th >Status</th>
									<th >Action</th>						
							   </tr>
							<c:forEach var="results" items="${scanCheckForm.resultsSearch}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
									<tr class="<c:out value='${tabclass}'/>">
										<td class="td_text_center" width="5%">${results.no}</td>
										<td class="td_text_center" width="7%">${results.boxNo}</td>
										<td class="td_text_center" width="7%">${results.issueReqNo}</td>
										<td class="td_text_center" width="7%">${results.checkOutDate}</td>
										<td class="td_text_center" width="5%">${results.wareHouse}</td>
										<td class="td_text" width="19%">${results.storeCode}-${results.storeName}</td>
										<td class="td_text_right" width="10%" align="right">${results.totalQty}</td>
							            <td class="td_text_center" width="10%">${results.statusDesc}</td>
										<td class="td_text_center" width="10%">
										 <c:if test="${results.canEdit == false}">
											  <a href="javascript:openEdit('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.boxNo}','${results.wareHouse}','view')">
											        <font size="2" > ดู</font>
											  </a>
										  </c:if>
										  <c:if test="${results.canEdit == true}">
											  <a href="javascript:openEdit('${pageContext.request.contextPath}', '${results.issueReqNo}','${results.boxNo}','${results.wareHouse}','edit')">
											         <font size="2" >แก้ไข</font>
											  </a>
										  </c:if>
										</td>
									</tr>
							  </c:forEach>
							  
							  <tr class="">
								   <td class=""></td>
							       <td class=""></td>
							       <td class=""></td>
							       <td class=""></td>
							       <td class=""></td>
								   <td class="hilight_text" align="right">
									  <B> Total </B>
									</td>
									<td class="hilight_text" align="right">
									 <B>  ${scanCheckForm.bean.totalQty}</B>
									</td>
									<td class=""></td>
									<td class=""></td>
							</tr>
					</table>
				</c:if>
	</c:if>
	
	 <c:if test="${scanCheckForm.bean.summaryType == 'detail'}">
            <c:if test="${scanCheckForm.resultsSearch != null}"> 
            <% 
					   int totalPage = scanCheckForm.getTotalPage();
					   int totalRecord = scanCheckForm.getTotalRecord();
					   int currPage =  scanCheckForm.getCurrPage();
					   int startRec = scanCheckForm.getStartRec();
					   int endRec = scanCheckForm.getEndRec();
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
									<th >No</th>
									<th >Material Master</th>
									<th >Barcode</th>
									<th >Pens Item</th>
									<th >Warehouse</th>
									<th >รหัสร้านค้า</th>
									<th >จำนวน scan check-out</th>
															
							   </tr>
							<c:forEach var="results" items="${scanCheckForm.resultsSearch}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
									<tr class="<c:out value='${tabclass}'/>">
										<td class="td_text_center" width="5%">${results.no}</td>
										<td class="td_text_center" width="8%">${results.materialMaster}</td>
										<td class="td_text_center" width="8%">${results.barcode}</td>
										<td class="td_text_center" width="8%">${results.pensItem}</td>
										<td class="td_text_center" width="5%">${results.wareHouse}</td>
										<td class="td_text" width="16%">${results.storeCode}-${results.storeName}</td>
										<td class="td_text_right" width="10%" align="right">${results.totalQty}</td> 
									</tr>
							
							  </c:forEach>
							  
							  <tr class="">
								   <td class=""></td>
							       <td class=""></td>
							       <td class=""></td>
							       <td class=""></td>
							       <td class=""></td>
								   <td class="hilight_text" align="right">
									  <B> Total </B>
									</td>
									<td class="hilight_text" align="right">
									 <B>  ${scanCheckForm.bean.totalQty}</B>
									</td>
									
							</tr>
					</table>
				</c:if>
	</c:if>
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