package com.isecinc.pens.web.profilesearch;


import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Role;


/**
 *Witty
 * 
 */
public class ManageProfileSearchForm extends I_Form {

	private static final long serialVersionUID = -366067722071055991L;

	private MangeProfileSearchCriteria criteria = new MangeProfileSearchCriteria();

	private List<ManageProfileSearchBean> results = null;
    
	private String mode ;
	
	
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public ManageProfileSearchBean getBean() {
		return criteria.getBean();
	}

	public void setBean(ManageProfileSearchBean role) {
		criteria.setBean(role);
	}
	

	public List<ManageProfileSearchBean> getResults() {
		return results;
	}

	public void setResults(List<ManageProfileSearchBean> results) {
		this.results = results;
	}

	public MangeProfileSearchCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(MangeProfileSearchCriteria criteria) {
		this.criteria = criteria;
	}


}
