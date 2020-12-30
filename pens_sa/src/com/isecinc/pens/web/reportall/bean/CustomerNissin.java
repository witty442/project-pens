package com.isecinc.pens.web.reportall.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import com.isecinc.core.model.I_PO;


/**
 * CustomerNissin
 * 
 * @author Wittaya
 * @version 1.0
 * 
 *          atiz.b : code prefix transient
 * 
 */
public class CustomerNissin {

	private static final long serialVersionUID = -8675517484785667619L;


	/**
	 * Default Constructor
	 */
	public CustomerNissin() {

	}


	/** ID */
	private int no;
	private long id;
	private String name;
	private String customerType;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private int orderAmount = 0;
	private String interfaces;
	private String exported;
	private String provinceId;
	private String districtId;
	private String provinceName;
	private String districtName;
	private String mobile;
    private String userName;
    private String addressSummary;
    private boolean canEdit;
    private String orderId;
   
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	public String getAddressSummary() {
		return addressSummary;
	}

	public void setAddressSummary(String addressSummary) {
		this.addressSummary = addressSummary;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(int orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}

	public String getExported() {
		return exported;
	}

	public void setExported(String exported) {
		this.exported = exported;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getDistrictId() {
		return districtId;
	}

	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

}
