<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620">
<title>Knowledge</title>
</head>
<body>
<table align="center" border="1" cellspacing="3" width="100%">
   <tr><td>
   <font size="3"> <b>Knowledge Sample </b></font>
   </td></tr>
  <tr><td>
     <a href="${pageContext.request.contextPath}/jsp/shopAction.do?do=prepare&action=newsearch&pageName=ShopPromotion">การแบ่ง Page Sample(Master C4 for Shop)</a>
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
       Struts autoComplete=off Text Field->
      <b>" html:text property="order.creditCardExpireDate" styleClass="disableText \" autoComplete=\"off"/> "</b>
    </td></tr>	
    <tr><td>
      <a href="${pageContext.request.contextPath}/jsp/adjustStockSAAction.do?do=prepare2&action=new">
      Validate Data Table (Javascript>)</a>
   </td></tr>
    <tr><td>
      <a href="${pageContext.request.contextPath}/jsp/jobAction.do?do=prepare2&action=new">
     Call Ajax (Javascript>)</a>
   </td></tr>
    <tr><td>
             การ Export to Excel ปัญหาภาษาไทย  add tag <.. meta charset='utf-8'..>
   </td></tr>
  </table>
</body>
</html>