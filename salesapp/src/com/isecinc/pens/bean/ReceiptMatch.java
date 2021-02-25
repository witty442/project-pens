package com.isecinc.pens.bean;

import java.sql.ResultSet;

import com.isecinc.core.model.I_PO;

/**
 * Receipt Match Class
 * 
 * @author atiz.b
 * @version $Id: ReceiptMatch.java,v 1.0 19/11/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ReceiptMatch extends I_PO {

	private static final long serialVersionUID = 6882790115063403843L;

	/**
	 * Default Constructor
	 */
	public ReceiptMatch() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @param rst
	 */
	public ReceiptMatch(ResultSet rst) throws Exception {
		setId(rst.getLong("RECEIPT_MATCH_ID"));
		setReceiptById(rst.getLong("RECEIPT_BY_ID"));
		setReceiptLineId(rst.getLong("RECEIPT_LINE_ID"));
		setPaidAmount(rst.getInt("PAID_AMOUNT"));

		setDisplayLabel();
	}

	protected void setDisplayLabel() throws Exception {

	}

	/** RECEIPT_MATCH_ID */
	private long id;

	/** RECEIPT_BY_ID */
	private long receiptById;

	/** RECEIPT_LINE_ID */
	private long receiptLineId;

	/** RECEIPT_ID */
	private long receiptId;

	/** PAID_AMOUNT */
	private double paidAmount;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getReceiptById() {
		return receiptById;
	}

	public void setReceiptById(long receiptById) {
		this.receiptById = receiptById;
	}

	public long getReceiptLineId() {
		return receiptLineId;
	}

	public void setReceiptLineId(long receiptLineId) {
		this.receiptLineId = receiptLineId;
	}

	public double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(double paidAmount) {
		this.paidAmount = paidAmount;
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

}
