package com.isecinc.pens.web.pick;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.ReqFinish;

public class ReqFinishForm extends I_Form {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ReqFinish> results = new ArrayList<ReqFinish>();
    private List<ReqFinish> resultsSearch = new ArrayList<ReqFinish>();

    private String mode;
    private ReqFinish bean ;
    private ReqFinish beanCriteria ;
    
    
	public List<ReqFinish> getResults() {
		return results;
	}
	public void setResults(List<ReqFinish> results) {
		this.results = results;
	}
	public List<ReqFinish> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<ReqFinish> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public ReqFinish getBean() {
		return bean;
	}
	public void setBean(ReqFinish bean) {
		this.bean = bean;
	}
	public ReqFinish getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(ReqFinish beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
    
}
