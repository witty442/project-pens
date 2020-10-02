package com.isecinc.pens.web.buds.page.export;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.bean.ConfPickingBean;
import com.isecinc.pens.bean.User;
import com.pens.util.ControlCode;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class ControlPickingExport {
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");
	
	public static ConfPickingBean searchControlPickingReportTypeSummary(ConfPickingBean o,boolean excel,User user){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer();
		StringBuffer html = null;
		ConfPickingBean item = null;
		ConfPickingBean itemNext = null;
		int r = 0;
		int countOrderBySale = 0,countOrderAll=0;;
		double totalAmount = 0,totalAmountAll=0;
		double totalVatAmount = 0,totalVatAmountAll=0;
		double totalNetAmount = 0,totalNetAmountAll=0;
		List<ConfPickingBean> itemsList = new ArrayList<ConfPickingBean>();
		try{
			//create connection
			conn = DBConnectionApps.getInstance().getConnection();
			sql = genSQLControlPickingReportTypeSummary(o);
	
			//logger.debug("sql:"+sql);
			if(logger.isDebugEnabled()){
				FileUtil.writeFile("d://dev_temp//temp//sql.sql", sql.toString(),"TIS-620");
			}
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				item = new ConfPickingBean();
				item.setPickingNo(Utils.isNull(rst.getString("picking_no")));
				item.setInvoiceNo(Utils.isNull(rst.getString("invoice_no")));
				item.setInvoiceDate(DateUtil.stringValueNull(rst.getDate("INVOICE_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				item.setOrderNo(Utils.isNull(rst.getString("order_no")));
				item.setOrderDate(DateUtil.stringValue(rst.getDate("ORDER_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				item.setPoNumber(Utils.isNull(rst.getString("po_number")));
				item.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
				item.setCustomerCode(Utils.isNull(rst.getString("customer_number")));
				item.setCustomerName(Utils.isNull(rst.getString("customer_name")));
				item.setAlternateName(Utils.isNull(rst.getString("alternate_name")));
                item.setMobile(Utils.isNull(rst.getString("telephone")));
                item.setAmphur(Utils.isNull(rst.getString("province"))+"/"+Utils.isNull(rst.getString("amphur")));
                
                item.setTotalAmountD(rst.getDouble("total_amount"));
                item.setVatAmountD(rst.getDouble("vat_amount"));
                item.setNetAmountD(rst.getDouble("net_amount"));
				itemsList.add(item)	;
			}//while
			
			 //gen Head Table
			 html = new StringBuffer("");
			 html.append(ExcelHeader.EXCEL_HEADER);
			 html.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
			 html.append("<tr><td colspan='12'><b> Invoice Summary</b></td> </tr>\n");
			 html.append("</table>\n");
			
			 html.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='table table-condensed table-striped'> \n");
			 html.append("<thead><tr> \n");
			 html.append("<th class='colum_head_hilight'>Picking List No.</th> \n");
			 html.append("<th class='colum_head_hilight'>Invoice No</th> \n");
			 html.append("<th class='colum_head_hilight'>Invoice Date</th> \n");
			 html.append("<th class='colum_head_hilight'>Order No</th> \n");
			 html.append("<th class='colum_head_hilight'>Order Date</th> \n");
			 html.append("<th class='colum_head_hilight'>Customer PO</th> \n");
			 html.append("<th class='colum_head_hilight'>Sales Code</th> \n");
			 html.append("<th class='colum_head_hilight'>Customer No</th> \n");
			 html.append("<th class='colum_head_hilight'>Customer Name</th> \n");
			 html.append("<th class='colum_head_hilight'>Reference Cust No.</th> \n");
			 html.append("<th class='colum_head_hilight'>Contact Info.</th> \n");
			 html.append("<th class='colum_head_hilight'>อำเภอ / จังหวัด</th> \n");
			 html.append("</tr></thead> \n");
			 html.append("<tbody> \n");
			//display 
			 for(int i=0;i<itemsList.size();i++){
				 itemNext = null;
				 r++;
				 item = itemsList.get(i);
				 if(i!= itemsList.size()-1){
					itemNext = itemsList.get(i+1);
				 }
				//by Sales
			    countOrderBySale++;
			    totalAmount += item.getTotalAmountD();
			    totalVatAmount += item.getVatAmountD();
			    totalNetAmount += item.getNetAmountD();
			    //all
			    countOrderAll++;
			    totalAmountAll += item.getTotalAmountD();
			    totalVatAmountAll += item.getVatAmountD();
			    totalNetAmountAll += item.getNetAmountD();
	
			  //Gen Row Table
				html.append("<tr class=''> \n");
				html.append("<td class='text' width='6%'>"+item.getPickingNo()+"</td> \n");
				html.append("<td class='text' width='6%'>"+item.getInvoiceNo()+"</td> \n");
				html.append("<td class='text' width='6%'>"+item.getInvoiceDate()+"</td> \n");
				html.append("<td class='text' width='6%'>"+item.getOrderNo()+"</td> \n");
				html.append("<td class='text' width='5%'>"+item.getOrderDate()+"</td> \n");
				html.append("<td class='text' width='7%'>"+item.getPoNumber()+"</td> \n");//
				html.append("<td class='text' width='3%'>"+item.getSalesrepCode()+"</td> \n");
				html.append("<td class='text' width='10%'>"+item.getCustomerCode()+"</td> \n");
				html.append("<td class='text' width='10%'>"+item.getCustomerName()+"</td> \n");
				html.append("<td class='text' width='10%'>"+item.getAlternateName()+"</td> \n");
				html.append("<td class='text' width='10%'>"+item.getMobile()+"</td> \n");
				html.append("<td class='text' width='10%'>"+item.getAmphur()+"</td> \n");
				html.append("</tr> \n");
				//gen summary by SalesCode
				if(itemNext== null ||
					(itemNext != null && !item.getSalesrepCode().equalsIgnoreCase(itemNext.getSalesrepCode())) ){
					html.append("<tr class='colum_head'> \n");
					html.append("<td class='text' colspan='8' align='right'>Total By Sales :"+Utils.isNull(item.getSalesrepCode())+"</td> \n");
					html.append("<td class='text' align='center'>"+countOrderBySale+" ใบ </td> \n");
					html.append("<td class='text' align='center'>"+Utils.decimalFormat(totalAmount,Utils.format_current_2_disgit)+"</td>\n");
					html.append("<td class='text' align='center'>"+Utils.decimalFormat(totalVatAmount,Utils.format_current_2_disgit)+" </td>\n");
					html.append("<td class='text' align='center'>"+Utils.decimalFormat(totalNetAmount,Utils.format_current_2_disgit)+"</td> \n");
					html.append("</tr> \n");
					
					//reset by Sales
					totalAmount = 0;
					totalVatAmount =0;
					totalNetAmount = 0;
				}
			 }//for
			 
			 //GrandTotal
			html.append("<tr class='colum_head'> \n");
			html.append("<td class='text' colspan='8' align='right'>GRAND TOTAL :</td> \n");
			html.append("<td class='text' align='center'>"+countOrderAll+" ใบ </td> \n");
			html.append("<td class='text' align='center'>"+Utils.decimalFormat(totalAmountAll,Utils.format_current_2_disgit)+"</td>\n");
			html.append("<td class='text' align='center'>"+Utils.decimalFormat(totalVatAmountAll,Utils.format_current_2_disgit)+" </td>\n");
			html.append("<td class='text' align='center'>"+Utils.decimalFormat(totalNetAmountAll,Utils.format_current_2_disgit)+"</td> \n");
			html.append("</tr> \n");
				
			//Check Execute Found data
			if(r>0){
			   html.append("<tbody> \n");
			   html.append("</table>");
			}

		   o.setDataStrBuffer(html);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			//e.printStackTrace();
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	  return o;
	}
	public static ConfPickingBean searchControlPickingReportTypeDetail(ConfPickingBean o,boolean excel,User user){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer();
		StringBuffer html = new StringBuffer("");
		ConfPickingBean item = null;
		ConfPickingBean itemNext = null;
		int r = 0;
		int noByInvoice = 0;
		double totalAmount = 0;
		double totalVatAmount = 0;
		double totalNetAmount = 0;
		List<ConfPickingBean> itemsList = new ArrayList<ConfPickingBean>();
		String invoiceNoTemp = "";
		try{
			//create connection
			conn = DBConnectionApps.getInstance().getConnection();
			sql = genSQLControlPickingReportTypeDetail(o);
	
			//logger.debug("sql:"+sql);
			if(logger.isDebugEnabled()){
				FileUtil.writeFile("d://dev_temp//temp//sql.sql", sql.toString(),"TIS-620");
			}
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				item = new ConfPickingBean();
				item.setPickingNo(Utils.isNull(rst.getString("picking_no")));
				item.setInvoiceNo(Utils.isNull(rst.getString("invoice_no")));
				item.setInvoiceDate(DateUtil.stringValue(rst.getDate("INVOICE_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				item.setInvoiceType(Utils.isNull(rst.getString("invoice_type")));
				item.setInvoiceRef(Utils.isNull(rst.getString("ct_reference")));//orderNo (SB001 replace to 2201
				
				item.setOrderNo(Utils.isNull(rst.getString("order_no")));
				item.setOrderDate(DateUtil.stringValue(rst.getDate("ORDER_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				item.setPoNumber(Utils.isNull(rst.getString("cust_po_number")));
				item.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
				item.setSalesrepName(Utils.isNull(rst.getString("salesrep_name")));
				item.setCustomerCode(Utils.isNull(rst.getString("customer_number")));
				item.setCustomerName(Utils.isNull(rst.getString("customer_name")));

                item.setAddressBillTo(getLineAddress("B", rst));
                item.setAddressShipTo(getLineAddress("S", rst));
                
                item.setProductCode(Utils.isNull(rst.getString("product_code")));
				item.setProductName(Utils.isNull(rst.getString("product_name")));
				item.setUom(Utils.isNull(rst.getString("uom_code")));
                item.setQty(Utils.decimalFormat(rst.getDouble("qty"),Utils.format_number_no_disgit));
                item.setUnitStandardPrice(Utils.decimalFormat(rst.getDouble("UNIT_STANDARD_PRICE"),Utils.format_current_2_disgit));
                item.setUnitSellingPrice(Utils.decimalFormat(rst.getDouble("UNIT_SELLING_PRICE"),Utils.format_current_2_disgit));
                
                item.setTotalAmount(Utils.decimalFormat(rst.getDouble("Total_Amount"),Utils.format_current_2_disgit));
                item.setVatAmount(Utils.decimalFormat(rst.getDouble("vat_amount"),Utils.format_current_2_disgit));
                item.setNetAmount(Utils.decimalFormat(rst.getDouble("net_amount"),Utils.format_current_2_disgit));
                
                
                item.setTotalAmountD(rst.getDouble("total_amount"));
                item.setVatAmountD(rst.getDouble("vat_amount"));
                item.setNetAmountD(rst.getDouble("net_amount"));
				itemsList.add(item)	;
			}//while
			
			 //gen Head Table
			 html.append(ExcelHeader.EXCEL_HEADER);
			 html.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
			 html.append("<tr>");
			 html.append("  <td colspan='10' class='text'><b>Invoice Information</b></td> ");
			 html.append("</tr>\n");
			 
			//display 
			 for(int i=0;i<itemsList.size();i++){
				 itemNext = null;
				 r++;
				 item = itemsList.get(i);
				 if(i!= itemsList.size()-1){
					itemNext = itemsList.get(i+1);
				 }
				 
				 //by invoice
			     totalAmount += item.getTotalAmountD();
			     totalVatAmount += item.getVatAmountD();
			     totalNetAmount += item.getNetAmountD();
			     noByInvoice++;
				  
				 //Gen Header Table Excel
				 if(invoiceNoTemp.equals("")){
					
					 html.append("<tr>");
					 html.append("  <td class='colum_head_hilight'><b> Picking List No</b></td><td class='text'>"+item.getPickingNo()+"</td> ");
					 html.append("  <td class='colum_head_hilight'><b> Customer PO</b></td> <td colspan='7' class='text'>"+item.getPoNumber()+"</td> ");
					 html.append("</tr>\n");
					 html.append("<tr>");
					 html.append("  <td  class='colum_head_hilight'><b> Invoice </b></td><td class='text'>"+item.getInvoiceNo()+"</td> ");
					 html.append("  <td  class='colum_head_hilight'><b> Invoice Date</b></td> <td colspan='7' class='text'>"+item.getInvoiceDate()+"</td> ");
					 html.append("</tr>\n");
					 html.append("<tr>");
					 html.append("  <td class='colum_head_hilight'><b> Invoice Type Name</b></td><td class='text'>"+item.getInvoiceType()+"</td> ");
					 html.append("  <td  class='colum_head_hilight'><b> Reference</b></td> <td colspan='7' class='text'>"+item.getInvoiceRef()+"</td> ");
					 html.append("</tr>\n");
					 html.append("<tr>");
					 html.append("  <td class='colum_head_hilight'><b> Salesperson</b></td><td class='text'>"+item.getSalesrepCode()+"</td> ");
					 html.append("  <td  class='colum_head_hilight'><b> Salesperson Name</b></td> <td colspan='7' class='text'>"+item.getSalesrepName()+"</td> ");
					 html.append("</tr>\n");
					 html.append("<tr>");
					 html.append("  <td class='colum_head_hilight'><b> Customer Number</b></td><td class='text'>"+item.getCustomerCode()+"</td> ");
					 html.append("  <td  class='colum_head_hilight'><b> Customer Name</b></td> <td colspan='7' class='text'>"+item.getCustomerName()+"</td> ");
					 html.append("</tr>\n");
					 html.append("<tr>");
					 html.append("  <td  class='colum_head_hilight'><b> Bill-To</b></td><td colspan='9' class='text'>"+item.getAddressBillTo()+"</td> ");
					 html.append("</tr>\n");
					 html.append("<tr>");
					 html.append("  <td  class='colum_head_hilight'><b> Ship-To</b></td><td colspan='9' class='text'>"+item.getAddressShipTo()+"</td> ");
					 html.append("</tr>\n");
					 
					 html.append("<tr>");
					 html.append("  <td colspan='10' class='text'>&nbsp;</td> ");
					 html.append("</tr>\n");
						
					 html.append("<tr> \n");
					 html.append("<th class='colum_head_hilight'>No.</th> \n");
					 html.append("<th class='colum_head_hilight'>SKU</th> \n");
					 html.append("<th class='colum_head_hilight'>Description</th> \n");
					 html.append("<th class='colum_head_hilight'>Qty</th> \n");
					 html.append("<th class='colum_head_hilight'>Uom</th> \n");
					 html.append("<th class='colum_head_hilight'>Unit List Price</th> \n");
					 html.append("<th class='colum_head_hilight'>Unit Selling Price</th> \n");
					 html.append("<th class='colum_head_hilight'>Subtotal</th> \n");
					 html.append("<th class='colum_head_hilight'>TAX</th> \n");
					 html.append("<th class='colum_head_hilight'>Total</th> \n");
					 html.append("</tr>\n");
				 }//if
					 
					
				  //Gen Row Table
					html.append("<tr class=''> \n");
					html.append(" <td class='text' width='6%' class='text_center'>"+noByInvoice+"</td> \n");
					html.append(" <td class='text' width='6%' class='text_center'>"+item.getProductCode()+"</td> \n");
					html.append(" <td class='text' width='6%' class='text'>"+item.getProductName()+"</td> \n");
					html.append(" <td class='text' width='6%' class='num'>"+item.getQty()+"</td> \n");
					html.append(" <td class='text' width='5%' class='text_center'>"+item.getUom()+"</td> \n");
					html.append(" <td class='text' width='7%' class='currency'>"+item.getUnitStandardPrice()+"</td> \n");//
					html.append(" <td class='text' width='3%' class='currency'>"+item.getUnitSellingPrice()+"</td> \n");
					html.append(" <td class='text' width='7%' class='currency'>"+item.getTotalAmount()+"</td> \n");
					html.append(" <td class='text' width='7%' class='currency'>"+item.getVatAmount()+"</td> \n");
					html.append(" <td class='text' width='7%' class='currency'>"+item.getNetAmount()+"</td> \n");
					html.append("</tr> \n");
					//gen summary by SalesCode
					if(itemNext== null ||
						(itemNext != null && !item.getInvoiceNo().equalsIgnoreCase(itemNext.getInvoiceNo())) ){
						html.append("<tr class='colum_head'> \n");
						html.append("<td class='text' colspan='7' align='right'>Total :</td> \n");
						html.append("<td class='text' class='currency_bold'>"+Utils.decimalFormat(totalAmount,Utils.format_current_2_disgit)+"</td>\n");
						html.append("<td class='text' class='currency_bold'>"+Utils.decimalFormat(totalVatAmount,Utils.format_current_2_disgit)+" </td>\n");
						html.append("<td class='text' class='currency_bold'>"+Utils.decimalFormat(totalNetAmount,Utils.format_current_2_disgit)+"</td> \n");
						html.append("</tr> \n");
						html.append("<tr>");
						html.append("  <td colspan='10' class='text'></td> ");
						html.append("</tr>\n");
						//reset by Sales
						totalAmount = 0;
						totalVatAmount =0;
						totalNetAmount = 0;
						noByInvoice = 0;
						invoiceNoTemp = "";
					}else{
						invoiceNoTemp = item.getInvoiceNo();
					}//if summary
			 }//for
				
			//Check Execute Found data
			if(r>0){
			   html.append("</table>");
			}

		   o.setDataStrBuffer(html);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			//e.printStackTrace();
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	  return o;
	}
	public static String getLineAddress( String purpose,ResultSet rs) throws Exception{
		String lineString;
		lineString = "";
		if( !Utils.isNull(rs.getString(purpose+"_alternate_name")).equals("")){
		   lineString = "("+Utils.isNull(rs.getString(purpose+"_alternate_name"))+") ";
		}
		lineString += Utils.isNull(rs.getString(purpose+"_line1")) + " ";
		lineString += Utils.isNull(rs.getString(purpose+"_line2")) + " ";
		if ("กรุงเทพฯ".equalsIgnoreCase(rs.getString(purpose+"_province"))
				|| "กรุงเทพมหานคร".equalsIgnoreCase(rs.getString(purpose+"_province"))) {
			//lineString += "แขวง";
			lineString += (rs.getString(purpose+"_line3")) + " ";
			//lineString += "เขต";
			lineString += (rs.getString(purpose+"_amphur")) + " ";
			lineString += "";
		} else {
			lineString += "ตำบล";
			lineString += (rs.getString(purpose+"_line3")) + " ";
			lineString += "อำเภอ";
			lineString += (rs.getString(purpose+"_amphur")) + " ";
			lineString += "จังหวัด";
		}
		lineString += (rs.getString(purpose+"_province")) + " ";
		lineString += Utils.isNull(rs.getString(purpose+"_postal_code"));
		return lineString;
	}
	private static StringBuffer genSQLControlPickingReportTypeSummary(ConfPickingBean o) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n  /** genSQLControlPickingReportTypeSummary **/");
		sql.append("\n  SELECT M.*");
		sql.append("\n  FROM (");
		sql.append("\n    /** SALES_APP **/ ");
		sql.append("\n    SELECT t.picking_no,inv.invoice_no,inv.invoice_date");
		sql.append("\n    , t.ORDER_NO ,t.ORDER_DATE,t.po_number ");
		sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
		sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
		sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
		sql.append("\n    ,(select code from pensso.ad_user ad where ad.user_id = t.user_id) as salesrep_code");
		sql.append("\n    ,a.alternate_name ,a.telephone");
		sql.append("\n    ,t.total_amount ,t.vat_amount,t.NET_AMOUNT");
		sql.append("\n    FROM pensso.t_order t ,pensso.t_invoice inv ");
		sql.append("\n    ,pensso.m_customer c,pensso.m_address a");
		sql.append("\n    WHERE 1=1 ");
		sql.append("\n    and t.order_no = inv.ref_order ");
		sql.append("\n    and t.customer_id = c.customer_id ");
		sql.append("\n    AND c.customer_id = a.customer_id ");
		sql.append("\n    AND t.ship_address_id = a.address_id ");
		sql.append("\n    AND a.purpose = 'S' ");
		sql.append("\n    and t.PICKING_NO ='"+o.getPickingNo()+"'");
		sql.append("\n    and t.doc_status ='"+I_PO.STATUS_LOADING+"'");
		
		if(ControlCode.canExecuteMethod("Picking", "OrderEDI")){
			sql.append("\n    UNION ALL ");
			
			sql.append("\n    /** EDI **/ ");
			sql.append("\n    SELECT t.picking_no,inv.invoice_no ,inv.invoice_date");
			sql.append("\n    , t.CUST_PO_NUMBER as ORDER_NO ,t.ORDERED_DATE as ORDER_DATE");
			sql.append("\n    ,t.CUST_PO_NUMBER as po_number");
			sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
			sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
			sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
			sql.append("\n    ,(select code from apps.xxpens_salesreps_v ad where ad.salesrep_id = t.salesrep_id) as salesrep_code");
			sql.append("\n    ,a.alternate_name ,a.telephone");
			sql.append("\n    ,NVL(sum(l.LINE_AMOUNT),0) as total_amount ,NVL(SUM((l.LINE_AMOUNT *0.07)),0) as vat_amount ");
			sql.append("\n    ,NVL(SUM((l.LINE_AMOUNT+(l.LINE_AMOUNT *0.07))),0) as NET_AMOUNT");
			sql.append("\n    FROM pensso.T_EDI t ,pensso.t_invoice inv  ");
			sql.append("\n    ,pensso.T_EDI_LINE l ,pensso.m_customer c,pensso.m_address a");
			sql.append("\n    WHERE t.HEADER_ID = l.HEADER_ID ");
			sql.append("\n    AND to_char(t.header_id) = inv.ref_order ");
			sql.append("\n    AND t.customer_id = c.customer_id ");
			sql.append("\n    AND c.customer_id = a.customer_id ");
			sql.append("\n    AND t.ship_to_address_id = a.address_id ");
			sql.append("\n    AND a.purpose = 'S' ");
			sql.append("\n    and t.PICKING_NO ='"+o.getPickingNo()+"'");
			sql.append("\n    and t.doc_status ='"+I_PO.STATUS_LOADING+"'");
			
			sql.append("\n    GROUP BY t.picking_no,inv.invoice_no,inv.invoice_date");
			sql.append("\n    ,t.CUST_PO_NUMBER ,t.ORDERED_DATE  ");
			sql.append("\n    ,c.code ,c.name ,a.district_id,a.province_id");
			sql.append("\n    ,t.salesrep_id ,a.alternate_name ,a.telephone");
		}
		sql.append("\n )M ");
		sql.append("\n ORDER BY M.salesrep_code,M.customer_number");
		
		return sql;
	}
	
	private static StringBuffer genSQLControlPickingReportTypeDetail(ConfPickingBean o) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n  /** genSQLControlPickingReportTypeDetail **/");
		sql.append("\n  SELECT M.*");
		sql.append("\n  FROM (");
		sql.append("\n    /** SALES_APP **/ ");
		sql.append("\n    SELECT t.picking_no ,t.ORDER_NO ,t.ORDER_DATE ");
		sql.append("\n    ,inv.cust_po_number,inv.invoice_no , inv.invoice_date,inv.ct_reference");
		sql.append("\n    ,(select name from pensso.m_invoice_type ad where ad.type_id = inv.invoice_type_id) as invoice_type");
		sql.append("\n    ,s.code as salesrep_code ,s.name as salesrep_name");
		sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
		
		sql.append("\n    ,ship_address.line1 as s_line1,ship_address.line2 as s_line2,ship_address.line3 as s_line3");
		sql.append("\n    ,ship_address.amphur as s_amphur,ship_address.province as s_province");
		sql.append("\n    ,ship_address.postal_code as s_postal_code ,ship_address.alternate_name as s_alternate_name ");
		
		sql.append("\n    ,bill_address.line1 as b_line1,bill_address.line2 as b_line2,bill_address.line3 as b_line3");
		sql.append("\n    ,bill_address.amphur as b_amphur,bill_address.province as b_province");
		sql.append("\n    ,bill_address.postal_code as b_postal_code ,bill_address.alternate_name as b_alternate_name ");
		
		sql.append("\n    ,(select code from pensso.m_product p where p.product_id = inv_l.inventory_item_id ) as product_code");
		sql.append("\n    ,inv_l.description as product_name ");
		sql.append("\n    ,inv_l.QUANTITY_INVOICED as qty ,inv_l.uom_code ,inv_l.UNIT_STANDARD_PRICE,inv_l.UNIT_SELLING_PRICE");
		sql.append("\n    ,inv_l.total_amount ,inv_l.vat_amount,inv_l.NET_AMOUNT");
		
		sql.append("\n    FROM pensso.t_order t ");
		sql.append("\n    ,pensso.t_invoice inv ,pensso.t_invoice_line inv_l ");
		sql.append("\n    ,pensso.m_customer c,pensso.ad_user s ");
		
		sql.append("\n    ,(select a.customer_id,a.address_id,a.line1,a.line2,a.line3 ,a.postal_code,a.alternate_name ");
		sql.append("\n      ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur ");
		sql.append("\n      ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province ");
		sql.append("\n      from pensso.m_address a where a.purpose ='S'");
		sql.append("\n    ) ship_address");
		
		sql.append("\n    ,(select a.customer_id,a.address_id,a.line1,a.line2,a.line3 ,a.postal_code,a.alternate_name  ");
		sql.append("\n      ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur ");
		sql.append("\n      ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province ");
		sql.append("\n      from pensso.m_address a where a.purpose ='B'");
		sql.append("\n    ) bill_address");
		sql.append("\n    WHERE t.order_no = inv.ref_order");
		sql.append("\n    and inv.invoice_id = inv_l.invoice_id");
		sql.append("\n    and t.customer_id = c.customer_id ");
		sql.append("\n    AND t.user_id = s.user_id ");
		sql.append("\n    AND c.customer_id = ship_address.customer_id ");
		sql.append("\n    AND t.ship_address_id = ship_address.address_id ");
		sql.append("\n    AND c.customer_id = bill_address.customer_id ");
		sql.append("\n    AND t.bill_address_id = bill_address.address_id ");
		
		sql.append("\n    and t.PICKING_NO ='"+o.getPickingNo()+"'");
		sql.append("\n    and t.doc_status ='"+I_PO.STATUS_LOADING+"'");
		
		if(ControlCode.canExecuteMethod("Picking", "OrderEDI")){
			sql.append("\n    UNION ALL ");
			
			sql.append("\n    /** EDI **/ ");
			sql.append("\n    SELECT t.picking_no ,t.CUST_PO_NUMBER AS ORDER_NO ,t.ORDERED_DATE AS ORDER_DATE ");
			sql.append("\n    ,inv.cust_po_number,inv.invoice_no , inv.invoice_date,inv.ct_reference");
			sql.append("\n    ,(select name from pensso.m_invoice_type ad where ad.type_id = inv.invoice_type_id) as invoice_type");
			sql.append("\n    ,s.code as salesrep_code ,s.name as salesrep_name");
			sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
			
			sql.append("\n    ,ship_address.line1 as s_line1,ship_address.line2 as s_line2,ship_address.line3 as s_line3");
			sql.append("\n    ,ship_address.amphur as s_amphur,ship_address.province as s_province");
			sql.append("\n    ,ship_address.postal_code as s_postal_code ,ship_address.alternate_name as s_alternate_name ");
			
			sql.append("\n    ,bill_address.line1 as b_line1,bill_address.line2 as b_line2,bill_address.line3 as b_line3");
			sql.append("\n    ,bill_address.amphur as b_amphur,bill_address.province as b_province");
			sql.append("\n    ,bill_address.postal_code as b_postal_code ,bill_address.alternate_name as b_alternate_name ");
			
			sql.append("\n    ,(select code from pensso.m_product p where p.product_id = inv_l.inventory_item_id ) as product_code");
			sql.append("\n    ,inv_l.description as product_name ");
			sql.append("\n    ,inv_l.QUANTITY_INVOICED as qty ,inv_l.uom_code ,inv_l.UNIT_STANDARD_PRICE,inv_l.UNIT_SELLING_PRICE");
			sql.append("\n    ,inv_l.total_amount ,inv_l.vat_amount,inv_l.NET_AMOUNT");
			
			sql.append("\n    FROM pensso.T_EDI t ");
			sql.append("\n    ,pensso.t_invoice inv ,pensso.t_invoice_line inv_l ");
			sql.append("\n    ,pensso.m_customer c,pensso.ad_user s ");
			
			sql.append("\n    ,(select a.customer_id,a.address_id,a.line1,a.line2,a.line3 ,a.postal_code,a.alternate_name ");
			sql.append("\n      ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur ");
			sql.append("\n      ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province ");
			sql.append("\n      from pensso.m_address a where a.purpose ='S'");
			sql.append("\n    ) ship_address");
			
			sql.append("\n    ,(select a.customer_id,a.address_id,a.line1,a.line2,a.line3 ,a.postal_code,a.alternate_name  ");
			sql.append("\n      ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur ");
			sql.append("\n      ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province ");
			sql.append("\n      from pensso.m_address a where a.purpose ='B'");
			sql.append("\n    ) bill_address");
			
			sql.append("\n    WHERE to_char(t.header_id) = inv.ref_order");
			sql.append("\n    and inv.invoice_id = inv_l.invoice_id");
			sql.append("\n    and t.customer_id = c.customer_id ");
			sql.append("\n    AND t.salesrep_id = s.user_id ");
			sql.append("\n    AND c.customer_id = ship_address.customer_id ");
			sql.append("\n    AND t.ship_to_address_id = ship_address.address_id ");
			sql.append("\n    AND c.customer_id = bill_address.customer_id ");
			sql.append("\n    AND t.bill_to_address_id = bill_address.address_id ");
			
			sql.append("\n    and t.PICKING_NO ='"+o.getPickingNo()+"'");
			sql.append("\n    and t.doc_status ='"+I_PO.STATUS_LOADING+"'");
		}
		sql.append("\n )M ");
		sql.append("\n where 1=1");
		sql.append("\n and M.PICKING_NO ='"+o.getPickingNo()+"'");
		sql.append("\n ORDER BY M.PICKING_NO,M.invoice_no,M.product_code");
		
		return sql;
	}
}
