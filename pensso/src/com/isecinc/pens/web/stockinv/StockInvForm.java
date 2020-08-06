package com.isecinc.pens.web.stockinv;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;

public class StockInvForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<StockInvBean> results = new ArrayList<StockInvBean>();
    private List<StockInvBean> resultsSearch = new ArrayList<StockInvBean>();
    private List<StockInvBean> resultsSearchPrev = new ArrayList<StockInvBean>();
    
    private String mode;
    private StockInvBean bean ;
    private StockInvBean beanCriteria ;
    
    
	public List<StockInvBean> getResultsSearchPrev() {
		return resultsSearchPrev;
	}
	public void setResultsSearchPrev(List<StockInvBean> resultsSearchPrev) {
		this.resultsSearchPrev = resultsSearchPrev;
	}
	public List<StockInvBean> getResults() {
		return results;
	}
	public void setResults(List<StockInvBean> results) {
		this.results = results;
	}
	public List<StockInvBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<StockInvBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public StockInvBean getBean() {
		return bean;
	}
	public void setBean(StockInvBean bean) {
		this.bean = bean;
	}
	public StockInvBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(StockInvBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		 //Initialize the property 
			if(bean !=null){
		      //bean.setIncludeCancel("");
			}
	}
    
   
}
