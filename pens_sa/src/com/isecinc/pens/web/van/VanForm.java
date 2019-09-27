package com.isecinc.pens.web.van;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import com.isecinc.core.web.I_Form;

public class VanForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<VanBean> results = new ArrayList<VanBean>();
    private List<VanBean> resultsSearch = new ArrayList<VanBean>();
    private String pageName;
    private VanBean bean ;
    private VanBean beanCriteria ;
    
	public List<VanBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<VanBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public List<VanBean> getResults() {
		return results;
	}
	public void setResults(List<VanBean> results) {
		this.results = results;
	}
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public VanBean getBean() {
		return bean;
	}
	public void setBean(VanBean bean) {
		this.bean = bean;
	}
	public VanBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(VanBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		if(getBean()!=null){
		
		}
	}
    
   
}
