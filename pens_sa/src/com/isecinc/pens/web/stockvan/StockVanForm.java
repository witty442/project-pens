package com.isecinc.pens.web.stockvan;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import com.isecinc.core.web.I_Form;

public class StockVanForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<StockVanBean> results = new ArrayList<StockVanBean>();
    private List<StockVanBean> resultsSearch = new ArrayList<StockVanBean>();
    private String pageName;
    private StockVanBean bean ;
    private StockVanBean beanCriteria ;
    
	public List<StockVanBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<StockVanBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public List<StockVanBean> getResults() {
		return results;
	}
	public void setResults(List<StockVanBean> results) {
		this.results = results;
	}
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public StockVanBean getBean() {
		return bean;
	}
	public void setBean(StockVanBean bean) {
		this.bean = bean;
	}
	public StockVanBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(StockVanBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		if(getBean()!=null){
			this.getBean().setDispPlan("");
			this.getBean().setDispHaveQty("");
		}
	}
    
   
}
