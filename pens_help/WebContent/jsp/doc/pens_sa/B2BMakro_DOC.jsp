<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620">
<title>PENS_SA DOCUMENT INDEX</title>
</head>
<body>

<a href="/pens_help/jsp/doc/pens_sa/sa_index_doc.jsp">PENSSA Main Document Index</a>
<table border="1">
<tr>
 <th>Document</th>
</tr>
<tr>
 <td>B2B Makro</td>
 </tr>
 <tr> <td>
B2B Makro	Import Excel to Oracle Temp and Call Procedure and Export
 </td></tr>
 <tr> <td>
   Action	Import EXCEL
</td></tr>
 <tr> <td>
   1)B2B_ITEM :APPS.XXPENS_OM_PUSH_ORDER_ITEM
</td></tr>
 <tr> <td>
    2)SALES_BY_ITEM: APPS.XXPENS_OM_PUSH_ORDER_TEMP
</td></tr>
 <tr> <td>
Export EXCEL
 </td></tr>
 <tr> <td>
1) call apps.xxpens_om_push_order_pkg.b2b();
 </td></tr>
 <tr> <td>
2) Export sheet_data
 </td></tr>
  <tr> <td>
     2.1)sheet DATA : apps.xxpens_om_push_order_vl
 </td></tr>
  <tr> <td>
     2.2)sheet_PO_UPLOAD2: apps.xxpens_om_push_order_v1
 </td></tr>
</table>
</body>
</html>