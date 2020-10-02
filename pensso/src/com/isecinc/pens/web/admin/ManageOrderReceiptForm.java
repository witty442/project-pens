package com.isecinc.pens.web.admin;

import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.Receipt;

public class ManageOrderReceiptForm extends I_Form {

	private static final long serialVersionUID = -6926286090895730569L;

	private String documentDate;
	private String documentDateFrom;
	private String documentDateTo;
	private String customerCode;
	private String customerName;
	private List<Order> orders;

	private List<Receipt> receipts;

	private int orderSize;

	private int receiptSize;

	
	public String getDocumentDateFrom() {
		return documentDateFrom;
	}

	public void setDocumentDateFrom(String documentDateFrom) {
		this.documentDateFrom = documentDateFrom;
	}

	public String getDocumentDateTo() {
		return documentDateTo;
	}

	public void setDocumentDateTo(String documentDateTo) {
		this.documentDateTo = documentDateTo;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public int getOrderSize() {
		return orderSize;
	}

	public void setOrderSize(int orderSize) {
		this.orderSize = orderSize;
	}

	public int getReceiptSize() {
		return receiptSize;
	}

	public void setReceiptSize(int receiptSize) {
		this.receiptSize = receiptSize;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public List<Receipt> getReceipts() {
		return receipts;
	}

	public void setReceipts(List<Receipt> receipts) {
		this.receipts = receipts;
	}

}
