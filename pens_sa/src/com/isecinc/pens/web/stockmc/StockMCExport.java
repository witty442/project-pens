package com.isecinc.pens.web.stockmc;

import java.util.List;

import util.ExcelHeader;

public class StockMCExport {
	
	
	public static StringBuffer genExportStockMCReport(List<StockMCBean> items) throws Exception{
		 StringBuffer h = new StringBuffer("");
		 int no = 0;
		 h.append(ExcelHeader.EXCEL_HEADER);
		 
		 h.append("<table border='1'> \n");
		 h.append("   <tr><td colspan='10'><b>รายงาน  เช็คสินค้าห้าง </b></td></tr> \n");
		 h.append("</table> \n");
		 
		 h.append("<table border='1'> \n");
		 h.append("<tr> \n");
		 h.append("<th rowspan='3'>วันที่ตรวจนับสต๊อก</th>\n");
		 h.append("<th rowspan='3'>ห้าง</th>\n");
		 h.append("<th rowspan='3'>ชื่อห้าง</th>\n");
		 h.append("<th rowspan='3'>สาขา</th>\n");
		 h.append("<th rowspan='3'>ผู้บันทึกตรวจเช็คสต๊อก</th>\n");
		 h.append("<th rowspan='3'>พนักงาน PC</th>\n");
		 h.append("<th rowspan='3'>รหัสสินค้า</th>\n");
		 h.append("<th rowspan='3'>บาร์โค้ด</th>\n");
		 h.append("<th rowspan='3'>รายละเอียดสินค้า</th>\n");
		 h.append("<th rowspan='3'>บรรจุ</th>\n");
		 h.append("<th rowspan='3'>อายุสินค้า</th>\n");
		 h.append("<th rowspan='3'>ราคาปลีก</th>\n");
		 h.append("<th rowspan='3'>ราคาโปรโมชั่น</th>\n");
		 h.append("<th rowspan='3'>ขา</th>\n");
		 h.append("<th colspan='12'>สต๊อกสินค้า</th>\n");
		 h.append("</tr> \n");
		 h.append("<tr> \n");
		 h.append("<th rowspan='2'>ในระบบห้าง</th>\n");
		 h.append("<th rowspan='2'>หลังร้าน</th>\n");
		 h.append("<th rowspan='2'>หน่วยบรรจุ</th>\n");
		 h.append("<th colspan='3'>กลุ่มหมดอายุที่ 1</th>\n");
		 h.append("<th colspan='3'>กลุ่มหมดอายุที่ 2</th>\n");
		 h.append("<th colspan='3'>กลุ่มหมดอายุที่ 3</th>\n");
		 h.append("</tr> \n");
		 h.append("<tr> \n");
		 h.append("<th>หน้าร้าน</th>\n");
		 h.append("<th>หน่วยบรรจุ</th>\n");
		 h.append("<th>วันหมดอายุ</th>\n");
		 h.append("<th>หน้าร้าน</th>\n");
		 h.append("<th>หน่วยบรรจุ</th>\n");
		 h.append("<th>วันหมดอายุ</th>\n");
		 h.append("<th>หน้าร้าน</th>\n");
		 h.append("<th>หน่วยบรรจุ</th>\n");
		 h.append("<th>วันหมดอายุ</th>\n");
		 h.append("</tr> \n");
		 for(int i=0;i<items.size();i++){
			 StockMCBean p = items.get(i);
			 h.append("<tr> \n");
			 h.append("<td class='text'> "+p.getStockDate()+"</td>\n");
			 h.append("<td class='text'> "+p.getCustomerCode()+"</td>\n");
			 h.append("<td class='text'> "+p.getCustomerName()+"</td>\n");
			 h.append("<td class='text'> "+p.getStoreCode()+"</td>\n");
			 h.append("<td class='text'> "+p.getCreateUser()+"</td>\n");
			 h.append("<td class='text'> "+p.getMcName()+"</td>\n");
			 h.append("<td class='text'> "+p.getProductCode()+"</td>\n");
			 h.append("<td class='text'> "+p.getBarcode()+"</td>\n");
			 h.append("<td class='text'> "+p.getProductName()+"</td>\n");
			 h.append("<td class='num'> "+p.getProductPackSize()+"</td>\n");
			 h.append("<td class='text'> "+p.getProductAge()+"</td>\n");
			 h.append("<td class='currency'> "+p.getRetailPriceBF()+"</td>\n");
			 h.append("<td class='currency'> "+p.getPromotionPrice()+"</td>\n");
			 h.append("<td class='num'> "+p.getLegQty()+"</td>\n");
			 h.append("<td class='num'> "+p.getInStoreQty()+"</td>\n");
			 h.append("<td class='num'> "+p.getBackendQty()+"</td>\n");
			 h.append("<td class='text'> "+p.getUom()+"</td>\n");
			 h.append("<td class='num'> "+p.getFrontendQty1()+"</td>\n");
			 h.append("<td class='text'> "+p.getUom1()+"</td>\n");
			 h.append("<td class='text'> "+p.getExpireDate1()+"</td>\n");
			 h.append("<td class='num'> "+p.getFrontendQty2()+"</td>\n");
			 h.append("<td class='text'> "+p.getUom2()+"</td>\n");
			 h.append("<td class='text'> "+p.getExpireDate2()+"</td>\n");
			 h.append("<td class='num'> "+p.getFrontendQty3()+"</td>\n");
			 h.append("<td class='text'> "+p.getUom3()+"</td>\n");
			 h.append("<td class='text'> "+p.getExpireDate3()+"</td>\n");
			 h.append("</tr> \n");
		 }
		 h.append("</table> \n");
		 return h;
		}
	
	public static StringBuffer genExportStockMCMasterItemReport(List<StockMCBean> items) throws Exception{
		 StringBuffer h = new StringBuffer("");
		 h.append(ExcelHeader.EXCEL_HEADER);
		 
		 h.append("<table border='1'> \n");
		 h.append("   <tr><td colspan='9'><b>รายงาน รหัสสินค้านับสต๊อก MC</b></td></tr> \n");
		 h.append("</table> \n");
		 
		 h.append("<table border='1'> \n");
		 h.append("<tr> \n");
		 h.append("<th>ห้าง</th>\n");
		 h.append("<th>ชื่อห้าง</th>\n");
		 h.append("<th>รหัสสินค้า Pens</th>\n");
		 h.append("<th>รหัสสินค้าห้าง</th>\n");
		 h.append("<th>Barcode</th>\n");
		 h.append("<th>Description</th>\n");
		 h.append("<th>บรรจุ</th>\n");
		 h.append("<th>อายุสินค้า</th>\n");
		 h.append("<th>ราคาปลีก</th>\n");
		 h.append("</tr> \n");
		 for(int i=0;i<items.size();i++){
			 StockMCBean p = items.get(i);
			 h.append("<tr> \n");
			 h.append("<td class='text'> "+p.getCustomerCode()+"</td>\n");
			 h.append("<td class='text'> "+p.getCustomerName()+"</td>\n");
			 h.append("<td class='text'> "+p.getProductCode()+"</td>\n");
			 h.append("<td class='text'> "+p.getItemCust()+"</td>\n");
			 h.append("<td class='text'> "+p.getBarcode()+"</td>\n");
			 h.append("<td class='text'> "+p.getProductName()+"</td>\n");
			 h.append("<td class='text'> "+p.getProductPackSize()+"</td>\n");
			 h.append("<td class='text'> "+p.getProductAge()+"</td>\n");
			 h.append("<td class='currency'> "+p.getRetailPriceBF()+"</td>\n");
			 h.append("</tr> \n");
		 }
		 h.append("</table> \n");
		 return h;
		}
}
