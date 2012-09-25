package com.isecinc.pens.process.interfaces;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.pens.bean.InterfaceSummary;

/**
 * Interface Summary Class for ORDER
 * 
 * @author Atiz.b
 * @version $Id: InterfaceSummary.java,v 1.0 7/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class InterfaceSummaryVisit extends InterfaceSummaryProcess {

	protected String createSQL(InterfaceSummary criteria) throws Exception {
		String sql = "select code, visit_date, visit_time, customer_name, exported,interfaces, SALES_CLOSED ,'' as doc_status ";
		sql += " from t_visit ";
		sql += " where isactive = 'Y' ";
		sql += "  and user_id = " + criteria.getUserId();
		if (ConvertNullUtil.convertToString(criteria.getRecordTime()).trim().length() > 0)
			sql += "  and visit_date = '" + DateToolsUtil.convertToTimeStamp(criteria.getRecordTime().trim()) + "' ";
		sql += " order by code desc ";
		log.debug(sql);
		return sql;
	}

	protected List<InterfaceSummary> getResult(ResultSet rst) throws Exception {
		List<InterfaceSummary> pos = new ArrayList<InterfaceSummary>();
		InterfaceSummary p;
		while (rst.next()) {
			p = new InterfaceSummary();
			p.setRecordType(InterfaceSummary.TYPE_RECEIPT);
			p.setRecordNo(rst.getString("code"));
			p.setRecordTime(DateToolsUtil.convertToString(rst.getTimestamp("visit_date")) + " "
					+ rst.getString("visit_time"));
			p.setCustomer(rst.getString("customer_name"));
			p.setExported(rst.getString("exported"));
			p.setInterfaces(rst.getString("interfaces"));
			p.setClosed(rst.getString("SALES_CLOSED"));
			pos.add(p);
		}
		return pos;
	}

	public static void main(String[] args) throws Exception {
		InterfaceSummary criteria = new InterfaceSummary();
		criteria.setUserId(3);

		InterfaceSummaryProcess intfs = new InterfaceSummaryVisit();
		intfs.getSummaryInterfaces(criteria);
	}
}
