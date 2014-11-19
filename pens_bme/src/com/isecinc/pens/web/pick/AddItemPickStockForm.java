package com.isecinc.pens.web.pick;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.PickStock;
import com.isecinc.pens.bean.ReqPickStock;

public class AddItemPickStockForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private List<ReqPickStock> results = new ArrayList<ReqPickStock>();
    private List<ReqPickStock> resultsSearch = new ArrayList<ReqPickStock>();
    private String mode;
    private ReqPickStock bean ;
    private ReqPickStock beanCriteria ;
    
 
	public List<ReqPickStock> getResults() {
		return results;
	}

	public void setResults(List<ReqPickStock> results) {
		this.results = results;
	}

	public ReqPickStock getBean() {
		return bean;
	}

	public void setBean(ReqPickStock bean) {
		this.bean = bean;
	}

	public ReqPickStock getBeanCriteria() {
		return beanCriteria;
	}

	public void setBeanCriteria(ReqPickStock beanCriteria) {
		this.beanCriteria = beanCriteria;
	}

	public List<ReqPickStock> getResultsSearch() {
		return resultsSearch;
	}

	public void setResultsSearch(List<ReqPickStock> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}


	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
