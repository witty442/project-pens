package com.isecinc.pens.web.pricelist;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.PriceList;

/**
 * Price List Criteria
 * 
 * @author Aneak.t
 * @version $Id: PriceListCriteria.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class PriceListCriteria extends I_Criteria{

	private static final long serialVersionUID = 3160337216924575212L;
	
	private PriceList priceList = new PriceList();

	public PriceList getPriceList() {
		return priceList;
	}

	public void setPriceList(PriceList priceList) {
		this.priceList = priceList;
	}
	
}
