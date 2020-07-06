<%@page import="com.isecinc.pens.web.stock.StockCreditExpireReport"%>
<%@page import="com.isecinc.pens.web.stock.StockBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>Brand Save Zone Day</title>
</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<form name="brandSaveZonePopupForm">

<table align="center" border="1" cellpadding="1" cellspacing="0" width="100%">
     <tr>
		<th align="center">Item Code</th>
		<th align="center">SHELF LIFE DAY</th>
		<th align="center">HALF SHELF LIFE DAY</th>
	</tr>
	<%
    List<StockBean> brandSaveZoneList = StockCreditExpireReport.searchBrandSaveZoneList();
	if(brandSaveZoneList != null){
		for(int i=0;i<brandSaveZoneList.size();i++){
			StockBean item = brandSaveZoneList.get(i);
    %>
		<tr>
			<td align="center"><%=item.getItemCode() %></td>
			<td align="center"><%=item.getShelfLifeDay() %></td>
			<td align="center"><%=item.getHalfShelfLifeDay() %></td>
		</tr>
   <%   }
	} %>
</table>
<!-- RESULT -->

</form>
</body>
</html>