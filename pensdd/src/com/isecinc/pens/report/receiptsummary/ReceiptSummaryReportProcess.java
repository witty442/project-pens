package com.isecinc.pens.report.receiptsummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jfree.util.Log;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;

/**
 * Detailed Sales Report
 * 
 * @author Aneak.t
 * @version $Id: DetailedSalesReport.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class ReceiptSummaryReportProcess extends I_ReportProcess<ReceiptSummaryReport>{

	/**
	 * Search for report.
	 */
	public List<ReceiptSummaryReport> doReport(ReceiptSummaryReport receiptSumRpt, User user,
			Connection conn) throws Exception {
		
		List<ReceiptSummaryReport> lstData = new ArrayList<ReceiptSummaryReport>();
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT t.* FROM (SELECT distinct rb.payment_method ,'A' as type ,rh.receipt_date as document_date, rh.receipt_no as document_no \n")
			.append("\t\t, cus.code as customer_code , concat(cus.name,' ',cus.name2) as customer_name\n")
			// .append("\t\t,th.lines_amount ,th.vat_amount, th.total_amount\n")
			.append("\t\t, rh.receipt_amount as lines_amount , 0 as vat_amount , rh.receipt_amount as total_amount\n")
			.append("FROM t_receipt rh INNER JOIN t_receipt_line rl ON rh.receipt_id = rl.receipt_id\n")
			.append("INNER JOIN m_customer cus ON cus.customer_id = rh.customer_id\n")
			//.append("INNER JOIN t_taxinvoice_line tl ON tl.order_line_id = rl.order_line_id\n")
			//.append("INNER JOIN t_taxinvoice th ON th.taxinvoice_id = tl.taxinvoice_id\n")
			.append("INNER JOIN t_receipt_by rb ON rb.receipt_id = rh.receipt_id\n")
			.append("WHERE rh.doc_status = 'SV' AND rh.taxinvoice_id is null\n")
			.append("UNION SELECT distinct rb.payment_method , 'B' as type , rh.receipt_date as document_date, rh.receipt_no as document_no\n")
			.append("\t\t, cus.code as customer_code , concat(cus.name,' ',cus.name2) as customer_name\n")
			.append("\t\t,th.lines_amount ,th.vat_amount, th.total_amount\n")
			.append("FROM t_receipt rh INNER JOIN t_taxinvoice th ON rh.taxinvoice_id = th.taxinvoice_id\n")
			.append("INNER JOIN m_customer cus ON cus.customer_id = rh.customer_id\n")
			.append("INNER JOIN t_receipt_by rb ON rb.receipt_id = rh.receipt_id\n")
			.append("WHERE rh.doc_status = 'SV'\n")
			.append("UNION SELECT distinct ol.payment_method , 'C' as type , sh.shipment_date as document_date, concat(od.order_no,'/',ol.trip_no) as document_no\n")
			.append("\t\t,cus.code as customer_code , concat(cus.name,' ',cus.name2) as customer_name\n")
			.append("\t\t,sh.lines_amount ,sh.vat_amount, sh.total_amount\n")
			.append("FROM t_shipment sh INNER JOIN t_shipment_line sl ON sh.shipment_id = sl.shipment_id\n")
			.append("INNER JOIN t_order_line ol on sl.order_line_id = ol.order_line_id\n")
			.append("INNER JOIN t_order od on od.order_id = ol.order_id\n")
			.append("INNER JOIN m_customer cus ON cus.customer_id = od.customer_id\n")
			.append("WHERE sh.taxinvoice_id is null and ol.prepay = 'Y' and sh.shipment_status = 'SV' ) t \n")
			.append("WHERE t.document_date >= ? AND t.document_date <= ? \n")
			.append("AND t.payment_method = coalesce(?,t.payment_method) \n")
			.append("ORDER BY t.payment_method DESC,t.type ");
		
		logger.info("SQL => "+sql.toString());
		logger.info("Receipt Date From "+receiptSumRpt.getReceiptDateFrom());
		logger.info("Receipt Date To "+receiptSumRpt.getReceiptDateTo());
		logger.info("Payment Method "+receiptSumRpt.getPaymentMethod());
		
		PreparedStatement ppstmt = conn.prepareStatement(sql.toString());
		ResultSet rset = null;
		
		try {
			ppstmt.setDate(1,new java.sql.Date(DateToolsUtil.convertToTimeStamp(receiptSumRpt.getReceiptDateFrom()).getTime()));
			ppstmt.setDate(2, new java.sql.Date(DateToolsUtil.convertToTimeStamp(receiptSumRpt.getReceiptDateTo()).getTime()));
			if(StringUtils.isEmpty(receiptSumRpt.getPaymentMethod()))
				ppstmt.setString(3,null);
			else
				ppstmt.setString(3, receiptSumRpt.getPaymentMethod());
			
			rset = ppstmt.executeQuery();
			
			while(rset.next()){
				ReceiptSummaryReport receiptSum = new ReceiptSummaryReport();
				receiptSum.setReceiptDateFrom(receiptSumRpt.getReceiptDateFrom());
				receiptSum.setReceiptDateTo(receiptSumRpt.getReceiptDateTo());
				
				receiptSum.setPaymentMethod(rset.getString("payment_method"));
				receiptSum.setReceiptType(rset.getString("type"));
				receiptSum.setCustomerCode(rset.getString("customer_code"));
				receiptSum.setCustomerName(rset.getString("customer_name"));
				receiptSum.setReceiptDate(DateToolsUtil.convertToString(rset.getTimestamp("document_date")));
				receiptSum.setReceiptNo(rset.getString("document_no"));
				receiptSum.setTaxBaseAmt(rset.getBigDecimal("lines_amount"));
				receiptSum.setVatAmt(rset.getBigDecimal("vat_amount"));
				receiptSum.setTotalAmt(rset.getBigDecimal("total_amount"));
				
				lstData.add(receiptSum);
			}
			
		} catch (Exception e) {
			throw e;
		}finally{
			try {
				rset.close();
				rset = null;
				ppstmt.close();
				ppstmt = null;
			} catch (Exception e2) {}
		}
		
		return lstData;
	}
}
