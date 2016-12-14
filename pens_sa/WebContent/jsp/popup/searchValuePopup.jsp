<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.report.salesanalyst.helper.DBConnection"%>
<%@page import="com.isecinc.pens.report.salesanalyst.helper.Utils"%>
<%@page import="com.isecinc.pens.report.salesanalyst.SAInitial"%>

<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="searchValuePopupForm" class="com.isecinc.pens.web.popup.SearchValuePopupForm" scope="session" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title>ค้นหาข้อมูล</title>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<%
 boolean isMultiSelect = false;
 String load = Utils.isNull(request.getParameter("load"));

 String currCondNo = Utils.isNull(request.getParameter("currCondNo"));
 String currCondTypeValue = Utils.isNull(request.getParameter("currCondTypeValue"));
 String currCondNameText = Utils.isNull((String)SAInitial.getInstance().GROUP_BY_MAP.get(request.getParameter("currCondTypeValue")));
 String searchType = Utils.isNull(request.getParameter("searchType"));


//String currentPage = request.getParameter("d-1552-p")==null?"1":request.getParameter("d-1552-p");
String queryStr= request.getQueryString();
if(queryStr.indexOf("d-") != -1){
	queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
	System.out.println("queryStr:"+queryStr);
}
String currentPage = request.getParameter(queryStr);

 System.out.println("currCondNo:"+currCondNo);
System.out.println("currCondTypeValue:"+currCondTypeValue);
System.out.println("currCondNameText:"+currCondNameText);
System.out.println("searchType:"+searchType);
System.out.println("currentPage:"+Utils.isNull(currentPage)); 
System.out.println("desc1:"+request.getParameter("salesBean.desc"));

String code = session.getAttribute("code_session")!=null?(String)session.getAttribute("code_session"):"";
String desc = session.getAttribute("desc_session")!=null?(String)session.getAttribute("desc_session"):"";

 if(SAInitial.MULTI_SELECTION_LIST.contains(currCondTypeValue)){
	 isMultiSelect = true;
 } 
 
 pageContext.setAttribute("isMultiSelect", isMultiSelect, PageContext.PAGE_SCOPE);
 
 /** Store Select MutilCode in each Page **/
 String codes = Utils.isNull(session.getAttribute("codes"));
 String keys = Utils.isNull(session.getAttribute("keys"));
 String descs = Utils.isNull(session.getAttribute("descs"));
 
 //System.out.println("codes:"+codes);
 
 String condType1 = Utils.isNull(request.getParameter("condType1"));
 String condType2 = Utils.isNull(request.getParameter("condType2"));
 String condType3 = Utils.isNull(request.getParameter("condType3"));
 String condType4 = Utils.isNull(request.getParameter("condType4"));

 //System.out.println("condType1:"+condType1);
 //System.out.println("condType1:"+Utils.isNull(request.getParameter("condType1")));
%>
<script type="text/javascript">

function searchPopup(path, type) {

    var condType1 = document.getElementById("condType1").value;
	var condCode1 = document.getElementById("condCode1").value ;
	
	var condType2 = document.getElementById("condType2").value;
	var condCode2 = document.getElementById("condCode2").value;
	
	var condType3 = document.getElementById("condType3").value;
	var condCode3  = document.getElementById("condCode3").value;
	
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
	//alert(codesChk);
	
	for(var i=0;i<chk.length;i++){
		for(var c=0;c<codesChk.length;c++){
			if(code[i].value == codesChk[c]){
				chk[i].checked = true;
				break;
			}
		}
	}	
}

window.onload = function(){
	setChkInPage();
	//loadDataFromMain();
	loadNav();
	//load Critiria thai
	loadCritiria();
}
function loadCritiria(){
	<%if( !Utils.isNull(code).equals("")){ %>
	   document.getElementsByName("salesBean.code")[0].value ="<%=Utils.isNull(code)%>";
	<%}%>
	<%if( !Utils.isNull(desc).equals("")){ %>
	   document.getElementsByName("salesBean.desc")[0].value ="<%=Utils.isNull(desc)%>";
	<%}%>
}
function loadNav(){
	var nav1 = document.getElementById("nav1");
	var nav2 = document.getElementById("nav2");
	var nav3 = document.getElementById("nav3");
	var nav4 = document.getElementById("nav4");
	
	var currCondNo = document.getElementById("currCondNo");
	//alert(<%=condType1%>);
	if(currCondNo.value =='1'){
	    nav1.innerHTML = "<%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(currCondTypeValue))%>";
	}else if(currCondNo.value =='2'){
		nav1.innerHTML = "<%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType1))%>["+document.getElementById("condValueDisp1").value+"]";
		nav2.innerHTML = "#<%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(currCondTypeValue))%>";
		
	}else if(currCondNo.value =='3'){
		nav1.innerHTML= "<%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType1))%>["+document.getElementById("condValueDisp1").value+"]";
		nav2.innerHTML= "#<%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType2))%>["+document.getElementById("condValueDisp2").value+"]";
		nav3.innerHTML= "#<%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(currCondTypeValue))%>";
	
	}else if(currCondNo.value =='4'){
		nav1.innerHTML = "<%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType1))%>["+document.getElementById("condValueDisp1").value+"]";
		nav2.innerHTML= " #<%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType2))%>["+document.getElementById("condValueDisp2").value+"]";
		nav3.innerHTML= " #<%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType3))%>["+document.getElementById("condValueDisp3").value+"]";
		nav4.innerHTML= " #<%=Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(currCondTypeValue))%>";
	}
}

function loadDataFromMain(){
    //alert(window.opener.$("#condName1").val());
	//alert(window.opener.$("#condCode1").val());
	//alert(document.getElementById("condValueDisp1").value);
	
	<% if("1".equals(load)){ %>
		document.getElementById("condType1").value = window.opener.$("#condName1").val();
		document.getElementById("condCode1").value = window.opener.$("#condCode1").val();
		document.getElementById("condValueDisp1").value = window.opener.$("#condValue1").val();
		
		document.getElementById("condType2").value = window.opener.$("#condName2").val();
		document.getElementById("condCode2").value = window.opener.$("#condCode2").val();
		document.getElementById("condValueDisp2").value = window.opener.$("#condValue2").val();
		
		document.getElementById("condType3").value = window.opener.$("#condName3").val();
		document.getElementById("condCode3").value = window.opener.$("#condCode3").val();
		document.getElementById("condValueDisp3").value = window.opener.$("#condValue3").val(); 
   <%}%>
}
</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/searchValuePopupAction">
<input type="hidden" name="currCondNo" id="currCondNo" value ="<%=currCondNo%>" />
<input type="hidden" name="currCondTypeValue" id="currCondTypeValue" value ="<%=currCondTypeValue%>" />
<input type="hidden" name="currCondNameText"  id="currCondNameText"  value ="<%=currCondNameText%>" />
<input type="hidden" name="searchType" id="searchType" value ="<%=searchType%>" />
<input type="hidden" name="selectcode" id="selectcode" value ="" />

<html:hidden property="filterBean.condType1" styleId="condType1" />
<html:hidden property="filterBean.condCode1" styleId="condCode1" />
<html:hidden property="filterBean.condValueDisp1" styleId="condValueDisp1" />

<html:hidden property="filterBean.condType2" styleId="condType2" />
<html:hidden property="filterBean.condCode2" styleId="condCode2" />
<html:hidden property="filterBean.condValueDisp2" styleId="condValueDisp2" />

<html:hidden property="filterBean.condType3" styleId="condType3" />
<html:hidden property="filterBean.condCode3" styleId="condCode3" />
<html:hidden property="filterBean.condValueDisp3" styleId="condValueDisp3" />

<input type="hidden" name="codes" size="50" value ="<%=codes%>" />
<input type="hidden" name="keys" size="50" value ="<%=keys%>" />
<input type="hidden" name="descs" size="50" value ="<%=descs%>" />

<input type="hidden" name="currentPage" size="50" value ="<%=currentPage%>" />

<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%"  bgcolor="#FAFAFA">
    <%-- <tr height="21px">
		<td class="h1" colspan="2"><b>ค้นหาข้อมูล :${searchValuePopupForm.curNavigation}</b></td>
	</tr> --%>
	 <tr height="21px" bgcolor="#FAFAFA">
		<td class="h1" colspan="2" align="center" bgcolor="#FAFAFA"><font size="3">ค้นหาข้อมูล :<%=currCondNameText %></font></td>
	</tr>
	<tr height="21px" bgcolor="#F1F1F1">
		<td class="h1" colspan="2" align="left"><font size="3"><b><span id="nav1"></span><span id="nav2"></span><span id="nav3"></span><span id="nav4"></span></b></font></td>
	</tr>
	<%-- <tr height="21px">
		<td class="h1" colspan="2"><b>${searchValuePopupForm.navigation}</b></td>
	</tr> --%>
	<tr height="21px">
		<td width="15%" align="right"><b>รหัส</b>  </td>
		<td width="90%" align="left"><html:text property="salesBean.code"  size="30" style="height:20px"/>
		</td>
	</tr>
	<tr height="21px">
		<td width="15%"  align="right"><b>รายละเอียด</b></td>
		<td width="90%" align="left"><html:text property="salesBean.desc"  size="60" style="height:20px"/> 
		<input type="button" name="search" class="newPosBtn1"  value="ค้นหาข้อมูล" onclick="searchPopup('<%=request.getContextPath()%>','')" />
		</td>
	</tr>
</table>

<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" bgcolor="#F1F1F1">
	<tr>
		<td align="left">
			<c:if test="${isMultiSelect}" >
				<input type="button" name="ok" value=" OK " class="newPosBtn1"  onclick="selectMultiple()" style="width:80px;"/>
			</c:if>
			<c:if test="${!isMultiSelect}" >
				<input type="button" name="ok" value=" OK " class="newPosBtn1"  onclick="selectOneRadio()" style="width:80px;"/>
			</c:if>
			<input type="button" name="close" value="Close" class="newPosBtn1"  onclick="javascript:window.close();" style="width:80px;"/>
		</td>
	</tr>
</table>
<!-- RESULT -->
<%-- <display:table style="width:100%;" id="item" name="sessionScope.VALUE_LIST"  defaultsort="0" defaultorder="descending" requestURI="#" sort="list" pagesize="20" class="resultDisp">	
 --%>    
 <display:table style="width:100%;" id="item" name="sessionScope.VALUE_LIST" defaultsort="0"  defaultorder="descending" class="resultDisp" requestURI="#" sort="list" pagesize="20">
 	
    <display:column style="align:left;white-space:nowrap"  title="เลือกข้อมูล" class="chk">
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
    											    
    <display:column style="align:left;white-space:nowrap" title="รหัส" property="key"  sortable="false" class="code"/>
    <display:column style="align:left;white-space:nowrap" title="รายละเอียด" property="name"  sortable="false" class="desc"/>				
</display:table>
<!-- RESULT -->

	<Script>
	loadDataFromMain();
	</Script>
		
</html:form>
</body>
</html>