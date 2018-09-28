<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.bean.PickStock"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.StoreBean"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
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
<jsp:useBean id="renewBoxForm" class="com.isecinc.pens.web.pick.RenewBoxForm" scope="session" />

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css" type="text/css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	// new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
	//sumTotal();
}
function clearForm(path){
	var form = document.renewBoxForm;
	form.action = path + "/jsp/renewBoxAction.do?do=clear2";
	form.submit();
	return true;
}

function exportExcel(path){
	var form = document.renewBoxForm;
	form.action = path + "/jsp/renewBoxAction.do?do=exportExcel";
	form.submit();
	return true;
}

function search(path){
	var form = document.renewBoxForm;
	form.action = path + "/jsp/renewBoxAction.do?do=search2&action=newsearch";
	form.submit();
	return true;
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
	
	// $('#totalBox').val(totalBoxTemp);
	// $('#totalQty').val(totalQtyTemp);
}

function isNum(obj){
  if(obj.value != ""){
	var newNum = parseInt(obj.value);
	if(isNaN(newNum)){
		alert('ให้กรอกได้เฉพาะตัวเลขเท่านั้น');
		obj.value = "";
		obj.focus();
		return false;
	}else{return true;}
   }
  return true;
}

function openJobPopup(path){
    var param = "";
	url = path + "/jsp/searchJobPopupAction.do?do=prepare&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc){
	//alert(types+":"+desc);
	document.getElementsByName("bean.jobId")[0].value = code;
	document.getElementsByName("bean.jobName")[0].value = desc;
}

function getJobNameKeypress(e,code){
	var form = document.renewBoxForm;
	if(e != null && e.keyCode == 13){
		if(code.value ==''){
			form.name.value = '';
		}else{
			getJobNameModel(code);
		}
	}
}

function getJobNameModel(code){
	var returnString = "";
	var form = document.renewBoxForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoJob.jsp",
			data : "code=" + code.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	document.getElementsByName("bean.jobName")[0].value = returnString;	
}

function openDetail(path,jobId,boxNo,mode){
	var form = document.renewBoxForm;
	form.action = path + "/jsp/renewBoxAction.do?do=prepare&jobId="+jobId+"&boxNo="+boxNo+"&mode="+mode;
	form.submit();
	return true;
}
function printReport(path,jobId,boxNo){
	var form = document.renewBoxForm;
	form.action = path + "/jsp/renewBoxAction.do?do=printReport&jobId="+jobId+"&boxNo="+boxNo;
	form.submit();
	return true;
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
				<jsp:param name="function" value="renewBox"/>
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
						<html:form action="/jsp/renewBoxAction">
						<jsp:include page="../error.jsp"/>

						    <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
								    <td>รับคืนจาก</td>
									<td >
									    <html:text property="bean.jobId" styleId="jobId" size="20" 
						                               onkeypress="getJobNameKeypress(event,this)"/> 
						                 <input type="button" name="x1" value="..." onclick="openJobPopup('${pageContext.request.contextPath}')"/>
						                  <html:text property="bean.jobName" styleId="jobName" size="30" readonly="true" styleClass="disableText"/>
									</td>
								</tr>
								<tr>
								    <td> เลขที่กล่อง</td>
                                    <td>
                                      <html:text property="bean.boxNo" styleId="boxNo" size="20" />
                                      </td>
								</tr>	
						   </table>
						    <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										
										<a href="javascript:openDetail('${pageContext.request.contextPath}','','','new')">
										  <input type="button" value="ReNewBox รายการใหม่    " class="newPosBtnLong"> 
										</a>	
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="  Clear  " class="newPosBtnLong"> 
										</a>												
									</td>
								</tr>
							</table>
					    </div>
					  
                     <!-- Table Content -->					
						<c:if test="${renewBoxForm.resultsSearch != null}">
				
								<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
								    <tr>
										<th >No.</th>
										<th >เลขที่กล่อง</th>
										<th >Job Id</th>
										<th >Job Name</th>
										<th >Qty</th>
										<th >New Box No</th>
										<th >Link</th>			
									</tr>
									<c:forEach var="results" items="${renewBoxForm.resultsSearch}" varStatus="rows">
										<c:choose>
											<c:when test="${rows.index %2 == 0}">
												<c:set var="tabclass" value="lineO"/>
											</c:when>
											<c:otherwise>
												<c:set var="tabclass" value="lineE"/>
											</c:otherwise>
										</c:choose>
										
											<tr class="<c:out value='${tabclass}'/>">
											
												<td class="search_no" align="center">${results.lineId}
													<input type="hidden" name="lineId" value ="${results.lineId}" size="40" readonly class="disableText"/>
													
												</td>
												<td class="search_boxNo" align="center">${results.boxNo}
													<input type="hidden" name="boxNo" value ="${results.boxNo}" size="40" readonly class="disableText"/>
												</td>
												<td class="search_jobId" align="center">${results.jobId}
													<input type="hidden" name="jobId" value ="${results.jobId}" size="40" readonly class="disableText"/>
												</td>
												<td class="search_jobName" align="left">${results.jobName}
													<input type="hidden" name="jobName" value ="${results.jobName}" size="40" readonly class="disableText"/>
												</td>
												<td class="search_qty" align="left">${results.qty}</td>
												<td class="search_boxNoRef" align="left">${results.boxNoRef}</td>
												<td class="search_link" align="center"> 
												     <a href="javascript:printReport('${pageContext.request.contextPath}','${results.jobId}','${results.boxNoRef}')">
											            <input type="button" value="พิมพ์ไปปะหน้ากล่อง" class="newPosBtnLong">
											        </a>
												      
												</td>
											</tr>
									
									  </c:forEach>
							</table>
						</c:if>


					<!-- BUTTON ACTION-->
					<div align="center">
						<table  border="0" cellpadding="3" cellspacing="0" >
									<tr>
										<td align="left">
										
																
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