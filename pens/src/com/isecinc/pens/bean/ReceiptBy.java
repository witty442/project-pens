package com.isecinc.pens.bean;

import java.sql.ResultSet;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MReceiptMatch;
import com.isecinc.pens.model.MReceiptMatchCN;

/**
 * Receipt By Class
 * 
 * @author atiz.b
 * @version $Id: ReceiptBy.java,v 1.0 19/11/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ReceiptBy extends I_PO {

	private static final long serialVersionUID = 6882790115063403843L;

	/**
	 * Default Constructor
	 */
	public ReceiptBy() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @param rst
	 */
	public ReceiptBy(ResultSet rst) throws Exception {
		setId(rst.getInt("RECEIPT_BY_ID"));
		setReceiptId(rst.getInt("RECEIPT_ID"));
		setPaymentMethod(rst.getString("PAYMENT_METHOD"));
		setReceiptAmount(rst.getDouble("RECEIPT_AMOUNT"));
		setCreditCardType(ConvertNullUtil.convertToString(rst.getString("CREDIT_CARD_TYPE")).trim());
		setBank(ConvertNullUtil.convertToString(rst.getString("BANK")).trim());
		setChequeNo(ConvertNullUtil.convertToString(rst.getString("CHEQUE_NO")).trim());
		setChequeDate("");
		if (rst.getTimestamp("CHEQUE_DATE") != null)
			setChequeDate(DateToolsUtil.convertToString(rst.getTimestamp("CHEQUE_DATE")));
		setPaidAmount(rst.getDouble("PAID_AMOUNT"));
		setRemainAmount(rst.getDouble("REMAIN_AMOUNT"));
		setSeedId(rst.getString("SEED_ID"));
		setCreditcardExpired(ConvertNullUtil.convertToString(rst.getString("CREDITCARD_EXPIRED")).trim());
		setWriteOff(rst.getString("WRITE_OFF"));
		new MReceiptMatch().lookUp(this);
		new MReceiptMatchCN().lookUp(this);
		setDisplayLabel();
	}

	protected void setDisplayLabel() throws Exception {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD)) {
			if (r.getKey().equalsIgnoreCase(getPaymentMethod())) {
				setPaymentMethodName(r.getName());
				break;
			}
		}

		for (References r : InitialReferences.getReferenes().get(InitialReferences.BANK)) {
			if (r.getKey().equalsIgnoreCase(getBank())) {
				setBankName(r.getName());
				break;
			}
		}
	}

	/** RECEIPT_BY_ID */
	private int id;

	/** RECEIPT_ID */
	private int receiptId;

	/** PAYMENT_METHOD */
	private String paymentMethod;

	/** CREDIT_CARD_TYPE */
	private String creditCardType;

	/** BANK */
	private String bank;

	/** CHEQUE_NO */
	private String chequeNo;

	/** CHEQE_DATE */
	private String chequeDate;

	/** RECEIPT_AMOUNT */
	private double receiptAmount;

	/** PAID_AMOUNT */
	private double paidAmount;

	/** REMAIN_AMOUNT */
	private double remainAmount;

	// Transient
	/** Payment Method Name */
	private String paymentMethodName;

	/** Bank Name */
	private String bankName;

	/** Credit Type Name */
	private String creditTypeName;

	/** Seed ID */
	private String seedId;

	/** All Bills ID */
	private String allBillId;

	/** All Paids */
	private String allPaid;

	/** All CNs ID */
	private String allCNId;

	/** All CN Paids */
	private String allCNPaid;

	/** CREDITCARD_EXPIRED */
	private String creditcardExpired;

	/** WRITE_OFF */
	private String writeOff;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(int receiptId) {
		this.receiptId = receiptId;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getCreditCardType() {
		return creditCardType;
	}

	public void setCreditCardType(String creditCardType) {
		this.creditCardType = creditCardType;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}

	public double getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(double receiptAmount) {
		this.receiptAmount = receiptAmount;
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

	public String getPaymentMethodName() {
		return paymentMethodName;
	}

	public void setPaymentMethodName(String paymentMethodName) {
		this.paymentMethodName = paymentMethodName;
	}

	public String getCreditTypeName() {
		return creditTypeName;
	}

	public void setCreditTypeName(String creditTypeName) {
		this.creditTypeName = creditTypeName;
	}

	public String getSeedId() {
		return seedId;
	}

	public void setSeedId(String seedId) {
		this.seedId = seedId;
	}

	public String getAllBillId() {
		return allBillId;
	}

	public void setAllBillId(String allBillId) {
		this.allBillId = allBillId;
	}

	public String getAllPaid() {
		return allPaid;
	}

	public void setAllPaid(String allPaid) {
		this.allPaid = allPaid;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCreditcardExpired() {
		return creditcardExpired;
	}

	public void setCreditcardExpired(String creditcardExpired) {
		this.creditcardExpired = creditcardExpired;
	}

	public String getWriteOff() {
		return writeOff;
	}

	public void setWriteOff(String writeOff) {
		this.writeOff = writeOff;
	}

	public String getAllCNId() {
		return allCNId;
	}

	public void setAllCNId(String allCNId) {
		this.allCNId = allCNId;
	}

	public String getAllCNPaid() {
		return allCNPaid;
	}

	public void setAllCNPaid(String allCNPaid) {
		this.allCNPaid = allCNPaid;
	}

}
