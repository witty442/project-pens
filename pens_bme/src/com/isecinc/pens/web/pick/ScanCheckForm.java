package com.isecinc.pens.web.pick;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.AdjustStock;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.ScanCheckBean;

public class ScanCheckForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<ScanCheckBean> results = new ArrayList<ScanCheckBean>();
    private List<ScanCheckBean> resultsSearch = new ArrayList<ScanCheckBean>();
    private List<ScanCheckBean> resultsSearchPrev = new ArrayList<ScanCheckBean>();

    private String mode;
    private ScanCheckBean bean ;
    private ScanCheckBean beanCriteria ;
    
    
	public List<ScanCheckBean> getResultsSearchPrev() {
		return resultsSearchPrev;
	}
	public void setResultsSearchPrev(List<ScanCheckBean> resultsSearchPrev) {
		this.resultsSearchPrev = resultsSearchPrev;
	}
	public List<ScanCheckBean> getResults() {
		return results;
	}
	public void setResults(List<ScanCheckBean> results) {
		this.results = results;
	}
	public List<ScanCheckBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<ScanCheckBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public ScanCheckBean getBean() {
		return bean;
	}
	public void setBean(ScanCheckBean bean) {
		this.bean = bean;
	}
	public ScanCheckBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(ScanCheckBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		 //Initialize the property 
			if(bean !=null){
		    // bean.setIncludeCancel("");
			}
	}
    
   
}
