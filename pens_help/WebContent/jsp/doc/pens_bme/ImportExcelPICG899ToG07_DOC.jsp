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
 <td>	Load Excel สำหรับโอนสต๊อก PIC- G899 ไป G07 <br/>
 เป็นส่วนที่จะเป็นการลดขั้นตอนการคีย์ ของ Sales Admin  					
เนื่องจาก ปกติ การเปิดสินค้าของ Bme นั้น  มี 2 ส่วน คือ ส่วนที่เปิดโรงงาน และ ส่วนที่เปิดจาก PIC -G899					
	 - ส่วนที่เปิดจากโรงงาน --  ได้ทำ Auto PO ตามข้อมูล Order ที่เปิดจากโรงงาน  แล้วให้รับเข้าที่ G07  เรียบร้อยแล้ว				
	 - ส่วนที่เปิดจาก PIC-G899  -- กรณีนี้ จะต้องเป็นการโอนสินค้าที่เบิก เข้ามาที่ G07  เช่นกัน  แต่ยังไม่ได้ทำ Auto-BillPlan (เพราะต้องเป็นการโอน โดยใช้ Interorg Transfer จาก G08 --> G07 )				
	   ซึ่งปัจจุบัน ยังคีย์ manual อยู่  ดังนั้น ในส่วนนี้ จะให้ทำโดยการ Load Excel 				
	    ( ที่ให้โหลด Excel เพราะ เผื่อให้ทางทีม Bme ทำการ Recheck จำนวนสินค้าที่เบิกจาก PIC ไปในตัวด้วย ถ้าไปหาจาก Request ซึ่งบางทีอาจจะมีหลาย Request และโปรแกรมก้อต้องเช็คความถูกต้อง				
	      อื่น ๆ เพิ่มเติมอีก  ดังนั้น ใช้ Excel สรุปข้อมูลแล้ว Load น่าจะเร็วกว่า )				
 
 </td>
 </tr>
 <tr>
   <td align="center"><b>DEV</b></td>
</tr>
 <tr>
  <td>
  1.import to DATA TABLE PENSBI.BME_INV_EXCEL_BILLT_TEMP <br/>
  2.Sum to TABLE apps.XXPENS_INV_EXCEL_BILLT_MST , apps.XXPENS_INV_EXCEL_BILLT_DT
 </td>
</tr>
 <tr>
   <td align="center"><b>Format Excel File</b></td>
</tr>
 <tr>
   <td align="center">
      <table align="center" border="1" cellpadding="1" cellspacing="1">
	     <tr>
		  <th width="5%">วันที่ทำเอกสาร</th>
		  <th width="5%">อ้างอิงใบโอน</th>
		  <th width="5%">From Org</th>
		  <th width="5%">From SubInv</th>
		  <th width="5%">To Org</th>
		  <th width="5%">To SubInv</th>
		  <th width="5%">Item</th>
		  <th width="5%">Qty</th>
	    </tr>
	
		<tr>
		   <td>14/01/2020</td>
		   <td>ใบโอน 502/25099</td>
		   <td>G08</td>
		   <td>G899</td>
		   <td>G07</td>
		   <td>G073</td>
		   <td>880266</td>
		   <td>10</td>
		</tr>
		</table>
   </td>
</tr>
</table>
</body>
</html>