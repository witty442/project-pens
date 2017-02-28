package com.isecinc.pens.web.lockitem;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.LockItemOrderBean;

public class LockItemOrderForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<LockItemOrderBean> results = new ArrayList<LockItemOrderBean>();
    private List<LockItemOrderBean> resultsSearch = new ArrayList<LockItemOrderBean>();
    private LockItemOrderBean bean ;
    private LockItemOrderBean beanCriteria ;

	public List<LockItemOrderBean> getResults() {
		return results;
	}
	public void setResults(List<LockItemOrderBean> results) {
		this.results = results;
	}
	public List<LockItemOrderBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<LockItemOrderBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public LockItemOrderBean getBean() {
		return bean;
	}
	public void setBean(LockItemOrderBean bean) {
		this.bean = bean;
	}
	public LockItemOrderBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(LockItemOrderBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		if(bean !=null)
		  bean.setAllStore("");
	}
   
}
