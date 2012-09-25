package com.isecinc.pens.report.receipttemp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.NumtoWord;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;

/**
 * Receipt Temporary Report Process
 * 
 * @author Aneak.t
 * @version $Id: ReceiptTempReportProcess.java,v 1.0 17/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ReceiptTempReportProcess extends I_ReportProcess<ReceiptTempReport> {

	/**
	 * Search for report
	 */
	public List<ReceiptTempReport> doReport(ReceiptTempReport t, User user, Connection conn) throws Exception {
		List<ReceiptTempReport> pos = new ArrayList<ReceiptTempReport>();
		ReceiptTempReport receiptTemp = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();

		try {
			sql.delete(0, sql.length());
			sql.append(" SELECT rc.RECEIPT_NO, cus.CODE AS CUSTOMER_CODE, ");
			sql.append(" cus.NAME AS CUSTOMER_NAME,rc.RECEIPT_AMOUNT ");
			sql.append(" FROM t_receipt rc ");
			sql.append(" INNER JOIN m_customer cus ON rc.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append(" WHERE cus.CUSTOMER_ID = " + t.getCustomerId());
			sql.append(" AND rc.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			sql.append(" AND rc.DOC_STATUS = 'SV' ");
			sql.append(" AND cus.CUSTOMER_TYPE = 'DD' ");

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int i = 1;
			NumtoWord numToWord = new NumtoWord();
			while (rst.next()) {
				receiptTemp = new ReceiptTempReport();
				receiptTemp.setId(i++);
				receiptTemp.setReceiptNo(rst.getString("RECEIPT_NO"));
				receiptTemp.setCustomerCode(rst.getString("CUSTOMER_CODE"));
				receiptTemp.setCustomerName(rst.getString("CUSTOMER_NAME"));
				receiptTemp.setReceiptAmount(rst.getDouble("RECEIPT_AMOUNT"));
				receiptTemp.setReceiptAmountWord(numToWord.numToWord(receiptTemp.getReceiptAmount()));

				pos.add(receiptTemp);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e2) {}
		}

		return pos;
	}

}
