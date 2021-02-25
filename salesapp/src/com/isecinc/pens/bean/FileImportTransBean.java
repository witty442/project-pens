package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class FileImportTransBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6472434905791868622L;
    private String fileName;
    private String fileSize;
    private int fileCount;
    private List<KeyNoImportTransBean> keyNoImportTransList;
    
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public int getFileCount() {
		return fileCount;
	}
	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}
	public List<KeyNoImportTransBean> getKeyNoImportTransList() {
		return keyNoImportTransList;
	}
	public void setKeyNoImportTransList(
			List<KeyNoImportTransBean> keyNoImportTransList) {
		this.keyNoImportTransList = keyNoImportTransList;
	}
	
}
