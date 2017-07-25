<%@page import="util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.report.salesanalyst.helper.Utils" %>
<%@page import="com.isecinc.pens.report.salesanalyst.SAInitial"%>
<%
if(request.getParameter("action") != null){
    SAInitial.getInstance().initSession(request);
 }

String typeSearch = Utils.isNull(request.getAttribute("DATA"));
java.util.List yearList = null;
if(session.getAttribute("yearList") != null) 
	yearList = (java.util.List)session.getAttribute("yearList");

java.util.List yearListASC = null;
if(session.getAttribute("yearListASC") != null) 
	yearListASC = (java.util.List)session.getAttribute("yearListASC");

String screenWidth = "";
if(session.getAttribute("screenWidth") != null){ 
	screenWidth = (String)session.getAttribute("screenWidth");
}
System.out.println("screenWidth:"+screenWidth);
%>
<html>
<head>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="cache-control" content="no-store" />
<!-- <meta http-equiv="cache-control" content="no-cache" /> -->
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" /> 
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
select#summaryType{width:150px;}
fieldset#condition-frame{height:186px}
fieldset#display-frame{height:186px}

fieldset {
    font-family: sans-serif;
    border: 1px solid #1F497D;
  /*   background: #ddd; */
    border-radius: 5px;
    padding: 10px;
}
fieldset legend {
    background: #3F5C93; /* #1F497D; */
    color: #fff;
    padding: 5px 10px ;
    font-size: 14px;
    border-radius: 2px;
    box-shadow: 0 0 0 1px #ddd;
    margin-left: 20px;
}
.txt_style {
	font-family: "Lucida Grande", Tahoma, Arial, Verdana, sans-serif;
	font-size: 14px;
	font-weight: normal;
	text-decoration: none;
}
 #scroll {
<%if(!"0".equals(screenWidth)){%>
    width:<%=screenWidth%>px; 
    background:#A3CBE0;
	border:1px solid #000;
	overflow:auto;
	white-space:nowrap;
	box-shadow:0 0 25px #000;
<%}%>
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesAnalyst.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.tablednd_0_5.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/date.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.tablesorter.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>


<script type="text/javascript" language="javascript">

function exportData(path,type){

	var columnCount = document.getElementsByName("columnCount")[0].value;
	var cond1 = document.getElementsByName("salesBean.condName1")[0];
    var cond2 = document.getElementsByName("salesBean.condName2")[0];
    var cond3 = document.getElementsByName("salesBean.condName3")[0];
    var cond4 = document.getElementsByName("salesBean.condName4")[0];

    var condDisp1 = cond1.options[cond1.selectedIndex].text;
    var condDisp2 = cond1.options[cond2.selectedIndex].text;
    var condDisp3 = cond1.options[cond3.selectedIndex].text;
    var condDisp4 = cond1.options[cond4.selectedIndex].text;
    //var condDisp5 = document.getElementsByName("salesBean.condName1")[0];
    //var maxOrderedDate = session.getAttribute("maxOrderedDate");
    //var maxOrderedTime = session.getAttribute("maxOrderedTime");
    
    var maxOrderedDate = document.getElementById('maxOrderedDate').value;
    var maxOrderedTime = document.getElementById('maxOrderedTime').value;
  
    
    //alert("xx"+maxOrderedDate);
	var condDisp5 = "ข้อมูล ณ วันที่  "+maxOrderedDate+" เวลา "+maxOrderedTime;
	//var condDisp5 = "ข้อมูล ณ วันที่ : "+maxOrderedDate; 
	//var condDisp5 = "ข้อมูล ณ วันที่ : ";
	
    // alert(cond1.value+":"+);
    var param  ="&condDisp1="+condDisp1;
        param +="&condDisp2="+condDisp2;
        param +="&condDisp3="+condDisp3;
        param +="&condDisp4="+condDisp4;
        param +="&condDisp5="+condDisp5;
                                                                
	document.salesAnalystReportForm.action = path + "/jsp/salesAnalystReportAction.do?do=export&columnCount="+columnCount+param;
	document.salesAnalystReportForm.submit();
	
	return true;
}

function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('day'));
	new Epoch('epoch_popup','th',document.getElementById('dayTo'));
	chkSearch();
	chkYear();
}

function chkSearch(){
   var typeSearch = document.getElementsByName("salesBean.typeSearch")[0];
   //alert(typeSearch.value);
	disabledObj(document.getElementsByName("salesBean.day")[0] ,false);
	disabledObj(document.getElementsByName("salesBean.dayTo")[0] ,false);
	
	var quarterList = document.getElementsByName("salesBean.chkQuarter");
	for(i=0;i<quarterList.length;i++){
	   disabledObj(document.getElementsByName("salesBean.chkQuarter")[i],false);
	}
	
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
	   for(i=0;i<quarterList.length;i++){
		   disabledObj(document.getElementsByName("salesBean.chkQuarter")[i],true);
		}
    }else  if(typeSearch.value == 'MONTH'){
       disabledObj(document.getElementsByName("salesBean.day")[0] ,true);
       disabledObj(document.getElementsByName("salesBean.dayTo")[0] ,true);
       for(i=0;i < <%=yearList!=null?yearList.size():0%>; i++){
    	   disabledObj(document.getElementsByName("salesBean.chkYear")[i],true);
    	}
       for(i=0;i<quarterList.length;i++){
    	   disabledObj(document.getElementsByName("salesBean.chkQuarter")[i],true);
       }
    }else  if(typeSearch.value == 'QUARTER'){
   	   disabledObj(document.getElementsByName("salesBean.day")[0] ,true);
       disabledObj(document.getElementsByName("salesBean.dayTo")[0] ,true);
   	   for(i=0;i<monthList.length;i++){
	      disabledObj(document.getElementsByName("salesBean.chkMonth")[i],true);
	   }
	   	for(i=0;i < <%=yearList!=null?yearList.size():0%>; i++){
	 	   disabledObj(document.getElementsByName("salesBean.chkYear")[i],true);
	 	}
    }else  if(typeSearch.value == 'YEAR'){
    	disabledObj(document.getElementsByName("salesBean.day")[0] ,true);
    	disabledObj(document.getElementsByName("salesBean.dayTo")[0] ,true);
    	for(i=0;i<monthList.length;i++){
 	       disabledObj(document.getElementsByName("salesBean.chkMonth")[i],true);
 	    }
    	for(i=0;i<quarterList.length;i++){
    	   disabledObj(document.getElementsByName("salesBean.chkQuarter")[i],true);
        }
    }
}

function chkYear(){
	var year = $('select#yearList').val();
	
	var yearList = $('select#yearList option');

	//Month
	for(var i=0; i<yearList.size();i++){
		if(yearList[i].value == year){
			$('tr#'+yearList[i].value).show();
		}
		else{
			$('tr#'+yearList[i].value).hide();
		}
	}
	
	//Quarter
	for(var i=0; i<yearList.size();i++){
		if(yearList[i].value == year){
			$('tr#'+yearList[i].value+"_Q").show();
		}
		else{
			$('tr#'+yearList[i].value+"_Q").hide();
		}
	}
}

function disabledObj(obj,flag){
   obj.disabled = flag;
}

function search(path, type) {
	if(validateCriteria()){
	   document.salesAnalystReportForm.action = path + "/jsp/salesAnalystReportAction.do?do=search&rf=N&";
	   document.salesAnalystReportForm.submit();
	   return true;
	}
	return false;
}

function searchOrder(path, type, field) {
       var search_url2= path + "/jsp/salesAnalystReportAction.do?do=search&rf=N&order_type="+type+"&order_by_name="+field;
       //alert(search_url2);
	   document.salesAnalystReportForm.action = search_url2;
	   document.salesAnalystReportForm.submit();
	   return true;
}

function getSQL(path, type) {
	if(validateCriteria()){
	   document.salesAnalystReportForm.action = path + "/jsp/salesAnalystReportAction.do?do=getSQL";
	   document.salesAnalystReportForm.submit();
	   return true;
	}
	return false;
}

function saveProfile(path, type) {
	var profileId = $('#profileId').val();
	if(profileId != '0'){
	   document.salesAnalystReportForm.action = path + "/jsp/salesAnalystReportAction.do?do=saveProfile";
	   document.salesAnalystReportForm.submit();
	   return true;
	}
	return false;
}

function changeProfile(path, type) {
	var profileId = $('#profileId').val();
	if(profileId != '0'){
	   document.salesAnalystReportForm.action = path + "/jsp/salesAnalystReportAction.do?do=changeProfile";
	   document.salesAnalystReportForm.submit();
	   return true;
	}
	return false;
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

	 }else  if(typeSearch.value == 'QUARTER'){
		 var obj = document.getElementsByName("salesBean.chkQuarter");
		 if( !isChkOne(obj)){
			 alert("โปรดเลือกแสดงข้อมูลอย่างน้อย 1 ไตรมาส");
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
	
	if (document.getElementsByName("salesBean.condName1")[0].value == 'INVOICE_NO'){
        return true;
	}
	
	if (document.getElementsByName("salesBean.condName1")[0].value == 'SALES_ORDER_NO'){
		return true;
	}

	if (document.getElementsByName("salesBean.condName2")[0].value == 'INVOICE_NO') {
	   return true;
	}
	
	if (document.getElementsByName("salesBean.condName2")[0].value == 'SALES_ORDER_NO'){
	   return true;
	}

	if (document.getElementsByName("salesBean.condName3")[0].value == 'INVOICE_NO'){
	   return true;
	}
	
	if (document.getElementsByName("salesBean.condName3")[0].value == 'SALES_ORDER_NO'){
	   return true;
	}

	if (document.getElementsByName("salesBean.condName4")[0].value == 'INVOICE_NO'){
	   return true;
	}
	
	if (document.getElementsByName("salesBean.condName4")[0].value == 'SALES_ORDER_NO'){
	   return true;
	}
	
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


function refereshListBox(path){
    /** remove ListBox Value **/
	remove_all(path);
	/** Init new ListBox **/
	var returnString =document.getElementsByName("salesBean.returnString")[0].value;
	if(returnString ==''){
		append_item(path, "0", "ไม่เลือก");
	}else{
		var t = returnString.split(",");  //ถ้าเจอวรรคแตกเก็บลง array t
		for(var i=0; i<t.length ; i++){
	        var code = t[i].split("|")[0];
	        var value = t[i].split("|")[1];
		    append_item(path, code, value);
		}
	}
}

//manipulation
function get_selected(path){
	return $(path + " option:selected");
}
function remove_selected(path){
	$(path + " option:selected").remove();
}
function append_item(path, value, text){
	$(path).append("<option value='" + value + "'>" + text + "</option>");
}
function select_first(path){
	$(path + " option:first-child").attr("selected","selected");
}
function select_last(path){
	$(path + " option:last-child").attr("selected","selected");
}
function remove_all(path){
	$(path).empty();
}
function select_by_value(path, value){
	$(path + " option[value=" + value + "]").attr("selected","selected");
}
function remove_by_value(path, value){
	$(path + " option[value=" + value + "]").remove();
}

function showSearchValuePopup(path,currCondNo){
	var currCondTypeValue = "";
	
	/** Set Fro Filter By Parent Listbox **/
	var condType1 = document.getElementsByName("salesBean.condName1")[0].value;
	var condCode1 = document.getElementsByName("salesBean.condCode1")[0].value;
	var condValueDisp1 = document.getElementsByName("salesBean.condValueDisp1")[0].value;
	
	var condType2 = document.getElementsByName("salesBean.condName2")[0].value;
	var condCode2 = document.getElementsByName("salesBean.condCode2")[0].value;
	var condValueDisp2 = document.getElementsByName("salesBean.condValueDisp2")[0].value;
	
	var condType3 = document.getElementsByName("salesBean.condName3")[0].value;
	var condCode3  = document.getElementsByName("salesBean.condCode3")[0].value;
	var condValueDisp3 = document.getElementsByName("salesBean.condValueDisp3")[0].value;

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
			param += "&condType1="+condType1+"&condCode1="+condCode1+"&condValueDisp1="+condValueDisp1;
		}else if(currCondNo =='3'){
			param = "&currCondNo="+currCondNo+"&currCondTypeValue="+currCondTypeValue;
			param += "&condType1="+condType1+"&condCode1="+condCode1+"&condValueDisp1="+condValueDisp1;
			param += "&condType2="+condType2+"&condCode2="+condCode2+"&condValueDisp2="+condValueDisp2;
		}else if(currCondNo =='4'){
			param = "&currCondNo="+currCondNo+"&currCondTypeValue="+currCondTypeValue;
			param += "&condType1="+condType1+"&condCode1="+condCode1+"&condValueDisp1="+condValueDisp1;
			param += "&condType2="+condType2+"&condCode2="+condCode2+"&condValueDisp2="+condValueDisp2;
			param += "&condType3="+condType3+"&condCode3="+condCode3+"&condValueDisp3="+condValueDisp3;
		}
		
		param +="&decode=true&load=1";
		//alert(param);
		
		url = path + "/jsp/searchValuePopupAction.do?do=prepare&action=new"+param;
	//	window.open(encodeURI(url),"",
				   //"menubar=no,resizable=no,titlebar=no,location=no,toolbar=no,scrollbars=yes,status=no,menubar=no,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
	      PopupCenterFullHeight(encodeURI(url),"","700");
	}
}

// declare variable using in multiple selection
var multicode ;
var multikey ;
var multiValueDisp;
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

/**** For Sort  Table **************************************************/

$(document).ready(function() {
	$('.link-sort').click(function(e) {
		var $sort = this;
		var $table = $('#sort-table');
		var $rows = $('tbody > tr',$table);
		var id = $(this).attr("id");
		//alert(id);
		$rows.sort(function(a, b){
			var keyA = 0;
			var keyB = 0;
			
            if($('td.sort_'+id,a).text() != '' && $('td.sort_'+id,b).text()){
				
				if(isNumber($('td.sort_'+id,a).text()) && isNumber($('td.sort_'+id,b).text())){
					keyA = parseFloat(ReplaceAll($('td.sort_'+id,a).text(),",","",false));
					keyB = parseFloat(ReplaceAll($('td.sort_'+id,b).text(),",","",false));
				}else{
				    keyA = $('td.sort_'+id,a).text();
				    keyB = $('td.sort_'+id,b).text();
				}
				//alert(keyA+":"+keyB);
				if($($sort).hasClass('asc')){
					//alert("asc");
					return (keyA > keyB) ? 1 : -1;
				} else {
					//alert("desc");
					return (keyA < keyB) ? 1 : -1;
				}
			}
		});
		var no =1;
		$.each($rows, function(index, row){
			//alert(row[0].cells);
			row.cells[0].innerHTML = no;
			$table.append(row);
			no++;
		});
		e.preventDefault();
	});
	
});

 function isNumber(value){
	var numberRegex = /^[+-]?\d+(\.\d+)?([eE][+-]?\d+)?$/;
	var str = value;
	str = ReplaceAll(str,",","",true);
	//alert(str);
	if(numberRegex.test(str)) {
	   //alert('I am a number');
	   return true;
	}
	return false;
}

function ReplaceAll( inText, inFindStr, inReplStr, inCaseSensitive ) {
    var searchFrom = 0;
    var offset = 0;
    var outText = "";
    var searchText = "";
    if ( inCaseSensitive == null ) {
       inCaseSensitive = false;
    }
    if ( inCaseSensitive ) {
       searchText = inText.toLowerCase();
       inFindStr = inFindStr.toLowerCase();
    } else {
       searchText = inText;
    }
    offset = searchText.indexOf( inFindStr, searchFrom );
    while ( offset != -1 ) {
       outText += inText.substring( searchFrom, offset );
       outText += inReplStr;
       searchFrom = offset + inFindStr.length;
       offset = searchText.indexOf( inFindStr, searchFrom );
    }
    outText += inText.substring( searchFrom, inText.length );

    return ( outText );
 }
 
 function controlRoleTab(){
	 var roleTabText =document.getElementsByName("roleTabText")[0];
	 var span = document.getElementById('roleTabSpan');
	 while( span.firstChild ) {
	     span.removeChild( span.firstChild );
	 }
	 
	 if("" ==roleTabText.value){
		 //show 
		 document.getElementById("roleTab").style.display = 'block';
		 // document.getElementById("roleTabText").innerHTML = "(Hide Role )";
		 
		 span.appendChild( document.createTextNode("(Hide Role Detail") );
		 
		 //alert(document.getElementById("roleTabText").innerHTML);
		 roleTabText.value ="show";
	 }else{
		 //HIDE
		 document.getElementById("roleTab").style.display = 'none';    
		 // document.getElementById("roleTabText").innerHTML = "(Show Role )";
		 span.appendChild( document.createTextNode("(Show Role Detail)") );
		 roleTabText.value ="";
	 }
 }

//String headerHtml  = SAGenerate.genHeaderReportExportExcel( user,formBean.getSalesBean(),columnCount,condDisp1,condDisp2,condDisp3,condDisp4,condDisp5).toString();
 function genHeadTable(){
	 var columnCount = document.getElementsByName("columnCount")[0].value;
	 
	 var cond1 = document.getElementsByName("salesBean.condName1")[0];
     var cond2 = document.getElementsByName("salesBean.condName2")[0];
     var cond3 = document.getElementsByName("salesBean.condName3")[0];
     var cond4 = document.getElementsByName("salesBean.condName4")[0];

     var condDisp1 = cond1.options[cond1.selectedIndex].text;
     var condDisp2 = cond1.options[cond2.selectedIndex].text;
     var condDisp3 = cond1.options[cond3.selectedIndex].text;
     var condDisp4 = cond1.options[cond4.selectedIndex].text;
	    
     var maxOrderedDate = document.getElementById('maxOrderedDate').value;
     var maxOrderedTime = document.getElementById('maxOrderedTime').value;
   
     //alert("xx"+maxOrderedDate);
 	 var condDisp5 = "ข้อมูล ณ วันที่  "+maxOrderedDate+" เวลา "+maxOrderedTime;
 	
     var headerTable = "";
	 var param   = "columnCount="+columnCount;
	     param  += "&condDisp1="+condDisp1;
	     param  += "&condDisp2="+condDisp2;
	     param  += "&condDisp3="+condDisp3;
	     param  += "&condDisp4="+condDisp4;
	     param  += "&condDisp5="+condDisp5
	     
	 $(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/genHeaderExcelAjax.jsp",
				data : param ,
				async: false,
				cache: true,
				success: function(getData){
					headerTable = jQuery.trim(getData);
				}
			}).responseText;
		});
	 
	// alert(headerTable);
	 return headerTable;
 }
 
 function fnExcelReport(){
	 var style ="<style>"
	         +".summary{"
		     +"   font-weight: bold;"
		     +" } ";
		     +"</style> ";
	 var headerTable = genHeadTable();
	// alert(headerTable);
	 
     var tab_text= headerTable+"\n <table border='2px'><tr bgcolor=''>";
     var textRange; var j=0;
     tab = document.getElementById('sort-table'); // id of table

     for(j = 0 ; j < tab.rows.length ; j++){     
         tab_text=tab_text+tab.rows[j].innerHTML+"</tr>";
         //tab_text=tab_text+"</tr>";
     }

     tab_text=tab_text+"</table>";
     tab_text= tab_text.replace(/<A[^>]*>|<\/A>/g, "");//remove if u want links in your table
     tab_text= tab_text.replace(/<img[^>]*>/gi,""); // remove if u want images in your table
     tab_text= tab_text.replace(/<input[^>]*>|<\/input>/gi, ""); // reomves input params

     var ua = window.navigator.userAgent;
     var msie = ua.indexOf("MSIE "); 

     if (msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./))      // If Internet Explorer
     {
         txtArea1.document.open("txt/html","replace");
         txtArea1.document.write(tab_text);
         txtArea1.document.close();
         txtArea1.focus(); 
         sa=txtArea1.document.execCommand("SaveAs",true,"Say Thanks to Sumit.xls");
     }else{                 //other browser not tested on IE 11
         sa = window.open('data:application/vnd.ms-excel,' + encodeURIComponent(tab_text));  
     }
     return (sa);
 }
</script>
<!-- Move for new index. -->
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe(); MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">

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
				<jsp:param name="function" value="SalesAnalysis"/>
				<jsp:param name="code" value=""/>
			</jsp:include>
			<!-- For Export To Excel display -->
			<iframe id="txtArea1" style="display:none"></iframe>
			
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
						<html:form action="/jsp/salesAnalystReportAction">
						<html:hidden property="salesBean.returnString"/>
						<input type="hidden" name="roleTabText" id="roleTabText"/>
			            <jsp:include page="../error.jsp"/>	
			               <fieldset>
			                <table width="80%" border="0" align="center" cellpadding="3" cellspacing="1">
			                  <tr><td>
                               <%out.println(Utils.isNull(session.getAttribute("USER_ROLE_INFO"))); %>
                              </td></tr>
                            </table>
                           </fieldset>
                           
                           <fieldset>
			                <table width="80%" border="0" align="center" cellpadding="1" cellspacing="1">
			                  <tr><td>
			                  
		                            <fieldset>
		                             <legend><b>รูปแบบการค้นหาที่ใช้ประจำ</b></legend>
					                <table width="80%" border="0" align="center" cellpadding="1" cellspacing="1">
					                  <tr>
					                      <td width="15%" align="left">
		                                       <html:select property="salesBean.profileId" styleId="profileId" onchange="changeProfile('${pageContext.request.contextPath}','')" styleClass="txt_style" >
											         <html:options collection="profileList" property="key" labelProperty="name"/>
									            </html:select>
		                                  </td>
		                                  <td width="65%" align="left">
		                                    <input type="button" value="บันทึกรูปแบบการค้นหา" class="newPosBtnLong" style="width: 180px;" 
												     onClick="javascript:saveProfile('${pageContext.request.contextPath}','admin')" />
										 </td>
		                              </tr>
		                            </table>
		                           </fieldset>
                                </td> </tr>
                            </table>
                           </fieldset>
                           
                            <!-- Criteria -->
                            <fieldset>
                           
                            <table width="80%" border="0" align="center" cellpadding="3" cellspacing="1">
									<tr><td colspan=8 align="left"><b>ข้อมูล ณ วันที่ &nbsp;:&nbsp;<%=(String)session.getAttribute("maxOrderedDate")%>&nbsp;&nbsp;เวลา&nbsp;:<%=(String)session.getAttribute("maxOrderedTime")%></b></td>
									</tr>
									<tr>
									  <td colspan="8" align="right">
									    <table width="100%" border="0" align="center" cellpadding="3" cellspacing="1">
										<tr class="txt_style" >
									  	<td width="13%" align="left">รอบเวลา &nbsp;&nbsp;<html:select property="salesBean.typeSearch" onchange="chkSearch()" styleClass="txt_style">
									         <html:options collection="typeSearchList" property="key" labelProperty="name"/>
								           </html:select>
								           </td>
									     <td width="6%" align="right">วันที่</td>
									     <td width="27%" align="left">
									      <html:text property="salesBean.day" readonly="true" styleId="day" size="15"></html:text>&nbsp;&nbsp;-&nbsp;&nbsp;<html:text property="salesBean.dayTo" readonly="true" styleId="dayTo" size="15"></html:text>
                                        </td> 
										<td width="2%" align="right">ปี</td>
										<td width="6%" align="left">
										  <html:select property="salesBean.year" styleId="yearList" onchange="chkYear()" styleClass="txt_style">
									         <html:options collection="yearList" property="key" labelProperty="name"/>
							              </html:select>
										</td>
									    <td width="8%" align="right">จัดกลุ่มตาม</td>
									    <td width="20%" align="left">
									        <html:select property="salesBean.groupBy" styleClass="txt_style">
										         <html:options collection="groupByList" property="key" labelProperty="name"/>
								            </html:select>
                                        </td>
                                       
                                       <input type="hidden" name="maxOrderedDate" value=<%=(String)session.getAttribute("maxOrderedDate")%> id="maxOrderedDate"/>
                                       <input type="hidden" name="maxOrderedTime" value=<%=(String)session.getAttribute("maxOrderedTime")%> id="maxOrderedTime"/>
									</tr>
										</table>
									  </td>
                                    </tr>
									
									<tr>
									  <td colspan="8" align="left">
									     <fieldset>
									       <legend>เดือน</legend>
										    <table width="100%" border="0">
										    <c:forEach var="item" items="${yearList}" >
	                                         <tr id="${item.key}" class="txt_style" >
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}01</html:multibox>ม.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}02</html:multibox>ก.พ.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}03</html:multibox>มี.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}04</html:multibox>เม.ย.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}05</html:multibox>พ.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}06</html:multibox>มิ.ย.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}07</html:multibox>ก.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}08</html:multibox>ส.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}09</html:multibox>ก.ย.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}10</html:multibox>ต.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}11</html:multibox>พ.ย.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}12</html:multibox>ธ.ค.</td>
	                                         </tr>
	                                         </c:forEach>
                                        </table>
                                        </fieldset>
                                      </td>
							       </tr>
							       <tr>
									  <td colspan="8" align="left">
									       <fieldset>
									       <legend>ไตรมาส</legend>
										    <table width="100%" border="0">
										     <c:forEach var="item" items="${yearList}" >
		                                         <tr id="${item.key}_Q" class="txt_style" >
		                                          <td width="5%"><html:multibox  property="salesBean.chkQuarter">${item.key}1</html:multibox>ไตรมาส 1</td>
		                                          <td width="5%"><html:multibox  property="salesBean.chkQuarter">${item.key}2</html:multibox>ไตรมาส 2</td>
		                                          <td width="5%"><html:multibox  property="salesBean.chkQuarter">${item.key}3</html:multibox>ไตรมาส 3</td>
		                                          <td width="5%"><html:multibox  property="salesBean.chkQuarter">${item.key}4</html:multibox>ไตรมาส 4</td>
		                                         </tr>
		                                     </c:forEach>
                                           </table>
                                        </fieldset>
                                      </td>
							        </tr>
							       <tr>
									  <td colspan="8" align="left">
									    <fieldset>
									       <legend>ปี</legend>
										    <table width="80%" border="0">
	                                         <tr class="txt_style" >
	                                         <%if(yearListASC != null){ 
	                                              for(int i=0;i<yearListASC.size();i++){
	                                            	  com.isecinc.core.bean.References ref=(com.isecinc.core.bean.References)yearListASC.get(i);
	                                              
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
		                              <tr nowarp="nowarp" class="txt_style" >
		                                <td align="left" width="20%">ขอบเขตข้อมูล </td>
		                                <td align="center" width="5%">=</td>
		                                <td align="left" width="75%">ข้อมูลเงื่อนไข</td>
		                              </tr>
		                              <tr nowarp="nowarp" class="txt_style" >
		                                <td align="left">
		                                    <html:select property="salesBean.condName1" onchange="clearText(1);" styleId="condName1">
										        <html:options collection="conditionList" property="key" labelProperty="name"/>
									        </html:select>
								        </td>
		                                <td align="center" >
		                                   =
		                                </td>
		                                <td align="left">
											 <html:text property="salesBean.condCode1" styleId="condCode1" style="width:100px;" onkeyup="loadValue(event, 1,true);" onchange="set_display_value1(event, 1);"/>&nbsp;   
											 <a href="javascript:showSearchValuePopup('${pageContext.request.contextPath}','1');">
									           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" width="20px" height="20px"/>
									        </a>
									        &nbsp;
									         <a href="javascript:clearText('1');">
									          <img border=0 src="${pageContext.request.contextPath}/icons/clean.png" width="20px" height="20px"/>
									        </a>                                        
									         <html:text property="salesBean.condValueDisp1" readonly ="true" styleId="condValueDisp1" style="width:200px"></html:text>
									         <html:hidden property="salesBean.condValue1" styleId="condValue1"></html:hidden>
								        </td>
		                              </tr>
		                             <tr nowarp="nowarp" class="txt_style" >
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
									           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"  width="20px" height="20px"/>
									        </a>
									        &nbsp;
									         <a href="javascript:clearText('2');">
									          <img border=0 src="${pageContext.request.contextPath}/icons/clean.png"  width="20px" height="20px"/>
									        </a>
                                             <html:text property="salesBean.condValueDisp2" readonly="true" styleId="condValueDisp2" style="width:200px"></html:text>
									         <html:hidden property="salesBean.condValue2"  styleId="condValue2"></html:hidden>
									        
								       </td>
		                              </tr>
		                             <tr nowarp="nowarp" class="txt_style" >
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
									           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"  width="20px" height="20px"/>
									        </a>
									        &nbsp;
									         <a href="javascript:clearText('3');">
									          <img border=0 src="${pageContext.request.contextPath}/icons/clean.png"  width="20px" height="20px"/>
									        </a>
                  							 <html:text property="salesBean.condValueDisp3" styleId="condValueDisp3" readonly="true" style="width:200px"></html:text>
									         <html:hidden property="salesBean.condValue3"  styleId="condValue3"></html:hidden>
									       
								       </td>
		                              </tr>
		                              <tr nowarp="nowarp" class="txt_style" >
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
									           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"  width="20px" height="20px"/>
									        </a>
									        &nbsp;
									         <a href="javascript:clearText('4');">
									          <img border=0 src="${pageContext.request.contextPath}/icons/clean.png" width="20px" height="20px"/>
									        </a>
                  							 <html:text property="salesBean.condValueDisp4" styleId="condValueDisp4" readonly="true" style="width:200px"></html:text>
									         <html:hidden property="salesBean.condValue4"  styleId="condValue4"></html:hidden>							 
								        </td>
		                              </tr>
		                              
		                              <tr nowarp="nowarp" >
		                                <%-- <td align="left" width="20%"></td>
		                                <td align="center" width="5%"></td>
		                                <td align="left" width="75%"><html:radio property="salesBean.includePos" value="Y" />รวม Pos
		                                &nbsp;<html:radio property="salesBean.includePos" value="N"/>Offtake</td> --%>
		                              </tr>
		                              
	                              </table>
	                           </fieldset>
                            </td>
                            <td>
	                            <fieldset id="display-frame">
	                            <legend>การเลือกแสดงข้อมูล</legend>
			                             <table width="100%"  border="0" cellpadding="1" cellspacing="1">
			                              <tr>
			                              	<td colspan="3">
			                              		<html:select property="salesBean.summaryType" styleId="summaryType">
											    	<html:options collection="summaryTypeList" property="key" labelProperty="name"/>
									         	</html:select>
			                              	</td>
			                              </tr>
			                              <tr class="txt_style" >
			                                <td align="left">ประเภทข้อมูล</td>
			                                <td align="left">หน่วย</td>
			                                <td align="left">เปรียบเทียบ</td>
			                              </tr>
			                              <tr class="txt_style" >
			                                <td align="left">
			                                  <html:select property="salesBean.colNameDisp1">
											        <html:options collection="dispColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.colNameUnit1">
											        <html:options collection="unitColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.compareDisp1">
											        <html:options collection="compareColumnList" property="key" labelProperty="name"/>
									          </html:select>
										    </td>
			                              </tr>
			                              <tr class="txt_style" >
			                                <td align="left">
			                                  <html:select property="salesBean.colNameDisp2">
											        <html:options collection="dispColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.colNameUnit2">
											        <html:options collection="unitColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                 
										    </td>
			                              </tr>
			                            <tr class="txt_style" >
			                                <td align="left">
			                                  <html:select property="salesBean.colNameDisp3">
											        <html:options collection="dispColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.colNameUnit3">
											        <html:options collection="unitColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.compareDisp2">
											        <html:options collection="compareColumnList" property="key" labelProperty="name"/>
									          </html:select>
										    </td>
			                              </tr>
			                             <tr class="txt_style" >
			                                <td align="left">
			                                  <html:select property="salesBean.colNameDisp4">
											        <html:options collection="dispColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.colNameUnit4">
											        <html:options collection="unitColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                  
										    </td>
			                              </tr>
		                              </table>
			                    </fieldset>        
                            </td>
                          </tr>
                        </table>	
                     </fieldset>					
                            <!-- Criteria -->
             
                            
							<!-- BUTTON -->
							<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
								<tr>
									<td align="right">
									     <input type="button" value=" Search " class="newPosBtnLong" style="width: 120px;"
										     onClick="javascript:search('${pageContext.request.contextPath}','admin')" />
										
										 <button type="button" id="btnExport" onclick="fnExcelReport();" class="newPosBtnLong" style="width:120px;" > Export</button>  
										 <input type="button" value=" Clear " class="newPosBtnLong" style="width: 120px;" 
										     onClick="javascript:clearForm('${pageContext.request.contextPath}','admin')" />	 
										  
										  <a href="javascript:getSQL('${pageContext.request.contextPath}','admin')"  > ...?</a>
										  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										    <input type="button" value=" Export(ข้อมูลจำนวนมาก) " class="newPosBtnLong" style="width: 220px;" 
										     onClick="javascript:exportData('${pageContext.request.contextPath}','admin')" />
										     
										<%--  <input type="button" value=" GET SQL " class="newPosBtn" style="width: 120px;" 
										     onClick="javascript:getSQL('${pageContext.request.contextPath}','admin')" />
										  --%>    
									
									</td>
								</tr>
							</table>
							<!-- BUTTON -->
							
							<!-- RESULT -->
							<script>
						
							</script>
							
							<div id ="scroll">
								<%out.print(Utils.isNull(session.getAttribute("RESULT"))); %>
							</div>
						    <!-- RESULT -->
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