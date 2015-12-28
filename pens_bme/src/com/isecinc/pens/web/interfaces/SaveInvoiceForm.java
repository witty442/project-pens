package com.isecinc.pens.web.interfaces;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.SaveInvoiceBean;

public class SaveInvoiceForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<SaveInvoiceBean> results = new ArrayList<SaveInvoiceBean>();
    private List<SaveInvoiceBean> resultsSearch = new ArrayList<SaveInvoiceBean>();

    private String mode;
    private SaveInvoiceBean bean ;
    private SaveInvoiceBean beanCriteria ;
    
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	
	public List<SaveInvoiceBean> getResults() {
		return results;
	}
	public void setResults(List<SaveInvoiceBean> results) {
		this.results = results;
	}
	public List<SaveInvoiceBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<SaveInvoiceBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public SaveInvoiceBean getBean() {
		return bean;
	}
	public void setBean(SaveInvoiceBean bean) {
		this.bean = bean;
	}
	public SaveInvoiceBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(SaveInvoiceBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		//if(bean !=null)
		 // bean.setActive("");
	}
   
}
