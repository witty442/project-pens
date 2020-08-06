package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

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
	private String salesrepName;
	private String status;
	
	private String province;
	private String provinceName;
	private String district;
	private String districtName;
	
	private String salesZone;
	private String salesZoneDesc;
	
	private String pdCode;
	private String pdDesc;

	private String storeType;
	private String code;
	private String desc;
	private String desc2;
	private String desc3;
	private String price;
	private String uom1;
	private String uom2;
	//search3
	private String storeCode;
	private String storeName;
	private String storeNo;
	private String subInv;
	
	private String customerId;
	private String subBrand;
	private String subBrandDesc;
	private String productCode;
	private String productName;
	private String productId;
	private String page;
	private String userId;
	private String stringDate;
	private String address;
	private String requestNumber;
	private String priAllQty;
	private String priQty;
	private String subQty;
	private String remainAmount;
	
	List<PopupBean> dataList;
	List<PopupBean> data2List;
	
	
	public String getStoreType() {
		return storeType;
	}
	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getDesc2() {
		return desc2;
	}
	public void setDesc2(String desc2) {
		this.desc2 = desc2;
	}
	public String getDesc3() {
		return desc3;
	}
	public void setDesc3(String desc3) {
		this.desc3 = desc3;
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
	public String getStoreNo() {
		return storeNo;
	}
	public void setStoreNo(String storeNo) {
		this.storeNo = storeNo;
	}
	public String getSubInv() {
		return subInv;
	}
	public void setSubInv(String subInv) {
		this.subInv = subInv;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getSubBrand() {
		return subBrand;
	}
	public void setSubBrand(String subBrand) {
		this.subBrand = subBrand;
	}
	public String getSubBrandDesc() {
		return subBrandDesc;
	}
	public void setSubBrandDesc(String subBrandDesc) {
		this.subBrandDesc = subBrandDesc;
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
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getStringDate() {
		return stringDate;
	}
	public void setStringDate(String stringDate) {
		this.stringDate = stringDate;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRequestNumber() {
		return requestNumber;
	}
	public void setRequestNumber(String requestNumber) {
		this.requestNumber = requestNumber;
	}
	public String getPriAllQty() {
		return priAllQty;
	}
	public void setPriAllQty(String priAllQty) {
		this.priAllQty = priAllQty;
	}
	public String getPriQty() {
		return priQty;
	}
	public void setPriQty(String priQty) {
		this.priQty = priQty;
	}
	public String getSubQty() {
		return subQty;
	}
	public void setSubQty(String subQty) {
		this.subQty = subQty;
	}
	public String getRemainAmount() {
		return remainAmount;
	}
	public void setRemainAmount(String remainAmount) {
		this.remainAmount = remainAmount;
	}
	public List<PopupBean> getDataList() {
		return dataList;
	}
	public void setDataList(List<PopupBean> dataList) {
		this.dataList = dataList;
	}
	public List<PopupBean> getData2List() {
		return data2List;
	}
	public void setData2List(List<PopupBean> data2List) {
		this.data2List = data2List;
	}
	public String getSalesrepName() {
		return salesrepName;
	}
	public void setSalesrepName(String salesrepName) {
		this.salesrepName = salesrepName;
	}
	public String getPdCode() {
		return pdCode;
	}
	public void setPdCode(String pdCode) {
		this.pdCode = pdCode;
	}
	public String getPdDesc() {
		return pdDesc;
	}
	public void setPdDesc(String pdDesc) {
		this.pdDesc = pdDesc;
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
	public PopupBean(String keyName,String value){
	   setKeyName(keyName);
	   setValue(value);
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
