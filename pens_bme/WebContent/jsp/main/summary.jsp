<%@page import="com.isecinc.pens.web.batchtask.BatchTaskConstants"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="summaryForm" class="com.isecinc.pens.web.summary.SummaryForm" scope="session" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<%
   /*clear session form other page */
   SessionUtils.clearSessionUnusedForm(request, "summaryForm");

	String storeType ="";
	User user = (User)session.getAttribute("user");
	String screenWidth = Utils.isNull(session.getAttribute("screenWidth"));
	String screenHeight = Utils.isNull(session.getAttribute("screenHeight"));
	String pageName = Utils.isNull(request.getParameter("page"));
%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/summary.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<script type="text/javascript">

function loadMe(){
	<%if("lotus".equalsIgnoreCase(request.getParameter("page")) || "sumByGroupCode".equalsIgnoreCase(request.getParameter("page"))  ) {%>
	    new Epoch('epoch_popup', 'th', document.getElementById('salesDateFrom'));
	    new Epoch('epoch_popup', 'th', document.getElementById('salesDateTo'));
	<%}else if("bigc".equalsIgnoreCase(request.getParameter("page")) 
			|| "bigc_temp".equalsIgnoreCase(request.getParameter("page")) ) {%>
	    new Epoch('epoch_popup', 'th', document.getElementById('salesDateFrom'));
	    new Epoch('epoch_popup', 'th', document.getElementById('salesDateTo'));
    <%}else if("tops".equalsIgnoreCase(request.getParameter("page"))) {%>
	    new Epoch('epoch_popup', 'th', document.getElementById('salesDateFrom'));
	    new Epoch('epoch_popup', 'th', document.getElementById('salesDateTo'));
   <%}else if("king".equalsIgnoreCase(request.getParameter("page"))) {%>
	    new Epoch('epoch_popup', 'th', document.getElementById('salesDateFrom'));
	    new Epoch('epoch_popup', 'th', document.getElementById('salesDateTo'));
	<%} else if("physical".equalsIgnoreCase(request.getParameter("page"))) {%>
        new Epoch('epoch_popup', 'th', document.getElementById('countDateFrom'));
        new Epoch('epoch_popup', 'th', document.getElementById('countDateTo'));
    <%} else if("diff_stock".equalsIgnoreCase(request.getParameter("page"))) {%>
        new Epoch('epoch_popup', 'th', document.getElementById('asOfDate'));
    <%} else if("openBillRobinsonReport".equalsIgnoreCase(request.getParameter("page"))) {%>
        new Epoch('epoch_popup', 'th', document.getElementById('asOfDate'));
	<%}else if("onhandLotusPeriod".equalsIgnoreCase(request.getParameter("page"))) {%>
	    new Epoch('epoch_popup', 'th', document.getElementById('asOfDateFrom'));
	    new Epoch('epoch_popup', 'th', document.getElementById('asOfDateTo'));
	<%}else if("bmeTrans".equalsIgnoreCase(request.getParameter("page"))) {%>
	    new Epoch('epoch_popup', 'th', document.getElementById('asOfDateFrom'));
	    new Epoch('epoch_popup', 'th', document.getElementById('asOfDateTo'));
	    
    <%}else if("onhandMTT".equalsIgnoreCase(request.getParameter("page")) 
    		 || "onhandMTTDetail".equalsIgnoreCase(request.getParameter("page"))
    		 || "sizeColorBigC".equalsIgnoreCase(request.getParameter("page"))
    		 || "sizeColorLotus".equalsIgnoreCase(request.getParameter("page"))
    		 || "sizeColorKing".equalsIgnoreCase(request.getParameter("page"))
    		 || "reportEndDateLotus".equalsIgnoreCase(request.getParameter("page"))
    		 || "ReportStockWacoalLotus".equalsIgnoreCase(request.getParameter("page"))
    		 || "onhandBigC".equalsIgnoreCase(request.getParameter("page"))
 			 || "onhandBigCSP".equalsIgnoreCase(request.getParameter("page"))
 			 || "monthEndLotus".equalsIgnoreCase(request.getParameter("page"))
 			 || "onhandAsOf_Robinson".equalsIgnoreCase(request.getParameter("page"))
 			 || "onhandAsOfRobinson".equalsIgnoreCase(request.getParameter("page"))
 			 || "onhandTops".equalsIgnoreCase(request.getParameter("page"))
 			 || "onhandLotus".equalsIgnoreCase(request.getParameter("page"))
 			 || "onhandAsOfKing".equalsIgnoreCase(request.getParameter("page"))
    		 ) {%>
	     new Epoch('epoch_popup', 'th', document.getElementById('salesDate'));
	 <%}%>
	
}

function search(path){
	var form = document.summaryForm;
	<%if("lotus".equalsIgnoreCase(request.getParameter("page"))) {%>
	   var salesDateFrom = form.salesDateFrom.value;
	   var salesDateTo = form.salesDateTo.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	  // var pensCustCodeTo = form.pensCustCodeTo.value;
	   var fileName = form.fileName.value;
	   
	   if(salesDateFrom =="" && salesDateTo =="" && pensCustCodeFrom =="" &&fileName ==""){
		   alert("��سҡ�͡������㹡�ä������ҧ����˹����¡��");
		   return false;
	   }
	<%}else if("sumByGroupCode".equalsIgnoreCase(request.getParameter("page"))) {%>
	
	   var salesDateFrom = form.salesDateFrom.value;
	   var salesDateTo = form.salesDateTo.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	  
	   if(salesDateFrom =="" && salesDateTo =="" && pensCustCodeFrom ==""){
		   alert("��سҡ�͡������㹡�ä������ҧ����˹����¡��");
		   return false;
	   }
   <%}else if("bigc".equalsIgnoreCase(request.getParameter("page"))
             || "bigc_temp".equalsIgnoreCase(request.getParameter("page")) ) {%>
	   var salesDateFrom = form.salesDateFrom.value;
	   var salesDateTo = form.salesDateTo.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   //var pensCustCodeTo = form.pensCustCodeTo.value;
	   var fileName = form.fileName.value;
	   
	   if(salesDateFrom =="" && salesDateTo =="" && pensCustCodeFrom ==""  &&fileName ==""){
		   alert("��سҡ�͡������㹡�ä������ҧ����˹����¡��");
		   return false;
	   }
   <%}else if("tops".equalsIgnoreCase(request.getParameter("page"))) {%>
	   var salesDateFrom = form.salesDateFrom.value;
	   var salesDateTo = form.salesDateTo.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   //var pensCustCodeTo = form.pensCustCodeTo.value;
	   var fileName = form.fileName.value;
	   
	   if(salesDateFrom =="" && salesDateTo =="" && pensCustCodeFrom ==""  &&fileName ==""){
		   alert("��سҡ�͡������㹡�ä������ҧ����˹����¡��");
		   return false;
	   } 
	   
   <%}else if("king".equalsIgnoreCase(request.getParameter("page"))) {%>
	   var salesDateFrom = form.salesDateFrom.value;
	   var salesDateTo = form.salesDateTo.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   //var pensCustCodeTo = form.pensCustCodeTo.value;
	   var fileName = form.fileName.value;
	   var groupCode = form.groupCode.value;
	   
	   if(salesDateFrom =="" && salesDateTo =="" && pensCustCodeFrom ==""  &&fileName =="" && groupCode.value ==""){
		   alert("��سҡ�͡������㹡�ä������ҧ����˹����¡��");
		   return false;
	   } 
	<%}else if("physical".equalsIgnoreCase(request.getParameter("page"))) {%>
	   var salesDateFrom = form.countDateFrom.value;
	   var salesDateTo = form.countDateTo.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   var pensCustCodeTo = form.pensCustCodeTo.value;
	   var fileName = form.fileName.value;
	   
	   if(salesDateFrom =="" && salesDateTo =="" && pensCustCodeFrom =="" && pensCustCodeTo =="" &&fileName ==""){
		   alert("��سҡ�͡������㹡�ä������ҧ����˹����¡��");
		   return false;
	   }

	<%}else if("diff_stock".equalsIgnoreCase(request.getParameter("page"))) {%>
	   var asOfDateFrom = form.asOfDate.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   
	   if(asOfDateFrom =="" && pensCustCodeFrom ==""){
		   alert("��سҡ�͡������㹡�ä������ҧ����˹����¡��");
		   return false;
	   }
    <%}else if("openBillRobinsonReport".equalsIgnoreCase(request.getParameter("page"))) {%>
	   var asOfDateFrom = form.asOfDate.value;
	  
	   if(asOfDateFrom ==""){
		   alert("��سҡ�͡������ �ѹ����Դ Order �ç�ҹ/ �ѹ��� Issue �ҡ PC");
		   return false;
	   }
	      
   <%}else if("onhandLotus".equalsIgnoreCase(request.getParameter("page"))
		   || "monthEndLotus".equalsIgnoreCase(request.getParameter("page"))
		   || "onhandTops".equalsIgnoreCase(request.getParameter("page"))
		   || "onhandAsOfRobinson".equalsIgnoreCase(request.getParameter("page"))
		   || "onhandAsOf_Robinson".equalsIgnoreCase(request.getParameter("page"))
		   || "onhandMTT".equalsIgnoreCase(request.getParameter("page")) 
		   || "onhandMTTDetail".equalsIgnoreCase(request.getParameter("page"))
		   || "onhandBigC".equalsIgnoreCase(request.getParameter("page"))
		   || "onhandBigCSP".equalsIgnoreCase(request.getParameter("page"))
		   || "reportEndDateLotus".equalsIgnoreCase(request.getParameter("page"))
		   || "ReportStockWacoalLotus".equalsIgnoreCase(request.getParameter("page"))
		   || "sizeColorLotus".equalsIgnoreCase(request.getParameter("page"))
		   || "sizeColorBigC".equalsIgnoreCase(request.getParameter("page"))
		   || "onhandAsOfKing".equalsIgnoreCase(request.getParameter("page"))
		   || "sizeColorKing".equalsIgnoreCase(request.getParameter("page"))
	 ) {%>
	   var asOfDateFrom = form.salesDate.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   
	   if(asOfDateFrom ==""){ 
		   alert("��سҡ�͡�������ѹ��� As Of");
		   return false;
	   }
	    if(pensCustCodeFrom ==""){ 
		   alert("��سҡ�͡������������ҹ���");
		   return false;
	   } 
	   /**Case Select All Store ->insert groupCode */
	   <%if( "sizeColorLotus".equalsIgnoreCase(request.getParameter("page"))
	       || "sizeColorBigC".equalsIgnoreCase(request.getParameter("page"))
	       || "sizeColorKing".equalsIgnoreCase(request.getParameter("page"))
	   ){%>
	      var group = form.group.value;
	      if(pensCustCodeFrom=='ALL' && group ==''){
	    	  alert("��سҡ�͡������ Group �ó����͡ ALL ��ҹ���");
	    	  form.group.focus();
	    	  return false;
	      }
	   <%}%>
	   
   <%}else if("bmeTrans".equalsIgnoreCase(request.getParameter("page"))) {%>
	   var asOfDateFrom = form.asOfDateFrom.value;
	   var asOfDateTo = form.asOfDateTo.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   
	   if(asOfDateFrom =="" || asOfDateTo==""){ 
		   alert("��سҡ�͡�������ѹ���  From-To");
		   return false;
	   }
	    if(pensCustCodeFrom ==""){ 
		   alert("��سҡ�͡������������ҹ���");
		   return false;
	   }   
   
	 <%}else if("onhandLotusPeriod".equalsIgnoreCase(request.getParameter("page"))) {%>
		   var asOfDateFrom = form.asOfDateFrom.value;
		   var asOfDateTo = form.asOfDateTo.value;
		   var pensCustCodeFrom = form.pensCustCodeFrom.value;
		   
		   if(asOfDateFrom ==""){ 
			   alert("��سҡ�͡�������ѹ����� From");
			   return false;
		   }
		   if(asOfDateTo ==""){ 
			   alert("��سҡ�͡�������ѹ����� To");
			   return false;
		   }
		    if(pensCustCodeFrom ==""){ 
			   alert("��سҡ�͡������������ҹ���");
			   return false;
		   } 
	 <%}else if("onhand".equalsIgnoreCase(request.getParameter("page"))) {%>
	      var location = document.getElementById("location").value ;//form.location.value;
	   
	      if(location ==""){ 
		    alert("��س����͡������ Location");
		    return false;
	      }     
	 <% }%>
	 
	 /** new search in page criteria */
	 <%if( !"onhandBigCOracle".equalsIgnoreCase(request.getParameter("page"))) { %>
		form.action = path + "/jsp/summaryAction.do?do=search&page=<%=request.getParameter("page")%>";
		form.submit();
	 <%}else{%>
	     return searchInPage(path);
	 <%}%>
	return true;
}

function exportExcel(path){
	var form = document.summaryForm;
	form.action = path + "/jsp/summaryAction.do?do=export&page=<%=request.getParameter("page")%>";
	form.submit();
	return true;
}

function genMonthEnd(path,storeType){
   var form = document.summaryForm;
   var asOfDateFrom = form.salesDate.value;
   var pensCustCodeFrom = form.pensCustCodeFrom.value;
   
   if(confirm("��س��׹�ѹ��� Gen Month End")){
	   if(asOfDateFrom ==""){ 
		   alert("��سҡ�͡�������ѹ��� As Of");
		   asOfDateFrom.focus();
		   return false;
	   }
	    if(pensCustCodeFrom ==""){ 
		   alert("��سҡ�͡������������ҹ���");
		   pensCustCodeFrom.focus();
		   return false;
	   } 
		form.action = path + "/jsp/summaryAction.do?do=genMonthEnd&page=<%=request.getParameter("page")%>&storeType="+storeType;
		form.submit();
		return true;
   }
}

function genEndDateLotus(path){
	var form = document.summaryForm;
	var asOfDateFrom = form.salesDate.value;
	var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   
    var param = "&customerCode="+form.pensCustCodeFrom.value;
        param += "&salesDate="+form.salesDate.value;
        param += "&pageName=GEN_STOCK_ENDDDATE_LOTUS";
        param += "&pageStaus=child";
        
        if(asOfDateFrom ==""){ 
 		   alert("��سҡ�͡�������ѹ��� As Of");
 		   asOfDateFrom.focus();
 		   return false;
 	   }
 	    if(pensCustCodeFrom ==""){ 
 		   alert("��سҡ�͡������������ҹ���");
 		   pensCustCodeFrom.focus();
 		   return false;
 	   } 
 	    
        if(confirm("��س��׹�ѹ��� Stock End Date")){
			url = path + "/jsp/interfacesAction.do?do=runBatch&action="+param;
			//window.open(encodeURI(url),"",
					   //"menubar=no,resizable=no,toolbar=no,scrollbars=no,width=<%=screenWidth%>px,height=<%=screenHeight%>px,status=no,left=0,top= 0");
			var params = [
			              'height='+screen.height,
			              'width='+screen.width,
			              'fullscreen=yes' // only works in IE, but here for completeness
			          ].join(',');
			var popup = window.open(encodeURI(url), 'popup_window', params); 
			popup.moveTo(0,0);
        }
}

function genReportEndDate(path){
	var form = document.summaryForm;
	var asOfDateFrom = form.salesDate.value;
	var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   
    var param = "&customerCode="+form.pensCustCodeFrom.value;
        param += "&salesDate="+form.salesDate.value;
        param += "&pageName=GEN_STOCK_REPORT_ENDDDATE_LOTUS";
        param += "&pageStaus=child";
        
        if(asOfDateFrom ==""){ 
  		   alert("��سҡ�͡�������ѹ��� As Of");
  		   asOfDateFrom.focus();
  		   return false;
  	   }
  	    if(pensCustCodeFrom ==""){ 
  		   alert("��سҡ�͡������������ҹ���");
  		   pensCustCodeFrom.focus();
  		   return false;
  	   } 
  	    
  	    //Validate Endate Stock
  	    var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/validEndDateStockLotusAjax.jsp",
			data : "storeCode=" + pensCustCodeFrom+"&asOfDate="+asOfDateFrom,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
  	    
  	   // alert(returnString);
  	    if(returnString != ''){
  	    	if(returnString =='END_STOCK_LOTUS_NOT_FOUND'){
  	    		alert("��ͧ�ӡ�� End Date Stock ��ԧ���ҧ���� 1 ���� ");
  	    	}else if(returnString =='END_STOCK_LOTUS_DATE_MUST_MORE_THAN_ENDING_DATE'){
  	    		alert("��ͧ�к� �ѹ�����(as of) ����ҡ���� �ѹ���Դʵ�͡����ش ");
  	    	}
  	    	return false;
  	    }
  	    
        if(confirm("��س��׹�ѹ���  Data ���º��º�Ѻʵ�͡")){
			url = path + "/jsp/interfacesAction.do?do=runBatch&action="+param;
			//window.open(encodeURI(url),"",
					   //"menubar=no,resizable=no,toolbar=no,scrollbars=no,width=<%=screenWidth%>px,height=<%=screenHeight%>px,status=no,left=0,top= 0");
			var params = [
			              'height='+screen.height,
			              'width='+screen.width,
			              'fullscreen=yes' // only works in IE, but here for completeness
			          ].join(',');
			var popup = window.open(encodeURI(url), 'popup_window', params); 
			popup.moveTo(0,0);
        }
}

function clearForm(path){
	var form = document.summaryForm;
	form.action = path + "/jsp/summaryAction.do?do=prepare&action=new&page=<%=request.getParameter("page")%>";
	form.submit();
	return true;
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
	    	<%if("onhand".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEFromWacoal"/>
				</jsp:include>
			<%}else if("onhandLotus".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEOnhandLotus"/>
				</jsp:include>
			<%}else if("onhandTops".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEOnhandTops"/>
				</jsp:include>
			<%}else if("monthEndLotus".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEMonthEndLotus"/>
				</jsp:include>
		    <%}else if("onhandBigC".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEOnhandBigC"/>
				</jsp:include>
			<%}else if("onhandBigCSP".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEOnhandBigCSP"/>
				</jsp:include>
			<%}else if("onhandLotusPeriod".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEOnhandLotusPeriod"/>
				</jsp:include>
			<%}else if("physical".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEFromPhysical"/>
				</jsp:include>
			<%}else if("diff_stock".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEDiffStock"/>
				</jsp:include>
			<%}else if("lotus".equalsIgnoreCase(request.getParameter("page"))){ %>
			   <jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEFromLotus"/>
				</jsp:include>
			<%}else if("bigc".equalsIgnoreCase(request.getParameter("page"))){ %>
			   <jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEFromBigC"/>
				</jsp:include>
			<%}else if("bigc_temp".equalsIgnoreCase(request.getParameter("page"))){ %>
			   <jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEFromBigCTemp"/>
				</jsp:include>
			<%}else if("tops".equalsIgnoreCase(request.getParameter("page"))){ %>
			   <jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEFromTops"/>
				</jsp:include>
			<%}else if("king".equalsIgnoreCase(request.getParameter("page"))){ %>
			   <jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEFromKing"/>
				</jsp:include>
			<%}else if("onhandMTT".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEOnhandMTT"/>
				</jsp:include>
			<%}else if("onhandAsOfKing".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEOnhandKing"/>
				</jsp:include>
			<%}else if("onhandMTTDetail".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEOnhandMTTDetail"/>
				</jsp:include>
			<%}else if("sumbyGroupCode".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEByGroupCode"/>
				</jsp:include>
			<%}else if("bmeTrans".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMETransaction"/>
				</jsp:include>
			<%}else if("sizeColorBigC".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMESizeColorBigC"/>
				</jsp:include>
		   <%}else if("onhandBigCOracle".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEOnhandBigCAtOracle"/>
				</jsp:include>
		
		    <%}else if("sizeColorLotus".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMESizeColorLotus"/>
				</jsp:include>
		    <%}else if("sizeColorKing".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMESizeColorKing"/>
				</jsp:include>
			 <%}else if("reportEndDateLotus".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="reportEndDateLotus"/>
				</jsp:include>
			<%}else if("onhandAsOfRobinson".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="onhandAsOf_Robinson"/>
				</jsp:include>
		    <%}else if("onhandAsOf_Robinson".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="onhandAsOf_Robinson"/>
				</jsp:include>
		    <%}else if("openBillRobinsonReport".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="openBillRobinsonReport"/>
				</jsp:include>
			<%}else{ %>
				<jsp:include page="../program.jsp">
					<jsp:param name="function" value="<%=pageName %>"/>
				</jsp:include>
			<%} %>
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
						<html:form action="/jsp/summaryAction">
						<jsp:include page="../error.jsp"/>
						
						<div id="div_message" style="font-size:15px;color:green" align="center"></div> 
						<div id="div_error_message" style="font-size:15px;color:red" align="center"></div> 
						
					<div id="div_m" align="center">	
					
						<%if("lotus".equalsIgnoreCase(request.getParameter("page"))) {%>
						   <jsp:include page="criteria/LotusCriteria.jsp" /> 
						<%}else if("sumByGroupCode".equalsIgnoreCase(request.getParameter("page"))) {%>
						    <jsp:include page="criteria/SumByGroupCodeCriteria.jsp" /> 
						<%}else if("bigc".equalsIgnoreCase(request.getParameter("page")) 
								|| "bigc_temp".equalsIgnoreCase(request.getParameter("page")) ) {%>
						   <jsp:include page="criteria/BigCCriteria.jsp" /> 
						<%}else if("tops".equalsIgnoreCase(request.getParameter("page")) ) {%>
						      <jsp:include page="criteria/TopsCriteria.jsp" /> 
						<%}else if("king".equalsIgnoreCase(request.getParameter("page"))) {%>
							  <jsp:include page="criteria/KingCriteria.jsp" /> 
						<%}else if("physical".equalsIgnoreCase(request.getParameter("page"))) {%>
						     <jsp:include page="criteria/PhysicalCriteria.jsp" /> 
						<%}else if("diff_stock".equalsIgnoreCase(request.getParameter("page"))) {%>
						     <jsp:include page="criteria/DiffStockCriteria.jsp" /> 
				        <%}else if("openBillRobinsonReport".equalsIgnoreCase(request.getParameter("page"))) {%>
						     <jsp:include page="criteria/openBillRobinsonReportCriteria.jsp" /> 
						<%}else if("onhand".equalsIgnoreCase(request.getParameter("page"))) {%>
						     <jsp:include page="criteria/OnhandCriteria.jsp" /> 
						 <%}else if("onhandBigCOracle".equalsIgnoreCase(request.getParameter("page"))) {%>
						     <jsp:include page="criteria/OnhandBigCOracleCriteria.jsp" /> 
						<%}else if("onhandLotus".equalsIgnoreCase(request.getParameter("page"))
								|| "onhandBigC".equalsIgnoreCase(request.getParameter("page"))
								|| "onhandBigCSP".equalsIgnoreCase(request.getParameter("page"))
								|| "onhandMTT".equalsIgnoreCase(request.getParameter("page"))
								|| "onhandMTTDetail".equalsIgnoreCase(request.getParameter("page"))
								|| "sizeColorBigC".equalsIgnoreCase(request.getParameter("page"))
								|| "sizeColorLotus".equalsIgnoreCase(request.getParameter("page"))
								|| "sizeColorKing".equalsIgnoreCase(request.getParameter("page"))
								|| "monthEndLotus".equalsIgnoreCase(request.getParameter("page"))
								|| "reportEndDateLotus".equalsIgnoreCase(request.getParameter("page"))
								|| "onhandTops".equalsIgnoreCase(request.getParameter("page"))
							    || "onhandAsOfRobinson".equalsIgnoreCase(request.getParameter("page"))
							    || "onhandAsOf_Robinson".equalsIgnoreCase(request.getParameter("page"))
							    || "onhandAsOfKing".equalsIgnoreCase(request.getParameter("page"))
						 ){
							
								String hideAll = "";
								if(   "onhandMTTDetail".equalsIgnoreCase(request.getParameter("page"))
									||"reportEndDateLotus".equalsIgnoreCase(request.getParameter("page"))
								){
									hideAll = "true";
								}
							    storeType ="lotus";//defualt
								if(   "onhandBigC".equalsIgnoreCase(request.getParameter("page"))
								   || "sizeColorBigC".equalsIgnoreCase(request.getParameter("page")) 
								   || "onhandBigCSP".equalsIgnoreCase(request.getParameter("page"))
								){
									storeType="bigc";
								}else if(  "onhandMTT".equalsIgnoreCase(request.getParameter("page")) 
										|| "onhandMTTDetail".equalsIgnoreCase(request.getParameter("page"))){
									storeType="MTT";
								}else if("onhandTops".equalsIgnoreCase(request.getParameter("page"))){
									storeType="TOPS";
								}else if("onhandAsOf_Robinson".equalsIgnoreCase(request.getParameter("page"))
										|| "onhandAsOfRobinson".equalsIgnoreCase(request.getParameter("page"))){
									storeType="ROBINSON";
								}else if( "onhandAsOfKing".equalsIgnoreCase(request.getParameter("page"))
										|| "sizeColorKing".equalsIgnoreCase(request.getParameter("page"))){
									storeType="DUTYFREE";
								}
								%>
					    	<table  border="0" cellpadding="3" cellspacing="0" class="body" width="65%">
						        <tr>
								<td align="right"  nowrap>�ҡ �ѹ����� (As Of)<font color="red">*</font> 
								&nbsp;&nbsp;
								<html:text property="onhandSummary.salesDate" styleId="salesDate" readonly="true"/>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								</td>
								<td align="left" width="50%" nowrap>
								   <% if("reportEndDateLotus".equalsIgnoreCase(request.getParameter("page"))){ %>
								      �Դ�ͺʵ�͡����ش�ѹ���:
								   	  <html:text property="endDate" styleId="endDate" size="20" styleClass="disableText" readonly="true"/> 
								   	  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								   	       �����Ţ�¶֧�ѹ���:
								   	  <html:text property="endSaleDate" styleId="endSaleDate" size="20" styleClass="disableText" readonly="true"/> 
								   <%} %>
								</td>
								</tr>
						       <tr>
									<td align="right"  nowrap>������ҹ���<font color="red">*</font>
									  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									   <html:text property="onhandSummary.pensCustCodeFrom" styleId="pensCustCodeFrom" 
									    size="20" onkeypress="getCustNameKeypress('${pageContext.request.contextPath}',event,this,'pensCustNameFrom')"
									    styleClass="\" autoComplete=\"off"/>
									    &nbsp;
									    <input type="button" name="x1" value="..." onclick="openPopupCustomerAll('${pageContext.request.contextPath}','from','<%=storeType%>','<%=hideAll%>')"/>
									</td>
									<td align="left" width="30%"  nowrap> 
									    <html:text property="onhandSummary.pensCustNameFrom" styleId="pensCustNameFrom" readonly="true" styleClass="disableText" size="50"/>
									</td>
								</tr>
								
								<% if(     "sizeColorBigC".equalsIgnoreCase(request.getParameter("page")) 
										|| "sizeColorLotus".equalsIgnoreCase(request.getParameter("page"))
										|| "sizeColorKing".equalsIgnoreCase(request.getParameter("page"))
										|| "onhandBigCSP".equalsIgnoreCase(request.getParameter("page")) 
										|| "onhandMTTDetail".equalsIgnoreCase(request.getParameter("page")) 
									){%>
									 <tr>
										<td align="right"  nowrap>�ѹ�������ش����ա�õ�Ǩ�Ѻʵ�͡<font color="red"></font>
										<html:text property="onhandSummary.initDate" styleId="initDate" size="20" styleClass="disableText" readonly="true"/> 
										</td>	
										<td align="left" width="30%"> </td>
									</tr>
								<%} %>
								<% if(   "onhandMTT".equalsIgnoreCase(request.getParameter("page")) 
									  || "onhandAsOfKing".equalsIgnoreCase(request.getParameter("page")) ){%>
									 <tr>
										<td align="right"  nowrap>�ѹ�������ش����ա�� Load �����ŹѺʵ�͡ 
										
										  <html:text property="onhandSummary.initDate" styleId="initDate" size="20" styleClass="disableText" readonly="true"/> 
										  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										</td>	
										<td align="left" width="30%"> 
										  ������ҹ���� Oracle ���   <html:text property="onhandSummary.custNo" styleId="initDate" size="20" styleClass="disableText" readonly="true"/>  
										</td>
									</tr>
								<%} %>
								<tr>
									<td align="right" width="30%"  nowrap>
									     Pens Item From &nbsp;&nbsp;<html:text property="onhandSummary.pensItemFrom" styleId="pensItemFrom" 
									     styleClass="\" autoComplete=\"off"/>
									     &nbsp;
									    <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','from','<%=storeType %>')"/>
									</td>
									<td align="left" width="30%"  nowrap>
									     Pens Item To&nbsp;&nbsp; <html:text property="onhandSummary.pensItemTo" styleId="pensItemTo" 
									     styleClass="\" autoComplete=\"off"/>
									     <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','to','<%=storeType %>')"/>   
									</td>
								</tr>
								<tr>
									<td align="right" width="30%"  nowrap>Group &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									    <html:text property="onhandSummary.group" styleId="group" styleClass="\" autoComplete=\"off"/>
									    &nbsp;
									    <% if( "reportEndDateLotus".equalsIgnoreCase(request.getParameter("page"))
									        || "onhandLotus".equalsIgnoreCase(request.getParameter("page")) 
									        || "onhandTops".equalsIgnoreCase(request.getParameter("page"))
									        || "onhandAsOfRobinson".equalsIgnoreCase(request.getParameter("page"))
									        || "onhandAsOf_Robinson".equalsIgnoreCase(request.getParameter("page"))
									        || "sizeColorLotus".equalsIgnoreCase(request.getParameter("page"))
									        || "sizeColorBigC".equalsIgnoreCase(request.getParameter("page"))
									        || "onhandAsOfKing".equalsIgnoreCase(request.getParameter("page"))
									       ){ 
									       /** Select One group **/
									       %>
									       <input type="button" name="x1" value="..." onclick="openPopupGroup('${pageContext.request.contextPath}','selectOne','<%=storeType%>')"/>
									    <%}else{ 
									    	 /** Select Multi group **/
									    %>
									       <input type="button" name="x1" value="..." onclick="openPopupGroup('${pageContext.request.contextPath}','','<%=storeType%>')"/>
									    <%} %>
									     <html:hidden property="onhandSummary.groupDesc" styleId="groupDesc" />
									  </td>
									<td align="left" width="30%">  
								       <% if(   "sizeColorBigC".equalsIgnoreCase(request.getParameter("page")) 
								    		 || "sizeColorLotus".equalsIgnoreCase(request.getParameter("page"))
								    		 || "onhandBigCSP".equalsIgnoreCase(request.getParameter("page")) 
								    		 || "reportEndDateLotus".equalsIgnoreCase(request.getParameter("page")) 
								    		 || "onhandMTTDetail".equalsIgnoreCase(request.getParameter("page")) 
								    		 || "onhandBigC".equalsIgnoreCase(request.getParameter("page")) 
								    		 || "sizeColorKing".equalsIgnoreCase(request.getParameter("page")) 
								    		 ){%>
									      <html:checkbox property="onhandSummary.dispHaveQty" />�ʴ�੾����¡�÷���ըӹǹ
									    <%} %>
									 </td>
							   </tr>
							    <% if(    "sizeColorLotus_XX".equalsIgnoreCase(request.getParameter("page")) 
								       || "onhandLotus".equalsIgnoreCase(request.getParameter("page"))
								       || "onhandBigCSP".equalsIgnoreCase(request.getParameter("page")) 
								       || "monthEndLotus".equalsIgnoreCase(request.getParameter("page"))  
								       || "reportEndDateLotus".equalsIgnoreCase(request.getParameter("page")) 
								       || "onhandMTT".equalsIgnoreCase(request.getParameter("page")) 
								       || "onhandTops".equalsIgnoreCase(request.getParameter("page"))
								       || "onhandAsOfRobinson".equalsIgnoreCase(request.getParameter("page"))
								       || "onhandAsOf_Robinson".equalsIgnoreCase(request.getParameter("page"))
								       || "onhandAsOfKing".equalsIgnoreCase(request.getParameter("page"))
							    	){%>
									   <tr>
											<td align="right" width="30%"  nowrap>�ʴ���� &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											    <html:select property="summaryType">
												   <html:option value="GroupCode">Group Code</html:option>
												   <html:option value="PensItem">Pens Item</html:option>
											     </html:select>
											 </td>
											<td align="left" width="30%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </td>    
									   </tr>
							   <%} %>
							  </table>
						<%}else if("bmeTrans".equalsIgnoreCase(request.getParameter("page"))) {
							    storeType ="";
						%>
							 <jsp:include page="criteria/BmeTransCriteria.jsp" /> 
						<%}else if("onhandLotusPeriod".equalsIgnoreCase(request.getParameter("page"))) {%>
						     <jsp:include page="criteria/OnhandLotusPeriodCriteria.jsp" /> 
						     
					    <%}else if("ReportStockWacoalLotus".equalsIgnoreCase(request.getParameter("page"))) {%>
					         <jsp:include page="criteria/ReportStockWacoalLotusCriteria.jsp" /> 
					         
						<%} %>

					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center" width="60%">
								<a href="javascript:search('${pageContext.request.contextPath}')">
								  <input type="button" value="����" class="newPosBtn"> 
								</a>&nbsp;
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value="Clear" class="newPosBtn">
								</a>&nbsp;
								<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export" class="newPosBtn">
								</a>
							</td>
							 <td align="right" width="40%" nowrap>
								<%if("onhandLotus".equalsIgnoreCase(request.getParameter("page"))){%>
								   <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICKADMIN}) ){%>
								 
									<a href="javascript:genEndDateLotus('${pageContext.request.contextPath}')">
									  <input type="button" value="Gen Stock End Date" class="newPosBtn">
									</a>
								<%}} %>
								<%if("reportEndDateLotus".equalsIgnoreCase(request.getParameter("page"))){%>
								   <%if ( Utils.userInRole(user,new String[]{User.ADMIN,User.PICKADMIN}) ){%>
								 
									<a href="javascript:genReportEndDate('${pageContext.request.contextPath}')">
									  <input type="button" value="Gen Data ���º��º�Ѻʵ�͡" class="newPosBtn">
									</a>
									<%-- <a href="javascript:genStockOnhandTemp('${pageContext.request.contextPath}')">
									  <input type="button" value="Gen Stock Onhand" class="newPosBtn">
									</a> --%>
								<%}} %>
								<%if("onhandLotus".equalsIgnoreCase(request.getParameter("page")) && User.ADMIN.equals(user.getRole().getKey())) {%>
									<%-- <a href="javascript:genMonthEnd('${pageContext.request.contextPath}','Lotus')">
									  <input type="button" value="Gen MonthEnd" class="newPosBtn">
									</a> --%>
								<%} %>
							</td>
						</tr>
					</table>
			</div>
					<%-- Page:${summaryForm.page} --%>
				<!-- ****** RESULT ***************************************************************** -->
                   <c:choose>
                       <c:when test="${summaryForm.page == 'ReportStockWacoalLotus'}">
                          <jsp:include page="subreports/subReportStockWacoalLotus.jsp" /> 
				      </c:when> 
                      <c:when test="${summaryForm.page == 'onhandBigCSP'}">
                          <jsp:include page="subreports/subReportOnhandBigCSP.jsp" /> 
				      </c:when>    
				      <c:when test="${summaryForm.page == 'onhandLotus'}">
                           <jsp:include page="subreports/subReportOnhandLotus.jsp" /> 
                      </c:when>
                       <c:when test="${summaryForm.page == 'onhandTops'}">
                           <jsp:include page="subreports/subReportOnhandTops.jsp" /> 
                      </c:when>
                       <c:when test="${summaryForm.page == 'onhandAsOfRobinson'}">
                           <jsp:include page="subreports/subReportOnhandAsOfRobinson.jsp" /> 
                      </c:when>
                       <c:when test="${summaryForm.page == 'onhandAsOf_Robinson'}">
                           <jsp:include page="subreports/subReportOnhandAsOf_Robinson.jsp" /> 
                      </c:when>
                      <c:when test="${summaryForm.page == 'sizeColorLotus'}">
                        <jsp:include page="subreports/subReportSizeColorLotus.jsp" /> 
                      </c:when>
                      <c:when test="${summaryForm.page == 'sizeColorBigC'}">
                        <jsp:include page="subreports/subReportSizeColorBigC.jsp" /> 
                      </c:when>
                       <c:when test="${summaryForm.page == 'monthEndLotus'}">
                        <jsp:include page="subreports/subReportMonthEndLotus.jsp" /> 
	                   </c:when>
	                   <c:when test="${summaryForm.page == 'onhandMTT'}">
	                       <jsp:include page="subreports/subReportOnhandMTT.jsp" /> 
	                   </c:when>
	                    <c:when test="${summaryForm.page == 'onhandMTTDetail'}">
	                       <jsp:include page="subreports/subReportOnhandMTTDetail.jsp" /> 
	                   </c:when>
	                    <c:when test="${summaryForm.page == 'onhandAsOfKing'}">
	                       <jsp:include page="subreports/subReportOnhandAsOfKing.jsp" /> 
	                   </c:when>
	                    <c:when test="${summaryForm.page == 'sizeColorKing'}">
	                       <jsp:include page="subreports/subReportOnhandSizeColorKing.jsp" /> 
	                   </c:when>
	                   <c:when test="${summaryForm.page == 'reportEndDateLotus'}">
	                        <jsp:include page="subreports/subReportEndDateLotus.jsp" /> 
	                   </c:when>
	                   <c:when test="${summaryForm.page == 'lotus'}">
	                        <jsp:include page="subreports/subReportSalesLotus.jsp" /> 
	                   </c:when>
	                   <c:when test="${summaryForm.page == 'BigC'}">
	                        <jsp:include page="subreports/subReportSalesBigC.jsp" /> 
	                   </c:when>
	                     <c:when test="${summaryForm.page == 'BigC_TEMP'}">
	                        <jsp:include page="subreports/subReportSalesBigC.jsp" /> 
	                   </c:when>
	                   <c:when test="${summaryForm.page == 'tops'}">
	                        <jsp:include page="subreports/subReportSalesTops.jsp" /> 
	                   </c:when>
	                   <c:when test="${summaryForm.page == 'king'}">
	                        <jsp:include page="subreports/subReportSalesKing.jsp" /> 
	                   </c:when>
	                   <c:when test="${summaryForm.page == 'onhandBigC'}">
	                         <jsp:include page="subreports/subReportOnhandAsOf_BigC.jsp" /> 
	                   </c:when>
	                    <c:when test="${summaryForm.page == 'openBillRobinsonReport'}">
	                         <jsp:include page="subreports/subOpenBillRobinsonReport.jsp" /> 
	                   </c:when>
	                   <c:when test="${summaryForm.page == 'onhandBigCOracle'}">
	                         <jsp:include page="subreports/subReportOnhandBigCOracle.jsp" /> 
	                   </c:when>
				      <c:otherwise>
				         <!-- ALL SUB Report By old code -->
				         <jsp:include page="subreports/subReportAll.jsp" /> 
				      </c:otherwise>
				   </c:choose>
                    
                    <!-- ****** RESULT ***************************************************************** -->
                    
					<jsp:include page="../searchCriteria.jsp"></jsp:include>
					
					<!-- hidden field -->
					<input type="hidden" name="page" value="<%=request.getParameter("page") %>"/>
					<input type="hidden" name="storeType" id="storeType" value="<%=storeType%>"/>
					<input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
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