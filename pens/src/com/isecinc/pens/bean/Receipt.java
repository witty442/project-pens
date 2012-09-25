package com.isecinc.pens.bean;

import java.sql.ResultSet;
import java.util.List;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MReceiptBy;
import com.isecinc.pens.model.MReceiptCN;
import com.isecinc.pens.model.MReceiptLine;
import com.isecinc.pens.model.MUser;

/**
 * Receipt Class
 * 
 * @author atiz.b
 * @version $Id: Receipt.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class Receipt extends I_PO {

	private static final long serialVersionUID = -6092361860126609201L;

	public static final String MODERN_TARDE = "MT";
	public static final String CREDIT = "CR";
	public static final String CASH = "CS";
	public static final String DIRECT_DELIVERY = "DD";

	public Receipt() {}

	public Receipt(ResultSet rst) throws Exception {
		setId(rst.getInt("RECEIPT_ID"));
		setReceiptNo(rst.getString("RECEIPT_NO").trim());
		setReceiptDate(DateToolsUtil.convertToString(rst.getTimestamp("RECEIPT_DATE")));
		setOrderType(rst.getString("ORDER_TYPE").trim());
		setCustomerId(rst.getInt("CUSTOMER_ID"));
		setCustomerName(rst.getString("CUSTOMER_NAME").trim());
		setPaymentMethod(ConvertNullUtil.convertToString(rst.getString("PAYMENT_METHOD")).trim());
		setBank(ConvertNullUtil.convertToString(rst.getString("BANK")).trim());
		setChequeNo(ConvertNullUtil.convertToString(rst.getString("CHEQUE_NO")).trim());
		setChequeDate("");
		if (rst.getTimestamp("CHEQUE_DATE") != null)
			setChequeDate(DateToolsUtil.convertToString(rst.getTimestamp("CHEQUE_DATE")));
		setReceiptAmount(rst.getDouble("RECEIPT_AMOUNT"));
		setInterfaces(rst.getString("INTERFACES").trim());
		setDocStatus(rst.getString("DOC_STATUS").trim());
		setSalesRepresent(new MUser().find(rst.getString("USER_ID")));
		setCreditCardType(ConvertNullUtil.convertToString(rst.getString("CREDIT_CARD_TYPE")).trim());
		setDescription(ConvertNullUtil.convertToString(rst.getString("DESCRIPTION")).trim());
		setPrepaid(rst.getString("PREPAID").trim());
		setApplyAmount(rst.getDouble("APPLY_AMOUNT"));

		setExported(rst.getString("EXPORTED"));

		setInternalBank(ConvertNullUtil.convertToString(rst.getString("INTERNAL_BANK")));

		// Lookup Lines
		setReceiptLines(new MReceiptLine().lookUp(getId()));

		// Lookup Bys
		setReceiptBys(new MReceiptBy().lookUp(getId()));

		// Lookup CNs
		setReceiptCNs(new MReceiptCN().lookUp(getId()));

		// set display
		setDisplayLabel();

	}

	/**
	 * Set display
	 */
	protected void setDisplayLabel() throws Exception {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.DOC_STATUS)) {
			if (r.getKey().equalsIgnoreCase(getDocStatus())) {
				setDocStatusLabel(r.getName());
				break;
			}
		}
		String temp = "";
		for (ReceiptLine l : getReceiptLines()) {
			temp += ", " + l.getArInvoiceNo();
		}
		if (temp.length() > 0) temp = temp.substring(1).trim();
		setInvoiceNoLabel(temp);
	}

	/** RECEIPT_ID */
	private int id;

	/** RECEIPT_NO */
	private String receiptNo;

	/** RECEIPT_DATE */
	private String receiptDate;

	/** ORDER_TYPE */
	private String orderType;

	/** CUSTOMER_ID */
	private int customerId;

	/** CUSTOMER_NAME */
	private String customerName;

	/** PAYMENT_METHOD */
	private String paymentMethod;

	/** BANK */
	private String bank;

	/** CHEQUE_NO */
	private String chequeNo;

	/** CHEQUE_DATE */
	private String chequeDate;

	/** RECEIPT_AMOUNT */
	private double receiptAmount;

	/** INTERFACES */
	private String interfaces;

	/** DOC_STATUS */
	private String docStatus;

	/** USER_ID */
	private User salesRepresent = new User();

	/** Doc Status Label */
	private String docStatusLabel;

	/** From */
	private String receiptDateFrom;
	
	private String orderNoFrom;

	/** To */
	private String receiptDateTo;
	
	private String orderNoTo;

	/** Search Invoice No */
	private String searchInvoiceNo;

	/** RECEIPT LINE */
	private List<ReceiptLine> receiptLines = null;

	/** INVOICE NO LABEL */
	private String invoiceNoLabel;

	/** CREDIT_CARD_TYPE */
	private String creditCardType;

	/** DESCRIPTION */
	private String description;

	/** PREPAID */
	private String prepaid = "N";

	/** RECEIPT BY */
	private List<ReceiptBy> receiptBys = null;

	/** APPLY_AMOUNT */
	private double applyAmount;

	/** EXPORTED */
	private String exported;

	/** INTERNAL_BANK */
	private String internalBank;

	/** RECEIPT CN */
	private List<ReceiptCN> receiptCNs = null;
      
	private String reason;
	
	private String isPDPaid;
	private String pdPaidDate;
	private String pdPaymentMethod;
	
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
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

	public String getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public User getSalesRepresent() {
		return salesRepresent;
	}

	public void setSalesRepresent(User salesRepresent) {
		this.salesRepresent = salesRepresent;
	}

	public String getDocStatusLabel() {
		return docStatusLabel;
	}

	public void setDocStatusLabel(String docStatusLabel) {
		this.docStatusLabel = docStatusLabel;
	}

	public String getReceiptDateFrom() {
		return receiptDateFrom;
	}

	public void setReceiptDateFrom(String receiptDateFrom) {
		this.receiptDateFrom = receiptDateFrom;
	}

	public String getReceiptDateTo() {
		return receiptDateTo;
	}

	public void setReceiptDateTo(String receiptDateTo) {
		this.receiptDateTo = receiptDateTo;
	}

	public String getSearchInvoiceNo() {
		return searchInvoiceNo;
	}

	public void setSearchInvoiceNo(String searchInvoiceNo) {
		this.searchInvoiceNo = searchInvoiceNo;
	}

	public List<ReceiptLine> getReceiptLines() {
		return receiptLines;
	}

	public void setReceiptLines(List<ReceiptLine> receiptLines) {
		this.receiptLines = receiptLines;
	}

	public String getInvoiceNoLabel() {
		return invoiceNoLabel;
	}

	public void setInvoiceNoLabel(String invoiceNoLabel) {
		this.invoiceNoLabel = invoiceNoLabel;
	}

	public String getCreditCardType() {
		return creditCardType;
	}

	public void setCreditCardType(String creditCardType) {
		this.creditCardType = creditCardType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrepaid() {
		return prepaid;
	}

	public void setPrepaid(String prepaid) {
		this.prepaid = prepaid;
	}

	public List<ReceiptBy> getReceiptBys() {
		return receiptBys;
	}

	public void setReceiptBys(List<ReceiptBy> receiptBys) {
		this.receiptBys = receiptBys;
	}

	public double getApplyAmount() {
		return applyAmount;
	}

	public void setApplyAmount(double applyAmount) {
		this.applyAmount = applyAmount;
	}

	public String getExported() {
		return exported;
	}

	public void setExported(String exported) {
		this.exported = exported;
	}

	public List<ReceiptCN> getReceiptCNs() {
		return receiptCNs;
	}

	public void setReceiptCNs(List<ReceiptCN> receiptCNs) {
		this.receiptCNs = receiptCNs;
	}

	public String getInternalBank() {
		return internalBank;
	}

	public void setInternalBank(String internalBank) {
		this.internalBank = internalBank;
	}

	public String getIsPDPaid() {
		return isPDPaid;
	}

	public void setIsPDPaid(String isPDPaid) {
		this.isPDPaid = isPDPaid;
	}

	public String getPdPaidDate() {
		return pdPaidDate;
	}

	public void setPdPaidDate(String pdPaidDate) {
		this.pdPaidDate = pdPaidDate;
	}

	public String getPdPaymentMethod() {
		return pdPaymentMethod;
	}

	public void setPdPaymentMethod(String pdPaymentMethod) {
		this.pdPaymentMethod = pdPaymentMethod;
	}

	public String getOrderNoFrom() {
		return orderNoFrom;
	}

	public void setOrderNoFrom(String orderNoFrom) {
		this.orderNoFrom = orderNoFrom;
	}

	public String getOrderNoTo() {
		return orderNoTo;
	}

	public void setOrderNoTo(String orderNoTo) {
		this.orderNoTo = orderNoTo;
	}
	
}
