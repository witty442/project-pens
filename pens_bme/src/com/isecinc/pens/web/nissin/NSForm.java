package com.isecinc.pens.web.nissin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.NSBean;
import com.isecinc.pens.bean.RTBean;

public class NSForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<NSBean> results = new ArrayList<NSBean>();
    private NSBean summary ;
    
    private String mode;
    private NSBean bean ;
    private NSBean beanCriteria ;
    
    
	public NSBean getSummary() {
		return summary;
	}
	public void setSummary(NSBean summary) {
		this.summary = summary;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public List<NSBean> getResults() {
		return results;
	}
	public void setResults(List<NSBean> results) {
		this.results = results;
	}
	
	public NSBean getBean() {
		return bean;
	}
	public void setBean(NSBean bean) {
		this.bean = bean;
	}
	public NSBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(NSBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		if(bean !=null)
		   bean.setNoPicRcv("");
	}
   
}
