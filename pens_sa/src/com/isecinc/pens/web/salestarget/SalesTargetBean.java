package com.isecinc.pens.web.salestarget;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.isecinc.pens.bean.User;

public class SalesTargetBean implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4104083172800233911L;
	private long id;
	private long rowId;
    private String priceListId;
	private String targetMonth;
	private String targetYear;
	private String targetQuarter;
	private String startDate;
	private String endDate;
	private String brand;
	private String brandGroup;
	private String brandName;
	private String salesChannelNo;
	private String salesChannelName;
	private String salesZone;
	private String salesZoneDesc;
	private String division;
	private String custCatNo;
	private String custCatDesc;
	private String customerId;
	private String customerGroup;
	private String customerCode;
	private String customerName;
	private String createUser;
	private String updateUser;
	private List<SalesTargetBean> items;
	
	private long lineId;
	private String lineReadonly;
	private String lineStyle ="normalText";
	private String lineNumberStyle ="enableNumber";
	private String lineRejectReasonStyle ="disableText";
	private String lineStatusStyle ="disableTextCenter";
	private String salesrepId;
	private String salesrepCode;
	private String salesrepName;
	private String orderAmt12Month;
	private String orderAmt3Month ;
	private String itemCode;
	private String itemName;
	private String itemId;
	private String price;
	private String targetQty;
	private String targetAmount;
	private String salesTargetQty;
	private String salesTargetAmount;
	private String status;
	private String remark;
	private String rejectReason;
	private String totalAmountBrandBySale;

	private String invoicedQty;
	private String invoicedAmt;
	private String estimateQty;
	private String estimateAmt;
	
	//total
	private String totalTargetQty;
	private String totalTargetAmount;
	private String totalSalesTargetQty;
	private String totalSalesTargetAmount;
	private String totalOrderAmt12Month;
	private String totalOrderAmt3Month ;
	
	//Control Access Button
	private boolean canSet= false;
	private boolean canPost = false;
	private boolean canReject = false;
	private boolean canAccept = false;
	private boolean canUnAccept = false;
	private boolean canExport = false;
	private boolean canFinish = false;
	private boolean canCopy = false;
	//optional
	private String period;
	private String periodDesc;
	private String reportType;
	private String dispType;
	
	private List<SalesTargetBean> itemsList;
	
    
	
	public String getInvoicedAmt() {
		return invoicedAmt;
	}
	public void setInvoicedAmt(String invoicedAmt) {
		this.invoicedAmt = invoicedAmt;
	}
	
	public String getInvoicedQty() {
		return invoicedQty;
	}
	public void setInvoicedQty(String invoicedQty) {
		this.invoicedQty = invoicedQty;
	}
	public String getEstimateQty() {
		return estimateQty;
	}
	public void setEstimateQty(String estimateQty) {
		this.estimateQty = estimateQty;
	}
	public String getEstimateAmt() {
		return estimateAmt;
	}
	public void setEstimateAmt(String estimateAmt) {
		this.estimateAmt = estimateAmt;
	}
	public boolean isCanCopy() {
		return canCopy;
	}
	public void setCanCopy(boolean canCopy) {
		this.canCopy = canCopy;
	}
	public String getTotalAmountBrandBySale() {
		return totalAmountBrandBySale;
	}
	public void setTotalAmountBrandBySale(String totalAmountBrandBySale) {
		this.totalAmountBrandBySale = totalAmountBrandBySale;
	}
	public String getTotalSalesTargetQty() {
		return totalSalesTargetQty;
	}
	public void setTotalSalesTargetQty(String totalSalesTargetQty) {
		this.totalSalesTargetQty = totalSalesTargetQty;
	}
	public String getTotalSalesTargetAmount() {
		return totalSalesTargetAmount;
	}
	public void setTotalSalesTargetAmount(String totalSalesTargetAmount) {
		this.totalSalesTargetAmount = totalSalesTargetAmount;
	}
	public String getSalesTargetQty() {
		return salesTargetQty;
	}
	public void setSalesTargetQty(String salesTargetQty) {
		this.salesTargetQty = salesTargetQty;
	}
	public String getSalesTargetAmount() {
		return salesTargetAmount;
	}
	public void setSalesTargetAmount(String salesTargetAmount) {
		this.salesTargetAmount = salesTargetAmount;
	}
	public String getSalesrepName() {
		return salesrepName;
	}
	public void setSalesrepName(String salesrepName) {
		this.salesrepName = salesrepName;
	}
	public String getCustCatDesc() {
		return custCatDesc;
	}
	public void setCustCatDesc(String custCatDesc) {
		this.custCatDesc = custCatDesc;
	}
	public String getSalesZone() {
		return salesZone;
	}
	public void setSalesZone(String salesZone) {
		this.salesZone = salesZone;
	}
	public String getSalesZoneDesc() {
		return salesZoneDesc;
	}
	public void setSalesZoneDesc(String salesZoneDesc) {
		this.salesZoneDesc = salesZoneDesc;
	}
	public String getTotalOrderAmt12Month() {
		return totalOrderAmt12Month;
	}
	public void setTotalOrderAmt12Month(String totalOrderAmt12Month) {
		this.totalOrderAmt12Month = totalOrderAmt12Month;
	}
	public String getTotalOrderAmt3Month() {
		return totalOrderAmt3Month;
	}
	public void setTotalOrderAmt3Month(String totalOrderAmt3Month) {
		this.totalOrderAmt3Month = totalOrderAmt3Month;
	}
	public String getLineStatusStyle() {
		return lineStatusStyle;
	}
	public void setLineStatusStyle(String lineStatusStyle) {
		this.lineStatusStyle = lineStatusStyle;
	}
	public List<SalesTargetBean> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<SalesTargetBean> itemsList) {
		this.itemsList = itemsList;
	}
	public String getDispType() {
		return dispType;
	}
	public void setDispType(String dispType) {
		this.dispType = dispType;
	}
	public String getLineRejectReasonStyle() {
		return lineRejectReasonStyle;
	}
	public void setLineRejectReasonStyle(String lineRejectReasonStyle) {
		this.lineRejectReasonStyle = lineRejectReasonStyle;
	}
	public String getCustomerGroup() {
		return customerGroup;
	}
	public void setCustomerGroup(String customerGroup) {
		this.customerGroup = customerGroup;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public boolean isCanFinish() {
		return canFinish;
	}
	public void setCanFinish(boolean canFinish) {
		this.canFinish = canFinish;
	}
	public long getRowId() {
		return rowId;
	}
	public void setRowId(long rowId) {
		this.rowId = rowId;
	}
	public String getLineNumberStyle() {
		return lineNumberStyle;
	}
	public void setLineNumberStyle(String lineNumberStyle) {
		this.lineNumberStyle = lineNumberStyle;
	}
	public boolean isCanExport() {
		return canExport;
	}
	public void setCanExport(boolean canExport) {
		this.canExport = canExport;
	}
	public String getBrandGroup() {
		return brandGroup;
	}
	public void setBrandGroup(String brandGroup) {
		this.brandGroup = brandGroup;
	}
	public String getPriceListId() {
		return priceListId;
	}
	public void setPriceListId(String priceListId) {
		this.priceListId = priceListId;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getCustCatNo() {
		return custCatNo;
	}
	public void setCustCatNo(String custCatNo) {
		this.custCatNo = custCatNo;
	}
	public String getSalesChannelName() {
		return salesChannelName;
	}
	public void setSalesChannelName(String salesChannelName) {
		this.salesChannelName = salesChannelName;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getLineStyle() {
		return lineStyle;
	}
	public void setLineStyle(String lineStyle) {
		this.lineStyle = lineStyle;
	}
	
	public String getPeriodDesc() {
		return periodDesc;
	}
	public void setPeriodDesc(String periodDesc) {
		this.periodDesc = periodDesc;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getSalesChannelNo() {
		return salesChannelNo;
	}
	public void setSalesChannelNo(String salesChannelNo) {
		this.salesChannelNo = salesChannelNo;
	}
	
	public String getTotalTargetQty() {
		return totalTargetQty;
	}
	public void setTotalTargetQty(String totalTargetQty) {
		this.totalTargetQty = totalTargetQty;
	}
	public String getTotalTargetAmount() {
		return totalTargetAmount;
	}
	public void setTotalTargetAmount(String totalTargetAmount) {
		this.totalTargetAmount = totalTargetAmount;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getTargetQty() {
		return targetQty;
	}
	public void setTargetQty(String targetQty) {
		this.targetQty = targetQty;
	}
	public String getTargetAmount() {
		return targetAmount;
	}
	public void setTargetAmount(String targetAmount) {
		this.targetAmount = targetAmount;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public String getTargetMonth() {
		return targetMonth;
	}
	public void setTargetMonth(String targetMonth) {
		this.targetMonth = targetMonth;
	}
	public String getTargetYear() {
		return targetYear;
	}
	public void setTargetYear(String targetYear) {
		this.targetYear = targetYear;
	}
	public String getTargetQuarter() {
		return targetQuarter;
	}
	public void setTargetQuarter(String targetQuarter) {
		this.targetQuarter = targetQuarter;
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
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
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
	public List<SalesTargetBean> getItems() {
		return items;
	}
	public void setItems(List<SalesTargetBean> items) {
		this.items = items;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getLineId() {
		return lineId;
	}
	public void setLineId(long lineId) {
		this.lineId = lineId;
	}
	
	public String getLineReadonly() {
		return lineReadonly;
	}
	public void setLineReadonly(String lineReadonly) {
		this.lineReadonly = lineReadonly;
	}
	public String getSalesrepId() {
		return salesrepId;
	}
	public void setSalesrepId(String salesrepId) {
		this.salesrepId = salesrepId;
	}
	public String getSalesrepCode() {
		return salesrepCode;
	}
	public void setSalesrepCode(String salesrepCode) {
		this.salesrepCode = salesrepCode;
	}
	public String getOrderAmt12Month() {
		return orderAmt12Month;
	}
	public void setOrderAmt12Month(String orderAmt12Month) {
		this.orderAmt12Month = orderAmt12Month;
	}
	public String getOrderAmt3Month() {
		return orderAmt3Month;
	}
	public void setOrderAmt3Month(String orderAmt3Month) {
		this.orderAmt3Month = orderAmt3Month;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getRejectReason() {
		return rejectReason;
	}
	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}
	
	public boolean isCanSet() {
		return canSet;
	}
	public void setCanSet(boolean canSet) {
		this.canSet = canSet;
	}
	public boolean isCanPost() {
		return canPost;
	}
	public void setCanPost(boolean canPost) {
		this.canPost = canPost;
	}
	public boolean isCanReject() {
		return canReject;
	}
	public void setCanReject(boolean canReject) {
		this.canReject = canReject;
	}
	public boolean isCanAccept() {
		return canAccept;
	}
	public void setCanAccept(boolean canAccept) {
		this.canAccept = canAccept;
	}
	public boolean isCanUnAccept() {
		return canUnAccept;
	}
	public void setCanUnAccept(boolean canUnAccept) {
		this.canUnAccept = canUnAccept;
	}

	
	
	
}
