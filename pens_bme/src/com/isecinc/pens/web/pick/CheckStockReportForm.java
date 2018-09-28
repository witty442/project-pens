package com.isecinc.pens.web.pick;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.CheckStockReportBean;
import com.isecinc.pens.bean.StockQuery;

public class CheckStockReportForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<CheckStockReportBean> results = new ArrayList<CheckStockReportBean>();

    private String mode;
    private String pageName;
    private String warehouse;
    private CheckStockReportBean bean ;
    private CheckStockReportBean beanCriteria ;
    
    
	public String getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public List<CheckStockReportBean> getResults() {
		return results;
	}
	public void setResults(List<CheckStockReportBean> results) {
		this.results = results;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public CheckStockReportBean getBean() {
		return bean;
	}
	public void setBean(CheckStockReportBean bean) {
		this.bean = bean;
	}
	public CheckStockReportBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(CheckStockReportBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		if(getBean() != null){
		   getBean().setDispDiffQtyNoZero("");
		}
		
	}
    
}
