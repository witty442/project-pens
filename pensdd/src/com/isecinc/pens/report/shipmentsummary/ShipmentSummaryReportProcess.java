package com.isecinc.pens.report.shipmentsummary;

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

public class ShipmentSummaryReportProcess extends I_ReportProcess<ShipmentSummaryReport> {
	/**
	 * Search for performance report.
	 */
	public List<ShipmentSummaryReport> doReport(ShipmentSummaryReport report, User user, Connection conn) throws Exception {
		
		boolean shippingDateP = false;
		boolean confirmDateP = false;
		List<ShipmentSummaryReport> pos = new ArrayList<ShipmentSummaryReport>();
		// Get Sales Target Match With Sales Order
		
		String p_shippingDateFrom = report.getShippingDateFrom();
		String p_shippingDateTo = report.getShippingDateTo();
		
		String p_confirmDateFrom = report.getConfirmDateFrom();
		String p_confirmDateTo = report.getConfirmDateTo();
		
		StringBuffer sql = new StringBuffer("");
		sql.append("SELECT \n" +
				" cust.CUSTOMER_ID ,  \n" +
				" odl.CF_SHIP_DATE , \n" +
				" odl.SHIPPING_DATE   , \n" +
				" odl.CF_SHIP_DATE , \n" +
				" cust.code  as MEMBER_CODE,  \n" +
				" concat(cust.name,concat(' ' ,cust.name2)) as MEMBER_NAME,  \n" +
				" cust.delivery_group as route , \n" +
				" odl.trip_no , \n" +
				" p.code as product_code ,  \n" +
				" odl.uom_id, \n" +

				" SUM(COALESCE(odl.qty,0)) as  qty, \n" +
				" SUM(COALESCE(odl.actual_qty,0)) as act_qty, \n" +
				" SUM(COALESCE(odl.need_bill,0)) as need_bill, \n" +
				" SUM(COALESCE(odl.act_need_bill,0)) as act_need_bill \n" +
			    
		    	" FROM t_order od \n" +
		    	" INNER JOIN t_order_line odl ON od.order_id = odl.order_id \n"+
			    " INNER JOIN  m_customer cust  ON cust.CUSTOMER_ID = od.customer_id  \n"+
		    	" INNER JOIN m_product p ON p.product_id = odl.product_id \n"+
			    " WHERE odl.ISCANCEL = 'N' \n" +
			    //" AND odl.NEED_BILL > 0  \n" +
	            " AND odl.cf_ship_date is not null  \n");
			
		    if( !"".equals(p_shippingDateFrom) && !"".equals(p_shippingDateTo)){
			    sql.append(" AND odl.SHIPPING_DATE >= ? AND odl.SHIPPING_DATE <= ?\n");
			    shippingDateP = true;
	        }
		    if( !"".equals(p_confirmDateFrom) && !"".equals(p_confirmDateTo)){
				sql.append(" AND odl.cf_ship_date >= ? AND odl.cf_ship_date <= ?\n");
				confirmDateP = true;
		    }
		    if(!"".equals(Utils.isNull(report.getMemberCode()))){
		    	sql.append(" AND cust.code ='"+Utils.isNull(report.getMemberCode())+"' \n");
		    }
		    if(!"".equals(Utils.isNull(report.getMemberName()))){
		        sql.append(" AND cust.name like '%"+Utils.isNull(report.getMemberName())+"%' \n");
		    }
 
			sql.append(" GROUP BY \n" +
					" cust.CUSTOMER_ID ,  \n" +
					" odl.SHIPPING_DATE   , \n" +
					" odl.CF_SHIP_DATE , \n" +
					" cust.code  ,  \n" +
					" cust.name ,  \n" +
					" cust.delivery_group ,\n"+
					" odl.trip_no , \n" +
					" odl.product_id ,  \n" +
					" odl.uom_id \n")
			.append(" ORDER BY odl.SHIPPING_DATE  ,odl.CF_SHIP_DATE ,cust.code,odl.trip_no ,p.code \n");
		
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
		int no = 0;
		while(rset.next()){
			ShipmentSummaryReport result = new ShipmentSummaryReport();
			no++;
			
			result.setShippingDate(rset.getTimestamp("SHIPPING_DATE"));
			result.setCfShipDate(rset.getTimestamp("CF_SHIP_DATE"));
			result.setMemberCode(rset.getString("MEMBER_CODE"));
			result.setMemberName(rset.getString("MEMBER_NAME"));
			result.setRoute(rset.getString("route"));
			result.setTripNo(rset.getInt("trip_no"));
			result.setProductCode(rset.getString("PRODUCT_CODE"));
			result.setUom(rset.getString("uom_id"));
			result.setQty(rset.getBigDecimal("qty"));
			result.setAct_qty(rset.getBigDecimal("act_qty"));
			result.setNeed_bill(rset.getBigDecimal("need_bill"));
			result.setAct_need_bill(rset.getBigDecimal("act_need_bill"));

			result.setShippingDateFrom(p_shippingDateFrom);
			result.setShippingDateTo(p_shippingDateTo);
			result.setNo(no);
			
			pos.add(result);
		}
		
		logger.info("Report Size:"+pos != null?pos.size():0);
		return pos;
	}
}
