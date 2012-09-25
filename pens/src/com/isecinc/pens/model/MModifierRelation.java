package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.DBCPConnectionProvider;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.ModifierRelation;

public class MModifierRelation extends I_Model<ModifierRelation> {
	private static final long serialVersionUID = 1149816246642304671L;

	public static String TABLE_NAME = "m_relation_modifier";
	public static String COLUMN_ID = "relation_modifier_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ModifierRelation find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, ModifierRelation.class);
	}

	/**
	 * Look up with line from Id
	 * 
	 * @param modifierLineFromId
	 * @return
	 */
	public List<ModifierRelation> lookUp(int modifierLineFromId) {
		List<ModifierRelation> pos = new ArrayList<ModifierRelation>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			String sql = "select rm.RELATION_MODIFIER_ID ";
			sql += "from m_relation_modifier rm , m_modifier_line ml ";
			sql += "where rm.MODIFIER_LINE_TO_ID = ml.MODIFIER_LINE_ID ";
			sql += "  and rm.MODIFIER_LINE_FROM_ID = " + modifierLineFromId;
			sql += " order by ml.VALUE ";
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while (rst.next()) {
				pos.add(find(rst.getString("rm.RELATION_MODIFIER_ID")));
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
		return pos;
	}
}
