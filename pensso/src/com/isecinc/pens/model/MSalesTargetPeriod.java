package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.SalesTargetNew;
import com.isecinc.pens.inf.helper.Utils;
import com.pens.util.DateToolsUtil;

public class MSalesTargetPeriod {
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");
	
	public static SalesTargetNew getSalesPeriodDate(Connection conn,String month,String year) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		SalesTargetNew s = null;
		try {
			String sql = "select  a.*\n";
				 sql +=" from pens.m_sales_target_period a \n";
				 sql +=" where month = '"+month+"'";
				 sql +=" and year = '"+year+"'";
				 
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				s = new SalesTargetNew();
				s.setPeriod(Utils.isNull(rst.getString("period")));
				s.setMonth(month);
				s.setYear(year);
				s.setSalesStartDate(DateToolsUtil.convertToString(rst.getTimestamp("sales_start_date")));
				s.setSalesEndDate(DateToolsUtil.convertToString(rst.getTimestamp("sales_end_date")));
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
	
		}
		
		return s;
	}
}
