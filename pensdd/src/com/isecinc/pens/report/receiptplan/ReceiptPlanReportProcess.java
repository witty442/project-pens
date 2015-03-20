package com.isecinc.pens.report.receiptplan;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.jfree.util.Log;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;


/**
 * InvoiceDetailReportProcess Report
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportProcess.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ReceiptPlanReportProcess extends I_ReportProcess<ReceiptPlanReport> {
	/**
	 * Search for performance report.
	 */
	public List<ReceiptPlanReport> doReport(ReceiptPlanReport report, User user, Connection conn) throws Exception {
		
		int i =0;
		List<ReceiptPlanReport> pos = new ArrayList<ReceiptPlanReport>();
		// Get Sales Target Match With Sales Order
		
		String p_dateFrom =report.getDateFrom();
		String p_dateTo = report.getDateTo();
		
		StringBuffer sql = new StringBuffer("SELECT cust.CUSTOMER_ID , cust.MEMBER_CODE , cust.tel_no ,od.CUSTOMER_NAME as MEMBER_NAME ,od.ORDER_NO , odl.trip_no ,rf.NAME as payment_method , odl.payment_method as payment_method_code ,odl.SHIPPING_DATE \n");
		sql.append(" ,cust.BANK_NAME , cust.CREDITCARD_EXPIRED , cust.CARD_NO , cust.CARD_NAME , SUM(COALESCE(odl.NEED_BILL,0)) as PLAN_BILL_AMT \n")
			.append(" FROM t_order od INNER JOIN t_order_line odl ON od.order_id = odl.order_id \n")
			.append(" INNER JOIN (SELECT cm.CUSTOMER_ID , cm.CODE as MEMBER_CODE , bank.NAME as BANK_NAME , cm.CREDITCARD_EXPIRED , cm.CARD_NO ,cm.CARD_NAME\n")
			.append("  , GROUP_CONCAT(DISTINCT cmc.MOBILE SEPARATOR ',') as tel_no\n")
			.append("  FROM m_customer cm LEFT JOIN m_contact cmc ON cm.CUSTOMER_ID = cmc.CUSTOMER_ID\n")
			.append("  LEFT JOIN c_reference bank ON bank.VALUE = cm.CARD_BANK AND bank.CODE = 'Bank'\n")
			.append("  GROUP BY cm.CUSTOMER_ID , cm.CODE , bank.NAME , cm.CREDITCARD_EXPIRED , cm.CARD_NO ,cm.CARD_NAME ) cust ON cust.CUSTOMER_ID = od.customer_id \n")
			.append(" LEFT JOIN c_reference rf ON rf.VALUE = odl.payment_method \n")
			.append(" WHERE odl.ISCANCEL = 'N' AND odl.NEED_BILL > 0 AND rf.CODE = 'PaymentMethod'\n")
			.append(" AND odl.SHIPPING_DATE <= ? AND odl.SHIPPING_DATE >= ?\n")
			.append(" AND odl.PAYMENT_METHOD = COALESCE(?,odl.PAYMENT_METHOD)\n")
			.append(" GROUP BY cust.CUSTOMER_ID , cust.MEMBER_CODE , cust.tel_no ,od.CUSTOMER_NAME,od.ORDER_NO , odl.trip_no ,rf.NAME , odl.payment_method \n")
			.append(" ,odl.SHIPPING_DATE ,cust.BANK_NAME , cust.CREDITCARD_EXPIRED , cust.CARD_NO , cust.CARD_NAME \n")
			.append(" ORDER BY odl.payment_method  , cust.MEMBER_CODE ");
		
		logger.debug(sql.toString());
		
		PreparedStatement ppstmt = conn.prepareStatement(sql.toString());
		Timestamp shippingDateFrom = DateToolsUtil.convertToTimeStamp(p_dateFrom);
		Timestamp shippingDateTo = DateToolsUtil.convertToTimeStamp(p_dateTo);
		String paymentMethod = report.getParam_PaymentMethod();
		if(StringUtils.isEmpty(paymentMethod))
			paymentMethod = null;
		
		
		ppstmt.setTimestamp(1, shippingDateTo);
		ppstmt.setTimestamp(2, shippingDateFrom);
		ppstmt.setString(3, paymentMethod);
		
		ResultSet rset = ppstmt.executeQuery();
		while(rset.next()){
			ReceiptPlanReport result = new ReceiptPlanReport();
			//result.setExpireDate(rset.getTimestamp("max_ship_date"));
			result.setMemberCode(rset.getString("MEMBER_CODE"));
			result.setMemberName(rset.getString("MEMBER_NAME"));
			result.setMemberTel(rset.getString("tel_no"));
			result.setOrderNo(rset.getString("ORDER_NO"));
			result.setTripNo(rset.getInt("trip_no"));
			result.setPaymentMethod(rset.getString("payment_method"));
			result.setPaymentMethodCode(rset.getString("payment_method_code"));
			result.setShipDate(rset.getTimestamp("SHIPPING_DATE"));
			result.setPlanBillAmt(rset.getBigDecimal("PLAN_BILL_AMT"));
			
			result.setCreditCardBank(rset.getString("BANK_NAME"));
			result.setCreditCardExpireDate(rset.getString("CREDITCARD_EXPIRED"));
			result.setCreditCardNo(rset.getString("CARD_NO"));
			result.setCreditCardName(rset.getString("CARD_NAME"));
			
			result.setDateFrom(p_dateFrom);
			result.setDateTo(p_dateTo);
			
			pos.add(result);
		}
		
		return pos;
	}
}
