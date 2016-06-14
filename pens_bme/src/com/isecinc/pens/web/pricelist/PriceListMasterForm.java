package com.isecinc.pens.web.pricelist;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.PayBean;
import com.isecinc.pens.bean.PriceListMasterBean;

public class PriceListMasterForm extends I_Form {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2971041771971024254L;
	private String mode;
	private PriceListMasterBean bean ;
	private PriceListMasterBean beanOLD ;
	private PriceListMasterBean beanCriteria ;
	
	private List<PriceListMasterBean> results = new ArrayList<PriceListMasterBean>();
	private List<PriceListMasterBean> resultsSearch = new ArrayList<PriceListMasterBean>();

	
	public PriceListMasterBean getBeanOLD() {
		return beanOLD;
	}
	public void setBeanOLD(PriceListMasterBean beanOLD) {
		this.beanOLD = beanOLD;
	}
	public List<PriceListMasterBean> getResults() {
		return results;
	}
	public void setResults(List<PriceListMasterBean> results) {
		this.results = results;
	}
	public List<PriceListMasterBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<PriceListMasterBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public PriceListMasterBean getBean() {
		return bean;
	}
	public void setBean(PriceListMasterBean bean) {
		this.bean = bean;
	}
	public PriceListMasterBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(PriceListMasterBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	    
    
}
