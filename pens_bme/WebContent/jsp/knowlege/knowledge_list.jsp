<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620">
<title>Knowledge</title>
<script type="text/javascript">
 function showDiv(id){
	 //alert(id);
	 document.getElementById(id).style.display = "block";
 }
</script>
</head>
<body>
<table align="center" border="1" cellspacing="3" width="100%">
   <tr><td>
   <font size="3"> <b>Knowledge Sample </b></font>
   </td></tr>
  <tr><td>
     <a href="${pageContext.request.contextPath}/jsp/mttAction.do?do=prepare2&action=new">การแบ่ง Page Sample(บันทึกและค้นหาข้อมูลขาย Sale-Out)</a>
  </td></tr>
  <tr><td>
     <a href="${pageContext.request.contextPath}/jsp/autoCNAction.do?do=prepare2&action=new">add row,delete row table Sample(Request Job เพื่อทำ Auto-CN(LOTUS)</a>
  </td></tr>
   <tr><td>
      <a href="${pageContext.request.contextPath}/jsp/popupsearch/popupSearch.jsp">Popup search all </a>
   </td></tr>
   <tr><td>
      <a href="${pageContext.request.contextPath}/jsp/toolManageAction.do?do=prepareSearch&action=new&pageName=ToolManageReport">Call Popup Search Sample(	รายงานสรุปยอดอุปกรณ์และของพรีเมี่ยม) </a>
   </td></tr>
   <tr><td>
      <a href="${pageContext.request.contextPath}/jsp/confFinishAction.do?do=prepare2&action=new">
      Control Save Lock Screen </a>
   </td></tr>
   <tr><td>
      <a href="${pageContext.request.contextPath}/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName=ImportOrderToOracleFromExcel">
      Batch Task Sample(Load Order Excel to ORACLE)</a>
   </td></tr>
    <tr><td>
      <b> Struts autoComplete=off Text Field-> </b>
     <textarea rows="3" cols="200">
        <html_X:text property="order.creditCardExpireDate" styleClass="disableText \" autoComplete=\"off"/>
     </textarea>
    </td></tr>	
    <tr><td>
      <a href="${pageContext.request.contextPath}/jsp/adjustStockSAAction.do?do=prepare2&action=new">
      <b>Validate Data Table (Javascript>)</b></a>
   </td></tr>
    <tr><td>
      <a href="${pageContext.request.contextPath}/jsp/jobAction.do?do=prepare2&action=new">
      <b> Call Ajax (Javascript>)</b></a>
   </td></tr>
    <tr><td>
           <b>  การ Export to Excel ปัญหาภาษาไทย  add tag <.. meta charset='utf-8'..></b>
   </td></tr>
   <tr><td>
        <b>การ Import pic หลายรูปพร้อมกัน  ProdShowServlet path->   /pens/jsp/prodshow/prodshow.jsp</b>
   </td></tr>
   <tr><td>
        <b> Calendar Popup (support thai Calendar) <a href="javascript:showDiv('div_Calendar');">Show Detail</a></b>
         <div style="display: none;" id="div_Calendar">
              <textarea rows="15" cols="200">
         <!-- Calendar -->
			<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/calendar/jquery.calendars.picker.css" type="text/css" />
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.10.0.js"></script> 
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.plugin.js"></script> 
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.plus.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.picker.js"></script> 
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.thai.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.thai-th.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.picker-th.js"></script>
		    
		    //$('#expireDate1_1').calendarsPicker({calendar: $.calendars.instance('thai','th')});
		    </textarea>
		</div>	 
   </td></tr>
  </table>
</body>
</html>