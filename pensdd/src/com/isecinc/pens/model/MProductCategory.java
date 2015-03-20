package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.ProductCategory;

/**
 * MProductCategory Class
 * 
 * @author Aneak.t
 * @version $Id: MProductCategory.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MProductCategory extends I_Model<ProductCategory> {

	private static final long serialVersionUID = -6654613129712945809L;

	public static String TABLE_NAME = "m_product_category";
	public static String COLUMN_ID = "PRODUCT_CATEGORY_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ProductCategory find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, ProductCategory.class);
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
	public ProductCategory[] search(String whereCause) throws Exception {
		List<ProductCategory> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ProductCategory.class);
		if (pos.size() == 0) return null;
		ProductCategory[] array = new ProductCategory[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Look Up
	 */
	public List<ProductCategory> lookUp() {
		List<ProductCategory> pos = new ArrayList<ProductCategory>();
		try {
			String whereCause = "  AND ISACTIVE = 'Y' ORDER BY NAME ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ProductCategory.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}

	/**
	 * Look Up
	 * 
	 * @param segment
	 * @param psegmentId
	 * @return
	 * @throws Exception
	 */
	public List<ProductCategory> lookUp(int segment, int psegmentId) throws Exception {
		List<ProductCategory> pos = new ArrayList<ProductCategory>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			String sql = "SELECT SEG_ID" + segment + ", SEG_VALUE" + segment + " FROM " + MProductCategory.TABLE_NAME;
			sql += " WHERE 1=1 ";
			sql += "  AND ISACTIVE = 'Y' ";
			if (psegmentId != 0) sql += "  AND SEG_ID" + (segment - 1) + "=" + psegmentId;
			sql += "  AND SEG_ID" + segment + " IS NOT NULL";
			sql += " GROUP BY SEG_VALUE" + segment;
			conn = new DBCPConnectionProvider().getConnection(conn);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			ProductCategory pc;
			while (rst.next()) {
				pc = new ProductCategory();
				pc.setId(rst.getInt("SEG_ID" + segment));
				pc.setName(ConvertNullUtil.convertToString(rst.getString("SEG_VALUE" + segment)));
				pos.add(pc);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
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
	
	
	public List<References> lookUpBrandList() throws Exception {
		List<References> pos = new ArrayList<References>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			String sql = "select DISTINCT TRIM(SUBSTRING_INDEX(name,'-',1)) as brand from "+TABLE_NAME+" where NAME NOT IN('Default','ว่าง')";
			conn = new DBCPConnectionProvider().getConnection(conn);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while (rst.next()) {
				References r = new References(rst.getString("brand"),rst.getString("brand"),rst.getString("brand"));
				pos.add(r);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
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
