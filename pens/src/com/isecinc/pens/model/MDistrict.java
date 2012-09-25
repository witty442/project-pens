package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.DBCPConnectionProvider;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.District;

/**
 * Model District Class
 * 
 * @author atiz.b
 * @version $Id: MDistrict.java,v 1.0 12/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MDistrict extends I_Model<District> {

	private static final long serialVersionUID = -6254839726381212634L;

	public static String TABLE_NAME = "m_district";
	public static String COLUMN_ID = "DISTRICT_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public District find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, District.class);
	}

	/**
	 * Search
	 * 
	 * @param whereCause
	 * @return
	 * @throws Exception
	 */
	public District[] search(String whereCause) throws Exception {
		List<District> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, District.class);
		if (pos.size() == 0) return null;
		District[] array = new District[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Look Up
	 */
	public List<District> lookUp() {
		List<District> pos = new ArrayList<District>();
		try {
			String whereCause = "  ORDER BY NAME ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, District.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}

	/**
	 * Look Up
	 */
	public List<District> lookUp(int provinceId) {
		List<District> pos = new ArrayList<District>();
		try {
			String whereCause = " AND PROVINCE_ID = " + provinceId + " ORDER BY NAME ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, District.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}

	/**
	 * Get Delivery Group
	 * 
	 * @param districtId
	 * @return
	 */
	public String getDeliveryGroup(int districtId) {
		String dg = "";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			String sql = "select cr.VALUE ";
			sql += "from m_delivery_group dg, c_reference cr ";
			sql += "where dg.DISTRICT_ID = " + districtId;
			sql += "  and dg.REFERENCE_ID = cr.REFERENCE_ID ";
			stmt = conn.createStatement();
			logger.debug(sql);
			rst = stmt.executeQuery(sql);
			if (rst.next()) {
				dg = rst.getString("VALUE");
			}
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
		return dg;
	}
}
