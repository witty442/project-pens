package com.isecinc.pens.web.moveorder;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import com.isecinc.core.web.I_Form;

public class MoveOrderForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<MoveOrderBean> results = new ArrayList<MoveOrderBean>();
    private List<MoveOrderBean> resultsSearch = new ArrayList<MoveOrderBean>();
    private String pageName;
    private MoveOrderBean bean ;
    private MoveOrderBean beanCriteria ;
    
	public List<MoveOrderBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<MoveOrderBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public List<MoveOrderBean> getResults() {
		return results;
	}
	public void setResults(List<MoveOrderBean> results) {
		this.results = results;
	}
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public MoveOrderBean getBean() {
		return bean;
	}
	public void setBean(MoveOrderBean bean) {
		this.bean = bean;
	}
	public MoveOrderBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(MoveOrderBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {
      if(getBean() != null){
    	  getBean().setDispHaveReason("");
    	  getBean().setDispCheckMoveDay("");
      }
	}
    
   
}
