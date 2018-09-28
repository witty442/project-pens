package com.isecinc.pens.web.autocn.hisher;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.LockItemOrderBean;

public class AutoCNHISHERForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<AutoCNHISHERBean> results = new ArrayList<AutoCNHISHERBean>();
    private List<AutoCNHISHERBean> resultsSearch = new ArrayList<AutoCNHISHERBean>();
    private AutoCNHISHERBean bean ;
    private AutoCNHISHERBean beanCriteria ;

	public List<AutoCNHISHERBean> getResults() {
		return results;
	}
	public void setResults(List<AutoCNHISHERBean> results) {
		this.results = results;
	}
	public List<AutoCNHISHERBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<AutoCNHISHERBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public AutoCNHISHERBean getBean() {
		return bean;
	}
	public void setBean(AutoCNHISHERBean bean) {
		this.bean = bean;
	}
	public AutoCNHISHERBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(AutoCNHISHERBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		//if(bean !=null)
		 // bean.setAllStore("");
	}
   
}
