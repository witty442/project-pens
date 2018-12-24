<%@page import="com.isecinc.pens.web.pick.PickReportAction"%>
<%@page import="com.isecinc.pens.bean.PickReportBean"%>
<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
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
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<jsp:useBean id="pickReportForm" class="com.isecinc.pens.web.pick.PickReportForm" scope="session" />
<%
String codes = Utils.isNull(session.getAttribute("pick_report_codes"));
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css"></style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/DateUtils.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('issueReqDateFrom'));
	 new Epoch('epoch_popup', 'th', document.getElementById('issueReqDateTo'));
	 
	 setChkInPage();
}
function clearForm(path){
	var form = document.pickReportForm;
	form.action = path + "/jsp/pickReportAction.do?do=clear";
	form.submit();
	return true;
}

function exportExcel(path,reportType){
	var form = document.pickReportForm;
	if( $('#custGroup').val()=="" ){
		alert("กรุณากรอก กลุ่มร้านค้า");
		$('#custGroup').focus();
		return false;
	 }
	//validate start end date is same month
	if( $('#issueReqDateFrom').val() !="" && $('#issueReqDateTo').val() !=""){
		 if(!dateFromToInSameMonth($('#issueReqDateFrom').val(),$('#issueReqDateTo').val())){
			 alert("กรุณาเลือกวันที่ ในช่วงเดือนเดียวกันเท่านั้น")
			 $('#issueReqDateTo').focus();
			 return false;
		 }
	 }
	//alert("codes["+document.getElementsByName("codes")[0].value+"]");
	if("Normal" !=reportType){
		var codes = document.getElementsByName("codes")[0].value;
		if(codes == ''){
			alert("กรุณาระบุ รายการที่ต้องการ Export ก่อน");
			return false;
		}
	}
	form.action = path + "/jsp/pickReportAction.do?do=exportReport&action=newsearch&reportType="+reportType;
	form.submit();
	return true;
}

function search(path){
	var form = document.pickReportForm;
	 if( $('#custGroup').val()=="" ){
		alert("กรุณากรอก กลุ่มร้านค้า");
		$('#custGroup').focus();
		return false;
	 }
	//validate start end date is same month
	if( $('#issueReqDateFrom').val() !="" && $('#issueReqDateTo').val() !=""){
		 if(!dateFromToInSameMonth($('#issueReqDateFrom').val(),$('#issueReqDateTo').val())){
			 alert("กรุณาเลือกวันที่ ในช่วงเดือนเดียวกันเท่านั้น")
			 $('#issueReqDateTo').focus();
			 return false;
		 }
	 }
	
	form.action = path + "/jsp/pickReportAction.do?do=searchReport&action=newsearch";
	form.submit();
	return true;
}

function gotoPage(path,currPage){
	var form = document.pickReportForm;
	form.action = path + "/jsp/pickReportAction.do?do=searchReport&currPage="+currPage;
    form.submit();
    return true;
}

function openPopupCustomer(path,types,storeType){
	var form = document.pickReportForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	//window.open(encodeURI(url),"",
			  // "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
	PopupCenterFullHeight(url,"",600);
}

function setStoreMainValue(code,desc,storeNo,subInv,types){
	var form = document.pickReportForm;
	//alert(form);
	form.storeCode.value = code;
	form.storeName.value = desc;
} 

function getCustNameKeypress(e,custCode,fieldName){
	var form = document.pickReportForm;
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			if("storeCode" == fieldName){
				form.storeCode.value = '';
				form.storeName.value = "";
			}
		}else{
		   getCustName(custCode,fieldName);
		}
	}
}
function getCustName(custCode,fieldName){
	var returnString = "";
	var form = document.pickReportForm;
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
				}
			}else{
				alert("ไม่พบข้อมูล");
				form.storeCode.focus();
				form.storeCode.value ="";
				form.storeName.value = "";
			}
		}
}
function resetStore(){
	var form = document.pickReportForm;
	var storeGrouptext = $("#custGroup option:selected").text();
	if(storeGrouptext != ''){
		form.storeCode.value = "";
		form.storeName.value = "";
	}
}
/* set Chechbox Page **/
function saveSelectedInPage(no){
	//alert(no);
	var chk = document.getElementsByName("chCheck");
	var currentPage = document.getElementsByName("currentPage")[0].value;
	var code = document.getElementsByName("code_temp");

	var retCode = '';
	var codesAllNew = "";

	//alert("no:"+no);
	
	if(no > <%=PickReportAction.pageSize%>){
	   no = no - ( (currentPage-1) *<%=PickReportAction.pageSize%>);
	}
	no = no -1;
	//alert("no["+no+"]checked:"+chk[no].checked);
    if(chk[no].checked){
    	//Add 
        retCode = code[no].value;
		
		//alert("code["+no+"]="+retCode);
	    codesAllNew = document.getElementsByName("codes")[0].value;
		
	    var found = chekCodeDupInCodesAll(retCode);
	    if(found == false){
	  	   codesAllNew += retCode +",";
	    }
	    document.getElementsByName("codes")[0].value =  codesAllNew;
	    //alert(no+":found["+found+"] codes["+document.getElementsByName("codes")[0].value+"]");
    }else{
    	//remove
    	retCode = code[no].value;
		
		//alert(retCode);
	    codesAllNew = document.getElementsByName("codes")[0].value;
		
	    removeUnSelected(retCode);
    }
	
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/pick/ajax/setValueSelected.jsp",
			data : "codes=" + encodeURIComponent(document.getElementsByName("codes")[0].value),
			//async: false,
			cache: true,
			success: function(){
			}
		}).responseText;
	});
}

function chekCodeDupInCodesAll(codeCheck){
	var codesAll = document.getElementsByName("codes")[0].value;
	var codesAllArray = codesAll.split(",");
	var found = false;;
	for(var i=0;i < codesAllArray.length; i++){
   		if(codesAllArray[i] == codeCheck){
   			found = true;
   			break;
   		}//if
	}//for
	return found;
}

function removeUnSelected(codeCheck){
	var codesAll = document.getElementsByName("codes")[0].value;
	var codesAllArray = codesAll.split(",");
	var codesAllNew  = "";
	for(var i=0;i < codesAllArray.length; i++){
   		if(codesAllArray[i] != codeCheck){
   		   codesAllNew += codesAllArray[i] +",";
   		}//if
	}//for
	
	codesAllNew = codesAllNew.substring(0,codesAllNew.length-1);
	document.getElementsByName("codes")[0].value =  codesAllNew;
}

function setChkInPage(){
	var chk = document.getElementsByName("chCheck");
	var code = document.getElementsByName("code_temp");
	//alert("Code Size:"+code.length);
	
	var codes = document.getElementsByName("codes")[0].value;
	var codesChk = codes.split(",");
	//alert("codes:"+codes);
	
	if(codesChk != ''){
		for(var i=0;i<chk.length;i++){
			for(var c=0;c<codesChk.length;c++){
				if(code[i].value == codesChk[c]){
					chk[i].checked = true;
					break;
				}//if equals
			} //for 2
		}//for 1
  }//if
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
				<jsp:param name="function" value="PickReport"/>
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
						<html:form action="/jsp/pickReportAction">
						<jsp:include page="../error.jsp"/>
                           <input type="hidden" name="currentPage"  value ="<%=pickReportForm.getCurrPage()%>" />
                           <input type="hidden" name="codes" value ="<%=codes%>" />

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						      
								<tr>
                                    <td> กลุ่มร้านค้า <font color="red">*</font> </td>
									<td>		
										 <html:select property="bean.custGroup" styleId="custGroup" onchange="resetStore()">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
									<td >รหัสร้านค้า
									</td>
									<td align="left"> 
									  <html:text property="bean.storeCode" styleId="storeCode" size="20" onkeypress="getCustNameKeypress(event,this,'storeCode')"/>-
									  <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="30"/>
									
									  <html:hidden property="bean.subInv" styleId="subInv"/>
									  <html:hidden property="bean.storeNo" styleId="storeNo"/>
									</td>
								</tr>
								 <tr>
                                    <td> From Issue Request Date</td>
									<td>					
									   <html:text property="bean.issueReqDateFrom" styleId="issueReqDateFrom" size="20" readonly="true"/>
									   To Issue Request Date   				
									   <html:text property="bean.issueReqDateTo" styleId="issueReqDateTo" size="20" readonly="true"/>
									</td>
								</tr>
								<tr>
                                    <td> Status</td>
									<td><html:text property="bean.status" styleId="status" readonly="true" styleClass="disableText"/></td>
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
										<a href="javascript:exportExcel('${pageContext.request.contextPath}','Normal')">
						                   <input type="button" value="  Export  " class="newPosBtnLong"> 
						                </a>	
						                <a href="javascript:exportExcel('${pageContext.request.contextPath}','Summary')">
						                   <input type="button" value="Export Summary by Item" class="newPosBtnLong"> 
						                </a>
						                <a href="javascript:exportExcel('${pageContext.request.contextPath}','Detail')">
						                   <input type="button" value="Export Detail" class="newPosBtnLong"> 
						                </a>				
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${pickReportForm.results != null}">
                  	 <jsp:include page="../pageing.jsp">
				       <jsp:param name="totalPage" value="<%=pickReportForm.getTotalPage() %>"/>
				       <jsp:param name="totalRecord" value="<%=pickReportForm.getTotalRecord() %>"/>
				       <jsp:param name="currPage" value="<%=pickReportForm.getCurrPage() %>"/>
				       <jsp:param name="startRec" value="<%=pickReportForm.getStartRec() %>"/>
				       <jsp:param name="endRec" value="<%=pickReportForm.getEndRec() %>"/>
			         </jsp:include>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
									<th >No</th>
									<th >Select</th>
									<th >Store Code</th>
									<th >Store Name</th>
									<th >Issue Req No</th>
									<th >Issue Date</th>
									<th >Qty</th>
									<th >User Request</th>
									<th >Remark</th>					
							   </tr>
							<c:forEach var="results" items="${pickReportForm.results}" varStatus="rows">
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
									   <td class="td_text_center" width="5%">
									      <input type ="checkbox" name="chCheck" id="chCheck" onclick="saveSelectedInPage(${results.no})"  />
			                              <input type ="hidden" name="code_temp" value="${results.issueReqNo}" />
										</td>
										<td class="td_text_center" width="10%">${results.storeCode}</td>
										<td class="td_text" width="15%">${results.storeName}</td>
										<td class="td_text_center" width="10%">${results.issueReqNo}</td>
										<td class="td_text_center" width="10%">${results.issueReqDate}</td>
										<td class="td_text_right"  width="5%">${results.issueQty}</td>
										<td class="td_text" width="10%">${results.userRequest}</td>
										<td class="td_text"  width="10%">${results.remark}</td>
									</tr>
							  </c:forEach>
					</table>
							
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