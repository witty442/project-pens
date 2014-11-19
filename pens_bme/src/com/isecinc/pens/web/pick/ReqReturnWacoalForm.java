package com.isecinc.pens.web.pick;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.AdjustStock;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.ReqReturnWacoal;

public class ReqReturnWacoalForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<ReqReturnWacoal> results = new ArrayList<ReqReturnWacoal>();
    private List<ReqReturnWacoal> resultsSearch = new ArrayList<ReqReturnWacoal>();

    private String mode;
    private ReqReturnWacoal bean ;
    private ReqReturnWacoal beanCriteria ;
    
    
	public List<ReqReturnWacoal> getResults() {
		return results;
	}
	public void setResults(List<ReqReturnWacoal> results) {
		this.results = results;
	}
	public List<ReqReturnWacoal> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<ReqReturnWacoal> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public ReqReturnWacoal getBean() {
		return bean;
	}
	public void setBean(ReqReturnWacoal bean) {
		this.bean = bean;
	}
	public ReqReturnWacoal getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(ReqReturnWacoal beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
    
}
