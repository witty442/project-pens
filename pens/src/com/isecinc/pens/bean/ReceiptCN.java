package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.model.MCreditNote;

/**
 * Receipt with CN
 * 
 * @author atiz.b
 * @version $Id: ReceiptCN.java,v 1.0 15/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ReceiptCN extends I_PO implements Serializable {

	private static final long serialVersionUID = 7945228045891465736L;

	/**
	 * Default Constructor
	 */
	public ReceiptCN() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @throws Exception
	 */
	public ReceiptCN(ResultSet rst) throws Exception {
		setId(rst.getInt("RECEIPT_CN_ID"));
		setCreditNote(new MCreditNote().find(rst.getString("CREDIT_NOTE_ID")));
		setReceiptId(rst.getInt("RECEIPT_ID"));
		setCreditAmount(rst.getDouble("CREDIT_AMOUNT"));
		setPaidAmount(rst.getDouble("PAID_AMOUNT"));
		setRemainAmount(rst.getDouble("REMAIN_AMOUNT"));
		/** Display */
		setDisplayLabel();
	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() throws Exception {}

	/** RECEIPT_CN_ID */
	private int id;

	/** CREDIT_NOTE_ID */
	private CreditNote creditNote = new CreditNote();

	/** RECEIPT_ID */
	private int receiptId;

	/** CREDIT_AMOUNT */
	private double creditAmount;

	/** PAID_AMOUNT */
	private double paidAmount;

	/** REMAIN_AMOUNT */
	private double remainAmount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CreditNote getCreditNote() {
		return creditNote;
	}

	public void setCreditNote(CreditNote creditNote) {
		this.creditNote = creditNote;
	}

	public int getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(int receiptId) {
		this.receiptId = receiptId;
	}

	public double getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(double creditAmount) {
		this.creditAmount = creditAmount;
	}

	public double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(double paidAmount) {
		this.paidAmount = paidAmount;
	}

	public double getRemainAmount() {
		return remainAmount;
	}

	public void setRemainAmount(double remainAmount) {
		this.remainAmount = remainAmount;
	}

}
