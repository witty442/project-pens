package com.isecinc.pens.web.temp;

import com.isecinc.core.web.I_Form;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class TempForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private TempCriteria criteria = new TempCriteria();


	public TempCriteria getCriteria() {
		return criteria;
	}


	public void setCriteria(TempCriteria criteria) {
		this.criteria = criteria;
	}


	
    
}
