package com.isecinc.pens.web.stockmc;

import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.pens.util.PageingGenerate;
import com.pens.util.excel.ExcelHeader;

public class StockMCExport {
	/** Logger */
    public static Logger logger = Logger.getLogger("PENS");
	
	public static StringBuffer genExportStockMCReport(String pageName,User user,List<StockMCBean> items,boolean excel) throws Exception{
		return genExportStockMCReportModel(pageName,user,items,excel,0,0,0,0, items.size());
	}
	public static StringBuffer genExportStockMCReport(String pageName,User user,List<StockMCBean> items,boolean excel,int totalPage, int totalRecord, int currPage,int startRec,int endRec) throws Exception{
		return genExportStockMCReportModel(pageName,user,items,excel,totalPage,totalRecord,currPage, startRec, endRec);
	}
	public static StringBuffer genExportStockMCReportModel(String pageName,User user,List<StockMCBean> items,boolean excel,int totalPage, int totalRecord, int currPage,int startRec,int endRec) throws Exception{
		 StringBuffer h = new StringBuffer("");
		 int no = 0;
		 String text ="td_text";
		 String textCenter ="td_text_center";
		 String number ="td_number";
		 logger.debug("startRec["+startRec+"]endRec["+endRec+"]");
		 if(excel){
			 h.append(ExcelHeader.EXCEL_HEADER);
			 h.append("<table border='1'> \n");
			 h.append("   <tr><td colspan='10'><b>��§ҹ  ���Թ�����ҧ </b></td></tr> \n");
			 h.append("</table> \n");
			 h.append("<table border='1'> \n");
			 
			 text ="text";
			 textCenter ="text";
			 number ="num";
		 }else{
			 //Gen Pageing
			 h.append(PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no));
			 
			 h.append("<div style='height:450px;width:"+(new Double(user.getScreenWidth())).intValue()+"px;' >\n");
			 h.append("<table id='myTable' class='table table-condensed table-striped' border='1'>\n");
		 }
		
		 h.append("<thead> \n");
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
		 h.append("</thead> \n");
		 h.append("<tbody> \n");
		 for(int i=startRec;i<endRec;i++){
			 StockMCBean p = items.get(i);
			 h.append("<tr> \n");
			 h.append("<td class='"+textCenter+"' width='5%'> "+p.getStockDate()+"</td>\n");
			 h.append("<td class='"+textCenter+"' width='7%'> "+p.getCustomerCode()+"</td>\n");
			 h.append("<td class='"+text+"' width='10%'> "+p.getCustomerName()+"</td>\n");
			 h.append("<td class='"+textCenter+"'  width='7%'> "+p.getStoreCode()+"</td>\n");
			 h.append("<td class='"+textCenter+"' width='5%'> "+p.getCreateUser()+"</td>\n");
			 h.append("<td class='"+text+"' width='10%'> "+p.getMcName()+"</td>\n");
			 h.append("<td class='"+textCenter+"' width='5%'> "+p.getProductCode()+"</td>\n");
			 h.append("<td class='"+text+"' width='7%'> "+p.getBarcode()+"</td>\n");
			 h.append("<td class='"+text+"' width='10%'> "+p.getProductName()+"</td>\n");
			 h.append("<td class='"+textCenter+"' width='5%'> "+p.getProductPackSize()+"</td>\n");
			 h.append("<td class='"+textCenter+"' width='5%'> "+p.getProductAge()+"</td>\n");
			 h.append("<td class='"+number+"' width='5%'> "+p.getRetailPriceBF()+"</td>\n");
			 h.append("<td class='"+number+"' width='5%'> "+p.getPromotionPrice()+"</td>\n");
			 h.append("<td class='"+number+"' width='3%'> "+p.getLegQty()+"</td>\n");
			 h.append("<td class='"+number+"' width='3%'> "+p.getInStoreQty()+"</td>\n");
			 h.append("<td class='"+number+"' width='3%'> "+p.getBackendQty()+"</td>\n");
			 h.append("<td class='"+text+"' width='3%'> "+p.getUom()+"</td>\n");
			 h.append("<td class='"+number+"' width='5%'> "+p.getFrontendQty1()+"</td>\n");
			 h.append("<td class='"+textCenter+"' width='3%'> "+p.getUom1()+"</td>\n");
			 h.append("<td class='"+textCenter+"' width='5%'> "+p.getExpireDate1()+"</td>\n");
			 h.append("<td class='"+number+"' width='5%'> "+p.getFrontendQty2()+"</td>\n");
			 h.append("<td class='"+textCenter+"' width='3%'> "+p.getUom2()+"</td>\n");
			 h.append("<td class='"+textCenter+"' width='5%'> "+p.getExpireDate2()+"</td>\n");
			 h.append("<td class='"+number+"' width='5%'> "+p.getFrontendQty3()+"</td>\n");
			 h.append("<td class='"+textCenter+"' width='3%'> "+p.getUom3()+"</td>\n");
			 h.append("<td class='"+textCenter+"' width='5%'> "+p.getExpireDate3()+"</td>\n");
			 h.append("</tr> \n");
		 }
		 h.append(" </tbody> \n");
		 h.append("</table> \n");
		 if( !excel){h.append("</div> \n");}
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
