package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.SalesInventory;

public class MSalesInventory extends I_Model<SalesInventory> {

	private static final long serialVersionUID = -8270239957704777085L;

	public static String TABLE_NAME = "m_sales_inventory";
	public static String COLUMN_ID = "SUB_INVENTORY_ID";
	public static String COLUMN_ID2 = "USER_ID";

	/**
	 * Save
	 * 
	 * @param subInvIds
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(String[] subInvIds, int activeUserID, Connection conn) throws Exception {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID2 + " = " + activeUserID;
			stmt.execute(sql);
			for (String s : subInvIds) {
				sql = "INSERT INTO " + TABLE_NAME + "(" + COLUMN_ID + "," + COLUMN_ID2 + ") ";
				sql += "VALUES (" + s + "," + activeUserID + ") ";
				stmt.execute(sql);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				stmt.close();
			} catch (Exception e2) {}
		}
		return true;
	}

	/**
	 * Look UP
	 */
	public List<SalesInventory> lookUp(int userId) {
		List<SalesInventory> pos = new ArrayList<SalesInventory>();
		try {
			String whereCause = " AND " + COLUMN_ID2 + "=" + userId;
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, SalesInventory.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}

	/**
	 * Delete
	 * 
	 * @param userId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean deleteByUserId(String userId, Connection conn) throws Exception {
		Statement stmt = null;
		try {
			String sql = "DELETE FROM " + TABLE_NAME + " WHERE USER_ID = " + userId;
			stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				stmt.close();
			} catch (Exception e2) {}
		}
		return true;
	}
}
