<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.web.popup.PopupHelper"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<%
User user = (User)session.getAttribute("user");
%>
<title>Popup Search </title>
 <%if(user.isMobile()){ %>
    <link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_mobile_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
 <%}else{%>
    <link rel="StyleSheet" href="${pageContext.request.contextPath}/css/popup_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
    <link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
 <% }%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 

<!-- Bootstrap -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap-4.5.2.min.css">
<script src="${pageContext.request.contextPath}/js/bootstrap/bootstrap-4.5.2.min.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap/grid.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

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
    
    String queryStr= request.getQueryString();
	 if(queryStr.indexOf("d-") != -1){
	 	queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
	 	System.out.println("queryStr:"+queryStr);
	    currentPage = request.getParameter(queryStr)==null?"1":request.getParameter(queryStr);
	 }
	 
   // System.out.println("DATA_LIST:"+session.getAttribute("DATA_LIST"));
%>

<script type="text/javascript">
function searchPopup(path) {
    document.popupForm.action = path + "/jsp/popupAction.do?do=search&action=newsearch";
    document.popupForm.submit();
   return true;
}
function gotoPage(currPage){
	document.popupForm.action = "${pageContext.request.contextPath}/jsp/popupAction.do?do=search&currPage="+currPage;
	document.popupForm.submit();
	return true;
}

function selectAll(){
	document.getElementsByName("codes")[0].value = 'ALL,';
	document.getElementsByName("descs")[0].value = 'ALL,';
}
function selectOneRadio(){
	var found = false;
	var pageName ='<%=pageName%>';
	var chRadio = document.getElementsByName("chCheck");
	var retCode = document.getElementsByName("code_temp");
	var retDesc = document.getElementsByName("desc");

	for(var i=0;i<chRadio.length;i++){
        if(chRadio[i].checked){
        	//alert(i+":"+code[i+1].value);
            window.opener.setDataPopupValue(retCode[i].value,retDesc[i].value,pageName);
        	window.close();
        	found = true;
            break;
        }
	}
	if(!found){
	  alert("กรุณาเลือกข้อมูลก่อน กด OK");
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
	
    <%if(selectone.equalsIgnoreCase("false")){%>
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
<meta name="viewport" content="width=device-width, initial-scale=1" />
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/popupAction">

<input type="hidden" name=pageName value ="<%=pageName%>" />
<input type="hidden" name="currentPage"  value ="<%=currentPage%>" />
<input type="hidden" name="codes" value ="<%=codes%>" />
<input type="hidden" name="descs" value ="<%=descs%>" />
<input type="hidden" name="hideAll" value ="<%=hideAll%>" />
<input type="hidden" name="selectone" value ="<%=selectone%>" />

<!-- <div class="container"> -->
  <!-- Head Table -->
  <div class="row mb-1">
     <div class="col-12 themed-grid-col" align="center">ค้นหาข้อมูล <%=headName%></div>
  </div>
  <div class="row mb-1">
     <div class="col-4 themed-grid-col-detail"><%=codeSearchTxtName %></div>
      <div class="col-8 themed-grid-col-detail">
        <html:text property="codeSearch"  styleClass="\" autoComplete=\"off"/>
      </div>
  </div>
   <div class="row mb-1">
     <div class="col-4 themed-grid-col-detail"><%=descSearchTxtName %></div>
      <div class="col-8 themed-grid-col-detail">
        <html:text property="descSearch"  styleClass="\" autoComplete=\"off"/>
        <input type="button" name="Search"  value=" ค้นหา " class="btn btn-primary" onclick="searchPopup('<%=request.getContextPath()%>')" />
      
      </div>
  </div>
  <%if(session.getAttribute("DATA_LIST") != null){ %>
	 <div class="row mb-1">
	      <div class="col-12 themed-grid-col-detail" align="center">
	        
			  <%if(selectone.equalsIgnoreCase("false")){ %>
			  <input type="button" name="ok" value="  OK  " onclick="selectMultiple()"  class="btn btn-primary" />
			<%}else{ %>
			  <input type="button" name="ok" value="  OK  " onclick="selectOneRadio()"  class="btn btn-primary" />
			<%} %>
	   
	         <input type="button" name="Close" value="ปิดหน้าจอนี้" onclick="javascript:window.close();" style="width:120px" class="btn btn-primary" />
				&nbsp;
			 <%if(!"true".equals(hideAll)){ %><input type ="checkbox" name="chCheckAll" id="chCheckAll" onclick="selectAll();"  />&nbsp; เลือกทั้งหมด <%} %>
	     </div>
	  </div>
  <%}	 %>
<!-- </div> -->

<!-- RESULT -->
<%if(session.getAttribute("DATA_LIST") != null){ %>
	<%if("Brand".equalsIgnoreCase(pageName) || "BrandStock".equalsIgnoreCase(pageName)
		|| "BrandProdShow".equalsIgnoreCase(pageName)|| "BrandStockVan".equalsIgnoreCase(pageName)){ %>
	    <%--  <jsp:include page="popup_sub/BrandResult.jsp" />  --%>
	    <jsp:include page="popup_sub/popupResult.jsp" /> 
	    
	<%}else if("Customer".equalsIgnoreCase(pageName) || "CustomerStock".equalsIgnoreCase(pageName)
			|| "CustomerLocation".equalsIgnoreCase(pageName) || "CustomerVanProdShow".equalsIgnoreCase(pageName)
			|| "CustomerCreditPromotion".equalsIgnoreCase(pageName)|| "CustomerLocNoTrip".equalsIgnoreCase(pageName)
			|| "CustomerStockMC".equalsIgnoreCase(pageName)
			){ 
	%>
	     <%-- <jsp:include page="popup_sub/CustomerResult.jsp" />  --%>
	     <jsp:include page="popup_sub/popupResult.jsp" /> 
	<%}else if("ItemStock".equalsIgnoreCase(pageName) || "ItemCreditPromotion".equalsIgnoreCase(pageName) 
			 || "ItemStockVan".equalsIgnoreCase(pageName)){ %>
	    <%--  <jsp:include page="popup_sub/ItemResult.jsp" />  --%>
	     <jsp:include page="popup_sub/popupResult.jsp" /> 
	<%}else if("PDStockVan".equalsIgnoreCase(pageName) ){ %>
	     <jsp:include page="popup_sub/pdStockVanResult.jsp" /> 
	<%}else{ %>  
	   <!-- Result all PageName -->
	   <jsp:include page="popup_sub/popupResult.jsp" /> 
	<%} %> 
<%}else if(session.getAttribute("search_submit") != null){ %>
    <font size="2" color="red">ไม่พบข้อมูล</font>
<%} %>
<!-- RESULT -->

 <!-- Button Action -->
 <%if(session.getAttribute("DATA_LIST") != null){ %>
   <div class="row mb-1">
      <div class="col-12 themed-grid-col-detail" align="center">
        
		<%if(selectone.equalsIgnoreCase("false")){ %>
		  <input type="button" name="ok" value="  OK  " onclick="selectMultiple()"  class="btn btn-primary" />
		<%}else{ %>
		  <input type="button" name="ok" value="  OK  " onclick="selectOneRadio()"  class="btn btn-primary" />
		<%} %>
   
         <input type="button" name="close" value="ปิดหน้าจอนี้" onclick="javascript:window.close();" style="width:120px" class="btn btn-primary" />
     </div>
  </div>
 <%} %>
</html:form>
</body>
</html>