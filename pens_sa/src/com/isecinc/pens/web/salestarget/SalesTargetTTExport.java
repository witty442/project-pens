package com.isecinc.pens.web.salestarget;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class SalesTargetTTExport {
	protected static Logger logger = Logger.getLogger("PENS");

	public static StringBuffer genResultSearchTargetHeadByMKT_TT(HttpServletRequest request,SalesTargetBean o,User user) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className = "";
		String action ="";
		char singleQuote ='"';
		double totalCTNByCat = 0;
		double totalAmountByCat = 0;
		SalesTargetBean itemNext = null;
		try{
			if(o.getItems() != null && o.getItems().size() >0){
				
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
			h.append("<tr> \n");
			h.append("<th >��������� </th> \n");
			h.append("<th >�Ҥ�����´���</th> \n");
			h.append("<th >�����Ҥ�����´���</th> \n");
			h.append("<th >������¢�� (�պ)</th> \n");
			h.append("<th >������¢�� (�ҷ)</th> \n");
			h.append("<th >ʶҹ�</th> \n");
			h.append("<th >Set</th>	 \n");		
			h.append("</tr> \n");
		    for(int i=0;i<o.getItems().size();i++){
		    	SalesTargetBean item = o.getItems().get(i);
		    	className = (i %2 == 0)?"lineE":"lineO";
		    	
				h.append("<tr class='"+className+"'> \n");
			    h.append("<td class='td_text_center' width='10%'>"+item.getCustCatDesc()+"</td> \n");
				h.append("<td class='td_text_center' width='10%'>"+item.getSalesZone()+"</td> \n");
				h.append("<td class='td_text_center' width='20%'>"+item.getSalesZoneDesc()+"</td> \n");
				h.append("<td class='td_number' width='10%'>"+item.getTargetQty()+"</td> \n");
				h.append("<td class='td_number' width='10%'>"+item.getTargetAmount()+"</td> \n");
				h.append("<td class='td_text_center' width='10%'>"+item.getStatus()+"</td> \n");
				h.append("<td class='td_text_center' width='20%'> \n");
				
				action = item.isCanSet()?"Set":"View";
				
				//function openEdit(path,salesZone,salesZoneDesc,custCatNo,custCatNoDesc,mode){
				h.append(" <a href="+singleQuote+"javascript:openEdit('"+request.getContextPath()+"'  \n");
				h.append("  ,'"+item.getSalesZone()+"' \n");
				h.append("  ,'"+item.getCustCatNo()+"','"+action+"')"+singleQuote+" > \n");
				h.append("   "+action);
				h.append("</a> \n");
				h.append("</td> \n");
				h.append("</tr> \n");
				
				//summary
				totalCTNByCat += Utils.convertStrToDouble(item.getTargetQty());
				totalAmountByCat += Utils.convertStrToDouble(item.getTargetAmount());
				
				itemNext = null;
				if(i <o.getItems().size()-1){
					itemNext = o.getItems().get(i+1);
				}
				if(itemNext==null || !itemNext.getCustCatNo().equalsIgnoreCase(item.getCustCatNo())){
					h.append("<tr class='hilight_text'> \n");
					h.append("<td class=''></td>  \n");
					h.append("<td class=''></td>  \n");
					h.append("<td class='' align='right'> \n");
					h.append("  <B> �ʹ��������������� </B> \n");
					h.append("</td> \n");
					h.append("<td class='td_number_bold' align='right'> \n");
					h.append("  <B>  "+Utils.decimalFormat(totalCTNByCat,Utils.format_current_no_disgit)+" </B> \n");
					h.append("</td> \n");
					h.append("<td class='td_number_bold' align='right'> \n");
					h.append("  <B> "+Utils.decimalFormat(totalAmountByCat,Utils.format_current_2_disgit)+"</B> \n");
					h.append("</td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("</tr> \n");
					//reset value 
					totalCTNByCat = 0;
					totalAmountByCat = 0;
		    	}//if
				
		    }//for
			h.append("<tr class='hilight_text'> \n");
			h.append("<td class=''></td>  \n");
			h.append("<td class=''></td>  \n");
			h.append("<td align='right'> \n");
			h.append(" <font color='#000099' size='2'> <B> �ʹ��� TT By Brand </font> \n");
			h.append("</td> \n");
			h.append("<td align='right'> \n");
			h.append(" <font color='#000099' size='2'> <B>  "+o.getTotalTargetQty()+" </B> </font>\n");
			h.append("</td> \n");
			h.append("<td  align='right'> \n");
			h.append(" <font color='#000099' size='2'> <B> "+o.getTotalTargetAmount()+"</font></B> \n");
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
	
	public static StringBuffer genResultSearchTargetHeadByTTSUPER(HttpServletRequest request,SalesTargetBean o,User user) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className = "";
		String action ="";
		char singleQuote ='"';
		double totalCTNByCat = 0;
		double totalAmountByCat = 0;
		SalesTargetBean itemNext = null;
		try{
			if(o.getItems() != null && o.getItems().size() >0){
				
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
			h.append("<tr> \n");
			h.append("<th >��������� </th> \n");
			h.append("<th >�Ҥ�����ô��� </th> \n");
			h.append("<th >�ù��</th> \n");
			h.append("<th >�����ù��</th> \n");
			h.append("<th >������¢�� (�պ)</th> \n");
			h.append("<th >������¢�� (�ҷ)</th> \n");
			h.append("<th >ʶҹ�</th> \n");
			h.append("<th >��˹����</th>	 \n");	
			h.append("<th >�觤׹ MKT</th>	 \n");	
			h.append("<th >Copy �����͹�������</th>	 \n");
			h.append("<th >�����˵ء�� Reject</th>	 \n");	
			h.append("</tr> \n");
		    for(int i=0;i<o.getItems().size();i++){
		    	SalesTargetBean item = o.getItems().get(i);
		    	className = (i %2 == 0)?"lineE":"lineO";
		    	
				h.append("<tr class='"+className+"'> \n");
			    h.append("<td class='td_text_center' width='10%'>"+item.getCustCatDesc()+"</td> \n");
			    h.append("<td class='td_text_center' width='14%'>"+item.getSalesZoneDesc()+"</td> \n");
				h.append("<td class='td_text_center' width='5%'>"+item.getBrand()+"</td> \n");
				h.append("<td class='td_text_center' width='14%'>"+item.getBrandName()+"</td> \n");
				h.append("<td class='td_number' width='7%'>"+item.getTargetQty()+"</td> \n");
				h.append("<td class='td_number' width='7%'>"+item.getTargetAmount()+"</td> \n");
				h.append("<td class='td_text_center' width='5%'> \n");
				h.append(" <input type='text' readonly name='status' size='6' value="+item.getStatus()+" class='disableText'/> \n");
				h.append("</td> \n");
				
				//Set
				h.append("<td class='td_text_center' width='7%'> \n");
				action = item.isCanSet()?"Set":"View";
				
				//function openEdit(path,salesZone,salesZoneDesc,custCatNo,custCatNoDesc,mode){
				h.append(" <a href="+singleQuote+"javascript:openEdit('"+request.getContextPath()+"'  \n");
				h.append("  ,'"+item.getSalesZone()+"','"+item.getBrand()+"' \n");
				h.append("  ,'"+item.getCustCatNo()+"','"+action+"')"+singleQuote+" > \n");
				h.append("   "+action);
			
				h.append("</a> \n");
				h.append("</td> \n");
				
				//reject
				h.append("<td class='td_text_center' width='7%'> \n");
				action = item.isCanReject()?"Reject":"";
				h.append(" <a href="+singleQuote+"javascript:rejectRow('"+request.getContextPath()+"'  \n");
				h.append("  ,'"+item.getSalesZone()+"','"+item.getBrand()+"' \n");
				h.append("  ,'"+item.getCustCatNo()+"','"+item.getPeriod()+"' \n");	
				h.append("  ,"+item.getRowId()+")"+singleQuote+" > \n");
				h.append("   <span id='span_reject_action_"+item.getRowId()+"'> "+action+"</span>");
				h.append(" </a> \n");
				h.append("</td> \n");
				//Copy 
				action = item.isCanCopy()?"Copy ":"";
				h.append("<td class='td_text_center' width='7%'> \n");
				h.append(" <a href="+singleQuote+"javascript:copyRowByBrand('"+request.getContextPath()+"'  \n");
				h.append("  ,'"+item.getSalesZone()+"','"+item.getBrand()+"' \n");
				h.append("  ,'"+item.getCustCatNo()+"','"+item.getPeriod()+"' \n");	
				h.append("  ,"+item.getRowId()+")"+singleQuote+" > \n");
				h.append("   <span id='span_copy_action_"+item.getRowId()+"'> "+action+"</span>");
				h.append(" </a> \n");
				h.append("</td> \n");
				
				h.append("<td class='td_text_center' width='16%'> \n");
				h.append(" <input type='text' name='rejectReason' readonly value='"+item.getRejectReason()+"' size='20' class='disableText'/> \n");
				h.append("</td> \n");
				h.append("</tr> \n");
				
				//summary
				totalCTNByCat += Utils.convertStrToDouble(item.getTargetQty());
				totalAmountByCat += Utils.convertStrToDouble(item.getTargetAmount());
				
				itemNext = null;
				if(i <o.getItems().size()-1){
					itemNext = o.getItems().get(i+1);
				}
				if(itemNext==null || !itemNext.getCustCatNo().equalsIgnoreCase(item.getCustCatNo())){
					h.append("<tr class='hilight_text'> \n");
					h.append("<td class=''></td>  \n");
					h.append("<td class=''></td>  \n");
					h.append("<td class=''></td>  \n");
					h.append("<td class='' align='right'> \n");
					h.append("  <B> TOTAL</B> \n");
					h.append("</td> \n");
					h.append("<td class='td_number_bold' align='right'> \n");
					h.append("  <B>  "+Utils.decimalFormat(totalCTNByCat,Utils.format_current_no_disgit)+" </B> \n");
					h.append("</td> \n");
					h.append("<td class='td_number_bold' align='right'> \n");
					h.append("  <B> "+Utils.decimalFormat(totalAmountByCat,Utils.format_current_2_disgit)+"</B> \n");
					h.append("</td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("</tr> \n");
					//reset value 
					totalCTNByCat = 0;
					totalAmountByCat = 0;
		    	}//if
				
		    }//for
			h.append("<tr class='hilight_text'> \n");
			h.append("<td class=''></td>  \n");
			h.append("<td class=''></td>  \n");
			h.append("<td class=''></td>  \n");
			h.append("<td class='' align='right'> \n");
			h.append("  <font color='#000099' size='2'><B> GRAND TOTAL </B></font> \n");
			h.append("</td> \n");
			h.append("<td class='td_number_bold' align='right'> \n");
			h.append("  <font color='#000099' size='2'><B>  "+o.getTotalTargetQty()+" </B> </font>\n");
			h.append("</td> \n");
			h.append("<td class='td_number_bold' align='right'> \n");
			h.append(" <font color='#000099' size='2'> <B> "+o.getTotalTargetAmount()+"</B></font> \n");
			h.append("</td> \n");
			h.append("<td class=''></td> \n");
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
	
	public static StringBuffer genResultSearchTargetHeadByTTMGR(HttpServletRequest request,SalesTargetBean o,User user) throws Exception{
		return genResultSearchTargetHeadByTTMGRTypeBrand(request, o, user);
	}
	public static StringBuffer genResultSearchTargetHeadByTTMGRTypeBrand(HttpServletRequest request,SalesTargetBean o,User user) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className = "";
		String action ="";
		char singleQuote ='"';
		double totalCTNByCat = 0;
		double totalAmountByCat = 0;
		double totalSalesCTNByCat = 0;
		double totalSalesAmountByCat = 0;
		SalesTargetBean itemNext = null;
		try{
			if(o.getItems() != null && o.getItems().size() >0){
				
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
			h.append("<tr> \n");
			h.append("<th rowspan='2'>��������� </th> \n");
			h.append("<th rowspan='2' >�Ҥ�����ô���</th> \n");
			h.append("<th rowspan='2' >�ù��</th> \n");
			h.append("<th rowspan='2' >�����ù��</th> \n");
			h.append("<th colspan='2'>��ǹ Marketing ��˹����</th> \n");
			h.append("<th colspan='2' >��ǹ���Ἱ�����Ѻ���</th> \n");
			h.append("<th rowspan='2' >ʶҹ�</th> \n");
			h.append("<th rowspan='2' >��������´</th>	 \n");	
			h.append("<th rowspan='2' >͹��ѵ�</th>	 \n");	
			h.append("<th rowspan='2' >���͹��ѵ�</th>	 \n");		
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append(" <th >������¢�� (�պ)</th> \n");
			h.append(" <th >������¢�� (�ҷ)</th> \n");
			h.append(" <th >������¢�� (�պ)</th> \n");
			h.append(" <th >������¢�� (�ҷ)</th> \n");
			h.append("</tr> \n");
			
		    for(int i=0;i<o.getItems().size();i++){
		    	SalesTargetBean item = o.getItems().get(i);
		    	className = (i %2 == 0)?"lineE":"lineO";
		    	
				h.append("<tr class='"+className+"'> \n");
			    h.append("<td class='td_text_center' width='8%'><span id='span_cust_cat_no_"+i+"'>"+item.getCustCatDesc()+"</span></td> \n");
			    h.append("<td class='td_text_center' nowrap width='14%'><span id='span_sales_zone_"+i+"'>"+item.getSalesZoneDesc()+"</span></td> \n");
				h.append("<td class='td_text_center' width='5%'><span id='span_brand_"+i+"'>"+item.getBrand()+"</span></td> \n");
				h.append("<td class='td_text_center' width='15%'><span id='span_brand_name_"+i+"'>"+item.getBrandName()+"</span></td> \n");
				h.append("<td class='td_number' width='8%'><span id='span_target_qty_"+i+"'>"+item.getTargetQty()+"</span></td> \n");
				h.append("<td class='td_number' width='8%'><span id='span_target_amount_"+i+"'>"+item.getTargetAmount()+"</span></td> \n");
				h.append("<td class='td_number' width='8%'><span id='span_sales_target_qty_"+i+"'>"+item.getSalesTargetQty()+"</span></td> \n");
				h.append("<td class='td_number' width='8%'><span id='span_sales_target_amount_"+i+"'>"+item.getSalesTargetAmount()+"</span></td> \n");
				
				h.append("<td class='td_text_center' width='5%'> \n");
				h.append(" <input type='text' readonly class='disableText' size='5' name='status' value='"+item.getStatus()+"' /> \n");
				h.append("</td> \n");
				//View
				h.append("<td class='td_text_center' width='8%'> \n");
				action = "View";
				h.append(" <a href="+singleQuote+"javascript:openEdit('"+request.getContextPath()+"'  \n");
				h.append("  ,'"+item.getSalesZone()+"','"+item.getBrand()+"' \n");
				h.append("  ,'"+item.getCustCatNo()+"','"+action+"')"+singleQuote+" > \n");
				h.append("   "+action);
				h.append("</a> \n");
				h.append("<input type='hidden' name ='custCatNo' value ='"+item.getCustCatNo()+"'/>\n");
				h.append("<input type='hidden' name ='brand' value ='"+item.getBrand()+"'/>\n");
				h.append("<input type='hidden' name ='zone' value ='"+item.getSalesZone()+"'/>\n");
				h.append("</td> \n");
				
				/**** accept ***************************************/
				h.append("<td class='td_text_center' width='5%'> \n");
				action = item.isCanFinish()?"͹��ѵ�":"";
				
				h.append("<div name='accept_div' >");
				h.append(" <a href="+singleQuote+"javascript:acceptRow('"+request.getContextPath()+"'  \n");
				h.append("  ,'"+item.getSalesZone()+"' \n");
				h.append("  ,'"+item.getBrand()+"' ");
				h.append("  ,'"+item.getCustCatNo() +"' \n");
				h.append("  ,'"+item.getPeriod()+"' \n");	
				h.append("  ,'"+item.getStartDate()+"' ");	
				h.append("  ,'"+item.getRowId()+"' \n");	
				h.append("  )"+singleQuote+" > \n");
				h.append("   "+action);
				h.append(" </a> \n");
				h.append(" </div> \n");
				h.append("</td> \n");
				
				/*** unaccept ******************************************/
				h.append("<td class='td_text_center' width='5%'> \n");
				action = item.isCanUnAccept()?"���͹��ѵ�":"";
				
				//path,salesZone,brand,custCatNo,period,startDate,rowId){
				
				h.append("<div name='unaccept_div' >");
				h.append(" <a href="+singleQuote+"javascript:unacceptRow('"+request.getContextPath()+"'  \n");
				h.append("  ,'"+item.getSalesZone()+"' \n");
				h.append("  ,'"+item.getBrand()+"' ");
				h.append("  ,'"+item.getCustCatNo() +"' \n");
				h.append("  ,'"+item.getPeriod()+"' \n");	
				h.append("  ,'"+item.getStartDate()+"' ");	
				h.append("  ,'"+item.getRowId()+"' \n");	
				h.append("  )"+singleQuote+" > \n");
				h.append("   "+action);
				h.append(" </a> \n");
				h.append(" </div> \n");
				h.append("</td> \n");
				
				h.append("</tr> \n");
				
				//summary
				totalCTNByCat += Utils.convertStrToDouble(item.getTargetQty());
				totalAmountByCat += Utils.convertStrToDouble(item.getTargetAmount());
				totalSalesCTNByCat += Utils.convertStrToDouble(item.getSalesTargetQty());
				totalSalesAmountByCat += Utils.convertStrToDouble(item.getSalesTargetAmount());
				
				itemNext = null;
				if(i <o.getItems().size()-1){
					itemNext = o.getItems().get(i+1);
				}
				if(itemNext==null || !itemNext.getCustCatNo().equalsIgnoreCase(item.getCustCatNo())){
					h.append("<tr class='hilight_text'> \n");
					h.append("<td class=''></td>  \n");
					h.append("<td class=''></td>  \n");
					h.append("<td class=''></td>  \n");
					h.append("<td class='' align='right'> \n");
					h.append("  <font color='#000099'><B> �ʹ���</B></font> \n");
					h.append("</td> \n");
					h.append("<td class='td_number_bold' align='right'> \n");
					h.append("  <font color='#000099'><B>  "+Utils.decimalFormat(totalCTNByCat,Utils.format_current_no_disgit)+" </B></font> \n");
					h.append("</td> \n");
					h.append("<td class='td_number_bold' align='right'> \n");
					h.append("  <font color='#000099'><B> "+Utils.decimalFormat(totalAmountByCat,Utils.format_current_2_disgit)+"</B></font> \n");
					h.append("</td> \n");
					h.append("<td class='td_number_bold' align='right'> \n");
					h.append("  <font color='#000099'><B>  "+Utils.decimalFormat(totalSalesCTNByCat,Utils.format_current_no_disgit)+" </B></font> \n");
					h.append("</td> \n");
					h.append("<td class='td_number_bold' align='right'> \n");
					h.append("  <font color='#000099'><B> "+Utils.decimalFormat(totalSalesAmountByCat,Utils.format_current_2_disgit)+"</B></font> \n");
					h.append("</td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("<td class=''></td> \n");
					h.append("</tr> \n");
					//reset value 
					totalCTNByCat = 0;
					totalAmountByCat = 0;
					totalSalesCTNByCat = 0;
					totalSalesAmountByCat = 0;
		    	}//if
				
		    }//for
			h.append("<tr class='hilight_text'> \n");
			h.append("<td class=''></td>  \n");
			h.append("<td class=''></td>  \n");
			h.append("<td class=''></td>  \n");
			h.append("<td class='' align='right'> \n");
			h.append("  <font color='#000099' size='2'><B> �ʹ��������� </B></font> \n");
			h.append("</td> \n");
			h.append("<td class='td_number_bold' align='right'> \n");
			h.append("  <font color='#000099' size='2'><B>  "+o.getTotalTargetQty()+" </B></font>  \n");
			h.append("</td> \n");
			h.append("<td class='td_number_bold' align='right'> \n");
			h.append("  <font color='#000099' size='2'><B> "+o.getTotalTargetAmount()+"</B></font>  \n");
			h.append("</td> \n");
			h.append("<td class='td_number_bold' align='right'> \n");
			h.append("  <font color='#000099' size='2'><B>  "+o.getTotalSalesTargetQty()+" </B></font>  \n");
			h.append("</td> \n");
			h.append("<td class='td_number_bold' align='right'> \n");
			h.append(" <font color='#000099' size='2'> <B> "+o.getTotalSalesTargetAmount()+"</B></font>  \n");
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

	public static StringBuffer genResultSearchTargetHeadByTTADMIN(HttpServletRequest request,SalesTargetBean o,User user) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className = "";
		try{
			if(o.getItems() != null && o.getItems().size() >0){
				
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='1' width='60%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
			h.append("<tr> \n");
			h.append("<th>�ù��</th> \n");
			h.append("<th>������¢�� (�պ)</th> \n");
			h.append("<th>������¢�� (�ҷ)</th> \n");
			h.append("<th>ʶҹ�</th> \n");
			h.append("<th>Change Status To</th>	\n");		
			h.append("</tr> \n");
			
		    for(int i=0;i<o.getItems().size();i++){
		    	SalesTargetBean item = o.getItems().get(i);
		    	className = (i %2 == 0)?"lineE":"lineO";
		    	
				h.append("<tr class='"+className+"'> \n");
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
