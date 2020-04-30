package com.isecinc.pens.web.boxno;

import java.util.List;

import com.isecinc.core.web.I_Form;

/**
 * BoxNo Form
 * 
 * @author Witty.B
 * @version $Id: $
 * 
 */
public class BoxNoForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private BoxNoBean bean ;
	private List<BoxNoBean> results;
	private String mode;
	
	
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public BoxNoBean getBean() {
		return bean;
	}

	public void setBean(BoxNoBean bean) {
		this.bean = bean;
	}

	public List<BoxNoBean> getResults() {
		return results;
	}

	public void setResults(List<BoxNoBean> results) {
		this.results = results;
	}
	
	
}
