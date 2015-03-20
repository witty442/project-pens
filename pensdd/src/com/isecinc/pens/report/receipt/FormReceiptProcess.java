package com.isecinc.pens.report.receipt;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;

/**
 * Shipment Report Process
 * 
 * @author Aneak.t
 * @version $Id: ShipmentReportProcess.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */
public class FormReceiptProcess extends I_ReportProcess<FormReceipt> {

	/**
	 * Search for shipment report
	 */
	public List<FormReceipt> doReport(FormReceipt t, User user, Connection conn) throws Exception {
		PreparedStatement ppstmt = null;
		ResultSet rst = null;
		List<FormReceipt> pos = new ArrayList<FormReceipt>();
		StringBuffer sql = new StringBuffer("select  r.receipt_no , cus.code as customerCode , cus.name as customerName \n" );
		sql.append("\t\t, r.receipt_date , ad.line1 , ad.line2 , ad.line3 , ad.line4\n")
		   .append("\t\t, pv.name as province_name, ad.postal_code ,cus.card_no as creditcard_no , rb.cheque_no , rb.payment_method , r.doc_status as receipt_status \n")
		   .append("\t\t, sum(rb.receipt_amount) as receipt_amount \n")
		   .append("from t_receipt r \n")
		   .append("inner join t_receipt_line l on r.receipt_id = l.receipt_id \n")
		   .append("inner join t_receipt_match m on l.receipt_line_id = m.receipt_line_id \n")
		   .append("inner join t_receipt_by rb on m.receipt_by_id = rb.receipt_by_id \n")
		   .append("inner join m_customer cus on cus.customer_id = r.customer_id\n")
		   .append("inner join m_address ad on ad.customer_id = cus.customer_id\n")
		   .append("inner join m_province pv on ad.province_id = pv.province_id\n")
		   .append("inner join t_order_line ol on l.order_line_id = ol.order_line_id \n")
		   .append("where  ad.purpose = 'B' \n")
		   .append("and r.receipt_date >= ? and r.receipt_date <= ? \n")
		   .append("and cus.code = coalesce(?,cus.code) \n")
		   .append("group by  r.receipt_no , cus.code  , cus.name  \n" )
		   .append(", r.receipt_date  , ad.line1 , ad.line2 , ad.line3 , ad.line4 \n" )
		   .append(", pv.name , ad.postal_code ,cus.card_no  , rb.cheque_no , rb.payment_method , r.doc_status  \n" )
		   .append("order by r.receipt_no , cus.code ");

		try {
			ppstmt = conn.prepareStatement(sql.toString());
			
			logger.info("Report SQL "+sql.toString());
			logger.info("Customer Code "+t.getCustomerCode());
			
			ppstmt.setTimestamp(1, DateToolsUtil.convertToTimeStamp(t.getReceiptDateFrom()));
			ppstmt.setTimestamp(2, DateToolsUtil.convertToTimeStamp(t.getReceiptDateTo()));
			ppstmt.setString(3, StringUtils.isEmpty(t.getCustomerCode())?null:t.getCustomerCode());
			
			rst = ppstmt.executeQuery();
			
			while(rst.next()){
				FormReceipt receiptFrm = new FormReceipt();
				receiptFrm.setReceiptNo(rst.getString("receipt_no"));
				receiptFrm.setReceiptDate(DateToolsUtil.convertToString(rst.getTimestamp("receipt_date")));
				
				String line1 = rst.getString("line1");
				String line2 = rst.getString("line2");
				String line3 = rst.getString("line3");
				String line4 = rst.getString("line4");
				String provinceName = rst.getString("province_name");
				String postal = rst.getString("postal_code");
				
				String address = line1;
				address +=(StringUtils.isEmpty(line2)?"":" "+line2);
				address +=(StringUtils.isEmpty(line3)?"":" "+line3);
				address	+=(StringUtils.isEmpty(line4)?"":" "+line4);
				
				address += " "+provinceName ;
				address += " "+postal;
				
				receiptFrm.setCustomerAddress(address);
				
				receiptFrm.setCustomerCode(rst.getString("customerCode"));
				receiptFrm.setCustomerName(rst.getString("customerName"));
				
				BigDecimal totalAmt = rst.getBigDecimal("receipt_amount");
				//BigDecimal onehoundred = Constants.ONE_HUNDRED;
				//BigDecimal baseAmt = totalAmt.multiply(util.Constants.ONE_HUNDRED).divide(util.Constants.ONE_HUNDRED_SEVEN,2,2);
				//BigDecimal vatAmt = totalAmt.subtract(baseAmt);
				
				receiptFrm.setLineAmt(totalAmt);
				receiptFrm.setSumLinesAmt(totalAmt);
				receiptFrm.setCreditCardNo(rst.getString("creditcard_no"));
				receiptFrm.setChequeNo(rst.getString("cheque_no"));
				receiptFrm.setPaymentMethod(rst.getString("payment_method"));
				
				receiptFrm.setReceiptStatus(rst.getString("receipt_status"));
				
				pos.add(receiptFrm);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ppstmt.close();
			} catch (Exception e) {}
		}

		return pos;
	}

}
