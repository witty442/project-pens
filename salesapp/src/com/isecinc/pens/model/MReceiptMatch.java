package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.ReceiptBy;
import com.isecinc.pens.bean.ReceiptMatch;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.seq.SequenceProcessAll;

/**
 * Receipt Match Model
 * 
 * @author atiz.b
 * @version $Id: MReceiptMatch.java,v 1.0 21/11/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MReceiptMatch extends I_Model<ReceiptMatch> {

	private static final long serialVersionUID = -8532039857520296789L;

	public static String TABLE_NAME = "t_receipt_match";
	public static String COLUMN_ID = "RECEIPT_MATCH_ID";

	private String[] columns = { COLUMN_ID, "RECEIPT_BY_ID", "RECEIPT_LINE_ID", "PAID_AMOUNT", "CREATED_BY",
			"UPDATED_BY", "RECEIPT_ID" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ReceiptMatch find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, ReceiptMatch.class);
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
	public ReceiptMatch[] search(String whereCause) throws Exception {
		List<ReceiptMatch> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ReceiptMatch.class);
		if (pos.size() == 0) return null;
		ReceiptMatch[] array = new ReceiptMatch[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param receiptMatch
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(ReceiptMatch receiptMatch, int activeUserID, Connection conn) throws Exception {
		long id = 0;
		if (receiptMatch.getId() == 0) {
			id = SequenceProcessAll.getIns().getNextValue("t_receipt_match.receipt_match_id").longValue();
		} else {
			id = receiptMatch.getId();
		}

		Object[] values = { id, receiptMatch.getReceiptById(), receiptMatch.getReceiptLineId(),
				receiptMatch.getPaidAmount(), activeUserID, activeUserID, receiptMatch.getReceiptId() };
		if (super.save(TABLE_NAME, columns, values, receiptMatch.getId(), conn)) {
			receiptMatch.setId(id);
		}
		return true;
	}

	/**
	 * Delete
	 * 
	 * @param deleteId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean delete(String deleteId, Connection conn) throws Exception {
		return super.delete(TABLE_NAME, COLUMN_ID, deleteId, conn);
	}

	/**
	 * Delete
	 * 
	 * @param deleteId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean deleteByReceiptId(String receiptId, Connection conn) throws Exception {
		Statement stmt = null;
		try {
			String sql = "DELETE FROM " + TABLE_NAME + " WHERE RECEIPT_ID = " + receiptId;
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

	public boolean deleteByReceiptLineId(String receiptLineId, Connection conn) throws Exception {
		Statement stmt = null;
		try {
			String sql = "DELETE FROM " + TABLE_NAME + " WHERE RECEIPT_LINE_ID IN( " + receiptLineId+")";
			logger.debug("sql:"+sql);
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
	/***
	 * Look Up
	 * 
	 * @param receiptBy
	 */
	public void lookUp(ReceiptBy receiptBy) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		String allBillId = "";
		String allPaid = "";
		try {
			String sql = "select l.invoice_id,m.paid_amount ";
			sql += " from t_receipt_match m, t_receipt_line l ";
			sql += " where m.receipt_line_id = l.receipt_line_id ";
			sql += "   and m.receipt_by_id = " + receiptBy.getId();
			conn = new DBCPConnectionProvider().getConnection(conn);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while (rst.next()) {
				allBillId += "," + rst.getString("l.invoice_id");
				allPaid += "|" + rst.getString("m.paid_amount");
			}
			if (allBillId.length() > 0) allBillId = allBillId.substring(1);
			if (allPaid.length() > 0) allPaid = allPaid.substring(1);

			receiptBy.setAllBillId(allBillId);
			receiptBy.setAllPaid(allPaid);
		} catch (Exception e) {
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
		return;
	}
}
