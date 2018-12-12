package com.isecinc.pens.web.shop;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

//implements Comparable<MayaBean>,
public class ShopBean implements  Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4179679304223069016L;
	//Criteria
	private String custGroup;
	private String type;
	private String asOfDate;
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
	private String groupCode;
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
    private String totalAmountExVat;
    //stockOnhand
    private String initSaleQty;
    private String transInQty;
    private String saleOutQty;
    private String saleReturnQty;
    private String adjustQty;
    private String onhandQty;
    
    //shopPromotion
    private long promoId;
    private long subPromoId;
    private String promoName;
    private String subPromoName;
    private String startPromtionQty;
    private String endPromtionQty;
    private String discountPercent;
    private String discountAmt;
    private String wholeSellAmt;
    private String retailSellAmt;
    private String sellAfDisc;
    private String wacoalAmt;
    private String pensAmt;
    private String userName;
    private List<ShopBean> itemsList;
    private List<ShopBean> itemsDeleteList;
    private ShopBean summary;
    private int totalRec;
    //control 
    private boolean canEdit;
    private String reportType;
    
    
	public String getTotalAmountExVat() {
		return totalAmountExVat;
	}
	public void setTotalAmountExVat(String totalAmountExVat) {
		this.totalAmountExVat = totalAmountExVat;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public int getTotalRec() {
		return totalRec;
	}
	public void setTotalRec(int totalRec) {
		this.totalRec = totalRec;
	}
	public String getDiscountAmt() {
		return discountAmt;
	}
	public void setDiscountAmt(String discountAmt) {
		this.discountAmt = discountAmt;
	}
	public String getWholeSellAmt() {
		return wholeSellAmt;
	}
	public void setWholeSellAmt(String wholeSellAmt) {
		this.wholeSellAmt = wholeSellAmt;
	}
	public String getRetailSellAmt() {
		return retailSellAmt;
	}
	public void setRetailSellAmt(String retailSellAmt) {
		this.retailSellAmt = retailSellAmt;
	}
	public String getSellAfDisc() {
		return sellAfDisc;
	}
	public void setSellAfDisc(String sellAfDisc) {
		this.sellAfDisc = sellAfDisc;
	}
	public String getWacoalAmt() {
		return wacoalAmt;
	}
	public void setWacoalAmt(String wacoalAmt) {
		this.wacoalAmt = wacoalAmt;
	}
	public String getPensAmt() {
		return pensAmt;
	}
	public void setPensAmt(String pensAmt) {
		this.pensAmt = pensAmt;
	}
	public List<ShopBean> getItemsDeleteList() {
		return itemsDeleteList;
	}
	public void setItemsDeleteList(List<ShopBean> itemsDeleteList) {
		this.itemsDeleteList = itemsDeleteList;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	
	public long getPromoId() {
		return promoId;
	}
	public void setPromoId(long promoId) {
		this.promoId = promoId;
	}
	public long getSubPromoId() {
		return subPromoId;
	}
	public void setSubPromoId(long subPromoId) {
		this.subPromoId = subPromoId;
	}
	public String getPromoName() {
		return promoName;
	}
	public void setPromoName(String promoName) {
		this.promoName = promoName;
	}
	public String getSubPromoName() {
		return subPromoName;
	}
	public void setSubPromoName(String subPromoName) {
		this.subPromoName = subPromoName;
	}
	public String getStartPromtionQty() {
		return startPromtionQty;
	}
	public void setStartPromtionQty(String startPromtionQty) {
		this.startPromtionQty = startPromtionQty;
	}
	public String getEndPromtionQty() {
		return endPromtionQty;
	}
	public void setEndPromtionQty(String endPromtionQty) {
		this.endPromtionQty = endPromtionQty;
	}
	
	public String getDiscountPercent() {
		return discountPercent;
	}
	public void setDiscountPercent(String discountPercent) {
		this.discountPercent = discountPercent;
	}
	public String getInitSaleQty() {
		return initSaleQty;
	}
	public void setInitSaleQty(String initSaleQty) {
		this.initSaleQty = initSaleQty;
	}
	public String getTransInQty() {
		return transInQty;
	}
	public void setTransInQty(String transInQty) {
		this.transInQty = transInQty;
	}
	public String getSaleOutQty() {
		return saleOutQty;
	}
	public void setSaleOutQty(String saleOutQty) {
		this.saleOutQty = saleOutQty;
	}
	public String getSaleReturnQty() {
		return saleReturnQty;
	}
	public void setSaleReturnQty(String saleReturnQty) {
		this.saleReturnQty = saleReturnQty;
	}
	public String getAdjustQty() {
		return adjustQty;
	}
	public void setAdjustQty(String adjustQty) {
		this.adjustQty = adjustQty;
	}
	public String getOnhandQty() {
		return onhandQty;
	}
	public void setOnhandQty(String onhandQty) {
		this.onhandQty = onhandQty;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getCustGroup() {
		return custGroup;
	}
	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
	}
	public String getAsOfDate() {
		return asOfDate;
	}
	public void setAsOfDate(String asOfDate) {
		this.asOfDate = asOfDate;
	}
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
	public List<ShopBean> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<ShopBean> itemsList) {
		this.itemsList = itemsList;
	}
	public ShopBean getSummary() {
		return summary;
	}
	public void setSummary(ShopBean summary) {
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
