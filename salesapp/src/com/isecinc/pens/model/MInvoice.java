package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Invoice;
import com.isecinc.pens.bean.InvoiceLine;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class MInvoice {
	
	private static Logger logger = Logger.getLogger("PENS");
	
	public boolean insertInvoice(List<Invoice> invoiceList) throws Exception {
		Connection conn = null;
		boolean r = false;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			/** insert invoice to DB **/
			MInvoice mInv = new MInvoice();
			
			if(invoiceList != null && invoiceList.size() >0) {
				for(int i=0;i<invoiceList.size();i++) {
					Invoice invoice = invoiceList.get(i);
					mInv.insertInvoiceModel(conn,invoice);
					
					//insert invoice line
					if(invoice.getInvoiceLineList() != null && invoice.getInvoiceLineList().size()>0) {
						for(int j=0;j<invoice.getInvoiceLineList().size();j++) {
							InvoiceLine invLine = invoice.getInvoiceLineList().get(j);
							insertInvoiceLineModel(conn, invLine);
						}//for 2
					}//if 2
				}//for 1
			}//if 1
			r = true;
			conn.commit();
			return r;
		} catch (Exception ex) {
			conn.rollback();
			throw ex;
		} finally {
			
			if(conn != null){
				conn.close();conn = null;
			}
		}
	}
	public boolean insertInvoiceModel(Connection conn,Invoice invoice) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		int index = 0;
		try {
			
			StringBuffer sql = new StringBuffer("insert into pensonline.t_invoice \n");
			sql.append(" (INVOICE_ID,INVOICE_NO ,INVOICE_DATE ,\n");
			sql.append(" INVOICE_TYPE_ID,CT_REFERENCE ,ORDER_TYPE ,\n");
			sql.append(" BILL_TO_CUSTOMER_ID ,BILL_TO_SITE_USE_ID ,SHIP_TO_CUSTOMER_ID ,\n");
			sql.append(" SHIP_TO_SITE_USE_ID,TERM_ID ,ATTRIBUTE1 ,\n");
			sql.append(" ATTRIBUTE2 ,ATTRIBUTE3 ,ATTRIBUTE4 ,\n");
			sql.append(" ATTRIBUTE5 ,ATTRIBUTE6 ,ATTRIBUTE7 ,\n");
			sql.append(" ATTRIBUTE8 ,ATTRIBUTE9 ,ATTRIBUTE10 ,\n");
			sql.append(" ATTRIBUTE11 ,ATTRIBUTE12 ,INTERFACE_HEADER_CONTEXT ,\n");
			sql.append(" INTERFACE_HEADER_ATTRIBUTE1 ,INTERFACE_HEADER_ATTRIBUTE2 ,STATUS ,\n");
			sql.append(" CLASS ,ORDER_CATEGORY_CODE ,REF_ORDER ,\n");
			sql.append(" CUST_PO_NUMBER ,TOTAL_AMOUNT ,VAT_AMOUNT ,\n");
			sql.append(" NET_AMOUNT ,PRIMARY_SALESREP_ID ,CREATE_DATE ,\n");
			sql.append(" CREAT_USER ) \n"); //37
			sql.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) \n"); 
			
			//logger.debug("SQL:"+sql);

			ps = conn.prepareStatement(sql.toString());
			ps.setLong(++index, invoice.getInvoiceId());
			ps.setString(++index, invoice.getInvoiceNo());
			ps.setDate(++index, new java.sql.Date(DateUtil.parse(invoice.getInvoiceDate(), DateUtil.DD_MM_YYYY_WITHOUT_SLASH,DateUtil.local_th).getTime()));
			ps.setLong(++index, invoice.getInvoiceTypeId());
			ps.setString(++index, invoice.getCtReference());
			ps.setString(++index, invoice.getOrderType());
			ps.setLong(++index, invoice.getBillToCustomerId());
			ps.setLong(++index, invoice.getBillToSiteUseId());
			ps.setLong(++index, invoice.getShipToCustomerId());
			ps.setLong(++index, invoice.getShipToSiteUseId());
			ps.setLong(++index, invoice.getTermId());
			ps.setString(++index, invoice.getAttribute1());
			ps.setString(++index, invoice.getAttribute2());
			ps.setString(++index, invoice.getAttribute3());
			ps.setString(++index, invoice.getAttribute4());
			ps.setString(++index, invoice.getAttribute5());
			ps.setString(++index, invoice.getAttribute6());
			ps.setString(++index, invoice.getAttribute7());
			ps.setString(++index, invoice.getAttribute8());
			ps.setString(++index, invoice.getAttribute9());
			ps.setString(++index, invoice.getAttribute10());
			ps.setString(++index, invoice.getAttribute11());
			ps.setString(++index, invoice.getAttribute12());
			ps.setString(++index, invoice.getInterfaceHeaderContext());
			ps.setString(++index, invoice.getInterfaceHeaderAttribute1());
			ps.setString(++index, invoice.getInterfaceHeaderAttribute2());
			ps.setString(++index, invoice.getStatus());
			ps.setString(++index, invoice.getClassT());
			ps.setString(++index, invoice.getOrderCategoryCode());
			ps.setString(++index, invoice.getRefOrder());
			ps.setString(++index, invoice.getCustPONumber());
			ps.setDouble(++index, Utils.convertStrToDouble(invoice.getTotalAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(invoice.getVatAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(invoice.getNetAmount()));
			ps.setLong(++index, invoice.getPrimarySalesrepId());
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, "999");//updated_by 999 System
			
			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("ins:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
			
		}
		return result;
	}
	
	public boolean insertInvoiceLineModel(Connection conn,InvoiceLine invoice) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		int index = 0;
		try {
			StringBuffer sql = new StringBuffer("insert into pensonline.t_invoice_line \n");
			sql.append(" (INVOICE_ID,INVOICE_LINE_ID ,LINE_NUMBER ,\n");
			sql.append(" REASON_CODE,INVENTORY_ITEM_ID ,DESCRIPTION ,\n");
			sql.append(" UOM_CODE ,QUANTITY_ORDERED ,QUANTITY_CREDITED ,\n");
			sql.append(" QUANTITY_INVOICED,UNIT_STANDARD_PRICE ,UNIT_SELLING_PRICE ,\n");
			sql.append(" SALES_ORDER ,SALES_ORDER_LINE ,SALES_ORDER_DATE ,\n");
			sql.append(" INTERFACE_LINE_CONTEXT ,INTERFACE_LINE_ATTRIBUTE1 ,INTERFACE_LINE_ATTRIBUTE2,INTERFACE_LINE_ATTRIBUTE6 ,\n");
			sql.append(" TOTAL_AMOUNT ,VAT_AMOUNT ,NET_AMOUNT , \n");
			sql.append(" CREATE_DATE, CREAT_USER ) \n"); //24
			sql.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) \n"); 
			
			//logger.debug("SQL:"+sql);

			ps = conn.prepareStatement(sql.toString());
			
			ps.setLong(++index, invoice.getInvoiceId());
			ps.setLong(++index, invoice.getInvoiceLineId());
			ps.setLong(++index, invoice.getLineNumber());
			ps.setString(++index, invoice.getReasonCode());
			ps.setLong(++index, invoice.getInventoryItemId());
			ps.setString(++index, invoice.getDescription());
			ps.setString(++index, invoice.getUomCode());
			ps.setInt(++index, invoice.getQuantityOrdered());
			ps.setInt(++index, invoice.getQuantityCredited());
			ps.setInt(++index, invoice.getQuantityInvoiced());
			ps.setDouble(++index, Utils.convertStrToDouble(invoice.getUnitStandardPrice()));
			ps.setDouble(++index, Utils.convertStrToDouble(invoice.getUnitSellingPrice()));
			ps.setString(++index, invoice.getSalesOrder());
			ps.setString(++index, invoice.getSalesOrderLine());
			ps.setDate(++index, new java.sql.Date(DateUtil.parse(invoice.getSalesOrderDate(), DateUtil.DD_MM_YYYY_WITHOUT_SLASH,DateUtil.local_th).getTime()));
		
			ps.setString(++index, invoice.getInterfaceLineContext());
			ps.setString(++index, invoice.getInterfaceLineAttribute1());
			ps.setString(++index, invoice.getInterfaceLineAttribute2());
			ps.setString(++index, invoice.getInterfaceLineAttribute6());
			
			ps.setDouble(++index, Utils.convertStrToDouble(invoice.getTotalAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(invoice.getVatAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(invoice.getNetAmount()));
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, "999");//updated_by 999 System
			
			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("ins:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
			
		}
		return result;
	}
	/**
	 * get only exported_to_sales ='N'
	 * @return
	 */
	public static List<Invoice> getInvoiceList(long invoiceId)  {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rst = null;
		Invoice inv = null;
		List<Invoice> p = new ArrayList<Invoice>();
		String sql  = " \n SELECT * from pensonline.t_invoice where ";
		       sql += " \n (exported_to_sales is null or  exported_to_sales ='N') ";
					 
		logger.debug("sql:"+sql);
		try {
			conn = DBConnection.getInstance().getConnection();
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

	public static List<InvoiceLine> getInvoiceLineList(long invoiceId) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rst = null;
		InvoiceLine inv = null;
		List<InvoiceLine> p = new ArrayList<InvoiceLine>();
		String sql  = " \n SELECT * from pensonline.t_invoice_line where ";
		       sql += " \n invoice_id ="+invoiceId;
					 
		logger.debug("sql:"+sql);
		try {
			conn = DBConnection.getInstance().getConnection();
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
	
	public static int updateExportToSalesInvoice_TEMP(List<Invoice> invoiceList) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null; 
		String invoiceIdArr = "";
		int r = 0;
		try {
			if(invoiceList != null && invoiceList.size() >0){
				for(int i=0;i<invoiceList.size();i++){
					invoiceIdArr +=invoiceList.get(i).getInvoiceId()+",";
				}
				invoiceIdArr = invoiceIdArr.substring(0,invoiceIdArr.length()-1);
			}
			String sql  = " \n update pensonline.t_invoice_line set exported_to_sales ='Y' ";
		          sql += " \n where invoice_id in("+SQLHelper.converToTextSqlIn(invoiceIdArr)+")";
		       
		       logger.debug("sql:"+sql);
		       
			conn = DBConnection.getInstance().getConnection();
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
