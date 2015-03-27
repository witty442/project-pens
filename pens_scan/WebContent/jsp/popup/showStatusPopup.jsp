<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<%
	request.setAttribute("STATUS_LIST", PickConstants.getStockQueryStatusList());
	
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">

<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css" type="text/css" />

<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">

<script type="text/javascript">
function loadMe(_path){
	
}

function selectAll(){
	//document.getElementsByName("codes")[0].value = 'ALL,';
	//document.getElementsByName("descs")[0].value = 'ALL,';
}

function selectMultiple(){
	var codesAllNew = "";
	var descsAllNew = "";
	var chAll = document.getElementsByName("chCheckAll");
	
	//Add Select Muti in each page 
	var checkInTable = document.getElementsByName("chCheck");
    var codeInTable = document.getElementsByName("code");
    var descInTable = document.getElementsByName("desc");
    //alert(codeInTable.length);
    
  
	for(var i=0;i < codeInTable.length; i++){
		//alert(checkInTable[i].checked);
   		if(checkInTable[i].checked){
   		   codesAllNew += codeInTable[i].value +",";
	  	   descsAllNew += descInTable[i].value +",";
   		}//if
	    
	}//for

	if(codesAllNew.length >0){
		codesAllNew = codesAllNew.substr(0,codesAllNew.length-1);
		descsAllNew = descsAllNew.substr(0,descsAllNew.length-1);
	}
	
	//alert(chAll[0].checked);
	if(chAll[0].checked){
		codesAllNew = "ALL";
		descsAllNew = "ALL";
	}
	window.opener.setStatusMainValue(codesAllNew,descsAllNew);
	window.close();
}

</script>

</head>
<body onload="loadMe('${pageContext.request.contextPath}');" topmargin="0" bottommargin="0" leftmargin="100" rightmargin="150" class="popbody">
<!-- BODY -->
<html:form action="/jsp/tempAction">
<input type="hidden" name="load" value="">

<!-- INCLUDE -->
<jsp:include page="../program.jsp">
	<jsp:param name="system" value="Transaction"/>
	<jsp:param name="function" value=""/>
</jsp:include>
<jsp:include page="../error.jsp"/>
<!-- BUTTON -->
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td align="right">
		    <table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
				<tr>
					<td align="center">
						<input type="button" name="ok" value="OK" onclick="selectMultiple()" style="width:60px;"/>
						<input type="button" name="close" value="Close" onclick="javascript:window.close();" style="width:60px;"/>
						
						<input type ="checkbox" name="chCheckAll" id="chCheckAll"/>เลือกทั้งหมด
					</td>
				</tr>
			</table>
			
			<!-- RESULT -->
			<display:table width="100%" id="item" name="requestScope.STATUS_LIST" 
			    defaultsort="0" defaultorder="descending" requestURI=".." sort="list" pagesize="20" class="resultDisp">	
			    	
			    <display:column align="left" title="เลือกข้อมูล"  nowrap="true" sortable="false" class="chk">
					<input type ="checkbox" name="chCheck" id="chCheck"   />
					<input type ="hidden" name="code" value="<bean:write name="item" property="key"/>" />
	            	<input type ="hidden" name="desc" value="<bean:write name="item" property="name"/>" /> 
				 </display:column>
			    											    
			    <display:column align="left" title="รหัส" property="key"  nowrap="false" sortable="false" class="code"/>
			    <display:column align="left" title="รายละเอียด" property="name" nowrap="false" sortable="false" class="desc"/>								
			</display:table>	
			<!-- RESULT -->
		</td>
	</tr>
</table>
</html:form>
<!-- BODY -->
</body>
</html>