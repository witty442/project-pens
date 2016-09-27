package com.isecinc.pens.web.sa;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.MCEmpBean;
import com.isecinc.pens.bean.SAEmpBean;

public class SAEmpForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<SAEmpBean> results = new ArrayList<SAEmpBean>();
    private List<SAEmpBean> resultsSearch = new ArrayList<SAEmpBean>();

    private String mode;
    private SAEmpBean bean ;
    private SAEmpBean beanCriteria ;
    
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	
	public List<SAEmpBean> getResults() {
		return results;
	}
	public void setResults(List<SAEmpBean> results) {
		this.results = results;
	}
	public List<SAEmpBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<SAEmpBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public SAEmpBean getBean() {
		return bean;
	}
	public void setBean(SAEmpBean bean) {
		this.bean = bean;
	}
	public SAEmpBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(SAEmpBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		//if(bean !=null)
		  //bean.setActive("");
	}
   
}
