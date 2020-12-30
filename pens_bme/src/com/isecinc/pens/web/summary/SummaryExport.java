package com.isecinc.pens.web.summary;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.DiffStockSummary;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.PhysicalSummary;
import com.isecinc.pens.bean.TransactionSummary;
import com.isecinc.pens.bean.User;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class SummaryExport {
	private static Logger logger = Logger.getLogger("PENS");
	
	public StringBuffer genOnhandHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			//Header
			h.append("<table border='1'> \n");
			h.append("<tr> \n");
			h.append("   <td align='left' colspan='9'>รายงานข้อมูล  Stock Onhand B'ME</td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("  <td align='left' colspan='9' >จากรหัสสินค้า:"+form.getOnhandSummary().getItemCodeFrom()+"  ถึงรหัสสินค้า:"+form.getOnhandSummary().getItemCodeTo()+"</td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("   <td align='left' colspan='9' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("   <td align='left' colspan='9' >File Name:"+form.getOnhandSummary().getFileName()+"</td>\n");
			h.append("</tr> \n");
			h.append("</table> \n");

			if(form.getOnhandSummaryResults() != null){
			    List<OnhandSummary> list = (List<OnhandSummary>)form.getOnhandSummaryResults();
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>Group</td> \n");
				  h.append("<td>Item</td> \n");
				  h.append("<td>Description</td> \n");
				  h.append("<td>OnHand</td> \n");
				  h.append("<td>ราคาขายส่งก่อน VAT </td> \n");
				  h.append("<td>ราคาขายปลีกก่อน VAT </td> \n");
				  h.append("<td>Pens Item</td> \n");
				  h.append("<td>Barcode</td> \n");
				  h.append("<td>Material Master</td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getGroup()+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getItem())+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getItemDesc())+"</td> \n");
					  h.append("<td class='num'>"+s.getOnhandQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getWholePriceBF()+"</td> \n");
					  h.append("<td class='num'>"+s.getRetailPriceBF()+"</td> \n");
					  h.append("<td class='text'>"+s.getPensItem()+"</td> \n");
					  h.append("<td class='text'>"+s.getBarcode()+"</td> \n");
					  h.append("<td class='text'>"+s.getMaterialMaster()+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genOnhandLotusHTML(String page,HttpServletRequest request,SummaryForm form,User user,List<OnhandSummary> list){
		StringBuffer h = new StringBuffer("");
		String colspan ="12";
		String bStart = "";
		String bEnd = "";
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			
			if("GroupCode".equalsIgnoreCase(form.getSummaryType())){
				colspan ="11";
			}
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"'>รายงาน B'me Stock on-hand at Lotus(As Of)</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(list != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<th>รหัสร้านค้า</th> \n");
				  h.append("<th>ชื่อร้านค้า</th> \n");
				  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
				    h.append("<th>PensItem</th> \n");
				  }
				  h.append("<th>Group</th> \n");
				  h.append("<th>Sale In Qty</th> \n");
				  h.append("<th>Sale Return Qty </th> \n");
				  h.append("<th>Sales Out Qty </th> \n");
				  h.append("<th>Adjust </th> \n");
				  h.append("<th>Stock short </th> \n");
				  h.append("<th>Onhand Qty </th> \n");
				  h.append("<th>Price List</th> \n");
				  h.append("<th>Amount </th> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getStoreCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getStoreName()+"</td> \n");
					  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
					     h.append("<td class='text'>"+s.getPensItem()+"</td> \n");
					  }
					  h.append("<td class='text'>"+s.getGroup()+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getSaleInQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getSaleReturnQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getSaleOutQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getAdjustQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getStockShortQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getOnhandQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getRetailPriceBF()+bEnd+"</td> \n");
					  h.append("<td class='currency'>"+bStart+s.getOnhandAmt()+bEnd+"</td> \n");
					h.append("</tr>");
					
					logger.debug("onhandQty:"+s.getOnhandQty());
				}
				/** Summary **/
				bStart ="<b>";
				bEnd ="</b>";
				OnhandSummary s = (OnhandSummary)request.getSession().getAttribute("summary");
				h.append("<tr> \n");
				 
				  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;<b>รวม</b></td> \n");
				  }else{
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;<b>รวม</b></td> \n");
				  }
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleInQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleReturnQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleOutQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getAdjustQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getStockShortQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getOnhandQty()+bEnd+"</td> \n");
				  h.append("<td></td> \n");
				  h.append("<td class='currency_bold'>"+bStart+s.getOnhandAmt()+bEnd+"</td> \n");
				h.append("</tr>");
				
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	public StringBuffer genOnhandTopsHTML(String page,HttpServletRequest request,SummaryForm form,User user,List<OnhandSummary> list){
		StringBuffer h = new StringBuffer("");
		String colspan ="12";
		String bStart = "";
		String bEnd = "";
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			
			if("GroupCode".equalsIgnoreCase(form.getSummaryType())){
				colspan ="11";
			}
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"'>รายงาน B'me Stock on-hand at Tops(As Of)</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(list != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<th>รหัสร้านค้า</th> \n");
				  h.append("<th>ชื่อร้านค้า</th> \n");
				  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
				    h.append("<th>PensItem</th> \n");
				  }
				  h.append("<th>Group</th> \n");
				  h.append("<th>Sale In Qty</th> \n");
				  h.append("<th>Sale Return Qty </th> \n");
				  h.append("<th>Sales Out Qty </th> \n");
				  h.append("<th>Adjust </th> \n");
				  h.append("<th>Stock short </th> \n");
				  h.append("<th>Onhand Qty </th> \n");
				  h.append("<th>Price List</th> \n");
				  h.append("<th>Amount </th> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getStoreCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getStoreName()+"</td> \n");
					  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
					     h.append("<td class='text'>"+s.getPensItem()+"</td> \n");
					  }
					  h.append("<td class='text'>"+s.getGroup()+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getSaleInQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getSaleReturnQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getSaleOutQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getAdjustQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getStockShortQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getOnhandQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getRetailPriceBF()+bEnd+"</td> \n");
					  h.append("<td class='currency'>"+bStart+s.getOnhandAmt()+bEnd+"</td> \n");
					h.append("</tr>");
					
					logger.debug("onhandQty:"+s.getOnhandQty());
				}
				/** Summary **/
				bStart ="<b>";
				bEnd ="</b>";
				OnhandSummary s = (OnhandSummary)request.getSession().getAttribute("summary");
				h.append("<tr> \n");
				 
				  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;<b>รวม</b></td> \n");
				  }else{
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;<b>รวม</b></td> \n");
				  }
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleInQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleReturnQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleOutQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getAdjustQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getStockShortQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getOnhandQty()+bEnd+"</td> \n");
				  h.append("<td></td> \n");
				  h.append("<td class='currency_bold'>"+bStart+s.getOnhandAmt()+bEnd+"</td> \n");
				h.append("</tr>");
				
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	public StringBuffer genOnhandRobinsonHTML(String page,HttpServletRequest request,SummaryForm form,User user,List<OnhandSummary> list){
		StringBuffer h = new StringBuffer("");
		String colspan ="13";
		String bStart = "";
		String bEnd = "";
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			
			if("GroupCode".equalsIgnoreCase(form.getSummaryType())){
				colspan ="12";
			}
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"'>รายงาน B'me Stock on-hand at Robinson(As Of)</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(list != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<th>รหัสร้านค้า</th> \n");
				  h.append("<th>ชื่อร้านค้า</th> \n");
				  h.append("<th>Store No</th> \n");
				  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
				    h.append("<th>PensItem</th> \n");
				  }
				  h.append("<th>Group</th> \n");
				  h.append("<th>Sale In Qty</th> \n");
				  h.append("<th>Sale Return Qty </th> \n");
				  h.append("<th>Sales Out Qty </th> \n");
				  h.append("<th>Adjust </th> \n");
				  h.append("<th>Stock short </th> \n");
				  h.append("<th>Onhand Qty </th> \n");
				  h.append("<th>Price List</th> \n");
				  h.append("<th>Amount </th> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getStoreCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getStoreName()+"</td> \n");
					  h.append("<td class='text'>"+s.getStoreNo()+"</td> \n");
					  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
					     h.append("<td class='text'>"+s.getPensItem()+"</td> \n");
					  }
					  h.append("<td class='text'>"+s.getGroup()+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getSaleInQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getSaleReturnQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getSaleOutQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getAdjustQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getStockShortQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getOnhandQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getRetailPriceBF()+bEnd+"</td> \n");
					  h.append("<td class='currency'>"+bStart+s.getOnhandAmt()+bEnd+"</td> \n");
					h.append("</tr>");
					
					logger.debug("onhandQty:"+s.getOnhandQty());
				}
				/** Summary **/
				bStart ="<b>";
				bEnd ="</b>";
				OnhandSummary s = (OnhandSummary)request.getSession().getAttribute("summary");
				h.append("<tr> \n");
				 
				  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;<b>รวม</b></td> \n");
				  }else{
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;<b>รวม</b></td> \n");
				  }
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleInQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleReturnQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleOutQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getAdjustQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getStockShortQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getOnhandQty()+bEnd+"</td> \n");
				  h.append("<td></td> \n");
				  h.append("<td class='currency_bold'>"+bStart+s.getOnhandAmt()+bEnd+"</td> \n");
				h.append("</tr>");
				
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	public StringBuffer genReportStockWacoalLotusHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		String colspan ="9";
		String bStart = "";
		String bEnd = "";
		List<OnhandSummary> list = form.getResults();
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			
			//Header
			h.append("<table border='1'> \n");
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"'>รายงาน Stock Wacoal คงเหลือ at Lotus</td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >As Of Date:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Branch ID:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >วันที่ล่าสุดที่มีการตรวจนับสต็อก:"+form.getOnhandSummary().getInitDate()+"</td> \n");
			h.append("</tr> \n");
			h.append("</table> \n");

			if(list != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<th>Branch ID</th> \n");
				  h.append("<th>Branch Name</th> \n");
				  h.append("<th>วันที่เช็คสต๊อกล่าสุด </th> \n");
				  h.append("<th>Group</th> \n");
				  h.append("<th>Initial Stock </th> \n");
				  h.append("<th>Sale In Qty</th> \n");
				  h.append("<th>Sales Out Qty </th> \n");
				  h.append("<th>Return Qty </th> \n");
				  h.append("<th>Onhand Qty </th> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getStoreCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getStoreName()+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getInitDate())+"</td> \n");
					  h.append("<td class='text'>"+s.getGroup()+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getInitSaleQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getSaleInQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getSaleOutQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getSaleReturnQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getOnhandQty()+bEnd+"</td> \n");
	
					h.append("</tr>");
					
					logger.debug("onhandQty:"+s.getOnhandQty());
				}
				/** Summary **/
				bStart ="<b>";
				bEnd ="</b>";
				OnhandSummary s = (OnhandSummary)request.getSession().getAttribute("summary");
				h.append("<tr> \n");
				  h.append("<td>&nbsp;</td> \n");
				  h.append("<td>&nbsp;</td> \n");
				  h.append("<td>&nbsp;</td> \n");
				  h.append("<td>&nbsp;<b>รวม</b></td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getInitSaleQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleInQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleOutQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleReturnQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getOnhandQty()+bEnd+"</td> \n");
				h.append("</tr>");
				
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genBmeTransHTML(String page,HttpServletRequest request,SummaryForm form,User user,List<OnhandSummary> list){
		StringBuffer h = new StringBuffer("");
		String colspan ="9";
		String bStart = "";
		String bEnd = "";
		try{
			if("GroupCode".equalsIgnoreCase(form.getSummaryType())){
				colspan ="8";
			}
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"'>รายงาน  Transaction B'me </td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >จากวันที่:"+form.getOnhandSummary().getAsOfDateFrom()+"ถึง"+form.getOnhandSummary().getAsOfDateTo() +"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(list != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>รหัสสาขา</td> \n");
				  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
				    h.append("<td>PensItem</td> \n");
				  }
				  h.append("<td>Group</td> \n");
				  h.append("<td>Sale In Qty</td> \n");
				  h.append("<td>Sale Return Qty </td> \n");
				  h.append("<td>Sales Out Qty </td> \n");
				  h.append("<td>Adjust </td> \n");
				  h.append("<td>Stock short </td> \n");
				  h.append("<td>Onhand Qty </td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>&nbsp;"+s.getStoreCode()+"</td> \n");
					  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
					  h.append("<td>&nbsp;"+s.getPensItem()+"</td> \n");
					  }
					  h.append("<td>"+s.getGroup()+"</td> \n");
					  h.append("<td>"+bStart+s.getSaleInQty()+bEnd+"</td> \n");
					  h.append("<td>"+bStart+s.getSaleReturnQty()+bEnd+"</td> \n");
					  h.append("<td>"+bStart+s.getSaleOutQty()+bEnd+"</td> \n");
					  h.append("<td>"+bStart+s.getAdjustQty()+bEnd+"</td> \n");
					  h.append("<td>"+bStart+s.getStockShortQty()+bEnd+"</td> \n");
					  h.append("<td>"+bStart+s.getOnhandQty()+bEnd+"</td> \n");
					  
					h.append("</tr>");
				}
				/** Summary **/
				bStart ="<b>";
				bEnd ="</b>";
				OnhandSummary s = (OnhandSummary)request.getSession().getAttribute("summary");
				h.append("<tr> \n");
				  h.append("<td>&nbsp;"+s.getStoreCode()+"</td> \n");
				  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
				  h.append("<td>&nbsp;"+s.getPensItem()+"</td> \n");
				  }
				  h.append("<td>"+s.getGroup()+"</td> \n");
				  h.append("<td>"+bStart+s.getSaleInQty()+bEnd+"</td> \n");
				  h.append("<td>"+bStart+s.getSaleReturnQty()+bEnd+"</td> \n");
				  h.append("<td>"+bStart+s.getSaleOutQty()+bEnd+"</td> \n");
				  h.append("<td>"+bStart+s.getAdjustQty()+bEnd+"</td> \n");
				  h.append("<td>"+bStart+s.getStockShortQty()+bEnd+"</td> \n");
				  h.append("<td>"+bStart+s.getOnhandQty()+bEnd+"</td> \n");
				h.append("</tr>");
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genMonthEndLotusHTML(String page,HttpServletRequest request,SummaryForm form,User user,List<OnhandSummary> list){
		StringBuffer h = new StringBuffer("");
		int colspan = 11;
		try{
			if("GroupCode".equalsIgnoreCase(form.getSummaryType())){
				colspan = 10;
			}
			
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"'>รายงานปิดรอบ LOTUS</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(list != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>รหัสสาขา</td> \n");
				  h.append("<td>ชื่อสาขา</td> \n");
				  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
				     h.append("<td>PensItem</td> \n");
				  }
				  h.append("<td>Group</td> \n");
				  h.append("<td>Begining Qty</td> \n");
				  h.append("<td>Sale In Qty</td> \n");
				  h.append("<td>Sale Return Qty </td> \n");
				  h.append("<td>Sales Out Qty </td> \n");
				  h.append("<td>Adjust Qty</td> \n");
				  h.append("<td>Stock short </td> \n");
				  h.append("<td>Onhand Qty </td> \n");
				 
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getStoreCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getStoreName()+"</td> \n");
					  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
					     h.append("<td class='text'>"+s.getPensItem()+"</td> \n");
					  }
					  h.append("<td class='text'>"+s.getGroup()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getBeginingQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getSaleInQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getAdjustQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getStockShortQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getOnhandQty()+"</td> \n");
					  
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genReportEndDateLotusHTML(String page,HttpServletRequest request,SummaryForm form,User user,List<OnhandSummary> list){
		StringBuffer h = new StringBuffer("");
		int colspan = 13;
		try{
			if("GroupCode".equalsIgnoreCase(form.getSummaryType())){
				colspan = 12;
			}
			//Head Style
			h.append(ExcelHeader.EXCEL_HEADER);
			
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"'>รายงานปิดรอบ LOTUS(EndDate)</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >ปิดสต๊อกล่าสุดวันที่:"+form.getEndDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(list != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<th>รหัสสาขา</th> \n");
				  h.append("<th>ชื่อสาขา</th> \n");
				  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
				     h.append("<th>PensItem</th> \n");
				  }
				  h.append("<th>Group</th> \n");
				  h.append("<th>Begining Qty</th> \n");
				  h.append("<th>Sale In Qty</th> \n");
				  h.append("<th>Sale Return Qty </th> \n");
				  h.append("<th>Sales Out Qty </th> \n");
				  h.append("<th>Adjust Qty</th> \n");
				  h.append("<th>Stock short </th> \n");
				  h.append("<th>Onhand Qty </th> \n");
				  h.append("<th>Price List </th> \n");
				  h.append("<th>Amount </th> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>"+s.getStoreCode()+"</td> \n");
					  h.append("<td>"+s.getStoreName()+"</td> \n");
					  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
					     h.append("<td>"+s.getPensItem()+"</td> \n");
					  }
					  h.append("<td>"+s.getGroup()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getBeginingQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getSaleInQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getAdjustQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getStockShortQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getOnhandQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getRetailPriceBF()+"</td> \n");
					  h.append("<td class='currency'>"+s.getOnhandAmt()+"</td> \n");
					h.append("</tr>");
				}
				
				OnhandSummary s = (OnhandSummary)request.getSession().getAttribute("summary");
				h.append("<tr> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
				     h.append("<td></td> \n");
				  }
				  h.append("<td class='colum_head'>Total</td> \n");
				  h.append("<td class='num_currency_bold'>"+s.getBeginingQty()+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+s.getSaleInQty()+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+s.getSaleReturnQty()+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+s.getSaleOutQty()+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+s.getAdjustQty()+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+s.getStockShortQty()+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+s.getOnhandQty()+"</td> \n");
				  h.append("<td class='currency_bold'></td> \n");
				  h.append("<td class='currency_bold'>"+s.getOnhandAmt()+"</td> \n");
				h.append("</tr>");
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genOnhandMTTHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		String colspan = "10";
		try{
			if("GroupCode".equalsIgnoreCase(form.getSummaryType())){
				colspan = "9";
			}
			h.append(ExcelHeader.EXCEL_HEADER);
			
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td  class='colum_head' align='left' colspan='"+colspan+"'>รายงาน B'me Stock on-hand MTT</td> \n");
			
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td class='colum_head'  align='left' colspan='"+colspan+"' >จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td  class='colum_head' align='left' colspan='"+colspan+"' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td  class='colum_head' align='left' colspan='"+colspan+"' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td  class='colum_head' align='left' colspan='"+colspan+"' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getResults() != null){
			    List<OnhandSummary> list = (List<OnhandSummary>)form.getResults();
			 	    
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td class='colum_head'>รหัสร้านค้า(Bme)</td> \n");
				  h.append("<td class='colum_head'>CustNo(Oracle)</td> \n");
				  h.append("<td class='colum_head'>ชื่อร้านค้า</td> \n");
				  h.append("<td class='colum_head'>Group</td> \n");
				  if( !"GroupCode".equalsIgnoreCase(form.getSummaryType())){
				    h.append("<td class='colum_head'>PensItem</td> \n");
				  }
				  h.append("<td class='colum_head'>Initial Stock</td> \n");
				  h.append("<td class='colum_head'>Sale In Qty</td> \n");
				  h.append("<td class='colum_head'>Sale Out Qty</td> \n");
				  h.append("<td class='colum_head'>Return Qty </td> \n");
				  h.append("<td class='colum_head'>Onhand Qty </td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>"+s.getStoreCode()+"</td> \n");
					  h.append("<td>"+s.getCustNo()+"</td> \n");
					  h.append("<td>"+s.getStoreName()+"</td> \n");
					  h.append("<td>"+s.getGroup()+"</td> \n");
					  if( !"GroupCode".equalsIgnoreCase(form.getSummaryType())){
					    h.append("<td>"+s.getPensItem()+"</td> \n");
					  }
					  h.append("<td class='num'>"+s.getInitSaleQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleInQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getOnhandQty()+"</td> \n");
					h.append("</tr>");
				}
				OnhandSummary sum = (OnhandSummary)request.getSession().getAttribute("summary");
				   h.append("<tr> \n");
				      if( !"GroupCode".equalsIgnoreCase(form.getSummaryType())){
					    h.append("<td class='colum_head' colspan='5' align='right'>รวม</td> \n");
					  }else{
						h.append("<td class='colum_head' colspan='4' align='right'>รวม</td> \n");  
					  }
					  h.append("<td class='num_currency_bold'>"+sum.getInitSaleQty()+"</td> \n");
					  h.append("<td class='num_currency_bold'>"+sum.getSaleInQty()+"</td> \n");
					  h.append("<td class='num_currency_bold'>"+sum.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num_currency_bold'>"+sum.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num_currency_bold'>"+sum.getOnhandQty()+"</td> \n");
				   h.append("</tr>");
				
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genOnhandAsOfKingHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		String colspan = "11";
		try{
			if("GroupCode".equalsIgnoreCase(form.getSummaryType())){
				colspan = "10";
			}
			h.append(ExcelHeader.EXCEL_HEADER);
			
			//Header
			h.append("<table border='1'> \n");
			h.append("<tr> \n");
			h.append("<td  class='colum_head' align='left' colspan='"+colspan+"'>รายงาน B'me Stock on-hand DutyFree</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td class='colum_head'  align='left' colspan='"+colspan+"' >จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td  class='colum_head' align='left' colspan='"+colspan+"' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td  class='colum_head' align='left' colspan='"+colspan+"' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td  class='colum_head' align='left' colspan='"+colspan+"' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");
			if(form.getResults() != null){
			    List<OnhandSummary> list = (List<OnhandSummary>)form.getResults();
			 	    
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td class='colum_head'>รหัสร้านค้า(Bme)</td> \n");
				  h.append("<td class='colum_head'>CustNo(Oracle)</td> \n");
				  h.append("<td class='colum_head'>ชื่อร้านค้า</td> \n");
				  h.append("<td class='colum_head'>Group</td> \n");
				  if( !"GroupCode".equalsIgnoreCase(form.getSummaryType())){
				    h.append("<td class='colum_head'>PensItem</td> \n");
				  }
				  h.append("<td class='colum_head'>Initial Stock</td> \n");
				  h.append("<td class='colum_head'>Sale In Qty</td> \n");
				  h.append("<td class='colum_head'>Sale Out Qty</td> \n");
				  h.append("<td class='colum_head'>Adjust Qty</td> \n");
				  h.append("<td class='colum_head'>Return Qty </td> \n");
				  h.append("<td class='colum_head'>Onhand Qty </td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getStoreCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getCustNo()+"</td> \n");
					  h.append("<td class='text'>"+s.getStoreName()+"</td> \n");
					  h.append("<td class='text'>"+s.getGroup()+"</td> \n");
					  if( !"GroupCode".equalsIgnoreCase(form.getSummaryType())){
					    h.append("<td class='text'>"+s.getPensItem()+"</td> \n");
					  }
					  h.append("<td class='num'>"+s.getInitSaleQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleInQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getAdjustQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getOnhandQty()+"</td> \n");
					h.append("</tr>");
				}
				OnhandSummary sum = (OnhandSummary)request.getSession().getAttribute("summary");
				   h.append("<tr> \n");
				      if( !"GroupCode".equalsIgnoreCase(form.getSummaryType())){
					    h.append("<td class='colum_head' colspan='5' align='right'>รวม</td> \n");
					  }else{
						h.append("<td class='colum_head' colspan='4' align='right'>รวม</td> \n");  
					  }
					  h.append("<td class='num_currency_bold'>"+sum.getInitSaleQty()+"</td> \n");
					  h.append("<td class='num_currency_bold'>"+sum.getSaleInQty()+"</td> \n");
					  h.append("<td class='num_currency_bold'>"+sum.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num_currency_bold'>"+sum.getAdjustQty()+"</td> \n");
					  h.append("<td class='num_currency_bold'>"+sum.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num_currency_bold'>"+sum.getOnhandQty()+"</td> \n");
				   h.append("</tr>");
				
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genOnhandAsOfRobinsonHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		String colspan = "10";
		try{
			if("GroupCode".equalsIgnoreCase(form.getSummaryType())){
				colspan = "9";
			}
			h.append(ExcelHeader.EXCEL_HEADER);
			
			//Header
			h.append("<table border='1'> \n");
			h.append("<tr> \n");
			h.append("<td  class='colum_head' align='left' colspan='"+colspan+"'>รายงาน B'me Stock on-hand Robinson</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td class='colum_head'  align='left' colspan='"+colspan+"' >จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td  class='colum_head' align='left' colspan='"+colspan+"' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td  class='colum_head' align='left' colspan='"+colspan+"' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td  class='colum_head' align='left' colspan='"+colspan+"' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");
			if(form.getResults() != null){
			    List<OnhandSummary> list = (List<OnhandSummary>)form.getResults();
			 	    
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td class='colum_head'>รหัสร้านค้า(Bme)</td> \n");
				  h.append("<td class='colum_head'>CustNo(Oracle)</td> \n");
				  h.append("<td class='colum_head'>ชื่อร้านค้า</td> \n");
				  h.append("<td class='colum_head'>Group</td> \n");
				  if( !"GroupCode".equalsIgnoreCase(form.getSummaryType())){
				    h.append("<td class='colum_head'>PensItem</td> \n");
				  }
				  h.append("<td class='colum_head'>Initial Stock</td> \n");
				  h.append("<td class='colum_head'>Sale In Qty</td> \n");
				  h.append("<td class='colum_head'>Sale Out Qty</td> \n");
				  h.append("<td class='colum_head'>Return Qty </td> \n");
				  h.append("<td class='colum_head'>Onhand Qty </td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getStoreCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getCustNo()+"</td> \n");
					  h.append("<td class='text'>"+s.getStoreName()+"</td> \n");
					  h.append("<td class='text'>"+s.getGroup()+"</td> \n");
					  if( !"GroupCode".equalsIgnoreCase(form.getSummaryType())){
					    h.append("<td class='text'>"+s.getPensItem()+"</td> \n");
					  }
					  h.append("<td class='num'>"+s.getInitSaleQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleInQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getOnhandQty()+"</td> \n");
					h.append("</tr>");
				}
				OnhandSummary sum = (OnhandSummary)request.getSession().getAttribute("summary");
				   h.append("<tr> \n");
				      if( !"GroupCode".equalsIgnoreCase(form.getSummaryType())){
					    h.append("<td class='colum_head' colspan='5' align='right'>รวม</td> \n");
					  }else{
						h.append("<td class='colum_head' colspan='4' align='right'>รวม</td> \n");  
					  }
					  h.append("<td class='num_currency_bold'>"+sum.getInitSaleQty()+"</td> \n");
					  h.append("<td class='num_currency_bold'>"+sum.getSaleInQty()+"</td> \n");
					  h.append("<td class='num_currency_bold'>"+sum.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num_currency_bold'>"+sum.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num_currency_bold'>"+sum.getOnhandQty()+"</td> \n");
				   h.append("</tr>");
				
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genOnhandMTTDetailHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		String colSpan ="12";
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'>รายงาน Stock B'me Stock onhand - MTT ( ระดับสี/ไซร์ ) </td> \n");
			
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'>จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >วันที่ล่าสุดที่มีการตรวจนับสต็อก :"+form.getOnhandSummary().getInitDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'>Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'>Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getOnhandSummaryMTTDetailResults() != null){
			    List<OnhandSummary> list = (List<OnhandSummary>)form.getOnhandSummaryMTTDetailResults();
			    h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>รหัสร้านค้า(Bme)</td> \n");
				  h.append("<td>CustNo(Oracle)</td> \n");
				  h.append("<td>ชื่อร้านค้า</td> \n");
				  h.append("<td>Group</td> \n");
				  h.append("<td>PensItem</td> \n");
				  h.append("<td>Materila Master</td> \n");
				  h.append("<td>Barcode</td> \n");
				  h.append("<td>Initial Stock</td> \n");
				  h.append("<td>Sale In Qty</td> \n");
				  h.append("<td>Sale Out Qty</td> \n");
				  h.append("<td>Return Qty </td> \n");
				  h.append("<td>Onhand Qty </td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getStoreCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getCustNo()+"</td> \n");
					  h.append("<td class='text'>"+s.getStoreName()+"</td> \n");
					  h.append("<td class='text'>"+s.getGroup()+"</td> \n");
					  h.append("<td class='text'>"+s.getPensItem()+"</td> \n");
					  h.append("<td class='text'>"+s.getMaterialMaster()+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getBarcode())+"</td> \n");
					  h.append("<td class='num'>"+s.getInitSaleQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleInQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getOnhandQty()+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genOnhandSizeColorKingHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		String a= "@";
		String colSpan ="13";
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'>รายงาน Stock B'me Stock onhand - DutyFree ( ระดับสี/ไซร์ ) </td> \n");
			
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'>จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >วันที่ล่าสุดที่มีการตรวจนับสต็อก :"+form.getOnhandSummary().getInitDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'>Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'>Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getResults() != null){
			    List<OnhandSummary> list = (List<OnhandSummary>)form.getResults();
			    h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>รหัสร้านค้า(Bme)</td> \n");
				  h.append("<td>CustNo(Oracle)</td> \n");
				  h.append("<td>ชื่อร้านค้า</td> \n");
				  h.append("<td>Group</td> \n");
				  h.append("<td>PensItem</td> \n");
				  h.append("<td>Materila Master</td> \n");
				  h.append("<td>Barcode</td> \n");
				  h.append("<td>Initial Stock</td> \n");
				  h.append("<td>Sale In Qty</td> \n");
				  h.append("<td>Sale Out Qty</td> \n");
				  h.append("<td>Adjust Qty</td> \n");
				  h.append("<td>Return Qty </td> \n");
				  h.append("<td>Onhand Qty </td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getStoreCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getCustNo()+"</td> \n");
					  h.append("<td class='text'>"+s.getStoreName()+"</td> \n");
					  h.append("<td class='text'>"+s.getGroup()+"</td> \n");
					  h.append("<td class='text'>"+s.getPensItem()+"</td> \n");
					  h.append("<td class='text'>"+s.getMaterialMaster()+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getBarcode())+"</td> \n");
					  h.append("<td class='num'>"+s.getInitSaleQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleInQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getAdjustQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getOnhandQty()+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	public StringBuffer genBigCSizeColorHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		String a= "@";
		String colSpan= "13";
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >รายงาน Stock B'me Stock onhand At BigC ( ระดับสี/ไซร์ ) </td> \n");
			
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'  >จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'  >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >วันที่ล่าสุดที่มีการตรวจนับสต็อก:"+form.getOnhandSummary().getInitDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'  >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");
			
			if(form.getResults() != null){
			    List<OnhandSummary> list = (List<OnhandSummary>)form.getResults();
			    
			    h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>รหัสร้านค้า(Bme)</td> \n");
				  h.append("<td>Sub Inv</td> \n");
				  h.append("<td>ชื่อร้านค้า</td> \n");
				  h.append("<td>Group</td> \n");
				  h.append("<td>PensItem</td> \n");
				  h.append("<td>Materila Master</td> \n");
				  h.append("<td>Barcode</td> \n");
				  h.append("<td>Initial Stock</td> \n");
				  h.append("<td>Trans In Qty</td> \n");
				  h.append("<td>Sale Out Qty</td> \n");
				  h.append("<td>Return Qty </td> \n");
				  h.append("<td>Adjust Qty </td> \n");
				  h.append("<td>Onhand Qty </td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>"+s.getStoreCode()+"</td> \n");
					  h.append("<td>"+s.getSubInv()+"</td> \n");
					  h.append("<td>"+s.getStoreName()+"</td> \n");
					  h.append("<td>"+s.getGroup()+"</td> \n");
					  h.append("<td>"+s.getPensItem()+"</td> \n");
					  h.append("<td>"+s.getMaterialMaster()+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getBarcode())+"</td> \n");
					  h.append("<td class='num'>"+s.getInitSaleQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getTransInQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getAdjustSaleQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getOnhandQty()+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
				
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genOnhandBigCSPHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		String a= "@";
		String colSpan= "11";
		try{
			if("GroupCode".equalsIgnoreCase(form.getSummaryType())){
				colSpan ="10";
			}
			h.append("<style> \n");
			h.append(" .num { \n");
			h.append("  mso-number-format:General; \n");
			h.append(" } \n");
			h.append(" .text{ \n");
			h.append("   mso-number-format:'"+a+"'; \n");
			h.append(" } \n");
			h.append("</style> \n");
			
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >รายงาน Stock Onhand at BigC (For SP) </td> \n");
			
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'  >จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'  >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >วันที่ล่าสุดที่มีการตรวจนับสต็อก:"+form.getOnhandSummary().getInitDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'  >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");
			
			if(form.getResults() != null){
			    List<OnhandSummary> list = (List<OnhandSummary>)form.getResults();
			    
			    h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>รหัสร้านค้า(Bme)</td> \n");
				  h.append("<td>Sub Inv</td> \n");
				  h.append("<td>ชื่อร้านค้า</td> \n");
				  h.append("<td>Group</td> \n");
				  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
				  h.append("<td>Pens Item</td> \n");
				  }
				  h.append("<td>Initial Stock</td> \n");
				  h.append("<td>Trans In Qty</td> \n");
				  h.append("<td>Sale Out Qty</td> \n");
				  h.append("<td>Return Qty </td> \n");
				  h.append("<td>Adjust Qty </td> \n");
				  h.append("<td>Onhand Qty </td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>"+s.getStoreCode()+"</td> \n");
					  h.append("<td>"+s.getSubInv()+"</td> \n");
					  h.append("<td>"+s.getStoreName()+"</td> \n");
					  h.append("<td>"+s.getGroup()+"</td> \n");
					  if("PensItem".equalsIgnoreCase(form.getSummaryType())){
					  h.append("<td>"+s.getPensItem()+"</td> \n");
					  }
					  h.append("<td class='num'>"+s.getInitSaleQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getTransInQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getAdjustSaleQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getOnhandQty()+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
				
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genLotusSizeColorHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		String a= "@";
		String colSpan= "13";
		try{
			if("GroupCode".equalsIgnoreCase(form.getSummaryType())){
				colSpan= "10";
			}
			h.append("<style> \n");
			h.append(" .num { \n");
			h.append("  mso-number-format:General; \n");
			h.append(" } \n");
			h.append(" .text{ \n");
			h.append("   mso-number-format:'"+a+"'; \n");
			h.append(" } \n");
			h.append("</style> \n");
			
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >Stock on-hand at LOTUS สี-ไซร์ (ตั้งสต๊อกใหม่)</td> \n");
			
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'  >จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'  >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >วันที่ล่าสุดที่มีการตรวจนับสต็อก:"+form.getOnhandSummary().getInitDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'  >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");
			
			if(form.getResults() != null){
			    List<OnhandSummary> list = (List<OnhandSummary>)form.getResults();
			    
			    h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>รหัสร้านค้า(Bme)</td> \n");
				  h.append("<td>Sub Inv</td> \n");
				  h.append("<td>ชื่อร้านค้า</td> \n");
				  h.append("<td>Group</td> \n");
				  if("PensItem".equalsIgnoreCase(form.getSummaryType()) || "".equalsIgnoreCase(Utils.isNull(form.getSummaryType()))){
				    h.append("<td>PensItem</td> \n");
				    h.append("<td>Materila Master</td> \n");
				    h.append("<td>Barcode</td> \n");
				  }
				  h.append("<td>Initial Stock</td> \n");
				  h.append("<td>Trans In Qty</td> \n");
				  h.append("<td>Sale Out Qty</td> \n");
				  h.append("<td>Return Qty </td> \n");
				  h.append("<td>Adjust Qty </td> \n");
				  h.append("<td>Onhand Qty </td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>"+s.getStoreCode()+"</td> \n");
					  h.append("<td>"+s.getSubInv()+"</td> \n");
					  h.append("<td>"+s.getStoreName()+"</td> \n");
					  h.append("<td>"+s.getGroup()+"</td> \n");
					  if("PensItem".equalsIgnoreCase(form.getSummaryType()) || "".equalsIgnoreCase(Utils.isNull(form.getSummaryType()))){
					  h.append("<td>"+s.getPensItem()+"</td> \n");
					  h.append("<td>"+s.getMaterialMaster()+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getBarcode())+"</td> \n");
					  }
					  h.append("<td class='num'>"+s.getInitSaleQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getTransInQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getAdjustSaleQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getOnhandQty()+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
				
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genOnhandBigCHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		String colSpan = "11";
		try{
			//Style Class
			h.append(ExcelHeader.EXCEL_HEADER);
			
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
		    h.append("<td align='left' colspan='"+colSpan+"'>รายงาน B'me Stock on-hand at BigC(As Of) </td> \n");
			
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getResults() != null){
			    List<OnhandSummary> list = (List<OnhandSummary>)form.getResults();
			    
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>รหัสสาขา</td> \n");
				  h.append("<td>Sub Inv</td> \n");
				  h.append("<td>ชื่อสาขา</td> \n");
				  h.append("<td>Group</td> \n");
				  h.append("<td>Pens Item</td> \n");
				  h.append("<td>Initial Qty</td> \n");
				  h.append("<td>Trans In Qty</td> \n");
				  h.append("<td>Return Qty </td> \n");
				  h.append("<td>SalesOut+StockShort Qty </td> \n");
				  h.append("<td>Adjust Qty </td> \n");
				/*  h.append("<td>Stock Short Qty </td> \n");*/
				  h.append("<td>Onhand Qty </td> \n");
				 
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getStoreCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getSubInv()+"</td> \n");
					  h.append("<td class='text'>"+s.getStoreName()+"</td> \n");
					  h.append("<td class='text'>"+s.getGroup()+"</td> \n");
					  h.append("<td class='text'>"+s.getPensItem()+"</td> \n");
					  h.append("<td class='currency'>"+s.getInitSaleQty()+"</td> \n");
					  h.append("<td class='currency'>"+s.getTransInQty()+"</td> \n");
					  h.append("<td class='currency'>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='currency'>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td class='currency'>"+s.getAdjustQty()+"</td> \n");
					 /* h.append("<td class='currency'>"+s.getStockShortQty()+"</td> \n");*/
					  h.append("<td class='currency'>"+s.getOnhandQty()+"</td> \n");
					  
					h.append("</tr>");
				}//for
				
				OnhandSummary ss = (OnhandSummary)request.getSession().getAttribute("summary");
				logger.debug("summary:"+ss);
				h.append("<tr> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td class='colum_head'><b>รวม</b></td> \n");
				  h.append("<td class='currency_bold'>"+ss.getInitSaleQty()+"</td> \n");
				  h.append("<td class='currency_bold'>"+ss.getTransInQty()+"</td> \n");
				  h.append("<td class='currency_bold'>"+ss.getSaleReturnQty()+"</td> \n");
				  h.append("<td class='currency_bold'>"+ss.getSaleOutQty()+"</td> \n");
				  h.append("<td class='currency_bold'>"+ss.getAdjustQty()+"</td> \n");
				  /*h.append("<td class='currency_bold'>"+ss.getStockShortQty()+"</td> \n");*/
				  h.append("<td class='currency_bold'>"+ss.getOnhandQty()+"</td> \n");

				h.append("</tr>");
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genOnhandLotusPeriodHTML(HttpServletRequest request,SummaryForm form,User user,String storeType){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			if("lotus".equalsIgnoreCase(storeType)){
			   h.append("<td align='left' colspan='7'>รายงาน B'me Stock on-hand at Lotus(As Of)</td> \n");
			}else{
			  h.append("<td align='left' colspan='7'>รายงาน B'me Stock on-hand at BigC(As Of) </td> \n");
			}
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='7' >จากวันที่ขาย:"+form.getOnhandSummary().getAsOfDateFrom()+" ถึงวันที่ขาย:"+form.getOnhandSummary().getAsOfDateTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='7' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='7' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='7' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getOnhandSummaryLotusResults() != null){
			    List<OnhandSummary> list = (List<OnhandSummary>)form.getOnhandSummaryLotusResults();
			    
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>รหัสสาขา</td> \n");
				  h.append("<td>ชื่อสาขา</td> \n");
				  h.append("<td>Group</td> \n");
				  h.append("<td>Sale In Qty</td> \n");
				  h.append("<td>Sale Return Qty </td> \n");
				  h.append("<td>Sales Out Qty </td> \n");
				  h.append("<td>Onhand Qty </td> \n");
				 
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>&nbsp;"+s.getStoreCode()+"</td> \n");
					  h.append("<td>&nbsp;"+s.getStoreName()+"</td> \n");
					  h.append("<td>"+s.getGroup()+"</td> \n");
					  h.append("<td>"+s.getSaleInQty()+"</td> \n");
					  h.append("<td>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td>"+s.getOnhandQty()+"</td> \n");
					  
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genPhysicalHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='7'>รายงานข้อมูลการตรวจนับสต๊อก</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='7' >จาก วันที่นับสต็อก:"+form.getPhysicalSummary().getCountDateFrom()+"  ถึง วันที่นับสต็อก:"+form.getPhysicalSummary().getCountDateTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='7' >จาก รหัสร้านค้า:"+form.getPhysicalSummary().getPensCustCodeFrom()+"  ถึง รหัสร้านค้า:"+form.getPhysicalSummary().getPensCustCodeTo()+"</td> \n");
			h.append("</tr> \n");

			h.append("<tr> \n");
			h.append("<td align='left' colspan='7' >File Name:"+form.getPhysicalSummary().getFileName()+"</td>\n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getPhysicalSummaryResults() != null){
			    List<PhysicalSummary> list = (List<PhysicalSummary>)form.getPhysicalSummaryResults();
			    
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>Item</td> \n");
				  h.append("<td>Barcode</td> \n");
				  h.append("<td>Cust Code</td> \n");
				  h.append("<td>Cust Name</td> \n");
				  h.append("<td>Count Date </td> \n");
				  h.append("<td>File Name </td> \n");
				  h.append("<td>Create Date</td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					PhysicalSummary s = (PhysicalSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>&nbsp;"+s.getItem()+"</td> \n");
					  h.append("<td>&nbsp;"+s.getBarcode()+" </td> \n");
					  h.append("<td>"+s.getPensCustCode()+"</td> \n");
					  h.append("<td>"+s.getPensCustName()+"</td> \n");
					  h.append("<td>"+s.getCountDate()+"</td> \n");
					  h.append("<td>"+s.getFileName()+"</td> \n");
					  h.append("<td>"+s.getCreateDate()+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genDiffStockHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='8'>รายงานข้อมูล  Different Stock B'ME</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='8' > รหัสร้านค้า:"+form.getDiffStockSummary().getPensCustCodeFrom()+"-"+form.getDiffStockSummary().getPensCustNameFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='8' > As Of Date:"+form.getDiffStockSummary().getAsOfDate()+"</td> \n");
			h.append("</tr> \n");

			h.append("<tr> \n");
			h.append("<td align='left' colspan='8' >"+("true".equalsIgnoreCase(form.getDiffStockSummary().getHaveQty())?"Have Qty only":"")+"</td>\n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getDiffStockSummaryLists() != null){
			    List<DiffStockSummary> list = (List<DiffStockSummary>)form.getDiffStockSummaryLists();
			    
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>รหัสร้านค้า</td> \n");
				  h.append("<td>Item</td> \n");
				  h.append("<td>Description</td> \n");
				  h.append("<td>Order Consign</td> \n");
				  h.append("<td>Order From Lotus</td> \n");
				  h.append("<td>Data From Physical</td> \n");
				  h.append("<td>Adjust</td> \n");
				  h.append("<td>Diff</td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					DiffStockSummary s = (DiffStockSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>"+s.getCustCode()+"</td> \n");
					  h.append("<td>&nbsp;"+s.getItem()+"</td> \n");
					  h.append("<td>"+s.getDescription()+"</td> \n");
					  h.append("<td>"+s.getOrderConsign()+"</td> \n");
					  h.append("<td>"+s.getOrderFromLotus()+"</td> \n");
					  h.append("<td>"+s.getDataFromPhysical()+"</td> \n");
					  h.append("<td>"+s.getAdjust()+"</td> \n");
					  h.append("<td>"+s.getDiff()+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genLotusHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		TransactionSummary sum = new TransactionSummary();
		//hide column case Role WACOAl
		boolean isRoleWacoal = false;
		if ( Utils.userInRole(user,new String[]{User.WACOAL}) ){
			isRoleWacoal = true;
		}
		String colSpan="33";
		try{
			if(isRoleWacoal){
				colSpan="22";
			}
			if(request.getSession().getAttribute("summaryTrans") != null){
				sum = (TransactionSummary) request.getSession().getAttribute("summaryTrans");
			}
			h.append(ExcelHeader.EXCEL_HEADER);
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'>รายงานข้อมูลรายละเอียดขาย B'ME จาก LOTUS</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >จากวันที่ขาย:"+form.getTransactionSummary().getSalesDateFrom()+"  ถึง วันที่ขาย:"+form.getTransactionSummary().getSalesDateTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >จากรหัสร้านค้า:"+Utils.isNull(form.getTransactionSummary().getPensCustCodeFrom())+"  ถึงรหัสร้านค้า:"+Utils.isNull(form.getTransactionSummary().getPensCustCodeTo())+"</td> \n");
			h.append("</tr> \n");

			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >File Name:"+form.getTransactionSummary().getFileName()+"</td>\n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getResultsTrans() != null){
			    List<TransactionSummary> list = (List<TransactionSummary>)form.getResultsTrans();
			    
				h.append("<table border='1'> \n");
				h.append("<thead>\n");
				h.append("<tr> \n");
					h.append("<th>Sales Date</th>\n");
					h.append("<th>Pens Cust Code</th>\n");
					h.append("<th>Pens Cust Desc</th> \n");
					h.append("<th>Store No</th> \n");
					h.append("<th>Store Name </th> \n");
					h.append("<th>STYLE NO</th>  \n");
					h.append("<th>DESCRIPTION</th> \n");
					h.append("<th>QTY</th> \n");
					if( !isRoleWacoal) {
					h.append("<th>Pens Group</th>\n");
					}
					h.append("<th>Pens Group Type</th> \n");
					h.append("<th>Sales Year</th>\n");
					h.append("<th>Sales Month</th>  \n");
					h.append("<th>file Name</th>\n");
					h.append("<th>Vendor</th> \n");
					h.append("<th>Name</th>\n");
					h.append("<th>AP Type</th> \n");
					h.append("<th>LEASE VENDOR TYPE</th>\n");
					h.append("<th>COL</th> \n");
					h.append("<th>Size Type</th> \n");
					h.append("<th>SIZE</th> \n");
					if( !isRoleWacoal) {
					h.append("<th>GROSS SALES</th> \n");
					h.append("<th>RETURN AMT</th>\n");
					}
					h.append("<th>NET SALES INCL VAT</th> \n");
					if( !isRoleWacoal) {
					h.append("<th>VAT AMT</th> \n");
					h.append("<th>NET SALES EXC VAT</th> \n");
					h.append("<th>GP AMOUNT</th>  \n");
					h.append("<th>VAT ON GP AMOUNT</th> \n");
					h.append("<th>GP AMOUNT INCL VAT</th> \n");
					h.append("<th>AP AMOUNT</th> \n");
					h.append("<th>TOTAL VAT AMT</th> \n");
					h.append("<th>AP AMOUNT INCL VAT</th> \n");
					}
					h.append("<th>Create date</th>  \n");
					h.append("<th>Create by</th>\n");
				h.append("</tr> \n");
				h.append("</thead> \n");
				
				for(int i=0;i<list.size();i++){
					TransactionSummary s = (TransactionSummary)list.get(i);
					h.append("<tr> \n");
						h.append("<td>"+Utils.isNull(s.getSalesDate())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getPensCustCode())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getPensCustDesc())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getStoreNo())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getStoreName())+" </td> \n");
						h.append("<td>"+Utils.isNull(s.getStyleNo())+"</td>  \n");
						h.append("<td>"+Utils.isNull(s.getDescription())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getQty())+"</td> \n");
						if( !isRoleWacoal) {
						h.append("<td>"+Utils.isNull(s.getPensGroup())+"</td>\n");
						}
						h.append("<td>"+Utils.isNull(s.getPensGroupType())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getSalesYear())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getSalesMonth())+"</td>  \n");
						h.append("<td>"+Utils.isNull(s.getFileName())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getVendor())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getName())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getApType())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getLeaseVendorType())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getCol())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getSizeType())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getSizes())+"</td> \n");
						if( !isRoleWacoal) {
						h.append("<td>"+Utils.isNull(s.getGrossSales())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getReturnAmt())+"</td>\n");
						}
						h.append("<td>"+Utils.isNull(s.getNetSalesInclVat())+"</td> \n");
						if( !isRoleWacoal) {
						h.append("<td>"+Utils.isNull(s.getVatAmt())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getNetSalesExcVat())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getGpAmount())+"</td>  \n");
						h.append("<td>"+Utils.isNull(s.getVatOnGpAmount())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getGpAmountInclVat())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getApAmount())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getTotalVatAmt())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getApAmountInclVat())+"</td> \n");
						}
						h.append("<td>"+Utils.isNull(s.getCreateDate())+"</td>  \n");
						h.append("<td>"+Utils.isNull(s.getCreateUser())+"</td>\n");
					h.append("</tr>");
				}
				h.append("<tr> \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td> \n");
				h.append("<td></td>  \n");
				h.append("<td>Total</td> \n");
				h.append("<td class='num_bold'>"+sum.getQty()+"</td> \n");
				if( !isRoleWacoal) {
				h.append("<td></td>  \n");
				}
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				if( !isRoleWacoal) {
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				}
				h.append("<td></td>  \n");
				if( !isRoleWacoal) {
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
			    }
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
			    h.append("</tr>");
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genTopsHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		TransactionSummary sum = new TransactionSummary();
		try{
			if(request.getSession().getAttribute("summaryTrans") != null){
				sum = (TransactionSummary) request.getSession().getAttribute("summaryTrans");
			}
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='31'>รายงานข้อมูลรายละเอียดขาย B'ME จาก  Tops</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='31' >จากวันที่ขาย:"+form.getTransactionSummary().getSalesDateFrom()+"  ถึง วันที่ขาย:"+form.getTransactionSummary().getSalesDateTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='31' >รหัสร้านค้า:"+form.getTransactionSummary().getPensCustCodeFrom());
			h.append("</tr> \n");

			h.append("<tr> \n");
			h.append("<td align='left' colspan='31' >File Name:"+form.getTransactionSummary().getFileName()+"</td>\n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getResultsTrans() != null){
			    List<TransactionSummary> list = (List<TransactionSummary>)form.getResultsTrans();
			    
				h.append("<table border='1'> \n");
				h.append("<thead>\n");
				h.append("<tr> \n");
					h.append("<th>Sales Date</th>\n");
					h.append("<th>Pens Cust Code</th>\n");
					h.append("<th>Pens Cust Desc</th> \n");
					h.append("<th>Pens Group</th>\n");
					h.append("<th>Pens Group Type</th> \n");
					h.append("<th>Pens Item</th> \n");
					h.append("<th>QTY</th> \n");
					h.append("<th>Item</th> \n");
					h.append("<th>Item Desc</th> \n");
					h.append("<th>Branch Name</th> \n");
					h.append("<th>Group No</th> \n");
					h.append("<th>Group Name</th> \n");
					h.append("<th>DEPT</th> \n");
					h.append("<th>Dept Name</th> \n");
					h.append("<th>Unit Cost</th> \n");
					h.append("<th>Retail Price</th> \n");
					h.append("<th>GP PERCENT</th> \n");
					h.append("<th>NET SALES INCL VA </th> \n");
					h.append("<th>NET SALES EXC VAT </th> \n");
					h.append("<th>GP AMOUNT </th> \n");
					h.append("<th>GROSS SALES </th> \n");
					h.append("<th>Discount </th> \n");
					h.append("<th>CUS RETURN </th> \n");
					h.append("<th>DISCOUNT CUS RETURN </th> \n");
					h.append("<th>NET CUS RETURN </th> \n");
					h.append("<th>COGS </th> \n");
					h.append("<th>Sales Year</th>\n");
					h.append("<th>Sales Month</th>  \n");
					h.append("<th>file Name</th>\n");
					h.append("<th>Create date</th>  \n");
					h.append("<th>Create by</th>\n");
				
				h.append("</tr> \n");
				h.append("</thead> \n");
				
				for(int i=0;i<list.size();i++){
					TransactionSummary s = (TransactionSummary)list.get(i);
					h.append("<tr> \n");
						h.append("<td>"+Utils.isNull(s.getSalesDate())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getPensCustCode())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getPensCustDesc())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getPensGroup())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getPensGroupType())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getPensItem())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getQty())+"</td> \n");
						
						h.append("<td>"+Utils.isNull(s.getItem())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getItemDesc())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getBranchName())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getGroupNo())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getGroupName())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getDept())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getDeptName())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getUnitCost())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getRetailPrice())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getGpPercent())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getNetSalesInclVat())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getNetSalesExcVat())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getGpAmount())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getGrossSales())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getDiscount())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getCusReturn())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getDiscountCusReturn())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getNetCusReturn())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getCogs())+"</td> \n");
						
						h.append("<td>"+Utils.isNull(s.getSalesYear())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getSalesMonth())+"</td>  \n");
						h.append("<td>"+Utils.isNull(s.getFileName())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getCreateDate())+"</td>  \n");
						h.append("<td>"+Utils.isNull(s.getCreateUser())+"</td>\n");
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	//wait
	public StringBuffer genKingHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		TransactionSummary sum = new TransactionSummary();
		try{
			if(request.getSession().getAttribute("summaryTrans") != null){
				sum = (TransactionSummary) request.getSession().getAttribute("summaryTrans");
			}
			//Header
			h.append(ExcelHeader.EXCEL_HEADER);
			
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='17'>รายงานข้อมูลรายละเอียดขาย B'ME จาก  King Power</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='17' >จากวันที่ขาย:"+form.getTransactionSummary().getSalesDateFrom()+"  ถึง วันที่ขาย:"+form.getTransactionSummary().getSalesDateTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='17' >รหัสร้านค้า:"+form.getTransactionSummary().getPensCustCodeFrom());
			h.append("</tr> \n");

			h.append("<tr> \n");
			h.append("<td align='left' colspan='17' >File Name:"+form.getTransactionSummary().getFileName()+"</td>\n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getResultsTrans() != null){
			    List<TransactionSummary> list = (List<TransactionSummary>)form.getResultsTrans();
			    
				h.append("<table border='1'> \n");
				h.append("<thead>\n");
				h.append("<tr> \n");
					h.append("<th>Sales Date</th>\n");
					h.append("<th>Cust Group</th>\n");
					h.append("<th>Cust No</th> \n");
					h.append("<th>Cust Name</th>\n");
					h.append("<th>Code</th> \n");
					h.append("<th>Description/th> \n");
					h.append("<th>Reference</th> \n");
					h.append("<th>Unit Price</th> \n");
					h.append("<th>Unit Cost</th> \n");
					h.append("<th>QTY</th> \n");
					h.append("<th>Amount</th> \n");
					h.append("<th>Cost Amount</th> \n");
					h.append("<th>Pens Item</th> \n");
					h.append("<th>Group Code</th> \n");
					h.append("<th>File Name</th> \n");
					h.append("<th>Create date</th> \n");
					h.append("<th>Create by</th> \n");
				h.append("</tr> \n");
				h.append("</thead> \n");
				
				for(int i=0;i<list.size();i++){
					TransactionSummary s = (TransactionSummary)list.get(i);
					h.append("<tr> \n");
						h.append("<td>"+Utils.isNull(s.getSalesDate())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getCustGroup())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getStoreNo())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getStoreName())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getKingCode())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getKingDescription())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getKingReference())+"</td> \n");
						
						h.append("<td>"+Utils.isNull(s.getKingUnitPrice())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getKingUnitCost())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getQty())+"</td> \n");
						
						h.append("<td>"+Utils.isNull(s.getKingAmount())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getKingCostAmt())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getPensItem())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getGroupCode())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getFileName())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getCreateDate())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getCreateUser())+"</td> \n");
						
					h.append("</tr>");
				}
				h.append("<tr> \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td> \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td><b>Total</b></td> \n");
				h.append("<td class='num_bold'>"+sum.getQty()+"</td> \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				h.append("<td></td>  \n");
				
			h.append("</tr>");
				h.append("</table> \n");
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public StringBuffer genBigCHTML(HttpServletRequest request,SummaryForm form,User user,String reportType){
		StringBuffer h = new StringBuffer("");
		boolean isRoleWacoal = false;
		if ( Utils.userInRole(user,new String[]{User.WACOAL}) ){
			isRoleWacoal = true;
		}
		String colSpan="22";
		try{
			if(isRoleWacoal){
				colSpan="19";
			}
			//css Excel
			h.append(ExcelHeader.EXCEL_HEADER);
			
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'>รายงานข้อมูลรายละเอียดขาย B'ME จาก BIGC "+reportType+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >จากวันที่ขาย:"+form.getTransactionSummary().getSalesDateFrom()+"  ถึง วันที่ขาย:"+form.getTransactionSummary().getSalesDateTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >รหัสร้านค้า:"+form.getTransactionSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");

			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >File Name:"+form.getTransactionSummary().getFileName()+"</td>\n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getResultsTrans() != null){
			    List<TransactionSummary> list = (List<TransactionSummary>)form.getResultsTrans();
			    
				h.append("<table border='1'> \n");
				h.append("<thead>\n");
				h.append("<tr> \n");
					h.append("<th>Sales Date</th>\n");
					h.append("<th>Pens Cust Code</th>\n");
					h.append("<th>Pens Cust Desc</th> \n");
					h.append("<th>Store No</th> \n");
					h.append("<th>Store Name </th> \n");
					h.append("<th>STYLE NO</th>  \n");
					h.append("<th>DESCRIPTION</th> \n");
					h.append("<th>QTY</th> \n");
					if( !isRoleWacoal) {
					h.append("<th>WHOLE PRICE BF</th> \n");
					}
					h.append("<th>Retail PRICE BF</th> \n");
					if( !isRoleWacoal) {
					h.append("<th>TOTAL WHOLE PRICE BF</th> \n");
					}
					h.append("<th>Pens Group</th>\n");
					h.append("<th>Pens Group Type</th> \n");
					h.append("<th>Pens Item</th> \n");
					h.append("<th>Sales Year</th>\n");
					h.append("<th>Sales Month</th>  \n");
					h.append("<th>file Name</th>\n");
					h.append("<th>Vendor</th> \n");
					h.append("<th>Name</th>\n");
					if( !isRoleWacoal) {
					h.append("<th>GP PERCENT</th>  \n");
					}
					h.append("<th>Create date</th>  \n");
					h.append("<th>Create by</th>\n");
				h.append("</tr> \n");
				h.append("</thead> \n");
				
				for(int i=0;i<list.size();i++){
					TransactionSummary s = (TransactionSummary)list.get(i);
					h.append("<tr> \n");
						h.append("<td class='text'>"+Utils.isNull(s.getSalesDate())+"</td>\n");
						h.append("<td class='text'>"+Utils.isNull(s.getPensCustCode())+"</td>\n");
						h.append("<td class='text'>"+Utils.isNull(s.getPensCustDesc())+"</td> \n");
						h.append("<td class='text'>"+Utils.isNull(s.getStoreNo())+"</td> \n");
						h.append("<td class='text'>"+Utils.isNull(s.getStoreName())+" </td> \n");
						h.append("<td class='text'>"+Utils.isNull(s.getStyleNo())+"</td>  \n");
						h.append("<td class='text'>"+Utils.isNull(s.getDescription())+"</td> \n");
						h.append("<td class='currency'>"+Utils.isNull(s.getQty())+"</td> \n");
						if( !isRoleWacoal) {
						h.append("<td class='currency'>"+Utils.isNull(s.getWholePriceBF())+"</td> \n");
						}
						h.append("<td class='currency'>"+Utils.isNull(s.getRetailPriceBF())+"</td> \n");
						if( !isRoleWacoal) {
						h.append("<td class='currency'>"+Utils.isNull(s.getTotalWholePriceBF())+"</td> \n");
						}
						h.append("<td class='text'>"+Utils.isNull(s.getPensGroup())+"</td>\n");
						h.append("<td class='text'>"+Utils.isNull(s.getPensGroupType())+"</td> \n");
						h.append("<td class='text'>"+Utils.isNull(s.getPensItem())+"</td> \n");
						h.append("<td class='text'>"+Utils.isNull(s.getSalesYear())+"</td>\n");
						h.append("<td class='text'>"+Utils.isNull(s.getSalesMonth())+"</td>  \n");
						h.append("<td class='text'>"+Utils.isNull(s.getFileName())+"</td>\n");
						h.append("<td class='text'>"+Utils.isNull(s.getVendor())+"</td> \n");
						h.append("<td class='text'>"+Utils.isNull(s.getName())+"</td>\n");
						if( !isRoleWacoal) {
						h.append("<td class='currency'>"+Utils.isNull(s.getGpPercent())+"</td> \n");
						}
						h.append("<td class='text'>"+Utils.isNull(s.getCreateDate())+"</td>  \n");
						h.append("<td class='text'>"+Utils.isNull(s.getCreateUser())+"</td>\n");
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}

}
