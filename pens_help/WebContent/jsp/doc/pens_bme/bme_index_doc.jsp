<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
int no =0,subNo=0;
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620">
<title>PENSBME DOCUMENT INDEX</title>
</head>
<body>
<b>PENSBME Main Document Index</b>
<table border="1">
<tr>
 <th>Menu</th>
 <th>Sub Menu 1</th>
 <th>Sub Menu 2</th>
 <th>Link doc </th>
</tr>
<!-- ********** Stock Onhand B'ME ************************-->
<tr><%no++; %>
 <td><b><%=no %>) Stock Onhand B'ME</b></td> 
 <td><%subNo++; %><%=no %>.<%=subNo %>) Load ข้อมูล B'me Master</td>
 <td>-</td>
 <td><a href="/pens_help/jsp/doc/pens_bme/ImportBMEMaster_DOC.jsp">Link Doc</a></td>
</tr>
<!-- ********** WACOAL ***********************************-->

<!-- ********** SHOP *************************************-->

<!-- ********** Report ***********************************-->

<!-- ********** Order ************************************-->

<!-- ********** Interfaces *******************************-->

<!-- ********** Transaction ******************************-->
<tr><%no++; %>
 <td><b><%=no %>) Transaction</b></td>
 <td><%subNo++; %><%=no %>.<%=subNo %>) Request Job เพื่อทำ Auto-CN</td>
 <td>-</td>
 <td><a href="/pens_help/jsp/doc/pens_bme/AutoCN_DOC.jsp">Link Doc</a></td>
</tr>
<tr>
 <td></td>
 <td><%subNo++; %><%=no %>.<%=subNo %>) Request Job เพื่อทำ Auto-CN(HIS&HER)</td>
 <td>-</td>
 <td><a href="/pens_help/jsp/doc/pens_bme/AutoCNHisher_DOC.jsp">Link Doc</a></td>
</tr>

<!-- ********** PICK *************************************-->

<!-- ********** MC/PC/SA *********************************-->

<!-- ********** งาน SA ************************************-->

<!-- ********** Document MENU ****************************-->

<!-- ********** Other ************************************-->

<!-- ********** Master Data*******************************-->
</table>

</body>
</html>