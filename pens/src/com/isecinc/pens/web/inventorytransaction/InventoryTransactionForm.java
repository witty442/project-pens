package com.isecinc.pens.web.inventorytransaction;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.InventoryTransaction;

/**
 * Inventory Transaction Form
 * 
 * @author Aneak.t
 * @version $Id: InventoryTransactionForm.java,v 1.0 11/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class InventoryTransactionForm extends I_Form{

	private static final long serialVersionUID = -6695787096606013526L;

	private InventoryTransactionCriteria criteria = new InventoryTransactionCriteria();
	
	private InventoryTransaction[] results = null;
	
	public InventoryTransactionCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(InventoryTransactionCriteria criteria) {
		this.criteria = criteria;
	}

	public InventoryTransaction[] getResults() {
		return results;
	}

	public void setResults(InventoryTransaction[] results) {
		this.results = results;
	}

	public InventoryTransaction getInventoryTransaction() {
		return criteria.getInventoryTransaction();
	}

	public void setInventoryTransaction(
			InventoryTransaction inventoryTransaction) {
		criteria.setInventoryTransaction(inventoryTransaction);
	}
	
}
