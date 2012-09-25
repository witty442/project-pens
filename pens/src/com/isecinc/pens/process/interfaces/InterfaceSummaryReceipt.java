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
public class InterfaceSummaryReceipt extends InterfaceSummaryProcess {

	protected String createSQL(InterfaceSummary criteria) throws Exception {
		String sql = "select receipt_no, receipt_date, customer_name, receipt_amount,exported,interfaces,doc_status ";
		sql += " from t_receipt ";
		sql += " where 1=1 ";
		/** Wit edit 14/08/2554 :show all **/
		//sql +=" and doc_status = 'SV' ";
		/**************************************/
		if (!criteria.getOrderType().equalsIgnoreCase(Order.DIRECT_DELIVERY)) {
			sql += "  and user_id = " + criteria.getUserId();
		}
		if (ConvertNullUtil.convertToString(criteria.getRecordTime()).trim().length() > 0)
			sql += "  and receipt_date = '" + DateToolsUtil.convertToTimeStamp(criteria.getRecordTime().trim()) + "' ";
		sql += " order by receipt_no desc ";
		log.debug(sql);
		return sql;
	}

	protected List<InterfaceSummary> getResult(ResultSet rst) throws Exception {
		List<InterfaceSummary> pos = new ArrayList<InterfaceSummary>();
		InterfaceSummary p;
		while (rst.next()) {
			p = new InterfaceSummary();
			p.setRecordType(InterfaceSummary.TYPE_RECEIPT);
			p.setRecordNo(rst.getString("receipt_no"));
			p.setRecordTime(DateToolsUtil.convertToString(rst.getTimestamp("receipt_date")));
			p.setCustomer(rst.getString("customer_name"));
			p.setRecordAmount(rst.getDouble("receipt_amount"));
			p.setExported(rst.getString("exported"));
			p.setInterfaces(rst.getString("interfaces"));
			p.setStatus("VO".equals(rst.getString("doc_status"))?Constants.DOC_STATUS_VO_MSG:"");
			pos.add(p);
		}
		return pos;
	}

	public static void main(String[] args) throws Exception {
		InterfaceSummary criteria = new InterfaceSummary();
		criteria.setUserId(4);

		InterfaceSummaryProcess intfs = new InterfaceSummaryReceipt();
		intfs.getSummaryInterfaces(criteria);
	}
}
