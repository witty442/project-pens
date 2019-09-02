package com.isecinc.pens.web.salestarget;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import util.ExcelHeader;
import util.Utils;

import com.isecinc.pens.bean.User;

public class SalesTargetExport {
	protected static Logger logger = Logger.getLogger("PENS");

	public static StringBuffer genResultSearchTargetHeadByMKT(HttpServletRequest request,SalesTargetBean o,User user) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className = "";
		String action ="";
		char singleQuote ='"';
		try{
			logger.debug("status test:"+o.getStatus());
			
			if(o.getItems() != null && o.getItems().size() >0){
				
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
			h.append("<tr> \n");
			h.append("<th >พนักงานขาย </th> \n");
			h.append("<th >รหัสร้านค้า</th> \n");
			h.append("<th >ชื่อร้านค้า</th> \n");
			h.append("<th >เป้าหมายขาย (หีบ)</th> \n");
			h.append("<th >เป้าหมายขาย (บาท)</th> \n");
			h.append("<th >สถานะ</th> \n");
			h.append("<th >Set</th>	 \n");		
			if(Utils.isNull(o.getStatus()).equals(SalesTargetConstants.STATUS_FINISH)){
				h.append("<th >ขายได้(หีบ)</th>	 \n");	
				h.append("<th >ขายได้(บาท)</th> \n");	
				h.append("<th >คาดปิด(หีบ)</th>	 \n");	
				h.append("<th >ราคา</th> \n");
				h.append("<th >คาดปิด(บาท)</th> \n");	
			}
			h.append("</tr> \n");
		    for(int i=0;i<o.getItems().size();i++){
		    	SalesTargetBean item = o.getItems().get(i);
		    	className = (i %2 == 0)?"lineE":"lineO";
				h.append("<tr class='"+className+"'> \n");
				    h.append("<td class='td_text_center' width='6%'>"+item.getSalesrepCode()+"</td> \n");
					h.append("<td class='td_text_center' width='6%'>"+item.getCustomerCode()+"</td> \n");
					h.append("<td class='td_text_center' width='15%'>"+item.getCustomerName()+"</td> \n");
					h.append("<td class='td_number' width='10%'>"+item.getTargetQty()+"</td> \n");
					h.append("<td class='td_number' width='10%'>"+item.getTargetAmount()+"</td> \n");
					h.append("<td class='td_text_center' width='6%'>"+item.getStatus()+"</td> \n");
					h.append("<td class='td_text_center' width='10%'> \n");
					
					action = item.isCanSet()?"Set":"View";
					
					h.append(" <a href="+singleQuote+"javascript:openEdit('"+request.getContextPath()+"'  \n");
					h.append("  ,'"+item.getSalesrepCode()+"','"+item.getCustomerId()+"' \n");
					h.append("  ,'"+item.getCustomerCode()+"','"+item.getCustomerName()+"' \n");
					h.append("  ,'"+item.getSalesrepId()+"','"+item.getSalesChannelNo()+"' ,'"+action+"')"+singleQuote+" > \n");
					h.append("   "+action);
					h.append("</a> \n");
					h.append("</td> \n");
					if(Utils.isNull(o.getStatus()).equals(SalesTargetConstants.STATUS_FINISH)){
						h.append("<td class='td_text_center' width='8%'>"+Utils.isNull(item.getInvoicedQty())+"</td> \n");
						h.append("<td class='td_text_center' width='8%'>"+Utils.isNull(item.getInvoicedAmt())+"</td> \n");
						h.append("<td class='td_text_center' width='6%'>"+Utils.isNull(item.getEstimateQty())+"</td> \n");
						if( !Utils.isNull(item.getEstimateAmt()).equals("")){
						   h.append("<td class='td_text_center' width='6%'>"+Utils.isNull(item.getPrice())+"</td> \n");
						}else{
						   h.append("<td class='td_text_center' width='6%'></td> \n");
						}
						h.append("<td class='td_text_center' width='8%'>"+Utils.isNull(item.getEstimateAmt())+"</td> \n");
					}
				h.append("</tr> \n");
		    }
			h.append("<tr class='hilight_text'> \n");
				h.append("<td class=''></td>  \n");
				h.append("<td class=''></td>  \n");
				h.append("<td class='' align='right'> \n");
				h.append("  <B> Total By Brand </B> \n");
				h.append("</td> \n");
				h.append("<td class='td_number_bold' align='right'> \n");
				h.append("  <B>  "+o.getTotalTargetQty()+" </B> \n");
				h.append("</td> \n");
				h.append("<td class='td_number_bold' align='right'> \n");
				h.append("  <B> "+o.getTotalTargetAmount()+"</B> \n");
				h.append("</td> \n");
				h.append("<td class=''></td> \n");
				h.append("<td class=''></td> \n");
				if(Utils.isNull(o.getStatus()).equals(SalesTargetConstants.STATUS_FINISH)){
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
				}
			h.append("</tr> \n");
			h.append("</table> \n");
			}
		   return h;
		}catch(Exception e){
			throw e;
		}finally{
		}
	}
	
	/**
	 * Role MT(Sales)
	 * @param request
	 * @param o
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static StringBuffer genResultSearchTargetHeadByMT(HttpServletRequest request,SalesTargetBean o,User user) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className = "";
		String action ="";
		char singleQuote ='"';
		String nextCustomerTemp= "";
		double totalQty = 0;
		double totalAmount = 0;
		double grandTotalQty = 0;
		double grandTotalAmount = 0;
		int n = 0;
		int rowId = 0;
		try{
			if(o.getItems() != null && o.getItems().size() >0){
				
				//logger.debug("status:"+o.getStatus());
				
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
			h.append("<tr> \n");
			h.append("<th >รหัสร้านค้า</th> \n");
			h.append("<th >ชื่อร้านค้า</th> \n");
			h.append("<th >แบรนด์</th> \n");
			h.append("<th >ชื่อแบรนด์</th> \n");
			h.append("<th >เป้าหมายขาย (หีบ)</th> \n");
			h.append("<th >เป้าหมายขาย (บาท)</th> \n");
			h.append("<th >สถานะ</th> \n");
			h.append("<th >ดูรายละเอียด</th>	\n");	
			if(Utils.isNull(o.getStatus()).equals(SalesTargetConstants.STATUS_FINISH)){
				h.append("<th >ขายได้(หีบ)</th>	 \n");	
				h.append("<th >ขายได้(บาท)</th> \n");	
				h.append("<th >คาดปิด(หีบ)</th>	 \n");	
				h.append("<th >ราคา</th> \n");
				h.append("<th >คาดปิด(บาท)</th> \n");		
			}
			h.append("</tr> \n");
		    for(int i=0;i<o.getItems().size();i++){
		    	rowId++;
		    	SalesTargetBean item = o.getItems().get(i);
		    	className = (i %2 == 0)?"lineE":"lineO";
				h.append("<tr class='"+className+"'> \n");
					h.append("<td class='td_text_center' width='8%'>"+item.getCustomerCode()+"</td> \n");
					h.append("<td class='td_text_center' width='15%'>"+item.getCustomerName()+"</td> \n");
				    h.append("<td class='td_text_center' width='5%'>"+item.getBrand()+"</td> \n");
				    h.append("<td class='td_text_center' width='10%'>"+item.getBrandName()+"</td> \n");
					h.append("<td class='td_number' width='5%'>"+item.getTargetQty()+"</td> \n");
					h.append("<td class='td_number' width='5%'>"+item.getTargetAmount()+"</td> \n");
					h.append("<td class='td_text_center' width='8%'>"+item.getStatus()+"</td> \n");
					h.append("<td class='td_text_center' width='8%'> \n");
						action = item.isCanSet()?"View":"View";
						h.append(" <a href="+singleQuote+"javascript:openView('"+request.getContextPath()+"'  \n");
						h.append("  , "+item.getId()+" \n");
						h.append("  ,'"+item.getSalesrepCode()+"','"+item.getCustomerId()+"' \n");
						h.append("  ,'"+item.getCustomerCode()+"','"+item.getCustomerName()+"' \n");
						h.append("  ,'"+item.getSalesrepId()+"','"+item.getSalesChannelNo()+"' ,'"+action+"')"+singleQuote+" > \n");
						h.append("   "+action);
						h.append("</a> \n");
					h.append("</td> \n");
					
					if(Utils.isNull(o.getStatus()).equals(SalesTargetConstants.STATUS_FINISH)){
						h.append("<td class='td_text_center' width='8%'>"+item.getInvoicedQty()+" \n");
						h.append("  <input type='hidden' id ='invoicedQty' name='invoicedQty' value ='"+item.getInvoicedQty()+"' /> \n");
						h.append("  <input type='hidden' id ='invoicedAmt' name='invoicedAmt' value ='"+item.getInvoicedAmt()+"' /> \n");
						h.append("</td> \n");
						h.append("<td class='td_text_center' width='8%'>"+item.getInvoicedAmt()+"</td> \n");
						
						h.append("<td class='td_text_center' width='8%'> \n");
						h.append("  <input type='text' name='estimateQty' id='estimateQty' class='enableNumber'");
						h.append("    value ='"+item.getEstimateQty()+"' size='4' \n");
						h.append("    onblur='isNumPositive(this);calcEstimateAmt(this,"+rowId+")' autocomplete='off' />");
						h.append("  <input type='hidden' id ='ids' name='ids' value ='"+item.getId()+"' /> \n");
						h.append("</td> \n");
						h.append("<td class='td_text_center' width='8%'> \n");
						h.append("  <input type='text' name='price' id='price' class='enableNumber'");
						h.append("    value ='"+item.getPrice()+"' size='4' \n");
						h.append("    onblur='isNumPositive(this);calcEstimateAmt(this,"+rowId+")' autocomplete='off' />");
						h.append("</td> \n");
						
						h.append("<td class='td_text_center' width='8%'> \n");
						h.append("  <input type='text' name='estimateAmt' id='estimateAmt' class='disableNumber' readonly value ='"+item.getEstimateAmt()+"' size='10'/>");
						h.append("</td> \n");
					}
				h.append("</tr> \n");
				
				
				/** sum Total **/
				totalQty += Utils.convertStrToDouble(item.getTargetQty());
				totalAmount += Utils.convertStrToDouble(item.getTargetAmount());
				grandTotalQty += Utils.convertStrToDouble(item.getTargetQty());
				grandTotalAmount += Utils.convertStrToDouble(item.getTargetAmount());
				
				/** Gen Total Summary By Customer Code*/
				n = i+1;
				nextCustomerTemp = "";
				if(n<o.getItems().size()){
				   nextCustomerTemp = o.getItems().get(n).getCustomerCode();
				}
				if( (!nextCustomerTemp.equalsIgnoreCase(item.getCustomerCode()))){
					h.append("<tr class='hilight_text'> \n");
						h.append("<td class=''></td>  \n");
						h.append("<td class=''></td>  \n");
						h.append("<td class=''></td>  \n");
						h.append("<td class='' align='right'> \n");
						h.append("  <B> Total</B> \n");
						h.append("</td> \n");
						h.append("<td class='td_number_bold' align='right'> \n");
						h.append("  <B>  "+Utils.decimalFormat(totalQty, Utils.format_current_no_disgit)+" </B> \n");
						h.append("</td> \n");
						h.append("<td class='td_number_bold' align='right'> \n");
						h.append("  <B> "+Utils.decimalFormat(totalAmount, Utils.format_current_2_disgit)+"</B> \n");
						h.append("</td> \n");
						h.append("<td class=''></td> \n");
						h.append("<td class=''></td> \n");
						if(Utils.isNull(o.getStatus()).equals(SalesTargetConstants.STATUS_FINISH)){
							h.append("<td class=''></td> \n");
							h.append("<td class=''></td> \n");
							h.append("<td class=''></td> \n");
							h.append("<td class=''></td> \n");
							h.append("<td class=''></td> \n");
						}
				    h.append("</tr> \n");
				    //reset Total Summary
				    totalQty =0;
					totalAmount = 0;
				}
		    }
			h.append("<tr class='hilight_text'> \n");
				h.append("<td class=''></td>  \n");
				h.append("<td class=''></td>  \n");
				h.append("<td class=''></td>  \n");
				h.append("<td class='' align='right'> \n");
				h.append("  <B> Grand Total</B> \n");
				h.append("</td> \n");
				h.append("<td class='td_number_bold' align='right'> \n");
				h.append("  <B> "+Utils.decimalFormat(grandTotalQty, Utils.format_current_no_disgit)+" </B> \n");
				h.append("</td> \n");
				h.append("<td class='td_number_bold' align='right'> \n");
				h.append("  <B> "+Utils.decimalFormat(grandTotalAmount, Utils.format_current_2_disgit)+"</B> \n");
				h.append("</td> \n");
				h.append("<td class=''></td> \n");
				h.append("<td class=''></td> \n");
				if(Utils.isNull(o.getStatus()).equals(SalesTargetConstants.STATUS_FINISH)){
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
				}
			h.append("</tr> \n");
			h.append("</table> \n");
			}
		   return h;
		}catch(Exception e){
			throw e;
		}finally{
		}
	}
	
	/**
	 * Role MT(Sales)
	 * @param request
	 * @param o
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static StringBuffer genResultSearchTargetHeadExcelByMT(HttpServletRequest request,SalesTargetBean o,User user) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className = "";
		String nextCustomerTemp= "";
		double totalQty = 0;
		double totalAmount = 0;
		double grandTotalQty = 0;
		double grandTotalAmount = 0;
		int n = 0;
		int rowId = 0;
		try{
			if(o.getItems() != null && o.getItems().size() >0){
				
				//logger.debug("status:"+o.getStatus());
				
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
			h.append("<tr> \n");
			h.append("<th >รหัสร้านค้า</th> \n");
			h.append("<th >ชื่อร้านค้า</th> \n");
			h.append("<th >แบรนด์</th> \n");
			h.append("<th >ชื่อแบรนด์</th> \n");
			h.append("<th >เป้าหมายขาย (หีบ)</th> \n");
			h.append("<th >เป้าหมายขาย (บาท)</th> \n");
			h.append("<th >สถานะ</th> \n");
			if(Utils.isNull(o.getStatus()).equals(SalesTargetConstants.STATUS_FINISH)){
				h.append("<th >ขายได้(หีบ)</th>	 \n");	
				h.append("<th >ขายได้(บาท)</th> \n");	
				h.append("<th >คาดปิด(หีบ)</th>	 \n");	
				h.append("<th >ราคา</th> \n");
				h.append("<th >คาดปิด(บาท)</th> \n");		
			}
			h.append("</tr> \n");
		    for(int i=0;i<o.getItems().size();i++){
		    	rowId++;
		    	SalesTargetBean item = o.getItems().get(i);
		    	className = (i %2 == 0)?"lineE":"lineO";
				h.append("<tr class='"+className+"'> \n");
					h.append("<td class='td_text_center' width='8%'>"+item.getCustomerCode()+"</td> \n");
					h.append("<td class='td_text_center' width='15%'>"+item.getCustomerName()+"</td> \n");
				    h.append("<td class='td_text_center' width='5%'>"+item.getBrand()+"</td> \n");
				    h.append("<td class='td_text_center' width='10%'>"+item.getBrandName()+"</td> \n");
					h.append("<td class='td_number' width='5%'>"+item.getTargetQty()+"</td> \n");
					h.append("<td class='td_number' width='5%'>"+item.getTargetAmount()+"</td> \n");
					h.append("<td class='td_text_center' width='8%'>"+item.getStatus()+"</td> \n");
					
					if(Utils.isNull(o.getStatus()).equals(SalesTargetConstants.STATUS_FINISH)){
						h.append("<td class='td_text_center' width='8%'>"+item.getInvoicedQty()+" \n");
						h.append("<td class='td_text_center' width='8%'>"+item.getInvoicedAmt()+"</td> \n");
						h.append("<td class='td_text_center' width='8%'>"+item.getEstimateQty()+"</td> \n");
						h.append("<td class='td_text_center' width='8%'>"+item.getPrice()+" </td> \n");
						h.append("<td class='td_text_center' width='8%'> "+item.getEstimateAmt()+"</td> \n");
					}
				h.append("</tr> \n");
				
				
				/** sum Total **/
				totalQty += Utils.convertStrToDouble(item.getTargetQty());
				totalAmount += Utils.convertStrToDouble(item.getTargetAmount());
				grandTotalQty += Utils.convertStrToDouble(item.getTargetQty());
				grandTotalAmount += Utils.convertStrToDouble(item.getTargetAmount());
				
				/** Gen Total Summary By Customer Code*/
				n = i+1;
				nextCustomerTemp = "";
				if(n<o.getItems().size()){
				   nextCustomerTemp = o.getItems().get(n).getCustomerCode();
				}
				if( (!nextCustomerTemp.equalsIgnoreCase(item.getCustomerCode()))){
					h.append("<tr class='hilight_text'> \n");
						h.append("<td class=''></td>  \n");
						h.append("<td class=''></td>  \n");
						h.append("<td class=''></td>  \n");
						h.append("<td class='' align='right'> \n");
						h.append("  <B> Total</B> \n");
						h.append("</td> \n");
						h.append("<td class='td_number_bold' align='right'> \n");
						h.append("  <B>  "+Utils.decimalFormat(totalQty, Utils.format_current_no_disgit)+" </B> \n");
						h.append("</td> \n");
						h.append("<td class='td_number_bold' align='right'> \n");
						h.append("  <B> "+Utils.decimalFormat(totalAmount, Utils.format_current_2_disgit)+"</B> \n");
						h.append("</td> \n");
						h.append("<td class=''></td> \n");
						if(Utils.isNull(o.getStatus()).equals(SalesTargetConstants.STATUS_FINISH)){
							h.append("<td class=''></td> \n");
							h.append("<td class=''></td> \n");
							h.append("<td class=''></td> \n");
							h.append("<td class=''></td> \n");
							h.append("<td class=''></td> \n");
						}
				    h.append("</tr> \n");
				    //reset Total Summary
				    totalQty =0;
					totalAmount = 0;
				}
		    }
			h.append("<tr class='hilight_text'> \n");
				h.append("<td class=''></td>  \n");
				h.append("<td class=''></td>  \n");
				h.append("<td class=''></td>  \n");
				h.append("<td class='' align='right'> \n");
				h.append("  <B> Grand Total</B> \n");
				h.append("</td> \n");
				h.append("<td class='td_number_bold' align='right'> \n");
				h.append("  <B> "+Utils.decimalFormat(grandTotalQty, Utils.format_current_no_disgit)+" </B> \n");
				h.append("</td> \n");
				h.append("<td class='td_number_bold' align='right'> \n");
				h.append("  <B> "+Utils.decimalFormat(grandTotalAmount, Utils.format_current_2_disgit)+"</B> \n");
				h.append("</td> \n");
				h.append("<td class=''></td> \n");
				if(Utils.isNull(o.getStatus()).equals(SalesTargetConstants.STATUS_FINISH)){
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
				}
			h.append("</tr> \n");
			h.append("</table> \n");
			}
		   return h;
		}catch(Exception e){
			throw e;
		}finally{
		}
	}
	/**
	 * Role Manager MT(Sales)
	 * @param request
	 * @param o
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static StringBuffer genResultSearchTargetHeadByMTMGR(HttpServletRequest request,SalesTargetBean o,User user) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className = "";
		String action ="";
		char singleQuote ='"';
		double totalQty = 0;
		double totalAmount = 0;
		double grandTotalQty = 0;
		double grandTotalAmount = 0;
		String customerCodeNext = "";
		try{
			if(o.getItems() != null && o.getItems().size() >0){
				
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
			h.append("<tr> \n");
			h.append("<th >รหัสร้านค้า</th> \n");
			h.append("<th >ชื่อร้านค้า</th> \n");
			h.append("<th >แบรนด์</th> \n");
			h.append("<th >ชื่อแบรนด์</th> \n");
			h.append("<th >เป้าหมายขาย (หีบ)</th> \n");
			h.append("<th >เป้าหมายขาย (บาท)</th> \n");
			h.append("<th >สถานะ</th> \n");
			h.append("<th >ดูรายละเอียด</th>	 \n");	
			h.append("<th >อนุมัติ</th>	 \n");	
			h.append("<th >ไม่อนุมัติ</th>	 \n");	
			h.append("</tr> \n");
		    for(int i=0;i<o.getItems().size();i++){
		    	SalesTargetBean item = o.getItems().get(i);
		    	className = (i %2 == 0)?"lineE":"lineO";
				h.append("<tr class='"+className+"'> \n");
				
					h.append("<td class='td_text_center' width='10%'>"+item.getCustomerCode()+"</td> \n");
					h.append("<td class='td_text_center' width='15%'>"+item.getCustomerName()+"</td> \n");
				    h.append("<td class='td_text_center' width='5%'>"+item.getBrand()+"</td> \n");
				    h.append("<td class='td_text_center' width='15%'>"+item.getBrandName()+"</td> \n");
					h.append("<td class='td_number' width='10%'>"+item.getTargetQty()+"</td> \n");
					h.append("<td class='td_number' width='10%'>"+item.getTargetAmount()+"</td> \n");
					h.append("<td class='td_text_center' width='10%'><span name='statusSpan'>"+item.getStatus()+"</span></td> \n");
					h.append("<td class='td_text_center' width='5%'> \n");
					   h.append("<input type='hidden' name='ids' value='"+item.getId()+"'/> \n");
					   h.append("<input type='hidden' name='status' value="+item.getStatus()+" /> \n");
						action = item.isCanSet()?"View":"View";
						h.append(" <a href="+singleQuote+"javascript:openView('"+request.getContextPath()+"',"+item.getId()+")"+singleQuote+" > \n");
						h.append("   "+action);
						h.append("</a> \n");
					h.append("</td> \n");
					h.append("<td class='td_text_center' width='5%'> \n");
					if(item.isCanFinish()){
						h.append("<div name='accept_div' >");
						h.append(" <a href="+singleQuote+"javascript:acceptRow('"+request.getContextPath()+"',"+item.getId()+","+item.getRowId()+")"+singleQuote+" > \n");
						h.append("   อนุมัติ");
						h.append("</a> \n");
						h.append("</div>");
					}
					h.append("</td> \n");
					h.append("<td class='td_text_center' width='10%'> \n");
					if(item.isCanUnAccept()){
						h.append("<div name='unaccept_div' >");
						h.append(" <a href="+singleQuote+"javascript:unacceptRow('"+request.getContextPath()+"',"+item.getId()+","+item.getRowId()+")"+singleQuote+" > \n");
						h.append("   ไม่อนุมัติ");
						h.append("</a> \n");
						h.append("</div>");
					}
					h.append("</td> \n");
				h.append("</tr> \n");
				
				/** sum Total **/
				totalQty += Utils.convertStrToDouble(item.getTargetQty());
				totalAmount += Utils.convertStrToDouble(item.getTargetAmount());
				grandTotalQty += Utils.convertStrToDouble(item.getTargetQty());
				grandTotalAmount += Utils.convertStrToDouble(item.getTargetAmount());
				
				/** Gen Total Summary By Customer Code*/
				if(i==o.getItems().size()-1){
				   customerCodeNext = "GEN_LAST_ROW";
				}else{
					customerCodeNext =o.getItems().get(i+1).getCustomerCode(); 
				}
				
				if(!customerCodeNext.equals(item.getCustomerCode())){
					h.append("<tr class='hilight_text'> \n");
						h.append("<td class=''></td>  \n");
						h.append("<td class=''></td>  \n");
						h.append("<td class=''></td>  \n");
						h.append("<td class='' align='right'> \n");
						h.append("  <B> Total</B> \n");
						h.append("</td> \n");
						h.append("<td class='td_number_bold' align='right'> \n");
						h.append("  <B>  "+Utils.decimalFormat(totalQty, Utils.format_current_no_disgit)+" </B> \n");
						h.append("</td> \n");
						h.append("<td class='td_number_bold' align='right'> \n");
						h.append("  <B> "+Utils.decimalFormat(totalAmount, Utils.format_current_2_disgit)+"</B> \n");
						h.append("</td> \n");
						h.append("<td class=''></td> \n");
						h.append("<td class=''></td> \n");
						h.append("<td class=''></td> \n");
						h.append("<td class=''></td> \n");
				    h.append("</tr> \n");
				    //reset Total Summary
				    totalQty =0;
					totalAmount = 0;
				}//if
		    }//for
		    
		    //Summary Total
			h.append("<tr class='hilight_text'> \n");
				h.append("<td class=''></td>  \n");
				h.append("<td class=''></td>  \n");
				h.append("<td class=''></td>  \n");
				h.append("<td class='' align='right'> \n");
				h.append("  <B> Grand Total</B> \n");
				h.append("</td> \n");
				h.append("<td class='td_number_bold' align='right'> \n");
				h.append("  <B> "+Utils.decimalFormat(grandTotalQty, Utils.format_current_no_disgit)+" </B> \n");
				h.append("</td> \n");
				h.append("<td class='td_number_bold' align='right'> \n");
				h.append("  <B> "+Utils.decimalFormat(grandTotalAmount, Utils.format_current_2_disgit)+"</B> \n");
				h.append("</td> \n");
				h.append("<td class=''></td> \n");
				h.append("<td class=''></td> \n");
				h.append("<td class=''></td> \n");
				h.append("<td class=''></td> \n");
			h.append("</tr> \n");
			h.append("</table> \n");
			}
		   return h;
		}catch(Exception e){
			throw e;
		}finally{
		}
	}
	
	public static StringBuffer genExportExcelDetailByMT(HttpServletRequest request,SalesTargetBean o,User user) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className = "";
		String colspan = "10";
		try{
			if(o.getItems() != null && o.getItems().size() >0){
				
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
			h.append("<tr> \n");
			h.append("<td class='colum_head' colspan="+colspan+">ข้อมูลเป้ายอดขาย</td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td class='colum_head' colspan="+colspan+">เดือน "+o.getPeriod()+"   "+o.getStartDate()+"-"+o.getEndDate()+"   แบรนด์ "+o.getBrand()+"-"+o.getBrandName()+"</td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td class='colum_head' colspan="+colspan+">ภาคการขาย	 "+o.getSalesChannelName()+"   ประเภทขาย "+o.getCustCatNo()+"</td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td class='colum_head' colspan="+colspan+">รหัสร้านค้า	 "+o.getCustomerCode()+"   ชื่อร้านค้า "+o.getCustomerName()+"</td> \n");
			h.append("</tr> \n");
			h.append("</table> \n");
			
			h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
			h.append("<tr> \n");
			h.append("<th >รหัสสินค้า </th> \n");
			h.append("<th >ชื่อสินค้า</th> \n");
			h.append("<th >ยอดขาย-คืน(เฉลี่ย 12 เดือน)</th> \n");
			h.append("<th >ยอดขาย-คืน(เฉลี่ย 3 เดือน)</th> \n");
			h.append("<th >ราคา Last Price</th> \n");
			h.append("<th >เป้าหมาย ขาย(หีบ)</th>	 \n");		
			h.append("<th >ป้าหมาย ขาย(บาท)</th>	 \n");	
			h.append("<th >สถานะ</th>	 \n");	
			h.append("<th >หมายเหตุ</th>	 \n");	
			h.append("<th >เหตุผลที่ทาง Sale ได้ Reject</th>	 \n");	
			h.append("</tr> \n");
		    for(int i=0;i<o.getItems().size();i++){
		    	SalesTargetBean item = o.getItems().get(i);
		    	className = (i %2 == 0)?"lineE":"lineO";
				h.append("<tr class='"+className+"'> \n");
				    h.append("<td class='text' width='10%'>"+item.getItemCode()+"</td> \n");
					h.append("<td class='text' width='10%'>"+item.getItemName()+"</td> \n");
					h.append("<td class='text' width='20%'>"+item.getOrderAmt12Month()+"</td> \n");
					h.append("<td class='text' width='10%'>"+item.getOrderAmt3Month()+"</td> \n");
					h.append("<td class='text' width='10%'>"+item.getPrice()+"</td> \n");
					h.append("<td class='text' width='10%'>"+item.getTargetQty()+"</td> \n");
					h.append("<td class='text' width='10%'>"+item.getTargetAmount()+"</td> \n");
					h.append("<td class='text' width='10%'>"+item.getStatus()+"</td> \n");
					h.append("<td class='text' width='10%'>"+item.getRemark()+"</td> \n");
					h.append("<td class='text' width='10%'>"+item.getRejectReason()+"</td> \n");
				h.append("</tr> \n");
		    }//for
			h.append("</table> \n");
			}
		   return h;
		}catch(Exception e){
			throw e;
		}finally{
		}
	}
	public static StringBuffer genResultSearchTargetHeadByMTADMIN(HttpServletRequest request,SalesTargetBean o,User user) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className = "";
		try{
			if(o.getItems() != null && o.getItems().size() >0){
				
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='1' width='60%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
			h.append("<tr> \n");
			h.append("<th>ร้านค้า</th> \n");
			h.append("<th>ชื่อร้านค้า</th> \n");
			h.append("<th>แบรนด์</th> \n");
			h.append("<th>เป้าหมายขาย (หีบ)</th> \n");
			h.append("<th>เป้าหมายขาย (บาท)</th> \n");
			h.append("<th>สถานะ</th> \n");
			h.append("<th>Change Status To</th>	\n");		
			h.append("</tr> \n");
			
		    for(int i=0;i<o.getItems().size();i++){
		    	SalesTargetBean item = o.getItems().get(i);
		    	className = (i %2 == 0)?"lineE":"lineO";
		    	
				h.append("<tr class='"+className+"'> \n");
				h.append("<td class='td_text' width='10%'>"+item.getCustomerCode()+"</td> \n");
				h.append("<td class='td_text' width='10%'>"+item.getCustomerName()+"</td> \n");
				h.append("<td class='td_text' width='10%'>"+item.getBrand()+"-"+item.getBrandName()+"</td> \n");
				h.append("<td class='td_number' width='10%'>"+item.getTargetQty()+"</td> \n");
				h.append("<td class='td_number' width='10%'>"+item.getTargetAmount()+"</td> \n");
				h.append("<td class='td_text_center' width='10%'> \n");
				h.append(" <input type='text' readonly class='disableText' size='20' name='status' value='"+item.getStatus()+"' /> \n");
				h.append("</td> \n");

				/**** Change Status ***************************************/
				h.append("<td class='td_text_center' width='10%'> \n");
				h.append("<select id='status_change' name='status_change'> \n");
				h.append("<option value=''></option> \n");
				h.append("<option value='Open'>Open</option> \n");
				h.append("<option value='Post'>Post</option> \n");
				h.append("</select> \n");
				/** hidden field **/
				h.append("  <input type='hidden' name='customer_code_change' value='"+item.getBrand()+"'/> \n");
				h.append("  <input type='hidden' name='brand_change' value='"+item.getBrand()+"'/> \n");
				h.append("</td> \n");
				h.append("</tr> \n");
		    }//for
			h.append("</table> \n");
			}
		   return h;
		}catch(Exception e){
			throw e;
		}finally{
		}
	}
}
