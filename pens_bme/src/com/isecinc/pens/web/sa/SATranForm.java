package com.isecinc.pens.web.sa;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.SADamageBean;
import com.isecinc.pens.bean.SATranBean;

public class SATranForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<SATranBean> results = new ArrayList<SATranBean>();
    private List<SATranBean> resultsSearch = new ArrayList<SATranBean>();

    private String mode;
    private SATranBean bean ;
    private SATranBean beanCriteria ;
    
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public List<SATranBean> getResults() {
		return results;
	}
	public void setResults(List<SATranBean> results) {
		this.results = results;
	}
	public List<SATranBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<SATranBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public SATranBean getBean() {
		return bean;
	}
	public void setBean(SATranBean bean) {
		this.bean = bean;
	}
	public SATranBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(SATranBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		//if(bean !=null)
		  //bean.setActive("");
	}
   
}
