package com.isecinc.pens.web.pick;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.MoveWarehouse;

public class MoveWarehouseForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private List<MoveWarehouse> results = new ArrayList<MoveWarehouse>();
    private List<MoveWarehouse> resultsSearch = new ArrayList<MoveWarehouse>();
    private String mode;
    private MoveWarehouse bean ;
    private MoveWarehouse beanCriteria ;
    
	public List<MoveWarehouse> getResults() {
		return results;
	}

	public void setResults(List<MoveWarehouse> results) {
		this.results = results;
	}

	public MoveWarehouse getBean() {
		return bean;
	}

	public void setBean(MoveWarehouse bean) {
		this.bean = bean;
	}

	public MoveWarehouse getBeanCriteria() {
		return beanCriteria;
	}

	public void setBeanCriteria(MoveWarehouse beanCriteria) {
		this.beanCriteria = beanCriteria;
	}

	public List<MoveWarehouse> getResultsSearch() {
		return resultsSearch;
	}

	public void setResultsSearch(List<MoveWarehouse> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}

}
