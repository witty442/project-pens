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
boolean printerByUserDefaultIsOnline = PrinterUtils.isPrinterIsOnline(user.getOrgPrinterName());
System.out.println("printerByUserDefaultIsOnline =["+printerByUserDefaultIsOnline+"]");
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
  String printerSuccess ="";
  if(request.getAttribute("printerSuccess") != null){
     printerSuccess = (String)request.getAttribute("printerSuccess");
  }
 // System.out.println("printerSuccess:"+printerSuccess);
 // System.out.println("LocalName:"+request.getLocalName());
  
  //Case Printer IS Online Printer Auto
  if(printerByUserDefaultIsOnline && "PayInReport".equals(reportName) && "".equals(printerSuccess)){ 
 %>
         var param = "&typeReport=<%=typeReport%>&docNo=<%=docNo%>"
     
        // var submitUrl =  _path + "/jsp/payAction.do?do=printReport"+param;
         //$.post(submitUrl, $('#tempForm').serialize());
         
         document.tempForm.action = _path + "/jsp/payInterHAction.do?do=printReport"+param;
         document.tempForm.submit();
         
 <%} %>
 //after printer close window
 <%if("printerSuccess".equals(printerSuccess)){%>
   setTimeout(function(){window.close();},8000);
 <%}%>
 
   var fiveMinutes = 30 ;
   display = document.querySelector('#time');
   startTimer(fiveMinutes, display);
}
/** Case Printer offline user choose printer **/
  function printReportByUser(_path){
	  var printerName = $('input[name="printerName"]:checked').val();
      var param  = "&typeReport=PayInReport&docNo="+document.getElementsByName("docNo")[0].value;
          param += "&printerName="+printerName;
            
      //var submitUrl = document.tempForm.action = _path + "/jsp/payAction.do?do=printReport"+param;
     // $.post(submitUrl, $('#tempForm').serialize());
     
      document.tempForm.action = _path + "/jsp/payInterHAction.do?do=printReport"+param;
      document.tempForm.submit();
         
     // document.getElementById("btn_print").disabled = true;
      
     // setTimeout(function(){window.close();},9000);
}
  function startTimer(duration, display) {
	    var timer = duration, minutes, seconds;
	    setInterval(function () {
	        minutes = parseInt(timer / 60, 10);
	        seconds = parseInt(timer % 60, 10);

	        minutes = minutes < 10 ? "0" + minutes : minutes;
	        seconds = seconds < 10 ? "0" + seconds : seconds;

	        display.textContent = minutes + ":" + seconds;

	        if (--timer < 0) {
	            timer = duration;
	        }
	    }, 1000);
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
<%if( !printerByUserDefaultIsOnline && "".equals(printerSuccess)){ 
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
		<tr>
			<td align="center">
				 <div><font size="3" color="red">
				     กำลังจะปิดหน้าจอนี้... <span id="time">1</span> นาที!
				 </font> 
				 </div>
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
		<tr>
			<td align="center">
				 <div><font size="3" color="red">
				     กำลังจะปิดหน้าจอนี้... <span id="time">30</span> วินาที!
				 </font> 
				 </div>
			</td>
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