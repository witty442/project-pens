<%@page import="com.isecinc.pens.web.popupsearch.PopupSearchHelper"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/popup_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/field_size.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<jsp:useBean id="popupSearchForm" class="com.isecinc.pens.web.popupsearch.PopupSearchForm" scope="session" />
<%
    String pageName = popupSearchForm.getPageName(); 

    String currentPage = "1";
    String hideAll = Utils.isNull(request.getParameter("hideAll"));
    String multipleCheck = Utils.isNull(request.getParameter("multipleCheck"));
    String fieldName = Utils.isNull(request.getParameter("fieldName"));
    String titleSearch = "";
    String codeSearchTxtName = "";
	String descSearchTxtName = "";
	
	//SetDescription
	PopupSearchHelper criName = PopupSearchHelper.setDescription(pageName); 
	titleSearch = criName.getTitleSearch();
	codeSearchTxtName = criName.getCodeSearchTxtName();
	descSearchTxtName = criName.getDescSearchTxtName();
	
    /** Store Select MutilCode in each Page **/
    String codes = Utils.isNull(session.getAttribute("codes"));
    String descs = Utils.isNull(session.getAttribute("descs"));
    
    String queryStr= request.getQueryString();
	 if(queryStr.indexOf("d-") != -1){
	 	queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
	 	System.out.println("queryStr:"+queryStr);
	    currentPage = request.getParameter(queryStr)==null?"1":request.getParameter(queryStr);
	 }
	 
    System.out.println("codes:"+codes);
%>

<script type="text/javascript">

function searchPopup(path) {
    document.popupSearchForm.action = path + "/jsp/popupSearchAction.do?do=search";
    document.popupSearchForm.submit();
   return true;
}
function selectAll(){
	document.getElementsByName("codes")[0].value = 'ALL,';
	document.getElementsByName("descs")[0].value = 'ALL,';
}
function selectOneRadio(){
	var pageName ='<%=pageName%>';
	var chRadio = document.getElementsByName("chCheck");
	var code = document.getElementsByName("code_temp");
	var desc = document.getElementsByName("desc");

	for(var i=0;i<chRadio.length;i++){
        if(chRadio[i].checked){
        	//alert(i+":"+code[i+1].value);
        	<%if("".equals(fieldName)){%>
              window.opener.setDataPopupValue(code[i].value,desc[i].value ,pageName);
            <%}else{%>
              window.opener.setDataPopupValue(code[i].value,desc[i].value ,pageName,'<%=fieldName%>');
            <%}%>
        	window.close();
            break;
        }
	}
}
function selectMultiple(){
	var chk = document.getElementsByName("chCheck");
	var pageName ='<%=pageName%>';
	var idx = 2;
	//Add Select Muti in each page 
	var retCode = document.getElementsByName("codes")[0].value;
	var retDesc = document.getElementsByName("descs")[0].value;
	
	retCode = retCode.substring(0,retCode.length-1);
	retDesc = retDesc.substring(0,retDesc.length-1);
	
	//alert(retCode);
	
	//alert("idx:"+idx);
	if(idx ==1){
		//alert(currCondNo+","+retCode+":"+retKey+":"+retDesc);
		window.opener.setDataPopupValue(retCode,retDesc,pageName);
	}else{
		//alert(currCondNo+":"+retCode+":"+retKey+":"+retDesc);
		window.opener.setDataPopupValue(retCode,retDesc,pageName);
	}
	
	/** Clear Value in Session **/
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/setValueSelected.jsp",
			data : "codes=" + encodeURIComponent('') +
			       "&keys=" + encodeURIComponent('') +
			       "&descs=" + encodeURIComponent(''),
			//async: false,
			cache: true,
			success: function(){
			}
		}).responseText;
	});
	
	window.close();
}
function saveSelectedInPage(no){
	//alert(no);
	var chk = document.getElementsByName("chCheck");
	var currentPage = document.getElementsByName("currentPage")[0].value;
	var code = document.getElementsByName("code_temp");
	var desc = document.getElementsByName("desc");
	
	var retCode = '';
	var retDesc = '';
	
	var codesAllNew = "";
	var descsAllNew = "";

	if(no >= 21){
	   no = no - ( (currentPage-1) *20);
	}
	no = no -1;
	//alert("no["+no+"]checked:"+chk[no].checked);
    if(chk[no].checked){
    	//Add 
        retCode = code[no].value;
		retDesc = desc[no].value;
		
		//alert("code["+no+"]="+retCode);
	    codesAllNew = document.getElementsByName("codes")[0].value;
		descsAllNew = document.getElementsByName("descs")[0].value;
		
	    var found = chekCodeDupInCodesAll(retCode);

	    if(found == false){
	  	   codesAllNew += retCode +",";
	  	   descsAllNew += retDesc +",";
	    }
	    document.getElementsByName("codes")[0].value =  codesAllNew;
	    document.getElementsByName("descs")[0].value =  descsAllNew;
	    
	    //alert(no+":found["+found+"]"+document.getElementsByName("codes")[0].value);
    }else{
    	
    	//remove
    	retCode = code[no].value;
		retDesc = desc[no].value;
		
		//alert(retCode);
	    codesAllNew = document.getElementsByName("codes")[0].value;
		descsAllNew = document.getElementsByName("descs")[0].value;
		
	    removeUnSelected(retCode);
    }
	
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/setValueSelected.jsp",
			data : "codes=" + encodeURIComponent(document.getElementsByName("codes")[0].value) +
			       "&keys=" + encodeURIComponent('') +
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
	var descsAll = document.getElementsByName("descs")[0].value;
	
	var codesAllArray = codesAll.split(",");
	var descsAllArray = descsAll.split(",");
	
	var codesAllNew  = "";
	var descsAllNew  = "";
	
	for(var i=0;i < codesAllArray.length; i++){
   		if(codesAllArray[i] != codeCheck){
   		   codesAllNew += codesAllArray[i] +",";
	  	   descsAllNew += descsAllArray[i] +",";
   		}//if
	}//for
	
	codesAllNew = codesAllNew.substring(0,codesAllNew.length-1);
	descsAllNew = descsAllNew.substring(0,descsAllNew.length-1);
	
	document.getElementsByName("codes")[0].value =  codesAllNew;
	document.getElementsByName("descs")[0].value =  descsAllNew;
}

function setChkInPage(){
	var chk = document.getElementsByName("chCheck");
	var code = document.getElementsByName("code_temp");
	//alert("Code Size:"+code.length);
	
	var codes = document.getElementsByName("codes")[0].value;
	var codesChk = codes.split(",");
	//alert(codesChk);
	
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

window.onload = function(){
	setChkInPage();
}

</script>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/popupSearchAction">

<input type="hidden" name=pageName value ="<%=pageName%>" />
<input type="hidden" name=fieldName value ="<%=fieldName%>" />
<input type="hidden" name="currentPage"  value ="<%=currentPage%>" />
<input type="hidden" name="codes" value ="<%=codes%>" />
<input type="hidden" name="descs" value ="<%=descs%>" />

<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" class="tableHead">
    <tr height="21px" class="txt1">
		<th width="15%" >&nbsp;</th> 
		<th width="90%" ><b>ค้นหาข้อมูล <%=titleSearch%></b></th>
	</tr>
	<tr height="21px" class="txt1">
		<td width="15%" nowrap><b><%=codeSearchTxtName %></b>  </td>
		<td width="90%" ><html:text property="codeSearch"  size="30" style="height:20px"/>
		<input type="button" name="search"  class="newPosBtnLong"  value="Search" onclick="searchPopup('<%=request.getContextPath()%>')" />
		</td>
	</tr>
	<%if("StoreCodeBME".equalsIgnoreCase(pageName)){ %>
		<tr height="21px" class="txt1">
			<td nowrap><b><%=descSearchTxtName %></b></td>
			<td ><html:text property="descSearch"  size="60" style="height:20px"/></td>
		</tr>
	<%} %>
</table>

<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
	<tr>
		<td align="center">
			<% if("true".equalsIgnoreCase(multipleCheck)){ %>
				<input type="button" name="ok" value="OK" onclick="selectMultiple()" style="width:80px;"  class="newPosBtnLong" />
			<%}else{ %>
			   <input type="button" name="ok" value="OK" onclick="selectOneRadio()" style="width:80px;"  class="newPosBtnLong" />
			<%} %>
			<input type="button" name="close" value="Close" onclick="javascript:window.close();" style="width:80px;"  class="newPosBtnLong" />
			&nbsp;
			<%if(!"true".equals(hideAll)){ %><input type ="checkbox" name="chCheckAll" id="chCheckAll" onclick="selectAll();"  /> เลือกทั้งหมด <%} %>
		</td>
	</tr>
</table>
<!-- RESULT -->
<%if(session.getAttribute("DATA_LIST") != null){ %>
   <%if("StoreCodeBME".equalsIgnoreCase(pageName)){ %>
        <jsp:include page="popup_sub/storeCodeResult.jsp" /> 
        
   <%}else if("FIND_GroupCode_IN_StyleMappingLotus".equalsIgnoreCase(pageName)
		  || "FIND_StyleNo_IN_StyleMappingLotus".equalsIgnoreCase(pageName)
		  || "FIND_PensItem_IN_StyleMappingLotus".equalsIgnoreCase(pageName)){ %>
        <jsp:include page="popup_sub/FIND_StyleMappingLotus_Result.jsp" /> 
        
  <%}else if("FIND_Style".equalsIgnoreCase(pageName)
		  || "FIND_PensItem".equalsIgnoreCase(pageName)){ %>
        <jsp:include page="popup_sub/FIND_MSTRef_Result.jsp" /> 
  <%}else if("FIND_GroupCode".equalsIgnoreCase(pageName)){ %>
        <jsp:include page="popup_sub/FIND_GroupCodeMSTRef_Result.jsp" /> 
 
   <%}else if(session.getAttribute("search_submit") != null){ %>
        <font size="2" color="red">ไม่พบข้อมูล</font>
    <%} %>
<%} %>
<!-- RESULT -->
</html:form>
</body>
</html>