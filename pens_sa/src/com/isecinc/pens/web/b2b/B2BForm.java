package com.isecinc.pens.web.b2b;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.isecinc.core.web.I_Form;

public class B2BForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<B2BBean> results = new ArrayList<B2BBean>();
    
    private String pageName;
    private B2BBean bean ;
    private B2BBean beanCriteria ;
    private FormFile dataFormFile;
    
    
	public FormFile getDataFormFile() {
		return dataFormFile;
	}
	public void setDataFormFile(FormFile dataFormFile) {
		this.dataFormFile = dataFormFile;
	}
	public List<B2BBean> getResults() {
		return results;
	}
	public void setResults(List<B2BBean> results) {
		this.results = results;
	}
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public B2BBean getBean() {
		return bean;
	}
	public void setBean(B2BBean bean) {
		this.bean = bean;
	}
	public B2BBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(B2BBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {
	  if(getBean() !=null){
		 // getBean().setDispRequestDate("");
		//  getBean().setDispLastUpdate("");
	  }
		
	}
    
   
}
