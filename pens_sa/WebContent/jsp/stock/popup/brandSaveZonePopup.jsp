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
		<th align="center">Brand</th>
		<th align="center">Day</th>
	</tr>
	<%
    List<StockBean> brandSaveZoneList = StockCreditExpireReport.searchBrandSaveZoneList();
	if(brandSaveZoneList != null){
		for(int i=0;i<brandSaveZoneList.size();i++){
			StockBean item = brandSaveZoneList.get(i);
    %>
		<tr>
			<td align="center"><%=item.getBrand() %></td>
			<td align="center"><%=item.getBrandSaveZoneDay() %></td>
		</tr>
   <%   }
	} %>
</table>
<!-- RESULT -->

</form>
</body>
</html>