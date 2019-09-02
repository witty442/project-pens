package com.isecinc.pens.web.itmanage;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.PayBean;

public class ITManageForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<ITManageBean> results = new ArrayList<ITManageBean>();
    private List<ITManageBean> resultsSearch = new ArrayList<ITManageBean>();

    private String mode;
    private ITManageBean bean ;
    private ITManageBean beanCriteria ;
    private String pageName;
    
    
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public List<ITManageBean> getResults() {
		return results;
	}
	public void setResults(List<ITManageBean> results) {
		this.results = results;
	}
	public List<ITManageBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<ITManageBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public ITManageBean getBean() {
		return bean;
	}
	public void setBean(ITManageBean bean) {
		this.bean = bean;
	}
	public ITManageBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(ITManageBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		//if(bean !=null)
		 // bean.setActive("");
	}
   
}
