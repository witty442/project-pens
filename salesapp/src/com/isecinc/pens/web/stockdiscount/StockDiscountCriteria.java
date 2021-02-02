package com.isecinc.pens.web.stockdiscount;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.StockDiscount;
import com.isecinc.pens.bean.StockReturn;

/**
 * Summary Criteria
 * 
 * @author atiz.b
 * @version $Id: ReceiptCriteria.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class StockDiscountCriteria extends I_Criteria {

	private static final long serialVersionUID = -189773759780203365L;
	
	private StockDiscount bean = new StockDiscount();
	
	public StockDiscount getBean() {
		return bean;
	}

	public void setBean(StockDiscount bean) {
		this.bean = bean;
	}

}
