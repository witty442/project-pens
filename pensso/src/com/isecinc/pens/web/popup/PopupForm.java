package com.isecinc.pens.web.popup;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.struts.action.ActionForm;

import com.isecinc.pens.bean.PopupBean;

/**
 * PopupForm Class
 * 
 * 
 */
public class PopupForm extends ActionForm implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -4943665003292647343L;
	
	private PopupBean bean;
	private String pageName;
	private Map<String, String> criteriaMap; 
	
	
	public Map<String, String> getCriteriaMap() {
		return criteriaMap;
	}
	public void setCriteriaMap(Map<String, String> criteriaMap) {
		this.criteriaMap = criteriaMap;
	}
	public PopupBean getBean() {
		return bean;
	}
	public void setBean(PopupBean bean) {
		this.bean = bean;
	}
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	
	
}
