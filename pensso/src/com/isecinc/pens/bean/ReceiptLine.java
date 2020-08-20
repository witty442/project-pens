package com.isecinc.pens.bean;

import java.math.BigDecimal;
import java.sql.ResultSet;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.pens.util.ConvertNullUtil;

/**
 * Receipt Line Class
 * 
 * @author atiz.b
 * @version $Id: ReceiptLine.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ReceiptLine extends I_PO {

	private static final long serialVersionUID = 1392782377959417580L;

	public ReceiptLine() {}

	public ReceiptLine(ResultSet rst) throws Exception {
		setId(rst.getLong("RECEIPT_LINE_ID"));
		setLineNo(rst.getInt("LINE_NO"));
		setOrder(new MOrder().findInvoice(rst.getInt("INVOICE_ID")));
		setArInvoiceNo(rst.getString("AR_INVOICE_NO"));
		setSalesOrderNo(rst.getString("SALES_ORDER_NO"));
		setInvoiceAmount(rst.getDouble("INVOICE_AMOUNT"));
		setCreditAmount(rst.getDouble("CREDIT_AMOUNT"));
		setPaidAmount(rst.getDouble("PAID_AMOUNT"));
		setRemainAmount(rst.getDouble("REMAIN_AMOUNT"));
		setDescription(ConvertNullUtil.convertToString(rst.getString("DESCRIPTION")).trim());
		
		setDisplayLabel();
	}

	protected void setDisplayLabel() throws Exception {

	}

	/** RECEIPT_LINE_ID */
	private long id;

	/** RECEIPT_ID */
	private long receiptId;

	/** LINE_NO */
	private int lineNo;

	/** ORDER_ID */
	private Order order = new Order();

	/** AR_INVOICE_NO */
	private String arInvoiceNo;

	/** SALES_ORDER_NO */
	private String salesOrderNo;

	/** INVOICE_AMOUNT */
	private double invoiceAmount;

	/** CREDIT_AMOUNT */
	private double creditAmount;

	/** PAID_AMOUNT */
	private double paidAmount;

	/** REMAIN_AMOUNT */
	private double remainAmount;

	/** DESCIPRTION */
	private String description;


	// private String complete="N";

	private BigDecimal importTransId;
	
	
	public BigDecimal getImportTransId() {
		return importTransId;
	}

	public void setImportTransId(BigDecimal importTransId) {
		this.importTransId = importTransId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getLineNo() {
		return lineNo;
	}

	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}

	public String getArInvoiceNo() {
		return arInvoiceNo;
	}

	public void setArInvoiceNo(String arInvoiceNo) {
		this.arInvoiceNo = arInvoiceNo;
	}

	public String getSalesOrderNo() {
		return salesOrderNo;
	}

	public void setSalesOrderNo(String salesOrderNo) {
		this.salesOrderNo = salesOrderNo;
	}

	public double getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(double invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
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

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
