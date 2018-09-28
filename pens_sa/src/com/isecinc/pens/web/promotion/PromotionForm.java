package com.isecinc.pens.web.promotion;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import com.isecinc.core.web.I_Form;

public class PromotionForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<PromotionBean> results = new ArrayList<PromotionBean>();
    private List<PromotionBean> resultsSearch = new ArrayList<PromotionBean>();
    private String pageName;
    private PromotionBean bean ;
    private PromotionBean beanCriteria ;
    
	public List<PromotionBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<PromotionBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public List<PromotionBean> getResults() {
		return results;
	}
	public void setResults(List<PromotionBean> results) {
		this.results = results;
	}
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public PromotionBean getBean() {
		return bean;
	}
	public void setBean(PromotionBean bean) {
		this.bean = bean;
	}
	public PromotionBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(PromotionBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		
	}
    
   
}
