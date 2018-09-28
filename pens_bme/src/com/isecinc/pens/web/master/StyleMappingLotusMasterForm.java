package com.isecinc.pens.web.master;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.PayBean;
import com.isecinc.pens.bean.PriceListMasterBean;
import com.isecinc.pens.bean.StyleMappingLotusMasterBean;

public class StyleMappingLotusMasterForm extends I_Form {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7235050810968655051L;

	private String mode;
	private StyleMappingLotusMasterBean bean ;
	private StyleMappingLotusMasterBean beanOLD ;
	private StyleMappingLotusMasterBean beanCriteria ;
	
	private List<StyleMappingLotusMasterBean> results = new ArrayList<StyleMappingLotusMasterBean>();

	
	public StyleMappingLotusMasterBean getBeanOLD() {
		return beanOLD;
	}
	public void setBeanOLD(StyleMappingLotusMasterBean beanOLD) {
		this.beanOLD = beanOLD;
	}
	
	public List<StyleMappingLotusMasterBean> getResults() {
		return results;
	}
	public void setResults(List<StyleMappingLotusMasterBean> results) {
		this.results = results;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public StyleMappingLotusMasterBean getBean() {
		return bean;
	}
	public void setBean(StyleMappingLotusMasterBean bean) {
		this.bean = bean;
	}
	public StyleMappingLotusMasterBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(StyleMappingLotusMasterBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	    
    
}
