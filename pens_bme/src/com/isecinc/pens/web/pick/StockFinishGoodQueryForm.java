package com.isecinc.pens.web.pick;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.StockQuery;

public class StockFinishGoodQueryForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<StockQuery> results = new ArrayList<StockQuery>();

    private String mode;
    private StockQuery bean ;
    private StockQuery beanCriteria ;
    
	public List<StockQuery> getResults() {
		return results;
	}
	public void setResults(List<StockQuery> results) {
		this.results = results;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public StockQuery getBean() {
		return bean;
	}
	public void setBean(StockQuery bean) {
		this.bean = bean;
	}
	public StockQuery getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(StockQuery beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	
    
   
}
