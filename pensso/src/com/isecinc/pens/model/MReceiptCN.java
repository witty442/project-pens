package com.isecinc.pens.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.CreditNote;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptCN;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.seq.SequenceProcess;

/**
 * MAddress Class
 * 
 * @author atiz.b
 * @version $Id: MAddress.java,v 1.0 12/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MReceiptCN extends I_Model<ReceiptCN> {

	private static final long serialVersionUID = -4119649113903274964L;

	public static String TABLE_NAME = "t_receipt_cn";
	public static String COLUMN_ID = "RECEIPT_CN_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "CREDIT_NOTE_ID", "RECEIPT_ID", "CREATED_BY", "UPDATED_BY",
			"CREDIT_AMOUNT", "PAID_AMOUNT", "REMAIN_AMOUNT" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ReceiptCN find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, ReceiptCN.class);
	}

	/**
	 * Save
	 * 
	 * @param receiptCN
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(ReceiptCN receiptCN, int activeUserID, Connection conn) throws Exception {
		long id = 0;
		if (receiptCN.getId() ==0) {
			id = SequenceProcess.getNextValue(TABLE_NAME).longValue();
		} else {
			id = receiptCN.getId();
		}
		Object[] values = { id, receiptCN.getCreditNote().getId(), receiptCN.getReceiptId(), activeUserID,
				activeUserID, receiptCN.getCreditAmount(), receiptCN.getPaidAmount(), receiptCN.getRemainAmount() };
		if (super.save(TABLE_NAME, columns, values, receiptCN.getId(), conn)) {
			receiptCN.setId(id);
		}
		return true;
	}
	
	/**
	 * Lookup with receiptID
	 * 
	 * @param receiptId
	 * @return
	 */
	public List<ReceiptCN> lookUp(long receiptId) {
		List<ReceiptCN> pos = new ArrayList<ReceiptCN>();
		try {
			String whereCause = " AND RECEIPT_ID = " + receiptId + " ORDER BY " + COLUMN_ID;
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ReceiptCN.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
    
	//Add Adjust By ar_invoice_no
	
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
	
	public double getTotalReceiptCNAmt(Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		double cnPaidAmt = 0;
		try {
			String sql = "select SUM(rl.PAID_AMOUNT) as cn_amt ";
			sql += "from t_receipt_cn rl, t_credit_note o ";
			sql += "where 1=1 ";
			sql += " and rl.credit_note_id = o.credit_note_id ";
			sql += " and o.active = 'Y' ";
			sql += " and o.doc_status = 'SV' ";
			sql += " and rl.receipt_id in ("
					+ " select receipt_id from " + MReceipt.TABLE_NAME + ""
					+ " where doc_status = '"+ Receipt.DOC_SAVE 
					+ "' ) ";

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while (rst.next()) {
				cnPaidAmt += rst.getDouble("PAID_AMOUNT");
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
		}
		return cnPaidAmt;
	}

	public double calculateCNCreditAmount(CreditNote cn) throws Exception {
		Connection conn = null;
		try{
			conn = new DBCPConnectionProvider().getConnection(conn);
			return calculateCNCreditAmountModel(conn, cn);
		}catch(Exception e){
			throw e;
		}finally{
			try{
				conn.close();
			}catch(Exception ee){}
		}
	}
	
	public double calculateCNCreditAmount(Connection conn,CreditNote cn) throws Exception {
		return calculateCNCreditAmountModel(conn, cn);
	}
	/**
	 * Calculate Credit Amount
	 * 
	 * @param order
	 * @return
	 * @throws Exception
	 */
	public double calculateCNCreditAmountModel(Connection conn,CreditNote cn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		double creditAmt = cn.getTotalAmount();
		double paidAmt = 0;
		try {

			String sql = "select rl.PAID_AMOUNT ";
			sql += "from t_receipt_cn rl, t_credit_note o ";
			sql += "where rl.credit_note_id = " + cn.getId();
			sql += "  and rl.credit_note_id = o.credit_note_id ";
			sql += "  and o.active = 'Y' and o.doc_status ='SV' ";
			sql += "  and rl.receipt_id in (select receipt_id from " + MReceipt.TABLE_NAME + " where doc_status = '"
					+ Receipt.DOC_SAVE + "' ) ";

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while (rst.next()) {
				creditAmt -= rst.getDouble("PAID_AMOUNT");
				paidAmt += rst.getDouble("PAID_AMOUNT");
			}
			//logger.debug("Calc creditAmt:"+creditAmt);
			
			cn.setCreditAmount(creditAmt);
			cn.setPaidAmount(paidAmt);
			cn.setRemainAmount(creditAmt - paidAmt);

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
		}
		return creditAmt;
	}
}
