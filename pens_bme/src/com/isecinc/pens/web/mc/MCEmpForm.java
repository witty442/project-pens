package com.isecinc.pens.web.mc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.MCEmpBean;

public class MCEmpForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<MCEmpBean> results = new ArrayList<MCEmpBean>();
    private List<MCEmpBean> resultsSearch = new ArrayList<MCEmpBean>();

    private String mode;
    private MCEmpBean bean ;
    private MCEmpBean beanCriteria ;
    
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public List<MCEmpBean> getResults() {
		return results;
	}
	public void setResults(List<MCEmpBean> results) {
		this.results = results;
	}
	public List<MCEmpBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<MCEmpBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public MCEmpBean getBean() {
		return bean;
	}
	public void setBean(MCEmpBean bean) {
		this.bean = bean;
	}
	public MCEmpBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(MCEmpBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		//if(bean !=null)
		  //bean.setActive("");
	}
   
}
