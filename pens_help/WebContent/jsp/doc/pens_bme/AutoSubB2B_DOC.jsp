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
 <td>Request Auto-transfer stock ระหว่างสาขา					
					
	เดิมเราจะมีการทำ Auto Subtransfer Stock  อยู่แล้ว แต่เป็นการ Transfer จากการซื้อสินค้าจากโรงงาน  และ จากการโอนจากคลัง PIC (G899)				
					
	แต่กรณีนี้  คือ เป็นการ Transfer ระหว่างสาขากันเอง  เช่น จาก BigC สาขาปิ่นเกล้า ไป BigC สาขาบางนา    เนื่องจากสาเหตุใด ๆ ก้อตาม เช่น ไม่มี SA รับสินค้า หรือเหตุผลอื่น ๆ 				
	และไม่ต้องการนำสินค้ากลับเข้าคลัง PIC แล้วต้องเริ่ม Process ใหม่ เพราะจะเปลืองค่าขนส่งไปมา  				
	ดังนั้น ทุกวันนี้ จะใช้ Process เดิม แล้วเอามาให้ทาง Sales admin คีย์ เป็น BAS Transfer				
								
    Role	SALE , PICKADMIN				

 </td>
 </tr>
 <tr>
	 <td>
	  Table: PENSBI.PENSBME_AUTO_SUBTRANS_B2B ,PENSBI.PENSBME_AUTO_SUBTRANS_B2B_ITEM
	 </td>
</tr>
<tr>
	 <td>
	  1).เปิดจากโรงงาน: PENSBI.PENSBME_ORDER
	 </td>
</tr>
<tr>
	 <td>
	  2).เบิกจาก PIC: PENSBI.PENSBME_STOCK_ISSUE, PENSBI.PENSBME_PICK_STOCK
	 </td>
</tr>
</table>
</body>
</html>