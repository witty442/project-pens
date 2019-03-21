package com.isecinc.pens.web.stockmc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import com.isecinc.core.web.I_Form;

public class StockMCForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<StockMCBean> results = new ArrayList<StockMCBean>();
    private String pageName;
    private StockMCBean bean ;
    private StockMCBean beanCriteria ;
    private String mode;

    
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public List<StockMCBean> getResults() {
		return results;
	}
	public void setResults(List<StockMCBean> results) {
		this.results = results;
	}
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public StockMCBean getBean() {
		return bean;
	}
	public void setBean(StockMCBean bean) {
		this.bean = bean;
	}
	public StockMCBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(StockMCBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		
	}
    
   
}
