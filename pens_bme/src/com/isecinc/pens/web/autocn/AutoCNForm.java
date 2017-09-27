package com.isecinc.pens.web.autocn;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.LockItemOrderBean;

public class AutoCNForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<AutoCNBean> results = new ArrayList<AutoCNBean>();
    private List<AutoCNBean> resultsSearch = new ArrayList<AutoCNBean>();
    private AutoCNBean bean ;
    private AutoCNBean beanCriteria ;

	public List<AutoCNBean> getResults() {
		return results;
	}
	public void setResults(List<AutoCNBean> results) {
		this.results = results;
	}
	public List<AutoCNBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<AutoCNBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public AutoCNBean getBean() {
		return bean;
	}
	public void setBean(AutoCNBean bean) {
		this.bean = bean;
	}
	public AutoCNBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(AutoCNBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		//if(bean !=null)
		 // bean.setAllStore("");
	}
   
}
