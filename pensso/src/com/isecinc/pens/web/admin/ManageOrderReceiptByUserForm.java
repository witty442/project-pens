package com.isecinc.pens.web.admin;

import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.Receipt;

public class ManageOrderReceiptByUserForm extends I_Form {

	private static final long serialVersionUID = -6926286090895730569L;

	private String documentDate;

	private List<Order> orders;

	private List<Receipt> receipts;

	private int orderSize;

	private int receiptSize;

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
