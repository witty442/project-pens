package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.ReceiptBy;
import com.isecinc.pens.bean.ReceiptMatchCN;
import com.isecinc.pens.process.SequenceProcess;
import com.pens.util.DBCPConnectionProvider;

/**
 * Receipt Match CN Model
 * 
 * @author atiz.b
 * @version $Id: MReceiptMatchCN.java,v 1.0 21/11/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MReceiptMatchCN extends I_Model<ReceiptMatchCN> {

	private static final long serialVersionUID = -8532039857520296789L;

	public static String TABLE_NAME = "t_receipt_match_cn";
	public static String COLUMN_ID = "RECEIPT_MATCH_CN_ID";

	private String[] columns = { COLUMN_ID, "RECEIPT_BY_ID", "RECEIPT_CN_ID", "PAID_AMOUNT", "CREATED_BY",
			"UPDATED_BY", "RECEIPT_ID" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ReceiptMatchCN find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, ReceiptMatchCN.class);
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
	public ReceiptMatchCN[] search(String whereCause) throws Exception {
		List<ReceiptMatchCN> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ReceiptMatchCN.class);
		if (pos.size() == 0) return null;
		ReceiptMatchCN[] array = new ReceiptMatchCN[pos.size()];
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
	public boolean save(ReceiptMatchCN receiptMatchCN, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (receiptMatchCN.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
		} else {
			id = receiptMatchCN.getId();
		}

		Object[] values = { id, receiptMatchCN.getReceiptById(), receiptMatchCN.getReceiptCNId(),
				receiptMatchCN.getPaidAmount(), activeUserID, activeUserID, receiptMatchCN.getReceiptId() };
		if (super.save(TABLE_NAME, columns, values, receiptMatchCN.getId(), conn)) {
			receiptMatchCN.setId(id);
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
			String sql = "select l.credit_note_id,m.paid_amount ";
			sql += " from t_receipt_match_cn m, t_receipt_cn l ";
			sql += " where m.receipt_cn_id = l.receipt_cn_id ";
			sql += "   and m.receipt_by_id = " + receiptBy.getId();
			conn = new DBCPConnectionProvider().getConnection(conn);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while (rst.next()) {
				allBillId += "," + rst.getString("credit_note_id");
				allPaid += "|" + rst.getString("paid_amount");
			}
			if (allBillId.length() > 0) allBillId = allBillId.substring(1);
			if (allPaid.length() > 0) allPaid = allPaid.substring(1);

			receiptBy.setAllCNId(allBillId);
			receiptBy.setAllCNPaid(allPaid);
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
