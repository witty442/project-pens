package com.isecinc.pens.bean;

import java.io.Serializable;

public class ExportFileBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6608433357207830273L;
	private int totalRecord;
	private StringBuffer dataExport;
	
	public int getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}
	public StringBuffer getDataExport() {
		return dataExport;
	}
	public void setDataExport(StringBuffer dataExport) {
		this.dataExport = dataExport;
	}

	
}
