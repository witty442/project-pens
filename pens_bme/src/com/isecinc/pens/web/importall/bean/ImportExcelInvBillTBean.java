package com.isecinc.pens.web.importall.bean;

import org.apache.struts.upload.FormFile;

public class ImportExcelInvBillTBean {
	 private String storeCode;
	 private String subInv;
	 private FormFile dataFile;
	 
	public String getStoreCode() {
		return storeCode;
	}
	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}
	public String getSubInv() {
		return subInv;
	}
	public void setSubInv(String subInv) {
		this.subInv = subInv;
	}
	public FormFile getDataFile() {
		return dataFile;
	}
	public void setDataFile(FormFile dataFile) {
		this.dataFile = dataFile;
	}
	 
	 
}
