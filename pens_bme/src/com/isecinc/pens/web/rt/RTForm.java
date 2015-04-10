package com.isecinc.pens.web.rt;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.PayBean;
import com.isecinc.pens.bean.RTBean;

public class RTForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<RTBean> results = new ArrayList<RTBean>();
    private List<RTBean> resultsSearch = new ArrayList<RTBean>();

    private String mode;
    private RTBean bean ;
    private RTBean beanCriteria ;
    
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public List<RTBean> getResults() {
		return results;
	}
	public void setResults(List<RTBean> results) {
		this.results = results;
	}
	public List<RTBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<RTBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public RTBean getBean() {
		return bean;
	}
	public void setBean(RTBean bean) {
		this.bean = bean;
	}
	public RTBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(RTBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		if(bean !=null)
		   bean.setNoPicRcv("");
	}
   
}
