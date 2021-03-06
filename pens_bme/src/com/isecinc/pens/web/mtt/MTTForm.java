package com.isecinc.pens.web.mtt;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.AdjustStock;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.MTTBean;

public class MTTForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<MTTBean> results = new ArrayList<MTTBean>();

    private String mode;
    private MTTBean bean ;
    private MTTBean beanCriteria ;
    private MTTBean summary ;
    
    
	public MTTBean getSummary() {
		return summary;
	}
	public void setSummary(MTTBean summary) {
		this.summary = summary;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public List<MTTBean> getResults() {
		return results;
	}
	public void setResults(List<MTTBean> results) {
		this.results = results;
	}
	public MTTBean getBean() {
		return bean;
	}
	public void setBean(MTTBean bean) {
		this.bean = bean;
	}
	public MTTBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(MTTBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	
    
   
}
