package com.isecinc.pens.web.reqPromotion;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.RequestPromotion;
import com.isecinc.pens.bean.Summary;

/**
 * Summary Criteria
 * 
 * @author atiz.b
 * @version $Id: ReceiptCriteria.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class RequestPromotionCriteria extends I_Criteria {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3335929617907483840L;
	private RequestPromotion requestPromotion = new RequestPromotion();

	public RequestPromotion getRequestPromotion() {
		return requestPromotion;
	}

	public void setRequestPromotion(RequestPromotion requestPromotion) {
		this.requestPromotion = requestPromotion;
	}

   
}
