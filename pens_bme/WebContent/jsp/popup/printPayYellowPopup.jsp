<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@page import="com.pens.util.printer.*"%>
<%@page import="com.pens.util.EnvProperties"%>
<%@page import="com.pens.util.*"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%
try{
	User user = (User) session.getAttribute("user");
	EnvProperties env = EnvProperties.getInstance();
	
	//Check Printer Deafualt By User is Online
	boolean printerByUserDefaultIsOnline =true;// PrinterUtils.isPrinterIsOnline(user.getOrgPrinterName());
	System.out.println("printerByUserDefaultIsOnline["+user.getOrgPrinterName()+"] =["+printerByUserDefaultIsOnline+"]");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css" type="text/css">
<script type="text/javascript">
function loadMe(_path){	
  <%
  String reportName = request.getParameter("report_name");
  String typeReport = "PayInReport";
  String docNo = Utils.isNull(request.getParameter("docNo"));
  String printCopy = Utils.isNull(request.getAttribute("printCopy"));
 // System.out.println("LocalName:"+request.getLocalName());
  
 //Case Printer IS Online Printer Auto
 //if(printerByUserDefaultIsOnline && !"printCopy".equals(printCopy) && !"printSuccess".equals(printCopy)){

if(printerByUserDefaultIsOnline && !"printSuccess".equals(printCopy)){ 
%>
     //alert("printMain:<%=printCopy%>");
     var param = "&docNo=<%=docNo%>"
     document.tempForm.action = _path + "/jsp/payYellowAction.do?do=printReport"+param;
     document.tempForm.submit();
<%} %>
 
 <%if("printCopy".equals(printCopy)){%>
   //print copy
   //setTimeout(function(){printReportCopy();},2000);
 <%}%>
 
 //close auto
 setTimeout(function(){window.close();},10000);
}

//printCopy
function printReportCopy(){
  //alert("printCopy");
  var path = document.getElementsByName('path')[0].value;
  var param  = "";
      param += "&docNo="+document.getElementsByName("docNo")[0].value;
      param += "&docType=copy";
   //alert("printReport2");
	   
     document.tempForm.action = path + "/jsp/payYellowAction.do?do=printReport"+param;
     document.tempForm.submit();
} 
</script>
</head>
<body onload="loadMe('${pageContext.request.contextPath}');" topmargin="0" bottommargin="0" leftmargin="100" rightmargin="150" class="popbody">
<html:form action="/jsp/tempAction">
<!-- BODY -->
<input type="hidden" name="load" value="">
<input type="hidden" name="report_name" value="<%=reportName%>">
<input type="hidden" name="typeReport" value="<%=typeReport%>">
<input type="hidden" name="docNo" value="<%=docNo%>">

<!-- BUTTON -->
<%if( !printerByUserDefaultIsOnline ){ 
  List<PrinterBean> listPrinter = PrinterUtils.listPrinterXeroxPayslipIsOnline();
%>
	<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
		<tr>
			<td align="center">
			<b>	Printer[<%=user.getOrgPrinterName() %>] ของคุณไม่สามารถใช้งานได้ กรุณาเลือก Printer ที่สามารถใช้งานได้ จาม List ข้างล่าง </b>
			</td>
		</tr>
		<tr>
			<td align="left">
			<%if(listPrinter !=null && listPrinter.size() >0){ 
			  for(int i=0;i<listPrinter.size();i++){
				  PrinterBean p = listPrinter.get(i);
				 // System.out.println("printerName:"+p.getName());
			%>
				 <input type="radio" name="printerName" id="printerName"
				 value="<%=p.getName()%>"><b><%=p.getName()%>-<%=p.getDesc()%></b><br>
			<%}} %>
			</td>
		</tr>
		<tr>
			<td align="center">
				<input type="button" value=" พิมพ์ " id ="btn_print" class="newNegBtn" onclick="printReportByUser('${pageContext.request.contextPath}')">
				<a href="#" onclick="window.close();">
				<input type="button" value="ยกเลิก" class="newNegBtn">
				</a>
			</td>
		</tr>
	</table>
<%}else{ %>
	<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
		<tr>
			<td align="center"></td>
		</tr>
		<tr>
			<td align="center">
			  <font size="3" color="green">	กำลังพิมพ์เอกสาร กรุณารอกสักครู่.............</font>
			</td>
		</tr>
		<tr>
			<td align="center">
			 	<a href="#" onclick="window.close();">
				<input type="button" value="ปิดหน้าจอนี้" class="newBtn">
				</a> 
			</td>
		</tr>
	</table>
<%}%>
<input type="hidden" name="path" value="${pageContext.request.contextPath}"/>
</html:form>
<!-- BODY -->
</body>
</html>
<%
}catch(Exception e){
	e.printStackTrace();
}
%>