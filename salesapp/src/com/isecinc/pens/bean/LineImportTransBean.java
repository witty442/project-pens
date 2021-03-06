package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class LineImportTransBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6472434905791868622L;
	private String fileName;
    private String tableName;
    private String keyNo;
    private String receiptNo;
    private String lineStr;
    private double seq;
    
    
	public String getReceiptNo() {
		return receiptNo;
	}
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	public double getSeq() {
		return seq;
	}
	public void setSeq(double seq) {
		this.seq = seq;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getKeyNo() {
		return keyNo;
	}
	public void setKeyNo(String keyNo) {
		this.keyNo = keyNo;
	}
	public String getLineStr() {
		return lineStr;
	}
	public void setLineStr(String lineStr) {
		this.lineStr = lineStr;
	}
	
}
