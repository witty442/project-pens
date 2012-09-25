package com.isecinc.pens.web.moveorder;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.Summary;

/**
 * Summary Criteria
 * 
 * @author atiz.b
 * @version $Id: ReceiptCriteria.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MoveOrderCriteria extends I_Criteria {

	private static final long serialVersionUID = -189773759780203365L;

	private MoveOrder moveOrder = new MoveOrder();

	public MoveOrder getMoveOrder() {
		return moveOrder;
	}

	public void setMoveOrder(MoveOrder moveOrder) {
		this.moveOrder = moveOrder;
	}

}
