/**
 * 
 */
package com.isecinc.pens.bean;

import java.io.Serializable;

/**
 * @author WITTY
 *
 */
public class FTPFileBean implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6668376137092691185L;
	private String fileName;
    private String fileSize;
    private String[]  dataLineText;
    private int fileCount;
    private String dataResultStr;

	public String getDataResultStr() {
		return dataResultStr;
	}
	public void setDataResultStr(String dataResultStr) {
		this.dataResultStr = dataResultStr;
	}
	public int getFileCount() {
		return fileCount;
	}
	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String[] getDataLineText() {
		return dataLineText;
	}
	public void setDataLineText(String[] dataLineText) {
		this.dataLineText = dataLineText;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	
  
}
