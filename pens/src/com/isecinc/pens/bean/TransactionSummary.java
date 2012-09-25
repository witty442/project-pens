package com.isecinc.pens.bean;

import java.io.Serializable;

/**
 * Transaction Summary
 * 
 * @author atiz.b
 * @version $Id: TransactionSummary.java,v 1.0 08/12/2010 15:52:00 atiz.b Exp $
 * 
 */
public class TransactionSummary implements Serializable {
	private static final long serialVersionUID = 8473415159471918746L;

	private String transactionDate;
	private String transactionProduct;

	private int stockMainUOM;
	private int stockSubUOM;

	private int orderMainUOM;
	private int orderSubUOM;

	private int promoMainUOM;
	private int promoSubUOM;

	private int userId;
	private String customerId;
	private String orderType;
	private String dateFrom;
	private String dateTo;

	private int priceListId;

	public String toString() {
		return String.format("Trx Summary %s-[%s] stock[%s/%s] sales[%s/%s] promo[%s/%s]", getTransactionDate(),
				getTransactionProduct(), getStockMainUOM(), getStockSubUOM(), getOrderMainUOM(), getOrderSubUOM(),
				getPromoMainUOM(), getPromoSubUOM());
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionProduct() {
		return transactionProduct;
	}

	public void setTransactionProduct(String transactionProduct) {
		this.transactionProduct = transactionProduct;
	}

	public int getStockMainUOM() {
		return stockMainUOM;
	}

	public void setStockMainUOM(int stockMainUOM) {
		this.stockMainUOM = stockMainUOM;
	}

	public int getStockSubUOM() {
		return stockSubUOM;
	}

	public void setStockSubUOM(int stockSubUOM) {
		this.stockSubUOM = stockSubUOM;
	}

	public int getOrderMainUOM() {
		return orderMainUOM;
	}

	public void setOrderMainUOM(int orderMainUOM) {
		this.orderMainUOM = orderMainUOM;
	}

	public int getOrderSubUOM() {
		return orderSubUOM;
	}

	public void setOrderSubUOM(int orderSubUOM) {
		this.orderSubUOM = orderSubUOM;
	}

	public int getPromoMainUOM() {
		return promoMainUOM;
	}

	public void setPromoMainUOM(int promoMainUOM) {
		this.promoMainUOM = promoMainUOM;
	}

	public int getPromoSubUOM() {
		return promoSubUOM;
	}

	public void setPromoSubUOM(int promoSubUOM) {
		this.promoSubUOM = promoSubUOM;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public int getPriceListId() {
		return priceListId;
	}

	public void setPriceListId(int priceListId) {
		this.priceListId = priceListId;
	}

}
