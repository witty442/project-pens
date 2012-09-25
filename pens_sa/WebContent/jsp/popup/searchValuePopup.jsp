<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.isecinc.pens.report.salesanalyst.helper.Utils"%>

<html>
<head>
<title></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag_screen.css" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<%@page import="com.isecinc.pens.report.salesanalyst.SAProcess"%>
<jsp:useBean id="searchValuePopupForm" class="com.isecinc.pens.web.popup.SearchValuePopupForm" scope="request" />
<%
	boolean isMultiSelect = false;

 String currCondNo = Utils.isNull(request.getParameter("currCondNo"));
 String currCondTypeValue = Utils.isNull(request.getParameter("currCondTypeValue"));
 String currCondNameText = Utils.isNull((String)SAProcess.getInstance().GROUP_BY_MAP.get(request.getParameter("currCondTypeValue")));
 String searchType = Utils.isNull(request.getParameter("searchType"));
 
 
 String condType1 = Utils.isNull(request.getParameter("condType1"));
 String condCode1 = Utils.isNull(request.getParameter("condCode1"));
 
 String condType2 = Utils.isNull(request.getParameter("condType2"));
 String condCode2 = Utils.isNull(request.getParameter("condCode2"));
 
 String condType3 = Utils.isNull(request.getParameter("condType3"));
 String condCode3 = Utils.isNull(request.getParameter("condCode3"));
 
 System.out.println("currCondNo:"+currCondNo+",currCondNameValue:"+currCondTypeValue);
 
 String navigation = "";
 if(currCondNo.equals("1")){
	 navigation += Utils.isNull((String)SAProcess.getInstance().GROUP_BY_MAP.get(currCondTypeValue)); 
 }else if(currCondNo.equals("2")){
	 navigation += Utils.isNull((String)SAProcess.getInstance().GROUP_BY_MAP.get(condType1))+"["+condCode1+"]" +"->";
	 navigation += Utils.isNull((String)SAProcess.getInstance().GROUP_BY_MAP.get(currCondTypeValue)); 
 }else if(currCondNo.equals("3")){
	 navigation += Utils.isNull((String)SAProcess.getInstance().GROUP_BY_MAP.get(condType1))+"["+condCode1+"]" +"->";
	 navigation += Utils.isNull((String)SAProcess.getInstance().GROUP_BY_MAP.get(condType2))+"["+condCode2+"]" +"->";
	 navigation += Utils.isNull((String)SAProcess.getInstance().GROUP_BY_MAP.get(currCondTypeValue)); 
 }else if(currCondNo.equals("4")){
	 navigation += Utils.isNull((String)SAProcess.getInstance().GROUP_BY_MAP.get(condType1))+"["+condCode1+"]" +"->";
	 navigation += Utils.isNull((String)SAProcess.getInstance().GROUP_BY_MAP.get(condType2))+"["+condCode2+"]" +"->";
	 navigation += Utils.isNull((String)SAProcess.getInstance().GROUP_BY_MAP.get(condType3))+"["+condCode3+"]" +"->";
	 navigation += Utils.isNull((String)SAProcess.getInstance().GROUP_BY_MAP.get(currCondTypeValue)); 
 }
 
 
 if(SAProcess.MULTI_SELECTION_LIST.contains(currCondTypeValue)){
	 isMultiSelect = true;
 }
 
 pageContext.setAttribute("isMultiSelect", isMultiSelect, PageContext.PAGE_SCOPE);
%>
<script type="text/javascript">

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
        url: "${pageContext.request.contextPath}/jsp/salesAnalyst/searchpost.jsp",		
      	data : "condNo="+condNo+"&condCode="+condCode+"&condType="+condType,
      	async: false,
        success: function(msg){
          //alert( "Data Saved: " + msg );
        }
        });
}

function selectMultiple(){
	var chk = document.getElementsByName("chCheck");
	var code = document.getElementsByName("code");
	var key = document.getElementsByName("key");
	var desc = document.getElementsByName("desc");

	var currCondTypeValue = document.getElementsByName("currCondTypeValue")[0].value;
    var currCondNo = document.getElementsByName("currCondNo")[0].value;
	
	var idx = 0;
	var retCode = '';
	var retKey = '';
	var retDesc = '';

	for(i=0;i<chk.length;i++){
        if(chk[i].checked){
			if(idx<=0){
				retCode = code[i].value;
				retKey = key[i].value;
				retDesc = desc[i].value;
			}
			else{
				retCode = retCode+','+code[i].value;
				retKey = retKey+','+key[i].value;
				retDesc = retDesc+','+desc[i].value;
			}

			idx++;
        }
	}
	
	//alert("idx:"+idx);
	if(idx ==1){
		//alert(currCondNo+","+retCode+":"+retKey+":"+retDesc);
		window.opener.setMainValue(retCode,retKey,retDesc ,currCondNo);
	}
	else{
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

<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%">
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

<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td align="center">
			<c:if test="${isMultiSelect}" >
				<input type="button" name="ok" value="OK" onclick="selectMultiple()"/>
			</c:if>
			<c:if test="${!isMultiSelect}" >
				<input type="button" name="ok" value="OK" onclick="selectOneRadio()"/>
			</c:if>
			<input type="button" name="close" value="Close" onclick="javascript:window.close();"/>
		</td>
	</tr>
</table>
<!-- RESULT -->
<display:table width="100%" id="item" name="sessionScope.VALUE_LIST" 
    defaultsort="0" defaultorder="descending" requestURI="../jsp/searchValuePopupAction.do?do=search" sort="list" pagesize="20">	
    	
    <display:column align="left" title="เลือกข้อมูล"  width="20" nowrap="true" sortable="true">
		<c:if test="${isMultiSelect}" >
			<input type ="checkbox" name="chCheck" />
		</c:if>
		<c:if test="${!isMultiSelect}" >
			<input type ="radio" name="chRadio" />
		</c:if> 
		<input type ="hidden" name="code" value="<bean:write name="item" property="code"/>" />
		<input type ="hidden" name="key" value="<bean:write name="item" property="key"/>" />
		<input type ="hidden" name="desc" value="<bean:write name="item" property="name"/>" />
	 </display:column>
    											    
    <display:column align="left" title="รหัส" property="key" width="80" nowrap="false" sortable="true"/>
    <display:column align="left" title="รายละเอียด" property="name" width="100" nowrap="false" sortable="true"/>								
</display:table>	
<!-- RESULT -->


</html:form>
</body>
</html>