package com.isecinc.pens.web.pay;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.PayBean;

public class PayInterHForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<PayBean> results = new ArrayList<PayBean>();
    private List<PayBean> resultsSearch = new ArrayList<PayBean>();

    private String mode;
    private PayBean bean ;
    private PayBean beanCriteria ;
    
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public List<PayBean> getResults() {
		return results;
	}
	public void setResults(List<PayBean> results) {
		this.results = results;
	}
	public List<PayBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<PayBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public PayBean getBean() {
		return bean;
	}
	public void setBean(PayBean bean) {
		this.bean = bean;
	}
	public PayBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(PayBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		//if(bean !=null)
		 // bean.setActive("");
	}
   
}
