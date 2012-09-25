package com.isecinc.pens.process.interfaces;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import util.Constants;
import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.pens.bean.InterfaceSummary;
import com.isecinc.pens.bean.Order;

/**
 * Interface Summary Class for ORDER
 * 
 * @author Atiz.b
 * @version $Id: InterfaceSummary.java,v 1.0 7/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class InterfaceSummaryOrder extends InterfaceSummaryProcess {

	protected String createSQL(InterfaceSummary criteria) throws Exception {
		/*
		 * String sql = "select order_no,order_date,customer_name,net_amount," +
		 * "exported,interfaces,sales_order_no,ar_invoice_no "; sql += " from t_order "; sql +=
		 * " where doc_status = 'SV' "; sql += "  and user_id = " + criteria.getUserId(); if
		 * (ConvertNullUtil.convertToString(criteria.getRecordTime()).trim().length() > 0) sql += "  and order_date = '"
		 * + DateToolsUtil.convertToTimeStamp(criteria.getRecordTime().trim()) + "' "; sql +=
		 * " order by order_no desc ";
		 */

		// Aneak.t 21/01/2011
		String sql = " select o.order_no, o.order_date, o.customer_name, ";
		// Aneak.t
		// sql += " (sum(l.TOTAL_AMOUNT)-sum(l.DISCOUNT)) as net_amount,o.exported,o.interfaces, ";
		sql += " (sum(l.TOTAL_AMOUNT)) as net_amount,o.exported,o.interfaces, ";
		sql += " o.sales_order_no,o.ar_invoice_no ";
		sql += " ,o.doc_status ";
		sql += " from t_order o ";
		sql += " inner join t_order_line l on l.ORDER_ID = o.ORDER_ID ";
		sql += " where 1=1 ";
		/** WIT Edit 15/08/2554  SHOW ALL*********/
		//sql += " and o.doc_status ='SV' ";
		
		sql += " and l.ISCANCEL <> 'Y' ";
		if (!criteria.getOrderType().equalsIgnoreCase(Order.DIRECT_DELIVERY)) {
			sql += " and o.user_id = " + criteria.getUserId();
		}
		if (ConvertNullUtil.convertToString(criteria.getRecordTime()).trim().length() > 0)
			sql += " and order_date = '" + DateToolsUtil.convertToTimeStamp(criteria.getRecordTime().trim()) + "' ";
		sql += " group by o.ORDER_NO ";
		sql += " order by o.order_no desc ";

		log.debug(sql);
		return sql;
	}

	protected List<InterfaceSummary> getResult(ResultSet rst) throws Exception {
		List<InterfaceSummary> pos = new ArrayList<InterfaceSummary>();
		InterfaceSummary p;
		while (rst.next()) {
			p = new InterfaceSummary();
			p.setRecordType(InterfaceSummary.TYPE_ORDER);
			p.setRecordNo(rst.getString("order_no"));
			p.setRecordTime(DateToolsUtil.convertToString(rst.getTimestamp("order_date")));
			p.setCustomer(rst.getString("customer_name"));
			p.setRecordAmount(rst.getDouble("net_amount"));
			p.setExported(rst.getString("exported"));
			p.setInterfaces(rst.getString("interfaces"));
			p.setRecordReference1(ConvertNullUtil.convertToString(rst.getString("sales_order_no")));
			p.setRecordReference2(ConvertNullUtil.convertToString(rst.getString("ar_invoice_no")));
			p.setStatus("VO".equals(rst.getString("doc_status"))?Constants.DOC_STATUS_VO_MSG:"");
			pos.add(p);
		}
		return pos;
	}

	public static void main(String[] args) throws Exception {
		InterfaceSummary criteria = new InterfaceSummary();
		criteria.setUserId(4);

		InterfaceSummaryProcess intfs = new InterfaceSummaryOrder();
		intfs.getSummaryInterfaces(criteria);
	}
}
