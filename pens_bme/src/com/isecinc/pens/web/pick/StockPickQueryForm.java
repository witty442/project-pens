package com.isecinc.pens.web.pick;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Barcode;

public class StockPickQueryForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<Barcode> results = new ArrayList<Barcode>();

    private String mode;
    private Barcode bean ;
    private Barcode beanCriteria ;
    
	public List<Barcode> getResults() {
		return results;
	}
	public void setResults(List<Barcode> results) {
		this.results = results;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public Barcode getBean() {
		return bean;
	}
	public void setBean(Barcode bean) {
		this.bean = bean;
	}
	public Barcode getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(Barcode beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	
    
   
}
