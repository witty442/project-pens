package com.isecinc.pens.web.report.analyst.bean;

public class CriteriaBean {
  
	private String year;
	private String month;
	private String quarter;
	private String allCond;
	
	
	public String getAllCond() {
		return allCond;
	}
	public void setAllCond(String allCond) {
		this.allCond = allCond;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getQuarter() {
		return quarter;
	}
	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}
	
	
}
