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
 <td>Call Card ร้านค้าเครดิต	</td>
 </tr>
 <tr>
	 <td>
	  Table: <br/>
	   select pri_qty,sec_qty <br/>
	    apps.xxpens_om_check_order_v <br/>
	    
	    union all <br/>
	    
	  	select SUM((INVOICED_QTY+Promotion_QTY)) as qty<br/>
		FROM PENSBI.XXPENS_BI_SALES_ANALYSIS_V M ,<br/>
		PENSBI.XXPENS_BI_MST_ITEM P ,<br/>
		PENSBI.XXPENS_BI_MST_SALESREP S,<br/>
		PENSBI.XXPENS_BI_MST_CUSTOMER C ,<br/>
		PENSBI.XXPENS_BI_MST_SALES_ZONE Z <br/>
	  
	 </td>
</tr>

</table>
</body>
</html>