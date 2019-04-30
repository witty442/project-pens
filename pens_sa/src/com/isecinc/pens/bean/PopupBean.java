package com.isecinc.pens.bean;

import java.io.Serializable;

public class PopupBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7735372135088895737L;
	private String keyName;
	private String value;
	
	private String codeSearch;
	private String descSearch;
	private String brandId;
	private String brandName;
	
	private String salesChannelNo;
	private String salesChannelDesc;
	private String custCatNo;
	private String custCatDesc;
	private int no;
	private String customerCode;
	private String customerName;
	private String reportType;
	private String reportValue;
	private String salesrepCode;
	private String salesrepId;
	private String status;
	
	private String province;
	private String provinceName;
	private String district;
	private String districtName;
	
	private String salesZone;
	private String salesZoneDesc;
	
	
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
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public PopupBean(){
		
	}
	public PopupBean(String type ,String desc,String value){
		if("reportType".equalsIgnoreCase(type)){
		  setReportType(desc);
		  setReportValue(value);
		}else if("status".equalsIgnoreCase(type)){
			setStatus(value);
		}
	}
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSalesrepCode() {
		return salesrepCode;
	}
	public void setSalesrepCode(String salesrepCode) {
		this.salesrepCode = salesrepCode;
	}
	public String getSalesrepId() {
		return salesrepId;
	}
	public void setSalesrepId(String salesrepId) {
		this.salesrepId = salesrepId;
	}
	public String getReportValue() {
		return reportValue;
	}

	public void setReportValue(String reportValue) {
		this.reportValue = reportValue;
	}

	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getCustCatDesc() {
		return custCatDesc;
	}
	public void setCustCatDesc(String custCatDesc) {
		this.custCatDesc = custCatDesc;
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
	public String getCustCatNo() {
		return custCatNo;
	}
	public void setCustCatNo(String custCatNo) {
		this.custCatNo = custCatNo;
	}
	
	public String getSalesChannelNo() {
		return salesChannelNo;
	}
	public void setSalesChannelNo(String salesChannelNo) {
		this.salesChannelNo = salesChannelNo;
	}
	public String getSalesChannelDesc() {
		return salesChannelDesc;
	}
	public void setSalesChannelDesc(String salesChannelDesc) {
		this.salesChannelDesc = salesChannelDesc;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getBrandId() {
		return brandId;
	}
	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getCodeSearch() {
		return codeSearch;
	}
	public void setCodeSearch(String codeSearch) {
		this.codeSearch = codeSearch;
	}
	public String getDescSearch() {
		return descSearch;
	}
	public void setDescSearch(String descSearch) {
		this.descSearch = descSearch;
	}
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	

}
