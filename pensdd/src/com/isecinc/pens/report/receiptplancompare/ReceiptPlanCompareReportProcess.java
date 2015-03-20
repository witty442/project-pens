package com.isecinc.pens.report.receiptplancompare;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;


/**
 * InvoiceDetailReportProcess Report
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportProcess.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ReceiptPlanCompareReportProcess extends I_ReportProcess<ReceiptPlanCompareReport> {
	/**
	 * Search for performance report.
	 */
	public List<ReceiptPlanCompareReport> doReport(ReceiptPlanCompareReport report, User user, Connection conn) throws Exception {
		
		boolean shippingDateP = false;
		boolean confirmDateP = false;
		List<ReceiptPlanCompareReport> pos = new ArrayList<ReceiptPlanCompareReport>();
		// Get Sales Target Match With Sales Order
		
		String p_shippingDateFrom = report.getShippingDateFrom();
		String p_shippingDateTo = report.getShippingDateTo();
		
		String p_confirmDateFrom = report.getConfirmDateFrom();
		String p_confirmDateTo = report.getConfirmDateTo();
		
		StringBuffer sql = new StringBuffer("");
		sql.append("SELECT \n" +
				"cust.CUSTOMER_ID , \n" +
				"cust.MEMBER_CODE , \n" +
				"cust.tel_no ,\n" +
				"od.CUSTOMER_NAME as MEMBER_NAME , \n" +
				"od.ORDER_NO , \n" +
				"odl.trip_no ,\n" +
				"rf.NAME as payment_method , \n" +
				"odl.payment_method as payment_method_code , \n" +
				"odl.SHIPPING_DATE , \n"+
		        "cust.BANK_NAME , \n" +
		    	"cust.CREDITCARD_EXPIRED , \n" +
		    	"cust.CARD_NO , \n" +
		    	"cust.CARD_NAME , \n" +
		    	"SUM(COALESCE(odl.NEED_BILL,0)) as PLAN_BILL_AMT, \n"+
		    	"SUM(COALESCE(odl.ACT_NEED_BILL,0)) as CONFIRM_BILL_AMT, \n"+
		    	"(SUM(COALESCE(odl.NEED_BILL,0)) - SUM(COALESCE(odl.ACT_NEED_BILL,0)) ) as DIFF_BILL_AMT \n"+
			    " FROM t_order od INNER JOIN t_order_line odl ON od.order_id = odl.order_id \n"+
			    " INNER JOIN \n" +
					"  ( SELECT cm.CUSTOMER_ID , \n" +
					"    cm.CODE as MEMBER_CODE , \n" +
					"    bank.NAME as BANK_NAME , \n" +
					"    cm.CREDITCARD_EXPIRED , \n" +
					"    cm.CARD_NO ,\n" +
					"    cm.CARD_NAME \n"+
			        "  , GROUP_CONCAT(DISTINCT cmc.MOBILE SEPARATOR ',') as tel_no\n"+
			        "    FROM m_customer cm LEFT JOIN m_contact cmc ON cm.CUSTOMER_ID = cmc.CUSTOMER_ID\n"+
			        "    LEFT JOIN c_reference bank ON bank.VALUE = cm.CARD_BANK AND bank.CODE = 'Bank'\n"+
			        "    GROUP BY cm.CUSTOMER_ID , cm.CODE , bank.NAME , cm.CREDITCARD_EXPIRED , cm.CARD_NO ,cm.CARD_NAME  \n" +
					" ) cust ON cust.CUSTOMER_ID = od.customer_id \n"+
			   " LEFT JOIN c_reference rf ON rf.VALUE = odl.payment_method \n"+
			   " WHERE odl.ISCANCEL = 'N' AND odl.NEED_BILL > 0 AND rf.CODE = 'PaymentMethod' \n");
			
		    if( !"".equals(p_shippingDateFrom) && !"".equals(p_shippingDateTo)){
			    sql.append(" AND odl.SHIPPING_DATE >= ? AND odl.SHIPPING_DATE <= ?\n");
			    shippingDateP = true;
	        }
		    if( !"".equals(p_confirmDateFrom) && !"".equals(p_confirmDateTo)){
				sql.append(" AND odl.cf_ship_date >= ? AND odl.cf_ship_date <= ?\n");
				confirmDateP = true;
		    }
		    if(!"".equals(Utils.isNull(report.getCustCode()))){
		    	sql.append(" AND cust.member_code ='"+Utils.isNull(report.getCustCode())+"' \n");
		    }
		    if(!"".equals(Utils.isNull(report.getCustName()))){
		       sql.append(" AND od.name like '%"+Utils.isNull(report.getCustName())+"%' \n");
		    }
		    if(!"".equals(Utils.isNull(report.getParam_PaymentMethod()))){
		       sql.append(" AND odl.PAYMENT_METHOD = '"+Utils.isNull(report.getParam_PaymentMethod())+"' \n");
		    }
		    
			sql.append(" GROUP BY cust.CUSTOMER_ID , \n" +
					"          cust.MEMBER_CODE , \n" +
					"          cust.tel_no ,\n" +
					"          od.CUSTOMER_NAME, \n" +
					"          od.ORDER_NO , \n" +
					"          odl.trip_no , \n" +
					"          rf.NAME , \n" +
					"          odl.payment_method, \n"+
			        "          odl.SHIPPING_DATE ,\n" +
					"          cust.BANK_NAME , \n" +
					"          cust.CREDITCARD_EXPIRED , \n" +
					"          cust.CARD_NO , \n" +
					"          cust.CARD_NAME \n")
			.append(" ORDER BY odl.payment_method  , cust.MEMBER_CODE \n");
		
		logger.info(sql.toString());
		
		PreparedStatement ppstmt = conn.prepareStatement(sql.toString());
		int index = 0;
		
		if(shippingDateP){
		   logger.info("p_shippingDateFrom:"+DateToolsUtil.convertStringToDate(p_shippingDateFrom));
		   logger.info("p_shippingDateTo:"+DateToolsUtil.convertStringToDate(p_shippingDateTo));
		   
		   ppstmt.setDate(++index, new java.sql.Date(DateToolsUtil.convertStringToDate(p_shippingDateFrom).getTime()));
		   ppstmt.setDate(++index, new java.sql.Date(DateToolsUtil.convertStringToDate(p_shippingDateTo).getTime()));
		}
		if(confirmDateP){
		   ppstmt.setDate(++index, new java.sql.Date(DateToolsUtil.convertStringToDate(p_confirmDateFrom).getTime()));
		   ppstmt.setDate(++index, new java.sql.Date(DateToolsUtil.convertStringToDate(p_confirmDateTo).getTime()));
		}
		ResultSet rset = ppstmt.executeQuery();
		
		while(rset.next()){
			ReceiptPlanCompareReport result = new ReceiptPlanCompareReport();
			result.setCustCode(rset.getString("MEMBER_CODE"));
			result.setCustName(rset.getString("MEMBER_NAME"));
			result.setMemberTel(rset.getString("tel_no"));
			result.setOrderNo(rset.getString("ORDER_NO"));
			result.setTripNo(rset.getInt("trip_no"));
			result.setPaymentMethod(rset.getString("payment_method"));
			result.setPaymentMethodCode(rset.getString("payment_method_code"));
			result.setShipDate(rset.getTimestamp("SHIPPING_DATE"));
			result.setPlanBillAmt(rset.getBigDecimal("PLAN_BILL_AMT"));
			result.setConfirmBillAmt(rset.getBigDecimal("CONFIRM_BILL_AMT"));
			result.setDiffBillAmt(rset.getBigDecimal("DIFF_BILL_AMT"));
			
			result.setCreditCardBank(rset.getString("BANK_NAME"));
			result.setCreditCardExpireDate(rset.getString("CREDITCARD_EXPIRED"));
			result.setCreditCardNo(rset.getString("CARD_NO"));
			result.setCreditCardName(rset.getString("CARD_NAME"));
			
			result.setShippingDateFrom(p_shippingDateFrom);
			result.setShippingDateTo(p_shippingDateTo);
			
			pos.add(result);
		}
		
		logger.info("Report Size:"+pos != null?pos.size():0);
		return pos;
	}
}
