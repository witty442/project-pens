package com.isecinc.pens.web.stockinv;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class StockInvBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9008978294153751220L;
	private int no;
	private long headerId;
	private long lineId;
	private String transactionDate;
	private String transType;
	private String transTypeDesc;
	private String productId;
	private String productCode;
	private String productName;
	private String price;
	private String uom;
	private String uom1;
	private String uom2;
	private String qty;
	private String qty1;
	private String qty2;
	private String status;
	private String createUser;
	private String updateUser;
	private List<StockInvBean> items;
	private String qty1Style;
	private String qty1Readonly;
	private String qty2Style;
	private String qty2Readonly;
	private String deleteLineIds;

	
	//optional
	private boolean canEdit = false;
	private boolean canCancel = false;
	private boolean canConfirm = false;
	
	
	public String getDeleteLineIds() {
		return deleteLineIds;
	}
	public void setDeleteLineIds(String deleteLineIds) {
		this.deleteLineIds = deleteLineIds;
	}
	
	public String getQty1Style() {
		return qty1Style;
	}
	public void setQty1Style(String qty1Style) {
		this.qty1Style = qty1Style;
	}
	public String getQty1Readonly() {
		return qty1Readonly;
	}
	public void setQty1Readonly(String qty1Readonly) {
		this.qty1Readonly = qty1Readonly;
	}
	public String getQty2Style() {
		return qty2Style;
	}
	public void setQty2Style(String qty2Style) {
		this.qty2Style = qty2Style;
	}
	public String getQty2Readonly() {
		return qty2Readonly;
	}
	public void setQty2Readonly(String qty2Readonly) {
		this.qty2Readonly = qty2Readonly;
	}
	public long getHeaderId() {
		return headerId;
	}
	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}
	public String getTransTypeDesc() {
		return transTypeDesc;
	}
	public void setTransTypeDesc(String transTypeDesc) {
		this.transTypeDesc = transTypeDesc;
	}
	public boolean isCanConfirm() {
		return canConfirm;
	}
	public void setCanConfirm(boolean canConfirm) {
		this.canConfirm = canConfirm;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	
	public long getLineId() {
		return lineId;
	}
	public void setLineId(long lineId) {
		this.lineId = lineId;
	}
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getTransType() {
		return transType;
	}
	public void setTransType(String transType) {
		this.transType = transType;
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
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getUom1() {
		return uom1;
	}
	public void setUom1(String uom1) {
		this.uom1 = uom1;
	}
	public String getUom2() {
		return uom2;
	}
	public void setUom2(String uom2) {
		this.uom2 = uom2;
	}
	
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getQty1() {
		return qty1;
	}
	public void setQty1(String qty1) {
		this.qty1 = qty1;
	}
	public String getQty2() {
		return qty2;
	}
	public void setQty2(String qty2) {
		this.qty2 = qty2;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public List<StockInvBean> getItems() {
		return items;
	}
	public void setItems(List<StockInvBean> items) {
		this.items = items;
	}
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	public boolean isCanCancel() {
		return canCancel;
	}
	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}

	
}
