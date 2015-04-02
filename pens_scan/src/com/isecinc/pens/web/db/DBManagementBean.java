/**
 * 
 */
package com.isecinc.pens.web.db;

/**
 * @author WITTY
 *
 */
public class DBManagementBean {
    
	private int addressId;
	private String provinceName;
	private int provinceId;
	private String districtName;
	private int districtId;
	
	private String provinceStyle;
	private String districtStyle;
	
	private String masterProvinceName;
	private int masterProvinceId;
	private String masterDistrictName;
	private int masterDistrictId;
	
	
	
	public String getProvinceStyle() {
		return provinceStyle;
	}
	public void setProvinceStyle(String provinceStyle) {
		this.provinceStyle = provinceStyle;
	}
	public String getDistrictStyle() {
		return districtStyle;
	}
	public void setDistrictStyle(String districtStyle) {
		this.districtStyle = districtStyle;
	}
	public int getAddressId() {
		return addressId;
	}
	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public int getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public int getDistrictId() {
		return districtId;
	}
	public void setDistrictId(int districtId) {
		this.districtId = districtId;
	}
	public String getMasterProvinceName() {
		return masterProvinceName;
	}
	public void setMasterProvinceName(String masterProvinceName) {
		this.masterProvinceName = masterProvinceName;
	}
	public int getMasterProvinceId() {
		return masterProvinceId;
	}
	public void setMasterProvinceId(int masterProvinceId) {
		this.masterProvinceId = masterProvinceId;
	}
	public String getMasterDistrictName() {
		return masterDistrictName;
	}
	public void setMasterDistrictName(String masterDistrictName) {
		this.masterDistrictName = masterDistrictName;
	}
	public int getMasterDistrictId() {
		return masterDistrictId;
	}
	public void setMasterDistrictId(int masterDistrictId) {
		this.masterDistrictId = masterDistrictId;
	}
	
	
	
}
