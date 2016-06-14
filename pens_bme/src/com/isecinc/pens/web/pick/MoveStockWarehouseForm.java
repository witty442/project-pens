package com.isecinc.pens.web.pick;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.MoveStockWarehouseBean;
import com.isecinc.pens.bean.MoveWarehouse;

public class MoveStockWarehouseForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private List<MoveStockWarehouseBean> results = new ArrayList<MoveStockWarehouseBean>();
    private List<MoveStockWarehouseBean> resultsSearch = new ArrayList<MoveStockWarehouseBean>();
    private String mode;
    private MoveStockWarehouseBean bean ;
    private MoveStockWarehouseBean beanCriteria ;
    
	public List<MoveStockWarehouseBean> getResults() {
		return results;
	}

	public void setResults(List<MoveStockWarehouseBean> results) {
		this.results = results;
	}

	public MoveStockWarehouseBean getBean() {
		return bean;
	}

	public void setBean(MoveStockWarehouseBean bean) {
		this.bean = bean;
	}

	public MoveStockWarehouseBean getBeanCriteria() {
		return beanCriteria;
	}

	public void setBeanCriteria(MoveStockWarehouseBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}

	public List<MoveStockWarehouseBean> getResultsSearch() {
		return resultsSearch;
	}

	public void setResultsSearch(List<MoveStockWarehouseBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}

}
