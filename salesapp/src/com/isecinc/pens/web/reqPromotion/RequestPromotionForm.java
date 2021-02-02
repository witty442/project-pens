package com.isecinc.pens.web.reqPromotion;

import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.RequestPromotion;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class RequestPromotionForm extends I_Form {


	/**
	 * 
	 */
	private static final long serialVersionUID = 8942476363063066373L;
	private RequestPromotionCriteria criteria = new RequestPromotionCriteria();
    private List<RequestPromotion> results ;
	
    
    
	public List<RequestPromotion> getResults() {
		return results;
	}

	public void setResults(List<RequestPromotion> results) {
		this.results = results;
	}

	public RequestPromotionCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(RequestPromotionCriteria criteria) {
		this.criteria = criteria;
	}

	public RequestPromotion getRequestPromotion() {
		return criteria.getRequestPromotion();
	}

	public void setRequestPromotion(RequestPromotion moveOrder) {
		criteria.setRequestPromotion(moveOrder);
	}

	
	
}
