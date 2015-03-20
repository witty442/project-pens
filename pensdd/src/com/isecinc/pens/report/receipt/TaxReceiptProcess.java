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
public class TaxReceiptProcess extends I_ReportProcess<TaxReceipt> {

	/**
	 * Search for shipment report
	 */
	public List<TaxReceipt> doReport(TaxReceipt t, User user, Connection conn) throws Exception {
		PreparedStatement ppstmt = null;
		ResultSet rst = null;
		List<TaxReceipt> pos = new ArrayList<TaxReceipt>();
		StringBuffer sql = new StringBuffer("select distinct r.receipt_no , cus.code as customerCode , cus.name as customerName, r.receipt_date \n" );
		sql.append("\t\t, sum(rb.receipt_amount) as receipt_amount   ," )
		    .append(" ad.line1 , ad.line2 , ad.line3 , ad.line4\n")
		   .append("\t\t, pv.name as province_name, ad.postal_code ,cus.card_no as creditcard_no , rb.cheque_no , rb.payment_method , r.doc_status as receipt_status \n")
		   .append("from t_receipt r \n")
		   .append("inner join t_receipt_line l on r.receipt_id = l.receipt_id \n")
		   .append("inner join t_receipt_match m on l.receipt_line_id = m.receipt_line_id \n")
		   .append("inner join t_receipt_by rb on m.receipt_by_id = rb.receipt_by_id \n")
		   .append("inner join m_customer cus on cus.customer_id = r.customer_id\n")
		   .append("inner join m_address ad on ad.customer_id = cus.customer_id\n")
		   .append("inner join m_province pv on ad.province_id = pv.province_id\n")
		   .append("inner join t_order_line ol on l.order_line_id = ol.order_line_id \n")
		   .append("where ad.purpose = 'B' \n")
		   .append("and r.receipt_date >= ? and r.receipt_date <= ? \n")
		   .append("and cus.code = coalesce(?,cus.code) \n")
		   .append("group by  r.receipt_no , cus.code  , cus.name  \n" )
		   .append(", r.receipt_date  , ad.line1 , ad.line2 , ad.line3 , ad.line4 \n" )
		   .append(", pv.name , ad.postal_code ,cus.card_no  , rb.cheque_no , rb.payment_method , r.doc_status  \n" )
		   .append("order by r.receipt_no , cus.code ");

		BigDecimal vat7 = new BigDecimal(0.07);
		try {
			ppstmt = conn.prepareStatement(sql.toString());
			
			logger.info("Report SQL "+sql.toString());
			logger.info("Customer Code "+t.getCustomerCode());
			
			ppstmt.setTimestamp(1, DateToolsUtil.convertToTimeStamp(t.getReceiptDateFrom()));
			ppstmt.setTimestamp(2, DateToolsUtil.convertToTimeStamp(t.getReceiptDateTo()));
			ppstmt.setString(3, StringUtils.isEmpty(t.getCustomerCode())?null:t.getCustomerCode());
			
			rst = ppstmt.executeQuery();
			
			while(rst.next()){
				TaxReceipt taxReceipt = new TaxReceipt();
				taxReceipt.setReceiptNo(rst.getString("receipt_no"));
				taxReceipt.setReceiptDate(DateToolsUtil.convertToString(rst.getTimestamp("receipt_date")));
				
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
				
				taxReceipt.setCustomerAddress(address);
				
				taxReceipt.setCustomerCode(rst.getString("customerCode"));
				taxReceipt.setCustomerName(rst.getString("customerName"));
				
				BigDecimal totalAmt = rst.getBigDecimal("receipt_amount");
				BigDecimal vatAmt = totalAmt.multiply(vat7); 
				BigDecimal baseAmt = totalAmt.subtract(vatAmt);
				
				taxReceipt.setLineAmt(baseAmt);
				taxReceipt.setSumLinesAmt(baseAmt);
				taxReceipt.setVatAmt(vatAmt);
				taxReceipt.setSumTotalAmt(totalAmt);
				
				taxReceipt.setCreditCardNo(rst.getString("creditcard_no"));
				taxReceipt.setChequeNo(rst.getString("cheque_no"));
				taxReceipt.setPaymentMethod(rst.getString("payment_method"));
				
				taxReceipt.setReceiptStatus(rst.getString("receipt_status"));
				
				pos.add(taxReceipt);
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
