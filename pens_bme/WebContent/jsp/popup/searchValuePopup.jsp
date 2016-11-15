<%@page import="com.isecinc.pens.web.report.ReportProcess"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<html>
<head>
<title></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<jsp:useBean id="searchValuePopupForm" class="com.isecinc.pens.web.popup.SearchValuePopupForm" scope="request" />
<%
 boolean isMultiSelect = false;

 String currCondNo = Utils.isNull(request.getParameter("currCondNo"));
 String currCondTypeValue = Utils.isNull(request.getParameter("currCondTypeValue"));
 String currCondNameText = "";//Utils.isNull((String)SAProcess.getInstance().GROUP_BY_MAP.get(request.getParameter("currCondTypeValue")));
 String searchType = Utils.isNull(request.getParameter("searchType"));
 
 
 String condType1 = Utils.isNull(request.getParameter("condType1"));
 String condCode1 = Utils.isNull(request.getParameter("condCode1"));
 
 String condType2 = Utils.isNull(request.getParameter("condType2"));
 String condCode2 = Utils.isNull(request.getParameter("condCode2"));
 
 String condType3 = Utils.isNull(request.getParameter("condType3"));
 String condCode3 = Utils.isNull(request.getParameter("condCode3"));
 
 // System.out.println("currCondNo:"+currCondNo+",currCondNameValue:"+currCondTypeValue);
 String currentPage = request.getParameter("d-1552-p")==null?"1":request.getParameter("d-1552-p");
 System.out.println("currentPage:"+currentPage);

 String navigation = currCondTypeValue;
 
 if(ReportProcess.MULTI_SELECTION_LIST.contains(currCondTypeValue)){
	 isMultiSelect = true;
 }
 
 pageContext.setAttribute("isMultiSelect", isMultiSelect, PageContext.PAGE_SCOPE);
 
 
 /** Store Select MutilCode in each Page **/
 String codes = Utils.isNull(session.getAttribute("codes"));
 String keys = Utils.isNull(session.getAttribute("keys"));
 String descs = Utils.isNull(session.getAttribute("descs"));
 
 System.out.println("codes:"+codes);
 
%>
<script type="text/javascript">

window.onload = function(){
	setChkInPage();
}

function searchPopup(path, type) {

    var condType1 = document.getElementsByName("condType1")[0].value;
	var condCode1 = document.getElementsByName("condCode1")[0].value;
	
	var condType2 = document.getElementsByName("condType2")[0].value;
	var condCode2 = document.getElementsByName("condCode2")[0].value;
	
	var condType3 = document.getElementsByName("condType3")[0].value;
	var condCode3  = document.getElementsByName("condCode3")[0].value;
	
    var currCondTypeValue = document.getElementsByName("currCondTypeValue")[0].value;
    var currCondNo = document.getElementsByName("currCondNo")[0].value;
    
    var param = "&currCondNo="+currCondNo+"&currCondType="+currCondTypeValue;
        param += "&condType1="+condType1+"&condCode1="+condCode1;
        param += "&condType2="+condType2+"&condCode2="+condCode2;
        param += "&condType3="+condType3+"&condCode3="+condCode3;
        
       // alert(param);
        
    document.searchValuePopupForm.action = path + "/jsp/searchValuePopupAction.do?do=search"+param+"&searchType="<%=searchType%>;
    document.searchValuePopupForm.submit();
   return true;
}

function selectOneRadio(){
	var chRadio = document.getElementsByName("chRadio");
	var code = document.getElementsByName("code");
	var key = document.getElementsByName("key");
	var desc = document.getElementsByName("desc");

	var currCondTypeValue = document.getElementsByName("currCondTypeValue")[0].value;
    var currCondNo = document.getElementsByName("currCondNo")[0].value;
	
	var selectCode = "";
	for(i=0;i<chRadio.length;i++){
        if(chRadio[i].checked){
            window.opener.setMainValue(code[i].value,key[i].value,desc[i].value ,currCondNo );
            //alert("tutiya code="+code[i].value+":key="+key[i].value+":currCondNo:"+currCondNo);
            selectCode= code[i].value;
            document.getElementsByName("selectcode").value=selectCode;
          
            /** Set condition Parent to session  Cond1 only**/
            setConditionParent(currCondNo,selectCode,currCondTypeValue);
            
        	window.close();
            break;
        }
	}
}

/** Set condition Parent to session  Cond1 only**/
function setConditionParent(condNo,condCode,condType){
	$.ajax({
        type: "POST",
        url: "${pageContext.request.contextPath}/jsp/ajax/searchpost.jsp",		
      	data : "condNo="+condNo+"&condCode="+condCode+"&condType="+condType,
      	async: false,
        success: function(msg){
          //alert( "Data Saved: " + msg );
        }
        });
}

function selectMultiple(){
	var chk = document.getElementsByName("chCheck");

	var currCondTypeValue = document.getElementsByName("currCondTypeValue")[0].value;
    var currCondNo = document.getElementsByName("currCondNo")[0].value;
	
	var idx = 2;
	//Add Select Muti in each page 
	
	var retCode = document.getElementsByName("codes")[0].value;
	var retKey = document.getElementsByName("keys")[0].value;
	var retDesc = document.getElementsByName("descs")[0].value;
	
	retCode = retCode.substring(0,retCode.length-1);
	retKey = retKey.substring(0,retKey.length-1);
	retDesc = retDesc.substring(0,retDesc.length-1);
	
	//alert(retCode);
	
	//alert("idx:"+idx);
	if(idx ==1){
		//alert(currCondNo+","+retCode+":"+retKey+":"+retDesc);
		window.opener.setMainValue(retCode,retKey,retDesc ,currCondNo);
	}else{
		//alert(currCondNo+":"+retCode+":"+retKey+":"+retDesc);
		window.opener.setMultiCode(retKey);
		window.opener.setMultiKey(retCode);
		window.opener.setMultiValueDisp(retDesc);
		
		window.opener.setValueCondition(currCondNo);
	}
	
	 /** Set condition Parent to session  Cond1 only**/
    setConditionParent(currCondNo,retCode,currCondTypeValue);
	
	window.close();
}

function saveSelectedInPage(no){
	//alert(no);
	var chk = document.getElementsByName("chCheck");
	var code = document.getElementsByName("code");
	var key = document.getElementsByName("key");
	var desc = document.getElementsByName("desc");
	
	var currentPage = document.getElementsByName("currentPage")[0].value;
	
	var retCode = '';
	var retKey = '';
	var retDesc = '';
	
	var codesAllNew = "";
	var keysAllNew = "";
	var descsAllNew = "";

	if(no >= 21){
	   no = no - ( (currentPage-1) *20);
	}
	//alert(no);
	
    if(chk[no-1].checked){
    	//Add 
        retCode = code[no-1].value;
		retKey = key[no-1].value;
		retDesc = desc[no-1].value;
		
		//alert(retCode);
	    codesAllNew = document.getElementsByName("codes")[0].value;
		keysAllNew = document.getElementsByName("keys")[0].value;
		descsAllNew = document.getElementsByName("descs")[0].value;
		
	    var found = chekCodeDupInCodesAll(retCode);

	    if(found == false){
	  	   codesAllNew += retCode +",";
	  	   keysAllNew  += retKey  +",";
	  	   descsAllNew += retDesc +",";
	    }
	    document.getElementsByName("codes")[0].value =  codesAllNew;
	    document.getElementsByName("keys")[0].value =  keysAllNew;
	    document.getElementsByName("descs")[0].value =  descsAllNew;
	    
    }else{
    	//remove
    	retCode = code[no-1].value;
		retKey = key[no-1].value;
		retDesc = desc[no-1].value;
		
		//alert(retCode);
	    codesAllNew = document.getElementsByName("codes")[0].value;
		keysAllNew = document.getElementsByName("keys")[0].value;
		descsAllNew = document.getElementsByName("descs")[0].value;
		
	    removeUnSelected(retCode);
    }
	
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/setValueSelected.jsp",
			data : "codes=" + encodeURIComponent(document.getElementsByName("codes")[0].value) +
			       "&keys=" + encodeURIComponent(document.getElementsByName("keys")[0].value) +
			       "&descs=" + encodeURIComponent(document.getElementsByName("descs")[0].value),
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
	var keysAll = document.getElementsByName("keys")[0].value;
	var descsAll = document.getElementsByName("descs")[0].value;
	
	var codesAllArray = codesAll.split(",");
	var keysAllArray = keysAll.split(",");
	var descsAllArray = descsAll.split(",");
	
	var codesAllNew  = "";
	var keysAllNew  = "";
	var descsAllNew  = "";
	
	for(var i=0;i < codesAllArray.length; i++){
   		if(codesAllArray[i] != codeCheck){
   		   codesAllNew += codesAllArray[i] +",";
	  	   keysAllNew  += keysAllArray[i]  +",";
	  	   descsAllNew += descsAllArray[i] +",";
   		}//if
	}//for
	
	codesAllNew = codesAllNew.substring(0,codesAllNew.length-1);
	keysAllNew = keysAllNew.substring(0,keysAllNew.length-1);
	descsAllNew = descsAllNew.substring(0,descsAllNew.length-1);
	
	document.getElementsByName("codes")[0].value =  codesAllNew;
	document.getElementsByName("keys")[0].value =  keysAllNew;
	document.getElementsByName("descs")[0].value =  descsAllNew;
}

function setChkInPage(){
	var chk = document.getElementsByName("chCheck");
	var code = document.getElementsByName("code");
	
	var codes = document.getElementsByName("codes")[0].value;
	var codesChk = codes.split(",");
	//alert("codes:"+codes);
	
	for(var i=0;i<chk.length;i++){
		for(var c=0;c<codesChk.length;c++){
			if(code[i].value == codesChk[c]){
				chk[i].checked = true;
				break;
			}
		}//for 2
	} //for 1	
}



</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/searchValuePopupAction">
<input type="hidden" name="currCondNo" value ="<%=currCondNo%>" />
<input type="hidden" name="currCondTypeValue" value ="<%=currCondTypeValue%>" />
<input type="hidden" name="currCondNameText" value ="<%=currCondNameText%>" />
<input type="hidden" name="searchType" value ="<%=searchType%>" />
<input type="hidden" name="selectcode" id="selectcode" value ="" />

<input type="hidden" name="condType1" value ="<%=condType1%>" />
<input type="hidden" name="condCode1" value ="<%=condCode1%>" />

<input type="hidden" name="condType2" value ="<%=condType2%>" />
<input type="hidden" name="condCode2" value ="<%=condCode2%>" />

<input type="hidden" name="condType3" value ="<%=condType3%>" />
<input type="hidden" name="condCode3" value ="<%=condCode3%>" />

<input type="hidden" name="codes" size="50" value ="<%=codes%>" />
<input type="hidden" name="keys" size="50" value ="<%=keys%>" />
<input type="hidden" name="descs" size="50" value ="<%=descs%>" />

<input type="hidden" name="currentPage" size="50" value ="<%=currentPage%>" />

<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" >
    <tr height="21px">
		<td width="15%" >&nbsp;</td>
		<td width="90%" class="h1"><b>ค้นหาข้อมูล :<%=navigation %></b></td>
	</tr>
	<tr height="21px">
		<td width="15%" ><b>รหัส</b>  </td>
		<td width="90%" ><html:text property="salesBean.code"  size="30" style="height:20px"/>
		<input type="button" name="search" value="Search" onclick="searchPopup('<%=request.getContextPath()%>','')" />
		</td>
	</tr>
	<tr height="21px">
		<td ><b>รายละเอียด</b></td>
		<td ><html:text property="salesBean.desc"  size="60" style="height:20px"/></td>
	</tr>
</table>

<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
	<tr>
		<td align="center">
			<c:if test="${isMultiSelect}" >
				<input type="button" name="ok" value="OK" onclick="selectMultiple()" style="width:60px;"/>
			</c:if>
			<c:if test="${!isMultiSelect}" >
				<input type="button" name="ok" value="OK" onclick="selectOneRadio()" style="width:60px;"/>
			</c:if>
			<input type="button" name="close" value="Close" onclick="javascript:window.close();" style="width:60px;"/>
		</td>
	</tr>
</table>
<!-- RESULT -->
<display:table style="width:100%;" id="item" name="sessionScope.VALUE_LIST" 
    defaultsort="0" defaultorder="descending" requestURI="#" sort="list" pagesize="20" class="resultDisp">	
    	
    <display:column style="text-align:center;" title="เลือกข้อมูล"  sortable="false" class="chk">
		<c:if test="${isMultiSelect}" >
			<input type ="checkbox" name="chCheck" onclick="saveSelectedInPage(${item.no})"  />
		</c:if>
		<c:if test="${!isMultiSelect}" >
			<input type ="radio" name="chRadio" />
		</c:if> 
		<input type ="hidden" name="code" value="<bean:write name="item" property="code"/>" />
		<input type ="hidden" name="key" value="<bean:write name="item" property="key"/>" />
		<input type ="hidden" name="desc" value="<bean:write name="item" property="name"/>" />
	 </display:column>
    											    
    <display:column title="รหัส" property="key"  sortable="false" class="code"/>
    <display:column title="รายละเอียด" property="name"  sortable="false" class="desc"/>								
</display:table>	
<!-- RESULT -->



</html:form>
</body>
</html>