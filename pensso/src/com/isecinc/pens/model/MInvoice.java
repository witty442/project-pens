package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Invoice;
import com.isecinc.pens.bean.InvoiceLine;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.PD;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class MInvoice {
	
	private static Logger logger = Logger.getLogger("PENS");
	
	/**
	 * get only exported_to_sales ='N'
	 *  credit sale (no buds credit sales zone = 92)
	 * @return
	 */
	public static List<Invoice> getInvoiceListToSalesApp()  {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rst = null;
		Invoice inv = null;
		List<Invoice> p = new ArrayList<Invoice>();
		String sql  = " \n SELECT * from pensso.t_invoice v ,apps.xxpens_salesreps_v s ";
		       sql += " \n where v.primary_salesrep_id = s.salesrep_id ";
		       sql += " \n and (v.exported_to_sales is null or v.exported_to_sales ='N') ";
		       sql += " \n and s.zone <> '92' ";
					 
		logger.debug("sql:"+sql);
		try {
			conn = DBConnectionApps.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rst = ps.executeQuery();
			
			while(rst.next()){
				inv = new Invoice();
				inv.setInvoiceId(rst.getLong("invoice_id"));
				inv.setInvoiceNo(Utils.isNull(rst.getString("invoice_no")));
				inv.setInvoiceDate(DateUtil.stringValue(rst.getDate("invoice_date"), DateUtil.DD_MM_YYYY_WITHOUT_SLASH,DateUtil.local_th));
				inv.setInvoiceTypeId(rst.getLong("invoice_type_id"));
				inv.setCtReference(Utils.isNull(rst.getString("CT_REFERENCE")));
				inv.setOrderType(Utils.isNull(rst.getString("ORDER_TYPE")));
				inv.setBillToCustomerId(rst.getLong("BILL_TO_CUSTOMER_ID"));
				inv.setBillToSiteUseId(rst.getLong("BILL_TO_SITE_USE_ID"));
				inv.setShipToCustomerId(rst.getLong("SHIP_TO_CUSTOMER_ID"));
				inv.setBillToSiteUseId(rst.getLong("SHIP_TO_SITE_USE_ID"));
				inv.setTermId(rst.getLong("term_id"));
				inv.setAttribute1(Utils.isNull(rst.getString("ATTRIBUTE1")));
				inv.setAttribute2(Utils.isNull(rst.getString("ATTRIBUTE2")));
				inv.setAttribute3(Utils.isNull(rst.getString("ATTRIBUTE3")));
				inv.setStatus(Utils.isNull(rst.getString("status")));
				inv.setClassT(Utils.isNull(rst.getString("class")));
				inv.setOrderCategoryCode(Utils.isNull(rst.getString("ORDER_CATEGORY_CODE")));
				inv.setRefOrder(Utils.isNull(rst.getString("ref_order")));
				inv.setCustPONumber(Utils.isNull(rst.getString("CUST_PO_NUMBER")));
				inv.setTotalAmount(Utils.decimalFormat(rst.getDouble("TOTAL_AMOUNT"),Utils.format_current_5_digit));
				inv.setVatAmount(Utils.decimalFormat(rst.getDouble("VAT_AMOUNT"),Utils.format_current_5_digit));
				inv.setNetAmount(Utils.decimalFormat(rst.getDouble("NET_AMOUNT"),Utils.format_current_5_digit));
				inv.setPrimarySalesrepId(rst.getLong("PRIMARY_SALESREP_ID"));
				
				//get invoiceList List
				inv.setInvoiceLineList(getInvoiceLineListToSalesApp(inv.getInvoiceId()));
				
				p.add(inv);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				conn.close();
				ps.close();
			} catch (Exception e2) {}
		}
		
		return p;
	}

	public static List<InvoiceLine> getInvoiceLineListToSalesApp(long invoiceId) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rst = null;
		InvoiceLine inv = null;
		List<InvoiceLine> p = new ArrayList<InvoiceLine>();
		String sql  = " \n SELECT * from pensso.t_invoice_line where ";
		       sql += " \n invoice_id ="+invoiceId;
					 
		logger.debug("sql:"+sql);
		try {
			conn = DBConnectionApps.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rst = ps.executeQuery();
			
			while(rst.next()){
				inv = new InvoiceLine();
				inv.setInvoiceId(rst.getLong("invoice_id"));
				inv.setInvoiceLineId(rst.getLong("invoice_line_id"));
				inv.setLineNumber(rst.getLong("line_number"));
				inv.setReasonCode(Utils.isNull(rst.getString("reason_code")));
				inv.setInventoryItemId(rst.getLong("INVENTORY_ITEM_ID"));
				inv.setDescription(Utils.isNull(rst.getString("DESCRIPTION")));
				inv.setUomCode(Utils.isNull(rst.getString("uom_code")));
				inv.setQuantityOrdered(rst.getInt("QUANTITY_ORDERED"));
				inv.setQuantityCredited(rst.getInt("QUANTITY_CREDITED"));
				inv.setQuantityInvoiced(rst.getInt("QUANTITY_INVOICED"));
				inv.setUnitStandardPrice(Utils.decimalFormat(rst.getDouble("UNIT_STANDARD_PRICE"),Utils.format_current_5_digit));
				inv.setUnitSellingPrice(Utils.decimalFormat(rst.getDouble("UNIT_SELLING_PRICE"),Utils.format_current_5_digit));
				inv.setSalesOrder(Utils.isNull(rst.getString("sales_order")));
				inv.setSalesOrder(Utils.isNull(rst.getString("sales_order")));
				inv.setSalesOrderLine(Utils.isNull(rst.getString("sales_order_line")));
				inv.setSalesOrderDate(DateUtil.stringValue(rst.getDate("sales_order_date"), DateUtil.DD_MM_YYYY_WITHOUT_SLASH,DateUtil.local_th));
				inv.setTotalAmount(Utils.decimalFormat(rst.getDouble("TOTAL_AMOUNT"),Utils.format_current_5_digit));
				inv.setVatAmount(Utils.decimalFormat(rst.getDouble("VAT_AMOUNT"),Utils.format_current_5_digit));
				inv.setNetAmount(Utils.decimalFormat(rst.getDouble("NET_AMOUNT"),Utils.format_current_5_digit));
		
				p.add(inv);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				conn.close();
				ps.close();
			} catch (Exception e2) {}
		}
		
		return p;
	}
	
	public static int updateExportToSalesInvoice(String invoiceIdArr){
		Connection conn = null;
		PreparedStatement ps = null; 
		
		int r = 0;
		try {
			
			String sql  = "    update pensso.t_invoice set exported_to_sales ='Y' ";
		           sql += " \n where invoice_id in("+SQLHelper.converToTextSqlIn(invoiceIdArr)+")";
		       
		    logger.debug("sql:"+sql);
		       
			conn = DBConnectionApps.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			r= ps.executeUpdate();
	    } catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				conn.close();
				ps.close();
			} catch (Exception e2) {}
	    }
		return r;
	}
}
