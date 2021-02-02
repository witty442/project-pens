package com.isecinc.pens.web.pricelist;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.PriceList;

/**
 * Price List Form
 * 
 * @author Aneak.t
 * @version $Id: PriceListForm.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class PriceListForm extends I_Form{

	private static final long serialVersionUID = 1886984902546248520L;

	private PriceListCriteria criteria = new PriceListCriteria();
	
	private PriceList[] results = null;
	
	public PriceList getPriceList() {
		return criteria.getPriceList();
	}

	public void setPriceList(PriceList priceList) {
		criteria.setPriceList(priceList);
	}

	public PriceListCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(PriceListCriteria criteria) {
		this.criteria = criteria;
	}

	public PriceList[] getResults() {
		return results;
	}

	public void setResults(PriceList[] results) {
		this.results = results;
	}
	
	
}
