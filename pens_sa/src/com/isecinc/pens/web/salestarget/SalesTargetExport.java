package com.isecinc.pens.web.salestarget;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import util.ExcelHeader;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.salesanalyst.helper.Utils;

public class SalesTargetExport {
	protected static Logger logger = Logger.getLogger("PENS");

	public static StringBuffer genResultSearchTargetHeadByMKT(HttpServletRequest request,SalesTargetBean o,User user) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className = "";
		String action ="";
		char singleQuote ='"';
		try{
			if(o.getItems() != null && o.getItems().size() >0){
				
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
			h.append("<tr> \n");
			h.append("<th >��ѡ�ҹ��� </th> \n");
			h.append("<th >������ҹ���</th> \n");
			h.append("<th >������ҹ���</th> \n");
			h.append("<th >������¢�� (�պ)</th> \n");
			h.append("<th >������¢�� (�ҷ)</th> \n");
			h.append("<th >ʶҹ�</th> \n");
			h.append("<th >Set</th>	 \n");		
			h.append("</tr> \n");
		    for(int i=0;i<o.getItems().size();i++){
		    	SalesTargetBean item = o.getItems().get(i);
		    	className = (i %2 == 0)?"lineE":"lineO";
				h.append("<tr class='"+className+"'> \n");
				    h.append("<td class='td_text_center' width='10%'>"+item.getSalesrepCode()+"</td> \n");
					h.append("<td class='td_text_center' width='10%'>"+item.getCustomerCode()+"</td> \n");
					h.append("<td class='td_text_center' width='20%'>"+item.getCustomerName()+"</td> \n");
					h.append("<td class='td_number' width='10%'>"+item.getTargetQty()+"</td> \n");
					h.append("<td class='td_number' width='10%'>"+item.getTargetAmount()+"</td> \n");
					h.append("<td class='td_text_center' width='10%'>"+item.getStatus()+"</td> \n");
					h.append("<td class='td_text_center' width='20%'> \n");
					
					action = item.isCanSet()?"Set":"View";
					
					h.append(" <a href="+singleQuote+"javascript:openEdit('"+request.getContextPath()+"'  \n");
					h.append("  ,'"+item.getSalesrepCode()+"','"+item.getCustomerId()+"' \n");
					h.append("  ,'"+item.getCustomerCode()+"','"+item.getCustomerName()+"' \n");
					h.append("  ,'"+item.getSalesrepId()+"','"+item.getSalesChannelNo()+"' ,'"+action+"')"+singleQuote+" > \n");
					h.append("   "+action);
					h.append("</a> \n");
					h.append("</td> \n");
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
		try{
			if(o.getItems() != null && o.getItems().size() >0){
				
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
			h.append("<tr> \n");
			h.append("<th >������ҹ���</th> \n");
			h.append("<th >������ҹ���</th> \n");
			h.append("<th >�ù��</th> \n");
			h.append("<th >�����ù��</th> \n");
			h.append("<th >������¢�� (�պ)</th> \n");
			h.append("<th >������¢�� (�ҷ)</th> \n");
			h.append("<th >ʶҹ�</th> \n");
			h.append("<th >����������´</th>	 \n");		
			h.append("</tr> \n");
		    for(int i=0;i<o.getItems().size();i++){
		    	SalesTargetBean item = o.getItems().get(i);
		    	className = (i %2 == 0)?"lineE":"lineO";
				h.append("<tr class='"+className+"'> \n");
					h.append("<td class='td_text_center' width='10%'>"+item.getCustomerCode()+"</td> \n");
					h.append("<td class='td_text_center' width='20%'>"+item.getCustomerName()+"</td> \n");
				    h.append("<td class='td_text_center' width='5%'>"+item.getBrand()+"</td> \n");
				    h.append("<td class='td_text_center' width='15%'>"+item.getBrandName()+"</td> \n");
					h.append("<td class='td_number' width='10%'>"+item.getTargetQty()+"</td> \n");
					h.append("<td class='td_number' width='10%'>"+item.getTargetAmount()+"</td> \n");
					h.append("<td class='td_text_center' width='10%'>"+item.getStatus()+"</td> \n");
					h.append("<td class='td_text_center' width='20%'> \n");
						action = item.isCanSet()?"View":"View";
						h.append(" <a href="+singleQuote+"javascript:openView('"+request.getContextPath()+"'  \n");
						h.append("  , "+item.getId()+" \n");
						h.append("  ,'"+item.getSalesrepCode()+"','"+item.getCustomerId()+"' \n");
						h.append("  ,'"+item.getCustomerCode()+"','"+item.getCustomerName()+"' \n");
						h.append("  ,'"+item.getSalesrepId()+"','"+item.getSalesChannelNo()+"' ,'"+action+"')"+singleQuote+" > \n");
						h.append("   "+action);
						h.append("</a> \n");
					h.append("</td> \n");
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
			h.append("<th >������ҹ���</th> \n");
			h.append("<th >������ҹ���</th> \n");
			h.append("<th >�ù��</th> \n");
			h.append("<th >�����ù��</th> \n");
			h.append("<th >������¢�� (�պ)</th> \n");
			h.append("<th >������¢�� (�ҷ)</th> \n");
			h.append("<th >ʶҹ�</th> \n");
			h.append("<th >����������´</th>	 \n");	
			h.append("<th >͹��ѵ�</th>	 \n");	
			h.append("<th >���͹��ѵ�</th>	 \n");	
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
						h.append("   ͹��ѵ�");
						h.append("</a> \n");
						h.append("</div>");
					}
					h.append("</td> \n");
					h.append("<td class='td_text_center' width='10%'> \n");
					if(item.isCanUnAccept()){
						h.append("<div name='unaccept_div' >");
						h.append(" <a href="+singleQuote+"javascript:unacceptRow('"+request.getContextPath()+"',"+item.getId()+","+item.getRowId()+")"+singleQuote+" > \n");
						h.append("   ���͹��ѵ�");
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
	
	public static StringBuffer genExportExcelByMT(HttpServletRequest request,SalesTargetBean o,User user) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className = "";
		String colspan = "10";
		try{
			if(o.getItems() != null && o.getItems().size() >0){
				
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
			h.append("<tr> \n");
			  h.append("<td class='colum_head' colspan="+colspan+">����������ʹ���</td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			  h.append("<td class='colum_head' colspan="+colspan+">��͹ "+o.getPeriod()+"   "+o.getStartDate()+"-"+o.getEndDate()+"   �ù�� "+o.getBrand()+"-"+o.getBrandName()+"</td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			  h.append("<td class='colum_head' colspan="+colspan+">�Ҥ��â��	 "+o.getSalesChannelName()+"   ��������� "+o.getCustCatNo()+"</td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			  h.append("<td class='colum_head' colspan="+colspan+">������ҹ���	 "+o.getCustomerCode()+"   ������ҹ��� "+o.getCustomerName()+"</td> \n");
			h.append("</tr> \n");
			h.append("</table> \n");
			
			h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
			h.append("<tr> \n");
			h.append("<th >�����Թ��� </th> \n");
			h.append("<th >�����Թ���</th> \n");
			h.append("<th >�ʹ���-�׹(����� 12 ��͹)</th> \n");
			h.append("<th >�ʹ���-�׹(����� 3 ��͹)</th> \n");
			h.append("<th >�Ҥ� Last Price</th> \n");
			h.append("<th >������� ���(�պ)</th>	 \n");		
			h.append("<th >������� ���(�ҷ)</th>	 \n");	
			h.append("<th >ʶҹ�</th>	 \n");	
			h.append("<th >�����˵�</th>	 \n");	
			h.append("<th >�˵ؼŷ��ҧ Sale �� Reject</th>	 \n");	
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
		    }
			h.append("</table> \n");
			}
		   return h;
		}catch(Exception e){
			throw e;
		}finally{
		}
	}
	
}
