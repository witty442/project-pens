<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.SAReportBean"%>
<%@page import="com.isecinc.pens.dao.SAEmpDAO"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="saReportForm" class="com.isecinc.pens.web.sa.SAReportForm" scope="session" /> 

<%
User user = (User) request.getSession().getAttribute("user");
String pageN = Utils.isNull(request.getParameter("page"));


%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
span.pagebanner {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	margin-top: 10px;
	display: block;
	border-bottom: none;
	font-size: 15px;
}
span.pagelinks {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	display: block;
	border-top: none;
	margin-bottom: -1px;
	font-size: 15px;
}

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript">
function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('payDate'));
	 new Epoch('epoch_popup', 'th', document.getElementById('asOfDate'));
	 <%if(pageN.equals("saDamageReport")){ %>
		 new Epoch('epoch_popup', 'th', document.getElementById('payDateFrom'));
		 new Epoch('epoch_popup', 'th', document.getElementById('payDateTo'));
		 new Epoch('epoch_popup', 'th', document.getElementById('countStockDateFrom'));
		 new Epoch('epoch_popup', 'th', document.getElementById('countStockDateTo'));
		 new Epoch('epoch_popup', 'th', document.getElementById('invoiceDateFrom'));
		 new Epoch('epoch_popup', 'th', document.getElementById('invoiceDateTo'));
	 <%}%>
}
function clearForm(path){
	var form = document.saReportForm;
	form.action = path + "/jsp/saReportAction.do?do=clear";
	form.submit();
	return true;
}
function search(path){
	var form = document.saReportForm;
	<%if(pageN.equals("saStatementReport")){ %>
		if(form.asOfDate.value == ""){
			alert("กรุณา ระบุวันที่");
			form.asOfDate.focus();
			return false;
		}
  <%}else if(pageN.equals("saDamageReport")){ %>
		if(form.type.value == ""){
			alert("กรุณาเลือก Type");
			form.type.focus();
			return false;
		}
		if(form.summaryType.value == ""){
			alert("กรุณา เลือกแสดงแบบ");
			form.summaryType.focus();
			return false;
		}
   <%}%>
   
	form.action = path + "/jsp/saReportAction.do?do=search";
	form.submit();
	return true;
}
function exportToExcel(path,empId){
	var form = document.saReportForm;
	form.action = path + "/jsp/saReportAction.do?do=exportToExcel&empId="+empId;
	form.submit();
	return true;
}

function exportToExcelAll(path){
	var form = document.saReportForm;
	<%if(pageN.equals("saStatementReport")){ %>
		if(form.asOfDate.value == ""){
			alert("กรุณา ระบุวันที่");
			form.asOfDate.focus();
			return false;
		}
	<%}%>
	form.action = path + "/jsp/saReportAction.do?do=exportToExcelAll";
	form.submit();
	return true;
}

/********** Gen Head Excel**********************/
function genHeadTable(){
	  var colspan = 10;
	  var form = document.saReportForm;
	  if(form.summaryType.value=="Detail"){
		  colspan = 17;
	  }
	  var  headerTable ="";
	  //headerTable ="\n <table border='2px'>";
	  headerTable +="\n <tr>";
	  if(form.summaryType.value =="Detail2"){
	     headerTable +="\n   <td colspan="+colspan+"> <b>รายงานการจ่ายค่าเฝ้าตู้และการหักค่าความเสียหาย ( Detail-แสดงหักเงินเดือน )</b></td>";
	  }else{
		  headerTable +="\n   <td colspan="+colspan+"> <b>รายงานการจ่ายค่าเฝ้าตู้และการหักค่าความเสียหาย ( "+form.summaryType.value+" )</b></td>";  
	  }
	  headerTable +="\n </tr>";
	  if(form.type.value != '' || form.groupStore.value != ''){
		  headerTable +="\n <tr>";
		  headerTable +="\n   <td colspan="+colspan+">"+(form.type.value!=''?"Type : "+form.type.value:"")+"  "+(form.groupStore.value!=''?"Group Store : "+form.groupStore.value:"")+"</td>";
		  headerTable +="\n </tr>";
	  }
	  if(form.empId.value != '' || form.name.value != ''){
		  headerTable +="\n <tr>";
		  headerTable +="\n   <td colspan="+colspan+">"+(form.empId.value!=''?"Employee ID : "+form.empId.value:"")+"  "+(form.name.value!=''?"Name : "+form.name.value+"-"+form.surname.value:"")+"</td>";
		  headerTable +="\n </tr>";
	  }
	  if(form.payDateFrom.value != '' || form.payDateTo.value != ''){
		  headerTable +="\n <tr>";
		  headerTable +="\n   <td colspan="+colspan+">"+(form.payDateFrom.value!=''?"จากวันที่ส่งเงิน : "+form.payDateFrom.value:"")+"  "+(form.payDateTo.value!=''?"ถึงวันที่ส่งเงิน : "+form.payDateTo.value:"")+"</td>";
		  headerTable +="\n </tr>";
	  }
	  if(form.countStockDateFrom.value != '' || form.countStockDateFrom.value != ''){
		  headerTable +="\n <tr>";
		  headerTable +="\n   <td colspan="+colspan+">"+(form.countStockDateFrom.value!=''?"จากวันที่ตรวจนับ : "+form.countStockDateFrom.value:"")+"  "+(form.countStockDateTo.value!=''?" ถึงวันที่ตรวจนับ : "+form.countStockDateTo.value:"")+"</td>";
		  headerTable +="\n </tr>";
	  }
	  if(form.invoiceDateFrom.value != '' || form.invoiceDateFrom.value != ''){
		  headerTable +="\n <tr>";
		  headerTable +="\n   <td colspan="+colspan+">"+(form.invoiceDateFrom.value!=''?"จากวันที่ Invoice : "+form.invoiceDateFrom.value:"")+"  "+(form.invoiceDateTo.value!=''?" ถึงวันที่ Invoice : "+form.invoiceDateTo.value:"")+"</td>";
		  headerTable +="\n </tr>";
	  }
	  var currentdate = new Date(); 
	  var datetime = "" + currentdate.getDate() + "/"
	                  + (currentdate.getMonth()+1)  + "/" 
	                  + (currentdate.getFullYear()+543) + " "  
	                  + currentdate.getHours() + ":"  
	                  + currentdate.getMinutes() + ":" 
	                  + currentdate.getSeconds();
	  headerTable +="\n <tr>";
	  headerTable +="\n   <td colspan="+colspan+">Run report on :"+datetime+"</td>";
	  headerTable +="\n </tr>";
	  headerTable +="\n <tr>";
	  headerTable +="\n   <td colspan="+colspan+"></td>";
	  headerTable +="\n </tr>";
	  //headerTable +="\n </table>";
	  
	// alert(headerTable);
	 return headerTable;
}

var tableToExcel = (function () {
    var uri = 'data:application/vnd.ms-excel;base64,',
        template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40">'
         + '<head>'
         + '<!--[if gte mso 9]><xml>'
         + '<x:ExcelWorkbook>'
         + '<x:ExcelWorksheets><x:ExcelWorksheet>'
         + '<x:Name>{worksheet}</x:Name>'
         + '<x:WorksheetOptions><x:DisplayGridlines></x:DisplayGridlines></x:WorksheetOptions>'
         + '</x:ExcelWorksheet></x:ExcelWorksheets>'
         + '</x:ExcelWorkbook>'
         + '</xml><![endif]-->'
         + '</head>'
         + '<body>'
         + '<table border=1>{table}</table>'
         + '</body>'
         + '</html>',
        base64 = function (s) {
            return window.btoa(unescape(encodeURIComponent(s)))
        }, format = function (s, c) {
            return s.replace(/{(\w+)}/g, function (m, p) {
                return c[p];
            })
        }
    return function (table, name, filename,headTable) {
        if (!table.nodeType) table = document.getElementById(table)
      //  alert(table.innerHTML);
        var ctx = {
            worksheet: name || 'Worksheet',
            table:  headTable+ table.innerHTML
        }

        document.getElementById("dlink").href = uri + base64(format(template, ctx));
        document.getElementById("dlink").download = filename;
        document.getElementById("dlink").traget = "_blank";
        document.getElementById("dlink").click();
    }
})();

function exportToExcelByJavascript(){
    var name = "data";
    tableToExcel('tblProduct', 'Sheet 1', name+'.xls',genHeadTable());
    //setTimeout("window.location.reload()",0.0000001);
}
/*****************************************************/

</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../headerMC.jsp"/></td>
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
	       <%if(pageN.equals("saStatementReport")){ %>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="saStatementReport"/>
				</jsp:include>
		    <%}else if(pageN.equals("saDeptReport")){ %>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="saDeptReport"/>
				</jsp:include>
			<%}else if(pageN.equals("saDamageReport")){ %>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="saDamageReport"/>
				</jsp:include>
			<% }else{%>
				<jsp:include page="../program.jsp">
					<jsp:param name="function" value="saOrisoftReport"/>
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
						<html:form action="/jsp/saReportAction">
						<jsp:include page="../error.jsp"/>
						
					 <div align="center">
                        <%if(pageN.equals("saStatementReport")){ %>
                       
							      <table align="center" border="0" cellpadding="3" cellspacing="0" >
									<tr>
				                         <td> Employee ID <font color="red"></font></td>
										<td>		
											 <html:text property="bean.empId" styleId="empId" size="20"/>
										</td>
									   <td>Name<font color="red"></font></td>
										<td>		
					                       <html:text property="bean.name" styleId="name" size="20"/>
					                       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										    Surname
										      <html:text property="bean.surname" styleId="surname" size="20"/>
										</td>
									</tr>
									<tr>
									    <td  align="right">Region<font color="red"></font></td>    
										<td colspan="2">
										     <html:select property="bean.region" styleId="region">
												<html:options collection="empRegionList" property="code" labelProperty="desc"/>
										    </html:select>
										</td>
										<td>
										Group Store
										     <html:select property="bean.groupStore" styleId="groupStore">
												<html:options collection="groupStoreList" property="code" labelProperty="desc"/>
										    </html:select>
										    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										    Branch <html:text property="bean.branch" styleId="branch" size="20"/>
										</td>
									</tr>
									<tr>
									    <td  align="right"> ณ วันที่ <font color="red">*</font> </td>    
										<td colspan="2">
											<html:text property="bean.asOfDate" styleId="asOfDate" size="20"/> 
										  <%--    <html:select property="bean.year" styleId="year">
												<html:options collection="yearList" property="code" labelProperty="desc"/>
										    </html:select> --%>
										</td>
										<td>
										<%-- ณ เดือน
										     <html:select property="bean.month" styleId="month">
												<html:options collection="monthList" property="code" labelProperty="desc"/>
										    </html:select> --%>
										</td>
									</tr>
							      </table>
						    <%}else if(pageN.equals("saDeptReport")){ %>
                       
							      <table align="center" border="0" cellpadding="3" cellspacing="0" >
							      <tr>
									    <td  align="right">Group Store<font color="red"></font></td>    
										<td colspan="2">
										     <html:select property="bean.groupStore" styleId="groupStore">
												<html:options collection="groupStoreList" property="code" labelProperty="desc"/>
										    </html:select>
										</td>
										<td>
										ประเภท 
										    <html:select property="bean.empType" styleId="empType">
												<html:options collection="empTypeList" property="code" labelProperty="desc"/>
										    </html:select>
										</td>
									</tr>
									<tr>
				                        <td> Employee ID <font color="red"></font></td>
										<td>		
											 <html:text property="bean.empId" styleId="empId" size="20"/>
										</td>
									   <td>รหัสลูกหนี้ใน Oracle</td>
										<td>		
					                       <html:text property="bean.oracleRefId" styleId="oracleRefId" size="20"/>
										</td>
									</tr>
									<tr>
				                        <td> Name <font color="red"></font></td>
										<td>		
											 <html:text property="bean.name" styleId="name" size="20"/>
										</td>
									   <td>  Surname<font color="red"></font></td>
										<td>	
										     <html:text property="bean.surname" styleId="surname" size="20"/>
										</td>
									</tr>
									<tr>
				                        <td> แสดงตาม <font color="red"></font></td>
										<td>		
											 <html:select property="bean.summaryType" styleId="summaryType">
												<html:option value="Detail">Detail</html:option>
												<html:option value="Summary">Summary</html:option>
										    </html:select>
										</td>
									   <td></td>
									   <td></td>
									</tr>
							      </table>
						       <%}else if(pageN.equals("saDamageReport")){ %>
                       
							      <table align="center" border="0" cellpadding="3" cellspacing="0" >
							      <tr>
									    <td  align="right">Type<font color="red">*</font></td>    
										<td>
										    <html:select property="bean.type" styleId="type">
												<html:options collection="typeList" property="code" labelProperty="desc"/>
										    </html:select>
										</td>
										<td>Group Store</td>
										<td>
										     <html:select property="bean.groupStore" styleId="groupStore">
												<html:options collection="groupStoreList" property="code" labelProperty="desc"/>
										    </html:select>
										</td>
									</tr>
									<tr>
				                        <td> Employee ID <font color="red"></font></td>
										<td>		
											 <html:text property="bean.empId" styleId="empId" size="20"/>
										</td>
									   <td>Name</td>
										<td>		
					                       <html:text property="bean.name" styleId="name" size="20"/>
					                       Surname   <html:text property="bean.surname" styleId="surname" size="20"/>
										</td>
									</tr>
									<tr>
				                        <td> จากวันที่ส่งเงิน <font color="red"></font></td>
										<td>		
											 <html:text property="bean.payDateFrom" styleId="payDateFrom" readonly="true" size="20"/>
										</td>
									   <td> ถึงวันที่ส่งเงิน</td>
										<td>		
					                       <html:text property="bean.payDateTo" styleId="payDateTo" readonly="true" size="20"/>
										</td>
									</tr>
									<tr>
				                        <td> จากวันที่ตรวจนับ <font color="red"></font></td>
										<td>		
											 <html:text property="bean.countStockDateFrom" styleId="countStockDateFrom" readonly="true" size="20"/>
										</td>
									   <td> ถึงวันที่ตรวจนับ</td>
										<td>		
					                       <html:text property="bean.countStockDateTo" styleId="countStockDateTo" readonly="true" size="20"/>
										</td>
									</tr>
									<tr>
				                        <td> จากวันที่ Invoice <font color="red"></font></td>
										<td>		
											 <html:text property="bean.invoiceDateFrom" styleId="invoiceDateFrom" readonly="true" size="20"/>
										</td>
									   <td> ถึงวันที่ Invoice</td>
										<td>		
					                       <html:text property="bean.invoiceDateTo" styleId="invoiceDateTo" readonly="true" size="20"/>
										</td>
									</tr>
									<tr>
				                        <td> เลือกแสดงแบบ <font color="red">*</font></td>
										<td>		
											 <html:select property="bean.summaryType" styleId="summaryType">
												<html:option value="Detail">Detail</html:option>
												<html:option value="Detail2">Detail-แสดงหักเงินเดือน</html:option>
												<html:option value="Summary">Summary</html:option>
										    </html:select>
										</td>
									   <td></td>
									   <td></td>
									</tr>
							      </table>
						     <%}else if(pageN.equals("saOrisoftReport")){ %>
						      
						      <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
								    <td  align="right"> <!-- ปี    --><font color="red"></font></td>    
									<td colspan="2">
									  <%--    <html:select property="bean.year" styleId="year">
											<html:options collection="yearList" property="code" labelProperty="desc"/>
									    </html:select> --%>
									</td>
									<td>
									ณ เดือน <font color="red">*</font>
									     <html:select property="bean.month" styleId="month">
											<html:options collection="monthList" property="code" labelProperty="desc"/>
									    </html:select>
									  
									</td>
								</tr>
						      </table>
						   <%} %>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									 <%if(pageN.equals("saDamageReport")){ %>
										    <a href="javascript:exportToExcelByJavascript()">
											  <input type="button" value=" Export To Excel " class="newPosBtnLong"> 
											</a>
										<%}else{ %>
											 <a href="javascript:exportToExcelAll('${pageContext.request.contextPath}')">
											  <input type="button" value=" Export To Excel " class="newPosBtnLong"> 
											</a>
										<%} %>
										<a href="javascript:search('${pageContext.request.contextPath}')">
										  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
										</a>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>
					  
					   <%if(pageN.equals("saStatementReport")){ %>
						    <jsp:include page="saStatementReport_sub.jsp" />  
					   <%}else if(pageN.equals("saOrisoftReport")){ %>
						    <jsp:include page="saOrisoftReport_sub.jsp" />  
		               <%}else if(pageN.equals("saDeptReport")){ %>
						    <jsp:include page="saDeptReport_sub.jsp" />  
		                <%}else if(pageN.equals("saDamageReport")){ %>
						    <jsp:include page="saDamageReport_sub.jsp" />  
		               <%} %>
					
					
					<!-- hidden field -->
					<input type="hidden" name="page" value="<%=pageN %>"/>
					<a id="dlink" style="display:none;"></a>
                   <!--  <div id="name">filename</div> -->

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