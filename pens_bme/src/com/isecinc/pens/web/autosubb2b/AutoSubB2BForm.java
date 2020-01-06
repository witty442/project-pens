package com.isecinc.pens.web.autosubb2b;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.LockItemOrderBean;

public class AutoSubB2BForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<AutoSubB2BBean> results = new ArrayList<AutoSubB2BBean>();
    private List<AutoSubB2BBean> resultsSearch = new ArrayList<AutoSubB2BBean>();
    private AutoSubB2BBean bean ;
    private AutoSubB2BBean beanCriteria ;

	public List<AutoSubB2BBean> getResults() {
		return results;
	}
	public void setResults(List<AutoSubB2BBean> results) {
		this.results = results;
	}
	public List<AutoSubB2BBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<AutoSubB2BBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public AutoSubB2BBean getBean() {
		return bean;
	}
	public void setBean(AutoSubB2BBean bean) {
		this.bean = bean;
	}
	public AutoSubB2BBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(AutoSubB2BBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		if(bean !=null){
		   bean.setRefType("");;
		}
	}
   
}
