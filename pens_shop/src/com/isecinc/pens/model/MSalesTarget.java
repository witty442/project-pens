package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.DBCPConnectionProvider;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.SalesTarget;

/**
 * MSalesTarget Class
 * 
 * @author Aneak.t
 * @version $Id: MSalesTarget.java,v 1.0 07/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MSalesTarget extends I_Model<SalesTarget> {

	private static final long serialVersionUID = -8072387469875308327L;

	public static String TABLE_NAME = "m_sales_target";
	public static String COLUMN_ID = "Sales_Target_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SalesTarget find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, SalesTarget.class);
	}

	/**
	 * Search
	 * 
	 * @param id
	 * @param tableName
	 * @param columnID
	 * @return
	 * @throws Exception
	 */
	public SalesTarget[] search(String whereCause) throws Exception {
		List<SalesTarget> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, SalesTarget.class);
		if (pos.size() == 0) return null;
		SalesTarget[] array = new SalesTarget[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Get Year
	 * 
	 * @return
	 */
	public List<String> getYears() {
		List<String> pos = new ArrayList<String>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			stmt = conn.createStatement();
			String sql = "select distinct(year) as year from " + TABLE_NAME;
			rst = stmt.executeQuery(sql);
			while (rst.next())
				pos.add(rst.getString("year"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return pos;
	}
}
