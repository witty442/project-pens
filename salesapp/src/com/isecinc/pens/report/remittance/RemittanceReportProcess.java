package com.isecinc.pens.report.remittance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;
import com.pens.util.DateToolsUtil;

/**
 * Remittance Report Process
 * 
 * @author Aneak.t
 * @version $Id: RemittanceReportProcess.java,v 1.0 02/12/2010 15:52:00 aneak.t Exp $
 * 
 */

public class RemittanceReportProcess extends I_ReportProcess<RemittanceReport> {

	/**
	 * Search for report.
	 */
	public List<RemittanceReport> doReport(RemittanceReport t, User user, Connection conn) throws Exception {
		List<RemittanceReport> pos = new ArrayList<RemittanceReport>();
		RemittanceReport remittance = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();

		//logger.debug("CodeTo--->"+t.getCustomerCodeTo());
		//logger.debug("CodeFrom-->"+t.getCustomerCodeFrom());

		try {
			sql.delete(0, sql.length());
			sql.append(" SELECT rc.RECEIPT_DATE, cus.CODE AS CUSTOMER_CODE, ");
			sql.append(" cus.NAME AS CUSTOMER_NAME, cus.NAME2 AS LAST_NAME,rc.RECEIPT_AMOUNT ");
			sql.append(" FROM t_receipt rc ");
			sql.append(" INNER JOIN ad_user us ON rc.USER_ID = us.USER_ID ");
			sql.append(" INNER JOIN m_customer cus ON rc.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append(" WHERE cus.CUSTOMER_TYPE = 'DD' ");
			sql.append(" AND rc.DOC_STATUS = 'SV' ");
			
			if(!t.getReceiptDateFrom().isEmpty() || !t.getReceiptDateFrom().equalsIgnoreCase("")){
				sql.append(" AND rc.RECEIPT_DATE >= '" + DateToolsUtil.convertToTimeStamp(t.getReceiptDateFrom()) + "' ");
				sql.append(" AND rc.RECEIPT_DATE <= '" + DateToolsUtil.convertToTimeStamp(t.getReceiptDateTo()) + "' ");
			}
			
			sql.append(" AND rc.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");

			
			if(t.getCustomerCodeTo() != ""){
				if(t.getCustomerCodeFrom() != ""){
					sql.append(" AND cus.CODE >= '"+t.getCustomerCodeFrom() +"' AND cus.CODE <= '"+t.getCustomerCodeTo() +"'");
				}			
			}else{
				if(t.getCustomerCodeFrom() != ""){
					sql.append(" AND cus.CODE = '"+t.getCustomerCodeFrom() +"' ");
					
				}
			}
			
			sql.append(" ORDER BY rc.RECEIPT_NO ");

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int i = 1;
			while (rst.next()) {
				remittance = new RemittanceReport();

				remittance.setId(i++);
			
				//remittance.setReceiptDate(DateToolsUtil.convertToTimeStamp(rst.getDate("RECEIPT_DATE")));
				remittance.setReceiptDate(DateToolsUtil.convertToString(rst.getDate("RECEIPT_DATE")));
				remittance.setCustomerCode(rst.getString("CUSTOMER_CODE"));
				remittance.setCustomerName(rst.getString("CUSTOMER_NAME"));
				remittance.setLastName(rst.getString("LAST_NAME"));
				remittance.setReceiptAmount(rst.getDouble("RECEIPT_AMOUNT"));

				pos.add(remittance);
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
