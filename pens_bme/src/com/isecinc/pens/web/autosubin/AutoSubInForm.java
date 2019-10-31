package com.isecinc.pens.web.autosubin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.LockItemOrderBean;

public class AutoSubInForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<AutoSubInBean> results = new ArrayList<AutoSubInBean>();
    private List<AutoSubInBean> resultsSearch = new ArrayList<AutoSubInBean>();
    private AutoSubInBean bean ;
    private AutoSubInBean beanCriteria ;

	public List<AutoSubInBean> getResults() {
		return results;
	}
	public void setResults(List<AutoSubInBean> results) {
		this.results = results;
	}
	public List<AutoSubInBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<AutoSubInBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public AutoSubInBean getBean() {
		return bean;
	}
	public void setBean(AutoSubInBean bean) {
		this.bean = bean;
	}
	public AutoSubInBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(AutoSubInBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		//if(bean !=null)
		 // bean.setAllStore("");
	}
   
}
