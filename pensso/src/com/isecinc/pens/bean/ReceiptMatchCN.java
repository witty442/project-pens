package com.isecinc.pens.bean;

import java.sql.ResultSet;

import com.isecinc.core.model.I_PO;

/**
 * Receipt Match CN Class
 * 
 * @author atiz.b
 * @version $Id: ReceiptMatchCN.java,v 1.0 19/11/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ReceiptMatchCN extends I_PO {

	private static final long serialVersionUID = 6882790115063403843L;

	/**
	 * Default Constructor
	 */
	public ReceiptMatchCN() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @param rst
	 */
	public ReceiptMatchCN(ResultSet rst) throws Exception {
		setId(rst.getLong("RECEIPT_MATCH_CN_ID"));
		setReceiptById(rst.getLong("RECEIPT_BY_ID"));
		setReceiptCNId(rst.getLong("RECEIPT_CN_ID"));
		setPaidAmount(rst.getInt("PAID_AMOUNT"));

		setDisplayLabel();
	}

	protected void setDisplayLabel() throws Exception {

	}

	/** RECEIPT_MATCH_ID */
	private long id;

	/** RECEIPT_BY_ID */
	private long receiptById;

	/** RECEIPT_CN_ID */
	private long receiptCNId;

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

	public long getReceiptCNId() {
		return receiptCNId;
	}

	public void setReceiptCNId(long receiptCNId) {
		this.receiptCNId = receiptCNId;
	}

}
