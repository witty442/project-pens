package com.isecinc.pens.inf.manager.process.bean;

import java.io.Serializable;
import java.util.List;

public class KeyNoImportTransBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6472434905791868622L;
	private String fileName;
    private String tableName;
    private String keyNo;

    private List<LineImportTransBean> lineList;
    
  
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
	public List<LineImportTransBean> getLineList() {
		return lineList;
	}
	public void setLineList(List<LineImportTransBean> lineList) {
		this.lineList = lineList;
	}
	
}
