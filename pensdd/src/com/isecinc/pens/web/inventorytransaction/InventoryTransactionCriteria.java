package com.isecinc.pens.web.inventorytransaction;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.InventoryTransaction;

/**
 * Inventory Transaction Criteria
 * 
 * @author Aneak.t
 * @version $Id: InventoryTransactionCriteria.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class InventoryTransactionCriteria extends I_Criteria{

	private static final long serialVersionUID = 6786791484583767455L;

	private InventoryTransaction inventoryTransaction = new InventoryTransaction();
	
	public InventoryTransaction getInventoryTransaction() {
		return inventoryTransaction;
	}

	public void setInventoryTransaction(InventoryTransaction inventoryTransaction) {
		this.inventoryTransaction = inventoryTransaction;
	}

}
