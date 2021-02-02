package com.isecinc.pens.web.prodshow;

import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.ProdShowBean;
import com.isecinc.pens.bean.RequestPromotion;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ProdShowForm extends I_Form {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8942476363063066373L;
    private List<RequestPromotion> results ;
    private ProdShowBean bean;
	
	public List<RequestPromotion> getResults() {
		return results;
	}

	public void setResults(List<RequestPromotion> results) {
		this.results = results;
	}

	public ProdShowBean getBean() {
		return bean;
	}

	public void setBean(ProdShowBean bean) {
		this.bean = bean;
	}

	
}
