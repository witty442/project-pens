<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@page import="com.pens.util.*"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%
try{
User user = (User) session.getAttribute("user");
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
  if("PayInReport".equals(reportName) && "".equals(printerSuccess)){ 
 %>
         var param = "&typeReport=<%=typeReport%>&docNo=<%=docNo%>"
     
        // var submitUrl =  _path + "/jsp/payAction.do?do=printReport"+param;
         //$.post(submitUrl, $('#tempForm').serialize());
         
         document.tempForm.action = _path + "/jsp/payWhiteAction.do?do=printReport"+param;
         document.tempForm.submit();
         
 <%} %>
 
   //close window 3 minute 60000(ms)=1(minute)
   setTimeout(function(){window.close();},120000);
   
   var fiveMinutes = 60 * 2,
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
     
      document.tempForm.action = _path + "/jsp/payWhiteAction.do?do=printReport"+param;
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
	<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
		<tr>
			<td align="center"></td>
		</tr>
		<tr>
			<td align="center">
			  <font size="3" color="green">	กำลังพิมพ์เอกสาร กรุณา รอ สักครู่.............</font>
			</td>
		</tr>
		<tr>
			<td align="center">
			 	<a href="#" onclick="window.close();">
				<input type="button" value="ปิดหน้าจอนี้" class="newPosBtnLong">
				</a> 
			</td>
		</tr>
		<tr>
			<td align="center">
				 <div><font size="3" color="red">
				     กำลังจะปิดหน้าจอนี้... <span id="time">2</span> นาที!
				 </font> 
				 </div>
			</td>
		</tr>
	</table>

</html:form>
<!-- BODY -->
</body>
</html>
<%
}catch(Exception e){
	e.printStackTrace();
}
%>