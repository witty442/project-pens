package com.isecinc.pens.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.isecinc.core.model.I_PO;

public class StockDiscountLine extends I_PO implements Serializable{

	
	/**
	 * Default Constructor
	 */
	public StockDiscountLine() {}
	

	private static final long serialVersionUID = 8286170868166006317L;
	private String requestNumber ;
	private String inventoryItemId; 
	    
	private String status ;
	private String statusLabel;
	private String exported; 
	private String userId;
	
	private String created; 
	private String createdBy; 
	private String updated; 
	private String updateBy;
	private String actionDate;
	private String conversionRate;
	private boolean canEdit = false;

	private int no;
	
	/** ID */
	private int id;

	/** Line No */
	private int lineId;

	/** Order Id */
	private int orderId;

	/** Product */
	private Product product = new Product();
    private String productCode;
    private String productName;
    
	/** UOM */
  
    private String priQty;
    private String uom1ConvRate;
	private String uom2ConvRate;
	private String uom1Qty;
	private String uom2Qty;
	private String uom2;
	private String uom1Pac;
	private String uom2Pac;
	private String uom1Price;
	private String lineAmount;
	private String vatAmount;
	private String netAmount;
	private String arInvoiceNo;

	private String remainPriAllQty;
	private String remainPriQty;
	private String remainSubQty;
	private String remainAmount; 
	
	/** optional for report **/
	private String orderNo;
	private String customerName;
	private String customerCode;
	private Date orderDate;
	private String createDate;

	
	
	public String getRemainAmount() {
		return remainAmount;
	}


	public void setRemainAmount(String remainAmount) {
		this.remainAmount = remainAmount;
	}


	public String getVatAmount() {
		return vatAmount;
	}


	public void setVatAmount(String vatAmount) {
		this.vatAmount = vatAmount;
	}


	public String getRemainPriAllQty() {
		return remainPriAllQty;
	}


	public void setRemainPriAllQty(String remainPriAllQty) {
		this.remainPriAllQty = remainPriAllQty;
	}


	public String getRemainSubQty() {
		return remainSubQty;
	}


	public void setRemainSubQty(String remainSubQty) {
		this.remainSubQty = remainSubQty;
	}


	public String getPriQty() {
		return priQty;
	}


	public void setPriQty(String priQty) {
		this.priQty = priQty;
	}


	public String getUom1ConvRate() {
		return uom1ConvRate;
	}


	public void setUom1ConvRate(String uom1ConvRate) {
		this.uom1ConvRate = uom1ConvRate;
	}


	public String getUom2ConvRate() {
		return uom2ConvRate;
	}


	public void setUom2ConvRate(String uom2ConvRate) {
		this.uom2ConvRate = uom2ConvRate;
	}

    
	public String getRemainPriQty() {
		return remainPriQty;
	}


	public void setRemainPriQty(String remainPriQty) {
		this.remainPriQty = remainPriQty;
	}


	public String getConversionRate() {
		return conversionRate;
	}


	public void setConversionRate(String conversionRate) {
		this.conversionRate = conversionRate;
	}

	protected void setDisplayLabel() throws Exception {

	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}


	public String getActionDate() {
		return actionDate;
	}

	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getProductCode() {
		return productCode;
	}


	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}


	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}

	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	public String getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(String statusLabel) {
		this.statusLabel = statusLabel;
	}
	public String getRequestNumber() {
		return requestNumber;
	}
	public void setRequestNumber(String requestNumber) {
		this.requestNumber = requestNumber;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getExported() {
		return exported;
	}
	public void setExported(String exported) {
		this.exported = exported;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getLineId() {
		return lineId;
	}

	public void setLineId(int lineId) {
		this.lineId = lineId;
	}

	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
	public String getArInvoiceNo() {
		return arInvoiceNo;
	}
	public void setArInvoiceNo(String arInvoiceNo) {
		this.arInvoiceNo = arInvoiceNo;
	}
	
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getInventoryItemId() {
		return inventoryItemId;
	}

	public void setInventoryItemId(String inventoryItemId) {
		this.inventoryItemId = inventoryItemId;
	}


	public String getUom2() {
		return uom2;
	}


	public void setUom2(String uom2) {
		this.uom2 = uom2;
	}


	public String getUom1Qty() {
		return uom1Qty;
	}


	public void setUom1Qty(String uom1Qty) {
		this.uom1Qty = uom1Qty;
	}


	public String getUom2Qty() {
		return uom2Qty;
	}


	public void setUom2Qty(String uom2Qty) {
		this.uom2Qty = uom2Qty;
	}


	public String getUom1Pac() {
		return uom1Pac;
	}


	public void setUom1Pac(String uom1Pac) {
		this.uom1Pac = uom1Pac;
	}


	public String getUom2Pac() {
		return uom2Pac;
	}


	public void setUom2Pac(String uom2Pac) {
		this.uom2Pac = uom2Pac;
	}


	public String getUom1Price() {
		return uom1Price;
	}


	public void setUom1Price(String uom1Price) {
		this.uom1Price = uom1Price;
	}


	public String getLineAmount() {
		return lineAmount;
	}


	public void setLineAmount(String lineAmount) {
		this.lineAmount = lineAmount;
	}


	public String getNetAmount() {
		return netAmount;
	}


	public void setNetAmount(String netAmount) {
		this.netAmount = netAmount;
	}
    

}
