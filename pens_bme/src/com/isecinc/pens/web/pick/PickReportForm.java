package com.isecinc.pens.web.pick;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.AdjustStock;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.MTTBean;
import com.isecinc.pens.bean.PickReportBean;

public class PickReportForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<PickReportBean> results = new ArrayList<PickReportBean>();
    private List<PickReportBean> resultsSearch = new ArrayList<PickReportBean>();

    private String mode;
    private PickReportBean bean ;
    private PickReportBean beanCriteria ;
    private PickReportBean summary ;
    private String issueReqNoAll;
    
    
    
	public String getIssueReqNoAll() {
		return issueReqNoAll;
	}
	public void setIssueReqNoAll(String issueReqNoAll) {
		this.issueReqNoAll = issueReqNoAll;
	}
	public PickReportBean getSummary() {
		return summary;
	}
	public void setSummary(PickReportBean summary) {
		this.summary = summary;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public List<PickReportBean> getResults() {
		return results;
	}
	public void setResults(List<PickReportBean> results) {
		this.results = results;
	}
	public List<PickReportBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<PickReportBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public PickReportBean getBean() {
		return bean;
	}
	public void setBean(PickReportBean bean) {
		this.bean = bean;
	}
	public PickReportBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(PickReportBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	
    
   
}
