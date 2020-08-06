package com.isecinc.pens.bean;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


public class ProdShowBean implements Serializable{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 210633554609248543L;
	/** Criteria **/
	private String docDate;
	private String orderNo;
	private long orderId;
	private String customerCode;
	private String customerName;
	private String remark;
	private String export;
	private int id;
	private String brand;
	private String brandName;
	
	
	private List<ProdShowBean> items;
	private String createdBy;
	private String updatedBy;
	//control
	private String mode;
    private boolean canSave;
    
    private String inputFileNamePic1;
	private String inputFileNamePic2;
	private String inputFileNamePic3;
    private InputStream inputFileStreamPic1;
    private InputStream inputFileStreamPic2;
    private InputStream inputFileStreamPic3;

    private String inputFileNameDBPic1;
	private String inputFileNameDBPic2;
	private String inputFileNameDBPic3;

	private String statusPic1;
	private String statusPic2;
	private String statusPic3;
	private String statusRow;
	
	
	public String getStatusRow() {
		return statusRow;
	}

	public void setStatusRow(String statusRow) {
		this.statusRow = statusRow;
	}

	public String getStatusPic1() {
		return statusPic1;
	}

	public void setStatusPic1(String statusPic1) {
		this.statusPic1 = statusPic1;
	}

	public String getStatusPic2() {
		return statusPic2;
	}

	public void setStatusPic2(String statusPic2) {
		this.statusPic2 = statusPic2;
	}

	public String getStatusPic3() {
		return statusPic3;
	}

	public void setStatusPic3(String statusPic3) {
		this.statusPic3 = statusPic3;
	}

	public InputStream getInputFileStreamPic1() {
		return inputFileStreamPic1;
	}

	public void setInputFileStreamPic1(InputStream inputFileStreamPic1) {
		this.inputFileStreamPic1 = inputFileStreamPic1;
	}

	public InputStream getInputFileStreamPic2() {
		return inputFileStreamPic2;
	}

	public void setInputFileStreamPic2(InputStream inputFileStreamPic2) {
		this.inputFileStreamPic2 = inputFileStreamPic2;
	}

	public InputStream getInputFileStreamPic3() {
		return inputFileStreamPic3;
	}

	public void setInputFileStreamPic3(InputStream inputFileStreamPic3) {
		this.inputFileStreamPic3 = inputFileStreamPic3;
	}

	public String getInputFileNameDBPic1() {
		return inputFileNameDBPic1;
	}

	public void setInputFileNameDBPic1(String inputFileNameDBPic1) {
		this.inputFileNameDBPic1 = inputFileNameDBPic1;
	}

	public String getInputFileNameDBPic2() {
		return inputFileNameDBPic2;
	}

	public void setInputFileNameDBPic2(String inputFileNameDBPic2) {
		this.inputFileNameDBPic2 = inputFileNameDBPic2;
	}

	public String getInputFileNameDBPic3() {
		return inputFileNameDBPic3;
	}

	public void setInputFileNameDBPic3(String inputFileNameDBPic3) {
		this.inputFileNameDBPic3 = inputFileNameDBPic3;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getExport() {
		return export;
	}

	public void setExport(String export) {
		this.export = export;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}


	public String getInputFileNamePic1() {
		return inputFileNamePic1;
	}

	public void setInputFileNamePic1(String inputFileNamePic1) {
		this.inputFileNamePic1 = inputFileNamePic1;
	}

	public String getInputFileNamePic2() {
		return inputFileNamePic2;
	}

	public void setInputFileNamePic2(String inputFileNamePic2) {
		this.inputFileNamePic2 = inputFileNamePic2;
	}

	public String getInputFileNamePic3() {
		return inputFileNamePic3;
	}

	public void setInputFileNamePic3(String inputFileNamePic3) {
		this.inputFileNamePic3 = inputFileNamePic3;
	}

	public List<ProdShowBean> getItems() {
		return items;
	}

	public void setItems(List<ProdShowBean> items) {
		this.items = items;
	}

	public boolean isCanSave() {
		return canSave;
	}

	public void setCanSave(boolean canSave) {
		this.canSave = canSave;
	}

	public String getDocDate() {
		return docDate;
	}

	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	} 


    
}
