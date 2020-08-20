package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;
import com.pens.util.DateToolsUtil;

/**
 * Credit Note
 * 
 * @author atiz.b
 * @version $Id: CreditNote.java,v 1.0 15/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class CreditNote extends I_PO implements Serializable {

	private static final long serialVersionUID = -422634918501997839L;

	/**
	 * Default Constructor
	 */
	public CreditNote() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @throws Exception
	 */
	public CreditNote(ResultSet rst) throws Exception {
		setId(rst.getInt("CREDIT_NOTE_ID"));
		setCreditNoteNo(rst.getString("CREDIT_NOTE_NO"));
		setDocumentDate(DateToolsUtil.convertToString(rst.getTimestamp("DOCUMENT_DATE")));
		setArInvoiceNo(rst.getString("AR_INVOICE_NO"));
		setTotalAmount(rst.getDouble("TOTAL_AMOUNT"));
		setActive(rst.getString("ACTIVE"));

		/** Display */
		setDisplayLabel();
	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() throws Exception {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.ACTIVE)) {
			if (r.getKey().equalsIgnoreCase(getActive())) {
				setActiveLabel(r.getName());
				break;
			}
		}
	}

	/** CREDIT_NOTE_ID */
	private int id;

	/** CREDIT_NOTE_NO */
	private String creditNoteNo;

	/** DOCUMENT_DATE */
	private String documentDate;

	/** AR_INVOICE_NO */
	private String arInvoiceNo;

	/** TOTAL_AMOUNT */
	private double totalAmount;

	// --Transient
	/** CREDIT_AMOUNT */
	private double creditAmount;

	/** PAID_AMOUNT */
	private double paidAmount;

	/** REMAIN_AMOUNT */
	private double remainAmount;

	/** ACTIVE */
	private String active;

	/** USER_ID */
	private int userId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCreditNoteNo() {
		return creditNoteNo;
	}

	public void setCreditNoteNo(String creditNoteNo) {
		this.creditNoteNo = creditNoteNo;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}

	public String getArInvoiceNo() {
		return arInvoiceNo;
	}

	public void setArInvoiceNo(String arInvoiceNo) {
		this.arInvoiceNo = arInvoiceNo;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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
