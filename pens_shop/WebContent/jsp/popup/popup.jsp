<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="util.SessionGen"%>
<%@page import="util.Constants"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/popup_style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/field_size.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />

<style type="text/css"></style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<jsp:useBean id="popupForm" class="com.isecinc.pens.web.popup.PopupForm" scope="session" />
<%
    String pageName = popupForm.getPageName(); 

    String currentPage = "1";
    String hideAll = Utils.isNull(request.getParameter("hideAll"));
    String mutiple = Utils.isNull(request.getParameter("mutiple"));
    String headName ="";
    String codeSearchTxtName = "";
	String descSearchTxtName = "";
	String descSearchTxtName2 = "";
	
    /** Criteria Name **/
    if("Itembarcode".equalsIgnoreCase(pageName) || "Itembarcode".equalsIgnoreCase(pageName)
     ){
    	headName = "�����Թ���("+pageName+")";
    	codeSearchTxtName = "�����Թ���";
    	descSearchTxtName = "Material Master";
    	descSearchTxtName2 = "Barcode";
    }
    
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
    document.popupForm.action = path + "/jsp/popupAction.do?do=search";
    document.popupForm.submit();
   return true;
}
function selectAll(){
	document.getElementsByName("codes")[0].value = 'ALL,';
	document.getElementsByName("descs")[0].value = 'ALL,';
}
function selectOneRadio(){
	var chRadio = document.getElementsByName("chCheck");
	var pageName ='<%=pageName%>';
	var code = document.getElementsByName("code_temp");
	var desc = document.getElementsByName("desc");

	for(var i=0;i<chRadio.length;i++){
        if(chRadio[i].checked){
        	//alert(i+":"+code[i+1].value);
           // window.opener.setStoreMainValue(code[i].value,desc[i].value ,types);
            window.opener.setDataPopupValue(code[i].value,desc[i].value,pageName);
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
<html:form action="/jsp/popupAction">

<input type="hidden" name=pageName value ="<%=pageName%>" />
<input type="hidden" name="currentPage"  value ="<%=currentPage%>" />
<input type="hidden" name="codes" value ="<%=codes%>" />
<input type="hidden" name="descs" value ="<%=descs%>" />
<input type="hidden" name="mutiple" value ="<%=mutiple%>" />
<input type="hidden" name="hideAll" value ="<%=hideAll%>" />

<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" class="tableHead">
    <tr height="21px" class="txt1">
		<th width="15%" >&nbsp;</th> 
		<th width="90%" ><b>���Ң����� <%=headName%></b></th>
	</tr>
	<tr height="21px" class="txt1">
		<td width="15%" align="right"><b><%=codeSearchTxtName %></b>  </td>
		<td width="90%" ><html:text property="codeSearch"  size="30" style="height:20px"/>
		&nbsp;&nbsp;
		<input type="button" name="search"  class="newPosBtnLong"  value=" ���� " onclick="searchPopup('<%=request.getContextPath()%>')" />
		</td>
	</tr>
	<tr height="21px" class="txt1">
		<td nowrap align="right"><b><%=descSearchTxtName %></b></td>
		<td ><html:text property="descSearch"  size="40" style="height:20px"/></td>
	</tr>
	<%if("Itembarcode".equalsIgnoreCase(pageName) ) {%>
	<tr height="21px" class="txt1">
		<td nowrap align="right"><b><%=descSearchTxtName2 %></b></td>
		<td ><html:text property="barcodeSearch"  size="40" style="height:20px"/></td>
	</tr>
	<%} %>
</table>

<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
	<tr>
		<td align="center">
		<%if("true".equalsIgnoreCase(mutiple)){ %>
			<input type="button" name="ok" value=" �׹�ѹ " onclick="selectMultiple()" style="width:80px;"  class="newPosBtnLong" />
		<%}else{ %>
		    <input type="button" name="ok" value=" �׹�ѹ " onclick="selectOneRadio()" style="width:80px;"  class="newPosBtnLong" />
		<%} %>
			<input type="button" name="close" value="�Դ˹�Ҩ͹��" onclick="javascript:window.close();" style="width:100px;"  class="newPosBtnLong" />
			&nbsp;
			<%if(!"true".equals(hideAll)){ %><input type ="checkbox" name="chCheckAll" id="chCheckAll" onclick="selectAll();"  /> ���͡������ <%} %>
		</td>
	</tr>
</table>
<!-- RESULT -->
<%
//out.println("pageName["+pageName+"]DATA_LIST:"+(session.getAttribute("DATA_LIST")));
if(session.getAttribute("DATA_LIST") != null){ 
 //out.println("pageName["+pageName+"]DATA_LIST SIZE:"+((List)session.getAttribute("DATA_LIST")).size());
%>

	<%if("ItemBarcode".equalsIgnoreCase(pageName) ){ %>
	     <jsp:include page="popup_sub/ItemBarcodeResult.jsp?mutiple=<%=mutiple %>" /> 
	<%}%>

<%
}else if(session.getAttribute("search_submit") != null){ %>
 <font size="2" color="red">��辺������</font>
<%} %>
<!-- RESULT -->
</html:form>
</body>
</html>