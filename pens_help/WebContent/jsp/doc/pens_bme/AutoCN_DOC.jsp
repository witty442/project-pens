<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620">
<title>PENSBME DOCUMENT INDEX</title>
</head>
<body>

<a href="/pens_help/jsp/doc/pens_bme/bme_index_doc.jsp">PENSBME Main Document Index</a>
<table border="1">
<tr>
 <th>Document</th>
</tr>
<tr>
 <td>Request Job เพื่อทำ Auto-CN
   จากที่เช็คหลักการ คือ สำหรับ King power , Robinson จะมีวิธีการทำในลักษณะเดียวกับของ LOTUS  และมีรูปแบบข้อมูลลักษณะเดียวกัน	
 ( GMS , PT Town  เป็นกลุ่ม Duty free แต่ทำไม่ได้ เพราะทางห้าง จะระบุ Invoice ที่จะให้ทำ CN มาเลย จะมาทำ Auto ไม่ได้ )	
	
	Invoice Type = MT-Invoice-ICC  เหมือนกัน
	Order type   = Order-Modern Trade-ICC  เหมือนกัน
	RMA           =  RMA - Modern Trade-ICC เหมือนกัน
	Sub Inventory  = G071  เหมือนกัน
	Price List   =  หามาจาก Customer เหมือนกัน
	
	การระบุ RTN จากห้าง  จะแจ้งให้ทำในรูปแบบเดียวกับของ LOTUS  เพื่อให้เป็นแนวทางเดียวกัน
 
 </td>
 </tr>
 <tr>
 <td>
  Table: PENSBI.PENSBME_APPROVE_TO_AUTOCN ,PENSBME_APPROVE_TO_AUTOCN_ITEM
 </td>
</tr>
</table>
</body>
</html>