package com.isecinc.pens.web.location;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import com.isecinc.core.web.I_Form;

public class LocationForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<LocationBean> results = new ArrayList<LocationBean>();
    
    private String pageName;
    private LocationBean bean ;
    private LocationBean beanCriteria ;
    
	public List<LocationBean> getResults() {
		return results;
	}
	public void setResults(List<LocationBean> results) {
		this.results = results;
	}
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public LocationBean getBean() {
		return bean;
	}
	public void setBean(LocationBean bean) {
		this.bean = bean;
	}
	public LocationBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(LocationBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {
	  if(getBean() !=null){
		 
	  }
		
	}
    
   
}
