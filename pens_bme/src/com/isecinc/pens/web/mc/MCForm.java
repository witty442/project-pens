package com.isecinc.pens.web.mc;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.MCBean;
import com.isecinc.pens.bean.MTTBean;

public class MCForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<MCBean> results = new ArrayList<MCBean>();
    private List<MCBean> resultsSearch = new ArrayList<MCBean>();

    private String mode;
    private MCBean bean ;
    private MCBean beanCriteria ;
    
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public List<MCBean> getResults() {
		return results;
	}
	public void setResults(List<MCBean> results) {
		this.results = results;
	}
	public List<MCBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<MCBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public MCBean getBean() {
		return bean;
	}
	public void setBean(MCBean bean) {
		this.bean = bean;
	}
	public MCBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(MCBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	
    
   
}
