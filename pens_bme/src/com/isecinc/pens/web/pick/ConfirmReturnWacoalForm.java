package com.isecinc.pens.web.pick;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.ConfirmReturnWacoal;

public class ConfirmReturnWacoalForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<ConfirmReturnWacoal> results = new ArrayList<ConfirmReturnWacoal>();
    private List<ConfirmReturnWacoal> resultsSearch = new ArrayList<ConfirmReturnWacoal>();

    private String mode;
    private ConfirmReturnWacoal bean ;
    private ConfirmReturnWacoal beanCriteria ;
    
    
	public List<ConfirmReturnWacoal> getResults() {
		return results;
	}
	public void setResults(List<ConfirmReturnWacoal> results) {
		this.results = results;
	}
	public List<ConfirmReturnWacoal> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<ConfirmReturnWacoal> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public ConfirmReturnWacoal getBean() {
		return bean;
	}
	public void setBean(ConfirmReturnWacoal bean) {
		this.bean = bean;
	}
	public ConfirmReturnWacoal getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(ConfirmReturnWacoal beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
    
}
