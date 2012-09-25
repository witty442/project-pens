package com.isecinc.pens.web.inventory;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Inventory;

/**
 * Inventory Form
 * 
 * @author Aneak.t
 * @version $Id: InventoryForm.java,v 1.0 07/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class InventoryForm extends I_Form{

	private static final long serialVersionUID = 3215172027891313426L;

	private InventoryCriteria criteria = new InventoryCriteria();
	
	private Inventory[] results = null;

	public InventoryCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(InventoryCriteria criteria) {
		this.criteria = criteria;
	}

	public Inventory[] getResults() {
		return results;
	}

	public void setResults(Inventory[] results) {
		this.results = results;
	}

	public Inventory getInventory() {
		return criteria.getInventory();
	}

	public void setInventory(Inventory inventory) {
		criteria.setInventory(inventory);
	}
	
	
}
