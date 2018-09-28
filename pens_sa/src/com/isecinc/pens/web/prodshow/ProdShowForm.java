package com.isecinc.pens.web.prodshow;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import com.isecinc.core.web.I_Form;

public class ProdShowForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<ProdShowBean> results = new ArrayList<ProdShowBean>();
    private List<ProdShowBean> resultsSearch = new ArrayList<ProdShowBean>();
    private String pageName;
    private ProdShowBean bean ;
    private ProdShowBean beanCriteria ;
    
	public List<ProdShowBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<ProdShowBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public List<ProdShowBean> getResults() {
		return results;
	}
	public void setResults(List<ProdShowBean> results) {
		this.results = results;
	}
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public ProdShowBean getBean() {
		return bean;
	}
	public void setBean(ProdShowBean bean) {
		this.bean = bean;
	}
	public ProdShowBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(ProdShowBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		
	}
    
   
}
