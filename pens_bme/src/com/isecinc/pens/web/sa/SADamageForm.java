package com.isecinc.pens.web.sa;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.SADamageBean;

public class SADamageForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<SADamageBean> results = new ArrayList<SADamageBean>();
    private List<SADamageBean> resultsSearch = new ArrayList<SADamageBean>();

    private String mode;
    private SADamageBean bean ;
    private SADamageBean beanCriteria ;
    
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	
	public List<SADamageBean> getResults() {
		return results;
	}
	public void setResults(List<SADamageBean> results) {
		this.results = results;
	}
	public List<SADamageBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<SADamageBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public SADamageBean getBean() {
		return bean;
	}
	public void setBean(SADamageBean bean) {
		this.bean = bean;
	}
	public SADamageBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(SADamageBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		//if(bean !=null)
		  //bean.setActive("");
	}
   
}
