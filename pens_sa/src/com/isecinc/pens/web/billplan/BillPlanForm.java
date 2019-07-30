package com.isecinc.pens.web.billplan;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import com.isecinc.core.web.I_Form;

public class BillPlanForm extends I_Form {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1992834459956870881L;
    private List<BillPlanBean> resultsSearch = new ArrayList<BillPlanBean>();
    private String pageName;
    private BillPlanBean bean ;
    private BillPlanBean beanCriteria ;
    
	public List<BillPlanBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<BillPlanBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public BillPlanBean getBean() {
		return bean;
	}
	public void setBean(BillPlanBean bean) {
		this.bean = bean;
	}
	public BillPlanBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(BillPlanBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
   
}
