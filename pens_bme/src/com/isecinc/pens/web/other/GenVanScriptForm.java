package com.isecinc.pens.web.other;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.GenVanScriptBean;

public class GenVanScriptForm extends I_Form {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2026573441518748925L;
	/**
	 * 
	 */
	private String mode;
	private GenVanScriptBean bean ;


	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public GenVanScriptBean getBean() {
		return bean;
	}
	public void setBean(GenVanScriptBean bean) {
		this.bean = bean;
	}
	
}
