package com.isecinc.pens.web.toolmanage;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.LockItemOrderBean;

public class ToolManageForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<ToolManageBean> resultsSearch = new ArrayList<ToolManageBean>();
    private ToolManageBean bean ;
    private ToolManageBean beanCriteria ;

	public List<ToolManageBean> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<ToolManageBean> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public ToolManageBean getBean() {
		return bean;
	}
	public void setBean(ToolManageBean bean) {
		this.bean = bean;
	}
	public ToolManageBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(ToolManageBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		//if(bean !=null)
		 // bean.setAllStore("");
	}
   
}
