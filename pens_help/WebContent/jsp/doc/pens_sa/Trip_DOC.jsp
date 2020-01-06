<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620">
<title>PENS_SA DOCUMENT INDEX</title>
</head>
<body>

<a href="/pens_help/jsp/doc/pens_bme/sa_index_doc.jsp">PENSBME Main Document Index</a>
<table border="1">
<tr>
 <th>Document</th>
</tr>
<tr>
 <td>การกำหนด Trip ให้เซลส์					
		
 </td>
 </tr>
 <tr>
	 <td>
	  Table: 
	    apps.xxpens_ar_cust_sales_vl  <br/>
		 
		 //update trip master<br/>
		 updateCustTripMasterProc();<br/>
		 { call xxpens_om_trip_pkg.update_trip(?,?,?,?,?) }<br/>
		 <br/>
		 //update customerType<br/>
		 updateCustTypeMasterProc();<br/>
		 { call xxpens_om_trip_pkg.update_cust_class(?,?) }<br/>
		 <br/>
		 //insertCustTripChangeHis <br/>
		 insertCustTripChangeHis();<br/>
		  PENSBI.XXPENS_BI_CUST_TRIP_CHANGE_HIS<br/>
	 </td>
</tr>

</table>
</body>
</html>