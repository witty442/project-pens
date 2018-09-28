package com.isecinc.pens.web.autosub;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.LockItemOrderBean;

public class AutoSubBigCForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<AutoSubBigCBean> results = new ArrayList<AutoSubBigCBean>();
    private List<AutoSubBigCBean> resultsSearch = new ArrayList<AutoSubBigCBean>();
    private AutoSubBigCBean bean ;
    private AutoSubBigCBean beanCriteria ;

	public List<AutoSubBigCBean> getResults() {
		return results;
	}
	public void setResults(List<AutoSubBigCBean> results) {
		this.results = results;
	}
	public List<AutoSubBigCBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<AutoSubBigCBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public AutoSubBigCBean getBean() {
		return bean;
	}
	public void setBean(AutoSubBigCBean bean) {
		this.bean = bean;
	}
	public AutoSubBigCBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(AutoSubBigCBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		//if(bean !=null)
		 // bean.setAllStore("");
	}
   
}
