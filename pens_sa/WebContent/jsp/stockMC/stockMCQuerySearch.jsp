<%@page import="com.pens.util.PageingGenerate"%>
<%@page import="com.isecinc.pens.web.stockmc.StockMCAction"%>
<%@page import="com.pens.util.UserUtils"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@page import="com.isecinc.pens.web.stockmc.StockMCBean"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="stockMCForm" class="com.isecinc.pens.web.stockmc.StockMCForm" scope="session" />
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "stockMCForm");
User user = (User)session.getAttribute("user");
String pageName = Utils.isNull(request.getParameter("pageName")); 

String codes = Utils.isNull(session.getAttribute("stock_mc_codes"));
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/stockMC.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<!-- Calendar -->
 <link rel="StyleSheet" href="${pageContext.request.contextPath}/css/calendar/jquery.calendars.picker.css" type="text/css" />
<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.10.0.js"></script>  --%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.plugin.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.plus.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.picker.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.thai.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.thai-th.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.picker-th.js"></script>

<!-- For fix Head and Column Table -->

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-stickytable-3.0.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/jquery-stickytable-3.0.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<script type="text/javascript">

/** disable back button alway **/
window.location.hash="no-back-button";
window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
window.onhashchange=function(){window.location.hash="no-back-button";}

window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	  $('#stockDateFrom').calendarsPicker({calendar: $.calendars.instance('','')});
	  $('#stockDateTo').calendarsPicker({calendar: $.calendars.instance('','')});
}
function clearForm(path){
	var form = document.stockMCForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/stockMCAction.do?do=prepareSearch&action=back&pageName="+pageName;
	form.submit();
	return true;
}
function search(path){
	var form = document.stockMCForm;

 	if(   form.stockDateFrom.value =="" && form.stockDateTo.value==""
 	   && form.customerCode.value =="" && form.storeCode.value==""
 	   && form.brandFrom.value =="" && form.brandTo.value==""
 	   && form.productCodeFrom.value =="" && form.productCodeTo.value==""
 	  ){
		alert("กรุณาระบุ เงื่อนไข 1 อย่าง");
		form.stockDateFrom.focus();
		return false;
	} 
	
	form.action = path + "/jsp/stockMCAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function openEdit(path,action,id){
	var form = document.stockMCForm;
    var param = "&id="+id;
        param +="&action="+action;
        param +="&pageName="+document.getElementsByName("pageName")[0].value;
        
	form.action = path + "/jsp/stockMCAction.do?do=viewDetail"+param;
	form.submit();
	return true;
	
	//var url = path + "/jsp/stockMCAction.do?do=viewDetail"+param;
    //PopupCenterFull(url,"View")
}

function exportToExcel(path){
	var form = document.stockMCForm;
	form.action = path + "/jsp/stockMCAction.do?do=exportToExcel&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(currPage){
	var form = document.stockMCForm;
	form.action =  "${pageContext.request.contextPath}/jsp/stockMCAction.do?do=searchHead&currPage="+currPage;
    form.submit();
    return true;
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
	if(no > <%=StockMCAction.pageSize%>){
	   no = no - ( (currentPage-1) *<%=StockMCAction.pageSize%>);
	}
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
			url: "${pageContext.request.contextPath}/jsp/stockMC/ajax/saveValueSelected.jsp",
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
/** auto Key Get Detail **/
function getAutoOnblur(e,obj,pageName){
	var form = document.stockMCForm;
	if(obj.value ==''){
		if("CustomerStockMC" == pageName){
		  form.customerCode.value = '';
		  form.customerName.value = '';
		}else if("Brand" == pageName){
		  form.brand.value = '';
		  form.brandName.value = '';
		}
	}else{
		getAutoDetail(obj,pageName);
	}
}
/** enter **/
function getAutoKeypress(e,obj,pageName){
	var form = document.stockMCForm;
	if(e != null && e.keyCode == 13){
		if(obj.value ==''){
			if("CustomerStockMC" == pageName){
				form.customerCode.value = '';
				form.customerName.value = '';
			}else if("Brand" == pageName){
			    form.brand.value = '';
			    form.brandName.value = '';
			}
		}else{
			getAutoDetail(obj,pageName);
		}
	}
}

function getAutoDetail(obj,pageName){
	var returnString = "";
	var form = document.stockMCForm;
	
	//prepare parameter
	var param  ="pageName="+pageName;
	if("CustomerStockMC"==pageName){
		param +="&customerCode="+obj.value;
	}else if("Brand"==pageName){
		param +="&brand="+obj.value;
	}
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/getAutoKeypressAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	 
	if("CustomerStockMC" == pageName){
		var retArr = returnString.split("|");
		//alert(returnString);
		if(retArr[0] !=-1){
			form.customerCode.value = retArr[1];
			form.customerName.value = retArr[2];
		}else{
			alert("ไม่พบข้อมูล");
			form.customerCode.focus();
			form.customerCode.value = '';
			form.customerName.value = "";
		}
	}else if("Brand" == pageName){
		var retArr = returnString.split("|");
		//alert(returnString);
		if(retArr[0] !=-1){
			form.brand.value = retArr[1];
			form.brandName.value = retArr[2];
		}else{
			alert("ไม่พบข้อมูล");
			form.brand.focus();
			form.brand.value = '';
			form.brandName.value = "";
		}
	}
}

</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"  style="height: 100%;">
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
				<jsp:param name="function" value="StockMCQuery"/>
			</jsp:include>
			<!-- Hidden Field -->
		 <%--    <html:hidden property="pageName" value="<%=pageName %>"/> --%>
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
						<html:form action="/jsp/stockMCAction">
						<jsp:include page="../error.jsp"/>
						<div align="center">
						   	<!--  Criteria -->
						   <table align="center" border="0" cellpadding="3" cellspacing="0" >
					       <tr>
				                <td>วันที่ตรวจนับสต๊อก<font color="red"></font></td>
								<td colspan="3">		
			                        <html:text property="bean.stockDateFrom" styleId="stockDateFrom" size="15" readonly="true" styleClass=""/>					    
								    -
								    <html:text property="bean.stockDateTo" styleId="stockDateTo" size="15" readonly="true" styleClass=""/>					    
								</td>
							</tr>
							<tr>
				                <td align="right"> ห้าง  </td>
								<td colspan="2">
								  <html:text property="bean.customerCode" styleId="customerCode" 
								    size="15"  styleClass="\" autoComplete=\"off"
									onkeypress="getAutoKeypress(event,this,'CustomerStockMC')"
					                onblur="getAutoOnblur(event,this,'CustomerStockMC')"
									/>
								    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerStockMC')"/>   
								   <html:text property="bean.customerName" styleId="customerName" size="40" readonly="true" styleClass="disableText"/>
								   สาขา 
								  <html:text property="bean.storeCode" styleId="storeCode" 
								    size="10"  styleClass="\" autoComplete=\"off"/>
								    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','BranchStockMC')"/>   
								   <html:hidden property="bean.storeName" styleId="storeName"/>
								</td>
								
							</tr>	
							<tr>
				                <td align="right"> แบรนด์ </td>
								<td colspan="2">
								  <html:text property="bean.brandFrom" styleId="brandFrom" 
								    size="15"  styleClass="\" autoComplete=\"off"/>
								  -
								  <html:text property="bean.brandTo" styleId="brandTo" 
								    size="15"  styleClass="\" autoComplete=\"off"/>
								</td>
							</tr>	
							 <tr>
				                <td align="right">รหัสสินค้า</td>
								<td colspan="3">		
			                        <html:text property="bean.productCodeFrom" styleId="productCodeFrom" size="15" styleClass="\" autoComplete=\"off"/>					    
								    -
								    <html:text property="bean.productCodeTo" styleId="productCodeTo" size="15" styleClass="\" autoComplete=\"off"/>					    
								&nbsp;&nbsp;&nbsp;&nbsp;<html:checkbox property="bean.dispHaveCheckStock">&nbsp;&nbsp;แสดงเฉพาะ SKU ที่มีการบันทึกข้อมูล</html:checkbox>
								
								</td>
							</tr>
					   </table>
					   <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
									<a href="javascript:search('${pageContext.request.contextPath}')">
									  <input type="button" value="   ค้นหา     " class="newPosBtnLong"> 
									</a>
									<a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
									  <input type="button" value="Export To Excel" class="newPosBtnLong">
									</a>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									  <input type="button" value="   Clear   " class="newPosBtnLong">
									</a>						
								</td>
							</tr>
						</table>
					    
					 	   <!-- ************************Result *************-->
					 	    <%
					 	    if(request.getAttribute("RESULTS") !=null){
					 	    	out.println(((StringBuffer)request.getAttribute("RESULTS")).toString());
					 	    %>
					 	      <script>
								  //load jquery
								   $(function() {
										//Load fix column and Head
										$('#myTable').stickyTable({overflowy: true});
									});
								</script>
					 	    <%
					 	    }
					 	    %>
					 	</div>
					 	
					 	<!-- INPUT HIDDEN -->
					 	<input type="hidden" name="pageName" value="<%=pageName %>"/>
					 	<input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
					 	<input type="hidden" name="currentPage"  value ="<%=stockMCForm.getCurrPage()%>" />
                        <input type="hidden" name="codes" value ="<%=codes%>" />
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