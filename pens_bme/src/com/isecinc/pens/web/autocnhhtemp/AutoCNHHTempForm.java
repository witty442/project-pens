package com.isecinc.pens.web.autocnhhtemp;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.LockItemOrderBean;

public class AutoCNHHTempForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<AutoCNHHTempBean> results = new ArrayList<AutoCNHHTempBean>();
    private List<AutoCNHHTempBean> resultsSearch = new ArrayList<AutoCNHHTempBean>();
    private AutoCNHHTempBean bean ;
    private AutoCNHHTempBean beanCriteria ;
    private FormFile dataFile;

    
	public FormFile getDataFile() {
		return dataFile;
	}
	public void setDataFile(FormFile dataFile) {
		this.dataFile = dataFile;
	}
	public List<AutoCNHHTempBean> getResults() {
		return results;
	}
	public void setResults(List<AutoCNHHTempBean> results) {
		this.results = results;
	}
	public List<AutoCNHHTempBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<AutoCNHHTempBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public AutoCNHHTempBean getBean() {
		return bean;
	}
	public void setBean(AutoCNHHTempBean bean) {
		this.bean = bean;
	}
	public AutoCNHHTempBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(AutoCNHHTempBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		//if(bean !=null)
		 // bean.setAllStore("");
	}
   
}
