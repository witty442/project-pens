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
 <td>	Load Excel ����Ѻ�͹ʵ�͡ PIC- G899 � G07 <br/>
 ����ǹ�����繡��Ŵ��鹵͹��ä��� �ͧ Sales Admin  					
���ͧ�ҡ ���� ����Դ�Թ��Ңͧ Bme ���  �� 2 ��ǹ ��� ��ǹ����Դ�ç�ҹ ��� ��ǹ����Դ�ҡ PIC -G899					
	 - ��ǹ����Դ�ҡ�ç�ҹ --  ��� Auto PO ��������� Order ����Դ�ҡ�ç�ҹ  ��������Ѻ��ҷ�� G07  ���º��������				
	 - ��ǹ����Դ�ҡ PIC-G899  -- �óչ�� �е�ͧ�繡���͹�Թ��ҷ���ԡ ����ҷ�� G07  �蹡ѹ  ���ѧ������ Auto-BillPlan (���е�ͧ�繡���͹ ���� Interorg Transfer �ҡ G08 --> G07 )				
	   ��觻Ѩ�غѹ �ѧ���� manual ����  �ѧ��� ���ǹ��� �������¡�� Load Excel 				
	    ( ��������Ŵ Excel ���� �������ҧ��� Bme �ӡ�� Recheck �ӹǹ�Թ��ҷ���ԡ�ҡ PIC �㹵�Ǵ��� �����Ҩҡ Request ��觺ҧ���Ҩ�������� Request ����������͵�ͧ�礤����١��ͧ				
	      ��� � ��������ա  �ѧ��� �� Excel ��ػ���������� Load ��Ҩ����ǡ��� )				
 
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
		  <th width="5%">�ѹ�����͡���</th>
		  <th width="5%">��ҧ�ԧ��͹</th>
		  <th width="5%">From Org</th>
		  <th width="5%">From SubInv</th>
		  <th width="5%">To Org</th>
		  <th width="5%">To SubInv</th>
		  <th width="5%">Item</th>
		  <th width="5%">Qty</th>
	    </tr>
	
		<tr>
		   <td>14/01/2020</td>
		   <td>��͹ 502/25099</td>
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