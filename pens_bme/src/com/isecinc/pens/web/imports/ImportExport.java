package com.isecinc.pens.web.imports;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import util.excel.ExcelHeader;

import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.summary.process.GenerateEndDateLotus;

public class ImportExport {
	
private static Logger logger = Logger.getLogger("PENS");
	
	public StringBuffer genReconcileHTML(HttpServletRequest request,ImportForm form){
		StringBuffer h = new StringBuffer("");
		int colspan = 12;
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			
			//Header
			h.append("<table border='1'> \n");
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"'>Reconcile การตรวจนับสต๊อก</td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"'>รหัสร้าน :"+form.getStoreCode()+" - "+Utils.isNull(request.getParameter("storeName"))+"</td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"'>วันที่ตรวจนับ :"+GenerateEndDateLotus.getEndDateStockTemp(form.getStoreCode())+"</td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			  h.append("<td>Group</td> \n");
			  h.append("<td>Begining Qty</td> \n");
			  h.append("<td>Sale In Qty</td> \n");
			  h.append("<td>Sale Return Qty </td> \n");  
			  h.append("<td>Sales Out Qty </td> \n");
			  h.append("<td>Adjust Qty</td> \n");
			  h.append("<td>Stock short Qty</td> \n");
			  h.append("<td>Onhand Qty</td> \n");
			  h.append("<td>ตรวจนับจริง </td> \n");
			  h.append("<td>ส่วนต่าง</td> \n");
			  h.append("<td>20%</td> \n");
			  h.append("<td>รวมเงิน</td> \n");
			 h.append("</tr> \n");
			
			 String[] groupCode = request.getParameterValues("groupCode"); 
			 String[] beginingQty = request.getParameterValues("beginingQty"); 
			 String[] saleInQty = request.getParameterValues("saleInQty"); 
			 String[] saleReturnQty = request.getParameterValues("saleReturnQty"); 
			 String[] saleOutQty = request.getParameterValues("saleOutQty"); 
			 String[] adjustQty = request.getParameterValues("adjustQty"); 
			 String[] stockShortQty = request.getParameterValues("stockShortQty"); 
			 String[] onhandQty = request.getParameterValues("onhandQty"); 
			 String[] scanQty = request.getParameterValues("scanQty"); 
			 String[] diffQty = request.getParameterValues("diffQty"); 
			 String[] retailPriceBf = request.getParameterValues("retailPriceBf"); 
			 String[] diffAmt = request.getParameterValues("diffAmt"); 
				
			
			 String beginingQtyT = request.getParameter("beginingQtyT"); 
			 String saleInQtyT = request.getParameter("saleInQtyT"); 
			 String saleReturnQtyT = request.getParameter("saleReturnQtyT"); 
			 String saleOutQtyT = request.getParameter("saleOutQtyT"); 
			 String adjustQtyT = request.getParameter("adjustQtyT"); 
			 String stockShortQtyT = request.getParameter("stockShortQtyT"); 
			 String onhandQtyT = request.getParameter("onhandQtyT"); 
			 String scanQtyT = request.getParameter("scanQtyT"); 
			 String diffQtyT = request.getParameter("diffQtyT"); 
			 String diffAmtT = request.getParameter("diffAmtT"); 
			 
				for(int i=0;i<groupCode.length;i++){
					h.append("<tr> \n");
					  h.append("<td class='text'>"+groupCode[i]+"</td> \n");
					  h.append("<td class='num'>"+beginingQty[i]+"</td> \n");
					  h.append("<td class='num'>"+saleInQty[i]+"</td> \n");
					  h.append("<td class='num'>"+saleReturnQty[i]+"</td> \n");
					  h.append("<td class='num'>"+saleOutQty[i]+"</td> \n");
					  h.append("<td class='num'>"+adjustQty[i]+"</td> \n");
					  h.append("<td class='num'>"+stockShortQty[i]+"</td> \n");
					  h.append("<td class='num'>"+onhandQty[i]+"</td> \n");
					  h.append("<td class='num'>"+scanQty[i]+"</td> \n");
					  h.append("<td class='num'>"+diffQty[i]+"</td> \n");
					  h.append("<td class='num'>"+retailPriceBf[i]+"</td> \n");
					  h.append("<td class='currency_bold'>"+diffAmt[i]+"</td> \n");
					h.append("</tr>");
				}
				h.append("<tr> \n");
				  h.append("<td class='num_bold'>Total </td> \n");
				  h.append("<td class='num_bold'>"+beginingQtyT+"</td> \n");
				  h.append("<td class='num_bold'>"+saleInQtyT+"</td> \n");
				  h.append("<td class='num_bold'>"+saleReturnQtyT+"</td> \n");
				  h.append("<td class='num_bold'>"+saleOutQtyT+"</td> \n");
				  h.append("<td class='num_bold'>"+adjustQtyT+"</td> \n");
				  h.append("<td class='num_bold'>"+stockShortQtyT+"</td> \n");
				  h.append("<td class='num_bold'>"+onhandQtyT+"</td> \n");
				  h.append("<td class='num_bold'>"+scanQtyT+"</td> \n");
				  h.append("<td class='num_bold'>"+diffQtyT+"</td> \n");
				  h.append("<td class='num_bold'></td> \n");
				  h.append("<td class='currency_bold'>"+diffAmtT+"</td> \n");
				h.append("</tr>");
				h.append("</table> \n");
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
}
