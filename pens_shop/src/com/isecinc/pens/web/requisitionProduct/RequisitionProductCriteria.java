package com.isecinc.pens.web.requisitionProduct;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.RequisitionProduct;

/**
 * Summary Criteria
 * 
 * @author atiz.b
 * @version $Id: ReceiptCriteria.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class RequisitionProductCriteria extends I_Criteria {

	private static final long serialVersionUID = -189773759780203365L;

	private RequisitionProduct requisitionProduct = new RequisitionProduct();

	public RequisitionProduct getRequisitionProduct() {
		return requisitionProduct;
	}

	public void setRequisitionProduct(RequisitionProduct requisitionProduct) {
		this.requisitionProduct = requisitionProduct;
	}

}
