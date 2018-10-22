package com.isecinc.pens.web.maya;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

//implements Comparable<MayaBean>,
public class MayaBean implements  Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4179679304223069016L;
	//Criteria
	private String type;
	private String startDate;
	private String endDate;
	private String groupCodeFrom;
	private String groupCodeTo;
	private String styleFrom;
	private String styleTo;
	private String pensItemFrom;
	private String pensItemTo;

	//results
	private String orderDate;
	private String orderNo;
	private String pensItem;
    private String pensItemDesc;
    private String barcode;
    private String style;
	private String qty;
	private String freeItem;
	private String unitPrice;
	private String lineAmount;
	private String discount;
	private String vatAmount;
    private String totalAmount;
    private List<MayaBean> itemsList;
    private MayaBean summary;

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public String getGroupCodeFrom() {
		return groupCodeFrom;
	}
	public void setGroupCodeFrom(String groupCodeFrom) {
		this.groupCodeFrom = groupCodeFrom;
	}
	public String getGroupCodeTo() {
		return groupCodeTo;
	}
	public void setGroupCodeTo(String groupCodeTo) {
		this.groupCodeTo = groupCodeTo;
	}
	public String getStyleFrom() {
		return styleFrom;
	}
	public void setStyleFrom(String styleFrom) {
		this.styleFrom = styleFrom;
	}
	public String getStyleTo() {
		return styleTo;
	}
	public void setStyleTo(String styleTo) {
		this.styleTo = styleTo;
	}
	public String getPensItemFrom() {
		return pensItemFrom;
	}
	public void setPensItemFrom(String pensItemFrom) {
		this.pensItemFrom = pensItemFrom;
	}
	public String getPensItemTo() {
		return pensItemTo;
	}
	public void setPensItemTo(String pensItemTo) {
		this.pensItemTo = pensItemTo;
	}
	
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getPensItem() {
		return pensItem;
	}
	public void setPensItem(String pensItem) {
		this.pensItem = pensItem;
	}
	public String getPensItemDesc() {
		return pensItemDesc;
	}
	public void setPensItemDesc(String pensItemDesc) {
		this.pensItemDesc = pensItemDesc;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getFreeItem() {
		return freeItem;
	}
	public void setFreeItem(String freeItem) {
		this.freeItem = freeItem;
	}
	public String getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getLineAmount() {
		return lineAmount;
	}
	public void setLineAmount(String lineAmount) {
		this.lineAmount = lineAmount;
	}
	
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getVatAmount() {
		return vatAmount;
	}
	public void setVatAmount(String vatAmount) {
		this.vatAmount = vatAmount;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public List<MayaBean> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<MayaBean> itemsList) {
		this.itemsList = itemsList;
	}
	public MayaBean getSummary() {
		return summary;
	}
	public void setSummary(MayaBean summary) {
		this.summary = summary;
	}
    
   	
	/* public int compareTo(MayaBean o) {
	     return Comparators.STORE_CODE_GROUP_ASC.compare(this, o);
	  }*/
	 
	/* public static class Comparators {
		  public static Comparator<MayaBean> STORE_CODE_GROUP_ASC = new Comparator<MayaBean>() {
	            @Override
	            public int compare(MayaBean o1, MayaBean o2) {
	            	int order1= o1.getStoreCode().compareTo(o2.getStoreCode());
	            	
	            	if (order1 == 0) {
	                    // Strings are equal, sort by Group
	                    return o1.getGroup().compareTo(o2.getGroup());
	                }else {
	                    return order1;
	                }
	            }
	        };
	      
	 }*/
	
}
