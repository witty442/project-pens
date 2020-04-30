package com.isecinc.pens.web.stockonhand;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import com.isecinc.core.web.I_Form;

public class StockOnhandForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<StockOnhandBean> results = new ArrayList<StockOnhandBean>();
    private List<StockOnhandBean> resultsSearch = new ArrayList<StockOnhandBean>();
    private String pageName;
    private StockOnhandBean bean ;
    private StockOnhandBean beanCriteria ;
    
	public List<StockOnhandBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<StockOnhandBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public List<StockOnhandBean> getResults() {
		return results;
	}
	public void setResults(List<StockOnhandBean> results) {
		this.results = results;
	}
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public StockOnhandBean getBean() {
		return bean;
	}
	public void setBean(StockOnhandBean bean) {
		this.bean = bean;
	}
	public StockOnhandBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(StockOnhandBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		if(getBean()!=null){
			this.getBean().setDispPlan("");
			this.getBean().setDispHaveQty("");
			this.getBean().setDispPrice("");
			
		}
	}
    
   
}
