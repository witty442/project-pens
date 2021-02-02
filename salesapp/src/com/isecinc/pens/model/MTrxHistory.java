package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.TrxHistory;
import com.isecinc.pens.process.SequenceProcess;
import com.pens.util.ConvertNullUtil;

public class MTrxHistory extends I_Model<TrxHistory> {

	private static final long serialVersionUID = 6445753642997787687L;

	public static String TABLE_NAME = "c_trx_history";
	public static String COLUMN_ID = "TRX_HIST_ID";

	// Column Trip
	private String[] columns = { COLUMN_ID, "TRX_MODULE", "TRX_TYPE", "RECORD_ID", "USER_ID", "TRX_DATE" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public TrxHistory find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, TrxHistory.class);
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
	public TrxHistory[] search(String whereCause) throws Exception {
		List<TrxHistory> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, TrxHistory.class);
		if (pos.size() == 0) return null;
		TrxHistory[] array = new TrxHistory[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param trxHistory
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 * */
	public boolean save(TrxHistory trxHistory, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (trxHistory.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
		} else {
			id = trxHistory.getId();
		}
		Object[] values = { id, ConvertNullUtil.convertToString(trxHistory.getTrxModule()).trim(),
				ConvertNullUtil.convertToString(trxHistory.getTrxType()).trim(), trxHistory.getRecordId(),
				trxHistory.getUser().getId() };

		PreparedStatement pstmt = null;
		try {
			String cols = "";
			String vals = "";

			// Cols
			for (String s : columns)
				cols += "," + s;

			// Vals
			for (int i = 0; i < values.length; i++)
				vals += ",?";

			cols = cols.substring(1);
			vals = vals.substring(1);

			String sql = "INSERT INTO " + TABLE_NAME + "(";
			sql += cols;
			sql += " ) ";
			sql += " VALUES(";
			sql += vals;
			sql += ",CURRENT_TIMESTAMP";
			sql += ")";

			pstmt = conn.prepareStatement(sql);
			int p = 1;
			pstmt.setInt(p++, (Integer) values[0]);
			pstmt.setString(p++, (String) values[1]);
			pstmt.setString(p++, (String) values[2]);
			pstmt.setInt(p++, (Integer) values[3]);
			pstmt.setInt(p++, (Integer) values[4]);

			pstmt.executeUpdate();

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				pstmt.close();
			} catch (Exception e2) {}
		}

		return true;
	}

	/**
	 * Look Up
	 * 
	 * @param whereCause
	 * @return List of TrxHistory
	 * @throws Exception
	 */
	public List<TrxHistory> lookUp(String whereCause) throws Exception {
		List<TrxHistory> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, TrxHistory.class);
		return pos;
	}
}
