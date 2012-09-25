package com.isecinc.pens.report.taxinvoice;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;

/**
 * Tax Invoice Report Process
 * 
 * @author Aneak.t
 * @version $Id: TaxInvoiceReportProcess.java,v 1.0 01/12/2010 15:52:00 aneak.t Exp $
 * 
 */

public class TaxInvoiceDDReportProcess extends I_ReportProcess<TaxInvoiceDDReport> {

	/**
	 * Search for report
	 */
	
		

	@Override
	public List<TaxInvoiceDDReport> doReport(TaxInvoiceDDReport t, User user,
			Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<TaxInvoiceDDReport> pos = new ArrayList<TaxInvoiceDDReport>();
		TaxInvoiceDDReport taxInvoice = null;
		StringBuilder sql = new StringBuilder();

		try {
			sql.delete(0, sql.length());
			
			
			sql.append(" select * from ( ");

			sql.append(" SELECT t_receipt.receipt_no,t_receipt.receipt_date,t_receipt_line.invoice_amount,t_receipt_line.credit_amount, ");
			sql.append(" t_receipt_line.paid_amount,t_receipt_line.remain_amount,t_receipt.receipt_amount,t_receipt.customer_id, ");
			sql.append(" t_receipt_line.description,t_order.order_no,t_receipt.customer_name,ad_user.name as sale_name, ");
			sql.append(" CONCAT(ad.LINE1,' ', ad.LINE2,' ', ad.LINE3,' ', ad.LINE4,' ',ad.PROVINCE_NAME,' ',ad.POSTAL_CODE) AS ADDRESS, ");
			sql.append(" m_customer.CODE,m_product.CODE AS PRODUCT_CODE, m_product.NAME AS PRODUCT_NAME, ");
			sql.append(" CASE line.UOM_ID WHEN (SELECT DISTINCT(m_product.UOM_ID) FROM m_product WHERE m_product.PRODUCT_ID = line.PRODUCT_ID) THEN line.QTY ELSE 0 END AS MAIN_QTY, ");
			sql.append(" CASE WHEN line.UOM_ID <> (SELECT DISTINCT(m_product.UOM_ID) FROM m_product WHERE m_product.PRODUCT_ID = line.PRODUCT_ID) ");
			sql.append(" AND line.PRICE <> 0 THEN line.QTY ELSE 0 END AS SUB_QTY, ");
			sql.append(" CASE line.PRICE WHEN 0 THEN line.QTY ELSE 0 END AS ADD_QTY, ");
			sql.append(" (line.PRICE*line.QTY) AS SALE_PRICE,  line.DISCOUNT,(line.LINE_AMOUNT - line.DISCOUNT) AS LINE_AMOUNT, ");
			sql.append(" line.TOTAL_AMOUNT, line.VAT_AMOUNT, line.UOM_ID, ");
			sql.append(" CASE (line.PRICE*line.QTY) WHEN 0 THEN 'Y' ELSE 'N' END AS IS_ADD ");
			
			sql.append(" FROM t_receipt ");
			sql.append(" left join t_receipt_line on t_receipt.receipt_id = t_receipt_line.receipt_id ");
			sql.append(" left join t_order on t_receipt_line.order_id = t_order.order_id ");
			
			sql.append(" left join t_order_line line on t_order.order_id = line.order_id ");
			sql.append(" left join m_customer on t_receipt.customer_id = m_customer.customer_id ");
			sql.append(" left join ad_user on t_receipt.user_id = ad_user.user_id ");
			sql.append(" left join m_address ad on t_order.BILL_ADDRESS_ID = ad.ADDRESS_ID ");
			sql.append(" LEFT JOIN m_product  ON line.PRODUCT_ID = m_product.PRODUCT_ID ");
			sql.append(" where  t_receipt_line.order_line_id is not null and t_receipt.doc_status != 'VO' ");
			
			if(!t.getReceiptDateFrom().isEmpty() || !t.getReceiptDateFrom().equalsIgnoreCase("")){
				sql.append(" AND t_receipt.RECEIPT_DATE >= '" + DateToolsUtil.convertToTimeStamp(t.getReceiptDateFrom()) + "' ");
				sql.append(" AND t_receipt.RECEIPT_DATE <= '" + DateToolsUtil.convertToTimeStamp(t.getReceiptDateTo()) + "' ");
			}
			
			if(!t.getReceiptNo().isEmpty()){
				sql.append(" And t_receipt.receipt_no = '" +t.getReceiptNo() + "' ");
			}
			
			if(t.getCustomerCodeTo() != ""){
				if(t.getCustomerCodeFrom() != ""){
					sql.append(" AND m_customer.CODE >= '"+t.getCustomerCodeFrom() +"' AND m_customer.CODE <= '"+t.getCustomerCodeTo() +"'");
				}			
			}else{
				if(t.getCustomerCodeFrom() != ""){
					sql.append(" AND m_customer.CODE = '"+t.getCustomerCodeFrom() +"' ");
					
				}
			}
			
			sql.append(" group by t_receipt_line.order_line_id ");
			 
			 
			sql.append(" union all ");
			
			sql.append(" SELECT t_receipt.receipt_no,t_receipt.receipt_date,t_receipt_line.invoice_amount,t_receipt_line.credit_amount, ");
			sql.append(" t_receipt_line.paid_amount,t_receipt_line.remain_amount,t_receipt.receipt_amount,t_receipt.customer_id, ");
			sql.append(" t_receipt_line.description,t_order.order_no,t_receipt.customer_name,ad_user.name as sale_name, ");
			sql.append(" CONCAT(ad.LINE1,' ', ad.LINE2,' ', ad.LINE3,' ', ad.LINE4,' ',ad.PROVINCE_NAME,' ',ad.POSTAL_CODE) AS ADDRESS, ");
			sql.append(" m_customer.CODE,m_product.CODE AS PRODUCT_CODE, m_product.NAME AS PRODUCT_NAME, ");
			sql.append(" CASE line.UOM_ID WHEN (SELECT DISTINCT(m_product.UOM_ID) FROM m_product WHERE m_product.PRODUCT_ID = line.PRODUCT_ID) THEN line.QTY ELSE 0 END AS MAIN_QTY, ");
			sql.append(" CASE WHEN line.UOM_ID <> (SELECT DISTINCT(m_product.UOM_ID) FROM m_product WHERE m_product.PRODUCT_ID = line.PRODUCT_ID) ");
			sql.append(" AND line.PRICE <> 0 THEN line.QTY ELSE 0 END AS SUB_QTY, ");
			sql.append(" CASE line.PRICE WHEN 0 THEN line.QTY ELSE 0 END AS ADD_QTY, ");
			sql.append(" (line.PRICE*line.QTY) AS SALE_PRICE,  line.DISCOUNT,(line.LINE_AMOUNT - line.DISCOUNT) AS LINE_AMOUNT, ");
			sql.append(" line.TOTAL_AMOUNT, line.VAT_AMOUNT, line.UOM_ID, ");
			sql.append(" CASE (line.PRICE*line.QTY) WHEN 0 THEN 'Y' ELSE 'N' END AS IS_ADD ");
			
			sql.append(" FROM t_receipt ");
			sql.append(" left join t_receipt_line on t_receipt.receipt_id = t_receipt_line.receipt_id ");
			sql.append(" left join t_order on t_receipt_line.order_id = t_order.order_id ");
			
			sql.append(" left join t_order_line line on t_order.order_id = line.order_id ");
			sql.append(" left join m_customer on t_receipt.customer_id = m_customer.customer_id ");
			sql.append(" left join ad_user on t_receipt.user_id = ad_user.user_id ");
			sql.append(" left join m_address ad on t_order.BILL_ADDRESS_ID = ad.ADDRESS_ID ");
			sql.append(" LEFT JOIN m_product  ON line.PRODUCT_ID = m_product.PRODUCT_ID ");
			sql.append(" where  t_receipt_line.order_line_id is  null and t_receipt.doc_status != 'VO' ");
			
			if(!t.getReceiptDateFrom().isEmpty() || !t.getReceiptDateFrom().equalsIgnoreCase("")){
				sql.append(" AND t_receipt.RECEIPT_DATE >= '" + DateToolsUtil.convertToTimeStamp(t.getReceiptDateFrom()) + "' ");
				sql.append(" AND t_receipt.RECEIPT_DATE <= '" + DateToolsUtil.convertToTimeStamp(t.getReceiptDateTo()) + "' ");
			}
			
			if(!t.getReceiptNo().isEmpty()){
				sql.append(" And t_receipt.receipt_no = '" +t.getReceiptNo() + "' ");
			}
			
			if(t.getCustomerCodeTo() != ""){
				if(t.getCustomerCodeFrom() != ""){
					sql.append(" AND m_customer.CODE >= '"+t.getCustomerCodeFrom() +"' AND m_customer.CODE <= '"+t.getCustomerCodeTo() +"'");
				}			
			}else{
				if(t.getCustomerCodeFrom() != ""){
					sql.append(" AND m_customer.CODE = '"+t.getCustomerCodeFrom() +"' ");
					
				}
			}
			
			sql.append("  ) a");
			//sql.append(" WHERE 1 = 1 ");
			
			
			
			

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int i = 1;
			while (rst.next()) {
				taxInvoice = new TaxInvoiceDDReport();
				taxInvoice.setId(i++);
				taxInvoice.setOrderNo(rst.getString("ORDER_NO"));
				
				taxInvoice.setDescription(rst.getString("description"));
				taxInvoice.setInvoiceAmount(rst.getDouble("invoice_amount"));
				taxInvoice.setCreditAmount(rst.getDouble("credit_amount"));
				taxInvoice.setPaidAmount(rst.getDouble("paid_amount"));
				taxInvoice.setRemainAmount(rst.getDouble("remain_amount"));
				taxInvoice.setCustomerCode(rst.getString("CODE"));
				taxInvoice.setCustomerName(rst.getString("customer_name"));
				
				taxInvoice.setReceiptNo(rst.getString("RECEIPT_NO"));
				taxInvoice.setReceiptDate(DateToolsUtil.convertToString(rst.getDate("RECEIPT_DATE")));
				taxInvoice.setSaleName(rst.getString("sale_name"));
				
				taxInvoice.setAddress(rst.getString("ADDRESS"));
				taxInvoice.setReceiptAmount(rst.getDouble("receipt_amount"));
				
				taxInvoice.setUomId(rst.getString("UOM_ID"));
				taxInvoice.setMainQty(rst.getInt("MAIN_QTY"));
				taxInvoice.setSubQty(rst.getInt("SUB_QTY"));
				taxInvoice.setDiscount(rst.getDouble("DISCOUNT"));
				taxInvoice.setProductCode(rst.getString("PRODUCT_CODE"));
				taxInvoice.setProductName(rst.getString("PRODUCT_NAME"));
				taxInvoice.setLineAmount(rst.getDouble("LINE_AMOUNT"));
				taxInvoice.setSalePrice(rst.getDouble("SALE_PRICE"));
				taxInvoice.setTotalAmount(rst.getDouble("TOTAL_AMOUNT"));
				
				pos.add(taxInvoice);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}

		return pos;
	}

}
