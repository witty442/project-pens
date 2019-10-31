package com.isecinc.pens.web.autoorder;

import com.isecinc.core.web.I_Form;

/**
 *  AutoOrderFormForm
 * 
 * @author Witty:17/10/2019
 * @version 
 * 
 */

public class AutoOrderForm extends I_Form {

  private static final long serialVersionUID = 9066506758859129582L;
  private AutoOrderBean bean;
  private AutoOrderBean beanCriteria;
  
	public AutoOrderBean getBean() {
		return bean;
	}
	public void setBean(AutoOrderBean bean) {
		this.bean = bean;
	}
	public AutoOrderBean getBeanCriteria() {
		return beanCriteria;
	}
	public void setBeanCriteria(AutoOrderBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
	  
	  
}
