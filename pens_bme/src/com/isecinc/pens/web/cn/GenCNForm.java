package com.isecinc.pens.web.cn;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.GenCNBean;
import com.isecinc.pens.bean.MoveWarehouse;

public class GenCNForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private List<GenCNBean> results = new ArrayList<GenCNBean>();
    private List<GenCNBean> resultsSearch = new ArrayList<GenCNBean>();
    private String mode;
    private GenCNBean bean ;
    private GenCNBean beanCriteria ;
    
	public List<GenCNBean> getResults() {
		return results;
	}

	public void setResults(List<GenCNBean> results) {
		this.results = results;
	}

	public GenCNBean getBean() {
		return bean;
	}

	public void setBean(GenCNBean bean) {
		this.bean = bean;
	}

	public GenCNBean getBeanCriteria() {
		return beanCriteria;
	}

	public void setBeanCriteria(GenCNBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}

	public List<GenCNBean> getResultsSearch() {
		return resultsSearch;
	}

	public void setResultsSearch(List<GenCNBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}

}
