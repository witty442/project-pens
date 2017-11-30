package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;

/**
 * Credit Note
 * 
 * @author atiz.b
 * @version $Id: CreditNote.java,v 1.0 15/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class Adjust extends I_PO implements Serializable {

	private static final long serialVersionUID = -422634918501997839L;

	/**
	 * Default Constructor
	 */
	public Adjust() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @throws Exception
	 */
	public Adjust(ResultSet rst) throws Exception {
		setId(rst.getInt("ADJUST_ID"));
		setAdjustDate(DateToolsUtil.convertToString(rst.getTimestamp("ADJUST_DATE")));
		setArInvoiceNo(rst.getString("AR_INVOICE_NO"));
		setAdjustType(rst.getString("ADJUST_TYPE"));
		setAdjustAmount(rst.getDouble("ADJUST_AMOUNT"));
		setAdjustType(rst.getString("ADJUST_TYPE"));
		setReason(rst.getString("REASON"));
		setComment(rst.getString("COMMENT"));
		/** Display */
		setDisplayLabel();
	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() throws Exception {
		
	}

	/** CREDIT_NOTE_ID */
	private int id;
	private String adjustDate;
	private String arInvoiceNo;
	private double adjustAmount;
	private String adjustType;
	private String reason;
	private String comment;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAdjustDate() {
		return adjustDate;
	}

	public void setAdjustDate(String adjustDate) {
		this.adjustDate = adjustDate;
	}

	public String getArInvoiceNo() {
		return arInvoiceNo;
	}

	public void setArInvoiceNo(String arInvoiceNo) {
		this.arInvoiceNo = arInvoiceNo;
	}

	public double getAdjustAmount() {
		return adjustAmount;
	}

	public void setAdjustAmount(double adjustAmount) {
		this.adjustAmount = adjustAmount;
	}

	public String getAdjustType() {
		return adjustType;
	}

	public void setAdjustType(String adjustType) {
		this.adjustType = adjustType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
