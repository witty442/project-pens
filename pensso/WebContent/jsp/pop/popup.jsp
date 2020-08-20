<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.web.popup.PopupHelper"%> 

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
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<jsp:useBean id="popupForm" class="com.isecinc.pens.web.popup.PopupForm" scope="session" />
<%
    String pageName = popupForm.getPageName(); 

    String currentPage = "1";
    String hideAll = Utils.isNull(request.getParameter("hideAll"));//hide select all (checkbox)
    String selectone = Utils.isNull(request.getParameter("selectone"));//select multiple checkbox or radio(selectone)
    
    /** Gen Criteria Name **/
    String headName ="";
    String codeSearchTxtName = "";
	String descSearchTxtName = "";
    String[] headTextArr = PopupHelper.genHeadTextPopup(pageName);//Gen By PageName Serarch
    headName = headTextArr[0];
    codeSearchTxtName = headTextArr[1]; 
    descSearchTxtName = headTextArr[2];
    
    /** Store Select MutilCode in each Page **/
    String codes = Utils.isNull(session.getAttribute("codes"));
    String descs = Utils.isNull(session.getAttribute("descs"));
    String descs2 = Utils.isNull(session.getAttribute("descs2"));
    String descs3 = Utils.isNull(session.getAttribute("descs3"));
    
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
    document.popupForm.action = path + "/jsp/popupAction.do?do=searchAll";
    document.popupForm.submit();
   return true;
}
function selectAll(){
	document.getElementsByName("codes")[0].value = 'ALL,';
	document.getElementsByName("descs")[0].value = 'ALL,';
}
function selectMultiple(){
	var chk = document.getElementsByName("chCheck");
	var pageName ='<%=pageName%>';
	var idx = 2;
	//Add Select Muti in each page 
	var retCode = document.getElementsByName("codes")[0].value;
	var retDesc = document.getElementsByName("descs")[0].value;
	var retDesc2 = document.getElementsByName("descs2")[0].value;
	var retDesc3 = document.getElementsByName("descs3")[0].value;
	
	//alert(retDesc2);
	
	retCode = retCode.substring(0,retCode.length-1);
	retDesc = retDesc.substring(0,retDesc.length-1);
	retDesc2 = retDesc2.substring(0,retDesc2.length-1);
	retDesc3 = retDesc3.substring(0,retDesc3.length-1);
	
	//alert(retCode);
	
	//alert("idx:"+idx);
	if(idx ==1){
		//alert(retCode+":"+retKey+":"+retDesc);
		if(pageName=="SalesrepSales"){
			 window.opener.setDataPopupValue(retCode,retDesc,retDesc2,pageName);
		}else{
		     window.opener.setDataPopupValue(retCode,retDesc,pageName);
		}
	}else{
		//alert(pageName+":"+retCode+":"+retDesc+":"+retDesc2);
		if(pageName=="SalesrepSales"){
			//window.opener.setDataPopupValue(retCode,retDesc,retDesc2,retDesc3,pageName);
		}else{
			window.opener.setDataPopupValue(retCode,retDesc,pageName);
		}
	}
	
	/** Clear Value in Session **/
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/popup/ajax/popupSetValueSelected.jsp",
			data : "codes=" + encodeURIComponent('') +
			       "&keys=" + encodeURIComponent('') +
			       "&descs=" + encodeURIComponent('') +
			       "&descs2=" + encodeURIComponent('')+
			       "&descs3=" + encodeURIComponent(''),
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
	var selectone = document.getElementsByName("selectone");
	var currentPage = document.getElementsByName("currentPage")[0].value;
	var code = document.getElementsByName("code_temp");
	var desc = document.getElementsByName("desc");
	var desc2 = document.getElementsByName("desc2");
	var desc3 = document.getElementsByName("desc3");
	
	var retCode = '';
	var retDesc = '';
	var retDesc2 = '';
	var retDesc3 = '';
	
	var codesAllNew = "";
	var descsAllNew = "";
	var descsAllNew2 = "";
	var descsAllNew3 = "";

	if(no >= 20){
	   no = no - ( (currentPage-1) *20);
	}
	no = no -1;
	//alert("no["+no+"]checked:"+chk[no].checked);
    if(chk[no].checked){
    	//Add 
        retCode = code[no].value;
		retDesc = desc[no].value;
		retDesc2 = desc2[no].value;
		retDesc3 = desc3[no].value;
		
		//alert("code["+no+"]="+retCode);
	    codesAllNew = document.getElementsByName("codes")[0].value;
		descsAllNew = document.getElementsByName("descs")[0].value;
		descsAllNew2 = document.getElementsByName("descs2")[0].value;
		descsAllNew3 = document.getElementsByName("descs3")[0].value;
		
	    var found = chekCodeDupInCodesAll(retCode);

	    if(found == false){
	    	if(selectone[0].value =='false'){
		  	   codesAllNew += retCode +",";
		  	   descsAllNew += retDesc +",";
		  	   descsAllNew2 += retDesc2 +",";
		  	   descsAllNew3 += retDesc3 +",";
		  	}else{
		  	   // select One record
		  	   codesAllNew = retCode +",";
		  	   descsAllNew = retDesc +",";
		  	   descsAllNew2 = retDesc2 +",";
		  	   descsAllNew3 = retDesc3 +",";
		  	}
	    }
	    document.getElementsByName("codes")[0].value =  codesAllNew;
	    document.getElementsByName("descs")[0].value =  descsAllNew;
	    document.getElementsByName("descs2")[0].value =  descsAllNew2;
	    document.getElementsByName("descs3")[0].value =  descsAllNew3;
	    
	    //alert(no+":found["+found+"]["+document.getElementsByName("codes")[0].value+"]");
    }else{
    	
    	//remove
    	retCode = code[no].value;
		retDesc = desc[no].value;
		retDesc2 = desc2[no].value;
		retDesc3 = desc3[no].value;
		
		//alert(retCode);
	    codesAllNew = document.getElementsByName("codes")[0].value;
		descsAllNew = document.getElementsByName("descs")[0].value;
		descsAllNew2 = document.getElementsByName("descs2")[0].value;
		descsAllNew3 = document.getElementsByName("descs3")[0].value;
		
	    removeUnSelected(retCode);
    }
	
    <%if(selectone=="false"){%>
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/popup/ajax/popupSetValueSelected.jsp",
				data : "codes=" + encodeURIComponent(document.getElementsByName("codes")[0].value) +
				       "&keys=" + encodeURIComponent('') +
				       "&descs=" + encodeURIComponent(document.getElementsByName("descs")[0].value)+
				       "&descs2=" + encodeURIComponent(document.getElementsByName("descs2")[0].value)+
				       "&descs3=" + encodeURIComponent(document.getElementsByName("descs3")[0].value),
				//async: false,
				cache: true,
				success: function(){
				}
			}).responseText;
		});
	<%}%>
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
	var descsAll2 = document.getElementsByName("descs2")[0].value;
	var descsAll3 = document.getElementsByName("descs3")[0].value;
	
	var codesAllArray = codesAll.split(",");
	var descsAllArray = descsAll.split(",");
	var descsAllArray2 = descsAll2.split(",");
	var descsAllArray3 = descsAll3.split(",");
	
	var codesAllNew  = "";
	var descsAllNew  = "";
	var descsAllNew2  = "";
	var descsAllNew3  = "";
	
	for(var i=0;i < codesAllArray.length; i++){
   		if(codesAllArray[i] != codeCheck){
   		   codesAllNew += codesAllArray[i] +",";
	  	   descsAllNew += descsAllArray[i] +",";
	  	   descsAllNew2 += descsAllArray2[i] +",";
	  	   descsAllNew3 += descsAllArray3[i] +",";
   		}//if
	}//for
	
	codesAllNew = codesAllNew.substring(0,codesAllNew.length-1);
	descsAllNew = descsAllNew.substring(0,descsAllNew.length-1);
	descsAllNew2 = descsAllNew2.substring(0,descsAllNew2.length-1);
	descsAllNew3 = descsAllNew3.substring(0,descsAllNew3.length-1);
	
	document.getElementsByName("codes")[0].value =  codesAllNew;
	document.getElementsByName("descs")[0].value =  descsAllNew;
	document.getElementsByName("descs2")[0].value =  descsAllNew2;
	document.getElementsByName("descs3")[0].value =  descsAllNew3;
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
<input type="hidden" name="descs2" value ="<%=descs2%>" />
<input type="hidden" name="descs3" value ="<%=descs3%>" />
<input type="hidden" name="hideAll" value ="<%=hideAll%>" />
<input type="hidden" name="selectone" value ="<%=selectone%>" />

<table align="center" border="0" cellpadding="0" cellspacing="2"  width="100%" class="tableHead">
    <tr height="21px" class="headTitle1">
		<th width="15%" >&nbsp;</th> 
		<th width="90%" ><b>ค้นหาข้อมูล <%=headName%></b></th>
	</tr>
	<tr height="21px" class="headTitle1">
		<td width="15%" ><b><%=codeSearchTxtName %></b> </td>
		<td width="90%" ><html:text property="bean.codeSearch"  size="30" style="height:20px" styleClass="\" autoComplete=\"off"/>
		<input type="button" name="search"  class="newPosBtnLong"  value="   ค้นหา     " 
		onclick="searchPopup('<%=request.getContextPath()%>')" />
		</td> 
	</tr>
	<tr height="21px" class="headTitle1">
		<td nowrap><b><%=descSearchTxtName %></b></td>
		<td ><html:text property="bean.descSearch"  size="60" style="height:20px" styleClass="\" autoComplete=\"off"/></td>
	</tr>
</table>

<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%" >
	<tr>
		<td align="center">
			<input type="button" name="ok" value="ยืนยันเลือกข้อมูล" onclick="selectMultiple()"  class="newPosBtnLong" />
			<input type="button" name="close" value="Close" onclick="javascript:window.close();"  class="newPosBtnLong" />
			&nbsp;
			<%if(!"true".equals(hideAll)){ %><input type ="checkbox" name="chCheckAll" id="chCheckAll" onclick="selectAll();"  /> เลือกทั้งหมด <%} %>
		</td>
	</tr>
</table>
<!-- RESULT -->
<%if(session.getAttribute("DATA_LIST") != null){ %>
	  <!-- Result all PageName -->
	 <jsp:include page="popup_sub/popupResult.jsp" /> 
	
<%}else if(session.getAttribute("search_submit") != null){ %>
    <font size="2" color="red">ไม่พบข้อมูล</font>
<%} %>
<!-- RESULT -->
</html:form>
</body>
</html>