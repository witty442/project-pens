package com.isecinc.pens.web.stockpd;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.StockPD;
import com.isecinc.pens.bean.Summary;

/**
 * Summary Criteria
 * 
 * @author atiz.b
 * @version $Id: ReceiptCriteria.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class StockPDCriteria extends I_Criteria {

	private static final long serialVersionUID = -189773759780203365L;

	private StockPD stockPD = new StockPD();

	public StockPD getStockPD() {
		return stockPD;
	}

	public void setStockPD(StockPD stockPD) {
		this.stockPD = stockPD;
	}

	
}
