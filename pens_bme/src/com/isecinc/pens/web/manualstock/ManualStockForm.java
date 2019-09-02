package com.isecinc.pens.web.manualstock;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.AdjustStock;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.MTTBean;

public class ManualStockForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<ManualStockBean> results = new ArrayList<ManualStockBean>();

    private String mode;
    private ManualStockBean bean ;
    private ManualStockBean beanCriteria ;
    private ManualStockBean summary ;
    
    
	public ManualStockBean getSummary() {
		return summary;
	}
	public void setSummary(ManualStockBean summary) {
		this.summary = summary;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public List<ManualStockBean> getResults() {
		return results;
	}
	public void setResults(List<ManualStockBean> results) {
		this.results = results;
	}
	public ManualStockBean getBean() {
		return bean;
	}
	public void setBean(ManualStockBean bean) {
		this.bean = bean;
	}
	public ManualStockBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(ManualStockBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	
    
   
}
