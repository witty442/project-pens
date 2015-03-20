package com.isecinc.pens.bean;

import java.util.List;

public class ResultBean {
	
 private List results;
 private boolean moreMaxRecord;
 
	public List getResults() {
		return results;
	}
	public void setResults(List results) {
		this.results = results;
	}
	public boolean isMoreMaxRecord() {
		return moreMaxRecord;
	}
	public void setMoreMaxRecord(boolean moreMaxRecord) {
		this.moreMaxRecord = moreMaxRecord;
	}
	
}
