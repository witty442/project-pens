<%@page import="com.isecinc.pens.web.report.ReportProcess"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>

<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="reportForm" class="com.isecinc.pens.web.report.ReportForm" scope="session" />

<%
if(request.getParameter("action") != null){
   ReportProcess.getInstance().initSession(request);
}

String typeSearch = Utils.isNull(request.getAttribute("DATA"));
java.util.List yearList = null;
if(session.getAttribute("yearList") != null) 
	yearList = (java.util.List)session.getAttribute("yearList");
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />


<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/date.js"></script>
<script type="text/javascript">

function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('day'));
	new Epoch('epoch_popup','th',document.getElementById('dayTo'));
	chkSearch();
	chkYear();
}

function search(path) {
	if(validateCriteria()){
	   document.reportForm.action = path + "/jsp/reportAction.do?do=search";
	   document.reportForm.submit();
	   return true;
	}
	return false;
}

function exportExcel(path) {
	document.reportForm.action = path + "/jsp/reportAction.do?do=export";
	document.reportForm.submit();
	return true;
}

function clearForm(path) {
	document.reportForm.action = path + "/jsp/reportAction.do?do=clear";
	document.reportForm.submit();
	return true;
}

function validateCriteria(){
	 var typeSearch = document.getElementsByName("salesBean.typeSearch")[0];
	 if(typeSearch.value == 'DAY'){
		 var day = document.getElementsByName("salesBean.day")[0];
		 var dayTo = document.getElementsByName("salesBean.dayTo")[0];
		 if(day.value =='' && dayTo.value == ''){
			 alert("กรุณากรอกวันที่");
			 return false;
		 }

		 if(day.value != '' && dayTo.value != ''){
			// Check Date From Should More Than or Equal To Date To
			var dateFrom = getDateFromFormat(day.value,"dd/MM/yyyy");
			var dateTo = getDateFromFormat(dayTo.value,"dd/MM/yyyy");

			if(dateFrom > dateTo){
				alert("กรุณาระบุวันที่ให้ถูกต้อง");
				return false;
			}	 
		 }
	 }else  if(typeSearch.value == 'MONTH'){
		 var obj = document.getElementsByName("salesBean.chkMonth");
		 if( !isChkOne(obj)){
			 alert("โปรดเลือกแสดงข้อมูลอย่างน้อย 1 เดือน");
			 return false;
		 }
	 }else  if(typeSearch.value == 'YEAR'){
		 var obj = document.getElementsByName("salesBean.chkYear");
		 if( !isChkOne(obj)){
			 alert("โปรดเลือกแสดงข้อมูลอย่างน้อย 1 ปี");
			 return false;
		 }
	 }

	 if(!validateDisp()){
		 alert("โปรดเลือก คอลัมน์ ที่จะแสดงอย่างน้อย 1 คอลัมน์ ");
		 return false;
	 }
	 if(!validateCond()){
		 alert("โปรดเลือก เงื่อนไขในการเลือกข้อมูล");
	    return false;
	 }
	 return true;
}

function isChkOne(obj){
	for(i=0;i<obj.length;i++){
		var chk = obj[i].checked;
		if(chk){
		  return true;
		}
	}
	return false;
}

function validateDisp(){
	var disp1 = document.getElementsByName("salesBean.colNameDisp1")[0];
	var disp2 = document.getElementsByName("salesBean.colNameDisp2")[0];
	var disp3 = document.getElementsByName("salesBean.colNameDisp3")[0];
	var disp4 = document.getElementsByName("salesBean.colNameDisp4")[0];
	if(disp1.value =='0' && disp2.value =='0' && disp3.value =='0' && disp4.value =='0'){
        return false;
	}
	return true;
}

function validateCond(){
	if (document.getElementsByName("salesBean.condName1")[0].value != '-1' &&
			document.getElementsByName("salesBean.condValue1")[0].value == ''	)
	{
		 return false;
	}
	if (document.getElementsByName("salesBean.condName2")[0].value != '-1' &&
			document.getElementsByName("salesBean.condValue2")[0].value == ''	)
	{
		 return false;
	}
	if (document.getElementsByName("salesBean.condName3")[0].value != '-1' &&
			document.getElementsByName("salesBean.condValue3")[0].value == ''	)
	{
		 return false;
	}
	if (document.getElementsByName("salesBean.condName4")[0].value != '-1' &&
			document.getElementsByName("salesBean.condValue4")[0].value == ''	)
	{
		 return false;
	}
	return true;
}


function chkSearch(){
	   var typeSearch = document.getElementsByName("salesBean.typeSearch")[0];
	   //alert(typeSearch.value);
		disabledObj(document.getElementsByName("salesBean.day")[0] ,false);
		disabledObj(document.getElementsByName("salesBean.dayTo")[0] ,false);
		
		var monthList = document.getElementsByName("salesBean.chkMonth");
		for(i=0;i<monthList.length;i++){
	       disabledObj(document.getElementsByName("salesBean.chkMonth")[i],false);
	    }
		for(i=0;i < <%=yearList!=null?yearList.size():0 %>; i++){
		   disabledObj(document.getElementsByName("salesBean.chkYear")[i],false);
		}
		   
	   if(typeSearch.value == 'DAY'){
		   for(i=0;i<monthList.length;i++){
		      disabledObj(document.getElementsByName("salesBean.chkMonth")[i],true);
		   }
		   for(i=0;i< <%=yearList!=null?yearList.size():0%>; i++){
			  disabledObj(document.getElementsByName("salesBean.chkYear")[i],true);
		   }
		   
	    }else  if(typeSearch.value == 'MONTH'){
	       disabledObj(document.getElementsByName("salesBean.day")[0] ,true);
	       disabledObj(document.getElementsByName("salesBean.dayTo")[0] ,true);
	       for(i=0;i < <%=yearList!=null?yearList.size():0%>; i++){
	    	   disabledObj(document.getElementsByName("salesBean.chkYear")[i],true);
	    	}
	      
	    }else  if(typeSearch.value == 'YEAR'){
	    	disabledObj(document.getElementsByName("salesBean.day")[0] ,true);
	    	disabledObj(document.getElementsByName("salesBean.dayTo")[0] ,true);
	    	for(i=0;i<monthList.length;i++){
	 	       disabledObj(document.getElementsByName("salesBean.chkMonth")[i],true);
	 	    }
	    }
	}

	function chkYear(){
		var year = $('select#yearList').val();
		
		var yearList = $('select#yearList option');

		for(var i=0; i<yearList.size();i++){
			if(yearList[i].value == year){
				$('tr#'+yearList[i].value).show();
			}
			else{
				$('tr#'+yearList[i].value).hide();
			}
		}
	}

	function disabledObj(obj,flag){
	   obj.disabled = flag;
	}

	function showSearchValuePopup(path,currCondNo){
		var currCondTypeValue = "";
		
		/** Set Fro Filter By Parent Listbox **/
		var condType1 = document.getElementsByName("salesBean.condName1")[0].value;
		var condCode1 = document.getElementsByName("salesBean.condCode1")[0].value;
		
		var condType2 = document.getElementsByName("salesBean.condName2")[0].value;
		var condCode2 = document.getElementsByName("salesBean.condCode2")[0].value;
		
		var condType3 = document.getElementsByName("salesBean.condName3")[0].value;
		var condCode3  = document.getElementsByName("salesBean.condCode3")[0].value;
		

		if(currCondNo=='1'){
			currCondTypeValue = document.getElementsByName("salesBean.condName1")[0].value;
		}
		if(currCondNo=='2'){
			currCondTypeValue = document.getElementsByName("salesBean.condName2")[0].value;
		}
		if(currCondNo=='3'){
			currCondTypeValue = document.getElementsByName("salesBean.condName3")[0].value;
		}
		if(currCondNo=='4'){
			currCondTypeValue = document.getElementsByName("salesBean.condName4")[0].value;
		}

		if(currCondTypeValue =='-1'){
	       alert("โปรดเลือก ประเภทขอบเขตข้อมูล");
		}else{
			var param = "";
			var url =  "";
			if(currCondNo=='1'){
				param = "&currCondNo="+currCondNo+"&currCondTypeValue="+currCondTypeValue;
		    }else if(currCondNo=='2'){
		    	param = "&currCondNo="+currCondNo+"&currCondTypeValue="+currCondTypeValue;
				param += "&condType1="+condType1+"&condCode1="+condCode1;
			}else if(currCondNo =='3'){
				param = "&currCondNo="+currCondNo+"&currCondTypeValue="+currCondTypeValue;
				param += "&condType1="+condType1+"&condCode1="+condCode1;
				param += "&condType2="+condType2+"&condCode2="+condCode2;
			}else if(currCondNo =='4'){
				param = "&currCondNo="+currCondNo+"&currCondTypeValue="+currCondTypeValue;
				param += "&condType1="+condType1+"&condCode1="+condCode1;
				param += "&condType2="+condType2+"&condCode2="+condCode2;
				param += "&condType3="+condType3+"&condCode3="+condCode3;
			}
			
			//alert(param);
			
			url = path + "/jsp/searchValuePopupAction.do?do=prepare&action=new"+param;
			window.open(encodeURI(url),"",
					   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
		}
	}
	
	// declare variable using in multiple selection
	var multicode ;
	var multikey ;
	var multiValueDisp
	;
	function setMultiCode(p_code){
		multicode = p_code;
	}

	function setMultiKey(p_key){
		multikey = p_key;
	}

	function setMultiValueDisp(p_value_disp){
		multiValueDisp = p_value_disp;
	}

	function setValueCondition(condNo){
		if(condNo =='1'){
		   document.getElementsByName("salesBean.condCode1")[0].value = multicode;
		   document.getElementsByName("salesBean.condValue1")[0].value = multikey;
		   document.getElementsByName("salesBean.condValueDisp1")[0].value = multiValueDisp;
		}
		if(condNo =='2'){
		   document.getElementsByName("salesBean.condCode2")[0].value = multicode;
		   document.getElementsByName("salesBean.condValue2")[0].value = multikey;
		   document.getElementsByName("salesBean.condValueDisp2")[0].value = multiValueDisp;
		}
		if(condNo =='3'){
		   document.getElementsByName("salesBean.condCode3")[0].value = multicode;
		   document.getElementsByName("salesBean.condValue3")[0].value = multikey;
		   document.getElementsByName("salesBean.condValueDisp3")[0].value = multiValueDisp;
		}
		if(condNo =='4'){
		   document.getElementsByName("salesBean.condCode4")[0].value = multicode;
		   document.getElementsByName("salesBean.condValue4")[0].value = multikey;
		   document.getElementsByName("salesBean.condValueDisp4")[0].value = multiValueDisp;
		}	
	}

	function setMainValue(code,key,desc,condNo){
		if(condNo =='1'){
		   document.getElementsByName("salesBean.condCode1")[0].value = key;
		   document.getElementsByName("salesBean.condValue1")[0].value = code;
		   document.getElementsByName("salesBean.condValueDisp1")[0].value = desc;
		}
		if(condNo =='2'){
		   document.getElementsByName("salesBean.condCode2")[0].value = key;
		   document.getElementsByName("salesBean.condValue2")[0].value = code;
		   document.getElementsByName("salesBean.condValueDisp2")[0].value = desc;
		}
		if(condNo =='3'){
		   document.getElementsByName("salesBean.condCode3")[0].value = key;
		   document.getElementsByName("salesBean.condValue3")[0].value = code;
		   document.getElementsByName("salesBean.condValueDisp3")[0].value = desc;
		}
		if(condNo =='4'){
		   document.getElementsByName("salesBean.condCode4")[0].value = key;
		   document.getElementsByName("salesBean.condValue4")[0].value = code;
		   document.getElementsByName("salesBean.condValueDisp4")[0].value = desc;
		}	
	}

	function clearText(condNo){
		if(condNo =='1'){
		   document.getElementsByName("salesBean.condCode1")[0].value = '';
		   document.getElementsByName("salesBean.condValue1")[0].value = '';
		   document.getElementsByName("salesBean.condValueDisp1")[0].value = '';
		}
		if(condNo =='2'){
		   document.getElementsByName("salesBean.condCode2")[0].value = '';
		   document.getElementsByName("salesBean.condValue2")[0].value = '';
		   document.getElementsByName("salesBean.condValueDisp2")[0].value = '';
		}
		if(condNo =='3'){
		   document.getElementsByName("salesBean.condCode3")[0].value = '';
		   document.getElementsByName("salesBean.condValue3")[0].value = '';
		   document.getElementsByName("salesBean.condValueDisp3")[0].value = '';
		}
		if(condNo =='4'){
		   document.getElementsByName("salesBean.condCode4")[0].value = '';
		   document.getElementsByName("salesBean.condValue4")[0].value = '';
		   document.getElementsByName("salesBean.condValueDisp4")[0].value = '';
		}	
	}

	function set_display_value1(e, field){
		loadValueOnchange(e, field,true);
	}

	function set_display_value2(e, field){
		loadValueOnchange(e, field,true);
	}

	function set_display_value3(e, field){
		loadValueOnchange(e, field,true);
	}

	function set_display_value4(e, field){
		loadValueOnchange(e, field,true);
	}

	//call ajax
	function loadValueOnchange(e, field,change){
		var condType = new Object();
		var condCode = new Object();
		var condValue = new Object();
		var condDisp = new Object();
		switch(field){
		case 1 : {
			condType  = $('#condName1');
			condCode  = $('#condCode1');
	        }break;
		case 2 : {
			condType  = $('#condName2');
			condCode  = $('#condCode2');
			}break;
		case 3 : {
			condType  = $('#condName3');
			condCode  = $('#condCode3');
			}break;
		case 4 : {
			condType  = $('#condName4');
			condCode  = $('#condCode4');
			}break;
		default: break;
		}
		
		
		if(condType.val() == 0){
			alert("โปรดเลือก ประเภทขอบเขตข้อมูล");
			return;
		}
		
		if(condCode.val() !=""){
			loadValue(e, field,change);
		}
		
	}

	//call ajax
	function loadValue(e, field,change){
		var condType = new Object();
		var condCode = new Object();
		var condValue = new Object();
		var condDisp = new Object();
		switch(field){
		case 1 : {
			condType  = $('#condName1');
			condCode  = $('#condCode1');
			condValue = $('#condValue1');
			condDisp  = $('#condValueDisp1');}break;
		case 2 : {
			condType  = $('#condName2');
			condCode  = $('#condCode2');
			condValue = $('#condValue2');
			condDisp  = $('#condValueDisp2');}break;
		case 3 : {
			condType  = $('#condName3');
			condCode  = $('#condCode3');
			condValue = $('#condValue3');
			condDisp  = $('#condValueDisp3');}break;
		case 4 : {
			condType  = $('#condName4');
			condCode  = $('#condCode4');
			condValue = $('#condValue4');
			condDisp  = $('#condValueDisp4');}break;
		default: break;
		}

		if(e == null || (e != null && e.keyCode == 13) || change){
			if(condType.val() == 0){
				alert("โปรดเลือก ประเภทขอบเขตข้อมูล");
				return;
			}
			//alert('condCode.val():'+condCode.val()+',condType.val(:'+condType.val());
			
			$(function(){
				var getData = $.ajax({
					url: "${pageContext.request.contextPath}/jsp/ajax/searchValueQuery.jsp",
					data : "condCode=" + encodeURIComponent(condCode.val()) + "&condType=" + condType.val(),
					//async: false,
					cache: true,
					success: function(getData){
						var returnString = jQuery.trim(getData);
					    if(returnString.split('|')[0] ==''){
						   alert("ไม่พบข้อมูล");
						   condCode.focus();
					       condCode.val('');
					       condValue.val('');
						   condDisp.val('');
					    }else{
						   condValue.val(returnString.split('|')[0]);
						   condDisp.val(returnString.split('|')[1]);
					    }
					}
				}).responseText;
			});
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
			    <jsp:param name="function" value="Report"/>
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
						<html:form action="/jsp/reportAction">
						<jsp:include page="../error.jsp"/>
						
						    <!-- Criteria -->
                            <fieldset>
                           
                            <table width="80%" border="0" align="center" cellpadding="3" cellspacing="1">
									
									<tr>
									  <td colspan="8" align="right">
									  
									    <table width="100%" border="0" align="center" cellpadding="3" cellspacing="1">
										<tr>
										  	<td width="40%" align="right">รอบเวลา &nbsp;&nbsp;<html:select property="salesBean.typeSearch" onchange="chkSearch()">
										         <html:options collection="typeSearchList" property="key" labelProperty="name"/>
									           </html:select>
									        </td>
                                            <td width="60%" align="left">
                                                                                                                                                                             จัดกลุ่มตาม 
                                                <html:select property="salesBean.dispBy">
										           <html:options collection="dispList" property="key" labelProperty="name"/>
								                </html:select></td>
									    </tr>
										</table>
										
									  </td>
                                    </tr>
									<tr>
									  <td colspan="8" align="right">
									    <fieldset>
									    <table width="100%" border="0" align="center" cellpadding="3" cellspacing="1">
										<tr>
										     <td width="5%" align="left">วันที่</td>
										     <td width="95%" align="left">
										      <html:text property="salesBean.day" readonly="true" styleId="day" size="15"></html:text>&nbsp;&nbsp;-&nbsp;&nbsp;<html:text property="salesBean.dayTo" readonly="true" styleId="dayTo" size="15"></html:text>
	                                        </td> 
									    </tr>
										</table>
										</fieldset>
									  </td>
                                    </tr>
									
									<tr>
									  <td colspan="8" align="left">
									     <fieldset>
									       <legend>เดือน</legend>
										    <table width="100%" border="0">
										    <tr>
										     
												<td width="6%" align="left">ปี
												  <html:select property="salesBean.year" styleId="yearList" onchange="chkYear()" >
											         <html:options collection="yearList" property="key" labelProperty="name"/>
									              </html:select>
												</td>
										    </tr>
	                                         <tr>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">01</html:multibox>ม.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">02</html:multibox>ก.พ.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">03</html:multibox>มี.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">04</html:multibox>เม.ย.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">05</html:multibox>พ.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">06</html:multibox>มิ.ย.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">07</html:multibox>ก.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">08</html:multibox>ส.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">09</html:multibox>ก.ย.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">10</html:multibox>ต.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">11</html:multibox>พ.ย.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">12</html:multibox>ธ.ค.</td>
	                                         </tr>
                                        </table>
                                        </fieldset>
                                      </td>
							       </tr>
							      
							       <tr>
									  <td colspan="8" align="left">
									    <fieldset>
									       <legend>ปี</legend>
										    <table width="80%" border="0">
	                                         <tr>
	                                         <%if(yearList != null){ 
	                                              for(int i=0;i<yearList.size();i++){
	                                            	  com.isecinc.core.bean.References ref=(com.isecinc.core.bean.References)yearList.get(i);
	                                              
		                                         %>
		                                          <td width="5%"><html:multibox  property="salesBean.chkYear"><%=ref.getKey()%></html:multibox><%=ref.getName() %></td>
	                                         <%}} %>
	                                         </tr>
                                        </table>
                                       </fieldset>
                                      </td>
							     </tr>
									
				          </table>
  
						<table width="80%" align="center" border="0" cellpadding="1" cellspacing="1" >
						 
                          <tr>
                            <td>
	                            <fieldset id="condition-frame">
	                            <legend>เงื่อนไขในการเลือกข้อมูล</legend>
		                            <table width="100%"  border="0" cellpadding="1" cellspacing="1" >
		                              <tr nowarp="nowarp" >
		                                <td align="left" width="20%">ขอบเขตข้อมูล </td>
		                                <td align="center" width="5%">=</td>
		                                <td align="left" width="75%">ข้อมูลเงื่อนไข</td>
		                              </tr>
		                              <tr nowarp="nowarp">
		                                <td align="left">
		                                    <html:select property="salesBean.condName1" onchange="clearText(1);" styleId="condName1">
										        <html:options collection="conditionList" property="key" labelProperty="name"/>
									        </html:select>
								        </td>
		                                <td align="center">
		                                   =
		                                </td>
		                                <td align="left">
											 <html:text property="salesBean.condCode1" styleId="condCode1" style="width:100px;" onkeyup="loadValue(event, 1,true);" onchange="set_display_value1(event, 1);"/>&nbsp;   
											 <a href="javascript:showSearchValuePopup('${pageContext.request.contextPath}','1');">
									           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" />
									        </a>
									         <a href="javascript:clearText('1');">
									          <img border=0 src="${pageContext.request.contextPath}/icons/clean.png"/>
									        </a>                                        
									         <html:text property="salesBean.condValueDisp1" readonly ="true" styleId="condValueDisp1" style="width:200px"></html:text>
									         <html:hidden property="salesBean.condValue1" styleId="condValue1"></html:hidden>
								        </td>
		                              </tr>
		                             <tr nowarp="nowarp" >
		                                <td align="left">
		                                    <html:select property="salesBean.condName2" onchange="clearText(2);" styleId="condName2" >
										        <html:options collection="conditionList" property="key" labelProperty="name"/>
									        </html:select>
								       </td>
		                                <td align="center">
		                                   =
		                                </td>
		                                <td align="left">
											 <html:text property="salesBean.condCode2" styleId="condCode2" style="width:100px;" onkeyup="loadValue(event, 2,true);" onchange="set_display_value2(event, 2);"/>&nbsp;
											 <a href="javascript:showSearchValuePopup('${pageContext.request.contextPath}','2');">
									           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" />
									        </a>
									         <a href="javascript:clearText('2');">
									          <img border=0 src="${pageContext.request.contextPath}/icons/clean.png"/>
									        </a>
                                             <html:text property="salesBean.condValueDisp2" readonly="true" styleId="condValueDisp2" style="width:200px"></html:text>
									         <html:hidden property="salesBean.condValue2"  styleId="condValue2"></html:hidden>
									        
								       </td>
		                              </tr>
		                             <tr nowarp="nowarp">
		                                <td align="left">
		                                    <html:select property="salesBean.condName3" onchange="clearText(3);" styleId="condName3">
										        <html:options collection="conditionList" property="key" labelProperty="name"/>
									        </html:select>
								       </td>
		                                <td align="center">
		                                   =
		                                </td>
		                                <td align="left">
											 <html:text property="salesBean.condCode3" styleId="condCode3" style="width:100px;" onkeyup="loadValue(event, 3,true);" onchange="set_display_value3(event, 3);"/>&nbsp;
											  <a href="javascript:showSearchValuePopup('${pageContext.request.contextPath}','3');">
									           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" />
									        </a>
									         <a href="javascript:clearText('3');">
									          <img border=0 src="${pageContext.request.contextPath}/icons/clean.png"/>
									        </a>
                  							 <html:text property="salesBean.condValueDisp3" styleId="condValueDisp3" readonly="true" style="width:200px"></html:text>
									         <html:hidden property="salesBean.condValue3"  styleId="condValue3"></html:hidden>
									       
								       </td>
		                              </tr>
		                              <tr nowarp="nowarp">
		                                <td align="left">
		                                    <html:select property="salesBean.condName4" onchange="clearText(4);" styleId="condName4">
										        <html:options collection="conditionList" property="key" labelProperty="name"/>
									        </html:select>
								        </td>
		                                <td align="center">
		                                   =
		                                </td>
		                                <td align="left" >
                                             <html:text property="salesBean.condCode4" styleId="condCode4" style="width:100px;" onkeyup="loadValue(event, 4,true);" onchange="set_display_value4(event, 4);"/>&nbsp;
											  <a href="javascript:showSearchValuePopup('${pageContext.request.contextPath}','4');">
									           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" />
									        </a>
									         <a href="javascript:clearText('4');">
									          <img border=0 src="${pageContext.request.contextPath}/icons/clean.png"/>
									        </a>
                  							 <html:text property="salesBean.condValueDisp4" styleId="condValueDisp4" readonly="true" style="width:200px"></html:text>
									         <html:hidden property="salesBean.condValue4"  styleId="condValue4"></html:hidden>							 
								        </td>
		                              </tr>
	                              </table>
	                           </fieldset>
                            </td>
                            <td>
	                            <fieldset id="display-frame">
	                            <legend>การเลือกแสดงข้อมูล</legend>
			                             <table width="100%"  border="0" cellpadding="1" cellspacing="1">
			                        
			                              <tr>
			                                <td align="left">ประเภทข้อมูล</td>
			                               
			                              </tr>
			                              <tr>
			                                <td align="left">
			                                  <html:select property="salesBean.colNameDisp1">
											        <html:options collection="dispColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                              </tr>
			                              <tr>
			                                <td align="left">
			                                  <html:select property="salesBean.colNameDisp2">
											        <html:options collection="dispColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                              </tr>
			                            <tr>
			                                <td align="left">
			                                  <html:select property="salesBean.colNameDisp3">
											        <html:options collection="dispColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                              </tr>
			                             <tr>
			                                <td align="left">
			                                  <html:select property="salesBean.colNameDisp4">
											        <html:options collection="dispColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td> 
			                              </tr>
		                              </table>
			                    </fieldset>        
                            </td>
                          </tr>
                        </table>	
                     </fieldset>					
                     <!-- Criteria -->
					   
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
								<a href="javascript:search('${pageContext.request.contextPath}')">
								  <input type="button" value="ค้นหา" class="newPosBtn"> 
								</a>
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value="Clear" class="newPosBtn">
								</a>
								<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export" class="newPosBtn">
								</a>
							</td>
						</tr>
					</table>
					<!-- RESULT -->
					  <%out.print(Utils.isNull(session.getAttribute("RESULT"))); %>	
				    <!-- RESULT -->
					<jsp:include page="../searchCriteria.jsp"></jsp:include>
					
					<!-- hidden field -->
					<input type="hidden" name="page" value="<%=request.getParameter("page") %>"/>
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