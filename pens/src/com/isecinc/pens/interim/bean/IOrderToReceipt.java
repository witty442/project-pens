package com.isecinc.pens.interim.bean;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MReceiptLine;

public class IOrderToReceipt extends I_PO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6321077952678082609L;

	protected void setDisplayLabel() throws Exception {
		// TODO Auto-generated method stub
		for (References r : InitialReferences.getReferenes().get(InitialReferences.ACTIVE)) {
			if (r.getKey().equalsIgnoreCase("Y")) {
				setActiveLabel(r.getName());
				break;
			}
		}
	}
	
	public IOrderToReceipt(){
		
	}
	
	public IOrderToReceipt(ResultSet rs) throws Exception{
		setArInvoiceNo(rs.getString("AR_INVOICE_NO"));
		setOrderID(rs.getInt("ORDER_ID"));
		setCustomerName(rs.getString("CUSTOMER_NAME"));
		setCustomerID(rs.getInt("CUSTOMER_ID"));
		setOrderType(rs.getString("ORDER_TYPE"));
		setLoadDate(rs.getTimestamp("LOAD_DATE"));
		setSalesOrderNo(rs.getString("SALES_ORDER_NO"));
		setTotalAmount(rs.getDouble("TOTAL_AMOUNT"));
		setSessionId(rs.getString("SESSION_ID"));
		setReceiptNo(rs.getString("RECEIPT_NO"));
		setErrMsg(rs.getString("ERROR_MESSAGE"));
		
		Order order = new MOrder().find(""+rs.getInt("ORDER_ID"));
		if(order == null || order.getId() <=0)
			return ;
			
		double creditAmt = new MReceiptLine().calculateCreditAmount(order);
		
		setOrder(order);
		setCreditAmount(creditAmt);
		
		
	}
	
	private String arInvoiceNo;
	private String salesOrderNo;
	private String orderType;
	private String customerName;
	private int orderID ;
	private int customerID;
	private double totalAmount;
	private double creditAmount;
	private Timestamp loadDate;
	private String receiptNo;
	private String errMsg;
	private Timestamp createdDate;
	private String sessionId;
	
	private Order order ;

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
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public int getOrderID() {
		return orderID;
	}
	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}
	public int getCustomerID() {
		return customerID;
	}
	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public double getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(double creditAmount) {
		this.creditAmount = creditAmount;
	}
	public Timestamp getLoadDate() {
		return loadDate;
	}
	public void setLoadDate(Timestamp loadDate) {
		this.loadDate = loadDate;
	}
	public String getReceiptNo() {
		return receiptNo;
	}
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public Timestamp getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
	
}
