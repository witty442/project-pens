package com.isecinc.pens.web.adjuststock;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.AdjustStock;
import com.isecinc.pens.bean.Order;

/**
 * Summary Criteria
 * 
 * @author atiz.b
 * @version $Id: ReceiptCriteria.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class AdjustStockCriteria extends I_Criteria {

	private static final long serialVersionUID = -189773759780203365L;

	private AdjustStock adjustStock = new AdjustStock();

	public AdjustStock getAdjustStock() {
		return adjustStock;
	}

	public void setAdjustStock(AdjustStock adjustStock) {
		this.adjustStock = adjustStock;
	}

	
	
}
