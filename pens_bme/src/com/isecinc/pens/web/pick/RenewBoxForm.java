package com.isecinc.pens.web.pick;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.RenewBox;

public class RenewBoxForm extends I_Form {

    /**
	 * 
	 */
	private static final long serialVersionUID = -9037352286742645532L;
	private List<RenewBox> results = new ArrayList<RenewBox>();
    private List<RenewBox> resultsSearch = new ArrayList<RenewBox>();

    private String mode;
    private RenewBox bean ;
    private RenewBox beanCriteria ;
    
	public List<RenewBox> getResults() {
		return results;
	}
	public void setResults(List<RenewBox> results) {
		this.results = results;
	}
	public List<RenewBox> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<RenewBox> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public RenewBox getBean() {
		return bean;
	}
	public void setBean(RenewBox abean) {
		bean = abean;
	}
	public RenewBox getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(RenewBox jobCriteria) {
		beanCriteria = jobCriteria;
	}
    
   
}
