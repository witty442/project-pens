<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.web.stock.StockCallCardCreditReport"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.isecinc.pens.web.stock.StockBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String recordType = Utils.isNull(request.getParameter("recordType"));
String startDate = Utils.isNull(request.getParameter("startDate"));
String salesrepCode = Utils.isNull(request.getParameter("salesrepCode"));
String customerCode = Utils.isNull(request.getParameter("customerCode"));
String brand = Utils.isNull(request.getParameter("brand"));
String requestDate = Utils.isNull(request.getParameter("requestDate"));
String itemCode = Utils.isNull(request.getParameter("itemCode"));

StockBean o = new StockBean();
o.setStartDate(startDate);
o.setSalesrepCode(salesrepCode);
o.setCustomerCode(customerCode);
o.setBrand(brand);
o.setRequestDate(requestDate);
o.setItemCode(itemCode);

User user = (User) request.getSession().getAttribute("user");
List<StockBean> dataList = StockCallCardCreditReport.getDetailList(recordType, o, user);

%>
<html>
<head>
<title>Stock Detail</title>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<form name="brandSaveZonePopupForm">
<%
if(dataList != null && dataList.size() >0){
%>
<table align="center" border="1" cellpadding="1" cellspacing="0" width="100%">
         <tr>
			<th colspan="4">รายละเอียด</th>
		</tr>
    <%if(recordType.equalsIgnoreCase("S")){ %>
	     <tr>
			<th align="center" colspan="2">ยอดตรวจนับ</th>
			<th align="center" rowspan="2">วันที่หมดอายุ</th>
		</tr>
		 <tr>
			<th align="center">หีบ</th>
			<th align="center">เศษ</th>
		</tr>
	<%}else{ %>
	    <tr>
			<th align="center">เลขที่ขาย (InvoiceNo)</th>
			<th align="center">วันที่ขาย (Invoice Date)</th>
			<th align="center">จำนวน</th>
		</tr>
	<%} %>
	<%
  
		for(int i=0;i<dataList.size();i++){
			StockBean item = dataList.get(i);
    %>
        <%if(recordType.equalsIgnoreCase("S")){ %>
			<tr>
				<td align="center"><%=item.getPriQty() %></td>
				<td align="center"><%=item.getSecQty() %></td>
				<td align="center"><%=item.getExpireDate() %></td>
			</tr>
	    <%}else{ %>
	        <tr>
				<td align="center"><%=item.getInvoiceNo() %></td>
				<td align="center"><%=item.getInvoiceDate() %></td>
				<td align="center"><%=item.getPriQty() %></td>
			</tr>
	    <%} %>
   <%  }%>
</table>
 <% }else{ %>
    <div align="center"><font color="red"><b>ไม่พบข้อมูล</b></font></div>
 <% } %>
<!-- RESULT -->
<br/>
<div align="center"><input type="button" name="close" value="  ปิดหน้าจอนี้   " onclick="window.close()"/></div>
</form>
</body>
</html>