package com.isecinc.pens.web.stockmc;

import java.util.List;

import util.ExcelHeader;

public class StockMCExport {
	
	
	public static StringBuffer genExportStockMCReport(List<StockMCBean> items) throws Exception{
		 StringBuffer h = new StringBuffer("");
		 int no = 0;
		 h.append(ExcelHeader.EXCEL_HEADER);
		 
		 h.append("<table border='1'> \n");
		 h.append("   <tr><td colspan='10'><b>��§ҹ  ���Թ�����ҧ </b></td></tr> \n");
		 h.append("</table> \n");
		 
		 h.append("<table border='1'> \n");
		 h.append("<tr> \n");
		 h.append("<th rowspan='3'>�ѹ����Ǩ�Ѻʵ�͡</th>\n");
		 h.append("<th rowspan='3'>��ҧ</th>\n");
		 h.append("<th rowspan='3'>������ҧ</th>\n");
		 h.append("<th rowspan='3'>�Ң�</th>\n");
		 h.append("<th rowspan='3'>���ѹ�֡��Ǩ��ʵ�͡</th>\n");
		 h.append("<th rowspan='3'>��ѡ�ҹ PC</th>\n");
		 h.append("<th rowspan='3'>�����Թ���</th>\n");
		 h.append("<th rowspan='3'>������</th>\n");
		 h.append("<th rowspan='3'>��������´�Թ���</th>\n");
		 h.append("<th rowspan='3'>��è�</th>\n");
		 h.append("<th rowspan='3'>�����Թ���</th>\n");
		 h.append("<th rowspan='3'>�Ҥһ�ա</th>\n");
		 h.append("<th rowspan='3'>�Ҥ��������</th>\n");
		 h.append("<th rowspan='3'>��</th>\n");
		 h.append("<th colspan='12'>ʵ�͡�Թ���</th>\n");
		 h.append("</tr> \n");
		 h.append("<tr> \n");
		 h.append("<th rowspan='2'>��к���ҧ</th>\n");
		 h.append("<th rowspan='2'>��ѧ��ҹ</th>\n");
		 h.append("<th rowspan='2'>˹��º�è�</th>\n");
		 h.append("<th colspan='3'>�����������ط�� 1</th>\n");
		 h.append("<th colspan='3'>�����������ط�� 2</th>\n");
		 h.append("<th colspan='3'>�����������ط�� 3</th>\n");
		 h.append("</tr> \n");
		 h.append("<tr> \n");
		 h.append("<th>˹����ҹ</th>\n");
		 h.append("<th>˹��º�è�</th>\n");
		 h.append("<th>�ѹ�������</th>\n");
		 h.append("<th>˹����ҹ</th>\n");
		 h.append("<th>˹��º�è�</th>\n");
		 h.append("<th>�ѹ�������</th>\n");
		 h.append("<th>˹����ҹ</th>\n");
		 h.append("<th>˹��º�è�</th>\n");
		 h.append("<th>�ѹ�������</th>\n");
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
		 h.append("   <tr><td colspan='9'><b>��§ҹ �����Թ��ҹѺʵ�͡ MC</b></td></tr> \n");
		 h.append("</table> \n");
		 
		 h.append("<table border='1'> \n");
		 h.append("<tr> \n");
		 h.append("<th>��ҧ</th>\n");
		 h.append("<th>������ҧ</th>\n");
		 h.append("<th>�����Թ��� Pens</th>\n");
		 h.append("<th>�����Թ�����ҧ</th>\n");
		 h.append("<th>Barcode</th>\n");
		 h.append("<th>Description</th>\n");
		 h.append("<th>��è�</th>\n");
		 h.append("<th>�����Թ���</th>\n");
		 h.append("<th>�Ҥһ�ա</th>\n");
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
