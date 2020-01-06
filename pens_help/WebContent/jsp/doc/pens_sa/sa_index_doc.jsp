<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
int no =0,subNo=0;
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620">
<title>PENS_SA DOCUMENT INDEX</title>
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
<!-- ********** Credit Sales************************-->
<tr><%no++; %>
 <td><b><%=no %>) Credit Sales</b></td> 
 <td><%subNo++; %><%=no %>.<%=subNo %>) บันทึกสต๊อกสาขาของร้านค้า TT</td>
 <td>-</td>
 <td><a href="/pens_help/jsp/doc/pens_sa/ProjectC_DOC.jsp">Link Doc</a></td>
</tr>

</table>

</body>
</html>