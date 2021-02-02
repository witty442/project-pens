package com.isecinc.pens.report.cheque;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import util.Constants;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.SalesTargetNew;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.model.MSalesTargetNew;
import com.isecinc.pens.report.performance.PerformanceReport;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DateToolsUtil;

/**
 * Performance Report
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportProcess.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ChequeReportProcess extends I_ReportProcess<ChequeReport> {

	/**
	 * Search for performance report.
	 */
	public List<ChequeReport> doReport(ChequeReport t, User user, Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<ChequeReport> pos = new ArrayList<ChequeReport>();
		StringBuilder sql = new StringBuilder();
		try {
			
			sql.delete(0, sql.length());
			sql.append("\n SELECT  ");
			sql.append("\n od.ORDER_DATE,  ");
			sql.append("\n od.ORDER_NO,  ");
			sql.append("\n R.cheque_no,  ");
			sql.append("\n R.cheque_date,  ");
			sql.append("\n (select c.name from c_reference c where c.code ='Bank' and c.value= r.bank_code) as bank_name,  ");
			sql.append("\n R.cheque_amount,  ");
			sql.append("\n m.code,  ");
			sql.append("\n m.name  ");
			sql.append("\n FROM t_order od   ");
			sql.append("\n INNER JOIN  ");
			sql.append("\n ( select   ");
			sql.append("\n t_receipt_line.order_id,  ");
			sql.append("\n t_receipt_by.cheque_no,  ");
			sql.append("\n t_receipt_by.cheque_date,  ");
			sql.append("\n t_receipt_by.bank as bank_code,  ");
			sql.append("\n sum( t_receipt_by.paid_amount) as cheque_amount  ");
			sql.append("\n from t_receipt_line  ,t_receipt_match , t_receipt_by   ");
			sql.append("\n where 1=1  ");
			sql.append("\n and t_receipt_match.RECEIPT_LINE_ID = t_receipt_line.RECEIPT_LINE_ID   ");
			sql.append("\n and t_receipt_by.RECEIPT_BY_ID = t_receipt_match.RECEIPT_BY_ID   ");
			sql.append("\n and t_receipt_by.PAYMENT_METHOD ='CH'    ");
			sql.append("\n and  t_receipt_line.order_id in(  ");
			sql.append("\n select order_id from t_order o where 1=1  ");
			sql.append("\n and o.ORDER_DATE >=  '" + DateToolsUtil.convertToTimeStamp(t.getOrderDateFrom()) + "' ");
			sql.append("\n and o.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(t.getOrderDateTo()) + "' "); 
			sql.append("\n and o.DOC_STATUS = 'SV'   ");
			sql.append("\n and o.USER_ID = "+user.getId());
			sql.append("\n )  ");
			sql.append("\n group by  t_receipt_line.order_id,  ");
			sql.append("\n t_receipt_by.cheque_no,  ");
			sql.append("\n t_receipt_by.cheque_date,  ");
			sql.append("\n t_receipt_by.bank  ");
			sql.append("\n )R ON od.order_id = r.order_id  ");
			sql.append("\n INNER JOIN m_customer m  ");
			sql.append("\n ON m.customer_id = od.customer_id  ");
			sql.append("\n WHERE 1=1  ");
			sql.append("\n and od.ORDER_DATE >= '" + DateToolsUtil.convertToTimeStamp(t.getOrderDateFrom()) + "' ");
			sql.append("\n and od.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(t.getOrderDateTo()) + "' "); 
			sql.append("\n and od.DOC_STATUS = 'SV'   ");
			sql.append("\n and od.USER_ID = "+user.getId());
			sql.append("\n ORDER BY od.ORDER_ID ASC   ");
			
			logger.debug("sql:"+sql.toString());
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			int i = 1;
			while (rst.next()) {
				ChequeReport inv = new ChequeReport();
				inv.setNo(i);
				inv.setChequeNo(Utils.isNull(rst.getString("cheque_no")));
				inv.setChequeDate(DateToolsUtil.convertToString(rst.getDate("cheque_date")));
				inv.setBankName(Utils.isNull(rst.getString("bank_name")));
				inv.setChequeAmount(rst.getDouble("cheque_amount"));
				inv.setOrderNo(Utils.isNull(rst.getString("order_no")));
                inv.setOrderDate(DateToolsUtil.convertToString(rst.getDate("order_date")));
                inv.setCustomerCode(Utils.isNull(rst.getString("code")));
                inv.setCustomerName(Utils.isNull(rst.getString("name")));
				pos.add(inv);
				i++;
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
