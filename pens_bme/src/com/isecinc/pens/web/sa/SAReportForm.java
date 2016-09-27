package com.isecinc.pens.web.sa;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.SAReportBean;

public class SAReportForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

    private String mode;
    private SAReportBean bean ;
    private SAReportBean beanCriteria ;
    
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public SAReportBean getBean() {
		return bean;
	}
	public void setBean(SAReportBean bean) {
		this.bean = bean;
	}
	public SAReportBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(SAReportBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		//if(bean !=null)
		  //bean.setActive("");
	}
   
}
