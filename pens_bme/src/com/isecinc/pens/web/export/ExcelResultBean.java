package com.isecinc.pens.web.export;

import java.io.Serializable;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelResultBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2127864129550620658L;
	private XSSFWorkbook xssfWorkbook;
	private boolean found;
	
	public XSSFWorkbook getXssfWorkbook() {
		return xssfWorkbook;
	}
	public void setXssfWorkbook(XSSFWorkbook xssfWorkbook) {
		this.xssfWorkbook = xssfWorkbook;
	}
	public boolean isFound() {
		return found;
	}
	public void setFound(boolean found) {
		this.found = found;
	}
	
	

}
