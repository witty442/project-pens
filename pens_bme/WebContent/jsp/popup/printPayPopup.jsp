<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="util.printer.PrinterBean"%>
<%@page import="util.printer.PrinterUtils"%>
<%@page import="com.isecinc.pens.inf.helper.EnvProperties"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%
try{
User user = (User) session.getAttribute("user");
EnvProperties env = EnvProperties.getInstance();

//Check Printer Deafualt By User is Online
boolean printerByUserDefaultIsOnline = PrinterUtils.isPrinterIsOnline(user.getOrgPrinterName());
System.out.println("printerByUserDefaultIsOnline["+printerByUserDefaultIsOnline+"]");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript">
function loadMe(_path){	
  <%
  String reportName = request.getParameter("report_name");
  String typeReport = "PayInReport";
  String docNo = Utils.isNull(request.getParameter("docNo"));

  if(printerByUserDefaultIsOnline && "PayInReport".equals(reportName)){ 
		 
		   if(request.getLocalAddr().equals("192.168.202.8") 
			|| request.getLocalName().equals("0.0.0.0")
			){
	  %>
	         var param = "&typeReport=<%=typeReport%>&docNo=<%=docNo%>"
	       //  document.tempForm.action = _path + "/jsp/payAction.do?do=printReport"+param;
	        // document.tempForm.submit();
	        
	         var submitUrl =  _path + "/jsp/payAction.do?do=printReport"+param;
	         $.post(submitUrl, $('#tempForm').serialize());
	      <%}else{ %>
	          var param = "typeReport=<%=typeReport%>&docNo=<%=docNo%>&userName=<%=user.getUserName()%>";
	          //document.tempForm.action = "http://<%=env.getProperty("host.payinreport")%>/printPayInReport?"+param;
		     // document.tempForm.submit();
		     
		     var submitUrl = "http://<%=env.getProperty("host.payinreport")%>/printPayInReport?"+param;
		      $.post(submitUrl, $('#tempForm').serialize());
	      <%} %>
	 <%} %>
	 
	  setTimeout(function(){window.close();},10000);
}

  function printReportByUser(_path){
	  var printerName = $('input[name="printerName"]:checked').val();
	   
	  <%
		 typeReport = "PayInReport";
		 if(request.getLocalAddr().equals("192.168.202.8") 
			|| request.getLocalName().equals("0.0.0.0")
		   ){
	  %>
         var param  = "&typeReport=PayInReport&docNo="+document.getElementsByName("docNo")[0].value;
             param += "&printerName="+printerName;
             
           //alert("1:"+param);
          // document.tempForm.action = _path + "/jsp/payAction.do?do=printReport"+param;
           //document.tempForm.submit();
           
          var submitUrl = document.tempForm.action = _path + "/jsp/payAction.do?do=printReport"+param;
           
           $.post(submitUrl, $('#tempForm').serialize());
           
      <%}else{ %>
          var param  = "typeReport=PayInReport&docNo="+document.getElementsByName("docNo")[0].value;
              param += "&printerName="+printerName;
              
          //alert("2:"+param);
         // document.tempForm.action = "http://<%=env.getProperty("host.payinreport")%>/printPayInReport?"+param;
	      //document.tempForm.submit();
	       var submitUrl = "http://<%=env.getProperty("host.payinreport")%>/printPayInReport?"+param;
	      $.post(submitUrl, $('#tempForm').serialize());
	      
      <%} %>
      
      setTimeout(function(){window.close();},10000);
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
<%if( !printerByUserDefaultIsOnline){ 
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
				<a href="#" onclick="printReportByUser('${pageContext.request.contextPath}')">
				<input type="button" value=" พิมพ์ " class="newNegBtn">
				</a>
				<a href="#" onclick="window.close();">
				<input type="button" value="ยกเลิก" class="newNegBtn">
				</a>
			</td>
		</tr>
	</table>
<%}else{ %>
	<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
		<tr>
			<td align="right">
				
				<a href="#" onclick="window.close();">
				<input type="button" value="ยกเลิก" class="newNegBtn">
				</a>
			</td>
			<td width="20%">&nbsp;</td>
		</tr>
	</table>
<%}%>
</html:form>
<!-- BODY -->
</body>
</html>
<%
}catch(Exception e){
	e.printStackTrace();
}
%>