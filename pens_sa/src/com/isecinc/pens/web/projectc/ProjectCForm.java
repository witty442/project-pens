package com.isecinc.pens.web.projectc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import com.isecinc.core.web.I_Form;

public class ProjectCForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<ProjectCBean> resultsSearch = new ArrayList<ProjectCBean>();
    private List<ProjectCBean> branchList = new ArrayList<ProjectCBean>();
    private String pageName;
    private ProjectCBean bean ;
    private ProjectCBean beanCriteria ;
   
    
	public List<ProjectCBean> getBranchList() {
		return branchList;
	}
	public void setBranchList(List<ProjectCBean> branchList) {
		this.branchList = branchList;
	}
	public List<ProjectCBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<ProjectCBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public ProjectCBean getBean() {
		return bean;
	}
	public void setBean(ProjectCBean bean) {
		this.bean = bean;
	}
	public ProjectCBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(ProjectCBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		
	}
    
   
}
