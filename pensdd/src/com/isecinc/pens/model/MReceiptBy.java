package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import util.DateToolsUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.ReceiptBy;
import com.isecinc.pens.process.SequenceProcess;

/**
 * Receipt BY Model
 * 
 * @author atiz.b
 * @version $Id: MReceiptBy.java,v 1.0 21/11/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MReceiptBy extends I_Model<ReceiptBy> {

	private static final long serialVersionUID = -8532039857520296789L;

	public static String TABLE_NAME = "t_receipt_by";
	public static String COLUMN_ID = "RECEIPT_BY_ID";

	private String[] columns = { COLUMN_ID, "PAYMENT_METHOD", "BANK", "CHEQUE_NO", "CHEQUE_DATE", "RECEIPT_AMOUNT",
			"CREDIT_CARD_TYPE", "PAID_AMOUNT", "REMAIN_AMOUNT", "RECEIPT_ID", "CREATED_BY", "UPDATED_BY", "SEED_ID",
			"CREDITCARD_EXPIRED", "WRITE_OFF","credit_card_name" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ReceiptBy find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, ReceiptBy.class);
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
	public ReceiptBy[] search(String whereCause) throws Exception {
		List<ReceiptBy> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ReceiptBy.class);
		if (pos.size() == 0) return null;
		ReceiptBy[] array = new ReceiptBy[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param receiptBy
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(ReceiptBy receiptBy, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (receiptBy.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
		} else {
			id = receiptBy.getId();
		}
		
		Object[] values = { id, receiptBy.getPaymentMethod(), receiptBy.getBank(), receiptBy.getChequeNo(),
				receiptBy.getChequeDate() != null ? DateToolsUtil.convertToTimeStamp(receiptBy.getChequeDate()) : null, 
				receiptBy.getReceiptAmount(),
				receiptBy.getCreditCardType(), receiptBy.getPaidAmount(), receiptBy.getRemainAmount(),
				receiptBy.getReceiptId(), activeUserID, activeUserID, receiptBy.getSeedId(),
				receiptBy.getCreditcardExpired(), receiptBy.getWriteOff() != null ? receiptBy.getWriteOff() : "N", receiptBy.getCreditCardName() };
		if (super.save(TABLE_NAME, columns, values, receiptBy.getId(), conn)) {
			receiptBy.setId(id);
		}
		return true;
	}

	/**
	 * Lookup
	 * 
	 * @param receiptId
	 * @return
	 */
	public List<ReceiptBy> lookUp(int receiptId) {
		List<ReceiptBy> pos = new ArrayList<ReceiptBy>();
		try {
			String whereCause = " AND RECEIPT_ID = " + receiptId + " ORDER BY " + COLUMN_ID;
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ReceiptBy.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
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
}