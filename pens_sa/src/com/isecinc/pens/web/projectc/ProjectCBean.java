package com.isecinc.pens.web.projectc;

import java.io.Serializable;
import java.util.List;

public class ProjectCBean implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 2913956892396709661L;
	private String id;
	private String storeCode;
	private String storeName;
	private String branchSize;
	private String address;
	private String amphor;
	private String province;
	private String storeLat;
	private String storeLong;
	private String checkDate;
	private String checkUser;
	private String chkLatitude;
	private String chkLongitude;
	private String branchId;
	private String branchName;
	private String remark;
	private String brand;
	private String brandName;
	
	private long lineId;
	private String productCode;
	private String productName;
	private String found;
	private String leg;
	private String lineRemark;
	private List<ProjectCBean> items;
	private ProjectCImageBean imageBean;
	
	private String userName;
	private String mode;
    private boolean foundCheck;
    private boolean canSave;
    
    //for report
    private String typeSearch;
    private String reportType;
    private String condType;
    private String periodDesc;
    private String period;
    private String startDate;
    private String endDate;
    
    private List<ProjectCBean> itemsList;
    private StringBuffer dataStrBuffer;
    
    
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
	public List<ProjectCBean> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<ProjectCBean> itemsList) {
		this.itemsList = itemsList;
	}
	public StringBuffer getDataStrBuffer() {
		return dataStrBuffer;
	}
	public void setDataStrBuffer(StringBuffer dataStrBuffer) {
		this.dataStrBuffer = dataStrBuffer;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getCondType() {
		return condType;
	}
	public void setCondType(String condType) {
		this.condType = condType;
	}
	public String getPeriodDesc() {
		return periodDesc;
	}
	public void setPeriodDesc(String periodDesc) {
		this.periodDesc = periodDesc;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
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
	
	public String getTypeSearch() {
		return typeSearch;
	}
	public void setTypeSearch(String typeSearch) {
		this.typeSearch = typeSearch;
	}
	public boolean isCanSave() {
		return canSave;
	}
	public void setCanSave(boolean canSave) {
		this.canSave = canSave;
	}
	public boolean isFoundCheck() {
		return foundCheck;
	}
	public void setFoundCheck(boolean foundCheck) {
		this.foundCheck = foundCheck;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLineRemark() {
		return lineRemark;
	}
	public void setLineRemark(String lineRemark) {
		this.lineRemark = lineRemark;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getChkLatitude() {
		return chkLatitude;
	}
	public void setChkLatitude(String chkLatitude) {
		this.chkLatitude = chkLatitude;
	}
	public String getChkLongitude() {
		return chkLongitude;
	}
	public void setChkLongitude(String chkLongitude) {
		this.chkLongitude = chkLongitude;
	}
	public String getCheckUser() {
		return checkUser;
	}
	public void setCheckUser(String checkUser) {
		this.checkUser = checkUser;
	}
	public String getStoreCode() {
		return storeCode;
	}
	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	
	public String getBranchSize() {
		return branchSize;
	}
	public void setBranchSize(String branchSize) {
		this.branchSize = branchSize;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAmphor() {
		return amphor;
	}
	public void setAmphor(String amphor) {
		this.amphor = amphor;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getStoreLat() {
		return storeLat;
	}
	public void setStoreLat(String storeLat) {
		this.storeLat = storeLat;
	}
	public String getStoreLong() {
		return storeLong;
	}
	public void setStoreLong(String storeLong) {
		this.storeLong = storeLong;
	}
	public String getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(String checkDate) {
		this.checkDate = checkDate;
	}
	
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public long getLineId() {
		return lineId;
	}
	public void setLineId(long lineId) {
		this.lineId = lineId;
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
	public String getFound() {
		return found;
	}
	public void setFound(String found) {
		this.found = found;
	}
	public String getLeg() {
		return leg;
	}
	public void setLeg(String leg) {
		this.leg = leg;
	}
	public List<ProjectCBean> getItems() {
		return items;
	}
	public void setItems(List<ProjectCBean> items) {
		this.items = items;
	}
	public ProjectCImageBean getImageBean() {
		return imageBean;
	}
	public void setImageBean(ProjectCImageBean imageBean) {
		this.imageBean = imageBean;
	}
	
	
}
