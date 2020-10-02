package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class ConfPickingBean implements Serializable ,Comparable<ConfPickingBean> {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -5066534727878051512L;
	private String transactionDate;
	private String transactionDateFrom;
	private String transactionDateTo;
	private String regionCri;
	private String provinceCri;
	private String amphurCri;
	private String pickingNo;
	private String status;
	private String orderDateFrom;
	private String orderDateTo;
	private String orderNo;
	private String orderDate;
	private String customerCode;
	private String customerName;
	private String amphur;
	private String province;
	private String salesrepCode;
	private String salesrepName;
	private String productCode;
	private String productName;
	private String uom;
	private String qty;
	private String qty1;
	private String qty2;
	private String totalAmount;
	private String vatAmount;
	private String netAmount;
	private double totalAmountD;
	private double vatAmountD;
	private double netAmountD;
	private String invoiceNo;
	private String invoiceFlag;
	private String invoiceDate;
	private String invoiceType;
	private String invoiceRef;
	private String addressShipTo;
	private String addressBillTo;
	private String mobile;
	private StringBuffer dataStrBuffer;
	private StringBuffer rowTotalStrBuffer;
	private String selectedOrderNo;
	private List<ConfPickingBean> itemsList;
	private String userName;
	private String poNumber;
	private String alternateName;
	private boolean canFinish;
	private boolean canConfirm;
	private boolean canReject;
	private boolean canPrintPicking;
	private boolean canPrintLoading;
	private boolean canAddOrderManual;
	
	//for report
	private int qtyInt;
	private String subBrand;
	private String subBrandName;
	private String uom1;
	private String uom2;
	private String uom2Contain;
	private double subQty;
	private double priQty;
	private String summaryType;
	private String unitStandardPrice;
	private String unitSellingPrice;
	
	
	public String getInvoiceFlag() {
		return invoiceFlag;
	}
	public void setInvoiceFlag(String invoiceFlag) {
		this.invoiceFlag = invoiceFlag;
	}
	public String getTransactionDateFrom() {
		return transactionDateFrom;
	}
	public void setTransactionDateFrom(String transactionDateFrom) {
		this.transactionDateFrom = transactionDateFrom;
	}
	public String getTransactionDateTo() {
		return transactionDateTo;
	}
	public void setTransactionDateTo(String transactionDateTo) {
		this.transactionDateTo = transactionDateTo;
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
	public String getUnitStandardPrice() {
		return unitStandardPrice;
	}
	public void setUnitStandardPrice(String unitStandardPrice) {
		this.unitStandardPrice = unitStandardPrice;
	}
	public String getUnitSellingPrice() {
		return unitSellingPrice;
	}
	public void setUnitSellingPrice(String unitSellingPrice) {
		this.unitSellingPrice = unitSellingPrice;
	}
	public String getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}
	public String getInvoiceRef() {
		return invoiceRef;
	}
	public void setInvoiceRef(String invoiceRef) {
		this.invoiceRef = invoiceRef;
	}
	public String getAddressShipTo() {
		return addressShipTo;
	}
	public void setAddressShipTo(String addressShipTo) {
		this.addressShipTo = addressShipTo;
	}
	public String getAddressBillTo() {
		return addressBillTo;
	}
	public void setAddressBillTo(String addressBillTo) {
		this.addressBillTo = addressBillTo;
	}
	public String getSummaryType() {
		return summaryType;
	}
	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}
	public String getAlternateName() {
		return alternateName;
	}
	public void setAlternateName(String alternateName) {
		this.alternateName = alternateName;
	}
	public String getPoNumber() {
		return poNumber;
	}
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}
	public double getTotalAmountD() {
		return totalAmountD;
	}
	public void setTotalAmountD(double totalAmountD) {
		this.totalAmountD = totalAmountD;
	}
	public double getVatAmountD() {
		return vatAmountD;
	}
	public void setVatAmountD(double vatAmountD) {
		this.vatAmountD = vatAmountD;
	}
	public double getNetAmountD() {
		return netAmountD;
	}
	public void setNetAmountD(double netAmountD) {
		this.netAmountD = netAmountD;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public boolean isCanAddOrderManual() {
		return canAddOrderManual;
	}
	public void setCanAddOrderManual(boolean canAddOrderManual) {
		this.canAddOrderManual = canAddOrderManual;
	}
	public String getSalesrepName() {
		return salesrepName;
	}
	public void setSalesrepName(String salesrepName) {
		this.salesrepName = salesrepName;
	}
	public StringBuffer getRowTotalStrBuffer() {
		return rowTotalStrBuffer;
	}
	public void setRowTotalStrBuffer(StringBuffer rowTotalStrBuffer) {
		this.rowTotalStrBuffer = rowTotalStrBuffer;
	}
	public String getSubBrand() {
		return subBrand;
	}
	public void setSubBrand(String subBrand) {
		this.subBrand = subBrand;
	}
	public String getSubBrandName() {
		return subBrandName;
	}
	public void setSubBrandName(String subBrandName) {
		this.subBrandName = subBrandName;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public int getQtyInt() {
		return qtyInt;
	}
	public void setQtyInt(int qtyInt) {
		this.qtyInt = qtyInt;
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
	public String getUom2Contain() {
		return uom2Contain;
	}
	public void setUom2Contain(String uom2Contain) {
		this.uom2Contain = uom2Contain;
	}
	public double getSubQty() {
		return subQty;
	}
	public void setSubQty(double subQty) {
		this.subQty = subQty;
	}
	public double getPriQty() {
		return priQty;
	}
	public void setPriQty(double priQty) {
		this.priQty = priQty;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRegionCri() {
		return regionCri;
	}
	public void setRegionCri(String regionCri) {
		this.regionCri = regionCri;
	}
	public String getProvinceCri() {
		return provinceCri;
	}
	public void setProvinceCri(String provinceCri) {
		this.provinceCri = provinceCri;
	}
	
	public boolean isCanPrintPicking() {
		return canPrintPicking;
	}
	public void setCanPrintPicking(boolean canPrintPicking) {
		this.canPrintPicking = canPrintPicking;
	}
	public boolean isCanPrintLoading() {
		return canPrintLoading;
	}
	public void setCanPrintLoading(boolean canPrintLoading) {
		this.canPrintLoading = canPrintLoading;
	}
	public boolean isCanReject() {
		return canReject;
	}
	public void setCanReject(boolean canReject) {
		this.canReject = canReject;
	}
	
	public boolean isCanFinish() {
		return canFinish;
	}
	public void setCanFinish(boolean canFinish) {
		this.canFinish = canFinish;
	}
	public boolean isCanConfirm() {
		return canConfirm;
	}
	public void setCanConfirm(boolean canConfirm) {
		this.canConfirm = canConfirm;
	}
	public List<ConfPickingBean> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<ConfPickingBean> itemsList) {
		this.itemsList = itemsList;
	}
	public String getOrderDateFrom() {
		return orderDateFrom;
	}
	public void setOrderDateFrom(String orderDateFrom) {
		this.orderDateFrom = orderDateFrom;
	}
	public String getOrderDateTo() {
		return orderDateTo;
	}
	public void setOrderDateTo(String orderDateTo) {
		this.orderDateTo = orderDateTo;
	}
	public String getSelectedOrderNo() {
		return selectedOrderNo;
	}
	public void setSelectedOrderNo(String selectedOrderNo) {
		this.selectedOrderNo = selectedOrderNo;
	}
	
	public String getSalesrepCode() {
		return salesrepCode;
	}
	public void setSalesrepCode(String salesrepCode) {
		this.salesrepCode = salesrepCode;
	}
	public String getAmphur() {
		return amphur;
	}
	public void setAmphur(String amphur) {
		this.amphur = amphur;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
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
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getVatAmount() {
		return vatAmount;
	}
	public void setVatAmount(String vatAmount) {
		this.vatAmount = vatAmount;
	}
	public String getAmphurCri() {
		return amphurCri;
	}
	public void setAmphurCri(String amphurCri) {
		this.amphurCri = amphurCri;
	}
	
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
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
	public StringBuffer getDataStrBuffer() {
		return dataStrBuffer;
	}
	public void setDataStrBuffer(StringBuffer dataStrBuffer) {
		this.dataStrBuffer = dataStrBuffer;
	}
	
	public String getPickingNo() {
		return pickingNo;
	}
	public void setPickingNo(String pickingNo) {
		this.pickingNo = pickingNo;
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
	public String getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(String netAmount) {
		this.netAmount = netAmount;
	}
	 @Override
	 public int compareTo(ConfPickingBean o) {
	     return Comparators.PRODUCT_CODE_ASC.compare(this, o);
	  }
	 
	  public static class Comparators {
		  public static Comparator<ConfPickingBean> PRODUCT_CODE_ASC = new Comparator<ConfPickingBean>() {
	            @Override
	            public int compare(ConfPickingBean o1, ConfPickingBean o2) {
	            	 return o1.getProductCode().compareTo(o2.getProductCode());
	            }
	        };
	        public static Comparator<ConfPickingBean> PRODUCT_CODE_DESC = new Comparator<ConfPickingBean>() {
	            @Override
	            public int compare(ConfPickingBean o1, ConfPickingBean o2) {
	            	 return o2.getProductCode().compareTo(o1.getProductCode());
	            }
	        };
	  }
	
}
