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
		setId(rst.getInt("RECEIPT_MATCH_ID"));
		setReceiptById(rst.getInt("RECEIPT_BY_ID"));
		setReceiptLineId(rst.getInt("RECEIPT_LINE_ID"));
		setPaidAmount(rst.getInt("PAID_AMOUNT"));

		setDisplayLabel();
	}

	protected void setDisplayLabel() throws Exception {

	}

	/** RECEIPT_MATCH_ID */
	private int id;

	/** RECEIPT_BY_ID */
	private int receiptById;

	/** RECEIPT_LINE_ID */
	private int receiptLineId;

	/** RECEIPT_ID */
	private int receiptId;

	/** PAID_AMOUNT */
	private double paidAmount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getReceiptById() {
		return receiptById;
	}

	public void setReceiptById(int receiptById) {
		this.receiptById = receiptById;
	}

	public int getReceiptLineId() {
		return receiptLineId;
	}

	public void setReceiptLineId(int receiptLineId) {
		this.receiptLineId = receiptLineId;
	}

	public double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(double paidAmount) {
		this.paidAmount = paidAmount;
	}

	public int getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(int receiptId) {
		this.receiptId = receiptId;
	}

}
