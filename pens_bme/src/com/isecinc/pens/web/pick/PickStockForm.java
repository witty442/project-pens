package com.isecinc.pens.web.pick;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.PickStock;

public class PickStockForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private List<PickStock> results = new ArrayList<PickStock>();
    private List<PickStock> resultsSearch = new ArrayList<PickStock>();
    private String mode;
    private PickStock bean ;
    private PickStock beanCriteria ;
    
  

    
	public List<PickStock> getResults() {
		return results;
	}

	public void setResults(List<PickStock> results) {
		this.results = results;
	}

	public PickStock getBean() {
		return bean;
	}

	public void setBean(PickStock bean) {
		this.bean = bean;
	}

	public PickStock getBeanCriteria() {
		return beanCriteria;
	}

	public void setBeanCriteria(PickStock beanCriteria) {
		this.beanCriteria = beanCriteria;
	}

	public List<PickStock> getResultsSearch() {
		return resultsSearch;
	}

	public void setResultsSearch(List<PickStock> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}


	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
