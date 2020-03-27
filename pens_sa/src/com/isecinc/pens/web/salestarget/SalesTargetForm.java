package com.isecinc.pens.web.salestarget;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import com.isecinc.core.web.I_Form;

public class SalesTargetForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<SalesTargetBean> results = new ArrayList<SalesTargetBean>();
    
    private String pageName;
    private String subPageName;
    private SalesTargetBean bean ;
  
  
	public List<SalesTargetBean> getResults() {
		return results;
	}
	public void setResults(List<SalesTargetBean> results) {
		this.results = results;
	}
	
	public String getSubPageName() {
		return subPageName;
	}
	public void setSubPageName(String subPageName) {
		this.subPageName = subPageName;
	}
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public SalesTargetBean getBean() {
		return bean;
	}
	public void setBean(SalesTargetBean bean) {
		this.bean = bean;
	}
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		
	}
    
   
}
