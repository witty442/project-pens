package com.isecinc.pens.bean;

import java.io.Serializable;

import org.apache.struts.upload.FormFile;

public class InterfaceBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -492604978741933733L;
	
	private String productType;
	private String custGroup;
	private String custGroupDesc;
	private String textFileName;
	private String outputPath;
	private String transactionDate;
    private FormFile formDataFile;
	
    
	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public FormFile getFormDataFile() {
		return formDataFile;
	}

	public void setFormDataFile(FormFile formDataFile) {
		this.formDataFile = formDataFile;
	}
	
	public String getCustGroup() {
		return custGroup;
	}
	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
	}
	public String getCustGroupDesc() {
		return custGroupDesc;
	}
	public void setCustGroupDesc(String custGroupDesc) {
		this.custGroupDesc = custGroupDesc;
	}
	public String getTextFileName() {
		return textFileName;
	}
	public void setTextFileName(String textFileName) {
		this.textFileName = textFileName;
	}
	public String getOutputPath() {
		return outputPath;
	}
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	
	

}
